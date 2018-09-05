package com.conext;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.conext.utils.LruBitmapCache;
import com.conext.utils.TypefaceUtil;

/**
 * Created by sunil on 26-03-2016.
 */
public class AppManager extends Application {

    private static AppManager mAppContext = null;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppManager mInstance;

    public static final String TAG = AppManager.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "customFont/Roboto-Medium.ttf");

    }

    public AppManager() {
        mAppContext = this;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public static synchronized AppManager getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}