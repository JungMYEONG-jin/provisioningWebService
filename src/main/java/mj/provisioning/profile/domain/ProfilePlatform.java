package mj.provisioning.profile.domain;

import lombok.Getter;

@Getter
public enum ProfilePlatform {
    IOS("iOS"), MAC("macOS");

    private String value;

    ProfilePlatform(String value) {
        this.value = value;
    }
}
