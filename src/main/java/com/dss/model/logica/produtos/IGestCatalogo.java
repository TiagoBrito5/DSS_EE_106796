package com.dss.model.logica.produtos;

import java.util.List;
import java.util.Optional;

public interface IGestCatalogo {
    List<Produto> listarProdutos();
    Optional<Produto> obterProduto(String codigo);
}
