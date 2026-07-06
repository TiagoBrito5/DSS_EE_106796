package com.dss.model.logica.produtos;

import java.util.List;
import java.util.Optional;

import com.dss.model.data.ProdutoDAO;

public class GestCatalogo implements IGestCatalogo {

    private final ProdutoDAO produtoDAO;

    public GestCatalogo(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    @Override
    public List<Produto> listarProdutos() {
        return produtoDAO.listarTodos();
    }

    @Override
    public Optional<Produto> obterProduto(String codigo) {
        return produtoDAO.obterPorCodigo(codigo);
    }
}
