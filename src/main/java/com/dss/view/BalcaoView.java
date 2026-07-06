package com.dss.view;

import com.dss.controller.BalcaoController;
import com.dss.model.logica.pedidos.EstadoPreparacao;
import com.dss.model.logica.produtos.Personalizacao;
import com.dss.model.logica.produtos.Produto;
import com.dss.model.logica.pedidos.Pedido;
import java.util.*;

public class BalcaoView {
    private final BalcaoController controller;
    private final Scanner scanner;

    public BalcaoView(BalcaoController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void iniciar() {
        while (true) {
            System.out.println("\n--- Funcionário de Balcão ---");
            System.out.println("1. Registar Pedido");
            System.out.println("2. Consultar Estado do Pedido");
            System.out.println("3. Consultar Lista de Preparação");
            System.out.println("4. Entregar Pedido");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> registarPedido();
                case "2" -> consultarEstado();
                case "3" -> consultarListaPreparacao();
                case "4" -> entregarPedido();
                case "0" -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void registarPedido() {
        Pedido pedido = controller.iniciarPedido();

        System.out.println("\n=== Catálogo ===");
        controller.listarProdutos().forEach(System.out::println);

        while (true) {
            System.out.print("\nCódigo do produto (ou 'fim'): ");
            String codigo = scanner.nextLine().trim();
            if ("fim".equalsIgnoreCase(codigo)) break;

            Optional<Produto> opt = controller.obterProduto(codigo);
            if (opt.isEmpty()) { System.out.println("Produto não encontrado."); continue; }

            Produto produto = opt.get();

            System.out.print("Quantidade: ");
            int quantidade;
            try { quantidade = Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Quantidade inválida."); continue; }

            List<Personalizacao> selecionadas = new ArrayList<>();
            List<Personalizacao> disponiveis = produto.getPersonalizacoesDisponiveis();
            if (!disponiveis.isEmpty()) {
                System.out.println("Personalizações disponíveis:");
                for (int i = 0; i < disponiveis.size(); i++)
                    System.out.printf("  %d. %s%n", i + 1, disponiveis.get(i));
                System.out.print("Escolha (ex: 1,3) ou Enter para nenhuma: ");
                String escolha = scanner.nextLine().trim();
                if (!escolha.isEmpty()) {
                    for (String part : escolha.split(",")) {
                        try {
                            int idx = Integer.parseInt(part.trim()) - 1;
                            if (idx >= 0 && idx < disponiveis.size())
                                selecionadas.add(disponiveis.get(idx));
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }

            if (controller.adicionarItem(pedido, produto, quantidade, selecionadas))
                System.out.println("Item adicionado.");
            else
                System.out.println("Erro ao adicionar item.");
        }

        if (pedido.getItens().isEmpty()) {
            System.out.println("Pedido cancelado (sem itens).");
            return;
        }

        Pedido registado = controller.registarPedido(pedido);
        System.out.printf("Pedido #%d registado. Total: %.2f€  Estado: %s%n",
            registado.getId(), registado.getTotal(), registado.getEstado());
    }

    private void consultarEstado() {
        System.out.print("Número do pedido: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            controller.consultarEstadoPedido(id).ifPresentOrElse(
                p -> {
                    System.out.println(p);
                    p.getItens().forEach(i -> System.out.printf("  - %dx %s  [%s]%n",
                        i.getQuantidade(), i.getProduto().getNome(), i.getEstadoPreparacao()));
                },
                () -> System.out.println("Pedido não encontrado.")
            );
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }

    private void consultarListaPreparacao() {
        List<Pedido> pedidos = controller.consultarListaPreparacao();
        if (pedidos.isEmpty()) { System.out.println("Sem pedidos em preparação."); return; }
        System.out.println("\n=== Pedidos em preparação ===");
        for (Pedido p : pedidos) {
            System.out.printf("Pedido #%d:%n", p.getId());
            p.getItens().stream()
                .filter(i -> i.getEstadoPreparacao() == EstadoPreparacao.PENDENTE)
                .forEach(i -> System.out.printf("  - %dx %s%n", i.getQuantidade(), i.getProduto().getNome()));
        }
    }

    private void entregarPedido() {
        System.out.print("Número do pedido: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            if (controller.entregarPedido(id))
                System.out.println("Pedido entregue.");
            else
                System.out.println("Pedido não encontrado ou não está pronto para entrega.");
        } catch (NumberFormatException e) { System.out.println("ID inválido."); }
    }
}
