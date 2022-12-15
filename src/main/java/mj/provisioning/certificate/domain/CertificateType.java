package mj.provisioning.certificate.domain;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.util.StringUtils.hasText;

public enum CertificateType {
    DEVELOPMENT("DEVELOPMENT"), IOS_DISTRIBUTION("DISTRIBUTION");

    private String value;

    CertificateType(String value) {
        this.value = value;
    }

    private static final Map<String, CertificateType> CERTIFICATE_TYPE_MAP;

    static{
        Map<String, CertificateType> certificateTypeMap = new ConcurrentHashMap<>();
        for(CertificateType certificateType : CertificateType.values()){
            certificateTypeMap.put(certificateType.name(), certificateType);
        }
        CERTIFICATE_TYPE_MAP = Collections.unmodifiableMap(certificateTypeMap);
    }

    public static CertificateType get(String name){
        return hasText(name)?CERTIFICATE_TYPE_MAP.get(name):null;
    }
}
