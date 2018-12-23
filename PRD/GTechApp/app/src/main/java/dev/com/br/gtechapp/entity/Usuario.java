package dev.com.br.gtechapp.entity;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Usuario {

    public final static int VAl_PARAM_HIPER_PADRAO = 140;
    public final static int VAl_PARAM_HIPO_PADRAO = 70;

    public static final String NOM_USUARIO = "nomUsuario";
    public static final String TXT_EMAIL = "txtEmail";
    public static final String DAT_NASCIMENTO = "datNascimento";
    public static final String NOM_GENERO = "nomGenero";
    public static final String VAL_PESO = "valPeso";
    public static final String VAL_ALTURA = "valAltura";
    public static final String DAT_INI_DIABETE = "datInicioDiabete";
    public static final String NOM_TIPO_DIABETE = "nomTipoDiabete";
    public static final String VAL_HIPER = "valHiper";
    public static final String VAL_HIPO = "valHipo";
    public static final String COD_TERMO = "codTermo";

    private String nomUsuario;
    private String txtEmail;
    private String datNascimento;
    private String nomGenero;
    private Double valPeso;
    private Double valAltura;
    private String datInicioDiabete;
    private String nomTipoDiabete;
    private int valHiper;
    private int valHipo;
    private int codTermo;

    public Usuario() {
    }

    public Usuario(String username, String email) {
        this.nomUsuario = username;
        this.txtEmail = email;
    }

    public String getNomUsuario() {
        return nomUsuario;
    }

    public void setNomUsuario(String nomUsuario) {
        this.nomUsuario = nomUsuario;
    }

    public String getTxtEmail() {
        return txtEmail;
    }

    public void setTxtEmail(String txtEmail) {
        this.txtEmail = txtEmail;
    }

    public String getDatNascimento() {
        return datNascimento;
    }

    public void setDatNascimento(String datNascimento) {
        this.datNascimento = datNascimento;
    }

    public String getNomGenero() {
        return nomGenero;
    }

    public void setNomGenero(String nomGenero) {
        this.nomGenero = nomGenero;
    }

    public Double getValPeso() {
        return valPeso;
    }

    public void setValPeso(String valPeso) {
            this.valPeso = !valPeso.equals("") ? Double.parseDouble(valPeso): 0;
    }

    public Double getValAltura() {
        return valAltura;
    }

    public void setValAltura(String valAltura) {
        this.valAltura = !valAltura.equals("") ? Double.parseDouble(valAltura) : 0;
    }

    public String getDatInicioDiabete() {
        return datInicioDiabete;
    }

    public void setDatInicioDiabete(String datInicioDiabete) {
        this.datInicioDiabete = datInicioDiabete;
    }

    public String getNomTipoDiabete() {
        return nomTipoDiabete;
    }

    public void setNomTipoDiabete(String nomTipoDiabete) {
        this.nomTipoDiabete = nomTipoDiabete;
    }

    public int getValHiper() {
        return valHiper != 0 ? valHiper : VAl_PARAM_HIPER_PADRAO;
    }

    public void setValHiper(String valHiper) {
        try{
            this.valHiper = !valHiper.equals("") ? Integer.parseInt(valHiper) : 0;
        }catch (Exception e){
            Log.d("APP_USUARIO", "Valor não é um inteiro");
        }
    }

    public int getValHipo() {
        return valHipo != 0 ? valHipo : VAl_PARAM_HIPO_PADRAO;
    }

    public void setValHipo(String valHipo) {
        try{
            this.valHipo = !valHipo.equals("") ? Integer.parseInt(valHipo) : 0;
        }catch (Exception e){
            Log.d("APP_USUARIO", "Valor não é um inteiro");
        }
    }

    public int getCodTermo() {
        return codTermo;
    }

    public void setCodTermo(int codTermo) {
        this.codTermo = codTermo;
    }
}