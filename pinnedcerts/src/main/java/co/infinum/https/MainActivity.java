package co.infinum.https;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import de.greenrobot.event.EventBus;

/**
 * Activity that demonstrates the use of HttpClientBuilder and RetrofitClientBuilder.
 */
public class MainActivity extends ActionBarActivity {

    /**
     * Test URL.
     */
    private static final String TEST_URL = "https://api.github.com";

    /**
     * Password for the certificate store.
     */
    private static final char[] STORE_PASS = new char[]{'t', 'e', 's', 't', 'i', 'n', 'g'};


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

            String responseBody = convertStreamToString(response.getEntity().getContent());

            Log.d("Response", responseBody);

            EventBus.getDefault().post(response);
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(e);
        } catch (CertificateException e) {
            e.printStackTrace();
            EventBus.getDefault().post(e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            EventBus.getDefault().post(e);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            EventBus.getDefault().post(e);
        }
    }

    public void onEventMainThread(HttpResponse response) {
        statusTextView.setText(response.getStatusLine().toString());
    }

    public void onEventMainThread(IOException e) {
        statusTextView.setText(e.getStackTrace().toString());
    }

    public static String convertStreamToString(InputStream is) {
        /*
		 * To convert the InputStream to String we use the BufferedReader.readLine() method. We iterate until the
		 * BufferedReader return null which means there's no more data to read. Each line will appended to a
		 * StringBuilder and returned as String.
		 */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private void makeRetrofitRequest() {

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
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
