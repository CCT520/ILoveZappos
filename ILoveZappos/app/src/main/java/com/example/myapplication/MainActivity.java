package com.example.myapplication;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.myapplication.databinding.ActivityMainBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding bind;
    private RequestResult requestResult;
    private RetainedFragment dataFragment;
    private FragmentManager fm;
    private EventHandlers eventHandlers;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SearchView.SearchAutoComplete inputView;
    private String term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.camera);
        preferences  = getSharedPreferences("MainActivity", Activity.MODE_PRIVATE);             //never forget these two lines
        editor = preferences.edit();
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main);
        fm = getFragmentManager();
        dataFragment =  (RetainedFragment)fm.findFragmentByTag("data");
        // create the fragment and data the first time
        if (dataFragment != null) {
            term = dataFragment.getTerm();
            if(term!=null){
                eventHandlers = new EventHandlers(MainActivity.this,bind,term);
                eventHandlers.requestAction();
                dataFragment.setTerm(null);
                requestResult = eventHandlers.getRequestResult();//
            }else{
                requestResult = dataFragment.getRequestResult();
                if(requestResult==null){
                    try {                             //never miss following lines
                        requestResult = deSerialization(preferences.getString("requestResult", null));//
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                /**/
            }
        }else{
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();
            fm.executePendingTransactions();
            try {                             //never miss following lines
                requestResult = deSerialization(preferences.getString("requestResult", null));//
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        if(requestResult!=null){
            ListAdapter listAdapter = new ListAdapter(MainActivity.this,requestResult.getResults(),R.layout.list_item,BR.Product);
            bind.setAdapter(listAdapter);
            listItemEvent();
        }
        //listItemEvent();
    }

    public void listItemEvent(){
        bind.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(requestResult!=null) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, ProductActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Product", (Serializable) requestResult.getResults().get(position));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    public void setRequestResult(RequestResult requestResult){
        this.requestResult = requestResult;
    }

    public String serialize(RequestResult myRequestResult) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        objectOutputStream.writeObject(myRequestResult);
        String serStr = byteArrayOutputStream.toString("ISO-8859-1");
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return serStr;
    }

    private RequestResult deSerialization(String str) throws IOException,
            ClassNotFoundException {
        if(str==null)
            return null;
        String redStr = java.net.URLDecoder.decode(str, "UTF-8");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                redStr.getBytes("ISO-8859-1"));
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        RequestResult myrequestResult = (RequestResult) objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return myrequestResult;
    }
    public void onDestroy() {
        super.onDestroy();
        // store the data in the fragment
        dataFragment.setRequestResult(requestResult);
        if(requestResult!=null){
            try {                          //never forget following lines
                editor.putString("requestResult",serialize(requestResult)).commit();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        inputView = ( SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.search_button:
                term = inputView.getText().toString();
                dataFragment.setTerm(term);
                eventHandlers = new EventHandlers(MainActivity.this,bind,term);
                eventHandlers.requestAction();
                requestResult = eventHandlers.getRequestResult();//
                return true;
            case android.R.id.home:
                if(requestResult!=null){
                    try {                          //never forget following lines
                        editor.putString("requestResult",serialize(requestResult)).commit();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(MainActivity.this,ScanCodeActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
