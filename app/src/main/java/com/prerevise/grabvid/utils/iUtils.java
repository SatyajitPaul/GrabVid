package com.prerevise.grabvid.utils;

import android.content.Context;
import android.widget.Toast;

public class iUtils {

    public static void ShowToast(Context context, String str){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
    }
}
