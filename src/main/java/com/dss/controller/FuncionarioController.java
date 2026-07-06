package com.dss.controller;

import java.util.List;
import java.util.Optional;

import com.dss.model.ILogicaPadaria;
import com.dss.model.logica.funcionarios.Funcionario;

public class FuncionarioController {

    private final ILogicaPadaria gestorNegocio;

    public FuncionarioController(ILogicaPadaria gestorNegocio) {
        this.gestorNegocio = gestorNegocio;
    }

    public Optional<Funcionario> autenticar(int id) {
        return gestorNegocio.autenticar(id);
    }

    public List<Funcionario> listarTodos() {
        return gestorNegocio.listarFuncionarios();
    }
}
