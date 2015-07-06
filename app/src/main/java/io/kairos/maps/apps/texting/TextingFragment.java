package io.kairos.maps.apps.texting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.kairos.maps.R;

public class TextingFragment extends Fragment {
    private ListView textsListView;

    @Override
    public void onStart() {
        super.onStart();

        textsListView = (ListView) getActivity().findViewById(R.id.textsListView);
        textsListView.setAdapter(new TextingAdapter(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_texting, container, false);
    }
}
