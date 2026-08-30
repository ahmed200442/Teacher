package com.ahmed.teacher;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.print.*;
import android.view.*;
import android.widget.*;
import android.text.*;
import java.util.*;

public class MainActivity extends Activity {
    private LinearLayout root;
    private int grade=4, cls=1;
    private String week="الأسبوع الأول";
    private ArrayList<String> names=new ArrayList<>();
    private final String[] weeks={"الأسبوع الأول","الأسبوع الثاني","الأسبوع الثالث","الأسبوع الرابع","الأسبوع الخامس","الأسبوع السادس","الأسبوع السابع","الأسبوع الثامن","الأسبوع التاسع","الأسبوع العاشر","الأسبوع الحادي عشر","الأسبوع الثاني عشر","الأسبوع الثالث عشر","الأسبوع الرابع عشر","الأسبوع الخامس عشر","الأسبوع السادس عشر","الأسبوع السابع عشر","الأسبوع الثامن عشر"};
    // WEEK_DATES_2026: official school-year start 12/09/2026, one week apart
    private final String[] weekDates={"12/09/2026","19/09/2026","26/09/2026","03/10/2026","10/10/2026","17/10/2026","24/10/2026","31/10/2026","07/11/2026","14/11/2026","21/11/2026","28/11/2026","05/12/2026","12/12/2026","19/12/2026","26/12/2026","02/01/2027","09/01/2027"};


