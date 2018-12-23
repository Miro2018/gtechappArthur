package dev.com.br.gtechapp.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.entity.ProdutoInsulina;
import dev.com.br.gtechapp.entity.RegistroInsulina;
import dev.com.br.gtechapp.entity.TipoInsulina;
import dev.com.br.gtechapp.persistence.DadosFirebase;
import dev.com.br.gtechapp.persistence.DatabaseHelper;
import dev.com.br.gtechapp.service.ProdutoInsulinaService;
import dev.com.br.gtechapp.service.Util;

public class InputInsulinaActivity extends BaseActivity implements View.OnClickListener {

    //Calendário
    private int mYear, mMonth, mDay, mHour, mMinute, mSecond;

    private Button btnConfirmar;
    private EditText edtValDosagem;
    private TextView edtDatRegistro, edtHorRegistro;
    Spinner spinnerTipoInsulina, spinnerProdutoInsulina;
    private LinearLayout llDosagem, llData, llHora, llTipoInsulina, llProdutoInsulina;
    private  TextView txtDosagem, txtData, txtHora, txtTipoInsulina, txtProdutoInsulina;

    DatabaseHelper db;

    private DadosFirebase dadosFirebase;
    private DatabaseReference mGlicoseReference;
    private FirebaseAuth mAuth;

    private String idRegistro;
    private RegistroInsulina rInsulinaEdt;

    private ArrayAdapter spinnerArrayAdapterProduto;
    private ArrayAdapter spinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_insulina);

        db = new DatabaseHelper(this);

        dadosFirebase = new DadosFirebase();
        mAuth = FirebaseAuth.getInstance();

        idRegistro = getIntent().getStringExtra("idRegistro");

        if(idRegistro != null && !idRegistro.isEmpty()){
            setUpFirebase();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    definirObjetos();
                    obterDatHoraAgora();
                    definirObjetos();
                }
            }, 500);
        }else{
            definirObjetos();
            obterDatHoraAgora();
        }

