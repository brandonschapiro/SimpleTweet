package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";
    public static final int REPLY_REQUEST_CODE = 30;

    TwitterClient client;

    ImageView ivProfileImage;
    TextView tvName;
    TextView tvScreenName;
    TextView tvBody;
    TextView tvLikes;
    TextView tvRetweets;
    TextView tvTime;
    Tweet tweet;
    Button replyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvName = findViewById(R.id.tvName);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvBody = findViewById(R.id.tvBody);
        tvLikes = findViewById(R.id.tvLikes);
        tvRetweets = findViewById(R.id.tvRetweets);
        tvTime = findViewById(R.id.tvTime);
        replyBtn = findViewById(R.id.replyBtn);

        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, ComposeActivity.class);
                i.putExtra("is_reply", true);
                i.putExtra("in_reply_to_user", tweet.user.screenName);
                i.putExtra("in_reply_to_tweet_id", tweet.id);
                startActivityForResult(i, REPLY_REQUEST_CODE);
            }
        });

        Glide.with(this).load(tweet.user.publicImageUrl).into(ivProfileImage);
        tvName.setText(tweet.user.name);
        tvScreenName.setText("@" + tweet.user.screenName);
        tvBody.setText(tweet.body);
        tvLikes.setText("" + tweet.likes + " Likes");
        tvRetweets.setText("" + tweet.retweets + " Retweets");
        tvTime.setText("Created: " + TimeFormatter.getTimeDifference(tweet.createdAt));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REPLY_REQUEST_CODE && resultCode == RESULT_OK){
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}