package com.dss.view;

import java.util.Optional;
import java.util.Scanner;

import com.dss.controller.BalcaoController;
import com.dss.controller.FuncionarioController;
import com.dss.controller.GestorController;
import com.dss.controller.PreparacaoController;
import com.dss.model.logica.funcionarios.Funcionario;

public class MainView {
    private final BalcaoController balcaoController;
    private final PreparacaoController preparacaoController;
    private final GestorController gestorController;
    private final FuncionarioController funcionarioController;
    private final Scanner scanner = new Scanner(System.in);

    public MainView(BalcaoController balcaoController,
                    PreparacaoController preparacaoController,
                    GestorController gestorController,
                    FuncionarioController funcionarioController) {
        this.balcaoController = balcaoController;
        this.preparacaoController = preparacaoController;
        this.gestorController = gestorController;
        this.funcionarioController = funcionarioController;
    }

    public void iniciar() {
        LoginView loginView = new LoginView(funcionarioController, scanner);

        while (true) {
            Optional<Funcionario> sessao = loginView.iniciar();
            if (sessao.isEmpty()) {
                System.out.println("Até logo!");
                return;
            }

            Funcionario funcionario = sessao.get();
            switch (funcionario.getFuncao()) {
                case BALCAO     -> new BalcaoView(balcaoController, scanner).iniciar();
                case PREPARACAO -> new PreparacaoView(preparacaoController, scanner).iniciar();
                case GESTOR     -> new GestorView(gestorController, scanner).iniciar();
            }
        }
    }
}
