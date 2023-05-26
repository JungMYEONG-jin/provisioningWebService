package mj.provisioning.util.apple;

import com.google.gson.JsonObject;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import mj.provisioning.common.exception.CustomException;
import mj.provisioning.common.exception.ErrorCode;
import mj.provisioning.device.application.data.DeviceCreateDto;
import mj.provisioning.device.domain.Device;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private static final String Prefix = "Bearer ";

    @Autowired
    ResourceLoader resourceLoader;

    // 30 seconds
    private int CONN_TIME_OUT = 1000 * 30;

    /**
     * 현재 ID에 등록된 모든 기기를 가져온다.
     * @return
     */
    public String getAllRegisteredDevices(){
        String jwt = createJWT();
        URL url = getUrl("https://api.appstoreconnect.apple.com/v1/devices?limit=200");
        return transferToTargetURL(jwt,  url);
    }

    /**
     * url 변환
     * @param param
     * @return
     */
    private URL getUrl(String param) {
        URL url = null;
        try {
            url = new URL(param);
        } catch (MalformedURLException e) {
            throw new CustomException(ErrorCode.URL_ERROR.getMessage(), ErrorCode.URL_ERROR);
        }
        return url;
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
        URL url = getUrl("https://api.appstoreconnect.apple.com/v1/profiles?limit=200");
        String jwt = createJWT();
        return transferToTargetURL(jwt,  url);
    }

    /**
     * 테더링 없이 사용 가능.
     * @param jwt
     * @param url
     * @return
     * @throws NoSuchAlgorithmException
     */
    private String transferToTargetURL(String jwt, URL url) {

        String result = "";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        SSLContext sslContext = getSslContext();

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
                http.setHeader("Authorization", Prefix + jwt);
            } catch (Exception e) {
                http = new HttpPost(url.toURI());
            }

            HttpResponse response = null;
            HttpEntity entity = null;
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
            throw new CustomException(ErrorCode.API_ERROR.getMessage(), ErrorCode.API_ERROR);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }

    /**
     * apple 인증을 위한 jwt 인증 토큰 생성
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

        SignedJWT jwt = new SignedJWT(header,claimsSet);

        try{
            ECPrivateKey ecPrivateKey = new ECPrivateKeyImpl(readPrivateKey(keyPath));
            JWSSigner jwsSigner = new ECDSASigner(ecPrivateKey.getS());
            jwt.sign(jwsSigner);

        } catch (JOSEException e)
        {
            throw new CustomException(ErrorCode.JWT_TRANSFORM_FAILED.getMessage(), ErrorCode.JWT_TRANSFORM_FAILED);
        } catch (InvalidKeyException e) {
            throw new CustomException(ErrorCode.INVALID_KEY.getMessage(), ErrorCode.INVALID_KEY);
        }

        return jwt.serialize();
    }

    private byte[] readPrivateKey(String keyPath)
    {
        InputStream inputStream = null;
        try {
            inputStream = resourceLoader.getResource(keyPath).getInputStream();
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
            throw new CustomException(ErrorCode.IO_FAILED.getMessage(), ErrorCode.IO_FAILED);
        }
        return content;
    }


    /**
     * 한번에 가져올수있는 리스트는 200 최대
     * link에 next token 체크 필요.
     * 리뷰 수집할때 아니면 쓸일없음...
     * @param reviewDetails
     * @return
     */
    private String getNextURL(String reviewDetails){
        JSONObject obj = new JSONObject();
        JSONParser parser = new JSONParser();
        try {
            obj = (JSONObject) parser.parse(reviewDetails);
        } catch (ParseException e) {
            throw new CustomException(ErrorCode.PARSE_FAILED.getMessage(), ErrorCode.PARSE_FAILED);
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
        URL url = getUrl("https://api.appstoreconnect.apple.com/v1/profiles/"+id+"/devices?limit=100");
        return transferToTargetURL(jwt, url);
    }

    public String getAllBundleId(){
        URL url = getUrl("https://api.appstoreconnect.apple.com/v1/bundleIds?limit=200");
        return transferToTargetURL(createJWT(), url);
    }


    public String getBundleIdFromProfile(String id) {
        URL url = getUrl("https://api.appstoreconnect.apple.com/v1/profiles/"+id+"/bundleId");
        return transferToTargetURL(createJWT(), url);
    }

    /**
     * 모든 인증서를 가져오는 기능 필요함
     * @return
     * @throws MalformedURLException
     * @throws NoSuchAlgorithmException
     */
    public String getAllCertificates(){
        URL url = getUrl("https://api.appstoreconnect.apple.com/v1/certificates");
        return transferToTargetURL(createJWT(),  url);
    }


    public String getProfileCertificate(String id){
        String jwt = createJWT();
        URL url = getUrl("https://api.appstoreconnect.apple.com/v1/profiles/"+id+"/certificates");
        return transferToTargetURL(jwt, url);
    }

    public String createProfileNew(String token, JsonObject toPostData) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        SSLContext sslContext = getSslContext();

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
            URL url = getUrl("https://api.appstoreconnect.apple.com/v1/profiles");
            try {
                http = new HttpPost(url.toURI());
                http.setHeader("Authorization", Prefix + token);
                // post by json
                http.setHeader("Content-Type", "application/json");
                http.setHeader("Accept", "application/json");
            } catch (Exception e) {
                http = new HttpPost(url.toURI());
            }

            StringEntity entity = new StringEntity(toPostData.toString(), "UTF-8");
            http.setEntity(entity);
            HttpResponse response = httpClient.execute(http);
            return new BasicResponseHandler().handleResponse(response);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.API_ERROR.getMessage(), ErrorCode.API_ERROR);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    private SSLContext getSslContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(ErrorCode.ALGORITHM_NOT_EXIST.getMessage(), ErrorCode.ALGORITHM_NOT_EXIST);
        }
        return sslContext;
    }

    /**
     * Response Codes
     * 204 No Content
     * 400 Bad Request
     * 403 Forbidden Request not authorized.
     * 404 Not Found Resource not found.
     * 409 Conflict The provided resource data is not valid.
     */
    public int deleteProfile(String id){
        String token = createJWT();
        DefaultHttpClient httpClient = new DefaultHttpClient();
        SSLContext sslContext = getSslContext();
        int result;

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
            URL url = getUrl("https://api.appstoreconnect.apple.com/v1/profiles/"+id);
            try {
                http = new HttpDelete(url.toURI());
                http.setHeader("Authorization", Prefix + token);
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
        } catch (Exception e) {
            throw new CustomException(ErrorCode.API_ERROR.getMessage(), ErrorCode.API_ERROR);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }

    // Register DEVICE
    // 예상 계획 기존 디바이스 전부 삭제후
    // excel 읽어서 차례로 등록
    // 그리고 기기 전부 등록해서 프로비전이 파일 업데이트 하면 됨.
    public String registerDevice(String token, DeviceCreateDto info) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        SSLContext sslContext = getSslContext();

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
                url = new URL("https://api.appstoreconnect.apple.com/v1/devices");
                http = new HttpPost(url.toURI());
                http.setHeader("Authorization", Prefix + token);
                // post by json
                http.setHeader("Content-Type", "application/json");
                http.setHeader("Accept", "application/json");
            } catch (Exception e) {
                http = new HttpPost(url.toURI());
            }
            JsonObject param = new JsonObject();
            param.addProperty("type", "devices");
            JsonObject attributes = new JsonObject();
            attributes.addProperty("name", info.getNAME());
            attributes.addProperty("platform", info.getPLATFORM());
            attributes.addProperty("udid", info.getIDENTIFIER());
            param.add("attributes", attributes);
            JsonObject deviceCreateRequest = new JsonObject();
            deviceCreateRequest.add("data", param);
            StringEntity entity = new StringEntity(deviceCreateRequest.toString(), "UTF-8");
            http.setEntity(entity);
            HttpResponse response = httpClient.execute(http);
            return new BasicResponseHandler().handleResponse(response);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.API_ERROR.getMessage(), ErrorCode.API_ERROR);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    public void disableUserDevice(Device device){
        String jwt = createJWT();
        disableDevice(jwt, device);
    }

    // EDIT DEVICE
    // 예상 계획 기존 디바이스 전부 삭제후
    // excel 읽어서 차례로 등록
    // 그리고 기기 전부 등록해서 프로비전이 파일 업데이트 하면 됨.
    private String disableDevice(String token, Device device) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        SSLContext sslContext = getSslContext();

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

            HttpPatch http = null;
            URL url = getUrl("https://api.appstoreconnect.apple.com/v1/devices/"+device.getDeviceId());
            try {
                http = new HttpPatch(url.toURI());
                http.setHeader("Authorization", Prefix + token);
                // post by json
                http.setHeader("Content-Type", "application/json");
                http.setHeader("Accept", "application/json");
            } catch (Exception e) {
                http = new HttpPatch(url.toURI());
            }
            JsonObject param = new JsonObject();
            param.addProperty("type", "devices");
            param.addProperty("id", device.getDeviceId());
            JsonObject attributes = new JsonObject();
            attributes.addProperty("name", device.getName());
            attributes.addProperty("status", "DISABLED");
            param.add("attributes", attributes);
            JsonObject deviceDisableRequest = new JsonObject();
            deviceDisableRequest.add("data", param);
            StringEntity entity = new StringEntity(deviceDisableRequest.toString(), "UTF-8");
            http.setEntity(entity);
            HttpResponse response = httpClient.execute(http);
            return new BasicResponseHandler().handleResponse(response);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.API_ERROR.getMessage(), ErrorCode.API_ERROR);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

}
