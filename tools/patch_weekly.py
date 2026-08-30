from pathlib import Path
import re

path = Path("app/src/main/java/com/ahmed/teacher/MainActivity.java")
text = path.read_text(encoding="utf-8")

# Remove any fixed global weekly dates.
text, n = re.subn(r'\s*// WEEK_DATES_2026:.*?private final String\[\] weekDates=.*?;', '', text, count=1, flags=re.S)

# Add dates that belong to the currently selected class and week.
if 'private String dateKey(){return "assessment_date_"' not in text:
    marker='private final String[] weeks='
    p=text.find(marker); end=text.find(';',p)
    if p<0 or end<0: raise SystemExit("weeks declaration not found")
    helpers='''\n    private String dateKey(){return "assessment_date_"+grade+"_"+cls+"_"+weekIndex();}\n    private String savedWeekDate(){return prefs().getString(dateKey(),"");}\n    private void saveWeekDate(String value){prefs().edit().putString(dateKey(),value).apply();}\n    private String displayWeekDate(){String d=savedWeekDate();return d.isEmpty()?"لم يتم تحديد تاريخ التقييم":dayName(d)+" "+d;}\n    private String dayName(String value){try{java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("dd/MM/yyyy",java.util.Locale.US);java.util.Date d=f.parse(value);java.text.SimpleDateFormat df=new java.text.SimpleDateFormat("EEEE",new java.util.Locale("ar"));return df.format(d);}catch(Exception e){return "";}}\n    private void chooseWeekDate(){final java.util.Calendar c=java.util.Calendar.getInstance();String saved=savedWeekDate();if(!saved.isEmpty())try{c.setTime(new java.text.SimpleDateFormat("dd/MM/yyyy",java.util.Locale.US).parse(saved));}catch(Exception ignored){}android.app.DatePickerDialog dlg=new android.app.DatePickerDialog(this,(v,y,m,d)->{String s=String.format(java.util.Locale.US,"%02d/%02d/%04d",d,m+1,y);saveWeekDate(s);assessment();},c.get(java.util.Calendar.YEAR),c.get(java.util.Calendar.MONTH),c.get(java.util.Calendar.DAY_OF_MONTH));dlg.setTitle("تاريخ تقييم "+week+" - الصف "+grade+" الفصل "+cls);dlg.show();}\n'''
    text=text[:end+1]+helpers+text[end+1:]

# Replace old fixed date helpers if present.
text=re.sub(r'\s*private String weekDate\(\).*?private String weekTitle\(\)\{.*?\}', '\n    private String weekTitle(){return week+" • "+displayWeekDate();}', text, count=1, flags=re.S)
# If weekTitle wasn't found, add it before assessment.
if 'private String weekTitle(){return week+" • "+displayWeekDate();}' not in text:
    pos=text.find('private void assessment()')
    text=text[:pos]+'    private String weekTitle(){return week+" • "+displayWeekDate();}\n'+text[pos:]

# Add the automatic current-class date display and date picker button.
needle='root.addView(tv("📌 "+weekTitle(),17));'
if needle in text and 'تحديد / تغيير تاريخ التقييم' not in text:
    text=text.replace(needle,needle+'\n        root.addView(btn("📅 تحديد / تغيير تاريخ التقييم",v->chooseWeekDate()));',1)

path.write_text(text,encoding="utf-8")
print("Per-class weekly assessment dates patched successfully")
