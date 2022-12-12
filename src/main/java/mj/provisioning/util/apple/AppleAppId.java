package mj.provisioning.util.apple;

public enum AppleAppId {

    smarttax("1180862826"),
    noface("1194492265"),
    poney("1201584268"),
    senior("1174226278"),
    missionplus("486789090"),
    safe2chssf("896037244"),
    ebond("481951052"),
    mfolio("1169292742"),
    salimi("486872386"),
    sbizbank("587766126"),
    sbank("357484932"),
    sunnyalarm("1163682534"),
    smailid("1163682534"),
    sunnyclub("1064933073"),
    shinhanvn("1071033810"),
    sunnycalculator("1177867277"),
    sunnyswatch("1062479593"),
    shinhancn("1143462205"),
    sregister("1052014390"),
    sunnybank("1006963773"),
    shinhanid("1287409348"),
    shinhanca("1093365921"),
    smailvn("1016762804"),
    s_tongjang("1039324990"),
    shinhansa("1093362779"),
    ssurtax("725400385"),
    shinhangi("1131925580"),
    shinhankh("1071033100"),
    smailcn("1249194034"),
    sbankmini("458061594"),
    foreignerbank("1190468026"),
    O2O("1598850912");

    private String appPkg;

    AppleAppId(String appPkg) {
        this.appPkg = appPkg;
    }

    public String getAppPkg(){
        return appPkg;
    }

}
