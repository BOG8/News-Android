package com.example.news_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ru.mail.weather.lib.News;
import ru.mail.weather.lib.Storage;
import ru.mail.weather.lib.Topics;

public class MainActivity extends AppCompatActivity implements ServiceHelper.NewsResultListener {
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    Storage newsStorage;
    TextView headingTextView;
    TextView contentsTextView;
    TextView dateTextView;
    Button categoryChoiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsStorage = Storage.getInstance(this);

        headingTextView = (TextView) findViewById(R.id.heading_text_view);
        contentsTextView = (TextView) findViewById(R.id.contents_text_view);
        dateTextView = (TextView) findViewById(R.id.date_text_view);

        categoryChoiceButton = (Button) findViewById(R.id.category_choice_button);
        categoryChoiceButton.setOnClickListener(onCategoryChooseClick);

        findViewById(R.id.update_button).setOnClickListener(onUpdateClick);
        findViewById(R.id.background_update_button).setOnClickListener(onBackgroundUpdateClick);
        findViewById(R.id.do_not_background_update_button).setOnClickListener(onStopBackgroundUpdateClick);
    }

    @Override
    protected void onResume() {
        super.onResume();
        News news = newsStorage.getLastSavedNews();
        if (news != null) {
            String heading = news.getTitle();
            headingTextView.setText(heading);
            String contents = news.getBody();
            contentsTextView.setText(contents);
            Long date = news.getDate();
            dateTextView.setText(date.toString());
        } else {
            headingTextView.setText(R.string.load_news);
            contentsTextView.setText(R.string.empty_string);
            dateTextView.setText(R.string.empty_string);
        }
        String category = newsStorage.loadCurrentTopic();
        if (category == null || category.isEmpty()) {
            newsStorage.saveCurrentTopic(Topics.IT);
        }
        categoryChoiceButton.setText(category);

        initBroadcastReceiver(this);
    }

    @Override
    protected void onDestroy() {
        ServiceHelper.getInstance().removeAllListeners();
        super.onDestroy();
    }

    private final View.OnClickListener onCategoryChooseClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(LOG_TAG, "onCategoryChooseClick");
            final Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
            startActivity(intent);
        }
    };

    private final View.OnClickListener onUpdateClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(LOG_TAG, "onUpdateClick");
            startUpdate();
        }
    };

    private final View.OnClickListener onBackgroundUpdateClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(LOG_TAG, "onBackgroundUpdateClick");
            startBackgroundUpdate();
        }
    };

    private final View.OnClickListener onStopBackgroundUpdateClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(LOG_TAG, "onBackgroundUpdateClick");
            stopBackgroundUpdate();
        }
    };

    public void startUpdate() {
        Log.i(LOG_TAG, "startUpdate");
        String categoryName = categoryChoiceButton.getText().toString();
        ServiceHelper.getInstance().getNews(this, categoryName, this);
    }

    public void startBackgroundUpdate() {
        Log.i(LOG_TAG, "startBackgroundUpdate");
        String categoryName = categoryChoiceButton.getText().toString();
        ServiceHelper.getInstance().getNewsByBackgroundWork(this, categoryName);
    }

    public void stopBackgroundUpdate() {
        Log.i(LOG_TAG, "startBackgroundUpdate");
        ServiceHelper.getInstance().stopBackgroundWork(this);
    }

    @Override
    public void onNewsResult(boolean success) {
        Log.i(LOG_TAG, "onNewsResult");
        if (success) {
            News news = newsStorage.getLastSavedNews();
            String heading = news.getTitle();
            headingTextView.setText(heading);
            String contents = news.getBody();
            contentsTextView.setText(contents);
            Long date = news.getDate();
            dateTextView.setText(date.toString());
            Toast.makeText(this, "News was downloaded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Couldn't load news", Toast.LENGTH_SHORT).show();
        }
    }

    private void initBroadcastReceiver(Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(NewsIntentService.ACTION_NEWS_RESULT_SUCCESS);
        filter.addAction(NewsIntentService.ACTION_NEWS_RESULT_ERROR);

        LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                Log.i(LOG_TAG, "BroadcastReceiver onReceive");
                final boolean success = intent.getAction().equals(NewsIntentService.ACTION_NEWS_RESULT_SUCCESS);
                onNewsResult(success);
            }
        }, filter);
    }
}
