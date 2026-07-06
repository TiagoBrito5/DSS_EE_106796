package com.dss.model.logica.pedidos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private final LocalDateTime dataHora;
    private EstadoPedido estado;
    private final List<ItemPedido> itens;

    public Pedido() {
        this.dataHora = LocalDateTime.now();
        this.estado = EstadoPedido.EM_REGISTO;
        this.itens = new ArrayList<>();
    }

    public Pedido(int id, LocalDateTime dataHora, EstadoPedido estado) {
        this.id = id;
        this.dataHora = dataHora;
        this.estado = estado;
        this.itens = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public EstadoPedido getEstado() { return estado; }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
    }

    public double getTotal() {
        return itens.stream().mapToDouble(ItemPedido::getSubtotal).sum();
    }

    public void confirmar() {
        if (itens.isEmpty()) throw new IllegalStateException("Pedido sem itens.");
        boolean temPreparacao = itens.stream().anyMatch(i -> i.getProduto().requerPreparacao());
        this.estado = temPreparacao ? EstadoPedido.EM_PREPARACAO : EstadoPedido.PRONTO;
    }

    public void verificarConclusao() {
        boolean todosProntos = itens.stream()
            .allMatch(i -> i.getEstadoPreparacao() == EstadoPreparacao.PRONTO);
        if (todosProntos) this.estado = EstadoPedido.PRONTO;
    }

    public void entregar() {
        this.estado = EstadoPedido.ENTREGUE;
    }

    @Override
    public String toString() {
        return String.format("Pedido #%d | %s | %s | Total: %.2f€",
            id, dataHora.format(FMT), estado, getTotal());
    }
}
