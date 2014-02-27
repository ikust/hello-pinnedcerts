package co.infinum.https.retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Model that represents User object that can be fetched via Github API.
 */
public class User {

    public String login;

    public int id;

    public String avatarUrl;

    public String gravatarId;

    @SerializedName("html_url")
    public String htmlUrl;

}
