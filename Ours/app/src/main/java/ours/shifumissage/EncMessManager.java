package ours.shifumissage;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class EncMessManager {
    private static final String DATABASE_NAME = "messages_db";
    private MessageDatabase messageDatabase;
    private Crypthor crypthor;
    private Context appContext;
    private HashMap<String, Integer> phone_key = new HashMap<>();


    public EncMessManager(Context appContext) {
        this.appContext = appContext;
        messageDatabase = Room.databaseBuilder(appContext, MessageDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
        crypthor = new Crypthor();
    }

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

    public EncMessage getEncMessage(String messageId_) {
        final String messageId = messageId_;
        final EncMessage message = new EncMessage("", "");
        new Thread() {
            @Override
            public void run() {
                EncMessage encMessage = messageDatabase.dbAccess().getEncMessage(messageId);
                message.setMessage(encMessage.getMessage());
                message.setMessageId(encMessage.getMessageId());
                message.setNumber(encMessage.getNumber());
            }
        }.start();
        return message;
    }

    public EncMessage getEncMessageFromNumber(String number_) {
        final String number = number_;
        final EncMessage message = new EncMessage("", number);
        new Thread() {
            @Override
            public void run() {
                EncMessage encMessage = messageDatabase.dbAccess().getMessageFromNumber(number);
                message.setMessage(encMessage.getMessage());
                message.setMessageId(encMessage.getMessageId());
                message.setNumber(encMessage.getNumber());
            }
        }.start();
        return message;
    }

    public String encryptMessage(String message, int key) {
        return crypthor.cryptCesar(message, key);
    }

    public String decryptMessage(String message, int key) {
        return crypthor.decryptCesar(message, key);
    }

    public void insertPhoneKey(String phone, int key) {
        phone_key.put(phone, key);
    }

    public int getKeyFromPhone(String phone) {
        if (phone_key.containsKey(phone)) {
            return phone_key.get(phone);
        } else {
            return -1;
        }
    }
}