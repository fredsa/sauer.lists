package sauer.lists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity {

    public static final String TAG = LoginActivity.class.getName() + " *****";
    private static final int RC_SIGN_IN = 42;

    private SignInButton signInButton;
    private Button continueAsButton;
    private Button signOutButton;
    private TextView statusTextView;
    private FirebaseAuth auth;

    private static boolean firstRun =true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_login);

        statusTextView = (TextView) findViewById(R.id.status_text);

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        continueAsButton = (Button)findViewById(R.id.contine_as_button);
        continueAsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startListsActivity();
            }
        });

        signOutButton = (Button) findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        auth = FirebaseAuth.getInstance();
        if (firstRun && auth.getCurrentUser() != null) {
            firstRun = false;
            startListsActivity();
        } else {
            updateButtonsStates();
        }
    }

    private void updateButtonsStates() {
        boolean isSignedIn = auth.getCurrentUser() != null;

        signInButton.setVisibility(!isSignedIn ? View.VISIBLE : View.GONE);

        continueAsButton.setVisibility(isSignedIn ? View.VISIBLE : View.GONE);
        if (isSignedIn) {
            continueAsButton.setText(getString(R.string.continue_as, auth.getCurrentUser().getEmail()));
        }

        signOutButton.setVisibility(isSignedIn ? View.VISIBLE : View.GONE);

        if (isSignedIn) {
            setStatus("Signed in as " + auth.getCurrentUser().getEmail() + ".");
        } else {
            setStatus("Please sign in.");
        }
    }

    private void signOut() {
        Log.d(TAG, "Signing out…");
        setStatus("Signing out…");
        AuthUI.getInstance(getFirebaseApp())
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        setStatus("Sign out successful.");
                        updateButtonsStates();
                    }
                });
    }

    private void signIn() {
        Log.d(TAG, "Signing in…");
        setStatus("Signing in…");
        startActivityForResult(
                AuthUI.getInstance(getFirebaseApp())
                        .createSignInIntentBuilder()
                        .setProviders(AuthUI.GOOGLE_PROVIDER)
                        .build(),
                RC_SIGN_IN);
    }

    private FirebaseApp getFirebaseApp() {
        return FirebaseApp.getInstance();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                setStatus("Signed in succeeded.");
                startListsActivity();
            } else {
                setStatus("Sign in failed.");
            }
        }
    }

    private void startListsActivity() {

        Intent intent = new Intent(this, ListsActivity.class);
        intent.putExtra("email", auth.getCurrentUser().getEmail());
        intent.putExtra("uid", auth.getCurrentUser().getUid());
        startActivity(intent);
    }

    public void setStatus(String text) {
        Log.d(TAG, "STATUS: " + text);
        statusTextView.setText(text);
    }
}
