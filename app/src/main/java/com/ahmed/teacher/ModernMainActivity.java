package com.ahmed.teacher;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import java.lang.reflect.*;
import java.util.*;

/** Lightweight enhancement layer. Keeps the stable MainActivity grading UI. */
public class ModernMainActivity extends MainActivity {
    private final Handler ui=new Handler(Looper.getMainLooper());
    private boolean processing=false;
    private ArrayList<LinearLayout> cards=new ArrayList<>();
    private TextView progress;

    @Override public void onCreate(Bundle b){super.onCreate(b);}
    @Override public void setContentView(View view){super.setContentView(view);ui.post(()->enhance(view));}

    private void enhance(View root){
        if(processing)return; processing=true;
        try{
            boolean weekly=contains(root,"التقييم الأسبوعي"), monthly=contains(root,"التقييم الشهري والاختبارات");
            if(!weekly&&!monthly)return;
            cards.clear(); findCards(root,cards);
            addToolbar(root,weekly);
            ArrayList<EditText> edits=new ArrayList<>(); collect(root,edits);
            if(weekly){boolean locked=contains(root,"🔒 التقييم مقفول");for(EditText e:edits)e.setEnabled(!locked); connectStudents(edits);}
            updateProgress();
        }finally{processing=false;}
    }
    private boolean contains(View v,String s){if(v instanceof TextView&&String.valueOf(((TextView)v).getText()).contains(s))return true;if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++)if(contains(g.getChildAt(i),s))return true;}return false;}
    private void findCards(View v,ArrayList<LinearLayout> out){if(v instanceof LinearLayout){LinearLayout l=(LinearLayout)v;if(l.getChildCount()>0&&String.valueOf(l.getChildAt(0) instanceof TextView?((TextView)l.getChildAt(0)).getText():"").startsWith("👤 "))out.add(l);}if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++)findCards(g.getChildAt(i),out);}}
    private void collect(View v,ArrayList<EditText> out){if(v instanceof EditText){out.add((EditText)v);return;}if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++)collect(g.getChildAt(i),out);}}
    private ArrayList<EditText> fields(View v){ArrayList<EditText> a=new ArrayList<>();collect(v,a);return a;}
    private Button button(String s){Button b=new Button(this);b.setText(s);b.setTextSize(12);b.setAllCaps(false);b.setPadding(2,0,2,0);return b;}

    private void addToolbar(final View root,boolean weekly){
        if(!(root instanceof ViewGroup))return; ViewGroup g=(ViewGroup)root;
        for(int i=0;i<g.getChildCount();i++)if("fast_toolbar".equals(g.getChildAt(i).getTag()))return;
        LinearLayout bar=new LinearLayout(this);bar.setOrientation(LinearLayout.VERTICAL);bar.setPadding(5,5,5,5);bar.setBackgroundColor(Color.rgb(232,238,248));bar.setTag("fast_toolbar");
        LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER);
        Button quick=button("⚡ سريع"),cont=button("▶ استكمال"),group=button("👥 للكل"),search=button("🔎 بحث");
        r.addView(quick,new LinearLayout.LayoutParams(0,50,1));r.addView(cont,new LinearLayout.LayoutParams(0,50,1));r.addView(group,new LinearLayout.LayoutParams(0,50,1));r.addView(search,new LinearLayout.LayoutParams(0,50,1));bar.addView(r);
        progress=new TextView(this);progress.setGravity(Gravity.CENTER);progress.setTextSize(13);bar.addView(progress);
        LinearLayout f=new LinearLayout(this);f.setGravity(Gravity.CENTER);String[] fs={"الكل","ناقص","مكتمل","غائب"};for(String x:fs){Button b=button(x);b.setOnClickListener(v->filterCards(((Button)v).getText().toString()));f.addView(b,new LinearLayout.LayoutParams(0,42,1));}bar.addView(f);
        g.addView(bar,1);
        cont.setOnClickListener(v->focusIncomplete());group.setOnClickListener(v->groupDialog(weekly));quick.setOnClickListener(v->quickDialog(weekly));search.setOnClickListener(v->searchDialog());
    }
    private void updateProgress(){if(progress==null)return;int total=cards.size(),done=0;for(LinearLayout c:cards){ArrayList<EditText> f=fields(c);boolean ok=!f.isEmpty();for(EditText e:f)if(e.getText().toString().trim().isEmpty())ok=false;if(ok)done++;}progress.setText("👥 "+total+" طالب   •   ✅ تم: "+done+"   •   ⏳ متبقي: "+(total-done));}
    private void filterCards(String mode){for(LinearLayout c:cards){boolean absent=false;for(int i=0;i<c.getChildCount();i++)if(c.getChildAt(i)instanceof CheckBox)absent=((CheckBox)c.getChildAt(i)).isChecked();boolean done=true;ArrayList<EditText> f=fields(c);for(EditText e:f)if(e.getText().toString().trim().isEmpty())done=false;boolean show="الكل".equals(mode)||("مكتمل".equals(mode)&&done)||("ناقص".equals(mode)&&!done)||("غائب".equals(mode)&&absent);c.setVisibility(show?View.VISIBLE:View.GONE);}}
    private void focusIncomplete(){for(LinearLayout c:cards)for(EditText e:fields(c))if(e.getText().toString().trim().isEmpty()){e.requestFocus();e.selectAll();return;}Toast.makeText(this,"تم إكمال تقييم جميع الطلاب",Toast.LENGTH_SHORT).show();}

    private void connectStudents(ArrayList<EditText> e){int per=4;if(e.size()<8)return;int n=e.size()/per;for(int i=0;i<n-1;i++){EditText last=e.get(i*per+3),next=e.get((i+1)*per);if(Boolean.TRUE.equals(last.getTag()))continue;last.setTag(Boolean.TRUE);last.setImeOptions(EditorInfo.IME_ACTION_NEXT);last.setOnEditorActionListener((v,a,ev)->{if(a==EditorInfo.IME_ACTION_NEXT){next.requestFocus();next.selectAll();return true;}return false;});last.addTextChangedListener(new TextWatcher(){boolean moved=false;public void beforeTextChanged(CharSequence s,int a,int b,int c){}public void onTextChanged(CharSequence s,int a,int b,int c){if(!moved&&s.length()>0){moved=true;ui.postDelayed(()->{if(last.hasFocus()&&last.isEnabled()){next.requestFocus();next.selectAll();}},180);}}public void afterTextChanged(Editable e){}});}}

    private void searchDialog(){final EditText q=new EditText(this);q.setHint("اكتب جزءًا من اسم الطالب");new AlertDialog.Builder(this).setTitle("🔎 البحث عن طالب").setView(q).setNegativeButton("إلغاء",null).setPositiveButton("بحث",(d,w)->{String s=q.getText().toString().trim();for(LinearLayout c:cards){String n=c.getChildCount()>0&&c.getChildAt(0)instanceof TextView?String.valueOf(((TextView)c.getChildAt(0)).getText()):"";c.setVisibility(s.isEmpty()||n.contains(s)?View.VISIBLE:View.GONE);}}).show();}

    private void groupDialog(boolean weekly){String[] p=weekly?new String[]{"واجب منزلي /5","كراسة الحصة /5","تقييم أسبوعي /10","المواظبة والسلوك /5"}:new String[]{"الاختبار الأول /15","الاختبار الثاني /15"};new AlertDialog.Builder(this).setTitle("👥 إعطاء درجة للكل").setItems(p,(d,w)->askGroup(weekly,w)).setNegativeButton("إلغاء",null).show();}
    private void askGroup(boolean weekly,int part){int max=weekly?(part==2?10:5):15;EditText e=new EditText(this);e.setInputType(2);e.setHint("الدرجة من "+max);new AlertDialog.Builder(this).setTitle("الدرجة").setView(e).setNegativeButton("إلغاء",null).setPositiveButton("تطبيق",(d,w)->{try{int v=Math.max(0,Math.min(max,Integer.parseInt(e.getText().toString())));for(String n:getNames()){if(weekly){String[] p={"hw","copy","weekly","beh"};saveScore(n,p[part],v);}else saveExam(n,part+1,v);}call("assessment");}catch(Exception x){Toast.makeText(this,"تعذر التطبيق",Toast.LENGTH_SHORT).show();}}).show();}

    private void quickDialog(boolean weekly){ArrayList<String> ns=getNames();if(ns.isEmpty()){Toast.makeText(this,"لا يوجد طلاب",Toast.LENGTH_SHORT).show();return;}String[] parts=weekly?new String[]{"واجب منزلي /5","كراسة الحصة /5","تقييم أسبوعي /10","المواظبة والسلوك /5"}:new String[]{"الاختبار الأول /15","الاختبار الثاني /15"};final int[] si={0},pi={0};final AlertDialog dlg=new AlertDialog.Builder(this).setTitle("⚡ تقييم سريع").setNegativeButton("إغلاق",null).create();LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);TextView name=new TextView(this),pt=new TextView(this);name.setTextSize(18);name.setGravity(Gravity.CENTER);pt.setGravity(Gravity.CENTER);box.addView(name);box.addView(pt);LinearLayout scores=new LinearLayout(this);box.addView(scores);dlg.setView(box);dlg.setOnShowListener(x->renderQuick(name,pt,scores,ns,parts,si,pi,dlg,weekly));dlg.show();}
    private void renderQuick(TextView name,TextView pt,LinearLayout row,ArrayList<String> ns,String[] parts,int[] si,int[] pi,AlertDialog dlg,boolean weekly){name.setText("👤 "+ns.get(si[0]));pt.setText(parts[pi[0]]);row.removeAllViews();int max=weekly?(pi[0]==2?10:5):15;for(int v=0;v<=max;v++){final int score=v;Button b=button(String.valueOf(v));b.setTextSize(14);row.addView(b,new LinearLayout.LayoutParams(0,52,1));b.setOnClickListener(x->{if(weekly){String[] p={"hw","copy","weekly","beh"};saveScore(ns.get(si[0]),p[pi[0]],score);}else saveExam(ns.get(si[0]),pi[0]+1,score);if(pi[0]<parts.length-1)pi[0]++;else if(si[0]<ns.size()-1){si[0]++;pi[0]=0;}else{dlg.dismiss();call("assessment");return;}renderQuick(name,pt,row,ns,parts,si,pi,dlg,weekly);});}}

    @SuppressWarnings("unchecked") private ArrayList<String> getNames(){try{Field f=MainActivity.class.getDeclaredField("names");f.setAccessible(true);return new ArrayList<>((ArrayList<String>)f.get(this));}catch(Exception e){return new ArrayList<>();}}
    private void saveScore(String n,String part,int v){try{Field wf=MainActivity.class.getDeclaredField("week");wf.setAccessible(true);Method m=MainActivity.class.getDeclaredMethod("saveScore",String.class,String.class,String.class,int.class);m.setAccessible(true);m.invoke(this,n,wf.get(this),part,v);}catch(Exception ignored){}}
    private void saveExam(String n,int which,int v){try{Method m=MainActivity.class.getDeclaredMethod("saveExam",String.class,int.class,int.class);m.setAccessible(true);m.invoke(this,n,which,v);}catch(Exception ignored){}}
    private void call(String method){try{Method m=MainActivity.class.getDeclaredMethod(method);m.setAccessible(true);m.invoke(this);}catch(Exception ignored){}}
}
