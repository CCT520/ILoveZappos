package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
        IntentIntegrator integrator = new IntentIntegrator(ScanCodeActivity.this);
        integrator.initiateScan();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String result = scanResult.getContents();
            Log.d("code", result);
            Toast.makeText(this,result, Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setClass(ScanCodeActivity.this, GetSharedProductActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("ProductUrl", result);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
    }
}
