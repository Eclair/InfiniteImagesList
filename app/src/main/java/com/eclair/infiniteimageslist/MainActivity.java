package com.eclair.infiniteimageslist;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.eclair.infiniteimageslist.adapters.InfiniteImageListAdapter;
import com.eclair.infiniteimageslist.helpers.ImagesHandler;
import com.eclair.infiniteimageslist.network.ImageDownloadAsyncTask;

public class MainActivity extends AppCompatActivity implements ImageDownloadAsyncTask.ImageDownloadProgressUpdate {

    private ListView listView;
    private ImagesHandler imagesHandler;
    private InfiniteImageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.listView = (ListView)this.findViewById(R.id.listView);

        this.imagesHandler = new ImagesHandler(this, this);

        this.adapter = new InfiniteImageListAdapter(this, this.imagesHandler, this.imagesHandler);
        this.listView.setAdapter(this.adapter);
        this.listView.setOnScrollListener(this.adapter);
    }

    @Override
    public void onSuccess(int taskId, Bitmap bitmap) {
        // On image loaded we should reload listview
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(int taskId, String error) {

    }
}
