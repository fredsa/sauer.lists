package sauer.lists;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Store {

    static FirebaseDatabase database;
    static DatabaseReference lists;
    static DatabaseReference acls;

    static void init() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            lists = database.getReference("lists2");
            acls = database.getReference("acls");
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

    public static DatabaseReference getList(String listKey)
    {
        init();
        return lists.child(listKey);
    }

    public static DatabaseReference getUserListKeys(String uid) {
        init();
        return acls.child(uid);
    }

}
