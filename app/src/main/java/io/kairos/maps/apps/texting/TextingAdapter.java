package io.kairos.maps.apps.texting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.kairos.maps.R;

public class TextingAdapter extends ArrayAdapter<TextMessage> {
    private TextMessage[] textMessages = new TextMessage[] {
            new TextMessage("DG", "Lunch at 12.30"),
            new TextMessage("Daniel", "Projector work"),
            new TextMessage("Leslie", "Design discussion")
    };

    public TextingAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1, new ArrayList<TextMessage>());

        for (TextMessage textMessage : textMessages) {
            this.add(textMessage);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextMessage textMessage = getItem(position);

        convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.texting_list_item, parent, false);

        TextView smsSender = (TextView) convertView.findViewById(R.id.smsSender);
        smsSender.setText(textMessage.getSender());

        TextView smsContent = (TextView) convertView.findViewById(
                R.id.smsContent);
        smsContent.setText(textMessage.getMessage());

        return convertView;
    }
}
