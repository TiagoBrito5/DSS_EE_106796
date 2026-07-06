package com.dss.model.logica.funcionarios;

public class Funcionario {
    private final int id;
    private final String nome;
    private final FuncaoFuncionario funcao;

    public Funcionario(int id, String nome, FuncaoFuncionario funcao) {
        this.id = id;
        this.nome = nome;
        this.funcao = funcao;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public FuncaoFuncionario getFuncao() { return funcao; }

    @Override
    public String toString() {
        return nome + " (" + funcao + ")";
    }
}
