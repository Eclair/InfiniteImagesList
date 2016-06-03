package com.eclair.infiniteimageslist.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.eclair.infiniteimageslist.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by andreycherkashin on 6/4/16.
 */
public class ImageDownloadAsyncTask extends AsyncTask<Integer, Float, Bitmap> {
    // Callback interface for progress
    public interface ImageDownloadProgressUpdate {
        void onSuccess(int taskId, Bitmap bitmap);
        void onError(int taskId, String error);
    }

    private final int imageSize = 100;

    private Context context;
    private WeakReference<ImageDownloadProgressUpdate> progressUpdate;
    private Integer currentImageId;
    private File temporaryFile;

    public ImageDownloadAsyncTask(Context context, ImageDownloadProgressUpdate progressUpdate) {
        this.context = context;
        this.progressUpdate = new WeakReference<>(progressUpdate);
    }

    public boolean cancelAndInvalidate() {
        this.cancel(true);
        return this.temporaryFile == null || this.temporaryFile.delete();
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        this.currentImageId = params[0];

        try {
            URL imageUrl = new URL(String.format(context.getString(R.string.image_url_format), this.imageSize, this.currentImageId));
            URLConnection connection = imageUrl.openConnection();
            connection.connect();

            if (connection.getHeaderField("Content-Type") == null) {
                onError("Can't load URL");
                return null;
            }

            if (!isImage(connection)) {
                onError("URL is not an image");
                return null;
            }

            int length = connection.getContentLength();

            this.temporaryFile = File.createTempFile("temporary-image-", extensionFromFileName(imageUrl), context.getCacheDir());

            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream(this.temporaryFile);

            byte data[] = new byte[1024];
            int total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress(((float) total / length));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            onError(e.getLocalizedMessage());
            return null;
        }

        return BitmapFactory.decodeFile(this.temporaryFile.getAbsolutePath());
    }

    private boolean isImage(URLConnection connection) {
        return connection.getHeaderField("Content-Type").startsWith("image/");
    }

    private String extensionFromFileName(URL url) {
        String[] components = url.getFile().split("\\.");
        if (components.length > 1) {
            return "." + components[components.length - 1];
        }
        return "";
    }

    private void onSuccess(final Bitmap bitmap) {
        if (progressUpdate.get() == null) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressUpdate.get().onSuccess(currentImageId, bitmap);
            }
        });
    }

    private void onError(final String error) {
        if (progressUpdate.get() == null) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressUpdate.get().onError(currentImageId, error);
            }
        });
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (bitmap != null) {
            onSuccess(bitmap);
        }
    }
}
