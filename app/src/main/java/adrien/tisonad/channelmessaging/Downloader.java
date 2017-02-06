package adrien.tisonad.channelmessaging;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.login.LoginException;

/**
 * Created by tisonad on 20/01/2017.
 */
public class Downloader extends AsyncTask<Void,Integer,String>{

    private Context theContext;
    private HashMap<String,String> params = new HashMap<String,String>();
    private String url;
    private ArrayList<OnDownloadCompleteListener> listeners = new ArrayList<>();

    public Downloader(Context theContext, HashMap<String,String> params, String url){
        this.theContext = theContext;
        this.params = params;
        this.url = url;
    }

    public void setListener(OnDownloadCompleteListener listener){
        this.listeners.add(listener);
    }

    public void newsDownloadCompleted(String myNews){
        for(OnDownloadCompleteListener oneListener : listeners)
        {
            oneListener.onDownloadComplete(myNews);
        }
    }

    @Override
    protected String doInBackground(Void... arg0) {
        String connection = this.performPostCall(this.url, this.params);
        return connection;
    }

    @Override
    protected void onPostExecute(String result) {
        for (OnDownloadCompleteListener oneListener : listeners)
        {
            oneListener.onDownloadComplete(result);
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()){
            if (first) first = false;
            else result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    public String performPostCall(String requestURL, HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try
        {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br =new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else
            {
                response ="";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }


}