//        obterDatHoraAgora();

        setUpActionBar();
    }

    private void setUpActionBar(){

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary) ));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Insulina");
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
    }

    private void setUpFirebase(){

        //Glicose
        mGlicoseReference = FirebaseDatabase.getInstance().getReference()
                .child("user-registros-insulina")
                .child(mAuth.getUid())
                .child(idRegistro);

        ValueEventListener registroInsulinaListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                rInsulinaEdt = dataSnapshot.getValue(RegistroInsulina.class);

//                if(rInsulinaEdt.getValDosagem() != null)
//                    edtValDosagem.setText(rInsulinaEdt.getValDosagem().toString());
//                if(rInsulinaEdt.getDatReg() != null)
//                    edtDatRegistro.setText(rInsulinaEdt.getDatReg());
//                if(rInsulinaEdt.getHorRegistro() != null)
//                    edtHorRegistro.setText(rInsulinaEdt.getHorRegistro());
//                if(rInsulinaEdt.getCodTipoInsulina() > 0) {
////                    int count = spinnerTipoInsulina.getCount();
////                    int codTipoInsulina = rInsulinaEdt.getCodTipoInsulina();
////                    spinnerTipoInsulina.setSelection(rInsulinaEdt.getCodTipoInsulina() - 1);
//                }
//                if(rInsulinaEdt.getCodProdutoInsulina() > 0) {
////                    List<ProdutoInsulina> listaProdutoInsulina = ProdutoInsulinaService.obterListaProdutoInsulina(rInsulinaEdt.getCodTipoInsulina(), InputInsulinaActivity.this);
////                    spinnerArrayAdapterProduto = new ArrayAdapter(InputInsulinaActivity.this,
////                            android.R.layout.simple_spinner_dropdown_item,
////                            listaProdutoInsulina);
////                    int count = spinnerProdutoInsulina.getCount();
////                    int codProdutoInsulina = rInsulinaEdt.getCodProdutoInsulina();
////                    int codIndiceProdutoInsulina = ProdutoInsulinaService.obterIndiceListaProdutoInsulinaPorId(listaProdutoInsulina, rInsulinaEdt.getCodProdutoInsulina());
////                    spinnerProdutoInsulina.setAdapter(spinnerArrayAdapterProduto);
////                    spinnerProdutoInsulina.getAdapter().notify();
////                    spinnerProdutoInsulina.setSelection(ProdutoInsulinaService.obterIndiceListaProdutoInsulinaPorId(listaProdutoInsulina, rInsulinaEdt.getCodProdutoInsulina()));
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(getClass().getName(), "rInsulinaEdt:onCancelled", databaseError.toException());
                // ...
            }
        };
        mGlicoseReference.addValueEventListener(registroInsulinaListener);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void obterDatHoraAgora(){

        final Calendar c = Calendar.getInstance();

        // Get Current Date
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1;
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

        edtValDosagem = findViewById(R.id.edtDosagem);
        edtValDosagem.requestFocus();
        edtValDosagem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    escondeTeclado(v);
                }
            }
        });

        llDosagem = findViewById(R.id.llDosagem);
        llDosagem.setOnClickListener(this);
        txtDosagem = findViewById(R.id.txtDosagem);
        txtDosagem.setOnClickListener(this);

        final List<TipoInsulina> listaTipoInsulina = db.obterTiposInsulina();

        spinnerTipoInsulina = (Spinner) findViewById(R.id.spnTipoInsulina);
        spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                listaTipoInsulina);
        spinnerTipoInsulina.setAdapter(spinnerArrayAdapter);
        if(rInsulinaEdt != null && rInsulinaEdt.getCodTipoInsulina() > 0) {
            spinnerTipoInsulina.setSelection(rInsulinaEdt.getCodTipoInsulina() - 1);
        }
        spinnerTipoInsulina.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<ProdutoInsulina> listaProdutoInsulina = ProdutoInsulinaService.obterListaProdutoInsulina(listaTipoInsulina.get(position).getCodTipoInsulina(), InputInsulinaActivity.this);
                spinnerProdutoInsulina = (Spinner) findViewById(R.id.spnProdutoInsulina);
                spinnerArrayAdapterProduto = new ArrayAdapter(InputInsulinaActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        listaProdutoInsulina);
                spinnerProdutoInsulina.setAdapter(spinnerArrayAdapterProduto);

                if(rInsulinaEdt != null && rInsulinaEdt.getCodProdutoInsulina() > 0) {
                    spinnerProdutoInsulina.setSelection(ProdutoInsulinaService.obterIndiceListaProdutoInsulinaPorId(listaProdutoInsulina, rInsulinaEdt.getCodProdutoInsulina()));
                }

                llTipoInsulina = findViewById(R.id.llTipoInsulina);
                llTipoInsulina.setOnClickListener(InputInsulinaActivity.this);
                txtTipoInsulina = findViewById(R.id.txtTipoInsulina);
                txtTipoInsulina.setOnClickListener(InputInsulinaActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(listaTipoInsulina != null && listaTipoInsulina.size() > 0)
        {
            List<ProdutoInsulina> listaProdutoInsulina = db.obterProdutosInsulina(listaTipoInsulina.get(0).getCodTipoInsulina());
            spinnerProdutoInsulina = (Spinner) findViewById(R.id.spnProdutoInsulina);
            spinnerArrayAdapterProduto = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item,
                    listaProdutoInsulina);
            spinnerProdutoInsulina.setAdapter(spinnerArrayAdapterProduto);
            llTipoInsulina = findViewById(R.id.llTipoInsulina);
            llTipoInsulina.setOnClickListener(this);
            txtTipoInsulina = findViewById(R.id.txtTipoInsulina);
            txtTipoInsulina.setOnClickListener(this);
        }

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

        if(rInsulinaEdt != null){
            if(rInsulinaEdt.getValDosagem() != 0)
                edtValDosagem.setText(String.valueOf(rInsulinaEdt.getValDosagem()));
            if(rInsulinaEdt.getDatReg() != null)
                edtDatRegistro.setText(Util.converteDataSqlParaBr(rInsulinaEdt.getDatReg()));
            if(rInsulinaEdt.getHorRegistro() != null)
                edtHorRegistro.setText(rInsulinaEdt.getHorRegistro());
        }
    }

    @Override
    public void onClick(View view) {

        if(view.getId() != R.id.llDosagem || view.getId() != R.id.txtDosagem || view.getId() != R.id.edtDosagem){
            escondeTeclado(view);
        }

        switch (view.getId()) {
            case R.id.btnConfirmar:
                if (validarCampos()) {

                    RegistroInsulina rInsulina = new RegistroInsulina();
                    rInsulina.setDatReg(Util.converteDataBrParaSql(edtDatRegistro.getText().toString()));
                    rInsulina.setHorRegistro(edtHorRegistro.getText().toString());
                    rInsulina.setValDosagem(Integer.parseInt(edtValDosagem.getText().toString()));
                    rInsulina.setCodTipoInsulina(((TipoInsulina)spinnerTipoInsulina.getSelectedItem()).getCodTipoInsulina());
                    rInsulina.setCodProdutoInsulina(((ProdutoInsulina)spinnerProdutoInsulina.getSelectedItem()).getCodProdutoInsulina());

                    DadosFirebase dadosFirebase = new DadosFirebase();

                    if(idRegistro != null){
                        if(dadosFirebase.atualizaRegistroInsulina(rInsulina, idRegistro)){
                            finish();
                        }
                    }else {
                        if(dadosFirebase.novoRegistroInsulina(rInsulina)){
                            finish();
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
                edtValDosagem.requestFocus();
                mostraTeclado(edtValDosagem);
                break;
            case R.id.llTipoInsulina:
            case R.id.txtTipoInsulina:
                spinnerTipoInsulina.performClick();
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

        if(edtValDosagem.getText().toString().equals("")) {
            edtValDosagem.setError("Insira um valor");
            return false;
        }else{
            try{
                Integer.parseInt(edtValDosagem.getText().toString());
            }catch (Exception e){
                edtValDosagem.setError("Inserir um número inteiro");
                return false;
            }
        }

        if(spinnerTipoInsulina.getSelectedItem().toString().equals("")) {
//            spinnerAlimentacao.setError("Selecione um estado de alimentação");
            txtTipoInsulina.setError("Selecione um tipo");
            return false;
        }

        if(spinnerProdutoInsulina == null || spinnerProdutoInsulina.getSelectedItem().toString().equals("")) {
//            spinnerAlimentacao.setError("Selecione um estado de alimentação");
            txtProdutoInsulina.setError("Selecione um tipo");
            return false;
        }

        return true;
    }

}
