package sauer.lists;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = LoginActivity.class.getName() + " ***";
    private static final int RC_SIGN_IN = 42;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    GoogleApiClient mGoogleApiClient;
    private SignInButton signinButton;
    private TextView statusTextView;
    private Button signoutButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        statusTextView = (TextView) findViewById(R.id.status_text);

        signinButton = (SignInButton) findViewById(R.id.signin_button);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        signoutButton = (Button) findViewById(R.id.signout_button);
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                boolean isSignedin = user != null;
                signinButton.setEnabled(!isSignedin);
                signoutButton.setEnabled(isSignedin);
                if (isSignedin) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in: " + user.getEmail());
                    setStatus("Signed in as " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    setStatus("Signed out");
                }
                // ...
            }
        };

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle(" + acct.getEmail() + ")");

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential FAIL:", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            authFail();
                        } else {
                            Log.i(TAG, "signInWithCredential SUCCESS: " + task.getResult().getUser());
                            Toast.makeText(LoginActivity.this, "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();
                            authSuccess(task.getResult().getUser().getEmail());
                        }
                        // ...
                    }

                });
    }

    private void authFail() {
//        signinButton.setEnabled(true);
    }

    void authSuccess(String name) {
        setStatus("Signed in as " + name);
        Intent intent = new Intent(this, ListsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "OnConnectionFailed: " + connectionResult);
        setStatus("Connection failed: " + connectionResult);
    }

    private void signIn() {
        setStatus("Signing in…");
//        signinButton.setEnabled(false);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // This code clears which account is connected to the app.
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        if (status.isSuccess()) {
                            setStatus("Signout successful");
                            // Make sure mAuthListener fires
                            mAuth.signOut();
                        } else {
                            setStatus("Signout failed: " + status.getStatusMessage());
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.i(TAG, "Google Sign In successful: " + result.getSignInAccount().getEmail());
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                authSuccess(result.getSignInAccount().getEmail());
            } else {
                Log.e(TAG, "Google Sign In failed: " + result.getStatus());
                // Google Sign In failed, update UI appropriately
                // ...
                authFail();
            }
        }
    }

    public void setStatus(String text) {
        Log.d(TAG, "STATUS: " + text);
        statusTextView.setText(text);
    }
}
