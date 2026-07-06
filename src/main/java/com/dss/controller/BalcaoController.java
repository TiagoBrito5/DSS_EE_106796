package com.dss.controller;

import java.util.List;
import java.util.Optional;

import com.dss.model.ILogicaPadaria;
import com.dss.model.logica.produtos.Personalizacao;
import com.dss.model.logica.produtos.Produto;
import com.dss.model.logica.pedidos.Pedido;

public class BalcaoController {

    private final ILogicaPadaria gestorNegocio;

    public BalcaoController(ILogicaPadaria gestorNegocio) {
        this.gestorNegocio = gestorNegocio;
    }

    public List<Produto> listarProdutos() {
        return gestorNegocio.listarProdutos();
    }

    public Optional<Produto> obterProduto(String codigo) {
        return gestorNegocio.obterProduto(codigo);
    }

    public Pedido iniciarPedido() {
        return gestorNegocio.iniciarPedido();
    }

    public boolean adicionarItem(Pedido pedido, Produto produto, int quantidade, List<Personalizacao> selecionadas) {
        return gestorNegocio.adicionarItem(pedido, produto, quantidade, selecionadas);
    }

    public Pedido registarPedido(Pedido pedido) {
        return gestorNegocio.registarPedido(pedido);
    }

    public Optional<Pedido> consultarEstadoPedido(int id) {
        return gestorNegocio.consultarEstadoPedido(id);
    }

    public boolean entregarPedido(int id) {
        return gestorNegocio.entregarPedido(id);
    }

    public List<Pedido> consultarListaPreparacao() {
        return gestorNegocio.consultarListaPreparacao();
    }
}
