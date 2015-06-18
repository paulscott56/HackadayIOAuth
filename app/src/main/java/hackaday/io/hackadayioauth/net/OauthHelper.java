package hackaday.io.hackadayioauth.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

/**
 * Created by paul on 2015/06/18.
 */
public class OauthHelper {

    private OAuthConsumer mConsumer;
    private OAuthProvider mProvider;
    private String mCallbackUrl;

    public OauthHelper(String consumerKey, String consumerSecret,
                       String scope, String callbackUrl)
            throws UnsupportedEncodingException {
        mConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
        mProvider = new CommonsHttpOAuthProvider(
                "https://www.google.com/accounts/OAuthGetRequestToken?scope="
                        + URLEncoder.encode(scope, "utf-8"),
                "https://www.google.com/accounts/OAuthGetAccessToken",
                "https://www.google.com/accounts/OAuthAuthorizeToken?hd=default");

        //mCallbackUrl = (callbackUrl == null ? OAuth.OUT_OF_BAND : callbackUrl);
    }

}
