package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.sql.*;
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

        Object[] message = {
            "Código:", codigoField,
            "Tipo:", tipoField,
            "Nome:", nomeField,
            "Preço de Custo:", precoCustoField,
            "Preço de Venda:", precoVendaField,
            "Quantidade:", quantidadeField,
            "Descrição:", descricaoField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Adicionar Produto", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                if (codigoField.getText().isEmpty() || tipoField.getText().isEmpty() || nomeField.getText().isEmpty() ||
                    precoCustoField.getText().isEmpty() || precoVendaField.getText().isEmpty() || quantidadeField.getText().isEmpty() ||
                    descricaoField.getText().isEmpty()) {
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

                String sql = "INSERT INTO ItemMenu (codigo, tipo, nome, precoCusto, precoVenda, disponivel, quantidade, descricao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (Connection connection = ConnectionDB.getDatabaseConnection();
                     PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, codigo);
                    ps.setInt(2, tipo);
                    ps.setString(3, nome);
                    ps.setDouble(4, precoCusto);
                    ps.setDouble(5, precoVenda);
                    ps.setBoolean(6, disponivel);
                    ps.setInt(7, quantidade);
                    ps.setString(8, descricao);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Produto adicionado com sucesso!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao adicionar produto: " + e.getMessage());
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

        String selectSql = "SELECT * FROM ItemMenu WHERE ID = ?";
        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement selectPs = connection.prepareStatement(selectSql)) {
            selectPs.setInt(1, tempID);
            try (ResultSet rs = selectPs.executeQuery()) {
                if (rs.next()) {
                    int codigo = rs.getInt("codigo");
                    int tipo = rs.getInt("tipo");
                    String nome = rs.getString("nome");
                    double precoCusto = rs.getDouble("precoCusto");
                    double precoVenda = rs.getDouble("precoVenda");
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

                    String updateSql = "UPDATE ItemMenu SET codigo = ?, tipo = ?, nome = ?, precoCusto = ?, precoVenda = ?, disponivel = ?, quantidade = ?, descricao = ? WHERE ID = ?";
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

        // Verificar se o ID existe
        String checkSql = "SELECT COUNT(*) FROM ItemMenu WHERE ID = ?";
        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
            checkPs.setInt(1, tempID);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(null, "Produto com ID = " + tempID + " não existe.");
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao verificar existência do produto: " + e.getMessage());
            return;
        }

        // Excluir produto
        String sql = "DELETE FROM ItemMenu WHERE ID = ?";
        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tempID);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Produto (ID = " + tempID + ") foi excluído com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao excluir o produto. Produto com ID = " + tempID + " não encontrado.");
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

        String sql = "SELECT * FROM ItemMenu WHERE ID = ?";
        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tempID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("ID");
                    int codigo = rs.getInt("codigo");
                    int tipo = rs.getInt("tipo");
                    String nome = rs.getString("nome");
                    double precoCusto = rs.getDouble("precoCusto");
                    double precoVenda = rs.getDouble("precoVenda");
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
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM ItemMenu");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ID"),
                    rs.getInt("codigo"),
                    rs.getInt("tipo"),
                    rs.getString("nome"),
                    rs.getDouble("precoCusto"),
                    rs.getDouble("precoVenda"),
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
