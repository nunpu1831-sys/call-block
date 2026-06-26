package com.changnyeong.callblock;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/** 알림에서 '차단'을 누르면: 목록에 추가하고 알림을 지운다. */
public class BlockActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String number = intent.getStringExtra("number");
        if (number != null && !number.isEmpty()) {
            Store.add(context, number);
            Toast.makeText(context, number + " 차단됨 — 다음부터 자동 거절", Toast.LENGTH_LONG).show();
            context.getSystemService(NotificationManager.class).cancel(number.hashCode());
        }
    }
}
