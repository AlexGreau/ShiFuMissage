package ours.shifumissage;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.concurrent.ThreadLocalRandom;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ShiFuMissage";
    private static SmsManager smsManager;
    private static EncMessManager encMessManager;
    private BroadcastReceiver smsReceiver;
    private ListView listSms;
    private String phone_number;
    private TextView numberSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText textInput = (EditText) findViewById(R.id.textInput);

        /*final Button openSmsButton = (Button) findViewById(R.id.openSmsButton);
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
        });*/

        final Button smsButton = (Button) findViewById(R.id.sendButton);
        smsButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                smsButtonClicked();
            }
        });

        final Button keyButton = (Button) findViewById(R.id.keyButton);
        keyButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                keyButtonClicked();
            }
        });

        encMessManager = new EncMessManager(getApplicationContext());

        listSms = (ListView) findViewById(R.id.listSms);

        smsManager = SmsManager.getDefault();

        numberSelected = (TextView) findViewById(R.id.numberSelected);


        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle intentExtras = intent.getExtras();

                if (intentExtras != null) {
                    /* Get Messages */
                    Object[] sms = (Object[]) intentExtras.get("pdus");

                    for (int i = 0; i < sms.length; ++i) {
                        /* Parse Each Message */
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                        String action = intent.getAction();

                        String phone = smsMessage.getOriginatingAddress();
                        String message = smsMessage.getMessageBody().toString();

                        String intitule = message.split("=")[0];
                        String cont = message.split("=")[1];

                        

                        if ((intitule.compareTo("message") == 0)){
                            EncMessage encMessage = new EncMessage(cont, phone);
                            encMessManager.storeEncMessage(encMessage);
                        } else if (intitule.compareTo("key") == 0){
                            EncMessage encMessage = encMessManager.getEncMessageFromNumber(phone);
                            Toast.makeText(context, phone + ": " + encMessManager.decryptMessage(encMessage.getMessage(), Integer.parseInt(cont)), Toast.LENGTH_SHORT).show();
                            encMessManager.deleteEncMessage(encMessage);
                        }
                    }
                }
            }
        };


        askForReceptionPermission();
    }


    /*(Re) Initialize the list containing all phone numbers associated with sent ciphered sms*/
    private void initializeAdapter(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_2, android.R.id.text1, encMessManager.getPhoneList());

        listSms.setAdapter(adapter);

        listSms.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                phone_number = encMessManager.getPhoneList()[position];
                numberSelected.setText(phone_number);
            }
        });
    }


    /*Ask for sms reception permission if not already granted*/
    private void askForReceptionPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, READ_SMS) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{READ_SMS},
                    REQUEST_RECEIVE_SMS);
        } else {
            IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            getApplicationContext().registerReceiver(smsReceiver, intentFilter);
        }
    }


    /*Ask for sending sms permission if not already granted*/
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


    /*Ask for sending ssms permission if not already granted*/
    private void keyButtonClicked(){
        if ((ContextCompat.checkSelfPermission(MainActivity.this, SEND_SMS) != PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(MainActivity.this, READ_CONTACTS) != PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{SEND_SMS, READ_CONTACTS},
                    REQUEST_SEND_SMS);
        } else {
            sendKey();
        }
    }


    /*Method launched once a contact is selected through the contact activity*/
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



    /*Action to make once the request permission is allowed/denied by user*/
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
            case REQUEST_RECEIVE_SMS:
                IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
                getApplicationContext().registerReceiver(smsReceiver, intentFilter);
                break;
            default:
                Toast.makeText(getApplicationContext(), "WRONG REQUEST CODE in Permissions", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int REQUEST_SEND_SMS = 10;
    private static final int PICK_CONTACT_REQUEST = 20;
    private static final int REQUEST_RECEIVE_SMS = 30;


    /*Launch activity to select a contact*/
    private void pickContact() {
        Intent i = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, PICK_CONTACT_REQUEST);
    }


    /*Get the selected contact's Phone number*/
    private String getPhoneNumber(String id) {
        ContentResolver cr = getContentResolver();
        String where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id;
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, where, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        return null;
    }


    /*Send SMS to selected contact*/
    private void sendSMS(String content_, String number) {
        final String content = content_;
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        SmsManager smsManager = SmsManager.getDefault();
        try {
            int key = ThreadLocalRandom.current().nextInt(0, 27);
            String encContent = encMessManager.encryptMessage(content, key);
            encMessManager.insertPhoneKey(number, key);
            initializeAdapter();
            smsManager.sendTextMessage(number, null, "message=" + encContent, sentPI, deliveredPI);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"SMS failed, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    /*Send the key used to cipher the message sent to selected number*/
    private void sendKey() {
        int keyFromPhone = encMessManager.getKeyFromPhone(phone_number);
        if (keyFromPhone == -1) return;
        String content = "" +keyFromPhone;
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(phone_number, null, "key=" + content, sentPI, deliveredPI);
            encMessManager.deletePhoneKey(phone_number);
            phone_number = "";
            numberSelected.setText("No number selected");
            initializeAdapter();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"SMS failed, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}

