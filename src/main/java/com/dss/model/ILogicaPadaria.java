package com.dss.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dss.model.logica.Ingredientes.Ingrediente;
import com.dss.model.logica.funcionarios.Funcionario;
import com.dss.model.logica.produtos.Personalizacao;
import com.dss.model.logica.produtos.Produto;
import com.dss.model.logica.pedidos.Pedido;

public interface ILogicaPadaria {

    List<Produto> listarProdutos();
    Optional<Produto> obterProduto(String codigo);

    Pedido iniciarPedido();
    boolean adicionarItem(Pedido pedido, Produto produto, int quantidade, List<Personalizacao> selecionadas);
    Pedido registarPedido(Pedido pedido);
    Optional<Pedido> consultarEstadoPedido(int id);
    boolean entregarPedido(int id);
    List<Pedido> consultarListaPreparacao();

    boolean terminarPreparacaoItem(int pedidoId, int itemId);

    long consultarVolumePedidos(LocalDateTime inicio, LocalDateTime fim);
    Map<String, Long> consultarProdutosMaisVendidos(LocalDateTime inicio, LocalDateTime fim);
    double consultarFaturacaoTotal(LocalDateTime inicio, LocalDateTime fim);

    List<Ingrediente> consultarStock();
    boolean alterarQuantidadeIngrediente(String nome, double novaQuantidade);

    Optional<Funcionario> autenticar(int id);
    List<Funcionario> listarFuncionarios();
}
