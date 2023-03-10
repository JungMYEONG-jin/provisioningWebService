package mj.provisioning.device.application.service;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import mj.provisioning.device.application.data.DeviceEditDto;
import mj.provisioning.device.application.data.DeviceJsonDto;
import mj.provisioning.device.application.port.in.DeviceDto;
import mj.provisioning.device.application.port.in.DeviceEditCase;
import mj.provisioning.device.application.port.in.DeviceUseCase;
import mj.provisioning.device.application.port.out.DeviceRepositoryPort;
import mj.provisioning.device.domain.Device;
import mj.provisioning.util.apple.AppleApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DeviceService implements DeviceUseCase, DeviceEditCase {

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

    @Override
    public void disableDevice(Device device) {
        appleApi.disableUserDevice(device);
    }

    @Override
    public void disableDevicesFromExcel(String jsonPath) {
        Reader reader = null;
        try {
            reader = new FileReader(jsonPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Json 파일 읽어서, Lecture 객체로 변환
        // 해당 파일은 disable 할 대상임
        Gson gson = new Gson();
        DeviceJsonDto result = gson.fromJson(reader, DeviceJsonDto.class);
        List<String> toDeleteIdentifiers = result.getDevices().stream().map(DeviceEditDto::getIDENTIFIER).collect(Collectors.toList());
        List<Device> toDeleteDevices = deviceRepositoryPort.findByUdIds(toDeleteIdentifiers);
        for (Device toDeleteDevice : toDeleteDevices) {
            System.out.println("toDeleteDevice = " + toDeleteDevice);
            appleApi.disableUserDevice(toDeleteDevice);
        }
        System.out.println("toDeleteDevices = " + toDeleteDevices.size());


    }


}
