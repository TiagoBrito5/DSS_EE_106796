package com.dss.model.logica.funcionarios;

import java.util.List;
import java.util.Optional;

import com.dss.model.data.FuncionarioDAO;

public class GestFuncionario implements IGestFuncionarios {

    private final FuncionarioDAO funcionarioDAO;

    public GestFuncionario(FuncionarioDAO funcionarioDAO) {
        this.funcionarioDAO = funcionarioDAO;
    }

    @Override
    public Optional<Funcionario> autenticar(int id) {
        return funcionarioDAO.obterPorId(id);
    }

    @Override
    public List<Funcionario> listarTodos() {
        return funcionarioDAO.listarTodos();
    }
}
