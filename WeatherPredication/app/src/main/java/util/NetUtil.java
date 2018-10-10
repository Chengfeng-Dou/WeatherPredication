package util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by douchengfeng on 2018/10/9.
 *
 */

public class NetUtil {
    private enum NetState {
        NONE, WIFI, MOBILE
    }

    public static void toastNetworkState(Context context){
        Toast.makeText(context, getNetworkState(context).name(), Toast.LENGTH_LONG).show();

    }

    private static NetState getNetworkState(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if(networkInfo == null){
            return NetState.NONE;
        }

        switch (networkInfo.getType()){
            case ConnectivityManager.TYPE_MOBILE:
                return NetState.MOBILE;
            case ConnectivityManager.TYPE_WIFI:
                return NetState.WIFI;
        }

        return NetState.NONE;
    }

    public static boolean netWorkIsOk(Context context){
        NetState state = getNetworkState(context);
        return state == NetState.MOBILE || state == NetState.WIFI;
    }

    public static void getDataFromUrl(String address, int msgFlag, Handler msgHandler){
        UrlRequestTask.getDateFromUrl(address, msgFlag, msgHandler);
    }

}
