package mj.provisioning.profile.domain;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public enum ProfilePlatform {
    IOS("iOS"), MAC("macOS");

    private String value;

    ProfilePlatform(String value) {
        this.value = value;
    }

    private static final Map<String, ProfilePlatform> PROFILE_PLATFORM_MAP;

    static {
        Map<String, ProfilePlatform> map = new ConcurrentHashMap<>();
        for(ProfilePlatform platform : ProfilePlatform.values()){
            map.put(platform.name(), platform);
        }
        PROFILE_PLATFORM_MAP = Collections.unmodifiableMap(map);
    }

    public static ProfilePlatform get(String name){
        return PROFILE_PLATFORM_MAP.get(name);
    }


}
