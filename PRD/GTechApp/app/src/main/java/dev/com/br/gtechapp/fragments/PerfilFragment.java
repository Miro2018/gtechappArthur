package dev.com.br.gtechapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.activity.InputUserInfoActivity;
import dev.com.br.gtechapp.activity.LoginActivity;
import dev.com.br.gtechapp.entity.Usuario;
import dev.com.br.gtechapp.persistence.PrefsUsuario;


public class PerfilFragment extends Fragment  {

    private GoogleApiClient mGoogleApiClient;

    TextView txtNomUsuario, txtDatNascimento2, txtGenero2, txtPeso2, txtAltura2, txtDatInicioDiabete2, txtTipoDiabete2, txtHiper2, txtHiper3, txtHipo2, txtHipo3;

    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;

    private PrefsUsuario prefsUsuario;

    public static PerfilFragment newInstance() {
        PerfilFragment f = new PerfilFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        mAuth = FirebaseAuth.getInstance();

        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());

        iniciaBotaoLogOut(view);

        iniciaBotaoLogOut(view);

        iniciaViews(view);

        iniFirebase();

        return view;
    }

    private void iniFirebase(){
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                prefsUsuario = new PrefsUsuario(getActivity());

                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                if(usuario.getNomUsuario() != null)
                    txtNomUsuario.setText(usuario.getNomUsuario());
                if(usuario.getDatNascimento() != null)
                    txtDatNascimento2.setText(usuario.getDatNascimento());
                if(usuario.getNomGenero() != null)
                    txtGenero2.setText(usuario.getNomGenero());
                if(usuario.getValPeso() != null)
                    txtPeso2.setText(usuario.getValPeso().toString());
                if(usuario.getValAltura() != null)
                    txtAltura2.setText(usuario.getValAltura().toString());
                if(usuario.getDatInicioDiabete() != null)
                    txtDatInicioDiabete2.setText(usuario.getDatInicioDiabete().toString());
                if(usuario.getNomTipoDiabete() != null)
                    txtTipoDiabete2.setText(usuario.getNomTipoDiabete());
                if(usuario.getValHiper() != 0){
                    txtHiper2.setText(String.valueOf(usuario.getValHiper()));
                    prefsUsuario.setHiper(usuario.getValHiper());

                    txtHiper2.setVisibility(View.VISIBLE);
                    txtHiper3.setError(null);
                    txtHiper3.setVisibility(View.INVISIBLE);
                }else{
                    txtHiper2.setText("0");
                    txtHiper2.setVisibility(View.INVISIBLE);
                    txtHiper3.setVisibility(View.VISIBLE);
                    txtHiper3.setError("140 por padrão");
                }
                if(usuario.getValHipo() != 0){
                    txtHipo2.setText(String.valueOf(usuario.getValHipo()));
                    prefsUsuario.setHipo(usuario.getValHipo());

                    txtHipo2.setVisibility(View.VISIBLE);
                    txtHipo3.setVisibility(View.INVISIBLE);
                    txtHipo3.setError(null);
                }else{
                    txtHipo2.setText("0");
                    txtHipo2.setVisibility(View.INVISIBLE);
                    txtHipo3.setVisibility(View.VISIBLE);
                    txtHipo3.setError("70 por padrão");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(getClass().getName(), "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mUserReference.addValueEventListener(userListener);
    }

    private View.OnClickListener fragmentListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
//                case R.id.btnSair:
                case R.id.btn_sign_out_google:
                    revokeAccess();
                    break;
                case R.id.btn_fb_login_button:
                case R.id.btn_sign_out_firebase:
                case R.id.llSair:

                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();

                    break;
                default:
                    break;
            }
        }
    };

    private View.OnClickListener listenerCamposEditaveis = new View.OnClickListener() {
        public void onClick(View v) {

            Intent intent = new Intent(getActivity(), InputUserInfoActivity.class);

            switch (v.getId()) {
                case R.id.nom_usuario:
                    intent.putExtra("input_field", Usuario.NOM_USUARIO);
                    intent.putExtra("input_type", InputUserInfoActivity.TEXT_INPUT);
                    intent.putExtra("input_value", txtNomUsuario.getText());
                    intent.putExtra("nom_campo", "Nome:");
                    break;
                case R.id.nom_genero:
                    intent.putExtra("input_field", Usuario.NOM_GENERO);
                    intent.putExtra("input_type", InputUserInfoActivity.SPINNER_INPUT);
                    intent.putExtra("input_value", txtGenero2.getText());
                    intent.putExtra("nom_campo", "Gênero:");
                    break;
                case R.id.nom_tipo_diabete:
                    intent.putExtra("input_field", Usuario.NOM_TIPO_DIABETE);
                    intent.putExtra("input_type", InputUserInfoActivity.SPINNER_INPUT);
                    intent.putExtra("input_value", txtTipoDiabete2.getText());
                    intent.putExtra("nom_campo", "Tipo Diabete:");
                    break;
                case R.id.dat_nascimento:
                    intent.putExtra("input_field", Usuario.DAT_NASCIMENTO);
                    intent.putExtra("input_type", InputUserInfoActivity.DATE_INPUT);
                    intent.putExtra("input_value", txtDatNascimento2.getText());
                    intent.putExtra("nom_campo", "Data Nascimento:");
                    break;
                case R.id.dat_inicio_diabete:
                    intent.putExtra("input_field", Usuario.DAT_INI_DIABETE);
                    intent.putExtra("input_type", InputUserInfoActivity.DATE_INPUT);
                    intent.putExtra("input_value", txtDatInicioDiabete2.getText());
                    intent.putExtra("nom_campo", "Data Início Diabete:");
                    break;
                case R.id.val_peso:
                    intent.putExtra("input_field", Usuario.VAL_PESO);
                    intent.putExtra("input_type", InputUserInfoActivity.NUMBER_DECIMAL_INPUT);
                    intent.putExtra("input_value", txtPeso2.getText());
                    intent.putExtra("nom_campo", "Peso(kg):");
                    break;
                case R.id.val_altura:
                    intent.putExtra("input_field", Usuario.VAL_ALTURA);
                    intent.putExtra("input_type", InputUserInfoActivity.NUMBER_DECIMAL_INPUT);
                    intent.putExtra("input_value", txtAltura2.getText());
                    intent.putExtra("nom_campo", "Altura(cm):");
                    break;
                case R.id.val_hiper:
                    intent.putExtra("input_field", Usuario.VAL_HIPER);
                    intent.putExtra("input_type", InputUserInfoActivity.NUMBER_INPUT);
                    intent.putExtra("input_value", txtHiper2.getText());
                    intent.putExtra("nom_campo", "Hiper(mg/dl):");
                    break;
                case R.id.val_hipo:
                    intent.putExtra("input_field", Usuario.VAL_HIPO);
                    intent.putExtra("input_type", InputUserInfoActivity.NUMBER_INPUT);
                    intent.putExtra("input_value", txtHipo2.getText());
                    intent.putExtra("nom_campo", "Hipo(mg/dl):");
                    break;
                default:
                    break;
            }

            startActivity(intent);
        }
    };

    private void iniciaViews(View v){

        txtNomUsuario = (TextView) v.findViewById(R.id.txtNomUsuario);
        txtDatNascimento2 = (TextView) v.findViewById(R.id.txtDatNasc2);
        txtGenero2 = (TextView) v.findViewById(R.id.txtGenero2);
        txtPeso2 = (TextView) v.findViewById(R.id.txtPeso2);
        txtAltura2 = (TextView) v.findViewById(R.id.txtAltura2);
        txtDatInicioDiabete2 = (TextView) v.findViewById(R.id.txtInicioDiabete2);
        txtTipoDiabete2 = (TextView) v.findViewById(R.id.txtTipoDiabete2);
        txtHiper2 = (TextView) v.findViewById(R.id.txtHiper2);
        txtHiper3 = (TextView) v.findViewById(R.id.txtHiper3);
        txtHipo2 = (TextView) v.findViewById(R.id.txtHipo2);
        txtHipo3 = (TextView) v.findViewById(R.id.txtHipo3);

        v.findViewById(R.id.nom_usuario).setOnClickListener(listenerCamposEditaveis);
        v.findViewById(R.id.nom_genero).setOnClickListener(listenerCamposEditaveis);
        v.findViewById(R.id.nom_tipo_diabete).setOnClickListener(listenerCamposEditaveis);
        v.findViewById(R.id.dat_nascimento).setOnClickListener(listenerCamposEditaveis);
        v.findViewById(R.id.dat_inicio_diabete).setOnClickListener(listenerCamposEditaveis);
        v.findViewById(R.id.val_peso).setOnClickListener(listenerCamposEditaveis);
        v.findViewById(R.id.val_altura).setOnClickListener(listenerCamposEditaveis);
        v.findViewById(R.id.val_hiper).setOnClickListener(listenerCamposEditaveis);
        v.findViewById(R.id.val_hipo).setOnClickListener(listenerCamposEditaveis);

    }

    private void iniciaBotaoLogOut(View view){
        String provider = FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0);

        Button googleButton = view.findViewById(R.id.btn_sign_out_google);
        LoginButton loginButton = view.findViewById(R.id.btn_fb_login_button);
        TextView firebaseSairButton = view.findViewById(R.id.btn_sign_out_firebase);
        LinearLayout llFirebaseSairButton = view.findViewById(R.id.llSair);

        if(provider.equals("google.com"))
        {
            googleButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            firebaseSairButton.setVisibility(View.INVISIBLE);
            googleButton.setOnClickListener(fragmentListener);

        }else if(provider.equals("facebook.com")) {

            loginButton.setVisibility(View.VISIBLE);
            googleButton.setVisibility(View.INVISIBLE);
            firebaseSairButton.setVisibility(View.INVISIBLE);
            loginButton.setOnClickListener(fragmentListener);

        } else {
            loginButton.setVisibility(View.INVISIBLE);
            googleButton.setVisibility(View.INVISIBLE);
            firebaseSairButton.setVisibility(View.VISIBLE);
            firebaseSairButton.setOnClickListener(fragmentListener);
            llFirebaseSairButton.setOnClickListener(fragmentListener);
        }
    }

    private void revokeAccess() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        LoginManager.getInstance().logOut();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
    }
}
