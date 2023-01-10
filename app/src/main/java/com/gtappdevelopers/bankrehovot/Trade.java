package com.gtappdevelopers.bankrehovot;

import java.util.ArrayList;
import java.util.Date;

public class Trade {
    public String date;
    public String stockName;
    public Boolean longShort;//long is true, short is false
    public Double startPrice;
    public Double currentPrice;
    public Double amountInvested;
    public Double stopLoss; //if negative then there is no stop loss
    public Double limitProfit; //if negative then there is no limit stop
    public Double totalProfitLoss;
    public Double percentProfitLoss;
    public boolean openClose; //true is active trade, false is closed trade
    public long updateTime;

    public Trade(String date1, String stockName1, Double startPrice1, Double currentPrice1,
                 Double amountInvested1, Double stopLoss1, Double limitProfit1, Boolean longShort1) {
        updateTime = System.currentTimeMillis();
        date = date1;
        stockName = stockName1;
        startPrice = startPrice1;
        currentPrice = currentPrice1;
        amountInvested = amountInvested1;
        stopLoss = stopLoss1;
        limitProfit = limitProfit1;
        longShort = longShort1;
        totalProfitLoss = 0.0;
        percentProfitLoss = 0.0;
        openClose = true;
    }

    public void updateTrade() {
        updateTime = System.currentTimeMillis();
        int n = 0;
        for (StockModel s1 : MainActivity.stockModels) {
            if (s1.name.equals(this.stockName)) {
                break;
            }
            n++;
        }
        StockModel s1 = MainActivity.stockModels.get(n);
        currentPrice = s1.priceList.get(0);
        //assuming prices have updates and now i check if limit or stop loss got hit
        //also update profit and user balance
        int indexTaker = -1;
        for (int i = 0; i < s1.priceList.size(); i++) { // stops got hit somewhere
            if (s1.priceList.get(i) <= stopLoss || s1.priceList.get(i) >= limitProfit) {
                indexTaker = i;
                break;
            }
        }
        if (indexTaker == -1) { //meaning stops did not get hit
            currentPrice = s1.priceList.get(indexTaker);
            profitLossCalculator();
            openClose = false;

        } else {
            profitLossCalculator();

        }


        //if no one was hit then update user balance and trade profit accordingly and update on firebase

    }

    public void profitLossCalculator() {
        //gives -+16.66% percent for example
        if (longShort) {
            this.percentProfitLoss = ((this.currentPrice - this.startPrice) / this.startPrice) * 100;
            this.totalProfitLoss = (amountInvested + (percentProfitLoss / 100) * amountInvested) - amountInvested;
        } else {
            this.percentProfitLoss = -1 * (((this.currentPrice - this.startPrice) / this.startPrice) * 100);
            this.totalProfitLoss = -1 * ((amountInvested + (percentProfitLoss / 100) * amountInvested) - amountInvested);

        }

    }


    public static String FormalPerc(double num) {
        if (num == 0) {
            return "+0.00";
        }
        String str = "";
        if (num > 0) {
            str += "+";
        }
        if (num < 0) {
            str += "-";
            num *= -1;
        }


        int temp = (int) (num * 100);
        temp = temp % 100;
        int n = (int) num;
        int i = 1;
        ArrayList<String> l1 = new ArrayList<String>();
        while (n > 0) {
            if (i == 4) {
                i = 1;
                l1.add(",");
            }
            l1.add("" + (n % 10));
            n /= 10;
            i++;
        }

        if (num < 1) {
            str += "0";
        }
        for (i = l1.size() - 1; i > -1; i--) {
            str += (l1.get(i));
        }

        str += ".";
        int temp2 = temp % 10;
        temp /= 10;
        str += "" + temp % 10;
        str += "" + temp2;


        return str;
    }

    public static String FormalNum(double num) {
        //doesnt work with negative numbers and doesnt show + / - becasue account balance should never reach a negative number
        if (num == 0) {
            return "0.00";
        }

        int temp = (int) (num * 100);
        temp = temp % 100;
        int n = (int) num;
        int i = 1;
        ArrayList<String> l1 = new ArrayList<String>();
        while (n > 0) {
            if (i == 4) {
                i = 1;
                l1.add(",");
            }
            l1.add("" + (n % 10));
            n /= 10;
            i++;
        }
        String str = "";
        if (num < 1) {
            str += "0";
        }
        for (i = l1.size() - 1; i > -1; i--) {
            str += (l1.get(i));
        }

        str += ".";
        int temp2 = temp % 10;
        temp /= 10;
        str += "" + temp % 10;
        str += "" + temp2;


        return str;
    }


}
