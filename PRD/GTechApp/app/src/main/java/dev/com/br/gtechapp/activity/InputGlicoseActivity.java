package dev.com.br.gtechapp.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.entity.RegistroGlicose;
import dev.com.br.gtechapp.service.Util;
import dev.com.br.gtechapp.persistence.DadosFirebase;

public class InputGlicoseActivity extends BaseActivity implements View.OnClickListener{

    //Calendário
    private int mYear, mMonth, mDay, mHour, mMinute, mSecond;

    //Botão adicionar item_registro
    private Button btnConfirmar;

    //Campos editaveis
    private EditText edtValRegistro, edtNota;
    private TextView edtDatRegistro, edtHorRegistro;
    private Spinner spinnerAlimentacao;
    private LinearLayout llValor, llData, llHora, llAlimentacao, llNota;
    private  TextView txtValor, txtData, txtHora, txtAlimentacao, txtNota;


    private DadosFirebase dadosFirebase;
    private DatabaseReference mGlicoseReference;
    private FirebaseAuth mAuth;

    private String idRegistro;
    private RegistroGlicose rGlicoseEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_glicose);

        dadosFirebase = new DadosFirebase();
        mAuth = FirebaseAuth.getInstance();

        idRegistro = getIntent().getStringExtra("idRegistro");

        definirObjetos();

        obterDatHoraAgora();

        if(idRegistro != null && !idRegistro.isEmpty()){
            setUpFirebase();
        }

        setUpActionBar();
    }

    private void setUpActionBar(){

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary) ));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Glicose");
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void setUpFirebase(){
        //Glicose
        mGlicoseReference = FirebaseDatabase.getInstance().getReference()
                .child("user-registros-glicose")
                .child(mAuth.getUid())
                .child(idRegistro);

        ValueEventListener registroGlicoseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                rGlicoseEdt = dataSnapshot.getValue(RegistroGlicose.class);

                if(rGlicoseEdt != null){
                    if(rGlicoseEdt.getValRegistro() != 0)
                        edtValRegistro.setText(String.valueOf(rGlicoseEdt.getValRegistro()));
                    if(rGlicoseEdt.getDatReg() != null)
                        edtDatRegistro.setText(Util.converteDataSqlParaBr(rGlicoseEdt.getDatReg()));
                    if(rGlicoseEdt.getHorRegistro() != null)
                        edtHorRegistro.setText(rGlicoseEdt.getHorRegistro());
                    if(rGlicoseEdt.getNomEstadoAlimentacao() != null)
                        spinnerAlimentacao.setSelection(Util.getIndexOf( getResources().getStringArray(R.array.lista_estado_alimentacao), rGlicoseEdt.getNomEstadoAlimentacao()));
                    if(rGlicoseEdt.getTxtNota() != null)
                        edtNota.setText(rGlicoseEdt.getTxtNota());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(getClass().getName(), "rGlicoseEdt:onCancelled", databaseError.toException());
                // ...
            }
        };
        mGlicoseReference.addValueEventListener(registroGlicoseListener);
    }

    private void obterDatHoraAgora(){
        // Get Current Date
        final Calendar c = Calendar.getInstance(Locale.getDefault());
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1; //função retorna de 0 - 11
        mDay = c.get(Calendar.DAY_OF_MONTH);

        String strMes;
        String strDia;

        if (mMonth < 10)
        {
            strMes = "0" + (mMonth);
        }else {
            strMes = (mMonth) + "";
        }

        if (mDay < 10 ){
            strDia = "0" + mDay;
        } else{
            strDia = mDay + "";
        }

        edtDatRegistro.setText(strDia + "/" + strMes + "/" + mYear);

        // Get Current Time
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mSecond = c.get(Calendar.SECOND);

        String strHora;
        String strMinuto;
        String strSecond;

        if (mHour < 10){
            strHora = "0" + mHour;
        }else{
            strHora = mHour + "";
        }

        if (mMinute < 10){
            strMinuto = "0" + mMinute;
        }else{
            strMinuto = mMinute + "";
        }

        if (mSecond < 10){
            strSecond = "0" + mSecond;
        }else{
            strSecond = mSecond + "";
        }

        edtHorRegistro.setText(strHora + ":" + strMinuto + ":" + strSecond);
    }

    private void definirObjetos(){

        edtValRegistro = findViewById(R.id.edtValRegistro);
        edtValRegistro.requestFocus();
        edtValRegistro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    escondeTeclado(v);
                }
            }
        });

        llValor = findViewById(R.id.llValRegistro);
        llValor.setOnClickListener(this);
        txtValor = findViewById(R.id.txtValResgistro);
        txtValor.setOnClickListener(this);

        spinnerAlimentacao = (Spinner) findViewById(R.id.spnAlimentacao);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lista_estado_alimentacao, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlimentacao.setAdapter(adapter);

        llAlimentacao = findViewById(R.id.llEstadoAlimentacao);
        llAlimentacao.setOnClickListener(this);
        txtAlimentacao = findViewById(R.id.txtEstadoAlimentacao);
        txtAlimentacao.setOnClickListener(this);

        btnConfirmar = findViewById(R.id.btnConfirmar);
        btnConfirmar.setOnClickListener(this);

        edtDatRegistro = findViewById(R.id.edtDatRegsitro);
        edtDatRegistro.setOnClickListener(this);
        llData = findViewById(R.id.llDatRegistro);
        llData.setOnClickListener(this);
        txtData = findViewById(R.id.txtDatRegistro);
        txtData.setOnClickListener(this);

        edtHorRegistro = findViewById(R.id.edtHorRegsitro);
        edtHorRegistro.setOnClickListener(this);
        llHora = findViewById(R.id.llHorRegistro);
        llHora.setOnClickListener(this);
        txtHora = findViewById(R.id.txtHorRegistro);
        txtHora.setOnClickListener(this);

        llNota = findViewById(R.id.llNota);
        llNota.setOnClickListener(this);
        txtNota = findViewById(R.id.txtNota);
        txtNota.setOnClickListener(this);
        edtNota = findViewById(R.id.edtNota);
        edtNota.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        if(view.getId() != R.id.llValRegistro || view.getId() != R.id.txtValResgistro || view.getId() != R.id.edtValRegistro ||
                view.getId() != R.id.llNota || view.getId() != R.id.txtNota || view.getId() != R.id.edtNota){
            escondeTeclado(view);
        }

        switch (view.getId()) {
            case R.id.btnConfirmar:
                if (validarCampos()) {

                    RegistroGlicose rGlicose = new RegistroGlicose();
                    rGlicose.setDatReg(Util.converteDataBrParaSql(edtDatRegistro.getText().toString()));
                    rGlicose.setHorRegistro(edtHorRegistro.getText().toString());
                    rGlicose.setValRegistro(Integer.parseInt(edtValRegistro.getText().toString()));
                    rGlicose.setNomEstadoAlimentacao(spinnerAlimentacao.getSelectedItem().toString());
                    rGlicose.setTxtNota(edtNota.getText().toString());

                    dadosFirebase = new DadosFirebase();

                    if(idRegistro != null && !idRegistro.isEmpty()){
                        if(dadosFirebase.atualizaRegistroGlicose(rGlicose, idRegistro)){
                            finish();
                        }else{
                            Toast.makeText(this, "Erro ao tentar atualizar registro", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        if(dadosFirebase.novoRegistroGlicose(rGlicose)){
                            finish();
                        }else {
                            Toast.makeText(this, "Erro ao tentar gravar registro", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;
            case R.id.edtDatRegsitro:
            case R.id.txtDatRegistro:
            case R.id.llDatRegistro:
                mostrarDatePicker(view);
                break;
            case R.id.edtHorRegsitro:
            case R.id.txtHorRegistro:
            case R.id.llHorRegistro:
                mostrarTimePircker(view);
                break;
            case R.id.llValRegistro:
            case R.id.txtValResgistro:
                edtValRegistro.requestFocus();
                mostraTeclado(edtValRegistro);
                break;
            case R.id.llEstadoAlimentacao:
            case R.id.txtEstadoAlimentacao:
                spinnerAlimentacao.performClick();
                break;
            case R.id.llNota:
            case R.id.txtNota:
            case R.id.edtNota:
                edtNota.requestFocus();
                mostraTeclado(edtNota);
                break;

        }
    }

    private void mostrarDatePicker(View v){

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String strMes;
                        String strDia;

                        int intMes = monthOfYear + 1;

                        if (intMes < 10)
                        {
                            strMes = "0" + (intMes);
                        }else {
                            strMes = (intMes) + "";
                        }

                        if (dayOfMonth < 10 ){
                            strDia = "0" + dayOfMonth;
                        } else{
                            strDia = dayOfMonth + "";
                        }

                        DateFormat datFormatCompare = new SimpleDateFormat("yyyyMMdd");

                        String strDatSelecionada = year + strMes + strDia;
                        String strDatAtual = datFormatCompare.format(new Date());

                        if(strDatSelecionada.compareTo(strDatAtual) == 1){
                            DateFormat datFormat = new SimpleDateFormat("dd/MM/yyyy");
                            edtDatRegistro.setText(datFormat.format(new Date()));
                        }else{
                            edtDatRegistro.setText(strDia + "/" + strMes + "/" + year);
                        }

                    }
                }, mYear, mMonth - 1, mDay);
        datePickerDialog.show();
    }

    private void mostrarTimePircker(View v){

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, AlertDialog.THEME_HOLO_DARK,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        String strHora;
                        String strMinuto;

                        if (hourOfDay < 10){
                            strHora = "0" + hourOfDay;
                        }else{
                            strHora = hourOfDay + "";
                        }

                        if (minute < 10){
                            strMinuto = "0" + minute;
                        }else{
                            strMinuto = minute + "";
                        }


                        edtHorRegistro.setText(strHora + ":" + strMinuto + ":00");
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();

    }

    private boolean validarCampos(){

        if(edtValRegistro.getText().toString().equals("")) {
            edtValRegistro.setError("Insira um valor");
            return false;
        } else if(edtNota.getText() != null && edtNota.getText().toString().length() > 96){
            edtNota.setError("Máximo de 96 caracteres!");
            return false;
        }else{
            try{
                Integer.parseInt(edtValRegistro.getText().toString());
            }catch (Exception e){
                edtValRegistro.setError("Inserir um número inteiro");
                return false;
            }
        }

        if(spinnerAlimentacao.getSelectedItem().toString().equals("")) {
//            spinnerAlimentacao.setError("Selecione um estado de alimentação");
            return false;
        }

        return true;
    }
}