    private int dp(float v){return(int)(v*getResources().getDisplayMetrics().density+.5f);}
    private TextView tv(String s,float z){TextView t=new TextView(this);t.setText(s);t.setTextSize(z);t.setTextColor(Color.rgb(25,45,90));t.setGravity(Gravity.CENTER);t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);t.setPadding(dp(8),dp(8),dp(8),dp(8));return t;}
    private Button btn(String s,View.OnClickListener l){Button x=new Button(this);x.setText(s);x.setTextSize(15);x.setAllCaps(false);x.setOnClickListener(l);x.setTextColor(Color.rgb(25,45,90));x.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(52)));return x;}
    private void base(String h){ScrollView sc=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(12),dp(12),dp(12),dp(16));root.setBackgroundColor(Color.rgb(246,248,252));sc.addView(root);setContentView(sc);root.addView(tv(h,24));}
    private void section(String s){TextView t=tv(s,15);t.setGravity(Gravity.RIGHT);t.setTextColor(Color.rgb(70,90,125));t.setPadding(dp(8),dp(14),dp(8),dp(5));root.addView(t);}
    private SharedPreferences prefs(){return getPreferences(0);}

    @Override public void onCreate(Bundle b){super.onCreate(b);home();}

    private void home(){
        base("📚 سجل المعلم");
        root.addView(tv("التقييمات الأسبوعية",19));
        TextView owner=tv("© Ahmed Mostafa Abonajy",12);owner.setTextColor(Color.GRAY);root.addView(owner);
        section("إعدادات المدرسة");root.addView(btn("🏫 بيانات المدرسة / اسم المعلم / المادة",v->schoolData()));
        section("الفصول الدراسية");
        root.addView(btn("الصف الرابع  •  3 فصول",v->classes(4)));
        root.addView(btn("الصف الخامس  •  3 فصول",v->classes(5)));
        root.addView(btn("الصف السادس  •  3 فصول",v->classes(6)));
        section("التقارير والملفات");
        root.addView(btn("📊 التقييم الشهري والاختبارات",v->monthly()));
        root.addView(btn("📤 تصدير إلى Excel",v->exportExcel()));
        root.addView(btn("🖨 طباعة",v->printReport()));
    }

    private void schoolData(){
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);
        EditText school=field("اسم المدرسة",prefs().getString("school",""));
        EditText teacher=field("اسم المعلم",prefs().getString("teacher","Ahmed Mostafa Abonajy"));
        EditText subject=field("المادة",prefs().getString("subject",""));
        box.addView(school);box.addView(teacher);box.addView(subject);
        new AlertDialog.Builder(this).setTitle("🏫 بيانات المدرسة").setView(box)
            .setPositiveButton("حفظ",(d,w)->{prefs().edit().putString("school",school.getText().toString()).putString("teacher",teacher.getText().toString()).putString("subject",subject.getText().toString()).apply();Toast.makeText(this,"تم حفظ بيانات المدرسة",Toast.LENGTH_SHORT).show();})
            .setNegativeButton("إلغاء",null).show();
    }
    private EditText field(String hint,String value){EditText e=new EditText(this);e.setHint(hint);e.setText(value);e.setTextSize(16);e.setSingleLine(true);e.setPadding(dp(10),dp(10),dp(10),dp(10));return e;}

    private void classes(int g){
        grade=g;names.clear();load();base("الصف "+g);section("اختر الفصل");
        for(int i=1;i<=3;i++){final int c=i;root.addView(btn("الفصل "+c+"  •  "+studentCount(c)+" طالب",v->open(c)));}
        root.addView(btn("⬅ رجوع",v->home()));
    }
    private int studentCount(int c){String s=prefs().getString("names_"+grade+"_"+c,"");return s.isEmpty()?0:s.split("\\n").length;}
    private void load(){String s=prefs().getString("names_"+grade+"_"+cls,"");if(!s.isEmpty())names.addAll(Arrays.asList(s.split("\\n")));}
    private void saveNames(){StringBuilder s=new StringBuilder();for(String n:names)if(n!=null&&!n.trim().isEmpty()){if(s.length()>0)s.append('\n');s.append(n.trim());}prefs().edit().putString("names_"+grade+"_"+cls,s.toString()).apply();}

    private void open(int c){
        cls=c;names.clear();load();base("الصف "+grade+" - الفصل "+c);
        root.addView(btn("📥 إضافة / استبدال الأسماء من Excel",v->pickExcel()));
        root.addView(tv("عدد الطلاب: "+names.size(),17));
        root.addView(btn("➕ إضافة طالب جديد",v->addStudentDialog()));
        root.addView(btn("🗑 حذف طالب",v->deleteStudentDialog()));
        root.addView(btn("🗑 حذف جميع الطلاب",v->deleteAllStudentsDialog()));
        Spinner sp=new Spinner(this);sp.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,weeks));root.addView(sp);
        root.addView(btn("▶ بدء التقييم الأسبوعي",v->{week=sp.getSelectedItem().toString();assessment();}));
        root.addView(btn("📅 التقييم الشهري والاختبارات",v->monthly()));
        root.addView(btn("📤 تصدير الفصل إلى Excel",v->exportExcel()));
        root.addView(btn("🖨 طباعة الفصل",v->printReport()));
        root.addView(btn("⬅ رجوع",v->classes(grade)));
    }

    private void addStudentDialog(){
        final EditText input=field("اسم الطالب","");
        new AlertDialog.Builder(this).setTitle("➕ إضافة طالب جديد").setMessage("اكتب اسم الطالب بالكامل").setView(input)
            .setNegativeButton("إلغاء",null).setPositiveButton("إضافة",(d,w)->{
                String n=input.getText().toString().trim();
                if(n.isEmpty()){Toast.makeText(this,"اكتب اسم الطالب أولًا",Toast.LENGTH_SHORT).show();return;}
                for(String old:names)if(old.equalsIgnoreCase(n)){Toast.makeText(this,"الطالب موجود بالفعل",Toast.LENGTH_SHORT).show();return;}
                names.add(n);saveNames();Toast.makeText(this,"تمت إضافة الطالب: "+n,Toast.LENGTH_SHORT).show();open(cls);
            }).show();
    }
    private void deleteStudentDialog(){
        if(names.isEmpty()){Toast.makeText(this,"لا يوجد طلاب للحذف",Toast.LENGTH_SHORT).show();return;}
        final String[] arr=names.toArray(new String[0]);
        new AlertDialog.Builder(this).setTitle("🗑 اختر الطالب المراد حذفه").setItems(arr,(d,which)->confirmDeleteStudent(arr[which])).setNegativeButton("إلغاء",null).show();
    }
    private void confirmDeleteStudent(String n){
        new AlertDialog.Builder(this).setTitle("تأكيد حذف الطالب").setMessage("سيتم حذف الطالب ودرجاته وحضوره من هذا الفصل:\n\n"+n)
            .setNegativeButton("إلغاء",null).setPositiveButton("حذف",(d,w)->{deleteStudentData(n);names.remove(n);saveNames();Toast.makeText(this,"تم حذف "+n,Toast.LENGTH_SHORT).show();open(cls);}).show();
    }
    private void deleteStudentData(String n){
        String marker=grade+"_"+cls+"_"+n.hashCode()+"_";
        SharedPreferences.Editor e=prefs().edit();
        for(String k:prefs().getAll().keySet())if(k.contains(marker)||k.equals("exam1_"+grade+"_"+cls+"_"+n.hashCode())||k.equals("exam2_"+grade+"_"+cls+"_"+n.hashCode()))e.remove(k);
        e.apply();
    }
    private void deleteAllStudentsDialog(){
        if(names.isEmpty()){Toast.makeText(this,"لا يوجد طلاب للحذف",Toast.LENGTH_SHORT).show();return;}
        new AlertDialog.Builder(this).setTitle("⚠️ حذف جميع الطلاب").setMessage("سيتم حذف جميع طلاب هذا الفصل مع درجاتهم وحضورهم وبيانات التقييمات. لا يمكن التراجع عن العملية.")
            .setNegativeButton("إلغاء",null).setPositiveButton("حذف الكل",(d,w)->{clearClassData();names.clear();saveNames();Toast.makeText(this,"تم حذف جميع طلاب الفصل",Toast.LENGTH_SHORT).show();open(cls);}).show();
    }
    private void clearClassData(){
        String marker=grade+"_"+cls+"_";SharedPreferences.Editor e=prefs().edit();
        for(String k:prefs().getAll().keySet())if(k.equals("names_"+grade+"_"+cls)||k.contains(marker))e.remove(k);e.apply();
    }

    private void pickExcel(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,101);}
    @Override public void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(r==101&&c==RESULT_OK&&d!=null){try{names=ExcelReader.readNames(this,d.getData());saveNames();Toast.makeText(this,"تم استيراد "+names.size()+" اسمًا وحفظهم للفصل",Toast.LENGTH_SHORT).show();open(cls);}catch(Exception e){Toast.makeText(this,"تعذر قراءة الملف: "+e.getMessage(),Toast.LENGTH_LONG).show();}}}

    private String key(String student,String w,String part){return "score_"+grade+"_"+cls+"_"+student.hashCode()+"_"+w+"_"+part;}
    private int savedScore(String student,String w,String part,int max){return prefs().getInt(key(student,w,part),0);}
    private void saveScore(String student,String w,String part,int value){prefs().edit().putInt(key(student,w,part),Math.min(maxFor(part),Math.max(0,value))).apply();}
    private int maxFor(String p){if("hw".equals(p)||"copy".equals(p)||"beh".equals(p))return 5;return 10;}
    private String absentKey(String student,String w){return "absent_"+grade+"_"+cls+"_"+student.hashCode()+"_"+w;}
    private boolean isAbsent(String student,String w){return prefs().getBoolean(absentKey(student,w),false);}
    private void saveAbsent(String student,String w,boolean absent){prefs().edit().putBoolean(absentKey(student,w),absent).apply();}
    private String lockKey(){return "locked_"+grade+"_"+cls+"_"+week;}
    private boolean isWeekLocked(){return prefs().getBoolean(lockKey(),false);}
    private void setWeekLocked(boolean locked){prefs().edit().putBoolean(lockKey(),locked).apply();}

    
    private int weekIndex(){for(int i=0;i<weeks.length;i++)if(weeks[i].equals(week))return i;return 0;}
    private String weekDate(){int i=weekIndex();return i<weekDates.length?weekDates[i]:"";}
    private String weekTitle(){return week+" • "+weekDate();}
    private void goPreviousWeek(){int i=weekIndex();if(i>0){week=weeks[i-1];assessment();}else Toast.makeText(this,"أنت بالفعل في الأسبوع الأول",Toast.LENGTH_SHORT).show();}
    private void goNextWeek(){int i=weekIndex();if(i<weeks.length-1){week=weeks[i+1];assessment();}else Toast.makeText(this,"هذا آخر أسبوع في القائمة",Toast.LENGTH_SHORT).show();}
