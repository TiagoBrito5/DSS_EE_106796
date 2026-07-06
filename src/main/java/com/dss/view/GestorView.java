package com.dss.view;

import com.dss.controller.GestorController;
import com.dss.model.logica.Ingredientes.Ingrediente;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Scanner;

public class GestorView {
    private final GestorController controller;
    private final Scanner scanner;

    public GestorView(GestorController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void iniciar() {
        while (true) {
            System.out.println("\n--- Gestor ---");
            System.out.println("1. Consultar Volume de Pedidos");
            System.out.println("2. Consultar Produtos mais Vendidos");
            System.out.println("3. Consultar Faturação Total");
            System.out.println("4. Consultar Stock");
            System.out.println("5. Alterar Quantidade de Ingrediente");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> consultarVolume();
                case "2" -> consultarMaisVendidos();
                case "3" -> consultarFaturacao();
                case "4" -> consultarStock();
                case "5" -> alterarQuantidade();
                case "0" -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private LocalDateTime[] lerIntervalo() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            try {
                System.out.print("Data de início (dd/MM/yyyy): ");
                LocalDateTime inicio = LocalDate.parse(scanner.nextLine().trim(), fmt).atStartOfDay();
                System.out.print("Data de fim   (dd/MM/yyyy): ");
                LocalDateTime fim = LocalDate.parse(scanner.nextLine().trim(), fmt).atTime(23, 59, 59);
                if (fim.isBefore(inicio)) {
                    System.out.println("A data de fim não pode ser anterior à data de início.");
                    continue;
                }
                return new LocalDateTime[]{inicio, fim};
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Use dd/MM/yyyy.");
            }
        }
    }

    private void consultarVolume() {
        LocalDateTime[] intervalo = lerIntervalo();
        long total = controller.consultarVolumePedidos(intervalo[0], intervalo[1]);
        System.out.printf("Pedidos entregues no período: %d%n", total);
    }

    private void consultarFaturacao() {
        LocalDateTime[] intervalo = lerIntervalo();
        double total = controller.consultarFaturacaoTotal(intervalo[0], intervalo[1]);
        System.out.printf("Faturação total no período: %.2f€%n", total);
    }

    private void consultarMaisVendidos() {
        LocalDateTime[] intervalo = lerIntervalo();
        Map<String, Long> ranking = controller.consultarProdutosMaisVendidos(intervalo[0], intervalo[1]);
        if (ranking.isEmpty()) { System.out.println("Sem vendas registadas no período indicado."); return; }
        System.out.println("\n=== Produtos mais vendidos ===");
        int pos = 1;
        for (Map.Entry<String, Long> e : ranking.entrySet())
            System.out.printf("%2d. %-25s %d unid.%n", pos++, e.getKey(), e.getValue());
    }

    private void consultarStock() {
        System.out.println("\n=== Stock de ingredientes ===");
        for (Ingrediente i : controller.consultarStock()) {
            String alerta = i.abaixoNivelMinimo() ? "  *** ABAIXO DO MINIMO ***" : "";
            System.out.printf("%-20s %8.2f %-6s (min: %.2f)%s%n",
                i.getNome(), i.getQuantidadeEmStock(), i.getUnidade(), i.getNivelMinimo(), alerta);
        }
    }

    private void alterarQuantidade() {
        System.out.print("Nome do ingrediente: ");
        String nome = scanner.nextLine().trim();
        while (true) {
            System.out.print("Nova quantidade em stock: ");
            try {
                double novaQuantidade = Double.parseDouble(scanner.nextLine().trim());
                if (novaQuantidade < 0) { System.out.println("A quantidade não pode ser negativa."); continue; }
                if (controller.alterarQuantidadeIngrediente(nome, novaQuantidade))
                    System.out.printf("Stock de '%s' atualizado para %.2f.%n", nome, novaQuantidade);
                else
                    System.out.println("Ingrediente não encontrado.");
                return;
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido.");
            }
        }
    }
}
