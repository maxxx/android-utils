/*
 * Copyright (C) 2016 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import ru.maxdestroyer.utils.Util;
import ru.maxdestroyer.utils.UtilConfig;
import ru.maxdestroyer.utils.fragment.UtilFragment;
import ru.maxdestroyer.utils.view.ActivityViewHolder;

/**
 * @param <T> - required, must use your own holder
 */
public abstract class UtilCompatActivityWithHolder<T extends ActivityViewHolder>
    extends AppCompatActivity implements OnClickListener {
  protected T holder;
  private Toast toast = null;
  public ProgressDialog pDialog = null;
  public static UtilConfig cfg = null;
  public boolean currentlyVisible = true;
  public Handler handler = null;
  protected boolean blockBack = false;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (!hasTitle()) {
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    if (getLayoutId() != 0) {
      Class<T> holderClass = getGenericTypeClass();
      holder = buildHolder(holderClass);
      setContentView(holder.getRootView());
    } else {
      throw new RuntimeException(getClass().getSimpleName() + " called with layout id = 0");
    }

    cfg = UtilConfig.getInstance().init(this);
    pDialog = new ProgressDialog(this);
    handler = new Handler();
  }

  private T buildHolder(Class<T> holderClass) {
    Constructor<?> constructor = holderClass.getConstructors()[0];
    try {
      T instance = (T) constructor.newInstance();
      instance.build(this, getLayoutId());
      return instance;
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  protected boolean hasTitle() {
    return true;
  }

  @Override public void setContentView(View v) {
    super.setContentView(v);
    ButterKnife.bind(this);
  }

  /**
   * Layout id,
   *
   * @return avoid 0 value
   */
  protected abstract int getLayoutId();

  protected void msg(Object text) {
    Toast.makeText(this, String.valueOf(text), Toast.LENGTH_SHORT).show();
  }

  protected void hideMsg() {
    if (toast != null) {
      toast.cancel();
    }
  }

  protected void log(Object text) {
    Util.log(text);
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  @Override public void onClick(View arg0) {
    final Fragment f = getVisibleFragment();
    if (f instanceof UtilFragment) ((UtilFragment) f).onClick(arg0);
  }

  @Nullable public Fragment getVisibleFragment() {
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

  public String S(int res) {
    return getString(res);
  }

  protected boolean isLand() {
    return Util.isLand(this);
  }

  protected void hide(int res) {
    findViewById(res).setVisibility(View.GONE);
  }

  protected void hide(View res) {
    res.setVisibility(View.GONE);
  }

  protected void show(int res) {
    findViewById(res).setVisibility(View.VISIBLE);
  }

  protected void show(View res) {
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

  public void showDialog(final String msg) {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        if (pDialog.isShowing() || isFinishing()) {
          return;
        }

        pDialog.setTitle("");
        pDialog.setMessage(msg);
        pDialog.setIndeterminate(true);
        // ua.pDialog.setCancelable(false);
        pDialog.show();

        {
          TextView tv1 = (TextView) pDialog.findViewById(android.R.id.message);
          tv1.setTextColor(Color.WHITE);
          ((android.widget.LinearLayout) tv1.getParent()).setBackgroundColor(Color.BLACK);
        }
      }
    });
  }

  public void cancelDial() {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        if (pDialog != null && pDialog.isShowing()) {
          pDialog.cancel();
          pDialog = null;
        }
      }
    });
  }

  protected void runDelayed(final Runnable r, final long delay) {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        new Handler().postDelayed(r, delay);
      }
    });
  }

  public void run(final Runnable r) {
    runOnUiThread(r);
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

  @Override protected void onDestroy() {
    currentlyVisible = false;
    super.onDestroy();
  }

  @Override protected void onPause() {
    currentlyVisible = false;
    super.onPause();
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && blockBack) {
      // preventing any action
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @SuppressWarnings("unchecked") private Class<T> getGenericTypeClass() {
    try {
      String className =
          ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getClass()
              .getName();
      Class<?> clazz = Class.forName(className);
      return (Class<T>) clazz;
    } catch (Exception e) {
      throw new IllegalStateException(
          "Class is not parametrized with generic type!!! Please use extends <> ");
    }
  }
}
