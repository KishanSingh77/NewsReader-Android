package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.newsreader.MainActivity.CHOSEN_SOURCE;

public class NewsActivity extends AppCompatActivity {
    public static String API_KEY = "32e51c6db6df444fa6f6d9bc658f971f";
    public static String SOURCES_URL = "https://newsapi.org/v2/sources?apiKey="+API_KEY;
    public static String TOP_HEADLINES_URL = "https://newsapi.org/v2/top-headlines";

    ProgressBar progressBar ;
    Source chosen_source ;
    String fetchURL;
    List<News> newsList;
    ListView newsListView;
    NewsAdapter  newsNewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        //progressBar = findViewById(R.id.progressBar);
        newsList = new ArrayList();
        newsListView = findViewById(R.id.listView_newsList);


        //fetch from intent
        if(getIntent().getExtras()!=null){
            chosen_source = (Source)getIntent().getExtras().getSerializable(CHOSEN_SOURCE);
            fetchURL = TOP_HEADLINES_URL+"?sources="+chosen_source.id+"&apiKey="+API_KEY;
            setTitle(chosen_source.name);

        }

        //hit the news API
        if(isConnected()){
            try {
                newsList =  new GetNewsListAsyncTask().execute(fetchURL).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Error, not connected", Toast.LENGTH_SHORT).show();
        }

        Log.d("Demo,news main thread" , newsList+"");

          newsNewsAdapter = new NewsAdapter(this ,R.layout.news_item , newsList );

        newsListView.setAdapter(newsNewsAdapter);



    }

    private class GetNewsListAsyncTask extends AsyncTask<String, Void, ArrayList<News>> {

        @Override
        protected void onPostExecute(ArrayList<News> newsArrayListReceived) {
            Log.d("Demo , in postExecute" , "before");
           //newsList.addAll(newsArrayListReceived)  ;
            Log.d("Demo , in postExecute" , newsList+"");
           // progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
         //   progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<News> doInBackground(String... strings) {

            HttpURLConnection connection = null;
            ArrayList<News> newsArrayList = new ArrayList<>();

            String result = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    result = IOUtils.toString(connection.getInputStream(), "UTF-8");
                    JSONObject jsonObj = new JSONObject(result);
                    JSONArray ArticleArray =  jsonObj.getJSONArray("articles");

                    for( int i = 0 ; i < ArticleArray.length();i++)
                    {
                        JSONObject sourceJSON = ArticleArray.getJSONObject(i);
                        News news = new News();


                        news.author = sourceJSON.getString("author");
                        news.title = sourceJSON.getString("title");
                        news.url = sourceJSON.getString("url");
                        news.urlToImage = sourceJSON.getString("urlToImage");
                        news.publishedAt = sourceJSON.getString("publishedAt");


                        newsArrayList.add(news);
                    }

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            finally {
                //Close open connections and reader
                if (connection != null) {
                    connection.disconnect();
                }

            }
            return newsArrayList;

        }

    }


    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }
}
