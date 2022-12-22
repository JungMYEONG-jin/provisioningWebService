package mj.provisioning.util;

public class RegexParsing {

    public static String parseQuotations(String str){
        return str.replaceAll("\"","");
    }

    public static String parseDateFormat(String expirationDate){
        expirationDate = expirationDate.replaceAll("\"","").replaceAll("[^0-9]", "");
        expirationDate = expirationDate.substring(0, 4) + "/" + expirationDate.substring(4, 6) + "/" + expirationDate.substring(6, 8);
        return expirationDate;
    }
}
