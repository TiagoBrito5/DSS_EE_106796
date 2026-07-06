package com.dss.model.logica.Ingredientes;

import com.dss.model.data.IngredienteDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestStock implements IGestStock {

    private final IngredienteDAO ingredienteDAO;

    public GestStock(IngredienteDAO ingredienteDAO) {
        this.ingredienteDAO = ingredienteDAO;
    }

    @Override
    public void processarConsumo(Map<String, Double> consumoIngredientes) {
        Map<String, Ingrediente> mapa = new HashMap<>();
        ingredienteDAO.listarTodos().forEach(i -> mapa.put(i.getNome(), i));

        consumoIngredientes.forEach((nome, total) -> {
            Ingrediente ing = mapa.get(nome);
            if (ing != null) {
                ing.descontarStock(total);
                ingredienteDAO.atualizarStock(ing.getNome(), ing.getQuantidadeEmStock());
            }
        });
    }

    @Override
    public List<Ingrediente> consultarStock() {
        return ingredienteDAO.listarTodos();
    }

    @Override
    public boolean alterarQuantidade(String nome, double novaQuantidade) {
        if (novaQuantidade < 0) return false;
        return ingredienteDAO.alterarQuantidade(nome, novaQuantidade);
    }
}
