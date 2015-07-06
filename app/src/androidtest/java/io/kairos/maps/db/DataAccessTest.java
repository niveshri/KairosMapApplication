package io.kairos.maps.db;

import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import io.kairos.maps.ApplicationTest;

public class DataAccessTest extends ApplicationTest {
    private final String TAG = DataAccessTest.class.toString();

    private String[] CONTACTS_PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.TIMES_CONTACTED,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
    };

    private String CONTACTS_SELECTION = ContactsContract.Contacts.DISPLAY_NAME + " <> '' AND " +
            ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL AND " +
            ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1";

    private String[] CALL_LOGS_PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.DATE
    };

    public void testMostContactedCallAccess() {
        Cursor cursor = getContext().getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, CONTACTS_PROJECTION, CONTACTS_SELECTION,
                null, ContactsContract.Contacts.TIMES_CONTACTED + " DESC LIMIT 15");

        printCursor(cursor);

        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.i(TAG, "Name : " + name);

            Cursor phones = getContext().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " +
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)),
                    null, null);

            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phones.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.i(TAG, "Phone Number : " + phoneNumber);
            }
        }
    }

    public void testCallLogsAccess() {
        Cursor cursor = getContext().getContentResolver().query(
                CallLog.Calls.CONTENT_URI, CALL_LOGS_PROJECTION,
                null, null, CallLog.Calls.DATE + " DESC limit 10");

        printCursor(cursor);
    }

    public void printCursor(Cursor cursor) {
        Log.i(TAG, "****************************************");
        while (cursor.moveToNext()) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                sb.append(cursor.getString(i) + ", ");
            }

            Log.i(TAG, sb.toString());
        }
        Log.i(TAG, "****************************************");
    }

}
