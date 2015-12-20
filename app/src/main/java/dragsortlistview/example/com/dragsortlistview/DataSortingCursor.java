package dragsortlistview.example.com.dragsortlistview;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.ArrayList;
import java.util.HashMap;

public class DataSortingCursor extends CursorWrapper {

    private HashMap<Integer, Integer> mPositionsMap = new HashMap();

    private HashMap<Integer, Integer> mSortedPositionsMap = new HashMap();

    public DataSortingCursor(Cursor cursor) {
        super(cursor);
        ArrayList<Integer> sortedIds = new ArrayList();
        HashMap<Integer, Integer> map = new HashMap();
        cursor.moveToFirst();
        int endItemId = Constants.FieldValue.EMPTY_FIELD_VALUE;
        int position = 0;
        while (cursor.isAfterLast() == false) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.DB.DataDBTable._ID));
            int nextId = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.DB.DataDBTable.DB_COLUMN_NEXT_ID));
            if (nextId == Constants.FieldValue.EMPTY_FIELD_VALUE) {
                endItemId = id;
            } else {
                map.put(nextId, id);
            }
            mPositionsMap.put(id, position);
            position++;
            cursor.moveToNext();
        }
        if (endItemId != Constants.FieldValue.EMPTY_FIELD_VALUE) {
            sortedIds.add(endItemId);
            Integer id;
            while ((id = map.get(endItemId)) != null) {
                sortedIds.add(0, id);
                endItemId = id;
            }
            map.clear();
            for (int i = 0; i < sortedIds.size(); i++) {
                mSortedPositionsMap.put(i, sortedIds.get(i));
            }
        }

        cursor.moveToFirst();
    }

    private int pos = -1;

    @Override
    public int getCount() {
        return mSortedPositionsMap.size();
    }

    @Override
    public boolean moveToPosition(int position) {
        final int count = getCount();
        if (position >= count) {
            pos = count;
            return false;
        }

        if (position < 0) {
            pos = -1;
            return false;
        }

        final int realPosition;
        Integer id = mSortedPositionsMap.get(position);
        if (id == null) {
            realPosition = -1;
        } else {
            Integer posTemp = mPositionsMap.get(id);
            if (posTemp == null) {
                realPosition = -1;
            } else {
                realPosition = posTemp;
            }
        }
        boolean moved = realPosition == -1 ? true : super.moveToPosition(realPosition);
        if (moved) {
            pos = position;
        } else {
            pos = -1;
        }
        return moved;
    }

    @Override
    public final boolean move(int offset) {
        return moveToPosition(pos + offset);
    }

    @Override
    public final boolean moveToFirst() {
        return moveToPosition(0);
    }

    @Override
    public final boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }

    @Override
    public final boolean moveToNext() {
        return moveToPosition(pos + 1);
    }

    @Override
    public final boolean moveToPrevious() {
        return moveToPosition(pos - 1);
    }

    @Override
    public final boolean isFirst() {
        return pos == 0 && getCount() != 0;
    }

    @Override
    public final boolean isLast() {
        int cnt = getCount();
        return pos == (cnt - 1) && cnt != 0;
    }

    @Override
    public final boolean isBeforeFirst() {
        if (getCount() == 0) {
            return true;
        }
        return pos == -1;
    }

    @Override
    public final boolean isAfterLast() {
        if (getCount() == 0) {
            return true;
        }
        return pos == getCount();
    }

    @Override
    public int getPosition() {
        return pos;
    }

}
