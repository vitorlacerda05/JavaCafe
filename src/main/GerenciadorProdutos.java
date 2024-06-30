package main;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GerenciadorProdutos {

	private Map<Integer, ItemMenu> itens = new HashMap<>(); // Hash para guardar os produtos
	private int proximoID = 1;
	private int tempID = 1;
	Scanner scanner = new Scanner(System.in);

	public void createItem() { // Método para criar novo produto

		System.out.print("\n----- Criando o Produto de ID = " + proximoID + " -----");
		System.out.print("\n\nDigite o código do produto: ");
		int codigo = scanner.nextInt();
		scanner.nextLine(); // Consumir a quebra de linha para não bugar

		System.out.print("\nTipos de produto cadastrado");
		System.out.print("\n(1) Café");
		System.out.print("\n(2) Salgado");
		System.out.print("\n(3) Bebidas");
		System.out.print("\n(4) Doces");
		System.out.print("\nDigite o tipo do produto: ");
		int tipo = scanner.nextInt();
		scanner.nextLine();

		System.out.print("\nDigite o nome do produto: ");
		String nome = scanner.nextLine();

		System.out.print("\nDigite o preco de custo do produto: ");
		double precoCusto = scanner.nextDouble();
		scanner.nextLine();

		System.out.print("\nDigite o preco de venda do produto: ");
		double precoVenda = scanner.nextDouble();
		scanner.nextLine();

		System.out.print("\nDigite a quantidade do produto: ");
		int quantidade = scanner.nextInt();
		scanner.nextLine();

		boolean disponivel;
		if (quantidade == 0) { // Se a quantidade do produto for 0, não está disponivel
			disponivel = false;
		} else {
			disponivel = true;
		}

		if (tipo != 0) {
			ItemCafe novoItem = new ItemCafe(proximoID, codigo, tipo, nome, precoCusto, precoVenda, quantidade,
					disponivel, scanner); // Criar produto ItemCafe
			itens.put(proximoID, novoItem); // Adicionar na Hash
		}

		proximoID = proximoID + 1;
		return;
	}

	public void editItem() { // Método para editar item
		System.out.println("\n----- Editar produto -----");
		System.out.print("Digite o ID do produto para editar: ");
		tempID = scanner.nextInt();

		// -- Editar item da instancia ItemCafe

		if (itens.get(tempID) instanceof ItemCafe) {
			ItemCafe item = (ItemCafe) itens.get(tempID); // Casting para ItemCafe
			item.exibirDetalhes(itens, tempID);

			System.out.print("\nDigite qual campo do produto deseja editar: ");
			int opcao = scanner.nextInt();
			scanner.nextLine();

			int newValorInt;
			double newValorDouble;
			String newValorString;

			switch (opcao) {

			case 1: // Campo Nome
				System.out.print("\nEditar campo Nome: " + item.getNome());
				System.out.print("\nDigitar novo valor do campo: ");
				newValorString = scanner.nextLine();
				item.setNome(newValorString);
				break;

			case 2: // Campo Código
				System.out.print("\nEditar campo Código: " + item.getCodigo());
				System.out.print("\nDigitar novo valor do campo: ");
				newValorInt = scanner.nextInt();
				item.setCodigo(newValorInt);
				break;

			case 3: // Campo Tipo
				System.out.print("\nEditar campo Tipo: " + item.getTipo());
				System.out.print("\nDigitar novo valor do campo: ");
				newValorInt = scanner.nextInt();
				item.setTipo(newValorInt);
				break;

			case 4: // Campo Preco de Custo
				System.out.print("\nEditar campo Preço de Custo: " + item.getPrecoCusto());
				System.out.print("\nDigitar novo valor do campo: ");
				newValorDouble = scanner.nextInt();
				item.setPrecoCusto(newValorDouble);
				break;

			case 5: // Campo Preco de Venda
				System.out.print("\nEditar campo Preço de Venda: " + item.getPrecoVenda());
				System.out.print("\nDigitar novo valor do campo: ");
				newValorDouble = scanner.nextInt();
				item.setPrecoVenda(newValorDouble);
				break;

			case 6: // Campo Quantidade
				System.out.print("\nEditar campo Quantidade: " + item.getQuantidade());
				System.out.print("\nDigitar novo valor do campo: ");
				newValorInt = scanner.nextInt();
				item.setQuantidade(newValorInt);

				// Verificação de disponibilidade

				if (item.getQuantidade() == 0) { // Se a quantidade do produto for 0, não está disponivel
					item.setDisponivel(false);
				} else {
					item.setDisponivel(true);
				}

				break;

			case 7: // Campo Disponivel
				System.out.print("Edite a quantidade para modificar a disponibilidade");

			case 8: // Campo Descrição
				System.out.print("\nEditar campo Preço de Venda: " + item.getDescricao());
				System.out.print("\nDigitar novo valor do campo: ");
				newValorString = scanner.nextLine();
				item.setDescricao(newValorString);
				break;

			default:
				throw new IllegalArgumentException("Resposta inválida, digite a opção entre o intervalo dado");
			}
		}

		// ... (lógica para outros tipos de itens, caso for adicionar mais classes)
	}

	public void excludeItem() { // Método para excluir item

		System.out.println("----- Excluir produto -----");
		System.out.print("\nDigite o ID do produto para excluir: ");
		tempID = scanner.nextInt();

		if (itens.containsKey(tempID)) {
			System.out.print("Produto (ID = " + tempID + ") " + itens.get(tempID).getNome() + " foi excluido!\n");
			itens.remove(tempID); // Remover produto
			return;
		} else {
			throw new IllegalArgumentException("Produto não encontrado com o ID: " + tempID);
		}
	}

	public ItemMenu buscarItemPorID() { // Método para buscar item por ID
		System.out.print("Digite o ID do produto para buscar: ");
		tempID = scanner.nextInt();
		scanner.nextLine();

		if (itens.containsKey(tempID)) {

			if (itens.get(tempID) instanceof ItemCafe) {
				((ItemCafe) itens.get(tempID)).exibirDetalhes(itens, tempID);
				System.out.print("-----------------\n");

			}
			// ... (lógica para outros tipos de itens, caso for adicionar mais classes)

			return itens.get(tempID);

		} else {
			throw new IllegalArgumentException("Produto não encontrado com o ID: " + tempID);
		}
	}

	public void listarItens() { // Método para printar listagem de itens

		System.out.println("Listagem dos produtos: ");

		if (itens.size() == 0) { // Verificação caso não possui produtos registrados
			System.out.print("Não possui produtos registrados\n");
		} else {
			for (int i = 1; i <= proximoID; i++) {

				if (itens.get(i) instanceof ItemCafe) {
					((ItemCafe) itens.get(i)).exibirDetalhes(itens, i);
					System.out.print("-----------------\n");

					// ... (lógica para outros tipos de itens, caso for adicionar mais classes)

				}
			}
			return;
		}
	}

	public Map<Integer, ItemMenu> returnItens() { // Retornar a hash de itens
		return itens;
	}
}