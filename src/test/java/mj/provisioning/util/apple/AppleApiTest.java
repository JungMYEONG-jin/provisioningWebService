package mj.provisioning.util.apple;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import sun.security.ec.ECPrivateKeyImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppleApiTest {

    @Autowired
    AppleApi api;
    @Autowired
    ResourceLoader resourceLoader;
    /**
     * Type IOS_APP_STORE는 디바이스가 없음
     * IOS_APP_DEVELOPMENT만 존재
     * @throws MalformedURLException
     * @throws NoSuchAlgorithmException
     */
    @Test
    void getDeviceInfoFromCurrentProfile() throws MalformedURLException, NoSuchAlgorithmException {
        String id = "Z26MK4GYYD";
        String response = api.getDeviceInfoFromProfile(id);
        System.out.println("response = " + response);
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

    @Test
    void getAllCertificatesTest() {
        String allCertificates = api.getAllCertificates();
        System.out.println("allCertificates = " + allCertificates);
        JsonParser jsonParser = new JsonParser();
        JsonObject asJsonObject = jsonParser.parse(allCertificates).getAsJsonObject();
        JsonArray data = asJsonObject.getAsJsonArray("data");
        System.out.println("data.size() = " + data.size());
        for (JsonElement datum : data) {
            JsonObject object = datum.getAsJsonObject();
            String type = object.get("type").toString().replaceAll("\"", "");
            String id = object.get("id").toString().replaceAll("\"", "");
            JsonObject attributes = object.getAsJsonObject("attributes");
            String serialNumber = attributes.get("serialNumber").toString().replaceAll("\"", "");
            String displayName = attributes.get("displayName").toString().replaceAll("\"", "");
            String name = attributes.get("name").toString().replaceAll("\"", "");
            String expirationDate = attributes.get("expirationDate").toString().replaceAll("\"", "");
            String certificateType = attributes.get("certificateType").toString().replaceAll("\"", "");
        }
    }

    @Test
    void getBundleIdFromProfile() {
        String bundleIdFromProfile = api.getBundleIdFromProfile("Z26MK4GYYD");

        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(bundleIdFromProfile).getAsJsonObject();
        JsonObject data = object.getAsJsonObject("data");
        String type = data.get("type").toString().replaceAll("\"", "");
        String id = data.get("id").toString().replaceAll("\"", "");
        JsonObject attributes = data.getAsJsonObject("attributes");
        String name = attributes.get("name").toString().replaceAll("\"", "");
        String identifier = attributes.get("identifier").toString().replaceAll("\"", "");
        String seedId = attributes.get("seedId").toString().replaceAll("\"", "");


        System.out.println("bundleIdFromProfile = " + bundleIdFromProfile);
    }

    /**
     * id, name,identifier, seedId
     */
    @Test
    void getProfileCertificateTest() {
        String profileCertificate = api.getProfileCertificate("4D64AC92TM");
        System.out.println("profileCertificate = " + profileCertificate);
    }

    @Test
    void getBundleIdTest() {
        String allBundleId = api.getAllBundleId();
        System.out.println("allBundleId = " + allBundleId);
    }

    @Test
    void reflectionTest() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String keyPath = "classpath:static/apple/AuthKey_7JL62P566N.p8";
        Class<?> aClass = Class.forName("sun.security.ec.ECPrivateKeyImpl");
        Constructor<?> constructor = aClass.getConstructor(byte[].class);
        ECPrivateKeyImpl ecPrivateKey = (ECPrivateKeyImpl) constructor.newInstance(readPrivateKey(keyPath));
        System.out.println("ecPrivateKey = " + ecPrivateKey.getAlgorithm());
        System.out.println("ecPrivateKey = " + ecPrivateKey.getS());
    }

    private byte[] readPrivateKey(String keyPath)
    {
        InputStream inputStream = null;
        try {
            inputStream = resourceLoader.getResource(keyPath).getInputStream(); // in native read without spring core
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] content = null;
        try
        {
            PemReader pemReader = new PemReader(new InputStreamReader(inputStream)); // jar 배포시 getFile은 에러 발생 가능성 높음. inputstream으로 읽어오기
            PemObject pemObject = pemReader.readPemObject();
            content = pemObject.getContent();

        }catch(IOException e)
        {
            throw new RuntimeException("Private Key read Failed... " + e);
        }
        return content;
    }
}