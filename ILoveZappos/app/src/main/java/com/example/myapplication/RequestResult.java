package com.example.myapplication;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchutian on 2017/1/29.
 */

public class RequestResult implements Serializable{
    private String originalTerm;
    private String currentResultCount;
    private String totalResultCount;
    private String term;
    private ArrayList<Product> results;

    public RequestResult(){

    }
    public RequestResult(String originalTerm, String currentResultCount, String totalResultCount, String term, ArrayList<Product> results){
        this.originalTerm = originalTerm;
        this.currentResultCount = currentResultCount;
        this.totalResultCount = totalResultCount;
        this.term = term;
        this.results = results;
    }

    public void setOriginalTerm(String originalTerm){
        this.originalTerm = originalTerm;
    }
    public String getOriginalTerm(){
        return this.originalTerm;
    }

    public void setCurrentResultCount(String currentResultCount){
        this.currentResultCount = currentResultCount;
    }
    public String getCurrentResultCount(){
        return this.currentResultCount;
    }

    public void setTotalResultCount(String totalResultCount){
        this.totalResultCount = totalResultCount;
    }
    public String getTotalResultCount(){
        return this.totalResultCount;
    }

    public void setTerm(String term){
        this.term = term;
    }
    public String getTerm(){
        return this.term;
    }

    public void setResults(ArrayList<Product> results){
        this.results = results;
    }
    public ArrayList getResults(){
        return this.results;
    }
}
