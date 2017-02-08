package com.example.myapplication;

/**
 * Created by chenchutian on 2017/1/31.
 */

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;

/**
 * Created by chenchutian on 2017/1/29.
 */

public class Product implements Serializable {
    private String brandName;
    private String thumbnailImageUrl;
    private String productId;
    private String originalPrice;
    private String styleId;
    private String colorId;
    private String price;
    private String percentOff;
    private String productUrl;
    private String productName;

    public Product(){

    }
    public Product(String brandName, String thumbnailImageUrl, String productId, String originalPrice,
                   String styleId, String colorId, String price, String percentOff, String productUrl,
                   String productName){
        this.brandName = brandName;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.productId = productId;
        this.originalPrice = originalPrice;
        this.styleId = styleId;
        this.colorId = colorId;
        this.price = price;
        this.percentOff = percentOff;
        this.productUrl = productUrl;
        this.productName = productName;
    }


    public void setBrandName(String brandName){
        this.brandName = brandName;
    }
    public String getBrandName(){
        return this.brandName;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl){
        this.thumbnailImageUrl = thumbnailImageUrl;
    }
    public String getThumbnailImageUrl(){
        return this.thumbnailImageUrl;
    }

    public void setProductId(String productId){
        this.productId = productId;
    }
    public String getProductId(){
        return this.productId;
    }

    public void setOriginalPrice(String originalPrice){
        this.originalPrice = originalPrice;
    }
    public String getOriginalPrice(){
        return this.originalPrice;
    }

    public void setStyleId(String styleId){
        this.styleId = styleId;
    }
    public String getStyleId(){
        return this.styleId;
    }

    public void setColorId(String colorId){
        this.colorId = colorId;
    }
    public String getColorId(){
        return colorId;
    }

    public void setPrice(String price){
        this.price = price;
    }
    public String getPrice(){
        return this.price;
    }

    public void setPercentOff(String percentOff){
        this.percentOff = percentOff;
    }
    public String getPercentOff(){
        return this.percentOff;
    }

    public void setProductUrl(String productUrl){
        this.productUrl = productUrl;
    }
    public String getProductUrl(){
        return this.productUrl;
    }
    public void setProductName(String productName){
        this.productName = productName;
    }
    public String getProductName(){
        return productName;
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView imageView, String url) {
        Picasso.with(imageView.getContext()).load(url).into(imageView);
    }

}

