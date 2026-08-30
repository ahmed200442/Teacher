package com.ahmed.teacher;

import android.app.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import java.util.*;

/** Visual polish for grading screens. Keeps the existing MainActivity data and grading logic intact. */
public class ModernMainActivity extends MainActivity {
    private final Handler ui = new Handler(Looper.getMainLooper());
    private final Runnable poller = new Runnable(){
        public void run(){
            try { polish(); } catch(Exception ignored) {}
            ui.postDelayed(this, 900);
        }
    };

    private int dp(float v){ return (int)(v * getResources().getDisplayMetrics().density + .5f); }

    @Override public void onCreate(Bundle b){ super.onCreate(b); startPolishLoop(); }
    @Override protected void onResume(){ super.onResume(); startPolishLoop(); }
    @Override protected void onPause(){ super.onPause(); ui.removeCallbacks(poller); }

    private void startPolishLoop(){
        ui.removeCallbacks(poller);
        ui.postDelayed(poller, 250);
    }

    private GradientDrawable boxBackground(int fill, int stroke, int radiusDp){
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        g.setCornerRadius(dp(radiusDp));
        g.setStroke(dp(stroke), Color.rgb(190,200,220));
        return g;
    }

    private GradientDrawable rowBackground(int fill){
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        g.setCornerRadius(dp(16));
        g.setStroke(dp(1), Color.rgb(205,213,225));
        return g;
    }

    private void polish(){
        ViewGroup content = findViewById(android.R.id.content);
        if(content == null) return;

        String screen = screenText(content);
        boolean weekly = screen.contains("التقييم الأسبوعي");
        boolean monthly = screen.contains("التقييم الشهري") || screen.contains("الاختبارات");
        if(!weekly && !monthly) return;

        ArrayList<ViewGroup> rows = new ArrayList<>();
        collectRows(content, rows);
        int rowNo = 0;
        for(ViewGroup row : rows){
            if(row.getTag() != null && "modern_grade_row".equals(row.getTag())) continue;
            ArrayList<EditText> edits = new ArrayList<>();
            collectDirectEdits(row, edits);
            if(edits.size() == 0) continue;

            row.setTag("modern_grade_row");
            row.setBackground(rowBackground((rowNo++ % 2 == 0)
                    ? Color.rgb(249,252,255)
                    : Color.rgb(238,248,242)));
            row.setPadding(dp(7), dp(7), dp(7), dp(7));
            styleBoxes(row, edits, weekly, monthly);
        }
    }

    private String screenText(View v){
        StringBuilder s = new StringBuilder();
        collectText(v, s);
        return s.toString();
    }

    private void collectText(View v, StringBuilder s){
        if(v instanceof TextView) s.append(((TextView)v).getText()).append(' ');
        if(v instanceof ViewGroup){
            ViewGroup g = (ViewGroup)v;
            for(int i=0;i<g.getChildCount();i++) collectText(g.getChildAt(i), s);
        }
    }

    private void collectRows(View v, ArrayList<ViewGroup> out){
        if(!(v instanceof ViewGroup)) return;
        ViewGroup g = (ViewGroup)v;
        ArrayList<EditText> e = new ArrayList<>();
        collectDirectEdits(g, e);
        if(e.size() >= 1 && g.getChildCount() >= 2) out.add(g);
        for(int i=0;i<g.getChildCount();i++) collectRows(g.getChildAt(i), out);
    }

    private void collectDirectEdits(ViewGroup g, ArrayList<EditText> out){
        for(int i=0;i<g.getChildCount();i++){
            View c = g.getChildAt(i);
            if(c instanceof EditText) out.add((EditText)c);
        }
    }

    private String labelFor(boolean weekly, int index){
        if(weekly){
            String[] names = {"واجب منزلي", "كراسة الحصة", "تقييم أسبوعي", "المواظبة والسلوك"};
            return names[Math.min(index, names.length-1)];
        }
        String[] names = {"الاختبار الأول", "الاختبار الثاني", "المتوسط", "التقييمات"};
        return names[Math.min(index, names.length-1)];
    }

    private int maxFor(boolean weekly, int index){
        if(weekly){
            int[] max = {5,5,10,5};
            return max[Math.min(index, max.length-1)];
        }
        int[] max = {15,15,15,25};
        return max[Math.min(index, max.length-1)];
    }

    private void styleBoxes(ViewGroup row, ArrayList<EditText> edits, boolean weekly, boolean monthly){
        if(!(row instanceof LinearLayout)){
            for(EditText e : edits) styleEdit(e);
            return;
        }

        LinearLayout parent = (LinearLayout)row;
        parent.setOrientation(LinearLayout.HORIZONTAL);
        parent.setGravity(Gravity.CENTER_VERTICAL);

        for(int i=0;i<edits.size();i++){
            EditText e = edits.get(i);
            if(e.getParent() != parent) continue;

            int idx = parent.indexOfChild(e);
            if(idx < 0) continue;

            parent.removeView(e);
            styleEdit(e);

            LinearLayout wrap = new LinearLayout(this);
            wrap.setOrientation(LinearLayout.VERTICAL);
            wrap.setGravity(Gravity.CENTER);
            wrap.setPadding(dp(4), dp(2), dp(4), dp(2));
            wrap.setBackground(boxBackground(Color.WHITE, 1, 14));
            wrap.setElevation(dp(3));

            TextView limit = new TextView(this);
            limit.setText("/" + maxFor(weekly, i));
            limit.setTextSize(14);
            limit.setTextColor(Color.rgb(25,50,95));
            limit.setGravity(Gravity.CENTER);
            limit.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

            TextView title = new TextView(this);
            title.setText(labelFor(weekly, i));
            title.setTextSize(14);
            title.setTextColor(Color.rgb(25,50,95));
            title.setGravity(Gravity.CENTER);
            title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            title.setSingleLine(false);

            wrap.addView(limit, new LinearLayout.LayoutParams(-1, dp(23)));
            wrap.addView(title, new LinearLayout.LayoutParams(-1, dp(32)));
            e.setHint("");
            wrap.addView(e, new LinearLayout.LayoutParams(-1, dp(58)));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1f);
            lp.setMargins(dp(3), dp(2), dp(3), dp(2));
            parent.addView(wrap, Math.min(idx, parent.getChildCount()), lp);
        }
    }

    private void styleEdit(EditText e){
        e.setTextSize(21);
        e.setGravity(Gravity.CENTER);
        e.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        e.setPadding(dp(6), dp(3), dp(6), dp(3));
        e.setSelectAllOnFocus(false);
        e.setSingleLine(true);
        e.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        e.setBackground(boxBackground(Color.WHITE, 2, 12));
        e.setElevation(dp(2));
        e.setMinWidth(0);
    }
}
