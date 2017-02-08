package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainBinding;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chenchutian on 2017/1/29.
 */

public class EventHandlers {
    private String term;
    private String key = "b743e26728e16b81da139182bb2094357c31d331";
    private RequestResult requestResult;
    private EditText inputView;
    private ListView listView;
    private MainActivity mainActivity;
    private ProductActivity productActivity;
    private ActivityMainBinding bind;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private Bitmap bitMap;
    private ProgressDialog progressDialog;

    public EventHandlers(MainActivity mainActivity, ActivityMainBinding bind){
        this.mainActivity = mainActivity;
        this.bind = bind;
    }

    public EventHandlers(final String imageUrl, ProductActivity productActivity, int mShortAnimationDuration){
        this.productActivity = productActivity;
        this.mShortAnimationDuration = mShortAnimationDuration;
        new Thread(new Runnable() {
            @Override
            public void run() {
                bitMap = getBitmap(imageUrl);
            }
        }).start();
    }

    public EventHandlers(MainActivity mainActivity, ActivityMainBinding bind,String term){
        this.mainActivity = mainActivity;
        this.bind = bind;
        this.term = term;
    }
    public void requestAction() {
        //inputView = (EditText)mainActivity.findViewById(R.id.editText);
        //term = inputView.getText().toString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.zappos.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestServes requestServes = retrofit.create(RequestServes.class);
        Call<RequestResult> call = requestServes.getResult(term, key);
        progressDialog = ProgressDialog.show(mainActivity, "Waiting...", "Loading data...", true);
        call.enqueue(new Callback<RequestResult>(){

            @Override
            public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                progressDialog.dismiss();
                requestResult = new RequestResult(response.body().getOriginalTerm(),response.body().getCurrentResultCount(),
                        response.body().getTotalResultCount(),response.body().getTerm(),response.body().getResults());
                mainActivity.setRequestResult(requestResult);
                listView = (ListView) mainActivity.findViewById(R.id.listView);
                ListAdapter listAdapter = new ListAdapter(mainActivity,requestResult.getResults(),R.layout.list_item,BR.Product);
                listView.setAdapter(listAdapter);
                bind.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        if(requestResult!=null) {
                            Intent intent = new Intent();
                            intent.setClass(mainActivity, ProductActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Product", (Serializable) requestResult.getResults().get(position));
                            intent.putExtras(bundle);
                            mainActivity.startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<RequestResult> call, Throwable t) {
                progressDialog.dismiss();
                Toast toast = Toast.makeText(mainActivity,
                        "Bad connection, try again", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    public RequestResult getRequestResult(){
        return this.requestResult;
    }

    public void zoomImage(final View thumbView){
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) productActivity.findViewById(
                R.id.expandedImage);
        expandedImageView.setImageBitmap(bitMap);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        expandedImageView.getGlobalVisibleRect(startBounds);
        productActivity.findViewById(R.id.product_activity)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        //thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
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

    public void back(View view){
        productActivity.finish();
    }
}
