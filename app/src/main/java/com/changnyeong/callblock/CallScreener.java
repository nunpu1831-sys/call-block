package com.changnyeong.callblock;

import android.telecom.Call;
import android.telecom.CallScreeningService;

/**
 * 걸려오는 전화를 검사한다.
 * - 차단 목록에 있으면: 무음으로 자동 거절.
 * - 아니면: 그냥 받게 두고, 통화 끝난 뒤 '차단?' 물어보려고 번호를 기억해 둔다.
 * 문자(SMS)는 전혀 건드리지 않는다.
 */
public class CallScreener extends CallScreeningService {
    @Override
    public void onScreenCall(Call.Details details) {
        String number = "";
        if (details.getHandle() != null) {
            number = details.getHandle().getSchemeSpecificPart();
        }
        boolean incoming = details.getCallDirection() == Call.Details.DIRECTION_INCOMING;

        CallResponse.Builder b = new CallResponse.Builder();

        if (incoming && Store.isBlocked(this, number)) {
            // 차단 번호 -> 무음 거절
            b.setDisallowCall(true);
            b.setRejectCall(true);
            b.setSilenceCall(true);
            b.setSkipCallLog(false);
            b.setSkipNotification(true);
            respondToCall(details, b.build());
        } else {
            // 허용. 차단 안 된 수신 전화면 '차단?' 알림을 바로 띄운다.
            // (전화 오는 순간 알림이 생기고, 통화가 끝난 뒤에도 알림창에 남아 있어
            //  거기서 '차단'을 한 번 누르면 됨)
            respondToCall(details, b.build());
            if (incoming && !number.isEmpty()) {
                Notifications.showBlockPrompt(this, number);
            }
        }
    }
}
