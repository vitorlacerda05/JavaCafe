package main;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Vendas {

	private int vendaID;
	private LocalDateTime dataHora;
	private String cliente;
	private static List<ItemVenda> itensVendidos; // Array para adicionar ItemVenda
	private double valorVenda;
	private double valorCusto;
	private double valorLucro;
	private double valorDesconto;
	private Map<Integer, ItemMenu> itens;

	// Constructor

	public Vendas(int vendaID, Map<Integer, ItemMenu> itens) {
		this.vendaID = vendaID;
		this.itens = itens;
	}

	// Getters and Setters

	public int getVendaID() {
		return vendaID;
	}

	public void setVendaID(int vendaID) {
		this.vendaID = vendaID;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}

	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public double getValorVenda() {
		return valorVenda;
	}

	public void setValorVenda(double valorVenda) {
		this.valorVenda = valorVenda;
	}

	public double getValorLucro() {
		return valorLucro;
	}

	public void setValorLucro(double valorLucro) {
		this.valorLucro = valorLucro;
	}

	public double getValorCusto() {
		return valorCusto;
	}

	public void setValorCusto(double valorCusto) {
		this.valorCusto = valorCusto;
	}

	public double getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}
	

    public List<ItemVenda> getItensVendidos() {
        return itensVendidos;
    }


	// Methods

	Scanner scanner = new Scanner(System.in);

	public void inputVenda() {

		// Adicionar nome do cliente

		System.out.print("\nDigite o nome do cliente: ");
		this.cliente = scanner.nextLine();

		// Adicionar horário da venda

		this.dataHora = LocalDateTime.now();

		// While para adicionar produtos

		while (true) {
			System.out.println("\nAdicionar produto");
			System.out.print("\nDigite o ID do item (0 para finalizar): ");
			int itemID = scanner.nextInt();
			scanner.nextLine(); // Consumir a quebra de linha

			if (itemID == 0) {
				break;
			}

			ItemMenu item = itens.get(itemID);

			// Digitar a quantidade

			System.out.print("\nDigite a quantidade que foi vendida do produto: ");
			int quantidade = scanner.nextInt();
			scanner.nextLine(); // Consumir a quebra de linha

			recalcularEstoque(item, quantidade, 1); // Recalcula estoque e adiciona produto
		}

		// Calcula o valor total da venda, custo e lucro

		for (ItemVenda item : itensVendidos) { // Percorre todo ItensVendidos e adiciona o valor de cada item para obter
												// o total
			valorVenda += item.getSubtotal();
			valorCusto += item.getQuantidade() * item.getItemMenu().getPrecoCusto();
		}
		valorLucro = valorVenda - valorCusto;

		// Recalcular com descontos e printar o preço final

		recalcularDesconto();

	}
	
	public static void recalcularEstoque(ItemMenu item, int quantidade, int option) {
		
		if (option == 1) { // option = 1 é remover estoque
			if (item == null) {
				System.out.println("Item não encontrado.");
			}

			else if (item.getQuantidade() < quantidade) {
				System.out.println("Quantidade insuficiente em estoque.");
			}

			else {
				// Atualiza o estoque

				item.setQuantidade(item.getQuantidade() - quantidade);
				item.setDisponivel(item.getQuantidade() > 0);

				// Adição na lista do objeto itemVenda

				ItemVenda itemVenda = new ItemVenda(item, quantidade);
				itensVendidos = new ArrayList<>();
				itensVendidos.add(itemVenda);
			}
		}
		
		else if (option == 2) { // option = 2 é adicionar estoque
			if (item == null) {
				System.out.println("Item não encontrado.");
			}
			else {
				item.setQuantidade(item.getQuantidade() + quantidade);
				if (item.isDisponivel() == false){
					item.setDisponivel(true);
				}
			}
		}
	}
	
	public void recalcularDesconto() {
		System.out.println("Valor total da venda: " + valorVenda);
		System.out.print("\nDigite o valor da porcentagem de desconto [0-100](0 para não dar desconto): ");
		int desconto = scanner.nextInt();

		if (desconto == 0) {
			System.out.println("Não foi adicionado desconto");
			System.out.println("Valor final é: " + valorVenda);
		} else if (desconto > 0 && desconto <= 100) {
			System.out.println("\nDesconto de " + desconto + "%");
			valorDesconto = valorVenda * (desconto * 0.01); // Valor do desconto
			valorVenda = valorVenda - valorDesconto; // Valor da venda com o desconto
			valorLucro = valorVenda - valorCusto;

			System.out.println("Desconto sobre preço total de R$" + valorDesconto);
		} else {
			System.out.println("Não foi aplicado desconto. Digite um valor de desconto esperado");
		}

		System.out.println("\n----- Venda finalizada! -----");
		System.out.println("\nValor total da venda: " + valorVenda);
		System.out.println("Valor total do custo: " + valorCusto);
		System.out.println("Valor total do lucro: " + valorLucro);
	}

	public void exibirDetalhes() {
		System.out.println("\n----- Venda ID: " + vendaID + " -----");
		System.out.println("\nCliente: " + cliente);
		System.out.println("Horário: " + dataHora);

		System.out.println("\nProdutos vendidos");
		for (ItemVenda item : itensVendidos) {
			System.out.println("Produto: " + item.getItemMenuNome() + ", Quantidade: " + item.getQuantidade()
					+ ", Preço total: " + item.getSubtotal());
		}

		System.out.println("\nValor de Venda: " + valorVenda);
		System.out.println("Valor de Custo: " + valorCusto);
		System.out.println("Valor do desconto: " + valorDesconto);
		System.out.println("Lucro: " + valorLucro);
	}

}