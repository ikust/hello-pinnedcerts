package co.infinum.https;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import co.infinum.https.retrofit.GitHubService;
import co.infinum.https.retrofit.Logger;
import co.infinum.https.retrofit.User;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.Response;

/**
 * Activity that demonstrates the use of HttpClientBuilder and RetrofitClientBuilder. The main idea
 * is to show how to pin certificates to Apache and Retrofit clients and that requests to unauthorized
 * host's will be forbidden.
 * <p>
 * There are three examples that can be run from app by using options in the actionbar menu item.
 * <ul>
 *  <li>The first action demonstrates how to use HttpClientBuilder and make a valid request to the
 *  host whose certificate is pinned</li>
 *  <li>The second action demonstrates how to use RetrofitClientBuilder and make a valid request to
 *  the host whose certificate is pinned</li>
 *  <li>The last action demonstrates what happens if a request is being meade to a host signed with
 *  a certificate that isn't pinned</li>
 * </ul>
 * <p>
 * As a side note, EventBus library has been used to simplify callback from the Apache client.
 */
public class MainActivity extends ActionBarActivity {

    /**
     * Test URL.
     */
    private static final String TEST_URL = "https://api.github.com";

    /**
     * This URL will be forbidden since only the urls for host defined via certificate keystore
     * are allowed.
     */
    private static final String FORBIDDEN_URL = "https://www.google.hr";

    /**
     * Test Github username.
     */
    private static final String USER = "ikust";

    /**
     * Password for the certificate store.
     */
    private static final char[] STORE_PASS = new char[]{'t', 'e', 's', 't', 'i', 'n', 'g'};

    /**
     * TextView that displays request status or error.
     */
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = (TextView) findViewById(R.id.statusTextView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * An example of simple request with Apache HTTP client with pinned certificate.
     */
    private void makeApacheRequest() {
        HttpGet request = new HttpGet(TEST_URL);
        request.addHeader("User-Agent", "hello-pinnedcerts");

        EventBus.getDefault().post(request);
    }

    /**
     * Executes Apache HTTPS request on background thread.
     *
     * @param request
     */
    public void onEventAsync(HttpGet request) {

        try {
            DefaultHttpClient httpClient = new HttpClientBuilder()
                    .setConnectionTimeout(10000)
                    .setSocketTimeout(60000)
                    .setHttpPort(80)
                    .setHttpsPort(443)
                    .setCookieStore(new BasicCookieStore())
                    .pinCertificates(getResources(), R.raw.keystore, STORE_PASS)
                    .build();

            HttpResponse response = httpClient.execute(request);

            EventBus.getDefault().post(response);
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(request.getURI().toString()+" "+e.getMessage());
        } catch (CertificateException e) {
            e.printStackTrace();
            EventBus.getDefault().post(request.getURI().toString()+" "+e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            EventBus.getDefault().post(request.getURI().toString()+" "+e.getMessage());
        } catch (KeyStoreException e) {
            e.printStackTrace();
            EventBus.getDefault().post(request.getURI().toString()+" "+e.getMessage());
        }
    }

    /**
     * Called after Apache request was completed. Shows status in label.
     *
     * @param response
     */
    public void onEventMainThread(HttpResponse response) {
        statusTextView.setText(response.getStatusLine().toString());
    }

    /**
     * Called after there was an error executing Apache request. Shows exception in label.
     *
     * @param e
     */
    public void onEventMainThread(String e) {
        statusTextView.setText(e);
    }


    /**
     * Executes Retrofit request.
     */
    private void makeRetrofitRequest() {
        try {
            Client.Provider retrofitClient = new RetrofitClientBuilder()
                    .setConnectionTimeout(10000)
                    .setSocketTimeout(60000)
                    .setHttpPort(80)
                    .setHttpsPort(443)
                    .setCookieStore(new BasicCookieStore())
                    .pinCertificates(getResources(), R.raw.keystore, STORE_PASS)
                    .build();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setServer(TEST_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new Logger())
                    .setClient(retrofitClient)
                    .build();

            GitHubService service = restAdapter.create(GitHubService.class);

            service.getUser(USER, new Callback<User>() {

                @Override
                public void success(User user, Response response) {
                    statusTextView.setText(response.getStatus() + " " + response.getReason());
                }

                @Override
                public void failure(RetrofitError error) {
                    statusTextView.setText(TEST_URL+ " "+error.getMessage());
                }
            });
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Demonstrates that a request to a host with certificate different than the pinned one will fail.
     */
    private void makeForbiddenRequest() {
        HttpGet request = new HttpGet(FORBIDDEN_URL);
        request.addHeader("User-Agent", "hello-pinnedcerts");

        EventBus.getDefault().post(request);
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_apache:
                makeApacheRequest();
                return true;
            case R.id.action_retrofit:
                makeRetrofitRequest();
                return true;
            case R.id.action_not_allowed:
                makeForbiddenRequest();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
