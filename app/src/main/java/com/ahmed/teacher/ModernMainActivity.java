package com.ahmed.teacher;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import java.util.*;

/** Lightweight compatibility layer for grading navigation/locking fixes. */
public class ModernMainActivity extends MainActivity {
    private final Handler ui = new Handler(Looper.getMainLooper());
    private boolean processing = false;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /** MainActivity rebuilds each screen with setContentView(). We hook that single
     * event instead of polling, keeping the grading screen lightweight. */
    @Override public void setContentView(View view) {
        super.setContentView(view);
        ui.post(() -> enhanceCurrentScreen(view));
    }

    private void enhanceCurrentScreen(View root) {
        if (processing) return;
        processing = true;
        try {
            ArrayList<EditText> edits = new ArrayList<>();
            collectEdits(root, edits);
            boolean weekly = containsText(root, "التقييم الأسبوعي");
            boolean locked = containsText(root, "🔒 التقييم مقفول");

            if (weekly) {
                // The original lock flag did not disable the four score boxes.
                // Disable them visually and functionally when the week is locked.
                for (EditText e : edits) e.setEnabled(!locked);
                connectStudentToNext(edits, root);
            }
        } finally {
            processing = false;
        }
    }

    private void collectEdits(View v, ArrayList<EditText> out) {
        if (v instanceof EditText) { out.add((EditText)v); return; }
        if (v instanceof ViewGroup) {
            ViewGroup g = (ViewGroup)v;
            for (int i=0;i<g.getChildCount();i++) collectEdits(g.getChildAt(i), out);
        }
    }

    private boolean containsText(View v, String wanted) {
        if (v instanceof TextView && String.valueOf(((TextView)v).getText()).contains(wanted)) return true;
        if (v instanceof ViewGroup) {
            ViewGroup g=(ViewGroup)v;
            for(int i=0;i<g.getChildCount();i++) if(containsText(g.getChildAt(i),wanted)) return true;
        }
        return false;
    }

    /** Weekly screen has four EditTexts per student. After the fourth grade is
     * entered, focus moves to the first grade of the next student. */
    private void connectStudentToNext(ArrayList<EditText> edits, View root) {
        if (edits.size() < 8) return;
        final int perStudent = 4;
        final int studentCount = edits.size() / perStudent;
        for (int s=0; s<studentCount-1; s++) {
            EditText last = edits.get(s*perStudent + 3);
            EditText next = edits.get((s+1)*perStudent);
            if (Boolean.TRUE.equals(last.getTag())) continue;
            last.setTag(Boolean.TRUE);
            last.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            last.setOnEditorActionListener((v, action, event) -> {
                if (action == EditorInfo.IME_ACTION_NEXT) {
                    next.requestFocus();
                    next.selectAll();
                    return true;
                }
                return false;
            });
            last.addTextChangedListener(new TextWatcher() {
                boolean moved=false;
                public void beforeTextChanged(CharSequence s,int st,int c,int a){}
                public void onTextChanged(CharSequence s,int st,int before,int count) {
                    if(moved || s.length()==0) return;
                    // Last weekly field is /5, so one digit completes it.
                    if(s.length()>=1) {
                        moved=true;
                        ui.postDelayed(() -> {
                            if(last.hasFocus() && last.isEnabled()) { next.requestFocus(); next.selectAll(); }
                        }, 180);
                    }
                }
                public void afterTextChanged(Editable e){}
            });
        }
    }
}
