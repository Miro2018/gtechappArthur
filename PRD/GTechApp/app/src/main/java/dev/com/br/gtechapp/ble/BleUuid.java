package dev.com.br.gtechapp.ble;

import java.util.UUID;

public class BleUuid {
    //SERVICE
    public static final String SERVICE_DEVICE_INFORMATION = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String SERVICE_GLUCOSE = "00001808-0000-1000-8000-00805f9b34fb";
    public static final String SERVICE_NUS_BASE_UUID = "0000fff0-0000-1000-8000-00805f9b34fb";
    public static final String SERVICE_BATTERY = "0000180f-0000-1000-8000-00805f9b34fb";

    public static final UUID SERVICE_CHANGED_CHARACTERISTIC = UUID.fromString("00002A05-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_SERVICE = UUID.fromString(SERVICE_BATTERY);

    //CHAR
    public static final String CHAR_MANUFACTURER_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_GLUCOSE_MEASUREMENT_STRING = "00002a18-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_CHOLEST_MEASUREMENT_STRING = "0000fff7-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_GLUCOSE_MEASUREMENT_CONTEXT_STRING = "00002a34-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_RECORD_ACCESS_CONTROL_POINT_STRING = "00002a52-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_MODEL_NUMBER_STRING = "00002a24-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_SERIAL_NUMBEAR_STRING = "00002a25-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_BATTERY_LEVEL_STRING = "00002a19-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_CLIENT_CHARACTERISTIC_CONFIG_STRING = "00002902-0000-1000-8000-00805f9b34fb";

    public static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString(CHAR_CLIENT_CHARACTERISTIC_CONFIG_STRING);
    public static final UUID BATTERY_LEVEL_CHARACTERISTIC = UUID.fromString(CHAR_BATTERY_LEVEL_STRING);



    // 1802 Immediate Alert
    public static final String SERVICE_IMMEDIATE_ALERT = "00001807-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_ALERT_LEVEL = "00002a06-0000-1000-8000-00805f9b34fb";
}
