package mj.provisioning.device.domain;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.util.StringUtils.hasText;

public enum DeviceClass {
    IPOD("IPOD"), IPAD("IPAD"), IPHONE("IPHONE");

    private String value;

    DeviceClass(String value) {
        this.value = value;
    }

    private static final Map<String, DeviceClass> DEVICE_CLASS_MAP;

    static{
        Map<String, DeviceClass> map = new ConcurrentHashMap<>();
        for(DeviceClass deviceClass : DeviceClass.values())
        {
            map.put(deviceClass.name(), deviceClass);
        }
        DEVICE_CLASS_MAP = Collections.unmodifiableMap(map);
    }
    public static DeviceClass get(String name){
        return hasText(name)?DEVICE_CLASS_MAP.get(name):null;
    }
}
