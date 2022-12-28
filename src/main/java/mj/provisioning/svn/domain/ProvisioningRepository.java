package mj.provisioning.svn.domain;

import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 해당 경로 규칙은 꼭 지켜야 합니다...
 * 아니면 못 써요..
 */
public enum ProvisioningRepository {
    Global_SBank_구("buildsetting/iOS/MobileProvisioning/대고객앱/국내/Global S Bank(구)"),
    Intermax("buildsetting/iOS/MobileProvisioning/대고객앱/국내/intermax"),
    NextBankingEnz("buildsetting/iOS/MobileProvisioning/대고객앱/국내/nextBanking"),
    Poney("buildsetting/iOS/MobileProvisioning/대고객앱/국내/Poney"),
    SOL_개발("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL/development"),
    SOL_운영("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL/distribution"),
    SOL_Enz_개발("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL/enterprise_development"),
    SOL_Enz_운영("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL/enterprise_distribution"),
    SOL_Global("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL Global"),
    SOL_Global_Enz("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL Global"),
    구_SOL_개발("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL(구)/development"),
    구_SOL_운영("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL(구)/distribution"),
    구_SOL_Enz_개발("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL(구)/enterprise development"),
    구_SOL_Enz_운영("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL(구)/enterprise distribution"),
    SOLBiz("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOLBiz"),
    구_SOLBiz("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOLBiz(구)"),
    국민주택채권("buildsetting/iOS/MobileProvisioning/대고객앱/국내/국민주택채권"),
    서울시STAX("buildsetting/iOS/MobileProvisioning/대고객앱/국내/서울시STAX"),
    스마트납부("buildsetting/iOS/MobileProvisioning/대고객앱/국내/스마트납부"),
    신한S뱅크mini("buildsetting/iOS/MobileProvisioning/대고객앱/국내/신한S뱅크mini"),
    신한S부가세("buildsetting/iOS/MobileProvisioning/대고객앱/국내/신한S부가세"),
    제로페이Biz("buildsetting/iOS/MobileProvisioning/대고객앱/국내/제로페이Biz"),
    땡겨요("buildsetting/iOS/MobileProvisioning/대고객앱/국내/배달앱"),
    헤이영("buildsetting/iOS/MobileProvisioning/대고객앱/국내/헤이영"),
    Indonesia_Smail("buildsetting/iOS/MobileProvisioning/대고객앱/SMail/Indonesia Smail"),
    SOL알리미("buildsetting/iOS/MobileProvisioning/대고객앱/SMail/SOL알리미"),
    Vietnam_Smail("buildsetting/iOS/MobileProvisioning/대고객앱/SMail/Vietnam Smail"),
    America_Bank("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/America Bank"),
    Cambodia_Bank("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Cambodia Bank"),
    Cambodia_Bank_Old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Cambodia Bank Old"),
    Canada_Bank("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Canada Bank"),
    China_Bank("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/China Bank"),
    China_Bank_Old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/China Bank Old"),
    Global_Smart_Banking("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Global Smart Banking"),
    GMA("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/GMA"),
    Paperless("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Paperless"),
    SBJ_Bank("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/SBJ Bank"),
    SBJ_Bank_Old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/SBJ Bank Old"),
    SOL_India("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/SOL India"),
    SOL_Indonesia("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/SOL Indonesia"),
    SOL_Indonesia_Old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/SOL Indonesia Old"),
    SOL_Vietnam("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Vietnam SOL"),
    SOL_Vietnam_Old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Vietnam SOL Old");

    private String uri;

    ProvisioningRepository(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    private static final Map<String, ProvisioningRepository> PROVISIONING_MAP;

    static{
        Map<String, ProvisioningRepository> map = new ConcurrentHashMap<>();
        for (ProvisioningRepository repository : ProvisioningRepository.values()) {
            map.put(repository.name(), repository);
        }
        PROVISIONING_MAP = Collections.unmodifiableMap(map);
    }

    public static ProvisioningRepository get(String name){
        return StringUtils.hasText(name)?PROVISIONING_MAP.get(name):null;
    }
}
