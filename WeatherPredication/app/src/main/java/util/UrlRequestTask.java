package util;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by douchengfeng on 2018/10/9.
 *
 */

class UrlRequestTask implements Runnable{
    private String address;
    private int msgFlag;
    private Handler msgHandler;

    static void getDateFromUrl(String address, int msgFlag, Handler msgHandler){
        new Thread(new UrlRequestTask(address, msgFlag, msgHandler)).start();
    }

    private UrlRequestTask(String url, int msgFlag, Handler msgHandler) {
        this.msgFlag = msgFlag;
        this.address = url;
        this.msgHandler = msgHandler;
    }

    @Override
    public void run() {
        String response = connectAndGetResponse();
        if(response != null){
            handleResponse(response);
        }
    }

    private String connectAndGetResponse(){
        String response = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(4000);
            connection.setReadTimeout(4000);

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String str;
            StringBuilder builder = new StringBuilder();
            while((str = reader.readLine()) != null){
                builder.append(str);
            }
            response = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }

        return response;
    }

    private void handleResponse(String response) {
        Message message = new Message();
        message.what = msgFlag;
        message.obj = response;
        msgHandler.sendMessage(message);
    }
}
