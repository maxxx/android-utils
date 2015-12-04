/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Maxim Smirnov on 04.12.15.
 */
public class DateEditText extends AppCompatEditText
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private Context _context;
    protected String format = "dd-MM-yyyy";
    private Calendar myCalendar;


    public DateEditText(final Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        myCalendar = Calendar.getInstance();
        _context = context;
        setFocusable(false);
        setOnClickListener(this);
    }

    public DateEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DateEditText(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        SimpleDateFormat sdformat = new SimpleDateFormat(format, Locale.US);
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setText(sdformat.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        DatePickerDialog dialog = new DatePickerDialog(_context, this, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(final String format) {
        this.format = format;
    }
}