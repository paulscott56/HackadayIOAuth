package hackaday.io.hackadayioauth.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by paul on 2015/06/15.
 */
public class FeedContract {
    private FeedContract() {
    }

    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = "hackaday.io.hackadayioauth.provider";

    /**
     * Base URI. (content://com.example.android.network.sync.basicsyncadapter)
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path component for "entry"-type resources..
     */
    private static final String PATH_ENTRIES = "feeds";

    /**
     * Columns supported by "entries" records.
     */
    public static class Entry implements BaseColumns {
        /**
         * MIME type for lists of entries.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.hackadayioauth.feeds";
        /**
         * MIME type for individual entries.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.hackadayioauth.feeds";

        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "feeds";
        /**
         * Atom ID. (Note: Not to be confused with the database primary key, which is _ID.
         */
        public static final String COLUMN_NAME_ID = "id";

        /**
         * user_id
         */
        public static final String COLUMN_NAME_USER_ID = "user_id";

        /**
         * project_id
         */
        public static final String COLUMN_NAME_PROJECT_ID = "project_id";

        /**
         * user2_id
         */
        public static final String COLUMN_NAME_USER2_ID = "user2_id";

        /**
         * post_id
         */
        public static final String COLUMN_NAME_POST_ID = "post_id";

        /**
         * type
         */
        public static final String COLUMN_NAME_TYPE = "type";

        /**
         * activity
         */
        public static final String COLUMN_NAME_ACTIVITY = "activity";

        public static final String COLUMN_NAME_JSON = "json";


        /**
         * Date article was published.
         */
        public static final String COLUMN_NAME_CREATED = "created";
    }
}