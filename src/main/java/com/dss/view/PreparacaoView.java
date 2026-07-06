package com.dss.view;

import com.dss.controller.PreparacaoController;
import com.dss.model.logica.pedidos.EstadoPreparacao;
import com.dss.model.logica.produtos.Personalizacao;
import com.dss.model.logica.pedidos.ItemPedido;
import com.dss.model.logica.pedidos.Pedido;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class PreparacaoView {
    private final PreparacaoController controller;
    private final Scanner scanner;

    public PreparacaoView(PreparacaoController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void iniciar() {
        while (true) {
            System.out.println("\n--- Funcionário de Preparação ---");
            System.out.println("1. Consultar Lista de Preparação");
            System.out.println("2. Terminar Preparação do Pedido");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> consultarLista();
                case "2" -> terminarPreparacao();
                case "0" -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void consultarLista() {
        List<Pedido> pedidos = controller.consultarListaPreparacao();
        if (pedidos.isEmpty()) { System.out.println("Sem itens para preparar."); return; }
        System.out.println("\n=== Lista de preparação ===");
        for (Pedido p : pedidos) {
            System.out.printf("Pedido #%d:%n", p.getId());
            p.getItens().stream()
                .filter(i -> i.getEstadoPreparacao() == EstadoPreparacao.PENDENTE)
                .forEach(i -> {
                    String pers = i.getPersonalizacoesSelecionadas().stream()
                        .map(Personalizacao::getDescricao)
                        .collect(Collectors.joining(", "));
                    System.out.printf("  - %dx %s%s%n",
                        i.getQuantidade(), i.getProduto().getNome(),
                        pers.isEmpty() ? "" : "  (" + pers + ")");
                });
        }
    }

    private void terminarPreparacao() {
        System.out.print("Número do pedido: ");
        int pedidoId;
        try {
            pedidoId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        List<ItemPedido> pendentes = controller.consultarListaPreparacao().stream()
            .filter(p -> p.getId() == pedidoId)
            .findFirst()
            .map(p -> p.getItens().stream()
                .filter(i -> i.getEstadoPreparacao() == EstadoPreparacao.PENDENTE)
                .collect(java.util.stream.Collectors.toList()))
            .orElse(null);

        if (pendentes == null) {
            System.out.println("Pedido não encontrado ou não está em preparação.");
            return;
        }
        if (pendentes.isEmpty()) {
            System.out.println("Todos os itens deste pedido já estão prontos.");
            return;
        }

        System.out.println("\nItens pendentes:");
        for (int i = 0; i < pendentes.size(); i++) {
            var item = pendentes.get(i);
            String pers = item.getPersonalizacoesSelecionadas().stream()
                .map(Personalizacao::getDescricao)
                .collect(java.util.stream.Collectors.joining(", "));
            System.out.printf("  %d. %dx %s%s%n", i + 1, item.getQuantidade(),
                item.getProduto().getNome(), pers.isEmpty() ? "" : "  (" + pers + ")");
        }

        System.out.print("Número do item a marcar como pronto: ");
        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());
            if (escolha < 1 || escolha > pendentes.size()) {
                System.out.println("Opção inválida.");
                return;
            }
            int itemId = pendentes.get(escolha - 1).getId();
            if (controller.terminarPreparacaoItem(pedidoId, itemId))
                System.out.println("Item marcado como pronto.");
            else
                System.out.println("Erro ao marcar item.");
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida.");
        }
    }
}
