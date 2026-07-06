package com.dss;

import com.dss.controller.BalcaoController;
import com.dss.controller.FuncionarioController;
import com.dss.controller.GestorController;
import com.dss.controller.PreparacaoController;
import com.dss.model.GestorPadaria;
import com.dss.model.ILogicaPadaria;
import com.dss.model.data.DatabaseConnection;
import com.dss.model.data.FuncionarioDAO;
import com.dss.model.data.IngredienteDAO;
import com.dss.model.data.PedidoDAO;
import com.dss.model.data.ProdutoDAO;
import com.dss.model.logica.Ingredientes.IGestStock;
import com.dss.model.logica.Ingredientes.GestStock;
import com.dss.model.logica.funcionarios.GestFuncionario;
import com.dss.model.logica.funcionarios.IGestFuncionarios;
import com.dss.model.logica.pedidos.GestPedidos;
import com.dss.model.logica.pedidos.IGestPedidos;
import com.dss.model.logica.produtos.GestCatalogo;
import com.dss.model.logica.produtos.IGestCatalogo;
import com.dss.view.MainView;

public class App {
    public static void main(String[] args) {
        IngredienteDAO  ingredienteDAO  = new IngredienteDAO();
        ProdutoDAO      produtoDAO      = new ProdutoDAO();
        PedidoDAO       pedidoDAO       = new PedidoDAO(produtoDAO);
        FuncionarioDAO  funcionarioDAO  = new FuncionarioDAO();

        IGestCatalogo     gestorCatalogo     = new GestCatalogo(produtoDAO);
        IGestPedidos      gestorPedidos      = new GestPedidos(pedidoDAO);
        IGestStock        gestorStock        = new GestStock(ingredienteDAO);
        IGestFuncionarios gestorFuncionarios = new GestFuncionario(funcionarioDAO);

        ILogicaPadaria gestorNegocio = new GestorPadaria(gestorCatalogo, gestorPedidos, gestorStock, gestorFuncionarios);

        BalcaoController      balcao      = new BalcaoController(gestorNegocio);
        PreparacaoController  preparacao  = new PreparacaoController(gestorNegocio);
        GestorController      gestor      = new GestorController(gestorNegocio);
        FuncionarioController funcionario = new FuncionarioController(gestorNegocio);

        try {
            new MainView(balcao, preparacao, gestor, funcionario).iniciar();
        } finally {
            DatabaseConnection.fechar();
        }
    }
}
