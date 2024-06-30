package main;

import javax.swing.*;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import connection.ConnectionDB;

public class Vendas {
    private int vendaID;
    private LocalDateTime dataHora;
    private String cliente;
    private List<ItemVenda> itensVendidos; // Lista para adicionar ItemVenda
    private double valorVenda;
    private double valorCusto;
    private double valorLucro;
    private double valorDesconto;
    private Map<Integer, ItemMenu> itens;

    // Constructor
    public Vendas(Map<Integer, ItemMenu> itens) {
        this.itens = itens;
        this.itensVendidos = new ArrayList<>();
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

    public void inputVenda() {
        // Adicionar nome do cliente
        this.cliente = JOptionPane.showInputDialog("Digite o nome do cliente:");

        // Verificar se o cliente cancelou a entrada
        if (this.cliente == null) {
            return;
        }

        // Adicionar horário da venda
        this.dataHora = LocalDateTime.now();

        while (true) {
            String itemIDStr = JOptionPane.showInputDialog("Digite o ID do item (0 para finalizar):");
            if (itemIDStr == null || itemIDStr.trim().isEmpty()) {
                break;
            }

            int itemID;
            try {
                itemID = Integer.parseInt(itemIDStr.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ID do item inválido. Por favor, insira um número válido.");
                continue;
            }

            if (itemID == 0) {
                break;
            }

            ItemMenu item = consultarItemNoBanco(itemID);

            if (item != null) {
                String quantidadeStr = JOptionPane.showInputDialog("Digite a quantidade que foi vendida do produto:");
                if (quantidadeStr == null || quantidadeStr.trim().isEmpty()) {
                    continue;
                }

                int quantidade;
                try {
                    quantidade = Integer.parseInt(quantidadeStr.trim());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Quantidade inválida. Por favor, insira um número válido.");
                    continue;
                }

                if (quantidade > 0) {
                    ItemVenda itemVenda = new ItemVenda(item, quantidade);
                    itensVendidos.add(itemVenda);

                    // Atualizar a quantidade no estoque
                    item.setQuantidade(item.getQuantidade() - quantidade);
                    if (item.getQuantidade() == 0) {
                        item.setDisponivel(false);
                    }
                    atualizarQuantidadeNoBanco(item);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Produto não encontrado com o ID: " + itemID);
            }
        }
    }

    private ItemMenu consultarItemNoBanco(int itemID) {
        String sql = "SELECT * FROM ITEMMENU WHERE id = ?";
        try (Connection conn = ConnectionDB.getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int codigo = rs.getInt("codigo");
                    int tipo = rs.getInt("tipo");
                    String nome = rs.getString("nome");
                    double precoCusto = rs.getDouble("precoCusto");
                    double precoVenda = rs.getDouble("precoVenda");
                    boolean disponivel = rs.getBoolean("disponivel");
                    int quantidade = rs.getInt("quantidade");
                    String descricao = rs.getString("descricao");

                    return new ItemCafe(id, codigo, tipo, nome, precoCusto, precoVenda, quantidade, disponivel, descricao);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void atualizarQuantidadeNoBanco(ItemMenu item) {
        String sql = "UPDATE ITEMMENU SET quantidade = ?, disponivel = ? WHERE id = ?";
        try (Connection conn = ConnectionDB.getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, item.getQuantidade());
            ps.setBoolean(2, item.isDisponivel());
            ps.setInt(3, item.getID());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void recalcularEstoque(ItemMenu item, int quantidade, int option) {
        if (option == 1) { // option = 1 é remover estoque
            if (item == null) {
                JOptionPane.showMessageDialog(null, "Item não encontrado.");
            } else if (item.getQuantidade() < quantidade) {
                JOptionPane.showMessageDialog(null, "Quantidade insuficiente em estoque.");
            } else {
                item.setQuantidade(item.getQuantidade() - quantidade);
                item.setDisponivel(item.getQuantidade() > 0);

                atualizarQuantidadeNoBanco(item);

                ItemVenda itemVenda = new ItemVenda(item, quantidade);
                if (itensVendidos == null) {
                    itensVendidos = new ArrayList<>();
                }
                itensVendidos.add(itemVenda);
            }
        } else if (option == 2) { // option = 2 é adicionar estoque
            if (item == null) {
                JOptionPane.showMessageDialog(null, "Item não encontrado.");
            } else {
                item.setQuantidade(item.getQuantidade() + quantidade);
                if (!item.isDisponivel()) {
                    item.setDisponivel(true);
                }

                atualizarQuantidadeNoBanco(item);
            }
        }
    }

    public int recalcularDesconto() {
        JTextField descontoField = new JTextField();
        Object[] message = {
            "Valor total da venda: R$ " + valorVenda,
            "Valor total do custo: R$ " + valorCusto,
            "Valor total do lucro: R$ " + valorLucro,
            "\nDigite o valor da porcentagem de desconto [0-100](0 para não dar desconto):", descontoField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Aplicar Desconto", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int desconto = Integer.parseInt(descontoField.getText());
                if (desconto < 0 || desconto > 100) {
                    JOptionPane.showMessageDialog(null, "Por favor, insira um valor válido para o desconto.");
                    return recalcularDesconto(); // Chamar novamente até inserir um valor válido
                }
                return desconto;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Por favor, insira um valor numérico.");
                return recalcularDesconto(); // Chamar novamente até inserir um valor válido
            }
        } else {
            JOptionPane.showMessageDialog(null, "Operação cancelada.");
            return 0;
        }
    }

    public void exibirDetalhes() {
        StringBuilder detalhes = new StringBuilder();
        detalhes.append("Venda ID: ").append(vendaID).append("\n");
        detalhes.append("Cliente: ").append(cliente).append("\n");
        detalhes.append("Horário: ").append(dataHora).append("\n\n");

        detalhes.append("Produtos vendidos:\n");
        for (ItemVenda item : itensVendidos) {
            detalhes.append("Produto: ").append(item.getItemMenu().getNome())
                    .append(", Quantidade: ").append(item.getQuantidade())
                    .append(", Preço total: ").append(item.getSubtotal()).append("\n");
        }

        detalhes.append("\nValor de Venda: ").append(valorVenda).append("\n");
        detalhes.append("Valor de Custo: ").append(valorCusto).append("\n");
        detalhes.append("Valor do Desconto: ").append(valorDesconto).append("\n");
        detalhes.append("Lucro: ").append(valorLucro).append("\n");

        JOptionPane.showMessageDialog(null, detalhes.toString(), "Detalhes da Venda", JOptionPane.INFORMATION_MESSAGE);
    }
}
