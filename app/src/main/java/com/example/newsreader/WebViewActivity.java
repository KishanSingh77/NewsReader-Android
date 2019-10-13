package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView=findViewById(R.id.webView);
        String url= (String) getIntent().getExtras().getCharSequence("url");
        webView.setWebViewClient(new WebViewClient());
       // webView.loadUrl(url);
        webView.loadUrl(url);

    }
}
