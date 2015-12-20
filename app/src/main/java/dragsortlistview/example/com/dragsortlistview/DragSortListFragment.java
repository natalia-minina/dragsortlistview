package dragsortlistview.example.com.dragsortlistview;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DragSortListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mDataLV;

    private RecyclerView.Adapter mWrappedAdapter;

    private DataAdapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drag_sort_list, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDataLV = (RecyclerView) getView().findViewById(R.id.dataLV);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mDataLV.setLayoutManager(mLayoutManager);

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();

        // Start dragging after long press
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);

        mAdapter = new DataAdapter(getActivity(), getEmptyCursor());
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mAdapter);

        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        mDataLV.setAdapter(mWrappedAdapter);
        mDataLV.setItemAnimator(animator);
        mRecyclerViewDragDropManager.attachRecyclerView(mDataLV);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                Constants.DB.DataDBTable.CONTENT_URI,
                Constants.DB.DataDBTable.DEFAULT_PROJECTION,
                null,
                null,
                null);
    }

    private Cursor getEmptyCursor() {
        MatrixCursor matrixCursor = new MatrixCursor(Constants.DB.DataDBTable.DEFAULT_PROJECTION);
        return matrixCursor;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.swapCursor(getEmptyCursor());
        }
    }

    @Override
    public void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (mDataLV != null) {
            mDataLV.setItemAnimator(null);
            mDataLV.setAdapter(null);
            mDataLV = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
        mLayoutManager = null;

        super.onDestroyView();
    }

}
