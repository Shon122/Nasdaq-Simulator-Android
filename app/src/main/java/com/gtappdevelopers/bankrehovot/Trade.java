package com.gtappdevelopers.bankrehovot;

import java.util.Date;

public class Trade {
    public Date date;
    public String stockName;
    public Boolean longShort;//long is true, short is false
    public Double startPrice;
    public Double currentPrice;
    public Double amountInvested;
    public Double stopLoss;
    public Double limitProfit;
    public Double totalProfitLoss;
    public Double percentProfitLoss;

    public Trade(Date date1, String stockName1, Double startPrice1, Double currentPrice1,
                 Double amountInvested1, Double stopLoss1, Double limitProfit1, Boolean longShort1) {
        date = date1;
        stockName = stockName1;
        startPrice = startPrice1;
        currentPrice = currentPrice1;
        amountInvested = amountInvested1;
        stopLoss = stopLoss1;
        limitProfit = limitProfit1;
        longShort = longShort1;
        totalProfitLoss = 0.0;
        percentProfitLoss=0.0;
    }

    public void profitLossCalculator() {
//need to show percent and the normal one



    }

}
