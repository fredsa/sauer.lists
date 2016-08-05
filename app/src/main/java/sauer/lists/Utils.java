package sauer.lists;

import com.google.firebase.database.DataSnapshot;

class Utils {
    static String GetListNameFromSnapshot(DataSnapshot dataSnapshot) {
        return GetNameFromSnapshot(dataSnapshot, "Unnamed list");
    }

    static String GetItemNameFromSnapshot(DataSnapshot dataSnapshot) {
        return GetNameFromSnapshot(dataSnapshot, "Unnamed item");
    }

    static String GetNameFromSnapshot(DataSnapshot dataSnapshot, String defaultName) {
        Object value = dataSnapshot.child("name").getValue();
        return value == null ? defaultName : value.toString();
    }
}
