package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    public static final int TWEET_REQUEST_CODE = TimelineActivity.TWEET_REQUEST_CODE;

    Context context;
    List<Tweet> tweets;
    TwitterClient client;

    public TweetsAdapter(List<Tweet> t, Context c){
        context = c;
        tweets = t;
        client = TwitterApp.getRestClient(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }
    //Clears all elements of the recycler view on swipe to refresh
    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    //Adds all new tweets to the recycler view on swipe to refresh
    public void addAll(List<Tweet> t){
        tweets.addAll(t);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout rlTweet;
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvTime;
        ImageView ivReply;
        ImageView ivRetweet;
        ImageView ivLike;
        TextView tvLikes;
        TextView tvRetweets;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            rlTweet = itemView.findViewById(R.id.rlTweet);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivReply = itemView.findViewById(R.id.ivReply);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvLikes = itemView.findViewById(R.id.tvLikeCount);
            tvRetweets = itemView.findViewById(R.id.tvRetweetCount);

        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.name + " @" + tweet.user.screenName);
            Glide.with(context).load(tweet.user.publicImageUrl).into(ivProfileImage);
            tvTime.setText("" + TimeFormatter.getTimeDifference(tweet.createdAt));
            tvLikes.setText("" + tweet.likes);
            tvRetweets.setText("" + tweet.retweets);

            if(tweet.liked){
                ivLike.setImageResource(R.drawable.ic_vector_heart);
            }
            else{
                ivLike.setImageResource(R.drawable.ic_vector_heart_stroke);
            }
            if(tweet.retweeted){
                ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
            }
            else{
                ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            }

            ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ComposeActivity.class);
                    i.putExtra("tweet", Parcels.wrap(tweet));
                    i.putExtra("is_reply", true);
                    i.putExtra("in_reply_to_user", tweet.user.screenName);
                    i.putExtra("in_reply_to_tweet_id", tweet.id);
                    ((Activity)context).startActivityForResult(i, TWEET_REQUEST_CODE);
                }
            });

            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!tweet.retweeted){
                        client.retweet(new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.retweets++;
                                tvRetweets.setText("" + tweet.retweets);
                                ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Toast.makeText(context, "Failed to retweet", Toast.LENGTH_SHORT).show();
                            }
                        }, tweet.id);
                    } else{
                        client.unretweet(new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.retweets--;
                                tvRetweets.setText("" + tweet.retweets);
                                ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        }, tweet.id);
                    }
                    tweet.retweeted = !tweet.retweeted;
                }
            });

            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!tweet.liked){
                        client.likeTweet(new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.likes++;
                                tvLikes.setText("" + tweet.likes);
                                ivLike.setImageResource(R.drawable.ic_vector_heart);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Toast.makeText(context, "Failed to like tweet", Toast.LENGTH_SHORT).show();
                            }
                        }, tweet.id);
                    } else{
                        client.unlikeTweet(new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.likes--;
                                tvLikes.setText("" + tweet.likes);
                                ivLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Toast.makeText(context, "Failed to unlike tweet", Toast.LENGTH_SHORT).show();
                            }
                        }, tweet.id);
                    }
                    tweet.liked = !tweet.liked;
                }
            });
            /* Disabled until I have time to make the DetailActivity look better
            rlTweet.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Parcelable tweetObject = Parcels.wrap(tweet);
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("tweet", tweetObject);
                    context.startActivity(i);
                }
            }); */
        }
    }
}
