package com.dss.controller;

import java.util.List;

import com.dss.model.ILogicaPadaria;
import com.dss.model.logica.pedidos.Pedido;

public class PreparacaoController {

    private final ILogicaPadaria gestorNegocio;

    public PreparacaoController(ILogicaPadaria gestorNegocio) {
        this.gestorNegocio = gestorNegocio;
    }

    public List<Pedido> consultarListaPreparacao() {
        return gestorNegocio.consultarListaPreparacao();
    }

    public boolean terminarPreparacaoItem(int pedidoId, int itemId) {
        return gestorNegocio.terminarPreparacaoItem(pedidoId, itemId);
    }
}
