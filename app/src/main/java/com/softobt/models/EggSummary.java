package com.softobt.models;

/**
 * Created by Abdulgafar Obeitor on 6/9/2018.
 */
public class EggSummary {
    private long totalEggs;
    private long totalCracked;
    private long totalSold;
    private long totalSoldCracked;
    private String updated;//date last updated


    public long getTotalCracked() {
        return totalCracked;
    }

    public void setTotalCracked(long totalCracked) {
        this.totalCracked = totalCracked;
    }

    public long getTotalEggs() {
        return totalEggs;
    }

    public void setTotalEggs(long totalEggs) {
        this.totalEggs = totalEggs;
    }

    public long getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(long totalSold) {
        this.totalSold = totalSold;
    }

    public long getTotalSoldCracked() {
        return totalSoldCracked;
    }

    public void setTotalSoldCracked(long totalSoldCracked) {
        this.totalSoldCracked = totalSoldCracked;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
