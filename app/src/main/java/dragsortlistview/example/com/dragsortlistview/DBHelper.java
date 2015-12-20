package dragsortlistview.example.com.dragsortlistview;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "DragSortListViewDB";

    private static final int DB_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + Constants.DB.DataDBTable.DB_TABLE + "("
            + Constants.DB.DataDBTable._ID + " integer primary key autoincrement, "
            + Constants.DB.DataDBTable.DB_COLUMN_NAME + " text not null, "
            + Constants.DB.DataDBTable.DB_COLUMN_NEXT_ID + " integer REFERENCES " + Constants.DB.DataDBTable.DB_TABLE + "(" + Constants.DB.DataDBTable._ID + "));";

    private Context mContext;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        ContentValues prevCv = null;
        int prevId = Constants.FieldValue.EMPTY_FIELD_VALUE;
        for (int i = 1; i <= Constants.TestData.ITEMS_COUNT_IN_TABLE; i++) {
            ContentValues cv = new ContentValues();
            cv.put(Constants.DB.DataDBTable.DB_COLUMN_NAME, mContext.getString(R.string.table_item_start_name) + " " + i);
            cv.put(Constants.DB.DataDBTable.DB_COLUMN_NEXT_ID, Constants.FieldValue.EMPTY_FIELD_VALUE);
            long id = db.insert(Constants.DB.DataDBTable.DB_TABLE, null, cv);

            if (prevCv != null) {
                prevCv.put(Constants.DB.DataDBTable.DB_COLUMN_NEXT_ID, (int) id);
                db.update(Constants.DB.DataDBTable.DB_TABLE, prevCv,
                        Constants.DB.DataDBTable._ID + "=" + prevId, null);
            }
            prevCv = new ContentValues(cv);
            prevId = (int) id;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.DB.DataDBTable.DB_TABLE);
        onCreate(db);
    }
}
