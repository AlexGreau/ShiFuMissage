package ours.shifumissage;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.concurrent.ThreadLocalRandom;

public class EncMessManager {
    private static final String DATABASE_NAME = "messages_db";
    private MessageDatabase messageDatabase;
    private Crypthor crypthor;
    private Context appContext;


    public EncMessManager(Context appContext){
        this.appContext = appContext;
        messageDatabase = Room.databaseBuilder(appContext, MessageDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
        crypthor = new Crypthor();
    }

    public String encAndSave(String message_){
        final String message = message_;
        int key = ThreadLocalRandom.current().nextInt(0, 27);
        final EncMessage encMessage = new EncMessage(crypthor.cryptCesar(message, key), key);
        new Thread(){
            @Override
            public void run(){
                messageDatabase.dbAccess().insertUniqueMessage(encMessage);
            }
        }.start();
        return encMessage.getMessageId();
    }

    public String getAndDecrypt(final String messageId_){
        final String messageId = messageId_;
        final EncMessage message = new EncMessage("", 0);
        new Thread(){
            @Override
            public void run(){
                EncMessage encMessage = messageDatabase.dbAccess().getEncMessage(messageId);
                message.setMessage(crypthor.decryptCesar(encMessage.getMessage(), encMessage.getEncKey()));
            }
        }.start();
        return message.getMessage();
    }
}
