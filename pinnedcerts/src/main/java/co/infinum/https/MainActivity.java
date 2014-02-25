package co.infinum.https;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * Activity that demonstrates the use of HttpClientBuilder and RetrofitClientBuilder.
 */
public class MainActivity extends ActionBarActivity {

    /**
     * Test URL.
     */
    private static final String TEST_URL = "https://api.github.com/users/ikust/repos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        EventBus.getDefault().post(request);
    }

    /**
     * Executes Apache HTTPS request on background thread.
     *
     * @param request
     */
    public void onEventAsync(HttpGet request) {
        DefaultHttpClient httpClient = new HttpClientBuilder()
                .setConnectionTimeout(10000)
                .setSocketTimeout(60000)
                .build();

        try {
            HttpResponse response = httpClient.execute(request);

            EventBus.getDefault().post(response);
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(e);
        }
    }

    public void onEventMainThread(HttpResponse response) {

    }

    public void onEventMainThread(IOException e) {

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

        switch(id) {
            case R.id.action_apache:

                return true;

            case R.id.action_retrofit:

                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
