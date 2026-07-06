package com.dss.model.logica.pedidos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dss.model.logica.produtos.Personalizacao;
import com.dss.model.logica.produtos.Produto;

public class ItemPedido {
    private int id;
    private final Produto produto;
    private final int quantidade;
    private final double precoUnitario;
    private final List<Personalizacao> personalizacoesSelecionadas;
    private EstadoPreparacao estadoPreparacao;

    public ItemPedido(Produto produto, int quantidade, List<Personalizacao> personalizacoesSelecionadas) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPreco();
        this.personalizacoesSelecionadas = new ArrayList<>(personalizacoesSelecionadas);
        this.estadoPreparacao = produto.requerPreparacao() ? EstadoPreparacao.PENDENTE : EstadoPreparacao.PRONTO;
    }

    public ItemPedido(Produto produto, int quantidade, List<Personalizacao> personalizacoesSelecionadas,
                      EstadoPreparacao estadoPreparacao, double precoUnitario) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.personalizacoesSelecionadas = new ArrayList<>(personalizacoesSelecionadas);
        this.estadoPreparacao = estadoPreparacao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public double getPrecoUnitario() { return precoUnitario; }
    public EstadoPreparacao getEstadoPreparacao() { return estadoPreparacao; }

    public List<Personalizacao> getPersonalizacoesSelecionadas() {
        return Collections.unmodifiableList(personalizacoesSelecionadas);
    }

    public void marcarPronto() {
        this.estadoPreparacao = EstadoPreparacao.PRONTO;
    }

    public double getSubtotal() {
        return precoUnitario * quantidade;
    }
}
