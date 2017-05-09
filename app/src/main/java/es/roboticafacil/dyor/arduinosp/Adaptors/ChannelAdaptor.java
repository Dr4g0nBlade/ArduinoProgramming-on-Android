package es.roboticafacil.dyor.tabbed.Adaptors;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import es.roboticafacil.dyor.tabbed.Models.Channel;
import es.roboticafacil.dyor.tabbed.R;

/**
 * Created by Dragos Dunareanu on 06-Apr-17.
 */

public class ChannelAdaptor extends BaseAdapter {

    private Context mContext;
    private Bitmap avatar;
    private List<Channel> channels;

    public ChannelAdaptor(Context mContext, List<Channel> channels) {
        this.mContext = mContext;
        this.channels = channels;
    }

    @Override
    public int getCount() {
        return channels.size();
    }

    @Override
    public Object getItem(int position) {
        return channels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Channel entry = channels.get(position);

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(mContext);
            v = inflater.inflate(R.layout.channel, null);
        }

        ImageView ivAvatar = (ImageView) v.findViewById(R.id.iv_channel_avatar);
        ivAvatar.setImageBitmap(entry.getAvatar());

        TextView tvName = (TextView) v.findViewById(R.id.tv_channel_name);
        tvName.setText(entry.getName());

        TextView tvPreview = (TextView) v.findViewById(R.id.tv_channel_preview);
        tvPreview.setText(entry.getLastMessage());

        return v;
    }
}
