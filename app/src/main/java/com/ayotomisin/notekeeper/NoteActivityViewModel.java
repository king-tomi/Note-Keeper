package com.ayotomisin.notekeeper;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

public class NoteActivityViewModel extends ViewModel {
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.ayotomisin.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.ayotomisin.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.ayotomisin.notekeeper.ORIGINAL_NOTE_TEXT";

    public String originalCourseId;
    public String originalNoteText;
    public String originalNoteTitle;
    public Boolean isNewlyCreated = true;

    public void saveState(Bundle outState) {
        outState.putString(ORIGINAL_NOTE_COURSE_ID, originalCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, originalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, originalNoteText);
    }

    public void restoreState(Bundle instate) {
        originalCourseId = instate.getString(ORIGINAL_NOTE_COURSE_ID);
        originalNoteTitle = instate.getString(ORIGINAL_NOTE_TITLE);
        originalNoteText = instate.getString(ORIGINAL_NOTE_TEXT);
    }
}
