package com.example.myapplication;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by chenchutian on 2017/1/31.
 */

public class RetainedFragment extends Fragment {

    // data object we want to retain
    private String term;
    private RequestResult requestResult;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTerm() {
        return this.term;
    }

    public void setRequestResult(RequestResult requestResult){
        this.requestResult = requestResult;
    }

    public RequestResult getRequestResult(){
        return this.requestResult;
    }
}