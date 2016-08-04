package sauer.lists;

import com.google.firebase.database.DataSnapshot;

class Utils {
    static String GetListNameFromSnapshot(DataSnapshot dataSnapshot) {
        Object value = dataSnapshot.child("name").getValue();
        return value == null ? "Unnamed list" : value.toString();
    }
}
