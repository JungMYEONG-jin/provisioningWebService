package mj.provisioning.profiledevice.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import mj.provisioning.common.exception.CustomException;
import mj.provisioning.common.exception.ErrorCode;
import mj.provisioning.device.application.port.in.DeviceShowDto;
import mj.provisioning.device.application.port.in.DeviceShowListDto;
import mj.provisioning.device.application.port.out.DeviceRepositoryPort;
import mj.provisioning.device.domain.Device;
import mj.provisioning.device.domain.DeviceClass;
import mj.provisioning.profile.application.port.out.ProfileRepositoryPort;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profile.domain.ProfileType;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import mj.provisioning.profiledevice.application.port.out.ProfileDeviceDeletePort;
import mj.provisioning.profiledevice.application.port.out.ProfileDeviceFindPort;
import mj.provisioning.profiledevice.application.port.out.ProfileDeviceSavePort;
import mj.provisioning.profiledevice.domain.ProfileDevice;
import mj.provisioning.util.apple.AppleApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileDeviceService implements ProfileDeviceUseCase {

    private final ProfileDeviceSavePort profileDeviceSavePort;
    private final ProfileDeviceDeletePort profileDeviceDeletePort;
    private final ProfileDeviceFindPort profileDeviceFindPort;
    private final AppleApi appleApi;
    private final ProfileRepositoryPort profileRepositoryPort;
    private final DeviceRepositoryPort deviceRepositoryPort;

    @Override
    public void saveProfileDevice(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(()-> new CustomException( ErrorCode.PROFILE_NOT_EXIST.getMessage(), ErrorCode.PROFILE_NOT_EXIST));
        // 기존 삭제
        profileDeviceDeletePort.deleteByProfile(profile);
        // set list
        List<ProfileDevice> profileDevices = new ArrayList<>();
        /**
         * distribution 제외하고 dev, adhoc device 있음.
         */
        if (!profile.getProfileType().equals(ProfileType.IOS_APP_STORE)) {
            String response = appleApi.getDeviceInfoFromProfile(profileId);

            JsonParser parser = new JsonParser();
            JsonObject parse = parser.parse(response).getAsJsonObject();
            JsonArray data = parse.getAsJsonArray("data");
            if (data!=null) {
                for (JsonElement element : data) {
                    JsonObject jsonElement = element.getAsJsonObject();
                    String deviceId = jsonElement.get("id").getAsString();
                    String type = jsonElement.get("type").getAsString();
                    profileDevices.add(ProfileDevice.builder().deviceId(deviceId).type(type).build());
                }
                profile.insertAll(profileDevices);
                profileDeviceSavePort.saveAll(profileDevices);
            }
        }
    }

    @Override
    public void deleteByProfile(Profile profile) {
        profileDeviceDeletePort.deleteByProfile(profile);
    }

    /**
     * 업데이트 결과를 저장하자
     * @param profile
     * @param deviceIds
     */
    @Override
    public void saveUpdatedResult(Profile profile, List<String> deviceIds) {
        List<Device> devices = deviceRepositoryPort.findByIds(deviceIds);
        List<ProfileDevice> profileDevices;
        // 운영만 아니면 다 디바이스 가짐
        if (!profile.getProfileType().equals(ProfileType.IOS_APP_STORE)) {
                profileDevices = devices.stream().map(ProfileDevice::of).collect(Collectors.toList());
                profile.insertAll(profileDevices);
            profileDeviceSavePort.saveAll(profileDevices);
        }
    }

    /**
     * 프로비저닝 애플 디바이스 등록화면에서 선택한 디바이스 리스트 반영
     * @param deviceData
     * @return
     */
    @Override
    public JsonObject getDeviceForUpdateProfile(List<DeviceShowDto> deviceData) {
        JsonArray deviceJson = new JsonArray();
        deviceData.stream().filter(DeviceShowDto::isChosen).forEach(
                deviceShowDto -> {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("id", deviceShowDto.getDeviceId());
                    obj.addProperty("type", deviceShowDto.getType());
                    deviceJson.add(obj);
                }
        );
        JsonObject device = new JsonObject();
        device.add("data", deviceJson);
        return device;
    }

    /**
     * edit 페이지에서 사용자에게 체크박스 제공하기 위한 list
     * @param profileId
     * @return
     */
    @Override
    public DeviceShowListDto getDeviceShowList(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(()-> new CustomException( ErrorCode.PROFILE_NOT_EXIST.getMessage(), ErrorCode.PROFILE_NOT_EXIST));
        List<Device> all = deviceRepositoryPort.findAll().orElseThrow(()->new RuntimeException("등록된 디바이스가 존재하지 않습니다."));
        final long[] idx = {0};
        return DeviceShowListDto.builder().deviceData(all.stream().map(device -> DeviceShowDto.of(device, profileDeviceFindPort.isExist(device.getDeviceId(), profile) ,idx[0]++)).collect(Collectors.toList())).build();
    }

    @Override
    public List<DeviceShowDto> getDeviceForEdit(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(()-> new CustomException( ErrorCode.PROFILE_NOT_EXIST.getMessage(), ErrorCode.PROFILE_NOT_EXIST));
        List<Device> all = deviceRepositoryPort.findByClass(DeviceClass.IPHONE.name());
        final long[] idx = {0};
        return all.stream().map(device -> DeviceShowDto.of(device, profileDeviceFindPort.isExist(device.getDeviceId(), profile) ,idx[0]++)).collect(Collectors.toList());
    }

    /**
     * 모든 디바이스를 등록시킬때 사용.
     * @param profileId
     * @return
     */
    @Override
    public List<DeviceShowDto> getAllDeviceForEdit(String profileId) {
        // profile 검증
        Profile profile = profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(()-> new CustomException( ErrorCode.PROFILE_NOT_EXIST.getMessage(), ErrorCode.PROFILE_NOT_EXIST));
        List<Device> all = deviceRepositoryPort.findByClass(DeviceClass.IPHONE.name());
        final long[] idx = {0};
        return all.stream().map(device -> DeviceShowDto.of(device,true ,idx[0]++)).collect(Collectors.toList());
    }

    /**
     * 현재 등록된 디바이스 목록을 json array 반환
     * @param profileId
     * @return
     */
    private JsonArray getDeviceJson(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(()-> new CustomException( ErrorCode.PROFILE_NOT_EXIST.getMessage(), ErrorCode.PROFILE_NOT_EXIST));
        List<ProfileDevice> devices = profileDeviceFindPort.findByProfile(profile);
        JsonArray jsonArray = new JsonArray();
        devices.forEach(profileDevice -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", profileDevice.getDeviceId());
            obj.addProperty("type", profileDevice.getType());
            jsonArray.add(obj);
        });
        return jsonArray;
    }
}

