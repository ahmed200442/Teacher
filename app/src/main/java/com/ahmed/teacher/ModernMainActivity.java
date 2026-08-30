package com.ahmed.teacher;

import android.os.Bundle;

/**
 * Lightweight compatibility entry point.
 * Uses the original MainActivity without extra UI hooks so the app remains
 * stable and responsive. New grading features will be added directly to the
 * original screen logic in small, tested changes.
 */
public class ModernMainActivity extends MainActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
