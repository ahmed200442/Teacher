package com.ahmed.teacher;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
 private int dp(float v){return(int)(v*getResources().getDisplayMetrics().density+.5f);} private LinearLayout root; private int grade=4,cls=1; private String week="الأسبوع الأول";
 private final String[] weeks={"الأسبوع الأول","الأسبوع الثاني","الأسبوع الثالث","الأسبوع الرابع","الأسبوع الخامس","الأسبوع السادس","الأسبوع السابع","الأسبوع الثامن","الأسبوع التاسع","الأسبوع العاشر"};
 private ArrayList<String> names=new ArrayList<>();
 @Override public void onCreate(Bundle b){super.onCreate(b);home();}
 private TextView tv(String s,float z){TextView t=new TextView(this);t.setText(s);t.setTextSize(z);t.setTextColor(Color.rgb(25,45,90));t.setGravity(Gravity.CENTER);t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);t.setPadding(dp(6),dp(8),dp(6),dp(8));return t;}
 private Button btn(String s,View.OnClickListener l){Button x=new Button(this);x.setText(s);x.setTextSize(16);x.setAllCaps(false);x.setOnClickListener(l);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(56));p.setMargins(dp(8),dp(4),dp(8),dp(4));x.setLayoutParams(p);return x;}
 private void base(String h){ScrollView sc=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(8),dp(10),dp(8),dp(10));root.setBackgroundColor(Color.rgb(248,249,252));sc.addView(root);setContentView(sc);root.addView(tv(h,23));}
 private void home(){base("📝 التقييمات الأسبوعية");root.addView(tv("تطبيق المعلم",18));root.addView(btn("الصف الرابع  •  3 فصول",v->classes(4)));root.addView(btn("الصف الخامس  •  3 فصول",v->classes(5)));root.addView(btn("الصف السادس  •  3 فصول",v->classes(6)));}
 private void classes(int g){grade=g;names.clear();load();base("الصف "+g);for(int i=1;i<=3;i++){final int c=i;root.addView(btn("الفصل "+c+"  •  "+(c==cls&&!names.isEmpty()?names.size():50)+" طالب",v->open(c)));}root.addView(btn("⬅ رجوع",v->home()));}
 private void load(){String s=getPreferences(0).getString("names_"+grade+"_"+cls,"");if(!s.isEmpty())names.addAll(Arrays.asList(s.split("\\n")));}
 private void saveNames(){StringBuilder s=new StringBuilder();for(String n:names){if(n!=null&&!n.trim().isEmpty()){if(s.length()>0)s.append('\n');s.append(n.trim());}}getPreferences(0).edit().putString("names_"+grade+"_"+cls,s.toString()).apply();}
 private void open(int c){cls=c;names.clear();load();base("الصف "+grade+" - الفصل "+c);root.addView(btn("📥 إضافة / استبدال الأسماء من Excel",v->pickExcel()));root.addView(tv("عدد الطلاب: "+(names.isEmpty()?"لم يتم الاستيراد بعد":names.size()),17));Spinner sp=new Spinner(this);sp.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,weeks));root.addView(sp);root.addView(btn("▶ بدء التقييم",v->{week=sp.getSelectedItem().toString();assessment();}));root.addView(btn("⬅ رجوع",v->classes(grade)));}
 private void pickExcel(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,101);}
 @Override protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(r==101&&c==RESULT_OK&&d!=null){try{getContentResolver().takePersistableUriPermission(d.getData(),Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception e){}Toast.makeText(this,"تم اختيار ملف Excel. قراءة عمود اسم الطالب ستتم في الخطوة التالية.",Toast.LENGTH_LONG).show();}}
 private void assessment(){base("الصف "+grade+" - الفصل "+cls);root.addView(tv(week,20));root.addView(tv("بنود التقييم • المجموع / 25 يحسب تلقائيًا",16));if(names.isEmpty())for(int i=1;i<=50;i++)names.add("طالب "+i);for(String n:names)addStudent(n);root.addView(btn("💾 حفظ التقييمات",v->Toast.makeText(this,"تم الحفظ",Toast.LENGTH_SHORT).show()));root.addView(btn("⬅ رجوع",v->open(cls)));}
 private void addStudent(String n){LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);TextView name=tv(n,16);name.setGravity(Gravity.RIGHT);card.addView(name);LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER);EditText hw=edit("واجب /5"),copy=edit("كراسة /5"),test=edit("تقييم /10"),beh=edit("سلوك /5");TextView total=tv("المجموع\n0 / 25",14);row.addView(hw);row.addView(copy);row.addView(test);row.addView(beh);row.addView(total);card.addView(row);TextWatcher w=new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int b,int c){}public void onTextChanged(CharSequence s,int a,int b,int c){int sum=num(hw,5)+num(copy,5)+num(test,10)+num(beh,5);total.setText("المجموع\n"+sum+" / 25");}public void afterTextChanged(Editable e){}};hw.addTextChangedListener(w);copy.addTextChangedListener(w);test.addTextChangedListener(w);beh.addTextChangedListener(w);root.addView(card);}
 private EditText edit(String h){EditText e=new EditText(this);e.setHint(h);e.setGravity(Gravity.CENTER);e.setInputType(2);e.setTextSize(11);e.setSingleLine(true);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(54),1);p.setMargins(2,2,2,2);e.setLayoutParams(p);return e;}
 private int num(EditText e,int max){try{return Math.min(max,Math.max(0,Integer.parseInt(e.getText().toString())));}catch(Exception x){return 0;}}
}
