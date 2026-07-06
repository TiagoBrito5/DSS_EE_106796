package com.dss.model.logica.Ingredientes;

import java.util.List;
import java.util.Map;

public interface IGestStock {
    void processarConsumo(Map<String, Double> consumoIngredientes);
    List<Ingrediente> consultarStock();
    boolean alterarQuantidade(String nome, double novaQuantidade);
}
