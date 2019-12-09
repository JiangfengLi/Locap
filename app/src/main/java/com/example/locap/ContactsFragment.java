package com.example.locap;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;


/*
 * @author: Jiangfeng Li
 * @description: This is the ContactsFragment class of Sketcher, which is the subclass of fragment.
 * it sets up a list view of the contacts and their corresponding ID and create SEND message intent
 * to share the image that user created to the designated contact by email.
 *
 */
public class ContactsFragment extends Fragment {

    private Activity containerActivity = null;
    private View inflatedView = null;

    private ListView contactsListView;
    private ArrayAdapter<String> contactsAdapter = null;
    private ArrayList<String> contacts = new ArrayList<String>();
    private ArrayList<String> contact_ids = new ArrayList<String>();
    private String shareLocation = "";

    /**
     * This is the constructor of DrawingFragment function.
     */
    public ContactsFragment() { }

    /**
     * This setContainerActivity function, it accepts an Activity of containerActivity, assign
     * containerActivity and return nothing.
     */
    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    /**
     * This onCreateView function, it sets up the configurations of fragment and set drawing view to
     * specific LinearLayout. It accepts an LayoutInflater of inflater, ViewGroup of container,
     * Bundle of savedInstanceState and return a View.
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_contacts, container, false);
        containerActivity.setContentView(R.layout.fragment_contacts);
        return inflatedView;
    }

    /**
     * This onCreate function, it sets up the environment and get the contacts for user. It accepts
     * a 'savedInstanceState' bundle to create activity and return nothing.
     */
    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        getContacts();
    }

    /**
     * This onPause function, it sets up the ContactsAdapter to list view. It accepts and return
     * nothing.
     */
    @Override
    public void onResume() {
        super.onResume();
        setupContactsAdapter();
    }

    /**
     * This getContacts function, it gets a list of contact in the phone by content provider and
     * add user's contact and ID of contact to specific array.
     * It accepts and return nothing.
     */
    public void getContacts() {
        int limit = 1000;
        Cursor cursor = containerActivity.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,null, null, null,
                null);
        while (cursor.moveToNext() && limit > 0) {
            String contactId = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts._ID));
            String given = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));
            String contact = "Contact || " + given + " :: " + contactId;
            contacts.add(contact);
            contact_ids.add(contactId);
            limit--;
        }
        cursor.close();
    }

    /**
     * This setupContactsAdapter function, it sets up an ArrayAdapter of Strings and set the
     * OnItemClickListener of ListView to create sending email to specific contact event if one of
     * the contact in ListView is elected. It accepts and return nothing.
     */
    private void setupContactsAdapter() {
        contactsListView =
                (ListView)containerActivity.findViewById(R.id.contact_list_view);
        contactsAdapter = new
                ArrayAdapter<String>(containerActivity, R.layout.text_row,
                R.id.text_row_text_view, contacts);
        contactsListView.setAdapter(contactsAdapter);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String contactId = contact_ids.get(position);
                String email = "";
                Cursor emails = getActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " +
                                contactId, null, null);
                if (emails.moveToNext()) {
                    email = emails.getString(emails.getColumnIndex(
                            ContactsContract.CommonDataKinds.Email.ADDRESS));
                }
                emails.close();
                // Do something with the email address

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("vnd.android.cursor.dir/email");

                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
                intent.putExtra(Intent.EXTRA_TEXT, shareLocation);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                intent.setType("image/png");
                startActivity(intent);
            }
        });
    }

    public void setShareLocation(String locationName) {
        this.shareLocation = locationName;
    }
}
