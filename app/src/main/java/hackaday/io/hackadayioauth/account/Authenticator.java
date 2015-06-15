package hackaday.io.hackadayioauth.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import hackaday.io.hackadayioauth.activities.OAuthAccountActivity;

/**
 * Created by paul on 2015/06/15.
 */
public class Authenticator extends AbstractAccountAuthenticator {

    private static final String TAG = "Authenticator";
    protected String mAccountType;
    protected String mOAuthTokenType;
    protected String mOAuthSecretType;

    protected Class mAccountActivityKlass;

    private final Context mContext;

    public Authenticator(Context context) {
        super(context);

        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response,
                                 String accountType) {
        Log.v(TAG, "OAuth editProperties()");

        // Core OAuth has no properties. This may be overridden.
        // TODO: should this send a user to some sort of OAuth browser page for
        // the specific service?
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
                             String accountType, String authTokenType,
                             String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {

        Log.v(TAG, "OAuth addAccount()");

        Bundle result = new Bundle();

        if (!accountType.equals(mAccountType)) {
            result.putString(AccountManager.KEY_ERROR_MESSAGE, String.format(
                    "Invalid accountType sent to OAuth: %s", accountType));
            return result;
        }

        // Purposefully ignoring requiredFeatures. OAuth has none.

        addAuthActivityToBundle(response, authTokenType, result);
        return result;
    }

    /**
     * @param response
     * @param authTokenType
     * @param bundle
     */
    private void addAuthActivityToBundle(AccountAuthenticatorResponse response,
                                         String authTokenType, Bundle bundle) {
        Intent intent = new Intent(mContext, mAccountActivityKlass);
        intent.putExtra(OAuthAccountActivity.PARAM_AUTHTOKEN_TYPE,
                authTokenType);
        intent.putExtra(OAuthAccountActivity.PARAM_ACCOUNT_TYPE, mAccountType);
        intent.putExtra(OAuthAccountActivity.PARAM_OAUTH_TOKEN_TYPE,
                mOAuthTokenType);
        intent.putExtra(OAuthAccountActivity.PARAM_OAUTH_SECRET_TYPE,
                mOAuthSecretType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
                response);

        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
                                     Account account, Bundle options) throws NetworkErrorException {
        Log.v(TAG, "OAuth confirmCredentials()");

        // There is no "confirm credentials" equivalent for OAuth, since
        // everything is handled through the browser.
        // TODO: return null; instead?
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response,
                               Account account, String authTokenType, Bundle options)
            throws NetworkErrorException {
        Log.v(TAG, "OAuth getAuthToken()");

        Bundle result = new Bundle();

        if (!authTokenType.equals(mOAuthTokenType)
                || !authTokenType.equals(mOAuthSecretType)) {
            result.putString(AccountManager.KEY_ERROR_MESSAGE, String.format(
                    "Invalid OAuth authTokenType: %s", authTokenType));
            return result;
        }

		/*
		 * OAuth by default has no way to re-request an authToken. The whole
		 * multi-step workflow must be repeated. For particular OAuth providers
		 * such as Facebook who provide extensions for expiring tokens, override
		 * this method with custom logic.
		 */

        addAuthActivityToBundle(response, authTokenType, result);
        return result;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        Log.v(TAG, "OAuth getAuthTokenLabel()");

        if (authTokenType.equals(mOAuthTokenType)) {
            return "OAuth Token";
        } else if (authTokenType.equals(mOAuthSecretType)) {
            return "OAuth Token Secret";
        }

        return "";
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
                                    Account account, String authTokenType, Bundle options)
            throws NetworkErrorException {
        Log.v(TAG, "OAuth updateCredentials()");

        // Updating credentials makes no sense in the context of OAuth.
        // TODO: return null; instead?
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
                              Account account, String[] features) throws NetworkErrorException {
        Log.v(TAG, "OAuth hasFeatues()");

        // Features are definable per-authenticator. OAuth has none.
        Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }
}
