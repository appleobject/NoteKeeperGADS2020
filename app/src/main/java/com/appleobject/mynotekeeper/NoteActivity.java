package com.appleobject.mynotekeeper;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;



import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public String TAG = "NOTE_POSITION";
    public static final String NOTE_POSITION = "com.appleobject.notekeeper.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNote;  // a reference from the NoteInfo class
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private boolean mIsCancelling;
    private int mNotePosition;
    private NoteActivityViewModel mViewModel;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null){
            mViewModel.saveState(outState);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));
          mViewModel = viewModelProvider.get(NoteActivityViewModel.class);

          if(mViewModel.mIsNewlyCreated && savedInstanceState != null){
              mViewModel.restoreState(savedInstanceState);
          }

          mViewModel.mIsNewlyCreated = false;


        mSpinnerCourses = findViewById(R.id.spinner_courses);
        // A singleton class that call on methods for getting all courses
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        // Drop-down view resource for the spinner
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the setAdapter method on the spinner as our ArrayAdapter variable
        // invoke by the variable assign to the findViewById on the spinner
        mSpinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        saveOriginalNoteValues();

         mTextNoteTitle = findViewById(R.id.text_note_title);
         mTextNoteText =  findViewById(R.id.text_note_text);


        /*
        * let's go ahead and set a field called isNewNote that's a Boolean based on whether the note is null.
        * Now if there's no note passed,isNewNote will be true, but if there is a note passed,
        * isNewNote will be false. We probably want this to go
        * */
        if(!mIsNewNote)
            displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);


    }

    private void saveOriginalNoteValues() {
        if (mIsNewNote)
            return;
        mViewModel.mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mViewModel.mOriginalNoteTitle = mNote.getTitle();
        mViewModel.mOriginalNoteText = mNote.getText();



    }

    /*
    * DataManager is the singleton class (an object that contains fields&methods)/ like a database 
    * while getInstance() is the method for initializing an instance if the instance is null(DAO)
    * CourseInfo.java and NoteInfo are the data class that will parcel into an intent from the NoteListActivity
    * Get reference to the two fields textNoteTitle and textNoteText and set their text respectively.
    * And then what we'll do here is first of all, let's get the list of courses from our DataManager.
    * So that gives us our list of courses.
     * */
    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(mNote.getCourse());
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(mNote.getTitle());
        textNoteText.setText(mNote.getText());
    }

    /*
    * Getting the value out of our intent coming from the NoteListActivity
    * create an intent object assign it to getIntent()
    * the intent object will invoke the getParcelableExtra()
    * and then pass the CONSTANT Key from the source activity
    * */
    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mNotePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        mIsNewNote = mNotePosition == POSITION_NOT_SET;
        if (mIsNewNote){
            createNewNote();
        }

        Log.i(TAG, "mNotePosition " + mNotePosition);

        mNote = DataManager.getInstance().getNotes().get(mNotePosition);
    }

    /*
    * createNewNote method, we need a reference to the DataManager
    * call on getInstance() and assign it to the local variable - dm
    * which will invoke the createNewNote() inside the DataManager and
    * assign the return value from this method into another local variable - notePosition
    * with the return type from the method as int. what that tells me is
    * what is the position of the newly created note. Now as I think about this,
    * is I probably want that as a field, not a variable. ctrl+alt+f to covert the
    * local variable into a field - mNotePosition, now we have the position of the note
    * */
    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
        mNote = dm.getNotes().get(mNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        * Handle action bar item clicks here. The action bar will
        * automatically handle clicks on the Home/Up button, so long
        * as you specify a parent activity in AndroidManifest.xml.
        * */
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_email) {
            sendEmail();
            return true;
        }else if(id == R.id.action_cancel){
            mIsCancelling = true;
            finish();

        }else if (id == R.id.action_next){
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    * note :- the last index of a list is always the size of the list -1,
    * what we will do is use the lastNoteIndex to determine whether we will disable the menu item
    * or not and item.setEnabled() takes in a boolean value and check if it's true or false
    * in other to enable(true) or disable(false) the menu item.
     * On getting to the lastNote, the invalidateOptionMenu will schedule a call on the onPrepareOptionsMenu
    * */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next); // reference the id of the menu item
        int lastNoteIndex = DataManager.getInstance().getNotes().size() -1; // get the size of note -1
        item.setEnabled(mNotePosition < lastNoteIndex);
        Log.i(TAG, "lastNoteIndex : " +lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);


    }

    // Implementing the Next menu item behaviour
    private void moveNext() {
        saveNote(); // We save any changes they might have make to the existing note
        ++mNotePosition; // Advance to the next note and get it

        mNote = DataManager.getInstance().getNotes().get(mNotePosition);

        saveOriginalNoteValues(); // Save the original value for that note
        displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText); // display them
        /*
        * invalidateOptionsMenu send a call to the onPrepareOptionsMenu to check on disabling
        * the menu item when the setEnabled method return false
        * */
        invalidateOptionsMenu();
        
    }

    /*
    * if we are canceling? Well remember that when we created the new note we automatically
    * created the backing store for it, but now we would have an empty note
    * if the user cancels out in the process of creating a new note.
    * So what we want to do is remove that note that we created from our backing store
    * if the user's canceling out after selecting new note.
    * So what we'll do here is inside of our isCancelling, our isCancelling, we want to
    * go to the DataManager and get the instance. And the DataManager has a method
    * called removeNote that accepts the position when removed. And remember, we stored
    * the position of our new note in our field mNotePosition,
    * so we'll call removeNote with mNotePosition.
    * */
    @Override
    protected void onPause() {
        super.onPause();
        if (mIsCancelling){
            if(mIsNewNote) {
                DataManager.getInstance().removeNote(mNotePosition);
            }else{
                storePreviousNoteValues();
            }
        }else{
            saveNote(); // Save the note after the user move to another activity
        }
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());

    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mViewModel.mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mViewModel.mOriginalNoteTitle);
        mNote.setText(mViewModel.mOriginalNoteText);
    }

    // the sendEmail method on an implicit intent on the menu
    private void sendEmail() {
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = "Check out what I learned in the Pluralsight course, \" " +
                course.getTitle() + "\"\n " + mTextNoteText.getText();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);

    }
}
