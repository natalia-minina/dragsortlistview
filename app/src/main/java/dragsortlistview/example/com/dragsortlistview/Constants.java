package dragsortlistview.example.com.dragsortlistview;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

public class Constants {

    public static final class TestData {

        public static final int ITEMS_COUNT_IN_TABLE = 50;
    }

    public static final class DB {

        public static final String AUTHORITY = "dragsortlistview.example.com.dragsortlistview.DataDB";

        private static final String SCHEME = "content://";

        public static final class DataDBTable implements BaseColumns {

            public static final String DB_TABLE = "data";

            public static final String DB_COLUMN_NAME = "name";

            public static final String DB_COLUMN_NEXT_ID = "next_id";

            public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/" + DB_TABLE);

            public static final String[] DEFAULT_PROJECTION = new String[]{
                    _ID,
                    DB_COLUMN_NAME,
                    DB_COLUMN_NEXT_ID
            };

            public static final HashMap DEFAULT_PROJECTION_MAP = new HashMap();

            static {
                for (int i = 0; i < DEFAULT_PROJECTION.length; i++) {
                    DEFAULT_PROJECTION_MAP.put(
                            DEFAULT_PROJECTION[i],
                            DEFAULT_PROJECTION[i]);
                }
            }
        }

        public static final String UPDATE_ID = "UPDATE_ID";

        public static final String NEW_BEFORE_ID = "NEW_BEFORE_ID";

        public static final String START_LIST_ID = "START_LIST_ID";
    }

    public static final class FieldValue {

        public static final int EMPTY_FIELD_VALUE = -1;
    }


    public static final class Debug {

        public static final String TAG = "dragsortlistview";
    }
}
