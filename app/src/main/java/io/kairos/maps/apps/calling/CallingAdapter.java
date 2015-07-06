package io.kairos.maps.apps.calling;

import android.content.Context;
import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import io.kairos.maps.R;
import io.kairos.maps.utils.Utils;

public class CallingAdapter extends ArrayAdapter<ContactInfo> {
    private CallingFragment callingFragment;

    public CallingAdapter(Context context, CallingFragment callingFragment,
                          List<ContactInfo> contactInfoList) {
        super(context, R.layout.contact_list_item, contactInfoList);

        this.callingFragment = callingFragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactInfo contactInfo = getItem(position);

        convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.contact_list_item, parent, false);

        LinearLayout layout = (LinearLayout) convertView;
        int displayHeightPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                callingFragment.isDisplayFidelityHigh() ? 80 : 122,
                getContext().getResources().getDisplayMetrics());
        layout.getLayoutParams().height = displayHeightPixels;

        TextView contactName = (TextView) convertView.findViewById(R.id.contactNameTextView);
        if (contactInfo.getName() != null && !contactInfo.getName().trim().equalsIgnoreCase("")) {
            contactName.setText(contactInfo.getName());
        } else {
            contactName.setText(Utils.formatPhoneNumber(contactInfo.getPhoneNumber()));
        }
        contactName.setTextSize(callingFragment.isDisplayFidelityHigh() ? 16 : 22);

        if (contactInfo.getThumbnail() != null) {
            ImageView contactThumbnail = (ImageView) convertView.findViewById(R.id.contactThumbnailImageView);
            contactThumbnail.setImageURI(Uri.parse(contactInfo.getThumbnail()));
        }

        return convertView;
    }
}
