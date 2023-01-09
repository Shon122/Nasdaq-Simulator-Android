package com.gtappdevelopers.bankrehovot;

import java.util.ArrayList;
import java.util.Date;

public class StockModel {
    public long updateTime;
    public String name;
    public String timeInterval;
    public ArrayList<Double> priceList;
    public ArrayList<String> dateList; //for each price it will save the date
    public String analysis;
    public Double gainLossPercent;

    public StockModel(String name1, ArrayList<Double> priceList1, ArrayList<String> dateList1, String timeInterval1) {
        updateTime=System.currentTimeMillis();
        name = name1;
        priceList = priceList1;
        timeInterval = timeInterval1;
        dateList = dateList1;
        if (priceList1.size() > 1) {
            gainLossPercent = profitLossCalculator(priceList1.get(0), priceList1.get(priceList1.size() - 1));
            gainLossPercent = Double.valueOf(removeInfiniteNumbers(gainLossPercent.toString()));
        } else
            gainLossPercent = 0.0;
        if (priceList1.size() > 1)
            analysis = updateAnalysis(priceList1);
        else
            analysis = "Analysis is currently unavailable...";
    }

    public Double calculateMA(ArrayList<Double> prices, int period) {
        double sum = 0;
        for (int i = prices.size() - period; i < prices.size(); i++) {
            sum += prices.get(i);
        }
        return sum / period;
    }

    public String updateAnalysis(ArrayList<Double> prices) {
        StringBuilder analysis = new StringBuilder();
        // Calculate the average price
        double sum = 0;
        for (double price : prices)
            sum += price;
        double averagePrice = sum / prices.size();
        averagePrice = Double.parseDouble(removeInfiniteNumbers(String.valueOf(averagePrice)));
        // Calculate the minimum and maximum prices
        double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;
        for (double price : prices) {
            if (price < minPrice) {
                minPrice = price;
            }
            if (price > maxPrice) {
                maxPrice = price;
            }
        }
        minPrice = Double.parseDouble(removeInfiniteNumbers(String.valueOf(minPrice)));
        maxPrice = Double.parseDouble(removeInfiniteNumbers(String.valueOf(maxPrice)));
        // Calculate the price range
        double priceRange = maxPrice - minPrice;
        priceRange = Double.parseDouble(removeInfiniteNumbers(String.valueOf(priceRange)));
        // Calculate the standard deviation
        double sumSquaredDifferences = 0;
        for (double price : prices) {
            double difference = price - averagePrice;
            sumSquaredDifferences += difference * difference;
        }
        double variance = sumSquaredDifferences / prices.size();
        double standardDeviation = Math.sqrt(variance);
        standardDeviation = Double.parseDouble(removeInfiniteNumbers(String.valueOf(standardDeviation)));
        //calculate MA's
        double maAll = calculateMA(prices, prices.size());
        maAll = Double.parseDouble(removeInfiniteNumbers(String.valueOf(maAll)));
        double supportLevel = averagePrice - standardDeviation;
        double resistanceLevel = averagePrice + standardDeviation;
        resistanceLevel = Double.parseDouble(removeInfiniteNumbers(String.valueOf(resistanceLevel)));
        supportLevel = Double.parseDouble(removeInfiniteNumbers(String.valueOf(supportLevel)));

        //now put in a string
        //verbal:
        analysis.append("Analysis for "+name+":"+"\n");
        if (standardDeviation > averagePrice * 0.1) {
            analysis.append("The stock has had high volatility.\n");
        } else {
            analysis.append("The stock has had low volatility.\n");
        }
        if (priceRange > (maxPrice - minPrice) * 0.1) {
            analysis.append("The stock has had a significant price range.\n");
        } else {
            analysis.append("The stock has had a stable price range.\n");
        }
        if (averagePrice > maxPrice) {
            analysis.append("The stock has been consistently gaining value.\n");
        } else if (averagePrice < minPrice) {
            analysis.append("The stock has been consistently losing value.\n");
        } else {
            analysis.append("The stock has had mixed performance.\n");
        }
        //numbers:
        analysis.append("All-Time Average: " + maAll + "." + "\n");
        analysis.append("Resistance Level: " + resistanceLevel + "." + "\n");
        analysis.append("Support Level: " + supportLevel + "." + "\n");
        analysis.append("Standard deviation: " + standardDeviation + "." + "\n");
        analysis.append("Price range: " + priceRange + "\n");
        analysis.append("Average price: " + averagePrice + "." + "\n");
        analysis.append("Minimum price: " + minPrice + "\n");
        analysis.append("Maximum price: " + maxPrice + "\n");


        return analysis.toString();
    }

    public Double profitLossCalculator(Double currentPrice, Double startPrice) {
        //gives -+16.66% percent for example
        return (((currentPrice - startPrice) / startPrice) * 100);
    }

    public String removeInfiniteNumbers(String price) {
        //make sure the price is small and compact like 302.3656 and not 302.363573895
        //0.12345678 ---> 0.12345
        //1.12345678 ---> 1.1234
        //12.12345678 --->12.123
        //123.12345678 --->123.123
        //1234.12345678 --->1234.123
        //12345.12345678 --->12345.123

        //first of all cut all the zeros at the end


        int take1 = price.length();
        while (price.charAt(take1 - 1) == '0') {
            price = price.substring(0, price.length() - 1);
            take1 = price.length();
        }
        if (price.charAt(price.length() - 1) == '.') {
            price = price.substring(0, price.length() - 1);
            return price;
        }
        //now dealing with prices who are not "37.00000"

        String beforePoint = price.substring(0, price.indexOf("."));
        String afterPoint = price.substring(price.indexOf(".") + 1);


        if (afterPoint.length() > 5)
            afterPoint = afterPoint.substring(0, 5);
        String result = beforePoint + "." + afterPoint;


        return result;

    }


}
