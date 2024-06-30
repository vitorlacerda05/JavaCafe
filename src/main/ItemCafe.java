package main;

import java.util.Map;
import java.util.Scanner;

public class ItemCafe extends ItemMenu {

	public ItemCafe(int ID, int codigo, int tipo, String nome, double precoCusto, double precoVenda, int quantidade,
			boolean disponivel, Scanner scanner) {
		super(ID, codigo, tipo, nome, precoCusto, precoVenda, quantidade, disponivel); 

		System.out.println("\nDigite a descrição do produto: ");
		String descricao = scanner.nextLine();
		setDescricao(descricao); // descricão é específico para ItemCafe, portanto é setado aqui, e não em ItemMenu

	}

	private String descricao;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public void exibirDetalhes(Map<Integer, ItemMenu> itens, int tempID) {
		System.out.println("\n----- Produto ID: " + this.getID() + " -----");
		System.out.println("\n1. Nome: " + this.getNome());
		System.out.println("2. Código: " + this.getCodigo());
		System.out.println("3. Tipo: " + this.getTipo());
		System.out.println("4. Preço de Custo: " + this.getPrecoCusto());
		System.out.println("5. Preço de Venda: " + this.getPrecoVenda());
		System.out.println("6. Quantidade: " + this.getQuantidade());
		System.out.println("7. Disponível: " + this.isDisponivel());
		System.out.println("8. Descrição: " + this.getDescricao());
	}

}