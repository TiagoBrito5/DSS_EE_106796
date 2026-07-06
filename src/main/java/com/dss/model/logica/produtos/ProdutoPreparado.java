package com.dss.model.logica.produtos;

public class ProdutoPreparado extends Produto {
    private final int tempoEstimadoPreparacao;

    public ProdutoPreparado(String codigo, String nome, double preco, int tempoEstimadoPreparacao) {
        super(codigo, nome, preco);
        this.tempoEstimadoPreparacao = tempoEstimadoPreparacao;
    }

    public int getTempoEstimadoPreparacao() { return tempoEstimadoPreparacao; }

    @Override
    public boolean requerPreparacao() { return true; }
}
