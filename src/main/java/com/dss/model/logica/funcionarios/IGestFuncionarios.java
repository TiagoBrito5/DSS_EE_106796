package com.dss.model.logica.funcionarios;

import java.util.List;
import java.util.Optional;

public interface IGestFuncionarios {
    Optional<Funcionario> autenticar(int id);
    List<Funcionario> listarTodos();
}
