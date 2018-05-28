package ours.shifumissage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {EncMessage.class}, version = 1, exportSchema = false)
public abstract class MessageDatabase extends RoomDatabase {
    public abstract DBAccess dbAccess();
}
