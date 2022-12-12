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
    public Double stopLoss;
    public Double limitProfit;
    public Double totalProfitLoss;
    public Double percentProfitLoss;

    public Trade(String date1, String stockName1, Double startPrice1, Double currentPrice1,
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
        percentProfitLoss = 0.0;
    }


    public void profitLossCalculator() {
        //gives -+16.66% percent for example
        this.percentProfitLoss = ((this.currentPrice - this.startPrice) / this.startPrice) * 100;


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
