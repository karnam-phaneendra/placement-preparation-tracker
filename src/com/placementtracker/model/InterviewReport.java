package com.placementtracker.model;

public class InterviewReport {

    private int totalInterviews;
    private int selected;
    private int rejected;
    private int pending;

    public int getTotalInterviews() {
        return totalInterviews;
    }

    public void setTotalInterviews(int totalInterviews) {
        this.totalInterviews = totalInterviews;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public int getRejected() {
        return rejected;
    }

    public void setRejected(int rejected) {
        this.rejected = rejected;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    public double getSelectionPercentage() {

        if (totalInterviews == 0)
            return 0;

        return (selected * 100.0) / totalInterviews;

    }

}