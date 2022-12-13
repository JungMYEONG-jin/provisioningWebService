package mj.provisioning.util.apple;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppleApiTest {

    AppleApi api = new AppleApi();
    /**
     * Type IOS_APP_STORE는 디바이스가 없음
     * IOS_APP_DEVELOPMENT만 존재
     * @throws MalformedURLException
     * @throws NoSuchAlgorithmException
     */
    @Test
    void getDeviceInfoFromCurrentProfile() throws MalformedURLException, NoSuchAlgorithmException {
        String id = "L2S6C3JMLN";
        JSONArray deviceInfoFromProfile = api.getDeviceInfoFromProfile(api.createJWT(), id);
        for (Object o : deviceInfoFromProfile) {
            JSONObject it = (JSONObject) o;
            System.out.println("it = " + it);
        }
    }

    /**
     * udid 매치로 찾아서 등록해주자
     * device table을 만들자 우선
     * id, device_id, type, name, udid, deviceClass
     */
    @Test
    void getAllRegisteredDevice() throws MalformedURLException, NoSuchAlgorithmException, ParseException {
        String allDevices = api.getAllDevices(api.createJWT());
        JsonParser parser = new JsonParser();
        JsonObject deviceJson = parser.parse(allDevices).getAsJsonObject();
        JsonArray dataArray = deviceJson.get("data").getAsJsonArray();
        for (JsonElement jsonElement : dataArray) {
            JsonObject object = jsonElement.getAsJsonObject();
            String type = object.get("type").toString().replaceAll("\"", "");
            String deviceId = object.get("id").toString().replaceAll("\"", "");
            JsonObject attributes = object.getAsJsonObject("attributes");
            String name = attributes.get("name").toString().replaceAll("\"", "");
            String deviceClass = attributes.get("deviceClass").toString().replaceAll("\"", "");
            String udid = attributes.get("udid").toString().replaceAll("\"", "");

            System.out.print(" type = " + type);
            System.out.print(" udid = " + udid);
            System.out.print(" deviceClass = " + deviceClass);
            System.out.print(" deviceId = " + deviceId);
            System.out.println(" name = " + name);
        }
        System.out.println("allDevices = " + allDevices);
    }

}