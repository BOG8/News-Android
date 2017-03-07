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

    public final static String ACTION_NEWS = "action.NEWS";
    public final static String ACTION_NEWS_RESULT_SUCCESS = "action.NEWS_RESULT_SUCCESS";
    public final static String ACTION_NEWS_RESULT_ERROR = "action.NEWS_RESULT_ERROR";
    public final static String EXTRA_NEWS_CITY = "extra.NEWS_TEXT";
    public final static String EXTRA_NEWS_RESULT_RECEIVER = "extra.EXTRA_NEWS_RESULT_RECEIVER";
    public final static String EXTRA_NEWS_BACKGROUND = "extra.NEWS_BACKGROUND";
    public final static String EXTRA_NEWS_IS_BACKGROUND = "extra.NEWS_IS_BACKGROUND";

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
                if (backgroundInfo.equals("BACKGROUND")) {
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
        Log.i(LOG_TAG, "handleActionWeather");
//        final Bundle data = new Bundle();
        Log.i(LOG_TAG, "handleActionWeather1");
        try {
            Log.i(LOG_TAG, "handleActionWeather2");
            final boolean success = NewsProcessor.processCategory(this, currentCategory);
            Log.i(LOG_TAG, "handleActionWeather3");

            if (isBackground) {
                final Intent intent = new Intent(success ? ACTION_NEWS_RESULT_SUCCESS : ACTION_NEWS_RESULT_ERROR);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            } else {
                receiver.send(NEWS_RESULT_SUCCESS, null);
            }

//            if (success && isBackground) {
//                Log.i(LOG_TAG, "handleActionWeather41");
//                data.putBoolean(EXTRA_NEWS_IS_BACKGROUND, true);
//                receiver.send(NEWS_RESULT_SUCCESS, data);
//            } else if (success) {
//                Log.i(LOG_TAG, "handleActionWeather42");
//                data.putBoolean(EXTRA_NEWS_IS_BACKGROUND, false);
//                receiver.send(NEWS_RESULT_SUCCESS, data);
//            } else if (isBackground) {
//                Log.i(LOG_TAG, "handleActionWeather43");
//                data.putBoolean(EXTRA_NEWS_IS_BACKGROUND, true);
//                receiver.send(NEWS_RESULT_ERROR, data);
//            } else {
//                Log.i(LOG_TAG, "handleActionWeather44");
//                data.putBoolean(EXTRA_NEWS_IS_BACKGROUND, false);
//                receiver.send(NEWS_RESULT_ERROR, data);
//            }
            Log.i(LOG_TAG, "handleActionWeather4");
        }
        catch (Exception ex) {
            Log.i(LOG_TAG, "handleActionWeather5");
            Log.i(LOG_TAG, ex.getMessage());
            if (isBackground) {
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_NEWS_RESULT_ERROR));
            } else {
                receiver.send(NEWS_RESULT_ERROR, null);
            }
            Log.i(LOG_TAG, "handleActionWeather6");
        }
    }
}
