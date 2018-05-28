package ours.shifumissage;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class EncMessManager {
    private static final String DATABASE_NAME = "messages_db";
    private MessageDatabase messageDatabase;
    private Crypthor crypthor;
    private Context appContext;
    private HashMap<String, Integer> phone_key = new HashMap<>();
    private ArrayList<String> phoneList = new ArrayList<>();



    /*Get all phone numbers associated with already sent ciphered message*/
    public String[] getPhoneList() {
        return phoneList.toArray(new String[phoneList.size()]);
    }




    /*Creation of the database in the application context*/
    public EncMessManager(Context appContext) {
        this.appContext = appContext;
        messageDatabase = Room.databaseBuilder(appContext, MessageDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
        crypthor = new Crypthor();
    }


    /*Insertion of the ciphered message (with associated meta data) in the database*/
    public void storeEncMessage(EncMessage encMessage_) {
        final EncMessage encMessage = new EncMessage(encMessage_.getMessage(), encMessage_.getNumber());
        encMessage.setMessageId(encMessage_.getMessageId());
        new Thread() {
            @Override
            public void run() {
                messageDatabase.dbAccess().insertUniqueMessage(encMessage);
            }
        }.start();
    }


    /*Once the secret key is received, ciphered message is discovered and removed from the database*/
    public void deleteEncMessage(EncMessage encMessage_) {
        final EncMessage encMessage = new EncMessage(encMessage_.getMessage(), encMessage_.getNumber());
        encMessage.setMessageId(encMessage_.getMessageId());
        new Thread(){
            @Override
            public void run(){
                messageDatabase.dbAccess().deleteMessage(encMessage);
            }
        }.start();
    }


    /*Get the ciphered message associated with the phone number number_, in case of existence*/
    public EncMessage getEncMessageFromNumber(String number_) {
        final String number = number_;
        final EncMessage message = new EncMessage("", number);
        new Thread() {
            @Override
            public void run() {
                EncMessage encMessage = messageDatabase.dbAccess().getMessageFromNumber(number);
                if (encMessage != null) {
                    message.setMessage(encMessage.getMessage());
                    message.setMessageId(encMessage.getMessageId());
                    message.setNumber(encMessage.getNumber());
                }
            }
        }.start();
        return message;
    }

    /*Encrypt the message with Caesar cipher and given key*/
    public String encryptMessage(String message, int key) {
        return crypthor.cryptCesar(message, key);
    }


    /*Decrypt the ciphered message using the given key*/
    public String decryptMessage(String message, int key) {
        return crypthor.decryptCesar(message, key);
    }


    /*Register the key used to cipher the message sent to given phone number*/
    public void insertPhoneKey(String phone, int key) {
        phone_key.put(phone, key);
        phoneList.add(phone);
    }


    /*Get the key used to cipher the message sent to given phone number*/
    public int getKeyFromPhone(String phone) {
        if (phone_key.containsKey(phone)) {
            return phone_key.get(phone);
        } else {
            return -1;
        }
    }


    /*Once key sent to the given phone number, delete the pair <phone, key>*/
    public void deletePhoneKey(String phone){
        if (phone_key.containsKey(phone)) {
            phone_key.remove(phone);
        }
    }

}