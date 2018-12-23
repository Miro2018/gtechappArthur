package dev.com.br.gtechapp.entity;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Registro implements Comparable<Registro>{

    private String datReg;
    private String horRegistro;
    private int valRegistro;
    private String txtExtraInfo;
    private int tipInput;
    private int tipRegistro; //1-Glicose, 2-Insulina
    private String idRegistro;
    private String nomEstadoAlimentacao;
    private int codTipoNota;
    private String txtNotaNota;

    public String getDatReg() {
        return datReg;
    }

    public void setDatReg(String datReg) {
        this.datReg = datReg;
    }

    public String getHorRegistro() {
        return horRegistro;
    }

    public String getHorRegistroSemSegundos() {
        return horRegistro.substring(0, 5);
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

    public String getTxtExtraInfo() {
        return txtExtraInfo;
    }

    public void setTxtExtraInfo(String txtExtraInfo) {
        this.txtExtraInfo = txtExtraInfo;
    }

    public int getTipInput() {
        return tipInput;
    }

    public void setTipInput(int tipInput) {
        this.tipInput = tipInput;
    }

    public int getTipRegistro() {
        return tipRegistro;
    }

    public void setTipRegistro(int tipRegistro) {
        this.tipRegistro = tipRegistro;
    }

    public String getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(String idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getNomEstadoAlimentacao() {
        return nomEstadoAlimentacao;
    }

    public void setNomEstadoAlimentacao(String nomEstadoAlimentacao) {
        this.nomEstadoAlimentacao = nomEstadoAlimentacao;
    }

    public int getCodTipoNota() {
        return codTipoNota;
    }

    public void setCodTipoNota(int codTipoNota) {
        this.codTipoNota = codTipoNota;
    }

    public String getTxtNotaNota() {
        return txtNotaNota;
    }

    public void setTxtNotaNota(String txtNotaNota) {
        this.txtNotaNota = txtNotaNota;
    }

    @Override
    public int compareTo(Registro comparar) {
        return getDataHoraCompleta(this).compareTo(getDataHoraCompleta(comparar));
    }

    public Date getDataHoraCompleta(Registro rg){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        try {
            return sdf.parse(rg.getDatReg() + rg.getHorRegistro());
        } catch (ParseException ex) {
            Log.e(RegistroGlicose.class.getName(), ex.getMessage() + "\n" + ex.getStackTrace());
        }
        return null;
    }
}
