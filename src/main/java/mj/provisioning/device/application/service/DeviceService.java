package mj.provisioning.device.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import mj.provisioning.device.application.port.in.DeviceDto;
import mj.provisioning.device.application.port.in.DeviceUseCase;
import mj.provisioning.device.application.port.out.DeviceRepositoryPort;
import mj.provisioning.device.domain.Device;
import mj.provisioning.util.apple.AppleApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DeviceService implements DeviceUseCase{

    private final DeviceRepositoryPort deviceRepositoryPort;
    private final AppleApi appleApi;

    /**
     * spring scheduler로 돌릴거
     */
    @Override
    public void saveDevices() {
        String allDevices = appleApi.getAllRegisteredDevices();
        deviceRepositoryPort.deleteAll(); // 기존꺼 삭제
        JsonParser parser = new JsonParser();
        JsonObject deviceJson = parser.parse(allDevices).getAsJsonObject();
        JsonArray dataArray = deviceJson.get("data").getAsJsonArray();
        List<Device> devices = new ArrayList<>();
        for (JsonElement jsonElement : dataArray) {
            JsonObject object = jsonElement.getAsJsonObject();
            String type = object.get("type").toString().replaceAll("\"", "");
            String deviceId = object.get("id").toString().replaceAll("\"", "");
            JsonObject attributes = object.getAsJsonObject("attributes");
            String name = attributes.get("name").toString().replaceAll("\"", "");
            String deviceClass = attributes.get("deviceClass").toString().replaceAll("\"", "");
            String udid = attributes.get("udid").toString().replaceAll("\"", "");

            Device build = Device.builder().type(type).udId(udid).deviceClass(deviceClass).deviceId(deviceId).name(name).build();
            devices.add(build);
        }
        deviceRepositoryPort.saveAll(devices); // 최신 동기화
    }


    @Override
    public List<DeviceDto> getAllDeviceList() {
        List<Device> all = deviceRepositoryPort.findAll().orElseThrow(()->new RuntimeException("No Devices..."));
        return all.stream().map(DeviceDto::to).collect(Collectors.toList());
    }
}
