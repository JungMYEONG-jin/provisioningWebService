package mj.provisioning.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.provisioning.device.application.port.in.DeviceUseCase;
import mj.provisioning.profile.application.port.in.ProfileUseCase;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

    private final ProfileUseCase profileUseCase;
    private final ProfileDeviceUseCase profileDeviceUseCase;
    private final DeviceUseCase deviceUseCase;

//A2BFWL3C73
//    @EventListener(ApplicationReadyEvent.class)
//    public void init(){
//        profileUseCase.saveProfiles(); // 모든 profiles 업데이트
//        deviceUseCase.saveDevices(); // 모든 device 업데이트
//    }

}
