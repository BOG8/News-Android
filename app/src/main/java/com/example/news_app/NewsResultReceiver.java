package com.example.news_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * Created by Олег on 07.03.2017.
 */

@SuppressLint("ParcelCreator")
class NewsResultReceiver extends ResultReceiver {
    private final static String LOG_TAG = NewsResultReceiver.class.getSimpleName();

    private final int requestId;
    private ServiceHelper.NewsResultListener listener;

    NewsResultReceiver(int requestId, final Handler handler) {
        super(handler);
        this.requestId = requestId;
        Log.i(LOG_TAG, "NewsResultReceiver");
    }

    void setListener(final ServiceHelper.NewsResultListener listener) {
        Log.i(LOG_TAG, "setListener");
        this.listener = listener;
    }

    @Override
    protected void onReceiveResult(final int resultCode, final Bundle resultData) {
        Log.i(LOG_TAG, "onReceiveResult");
        if (listener != null) {
            final boolean success = (resultCode == NewsIntentService.NEWS_RESULT_SUCCESS);
            listener.onNewsResult(success);
        }
        ServiceHelper.getInstance().removeListener(requestId);
    }
}
