package co.infinum.https.retrofit;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Path;

/**
 * API interface for Retrofit.
 */
public interface GitHubService {

    /**
     * Fetches public information about a user.
     *
     * @param user Github username
     * @param callback callback to notify the results as User object
     */
    @GET("/users/{user}")
    @Headers("User-Agent: hello-pinnedcerts")
    void getUser(
            @Path("user") String user,
            @Header("Authorization") String authorizationHeader,
            Callback<User> callback

    );
}
