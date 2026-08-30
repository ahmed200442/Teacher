package com.ahmed.teacher;

import android.app.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import java.util.*;

/** Modern visual layer for grading screens. */
public class ModernMainActivity extends MainActivity {
    private final Handler ui = new Handler(Looper.getMainLooper());
    private final Runnable poller = new Runnable(){ public void run(){ try{ polish(); }catch(Exception ignored){} ui.postDelayed(this,1000); } };
    private int dp(float v){ return (int)(v*getResources().getDisplayMetrics().density+.5f); }
    @Override public void onCreate(Bundle b){ super.onCreate(b); startPolishLoop(); }
    @Override protected void onResume(){ super.onResume(); startPolishLoop(); }
    @Override protected void onPause(){ super.onPause(); ui.removeCallbacks(poller); }
    private void startPolishLoop(){ ui.removeCallbacks(poller); ui.postDelayed(poller,250); }

    private GradientDrawable shape(int fill,int stroke,int radius){
        GradientDrawable g=new GradientDrawable(); g.setColor(fill); g.setCornerRadius(dp(radius));
        g.setStroke(dp(stroke),Color.rgb(190,200,220)); return g;
    }

    private void polish(){
        ViewGroup root=findViewById(android.R.id.content); if(root==null)return;
        String s=screenText(root); boolean weekly=s.contains("التقييم الأسبوعي");
        boolean monthly=s.contains("التقييم الشهري")||s.contains("الاختبارات"); if(!weekly&&!monthly)return;
        ArrayList<ViewGroup> rows=new ArrayList<>(); collectRows(root,rows); int n=0;
        for(ViewGroup row:rows){
            if("modern_grade_row".equals(row.getTag()))continue;
            ArrayList<EditText> edits=new ArrayList<>(); collectDirectEdits(row,edits); if(edits.isEmpty())continue;
            row.setTag("modern_grade_row");
            row.setBackground(shape((n++%2==0)?Color.rgb(249,252,255):Color.rgb(237,247,241),1,16));
            row.setPadding(dp(6),dp(6),dp(6),dp(6));
            styleRow(row,edits,weekly);
        }
    }

    private String screenText(View v){StringBuilder b=new StringBuilder();collectText(v,b);return b.toString();}
    private void collectText(View v,StringBuilder b){
        if(v instanceof TextView)b.append(((TextView)v).getText()).append(' ');
        if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++)collectText(g.getChildAt(i),b);}
    }
    private void collectRows(View v,ArrayList<ViewGroup> out){
        if(!(v instanceof ViewGroup))return; ViewGroup g=(ViewGroup)v; ArrayList<EditText> e=new ArrayList<>();collectDirectEdits(g,e);
        if(e.size()>=1&&g.getChildCount()>=2)out.add(g);
        for(int i=0;i<g.getChildCount();i++)collectRows(g.getChildAt(i),out);
    }
    private void collectDirectEdits(ViewGroup g,ArrayList<EditText> out){for(int i=0;i<g.getChildCount();i++)if(g.getChildAt(i) instanceof EditText)out.add((EditText)g.getChildAt(i));}

    private String title(boolean weekly,int i){
        if(weekly){String[] a={"واجب منزلي","كراسة الحصة","تقييم أسبوعي","المواظبة والسلوك"};return a[Math.min(i,a.length-1)];}
        String[] a={"الاختبار الأول","الاختبار الثاني","المتوسط","التقييمات"};return a[Math.min(i,a.length-1)];
    }
    private int max(boolean weekly,int i){
        if(weekly){int[] a={5,5,10,5};return a[Math.min(i,a.length-1)];}
        int[] a={15,15,15,25};return a[Math.min(i,a.length-1)];
    }

    private void styleRow(ViewGroup row,ArrayList<EditText> edits,boolean weekly){
        if(!(row instanceof LinearLayout)){for(EditText e:edits)styleEdit(e);return;}
        LinearLayout parent=(LinearLayout)row; parent.setOrientation(LinearLayout.HORIZONTAL); parent.setGravity(Gravity.CENTER_VERTICAL);
        for(int i=0;i<edits.size();i++){
            EditText e=edits.get(i); if(e.getParent()!=parent)continue; int idx=parent.indexOfChild(e); if(idx<0)continue;
            parent.removeView(e); styleEdit(e);
            LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setGravity(Gravity.CENTER); card.setPadding(dp(3),dp(3),dp(3),dp(3));
            card.setBackground(shape((i%2==0)?Color.rgb(248,251,255):Color.rgb(242,248,245),2,14)); card.setElevation(dp(3));
            TextView t=new TextView(this); t.setText(title(weekly,i)); t.setTextSize(13); t.setTextColor(Color.rgb(25,50,95)); t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); t.setGravity(Gravity.CENTER); t.setMaxLines(2); t.setEllipsize(null);
            TextView m=new TextView(this); m.setText("الدرجة من /"+max(weekly,i)); m.setTextSize(12); m.setTextColor(Color.rgb(65,85,120)); m.setGravity(Gravity.CENTER); m.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
            card.addView(t,new LinearLayout.LayoutParams(-1,dp(36))); card.addView(m,new LinearLayout.LayoutParams(-1,dp(24)));
            e.setHint("اكتب الدرجة"); card.addView(e,new LinearLayout.LayoutParams(-1,dp(52)));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(120),1f); lp.setMargins(dp(3),dp(2),dp(3),dp(2));
            parent.addView(card,Math.min(idx,parent.getChildCount()),lp);
        }
    }

    private void styleEdit(EditText e){
        e.setTextSize(21); e.setGravity(Gravity.CENTER); e.setTypeface(Typeface.DEFAULT,Typeface.BOLD); e.setPadding(dp(5),0,dp(5),0);
        e.setSelectAllOnFocus(false); e.setSingleLine(true); e.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        e.setBackground(shape(Color.WHITE,2,10)); e.setElevation(dp(2)); e.setMinWidth(0);
    }
}
