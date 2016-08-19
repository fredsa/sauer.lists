package sauer.lists;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Store {

    private static final String TAG = Store.class.getName() + " *******";

    private static final String INFO_CONNECTED = ".info/connected";

    static FirebaseDatabase database;
    static DatabaseReference lists;
    static DatabaseReference acls;

    static void init() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            lists = database.getReference("lists");
            acls = database.getReference("acls");
            database.getReference(INFO_CONNECTED).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    Log.d(TAG, INFO_CONNECTED + "--> " + (connected ? "CONNECTED" : "DISCONNECTED"));
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d(TAG, INFO_CONNECTED + "--> onCancelled()");
                }
            });
        }
    }

    public static DatabaseReference lists() {
        init();
        return lists;
    }

    public static DatabaseReference users() {
        init();
        return acls;
    }

    public static DatabaseReference getList(String listKey) {
        init();
        return lists.child(listKey);
    }

    public static DatabaseReference getUserAcls(String uid) {
        init();
        return acls.child(uid);
    }

}
