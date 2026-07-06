package com.dss.model.logica.produtos;

public class Personalizacao {
    private int id;
    private final String descricao;
    private final String categoria;

    public Personalizacao(String descricao, String categoria) {
        this.descricao = descricao;
        this.categoria = categoria;
    }

    public Personalizacao(int id, String descricao, String categoria) {
        this.id = id;
        this.descricao = descricao;
        this.categoria = categoria;
    }

    public int getId() { return id; }
    public String getDescricao() { return descricao; }
    public String getCategoria() { return categoria; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Personalizacao)) return false;
        return id == ((Personalizacao) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return categoria != null ? "[" + categoria + "] " + descricao : descricao;
    }
}
