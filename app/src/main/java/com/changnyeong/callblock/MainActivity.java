package com.changnyeong.callblock;

import android.app.Activity;
import android.app.role.RoleManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {
    private LinearLayout listContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button roleBtn = findViewById(R.id.btn_role);
        roleBtn.setOnClickListener(v -> requestRole());

        EditText input = findViewById(R.id.input_number);
        Button addBtn = findViewById(R.id.btn_add);
        addBtn.setOnClickListener(v -> {
            String num = input.getText().toString().trim();
            if (!num.isEmpty()) {
                Store.add(this, num);
                input.setText("");
                refresh();
            }
        });

        listContainer = findViewById(R.id.list_container);
        requestRuntimePerms();
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    /** 발신자 표시/스팸 앱(Call Screening)으로 이 앱을 지정 요청. */
    private void requestRole() {
        if (Build.VERSION.SDK_INT >= 29) {
            RoleManager rm = getSystemService(RoleManager.class);
            if (rm != null && rm.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
                if (rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                    Toast.makeText(this, "이미 켜져 있어요", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = rm.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
                    startActivityForResult(i, 1);
                }
            } else {
                Toast.makeText(this, "이 기기에선 이 기능을 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestRuntimePerms() {
        List<String> need = new ArrayList<>();
        if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            need.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            need.add(android.Manifest.permission.POST_NOTIFICATIONS);
        }
        if (!need.isEmpty()) {
            requestPermissions(need.toArray(new String[0]), 2);
        }
    }

    /** 차단 목록 화면 새로고침. */
    private void refresh() {
        if (listContainer == null) return;
        listContainer.removeAllViews();
        Set<String> blocked = Store.getBlocked(this);

        TextView count = findViewById(R.id.count);
        count.setText("차단된 번호: " + blocked.size() + "개");

        for (final String num : blocked) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 16, 0, 16);

            TextView t = new TextView(this);
            t.setText(num);
            t.setTextSize(18);
            t.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            Button del = new Button(this);
            del.setText("해제");
            del.setOnClickListener(v -> {
                Store.remove(this, num);
                refresh();
            });

            row.addView(t);
            row.addView(del);
            listContainer.addView(row);
        }
    }
}
