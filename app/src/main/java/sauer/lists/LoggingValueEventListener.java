package sauer.lists;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public abstract class LoggingValueEventListener implements ValueEventListener {

    private Context context;

    private String prefix;

    public LoggingValueEventListener(Context context, DatabaseReference ref) {
        this.context = context;
        this.prefix = getClass().getName() + "#" + ref.toString();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        String msg = prefix + ":\n" + databaseError.toString() + "\n" + databaseError.getDetails();
        Log.d(getClass().getName(), msg, databaseError.toException());
        //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
