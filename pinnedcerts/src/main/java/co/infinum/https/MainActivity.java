package co.infinum.https;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity that demonstrates the use of HttpClientBuilder and RetrofitClientBuilder.
 */
public class MainActivity extends ActionBarActivity {

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

    private void makeApacheRequest() {
        HttpClientBuilder builder = new HttpClientBuilder()
                .setConnectionTimeout(10000)
                .setSocketTimeout(60000)

    }

    private void makeRetrofitRequest() {

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
