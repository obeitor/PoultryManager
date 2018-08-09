package com.softobt.mainapplication;

import android.app.Application;
import java.text.DecimalFormat;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Abdulgafar Obeitor on 6/5/2017.
 */

public class PoultryApplication extends Application {
    public static String FIRST_TIME = "firsttime";
    public static String FARM_PREF = "farm_pref";
    public static String FARM_CODE = "fcode";
    public static String FARM_NAME = "fname";

    public static String CURRENT_FARM_CODE = null;

    public static final int HOME = 0;
    public static final int BIRDS = 1;
    public static final int EGGS = 2;
    public static final int FINANCE = 3;
    public static final int MEDICATION = 4;
    public static int CURRENT_PAGE = HOME;

    private RequestQueue requestQueue;
    private static PoultryApplication instance;
    public static final String TAG = PoultryApplication.class.getSimpleName();
    public static String currency = "₦";
    public static final String formatCash(double cash){
        DecimalFormat format = new DecimalFormat("#.##");
        String retCase = currency;
        String t = "";
        if(cash/1000000000 >= 1){
            cash/=1000000000d;
            t="B";
        }
        else if(cash/1000000 >= 0.1){
            cash/=1000000d;
            t="M";
        }
        retCase+=format.format(cash)+t;
        return retCase;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this.getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("poultryapp.realm")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        instance = this;
    }
    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }
    public static synchronized PoultryApplication getInstance(){
        return instance;
    }
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
