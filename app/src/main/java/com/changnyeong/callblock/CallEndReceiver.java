package com.changnyeong.callblock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

/**
 * 통화 상태가 '대기(IDLE)'로 돌아오면 = 방금 통화가 끝난 것.
 * 직전에 받은 번호가 아직 차단되지 않았으면 '차단?' 알림을 띄운다.
 */
public class CallEndReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            String last = Store.getLast(context);
            if (last != null && !last.isEmpty() && !Store.isBlocked(context, last)) {
                Notifications.showBlockPrompt(context, last);
                Store.setLast(context, ""); // 한 번만 묻기
            }
        }
    }
}
