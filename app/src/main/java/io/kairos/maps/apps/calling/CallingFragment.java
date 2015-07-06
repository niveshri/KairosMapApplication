package io.kairos.maps.apps.calling;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import io.kairos.maps.R;
import io.kairos.maps.context.CarContextProvider;
import io.kairos.maps.context.DriverLoad;
import io.kairos.maps.speechrec.MicButton;
import io.kairos.maps.speechrec.SpeechToTextListener;
import io.kairos.maps.ui.AppState;
import io.kairos.maps.ui.BackButtonHandler;

public class CallingFragment extends ListFragment implements
        AdapterView.OnItemClickListener, SearchView.OnQueryTextListener,
        LoaderManager.LoaderCallbacks<Cursor>, SpeechToTextListener,
        BackButtonHandler {
    private final String TAG = CallingFragment.class.toString();

    private static final int CONTACTS_LOADER_ID = 0;
    private static final int CALL_LOGS_LOADER_ID = 1;

    private static final int MAX_SEARCH_CONTACTS = 10;
    private static final int MAX_CALL_LOG_LIST_LOW_LOAD = 10;
    private static final int MAX_CALL_LOG_LIST_HIGH_LOAD = 5;
    private static final int MAX_CONTACTS_LIST_LOW_LOAD = 15;
    private static final int MAX_CONTACTS_LIST_HIGH_LOAD = 10;

    private String[] CONTACTS_PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.TIMES_CONTACTED,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
    };

    private String[] CALL_LOGS_PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.DATE
    };

    private String CONTACTS_SELECTION = ContactsContract.Contacts.DISPLAY_NAME + " <> '' AND " +
            ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL AND " +
            ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1";


    private String searchString;
    private LinkedHashSet<ContactInfo> displayContactsSet;
    private CallingAdapter callingAdapter;

    private boolean displayFidelityHigh = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        if (savedInstanceState != null) {
            // could be used to restart the fragment from an old state which might have
            // a term that was searched for.
        }

        AppState.instance().set("calling_fragment_checked_time", "" + DateTime.now().getMillis());

        displayContactsSet = new LinkedHashSet<ContactInfo>();
        callingAdapter = new CallingAdapter(getActivity(), this, new ArrayList<ContactInfo>());
        setListAdapter(callingAdapter);

        // Currently decides how to draw the UI when the fragment is loaded. Potentially could
        // slowly animate to larger and smaller as the state changes.
        // Also currently simply fetches the last state. Should potentially fetch the last stable
        // state. For instance low only if low for 3 times.
        if (CarContextProvider.instance().lastDriverLoad() == DriverLoad.LOW)
            displayFidelityHigh = true;

        getLoaderManager().initLoader(CONTACTS_LOADER_ID, savedInstanceState, this);
        getLoaderManager().initLoader(CALL_LOGS_LOADER_ID, savedInstanceState, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calling, container, false);

        MicButton micButton = (MicButton) view.findViewById(R.id.micButton);
        micButton.setSpeechToTextListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        getListView().setOnItemClickListener(this);
    }

    private Loader<Cursor> fetchContactsCursor() {
        Uri baseUri;
        if (searchString != null) {
            baseUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI,
                    Uri.encode(searchString));

            return new CursorLoader(getActivity(), baseUri, CONTACTS_PROJECTION, CONTACTS_SELECTION,
                    null, ContactsContract.Contacts.TIMES_CONTACTED + " LIMIT " + MAX_SEARCH_CONTACTS);
        } else {
            baseUri = ContactsContract.Contacts.CONTENT_URI;

            return new CursorLoader(getActivity(), baseUri, CONTACTS_PROJECTION, CONTACTS_SELECTION,
                    null, ContactsContract.Contacts.TIMES_CONTACTED + " DESC LIMIT "
                    + (displayFidelityHigh ? MAX_CONTACTS_LIST_LOW_LOAD : MAX_CONTACTS_LIST_HIGH_LOAD)
            );
        }
    }

    private Loader<Cursor> fetchCallLogsCursor() {
        return new CursorLoader(getActivity(), CallLog.Calls.CONTENT_URI, CALL_LOGS_PROJECTION,
                null, null, CallLog.Calls.DATE + " DESC limit "
                + (displayFidelityHigh ? MAX_CALL_LOG_LIST_LOW_LOAD : MAX_CALL_LOG_LIST_HIGH_LOAD)
        );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case CONTACTS_LOADER_ID:
                return fetchContactsCursor();
            case CALL_LOGS_LOADER_ID:
                return fetchCallLogsCursor();
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursorLoader.getId() == CONTACTS_LOADER_ID) {
            addDirectContacts(cursor);
        } else if (cursorLoader.getId() == CALL_LOGS_LOADER_ID) {
            if (searchString == null) addRecentCalled(cursor);
        }
    }

    private void addRecentCalled(Cursor cursor) {
        int orderCounter = 0;
        while (cursor.moveToNext()) {
            ContactInfo contactInfo = new ContactInfo(
                    cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID)),
                    cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)),
                    cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)),
                    null, // TODO: fetch photo URI
                    "1" + orderCounter
            );

            if (!displayContactsSet.contains(contactInfo)) {
                displayContactsSet.add(contactInfo);
                orderCounter++;
            }
        }

        updateCallingAdapter();
    }

    private void updateCallingAdapter() {
        callingAdapter.clear();
        callingAdapter.addAll(displayContactsSet);
    }

    public boolean isDisplayFidelityHigh() {
        return displayFidelityHigh;
    }

    private void addDirectContacts(Cursor cursor) {
        int orderCounter = 0;
        while (cursor.moveToNext()) {
            Cursor phones = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " +
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)),
                    null, null);

            String phoneNumber = null;
            while (phones.moveToNext()) {
                phoneNumber = phones.getString(phones.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
            }

            ContactInfo contactInfo = new ContactInfo(
                    cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)),
                    phoneNumber,
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)),
                    "2" + orderCounter);

            if (!displayContactsSet.contains(contactInfo)) {
                displayContactsSet.add(contactInfo);
                orderCounter++;
            }
        }

        updateCallingAdapter();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ContactInfo contactInfo = callingAdapter.getItem(position);

        try {
            String uri = "tel:" + contactInfo.getPhoneNumber();
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
            startActivity(callIntent);
        }catch(Exception e) {
            Toast.makeText(getActivity(), "Call failed...", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error calling : " + e);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchString = query;
        displayContactsSet.clear();
        getLoaderManager().restartLoader(CONTACTS_LOADER_ID, null, this);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onSpeechToText(List<String> textList) {
        if (textList == null || textList.size() == 0) return;

        this.onQueryTextSubmit(textList.get(0));
    }

    @Override
    public boolean onBackButtonPressed() {
        if (searchString != null) {
            this.onQueryTextSubmit(null);
            return true;
        }

        return false;
    }
}
