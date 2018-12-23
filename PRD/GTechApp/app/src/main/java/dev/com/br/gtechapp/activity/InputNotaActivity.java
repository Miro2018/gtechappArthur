package dev.com.br.gtechapp.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.entity.RegistroGlicose;
import dev.com.br.gtechapp.entity.RegistroNota;
import dev.com.br.gtechapp.entity.TipoInsulina;
import dev.com.br.gtechapp.entity.TipoNota;
import dev.com.br.gtechapp.persistence.DadosFirebase;
import dev.com.br.gtechapp.service.TipoInsulinaService;
import dev.com.br.gtechapp.service.TipoNotaService;
import dev.com.br.gtechapp.service.Util;

public class InputNotaActivity extends BaseActivity implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    Bitmap bitmap;
    String imageFileName;

    //Calendário
    private int mYear, mMonth, mDay, mHour, mMinute, mSecond;

    //Botão adicionar item_registro
    private Button btnConfirmar;

    //Campos editaveis
    private EditText edtNota;
    private TextView edtDatRegistro, edtHorRegistro;
    private Spinner spinnerTipoNota;
    private LinearLayout llData, llHora, llTipoNota, llNota, llFoto;
    private  TextView txtData, txtHora, txtTipoNota, txtNota, txtFoto;
    private ImageView imgFoto;

    //Firebase
    private DadosFirebase dadosFirebase;
    private DatabaseReference mGlicoseReference;
    private FirebaseAuth mAuth;

    private String idRegistro;
    private RegistroNota rNotaEdt;

    List<TipoNota> listaTipoNota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_nota);

        dadosFirebase = new DadosFirebase();
        mAuth = FirebaseAuth.getInstance();

        idRegistro = getIntent().getStringExtra("idRegistro");

        listaTipoNota = TipoNotaService.obterListaTIpoNota(this);

        definirObjetos();
        obterDatHoraAgora();

        if(idRegistro != null && !idRegistro.isEmpty()){
            setUpFirebase();
        }
