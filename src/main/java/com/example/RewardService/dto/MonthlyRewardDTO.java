package com.example.RewardService.dto;

/**
 * Reward points earned by a customer for a single calendar month.
 * Month is represented as "yyyy-MM" (e.g. "2024-01").
 */
public class MonthlyRewardDTO {

    private String month;
    private int points;

    public MonthlyRewardDTO() {
    }

    public MonthlyRewardDTO(String month, int points) {
        this.month = month;
        this.points = points;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
