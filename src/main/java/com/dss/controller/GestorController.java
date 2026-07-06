package com.dss.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.dss.model.ILogicaPadaria;
import com.dss.model.logica.Ingredientes.Ingrediente;

public class GestorController {

    private final ILogicaPadaria gestorNegocio;

    public GestorController(ILogicaPadaria gestorNegocio) {
        this.gestorNegocio = gestorNegocio;
    }

    public long consultarVolumePedidos(LocalDateTime inicio, LocalDateTime fim) {
        return gestorNegocio.consultarVolumePedidos(inicio, fim);
    }

    public Map<String, Long> consultarProdutosMaisVendidos(LocalDateTime inicio, LocalDateTime fim) {
        return gestorNegocio.consultarProdutosMaisVendidos(inicio, fim);
    }

    public double consultarFaturacaoTotal(LocalDateTime inicio, LocalDateTime fim) {
        return gestorNegocio.consultarFaturacaoTotal(inicio, fim);
    }

    public List<Ingrediente> consultarStock() {
        return gestorNegocio.consultarStock();
    }

    public boolean alterarQuantidadeIngrediente(String nome, double novaQuantidade) {
        return gestorNegocio.alterarQuantidadeIngrediente(nome, novaQuantidade);
    }
}
