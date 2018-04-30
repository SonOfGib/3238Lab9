package edu.temple.stockviewer;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

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

public class StockService extends Service {

    public static final String ACTION_STOCK_UPDATED = "edu.temple.stockviewer.ACTION_STOCK_UPDATED";
    HandlerThread thread;
    Handler mHandler;
    ArrayList<String> stocks = new ArrayList<>();
    BroadcastReceiver receiver;

    @Override
    public void onCreate() {
        thread = new HandlerThread("UpdateStocksThread");
        thread.start();
        mHandler = new Handler(thread.getLooper());
        mHandler.post(new StockRunnable(true));
        final IntentFilter filter = new IntentFilter();
        filter.addAction(SearchStocksActivity.ACTION_STOCK_ADD);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new Thread(){
                    @Override
                    public void run() {
                        new StockRunnable(false).run();
                    }
                }.start();
            }
        };
        // Registers the receiver so that your service will listen
        this.registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Do not forget to unregister the receiver!!!
        this.unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    private class StockRunnable implements Runnable {
        private boolean repeat = false;
        StockRunnable(boolean repeat){
            this.repeat = repeat;
        }
        public void run(){
            findStocks();
            for(String symbol: stocks){
                //first get the info for this symbol.
                findStockInfo(symbol);
                //next get the 1d stock image for this symbol.
                findStockChart(symbol);
                Log.d("StockServiceUpdate","Updated stock: " + symbol);
            }
            Intent intent = new Intent();
            //notify that we have addded a new stock.
            intent.setAction(ACTION_STOCK_UPDATED);
            getApplicationContext().sendBroadcast(intent);
            //run every 60 seconds
            if(repeat)
                mHandler.postDelayed(this, 60 * 1000);
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
            //using the iextrading api instead of the markitondemand one because the latter is broken for many symbols.
            URL url = new URL("https://api.iextrading.com/1.0/stock/"+symbol+"/batch?types=quote&range=1d&last=1");
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
