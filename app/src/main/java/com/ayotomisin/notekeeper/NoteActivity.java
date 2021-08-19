package com.ayotomisin.notekeeper;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_POSITION = "com.ayotomisin.notekeeper.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;

    private AppBarConfiguration appBarConfiguration;
    private NoteInfo mNote;
    private boolean isNewNote;
    private Spinner spinner;
    private EditText textNoteTitle;
    private EditText textNoteContent;
    private int notePosition;
    private boolean isCancelling;
    private NoteActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));

        viewModel = viewModelProvider.get(NoteActivityViewModel.class);

        if(viewModel.isNewlyCreated && savedInstanceState != null)
            viewModel.restoreState(savedInstanceState);

        viewModel.isNewlyCreated = false;

        spinner = findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCourses);

        readDisplayStateValues();
        saveOriginalNoteValues();

        textNoteTitle = findViewById(R.id.text_note_title);
        textNoteContent = findViewById(R.id.text_note_content);

        if(!isNewNote)
            displayNote(spinner, textNoteTitle, textNoteContent);
    }

    private void saveOriginalNoteValues() {
        if(isNewNote){
            return;
        }
        viewModel.originalCourseId = mNote.getCourse().getCourseId();
        viewModel.originalNoteTitle = mNote.getTitle();
        viewModel.originalNoteText = mNote.getText();
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        int position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);

        isNewNote = position == POSITION_NOT_SET;

        if(isNewNote) {
            createNewNote();
        }
        else {
            mNote = DataManager.getInstance().getNotes().get(position);
        }
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        notePosition = dm.createNewNote();
        mNote = dm.getNotes().get(notePosition);
    }

    private void displayNote(Spinner spinnerCourse, EditText title, EditText content) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(mNote.getCourse());
        spinnerCourse.setSelection(courseIndex);
        title.setText(mNote.getTitle());
        content.setText(mNote.getText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        }
        else if (id == R.id.action_cancel) {
            isCancelling = true;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCancelling) {
            if (isNewNote) {
                DataManager.getInstance().removeNote(notePosition);
            }
            else {
                storePreviousNoteValues();
            }
        }
        else {
            saveNote();
        }
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(viewModel.originalCourseId);
        mNote.setCourse(course);
        mNote.setTitle(viewModel.originalNoteTitle);
        mNote.setText(viewModel.originalNoteText);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewModel.saveState(outState);
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) spinner.getSelectedItem());
        mNote.setTitle(textNoteTitle.getText().toString());
        mNote.setText(textNoteContent.getText().toString());
    }

    /**
     *moves to the email app when the user clicks the send email button
     */
    private void sendEmail() {
        CourseInfo course = (CourseInfo) spinner.getSelectedItem();
        String subject = textNoteTitle.getText().toString();
        String body = "Checked what I learned in the Plurasight course \"" +
                course.getTitle() + "\"\n" + textNoteContent.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(intent);

    }
}