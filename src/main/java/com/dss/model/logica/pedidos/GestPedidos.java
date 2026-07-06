package com.dss.model.logica.pedidos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dss.model.data.PedidoDAO;
import com.dss.model.logica.produtos.Personalizacao;
import com.dss.model.logica.produtos.Produto;

public class GestPedidos implements IGestPedidos {

    private final PedidoDAO pedidoDAO;

    public GestPedidos(PedidoDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    @Override
    public Pedido iniciarPedido() {
        return new Pedido();
    }

    @Override
    public boolean adicionarItem(Pedido pedido, Produto produto, int quantidade, List<Personalizacao> selecionadas) {
        if (produto == null || quantidade <= 0) return false;

        List<Personalizacao> disponiveis = produto.getPersonalizacoesDisponiveis();
        for (Personalizacao p : selecionadas) {
            if (!disponiveis.contains(p)) return false;
        }

        pedido.adicionarItem(new ItemPedido(produto, quantidade, selecionadas));
        return true;
    }

    @Override
    public Pedido registarPedido(Pedido pedido) {
        pedido.confirmar();
        pedidoDAO.guardar(pedido);
        return pedido;
    }

    @Override
    public Optional<Pedido> consultarEstadoPedido(int id) {
        return pedidoDAO.obterPorId(id);
    }

    @Override
    public boolean entregarPedido(int id) {
        return pedidoDAO.obterPorId(id)
            .filter(p -> p.getEstado() == EstadoPedido.PRONTO)
            .<Boolean>map(p -> { p.entregar(); pedidoDAO.atualizar(p); return true; })
            .orElse(false);
    }

    @Override
    public List<Pedido> consultarListaPreparacao() {
        List<Pedido> lista = new ArrayList<>();
        for (Pedido p : pedidoDAO.listarTodos()) {
            if (p.getEstado() == EstadoPedido.EM_PREPARACAO) lista.add(p);
        }
        return lista;
    }

    @Override
    public boolean terminarPreparacaoItem(int pedidoId, int itemId) {
        return pedidoDAO.obterPorId(pedidoId)
            .filter(p -> p.getEstado() == EstadoPedido.EM_PREPARACAO)
            .<Boolean>map(p -> {
                p.getItens().stream()
                    .filter(i -> i.getId() == itemId && i.getEstadoPreparacao() == EstadoPreparacao.PENDENTE)
                    .findFirst()
                    .ifPresent(ItemPedido::marcarPronto);
                p.verificarConclusao();
                pedidoDAO.atualizar(p);
                return true;
            })
            .orElse(false);
    }

    @Override
    public long consultarVolumePedidos(LocalDateTime inicio, LocalDateTime fim) {
        return pedidoDAO.contarEntreguesNoIntervalo(inicio, fim);
    }

    @Override
    public Map<String, Long> consultarProdutosMaisVendidos(LocalDateTime inicio, LocalDateTime fim) {
        return pedidoDAO.produtosMaisVendidosNoIntervalo(inicio, fim);
    }

    @Override
    public double consultarFaturacaoTotal(LocalDateTime inicio, LocalDateTime fim) {
        return pedidoDAO.somarFaturacaoNoIntervalo(inicio, fim);
    }
}
