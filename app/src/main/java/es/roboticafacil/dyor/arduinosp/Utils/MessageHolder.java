package es.roboticafacil.dyor.arduinosp.Utils;

import android.view.View;
import android.widget.TextView;

/**
 * Created by Dragos Dunareanu on 24-Apr-17.
 */

public class MessageHolder {
    public TextView userName;
    public TextView textMessage;
    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView date = (TextView) v;
            if (date.getVisibility() == View.INVISIBLE)
                date.setVisibility(View.VISIBLE);
            else
                date.setVisibility(View.GONE);
        }
    };
    boolean vis = false;

    public class SentMessage extends MessageHolder {
        public TextView dateText;
    }

    public class RecievedMessage extends MessageHolder {
        public TextView dateText;
    }

}
