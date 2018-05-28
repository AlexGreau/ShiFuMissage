package ours.shifumissage;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.SEND_SMS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ShiFuMissage";
    private static SmsManager smsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText textInput = (EditText) findViewById(R.id.textInput);

        final Button openSmsButton = (Button) findViewById(R.id.openSmsButton);
        openSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = textInput.getText().toString();
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:5554")); // TODO change the parse uri
                smsIntent.putExtra("sms_body", content);

                if (smsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(smsIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "INTENT NOT RESOLVED", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button smsButton = (Button) findViewById(R.id.sendButton);
        smsButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                smsButtonClicked();
            }
        });

        smsManager = SmsManager.getDefault();
    }

    private void smsButtonClicked() {
        if ((ContextCompat.checkSelfPermission(MainActivity.this, SEND_SMS) != PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(MainActivity.this, READ_CONTACTS) != PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{SEND_SMS, READ_CONTACTS},
                    REQUEST_SEND_SMS);
        } else {
            pickContact();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            try {
                ContentResolver cr = getContentResolver();
                Uri dataUri = data.getData();
                String[] projection = {ContactsContract.Contacts._ID};
                Cursor cursor = cr.query(dataUri, projection, null, null, null);

                if (null != cursor && cursor.moveToFirst()) {
                    String id = cursor
                            .getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String number = getPhoneNumber(id);
                    if (number == null) {
                        Toast.makeText(getApplicationContext(), "No number in contact", Toast.LENGTH_SHORT).show();
                    } else {
                        final EditText addrText = (EditText) findViewById(R.id.textInput);
                        sendSMS(addrText.getText().toString(), number);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission denied " + requestCode, Toast.LENGTH_SHORT).show();
            return;
        }

        switch (requestCode) {
            case REQUEST_SEND_SMS:
                pickContact();
                break;
            default:
                Toast.makeText(getApplicationContext(), "WRONG REQUEST CODE in Permissions", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int REQUEST_SEND_SMS = 10;
    private static final int PICK_CONTACT_REQUEST = 20;

    private void pickContact() {
        Intent i = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, PICK_CONTACT_REQUEST);
    }

    private String getPhoneNumber(String id) {
        ContentResolver cr = getContentResolver();
        String where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id;
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, where, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        return null;
    }

    private void sendSMS(String content, String number) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(number, null, content, sentPI, deliveredPI);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"SMS failed, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }



    // TODO : Cryptage Message
    // TODO : sauver clé / message
    // Todo : detecter reponse pour pouvoir envoyer clé

}

