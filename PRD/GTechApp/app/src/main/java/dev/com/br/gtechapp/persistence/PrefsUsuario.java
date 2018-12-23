package dev.com.br.gtechapp.persistence;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsUsuario {

    SharedPreferences prefsUsuario;

    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "PrefsUsuario";

    private static final String KEY_HIPO = "hipo";
    private static final String KEY_HIPER = "hiper";
    private static final String KEY_TERMO = "termo";

    public PrefsUsuario(Context context){
        this._context = context;
        prefsUsuario = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = prefsUsuario.edit();
    }

    public void setHipo (int hipo){
        if(hipo != 0){
            editor.putInt(KEY_HIPO, hipo);
            editor.commit();
        }
    }

    public void setHiper (int hiper){
        if(hiper != 0){
            editor.putInt(KEY_HIPER, hiper);
            editor.commit();
        }
    }

    public void setTermo (int codTermo){
        if(codTermo != 0){
            editor.putInt(KEY_TERMO, codTermo);
            editor.commit();
        }
    }

    public int getHipo(){
        return prefsUsuario.getInt(KEY_HIPO, 0);
    }

    public int getHiper(){
        return prefsUsuario.getInt(KEY_HIPER, 0);
    }

    public int getTermo(){
        return prefsUsuario.getInt(KEY_TERMO, 0);
    }
}
