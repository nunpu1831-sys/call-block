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

    /** 숫자와 +만 남겨 번호를 통일된 형태로. */
    static String normalize(String number) {
        if (number == null) return "";
        return number.replaceAll("[^0-9+]", "");
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
