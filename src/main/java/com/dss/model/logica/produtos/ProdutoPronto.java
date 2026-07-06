package com.dss.model.logica.produtos;

public class ProdutoPronto extends Produto {

    public ProdutoPronto(String codigo, String nome, double preco) {
        super(codigo, nome, preco);
    }

    @Override
    public boolean requerPreparacao() { return false; }
}
