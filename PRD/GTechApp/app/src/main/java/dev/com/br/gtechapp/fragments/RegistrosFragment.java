package dev.com.br.gtechapp.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.activity.BleActivity;
import dev.com.br.gtechapp.activity.InputGlicoseActivity;
import dev.com.br.gtechapp.activity.InputInsulinaActivity;
import dev.com.br.gtechapp.activity.InputNotaActivity;
import dev.com.br.gtechapp.adapter.RegistrosAdapter;
import dev.com.br.gtechapp.adapter.RegistrosRecyclerTouchListener;
import dev.com.br.gtechapp.entity.Registro;
import dev.com.br.gtechapp.entity.RegistroGlicose;
import dev.com.br.gtechapp.entity.RegistroInsulina;
import dev.com.br.gtechapp.entity.RegistroNota;
import dev.com.br.gtechapp.persistence.DadosFirebase;
import dev.com.br.gtechapp.persistence.DatabaseHelper;

public class RegistrosFragment extends Fragment {

    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 999;

    Button btnGlicose, btnBluetooth, btnInsulina, btnNota;

    private RecyclerView rcvRegistros;
    LinearLayoutManager mLayoutManager;
    private RegistrosAdapter rAdapter;
    private List<RegistroGlicose> listaRegistroGlicose;
    private List<RegistroInsulina> listaRegistroInsulina;
    private List<RegistroNota> listaRegistroNota;
    private List<Registro> listaRegistro;


    DadosFirebase dadosFirebase;
    private FirebaseAuth mAuth;

    //Thread
    AtualizarRegistros AtualizarRegistrosTask;

    //Indice Atualizar Registros (Glicose + Insulina + Nota)
    private boolean blnAtualizarGlicose = false;
    private boolean blnAtualizarInsulina = false;
    private boolean blnAtualizarNota = false;

    public static RegistrosFragment newInstance() {
        RegistrosFragment f = new RegistrosFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registros, container, false);

        dadosFirebase = new DadosFirebase();
        mAuth = FirebaseAuth.getInstance();

        btnBluetooth = (Button) view.findViewById(R.id.btnBluetooth);
        btnBluetooth.setOnClickListener(fragmentListener);

        btnGlicose = (Button) view.findViewById(R.id.btnGlicose);
        btnGlicose.setOnClickListener(fragmentListener);

        btnInsulina = (Button) view.findViewById(R.id.btnInsulina);
        btnInsulina.setOnClickListener(fragmentListener);

        btnNota = (Button) view.findViewById(R.id.btnNota);
        btnNota.setOnClickListener(fragmentListener);

