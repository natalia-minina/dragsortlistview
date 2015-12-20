package dragsortlistview.example.com.dragsortlistview;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DataProvider extends ContentProvider {

    private DBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String orderBy = null;
        Cursor c = query(projection, selection, selectionArgs, orderBy);
        DataSortingCursor dataSortingCursor = new DataSortingCursor(c);
        dataSortingCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return dataSortingCursor;
    }

    private Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Constants.DB.DataDBTable.DB_TABLE);
        qb.setProjectionMap(Constants.DB.DataDBTable.DEFAULT_PROJECTION_MAP);
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count = 0;
        Integer updateId = values.getAsInteger(Constants.DB.UPDATE_ID);
        Integer newBeforeId = values.getAsInteger(Constants.DB.NEW_BEFORE_ID);
        Integer startListId = values.getAsInteger(Constants.DB.START_LIST_ID);
        if (updateId == null || newBeforeId == null || startListId == null) {
            return count;
        }
        db.beginTransaction();
        try {
            selection = Constants.DB.DataDBTable._ID + "=" + updateId + " OR " + Constants.DB.DataDBTable.DB_COLUMN_NEXT_ID + "=" + updateId;
            if (newBeforeId != Constants.FieldValue.EMPTY_FIELD_VALUE) {
                selection += " OR " + Constants.DB.DataDBTable._ID + "=" + newBeforeId;
            }
            Cursor c = query(Constants.DB.DataDBTable.DEFAULT_PROJECTION, selection, null, null);
            if (c.getCount() > 0 && c.getCount() <= 3) {
                c.moveToFirst();
                int nextIdForUpdateId = Constants.FieldValue.EMPTY_FIELD_VALUE;
                int nextIdForNewBeforeId = Constants.FieldValue.EMPTY_FIELD_VALUE;
                int idForNextUpdateId = Constants.FieldValue.EMPTY_FIELD_VALUE;
                while (c.isAfterLast() == false) {
                    int id = c.getInt(c.getColumnIndexOrThrow(Constants.DB.DataDBTable._ID));
                    int nextId = c.getInt(c.getColumnIndexOrThrow(Constants.DB.DataDBTable.DB_COLUMN_NEXT_ID));
                    if (id == updateId) {
                        nextIdForUpdateId = nextId;
                    } else if (id == newBeforeId) {
                        nextIdForNewBeforeId = nextId;
                    } else {
                        idForNextUpdateId = id;
                    }
                    c.moveToNext();
                }
                c.close();

                ContentValues toUpdateCV = new ContentValues();
                if (idForNextUpdateId != Constants.FieldValue.EMPTY_FIELD_VALUE) {
                    toUpdateCV.put(Constants.DB.DataDBTable.DB_COLUMN_NEXT_ID, nextIdForUpdateId);
                    selection = Constants.DB.DataDBTable._ID + "=" + idForNextUpdateId;
                    count = db.update(Constants.DB.DataDBTable.DB_TABLE, toUpdateCV, selection, null);
                }

                toUpdateCV.put(Constants.DB.DataDBTable.DB_COLUMN_NEXT_ID,
                        newBeforeId != Constants.FieldValue.EMPTY_FIELD_VALUE ? nextIdForNewBeforeId : startListId);
                selection = Constants.DB.DataDBTable._ID + "=" + updateId;
                count += db.update(Constants.DB.DataDBTable.DB_TABLE, toUpdateCV, selection, null);

                if (newBeforeId != Constants.FieldValue.EMPTY_FIELD_VALUE) {
                    toUpdateCV.put(Constants.DB.DataDBTable.DB_COLUMN_NEXT_ID, updateId);
                    selection = Constants.DB.DataDBTable._ID + "=" + newBeforeId;
                    count += db.update(Constants.DB.DataDBTable.DB_TABLE, toUpdateCV, selection, null);
                }

                db.setTransactionSuccessful();
                getContext().getContentResolver().notifyChange(uri, null);
            } else {
                c.close();
            }
        } finally {
            db.endTransaction();
        }
        return count;
    }
}
