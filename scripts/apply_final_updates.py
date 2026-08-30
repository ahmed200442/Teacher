from pathlib import Path
import re

p = Path('app/src/main/java/com/ahmed/teacher/MainActivity.java')
t = p.read_text(encoding='utf-8')

t = t.replace('private int maxFor(String p){return "test".equals(p)?10:5;}', 'private int maxFor(String p){return "weekly".equals(p)?10:5;}')
t = t.replace('new InputFilter.LengthFilter(max==10?2:1)', 'new InputFilter.LengthFilter(max>=10?2:1)')

pat = r'    private void addStudent\(String n,boolean locked\)\{.*?\n    \}\n    private void setAutoNext'
method = r'''    private void addStudent(String n,boolean locked){
        LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(dp(7),dp(8),dp(7),dp(8));card.setBackground(cardBg());
        TextView name=tv("👤 "+n,16);name.setGravity(Gravity.RIGHT);card.addView(name);
        CheckBox absent=new CheckBox(this);absent.setText("غائب ✓  (اتركه بدون علامة للحضور)");absent.setChecked(isAbsent(n,week));absent.setEnabled(!locked);card.addView(absent);
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER);row.setWeightSum(5);
        EditText hw=scoreEdit("واجب /5",5),copy=scoreEdit("كراسة الحصة /5",5),weekly=scoreEdit("تقييم أسبوعي /10",10),beh=scoreEdit("المواظبة والسلوك /5",5);
        loadEdit(hw,savedScore(n,week,"hw"));loadEdit(copy,savedScore(n,week,"copy"));loadEdit(weekly,savedScore(n,week,"weekly"));loadEdit(beh,savedScore(n,week,"beh"));
        TextView total=tv("المجموع\n0 /25",12);total.setBackgroundColor(Color.rgb(238,243,250));total.setLayoutParams(new LinearLayout.LayoutParams(0,dp(58),1));
        row.addView(hw);row.addView(copy);row.addView(weekly);row.addView(beh);row.addView(total);card.addView(row);
        if(!locked){
            EditText[] fields={hw,copy,weekly,beh};String[] parts={"hw","copy","weekly","beh"};
            TextWatcher watcher=new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int b,int c){}public void onTextChanged(CharSequence s,int a,int b,int c){int sum=0;for(int i=0;i<fields.length;i++){int v=num(fields[i],maxFor(parts[i]));saveScore(n,week,parts[i],v);sum+=v;}total.setText("المجموع\n"+sum+" /25");}public void afterTextChanged(Editable e){}};
            for(EditText e:fields)e.addTextChangedListener(watcher);
            for(int i=0;i<fields.length;i++)setAutoNext(fields[i],i<fields.length-1?fields[i+1]:null,maxFor(parts[i]));
            absent.setOnCheckedChangeListener((button,checked)->saveAbsent(n,week,checked));
        }
        int initial=savedScore(n,week,"hw")+savedScore(n,week,"copy")+savedScore(n,week,"weekly")+savedScore(n,week,"beh");total.setText("المجموع\n"+initial+" /25");root.addView(card);Space gap=new Space(this);gap.setLayoutParams(new LinearLayout.LayoutParams(1,dp(10)));root.addView(gap);
    }
    private void setAutoNext'''
t, n = re.subn(pat, method, t, count=1, flags=re.S)
if n != 1: raise SystemExit('weekly method not found')

pat = r'private double weeklyAverage\(String n\)\{.*?\n    private String fmt'
method = r'''private double weeklyAverage(String n){double sum=0;int count=0;for(String w:weeks){if(hasWeekData(n,w)){sum+=savedScore(n,w,"beh")+savedScore(n,w,"hw")+savedScore(n,w,"copy")+savedScore(n,w,"weekly");count++;}}return count==0?0:sum/count;}
    private boolean hasWeekData(String n,String w){return savedScore(n,w,"beh")>0||savedScore(n,w,"hw")>0||savedScore(n,w,"copy")>0||savedScore(n,w,"weekly")>0||isAbsent(n,w);}
    private String fmt'''
t, n = re.subn(pat, method, t, count=1, flags=re.S)
if n != 1: raise SystemExit('average method not found')

