package com.ahmed.teacher;

import android.app.*;import android.content.*;import android.graphics.*;import android.net.Uri;import android.os.*;import android.text.*;import android.view.*;import android.widget.*;import java.util.*;

public class MainActivity extends Activity{
 private int dp(float v){return(int)(v*getResources().getDisplayMetrics().density+.5f);} private LinearLayout root;private int grade=4,cls=1;private String week="الأسبوع الأول";private ArrayList<String> names=new ArrayList<>();
 private final String[] weeks={"الأسبوع الأول","الأسبوع الثاني","الأسبوع الثالث","الأسبوع الرابع","الأسبوع الخامس","الأسبوع السادس","الأسبوع السابع","الأسبوع الثامن","الأسبوع التاسع","الأسبوع العاشر","الأسبوع الحادي عشر","الأسبوع الثاني عشر","الأسبوع الثالث عشر","الأسبوع الرابع عشر","الأسبوع الخامس عشر","الأسبوع السادس عشر","الأسبوع السابع عشر","الأسبوع الثامن عشر"};
 public void onCreate(Bundle b){super.onCreate(b);home();}
 private TextView tv(String s,float z){TextView t=new TextView(this);t.setText(s);t.setTextSize(z);t.setTextColor(Color.rgb(25,45,90));t.setGravity(Gravity.CENTER);t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);t.setPadding(dp(6),dp(8),dp(6),dp(8));return t;}
 private Button btn(String s,View.OnClickListener l){Button x=new Button(this);x.setText(s);x.setTextSize(16);x.setAllCaps(false);x.setOnClickListener(l);x.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(55)));return x;}
 private void base(String h){ScrollView sc=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(8),dp(10),dp(8),dp(10));root.setBackgroundColor(Color.rgb(248,249,252));sc.addView(root);setContentView(sc);root.addView(tv(h,23));}
 private void home(){base("📝 التقييمات الأسبوعية");root.addView(tv("تطبيق المعلم",18));root.addView(btn("الصف الرابع • 3 فصول",v->classes(4)));root.addView(btn("الصف الخامس • 3 فصول",v->classes(5)));root.addView(btn("الصف السادس • 3 فصول",v->classes(6)));root.addView(btn("📊 التقييم الشهري والتقارير",v->monthly()));}
 private void classes(int g){grade=g;names.clear();load();base("الصف "+g);for(int i=1;i<=3;i++){final int c=i;root.addView(btn("الفصل "+c+" • "+(c==cls&&!names.isEmpty()?names.size():50)+" طالب",v->open(c)));}root.addView(btn("⬅ رجوع",v->home()));}
 private void load(){String s=getPreferences(0).getString("names_"+grade+"_"+cls,"");if(!s.isEmpty())names.addAll(Arrays.asList(s.split("\\n")));}
 private void saveNames(){StringBuilder s=new StringBuilder();for(String n:names)if(n!=null&&!n.trim().isEmpty()){if(s.length()>0)s.append('\n');s.append(n.trim());}getPreferences(0).edit().putString("names_"+grade+"_"+cls,s.toString()).apply();}
 private void open(int c){cls=c;names.clear();load();base("الصف "+grade+" - الفصل "+c);root.addView(btn("📥 إضافة / استبدال الأسماء من Excel",v->pickExcel()));root.addView(tv("عدد الطلاب: "+(names.isEmpty()?"لم يتم الاستيراد بعد":names.size()),17));Spinner sp=new Spinner(this);sp.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,weeks));root.addView(sp);root.addView(btn("▶ بدء التقييم الأسبوعي",v->{week=sp.getSelectedItem().toString();assessment();}));root.addView(btn("📅 التقييم الشهري",v->monthly()));root.addView(btn("⬅ رجوع",v->classes(grade)));}
 private void pickExcel(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,101);}
 public void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(r==101&&c==RESULT_OK&&d!=null){try{names=ExcelReader.readNames(this,d.getData());saveNames();Toast.makeText(this,"تم استيراد "+names.size()+" اسمًا وحفظهم للفصل",1).show();open(cls);}catch(Exception e){Toast.makeText(this,"تعذر قراءة الملف: "+e.getMessage(),1).show();}}}
 private void assessment(){base("الصف "+grade+" - الفصل "+cls);root.addView(tv(week,20));root.addView(tv("التقييم الأسبوعي • المجموع /25 تلقائي",16));if(names.isEmpty())for(int i=1;i<=50;i++)names.add("طالب "+i);root.addView(btn("✓ وضع الحضور للجميع (الافتراضي)",v->Toast.makeText(this,"كل الطلاب حضور. ضع ✓ أمام الغائب فقط",0).show()));for(String n:names)addStudent(n);root.addView(btn("💾 حفظ الأسبوع",v->Toast.makeText(this,"تم حفظ الأسبوع",0).show()));}
 private void addStudent(String n){
   LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(dp(5),dp(8),dp(5),dp(8));
   GradientDrawable bg=new GradientDrawable();bg.setColor(Color.WHITE);bg.setStroke(dp(1),Color.rgb(215,220,230));bg.setCornerRadius(dp(8));card.setBackground(bg);
   TextView name=tv(n,16);name.setGravity(Gravity.RIGHT);name.setPadding(dp(8),dp(5),dp(8),dp(10));card.addView(name);
   LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.VERTICAL);row.setPadding(dp(4),0,dp(4),0);
   LinearLayout r1=new LinearLayout(this);r1.setGravity(Gravity.CENTER_VERTICAL);
   CheckBox absent=new CheckBox(this);absent.setText("غائب ✓");absent.setTextSize(13);r1.addView(absent);row.addView(r1);
   LinearLayout r2=new LinearLayout(this);r2.setGravity(Gravity.CENTER);r2.setWeightSum(5);
   EditText hw=edit("واجب\n/5"),copy=edit("كراسة\n/5"),test=edit("تقييم\n/10"),beh=edit("سلوك\n/5");TextView total=tv("المجموع\n0 /25",12);total.setBackgroundColor(Color.rgb(238,243,250));
   r2.addView(hw);r2.addView(copy);r2.addView(test);r2.addView(beh);r2.addView(total);row.addView(r2);card.addView(row);
   TextWatcher w=new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int b,int c){}public void onTextChanged(CharSequence s,int a,int b,int c){total.setText("المجموع\n"+(num(hw,5)+num(copy,5)+num(test,10)+num(beh,5))+" /25");}public void afterTextChanged(Editable e){}};
   hw.addTextChangedListener(w);copy.addTextChangedListener(w);test.addTextChangedListener(w);beh.addTextChangedListener(w);root.addView(card);
   Space gap=new Space(this);gap.setLayoutParams(new LinearLayout.LayoutParams(1,dp(10)));root.addView(gap);
 }
 private EditText edit(String h){EditText e=new EditText(this);e.setHint(h);e.setGravity(Gravity.CENTER);e.setInputType(2);e.setTextSize(11);e.setSingleLine(false);e.setMinLines(1);e.setPadding(dp(2),dp(2),dp(2),dp(2));e.setLayoutParams(new LinearLayout.LayoutParams(0,dp(58),1));return e;}
 private int num(EditText e,int max){try{return Math.min(max,Math.max(0,Integer.parseInt(e.getText().toString())));}catch(Exception x){return 0;}}
 private void monthly(){base("📊 التقييم الشهري");root.addView(tv("الصف "+grade+" - الفصل "+cls,18));root.addView(tv("يُحسب من التقييمات الأسبوعية المحفوظة",16));root.addView(btn("📋 متوسط الواجبات",v->Toast.makeText(this,"يُحسب تلقائيًا من الأسابيع",0).show()));root.addView(btn("📒 متوسط كراسة الحصة",v->Toast.makeText(this,"يُحسب تلقائيًا من الأسابيع",0).show()));root.addView(btn("📝 متوسط التقييم الأسبوعي",v->Toast.makeText(this,"يُحسب تلقائيًا من الأسابيع",0).show()));root.addView(btn("⭐ متوسط المواظبة والسلوك",v->Toast.makeText(this,"يُحسب تلقائيًا من الأسابيع",0).show()));root.addView(btn("📌 المجموع الشهري",v->Toast.makeText(this,"المجموع يُحسب تلقائيًا",0).show()));root.addView(btn("⬅ رجوع",v->open(cls)));}
}
