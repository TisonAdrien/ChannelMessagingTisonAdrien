package adrien.tisonad.channelmessaging;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity implements OnDownloadCompleteListener, View.OnClickListener{

    private EditText identifiant;
    private EditText password;
    private Button boutonValider;
    public static final String PREFS_NAME = "PrefsFiles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        boutonValider = (Button) findViewById(R.id.buttonValider);
        boutonValider.setOnClickListener(this);
        identifiant = (EditText) findViewById(R.id.Identifiant);
        password = (EditText) findViewById(R.id.Password);
    }

    @Override
    public void onClick(View v) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username",identifiant.getText().toString());
        params.put("password",password.getText().toString());

        Downloader connexion = new Downloader(getApplicationContext(), params, "http://www.raphaelbischof.fr/messaging/?function=connect");

        connexion.setListener(new OnDownloadCompleteListener() {
            @Override
            public void onDownloadComplete(String content) {
                Gson gson = new Gson();
                ConnectionReturn obj = gson.fromJson(content, ConnectionReturn.class);

                if(obj.getResponse().equals("Ok")){
                    Toast.makeText(getApplicationContext(), "Connecté", Toast.LENGTH_SHORT).show();

                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("accesstoken", obj.getAccesstoken());

                    editor.commit();

                    Intent myIntent = new Intent(getApplicationContext(),ChannelListActivity.class);
                    startActivity(myIntent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
            }
        });
        connexion.execute();
        Toast.makeText(getApplicationContext(), "Connexion en cours...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDownloadComplete(String content) {
        Gson gson = new Gson();
        ConnectionReturn obj = gson.fromJson(content, ConnectionReturn.class);

        if(obj.getResponse().equals("Ok")){
            Toast.makeText(getApplicationContext(), "Connecté", Toast.LENGTH_SHORT).show();

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("accesstoken", obj.getAccesstoken());

            editor.commit();
        }
        else{
            Toast.makeText(getApplicationContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
        }    }
}
