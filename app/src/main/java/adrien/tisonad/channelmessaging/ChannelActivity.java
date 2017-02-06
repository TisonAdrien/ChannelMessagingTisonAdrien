package adrien.tisonad.channelmessaging;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.HashMap;

public class ChannelActivity extends AppCompatActivity {
    private ListView messages;
    private EditText myMessage;
    private Button btnSend;
    static HashMap<String,Bitmap> images = new HashMap<String,Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        messages = (ListView) findViewById(R.id.listViewMessages);
        myMessage = (EditText) findViewById(R.id.editTextMessage);
        btnSend = (Button) findViewById(R.id.buttonEnvoyer);

        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String accesstoken = settings.getString("accesstoken","");

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("accesstoken",accesstoken);
                params.put("channelid", getIntent().getStringExtra("channelid"));
                Downloader connexion = new Downloader(getApplicationContext(), params, "http://www.raphaelbischof.fr/messaging/?function=getmessages");
                connexion.execute();

                connexion.setListener(new OnDownloadCompleteListener() {
                    @Override
                    public void onDownloadComplete(String content) {
                        Gson gson = new Gson();
                        MessageContainer obj = gson.fromJson(content, MessageContainer.class);
                        Collections.reverse(obj.getMessages());
                        messages.setAdapter((new MessageListAdapter(getApplicationContext(), obj.getMessages())));
                    }
                });

                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(r, 1000);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String accesstoken = settings.getString("accesstoken","");

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("accesstoken",accesstoken);
                params.put("channelid",getIntent().getStringExtra("channelid"));
                params.put("message",myMessage.getText().toString());

                myMessage.setText("");
                Downloader connexion = new Downloader(getApplicationContext(), params, " http://www.raphaelbischof.fr/messaging/?function=sendmessage");

                connexion.setListener(new  OnDownloadCompleteListener() {
                    @Override
                    public void onDownloadComplete(String content) {
                        Gson gson = new Gson();
                        ConnectionReturn obj = gson.fromJson(content, ConnectionReturn.class);

                        if(!obj.getResponse().equals("Message envoyé au channel")){
                            Toast.makeText(getApplicationContext(), "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                connexion.execute();
            }
        });

        messages.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Message message = (Message) messages.getItemAtPosition(position);

                SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String username = settings.getString("username","");

                if(!message.getUsername().equals(username)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChannelActivity.this);
                    builder.setTitle("Ajouter un ami");
                    builder.setMessage("Voulez-vous vraiment ajouter cet utilisateur à votre liste d'amis ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //System.out.println(message.getImageUrl().toString());
                                    if(isExternalStorageWritable())
                                        System.out.println("SD OK");
                                    UserDataSource data = new UserDataSource(getApplicationContext());
                                    data.open();
                                    data.createFriend(message.getUsername(),message.getImageUrl());
                                    data.close();
                                }
                            })
                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    builder.show();
                }
            }
        });
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
