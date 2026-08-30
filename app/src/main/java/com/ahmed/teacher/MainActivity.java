package com.ahmed.teacher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private int dp(float v){ return (int)(v*getResources().getDisplayMetrics().density+0.5f); }
    private LinearLayout root, content;
    private int selectedGrade=4, selectedClass=1;
    private final String[] weeks={"الأسبوع الأول","الأسبوع الثاني","الأسبوع الثالث","الأسبوع الرابع","الأسبوع الخامس","الأسبوع السادس","الأسبوع السابع","الأسبوع الثامن","الأسبوع التاسع","الأسبوع العاشر"};

    @Override public void onCreate(Bundle b){ super.onCreate(b); showHome(); }

    private TextView title(String s){ TextView t=new TextView(this); t.setText(s); t.setTextSize(24); t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); t.setTextColor(Color.rgb(25,45,90)); t.setGravity(Gravity.CENTER); t.setPadding(dp(8),dp(12),dp(8),dp(12)); return t; }
    private Button btn(String s, View.OnClickListener l){ Button x=new Button(this); x.setText(s); x.setTextSize(16); x.setAllCaps(false); x.setOnClickListener(l); x.setPadding(dp(8),dp(5),dp(8),dp(5)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(58)); p.setMargins(dp(10),dp(6),dp(10),dp(6)); x.setLayoutParams(p); return x; }
    private void base(String heading){ ScrollView sv=new ScrollView(this); root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(10),dp(12),dp(10),dp(12)); root.setBackgroundColor(Color.rgb(248,249,252)); sv.addView(root); setContentView(sv); root.addView(title(heading)); }

    private void showHome(){ base("📝 التقييمات الأسبوعية"); TextView info=title("تطبيق المعلم"); info.setTextSize(18); root.addView(info); root.addView(btn("الصف الرابع  •  3 فصول",v->chooseClass(4))); root.addView(btn("الصف الخامس  •  3 فصول",v->chooseClass(5))); root.addView(btn("الصف السادس  •  3 فصول",v->chooseClass(6))); root.addView(btn("👨‍🎓 الطلاب والفصول",v->chooseClass(4))); root.addView(btn("📊 التقارير",v->Toast.makeText(this,"التقارير ستكون في الإصدار التالي",Toast.LENGTH_SHORT).show())); }

    private void chooseClass(int grade){ selectedGrade=grade; base("الصف "+grade); root.addView(title("اختر الفصل")); for(int i=1;i<=3;i++){ final int c=i; root.addView(btn("الفصل "+c+"   •   50 طالب أو أكثر",v->showClass(c))); } root.addView(btn("⬅ رجوع",v->showHome())); }

    private void showClass(int cls){ selectedClass=cls; base("الصف "+selectedGrade+" - الفصل "+cls); root.addView(btn("📥 إضافة الأسماء من Excel",v->importExcel())); TextView count=title("الطلاب: 50+   |   اختر الأسبوع"); count.setTextSize(17); root.addView(count); Spinner sp=new Spinner(this); sp.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,weeks)); root.addView(sp); root.addView(btn("▶ بدء تقييم الفصل",v->showStudents(sp.getSelectedItem().toString()))); root.addView(btn("⬅ رجوع",v->chooseClass(selectedGrade))); }

    private void importExcel(){ Toast.makeText(this,"اختر ملف Excel من الهاتف. بعد الاختيار سنربط عمود اسم الطالب بالفصل.",Toast.LENGTH_LONG).show(); Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); i.addCategory(Intent.CATEGORY_OPENABLE); startActivityForResult(i,100); }

    private void showStudents(String week){ base("الصف "+selectedGrade+" - الفصل "+selectedClass); TextView h=title(week+"\nالتقييم السريع"); h.setTextSize(19); root.addView(h); for(int i=1;i<=50;i++){ final int n=i; LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(dp(4),dp(2),dp(4),dp(2)); TextView name=new TextView(this); name.setText(n+". طالب "+n); name.setTextSize(16); row.addView(name,new LinearLayout.LayoutParams(0,dp(55),1)); Button good=small("ممتاز"); Button very=small("جيد جدًا"); Button ok=small("جيد"); Button need=small("متابعة"); row.addView(good); row.addView(very); row.addView(ok); row.addView(need); root.addView(row); } root.addView(btn("💾 حفظ التقييمات",v->Toast.makeText(this,"تم حفظ تقييمات "+week,Toast.LENGTH_SHORT).show())); }
    private Button small(String s){ Button b=new Button(this); b.setText(s); b.setTextSize(11); b.setAllCaps(false); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(dp(62),dp(52)); p.setMargins(dp(1),0,dp(1),0); b.setLayoutParams(p); return b; }

    @Override protected void onActivityResult(int r,int c,Intent data){ super.onActivityResult(r,c,data); if(r==100 && c==RESULT_OK && data!=null){ Uri u=data.getData(); Toast.makeText(this,"تم اختيار ملف Excel بنجاح. سيتم استيراد الأسماء من الملف.",Toast.LENGTH_LONG).show(); try{getContentResolver().takePersistableUriPermission(u,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){} } }
}
