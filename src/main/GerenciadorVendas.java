package main;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GerenciadorVendas {
	private int IDVenda = 1;
	private double valorHistoricoVenda;
	private double valorHistoricoCusto;
	private double valorHistoricoLucro;

	private Map<Integer, Vendas> historicoVendas = new HashMap<>(); // Hash com todas as vendas realizadas
	private Map<Integer, ItemMenu> itens; // Referência ao mapa de itens

	public double getValorHistoricoVenda() {
		return valorHistoricoVenda;
	}

	public double getValorHistoricoCusto() {
		return valorHistoricoCusto;
	}

	public double getValorHistoricoLucro() {
		return valorHistoricoLucro;
	}

	Scanner scanner = new Scanner(System.in);

	// Construtor

	public GerenciadorVendas(Map<Integer, ItemMenu> itens) {
		this.itens = itens;
	}

	// Método para adicionar venda no Hash

	public void novaVenda() {

		Vendas novaVenda = new Vendas(IDVenda, itens);
		novaVenda.inputVenda();
		
		historicoVendas.put(IDVenda, novaVenda);
		
		valorHistoricoCusto += novaVenda.getValorCusto();
		valorHistoricoVenda += novaVenda.getValorVenda();
		valorHistoricoLucro += novaVenda.getValorLucro();
		IDVenda++;
	}

	// Método para excluir venda

	public void excluirVenda() {
		System.out.println("\n----- Excluir venda -----");
		System.out.print("\nDigite o ID da venda para excluir: ");
		int tempID = scanner.nextInt();

		if (historicoVendas.containsKey(tempID)) {
			Vendas venda = historicoVendas.get(tempID);

			// Recalcular a quantidade de estoque de produto

			for (int i = 0; i < venda.getItensVendidos().size(); i++) {
				ItemVenda item = venda.getItensVendidos().get(i);
				int quantidade = item.getQuantidade();

				Vendas.recalcularEstoque(item.getItemMenu(), quantidade, 2);
			}

			historicoVendas.remove(tempID); // Remover venda
			
			System.out.println("\nVenda de ID = " + tempID + " foi excluida! Os produtos tiveram seus estoques realocados");
			
			return;
		} else {
			throw new IllegalArgumentException("Venda não encontrada com o ID: " + tempID);
		}
	}

	// Método para listar venda

	public void listarVendas() {
		System.out.println("\nListagem das vendas: ");

		if (historicoVendas.size() == 0) { // Verificação caso não possui produtos registrados
			System.out.println("\nNão possui vendas registradas");
		} else {
			for (int i = 1; i <= IDVenda; i++) {

				if (historicoVendas.get(i) != null) {
					(historicoVendas.get(i)).exibirDetalhes();
					System.out.print("-----------------\n");

				}
			}
		}
	}

	// Método para buscar venda

	public void buscarVendaPorID() {
		System.out.print("\nDigite o ID da venda para buscar: ");
		int tempID = scanner.nextInt();
		scanner.nextLine();

		if (historicoVendas.containsKey(tempID)) {

			if (historicoVendas.get(tempID) != null) {
				(historicoVendas.get(tempID)).exibirDetalhes();
				System.out.print("-----------------\n");
			}

		} else {
			throw new IllegalArgumentException("Venda não encontrada com o ID: " + tempID);
		}
	}
	
	public void relatorioVendas() {
		System.out.println("\nRelatório de todas as vendas:");
		System.out.println("Valor bruto total vendido: R$ " + getValorHistoricoVenda());
		System.out.println("Valor de custo total: R$ " + getValorHistoricoCusto());
		System.out.println("Valor de lucro total: R$ " + getValorHistoricoLucro());
	}
}
