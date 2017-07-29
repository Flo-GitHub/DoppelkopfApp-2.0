package com.example.admin.doppelkopfapp;

import android.app.Activity;
import android.view.View;

/**
 * Class that contains some Utils that do not implement any logic
 */

public class AndroidUtils {

    public static View findViewByName(String name, Activity context) {
        int id = context.getResources().getIdentifier(name, "id", context.getPackageName());
        return context.findViewById(id);
    }

}
