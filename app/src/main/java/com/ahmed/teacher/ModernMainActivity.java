package com.ahmed.teacher;

import android.app.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import java.util.*;

/** Modern visual layer for the existing grading screens. Keeps MainActivity logic/data intact. */
public class ModernMainActivity extends MainActivity {
    private final Handler ui = new Handler(Looper.getMainLooper());
    private int dp(float v){ return (int)(v*getResources().getDisplayMetrics().density+.5f); }
    @Override public void onCreate(Bundle b){ super.onCreate(b); schedulePolish(); }
    @Override protected void onResume(){ super.onResume(); schedulePolish(); }
    private void schedulePolish(){ ui.postDelayed(() -> { try { polish(); } catch(Exception ignored) {} }, 180); ui.postDelayed(() -> { try { polish(); } catch(Exception ignored) {} }, 650); }
    private GradientDrawable bg(int color, int stroke){
        GradientDrawable g=new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp(12)); g.setStroke(dp(stroke),Color.rgb(190,200,220)); return g;
    }
    private void polish(){
        ViewGroup content=findViewById(android.R.id.content); if(content==null) return;
        String screen=screenText(content);
        boolean weekly=screen.contains("التقييم الأسبوعي");
        boolean monthly=screen.contains("التقييم الشهري") || screen.contains("الاختبارات");
        if(!weekly && !monthly) return;
        ArrayList<ViewGroup> rows=new ArrayList<>(); collectRows(content,rows);
        int rowNo=0;
        for(ViewGroup row:rows){
            ArrayList<EditText> edits=new ArrayList<>(); collectDirectEdits(row,edits);
            if(edits.size()<1) continue;
            if(edits.size()>=2 || (weekly && edits.size()==1)){
                row.setBackground(bg((rowNo++%2==0)?Color.rgb(249,252,255):Color.rgb(238,248,242),1));
                row.setPadding(dp(5),dp(5),dp(5),dp(5));
                styleBoxes(edits,weekly,monthly);
            }
        }
    }
    private String screenText(View v){ StringBuilder s=new StringBuilder(); collectText(v,s); return s.toString(); }
    private void collectText(View v,StringBuilder s){
        if(v instanceof TextView) s.append(((TextView)v).getText()).append(' ');
        if(v instanceof ViewGroup){ ViewGroup g=(ViewGroup)v; for(int i=0;i<g.getChildCount();i++) collectText(g.getChildAt(i),s); }
    }
    private void collectRows(View v,ArrayList<ViewGroup> out){
        if(!(v instanceof ViewGroup)) return; ViewGroup g=(ViewGroup)v; ArrayList<EditText> e=new ArrayList<>(); collectDirectEdits(g,e);
        if(e.size()>=1 && g.getChildCount()>=2) out.add(g);
        for(int i=0;i<g.getChildCount();i++) collectRows(g.getChildAt(i),out);
    }
    private void collectDirectEdits(ViewGroup g,ArrayList<EditText> out){
        for(int i=0;i<g.getChildCount();i++) if(g.getChildAt(i) instanceof EditText) out.add((EditText)g.getChildAt(i));
    }
    private void styleBoxes(ArrayList<EditText> edits,boolean weekly,boolean monthly){
        int[] max=weekly?new int[]{5,5,10,5}:new int[]{15,15,15,15};
        int i=0;
        for(EditText e:edits){
            e.setTextSize(20); e.setGravity(Gravity.CENTER); e.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
            e.setPadding(dp(8),dp(7),dp(8),dp(7)); e.setSelectAllOnFocus(false);
            int limit=max[Math.min(i,max.length-1)];
            e.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            e.setBackground(bg(Color.WHITE,2)); e.setElevation(dp(3));
            if(e.getParent() instanceof LinearLayout && !hasScoreLabel((ViewGroup)e.getParent(),e)) addLabel((ViewGroup)e.getParent(),e,limit);
            i++;
        }
    }
    private boolean hasScoreLabel(ViewGroup p,EditText e){
        for(int i=0;i<p.getChildCount();i++){
            View c=p.getChildAt(i); if(c instanceof TextView && c!=e){String t=((TextView)c).getText().toString(); if(t.equals("/5")||t.equals("/10")||t.equals("/15")||t.equals("/40")) return true;}
        } return false;
    }
    private void addLabel(ViewGroup parent,EditText e,int max){
        int idx=parent.indexOfChild(e); if(idx<0) return; ViewGroup.LayoutParams old=e.getLayoutParams(); parent.removeView(e);
        LinearLayout wrap=new LinearLayout(this); wrap.setOrientation(LinearLayout.VERTICAL); wrap.setGravity(Gravity.CENTER); wrap.setPadding(dp(2),0,dp(2),0);
        TextView label=new TextView(this); label.setText("/"+max); label.setTextSize(13); label.setTextColor(Color.rgb(30,55,100)); label.setGravity(Gravity.CENTER); label.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        wrap.addView(label,new LinearLayout.LayoutParams(-1,dp(24))); wrap.addView(e,new LinearLayout.LayoutParams(-1,dp(54)));
        wrap.setLayoutParams(old); parent.addView(wrap,idx);
    }
}
