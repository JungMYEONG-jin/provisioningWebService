package mj.provisioning.util.apple;

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
        JSONArray profileInfo = getProfileInfo(jwt);
        JSONObject obj = null;

        for (Object o : profileInfo) {
            JSONObject temp = (JSONObject) o;
            if (temp.get("prevName").equals(profileName))
            {
                obj = temp;
                break;
            }
        }
        if (obj==null)
            return -1;

        String id = obj.get("id").toString(); // 올드 id
        JSONObject bundleIdFromProfile = getBundleIdFromProfile(jwt, id);
        System.out.println("bundleIdFromProfile = " + bundleIdFromProfile);
        JSONObject certificateFromProfile = getCertificateFromProfile(jwt, id);
        System.out.println("certificateFromProfile = " + certificateFromProfile);
        JSONArray devices = getDeviceInfoFromProfile(jwt, id);
        System.out.println("devices = " + devices);
        String profile = createProfile(jwt, obj.get("name").toString(), obj.get("type").toString(),
                bundleIdFromProfile.get("id").toString(), certificateFromProfile.get("id").toString(), devices);
        JSONParser parser = new JSONParser();
        try {
            JSONObject parse = (JSONObject)parser.parse(profile);
            JSONObject data = (JSONObject)parse.get("data");
            String newId = data.get("id").toString();
            int code = deleteProfile(jwt, id);// 기존 삭제
            return code;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1; // error
    }

    /**
     * links에 next 키가 있다면 계속 크롤링하여 리뷰를 획득한다.
     * @param jwt
     * @param id
     * @return
     * @throws NoSuchAlgorithmException
     * @throws MalformedURLException
     */
    public String getNextReviews(String jwt, String id, String link) throws NoSuchAlgorithmException, MalformedURLException{
        URL url = new URL(link);
        return getConnectResultByX509(jwt, id, url);
    }


    /**
     * 리뷰를 전부 크롤링 하는 함수, 멀티 스레드로 구현 가능할거같은데??
     * @param
     * @param id
     * @return 고객 리뷰, 상담사 답변, 답변 작성일
     * @throws MalformedURLException
     * @throws NoSuchAlgorithmException
     */
    public List<JSONObject> getAllReviews(String id) throws MalformedURLException, NoSuchAlgorithmException {

        List<JSONObject> result = new ArrayList<JSONObject>();
        String jwt = createJWT();
        String reviewDetails = getReviewDetails(jwt, id);
        result.addAll(getReviewList(reviewDetails));

        // 끝까지 작업 시작
        String nextURL = getNextURL(reviewDetails);
        while (nextURL!=null){
            String nextReviews = getNextReviews(jwt, id, nextURL);
            result.addAll(getReviewList(nextReviews)); // 계속 넣기
            nextURL = getNextURL(nextReviews);
            if (nextURL==null)
                break;
        }

        return result;
    }

    // for test
    public String getReviewSubmissions(String jwt, String id) throws NoSuchAlgorithmException, MalformedURLException{
        URL url = new URL("https://api.appstoreconnect.apple.com/v1/apps/"+id+"/reviewSubmissions?include=appStoreVersionForReview");
        return getConnectResultByX509(jwt, id, url);
    }

    // for test
    public String getReviewInfo(String jwt, String id) throws NoSuchAlgorithmException, MalformedURLException{
        URL url = new URL("https://api.appstoreconnect.apple.com/v1/apps/357484932/customerReviews?cursor=AMg.ANcXDEE&limit=200&sort=createdDate");
        return getConnectResultByX509(jwt, id, url);
    }

    // for test
    public String getBuildInfo(String jwt, String id) throws MalformedURLException{
        URL url = new URL("https://api.appstoreconnect.apple.com/v1/apps/"+id+"/builds?limit=1"); // 이름
        return getConnectResult(jwt, id, url);
    }

    /**
     * 테더링 사용할때 사용 가능
     * @param jwt
     * @param id
     * @param url
     * @return
     * @throws MalformedURLException
     */
    private String getConnectResult(String jwt, String id, URL url) throws MalformedURLException {
        String result = "";
        try{
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Bearer "+ jwt);

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = "";
            String res = "";
            while((line=br.readLine())!=null)
            {
                res+=line;
            }

            result = res;
            urlConnection.disconnect();

        } catch (IOException e) {
            throw new AppleAPIException(e);
        }
        return result;
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

    public Map<String, String> getCrawlingInfo(String id) throws MalformedURLException, NoSuchAlgorithmException, ParseException {
        String jwt = createJWT();
        String appVersions = getAppVersions(jwt, id);

        JSONObject obj = parseStrToJson(appVersions);
        JSONArray data = (JSONArray)obj.get("data");
        JSONObject temp = (JSONObject) data.get(0);
        JSONObject attributes = (JSONObject)temp.get("attributes"); // 버전 업데이트일

        Map<String, String> map = new HashMap<String, String>(attributes);

        String appTitle = getAppTitle(jwt, id);

        JSONObject obj2 = parseStrToJson(appTitle);
        JSONObject data1 = (JSONObject)obj2.get("data");
        JSONObject nameAttributes = (JSONObject)data1.get("attributes"); // 이름

        map.put("name", nameAttributes.get("name").toString());
        return map;
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


    private JSONObject parseStrToJson(String str) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(str);
        return obj;
    }


    private List<JSONObject> getReviewList(String reviewDetails){
        JSONObject obj = new JSONObject();
        JSONParser parser = new JSONParser();
        List<JSONObject> result = new ArrayList<JSONObject>();
        try {
            obj = (JSONObject) parser.parse(reviewDetails);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // response 또 따로 가져오려면 성능이 너무 느려짐... 맵에 넣자
        Map<String, JSONObject> responseMap = new HashMap<String, JSONObject>();
        if (obj.containsKey("included")) {
            JSONArray included = (JSONArray) obj.get("included");
            for (Object include : included) {
                JSONObject temp = (JSONObject) include;
                JSONObject attributes = (JSONObject) temp.get("attributes");
                responseMap.put(temp.get("id").toString(), attributes);
            }
        }

        // customer review
        if (obj.containsKey("data")) {
            JSONArray data = (JSONArray) obj.get("data");
            for (Object datum : data) {
                if (datum != null) {
                    JSONObject temp = (JSONObject) datum;
                    JSONObject attributes = (JSONObject) temp.get("attributes");
                    // get 답변자 ID
                    String existReponseID = isExistReponseID(responseMap, temp, attributes);
                    if (existReponseID!=null){
                        if (responseMap.containsKey(existReponseID)) {
                            JSONObject responseData = responseMap.get(existReponseID);
                            for (Object o : responseData.keySet()) {
                                String key = (String) o;
                                attributes.put(key, responseData.get(key).toString());
                            }
                        }
                    }
                    /**
                     * and ios  칼럼 동기화
                     */
                    attributes.put("createdDate", getModifiedDate(attributes.get("createdDate").toString()));
                    if (attributes.containsKey("lastModifiedDate") && attributes.get("lastModifiedDate")!=null) {
                        String lastModifiedDate = getModifiedDate(attributes.get("lastModifiedDate").toString());
                        attributes.put("answeredDate", lastModifiedDate);
                    }
                    else
                        attributes.put("answeredDate", "");
                    attributes.remove("lastModifiedDate");

                    if (!attributes.containsKey("responseBody"))
                        attributes.put("responseBody", "");
                    attributes.put("nickName", attributes.get("reviewerNickname").toString());
                    attributes.remove("reviewerNickname");
                    attributes.put("device", "");
                    attributes.put("appVersion","");
                    attributes.put("osVersion", "");
                    result.add(attributes);
                }
            }
        }
        return result;
    }

    private String getModifiedDate(String lastModifiedDate) {
        lastModifiedDate = lastModifiedDate.replaceAll("[^0-9]+", "");
        lastModifiedDate = lastModifiedDate.substring(0, 14);
        return lastModifiedDate;
    }

    /**
     * 상담사 답변이 달렸는지 체크하는 함수
     * @param responseMap
     * @param temp
     * @param attributes
     * @return
     */
    private String isExistReponseID(Map<String, JSONObject> responseMap, JSONObject temp, JSONObject attributes) {
        if (temp.containsKey("relationships")) {
            JSONObject relationships = (JSONObject) temp.get("relationships");
            if (relationships.containsKey("response")) {
                JSONObject response = (JSONObject) relationships.get("response");
                JSONObject resData = (JSONObject) response.get("data");
                if (resData != null && resData.containsKey("id")) {
                    String id = resData.get("id").toString();
                    return id;
                }
            }
        }
        return null;
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


    /**
     * profileId - 프로비저닝 id
     * name - 새로 저장될 이름
     * type - 프로비저닝 타입
     * prevName - 기존 이름
     * content - 프로비저닝 정보
     * @param jwt
     * @return
     * @throws NoSuchAlgorithmException
     * @throws MalformedURLException
     */
    public JSONArray getProfileInfo(String jwt) throws NoSuchAlgorithmException, MalformedURLException{
        URL url = new URL("https://api.appstoreconnect.apple.com/v1/profiles?limit=200");
        String response = getConnectResultByX509(jwt, "", url);

        LocalDateTime now = LocalDateTime.now();
        now = now.plusYears(1);
        String strToday = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // profile info로 부터 profile id, profileName, profileType 얻기
        JSONParser parser = new JSONParser();
        JSONObject parse = null;
        try {
            parse = (JSONObject)parser.parse(response);
            JSONArray data = (JSONArray)parse.get("data");
            System.out.println("data.size() = " + data.size());
            JSONArray arr = new JSONArray();
            for (Object datum : data) {
                JSONObject dd = (JSONObject) datum;
                JSONObject attributes = (JSONObject)dd.get("attributes");
                JSONObject obj = new JSONObject();
                String profileName = attributes.get("name").toString();
                String profileType = attributes.get("profileType").toString();
                String profileContent = attributes.get("profileContent").toString(); // base64 인코딩된거를 디코드하고 .mobileprovision 형식으로 저장하면 됨.
                String profileId = dd.get("id").toString();
                int i = profileName.lastIndexOf("_");
                String newName = profileName.substring(0, i+1)+strToday;

                obj.put("id", profileId);
                obj.put("name", newName); // 저장될 이름
                obj.put("type", profileType);
                obj.put("prevName", profileName); // 기존 이름
                obj.put("content", profileContent);
                arr.add(obj);
            }
            return arr;
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
