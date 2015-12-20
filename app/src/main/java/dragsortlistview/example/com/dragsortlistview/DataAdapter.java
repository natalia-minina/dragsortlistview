package dragsortlistview.example.com.dragsortlistview;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> implements DraggableItemAdapter<DataAdapter.ViewHolder> {

    private LayoutInflater mInflater;

    private CursorAdapter mCursorAdapter;

    private Context mContext;

    private AsyncQueryHandler mQueryHandler;

    public DataAdapter(Context context, Cursor c) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;

        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
        mQueryHandler = new AsyncQueryHandler(context.getContentResolver()) {
        };

        mCursorAdapter = new CursorAdapter(context, c, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View itemView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                itemView.setClickable(true);

                int[] attrs = new int[]{android.R.attr.selectableItemBackground /* index 0 */};
                TypedArray ta = context.obtainStyledAttributes(attrs);
                Drawable drawableFromTheme = ta.getDrawable(0 /* index */);
                ta.recycle();
                //noinspection deprecation
                itemView.setBackgroundDrawable(drawableFromTheme);

                ViewHolder holder = new ViewHolder(itemView);
                itemView.setTag(holder);
                return itemView;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {

                int id = cursor.getInt(cursor.getColumnIndex(Constants.DB.DataDBTable._ID));
                String name = cursor.getString(cursor.getColumnIndex(Constants.DB.DataDBTable.DB_COLUMN_NAME));
                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder != null) {
                    holder.nameTV.setText(name);
                    holder.id = id;
                }
            }

            @Override
            public long getItemId(int position) {
                Cursor c = getCursor();
                c.moveToPosition(position);
                return c.getLong(c.getColumnIndex(Constants.DB.DataDBTable._ID));
            }
        };
    }

    public void swapCursor(Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return mCursorAdapter.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        return new ViewHolder(v);
    }

    @Override
    public boolean onCheckCanStartDrag(ViewHolder holder, int position, int x, int y) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(ViewHolder viewHolder, int i) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }
        if (fromPosition > toPosition) {
            toPosition--;
        }
        ContentValues cv = new ContentValues();
        cv.put(Constants.DB.UPDATE_ID, getItemId(fromPosition));
        cv.put(Constants.DB.START_LIST_ID, getItemId(0));
        cv.put(Constants.DB.NEW_BEFORE_ID, toPosition < 0 ? Constants.FieldValue.EMPTY_FIELD_VALUE : getItemId(toPosition));
        mQueryHandler.startUpdate(0, null, Constants.DB.DataDBTable.CONTENT_URI, cv, null, null);
    }

    public static class ViewHolder extends AbstractDraggableItemViewHolder {

        public TextView nameTV;

        public int id;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTV = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }

}
