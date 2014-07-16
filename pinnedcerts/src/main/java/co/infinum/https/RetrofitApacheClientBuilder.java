package co.infinum.https;

import android.content.res.Resources;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.scheme.SocketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import retrofit.client.ApacheClient;
import retrofit.client.Client;

/**
 * Builder for creating Apache HttpClient with pinned certificate that can be used with Retrofit.
 */
public class RetrofitApacheClientBuilder {

    protected HttpClientBuilder httpClientBuilder = new HttpClientBuilder();

    public RetrofitApacheClientBuilder setConnectionTimeout(int connectionTimeout) {
        httpClientBuilder.setConnectionTimeout(connectionTimeout);

        return this;
    }

    public RetrofitApacheClientBuilder setSocketTimeout(int socketTimeout) {
        httpClientBuilder.setSocketTimeout(socketTimeout);

        return this;
    }

    public RetrofitApacheClientBuilder setCookieStore(CookieStore cookieStore) {
        httpClientBuilder.setCookieStore(cookieStore);

        return this;
    }

    public RetrofitApacheClientBuilder pinCertificates(InputStream resourceStream, char[] password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        httpClientBuilder.pinCertificates(resourceStream, password);

        return this;
    }

    public RetrofitApacheClientBuilder pinCertificates(Resources resources, int certificateRawResource, char[] password) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        httpClientBuilder.pinCertificates(resources, certificateRawResource, password);

        return this;
    }

    public RetrofitApacheClientBuilder ignoreCertificates() {
        httpClientBuilder.ignoreCertificates();

        return this;
    }

    public RetrofitApacheClientBuilder registerScheme(String name, SocketFactory factory, int port) {
        httpClientBuilder.registerScheme(name, factory, port);

        return this;
    }

    public RetrofitApacheClientBuilder setHttpPort(int port) {
        httpClientBuilder.setHttpPort(port);

        return this;
    }

    public RetrofitApacheClientBuilder setHttpsPort(int port) {
        httpClientBuilder.setHttpsPort(port);

        return this;
    }

    public Client.Provider build() {

        return new Client.Provider() {

            public Client get() {
                return new ApacheClient(httpClientBuilder.build());
            }
        };
    }
}
