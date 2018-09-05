package com.conext.model;

import android.graphics.Bitmap;

/**
 * Created by Shriram on 6/22/2017.
 */

public class MyMentees {

    private String menteeName;
    private String menteeDescription;
    private String menteeIn;
    private Bitmap menteeImage;
    private String mentorID;
    private String otherMentoring;

    public MyMentees() {
    }

    public MyMentees(String menteeName, String menteeDescription, String menteeIn, Bitmap menteeImage, String mentorID, String otherMentoring) {

        this.menteeName = menteeName;
        this.menteeDescription = menteeDescription;
        this.menteeIn = menteeIn;
        this.menteeImage = menteeImage;
        this.mentorID = mentorID;
        this.otherMentoring = otherMentoring;
    }

    public String getMenteeName() {

        return menteeName;
    }

    public void setMenteeName(String menteeName) {
        this.menteeName = menteeName;
    }

    public String getMenteeDescription() {
        return menteeDescription;
    }

    public void setMenteeDescription(String menteeDescription) {
        this.menteeDescription = menteeDescription;
    }

    public String getMenteeIn() {
        return menteeIn;
    }

    public void setMenteeIn(String menteeIn) {
        this.menteeIn = menteeIn;
    }

    public Bitmap getMenteeImage() {
        return menteeImage;
    }

    public void setMenteeImage(Bitmap menteeImage) {
        this.menteeImage = menteeImage;
    }

    public String getMentorID() {
        return mentorID;
    }

    public void setMentorID(String mentorID) {
        this.mentorID = mentorID;
    }

    public String getOtherMentoring() {
        return otherMentoring;
    }

    public void setOtherMentoring(String otherMentoring) {
        this.otherMentoring = otherMentoring;
    }
}
