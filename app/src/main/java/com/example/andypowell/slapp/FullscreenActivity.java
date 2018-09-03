package com.example.andypowell.slapp;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextClock;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private Typeface typefaceHP;
    private Typeface typefaceClockHP;
    private Typeface typefaceTO;
    private Typeface typefaceLUMOS;
    private TextClock tc;
    private TextView tv;
    private TextView tv2;
    private View root;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        root = findViewById(R.id.flo);

        typefaceHP = Typeface.createFromAsset(getAssets(), "fonts/MagicSchoolTwo.ttf");
        typefaceClockHP = Typeface.createFromAsset(getAssets(), "fonts/Adobe-Garamond-Pro_2012.ttf");
        typefaceLUMOS = Typeface.createFromAsset(getAssets(), "fonts/LUMOS.TTF");
        typefaceTO = Typeface.createFromAsset(getAssets(), "fonts/American Typewriter Regular.ttf");

        tc = (TextClock)findViewById(R.id.textClock);
        tv =  (TextView)findViewById((R.id.fullscreen_content));
        tv2 =  (TextView)findViewById((R.id.fullscreen_content2));

        final Handler handler = new Handler();

        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        updateView();
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask,500,3600000); //run every hour



        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
                //updateView();
            }
        });
        tc.setOnClickListener(new TextClock.OnClickListener() {
            public void onClick(View v){

                updateView();
            }
        });

    }
    private void hideClock(){
        if(tc.getVisibility()==View.GONE)
            tc.setVisibility(View.VISIBLE);
        else if(tc.getVisibility()==View.VISIBLE)
            tc.setVisibility(View.GONE);
    }

    private void updateView(){
        try {
            Random r = new Random();
            int HPorTO = r.nextInt(3 - 1) + 1;

            if(HPorTO == 1) {
                tv.setTypeface(typefaceClockHP);
                tc.setTypeface(typefaceLUMOS);
                tv2.setTypeface(typefaceHP);
                tv.setTextColor(Color.parseColor("#089ec7"));
                tc.setTextColor(Color.parseColor("#ba9368"));
                tv2.setTextColor(Color.parseColor("#ba9368"));
                root.setBackgroundColor(Color.parseColor("#20150e"));
                //#2b1c13
                JSONObject quote = changeQuotePotter();
                String q = quote.getString("description");
                tv2.setTextSize(38);
                String bookTitle = q.substring(q.indexOf("Harry Potter and the"),q.lastIndexOf("\n"));
                String hp = bookTitle.substring(0,12) + "\n";
                String andThe = bookTitle.substring(12);
                q = q.replace(bookTitle,"");
                int txtSize =32;
                if(q.length() > 250){txtSize = 27;}
                tv.setTextSize(txtSize);
                tv.setText(q);
                tv2.setText(hp + andThe);
            }
            else{
                tv.setTypeface(typefaceTO);
                tv.setTextColor(Color.parseColor("#5fc8ed"));
                tc.setTypeface(typefaceTO);
                tc.setTextColor(Color.parseColor("#ffffff"));
                tv2.setTypeface(typefaceTO);
                tv2.setTextColor(Color.parseColor("#ffffff"));
                tv2.setTextSize(25);
                root.setBackgroundColor(Color.parseColor("#000000"));
                JSONObject quote = changeQuoteOffice();
                String desc = quote.getString("description");
                int txtSize = 30;
                if(desc.length() > 220){txtSize = 26;}
                tv.setTextSize(txtSize);
                tv.setText(desc);

                tv2.setText( "-The Office "+quote.getString("author"));
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);

    }
    @Override
    protected void onResume() {
        super.onResume();
        mVisible = true;
        toggle();

}


    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    public JSONObject changeQuoteOffice(){
       JSONObject q = new JSONObject();

        try {
            Random r = new Random();
            int i1 = r.nextInt(8022 - 1) + 1;
            JSONObject obj = new JSONObject(loadJSONFromAsset("toquotes.json"));
            JSONArray m_jArry = obj.getJSONArray("quotes");
            ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> m_li;
            JSONObject jo_inside = m_jArry.getJSONObject(i1);
            q = jo_inside;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return q;
    }
    public JSONObject changeQuotePotter(){
        JSONObject q = new JSONObject();
        try {
            Random r = new Random();
            int i1 = r.nextInt(4372 - 1) + 1;
            JSONObject obj = new JSONObject(loadJSONFromAsset("hpquotes.json"));
            JSONArray m_jArry = obj.getJSONArray("quotes");
            ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> m_li;
            JSONObject jo_inside = m_jArry.getJSONObject(i1);
            q = jo_inside;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return q;
    }


}
