package com.dss.view;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.dss.controller.FuncionarioController;
import com.dss.model.logica.funcionarios.Funcionario;

public class LoginView {

    private final FuncionarioController controller;
    private final Scanner scanner;

    public LoginView(FuncionarioController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public Optional<Funcionario> iniciar() {
        System.out.println("\n========================================");
        System.out.println("  Sistema de Gestão de Pedidos — DSS EE");
        System.out.println("========================================");

        List<Funcionario> funcionarios = controller.listarTodos();
        System.out.println("  Utilizadores:");
        for (Funcionario f : funcionarios)
            System.out.printf("  [%d] %-15s %s%n", f.getId(), f.getNome(), f.getFuncao());

        System.out.println("----------------------------------------");
        System.out.print("ID (0 para sair): ");

        String input = scanner.nextLine().trim();
        if (input.equals("0") || input.isEmpty()) return Optional.empty();

        try {
            int id = Integer.parseInt(input);
            Optional<Funcionario> resultado = controller.autenticar(id);
            if (resultado.isEmpty()) {
                System.out.println("ID não encontrado.");
            } else {
                System.out.printf("Bem-vindo/a, %s!%n", resultado.get().getNome());
            }
            return resultado;
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return Optional.empty();
        }
    }
}
