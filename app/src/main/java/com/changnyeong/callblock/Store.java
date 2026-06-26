package com.changnyeong.callblock;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/** 차단 번호 목록을 폰에 저장/조회하는 도우미. */
public class Store {
    private static final String PREF = "blocklist_pref";
    private static final String KEY_BLOCKED = "blocked_numbers";
    private static final String KEY_LAST = "last_number";

    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    /**
     * 번호를 한 가지 형태로 통일.
     * - 숫자만 남기고
     * - 국가코드 82(+82)로 시작하면 맨 앞을 0으로 바꿔 국내 형식과 맞춘다.
     *   예: "+82 10-1234-5678" -> "01012345678",  "010-1234-5678" -> "01012345678"
     */
    static String normalize(String number) {
        if (number == null) return "";
        String d = number.replaceAll("[^0-9]", "");
        if (d.startsWith("82")) {
            d = "0" + d.substring(2);
        }
        return d;
    }

    static Set<String> getBlocked(Context c) {
        return new TreeSet<>(sp(c).getStringSet(KEY_BLOCKED, new HashSet<String>()));
    }

    static boolean isBlocked(Context c, String number) {
        String n = normalize(number);
        if (n.isEmpty()) return false;
        return sp(c).getStringSet(KEY_BLOCKED, new HashSet<String>()).contains(n);
    }

    static void add(Context c, String number) {
        String n = normalize(number);
        if (n.isEmpty()) return;
        Set<String> s = new HashSet<>(sp(c).getStringSet(KEY_BLOCKED, new HashSet<String>()));
        s.add(n);
        sp(c).edit().putStringSet(KEY_BLOCKED, s).apply();
    }

    static void remove(Context c, String number) {
        Set<String> s = new HashSet<>(sp(c).getStringSet(KEY_BLOCKED, new HashSet<String>()));
        s.remove(normalize(number));
        sp(c).edit().putStringSet(KEY_BLOCKED, s).apply();
    }

    static void setLast(Context c, String number) {
        sp(c).edit().putString(KEY_LAST, normalize(number)).apply();
    }

    static String getLast(Context c) {
        return sp(c).getString(KEY_LAST, "");
    }
}
