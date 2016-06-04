package com.eclair.infiniteimageslist.helpers;

import android.content.Context;
import android.graphics.Bitmap;

import com.eclair.infiniteimageslist.adapters.InfiniteImageListAdapter;
import com.eclair.infiniteimageslist.network.ImageDownloadAsyncTask;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by andreycherkashin on 6/4/16.
 */
public class ImagesHandler implements InfiniteImageListAdapter.OnVisibleAreaListener, InfiniteImageListAdapter.ImagesProvider {

    private Context context;
    private HashMap<Integer, ImageDownloadAsyncTask> tasksMap;
    private WeakReference<ImageDownloadAsyncTask.ImageDownloadProgressUpdate> imageProgressUpdate;

    public ImagesHandler(Context context, ImageDownloadAsyncTask.ImageDownloadProgressUpdate imageProgressUpdate) {
        this.context = context;
        this.imageProgressUpdate = new WeakReference<>(imageProgressUpdate);
        this.tasksMap = new HashMap<>();
    }

    @Override
    public void onVisibleAreaChange(int startIndex, int endIndex) {
        for(Iterator<Map.Entry<Integer, ImageDownloadAsyncTask>> it = this.tasksMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, ImageDownloadAsyncTask> entry = it.next();
            if(entry.getKey() < startIndex || entry.getKey() > endIndex) {
                entry.getValue().cancelAndInvalidate();
                it.remove();
            }
        }
    }

    @Override
    public void onVisibleAreaFix(int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; i++) {
            if (!this.tasksMap.containsKey(i)) {
                ImageDownloadAsyncTask downloadTask = new ImageDownloadAsyncTask(this.context, imageProgressUpdate.get());
                downloadTask.execute(i);
                this.tasksMap.put(i, downloadTask);
            }
        }
    }

    @Override
    public Bitmap imageForId(int imageId) {
        if (this.tasksMap.containsKey(imageId)) {
            return this.tasksMap.get(imageId).bitmap;
        }
        return null;
    }
}