        rcvRegistros = (RecyclerView) view.findViewById(R.id.rcvRegistros);
        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rcvRegistros.setLayoutManager(mLayoutManager);
        rcvRegistros.setItemAnimator(new DefaultItemAnimator());
        rcvRegistros.addOnItemTouchListener(new RegistrosRecyclerTouchListener(getActivity().getApplicationContext(), rcvRegistros, new RegistrosRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(listaRegistro.get(position).getTipRegistro() == 1){
                    Intent intent = new Intent(getActivity().getApplicationContext(), InputGlicoseActivity.class);
                    intent.putExtra("idRegistro", listaRegistro.get(position).getIdRegistro());
                    getActivity().getApplicationContext().startActivity(intent);
                }else if(listaRegistro.get(position).getTipRegistro() == 2){
                    Intent intent = new Intent(getActivity().getApplicationContext(), InputInsulinaActivity.class);
                    intent.putExtra("idRegistro", listaRegistro.get(position).getIdRegistro());
                    getActivity().getApplicationContext().startActivity(intent);
                }else if(listaRegistro.get(position).getTipRegistro() == 3){
                    Intent intent = new Intent(getActivity().getApplicationContext(), InputNotaActivity.class);
                    intent.putExtra("idRegistro", listaRegistro.get(position).getIdRegistro());
                    getActivity().getApplicationContext().startActivity(intent);
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        initFireBase();

        return view;
    }

    private void initFireBase(){

        blnAtualizarGlicose = false;
        blnAtualizarInsulina = false;
        blnAtualizarNota = false;

        //Glicose
        Query dbFirebaseQuery = FirebaseDatabase.getInstance().getReference()
                .child("user-registros-glicose")
                .child(mAuth.getUid())
                .orderByChild("datReg")
                .limitToLast(300);

        ValueEventListener registroGlicoseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listaRegistroGlicose = dadosFirebase.obterListaRegistrosGlicose(dataSnapshot);

//                Collections.sort(listaRegistroGlicose, Collections.reverseOrder());

                blnAtualizarGlicose = true;
                atualizaListaDeRegistros();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(getClass().getName(), "loadRegistrosGlicose:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbFirebaseQuery.addValueEventListener(registroGlicoseListener);

        //Insulina
        Query dbFirebaseQueryInsulina = FirebaseDatabase.getInstance().getReference()
                .child("user-registros-insulina")
                .child(mAuth.getUid())
                .orderByChild("datReg")
                .limitToLast(300);

        ValueEventListener registroInsulinaListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listaRegistroInsulina = dadosFirebase.obterListaRegistrosInsulina(dataSnapshot);

//                Collections.sort(listaRegistroGlicose, Collections.reverseOrder());

                blnAtualizarInsulina = true;
                atualizaListaDeRegistros();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(getClass().getName(), "loadRegistrosInsulina:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbFirebaseQueryInsulina.addValueEventListener(registroInsulinaListener);

        //Nota
        Query dbFirebaseQueryNota = FirebaseDatabase.getInstance().getReference()
                .child("user-registros-nota")
                .child(mAuth.getUid())
                .orderByChild("datReg")
                .limitToLast(300);

        ValueEventListener registroNotaListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listaRegistroNota = dadosFirebase.obterListaRegistrosNota(dataSnapshot);

                blnAtualizarNota = true;
                atualizaListaDeRegistros();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(getClass().getName(), "loadRegistrosNota:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbFirebaseQueryNota.addValueEventListener(registroNotaListener);
    }

    private View.OnClickListener fragmentListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnGlicose:
                    startActivity(new Intent(getActivity(), InputGlicoseActivity.class));
                    break;
                case R.id.btnBluetooth:
                    if (Build.VERSION.SDK_INT >= 23) {
                        // Marshmallow+ Permission APIs
                        fuckMarshMallow();

                        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            startActivity(new Intent(getActivity(), BleActivity.class));
                        }

                    }else{

                        startActivity(new Intent(getActivity(), BleActivity.class));
                    }
                    break;
                case R.id.btnInsulina:
                    startActivity(new Intent(getActivity(), InputInsulinaActivity.class));
                    break;
                case R.id.btnNota:
                    startActivity(new Intent(getActivity(), InputNotaActivity.class));
                    break;
                default:
                    break;
            }
        }
    };

    private void atualizaListaDeRegistros(){
        Log.d(getClass().getName(), "Lista Glicose pronta: " + blnAtualizarGlicose + ", Lista Insulina pronta: " + blnAtualizarInsulina + ", Lista Nota pronta: " + blnAtualizarNota);
        if(blnAtualizarGlicose && blnAtualizarInsulina && blnAtualizarNota){
            Log.d(getClass().getName(), "Listas prontas, atualizando Lista Registros");
            if(AtualizarRegistrosTask == null){
                AtualizarRegistrosTask = new AtualizarRegistros(getActivity());
                AtualizarRegistrosTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else{
                Log.d(getClass().getName(), "AtualizarRegistrosTask já em execução");
            }
        }else{
            Log.d(getClass().getName(), "Listas não estão prontas para atualizar Registros");
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void fuckMarshMallow() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Show Location");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {

                String message = "Requer permissão para acessar: " + permissionsNeeded.get(0);

                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {

        if (getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    /**
     * Thread que atualiza registros
     */
    private class AtualizarRegistros extends AsyncTask<Void, Void, Boolean> {
        Context context;
        DatabaseHelper db;
        ProgressBar progress;

        public AtualizarRegistros (Context context){
            this.context = context;
            this.db = new DatabaseHelper(context);
            progress = (ProgressBar) getActivity().findViewById(R.id.pgbRegistros);
        }

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground (Void... v){
            try {

                listaRegistro = new ArrayList<>();

                if(listaRegistroGlicose != null && listaRegistroGlicose.size() > 0){
                    for (int i = 0; i < listaRegistroGlicose.size(); i++){
                        Registro r = new Registro();
                        r.setDatReg(listaRegistroGlicose.get(i).getDatReg());
                        r.setHorRegistro(listaRegistroGlicose.get(i).getHorRegistro());
                        r.setValRegistro(listaRegistroGlicose.get(i).getValRegistro());
                        r.setTxtExtraInfo(listaRegistroGlicose.get(i).getNomEstadoAlimentacao());
                        r.setTipInput(listaRegistroGlicose.get(i).getTipInput());
                        r.setTipRegistro(1);
                        r.setIdRegistro(listaRegistroGlicose.get(i).getIdRegistro());
                        r.setNomEstadoAlimentacao(listaRegistroGlicose.get(i).getNomEstadoAlimentacao());

                        listaRegistro.add(r);
                    }
                }

                if(listaRegistroInsulina != null && listaRegistroInsulina.size() > 0){
                    for (int i = 0; i < listaRegistroInsulina.size(); i++){
                        Registro r = new Registro();
                        r.setIdRegistro(listaRegistroInsulina.get(i).getIdRegistro());
                        r.setDatReg(listaRegistroInsulina.get(i).getDatReg());
                        r.setHorRegistro(listaRegistroInsulina.get(i).getHorRegistro());
                        r.setValRegistro(listaRegistroInsulina.get(i).getValDosagem());
                        r.setTxtExtraInfo(db.obterNomTipoInsulina(listaRegistroInsulina.get(i).getCodTipoInsulina()) + " - " + db.obterNomProdutoInsulina(listaRegistroInsulina.get(i).getCodProdutoInsulina()));
                        r.setTipInput(0);
                        r.setTipRegistro(2);

                        listaRegistro.add(r);
                    }
                }

                if(listaRegistroNota != null && listaRegistroNota.size() > 0){
                    for (int i = 0; i < listaRegistroNota.size(); i++){
                        Registro r = new Registro();
                        r.setDatReg(listaRegistroNota.get(i).getDatReg());
                        r.setHorRegistro(listaRegistroNota.get(i).getHorRegistro());
                        r.setCodTipoNota(listaRegistroNota.get(i).getCodTipoNota());
                        r.setTipRegistro(3);
                        r.setIdRegistro(listaRegistroNota.get(i).getIdRegistro());
                        r.setTxtNotaNota(listaRegistroNota.get(i).getTxtNota());

                        listaRegistro.add(r);
                    }
                }

                Collections.sort(listaRegistro, Collections.reverseOrder());

                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onCancelled() {
            AtualizarRegistrosTask = null;
            progress.setVisibility(View.GONE);
            super.onCancelled();
            Log.d(getClass().getName(),"AtualizarRegistrosTask onCancelled()");
        }

        @Override
        protected void onPostExecute(Boolean blnResult){
            progress.setVisibility(View.GONE);
            if(isAdded() && !isDetached() && !isRemoving() && !getActivity().isFinishing() && !getActivity().isDestroyed()){
                if(blnResult) {
                    if(listaRegistro != null && listaRegistro.size() > 0){
                        rAdapter = new RegistrosAdapter(getActivity(), listaRegistro);
                        rcvRegistros.setAdapter(rAdapter);
                        rAdapter.notifyDataSetChanged();
                    }
                }else {
                    Toast.makeText(getActivity(), "Ops! Ocorreu um erro ao carregar os registros", Toast.LENGTH_LONG).show();
                }
            }
            AtualizarRegistrosTask = null;
        }
    }
}