//        else {
//            obterDatHoraAgora();
//        }

        setUpActionBar();
    }

    private void setUpActionBar(){

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary) ));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nota");
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
                .child("user-registros-nota")
                .child(mAuth.getUid())
                .child(idRegistro);

        ValueEventListener registroNotaListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                rNotaEdt = dataSnapshot.getValue(RegistroNota.class);

                if(rNotaEdt.getDatReg() != null)
                    edtDatRegistro.setText(Util.converteDataSqlParaBr(rNotaEdt.getDatReg()));
                if(rNotaEdt.getHorRegistro() != null)
                    edtHorRegistro.setText(rNotaEdt.getHorRegistro());
                if(rNotaEdt.getCodTipoNota() != 0){
                    int index = listaTipoNota.get(rNotaEdt.getCodTipoNota() -1).getCodTipoNota();
                    spinnerTipoNota.setSelection(index - 1);
                }
                if(rNotaEdt.getTxtNota() != null)
                    edtNota.setText(rNotaEdt.getTxtNota());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(getClass().getName(), "rNotaEdt:onCancelled", databaseError.toException());
                // ...
            }
        };
        mGlicoseReference.addValueEventListener(registroNotaListener);
    }

    private void obterDatHoraAgora(){
        // Get Current Date
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

        spinnerTipoNota = (Spinner) findViewById(R.id.spnTipoNota);
        ArrayAdapter<TipoNota> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, listaTipoNota);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoNota.setAdapter(adapter);

        llTipoNota = findViewById(R.id.llTipoNota);
        llTipoNota .setOnClickListener(this);
        txtTipoNota = findViewById(R.id.txtTipoNota);
        txtTipoNota.setOnClickListener(this);

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

        llFoto = findViewById(R.id.llFoto);
        llFoto.setOnClickListener(this);
        txtFoto = findViewById(R.id.txtFoto);
        txtFoto.setOnClickListener(this);
        imgFoto = findViewById(R.id.imgFoto);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnConfirmar:
                if (validarCampos()) {

                    RegistroNota rNota = new RegistroNota();
                    rNota.setDatReg(Util.converteDataBrParaSql(edtDatRegistro.getText().toString()));
                    rNota.setHorRegistro(edtHorRegistro.getText().toString());
                    rNota.setCodTipoNota(((TipoNota)spinnerTipoNota.getSelectedItem()).getCodTipoNota());
                    rNota.setTxtNota(edtNota.getText().toString());

                    dadosFirebase = new DadosFirebase();

                    if(idRegistro != null && !idRegistro.isEmpty()){
                        if(dadosFirebase.atualizaRegistroNota(rNota, idRegistro)){
                            finish();
                        }else{
                            Toast.makeText(this, "Erro ao tentar atualizar registro", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        String key = dadosFirebase.novoRegistroNota(rNota);
                        if( key != null){
                            finish();
                        }else {
                            Toast.makeText(this, "Erro ao tentar gravar registro", Toast.LENGTH_LONG).show();
                        }
                    }

//                    if(idRegistro != null && !idRegistro.isEmpty()){
//                        if(dadosFirebase.atualizaRegistroNota(rNota, idRegistro)){
//                            if(validarBitmap()){
//                                if(uploadImage(idRegistro)){
//                                    finish();
//                                }
//                            }
//                        }else{
//                            Toast.makeText(this, "Erro ao tentar atualizar registro", Toast.LENGTH_LONG).show();
//                        }
//                    }else{
//                        if(validarBitmap()){
//                            String key = dadosFirebase.novoRegistroNota(rNota);
//                            if( key != null){
//                                rNotaEdt = rNota;
//                                rNota.setIdRegistro(key);
//                                idRegistro = key;
//                                uploadImage(key);
//                            }else {
//                                Toast.makeText(this, "Erro ao tentar gravar registro", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    }
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
            case R.id.llTipoInsulina:
            case R.id.txtTipoNota:
                spinnerTipoNota.performClick();
                break;
            case R.id.llNota:
            case R.id.txtNota:
            case R.id.edtNota:
                edtNota.requestFocus();
                mostraTeclado(edtNota);
                break;
            case R.id.llFoto:
            case R.id.txtFoto:
            case R.id.imgFoto:
//                startActivity(new Intent(this, CameraActivity.class));
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 99);

//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                }

                dispatchTakePictureIntent();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic();
            updateImageView();
        }
    }

    private void updateImageView() {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.pgbFoto);
        progressBar.setVisibility(View.VISIBLE);
        imgFoto.setVisibility(View.GONE);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    handleImageOrientation();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable(){
                    public void run() {
                        if(!isDestroyed() && !isFinishing()) {
                            setPic();
                            progressBar.setVisibility(View.GONE);
                            imgFoto.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Ops! Ocorreu um erro ao tentar capturar uma imagem", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "dev.com.br.gtechapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {

         imgFoto = (ImageView) findViewById(R.id.imgFoto);

        // Get the dimensions of the View
        int targetW = imgFoto.getWidth();
        int targetH = imgFoto.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imgFoto.setImageBitmap(bitmap);
    }

    private void handleImageOrientation() {
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        Bitmap rotatedBitmap;
        try {
            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = Util.rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = Util.rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = Util.rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            if (rotatedBitmap != bitmap) {
                FileOutputStream fOut = new FileOutputStream(mCurrentPhotoPath);
                rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
            }
            bitmap.recycle();
            rotatedBitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
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

        if(edtNota.getText().toString().trim().equals("")) {
            edtNota.setError("Campo Obrigatório");
            return false;
        }
        if(spinnerTipoNota.getSelectedItem().toString().equals("")) {
//            spinnerTipoNota.setError("Selecione um estado de alimentação");
            return false;
        }

        return true;
    }

    public boolean uploadImage(String keyNota) {

        final boolean[] result = {false};

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Carregando...");
        progressDialog.show();
        progressDialog.setCancelable(true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images").child(mAuth.getUid()).child(keyNota).child("nota_foto");
        final UploadTask uploadTask = imagesRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressDialog.dismiss();
                mostrarFalha();
                result[0] = false;
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                finish();
                Log.d("APP_", "Sucesso em upload imagem");
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                        .getTotalByteCount());
                progressDialog.setMessage((int)progress+"%");
            }
        });

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                uploadTask.cancel();
            }
        });

        return result[0];
    }

    public void mostrarFalha(){
        Toast.makeText(this, "Falha", Toast.LENGTH_LONG).show();
    }

    private boolean validarBitmap(){
        if(bitmap == null){
            return false;
        }

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);
        byte[] ba = bao.toByteArray();
        int size = ba.length;

        if(size > 5000000){
            Toast.makeText(this, "Foto deve ser de no máximo 5MB", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
