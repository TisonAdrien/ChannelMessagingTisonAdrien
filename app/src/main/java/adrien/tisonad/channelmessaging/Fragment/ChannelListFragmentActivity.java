package adrien.tisonad.channelmessaging.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.HashMap;

import adrien.tisonad.channelmessaging.Channel;
import adrien.tisonad.channelmessaging.ChannelActivity;
import adrien.tisonad.channelmessaging.ChannelArrayAdapter;
import adrien.tisonad.channelmessaging.ChannelsContainer;
import adrien.tisonad.channelmessaging.Downloader;
import adrien.tisonad.channelmessaging.FriendsActivity;
import adrien.tisonad.channelmessaging.LoginActivity;
import adrien.tisonad.channelmessaging.OnDownloadCompleteListener;
import adrien.tisonad.channelmessaging.R;

public class ChannelListFragmentActivity extends Fragment {
    private ListView channels;
    private Button btnFriends;

    public ChannelListFragmentActivity() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.activity_channel_list);

        channels = (ListView) getActivity().findViewById(R.id.listViewChannels);
        btnFriends = (Button) getActivity().findViewById(R.id.buttonFriends);

        SharedPreferences settings = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        String accesstoken = settings.getString("accesstoken","");

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("accesstoken",accesstoken);

        Downloader connexion = new Downloader(getActivity().getApplicationContext(), params, "http://www.raphaelbischof.fr/messaging/?function=getchannels");

        connexion.setListener(new  OnDownloadCompleteListener() {
            @Override
            public void onDownloadComplete(String content) {
                Gson gson = new Gson();
                ChannelsContainer obj = gson.fromJson(content, ChannelsContainer.class);

                channels.setAdapter((new ChannelArrayAdapter(getActivity().getApplicationContext(), obj.getChannels())));
            }
        });


        channels.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Channel channel = (Channel) channels.getItemAtPosition(position);
                Intent intent = new Intent(getActivity().getApplicationContext(), ChannelActivity.class);
                intent.putExtra("channelid", Integer.toString(channel.getChannelID()));
                startActivity(intent);
            }
        });

        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), FriendsActivity.class);
                startActivity(intent);
            }
        });

        connexion.execute();
    }
}
