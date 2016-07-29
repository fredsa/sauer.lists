package sauer.lists;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class EditNameDialogFragment extends DialogFragment implements ValueEventListener {

    private static final String TAG = EditNameDialogFragment.class.getName();

    DatabaseReference nameRef;
    String title;
    EditText nameEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.edit_name_dialog, null);

        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        titleTextView.setText(title);

        nameEditText = (EditText) view.findViewById(R.id.name);
        nameRef.addValueEventListener(this);

        nameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL) {
                    save();
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                save();
                dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    public void show(FragmentManager manager, DatabaseReference ref, String title) {
        this.nameRef = ref.child("name");
        this.title = title;
        super.show(manager, null);
    }

    void save() {
        String value = nameEditText.getText().toString().trim();
        if (value.length() != 0) {
            nameRef.setValue(value);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        nameRef.removeEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Object value = dataSnapshot.getValue();
        nameEditText.append(value == null ? "" : value.toString());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        String msg = TAG + ":\n" + databaseError.toString() + "\n" + databaseError.getDetails();
        Log.d(TAG, msg, databaseError.toException());
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
