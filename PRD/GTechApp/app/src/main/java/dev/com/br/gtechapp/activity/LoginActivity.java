package dev.com.br.gtechapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;

import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.entity.Usuario;
import dev.com.br.gtechapp.persistence.PrefsUsuario;
import dev.com.br.gtechapp.service.Util;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    PrefsUsuario prefsUsuario;

    EditText edtEmail, edtSenha;
    TextView btnEntrar, btnCriarConta, btnRecuperarSenha;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    FirebaseUser user;

    private GoogleApiClient mGoogleApiClient; //google
    private FirebaseAuth mAuth; //firebase
    private CallbackManager mCallbackManager; //fb
    private DatabaseReference mUserReference;

    //Link providers
    AuthCredential credential;

    DatabaseReference mDatabase;
    DatabaseReference mTermoReference;

    int codTermoVigente;
    String txtDescricaoTermo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefsUsuario = new PrefsUsuario(this);

        firebaseObterTermo();

        iniciaGoogleProvider();

//        iniciaFacebookProvider();

        iniciaLoginComEmail();
    }
    private void firebaseObterTermo(){

        mTermoReference = mDatabase.child("termo");

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                codTermoVigente = dataSnapshot.child("cod_termo").getValue(Integer.class);
                txtDescricaoTermo = dataSnapshot.child("txt_descricao_termo").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(getClass().getName(), "firebaseObterTermo:onCancelled", databaseError.toException());
                // ...
            }
        };
        mTermoReference.addValueEventListener(userListener);
    }

    private void mostrarTermo(String strFraseTermo){

        if(txtDescricaoTermo == null){
            txtDescricaoTermo = getResources().getString(R.string.termo_link);
        }

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.alert_dialog_termo);
        dialog.setCancelable(false);

        TextView titulo = (TextView) dialog.findViewById(R.id.tvTitulo);
        TextView msg = (TextView) dialog.findViewById(R.id.tvMsg);
        TextView link = (TextView) dialog.findViewById(R.id.tvLink);

        titulo.setText("Termo de Uso");
        msg.setText(strFraseTermo);

        final SpannableString s =
                new SpannableString(txtDescricaoTermo);
        Linkify.addLinks(s, Linkify.WEB_URLS);
        link.setText(s);
        link.setMovementMethod(LinkMovementMethod.getInstance());

        Button btnConcordo = (Button) dialog.findViewById(R.id.btnConcordo);
        btnConcordo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("users").child(mAuth.getUid()).child("codTermo").setValue(codTermoVigente);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                dialog.dismiss();
            }
        });

        Button btnDiscordo = (Button) dialog.findViewById(R.id.btnDiscordo);
        btnDiscordo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                revokeAccess();
            }
        });

        dialog.show();

    }


    private void iniciaLoginComEmail(){

        edtEmail = (EditText) findViewById(R.id.edtEmail);

        edtSenha = (EditText) findViewById(R.id.edtSenha);

        btnEntrar = (TextView) findViewById(R.id.btnEntrar);
        btnEntrar.setOnClickListener(this);

        btnCriarConta = (TextView) findViewById(R.id.btnCriarConta);
        btnCriarConta.setOnClickListener(this);

        btnRecuperarSenha = (TextView) findViewById(R.id.btnRecuperarSenha);
        btnRecuperarSenha.setOnClickListener(this);
    }

    private void iniciaGoogleProvider(){

        SignInButton signInButton = findViewById(R.id.btn_sign_in_google);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void iniciaFacebookProvider(){
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.btn_fb_login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                updateUI(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                updateUI(null);
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //onAuthSuccess(mAuth.getCurrentUser());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void onAuthSuccess(FirebaseUser user) {

        String username = user.getDisplayName();

        if(username == null || username.isEmpty()){
            username = usernameFromEmail(user.getEmail());
        }

        writeNewUser(user.getUid(), username, user.getEmail());

        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                if(usuario.getCodTermo() == 0){
                    String strFraseTermo = "Clicando em Ok você concorda com o termo de uso.";
                    mostrarTermo(strFraseTermo);
                }else if(usuario.getCodTermo() < codTermoVigente){
                    String strFraseTermo = "O termo de uso foi atualizado.  É necessário aceitá-lo para continuar utilizando o aplicativo.";
                    mostrarTermo(strFraseTermo);
                }else{
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(getClass().getName(), "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mUserReference.addListenerForSingleValueEvent(userListener);
    }

    /**
     * Autenticação com google provider
     * @param acct
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressDialog();
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        if(credential != null){
                signInWithCredential();
        }
    }

    /**
     * Autenticação com Facebook provider
     * @param token
     */
    private void handleFacebookAccessToken(AccessToken token) {
        showProgressDialog();
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        credential = FacebookAuthProvider.getCredential(token.getToken());

        if(credential != null){
            signInWithCredential();
        }

    }

    @Override
    public void onClick(View v) {

        if(!Util.isOnline(this)){

            Toast.makeText(this, "Sem conexão com a internet.", Toast.LENGTH_LONG).show();

        }else{
            switch (v.getId()) {
                case R.id.btn_sign_in_google:
                    signIn();
                    break;
                case R.id.btnCriarConta:
                    startActivity(new Intent(this, CriarContaActivity.class));
                    finish();
                    break;
                case R.id.btnRecuperarSenha:
                    startActivity(new Intent(this, ResetPasswordActivity.class));
                    finish();
                    break;
                case R.id.btnEntrar:
                    signInWithEmail();
                    break;
                default:
                    return;
            }
        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.");
            }
        }else{
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        //TODO update UI
    }

    private void signInWithCredential(){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            updateUI(user);
                            onAuthSuccess(user);
                        } else {
                            LoginManager.getInstance().logOut();
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(LoginActivity.this, "E-mail já cadastrado", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void writeNewUser(String userId, String name, String email) {
        mDatabase.child("users").child(userId).child(Usuario.NOM_USUARIO).setValue(name);
        mDatabase.child("users").child(userId).child(Usuario.TXT_EMAIL).setValue(email);
    }


    private void signInWithEmail() {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = edtEmail.getText().toString();
        String password = edtSenha.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Falha na autenticação",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            edtEmail.setError("Required");
            result = false;
        } else {
            edtEmail.setError(null);
        }

        if (TextUtils.isEmpty(edtSenha.getText().toString())) {
            edtSenha.setError("Required");
            result = false;
        } else {
            edtSenha.setError(null);
        }

        return result;
    }

    private void revokeAccess() {

        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }
}