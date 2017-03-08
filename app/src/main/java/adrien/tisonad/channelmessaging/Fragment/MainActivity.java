package adrien.tisonad.channelmessaging.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.HashMap;

import adrien.tisonad.channelmessaging.Channel;
import adrien.tisonad.channelmessaging.ChannelArrayAdapter;
import adrien.tisonad.channelmessaging.ChannelsContainer;
import adrien.tisonad.channelmessaging.Downloader;
import adrien.tisonad.channelmessaging.LoginActivity;
import adrien.tisonad.channelmessaging.OnDownloadCompleteListener;
import adrien.tisonad.channelmessaging.R;

/**
 * Created by tisonad on 27/02/2017.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView channels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_channel_list_fragment);
            channels = (ListView) findViewById(R.id.listViewChannels);

            SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
            String accesstoken = settings.getString("accesstoken","");

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("accesstoken",accesstoken);

            Downloader connexion = new Downloader(getApplicationContext(), params, "http://www.raphaelbischof.fr/messaging/?function=getchannels");

            connexion.setListener(new  OnDownloadCompleteListener() {
                @Override
                public void onDownloadComplete(String content) {
                    Gson gson = new Gson();
                    ChannelsContainer obj = gson.fromJson(content, ChannelsContainer.class);
                    channels.setAdapter((new ChannelArrayAdapter(getApplicationContext(), obj.getChannels())));
                }
            });

            channels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Fragment fragA = (Fragment)getSupportFragmentManager().findFragmentById(R.id.fragmentA_ID);
                    Fragment fragB = (Fragment)getSupportFragmentManager().findFragmentById(R.id.fragmentB_ID);
                    Channel channel = (Channel) channels.getItemAtPosition(position);
                    String channelid = Integer.toString(channel.getChannelID());

                    if(fragB == null|| !fragB.isInLayout()){
                        Intent i = new Intent(getApplicationContext(),MessageFragment.class);
                        i.putExtra("channelid", channelid);
                        startActivity(i);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("channelid", channelid);
                        fragB.setArguments(bundle);
                    }
                }
            });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragA = (Fragment)getSupportFragmentManager().findFragmentById(R.id.fragmentA_ID);
        Fragment fragB = (Fragment)getSupportFragmentManager().findFragmentById(R.id.fragmentB_ID);
        Channel channel = (Channel) channels.getItemAtPosition(position);
        String channelid = Integer.toString(channel.getChannelID());

        if(fragB == null|| !fragB.isInLayout()){
            Intent i = new Intent(getApplicationContext(),MessageFragment.class);
            i.putExtra("channelid", channelid);
            startActivity(i);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("channelid", channelid);
            fragB.setArguments(bundle);
        }
    }
}