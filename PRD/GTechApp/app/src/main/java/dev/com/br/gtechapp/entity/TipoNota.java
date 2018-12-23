package dev.com.br.gtechapp.entity;

public class TipoNota {

    private int CodTipoNota;
    private String NomTipoNota;

    public TipoNota(int codTipoNota, String nomTipoNota) {
        CodTipoNota = codTipoNota;
        NomTipoNota = nomTipoNota;
    }

    public int getCodTipoNota() {
        return CodTipoNota;
    }

    public void setCodTipoNota(int codTipoNota) {
        CodTipoNota = codTipoNota;
    }

    public String getNomTipoNota() {
        return NomTipoNota;
    }

    public void setNomTipoNota(String nomTipoNota) {
        NomTipoNota = nomTipoNota;
    }

    @Override
    public String toString(){
        return this.NomTipoNota;
    }
}
