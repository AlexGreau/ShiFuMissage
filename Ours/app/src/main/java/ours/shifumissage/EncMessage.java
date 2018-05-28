package ours.shifumissage;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity
public class EncMessage {
    @NonNull
    @PrimaryKey
    private String messageId;
    private String message;
    private int encKey;

    public EncMessage(String message, int encKey){
        this.messageId = UUID.randomUUID().toString();
        this.message = message;
        this.encKey = encKey;
    }

    @NonNull
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(@NonNull String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getEncKey() {
        return encKey;
    }

    public void setEncKey(int encKey) {
        this.encKey = encKey;
    }
}
