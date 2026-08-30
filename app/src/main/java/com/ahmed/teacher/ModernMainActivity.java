package com.ahmed.teacher;

import android.os.Bundle;

/**
 * Compatibility entry point. The grading screens use the original MainActivity
 * layout and logic to keep scrolling and data entry lightweight and stable.
 */
public class ModernMainActivity extends MainActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
