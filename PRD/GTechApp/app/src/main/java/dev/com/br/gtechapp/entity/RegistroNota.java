package dev.com.br.gtechapp.entity;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class RegistroNota {

    private String datReg;
    private String horRegistro;
    private int codTipoNota;
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

    public int getCodTipoNota() {
        return codTipoNota;
    }

    public void setCodTipoNota(int codTipoNota) {
        this.codTipoNota = codTipoNota;
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
        result.put("codTipoNota", codTipoNota);
        result.put("txtNota", txtNota);

        return result;
    }
}
