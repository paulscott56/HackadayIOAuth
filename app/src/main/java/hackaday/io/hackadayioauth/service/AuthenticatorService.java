package hackaday.io.hackadayioauth.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import hackaday.io.hackadayioauth.account.Authenticator;

/**
 * Created by paul on 2015/06/15.
 */
public class AuthenticatorService extends Service {

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new Authenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