pat = r'    private void monthly\(\)\{.*?\n    private void printReport'
method = r'''    private void monthly(){
        base("📊 التقييم الشهري والاختبارات");root.addView(tv("الصف "+grade+" - الفصل "+cls,18));
        root.addView(tv("الأول /15   •   الثاني /15   •   المتوسط /15   •   التقييمات /25   •   الإجمالي /40",14));
        if(names.isEmpty())load();for(String n:new ArrayList<>(names))addMonthlyStudent(n);
        root.addView(btn("🗑 حذف درجات الاختبارات للكل",v->deleteAllMonthlyScoresDialog()));
        root.addView(btn("🗑 حذف الكل: الطلاب ودرجاتهم",v->deleteAllStudentsDialog()));
        root.addView(btn("📤 تصدير Excel",v->exportExcel()));root.addView(btn("⬅ رجوع",v->open(cls)));
    }
    private String examKey(String type,String n){return type+"_"+grade+"_"+cls+"_"+n.hashCode();}
    private void addMonthlyStudent(String n){
        LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(dp(8),dp(8),dp(8),dp(8));card.setBackground(cardBg());card.addView(tv("👤 "+n,16));
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER);row.setWeightSum(5);EditText a=scoreEdit("الأول /15",15),b=scoreEdit("الثاني /15",15);
        loadEdit(a,prefs().getInt(examKey("exam1",n),0));loadEdit(b,prefs().getInt(examKey("exam2",n),0));TextView avg=tv("المتوسط\n0 /15",12),wk=tv("التقييمات\n0 /25",12),tot=tv("الإجمالي\n0 /40",12);
        row.addView(a);row.addView(b);row.addView(avg);row.addView(wk);row.addView(tot);card.addView(row);
        Runnable recalc=()->{int x1=num(a,15),x2=num(b,15);double av=(x1+x2)/2.0,wa=weeklyAverage(n);avg.setText("المتوسط\n"+fmt(av)+" /15");wk.setText("التقييمات\n"+fmt(wa)+" /25");tot.setText("الإجمالي\n"+fmt(av+wa)+" /40");};
        TextWatcher tw=new TextWatcher(){public void beforeTextChanged(CharSequence s,int x,int y,int z){}public void onTextChanged(CharSequence s,int x,int y,int z){prefs().edit().putInt(examKey("exam1",n),num(a,15)).putInt(examKey("exam2",n),num(b,15)).apply();recalc.run();}public void afterTextChanged(Editable e){}};a.addTextChangedListener(tw);b.addTextChangedListener(tw);setAutoNext(a,b,15);setAutoNext(b,null,15);recalc.run();root.addView(card);Space gap=new Space(this);gap.setLayoutParams(new LinearLayout.LayoutParams(1,dp(8)));root.addView(gap);
    }
    private void deleteAllMonthlyScoresDialog(){if(names.isEmpty())load();new AlertDialog.Builder(this).setTitle("⚠️ حذف درجات الاختبارات للكل").setMessage("سيتم حذف الاختبار الأول والثاني لكل طلاب هذا الفصل فقط، مع الإبقاء على الطلاب والتقييمات الأسبوعية.").setNegativeButton("إلغاء",null).setPositiveButton("حذف الكل",(d,w)->{SharedPreferences.Editor e=prefs().edit();for(String n:names){e.remove(examKey("exam1",n));e.remove(examKey("exam2",n));}e.apply();monthly();}).show();}
    private void exportExcel(){if(names.isEmpty())load();try{org.apache.poi.xssf.usermodel.XSSFWorkbook wb=new org.apache.poi.xssf.usermodel.XSSFWorkbook();org.apache.poi.ss.usermodel.Sheet sh=wb.createSheet("الدرجات");String[] h={"الطالب","الأول /15","الثاني /15","المتوسط /15","التقييمات /25","الإجمالي /40","الغياب"};org.apache.poi.ss.usermodel.Row hr=sh.createRow(0);for(int i=0;i<h.length;i++)hr.createCell(i).setCellValue(h[i]);int r=1;for(String n:names){int x1=prefs().getInt(examKey("exam1",n),0),x2=prefs().getInt(examKey("exam2",n),0);double av=(x1+x2)/2.0,wa=weeklyAverage(n);org.apache.poi.ss.usermodel.Row row=sh.createRow(r++);row.createCell(0).setCellValue(n);row.createCell(1).setCellValue(x1);row.createCell(2).setCellValue(x2);row.createCell(3).setCellValue(av);row.createCell(4).setCellValue(wa);row.createCell(5).setCellValue(av+wa);int abs=0;for(String ww:weeks)if(isAbsent(n,ww))abs++;row.createCell(6).setCellValue(abs);}for(int i=0;i<h.length;i++)sh.setColumnWidth(i,6000);java.io.File dir=new java.io.File(getExternalFilesDir(null),"exports");if(!dir.exists())dir.mkdirs();java.io.File file=new java.io.File(dir,"Teacher_"+grade+"_"+cls+".xlsx");java.io.FileOutputStream out=new java.io.FileOutputStream(file);wb.write(out);out.close();wb.close();Toast.makeText(this,"تم إنشاء ملف Excel: "+file.getName(),Toast.LENGTH_LONG).show();}catch(Exception e){Toast.makeText(this,"تعذر إنشاء Excel: "+e.getMessage(),Toast.LENGTH_LONG).show();}}
    private void printReport'''
t, n = re.subn(pat, method, t, count=1, flags=re.S)
if n != 1: raise SystemExit('monthly method not found')

p.write_text(t, encoding='utf-8')
print('final updates applied successfully')
