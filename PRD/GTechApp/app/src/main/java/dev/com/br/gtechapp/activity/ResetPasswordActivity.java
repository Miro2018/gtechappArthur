package dev.com.br.gtechapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import dev.com.br.gtechapp.R;

public class ResetPasswordActivity extends BaseActivity{

    private EditText edtEmail;

    private TextView btnReset, btnBack;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reset_password);

        edtEmail = (EditText) findViewById(R.id.edtEmail);

        btnReset = (TextView) findViewById(R.id.btnRecuperarSenha);

        btnBack = (TextView) findViewById(R.id.btnLoginActivity);

        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Preenchimento obrigatório!");
                    return;
                }

                showProgressDialog();

                auth.sendPasswordResetEmail(email)

                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, "Instruções para recuperar a senha foram enviads ao seu e-mail!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, "Não foi possível enviar o e-mail com as instruções!", Toast.LENGTH_SHORT).show();
                                }

                                hideProgressDialog();
                            }
                        });
            }
        });
    }

    public void onBackPressed() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();

        return;
    }

}