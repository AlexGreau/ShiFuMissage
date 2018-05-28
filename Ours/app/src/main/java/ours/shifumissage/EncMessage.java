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
    @NonNull
    private String number;
    private String message;

    public EncMessage(String message, String number){
        this.messageId = UUID.randomUUID().toString();
        this.message = message;
        this.number = number;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
