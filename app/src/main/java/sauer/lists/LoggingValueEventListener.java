package sauer.lists;

import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public abstract class LoggingValueEventListener implements ValueEventListener {

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(getClass().getName(), databaseError.toString(), databaseError.toException());
    }

}
