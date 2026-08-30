package com.ahmed.teacher;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(20), dp(28), dp(20), dp(20));
        root.setBackgroundColor(Color.rgb(248, 249, 252));

        TextView title = new TextView(this);
        title.setText("Teacher");
        title.setTextSize(30);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.rgb(25, 45, 90));
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(55)));

        TextView subtitle = new TextView(this);
        subtitle.setText("مساعد المعلم");
        subtitle.setTextSize(22);
        subtitle.setTextColor(Color.DKGRAY);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle, new LinearLayout.LayoutParams(-1, dp(55)));

        addButton(root, "📚 حصص اليوم", "حصص اليوم");
        addButton(root, "📝 المهام السريعة", "المهام السريعة");
        addButton(root, "👨‍🎓 الطلاب", "الطلاب");
        addButton(root, "📊 التقارير", "التقارير");
        addButton(root, "⚙️ الإعدادات", "الإعدادات");

        TextView footer = new TextView(this);
        footer.setText("الإصدار 1.0 • تطبيق للمعلمين");
        footer.setTextSize(14);
        footer.setTextColor(Color.GRAY);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footerParams = new LinearLayout.LayoutParams(-1, dp(50));
        footerParams.topMargin = dp(12);
        root.addView(footer, footerParams);

        setContentView(root);
    }

    private void addButton(LinearLayout root, String text, final String toastText) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(17);
        button.setAllCaps(false);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(Color.rgb(43, 91, 181));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(58));
        params.topMargin = dp(8);
        root.addView(button, params);
    }
}
