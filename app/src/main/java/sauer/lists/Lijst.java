package sauer.lists;

import com.google.firebase.database.DatabaseReference;

public class Lijst {
    public DatabaseReference databaseReference;
    public String value;

    public Lijst(DatabaseReference ref, String s) {
        databaseReference = ref;
        value = s;
    }

    @Override
    public String toString() {
        return databaseReference.getKey().toString() + (value == null ? " (empty)" : " (NOT empty)");
    }
}
