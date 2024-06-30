package main;

import java.util.Scanner;

public class InterfaceUsuario {

    static boolean stop = false;
    static int resposta = 0;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        GerenciadorProdutos gerenciador = new GerenciadorProdutos();
        GerenciadorVendas gerenciadorVendas = new GerenciadorVendas(gerenciador.returnItens());

        System.out.println("----- Bem vindo ao Java Café -----");

        while (!stop) {
            System.out.println("\nO que deseja fazer?");
            System.out.println("1. Gerenciar produtos");
            System.out.println("2. Gerenciar vendas");
            System.out.println("3. Sair do Java Café");
            System.out.print("\nDigite sua resposta: ");
            resposta = scanner.nextInt();

            switch (resposta) {
                case 1:
                    gerenciarProdutos(scanner, gerenciador);
                    break;
                case 2:
                    gerenciarVendas(scanner, gerenciadorVendas);
                    break;
                case 3:
                    System.out.print("\nVocê saiu do Java Café!");
                    stop = true;
                    break;
                default:
                    throw new IllegalArgumentException("Resposta inválida, digite a opção entre o intervalo dado");
            }
        }
        scanner.close();
    }

    private static void gerenciarProdutos(Scanner scanner, GerenciadorProdutos gerenciador) {
        System.out.println("\n----- Gerenciando produtos -----");
        System.out.println("\nO que deseja fazer?");
        System.out.println("1. Criar produto");
        System.out.println("2. Editar produto");
        System.out.println("3. Excluir produto");
        System.out.println("4. Buscar produto por ID");
        System.out.println("5. Listar todos os produtos");
        System.out.println("6. Voltar ao menu principal");
        System.out.print("\nDigite sua resposta: ");
        resposta = scanner.nextInt();

        switch (resposta) {
            case 1:
                gerenciador.createItem();
                break;
            case 2:
                gerenciador.editItem();
                break;
            case 3:
                gerenciador.excludeItem();
                break;
            case 4:
                gerenciador.buscarItemPorID();
                break;
            case 5:
                gerenciador.listarItens();
                break;
            case 6:
                System.out.print("\nVoltando ao menu principal...\n");
                return;
            default:
                throw new IllegalArgumentException("Resposta inválida, digite a opção entre o intervalo dado");
        }
    }

    private static void gerenciarVendas(Scanner scanner, GerenciadorVendas gerenciadorVendas) {
        System.out.println("\n----- Gerenciando vendas -----");
        System.out.println("\nO que deseja fazer?");
        System.out.println("1. Adicionar nova venda");
        System.out.println("2. Excluir venda");
        System.out.println("3. Listar todas as vendas");
        System.out.println("4. Buscar venda por ID");
        System.out.println("5. Relatório das vendas");
        System.out.println("6. Voltar ao menu principal");
        System.out.print("\nDigite sua resposta: ");
        resposta = scanner.nextInt();

        switch (resposta) {
            case 1:
                gerenciadorVendas.novaVenda();
                break;
            case 2:
                gerenciadorVendas.excluirVenda();
                break;
            case 3:
                gerenciadorVendas.listarVendas();
                break;
            case 4:
                gerenciadorVendas.buscarVendaPorID();
                break;
            case 5:
                gerenciadorVendas.relatorioVendas();
                break;
            case 6:
                System.out.print("\nVoltando ao menu principal...\n");
                return;
            default:
                throw new IllegalArgumentException("Resposta inválida, digite a opção entre o intervalo dado");
        }
    }
}
