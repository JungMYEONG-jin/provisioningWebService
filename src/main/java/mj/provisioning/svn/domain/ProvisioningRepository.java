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
    foreignerbank_old("buildsetting/iOS/MobileProvisioning/대고객앱/국내/Global S Bank(구)"),
    foreignerbankEnz_old("buildsetting/iOS/MobileProvisioning/대고객앱/국내/Global S Bank(구)"),
    intermax_dev("buildsetting/iOS/MobileProvisioning/대고객앱/국내/intermax"),
    intermax_dist("buildsetting/iOS/MobileProvisioning/대고객앱/국내/intermax"),
    nextBankingEnz("buildsetting/iOS/MobileProvisioning/대고객앱/국내/nextBanking"),
    poney("buildsetting/iOS/MobileProvisioning/대고객앱/국내/Poney"),
    sol_dev("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL/development"),
    sol_dist("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL/distribution"),
    solEnz_dev("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL/enterprise_development"),
    solEnz_dist("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL/enterprise_distribution"),
    foreignerbank_dev("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL Global"),
    foreignerbank_dist("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL Global"),
    foreignerbankEnz_dist("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL Global"),
    foreignerbankEnz_dev("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL Global"),
    oldsol_dev("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL(구)/development"),
    oldsol_dist("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL(구)/distribution"),
    oldsolEnz_dev("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL(구)/enterprise development"),
    oldsolEnz_dist("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOL(구)/enterprise distribution"),
    sbizbank("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOLBiz"),
    sbizbankEnz("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOLBiz"),
    sbizbankEnz_old("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOLBiz(구)"),
    sbizbank_old("buildsetting/iOS/MobileProvisioning/대고객앱/국내/SOLBiz(구)"),
    ebond("buildsetting/iOS/MobileProvisioning/대고객앱/국내/국민주택채권"),
    ebondEnz("buildsetting/iOS/MobileProvisioning/대고객앱/국내/국민주택채권"),
    stax("buildsetting/iOS/MobileProvisioning/대고객앱/국내/서울시STAX"),
    staxEnz("buildsetting/iOS/MobileProvisioning/대고객앱/국내/서울시STAX"),
    smarttaxpaper("buildsetting/iOS/MobileProvisioning/대고객앱/국내/스마트납부"),
    sbankmini("buildsetting/iOS/MobileProvisioning/대고객앱/국내/신한S뱅크mini"),
    cutrade("buildsetting/iOS/MobileProvisioning/대고객앱/국내/신한S부가세"),
    zeropay("buildsetting/iOS/MobileProvisioning/대고객앱/국내/제로페이Biz"),
    o2o("buildsetting/iOS/MobileProvisioning/대고객앱/국내/배달앱"),
    heyoung("buildsetting/iOS/MobileProvisioning/대고객앱/국내/헤이영"),
    smailid("buildsetting/iOS/MobileProvisioning/대고객앱/SMail/Indonesia Smail"),
    smail("buildsetting/iOS/MobileProvisioning/대고객앱/SMail/SOL알리미"),
    smailvn("buildsetting/iOS/MobileProvisioning/대고객앱/SMail/Vietnam Smail"),
    global_sa("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/America Bank"),
    global_kh("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Cambodia Bank"),
    global_kh_old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Cambodia Bank Old"),
    global_khEnz_old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Cambodia Bank Old"),
    global_ca("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Canada Bank"),
    global_cn("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/China Bank"),
    global_cn_old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/China Bank Old"),
    global_cnEnz_old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/China Bank Old"),
    global_gi("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Global Smart Banking"),
    gma("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/GMA"),
    paperless("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Paperless"),
    global_jp("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/SBJ Bank"),
    global_jp_old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/SBJ Bank Old"),
    global_jpEnz_old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/SBJ Bank Old"),
    global_in("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/SOL India"),
    global_id("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/SOL Indonesia"),
    global_id_old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/SOL Indonesia Old"),
    global_idEnz_old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/SOL Indonesia Old"),
    global_vn("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Vietnam SOL"),
    global_vn_old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Vietnam SOL Old"),
    global_vnEnz_old("buildsetting/iOS/MobileProvisioning/대고객앱/글로벌/Vietnam SOL Old"),
    ;

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
