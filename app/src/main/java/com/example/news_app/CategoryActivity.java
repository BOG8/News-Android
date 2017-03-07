package com.example.news_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ru.mail.weather.lib.Storage;
import ru.mail.weather.lib.Topics;

public class CategoryActivity extends AppCompatActivity {
    private final static String LOG_TAG = CategoryActivity.class.getSimpleName();

    Storage newsStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        newsStorage = Storage.getInstance(this);

        Button categoryOneButton = (Button) findViewById(R.id.category_button_1);
        categoryOneButton.setText(Topics.AUTO);
        categoryOneButton.setOnClickListener(onCityClick);
        Button categoryTwoButton = (Button) findViewById(R.id.category_button_2);
        categoryTwoButton.setText(Topics.IT);
        categoryTwoButton.setOnClickListener(onCityClick);
        Button categoryThreeButton = (Button) findViewById(R.id.category_button_3);
        categoryThreeButton.setText(Topics.HEALTH);
        categoryThreeButton.setOnClickListener(onCityClick);
    }

    private final View.OnClickListener onCityClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(LOG_TAG, "onCityClick");
            Button categoryButton = (Button) view;
            String categoryName = categoryButton.getText().toString();
            newsStorage.saveCurrentTopic(categoryName);
            finish();
        }
    };
}
