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

    public void encAndSave(String message_){
        final String message = message_;
        new Thread(){
            @Override
            public void run(){
                int key = ThreadLocalRandom.current().nextInt(0, 27);
                EncMessage encMessage = new EncMessage(crypthor.cryptCesar(message, key), key);
                messageDatabase.dbAccess().insertUniqueMessage(encMessage);
            }
        }.start();
    }
}
