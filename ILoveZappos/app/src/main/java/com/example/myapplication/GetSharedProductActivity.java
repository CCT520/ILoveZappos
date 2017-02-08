package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class GetSharedProductActivity extends AppCompatActivity {

    private String productUrl;
    private WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_shared_product);
        productUrl = getIntent().getStringExtra("ProductUrl");
        webview = (WebView)findViewById(R.id.web_view);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(productUrl);
        finish();
    }
}
