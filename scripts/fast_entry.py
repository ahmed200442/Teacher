from pathlib import Path
p=Path('app/src/main/java/com/ahmed/teacher/MainActivity.java')
t=p.read_text(encoding='utf-8')
if 'AUTO_ADVANCE_V2' in t:
    print('auto advance already applied'); raise SystemExit(0)
needle='e.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int before,int count){try{int v=s.length()==0?0:Integer.parseInt(s.toString());saveScore(student,week,part,v);}catch(Exception ignored){}}public void afterTextChanged(Editable e){}});'
replacement='e.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int before,int count){try{int v=s.length()==0?0:Integer.parseInt(s.toString());saveScore(student,week,part,v);if(s.length()==1 && i<parts.length-1){final int nextIndex=i+1;postDelayed(()->{fields.get(nextIndex).requestFocus();fields.get(nextIndex).selectAll();},120);}}catch(Exception ignored){}}public void afterTextChanged(Editable e){}}); // AUTO_ADVANCE_V2'
if needle not in t: raise SystemExit('grading watcher not found')
t=t.replace(needle,replacement,1)
p.write_text(t,encoding='utf-8')
print('automatic next-field entry applied')
