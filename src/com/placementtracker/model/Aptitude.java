package com.placementtracker.model;

import java.sql.Date;

public class Aptitude {

    private int aptitudeId;
    private String topic;
    private int score;
    private Date testDate;
    private String status;

    public Aptitude() {

    }

    public Aptitude(int aptitudeId, String topic, int score,
            Date testDate, String status) {

        this.aptitudeId = aptitudeId;
        this.topic = topic;
        this.score = score;
        this.testDate = testDate;
        this.status = status;
    }

    public int getAptitudeId() {
        return aptitudeId;
    }

    public void setAptitudeId(int aptitudeId) {
        this.aptitudeId = aptitudeId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}