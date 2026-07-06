package com.dss.model.logica.pedidos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dss.model.logica.produtos.Personalizacao;
import com.dss.model.logica.produtos.Produto;


public interface IGestPedidos {

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
}
