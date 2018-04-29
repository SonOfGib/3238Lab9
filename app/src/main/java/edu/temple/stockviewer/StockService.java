package edu.temple.stockviewer;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class StockService extends IntentService {

    HandlerThread thread;
    Handler mHandler;
    ArrayList<String> stocks = new ArrayList<>();
    public StockService(){
        super("stockService");
        //make a handler thread that runs our update code.
        thread = new HandlerThread("UpdateStocksThread");
        thread.start();
        mHandler = new Handler(thread.getLooper());
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        //run every 60 seconds
        mHandler.postDelayed(StockRunnable, 60 * 1000);

    }
    final Runnable StockRunnable = new Runnable(){
        public void run(){
            findStocks();
            for(String symbol: stocks){
                //first get the info for this symbol.
                findStockInfo(symbol);
                //next get the 1d stock image for this symbol.
                findStockChart(symbol);
                Log.d("StockServiceUpdate","Updated stock: " + symbol);
            }
            //run every 60 seconds
            mHandler.postDelayed(StockRunnable, 60 * 1000);
        }
    };



    private void findStocks(){
        File myDirectory = getFilesDir();
        File[] directories = myDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        for(File dir: directories){
            if(!stocks.contains(dir.getName())){
                stocks.add(dir.getName());
            }
        }
    }

    private void findStockInfo(String symbol){
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String infoResponse = null;
        try {
            URL url = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + symbol);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            String storagePath = getFilesDir().getAbsolutePath() + File.separator + symbol;
            try (OutputStream output = new FileOutputStream(storagePath +"/details.txt")) {
                byte[] bytes = new byte[512];
                int bytesRead = 0;
                while ((bytesRead = stream.read(bytes, 0, bytes.length)) >= 0) {
                    output.write(bytes, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    private void findStockChart(String symbol) {
        try {
            URL url = new URL("https://www.google.com/finance/chart?q="+symbol+"&p=1d");
            try (InputStream input = url.openStream()) {
                String storagePath = getFilesDir().getAbsolutePath() + File.separator + symbol;
                try (OutputStream output = new FileOutputStream(storagePath + "/chart.gif")) {
                    byte[] bytes = new byte[512];
                    int bytesRead = 0;
                    while ((bytesRead = input.read(bytes, 0, bytes.length)) >= 0) {
                        output.write(bytes, 0, bytesRead);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
