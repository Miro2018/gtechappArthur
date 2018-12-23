package dev.com.br.gtechapp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.ble.BleUtil;
import dev.com.br.gtechapp.ble.BleUuid;
import dev.com.br.gtechapp.ble.DeviceAdapter;
import dev.com.br.gtechapp.ble.ScannedDevice;
import dev.com.br.gtechapp.entity.RegistroGlicose;
import dev.com.br.gtechapp.persistence.DadosFirebase;
import dev.com.br.gtechapp.persistence.PrefsBluetooth;

public class BleActivity extends BaseActivity implements BluetoothAdapter.LeScanCallback {

    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 999;

    private static final String TAG = "BLEDevice";
    private Button btnVoltar;
        private TextView tvwInfo;
//    private Button btnUpdateData;
    private static final int REQUEST_ENABLE_BT = 1;
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    private BluetoothAdapter mBTAdapter;
    private BluetoothDevice mDevice;
    private BluetoothGatt mConnGatt;
    private boolean mIsScanning, enabled = true;
    private int mStatus, GCcount = 0, count = 0;
    private DeviceAdapter mDeviceAdapter;
    private ListView deviceListView; //lvUpdateData;
    private ArrayAdapter<String> adUpdateData;
    final Context context = this;
    private AlertDialog dialog;
    private ScannedDevice item;
    private Queue<BluetoothGattDescriptor> descriptorWriteQueue = new LinkedList<BluetoothGattDescriptor>();
    private Queue<BluetoothGattCharacteristic> characteristicReadQueue = new LinkedList<BluetoothGattCharacteristic>();
    private BluetoothLeScanner mLEScanner;
    private ScanCallback mScanCallback;
    private FirebaseAuth mAuth;
    private String strDatUltimoRegistro;
    private String strNovaDatUltimoRegistro;
    private boolean blnFinalizado;
    private ProgressBar progressBar;
    List<RegistroGlicose> novosRegistros;
    DadosFirebase dbFire;
    private final static String[] listaDispositivosBle = {"01GM52"};

    private RegistroGlicose rGlicose;
    private PrefsBluetooth prefsBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

        prefsBluetooth = new PrefsBluetooth(this);

        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+ Permission APIs
            fuckMarshMallow();
        }

        mAuth = FirebaseAuth.getInstance();

//        initScanCallback();

//        btnConnect = (Button)findViewById(R.id.btnConnect);
//        btnUpdateData = (Button)findViewById(R.id.btnUpdateData);
//        lvUpdateData = (ListView)findViewById(R.id.lvGlucoseData);
        adUpdateData = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
