package co.infinum.https.retrofit;

import android.util.Log;

import retrofit.RestAdapter;

/**
 * Logger for Retrofit.
 */
public class Logger implements RestAdapter.Log {

    @Override
    public void log(String message) {
        Log.d("Retrofit", message);
    }
}
