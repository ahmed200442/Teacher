from pathlib import Path
import re

path = Path("app/src/main/java/com/ahmed/teacher/MainActivity.java")
text = path.read_text(encoding="utf-8")

helpers = r'''private int weekIndex(){for(int i=0;i<weeks.length;i++)if(weeks[i].equals(week))return i;return 0;}
    private String weekDate(){int i=weekIndex();return i<weekDates.length?weekDates[i]:"";}
    private String weekTitle(){return week+" • "+weekDate();}
    private String weekSchedule(){
        int i=weekIndex();
        if(i<0||i>=weekDates.length)return "";
        try{
            java.text.SimpleDateFormat in=new java.text.SimpleDateFormat("dd/MM/yyyy",java.util.Locale.US);
            java.util.Date d=in.parse(weekDates[i]);
            java.util.Calendar c=java.util.Calendar.getInstance(); c.setTime(d);
            String[] days={"السبت","الأحد","الإثنين","الثلاثاء","الأربعاء","الخميس","الجمعة"};
            StringBuilder s=new StringBuilder("📅 ");
            for(int j=0;j<7;j++){
                if(j>0)s.append("  •  ");
                s.append(days[j]).append(" ").append(in.format(c.getTime()));
                c.add(java.util.Calendar.DAY_OF_MONTH,1);
            }
            return s.toString();
        }catch(Exception e){return "📅 "+weekDate();}
    }
    private void goPreviousWeek(){int i=weekIndex();if(i>0){week=weeks[i-1];assessment();}else Toast.makeText(this,"أنت بالفعل في الأسبوع الأول",Toast.LENGTH_SHORT).show();}
    private void goNextWeek(){int i=weekIndex();if(i<weeks.length-1){week=weeks[i+1];assessment();}else Toast.makeText(this,"هذا آخر أسبوع في القائمة",Toast.LENGTH_SHORT).show();}
'''

text, n = re.subn(r'(?s)\s*private int weekIndex\(\).*?(?=private void assessment\(\))', '\n    ' + helpers, text, count=1)
if n != 1:
    raise SystemExit("weekly helper block not found")

header = r'''private void assessment(){
        base("الصف "+grade+" - الفصل "+cls);
        root.addView(tv("التقييم الأسبوعي",20));
        root.addView(tv("📌 "+weekTitle(),17));
        TextView schedule=tv(weekSchedule(),14);
        schedule.setGravity(Gravity.RIGHT);
        schedule.setTextColor(Color.rgb(70,90,125));
        root.addView(schedule);
        root.addView(tv("المجموع /25 تلقائي",15));'''

text, n = re.subn(
    r'(?s)private void assessment\(\)\{.*?root\.addView\(tv\("المجموع /25 تلقائي",15\)\);',
    header,
    text,
    count=1,
)
if n != 1:
    raise SystemExit("assessment header not found")

old = 'setWeekLocked(true);Toast.makeText(this,"تم قفل "+week+" بتاريخ "+weekDate(),Toast.LENGTH_SHORT).show();goNextWeek();'
new = old
if old not in text:
    old2 = 'setWeekLocked(true);assessment();Toast.makeText(this,"تم قفل "+week,Toast.LENGTH_SHORT).show();'
    if old2 in text:
        text = text.replace(old2, new, 1)

path.write_text(text, encoding="utf-8")
print("Weekly assessment schedule patched successfully")
