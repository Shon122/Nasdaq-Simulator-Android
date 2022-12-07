package com.gtappdevelopers.bankrehovot;

import java.sql.Timestamp;

public class Trade {
    public Timestamp date;
    public String stockName;
    public boolean longShort;//long is true, short is false
    public double startPrice;
    public double currentPrice;
    public double amountInvested;
    public double stopLoss;
    public double limitProfit;
    public double totalProfitLoss;
    public double percentProfitLoss;

    public Trade(Timestamp date1, String stockName1, double startPrice1, double currentPrice1,
                 double amountInvested1, double stopLoss1, double limitProfit1, boolean longShort1) {
        date = date1;
        stockName = stockName1;
        startPrice = startPrice1;
        currentPrice = currentPrice1;
        amountInvested = amountInvested1;
        stopLoss = stopLoss1;
        limitProfit = limitProfit1;
        longShort = longShort1;
        totalProfitLoss = 0;
        percentProfitLoss=0;
    }

    public void profitLossCalculator() {
//need to show percent and the normal one



    }

}
