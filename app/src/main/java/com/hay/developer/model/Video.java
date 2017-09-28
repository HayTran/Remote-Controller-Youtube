package com.hay.developer.model;

/**
 * Created by hay on 18-Sep-17.
 */

public class Video {
    private String mLink;
    private String mVideoTitle;
    private String mState;
    private int durationMilisecond;
    private int currentMilisecond;
    private String lastTimePlay;
    private String revolution;
    private static Video instance;

    private Video() {

    }

    public static Video getInstance(){
       if (instance == null) {
           instance = new Video();
       }
       return instance;
    }

    public String getmLink() {
        return mLink;
    }

    public void setmLink(String mLink) {
        this.mLink = mLink;
    }

    public String getmVideoTitle() {
        return mVideoTitle;
    }

    public void setmVideoTitle(String mVideoTitle) {
        this.mVideoTitle = mVideoTitle;
    }

    public int getDurationMilisecond() {
        return durationMilisecond;
    }

    public void setDurationMilisecond(int durationMilisecond) {
        this.durationMilisecond = (int)durationMilisecond/1000;
    }

    public String getmState() {
        return mState;
    }

    public void setmState(String mState) {
        this.mState = mState;
    }

    public int getCurrentMilisecond() {
        return currentMilisecond;
    }

    public void setCurrentMilisecond(int currentMilisecond) {
        this.currentMilisecond = (int)currentMilisecond/1000;
    }

    public String getLastTimePlay() {
        return lastTimePlay;
    }

    public void setLastTimePlay(String lastTimePlay) {
        this.lastTimePlay = lastTimePlay;
    }

    public String getRevolution() {
        return revolution;
    }

    public void setRevolution(String revolution) {
        this.revolution = revolution;
    }

    @Override
    public String toString() {
        return "Video{" +
                "mLink='" + mLink + '\'' +
                ", mVideoTitle='" + mVideoTitle + '\'' +
                ", mState='" + mState + '\'' +
                ", durationMilisecond=" + durationMilisecond +
                ", currentMilisecond=" + currentMilisecond +
                ", revolution='" + revolution + '\'' +
                '}';
    }
}
