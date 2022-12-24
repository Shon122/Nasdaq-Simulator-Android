package com.gtappdevelopers.bankrehovot;

import java.util.ArrayList;
import java.util.Date;

public class StockModel {
    public String name;
    public String timeInterval;
    public ArrayList<Double> priceList;
    public ArrayList<String> dateList; //for each price it will save the date
    public String analysis;
    public Double gainLossPercent;

    public StockModel(String name1, ArrayList<Double> priceList1, ArrayList<String> dateList1, String timeInterval1) {
        name = name1;
        priceList = priceList1;
        timeInterval = timeInterval1;
        dateList = dateList1;
        if (priceList1.size() > 10) {
            gainLossPercent = profitLossCalculator(priceList1.get(0), priceList1.get(priceList1.size() - 1));
            gainLossPercent = removeInfiniteNumbers(gainLossPercent);
        } else
            gainLossPercent = 0.0;
        if (priceList1.size() > 10)
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
        for (double price : prices) {
            sum += price;
        }
        double averagePrice = sum / prices.size();
        analysis.append("Average price: " + averagePrice + "." + "\n");

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
        analysis.append("Minimum price: " + minPrice + "\n");
        analysis.append("Maximum price: " + maxPrice + "\n");
        // Add verbal analysis of the prices
        if (averagePrice > maxPrice) {
            analysis.append("The stock has been consistently gaining value.\n");
        } else if (averagePrice < minPrice) {
            analysis.append("The stock has been consistently losing value.\n");
        } else {
            analysis.append("The stock has had mixed performance.\n");
        }
        // Calculate the price range
        double priceRange = maxPrice - minPrice;
        analysis.append("Price range: " + priceRange + "\n");
        if (priceRange > (maxPrice - minPrice) * 0.1) {
            analysis.append("The stock has had a significant price range.\n");
        } else {
            analysis.append("The stock has had a stable price range.\n");
        }
        // Calculate the standard deviation
        double sumSquaredDifferences = 0;
        for (double price : prices) {
            double difference = price - averagePrice;
            sumSquaredDifferences += difference * difference;
        }
        double variance = sumSquaredDifferences / prices.size();
        double standardDeviation = Math.sqrt(variance);
        analysis.append("Standard deviation: " + standardDeviation + "." + "\n");
        if (standardDeviation > averagePrice * 0.1) {
            analysis.append("The stock has had high volatility.\n");
        } else {
            analysis.append("The stock has had low volatility.\n");
        }

        //calculate MA's

//        double ma20 = calculateMA(prices, 20);
//        double ma50 = calculateMA(prices, 50);
        double maAll = calculateMA(prices, prices.size());

//        analysis.append("20-day Average: " + ma20 + "." + "\n");
//        analysis.append("50-day Average: " + ma50 + "." + "\n");
        analysis.append("All-Time Average: " + maAll + "." + "\n");

        double supportLevel = averagePrice - standardDeviation;
        double resistanceLevel = averagePrice + standardDeviation;

        analysis.append("Resistance Level: " + resistanceLevel + "." + "\n");
        analysis.append("Support Level: " + supportLevel + "." + "\n");

        return analysis.toString();

    }

    public Double profitLossCalculator(Double currentPrice, Double startPrice) {
        //gives -+16.66% percent for example
        return (((currentPrice - startPrice) / startPrice) * 100);
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

    public Double removeInfiniteNumbers(Double number) {
        String temp = String.valueOf(number);
        int count = 0;
        int totalCount = 0;
        Boolean afterPoint = false;
        String minimized = "";
        while (count < 3 && totalCount < temp.length()) {
            if (temp.charAt(totalCount) == '.') {
                minimized += temp.charAt(totalCount);
                totalCount++;
                afterPoint = true;
                continue;
            }
            if (afterPoint && count < 3) {
                minimized += temp.charAt(totalCount);
                count++;
            } else {
                minimized += temp.charAt(totalCount);
            }
            totalCount++;
        }
        if (minimized.equals(""))
            return 0.0;
        Double taker = Double.parseDouble(minimized);

        return taker;
    }

}
