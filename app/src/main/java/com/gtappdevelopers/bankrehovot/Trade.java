package com.gtappdevelopers.bankrehovot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public boolean orderHold;//true if ordered, false if not ordered
    public Double orderPrice;

    public Trade(String date1, String stockName1, Double startPrice1, Double currentPrice1,
                 Double amountInvested1, Double stopLoss1, Double limitProfit1, Boolean longShort1, Boolean orderHold1, Double orderPrice1) {
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
        orderHold = orderHold1;
        orderPrice = orderPrice1;
    }

    public Trade() {
        // Empty constructor needed for deserialization
    }


    public void updateTrade() throws ParseException {
        if (orderHold) {
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
            int indexTaker = -1;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1;
            Date d2 = sdf.parse(date);
            for (int i = 0; i < s1.priceList.size(); i++) {
                d1 = sdf.parse(s1.dateList.get(i));
                if (d2.before(d1)) {
                    if ((s1.priceList.get(i) > orderPrice && s1.priceList.get(i-1) < orderPrice)||(s1.priceList.get(i) < orderPrice && s1.priceList.get(i-1) > orderPrice) ){
                        indexTaker = i;
                        break;
                    }
                }
            }
            if (indexTaker != -1) {
                startPrice=s1.priceList.get(indexTaker);
                date=s1.dateList.get(indexTaker);
                currentPrice=startPrice;
                orderPrice=-1.0;
                openClose=true;
                orderHold=false;
            }


        } else {
            if (openClose) {
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

                    currentPrice = s1.priceList.get(0);
                    profitLossCalculator();




            }

        }
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
