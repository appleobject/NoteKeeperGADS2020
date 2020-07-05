package com.appleobject.mynotekeeper;


import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.Espresso.onView;
import static org.hamcrest.Matchers.*;
import java.util.List;

import static org.junit.Assert.*;

public class NextThroughNotesTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void nextThroughNotesTest(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open()); //open the drawer layout
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes)); // action on the navigation view

        // perform an item selected on the RecyclerView
        onView(withId(R.id.list_items)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // get the note at this index
        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        for(int index = 0; index < notes.size(); index++){
            NoteInfo note = notes.get(index);

            // and verify the note activity class display the right data for that selected note.
            onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText(note.getCourse().getTitle())));
            onView(withId(R.id.text_note_title)).check(matches(withText(note.getTitle())));
            onView(withId(R.id.text_note_text)).check(matches(withText(note.getText())));

            //Testing Menu behaviour
            if (index < notes.size() -1)
                onView(allOf(withId(R.id.action_next), isEnabled())).perform(click());



        }

        // We want to make sure when we get to the last index, the Next menu behaviour is actually disabled
        onView(withId(R.id.action_next)).check(matches(not(isEnabled())));
        pressBack();




    }

}