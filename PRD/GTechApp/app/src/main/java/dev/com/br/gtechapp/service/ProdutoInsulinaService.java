package dev.com.br.gtechapp.service;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.entity.ProdutoInsulina;
import dev.com.br.gtechapp.persistence.DatabaseHelper;

public class ProdutoInsulinaService {

    private static String TAG = "ProdutoInsulinaService";

    public static List<ProdutoInsulina> obterListaProdutoInsulina(int codTipoInsulina, Context context){

        DatabaseHelper db = new DatabaseHelper(context);
        try{
            return db.obterProdutosInsulina(codTipoInsulina);
        }catch (Exception e){
            Log.e(TAG, "Erro ao tentar obter lista de produtos insulina. Msg erro: " + e.getMessage().toLowerCase());
            e.printStackTrace();
            return null;
        }finally {
            db.close();
        }
    }

    public static int obterIndiceListaProdutoInsulinaPorId(List<ProdutoInsulina> listaProdutoInsulina, int codProdutoInsulina){
        if(listaProdutoInsulina != null && listaProdutoInsulina.size() > 0){
            for(int i = 0; i < listaProdutoInsulina.size(); i++){
                if(listaProdutoInsulina.get(i).getCodProdutoInsulina() == codProdutoInsulina){
                    return i;
                }
            }
        }
        return 0;
    }

}
