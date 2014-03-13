package co.infinum.https;

import android.content.res.Resources;

import com.squareup.okhttp.OkHttpClient;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import retrofit.client.OkClient;

/**
 * Builder for creating Square OkHttpClient with pinned certificate that can be used with Retrofit.
 */
public class RetrofitClientBuilder {

    protected OkHttpClient okHttpClient = new OkHttpClient();

    public RetrofitClientBuilder setConnectionTimeout(int connectionTimeout) {
        okHttpClient.setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);

        return this;
    }

    public RetrofitClientBuilder setCookieStore(CookieHandler cookieHandler) {
        okHttpClient.setCookieHandler(cookieHandler);

        return this;
    }

    public RetrofitClientBuilder pinCertificates(InputStream resourceStream, char[] password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, KeyManagementException {
        KeyStore keyStore = KeyStore.getInstance(HttpClientBuilder.BOUNCY_CASTLE);
        keyStore.load(resourceStream, password);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

        kmf.init(keyStore, password);
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        okHttpClient.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
        okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());

        return this;
    }

    public RetrofitClientBuilder pinCertificates(Resources resources, int certificateRawResource, char[] password) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException, KeyManagementException {
        InputStream in = resources.openRawResource(certificateRawResource);
        pinCertificates(in, password);

        return this;
    }

    public RetrofitClientBuilder ignoreCertificates() throws NoSuchAlgorithmException, KeyManagementException {
        X509TrustManager easyTrustManager = new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // Oh, I am easy!
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // Oh, I am easy!
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

        };

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {
                easyTrustManager
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());

        okHttpClient.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        okHttpClient.setSslSocketFactory(sc.getSocketFactory());

        return this;
    }

    public OkClient build() {
        return new OkClient(okHttpClient);
    }

}
