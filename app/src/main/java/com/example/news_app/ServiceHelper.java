package com.example.news_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Hashtable;
import java.util.Map;

import ru.mail.weather.lib.Scheduler;

/**
 * Created by Олег on 07.03.2017.
 */

public class ServiceHelper {
    private final static String LOG_TAG = ServiceHelper.class.getSimpleName();
    private final static int MINUTE = 60000;

    private static ServiceHelper instance;
    private int idCounter = 1;
    private final Map<Integer, NewsResultReceiver> resultReceivers = new Hashtable<>();

    private ServiceHelper() {

    }

    synchronized static ServiceHelper getInstance() {
        if (instance == null) {
            instance = new ServiceHelper();
        }
        return instance;
    }

    void getNews(final Context context, final String currentCategory, final NewsResultListener listener) {
        Log.i(LOG_TAG, "getNews");
        final NewsResultReceiver receiver = new NewsResultReceiver(idCounter, new Handler());
        receiver.setListener(listener);
        resultReceivers.put(idCounter, receiver);

        Intent intent = new Intent(context, NewsIntentService.class);
        intent.setAction(NewsIntentService.ACTION_NEWS);
        intent.putExtra(NewsIntentService.EXTRA_NEWS_CITY, currentCategory);
        intent.putExtra(NewsIntentService.EXTRA_NEWS_RESULT_RECEIVER, receiver);
        intent.putExtra(NewsIntentService.EXTRA_NEWS_BACKGROUND, "NOT_BACKGROUNG");
        context.startService(intent);

        idCounter++;
    }

    void getNewsByBackgroungWork(final Context context, final String currentCategory) {
        Log.i(LOG_TAG, "getNewsByBackgroungWork");
        Intent intent = new Intent(context, NewsIntentService.class);
        intent.setAction(NewsIntentService.ACTION_NEWS);
        intent.putExtra(NewsIntentService.EXTRA_NEWS_CITY, currentCategory);
        intent.putExtra(NewsIntentService.EXTRA_NEWS_BACKGROUND, "BACKGROUND");
        Scheduler.getInstance().schedule(context, intent, MINUTE);
    }

    void stopBackgroungWork(final Context context) {
        Log.i(LOG_TAG, "stopBackgroungWork");
        Intent intent = new Intent(context, NewsIntentService.class);
        intent.setAction(NewsIntentService.ACTION_NEWS);
        Scheduler.getInstance().unschedule(context, intent);
    }

    void removeListener(final int id) {
        Log.i(LOG_TAG, "removeListener");
        NewsResultReceiver receiver = resultReceivers.remove(id);
        if (receiver != null) {
            receiver.setListener(null);
        }
    }

    void removeAllListeners() {
        Log.i(LOG_TAG, "removeAllListeners");
        resultReceivers.clear();
    }

    interface NewsResultListener {
        void onNewsResult(final boolean success);
    }
}
