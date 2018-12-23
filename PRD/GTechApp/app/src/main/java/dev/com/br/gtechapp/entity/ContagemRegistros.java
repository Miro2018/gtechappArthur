package dev.com.br.gtechapp.entity;

public class ContagemRegistros {
    private String nomTitulo;
    private int qtdRegistros;

    public ContagemRegistros(String nomTitulo){
        this.nomTitulo = nomTitulo;
    }

    public String getNomTitulo() {
        return nomTitulo;
    }

    public void setNomTitulo(String nomTitulo) {
        this.nomTitulo = nomTitulo;
    }

    public int getQtdRegistros() {
        return qtdRegistros;
    }

    public void setQtdRegistros(int qtdRegistros) {
        this.qtdRegistros = qtdRegistros;
    }

    public void addOne(){
        this.qtdRegistros++;
    }
}
