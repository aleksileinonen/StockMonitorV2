package com.example.stockmonitorv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    ListView stockList;
    EditText stockNameText;
    EditText stockID;
    ArrayList<String> stockDataList;
    Button stockButton;
    String stockNameString;
    String stockIDString;
    double newStock;
    String stockName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stockList = findViewById(R.id.fetchedData);
        stockNameText = findViewById(R.id.stockName);
        stockID = findViewById(R.id.stockID);
        stockButton = findViewById(R.id.stockButton);

        FetchDataPrices task = new FetchDataPrices();
        task.execute();
    }

public class FetchDataPrices extends AsyncTask<String, String, String> {

        @Override
        public String doInBackground(String... strings) {
            final String data = loadFromWeb("https://financialmodelingprep.com/api/company/price/AAPL,INTC,IBM,GOOGL,FB,NOK,RHT,MSFT,AMZN,BRK-B,BABA,JNJ,JPM,XOM,BAC,WMT,WFC,RDS-B,V,PG,BUD,T,TWX,CVX,UNH?datatype=json");
            if (data != null) {
                stockDataList = parseStockData(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, stockDataList);
                        stockList.setAdapter(arrayAdapter);
                        stockButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                stockNameString = stockNameText.getText().toString();
                                stockIDString = stockID.getText().toString();
                                newStock = parseStockDataUser(data);
                                if (stockNameText.getText().toString().trim().equals("")) {
                                    Toast.makeText(MainActivity.this, "ADD STOCK NAME", Toast.LENGTH_SHORT).show();
                                } else if (stockID.getText().toString().trim().equals("")) {
                                    Toast.makeText(MainActivity.this, "ADD STOCK ID", Toast.LENGTH_SHORT).show();
                                }
                                    for (int i = 0; i < parseStockDataUser(data); i++) {
                                        if (stockID.getText().toString().equals(stockIDString)) {
                                            Toast.makeText(MainActivity.this, "STOCK " + stockNameString + " ADDED SUCCESFULLY", Toast.LENGTH_SHORT).show();
                                            stockDataList.add(" " + stockNameString + ": " + newStock + " USD");
                                            break;
                                        }
                                        else {
                                            Toast.makeText(MainActivity.this, "STOCK NOT FOUND", Toast.LENGTH_SHORT).show();
                                        }
                                 }
                            }
                        });

                    }
                });
            }
            return data;
        }

        private ArrayList<String> parseStockData(String data) {
            stockDataList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(data);
                Iterator<String> it = jsonObject.keys();
                int i = 0;
                while (it.hasNext()) {
                    stockName = it.next();
                    if (jsonObject.get(stockName) instanceof JSONObject) {
                        JSONObject stock = jsonObject.getJSONObject(stockName);
                        double stockPrice = stock.getDouble("price");
                        stockDataList.add(" " + stockName + ": " + stockPrice + " USD");
                        i++;
                        if(i > 6){
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stockDataList;
        }
        private double parseStockDataUser(String data){
            double newStock = 0;
            try{
                JSONObject jsonObject2 = new JSONObject(data);
                JSONObject added = jsonObject2.getJSONObject(stockIDString);
                newStock = added.getDouble("price");
            }   catch (JSONException e) {
                e.printStackTrace();
            }   return newStock;
    }

        public String loadFromWeb(String urlString) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(connection.getInputStream());
                String htmlText = FetchData.fromStream(in);
                return htmlText;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
