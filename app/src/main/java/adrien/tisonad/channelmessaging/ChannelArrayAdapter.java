package adrien.tisonad.channelmessaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import adrien.tisonad.channelmessaging.Channel;
import adrien.tisonad.channelmessaging.R;

/**
 * Created by tisonad on 27/01/2017.
 */
public class ChannelArrayAdapter extends ArrayAdapter<Channel> {

    public ChannelArrayAdapter(Context context, List<Channel> channels) {
        super(context, 0, channels);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Channel channel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.channels_list_row, parent, false);

        }
        // Lookup view for data population
        TextView channelsName = (TextView) convertView.findViewById(R.id.textViewChannelName);
        TextView channelsNbPers = (TextView) convertView.findViewById(R.id.textViewChannelNbPers);
        // Populate the data into the template view using the data object
        channelsName.setText(channel.getName());
        channelsNbPers.setText("Nombre de personnes connectées : " + channel.getConnectedusers().toString());
        // Return the completed view to render on screen
        return convertView;
    }
}
