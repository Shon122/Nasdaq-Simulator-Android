package com.gtappdevelopers.bankrehovot;

import java.util.ArrayList;
import java.util.Date;

public class StockModel {
    public String name;
    public ArrayList<Double> priceList;
    public ArrayList<String> dateList; //for each price it will save the date
    public String news;
    public String analysis;

    public StockModel(String name1) {
        name = name1;
        priceList = new ArrayList<Double>();
        dateList = new ArrayList<String>();
    }

    public ArrayList<Double> removeInfiniteNumbers(ArrayList<Double> priceList1) {
        //make sure the price is small and compact like 302.3656 and not 302.363573895
        ArrayList<Double> result = new ArrayList<Double>();

        for (int i = 0; i < priceList1.size(); i++) {
            String temp = priceList1.toString();
            int count = 0;
            int totalCount = 0;
            Boolean afterPoint = false;
            String minimized = "";
            while (count < 4) {

                if (temp.charAt(totalCount) == '.') {
                    minimized += temp.charAt(totalCount);
                    totalCount++;
                    afterPoint = true;
                    continue;
                }

                if (afterPoint && count < 4) {
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


    public void extractToArrayPrice(String info) {
        ArrayList<String> prices = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        int dateIndex = info.indexOf("date");
        while (dateIndex != -1) {
            int tempIndex = info.indexOf('"', dateIndex + 1);
            tempIndex = info.indexOf('"', tempIndex + 1);


            dateIndex = info.indexOf("date", dateIndex);
        }


    }

}
