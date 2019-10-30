package com.nanlt.example.utils;

import android.content.Context;
import android.widget.Toast;

public class ParamsSort {
    public void math(Context context){
        int a =10;
        int b =2;
        Toast.makeText(context,"math=第二个修补包==>"+(a/b),Toast.LENGTH_LONG).show();
    }

    public void show(Context context,TextData textData){
        if(textData!=null)
        Toast.makeText(context,textData.getName(),Toast.LENGTH_LONG).show();
    }
    class TextData{
        String name;
        public String getName() {
            return name;
        }
    }

}