private void assessment(){
        base("الصف "+grade+" - الفصل "+cls);root.addView(tv("التقييم الأسبوعي",20));
        root.addView(tv("📅 "+weekTitle(),17));
        root.addView(tv("المجموع /25 تلقائي",15));
        Spinner weekSpinner=new Spinner(this);
        weekSpinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,weeks));
        weekSpinner.setSelection(weekIndex());
        weekSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){
            public void onNothingSelected(android.widget.AdapterView<?> p){}
            public void onItemSelected(android.widget.AdapterView<?> p,View v,int pos,long id){if(!weeks[pos].equals(week)){week=weeks[pos];assessment();}}
        });
        root.addView(weekSpinner);
        LinearLayout weekNav=new LinearLayout(this);
        weekNav.setOrientation(LinearLayout.HORIZONTAL);
        weekNav.setGravity(Gravity.CENTER);
        Button prev=btn("⬅ الأسبوع السابق",v->goPreviousWeek());
        Button next=btn("الأسبوع التالي ➡",v->goNextWeek());
        prev.setLayoutParams(new LinearLayout.LayoutParams(0,dp(52),1));
        next.setLayoutParams(new LinearLayout.LayoutParams(0,dp(52),1));
        weekNav.addView(prev);weekNav.addView(next);root.addView(weekNav);root.addView(tv("التقييم الأسبوعي • المجموع /25 تلقائي",16));
        TextView auto=tv("💾 الحفظ التلقائي يعمل مع كل درجة",13);auto.setTextColor(Color.rgb(35,135,80));root.addView(auto);
        final boolean locked=isWeekLocked();
        TextView state=tv(locked?"🔒 التقييم مقفول ولا يمكن تعديل الدرجات":"✏️ التقييم مفتوح ويمكن إدخال الدرجات",13);state.setTextColor(locked?Color.rgb(190,70,70):Color.rgb(55,105,155));root.addView(state);
        if(locked)root.addView(btn("🔓 فتح التقييم للتعديل",v->confirmUnlock()));else root.addView(btn("🔒 قفل التقييم الأسبوعي",v->confirmLock()));
        root.addView(btn("➕ إضافة طالب",v->addStudentDialog()));
        root.addView(btn("🗑 إدارة وحذف طالب",v->deleteStudentDialog()));
        if(names.isEmpty())root.addView(tv("لا يوجد طلاب. استخدم «إضافة طالب» أو استيراد Excel.",15));
        for(String n:new ArrayList<>(names))addStudent(n,locked);
        root.addView(btn("💾 حفظ الأسبوع",v->Toast.makeText(this,"الدرجات محفوظة تلقائيًا بالفعل",Toast.LENGTH_SHORT).show()));
        root.addView(btn("📤 تصدير Excel",v->exportExcel()));root.addView(btn("🖨 طباعة",v->printReport()));
    }
    private void confirmLock(){new AlertDialog.Builder(this).setTitle("🔒 قفل التقييم الأسبوعي").setMessage("بعد القفل لن تستطيع تعديل درجات هذا الأسبوع حتى تختار فتح التقييم مرة أخرى. هل تريد القفل؟").setNegativeButton("إلغاء",null).setPositiveButton("قفل",(d,w)->{setWeekLocked(true);Toast.makeText(this,"تم قفل "+week+" بتاريخ "+weekDate(),Toast.LENGTH_SHORT).show();goNextWeek();}).show();}
    private void confirmUnlock(){new AlertDialog.Builder(this).setTitle("🔓 فتح التقييم").setMessage("سيتم السماح بتعديل درجات هذا الأسبوع مرة أخرى.").setNegativeButton("إلغاء",null).setPositiveButton("فتح",(d,w)->{setWeekLocked(false);assessment();Toast.makeText(this,"تم فتح "+week+" للتعديل",Toast.LENGTH_SHORT).show();}).show();}

    private void addStudent(String n,boolean locked){
        LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(dp(5),dp(8),dp(5),dp(8));
        GradientDrawable bg=new GradientDrawable();bg.setColor(Color.WHITE);bg.setStroke(dp(1),Color.rgb(215,220,230));bg.setCornerRadius(dp(8));card.setBackground(bg);
        TextView name=tv(n,16);name.setGravity(Gravity.RIGHT);name.setPadding(dp(8),dp(5),dp(8),dp(10));card.addView(name);
        LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.VERTICAL);row.setPadding(dp(4),0,dp(4),0);
        CheckBox absent=new CheckBox(this);absent.setText("غائب ✓");absent.setTextSize(13);absent.setChecked(isAbsent(n,week));absent.setEnabled(!locked);row.addView(absent);
        LinearLayout r2=new LinearLayout(this);r2.setGravity(Gravity.CENTER);r2.setWeightSum(5);
        EditText hw=edit("واجب\n/5"),copy=edit("كراسة\n/5"),test=edit("تقييم\n/10"),beh=edit("سلوك\n/5");
        loadEdit(hw,savedScore(n,week,"hw",5));loadEdit(copy,savedScore(n,week,"copy",5));loadEdit(test,savedScore(n,week,"test",10));loadEdit(beh,savedScore(n,week,"beh",5));
        hw.setEnabled(!locked);copy.setEnabled(!locked);test.setEnabled(!locked);beh.setEnabled(!locked);
        int current=savedScore(n,week,"hw",5)+savedScore(n,week,"copy",5)+savedScore(n,week,"test",10)+savedScore(n,week,"beh",5);
        TextView total=tv("المجموع\n"+current+" /25",12);total.setBackgroundColor(Color.rgb(238,243,250));
        r2.addView(hw);r2.addView(copy);r2.addView(test);r2.addView(beh);r2.addView(total);row.addView(r2);card.addView(row);
        if(!locked){
            TextWatcher w=new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int b,int c){}public void onTextChanged(CharSequence s,int a,int b,int c){int h=num(hw,5),co=num(copy,5),te=num(test,10),be=num(beh,5);saveScore(n,week,"hw",h);saveScore(n,week,"copy",co);saveScore(n,week,"test",te);saveScore(n,week,"beh",be);total.setText("المجموع\n"+(h+co+te+be)+" /25");}public void afterTextChanged(Editable e){}};
            hw.addTextChangedListener(w);copy.addTextChangedListener(w);test.addTextChangedListener(w);beh.addTextChangedListener(w);absent.setOnCheckedChangeListener((button,checked)->saveAbsent(n,week,checked));
        }
        root.addView(card);Space gap=new Space(this);gap.setLayoutParams(new LinearLayout.LayoutParams(1,dp(10)));root.addView(gap);
    }
    private void loadEdit(EditText e,int v){if(v>0)e.setText(String.valueOf(v));}
    private EditText edit(String h){EditText e=new EditText(this);e.setHint(h);e.setGravity(Gravity.CENTER);e.setInputType(2);e.setTextSize(11);e.setSingleLine(false);e.setMinLines(1);e.setPadding(dp(2),dp(2),dp(2),dp(2));e.setLayoutParams(new LinearLayout.LayoutParams(0,dp(58),1));return e;}
    private int num(EditText e,int max){try{return Math.min(max,Math.max(0,Integer.parseInt(e.getText().toString())));}catch(Exception x){return 0;}}
    private double weeklyAverage(String n){int count=0;double sum=0;for(String w:weeks){int s=savedScore(n,w,"hw",5)+savedScore(n,w,"copy",5)+savedScore(n,w,"test",10)+savedScore(n,w,"beh",5);if(s>0||hasWeekData(n,w)){sum+=s;count++;}}return count==0?0:sum/count;}
    private boolean hasWeekData(String n,String w){return savedScore(n,w,"hw",5)>0||savedScore(n,w,"copy",5)>0||savedScore(n,w,"test",10)>0||savedScore(n,w,"beh",5)>0||isAbsent(n,w);}
    private String fmt(double x){if(x==Math.rint(x))return String.valueOf((int)x);return String.format(Locale.US,"%.1f",x);}

    private void monthly(){
        base("📊 التقييم الشهري والاختبارات");root.addView(tv("الصف "+grade+" - الفصل "+cls,18));root.addView(tv("كل طالب له درجات الشهر الأول والثاني ومتوسط تلقائي",15));
        if(names.isEmpty())load();for(String n:names)addMonthlyStudent(n);
        root.addView(btn("📤 تصدير Excel",v->exportExcel()));root.addView(btn("🖨 طباعة",v->printReport()));root.addView(btn("⬅ رجوع",v->open(cls)));
    }
    private void addMonthlyStudent(String n){
        LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(dp(8),dp(8),dp(8),dp(8));GradientDrawable bg=new GradientDrawable();bg.setColor(Color.WHITE);bg.setStroke(dp(1),Color.rgb(215,220,230));bg.setCornerRadius(dp(9));card.setBackground(bg);card.addView(tv(n,16));
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER);
        EditText first=edit("الشهر الأول\n/15"),second=edit("الشهر الثاني\n/15");loadEdit(first,prefs().getInt("exam1_"+grade+"_"+cls+"_"+n.hashCode(),0));loadEdit(second,prefs().getInt("exam2_"+grade+"_"+cls+"_"+n.hashCode(),0));
        TextView avg=tv("متوسط الشهرين\n0 /15",12),weekly=tv("متوسط التقييمات\n0 /25",12),total=tv("المجموع الكلي\n0 /40",13);row.addView(first);row.addView(second);row.addView(avg);row.addView(weekly);row.addView(total);card.addView(row);
        TextWatcher tw=new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int b,int c){}public void onTextChanged(CharSequence s,int a,int b,int c){int a1=num(first,15),a2=num(second,15);prefs().edit().putInt("exam1_"+grade+"_"+cls+"_"+n.hashCode(),a1).putInt("exam2_"+grade+"_"+cls+"_"+n.hashCode(),a2).apply();double av=(a1+a2)/2.0,wk=weeklyAverage(n);avg.setText("متوسط الشهرين\n"+fmt(av)+" /15");weekly.setText("متوسط التقييمات\n"+fmt(wk)+" /25");total.setText("المجموع الكلي\n"+fmt(av+wk)+" /40");}public void afterTextChanged(Editable e){}};
        first.addTextChangedListener(tw);second.addTextChangedListener(tw);
        double av=(prefs().getInt("exam1_"+grade+"_"+cls+"_"+n.hashCode(),0)+prefs().getInt("exam2_"+grade+"_"+cls+"_"+n.hashCode(),0))/2.0;double wk=weeklyAverage(n);avg.setText("متوسط الشهرين\n"+fmt(av)+" /15");weekly.setText("متوسط التقييمات\n"+fmt(wk)+" /25");total.setText("المجموع الكلي\n"+fmt(av+wk)+" /40");
        root.addView(card);Space gap=new Space(this);gap.setLayoutParams(new LinearLayout.LayoutParams(1,dp(10)));root.addView(gap);
    }

    private void exportExcel(){Toast.makeText(this,"سيتم تجهيز ملف Excel بالدرجات المحفوظة",Toast.LENGTH_SHORT).show();}
    private void printReport(){if(Build.VERSION.SDK_INT>=19){PrintManager pm=(PrintManager)getSystemService(PRINT_SERVICE);pm.print("Teacher - الصف "+grade+" الفصل "+cls,new ReportPrintAdapter(this),null);}else Toast.makeText(this,"الطباعة غير مدعومة",Toast.LENGTH_SHORT).show();}
    private static class ReportPrintAdapter extends PrintDocumentAdapter{
        private final Context c;ReportPrintAdapter(Context x){c=x;}
        public void onLayout(PrintAttributes a,PrintAttributes b,CancellationSignal cs,LayoutResultCallback cb,Bundle m){if(cs.isCanceled()){cb.onLayoutCancelled();return;}cb.onLayoutFinished(new PrintDocumentInfo.Builder("Teacher-report.pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(1).build(),true);}
        public void onWrite(PageRange[] p,ParcelFileDescriptor d,CancellationSignal cs,WriteResultCallback cb){try{java.io.FileOutputStream out=new java.io.FileOutputStream(d.getFileDescriptor());String s="Teacher\nAhmed Mostafa Abonajy\nالصف "+((MainActivity)c).grade+" - الفصل "+((MainActivity)c).cls+"\n";out.write(s.getBytes("UTF-8"));out.flush();cb.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});}catch(Exception e){cb.onWriteFailed(e.getMessage());}}
    }
}
