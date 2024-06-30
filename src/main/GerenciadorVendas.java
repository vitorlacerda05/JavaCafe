package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao inserir nova venda: " + e.getMessage());
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
        String sql = "SELECT ID FROM ItemMenu WHERE ID = ?";
        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
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
        String idStr = JOptionPane.showInputDialog("Digite o ID da venda para excluir:");
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
        String checkSql = "SELECT COUNT(*) FROM vendas WHERE vendaID = ?";
        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
            checkPs.setInt(1, tempID);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(null, "Venda com ID = " + tempID + " não existe.");
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao verificar existência da venda: " + e.getMessage());
            return;
        }

        String vendaSql = "DELETE FROM vendas WHERE vendaID = ?";
        String itensVendaSql = "DELETE FROM itens_venda WHERE vendaID = ?";

        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement vendaPs = connection.prepareStatement(vendaSql);
             PreparedStatement itensVendaPs = connection.prepareStatement(itensVendaSql)) {

            connection.setAutoCommit(false); // Iniciar transação

            itensVendaPs.setInt(1, tempID);
            vendaPs.setInt(1, tempID);

            // Remover os itens da venda
            itensVendaPs.executeUpdate();
            // Remover a venda
            vendaPs.executeUpdate();

            connection.commit(); // Confirmar transação

            JOptionPane.showMessageDialog(null, "Venda de ID = " + tempID + " foi excluída!");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao excluir venda: " + e.getMessage());
        }
    }

    // Método para listar vendas

    public void listarVendas() {
        JFrame frame = new JFrame("Lista de Vendas");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Data/Hora", "Cliente", "Valor da Venda", "Valor de Custo", "Valor do Lucro", "Valor do Desconto", "Itens Vendidos"}, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        String vendaSql = "SELECT * FROM vendas";
        String itensVendaSql = "SELECT * FROM itens_venda WHERE vendaID = ?";

        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement vendaPs = connection.prepareStatement(vendaSql);
             PreparedStatement itensVendaPs = connection.prepareStatement(itensVendaSql);
             ResultSet vendaRs = vendaPs.executeQuery()) {

            while (vendaRs.next()) {
                int vendaID = vendaRs.getInt("vendaID");
                Object[] vendaData = {
                    vendaID,
                    vendaRs.getObject("dataHora"),
                    vendaRs.getString("cliente"),
                    vendaRs.getDouble("valorVenda"),
                    vendaRs.getDouble("valorCusto"),
                    vendaRs.getDouble("valorLucro"),
                    vendaRs.getDouble("valorDesconto"),
                    ""
                };

                itensVendaPs.setInt(1, vendaID);
                try (ResultSet itensVendaRs = itensVendaPs.executeQuery()) {
                    StringBuilder itensVendidos = new StringBuilder();
                    while (itensVendaRs.next()) {
                        int itemID = itensVendaRs.getInt("itemID");
                        int quantidade = itensVendaRs.getInt("quantidade");
                        String nomeItem = buscarNomeProduto(itemID);
                        itensVendidos.append(nomeItem).append(" (").append(quantidade).append("), ");
                    }
                    if (itensVendidos.length() > 0) {
                        itensVendidos.setLength(itensVendidos.length() - 2); // Remove a última vírgula e espaço
                    }
                    vendaData[7] = itensVendidos.toString();
                }

                model.addRow(vendaData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao listar vendas: " + e.getMessage());
        }

        frame.add(scrollPane);
        frame.setVisible(true);
    }

    private String buscarNomeProduto(int itemID) throws SQLException {
        String sql = "SELECT nome FROM ItemMenu WHERE ID = ?";
        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, itemID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nome");
                } else {
                    throw new SQLException("Produto não encontrado no banco de dados: " + itemID);
                }
            }

        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar nome do produto: " + e.getMessage());
        }
    }

    // Método para buscar venda por ID

    public void buscarVendaPorID() {
        String idStr = JOptionPane.showInputDialog("Digite o ID da venda para buscar:");
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

        String vendaSql = "SELECT * FROM vendas WHERE vendaID = ?";
        String itensVendaSql = "SELECT * FROM itens_venda WHERE vendaID = ?";

        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement vendaPs = connection.prepareStatement(vendaSql);
             PreparedStatement itensVendaPs = connection.prepareStatement(itensVendaSql)) {

            vendaPs.setInt(1, tempID);

            try (ResultSet vendaRs = vendaPs.executeQuery()) {
                if (vendaRs.next()) {
                    StringBuilder vendaInfo = new StringBuilder();
                    vendaInfo.append("ID: ").append(vendaRs.getInt("vendaID")).append("\n");
                    vendaInfo.append("Data/Hora: ").append(vendaRs.getObject("dataHora")).append("\n");
                    vendaInfo.append("Cliente: ").append(vendaRs.getString("cliente")).append("\n");
                    vendaInfo.append("Valor da Venda: ").append(vendaRs.getDouble("valorVenda")).append("\n");
                    vendaInfo.append("Valor de Custo: ").append(vendaRs.getDouble("valorCusto")).append("\n");
                    vendaInfo.append("Valor do Lucro: ").append(vendaRs.getDouble("valorLucro")).append("\n");
                    vendaInfo.append("Valor do Desconto: ").append(vendaRs.getDouble("valorDesconto")).append("\n");

                    itensVendaPs.setInt(1, tempID);
                    try (ResultSet itensVendaRs = itensVendaPs.executeQuery()) {
                        vendaInfo.append("Itens Vendidos: \n");
                        while (itensVendaRs.next()) {
                            int itemID = itensVendaRs.getInt("itemID");
                            int quantidade = itensVendaRs.getInt("quantidade");
                            String nomeItem = buscarNomeProduto(itemID);
                            vendaInfo.append(" - ").append(nomeItem).append(" (").append(quantidade).append(")\n");
                        }
                    }

                    JOptionPane.showMessageDialog(null, vendaInfo.toString(), "Detalhes da Venda", JOptionPane.INFORMATION_MESSAGE);

                } else {
                    JOptionPane.showMessageDialog(null, "Venda não encontrada com o ID: " + tempID);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao buscar venda por ID: " + e.getMessage());
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

            String relatorio = String.format("Relatório de todas as vendas:\n" +
                    "Valor bruto total vendido: R$ %.2f\n" +
                    "Valor de custo total: R$ %.2f\n" +
                    "Valor de lucro total: R$ %.2f", getValorHistoricoVenda(), getValorHistoricoCusto(), getValorHistoricoLucro());

            JOptionPane.showMessageDialog(null, relatorio, "Relatório de Vendas", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao gerar relatório de vendas: " + e.getMessage());
        }
    }
}
