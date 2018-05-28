package ours.shifumissage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DBAccess {
    @Insert
    void insertUniqueMessage(EncMessage message);
    @Insert
    void insertMultipleMessages(List<EncMessage> messageList);
    @Query("SELECT * FROM EncMessage WHERE messageId = :messageId")
    EncMessage getEncMessage(String messageId);
    @Query("SELECT * FROM EncMessage WHERE number = :number")
    EncMessage getMessageFromNumber(String number);
    @Update
    void updateMessage(EncMessage message);
    @Delete
    void deleteMessage(EncMessage message);
}
