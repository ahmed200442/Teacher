package com.ahmed.teacher;

import android.content.Context;
import android.net.Uri;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class ExcelReader {
    public static ArrayList<String> readNames(Context context, Uri uri) throws Exception {
        ArrayList<String> names = new ArrayList<>();
        ZipInputStream zip = new ZipInputStream(context.getContentResolver().openInputStream(uri));
        ArrayList<String> shared = new ArrayList<>();
        String sheet = null;
        ZipEntry e;
        while ((e = zip.getNextEntry()) != null) {
            if (e.getName().equals("xl/sharedStrings.xml")) shared = parseShared(zip);
            else if (e.getName().equals("xl/worksheets/sheet1.xml")) sheet = readAll(zip);
        }
        zip.close();
        if (sheet == null) throw new IOException("لم يتم العثور على ورقة البيانات");

        XmlPullParserFactory f = XmlPullParserFactory.newInstance();
        XmlPullParser p = f.newPullParser();
        p.setInput(new StringReader(sheet));

        HashMap<String,String> rowValues = new HashMap<>();
        String ref = "", type = "", val = "";
        boolean text = false, value = false;
        int nameCol = -1;
        boolean headerFound = false;

        while (p.next() != XmlPullParser.END_DOCUMENT) {
            int ev = p.getEventType();
            String tag = p.getName();

            if (ev == XmlPullParser.START_TAG && "c".equals(tag)) {
                ref = p.getAttributeValue(null, "r");
                type = p.getAttributeValue(null, "t");
                val = "";
            } else if (ev == XmlPullParser.START_TAG && "v".equals(tag)) {
                value = true;
            } else if (ev == XmlPullParser.START_TAG && "t".equals(tag)) {
                text = true;
            } else if (ev == XmlPullParser.TEXT && (value || text)) {
                val += p.getText();
            } else if (ev == XmlPullParser.END_TAG && "v".equals(tag)) {
                value = false;
            } else if (ev == XmlPullParser.END_TAG && "t".equals(tag)) {
                text = false;
            } else if (ev == XmlPullParser.END_TAG && "c".equals(tag)) {
                String v = val.trim();
                if ("s".equals(type) && !v.isEmpty()) {
                    int i = Integer.parseInt(v);
                    if (i >= 0 && i < shared.size()) v = shared.get(i);
                }
                rowValues.put(ref, v);
            } else if (ev == XmlPullParser.END_TAG && "row".equals(tag)) {
                if (!headerFound) {
                    int bestScore = 0;
                    int bestCol = -1;
                    for (Map.Entry<String,String> x : rowValues.entrySet()) {
                        int score = nameHeaderScore(x.getValue());
                        if (score > bestScore) {
                            bestScore = score;
                            String col = x.getKey().replaceAll("[0-9]", "");
                            bestCol = columnNumber(col);
                        }
                    }
                    if (bestCol > 0) {
                        nameCol = bestCol;
                        headerFound = true;
                    }
                } else {
                    for (Map.Entry<String,String> x : rowValues.entrySet()) {
                        String col = x.getKey().replaceAll("[0-9]", "");
                        if (columnNumber(col) == nameCol) {
                            String n = cleanCell(x.getValue());
                            if (!n.isEmpty() && nameHeaderScore(n) == 0 && !names.contains(n)) {
                                names.add(n);
                            }
                        }
                    }
                }
                rowValues.clear();
            }
        }

        if (names.isEmpty()) {
            throw new IOException("لم أجد أسماء الطلاب في عمود الاسم");
        }
        return names;
    }

    private static int nameHeaderScore(String value) {
        String s = normalize(value);
        if (s.isEmpty()) return 0;
        if (s.equals("اسم الطالب") || s.equals("الاسم") || s.equals("الإسم")) return 100;
        if (s.equals("اسم التلميذ") || s.equals("الاسم الكامل") || s.equals("الاسم بالكامل")) return 90;
        if (s.equals("اسم الطالب بالكامل") || s.equals("اسم الطالب كاملا") || s.equals("اسم الطالب كاملاً")) return 95;
        if (s.equals("الاسم رباعي") || s.equals("اسم الطالب رباعي")) return 90;
        return 0;
    }

    private static String normalize(String s) {
        if (s == null) return "";
        return s.replace('\uFEFF', ' ')
                .replace('\u00A0', ' ')
                .replace("ـ", "")
                .replaceAll("[\\t\\r\\n]+", " ")
                .trim()
                .replaceAll(" +", " ");
    }

    private static String cleanCell(String s) {
        return normalize(s);
    }

    private static int columnNumber(String s) {
        int n = 0;
        for (char c : s.toUpperCase(Locale.US).toCharArray()) {
            if (c >= 'A' && c <= 'Z') n = n * 26 + (c - 'A' + 1);
        }
        return n;
    }

    private static ArrayList<String> parseShared(InputStream in) throws Exception {
        ArrayList<String> a = new ArrayList<>();
        String xml = readAll(in);
        XmlPullParserFactory f = XmlPullParserFactory.newInstance();
        XmlPullParser p = f.newPullParser();
        p.setInput(new StringReader(xml));
        String s = "";
        boolean t = false;
        while (p.next() != XmlPullParser.END_DOCUMENT) {
            int ev = p.getEventType();
            String tag = p.getName();
            if (ev == XmlPullParser.START_TAG && "t".equals(tag)) {
                t = true;
                s = "";
            } else if (ev == XmlPullParser.TEXT && t) {
                s += p.getText();
            } else if (ev == XmlPullParser.END_TAG && "t".equals(tag)) {
                a.add(s);
                t = false;
            }
        }
        return a;
    }

    private static String readAll(InputStream in) throws Exception {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        byte[] x = new byte[8192];
        int n;
        while ((n = in.read(x)) != -1) b.write(x, 0, n);
        return b.toString("UTF-8");
    }
}
