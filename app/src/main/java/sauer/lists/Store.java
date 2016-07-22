package sauer.lists;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Store {

    static FirebaseDatabase database;
    static DatabaseReference lists;

    static void init() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            lists = database.getReference("lists");
        }
    }

    public static DatabaseReference lists() {
        init();
        return lists;
    }

    public static DatabaseReference getList(String listKey)
    {
        init();
        return lists.child(listKey);
    }
}
