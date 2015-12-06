package it.jaschke.alexandria.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by alvaro on 2/12/2015.
 */
public  class Utility {


    static public boolean isNetworkAvailable(Context c){
        ConnectivityManager cm =
                (ConnectivityManager)  c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

    }
}
