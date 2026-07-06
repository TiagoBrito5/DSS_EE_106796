package com.dss.model.logica.Ingredientes;

public class Ingrediente {
    private final String nome;
    private double quantidadeEmStock;
    private final double nivelMinimo;
    private final String unidade;

    public Ingrediente(String nome, double quantidadeEmStock, double nivelMinimo, String unidade) {
        this.nome = nome;
        this.quantidadeEmStock = quantidadeEmStock;
        this.nivelMinimo = nivelMinimo;
        this.unidade = unidade;
    }

    public String getNome() { return nome; }
    public double getQuantidadeEmStock() { return quantidadeEmStock; }
    public double getNivelMinimo() { return nivelMinimo; }
    public String getUnidade() { return unidade; }

    public void descontarStock(double quantidade) {
        this.quantidadeEmStock -= quantidade;
    }

    public boolean abaixoNivelMinimo() {
        return quantidadeEmStock < nivelMinimo;
    }
}
