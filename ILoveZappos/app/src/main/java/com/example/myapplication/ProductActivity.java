package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.databinding.ProductActivityBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {

    private ProductActivityBinding productActivityBinding;
    private FloatingActionButton floatingActionButton;
    private Product product;
    private TextView OriginalPriceTextView;
    private TextView buyNum_tv;
    private ViewGroup anim_mask_layout;
    private ImageView cart;
    private ImageView floatImage;
    private ImageView productImageView;
    private ImageView expandImageView;
    private boolean fabOpened = false;
    private int buyNum = 0;
    private Bitmap bitmap;
    private Bitmap qr_code_bitmap;
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        product = (Product)getIntent().getSerializableExtra("Product");
        productActivityBinding = DataBindingUtil.setContentView(this,R.layout.product_activity);
        productActivityBinding.setProduct(product);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        productActivityBinding.setHandlers(new EventHandlers(product.getThumbnailImageUrl(),ProductActivity.this,mShortAnimationDuration));
        expandImageView = (ImageView)findViewById(R.id.expandedImage);
        expandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandImageView.setVisibility(View.GONE);
            }
        });
        //download product image that will animate on the screen, fall down to the shopping cart
        new Thread(new Runnable() {
            @Override
            public void run() {
                bitmap = getBitmap(product.getThumbnailImageUrl());
            }
        }).start();
        OriginalPriceTextView = (TextView)findViewById(R.id.originalPrice) ;
        OriginalPriceTextView.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG |Paint.ANTI_ALIAS_FLAG);
        buyNum_tv = (TextView)findViewById(R.id.buyNum);
        productImageView = (ImageView)findViewById(R.id.productPicture);
        cart = (ImageView)findViewById(R.id.cartImage);


        floatingActionButton = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    int[] start_location = new int[2];//store coordinate of x y
                    productImageView.getLocationInWindow(start_location);// start location of animation
                    floatImage = new ImageView(ProductActivity.this);// create an image that will animate on the screen
                    floatImage.setImageBitmap(bitmap);// set animation image
                    setAnim(floatImage, start_location);// start animation
            }
        });
    }

    public void openMenu(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view,"rotation",0,-155,-135);
        animator.setDuration(500);
        animator.start();
        OriginalPriceTextView.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,0.7f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        OriginalPriceTextView.startAnimation(alphaAnimation);
        fabOpened = true;
    }

    public void closeMenu(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view,"rotation",-135,20,0);
        animator.setDuration(500);
        animator.start();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.7f,0);
        alphaAnimation.setDuration(500);
        OriginalPriceTextView.setAnimation(alphaAnimation);
        OriginalPriceTextView.setVisibility(View.GONE);
        fabOpened = false;
    }

    private void setAnim(final View v, int[] start_location) {
        anim_mask_layout = null;
        anim_mask_layout = createAnimLayout();
        anim_mask_layout.addView(v);//add image to animation layer
        final View view = addViewToAnimLayout(anim_mask_layout, v,
                start_location);
        int[] end_location = new int[2];// ending location of image
        cart.getLocationInWindow(end_location);//


        // calculate distance
        int endX = 0 - start_location[0]-50;
        int endY = end_location[1] - start_location[1] - 25;
        TranslateAnimation translateAnimationX = new TranslateAnimation(0,
                endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0,
                end_location[0], 0, endY);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());
        translateAnimationY.setRepeatCount(0);
        translateAnimationX.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(1000);// duration time
        view.startAnimation(set);
        //
        set.setAnimationListener(new Animation.AnimationListener() {
            // animation start
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            // animation end
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                buyNum++;
                buyNum_tv.setText(buyNum + "");//
                buyNum_tv.setVisibility(View.VISIBLE);
            }
        });

    }

    /**
     * @param
     * @return void
     * @throws
     * @Description: Creating animation layer
     */
    private ViewGroup createAnimLayout() {
        ViewGroup rootView = (ViewGroup) this.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    private View addViewToAnimLayout(final ViewGroup vg, final View view, int[] location) {
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
        return view;
    }

    public Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;

            int length = http.getContentLength();

            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            buyNum_tv.setText(buyNum + "");//
            buyNum_tv.setVisibility(View.VISIBLE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            buyNum_tv.setText(buyNum + "");//
            buyNum_tv.setVisibility(View.VISIBLE);
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product, menu);
        //MenuItem buttonItem = menu.findItem(R.id.add_button);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.detail:
                Intent intent = new Intent();
                intent.setClass(ProductActivity.this, GetSharedProductActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ProductUrl", product.getProductUrl());
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.create_qr_code:
                qr_code_bitmap = generateBitmap(product.getProductUrl(),350,350);
                expandImageView.setVisibility(View.VISIBLE);
                expandImageView.setImageBitmap(qr_code_bitmap);
                return true;
            case android.R.id.home:
                ProductActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Bitmap generateBitmap(String content,int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }


}
