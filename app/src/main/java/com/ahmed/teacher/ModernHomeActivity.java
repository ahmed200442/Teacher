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
    private int navy=Color.rgb(25,35,75), muted=Color.rgb(105,115,140);
    @Override public void onCreate(Bundle b){ super.onCreate(b); buildHome(); }
    private TextView text(String s,float size,int color,boolean bold){
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); t.setGravity(Gravity.CENTER); t.setTypeface(Typeface.DEFAULT,bold?Typeface.BOLD:Typeface.NORMAL); t.setPadding(dp(8),dp(6),dp(8),dp(6)); return t;
    }
    private GradientDrawable bg(int color,float radius){ GradientDrawable g=new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp(radius)); return g; }
    private GradientDrawable gradient(){ GradientDrawable g=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(77,61,235),Color.rgb(122,74,242)}); g.setCornerRadius(dp(24)); return g; }
    private void gap(LinearLayout r,int h){ Space s=new Space(this); s.setLayoutParams(new LinearLayout.LayoutParams(1,dp(h))); r.addView(s); }
    private Button big(String title,String sub,View.OnClickListener click){
        Button b=new Button(this); b.setAllCaps(false); b.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL); b.setText(title+"\n"+sub); b.setTextSize(15); b.setTextColor(navy); b.setPadding(dp(16),0,dp(16),0); b.setBackground(bg(Color.WHITE,16)); b.setOnClickListener(click); b.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(70))); return b;
    }
    private TextView stat(String number,String label){
        TextView t=text(number+"\n"+label,15,Color.rgb(45,55,90),true); t.setBackground(bg(Color.WHITE,16)); t.setPadding(4,dp(12),4,dp(12)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(78),1); p.setMargins(dp(4),0,dp(4),0); t.setLayoutParams(p); return t;
    }
    private LinearLayout tile(String title,String sub,View.OnClickListener click){
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setGravity(Gravity.CENTER); box.setPadding(dp(6),dp(8),dp(6),dp(8)); box.setBackground(bg(Color.WHITE,16)); box.setOnClickListener(click);
        box.addView(text(title,15,navy,true)); box.addView(text(sub,10,muted,false)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(88),1); p.setMargins(dp(4),0,dp(4),0); box.setLayoutParams(p); return box;
    }
    private void buildHome(){
        ScrollView scroll=new ScrollView(this); scroll.setBackgroundColor(Color.rgb(246,247,252));
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(16),dp(14),dp(16),dp(22)); scroll.addView(root);
        LinearLayout hero=new LinearLayout(this); hero.setOrientation(LinearLayout.VERTICAL); hero.setPadding(dp(20),dp(20),dp(20),dp(18)); hero.setBackground(gradient());
        TextView title=text("تطبيق المعلم",25,Color.WHITE,true); hero.addView(title); hero.addView(text("إدارة التقييمات والحضور والطلاب بسهولة",13,Color.WHITE,false)); hero.addView(text("مرحبًا بك 👋",17,Color.WHITE,true)); hero.addView(text("© Ahmed Mostafa Abonajy  •  إصدار 2026",11,Color.WHITE,false)); root.addView(hero);
        gap(root,14);
        LinearLayout stats=new LinearLayout(this); stats.setWeightSum(3); stats.addView(stat("3","صفوف")); stats.addView(stat("9","فصول")); stats.addView(stat("50+","طالب/فصل")); root.addView(stats);
        gap(root,18); root.addView(text("لوحة التحكم",19,navy,true)); gap(root,7);
        LinearLayout r1=new LinearLayout(this); r1.setWeightSum(2); r1.addView(tile("👥 الصف الرابع","3 فصول",v->openMain())); r1.addView(tile("👥 الصف الخامس","3 فصول",v->openMain())); root.addView(r1); gap(root,9);
        LinearLayout r2=new LinearLayout(this); r2.setWeightSum(2); r2.addView(tile("👥 الصف السادس","3 فصول",v->openMain())); r2.addView(tile("📊 التقارير","الشهرية والكلية",v->openMain())); root.addView(r2);
        gap(root,18); root.addView(text("⚡ أدوات توفير الوقت في الحصة",19,navy,true)); gap(root,7);
        LinearLayout r3=new LinearLayout(this); r3.setWeightSum(2); r3.addView(tile("📝 إدخال سريع","الانتقال بين الدرجات",v->openMain())); r3.addView(tile("✅ حضور سريع","تسجيل الغياب",v->openMain())); root.addView(r3); gap(root,9);
        LinearLayout r4=new LinearLayout(this); r4.setWeightSum(2); r4.addView(tile("🔒 قفل الأسبوع","حماية الدرجات",v->openMain())); r4.addView(tile("👥 إدارة الطلاب","إضافة وحذف",v->openMain())); root.addView(r4);
        gap(root,18); root.addView(text("أدوات سريعة",19,navy,true)); gap(root,7);
        root.addView(big("🏫 بيانات المدرسة","اسم المدرسة • اسم المعلم • المادة",v->openMain())); gap(root,8);
        root.addView(big("📥 الأسماء من Excel","استيراد سريع لفصل كامل",v->openMain())); gap(root,8);
        root.addView(big("📤 تصدير Excel","حفظ ومشاركة النتائج",v->openMain())); gap(root,8);
        root.addView(big("🖨 الطباعة","طباعة تقارير الفصل",v->openMain()));
        gap(root,18); root.addView(text("تقييمات أسبوعية • حفظ تلقائي • حساب المجموع • حضور وغياب",11,muted,false));
        setContentView(scroll);
    }
    private void openMain(){ startActivity(new Intent(this,MainActivity.class)); }
}
