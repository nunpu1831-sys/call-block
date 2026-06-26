package com.changnyeong.callblock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/** 통화 후 '차단?' 알림(버튼 1개)을 만든다. */
public class Notifications {
    static final String CH_PROMPT = "block_prompt";

    private static void ensureChannel(Context c) {
        NotificationManager nm = c.getSystemService(NotificationManager.class);
        NotificationChannel ch = new NotificationChannel(
                CH_PROMPT, "통화 후 차단 물어보기",
                NotificationManager.IMPORTANCE_HIGH);
        ch.setDescription("통화가 끝나면 그 번호를 차단할지 물어봅니다.");
        nm.createNotificationChannel(ch);
    }

    static void showBlockPrompt(Context c, String number) {
        ensureChannel(c);

        Intent block = new Intent(c, BlockActionReceiver.class);
        block.setAction("ACTION_BLOCK");
        block.putExtra("number", number);
        PendingIntent blockPi = PendingIntent.getBroadcast(
                c, number.hashCode(), block,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification.Action action = new Notification.Action.Builder(
                android.R.drawable.ic_menu_close_clear_cancel, "차단", blockPi).build();

        Notification n = new Notification.Builder(c, CH_PROMPT)
                .setSmallIcon(android.R.drawable.sym_action_call)
                .setContentTitle("방금 통화: " + number)
                .setContentText("이 번호를 차단할까요? 누르면 다음부터 자동 거절됩니다.")
                .addAction(action)
                .setAutoCancel(true)
                .build();

        c.getSystemService(NotificationManager.class).notify(number.hashCode(), n);
    }
}
