package com.placementtracker.model;

public class AptitudeReport {

    private int totalTests;
    private int excellent;
    private int good;
    private int average;
    private int needsImprovement;

    public int getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(int totalTests) {
        this.totalTests = totalTests;
    }

    public int getExcellent() {
        return excellent;
    }

    public void setExcellent(int excellent) {
        this.excellent = excellent;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }

    public int getNeedsImprovement() {
        return needsImprovement;
    }

    public void setNeedsImprovement(int needsImprovement) {
        this.needsImprovement = needsImprovement;
    }

    public double getExcellentPercentage() {

        if (totalTests == 0)
            return 0;

        return (excellent * 100.0) / totalTests;

    }

}