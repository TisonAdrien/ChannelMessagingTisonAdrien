package adrien.tisonad.channelmessaging.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import adrien.tisonad.channelmessaging.ChannelActivity;
import adrien.tisonad.channelmessaging.ConnectionReturn;
import adrien.tisonad.channelmessaging.Downloader;
import adrien.tisonad.channelmessaging.LoginActivity;
import adrien.tisonad.channelmessaging.Message;
import adrien.tisonad.channelmessaging.MessageContainer;
import adrien.tisonad.channelmessaging.MessageListAdapter;
import adrien.tisonad.channelmessaging.OnDownloadCompleteListener;
import adrien.tisonad.channelmessaging.R;
import adrien.tisonad.channelmessaging.UserDataSource;

/**
 * Created by tisonad on 27/02/2017.
 */
public class MessageFragment extends Fragment {
            private ListView messages;
            private EditText myMessage;
            private Button btnImage;
            private Button btnSend;
            private List<Message> oldMessages;
            private Integer PICTURE_REQUEST_CODE = 1;
            static HashMap<String,Bitmap> images = new HashMap<String,Bitmap>();

            public MessageFragment() { }


            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                // Inflate the layout for this fragment
                View v = inflater.inflate(R.layout.message_fragment, container, false);
                messages = (ListView) v.findViewById(R.id.listViewMessages);
                myMessage = (EditText) v.findViewById(R.id.editTextMessage);
                btnSend = (Button) v.findViewById(R.id.buttonEnvoyer);
                btnImage = (Button) v.findViewById(R.id.buttonImage);
                oldMessages = null;


                final Handler handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        SharedPreferences settings = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                        String accesstoken = settings.getString("accesstoken","");

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("accesstoken",accesstoken);
                        params.put("channelid", getActivity().getIntent().getStringExtra("channelid"));
                        Downloader connexion = new Downloader(getActivity().getApplicationContext(), params, "http://www.raphaelbischof.fr/messaging/?function=getmessages");
                        connexion.execute();


                        connexion.setListener(new OnDownloadCompleteListener() {
                            @Override
                            public void onDownloadComplete(String content) {
                                //déserialisation
                                Gson gson = new Gson();
                                MessageContainer obj = gson.fromJson(content, MessageContainer.class);
                                Collections.reverse(obj.getMessages());

                                //Teste s'il y a un nouveau message avant de refresh
                                if(!obj.getMessages().equals(oldMessages)){
                                    oldMessages = obj.getMessages();
                                    messages.setAdapter((new MessageListAdapter(getActivity().getApplicationContext(), obj.getMessages())));
                                }
                            }
                        });


                        handler.postDelayed(this, 1000);
                    }
                };

                handler.postDelayed(r, 1000);


                btnImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences settings = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                        String accesstoken = settings.getString("accesstoken", "");
                        Toast.makeText(getActivity().getApplicationContext(), "Click sur image", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI); //Création de l’appel à l’application appareil photo pour récupérer une image
                        startActivityForResult(intent, PICTURE_REQUEST_CODE);
                    }
                });

                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences settings = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                        String accesstoken = settings.getString("accesstoken", "");

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("accesstoken", accesstoken);
                        params.put("channelid", getActivity().getIntent().getStringExtra("channelid"));
                        params.put("message", myMessage.getText().toString());

                        myMessage.setText("");
                        Downloader connexion = new Downloader(getActivity().getApplicationContext(), params, " http://www.raphaelbischof.fr/messaging/?function=sendmessage");

                        connexion.setListener(new OnDownloadCompleteListener() {
                            @Override
                            public void onDownloadComplete(String content) {
                                Gson gson = new Gson();
                                ConnectionReturn obj = gson.fromJson(content, ConnectionReturn.class);

                                if (!obj.getResponse().equals("Message envoyé au channel")) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        connexion.execute();
                    }
                });

                return v;
            }


        public boolean isExternalStorageWritable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
            return false;
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
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
}