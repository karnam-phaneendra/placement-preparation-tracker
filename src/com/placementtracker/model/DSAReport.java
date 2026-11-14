package com.placementtracker.model;

public class DSAReport {

    private int totalProblems;
    private int easyProblems;
    private int mediumProblems;
    private int hardProblems;
    private int solvedProblems;
    private int revisionProblems;
    private int pendingProblems;

    public int getTotalProblems() {
        return totalProblems;
    }

    public void setTotalProblems(int totalProblems) {
        this.totalProblems = totalProblems;
    }

    public int getEasyProblems() {
        return easyProblems;
    }

    public void setEasyProblems(int easyProblems) {
        this.easyProblems = easyProblems;
    }

    public int getMediumProblems() {
        return mediumProblems;
    }

    public void setMediumProblems(int mediumProblems) {
        this.mediumProblems = mediumProblems;
    }

    public int getHardProblems() {
        return hardProblems;
    }

    public void setHardProblems(int hardProblems) {
        this.hardProblems = hardProblems;
    }

    public int getSolvedProblems() {
        return solvedProblems;
    }

    public void setSolvedProblems(int solvedProblems) {
        this.solvedProblems = solvedProblems;
    }

    public int getRevisionProblems() {
        return revisionProblems;
    }

    public void setRevisionProblems(int revisionProblems) {
        this.revisionProblems = revisionProblems;
    }

    public int getPendingProblems() {
        return pendingProblems;
    }

    public void setPendingProblems(int pendingProblems) {
        this.pendingProblems = pendingProblems;
    }

    public double getCompletionPercentage() {

        if (totalProblems == 0) {
            return 0;
        }

        return (solvedProblems * 100.0) / totalProblems;

    }

}