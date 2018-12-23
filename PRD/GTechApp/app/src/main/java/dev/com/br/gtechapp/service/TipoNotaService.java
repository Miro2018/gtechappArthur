package dev.com.br.gtechapp.service;

import android.content.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.entity.TipoNota;

public class TipoNotaService {

    public static List<TipoNota> obterListaTIpoNota(Context context){
        List<TipoNota> listaTipoNota = new ArrayList<>();

        String listaNomTipoNota[] = context.getResources().getStringArray(R.array.lista_tipo_nota);

        for (int i = 1; i <= listaNomTipoNota.length; i++) {
            TipoNota tipoNota = new TipoNota(i, listaNomTipoNota[i - 1]);
            listaTipoNota.add(tipoNota);
        }

        return listaTipoNota;
    }
}
