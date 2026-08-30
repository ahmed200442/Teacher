package com.ahmed.teacher;

import android.content.Context;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class ExcelReader {
    public static ArrayList<String> readNames(Context context, android.net.Uri uri) throws Exception {
        ArrayList<String> names = new ArrayList<>();
        ZipInputStream zip = new ZipInputStream(context.getContentResolver().openInputStream(uri));
        ArrayList<String> shared = new ArrayList<>();
        String sheet = null;
        ZipEntry e;
        while ((e = zip.getNextEntry()) != null) {
            String n = e.getName();
            if (n.equals("xl/sharedStrings.xml")) shared = parseShared(zip);
            else if (n.equals("xl/worksheets/sheet1.xml")) sheet = readAll(zip);
        }
        zip.close();
        if (sheet == null) throw new IOException("لم يتم العثور على ورقة البيانات");
        XmlPullParserFactory f = XmlPullParserFactory.newInstance();
        XmlPullParser p = f.newPullParser(); p.setInput(new StringReader(sheet));
        boolean inV=false, inT=false; String type=null, val=""; String currentRef=""; String cell="";
        HashMap<String,String> rowValues=new HashMap<>(); int row=-1;
        while(p.next()!=XmlPullParser.END_DOCUMENT){
            int ev=p.getEventType(); String tag=p.getName();
            if(ev==XmlPullParser.START_TAG && "c".equals(tag)){currentRef=p.getAttributeValue(null,"r");type=p.getAttributeValue(null,"t");val="";}
            else if(ev==XmlPullParser.START_TAG && "v".equals(tag)){inV=true;}
            else if(ev==XmlPullParser.START_TAG && "t".equals(tag)){inT=true;}
            else if(ev==XmlPullParser.TEXT && (inV||inT)){val+=p.getText();}
            else if(ev==XmlPullParser.END_TAG && "v".equals(tag)){inV=false;}
            else if(ev==XmlPullParser.END_TAG && "t".equals(tag)){inT=false;}
            else if(ev==XmlPullParser.END_TAG && "c".equals(tag)){
                String value=val.trim(); if("s".equals(type)&&!value.isEmpty()){int i=Integer.parseInt(value); if(i<shared.size())value=shared.get(i);}
                rowValues.put(currentRef,value);
            }
            else if(ev==XmlPullParser.END_TAG && "row".equals(tag)){
                String name="";
                for(Map.Entry<String,String> x:rowValues.entrySet()){
                    String v=x.getValue(); if(v!=null && !v.trim().isEmpty() && (x.getKey().matches("[A-Z]+"+(row+2)) || x.getKey().matches("[A-Z]+"+(row+1)))) { }
                }
                // Find a name column by common header names; otherwise use column E when it contains text.
                for(String ref:rowValues.keySet()){
                    String v=rowValues.get(ref); if(v==null)continue;
                    String col=ref.replaceAll("[0-9]","");
                    if(col.equals("E") && !v.trim().isEmpty()) name=v.trim();
                }
                if(!name.isEmpty() && !name.equals("الإسم") && !name.equals("اسم الطالب") && !names.contains(name)) names.add(name);
                rowValues.clear(); row++;
            }
        }
        if(names.isEmpty()) throw new IOException("لم أجد أسماء في العمود المخصص للأسماء");
        return names;
    }
    private static ArrayList<String> parseShared(InputStream in) throws Exception { ArrayList<String> a=new ArrayList<>(); String xml=readAll(in); XmlPullParserFactory f=XmlPullParserFactory.newInstance(); XmlPullParser p=f.newPullParser();p.setInput(new StringReader(xml));String s="";boolean t=false;while(p.next()!=XmlPullParser.END_DOCUMENT){int ev=p.getEventType();String tag=p.getName();if(ev==XmlPullParser.START_TAG&&"t".equals(tag)){t=true;s="";}else if(ev==XmlPullParser.TEXT&&t)s+=p.getText();else if(ev==XmlPullParser.END_TAG&&"t".equals(tag)){a.add(s);t=false;}}return a;}
    private static String readAll(InputStream in)throws Exception{ByteArrayOutputStream b=new ByteArrayOutputStream();byte[] x=new byte[8192];int n;while((n=in.read(x))!=-1)b.write(x,0,n);return b.toString("UTF-8");}
}
