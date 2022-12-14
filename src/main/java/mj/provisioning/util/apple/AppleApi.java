package mj.provisioning.util.apple;

import com.google.gson.JsonObject;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import mj.provisioning.common.exception.AppleAPIException;
import mj.provisioning.common.exception.CustomException;
import mj.provisioning.common.exception.ErrorCode;
import org.apache.http.HttpEntity;
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

    // 30 seconds
    private int CONN_TIME_OUT = 1000 * 30;

    /**
     * ?????? ID??? ????????? ?????? ????????? ????????????.
     * @return
     */
    public String getAllRegisteredDevices(){
        String jwt = createJWT();
        return getAllDevices(jwt);
    }

    private String getAllDevices(String jwt) {
        URL url = null;
        try {
            url = new URL("https://api.appstoreconnect.apple.com/v1/devices?limit=200");
        } catch (MalformedURLException e) {
            throw new CustomException(ErrorCode.URL_ERROR.getMessage(), ErrorCode.URL_ERROR);
        }
        try {
            return getConnectResultByX509(jwt,  url);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * profileId - ??????????????? id
     * name - ?????? ????????? ??????
     * type - ??????????????? ??????
     * prevName - ?????? ??????
     * content - ??????????????? ??????
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
            throw new CustomException(ErrorCode.URL_ERROR.getMessage(), ErrorCode.URL_ERROR);
        }
        try {
            return getConnectResultByX509(jwt,  url);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * ????????? ?????? ?????? ??????.
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
            String responseBody = null;
            /**
             * ??? ?? OUTPUT
             */
            // Time Out
            response = httpClient.execute(http);
            entity = response.getEntity();
            responseBody = EntityUtils.toString(entity, "UTF-8");
            result = responseBody; // json ??????

        } catch (Exception e) {
            throw new AppleAPIException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }

    /**
     * jwt ?????? ?????? ??????
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
            PemReader pemReader = new PemReader(new InputStreamReader(inputStream)); // jar ????????? getFile??? ?????? ?????? ????????? ??????. inputstream?????? ????????????
            PemObject pemObject = pemReader.readPemObject();
            content = pemObject.getContent();

        }catch(IOException e)
        {
            throw new RuntimeException("Private Key read Failed... " + e);
        }
        return content;
    }


    /**
     * ????????? ?????????????????? ???????????? 200??? ?????????, link??? next??? ????????? ???????????? ?????? ????????? ?????? ???????????? ??????
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
            throw new RuntimeException("????????? url ????????? : " + url.toString());
        }
        try {
            return getConnectResultByX509(createJWT(), url);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("?????? ????????? ?????? ??? ??? ????????????...");
        }
    }


    public String getBundleIdFromProfile(String id) {
        URL url = null;
        try {
            url = new URL("https://api.appstoreconnect.apple.com/v1/profiles/"+id+"/bundleId");
        } catch (MalformedURLException e) {
            throw new RuntimeException("????????? URL ???????????????.");
        }
        try {
            return getConnectResultByX509(createJWT(), url);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("?????? ???????????????..");
        }
    }

    /**
     * ?????? ???????????? ???????????? ?????? ?????????
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

    public String createProfileNew(String token, JsonObject toPostData) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        SSLContext sslContext = null;
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
