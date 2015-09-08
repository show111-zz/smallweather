package com.example.smallweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.smallweather.service.AutoUpdateService;

/**
 * Created by lhf on 2015/9/8.
 */
public class AutoUpdateReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context,AutoUpdateService.class);
        context.startActivity(i);
    }

}
