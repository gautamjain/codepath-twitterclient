package io.gautam.codepath.twitterclient.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Tweet {
    private String body;
    private long uid;
    private boolean favorited;
    private boolean retweeted;
    private User user;
    private Date createdAt;

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public long getId() {
        return uid;
    }

    public String getPrettyTime() {
        PrettyTime p = new PrettyTime();
        return p.format(createdAt);
    }

    public boolean isFavorited() {
        return favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public static Tweet fromJson(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"));


            String date = jsonObject.getString("created_at");
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
            try {
                tweet.createdAt = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return tweet;
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = Tweet.fromJson(tweetJson);
            if (tweet != null) {
                tweets.add(tweet);
            }
        }

        return tweets;
    }
}