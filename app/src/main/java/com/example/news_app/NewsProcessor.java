package com.example.news_app;

import android.content.Context;
import android.util.Log;

import ru.mail.weather.lib.News;
import ru.mail.weather.lib.NewsLoader;
import ru.mail.weather.lib.Storage;

/**
 * Created by Олег on 07.03.2017.
 */

class NewsProcessor {
    private final static String LOG_TAG = NewsProcessor.class.getSimpleName();

    static boolean processCategory(final Context context, String currentCategory) {
        Log.i(LOG_TAG, "processCategory (Making server request)");
        try {
            News news = new NewsLoader().loadNews(currentCategory);
            Storage.getInstance(context).saveNews(news);
        }
        catch (Exception ex) {
            Log.i(LOG_TAG, ex.getMessage());
            return false;
        }
        Log.i(LOG_TAG, "processCategory run without errors");
        return true;
    }
}
