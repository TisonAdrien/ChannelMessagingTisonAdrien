package adrien.tisonad.channelmessaging;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.HashMap;

public class ChannelListFragmentActivity extends AppCompatActivity {
    private ListView channels;
    private Button btnFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);

        channels = (ListView) findViewById(R.id.listViewChannels);
        btnFriends = (Button) findViewById(R.id.buttonFriends);

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


        channels.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Channel channel = (Channel) channels.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ChannelActivity.class);
                intent.putExtra("channelid", Integer.toString(channel.getChannelID()));
                startActivity(intent);
            }
        });

        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
                startActivity(intent);
            }
        });

        connexion.execute();
    }
}
