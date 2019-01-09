package com.example.admin.doppelkopfapp;

import android.app.Activity;
import android.view.View;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by delv on 8/1/2017.
 */

public class AndroidUtils {

    public static View findViewByName(Activity context, String name) {
        return context.findViewById(context.getResources().getIdentifier(name, "id", context.getPackageName()));
    }

    public static String getDate() {
        DateFormat format = DateFormat.getDateInstance();
        Date date = new Date();
        return format.format(date);
    }

}
