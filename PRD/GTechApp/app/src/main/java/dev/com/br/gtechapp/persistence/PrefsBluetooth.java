package dev.com.br.gtechapp.persistence;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsBluetooth {

    SharedPreferences prefsBluetooth;

    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "PrefsBluetooth";

    private static final String KEY_ESTADO_CONEXAO = "estado_conexao";

    public PrefsBluetooth(Context context){
        this._context = context;
        prefsBluetooth = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = prefsBluetooth.edit();
    }

    public void setEstadoConexao (int estadoConexao){
        editor.putInt(KEY_ESTADO_CONEXAO, estadoConexao);
        editor.commit();
    }
    public int getEstadoConexao(){
        return prefsBluetooth.getInt(KEY_ESTADO_CONEXAO, 0);
    }

}
