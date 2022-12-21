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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
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
import java.util.Date;

@Component
public class AppleApi{

    private static final String issuer_Id = "69a6de70-3bc8-47e3-e053-5b8c7c11a4d1";
    private static final String keyId = "7JL62P566N";
    private static final String keyPath = "classpath:static/apple/AuthKey_7JL62P566N.p8";

    @Autowired
    ResourceLoader resourceLoader;

    private int CONN_TIME_OUT = 1000 * 30;

    public String getAllRegisteredDevices(){
        String jwt = createJWT();
        return getAllDevices(jwt);
    }

    public String getAllDevices(String jwt) {
        URL url = null;
        try {
            url = new URL("https://api.appstoreconnect.apple.com/v1/devices?limit=200");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            return getConnectResultByX509(jwt,  url);
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
            return getConnectResultByX509(jwt,  url);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 테더링 없이 사용 가능.
     * @param jwt
     * @param url
     * @return
     * @throws NoSuchAlgorithmException
     */
    private String getConnectResultByX509(String jwt, URL url) throws NoSuchAlgorithmException {

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

        } catch (JOSEException e)
        {
            throw new RuntimeException("JWT Transformation failed! "+e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("InvalidKeyException "+e);
        }

        return jwt.serialize();

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

    public String getDeviceInfoFromProfile(String profileId){
        return getDeviceInfo(createJWT(), profileId);
    }


    private String getDeviceInfo(String jwt, String id) {
        URL url = null;
        try {
            url = new URL("https://api.appstoreconnect.apple.com/v1/profiles/"+id+"/devices?limit=100");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Wrong URL!! Visit Appstore Connect API...");
        }
        try {
            return getConnectResultByX509(jwt, url);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getAllBundleId(){
        URL url = null;
        try {
            url = new URL("https://api.appstoreconnect.apple.com/v1/bundleIds?limit=200");
        } catch (MalformedURLException e) {
            throw new RuntimeException("잘못된 url 입니다 : " + url.toString());
        }
        try {
            return getConnectResultByX509(createJWT(), url);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("해당 요청을 처리 할 수 없습니다...");
        }
    }


    public String getBundleIdFromProfile(String id) {
        URL url = null;
        try {
            url = new URL("https://api.appstoreconnect.apple.com/v1/profiles/"+id+"/bundleId");
        } catch (MalformedURLException e) {
            throw new RuntimeException("잘못된 URL 형식입니다.");
        }
        try {
            return getConnectResultByX509(createJWT(), url);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("통신 실패입니다..");
        }
    }

    /**
     * 모든 인증서를 가져오는 기능 필요함
     * @return
     * @throws MalformedURLException
     * @throws NoSuchAlgorithmException
     */
    public String getAllCertificates(){
        try {
            URL url = new URL("https://api.appstoreconnect.apple.com/v1/certificates");
            return getConnectResultByX509(createJWT(),  url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public String getProfileCertificate(String profileId){
        try {
            return getCertificateFromProfile(createJWT(), profileId);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    private String getCertificateFromProfile(String jwt, String id) throws MalformedURLException, NoSuchAlgorithmException {
        URL url = new URL("https://api.appstoreconnect.apple.com/v1/profiles/"+id+"/certificates");
        String response = getConnectResultByX509(jwt, url);
        return response;
    }

    /**
     * token is jwt
     * @param token
     * @return
     * @throws NoSuchAlgorithmException
     * @throws MalformedURLException
     */
    public String createProfile(String token, JsonObject toPostData) throws IOException {
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

            String paramData = toPostData.toString();
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

    public String createProfileNew(String token, JsonObject toPostData) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        SSLContext sslContext = null;
        int result;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }

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

            HttpPost http = null;
            URL url = null;
            try {
                url = new URL("https://api.appstoreconnect.apple.com/v1/profiles");
                http = new HttpPost(url.toURI());
                http.setHeader("Authorization", "Bearer "+ token);
                // post by json
                http.setHeader("Content-Type", "application/json");
                http.setHeader("Accept", "application/json");
            } catch (Exception e) {
                http = new HttpPost(url.toURI());
            }

            StringEntity entity = new StringEntity(toPostData.toString());
            http.setEntity(entity);
            HttpResponse response = httpClient.execute(http);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode = " + statusCode);
            String s = new BasicResponseHandler().handleResponse(response);
            return s;

        } catch (Exception e) {
            throw new AppleAPIException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    /**
     * Response Codes
     * 204 No Content
     * 400 Bad Request
     * 403 Forbidden Request not authorized.
     * 404 Not Found Resource not found.
     * 409 Conflict The provided resource data is not valid.
     */
    public int deleteProfile(String profileId){
        return deleteOldProfile(createJWT(), profileId);
    }

//    private int deleteOldProfile(String token, String id){
//        HttpURLConnection con = null;
//        try{
//            URL url = new URL("https://api.appstoreconnect.apple.com/v1/profiles/"+id);
//            con = (HttpURLConnection) url.openConnection();
//            // post
//            con.setRequestMethod("DELETE");
//            con.setRequestProperty("Authorization", "Bearer "+ token);
//            // post by json
//            con.setRequestProperty("Content-Type", "application/json");
//            con.setRequestProperty("Accept", "application/json");
//            con.setDoOutput(true); // outputstream 사용해서 post body 데이터 전송
//        } catch (IOException e) {
//            throw new RuntimeException("연결 실패...");
//        }
//
//        try {
//            return con.getResponseCode();
//        } catch (IOException e) {
//            throw new RuntimeException("연결 실패...");
//        }
//    }

//    public int deleteProfileOld(String profileId){
//        return deleteOldProfileByX509(createJWT(), profileId);
//    }

    private int deleteOldProfile(String token, String id){
        DefaultHttpClient httpClient = new DefaultHttpClient();
        SSLContext sslContext = null;
        int result;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }

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
            URL url = null;
            try {
                url = new URL("https://api.appstoreconnect.apple.com/v1/profiles/"+id);
                http = new HttpDelete(url.toURI());
                http.setHeader("Authorization", "Bearer "+ token);
                // post by json
                http.setHeader("Content-Type", "application/json");
                http.setHeader("Accept", "application/json");
            } catch (Exception e) {
                http = new HttpPost(url.toURI());
            }

            HttpResponse response = null;
            response = httpClient.execute(http);
            int statusCode = response.getStatusLine().getStatusCode();
            result = statusCode;
            System.out.println("statusCode = " + statusCode);

        } catch (Exception e) {
            throw new AppleAPIException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }

}
