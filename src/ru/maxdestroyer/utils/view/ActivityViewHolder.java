/*
 * Copyright (C) 2016 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.view;

import android.app.Activity;
import android.view.View;
import butterknife.ButterKnife;

/**
 * Created by Maxim Smirnov
 */
public abstract class ActivityViewHolder {

  protected View root;

  public View build(Activity context, int layId) {
    root = context.getLayoutInflater().inflate(layId, null);
    ButterKnife.bind(this, root);
    return root;
  }

  public View getRootView() {
    return root;
  }
}
