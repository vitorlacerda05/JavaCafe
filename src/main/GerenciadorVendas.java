package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

import connection.ConnectionDB;

public class GerenciadorVendas {

	private double valorHistoricoVenda;
	private double valorHistoricoCusto;
	private double valorHistoricoLucro;

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

	// Método para adicionar venda no banco de dados

	public void novaVenda() {
		Vendas novaVenda = new Vendas(itens);
		novaVenda.inputVenda();

		// Atualizar históricos
		valorHistoricoCusto += novaVenda.getValorCusto();
		valorHistoricoVenda += novaVenda.getValorVenda();
		valorHistoricoLucro += novaVenda.getValorLucro();

		// Inserir a nova venda no banco de dados
		try {
			inserirVendaNoBanco(novaVenda);
		} catch (SQLException e) {
			System.err.println("Erro ao inserir nova venda: " + e.getMessage());
		}
	}

	private void inserirVendaNoBanco(Vendas novaVenda) throws SQLException {
	    String vendaSql = "INSERT INTO vendas (dataHora, cliente, valorVenda, valorCusto, valorLucro, valorDesconto) VALUES (?, ?, ?, ?, ?, ?)";
	    String itensVendaSql = "INSERT INTO itens_venda (vendaID, itemID, quantidade, subtotal) VALUES (?, ?, ?, ?)";

	    Connection connection = null;
	    PreparedStatement vendaPs = null;
	    PreparedStatement itensVendaPs = null;
	    ResultSet generatedKeys = null;

	    try {
	        connection = ConnectionDB.getDatabaseConnection();
	        connection.setAutoCommit(false); // Iniciar transação

	        // Inserir a venda
	        vendaPs = connection.prepareStatement(vendaSql, PreparedStatement.RETURN_GENERATED_KEYS);
	        vendaPs.setObject(1, novaVenda.getDataHora());
	        vendaPs.setString(2, novaVenda.getCliente());
	        vendaPs.setDouble(3, novaVenda.getValorVenda());
	        vendaPs.setDouble(4, novaVenda.getValorCusto());
	        vendaPs.setDouble(5, novaVenda.getValorLucro());
	        vendaPs.setDouble(6, novaVenda.getValorDesconto());
	        vendaPs.executeUpdate();

	        // Obter o ID da venda gerada
	        generatedKeys = vendaPs.getGeneratedKeys();
	        if (generatedKeys.next()) {
	            novaVenda.setVendaID(generatedKeys.getInt(1));
	        } else {
	            connection.rollback(); // Reverter transação
	            throw new SQLException("Falha ao obter o ID da venda.");
	        }

	        // Inserir os itens vendidos
	        itensVendaPs = connection.prepareStatement(itensVendaSql);
	        for (ItemVenda itemVenda : novaVenda.getItensVendidos()) {
	            int itemID = buscarIDProduto(itemVenda.getItemMenu().getID()); // Busca o ID do produto no banco
	            itensVendaPs.setInt(1, novaVenda.getVendaID());
	            itensVendaPs.setInt(2, itemID);
	            itensVendaPs.setInt(3, itemVenda.getQuantidade());
	            itensVendaPs.setDouble(4, itemVenda.getSubtotal());
	            itensVendaPs.addBatch();
	        }
	        itensVendaPs.executeBatch();

	        connection.commit(); // Confirmar transação

	    } catch (SQLException e) {
	        if (connection != null) {
	            connection.rollback(); // Reverter transação em caso de erro
	        }
	        throw new SQLException("Erro ao inserir venda no banco: " + e.getMessage());
	    } finally {
	        if (generatedKeys != null) {
	            try {
	                generatedKeys.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	        if (vendaPs != null) {
	            try {
	                vendaPs.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	        if (itensVendaPs != null) {
	            try {
	                itensVendaPs.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	        if (connection != null) {
	            try {
	                connection.setAutoCommit(true); // Restaurar modo de confirmação automática
	                connection.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}

	private int buscarIDProduto(int id) throws SQLException {
	    String sql = "SELECT id FROM ITEMMENU WHERE id = ?";
	    try (Connection connection = ConnectionDB.getDatabaseConnection();
	         PreparedStatement ps = connection.prepareStatement(sql)) {

	        ps.setInt(1, id);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("id");
	            } else {
	                throw new SQLException("Produto não encontrado no banco de dados: " + id);
	            }
	        }

	    } catch (SQLException e) {
	        throw new SQLException("Erro ao buscar ID do produto: " + e.getMessage());
	    }
	}

	// Método para excluir venda

	public void excluirVenda() {
	    System.out.println("\n----- Excluir venda -----");
	    System.out.print("\nDigite o ID da venda para excluir: ");
	    int tempID = scanner.nextInt();

	    try (Connection connection = ConnectionDB.getDatabaseConnection();
	         PreparedStatement vendaPs = connection.prepareStatement("DELETE FROM vendas WHERE vendaID = ?");
	         PreparedStatement itensVendaPs = connection.prepareStatement("DELETE FROM itens_venda WHERE vendaID = ?")) {

	        vendaPs.setInt(1, tempID);
	        itensVendaPs.setInt(1, tempID);

	        // Remover os itens da venda
	        itensVendaPs.executeUpdate();
	        // Remover a venda
	        vendaPs.executeUpdate();

	        System.out.println("\nVenda de ID = " + tempID + " foi excluída!");

	    } catch (SQLException e) {
	        System.err.println("Erro ao excluir venda: " + e.getMessage());
	    }
	}


	// Método para listar vendas

	public void listarVendas() {
	    System.out.println("\nListagem das vendas: ");

	    String sql = "SELECT * FROM vendas";

	    try (Connection connection = ConnectionDB.getDatabaseConnection();
	         PreparedStatement ps = connection.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            System.out.println("ID: " + rs.getInt("vendaID"));
	            System.out.println("Data/Hora: " + rs.getObject("dataHora"));
	            System.out.println("Cliente: " + rs.getString("cliente"));
	            System.out.println("Valor da Venda: " + rs.getDouble("valorVenda"));
	            System.out.println("Valor de Custo: " + rs.getDouble("valorCusto"));
	            System.out.println("Valor do Lucro: " + rs.getDouble("valorLucro"));
	            System.out.println("Valor do Desconto: " + rs.getDouble("valorDesconto"));
	            System.out.println("-----------------\n");
	        }

	    } catch (SQLException e) {
	        System.err.println("Erro ao listar vendas: " + e.getMessage());
	    }
	}

	// Método para buscar venda por ID

	public void buscarVendaPorID() {
	    System.out.print("\nDigite o ID da venda para buscar: ");
	    int tempID = scanner.nextInt();
	    scanner.nextLine();

	    String sql = "SELECT * FROM vendas WHERE vendaID = ?";

	    try (Connection connection = ConnectionDB.getDatabaseConnection();
	         PreparedStatement ps = connection.prepareStatement(sql)) {

	        ps.setInt(1, tempID);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                System.out.println("ID: " + rs.getInt("vendaID"));
	                System.out.println("Data/Hora: " + rs.getObject("dataHora"));
	                System.out.println("Cliente: " + rs.getString("cliente"));
	                System.out.println("Valor da Venda: " + rs.getDouble("valorVenda"));
	                System.out.println("Valor de Custo: " + rs.getDouble("valorCusto"));
	                System.out.println("Valor do Lucro: " + rs.getDouble("valorLucro"));
	                System.out.println("Valor do Desconto: " + rs.getDouble("valorDesconto"));
	                System.out.println("-----------------\n");
	            } else {
	                throw new IllegalArgumentException("Venda não encontrada com o ID: " + tempID);
	            }
	        }

	    } catch (SQLException e) {
	        System.err.println("Erro ao buscar venda por ID: " + e.getMessage());
	    }
	}

	// Método para gerar relatório de vendas

	public void relatorioVendas() {
	    String sql = "SELECT SUM(valorVenda) AS totalVenda, SUM(valorCusto) AS totalCusto, SUM(valorLucro) AS totalLucro FROM vendas";

	    try (Connection connection = ConnectionDB.getDatabaseConnection();
	         PreparedStatement ps = connection.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        if (rs.next()) {
	            valorHistoricoVenda = rs.getDouble("totalVenda");
	            valorHistoricoCusto = rs.getDouble("totalCusto");
	            valorHistoricoLucro = rs.getDouble("totalLucro");
	        }

	        System.out.println("\nRelatório de todas as vendas:");
	        System.out.println("Valor bruto total vendido: R$ " + getValorHistoricoVenda());
	        System.out.println("Valor de custo total: R$ " + getValorHistoricoCusto());
	        System.out.println("Valor de lucro total: R$ " + getValorHistoricoLucro());

	    } catch (SQLException e) {
	        System.err.println("Erro ao gerar relatório de vendas: " + e.getMessage());
	    }
	}
}
