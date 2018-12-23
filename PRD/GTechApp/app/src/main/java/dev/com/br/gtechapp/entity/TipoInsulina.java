package dev.com.br.gtechapp.entity;

public class TipoInsulina {

    private int codTipoInsulina;
    private String nomTipoInsulina;

    public TipoInsulina(){}

    public TipoInsulina(int codTipoInsulina, String nomTipoInsulina){
        this.codTipoInsulina = codTipoInsulina;
        this.nomTipoInsulina = nomTipoInsulina;
    }

    public int getCodTipoInsulina() {
        return codTipoInsulina;
    }

    public void setCodTipoInsulina(int codTipoInsulina) {
        this.codTipoInsulina = codTipoInsulina;
    }

    public String getNomTipoInsulina() {
        return nomTipoInsulina;
    }

    public void setNomTipoInsulina(String nomTipoInsulina) {
        this.nomTipoInsulina = nomTipoInsulina;
    }

    @Override
    public String toString() {
        return nomTipoInsulina;
    }
}
