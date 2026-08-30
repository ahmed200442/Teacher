from pathlib import Path
import re
p=Path('app/src/main/java/com/ahmed/teacher/MainActivity.java')
t=p.read_text(encoding='utf-8')
# Replace fixed weekly dates with per-class/per-week dates.
t=re.sub(r'\s*// WEEK_DATES_2026:.*?\n\s*private final String\[\] weekDates=\{.*?\};\n', '\n', t, count=1, flags=re.S)
# Replace weekDate helper with stored date helpers.
pattern=r'\s*private String weekDate\(\)\{.*?\}\n    private String weekTitle\(\)\{.*?\}\n'
replacement='''
    private String dateKey(){return "weekdate_"+grade+"_"+cls+"_"+weekIndex();}
    private String weekDate(){return prefs().getString(dateKey(),"");}
    private String dayName(String date){
        if(date==null||date.isEmpty())return "";
        try{java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("dd/MM/yyyy",java.util.Locale.US);java.util.Date d=f.parse(date);java.util.Calendar c=java.util.Calendar.getInstance();c.setTime(d);String[] days={"الأحد","الإثنين","الثلاثاء","الأربعاء","الخميس","الجمعة","السبت"};return days[c.get(java.util.Calendar.DAY_OF_WEEK)-1];}catch(Exception e){return "";}
    }
    private String weekTitle(){String d=weekDate();return week+(d.isEmpty()?"":" • "+dayName(d)+" "+d);}
    private void chooseWeekDate(){
        String old=weekDate();
        java.util.Calendar cal=java.util.Calendar.getInstance();
        if(!old.isEmpty())try{java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("dd/MM/yyyy",java.util.Locale.US);cal.setTime(f.parse(old));}catch(Exception ignored){}
        DatePickerDialog dp=new DatePickerDialog(this,(view,y,m,day)->{
            String d=String.format(java.util.Locale.US,"%02d/%02d/%04d",day,m+1,y);
            prefs().edit().putString(dateKey(),d).apply();
            assessment();
        },cal.get(java.util.Calendar.YEAR),cal.get(java.util.Calendar.MONTH),cal.get(java.util.Calendar.DAY_OF_MONTH));
        dp.setTitle("تحديد تاريخ التقييم - "+week);
        dp.show();
    }
'''
t2=re.sub(pattern,replacement,t,count=1,flags=re.S)
if t2==t: raise SystemExit('week date helpers not found')
t=t2
# Replace the weekly schedule method, if present.
t=re.sub(r'\s*private String weekSchedule\(\)\{.*?\n    \}\n    private void goPreviousWeek', '\n    private void goPreviousWeek', t, count=1, flags=re.S)
# Add date button immediately after the week title line in assessment.
needle='root.addView(tv("📅 "+weekTitle(),17));'
insert='''root.addView(tv("📅 "+weekTitle(),17));
        root.addView(btn(weekDate().isEmpty()?"📅 تحديد تاريخ التقييم":"✏️ تغيير تاريخ التقييم",v->chooseWeekDate()));'''
if needle in t and 'chooseWeekDate()' not in t[t.find(needle):t.find(needle)+500]:
    t=t.replace(needle,insert,1)
else:
    raise SystemExit('assessment date line not found')
p.write_text(t,encoding='utf-8')
print('patched per-class per-week assessment dates')
