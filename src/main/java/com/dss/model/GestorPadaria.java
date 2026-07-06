package com.dss.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dss.model.logica.Ingredientes.IGestStock;
import com.dss.model.logica.Ingredientes.Ingrediente;
import com.dss.model.logica.funcionarios.Funcionario;
import com.dss.model.logica.funcionarios.IGestFuncionarios;
import com.dss.model.logica.pedidos.IGestPedidos;
import com.dss.model.logica.pedidos.Pedido;
import com.dss.model.logica.produtos.IGestCatalogo;
import com.dss.model.logica.produtos.Personalizacao;
import com.dss.model.logica.produtos.Produto;

public class GestorPadaria implements ILogicaPadaria {

    private final IGestCatalogo gestorCatalogo;
    private final IGestPedidos gestorPedidos;
    private final IGestStock gestorStock;
    private final IGestFuncionarios gestorFuncionarios;

    public GestorPadaria(IGestCatalogo gestorCatalogo, IGestPedidos gestorPedidos,
                         IGestStock gestorStock, IGestFuncionarios gestorFuncionarios) {
        this.gestorCatalogo    = gestorCatalogo;
        this.gestorPedidos     = gestorPedidos;
        this.gestorStock       = gestorStock;
        this.gestorFuncionarios = gestorFuncionarios;
    }

    @Override
    public List<Produto> listarProdutos() {
        return gestorCatalogo.listarProdutos();
    }

    @Override
    public Optional<Produto> obterProduto(String codigo) {
        return gestorCatalogo.obterProduto(codigo);
    }

    @Override
    public Pedido iniciarPedido() {
        return gestorPedidos.iniciarPedido();
    }

    @Override
    public boolean adicionarItem(Pedido pedido, Produto produto, int quantidade, List<Personalizacao> selecionadas) {
        return gestorPedidos.adicionarItem(pedido, produto, quantidade, selecionadas);
    }

    @Override
    public Pedido registarPedido(Pedido pedido) {
        Pedido registado = gestorPedidos.registarPedido(pedido);

        Map<String, Double> consumo = new HashMap<>();
        registado.getItens().forEach(item ->
            item.getProduto().getFichaIngredientes().forEach((nome, qtdPorUnid) ->
                consumo.merge(nome, qtdPorUnid * item.getQuantidade(), Double::sum)));
        gestorStock.processarConsumo(consumo);

        return registado;
    }

    @Override
    public Optional<Pedido> consultarEstadoPedido(int id) {
        return gestorPedidos.consultarEstadoPedido(id);
    }

    @Override
    public boolean entregarPedido(int id) {
        return gestorPedidos.entregarPedido(id);
    }

    @Override
    public List<Pedido> consultarListaPreparacao() {
        return gestorPedidos.consultarListaPreparacao();
    }

    @Override
    public boolean terminarPreparacaoItem(int pedidoId, int itemId) {
        return gestorPedidos.terminarPreparacaoItem(pedidoId, itemId);
    }

    @Override
    public long consultarVolumePedidos(LocalDateTime inicio, LocalDateTime fim) {
        return gestorPedidos.consultarVolumePedidos(inicio, fim);
    }

    @Override
    public Map<String, Long> consultarProdutosMaisVendidos(LocalDateTime inicio, LocalDateTime fim) {
        return gestorPedidos.consultarProdutosMaisVendidos(inicio, fim);
    }

    @Override
    public double consultarFaturacaoTotal(LocalDateTime inicio, LocalDateTime fim) {
        return gestorPedidos.consultarFaturacaoTotal(inicio, fim);
    }

    @Override
    public List<Ingrediente> consultarStock() {
        return gestorStock.consultarStock();
    }

    @Override
    public boolean alterarQuantidadeIngrediente(String nome, double novaQuantidade) {
        return gestorStock.alterarQuantidade(nome, novaQuantidade);
    }

    @Override
    public Optional<Funcionario> autenticar(int id) {
        return gestorFuncionarios.autenticar(id);
    }

    @Override
    public List<Funcionario> listarFuncionarios() {
        return gestorFuncionarios.listarTodos();
    }
}
