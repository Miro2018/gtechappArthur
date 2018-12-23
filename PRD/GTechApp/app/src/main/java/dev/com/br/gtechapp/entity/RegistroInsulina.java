package dev.com.br.gtechapp.entity;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class RegistroInsulina {

    private String idRegistro;
    private String datReg;
    private String horRegistro;
    private int valDosagem;
    private int codTipoInsulina ;
    private int codProdutoInsulina;
    private String nomTipoInsulina;
    private String nomProdutoInsulina;

    public String getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(String idRegistro) {
        this.idRegistro = idRegistro;
    }

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

    public int getValDosagem() {
        return valDosagem;
    }

    public void setValDosagem(int valDosagem) {
        this.valDosagem = valDosagem;
    }

    public int getCodTipoInsulina() {
        return codTipoInsulina;
    }

    public void setCodTipoInsulina(int codTipoInsulina) {
        this.codTipoInsulina = codTipoInsulina;
    }

    public int getCodProdutoInsulina() {
        return codProdutoInsulina;
    }

    public void setCodProdutoInsulina(int codProdutoInsulina) {
        this.codProdutoInsulina = codProdutoInsulina;
    }

    public String getNomTipoInsulina() {
        return nomTipoInsulina;
    }

    public void setNomTipoInsulina(String nomTipoInsulina) {
        this.nomTipoInsulina = nomTipoInsulina;
    }

    public String getNomProdutoInsulina() {
        return nomProdutoInsulina;
    }

    public void setNomProdutoInsulina(String nomProdutoInsulina) {
        this.nomProdutoInsulina = nomProdutoInsulina;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("datReg", datReg);
        result.put("horRegistro", horRegistro);
        result.put("valDosagem", valDosagem);
        result.put("codTipoInsulina", codTipoInsulina);
        result.put("codProdutoInsulina", codProdutoInsulina);

        return result;
    }
}
