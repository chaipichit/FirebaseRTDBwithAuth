package com.example.chaipichit_jek.a405rtdbfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WalksMan";
    private static final int SIGN_IN_REQUEST_CODE = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btnSignInWithGoogle;
    private FirebaseAuth mAuth;
    private LinearLayout llLogout;
    private Button btnLogout,btnShow;
    private TextView tvUserName,tvMessage;
    private EditText etUser,etMessage;
    private Button btnSave;
    private RecyclerView rvRecordList;
    private RecordAdapter recordAdapter;
    private List<RecordModel> recordModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInstance();


    }

    private void initInstance() {

        initView();
        initUserFirebase();
        initClickButton();




    }

    private void initClickButton() {
        btnSignInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();

            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logout();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("records");
                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                if (mAuth.getUid()!=null){
                    DatabaseReference mNameRef = mRootRef.child("records").child(mAuth.getUid()).child("name");
                    DatabaseReference mQuoteRef = mRootRef.child("records").child(mAuth.getUid()).child("quote");
                    mNameRef.setValue(etUser.getText().toString());
                    mQuoteRef.setValue(etMessage.getText().toString());
                }
                else {
                    Toast.makeText(MainActivity.this,"pls Login to edit message",Toast.LENGTH_LONG).show();
                }



                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue()+"";
                        Log.d(TAG, "Value is: " + value);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "Failed to read value.", databaseError.toException());
                    }
                });
            }
        });
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordModelList = new ArrayList<RecordModel>();
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("records");
                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

                myRef.orderByChild("name").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            RecordModel recordModel = dataSnapshot.getValue(RecordModel.class);
                            recordModelList.add(recordModel);
                            showRecord(recordModelList);

                            Log.d(TAG,recordModel.getName()+" "+recordModel.getQuote());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        RecordModel recordModel = new RecordModel(dataSnapshot.child(dataSnapshot.getKey()).child("name").getValue()+""
                                ,dataSnapshot.child(dataSnapshot.getKey()).child("qoute").getValue()+"");

//                        RecordModel recordModel1 = dataSnapshot.getValue(RecordModel.class);


                        String [] name = new String[dataSnapshot.getValue().toString().split(",").length];
                     /*   name=dataSnapshot.getValue().toString().split(",");
                        for (int i = 0; i <name.length ; i++) {
                            Log.d(TAG,name[i]);
                        }
                            Log.d(TAG,dataSnapshot.getValue()+"");
                    //    Log.d(TAG,recordModell);*/


                        dataSnapshot.getChildren();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void showRecord(List<RecordModel> recordModelList) {
     /*   recordAdapter = new RecordAdapter(1, recordModelList);
        rvRecordList.setAdapter(recordAdapter);

        recordAdapter.notifyDataSetChanged();*/
        String s="";
        tvMessage.setText("");
        for (int i = 0; i < recordModelList.size(); i++) {
            s+=recordModelList.get(i).getName()+" "+recordModelList.get(i).getQuote()+'\n';
        }
        tvMessage.setText(s);

    }

    private void singInWithOutGoogle() {
        tvMessage.setText("");
        tvUserName.setText(etUser.getText());
        llLogout.setVisibility(View.VISIBLE);
        btnSignInWithGoogle.setVisibility(View.GONE);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });

        tvMessage.setText("");
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE);
    }

    private void initUserFirebase() {

        mAuth = FirebaseAuth.getInstance();




    }

    private void initView() {
       // rvRecordList = (RecyclerView) findViewById(R.id.rv_list_message_and_name);
        tvMessage=findViewById(R.id.tv_message);
        btnShow=findViewById(R.id.btn_show);
        etMessage=findViewById(R.id.et_message);
        btnSave=findViewById(R.id.btn_save);
        etUser=findViewById(R.id.et_user);
        tvUserName=findViewById(R.id.tv_username);
        llLogout=(LinearLayout) findViewById(R.id.Logout);
        btnSignInWithGoogle = (SignInButton) findViewById(R.id.btn_sign_in_with_google);
        btnSignInWithGoogle.setSize(SignInButton.SIZE_STANDARD);
        btnSignInWithGoogle.setColorScheme(SignInButton.COLOR_LIGHT);
        btnLogout = (Button) findViewById(R.id.btn_sign_out); 

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                          //  updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this
                                    , "Authentication failed."
                                    , Toast.LENGTH_SHORT
                            ).show();

                          //  updateUI(null);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                updateUI(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void updateUI(GoogleSignInAccount account) {

        if (account!=null){
            tvUserName.setText(account.getDisplayName());
            llLogout.setVisibility(View.VISIBLE);
            btnSignInWithGoogle.setVisibility(View.GONE);

        }
        else {
            tvUserName.setText(etUser.getText());
            llLogout.setVisibility(View.GONE);
            btnSignInWithGoogle.setVisibility(View.VISIBLE);
        }

    }
}
