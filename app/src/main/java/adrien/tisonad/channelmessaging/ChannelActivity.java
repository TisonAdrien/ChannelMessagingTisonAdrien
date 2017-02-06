package adrien.tisonad.channelmessaging;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;

public class ChannelActivity extends AppCompatActivity {
    private ListView messages;
    private EditText myMessage;
    private Button btnSend;
    private Button btnImage;
    static HashMap<String, Bitmap> images = new HashMap<String, Bitmap>();
    static int PICTURE_REQUEST_CODE = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        messages = (ListView) findViewById(R.id.listViewMessages);
        myMessage = (EditText) findViewById(R.id.editTextMessage);
        btnSend = (Button) findViewById(R.id.buttonEnvoyer);
        btnImage = (Button) findViewById(R.id.buttonImage);

        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String accesstoken = settings.getString("accesstoken", "");

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("accesstoken", accesstoken);
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



        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String accesstoken = settings.getString("accesstoken", "");
                Toast.makeText(getApplicationContext(), "Click sur image", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI); //Création de l’appel à l’application appareil photo pour récupérer une image
                startActivityForResult(intent, PICTURE_REQUEST_CODE);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String accesstoken = settings.getString("accesstoken", "");

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("accesstoken", accesstoken);
                params.put("channelid", getIntent().getStringExtra("channelid"));
                params.put("message", myMessage.getText().toString());

                myMessage.setText("");
                Downloader connexion = new Downloader(getApplicationContext(), params, " http://www.raphaelbischof.fr/messaging/?function=sendmessage");

                connexion.setListener(new OnDownloadCompleteListener() {
                    @Override
                    public void onDownloadComplete(String content) {
                        Gson gson = new Gson();
                        ConnectionReturn obj = gson.fromJson(content, ConnectionReturn.class);

                        if (!obj.getResponse().equals("Message envoyé au channel")) {
                            Toast.makeText(getApplicationContext(), "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                connexion.execute();
            }
        });

        messages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Message message = (Message) messages.getItemAtPosition(position);

                SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String username = settings.getString("username", "");

                if (!message.getUsername().equals(username)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChannelActivity.this);
                    builder.setTitle("Ajouter un ami");
                    builder.setMessage("Voulez-vous vraiment ajouter cet utilisateur à votre liste d'amis ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //System.out.println(message.getImageUrl().toString());
                                    if (isExternalStorageWritable())
                                        System.out.println("SD OK");
                                    UserDataSource data = new UserDataSource(getApplicationContext());
                                    data.open();
                                    data.createFriend(message.getUsername(), message.getImageUrl());
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == PICTURE_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                data.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);
            }
        }
    }

    //decodes image and scales it to reduce memory consumption
    private void resizeFile(File f,Context context) throws IOException {
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

        //The new size we want to scale to
        final int REQUIRED_SIZE=400;

        //Find the correct scale value. It should be the power of 2.
        int scale=1;
        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
            scale*=2;

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        int i = getCameraPhotoOrientation(context, Uri.fromFile(f),f.getAbsolutePath());
        if (o.outWidth>o.outHeight)
        {
            Matrix matrix = new Matrix();
            matrix.postRotate(i); // anti-clockwise by 90 degrees
            bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap .getWidth(), bitmap .getHeight(), matrix, true);
        }
        try {
            f.delete();
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) throws IOException {
        int rotate = 0;
        context.getContentResolver().notifyChange(imageUri, null);
        File imageFile = new File(imagePath);
        ExifInterface exif = new ExifInterface(
                imageFile.getAbsolutePath());
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        return rotate;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Channel Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://adrien.tisonad.channelmessaging/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Channel Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://adrien.tisonad.channelmessaging/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
