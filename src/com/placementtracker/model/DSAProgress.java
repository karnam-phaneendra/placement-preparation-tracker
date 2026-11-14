package com.placementtracker.model;

import java.sql.Date;

public class DSAProgress {

    private int problemId;
    private String topic;
    private String problemName;
    private String platform;
    private String difficulty;
    private String status;
    private Date dateSolved;

    // Default Constructor
    public DSAProgress() {

    }

    // Parameterized Constructor
    public DSAProgress(int problemId, String topic, String problemName,
            String platform, String difficulty, String status, Date dateSolved) {

        this.problemId = problemId;
        this.topic = topic;
        this.problemName = problemName;
        this.platform = platform;
        this.difficulty = difficulty;
        this.status = status;
        this.dateSolved = dateSolved;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateSolved() {
        return dateSolved;
    }

    public void setDateSolved(Date dateSolved) {
        this.dateSolved = dateSolved;
    }

}