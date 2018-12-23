package dev.com.br.gtechapp.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.entity.Usuario;

public class InputUserInfoActivity extends BaseActivity{

    private DatabaseReference mDatabase;
    private  FirebaseAuth mAuth;

    Button btnConfirmar;
    EditText edtInput;
    LinearLayout include, llInput;
    Spinner spnInput;
    TextView txtNomCampo, txtInputDate;

    public static final int TEXT_INPUT = 1;
    public static final int NUMBER_INPUT = 2;
    public static final int DATE_INPUT = 3;
    public static final int SPINNER_INPUT = 4;
    public static final int NUMBER_DECIMAL_INPUT = 5;

    private int input_type = 0;
    private String input_field;
    private String input_value = "";
    private String nom_campo = "";

    //Calendário
    private int mYear, mMonth, mDay;
    String datHoje = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_user_info);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Bundle b = getIntent().getExtras();

        if(b != null) {
            input_type = b.getInt("input_type");
            input_field = b.getString("input_field");
            input_value = b.getString("input_value");
            nom_campo = b.getString("nom_campo");
        }

        showInput();

        setUpActionBar();
    }

    private void setUpActionBar(){

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary) ));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
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


    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null) {
            finish();
        }
    }

    private void showInput(){

        txtNomCampo = (TextView)findViewById(R.id.txtNomCampo);
        txtNomCampo.setText(nom_campo);
        txtNomCampo.setOnClickListener(listener);

        llInput = findViewById(R.id.llInput);
        llInput.setOnClickListener(listener);

        if (input_type != 0){

            switch (input_type){

                case TEXT_INPUT:
                    showTextInput();
                    break;
                case NUMBER_INPUT:
                    showNumberInput();
                    break;
                case NUMBER_DECIMAL_INPUT:
                    showNumberInput();
                    break;
                case DATE_INPUT:
                    showDateInput();
                    break;
                case SPINNER_INPUT:
                    showSpinner();
                    break;
                default:
                    finish();
            }

            btnConfirmar = (Button) findViewById(R.id.btnConfirmar);
            btnConfirmar.setOnClickListener(listener);

        }else{
            finish();
        }
    }

    private void showTextInput(){
        include = (LinearLayout) findViewById(R.id.include);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        include.addView(layoutInflater.inflate(R.layout.input_text, null, false));
        edtInput = (EditText)findViewById(R.id.edtInput);
        edtInput.setText(input_value);
        edtInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    escondeTeclado(edtInput);
                }
            }
        });
    }

    private  void showNumberInput(){
        include = (LinearLayout) findViewById(R.id.include);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        include.addView(layoutInflater.inflate(R.layout.input_number, null, false));
        edtInput = (EditText)findViewById(R.id.edtInput);
        if(input_type == NUMBER_INPUT){
            edtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        edtInput.setText(input_value);
        edtInput.selectAll();
        edtInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    escondeTeclado(v);
                }
            }
        });
        edtInput.requestFocus();
        mostraTeclado(edtInput);
    }

    private void showDateInput(){
        include = (LinearLayout) findViewById(R.id.include);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        include.addView(layoutInflater.inflate(R.layout.input_date, null, false));
        txtInputDate = (TextView) findViewById(R.id.txtInputDate);

        obterDatHoje();

        txtInputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker(v);
            }
        });
    }

    private void showSpinner() {
        include = (LinearLayout) findViewById(R.id.include);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        include.addView(layoutInflater.inflate(R.layout.input_spinner, null, false));

        List<String> arrLista = null;
        spnInput = (Spinner) findViewById(R.id.spnInput);
        switch (input_field){
            case Usuario.NOM_GENERO:
                arrLista = Arrays.asList(getResources().getStringArray(R.array.lista_genero));
                break;
            case Usuario.NOM_TIPO_DIABETE:
                arrLista = Arrays.asList(getResources().getStringArray(R.array.lista_tipo_diabete));
                break;
            default:
                arrLista = Arrays.asList(getResources().getStringArray(R.array.lista_default));
                break;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrLista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnInput.setAdapter(adapter);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnConfirmar:
                    if(validarInputData()){
                        atualizarInfo();
                    }
                    break;
                case R.id.llInput:
                case R.id.txtNomCampo:
                    definirFoco();
                    break;
                default:
                    break;
            }
        }
    };

    private void atualizarInfo(){

        if (input_type != 0){

            switch (input_type){

                case SPINNER_INPUT:
                    updateFieldOnFirebase(mAuth.getCurrentUser().getUid(), input_field, spnInput.getSelectedItem().toString());
                    finish();
                    break;
                case DATE_INPUT:
                    updateFieldOnFirebase(mAuth.getCurrentUser().getUid(), input_field, txtInputDate.getText().toString());
                    finish();
                    break;
                default:
                    if(validarValInput()){
                        updateFieldOnFirebase(mAuth.getCurrentUser().getUid(), input_field, edtInput.getText().toString());
                        finish();
                    }
                    break;
            }
        }else{
            finish();
        }
    }

    private void definirFoco(){
        if (input_type != 0){

            switch (input_type){

                case SPINNER_INPUT:
                    spnInput.performClick();
                    break;
                case DATE_INPUT:
                    mostrarDatePicker(txtInputDate);
                    break;
                default:
                    edtInput.requestFocus();
                    mostraTeclado(edtInput);
                    break;
            }
        }else{
            finish();
        }
    }

    private void updateFieldOnFirebase(String userId, String field, String value) {

        mDatabase.child("users").child(userId).child(field).setValue(value);
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

                        txtInputDate.setText(strDia + "/"  + strMes + "/" + year);
                    }
                }, mYear, mMonth - 1, mDay);
        datePickerDialog.show();
    }

    private void obterDatHoje(){
        final Calendar c = Calendar.getInstance();
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

        datHoje = strDia + "/" + strMes + "/" + mYear;

        if(input_value == null || input_value.equals("")) {
            txtInputDate.setText(strDia + "/" + strMes + "/" + mYear);
        }else{
            txtInputDate.setText(input_value);
        }
    }

    private boolean validarInputData(){
        if(input_type == DATE_INPUT && input_field.equals(Usuario.DAT_NASCIMENTO) && txtInputDate.getText().toString().equals(datHoje)){
            txtInputDate.setError("Data de nascimento inválida");
            txtInputDate.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validarValInput(){

        if(edtInput.getText().toString().equals("")){
            edtInput.setError("Insira um valor");
            return false;
        }

        if(input_type == NUMBER_DECIMAL_INPUT){
            try{
                Double.parseDouble(edtInput.getText().toString());
            }catch (Exception e){
                edtInput.setError("Insira um número");
                return false;
            }
        }

        if(input_type == NUMBER_INPUT){
            try{
                Integer.parseInt(edtInput.getText().toString());
            }catch (Exception e){
                edtInput.setError("Insira um número inteiro");
                return false;
            }
        }

        return true;
    }
}
