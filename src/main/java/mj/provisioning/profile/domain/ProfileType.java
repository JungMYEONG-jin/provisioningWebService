package mj.provisioning.profile.domain;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.util.StringUtils.hasText;

@Getter
public enum ProfileType {
    IOS_APP_STORE("IOS_DISTRIBUTION"), IOS_APP_DEVELOPMENT("DEVELOPMENT"), IOS_APP_ADHOC("IOS_DISTRIBUTION");

    private String value;

    ProfileType(String value) {
        this.value = value;
    }

    private static final Map<String,ProfileType> ENUM_MAP;
    // Build an immutable map of String name to enum pairs.
    // Any Map impl can be used.

    static {
        Map<String,ProfileType> map = new ConcurrentHashMap<String, ProfileType>();
        for (ProfileType instance : ProfileType.values()) {
            map.put(instance.name(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static ProfileType get (String name) {
        return hasText(name)?ENUM_MAP.get(name):null;
    }

}
