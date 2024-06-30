package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import connection.ConnectionDB;

public class ItemCafe extends ItemMenu {

	// Construtor
	public ItemCafe(int id, int codigo, int tipo, String nome, double precoCusto, double precoVenda, int quantidade,
			boolean disponivel, String descricao) {
		super(id, codigo, tipo, nome, precoCusto, precoVenda, quantidade, disponivel, descricao);

	}
	// Descrição é da classe ItemCafe para uma possível escalabilidade do projeto no futuro, alocando outras áreas
	private String descricao;
	
	// Getters and Setters
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	// Método para exibir os detalhes do item vendido
	public void exibirDetalhes(Map<Integer, ItemMenu> itens, int tempID) {
	    String selectSql = "SELECT * FROM ITEMMENU WHERE ID = ?";
	    try (PreparedStatement selectPs = ConnectionDB.getDatabaseConnection().prepareStatement(selectSql)) {
	        selectPs.setInt(1, tempID);
	        try (ResultSet rs = selectPs.executeQuery()) {
	            if (rs.next()) {
	                int codigo = rs.getInt("CODIGO");
	                int tipo = rs.getInt("TIPO");
	                String nome = rs.getString("NOME");
	                double precoCusto = rs.getDouble("PRECOCUSTO");
	                double precoVenda = rs.getDouble("PRECOVENDA");
	                boolean disponivel = rs.getBoolean("DISPONIVEL");
	                int quantidade = rs.getInt("QUANTIDADE");
	                String descricao = rs.getString("DESCRICAO");

	                System.out.println("\n----- Produto ID: " + tempID + " -----");
	                System.out.println("\n1. Nome: " + nome);
	                System.out.println("2. Código: " + codigo);
	                System.out.println("3. Tipo: " + tipo);
	                System.out.println("4. Preço de Custo: " + precoCusto);
	                System.out.println("5. Preço de Venda: " + precoVenda);
	                System.out.println("6. Quantidade: " + quantidade);
	                System.out.println("7. Disponível: " + disponivel);
	                System.out.println("8. Descrição: " + descricao);
	            } else {
	                throw new IllegalArgumentException("Produto não encontrado com o ID: " + tempID);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


}