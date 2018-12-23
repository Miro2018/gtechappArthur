package dev.com.br.gtechapp.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.entity.ProdutoInsulina;
import dev.com.br.gtechapp.entity.RegistroInsulina;
import dev.com.br.gtechapp.entity.TipoInsulina;
import dev.com.br.gtechapp.persistence.DatabaseHelper;

public class TipoInsulinaService {

    public static TipoInsulina obterTipoInsulinaPorId(int codTipoInsulina, Context context){

        List<TipoInsulina> listaTipoInsulina = new ArrayList<>();

        String listaNomTipoInsulina[] = context.getResources().getStringArray(R.array.lista_tipo_insulina);

        if(listaNomTipoInsulina[codTipoInsulina - 1] != null){
            return new TipoInsulina(codTipoInsulina, listaNomTipoInsulina[codTipoInsulina - 1]);
        }

        return null;
    }


}
