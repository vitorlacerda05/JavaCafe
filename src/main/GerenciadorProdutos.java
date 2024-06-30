package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import connection.ConnectionDB;

public class GerenciadorProdutos {
	private static Map<Integer, ItemMenu> itens = new HashMap<>(); // Hash para guardar os produtos
	private int proximoID = 1;
	private int tempID = 1;
	Scanner scanner = new Scanner(System.in);

	// Método para criar item e adicionar no banco de dados
	public void createItem() {
		JTextField codigoField = new JTextField();
		JTextField tipoField = new JTextField();
		JTextField nomeField = new JTextField();
		JTextField precoCustoField = new JTextField();
		JTextField precoVendaField = new JTextField();
		JTextField quantidadeField = new JTextField();
		JTextField descricaoField = new JTextField();

		Object[] message = { "Código:", codigoField, "Tipo:", tipoField, "Nome:", nomeField, "Preço de Custo:",
				precoCustoField, "Preço de Venda:", precoVendaField, "Quantidade:", quantidadeField, "Descrição:",
				descricaoField };

		int option = JOptionPane.showConfirmDialog(null, message, "Adicionar Produto", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			try {
				// Verificar se algum campo está vazio
				if (codigoField.getText().isEmpty() || tipoField.getText().isEmpty() || nomeField.getText().isEmpty()
						|| precoCustoField.getText().isEmpty() || precoVendaField.getText().isEmpty()
						|| quantidadeField.getText().isEmpty() || descricaoField.getText().isEmpty()) {
					throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
				}

				int codigo = Integer.parseInt(codigoField.getText());
				int tipo = Integer.parseInt(tipoField.getText());
				String nome = nomeField.getText();
				double precoCusto = Double.parseDouble(precoCustoField.getText());
				double precoVenda = Double.parseDouble(precoVendaField.getText());
				int quantidade = Integer.parseInt(quantidadeField.getText());
				String descricao = descricaoField.getText();

				boolean disponivel = quantidade > 0;

				Connection connection = null;
				PreparedStatement produtoPs = null;
				PreparedStatement itemMenuPs = null;
				ResultSet generatedKeys = null;

				try {
					connection = ConnectionDB.getDatabaseConnection();
					connection.setAutoCommit(false);

					String produtoSql = "INSERT INTO produtos (nome, preco) VALUES (?, ?)";
					produtoPs = connection.prepareStatement(produtoSql, PreparedStatement.RETURN_GENERATED_KEYS);
					produtoPs.setString(1, nome);
					produtoPs.setDouble(2, precoVenda);
					produtoPs.executeUpdate();

					generatedKeys = produtoPs.getGeneratedKeys();
					int produtoID = 0;
					if (generatedKeys.next()) {
						produtoID = generatedKeys.getInt(1);
					} else {
						connection.rollback();
						throw new SQLException("Falha ao obter o ID do produto.");
					}

					String itemMenuSql = "INSERT INTO ITEMMENU (codigo, tipo, nome, precocusto, precovenda, disponivel, quantidade, descricao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
					itemMenuPs = connection.prepareStatement(itemMenuSql);
					itemMenuPs.setInt(1, codigo);
					itemMenuPs.setInt(2, tipo);
					itemMenuPs.setString(3, nome);
					itemMenuPs.setDouble(4, precoCusto);
					itemMenuPs.setDouble(5, precoVenda);
					itemMenuPs.setBoolean(6, disponivel);
					itemMenuPs.setInt(7, quantidade);
					itemMenuPs.setString(8, descricao);
					itemMenuPs.executeUpdate();

					connection.commit();

					JOptionPane.showMessageDialog(null, "Produto adicionado com sucesso!");

					ItemCafe novoItem = new ItemCafe(proximoID, codigo, tipo, nome, precoCusto, precoVenda, quantidade,
							disponivel, descricao);
					itens.put(proximoID, novoItem);

					proximoID++;

				} catch (SQLException e) {
					if (connection != null) {
						try {
							connection.rollback();
						} catch (SQLException ex) {
							ex.printStackTrace();
						}
					}
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Erro ao adicionar produto: " + e.getMessage());
				} finally {
					if (generatedKeys != null) {
						try {
							generatedKeys.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if (produtoPs != null) {
						try {
							produtoPs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if (itemMenuPs != null) {
						try {
							itemMenuPs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if (connection != null) {
						try {
							connection.setAutoCommit(true);
							connection.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "Por favor, insira valores válidos para campos numéricos.");
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage());
			}
		}
	}

	// Método para editar algum item do banco de dados
    public void editItem() {
        String idStr = JOptionPane.showInputDialog("Digite o ID do produto para editar:");
        if (idStr == null || idStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID não pode estar vazio.");
            return;
        }
        int tempID;
        try {
            tempID = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
            return;
        }

        ItemCafe item = null;

        String selectSql = "SELECT * FROM ITEMMENU WHERE id = ?";
        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement selectPs = connection.prepareStatement(selectSql)) {
            selectPs.setInt(1, tempID);
            try (ResultSet rs = selectPs.executeQuery()) {
                if (rs.next()) {
                    int codigo = rs.getInt("codigo");
                    int tipo = rs.getInt("tipo");
                    String nome = rs.getString("nome");
                    double precoCusto = rs.getDouble("precocusto");
                    double precoVenda = rs.getDouble("precovenda");
                    boolean disponivel = rs.getBoolean("disponivel");
                    int quantidade = rs.getInt("quantidade");
                    String descricao = rs.getString("descricao");

                    item = new ItemCafe(tempID, codigo, tipo, nome, precoCusto, precoVenda, quantidade, disponivel, descricao);
                } else {
                    JOptionPane.showMessageDialog(null, "Produto não encontrado com o ID: " + tempID);
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao buscar produto: " + e.getMessage());
            return;
        }

        if (item != null) {
            JTextField codigoField = new JTextField(String.valueOf(item.getCodigo()));
            JTextField tipoField = new JTextField(String.valueOf(item.getTipo()));
            JTextField nomeField = new JTextField(item.getNome());
            JTextField precoCustoField = new JTextField(String.valueOf(item.getPrecoCusto()));
            JTextField precoVendaField = new JTextField(String.valueOf(item.getPrecoVenda()));
            JTextField quantidadeField = new JTextField(String.valueOf(item.getQuantidade()));
            JTextField descricaoField = new JTextField(item.getDescricao());

            Object[] message = {
                "Código:", codigoField,
                "Tipo:", tipoField,
                "Nome:", nomeField,
                "Preço de Custo:", precoCustoField,
                "Preço de Venda:", precoVendaField,
                "Quantidade:", quantidadeField,
                "Descrição:", descricaoField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Editar Produto", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    int codigo = Integer.parseInt(codigoField.getText());
                    int tipo = Integer.parseInt(tipoField.getText());
                    String nome = nomeField.getText();
                    double precoCusto = Double.parseDouble(precoCustoField.getText());
                    double precoVenda = Double.parseDouble(precoVendaField.getText());
                    int quantidade = Integer.parseInt(quantidadeField.getText());
                    String descricao = descricaoField.getText();

                    boolean disponivel = quantidade > 0;

                    String updateSql = "UPDATE ITEMMENU SET codigo = ?, tipo = ?, nome = ?, precocusto = ?, precovenda = ?, disponivel = ?, quantidade = ?, descricao = ? WHERE id = ?";
                    try (Connection connection = ConnectionDB.getDatabaseConnection();
                         PreparedStatement updatePs = connection.prepareStatement(updateSql)) {
                        updatePs.setInt(1, codigo);
                        updatePs.setInt(2, tipo);
                        updatePs.setString(3, nome);
                        updatePs.setDouble(4, precoCusto);
                        updatePs.setDouble(5, precoVenda);
                        updatePs.setBoolean(6, disponivel);
                        updatePs.setInt(7, quantidade);
                        updatePs.setString(8, descricao);
                        updatePs.setInt(9, item.getID());

                        int rowsUpdated = updatePs.executeUpdate();
                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(null, "Produto atualizado com sucesso!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Erro ao atualizar produto.");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao atualizar produto: " + e.getMessage());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, insira valores válidos para campos numéricos.");
                }
            }
        }
    }

    // Método para excluir item do banco de dados
    public void excludeItem() {
        String idStr = JOptionPane.showInputDialog("Digite o ID do produto para excluir:");
        if (idStr == null || idStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID não pode estar vazio.");
            return;
        }
        int tempID;
        try {
            tempID = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
            return;
        }

        String sql = "DELETE FROM ITEMMENU WHERE id = ?";
        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tempID);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Produto (ID = " + tempID + ") foi excluído com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "Produto não encontrado com o ID: " + tempID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao excluir o produto: " + e.getMessage());
        }
    }
    
    // Método para buscar item por ID
    public ItemMenu buscarItemPorID() {
        String idStr = JOptionPane.showInputDialog("Digite o ID do produto para buscar:");
        if (idStr == null || idStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID não pode estar vazio.");
            return null;
        }
        int tempID;
        try {
            tempID = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
            return null;
        }

        ItemMenu itemEncontrado = null;

        String sql = "SELECT * FROM ITEMMENU WHERE id = ?";
        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tempID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int codigo = rs.getInt("codigo");
                    int tipo = rs.getInt("tipo");
                    String nome = rs.getString("nome");
                    double precoCusto = rs.getDouble("precocusto");
                    double precoVenda = rs.getDouble("precovenda");
                    boolean disponivel = rs.getBoolean("disponivel");
                    int quantidade = rs.getInt("quantidade");
                    String descricao = rs.getString("descricao");

                    itemEncontrado = new ItemCafe(id, codigo, tipo, nome, precoCusto, precoVenda, quantidade, disponivel, descricao);

                    JOptionPane.showMessageDialog(null,
                        "ID: " + id +
                        "\nCódigo: " + codigo +
                        "\nTipo: " + tipo +
                        "\nNome: " + nome +
                        "\nPreço de Custo: " + precoCusto +
                        "\nPreço de Venda: " + precoVenda +
                        "\nDisponível: " + disponivel +
                        "\nQuantidade: " + quantidade +
                        "\nDescrição: " + descricao,
                        "Detalhes do Produto",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Produto não encontrado com o ID: " + tempID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao buscar produto: " + e.getMessage());
        }

        return itemEncontrado;
    }

    // Método para listar todos os produtos do banco de dados
    public void listarItens() {
        JFrame frame = new JFrame("Lista de Produtos");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Código", "Tipo", "Nome", "Preço de Custo", "Preço de Venda", "Disponível", "Quantidade", "Descrição"}, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM ITEMMENU");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getInt("codigo"),
                    rs.getInt("tipo"),
                    rs.getString("nome"),
                    rs.getDouble("precocusto"),
                    rs.getDouble("precovenda"),
                    rs.getBoolean("disponivel"),
                    rs.getInt("quantidade"),
                    rs.getString("descricao")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao listar produtos: " + e.getMessage());
        }

        frame.add(scrollPane);
        frame.setVisible(true);
    }

	public static Map<Integer, ItemMenu> returnItens() { // Retornar a hash de itens
		return itens;
	}
}