//        tvDeviceName = (TextView)findViewById(R.id.tvDeviceName);
        init();
        setUpActionBar();

    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart()");

        if(prefsBluetooth.getEstadoConexao() != 0){
            tvwInfo.setText("Finalizando última conexão, aguarde...");
            tvwInfo.setTextColor(Color.BLACK);
            btnVoltar.setVisibility(View.VISIBLE);
        }else{
            startScan();
        }

        obterUltimoRegistro();

        super.onStart();

    }

    private void obterUltimoRegistro(){
        //Glicose - último registro
        Query dbFirebaseQuery = FirebaseDatabase.getInstance().getReference()
                .child("user-registros-glicose")
                .child(mAuth.getUid())
                .child("0-ultimo-registro");

        ValueEventListener ultimoRegistroGlicoseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strDatUltimoRegistro = dataSnapshot.child("datUltimoRegistro").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(getClass().getName(), "loadUltimoRegistrosGlicose:onCancelled", databaseError.toException());
            }
        };
        dbFirebaseQuery.addValueEventListener(ultimoRegistroGlicoseListener);
    }

    private void setUpActionBar(){

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary) ));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bluetooth");
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
            if(progressBar.getVisibility() == View.VISIBLE){
                Toast.makeText(this, "Aguarde...", Toast.LENGTH_LONG).show();
            }else{
//                startActivity(new Intent(this, MainActivity.class));
//                super.onBackPressed();
                finish();
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
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {

        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    private void writeRACPchar(){
        //***PUSH 0x01 TO RECORD ACCESS CONTROL POINT
        final BluetoothGattCharacteristic writeRACPchar =
                mConnGatt.getService(UUID.fromString(BleUuid.SERVICE_GLUCOSE))
                        .getCharacteristic(UUID.fromString(BleUuid.CHAR_RECORD_ACCESS_CONTROL_POINT_STRING));
        byte[] data = new byte[2];
        data[0] = (byte)0x01;
        data[1] = (byte)0x01;
        writeRACPchar.setValue(data);

        if(count == 0)
        {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mConnGatt.writeCharacteristic(writeRACPchar);
                }
            }, 2000);

            count++;
        } else {
            count++;
        }

    }

    private void init() {

        blnFinalizado = false;
        tvwInfo = (TextView) findViewById(R.id.tvwInfo);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        btnVoltar = (Button) findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BleActivity.this.onBackPressed();
            }
        });

        // BLE check
        if (!BleUtil.isBLESupported(this)) {
            Toast.makeText(this,"Não suporta conexão BLE", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // BT check
        BluetoothManager manager = BleUtil.getManager(this);
        if (manager != null) {
            mBTAdapter = manager.getAdapter();
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
        }
        if (mBTAdapter == null) {
            Toast.makeText(this, "BLE não disponível", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        View viewScaned=View.inflate(this,R.layout.scaned_list, deviceListView);
        deviceListView = (ListView) viewScaned.findViewById(R.id.list);
        mDeviceAdapter = new DeviceAdapter(this, R.layout.listitem_device,
                new ArrayList<ScannedDevice>());
        deviceListView.setAdapter(mDeviceAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {
                item = mDeviceAdapter.getItem(position);
                if (item != null) {
                    count = 0; // primeira leitura precisa de algum tempo
                    connect2Gatt();
                    dialog.cancel();
                    // stop before change Activity
                    stopScan();
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(viewScaned).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnVoltar.setVisibility(View.VISIBLE);
                if(mDevice == null){
                    tvwInfo.setText("Dispositivo de aferição não foi conectado.");
                    tvwInfo.setTextColor(Color.RED);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnVoltar.setVisibility(View.VISIBLE);
                        }
                    }, 0);
                }
                stopScan();
                dialog.cancel();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(mDevice == null){
                    tvwInfo.setText("Dispositivo de aferição não foi conectado.");
                    tvwInfo.setTextColor(Color.RED);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnVoltar.setVisibility(View.VISIBLE);
                        }
                    }, 0);
                }
                stopScan();
                dialog.cancel();
            }
        });

        dialog = builder.create();
