package adrien.tisonad.channelmessaging;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dynamitechetan.flowinggradient.FlowingGradientClass;
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
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import adrien.tisonad.channelmessaging.Fragment.MainActivity;

public class LoginActivity extends AppCompatActivity implements OnDownloadCompleteListener, View.OnClickListener{

    private EditText identifiant;
    private EditText password;
    private Button boutonValider;
    private ImageView logoView;
    public static final String PREFS_NAME = "PrefsFiles";
    public Handler mHandlerTada = new Handler(); // android.os.handler
    public Handler mHandlerText = new Handler();
    public Handler mHandlerTextFin = new Handler();
    int mShortDelay = 4000; //milliseconds
    private static final String[] explainStringArray = {
            "Connecte toi pour chatter avec tes amis",
            "Blablabla",
            "C'est moi Sancho le Cubain !",
            "Voilà..."
    };
    private TextView textAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        boutonValider = (Button) findViewById(R.id.buttonValider);
        boutonValider.setOnClickListener(this);
        identifiant = (EditText) findViewById(R.id.Identifiant);
        password = (EditText) findViewById(R.id.Password);
        logoView = (ImageView) findViewById(R.id.logoView);
        textAnim = (TextView) findViewById(R.id.textView4);

        LinearLayout rl = (LinearLayout) findViewById(R.id.llBackground);
        FlowingGradientClass grad = new FlowingGradientClass();
        grad.setBackgroundResource(R.drawable.translate)
                .onLinearLayout(rl)
                .setTransitionDuration(4000)
                .start();

        mHandlerTada.postDelayed(new Runnable(){
            public void run(){
                YoYo.with(Techniques.Tada)
                        .duration(1200)
                        .playOn(logoView);
                mHandlerTada.postDelayed(this, mShortDelay);
            }
        }, mShortDelay);


        mHandlerText.postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.SlideOutLeft).playOn(textAnim);
                mHandlerTextFin.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String txt = explainStringArray[new Random().nextInt(explainStringArray.length)];
                        textAnim.setText(txt);
                        YoYo.with(Techniques.SlideInRight).playOn(textAnim);
                    }
                },800);

                mHandlerTada.postDelayed(this, 4000);
            }
        }, 4000);


    }

    @Override
    public void onClick(View v) {
        Animation animSlideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        findViewById(R.id.textView4).startAnimation(animSlideLeft);
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
                    Snackbar.make(findViewById(R.id.buttonValider), "Connecté", Snackbar.LENGTH_LONG).show();
                    findViewById(R.id.textView4).clearAnimation();
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("accesstoken", obj.getAccesstoken());

                    editor.commit();


                    Intent myIntent = new Intent(getApplicationContext(),ChannelListActivity.class);
                    startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, logoView, "logo").toBundle());
                    //Intent myIntent = new Intent(getApplicationContext(),MainActivity.class);
                    //startActivity(myIntent);
                }
                else{
                    Snackbar.make(findViewById(R.id.buttonValider), "Erreur de connection", Snackbar.LENGTH_LONG).show();
                    findViewById(R.id.textView4).clearAnimation();
                }
            }
        });
        connexion.execute();
        Snackbar.make(findViewById(R.id.buttonValider), "Connection en cours...", Snackbar.LENGTH_LONG).show();

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
