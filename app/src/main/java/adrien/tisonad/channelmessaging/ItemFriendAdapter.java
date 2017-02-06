package adrien.tisonad.channelmessaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tisonad on 06/02/2017.
 */
public class ItemFriendAdapter extends ArrayAdapter<Friend> {
    private final Context context;
    private final List<Friend> values;

    public ItemFriendAdapter(Context context, List<Friend> friends ){
        super(context, 0, friends);
        this.context = context;
        this.values = friends;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Friend friend = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_friend_layout, parent, false);
        }

        TextView friendName = (TextView) convertView.findViewById(R.id.textViewFriend);
        CircleImageView friendImage = (CircleImageView) convertView.findViewById(R.id.imageViewFriend);

        friendName.setText(friend.getUsername());

        if(ChannelActivity.images.containsKey(friend.getImageUrl())){
            friendImage.setImageBitmap(ChannelActivity.images.get(friend.getImageUrl()));
        }else{
            new DownloadImageTask(friendImage).execute(friend.getImageUrl());
        }

        return convertView;
    }
}
