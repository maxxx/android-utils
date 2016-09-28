/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.List;

import butterknife.ButterKnife;
import ru.maxdestroyer.utils.Util;
import ru.maxdestroyer.utils.UtilConfig;
import ru.maxdestroyer.utils.fragment.UtilFragment;

// ru.maxdestroyer.utils.activity.UtilActivity
@SuppressWarnings("unused")
public abstract class UtilCompatActivity extends AppCompatActivity implements OnClickListener {
    public static UtilCompatActivity _this = null;
    protected Integer realW = 0;
    protected Integer realH = 0;
    protected int width = 0;
    protected int height = 0;
    private Toast toast = null;
    protected boolean msg_queued = false;
    public ProgressDialog pDialog = null;
    public static UtilConfig cfg = null;
    public boolean currentlyVisible = true;
    public Handler handler = null;
    private boolean blockBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!hasTitle())
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        _this = this;
        getWandH();
        cfg = UtilConfig.getInstance().init(this);
        pDialog = new ProgressDialog(this);
        handler = new Handler();
    }

    protected boolean hasTitle() {
        return true;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    /**
     * Layout id
     * @return can be 0
     */
    protected abstract int getLayoutId();

    protected void MSG(Object text) {
        if (msg_queued) {
            if (toast != null) {
                // toast.cancel();
                toast.setText(String.valueOf(text));
            } else {
                toast = Toast.makeText(this, String.valueOf(text),
                        Toast.LENGTH_LONG);
            }
            toast.show();
        } else {
            Toast.makeText(this, String.valueOf(text),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ignore queue
    protected void MSG2(Object text) {
        Toast.makeText(this, String.valueOf(text),
                Toast.LENGTH_SHORT).show();
    }

    // UI thread
    public void MSG3(final Object text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.setText(String.valueOf(text));
                } else {
                    toast = Toast.makeText(_this, String.valueOf(text),
                            Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        });
    }

    protected void HideMSG() {
        if (toast != null) {
            toast.cancel();
        }
    }

    protected void LOG(Object text) {
        Util.log(text);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getWandH();
    }

    /**
     * get real device Width and Height
     */
    @SuppressLint("NewApi")
    protected void getWandH() {
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        // only 14 15 16
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            Method mGetRawH, mGetRawW;
            try {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");
                realW = (Integer) mGetRawW.invoke(display);
                realH = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                Log.e("mGetRawH", "error!");
                e.printStackTrace();
            }
        } else if (Build.VERSION.SDK_INT >= 17) // 4.2.2+
        {
            display.getRealMetrics(metrics);

            realW = metrics.widthPixels;
            realH = metrics.heightPixels;
        } else {
            realW = width;
            realH = height;
        }
    }

    @Override
    public void onClick(View arg0) {
        final Fragment f = getVisibleFragment();
        if (f instanceof UtilFragment)
            ((UtilFragment) f).onClick(arg0);
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null) {
            return null;
        }
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()) {
                return fragment;
            }
        }
        return null;
    }

//    public void goFragment(final Fragment fragment, boolean backstack) {
//        if (getVisibleFragment() != null && getVisibleFragment().getClass() == fragment.getClass() && fragment.getArguments() == null)
//            return;
//
//        FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
//        if (backstack) {
//            fr = fr.addToBackStack(fragment.toString());
//        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        }
//
//        fr.setCustomAnimations(R.anim.slide_in_right,
//                R.anim.slide_out_left,
//                android.R.anim.slide_in_left,
//                android.R.anim.slide_out_right)
//                .replace(R.id.frag, fragment).commitAllowingStateLoss();
//
//        onFragmentChanged(fragment);
//    }

    public String S(int res) {
        return getString(res);
    }

    protected boolean isLand() {
        return Util.isLand(_this);
    }

    protected void Hide(int res) {
        findViewById(res).setVisibility(View.GONE);
    }

    protected void Hide(View res) {
        res.setVisibility(View.GONE);
    }

    protected void Hide2(int res) {
        findViewById(res).setVisibility(View.INVISIBLE);
    }

    protected void Hide2(View res) {
        res.setVisibility(View.INVISIBLE);
    }

    protected void Show(int res) {
        findViewById(res).setVisibility(View.VISIBLE);
    }

    protected void Show(View res) {
        res.setVisibility(View.VISIBLE);
    }

    public static long NOW() {
        return System.currentTimeMillis();
    }

    public <T> T f(int id) {
        return (T) findViewById(id);
    }

    protected View fv(int id) {
        return findViewById(id);
    }


    public static void showDialog(final String msg) {
        _this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (_this.pDialog.isShowing() || _this.isFinishing()) {
                    return;
                }

                _this.pDialog.setTitle("");
                _this.pDialog.setMessage(msg);
                _this.pDialog.setIndeterminate(true);
                // ua.pDialog.setCancelable(false);
                _this.pDialog.show();

                {
                    TextView tv1 = (TextView) _this.pDialog
                            .findViewById(android.R.id.message);
                    tv1.setTextColor(Color.WHITE);
                    ((android.widget.LinearLayout) tv1.getParent())
                            .setBackgroundColor(Color.BLACK);
                }
            }
        });
    }

    public static void cancelDial() {
        _this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (_this.pDialog.isShowing()) {
                    _this.pDialog.cancel();
                }
            }
        });
    }

    protected static void runDelayed(final Runnable r, final int delay) {
        _this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(r, delay);
            }
        });
    }

    public static void run(final Runnable r) {
        _this.runOnUiThread(r);
    }

    public void back() {
        Util.back(this);
    }

    protected void startActivity(Class<?> ac) {
        startActivity(new Intent(this, ac));
    }

    protected void startActivity(Class<?> ac, Bundle extra) {
        startActivity(new Intent(this, ac).putExtras(extra));
    }

    protected void startActivityForResult(Class<?> ac, int code) {
        startActivityForResult(new Intent(this, ac), code);
    }

    protected void startActivityForResult(Class<?> ac, int code, Bundle extra) {
        startActivityForResult(new Intent(this, ac).putExtras(extra), code);
    }

    //@Override
    public void onTaskFinish(Object[] param) {
    }

    @Override
    protected void onDestroy() {
        currentlyVisible = false;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        currentlyVisible = false;
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && blockBack) {
            // preventing any action
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
