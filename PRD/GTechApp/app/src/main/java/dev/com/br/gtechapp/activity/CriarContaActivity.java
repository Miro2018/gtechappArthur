package dev.com.br.gtechapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.entity.Usuario;

public class CriarContaActivity extends BaseActivity implements View.OnClickListener{

    EditText edtEmail, edtSenha1, edtSenha2;
    TextView btnCriarConta, btnLoginActivity;

    int codTermoVigente;
    String txtDescricaoTermo;

    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    DatabaseReference mTermoReference;
    private DatabaseReference mUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        firebaseObterTermo();

        iniciaViews();
    }

    public void onBackPressed() {
        startActivity(new Intent(CriarContaActivity.this, LoginActivity.class));
        finish();

        return;
    }

    private void iniciaViews(){
        edtEmail = (EditText) findViewById(R.id.edtEmail);

        edtSenha1 = (EditText) findViewById(R.id.edtSenha);
        edtSenha2 = (EditText) findViewById(R.id.edtSenha2);

        btnCriarConta = (TextView) findViewById(R.id.btnCriarConta);
        btnCriarConta.setOnClickListener(this);

        btnLoginActivity = (TextView) findViewById(R.id.btnLoginActivity);
        btnLoginActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLoginActivity:
                startActivity(new Intent(CriarContaActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.btnCriarConta:
                signUp();
                break;
        }

    }

    private void onAuthSuccess(FirebaseUser user) {

        String username = user.getDisplayName();

        if(username == null || username.isEmpty()){
            username = usernameFromEmail(user.getEmail());
        }

        writeNewUser(user.getUid(), username, user.getEmail());

        String strFraseTermo = "Clicando em Ok você concorda com o termo de uso.";
        mostrarTermo(strFraseTermo);
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void signUp() {
        Log.d("APP_DEBUG", "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = edtEmail.getText().toString();
        String password = edtSenha1.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("APP_DEBUG", "createUser:onComplete:" + task.isSuccessful() + "  -  " + task.getException());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(CriarContaActivity.this, "Falha na criação de novo usuário",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            edtEmail.setError("Preenchimento obrigatório");
            result = false;
        } else {
            edtEmail.setError(null);
        }

        if (TextUtils.isEmpty(edtSenha1.getText().toString())) {
            edtSenha1.setError("Preenchimento obrigatório");
            result = false;
        } else {
            edtSenha1.setError(null);
        }

        if (TextUtils.isEmpty(edtSenha2.getText().toString())) {
            edtSenha2.setError("Preenchimento obrigatório");
            result = false;
        } else {
            edtSenha2.setError(null);
        }

        if(!edtSenha1.getText().toString().equals(edtSenha2.getText().toString())){
            edtSenha2.setError("Senhas não conferem");
            result = false;
        } else {
            edtSenha2.setError(null);
        }

        if((edtSenha1.getText().toString().length() < 6 || edtSenha2.getText().toString().length() < 6)){
            edtSenha2.setError("Senha deve ter 6 ou mais caracteres");
            result = false;
        } else {
            edtSenha2.setError(null);
        }

        return result;
    }

    private void writeNewUser(String userId, String name, String email) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userId).child(Usuario.NOM_USUARIO).setValue(name);
        mDatabase.child("users").child(userId).child(Usuario.TXT_EMAIL).setValue(email);
        Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_LONG).show();
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
                startActivity(new Intent(CriarContaActivity.this, MainActivity.class));
                finish();
                dialog.dismiss();
            }
        });

        Button btnDiscordo = (Button) dialog.findViewById(R.id.btnDiscordo);
        btnDiscordo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeAccess();
                startActivity(new Intent(CriarContaActivity.this, LoginActivity.class));
                finish();
                dialog.dismiss();
            }
        });

        dialog.show();
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
