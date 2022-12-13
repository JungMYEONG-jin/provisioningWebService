package mj.provisioning.util.apple;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import mj.provisioning.exception.AppleAPIException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import sun.security.ec.ECPrivateKeyImpl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.interfaces.ECPrivateKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class AppleApi{

    private static final String issuer_Id = "69a6de70-3bc8-47e3-e053-5b8c7c11a4d1";
    private static final String keyId = "7JL62P566N";
    private static final String keyPath = "static/apple/AuthKey_7JL62P566N.p8";


    private int CONN_TIME_OUT = 1000 * 30;
    public String getAppVersions(String jwt, String id) throws NoSuchAlgorithmException, MalformedURLException {
        URL url = new URL("https://api.appstoreconnect.apple.com/v1/apps"+"/"+ id +"/appStoreVersions"+"?limit=1"); // 버전 업데이트날짜
        return getConnectResultByX509(jwt, id, url);
    }

    public String getAppTitle(String jwt, String id) throws NoSuchAlgorithmException, MalformedURLException {
        URL url = new URL("https://api.appstoreconnect.apple.com/v1/apps/"+id); // 이름
        return getConnectResultByX509(jwt, id, url);
    }

    public String getReviewDetails(String jwt, String id) throws NoSuchAlgorithmException, MalformedURLException{
        URL url = new URL("https://api.appstoreconnect.apple.com/v1/apps/"+id+"/customerReviews"+"?include=response&sort=-createdDate&limit=200");
        return getConnectResultByX509(jwt, id, url);
    }

    public String getAllRegisteredDevices(){
        String jwt = createJWT();
        return getAllDevices(jwt);
    }

    public String getAllDevices(String jwt) {
        URL url = null;
        try {
            url = new URL("https://api.appstoreconnect.apple.com/v1/devices");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            return getConnectResultByX509(jwt, "", url);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int updateProfile(String jwt, String profileName) throws IOException, NoSuchAlgorithmException {
//        String profileInfo = getProfileInfo();
//        JSONObject obj = null;
//
//        for (Object o : profileInfo) {
//            JSONObject temp = (JSONObject) o;
//            if (temp.get("prevName").equals(profileName))
//            {
//                obj = temp;
//                break;
//            }
//        }
//        if (obj==null)
//            return -1;
//
//        String id = obj.get("id").toString(); // 올드 id
//        JSONObject bundleIdFromProfile = getBundleIdFromProfile(jwt, id);
//        System.out.println("bundleIdFromProfile = " + bundleIdFromProfile);
//        JSONObject certificateFromProfile = getCertificateFromProfile(jwt, id);
//        System.out.println("certificateFromProfile = " + certificateFromProfile);
//        JSONArray devices = getDeviceInfoFromProfile(jwt, id);
//        System.out.println("devices = " + devices);
//        String profile = createProfile(jwt, obj.get("name").toString(), obj.get("type").toString(),
//                bundleIdFromProfile.get("id").toString(), certificateFromProfile.get("id").toString(), devices);
//        JSONParser parser = new JSONParser();
//        try {
//            JSONObject parse = (JSONObject)parser.parse(profile);
//            JSONObject data = (JSONObject)parse.get("data");
//            String newId = data.get("id").toString();
//            int code = deleteProfile(jwt, id);// 기존 삭제
//            return code;
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return -1; // error
    }

    /**
     * profileId - 프로비저닝 id
     * name - 새로 저장될 이름
     * type - 프로비저닝 타입
     * prevName - 기존 이름
     * content - 프로비저닝 정보
     * @return
     * @throws NoSuchAlgorithmException
     * @throws MalformedURLException
     */
    public String getProfileInfo(){
        URL url = null;
        String jwt = createJWT();
        try {
            url = new URL("https://api.appstoreconnect.apple.com/v1/profiles?limit=200");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            return getConnectResultByX509(jwt, "", url);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 테더링 없이 사용 가능.
     * @param jwt
     * @param id
     * @param url
     * @return
     * @throws NoSuchAlgorithmException
     */
    private String getConnectResultByX509(String jwt, String id, URL url) throws NoSuchAlgorithmException {

        String result = "";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        SSLContext sslContext = SSLContext.getInstance("SSL");

        try {
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] arg0, String arg1)
                        throws CertificateException {

                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {

                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { trustManager },
                    new SecureRandom());
            SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext,
                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Scheme sch = new Scheme("https", 443, socketFactory);
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);

            HttpParams httpParam = httpClient.getParams();
            org.apache.http.params.HttpConnectionParams.setConnectionTimeout(httpParam, CONN_TIME_OUT);
            org.apache.http.params.HttpConnectionParams.setSoTimeout(httpParam, CONN_TIME_OUT);

            HttpRequestBase http = null;
            try {
                http = new HttpGet(url.toURI());
                http.setHeader("Authorization", "Bearer "+ jwt);
            } catch (Exception e) {
                http = new HttpPost(url.toURI());
            }

            HttpResponse response = null;
            HttpEntity entity = null;
            HttpRequest request = null;
            String responseBody = null;
            /**
             * ??? ?? OUTPUT
             */
            // Time Out
            response = httpClient.execute(http);
            entity = response.getEntity();
            responseBody = EntityUtils.toString(entity, "UTF-8");
            result = responseBody; // json 형식

        } catch (Exception e) {
            throw new AppleAPIException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return result;


    }

    /**
     * jwt 인증 토큰 생성
     * @return
     */
    public String createJWT( )
    {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(keyId).type(JOSEObjectType.JWT).build();

        JWTClaimsSet claimsSet = new JWTClaimsSet();
        Date now = new Date();
        claimsSet.setIssuer(issuer_Id);
        claimsSet.setIssueTime(now);
        claimsSet.setExpirationTime(new Date(now.getTime()+900000)); // exp 15 minutes
        claimsSet.setAudience("appstoreconnect-v1");
//        claimsSet.setClaim("scope", "GET /v1/apps/"+appId+"/appInfos");

        SignedJWT jwt = new SignedJWT(header,claimsSet);

        try{
            ECPrivateKey ecPrivateKey = new ECPrivateKeyImpl(readPrivateKey(keyPath));
            JWSSigner jwsSigner = new ECDSASigner(ecPrivateKey.getS());
            jwt.sign(jwsSigner);

        }catch(InvalidKeyException e)
        {
            throw new RuntimeException("JWT Private Key read Failed... " + e);
        }catch (JOSEException e)
        {
            throw new RuntimeException("JWT Transformation failed! "+e);
        }

        return jwt.serialize();

    }

    private byte[] readPrivateKey(String keyPath)
    {
        InputStream inputStream = null;
        inputStream = this.getClass().getClassLoader().getResourceAsStream(keyPath); // in native read without spring core
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


    /**
     * 한번에 가져올수있는 리스트는 200가 최대임, link에 next가 있는지 체크해서 계속 돌리기 위해 체크하는 함수
     * @param reviewDetails
     * @return
     */
    private String getNextURL(String reviewDetails){
        JSONObject obj = new JSONObject();
        JSONParser parser = new JSONParser();
        try {
            obj = (JSONObject) parser.parse(reviewDetails);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject data = (JSONObject)obj.get("links");
        if(data.containsKey("next") && data.get("next")!=null)
            return data.get("next").toString();
        return null;
    }

    public JSONArray getDeviceInfoFromProfile(String jwt, String id) throws MalformedURLException, NoSuchAlgorithmException {
        URL url = new URL("https://api.appstoreconnect.apple.com/v1/profiles/"+id+"/devices?limit=100");
        String response =  getConnectResultByX509(jwt, id, url);
        JSONParser parser = new JSONParser();
        JSONObject parse = null;
        JSONArray res = new JSONArray();
        try {
            parse = (JSONObject)parser.parse(response);
            JSONArray data = (JSONArray)parse.get("data");
            if (data!=null) {
                for (Object datum : data) {
                    JSONObject dd = (JSONObject) datum;
                    JSONObject temp = new JSONObject();
                    String deviceId = dd.get("id").toString();
                    String type = dd.get("type").toString();
                    temp.put("id", deviceId);
                    temp.put("type", type);
                    res.add(temp);
                }
            }
            return res;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res; // 빈값
    }

    public JSONObject getBundleIdFromProfile(String jwt, String id) throws MalformedURLException, NoSuchAlgorithmException {
        URL url = new URL("https://api.appstoreconnect.apple.com/v1/profiles/"+id+"/bundleId");
        String response = getConnectResultByX509(jwt, id, url);
        JSONParser parser = new JSONParser();
        JSONObject parse = null;
        try {
            parse = (JSONObject)parser.parse(response);
            JSONObject data = (JSONObject)parse.get("data");
            String bundleID = data.get("id").toString();
            String type = data.get("type").toString();
            data.clear();
            data.put("type", type);
            data.put("id", bundleID);
            return data;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getCertificateFromProfile(String jwt, String id) throws MalformedURLException, NoSuchAlgorithmException {
        URL url = new URL("https://api.appstoreconnect.apple.com/v1/profiles/"+id+"/certificates");
        String response = getConnectResultByX509(jwt, id, url);
        JSONParser parser = new JSONParser();
        try {
            JSONObject parse = (JSONObject)parser.parse(response);
            JSONArray data = (JSONArray)parse.get("data");
            // get latest
            JSONObject o =(JSONObject)data.get(0);
            String type = o.get("type").toString();
            String certificateId = o.get("id").toString();
            o.clear();
            o.put("type", type);
            o.put("id", certificateId);
            return o;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * token is jwt
     * @param token
     * @return
     * @throws NoSuchAlgorithmException
     * @throws MalformedURLException
     */
    public String createProfile(String token, String name, String profileType, String bundleId, String certificateId, JSONArray devicesArr) throws IOException {
        HttpURLConnection con = null;
        BufferedReader br = null;
        StringBuffer sb = null;
        String result = "";

        try{
            URL url = new URL("https://api.appstoreconnect.apple.com/v1/profiles");
            con = (HttpURLConnection) url.openConnection();
            // post
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer "+ token);
            // post by json
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true); // outputstream 사용해서 post body 데이터 전송

            JSONObject param = new JSONObject();

            JSONObject attr = new JSONObject();
            attr.put("name", name);
            attr.put("profileType", profileType);

            JSONObject relationships = new JSONObject();
            JSONObject bundle = new JSONObject();
            JSONObject data = new JSONObject();
            JSONObject certificates = new JSONObject();
            JSONArray certificateData = new JSONArray();

            JSONObject devices = new JSONObject();
            // bundle set
            data.put("type", "bundleIds");
            data.put("id", bundleId);
            bundle.put("data", data);
            relationships.put("bundleId", bundle); //json
            // certificate set
            JSONObject certiData = new JSONObject();
            certiData.put("type", "certificates");
            certiData.put("id", certificateId);
            certificateData.add(certiData); // json arr
            //certificate set
            certificates.put("data", certificateData);//json array
            relationships.put("certificates", certificates);
            // device set
            devices.put("data", devicesArr);
            relationships.put("devices", devices);


            param.put("type", "profiles");
            param.put("attributes", attr);
            param.put("relationships", relationships);

            JSONObject toPostData =new JSONObject();
            toPostData.put("data", param);

            String paramData = toPostData.toJSONString();
            System.out.println("paramData = " + paramData);
            try{
                OutputStream os = con.getOutputStream();
                byte[] req = paramData.getBytes("utf-8"); //post write
                os.write(req, 0, req.length);
                os.close();
            }catch (Exception e){
                e.printStackTrace();
            }


            br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
            StringBuilder response = new StringBuilder();
            while((result=br.readLine())!=null){
                response.append(result.trim());
            }
            result = response.toString();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param token
     * @param id
     * @return
     * @throws IOException
     * Response Codes
     * 204 No Content
     * 400 Bad Request
     * 403 Forbidden Request not authorized.
     * 404 Not Found Resource not found.
     * 409 Conflict The provided resource data is not valid.
     */
    public int deleteProfile(String token, String id) throws IOException {
        HttpURLConnection con = null;
        BufferedReader br = null;
        StringBuffer sb = null;
        String result = "";

        try{
            URL url = new URL("https://api.appstoreconnect.apple.com/v1/profiles/"+id);
            con = (HttpURLConnection) url.openConnection();
            // post
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Authorization", "Bearer "+ token);
            // post by json
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true); // outputstream 사용해서 post body 데이터 전송


        } catch (IOException e) {
            e.printStackTrace();
        }
        return con.getResponseCode();
    }

    // file svn에 올리는거 까지


}
