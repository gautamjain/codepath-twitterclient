package io.gautam.codepath.twitterclient;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import io.gautam.codepath.twitterclient.models.Tweet;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class MainActivity extends Activity {

    private PullToRefreshLayout mPullToRefreshLayout;

    ListView lvTweets;
    TweetsAdapter adapter;
    ArrayList<Tweet> tweets = new ArrayList<Tweet>(0);

    long maxId = -1;
    long sinceId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvTweets = (ListView) findViewById(R.id.lvTweets);
        adapter = new TweetsAdapter(getBaseContext(), tweets);
        lvTweets.setAdapter(adapter);

        // Setup endless/infinite scrolling
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadTweets(page);
            }
        });

        // Initialize image loader
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory()
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        ImageLoader.getInstance().init(config);

        // Now find the PullToRefreshLayout to setup
        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptrLayout);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this)
                // Mark All Children as pullable
                .allChildrenArePullable()
                // Set a OnRefreshListener
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        maxId = -1;
                        sinceId = -1;
                        adapter.clear();
                        loadTweets(0);
                    }
                })
        // Finally commit the setup to our PullToRefreshLayout
        .setup(mPullToRefreshLayout);

        // Start loading the first page of tweets & show progress indicator
        mPullToRefreshLayout.setRefreshing(true);
        loadTweets(0);
    }

    private void loadTweets(int page) {
        TwitterClientApp.getRestClient().getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonTweets) {
                ArrayList<Tweet> newTweets = Tweet.fromJson(jsonTweets);
                adapter.addAll(newTweets);

                // Set the maxId
                if (tweets != null && tweets.size() > 0) {
                    MainActivity.this.maxId = tweets.get(tweets.size() - 1).getId();
                }

                // If PTR action bar is refreshing, turn it off
                if (mPullToRefreshLayout.isRefreshing()) {
                    mPullToRefreshLayout.setRefreshComplete();
                }

            }

            @Override
            public void onFailure(Throwable throwable, JSONArray jsonArray) {
                // If PTR action bar is refreshing, turn it off
                if (mPullToRefreshLayout.isRefreshing()) {
                    mPullToRefreshLayout.setRefreshComplete();
                }

                super.onFailure(throwable, jsonArray);
            }
        }, maxId -1, -1, page);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
