package hackaday.io.hackadayioauth;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import hackaday.io.hackadayioauth.account.HackadayIOAccountService;
import hackaday.io.hackadayioauth.net.OAuth;
import hackaday.io.hackadayioauth.provider.FeedContract;
import hackaday.io.hackadayioauth.util.SyncUtils;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;


public class MainActivity extends ActionBarActivity {

    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "hackaday.io.hackadayioauth.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "hackaday.io.hackadayioauth";
    // The account name
    public static final String ACCOUNT = "account";
    private static final String TAG = "Main";
    // Instance fields
    Account mAccount;
    // Content provider scheme
    public static final String SCHEME = "content://";
    // Path for the content provider table
    public static final String TABLE_PATH = "data_table";
    // A content URI for the content provider's data table
    Uri mUri;
    // A content resolver for accessing the provider
    ContentResolver mResolver;
    private SimpleCursorAdapter mAdapter;
    private Object mSyncObserverHandle;

    /**
     * Options menu used to populate ActionBar.
     */
    private Menu mOptionsMenu;

    /**
     * Projection for querying the content provider.
     */
    private static final String[] PROJECTION = new String[]{
            FeedContract.Entry._ID,
            FeedContract.Entry.COLUMN_NAME_ID,
            FeedContract.Entry.COLUMN_NAME_POST_ID,
            FeedContract.Entry.COLUMN_NAME_ACTIVITY,
            FeedContract.Entry.COLUMN_NAME_TYPE,
            FeedContract.Entry.COLUMN_NAME_PROJECT_ID,
            FeedContract.Entry.COLUMN_NAME_CREATED,
            FeedContract.Entry.COLUMN_NAME_JSON,
            FeedContract.Entry.COLUMN_NAME_USER2_ID,
            FeedContract.Entry.COLUMN_NAME_USER_ID,
    };

    /** Column index for _ID */
    private static final int COLUMN_ID = 0;
    /** Column index for published */
    private static final int COLUMN_NAME_CREATED = 6;
    /** Column index for the json */
    private static final int COLUMN_NAME_JSON = 7;

    /**
     * List of Cursor columns to read from when preparing an adapter to populate the ListView.
     */
    private static final String[] FROM_COLUMNS = new String[]{
            FeedContract.Entry.COLUMN_NAME_JSON,
            FeedContract.Entry.COLUMN_NAME_CREATED
    };

    /**
     * List of Views which will be populated by Cursor data.
     */
    private static final int[] TO_FIELDS = new int[]{
            android.R.id.text1,
            android.R.id.text2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUri = new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .path(TABLE_PATH)
                .build();

        SyncUtils.CreateSyncAccount(this);
        mAdapter = new SimpleCursorAdapter(
                this,       // Current context
                android.R.layout.simple_list_item_activated_2,  // Layout for individual rows
                null,                // Cursor
                FROM_COLUMNS,        // Cursor columns to use
                TO_FIELDS,           // Layout fields to use
                0                    // No flags
        );

        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                if (i == COLUMN_NAME_CREATED
                        ) {
                    // Convert timestamp to human-readable date
                    Time t = new Time();
                    t.set(cursor.getLong(i));
                    ((TextView) view).setText(t.format("%Y-%m-%d %H:%M"));
                    return true;
                } else {
                    // Let SimpleCursorAdapter handle other fields automatically
                    return false;
                }
            }
        });

        OAuth auth = new OAuth("fkXPcIluWCvA1YKY", "Z13hZo6tHeJFsBF9uWF9bwCukdLRIT5DqCwoJv580XymflLT", "http://localhost?code=");
        try {
            auth.setupProvider("https://auth.hackaday.io/access_token", "https://auth.hackaday.io/access_token", "https://hackaday.io/authorize", "test","utf-8");
            String tok = auth.getRequestToken();
            Log.i(TAG, tok);
            auth.getAccessToken(tok);
            auth.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.menu_refresh:
                SyncUtils.TriggerRefresh();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSyncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

    /**
     * Crfate a new anonymous SyncStatusObserver. It's attached to the app's ContentResolver in
     * onResume(), and removed in onPause(). If status changes, it sets the state of the Refresh
     * button. If a sync is active or pending, the Refresh button is replaced by an indeterminate
     * ProgressBar; otherwise, the button itself is displayed.
     */
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        /** Callback invoked with the sync adapter status changes. */
        @Override
        public void onStatusChanged(int which) {
            runOnUiThread(new Runnable() {
                /**
                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
                 * runs on the UI thread.
                 */
                @Override
                public void run() {
                    // Create a handle to the account that was created by
                    // SyncService.CreateSyncAccount(). This will be used to query the system to
                    // see how the sync status has changed.
                    Account account = HackadayIOAccountService.GetAccount();
                    if (account == null) {
                        // GetAccount() returned an invalid value. This shouldn't happen, but
                        // we'll set the status to "not refreshing".
                        setRefreshActionButtonState(false);
                        return;
                    }

                    // Test the ContentResolver to see if the sync adapter is active or pending.
                    // Set the state of the refresh button accordingly.
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, FeedContract.CONTENT_AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, FeedContract.CONTENT_AUTHORITY);
                    setRefreshActionButtonState(syncActive || syncPending);
                }
            });
        }
    };

    /**
     * Set the state of the Refresh button. If a sync is active, turn on the ProgressBar widget.
     * Otherwise, turn it off.
     *
     * @param refreshing True if an active sync is occuring, false otherwise
     */
    public void setRefreshActionButtonState(boolean refreshing) {
        if (mOptionsMenu == null) {
            return;
        }

        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);
        if (refreshItem != null) {
            if (refreshing) {
                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }

}
