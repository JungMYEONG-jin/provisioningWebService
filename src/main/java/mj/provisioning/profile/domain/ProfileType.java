package mj.provisioning.profile.domain;

import lombok.Getter;

@Getter
public enum ProfileType {
    IOS_APP_STORE("App Store"), IOS_APP_DEVELOPMENT("Development"), IOS_APP_ADHOC("Ad hoc");

    private String value;

    ProfileType(String value) {
        this.value = value;
    }
}
