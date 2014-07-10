package co.infinum.https;

import android.content.res.Resources;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Builder for creating Apache HttpClient with pinned certificate.
 */
public class HttpClientBuilder {


    static final String BOUNCY_CASTLE = "BKS";

    static final String TLS = "TLS";

    /**
     * HTTP scheme name in SchemeRegistry.
     */
    static final String HTTP_SCHEME = "http";

    /**
     * HTTPS scheme name in SchemeRegistry.
     */
    static final String HTTPS_SCHEME = "https";

    /**
     * Default HTTP port.
     */
    static final int HTTP_PORT = 80;

    /**
     * Default HTTPS port.
     */
    static final int HTTPS_PORT = 443;

    /**
     * If set to true all HTTPS requests will ignore other side certificate.
     * Beware that this could pose a security risk and should be used only for
     * development purposes.
     */
    protected boolean ignoreHttpsCertificates = false;

    /**
     * KeyStore containing certificates for HTTPS requests.
     */
    protected KeyStore keyStore = null;

    protected HttpParams httpParams = new BasicHttpParams();

    protected SchemeRegistry schemeRegistry = new SchemeRegistry();

    protected int httpPort = HTTP_PORT;

    protected int httpsPort = HTTPS_PORT;

    protected CookieStore cookieStore = new BasicCookieStore();

    public HttpClientBuilder setConnectionTimeout(int connectionTimeout) {
        HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);

        return this;
    }

    public HttpClientBuilder setSocketTimeout(int socketTimeout) {
        HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);

        return this;
    }

    public HttpClientBuilder setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;

        return this;
    }

    public HttpClientBuilder pinCertificates(InputStream resourceStream, char[] password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        ignoreHttpsCertificates = false;

        keyStore = KeyStore.getInstance(BOUNCY_CASTLE);
        keyStore.load(resourceStream, password);

        return this;
    }

    public HttpClientBuilder pinCertificates(Resources resources, int certificateRawResource, char[] password) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        InputStream in = resources.openRawResource(certificateRawResource);
        pinCertificates(in, password);

        return this;
    }

    public HttpClientBuilder ignoreCertificates() {
        ignoreHttpsCertificates = true;

        return this;
    }

    public HttpClientBuilder registerScheme(String name, SocketFactory factory, int port) {
        schemeRegistry.register(new Scheme(name, factory, port));

        return this;
    }

    public HttpClientBuilder setHttpPort(int port) {
        httpPort = port;

        return this;
    }

    public HttpClientBuilder setHttpsPort(int port) {
        httpsPort = port;

        return this;
    }

    public DefaultHttpClient build() {
        DefaultHttpClient httpClient;

        schemeRegistry.register(new Scheme(HTTP_SCHEME, PlainSocketFactory.getSocketFactory(), httpPort));

        if (!ignoreHttpsCertificates && keyStore != null) {
            try {
                schemeRegistry.register(new Scheme(HTTPS_SCHEME, new SSLSocketFactory(keyStore), httpsPort));
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        } else {
            schemeRegistry.register(new Scheme(HTTPS_SCHEME, SSLSocketFactory.getSocketFactory(), httpsPort));
        }

        ThreadSafeClientConnManager clientMan = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

        if (ignoreHttpsCertificates) {
            httpClient = new IgnorantHttpClient();
        } else {
            httpClient = new DefaultHttpClient(clientMan, httpParams);
        }

        if(cookieStore != null) {
            httpClient.setCookieStore(cookieStore);
        }

        return httpClient;
    }
}
