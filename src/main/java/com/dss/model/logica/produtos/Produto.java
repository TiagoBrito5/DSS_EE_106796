package com.dss.model.logica.produtos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Produto {
    private final String codigo;
    private final String nome;
    private final double preco;
    private final List<Personalizacao> personalizacoesDisponiveis;
    private final Map<String, Double> fichaIngredientes;

    public Produto(String codigo, String nome, double preco) {
        this.codigo = codigo;
        this.nome = nome;
        this.preco = preco;
        this.personalizacoesDisponiveis = new ArrayList<>();
        this.fichaIngredientes = new LinkedHashMap<>();
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }

    public List<Personalizacao> getPersonalizacoesDisponiveis() {
        return Collections.unmodifiableList(personalizacoesDisponiveis);
    }

    public Map<String, Double> getFichaIngredientes() {
        return Collections.unmodifiableMap(fichaIngredientes);
    }

    public void adicionarPersonalizacao(Personalizacao p) {
        personalizacoesDisponiveis.add(p);
    }

    public void adicionarIngrediente(String nomeIngrediente, double quantidade) {
        fichaIngredientes.put(nomeIngrediente, quantidade);
    }

    public abstract boolean requerPreparacao();

    @Override
    public String toString() {
        return String.format("[%s] %-25s %.2f€", codigo, nome, preco);
    }
}
