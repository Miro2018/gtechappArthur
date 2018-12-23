package dev.com.br.gtechapp.entity;

public class ProdutoInsulina {

    private int codProdutoInsulina;
    private String nomProdutoInsulina;
    private int codTipoIntulina;

    public int getCodProdutoInsulina() {
        return codProdutoInsulina;
    }

    public void setCodProdutoInsulina(int codProdutoInsulina) {
        this.codProdutoInsulina = codProdutoInsulina;
    }

    public String getNomProdutoInsulina() {
        return nomProdutoInsulina;
    }

    public void setNomProdutoInsulina(String nomProdutoInsulina) {
        this.nomProdutoInsulina = nomProdutoInsulina;
    }

    public int getCodTipoIntulina() {
        return codTipoIntulina;
    }

    public void setCodTipoIntulina(int codTipoIntulina) {
        this.codTipoIntulina = codTipoIntulina;
    }

    @Override
    public String toString() {
        return nomProdutoInsulina;
    }
}
