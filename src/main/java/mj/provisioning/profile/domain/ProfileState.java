package mj.provisioning.profile.domain;

import lombok.Getter;

@Getter
public enum ProfileState {
    ACTIVE("active"), INVALID("invalid");

    private String value;

    ProfileState(String value) {
        this.value = value;
    }
}
