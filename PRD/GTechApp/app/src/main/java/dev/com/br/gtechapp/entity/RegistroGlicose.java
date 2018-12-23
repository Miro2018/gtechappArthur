package dev.com.br.gtechapp.entity;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class RegistroGlicose implements Comparable<RegistroGlicose>{

    private String datReg;
    private String horRegistro;
    private int valRegistro;
    private String nomEstadoAlimentacao;
    private int tipInput;
    private String txtNota;
    private String idRegistro;

    public String getDatReg() {
        return datReg;
    }

    public void setDatReg(String datReg) {
        this.datReg = datReg;
    }

    public String getHorRegistro() {
        return horRegistro;
    }

    public void setHorRegistro(String horRegistro) {
        this.horRegistro = horRegistro;
    }

    public int getValRegistro() {
        return valRegistro;
    }

    public void setValRegistro(int valRegistro) {
        this.valRegistro = valRegistro;
    }

    public String getNomEstadoAlimentacao() {
        return nomEstadoAlimentacao;
    }
    public void setNomEstadoAlimentacao(String nomEstadoAlimentacao) {
        this.nomEstadoAlimentacao = nomEstadoAlimentacao;
    }

    public int getTipInput() {
        return tipInput;
    }

    public void setTipInput(int tipInput) {
        this.tipInput = tipInput;
    }

    public String getTxtNota() {
        return txtNota;
    }

    public void setTxtNota(String txtNota) {
        this.txtNota = txtNota;
    }

    public String getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(String idRegistro) {
        this.idRegistro = idRegistro;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("datReg", datReg);
        result.put("horRegistro", horRegistro);
        result.put("valRegistro", valRegistro);
        result.put("nomEstadoAlimentacao", nomEstadoAlimentacao);
        result.put("tipInput", tipInput);
        result.put("txtNota", txtNota);

        return result;
    }

    @Override
    public int compareTo(RegistroGlicose comparar) {
        return getDataHoraCompleta().compareTo(comparar.getDataHoraCompleta());
    }

    public Date getDataHoraCompleta(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        try {
            return sdf.parse(this.getDatReg() + this.getHorRegistro());
        } catch (ParseException ex) {
            Log.e(RegistroGlicose.class.getName(), ex.getMessage() + "\n" + ex.getStackTrace());
        }
        return null;
    }
}