//        stopScan();
    }

    public void dialog_scaneddevice(){
        dialog.show();
    }

    @Override
    public void onLeScan(final BluetoothDevice newDeivce, final int newRssi,
                         final byte[] newScanRecord) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mBTAdapter!= null && mBTAdapter.isEnabled() && mDeviceAdapter != null && newDeivce != null &&
                        (PermissionChecker.checkSelfPermission(BleActivity.this,Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
                                || PermissionChecker.checkSelfPermission(BleActivity.this,Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED)){
                    for(String strDisposiitivo : listaDispositivosBle ){
                        String strNomDevice = newDeivce.getName();
                        if(strNomDevice != null){
                            if(newDeivce.getName().contains(strDisposiitivo)){
                                mDeviceAdapter.update(newDeivce, newRssi);
                                break;
                            }
                        }
                    }
                } else {
                    btnVoltar.setVisibility(View.VISIBLE);
                    if(mDevice == null){
                        tvwInfo.setText("Dispositivo de aferição não foi conectado.");
                        tvwInfo.setTextColor(Color.RED);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btnVoltar.setVisibility(View.VISIBLE);
                            }
                        }, 0);
                    }
                }
            }
        });
    }

    private void startScan() {
        Log.d(TAG, "startScan()");
        if ((mBTAdapter != null) && (!mIsScanning)) {
            mBTAdapter.startLeScan(this);
//            if (Build.VERSION.SDK_INT < 21) {
//                mBTAdapter.startLeScan(this);
//            } else {
//                mLEScanner = mBTAdapter.getBluetoothLeScanner();
//                mLEScanner.startScan(mScanCallback);
//            }
            mIsScanning = true;
            dialog_scaneddevice();
            invalidateOptionsMenu();
        }
    }

    private void stopScan() {
        if (mBTAdapter != null) {
            mBTAdapter.stopLeScan(this);
//            if (Build.VERSION.SDK_INT < 21) {
//                mBTAdapter.stopLeScan(this);
//            } else {
//                if(mScanCallback != null)
//                    mLEScanner.stopScan(mScanCallback);
//            }
        }
        mIsScanning = false;
        invalidateOptionsMenu();
        blnFinalizado = true;
    }

    private void connect2Gatt(){
        // check BluetoothDevice
        if (mDevice == null) {
            mDevice = item.getDevice();
            if (mDevice == null) {
                finish();
                return;
            } else{
                prefsBluetooth.setEstadoConexao(1);
                progressBar.setVisibility(View.VISIBLE);
                tvwInfo.setText("Aguarde, transferindo registros...");
                tvwInfo.setTextColor(Color.BLACK);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnVoltar.setVisibility(View.VISIBLE);
                    }
                }, 0);
                dbFire = new DadosFirebase();
                novosRegistros = new ArrayList<>();
            }
        }
        // connect to Gatt
        if ((mConnGatt == null)	&& (mStatus == BluetoothProfile.STATE_DISCONNECTED))
        {
            mConnGatt = mDevice.connectGatt(this, false, mGattcallback);
            mStatus = BluetoothProfile.STATE_CONNECTING;
        } else {
            if (mConnGatt != null) {
                // re-connect and re-discover Services
                mConnGatt.connect();
                mConnGatt.discoverServices();
            } else {
                Log.e(TAG, "state error");
                finish();
                return;
            }
        }
    }

    //connect to gatt
    private final BluetoothGattCallback mGattcallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {

            Log.d(TAG, "onConnectionStateChange - Estado da conexão BLE: " + newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mStatus = newState;
                mConnGatt.discoverServices();
                Log.d(TAG, "onConnectionStateChange - Estado da conexão BLE: conectado");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mStatus = newState;
                Log.d(TAG, "onConnectionStateChange - Estado da conexão BLE: desconectado");
                runOnUiThread(new Runnable() {
                    public void run() {
                        tvwInfo.setText("Desconectado");
                        tvwInfo.setTextColor(Color.RED);
                        close();
                    }
                });
            } else {
                Log.d(TAG, "onConnectionStateChange - Estado da conexão BLE: " + newState);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            for (BluetoothGattService service : gatt.getServices()) {
                if ((service == null) || (service.getUuid() == null)) {
                    continue;
                }

                BluetoothGattCharacteristic charGM =
                        mConnGatt.getService(UUID.fromString(BleUuid.SERVICE_GLUCOSE))
                                .getCharacteristic(UUID.fromString(BleUuid.CHAR_GLUCOSE_MEASUREMENT_STRING));
                mConnGatt.setCharacteristicNotification(charGM, enabled);
                BluetoothGattDescriptor descGM = charGM.getDescriptor(UUID.fromString(BleUuid.CHAR_CLIENT_CHARACTERISTIC_CONFIG_STRING));
                descGM.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                writeGattDescriptor(descGM);

                BluetoothGattCharacteristic charGMC =
                        mConnGatt.getService(UUID.fromString(BleUuid.SERVICE_GLUCOSE))
                                .getCharacteristic(UUID.fromString(BleUuid.CHAR_GLUCOSE_MEASUREMENT_CONTEXT_STRING));
                mConnGatt.setCharacteristicNotification(charGMC, enabled);
                BluetoothGattDescriptor descGMC = charGMC.getDescriptor(UUID.fromString(BleUuid.CHAR_CLIENT_CHARACTERISTIC_CONFIG_STRING));
                descGMC.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mConnGatt.writeDescriptor(descGMC);

                BluetoothGattCharacteristic charRACP =
                        mConnGatt.getService(UUID.fromString(BleUuid.SERVICE_GLUCOSE))
                                .getCharacteristic(UUID.fromString(BleUuid.CHAR_RECORD_ACCESS_CONTROL_POINT_STRING));
                mConnGatt.setCharacteristicNotification(charRACP, enabled);
                BluetoothGattDescriptor descRACP = charRACP.getDescriptor(UUID.fromString(BleUuid.CHAR_CLIENT_CHARACTERISTIC_CONFIG_STRING));
                descRACP.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                writeGattDescriptor(descRACP);

                //                BluetoothGattCharacteristic charBarrery =
                //                        mConnGatt.getService(UUID.fromString(BleUuid.SERVICE_BATTERY))
                //                                .getCharacteristic(UUID.fromString(BleUuid.CHAR_BATTERY_LEVEL_STRING));
                //                mConnGatt.setCharacteristicNotification(charBarrery, enabled);
                //                BluetoothGattDescriptor descBarrery = charBarrery.getDescriptor(UUID.fromString(BleUuid.CHAR_CLIENT_CHARACTERISTIC_CONFIG_STRING));
                //                descBarrery.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                //                mConnGatt.writeDescriptor(descBarrery);

                try{
                    writeRACPchar();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.d(TAG, "onCharacteristicRead estado: " + status);
            characteristicReadQueue.remove();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
            else{
                Log.d(TAG, "onCharacteristicRead error: " + status);
            }
            if(characteristicReadQueue.size() > 0)
                readCharacteristic(characteristicReadQueue.element());
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicWrite estado: " + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicChanged: " + characteristic.toString());

            BluetoothGattDescriptor cccd = characteristic.getDescriptor(BleUuid.CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
            if (cccd == null || cccd.getValue() == null || cccd.getValue().length != 2 || cccd.getValue()[0] == (byte) 1) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }else {
                onCharacteristicIndicated(characteristic);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Callback: Wrote GATT Descriptor successfully.");
            }
            else{
                servicoIndisponivel();
                Log.d(TAG, "Callback: Error writing GATT Descriptor: "+ status);
            }
            descriptorWriteQueue.remove();
            if(descriptorWriteQueue.size() > 0)
                mConnGatt.writeDescriptor(descriptorWriteQueue.element());
            else if(characteristicReadQueue.size() > 0)
                readCharacteristic(characteristicReadQueue.element());
        }
    };

    public void readCharacteristic(final BluetoothGattCharacteristic characteristic) {
        if (mBTAdapter == null || mConnGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        characteristicReadQueue.add(characteristic);

        if((characteristicReadQueue.size() == 1) && (descriptorWriteQueue.size() == 0)){
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mConnGatt.readCharacteristic(characteristic);
                }
            }, 100);
        }

    }

    private void writeGattDescriptor(final BluetoothGattDescriptor d){
        descriptorWriteQueue.add(d);
        if(descriptorWriteQueue.size() == 1){
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mConnGatt.writeDescriptor(d);
                }
            }, 100);
        }
    }

    private void onCharacteristicIndicated(BluetoothGattCharacteristic characteristic) {
        int opCode = characteristic.getIntValue(17, 0).intValue();
        int offset = 0 + 2;
        if (opCode == 5) {
            int number = characteristic.getIntValue(18, offset).intValue();
            Log.d(TAG, "number: " + number);
            if (number > 0) {
                Log.d(TAG, "number > 0");
            } else {
                Log.d(TAG, "number < 0");
            }
        } else if (opCode == 6) {
            int requestedOpCode = characteristic.getIntValue(17, offset).intValue();
            switch (characteristic.getIntValue(17, 3).intValue()) {
                case 1:
                    onComplete();
                    Log.d(TAG, "Operação abortada");
                    break;
                case 2:
                    Log.d(TAG, "Operação não suportada");
                    break;
                case 6:
                    onComplete();
                    Log.d(TAG, "Operação completa!!!!");
                    break;
                default:
                    Log.d(TAG, "Operação falhou");
                    break;
            }
        }
    }

    private void onComplete(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                prefsBluetooth.setEstadoConexao(0);

                Log.d(TAG, "Qtd novosRegistros: " + novosRegistros.size());
                dbFire.novosRegistroGlicose(novosRegistros);
                if(strNovaDatUltimoRegistro != null){
                    dbFire.atualizaDatUltimoRegistroGlicose(strNovaDatUltimoRegistro, mAuth);
                }

                if(!isFinishing() && !isDestroyed()){
                    tvwInfo.setText(novosRegistros.size() + " registro(s) transferidos");
                    tvwInfo.setTextColor(Color.BLACK);
                    btnVoltar.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    close();
                }
            }
        }, 0);
    }

    private void servicoIndisponivel(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "servicoIndisponivel");

                if(!isFinishing() && !isDestroyed()){
                    tvwInfo.setText("Serviço indisponível");
                    tvwInfo.setTextColor(Color.RED);
                    btnVoltar.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, 0);
    }


    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        if(BleUuid.CHAR_GLUCOSE_MEASUREMENT_STRING.equalsIgnoreCase(characteristic.getUuid().toString()) ||
                BleUuid.CHAR_GLUCOSE_MEASUREMENT_CONTEXT_STRING.equalsIgnoreCase(characteristic.getUuid().toString())){

            if(BleUuid.CHAR_GLUCOSE_MEASUREMENT_STRING.equalsIgnoreCase(characteristic.getUuid().toString())){

                rGlicose = new RegistroGlicose();
                rGlicose.setTipInput(2);

                obterObjetoRegistro(characteristic, rGlicose);

                if((strDatUltimoRegistro == null || strDatUltimoRegistro.compareTo(rGlicose.getDatReg() + " " + rGlicose.getHorRegistro()) < 0)
                    && rGlicose.getDataHoraCompleta().compareTo(new Date()) <= 0){
                    novosRegistros.add(rGlicose);
                    if((strNovaDatUltimoRegistro == null || strNovaDatUltimoRegistro.compareTo(rGlicose.getDatReg() + " " + rGlicose.getHorRegistro()) < 0)){
                        strNovaDatUltimoRegistro = rGlicose.getDatReg() + " " + rGlicose.getHorRegistro();
                    }
                }
            }else if(BleUuid.CHAR_GLUCOSE_MEASUREMENT_CONTEXT_STRING.equalsIgnoreCase(characteristic.getUuid().toString())){
                obterObjetoRegistro(characteristic, rGlicose);
            }
//            obterObjetoRegistro(characteristic);
        }

    }

    private void obterObjetoRegistro(final BluetoothGattCharacteristic characteristic, RegistroGlicose rGlicose){

        try {
            int offset;
            int flags;
            float mGlucoseValue = 0.0f;
            int blood_sugar_eat = 4;
            String input_date = "";

            if (BleUuid.CHAR_GLUCOSE_MEASUREMENT_STRING.equalsIgnoreCase(characteristic.getUuid().toString())) {
                flags = characteristic.getIntValue(17, 0).intValue();
                offset = 0 + 1;

                boolean timeOffsetPresent = (flags & 1) > 0;
                boolean typeAndLocationPresent = (flags & 2) > 0;
                if ((flags & 8) > 0) {
                }
                final boolean contextInfoFollows = (flags & 16) > 0;
//                int sequenceNumber = characteristic.getIntValue(18, offset).intValue();

                offset += 2;

                int year = characteristic.getIntValue(18, offset).intValue(); //3
                int month = characteristic.getIntValue(17, 5).intValue();
                int day = characteristic.getIntValue(17, 6).intValue();
                int hour = characteristic.getIntValue(17, 7).intValue();
                int minute = characteristic.getIntValue(17, 8).intValue();
                int second = characteristic.getIntValue(17, 9).intValue();

                offset += 7;

                if (timeOffsetPresent) {
                    offset += 2;
                }

                if (typeAndLocationPresent) {
                    mGlucoseValue = characteristic.getFloatValue(50, offset).floatValue(); //12
                    int typeAndLocation = characteristic.getIntValue(17, offset + 2).intValue();
                    if ((((typeAndLocation & 240) >> 4) == 10 || ((typeAndLocation & 240) >> 4) == 4) && ((typeAndLocation & 15) == 10 || (typeAndLocation & 15) == 4)) {
//                        blood_sugar_eat = 2;
                        rGlicose.setNomEstadoAlimentacao(NomAlimentacao(2));
                        Log.d("TESTE", "blood_sugar_eat: " + 2);
                    }
                    offset += 3;
                }

                //ajusta dia e mês
                String strDay, strMonth;
                strDay = day < 10 ? "0" + day : "" + day;
                strMonth = month < 10 ? "0" + month : "" + month;

                String strHora = hour < 10 ? "0" + hour : String.valueOf(hour);
                String strMin = hour < 10 ? "0" + hour : String.valueOf(hour);
                String strSec = hour < 10 ? "0" + hour : String.valueOf(hour);

                rGlicose.setDatReg(year + "-" + strMonth + "-" + strDay);
                rGlicose.setHorRegistro(strHora + ":" + strMin + ":" + strSec);
                Log.d("TESTE", "input_date(fragmented): " + year + "-" + strMonth + "-" + strDay + " " + strHora + ":" + strMin + ":" + strSec);

                String blood_sugar_value = String.valueOf(Math.round(100000.0f * mGlucoseValue));
                rGlicose.setValRegistro(Integer.parseInt(blood_sugar_value));
                Log.d("TESTE", "blood_sugar_value: " + blood_sugar_value);
                if(Integer.parseInt(blood_sugar_value) == 148)
                {
                    Log.d("TESTE_148", "blood_sugar_value: " + blood_sugar_value);
                }
            }

            if (BleUuid.CHAR_GLUCOSE_MEASUREMENT_CONTEXT_STRING.equalsIgnoreCase(characteristic.getUuid().toString())) {
                flags = characteristic.getIntValue(17, 0).intValue();
                offset = 0 + 1;
                boolean carbohydratePresent = (flags & 1) > 0;
                boolean mealPresent = (flags & 2) > 0;
                if ((flags & 4) > 0) {
                }
                if ((flags & 8) > 0) {
                }
                if ((flags & 16) > 0) {
                }
                if ((flags & 64) > 0) {
                }
                boolean moreFlagsPresent = (flags & 128) > 0;
                offset += 2;
                if (moreFlagsPresent) {
                    offset++;
                }
                if (carbohydratePresent) {
                    offset += 3;
                }
                if (mealPresent) {
                    int meal = characteristic.getIntValue(17, offset).intValue();
                    if (meal == 1) {
//                        blood_sugar_eat = 1;
                        rGlicose.setNomEstadoAlimentacao(NomAlimentacao(1));
                        Log.d("TESTE", "blood_sugar_eat: " + 1);
                    }
                    if (meal == 2) {
//                        blood_sugar_eat = 0;
                        rGlicose.setNomEstadoAlimentacao(NomAlimentacao(0));
                        Log.d("TESTE", "blood_sugar_eat: " + 0);
                    }
                    if (meal == 3) {
//                        blood_sugar_eat = 3;
                        rGlicose.setNomEstadoAlimentacao(NomAlimentacao(3));
                        Log.d("TESTE", "blood_sugar_eat: " + 3);
                    }
                    offset++;
                }
            }

//            if(ultimoRegistro == null || ultimoRegistro.getDatReg() == null || rGlicose.compareTo(ultimoRegistro) > 0)
//                novosRegistros.add(rGlicose);

        }catch (Exception e){
            Log.e(TAG,"Erro obterObjetoRegistro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String NomAlimentacao(int CodAlimentacao){
        switch (CodAlimentacao)
        {
            case 0:
                return getString(R.string.blood_sugar_eat_after);
            case 1:
                return getString(R.string.blood_sugar_eat_before);
            case 2:
                return getString(R.string.blood_sugar_eat_cs);
            case 3:
                return getString(R.string.blood_sugar_eat_fasting);
            default:
                return "";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
    }

    public void close() {
        if (mConnGatt == null) {
            return;
        }
        mStatus = BluetoothProfile.STATE_DISCONNECTED;
        mConnGatt.disconnect();
        mConnGatt.close();
//        unpairDevice(mDevice);
        mConnGatt = null;
        mDevice = null;
        adUpdateData.clear();
//        tvwInfo.setText("");
        btnVoltar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        prefsBluetooth.setEstadoConexao(0);
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            close();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    private void initScanCallback() {
//        if (Build.VERSION.SDK_INT >= 21) {
//            initCallbackLollipop();
//        }
//    }
//
//    @TargetApi(21)
//    private void initCallbackLollipop() {
//        if (this.mScanCallback == null) {
//            this.mScanCallback = new C03237();
//        }
//    }
//
//    class C03237 extends ScanCallback {
//        C03237() {
//        }
//
//        public void onScanResult(int callbackType, ScanResult result) {
//            super.onScanResult(callbackType, result);
//            BluetoothDevice device = result.getDevice();
//            if (device != null) {
//
//                try {
//                    mDeviceAdapter.update(device, result.getRssi());
//                } catch (Exception e) {
//                    Log.e(TAG, "Invalid data in Advertisement packet " + e.toString());
//                }
//            }
//        }
//
//        public void onBatchScanResults(List<ScanResult> results) {
//            super.onBatchScanResults(results);
//        }
//
//        public void onScanFailed(int errorCode) {
//            super.onScanFailed(errorCode);
//            Log.d(TAG, "onScanFailed: " + errorCode);
//        }
//    }
}
