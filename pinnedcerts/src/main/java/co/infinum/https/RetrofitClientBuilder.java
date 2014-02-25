package co.infinum.https;

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
public class RetrofitClientBuilder {

    protected HttpClientBuilder httpClientBuilder = new HttpClientBuilder();

    public RetrofitClientBuilder setConnectionTimeout(int connectionTimeout) {
        httpClientBuilder.setConnectionTimeout(connectionTimeout);

        return this;
    }

    public RetrofitClientBuilder setSocketTimeout(int socketTimeout) {
        httpClientBuilder.setSocketTimeout(socketTimeout);

        return this;
    }

    public RetrofitClientBuilder setCookieStore(CookieStore cookieStore) {
        httpClientBuilder.setCookieStore(cookieStore);

        return this;
    }

    public RetrofitClientBuilder pinCertificates(InputStream resourceStream, char[] password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        httpClientBuilder.pinCertificates(resourceStream, password);

        return this;
    }

    public RetrofitClientBuilder ignoreCertificates() {
        httpClientBuilder.ignoreCertificates();

        return this;
    }

    public RetrofitClientBuilder registerScheme(String name, SocketFactory factory, int port) {
        httpClientBuilder.registerScheme(name, factory, port);

        return this;
    }

    public RetrofitClientBuilder setHttpPort(int port) {
        httpClientBuilder.setHttpPort(port);

        return this;
    }

    public RetrofitClientBuilder setHttpsPort(int port) {
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
