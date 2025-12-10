package com.openticket.admin.dto;

public class AdminAnalyticsDTO {
    public long homepageViews;
    public double successRate;

    public AnalyticsDTO.ChartData traffic; // 每日流量
    public AnalyticsDTO.ChartData transactions; // 每日成功交易筆數
}
