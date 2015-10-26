package iiitd.ac.in.androidfaceproject;

import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;

public class GoogleSignIn extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 0;
    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        ((SignInButton) findViewById(R.id.sign_in_button)).setSize(SignInButton.SIZE_WIDE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addApi(LocationServices.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Message","Connection has been suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                }
                catch (IntentSender.SendIntentException e) {
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            }
            else {
                Log.e("Error","Some error!");
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mShouldResolve = false;

        String currentAccount = Plus.AccountApi.getAccountName(mGoogleApiClient);
        if(currentAccount.contains("iiitd")) {
            Toast.makeText(this,"Signed in...",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,LandingPage.class);
            startActivity(intent);
        }
        else {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }
        }
    }

    private void onSignInClicked() {
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }
}
