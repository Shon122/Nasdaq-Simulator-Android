package com.gtappdevelopers.bankrehovot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {
    //user essentials saving his data
    public static boolean acceptedPerms;
    public static StockModel viewingStock;
    public static Trade viewingTrade;
    public static User viewingUser;
    public static String news = "";
    public static String username = "";
    public static String password = "";
    public static int backToUsers = 0;//if zero you came from homepage and if one you came from userlist
    public static ArrayList<Trade> trades = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<>();
    public static User currentUser;
    public static int currentUserIndex = 0;
    private FirebaseFirestore db;
    Map<String, Object> docData;

    ///////////////////////
    public static int dummy = 0;
    public Long currentTime;
    public String[] apiList;
    public int apiIndex;
    public String[] allNames;
    public static ArrayList<StockModel> stockModels;
    int stockModelIndex;
    String apiLink;
    public Context mContext;
    String result;
    String timeInterval;
    String currentStockName;
    StockModel saveCurrentStockModel;
    String allInfo;
    String allUserInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File sharedPreferencesDirectory = new File(getApplicationInfo().dataDir + "/shared_prefs/");
        File[] sharedPreferencesFiles = sharedPreferencesDirectory.listFiles();

        if (sharedPreferencesFiles != null) {
            for (File sharedPreferencesFile : sharedPreferencesFiles) {
                sharedPreferencesFile.delete();
            }
        }
        acceptedPerms=false;

        users = new ArrayList<>();
        allUserInfo = "";
        allInfo = "";
        timeInterval = "";
        result = "";
        stockModelIndex = 0;
        mContext = this;
        db = FirebaseFirestore.getInstance();
        docData = new HashMap<>();
        updateApiIndexFirebase();

        allNames = new String[]{

                "ABNB", "ADBE"
                , "ADI", "ADP", "AEP", "ALGN", "AMD", "AMGN",
                "AMZN", "AAPL", "ATVI", "AUDNOK", "AUDPLN", "BNBUSD", "BTCUSD",
                "CADBRL", "CADZAR", "CHFJPY", "ETHUSD", "EURBRL", "EURUSD", "GILD",
                "GOOGL", "IBM", "INTC", "KO", "LTCUSD", "META",
                "MMM", "MSFT", "NKE", "NFLX", "NZDCZK", "NZDTRY", "PYPL",
                "SOLUSD", "TSLA", "TRYDKK", "USDJPY", "WMT", "XRPUSD"
        };


        currentTime = System.currentTimeMillis();
        apiList = new String[]{
                "c42711901b00e79841bb71702345719e",
                "262483bd904a81b091b2e27cbcfc0655",
                "5e4573cba51e730e43abbfdf9ed9b975",
                "cccd134cea3374b1ec72c38c08c0b0b0",
                "5aa107ade26d9f4076b8d60f0020d49b",
                "19ba45233fe86671685bdf936a24b931",
                "f44e40de3e5c0c5b6ec60df0730c10d6",
                "21161f524ff705577169d62f61047ff7",
                "c0c89ef92565d4cf7c7ffa0a013e7313",
                "f3366d9120bda80407791f48106ec000",
                "f5eca2647dfd717ee3c6541b48950600",
                "f47d87d5284e9b73dfe85379526ba0c9",
                "9a6a270b61f40c0e58d160cbb1c57131",
                "02d49e539ff86d6fa9aa0f549efc93a3",
                "b050b1fd76d5fb561c1fa00deeeea4d5",
                "8d30c21d048073535bd26cefff977b0c",
                "e3d886b55e36cf01a11c0c15b62353eb",
                "8902fd4b5be3cdf6f457d9404d52c16a",
                "07fef516f7504f5b2781c5c58b75a63d",
                "af6063b9faed6beb8bb0fec11951feaa",
                "03e7915a807c174eabdc070225bd7997",
                "97a895c7e212f394201ce0b775894703",
                "f46c28ada7aa5eb18007371f7c19bd41",
                "1abbcbbb11ed8fbb27bc3d71e698b76d",
                "b384bd91fb7fcb3a9657beac393cc9db",
                "7ddd34443e97cc9ad5d4e6fe6d2d5502",
                "c5492f9f3334045292514ace4409bf35",
                "9826c9f4968f4726aff2e843db493424",
                "2ccc83581cab1580034bd1d43219f421",
                "36c1d526b5750ae07a2de23109bedcda",
                "dda76b57b63a5a86e18a5e94e619a370",
                "0f569e49ec24d01e6abef9bd7f3aa3d2",
                "9b5236ded34b6ec08e2baf996f2e2604"


        };
        apiLink = "";
        stockModels = new ArrayList<>();
        for (int i = 0; i < allNames.length; i++) {
            stockModels.add(new StockModel(allNames[i], new ArrayList<>(), new ArrayList<>(), "none"));
        }
        /////////////////////////////////////


        news = readFromFile("saveNews.txt");
        try {
            firstLoadAll();
        } catch (ExecutionException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
        if (users.size() > 0)
            viewingUser = users.get(0);
        // viewingStock = stockModels.get(0);
        switchIntent();
        //end of oncreate
    }

    public void switchIntent() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public String readFromFile(String fileName) {
        StringBuilder returnString = new StringBuilder();
        try {
            InputStream inputStream = new FileInputStream(fileName);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                returnString.append(stringBuilder.toString());
            }
        } catch (IOException ignored) {
        }
        return returnString.toString();
    }

    public void firstLoadAll() throws ExecutionException, InterruptedException, ParseException {
        // getStockModelsFromFirebase();
        long currentTime = System.currentTimeMillis();
        long lastUpdate = 0;
        //get lastUpdate data from firebase
        db.collection("Trades").document("stockInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();


                    String uploaderTaker = (document.get("lastUpdateStocks").toString());
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("stockLastUpdate", uploaderTaker);
                    myEdit.apply();

                }
            }
        });
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String dataTaker = sharedPreferences.getString("stockLastUpdate", "5");
        lastUpdate = Long.parseLong((dataTaker));
        long diff = (lastUpdate - currentTime); // 1 min = 60000 ms
        //make sure more than 1 day has passed since last call
        if (diff > 1909600000)//more thank two weeks
        {
            getAllStockModels("4hour");
            uploadStockModelsFirebase();
        } else {
            getStockModelsFromFirebase();

        }
        updateNews();


        //here i get existing users from firebase
        users = new ArrayList<>();
        db.collection("Trades").document("Users").collection("usersAll")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                users.add(user);
                            }
                        }
                        for (int i = 0; i < users.size(); i++) {
                            double totalpnl = 0;
                            for (int j = 0; j < users.get(i).trades.size(); j++) {
                                try {
                                    totalpnl += users.get(i).trades.get(j).totalProfitLoss;
                                    users.get(i).trades.get(j).updateTrade();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            //now totalpnl + other pnl from stock game calculations
                            double otherpnl = 0;
                            otherpnl = -1 * (users.get(i).startingBalance - users.get(i).balance) - totalpnl;

                            Double temp = users.get(i).startingBalance;
                            for (int j = 0; j < users.get(i).trades.size(); j++) {
                                //   if(!users.get(i).trades.get(j).openClose)
                                temp += users.get(i).trades.get(j).totalProfitLoss;
                            }
                            users.get(i).balance = temp + otherpnl;
                        }

                        uploadUsersToFirestore();

                    }
                });


    }


    public static void uploadUsersToFirestore() {
        for (User user : users) {

            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
            db1.collection("Trades").document("Users").collection("usersAll").document(user.username).set(user);
        }
    }


    public void uploadStockModelsFirebase() {

        for (StockModel stock : stockModels) {

            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
            db1.collection("Trades").document("stockInfo").collection("allStocks").document(stock.name).set(stock);
        }
        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
        HashMap<String, Object> updateValues = new HashMap<>();
        updateValues.put("lastUpdateStocks", System.currentTimeMillis());
        db1.collection("Trades").document("stockInfo").set(updateValues);

    }

    public void getStockModelsFromFirebase() {

        SharedPreferences sharedPref = getSharedPreferences("stockPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("stockModels", null);
        editor.apply();
        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        CollectionReference stockRef = db2.collection("Trades").document("stockInfo").collection("allStocks");

        stockRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        StockModel stockModel = document.toObject(StockModel.class);
                        stockModels.add(stockModel);

                        SharedPreferences sharedPref = getSharedPreferences("stockPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(stockModels);
                        editor.putString("stockModels", json);
                        editor.apply();
                    }
                }
            }
        });


        sharedPref = getSharedPreferences("stockPref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString("stockModels", null);
        Type type = new TypeToken<ArrayList<StockModel>>() {
        }.getType();
        ArrayList<StockModel> stockModels = gson.fromJson(json, type);

        if (stockModels == null) {
            MainActivity.stockModels = new ArrayList<StockModel>();
        } else
            MainActivity.stockModels = stockModels;

    }

    public void getAllStockModels(String timeinterval1) {
        //puts all the information to the var stockModels
        db.collection("Trades").document("indexapi").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String uploaderTaker = String.valueOf(document.get("indexnumber"));
                    apiIndex = Integer.parseInt((uploaderTaker));

                    timeInterval = timeinterval1;
                    for (int i = 0; i < allNames.length; i++) {
                        currentStockName = allNames[i];
                        if (timeInterval.equals("day")) {
                            apiLink = "https://financialmodelingprep.com/api/v3/historical-price-full/" + currentStockName + "?serietype=line&apikey=" + apiList[apiIndex];
                        } else {
                            apiLink = "https://financialmodelingprep.com/api/v3/historical-chart/" + timeInterval + "/" + currentStockName + "?apikey=" + apiList[apiIndex];
                        }
                        GetDataTask task1 = new GetDataTask();
                        try {
                            task1.execute().get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        apiIndex++;
                        if (apiIndex >= apiList.length)
                            apiIndex = 0;
                        stockModels.set(i, saveCurrentStockModel);
                    }
                    uploadApiIndexFirebase();
                    uploadStockModelsFirebase();
                }

            }
        });

    }

    public void updateApiIndexFirebase() {
        db.collection("Trades").document("indexapi").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String uploaderTaker = String.valueOf(document.get("indexnumber"));
                    apiIndex = Integer.parseInt((uploaderTaker));
                }
            }
        });

    }

    public void uploadApiIndexFirebase() {
        if (apiIndex >= apiList.length)
            apiIndex = 0;
        docData.put("indexnumber", apiIndex);
        db.collection("Trades").document("indexapi").set(docData, SetOptions.merge());
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(apiLink).build();
            try {
                Response response = client.newCall(request).execute();
                String responseString = Objects.requireNonNull(response.body()).string();
                ArrayList<Double> priceList1 = extractPrices(responseString); //extract close values
                ArrayList<String> dateList1 = extractDates(responseString);
                saveCurrentStockModel = new StockModel(currentStockName, priceList1, dateList1, timeInterval);
            } catch (IOException | ParseException | JSONException ignored) {
            }
            return "";
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
        }
    }

    private class GetDataTaskNews extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(apiLink).build();
            try {
                Response response = client.newCall(request).execute();
                String responseString = Objects.requireNonNull(response.body()).string();
                //after i get string upload to firebase
                responseString = responseString.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
                responseString = responseString.replaceAll("\\r\\n|\\r|\\n", " ");
                String temp = String.valueOf((char) 92);
                responseString = responseString.replace(temp + "n", "");
                //now i just filter so it will only show content
                ArrayList<String> allContent = new ArrayList<>();
                int contentIndex = responseString.indexOf((String.valueOf('"') + "content" + String.valueOf('"')), 25); // searching for " ("content")"
                while (contentIndex != -1) {
                    String takePart = responseString.substring(contentIndex + 14, (responseString.indexOf("tickers", contentIndex + 9)) - 10);
                    allContent.add(takePart);

                    contentIndex = responseString.indexOf(String.valueOf('"') + "content" + String.valueOf('"'), contentIndex + 15);
                }
                responseString = "";
                for (int i = 0; i < allContent.size(); i++) {
                    responseString += "" + allContent.get(i) + ". ";

                }
                news = responseString;
            } catch (IOException ignored) {
            }
            return "";
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
        }
    }

    public ArrayList<Double> extractPrices(String info) throws JSONException {
        if (timeInterval.equals("day")) {
            ArrayList<Double> priceList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(info);
            JSONArray jsonArray = jsonObject.getJSONArray("historical");
            for (int i = 0; i < jsonArray.length(); i++) {
                if (priceList.size() >= 70) {
                    break;
                }
                JSONObject historicalObject = jsonArray.getJSONObject(i);
                priceList.add(Double.valueOf(removeInfiniteNumbers(String.valueOf(historicalObject.getDouble("close")))));
            }
            return priceList;
        } else {
            ArrayList<Double> priceList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(info);
            for (int i = 0; i < jsonArray.length(); i++) {
                if (priceList.size() >= 70) {
                    break;
                }
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                priceList.add(Double.valueOf(removeInfiniteNumbers(String.valueOf(jsonObject.getDouble("close")))));
            }
            return priceList;
        }
    }

    public ArrayList<String> extractDates(String info) throws ParseException, JSONException {
        if (timeInterval.equals("day")) {
            ArrayList<String> dateList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(info);
            JSONArray jsonArray = jsonObject.getJSONArray("historical");
            for (int i = 0; i < jsonArray.length(); i++) {
                if (dateList.size() >= 70) {
                    break;
                }
                JSONObject historicalObject = jsonArray.getJSONObject(i);
                dateList.add(historicalObject.getString("date"));
            }
            return dateList;
        } else {
            ArrayList<String> dateList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(info);
            for (int i = 0; i < jsonArray.length(); i++) {
                if (dateList.size() >= 70) {
                    break;
                }
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                dateList.add(jsonObject.getString("date"));
            }
            return dateList;
        }

    }

    public String removeInfiniteNumbers(String price) {
        double doublePrice = Double.parseDouble(price);
        String roundedPrice = String.format("%.5f", doublePrice);
        return roundedPrice;
    }

    public void updateNews() {
        //long lastUpdate = 0;
        ////get lastUpdate data from firebase
//        db.collection("Trades").document("newsAll").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//
//
//                    String uploaderTaker = (document.get("lastNewsUpdate").toString());
//                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
//                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
//                    myEdit.putString("dataLastUpdate", uploaderTaker);
//                    myEdit.apply();
//
//                }
//            }
//        });
//        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
//        String dataTaker = sharedPreferences.getString("dataLastUpdate", "5");
//        lastUpdate = Long.parseLong((dataTaker));
//        int diff = (int) (currentTime - lastUpdate); // 1 min = 60000 ms
//        //make sure more than 1 day has passed since last call


        apiLink = "https://financialmodelingprep.com/api/v3/fmp/articles?page=0&size=5&apikey=" + apiList[apiIndex];

        GetDataTaskNews task = new GetDataTaskNews();
        try {
            task.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        apiIndex++;
        if (apiIndex > apiList.length)
            apiIndex = 0;
        docData.put("lastNewsUpdate", currentTime);
        db.collection("Trades").document("newsAll").set(docData, SetOptions.merge());

        docData.put("news", news);
        db.collection("Trades").document("newsAll").set(docData, SetOptions.merge());

        db.collection("Trades").document("newsAll").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    news = (document.get("news").toString());
                }
            }
        });


    }


    //end of main
}
