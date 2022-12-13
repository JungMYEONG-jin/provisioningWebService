package mj.provisioning.profile.domain;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.util.StringUtils.hasText;

@Getter
public enum ProfileState {
    ACTIVE("active"), INVALID("invalid");

    private String value;

    ProfileState(String value) {
        this.value = value;
    }

    private static final Map<String, ProfileState> profileStateMap;

    static {
        Map<String, ProfileState> map = new ConcurrentHashMap<>();
        for(ProfileState instance : ProfileState.values()){
            map.put(instance.name(), instance);
        }
        profileStateMap = Collections.unmodifiableMap(map);
    }

    public static ProfileState get(String name){
        return hasText(name)?profileStateMap.get(name):null;
    }

}
