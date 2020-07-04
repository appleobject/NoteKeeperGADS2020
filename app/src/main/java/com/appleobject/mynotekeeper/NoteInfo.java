package com.appleobject.mynotekeeper;

import android.os.Parcel;
import android.os.Parcelable;
// In other for our class NoteInfo to be pass in an intent.putExtra for a type reference,
// it needs to be parcelable so to make it parcelable we need to implement the interface Parcelable
public final class NoteInfo implements Parcelable {
    private CourseInfo mCourse;
    private String mTitle;
    private String mText;

    public NoteInfo(CourseInfo course, String title, String text) {
        mCourse = course;
        mTitle = title;
        mText = text;
    }

    // The private constructor from createFromParcel() which of the inner class
    // from the creator class with the field CREATOR. which as a parcel object as a parameter.
    // now, just as we wrote the values into the Parcel, we're going to read them back out.
    // Now we know the first value we want to read back is course
    // because that was the first one we wrote, but let's come back to that one.
    private NoteInfo(Parcel parcel) {
        mCourse = parcel.readParcelable(CourseInfo.class.getClassLoader());
        mTitle = parcel.readString();
        mText = parcel.readString();
    }

    public CourseInfo getCourse() {
        return mCourse;
    }

    public void setCourse(CourseInfo course) {
        mCourse = course;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    private String getCompareKey() {
        return mCourse.getCourseId() + "|" + mTitle + "|" + mText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteInfo that = (NoteInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    //describeContents: - Indicate any special behavior that our parceling may require,
    //In most cases we don't have any special behaviors, so most implementations just return 0.
    @Override
    public int describeContents() {
        return 0;
    }

    // writeToParcel is responsible to write the member information for the type instance into the Parcel,
    // and it receives a Parcel as a parameter.So what we want to do is go through
    // and write each member of NoteInfo into that Parcel.
    // the mCourse is a reference type so we need to pass it inside writeParcelable()
    // but for the mTitle and mText they are both strings so we call on writeString()
    // with the parcel object invoking them
    // and then writeToParcel handles the details of writing our content into the Parcel.
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mCourse, 0);
        parcel.writeString(mTitle);
        parcel.writeString(mText);
    }

    // now we need to add the code so it'll make our class recreatable from a Parcel.
    // And that's where the field called CREATOR comes in. Remember, the field needs to be named CREATOR,
    // all caps, and it's got to be final static, and its type has to be Parcelable.Creator.
    public static final Creator<NoteInfo> CREATOR =
            new Creator<NoteInfo>() {
        // CreateFromParcel is where we create a new instance of our type and
        // then set all the values inside of it using the Parcel.
        // Now one thing that's important to remember is that when setting the values within createFromParcel,
        // you must set them in the same order that you wrote the values within writeToParcel
        // because values stored in the Parcel have no identifiers.
                @Override
                public NoteInfo createFromParcel(Parcel parcel) {
                    return new NoteInfo(parcel);
                }
                // newArray - NewArray is responsible to create an array of our type of the appropriate size.
                // NewArray receives an integer parameter, which indicates the desired size of the array.
                // Now currently that parameter is i, we can change it to size
                @Override
                public NoteInfo[] newArray(int size) {
                    return new NoteInfo[size];
                }
            };
}












