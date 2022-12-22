package mj.provisioning.util;

public class RegexParsing {

    public static String parseQuotations(String str){
        return str.replaceAll("\"","");
    }
}
