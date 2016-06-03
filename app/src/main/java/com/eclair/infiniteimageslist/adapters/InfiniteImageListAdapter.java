package com.eclair.infiniteimageslist.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eclair.infiniteimageslist.R;

/**
 * Created by andreycherkashin on 6/3/16.
 */
public class InfiniteImageListAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
    private Context context;
    private final int pageSize = 100;
    private final int visibleThreshold = 5;
    private int count = pageSize;

    public InfiniteImageListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item, null);
        }

        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        TextView textView = (TextView)view.findViewById(R.id.textView);

        textView.setText(String.format(context.getString(R.string.image_title_format), getItemId(position)));

        return view;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL || scrollState == SCROLL_STATE_FLING) {
            // TODO: Invalidate only non-visible images
            Log.d("IMAGES", "INVALIDATE (from " + view.getFirstVisiblePosition() + " to " + view.getLastVisiblePosition() + ")");
        }
        if (scrollState == SCROLL_STATE_IDLE) {
            // TODO: Load new images
            Log.d("IMAGES", "LOAD (from " + view.getFirstVisiblePosition() + " to " + view.getLastVisiblePosition() + ")");
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // Infinite scrolling: just add one more page when user scrolls to the end of current list
        if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + this.visibleThreshold)) {
            this.count += this.pageSize;
            this.notifyDataSetChanged();
        }
    }
}
