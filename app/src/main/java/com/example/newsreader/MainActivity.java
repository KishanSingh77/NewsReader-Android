package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar ;
    ListView listView;
    ArrayList<Source> sourceArrayList;
    ArrayList<String> sourceNameArrayList;
    public static String API_KEY = "32e51c6db6df444fa6f6d9bc658f971f";
    public static String SOURCES_URL = "https://newsapi.org/v2/sources?apiKey="+API_KEY;
    public static String TOP_HEADLINES_URL = "https://newsapi.org/v2/top-headlines";
    public static String CHOSEN_SOURCE = "CHOSEN_SOURCE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        listView = findViewById(R.id.listView);
        sourceArrayList = new ArrayList<>();
        sourceNameArrayList = new ArrayList<>();
        //progressBar.setVisibility(ProgressBar.INVISIBLE);


        if(isConnected()){

                new GetSourceListAsyncTask().execute(SOURCES_URL);


        }else{
            Toast.makeText(this, "Error, not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        //copy just the names to display in listview

        for(Source source : sourceArrayList){
            sourceNameArrayList.add(source.name);
        }

        ArrayAdapter<String> sourceArrayAdapter = new ArrayAdapter(this ,  android.R.layout.simple_list_item_1 , sourceNameArrayList);

        listView.setAdapter(sourceArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this, NewsActivity.class);
                 Source clickedSource = sourceArrayList.get(position);
                 Log.d("Demo chosen==> " , clickedSource+"");
                intent.putExtra(CHOSEN_SOURCE,clickedSource);
                startActivity(intent);
            }
        });


    }



    private class GetSourceListAsyncTask extends AsyncTask<String, Void, ArrayList<Source>> {

        @Override
        protected void onPostExecute(ArrayList<Source> sourceArrayListReceived) {
            Log.d("Demo , in postExecute" , "before");
          sourceArrayList.addAll(sourceArrayListReceived)  ;
            Log.d("Demo , in postExecute" , sourceArrayList+"");

            //copy just the names to display in listview

            for(Source source : sourceArrayList){
                sourceNameArrayList.add(source.name);
            }

            ArrayAdapter<String> sourceArrayAdapter = new ArrayAdapter(MainActivity.this ,  android.R.layout.simple_list_item_1 , sourceNameArrayList);

            listView.setAdapter(sourceArrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent(MainActivity.this, NewsActivity.class);
                    Source clickedSource = sourceArrayList.get(position);
                    Log.d("Demo chosen==> " , clickedSource+"");
                    intent.putExtra(CHOSEN_SOURCE,clickedSource);
                    startActivity(intent);
                }
            });
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Source> doInBackground(String... strings) {

            HttpURLConnection connection = null;
            ArrayList<Source> sourceArrayList = new ArrayList<>();

            String result = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    result = IOUtils.toString(connection.getInputStream(), "UTF-8");
                    JSONObject jsonObj = new JSONObject(result);
                    JSONArray SourceArray =  jsonObj.getJSONArray("sources");

                    for( int i = 0 ; i < SourceArray.length();i++)
                    {
                        JSONObject sourceJSON = SourceArray.getJSONObject(i);
                        Source source = new Source();

                        source.id = sourceJSON.getString("id");
                        source.name = sourceJSON.getString("name");


                        sourceArrayList.add(source);

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

            return sourceArrayList;

        }



    }

     boolean isConnected() {
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
