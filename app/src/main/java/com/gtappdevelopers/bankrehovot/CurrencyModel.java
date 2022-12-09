package com.gtappdevelopers.bankrehovot;

import java.util.ArrayList;
import java.util.Date;

public class CurrencyModel {
    public String name;
    public Date firstPriceDate; //gives the date according to the first price we took
    public ArrayList<Double> priceList;
    public ArrayList<Date> dateList; //for each price it will save the date

    public CurrencyModel(String name1, Date firstPriceDate1, ArrayList<Double> priceList1, ArrayList<Date> dateList1) {
        name = name1;
        firstPriceDate = firstPriceDate1;
        priceList = removeInfiniteNumbers(priceList1);
        dateList = dateList1;
    }

    public ArrayList<Double> removeInfiniteNumbers(ArrayList<Double> priceList1) {
        //make sure the price is small and compact like 302.36 and not 302.363573895
        ArrayList<Double> result = new ArrayList<Double>();

        for (int i = 0; i < priceList1.size(); i++) {
            String temp = priceList1.toString();
            int count = 0;
            int totalCount = 0;
            Boolean afterPoint = false;
            String minimized = "";
            while (count < 2) {

                if (temp.charAt(totalCount) == '.') {
                    minimized += temp.charAt(totalCount);
                    totalCount++;
                    afterPoint = true;
                    continue;
                }

                if (afterPoint && count < 2) {
                    minimized += temp.charAt(totalCount);
                    count++;
                } else {
                    minimized += temp.charAt(totalCount);
                }

                totalCount++;
            }
            Double taker = Double.parseDouble(minimized);
            result.add(taker);
            //adds the minimized number to the list


        }


        return result;
    }


}
