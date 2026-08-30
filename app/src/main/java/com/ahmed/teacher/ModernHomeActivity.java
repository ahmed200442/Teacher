package com.ahmed.teacher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class ModernHomeActivity extends Activity {
    private int dp(float v){ return (int)(v*getResources().getDisplayMetrics().density+.5f); }

    @Override public void onCreate(Bundle b){ super.onCreate(b); buildHome(); }

    private TextView text(String s,float size,int color,boolean bold){
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER); t.setTypeface(Typeface.DEFAULT,bold?Typeface.BOLD:Typeface.NORMAL);
        t.setPadding(dp(8),dp(6),dp(8),dp(6)); return t;
    }
    private GradientDrawable bg(int color,float radius){ GradientDrawable g=new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp(radius)); return g; }
    private GradientDrawable gradient(){ GradientDrawable g=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(77,61,235),Color.rgb(122,74,242)}); g.setCornerRadius(dp(24)); return g; }
    private LinearLayout card(){ LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setGravity(Gravity.CENTER); l.setPadding(dp(8),dp(10),dp(8),dp(10)); l.setBackground(bg(Color.WHITE,18)); return l; }
    private void addGap(LinearLayout root,int h){ Space s=new Space(this); s.setLayoutParams(new LinearLayout.LayoutParams(1,dp(h))); root.addView(s); }
    private Button action(String title,String sub,View.OnClickListener click){
        LinearLayout box=card(); box.setOnClickListener(click); box.setClickable(true);
        TextView a=text(title,16,Color.rgb(25,35,75),true); TextView b=text(sub,11,Color.rgb(105,115,140),false);
        box.addView(a,new LinearLayout.LayoutParams(-1,dp(34))); box.addView(b,new LinearLayout.LayoutParams(-1,dp(30)));
        return makeButton(box);
    }
    private Button makeButton(View v){ Button b=new Button(this); b.setAllCaps(false); b.setBackgroundColor(Color.TRANSPARENT); b.setPadding(0,0,0,0); b.setText(""); b.setContentDescription("Teacher action"); b.setLayoutParams(new LinearLayout.LayoutParams(0,dp(94),1)); b.setOnClickListener(vv->v.performClick()); return b; }

    private void buildHome(){
        ScrollView scroll=new ScrollView(this); scroll.setBackgroundColor(Color.rgb(246,247,252));
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(16),dp(14),dp(16),dp(22)); scroll.addView(root);

        LinearLayout hero=new LinearLayout(this); hero.setOrientation(LinearLayout.VERTICAL); hero.setPadding(dp(20),dp(20),dp(20),dp(18)); hero.setBackground(gradient());
        LinearLayout top=new LinearLayout(this); top.setGravity(Gravity.CENTER_VERTICAL);
        TextView icon=text("◉",30,Color.WHITE,true); icon.setBackground(bg(Color.argb(45,255,255,255),18));
        top.addView(icon,new LinearLayout.LayoutParams(dp(54),dp(54)));
        LinearLayout titleBox=new LinearLayout(this); titleBox.setOrientation(LinearLayout.VERTICAL); titleBox.setPadding(dp(12),0,0,0);
        titleBox.addView(text("تطبيق المعلم",23,Color.WHITE,true)); titleBox.addView(text("إدارة الطلاب والتقييمات والحضور",12,Color.WHITE,false));
        top.addView(titleBox,new LinearLayout.LayoutParams(0,dp(62),1)); hero.addView(top);
        hero.addView(text("مرحبًا بك 👋",16,Color.WHITE,true));
        TextView owner=text("© Ahmed Mostafa Abonajy  •  إصدار 2026",11,Color.WHITE,false); hero.addView(owner);
        root.addView(hero);

        addGap(root,14);
        LinearLayout stats=new LinearLayout(this); stats.setWeightSum(3); stats.setGravity(Gravity.CENTER);
        stats.addView(stat("3","صفوف")); stats.addView(stat("9","فصول")); stats.addView(stat("50+","طالب/فصل")); root.addView(stats);

        addGap(root,16); root.addView(text("لوحة التحكم",18,Color.rgb(30,40,75),true));
        addGap(root,6);
        LinearLayout row1=new LinearLayout(this); row1.setWeightSum(2); row1.setGravity(Gravity.CENTER);
        row1.addView(action("👥 الصف الرابع","3 فصول",v->openMain())); row1.addView(action("👥 الصف الخامس","3 فصول",v->openMain())); root.addView(row1);
        addGap(root,10);
        LinearLayout row2=new LinearLayout(this); row2.setWeightSum(2); row2.setGravity(Gravity.CENTER);
        row2.addView(action("👥 الصف السادس","3 فصول",v->openMain())); row2.addView(action("📊 التقارير","التقييم الشهري والكلي",v->openMain())); root.addView(row2);

        addGap(root,16); root.addView(text("أدوات سريعة",18,Color.rgb(30,40,75),true)); addGap(root,6);
        root.addView(bigAction("🏫 بيانات المدرسة","اسم المدرسة • اسم المعلم • المادة",v->openMain()));
        addGap(root,8); root.addView(bigAction("📥 الأسماء من Excel","استيراد سريع لفصل كامل",v->openMain()));
        addGap(root,8); root.addView(bigAction("📤 تصدير Excel","حفظ ومشاركة النتائج",v->openMain()));
        addGap(root,8); root.addView(bigAction("🖨 الطباعة","طباعة تقارير الفصل",v->openMain()));

        addGap(root,18);
        TextView footer=text("تقييمات أسبوعية • حضور وغياب • امتحانا الشهر • حساب تلقائي",11,Color.rgb(110,120,145),false); root.addView(footer);
        setContentView(scroll);
    }

    private TextView stat(String number,String label){
        TextView t=text(number+"\n"+label,15,Color.rgb(45,55,90),true); t.setBackground(bg(Color.WHITE,16));
        t.setPadding(4,dp(12),4,dp(12)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(78),1); p.setMargins(dp(4),0,dp(4),0); t.setLayoutParams(p); return t;
    }
    private Button bigAction(String title,String sub,View.OnClickListener click){
        Button b=new Button(this); b.setAllCaps(false); b.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL); b.setText(title+"\n"+sub); b.setTextSize(15); b.setTextColor(Color.rgb(25,35,75)); b.setPadding(dp(16),0,dp(16),0); b.setBackground(bg(Color.WHITE,16)); b.setOnClickListener(click); b.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(68))); return b;
    }
    private void openMain(){ startActivity(new Intent(this,MainActivity.class)); }
}
