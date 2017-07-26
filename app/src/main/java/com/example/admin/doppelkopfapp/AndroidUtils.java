package com.example.admin.doppelkopfapp;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by Admin on 12/07/2017.
 */

public class AndroidUtils {

    public static View findViewByName(String name, Activity context) {
        int id = context.getResources().getIdentifier(name, "id", context.getPackageName());
        return context.findViewById(id);
    }

}
