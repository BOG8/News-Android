package com.example.news_app;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import ru.mail.weather.lib.Storage;

/**
 * Created by Олег on 07.03.2017.
 */

public class NewsIntentService extends IntentService {
    private final static String LOG_TAG = NewsIntentService.class.getSimpleName();
    private final static String BACKGROUND = "BACKGROUND";

    public final static String ACTION_NEWS = "action.NEWS";
    public final static String ACTION_NEWS_RESULT_SUCCESS = "action.NEWS_RESULT_SUCCESS";
    public final static String ACTION_NEWS_RESULT_ERROR = "action.NEWS_RESULT_ERROR";
    public final static String EXTRA_NEWS_CITY = "extra.NEWS_TEXT";
    public final static String EXTRA_NEWS_RESULT_RECEIVER = "extra.EXTRA_NEWS_RESULT_RECEIVER";
    public final static String EXTRA_NEWS_BACKGROUND = "extra.NEWS_BACKGROUND";

    public final static int NEWS_RESULT_SUCCESS = 1;
    public final static int NEWS_RESULT_ERROR = 2;

    public NewsIntentService() {
        super("NewsIntentService");
        Log.i(LOG_TAG, "NewsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(LOG_TAG, "onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NEWS.equals(action)) {
                final String currentCategory = Storage.getInstance(this).loadCurrentTopic();
                final String backgroundInfo = intent.getStringExtra(EXTRA_NEWS_BACKGROUND);
                if (backgroundInfo.equals(BACKGROUND)) {
                    Log.i(LOG_TAG, "onHandleIntent BACKGROUND");
                    handleActionNews(currentCategory, null, true);
                } else {
                    Log.i(LOG_TAG, "onHandleIntent NOT_BACKGROUND");
                    final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_NEWS_RESULT_RECEIVER);
                    handleActionNews(currentCategory, receiver, false);
                }
            }
        }
    }

    private void handleActionNews(final String currentCategory, final ResultReceiver receiver, boolean isBackground) {
        Log.i(LOG_TAG, "handleActionNews");
        try {
            final boolean success = NewsProcessor.processCategory(this, currentCategory);
            if (isBackground) {
                final Intent intent = new Intent(success ? ACTION_NEWS_RESULT_SUCCESS : ACTION_NEWS_RESULT_ERROR);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            } else {
                receiver.send(success ? NEWS_RESULT_SUCCESS : NEWS_RESULT_ERROR, null);
            }
        }
        catch (Exception ex) {
            Log.i(LOG_TAG, ex.getMessage());
            if (isBackground) {
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_NEWS_RESULT_ERROR));
            } else {
                receiver.send(NEWS_RESULT_ERROR, null);
            }
        }
    }
}
