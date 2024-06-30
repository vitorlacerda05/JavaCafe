package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import connection.ConnectionDB;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InterfaceUsuario extends JFrame {
    private static final long serialVersionUID = 1L;
	private GerenciadorProdutos gerenciadorProdutos;
    private GerenciadorVendas gerenciadorVendas;
    private JPanel mainPanel;
    private JLabel valorHistoricoVendaLabel;
    private JLabel valorHistoricoLucroLabel;

    public InterfaceUsuario() {
        gerenciadorProdutos = new GerenciadorProdutos();
        gerenciadorVendas = new GerenciadorVendas(GerenciadorProdutos.returnItens());

        setTitle("Sistema de Gerenciamento de Cafeteria");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        mainPanel = new JPanel(new CardLayout());

        // Adicionando as opções ao menu
        JMenuBar menuBar = new JMenuBar();

        JMenu menuProduto = new JMenu("Produtos");
        JMenuItem addProduto = new JMenuItem("Adicionar Produto");
        JMenuItem editProduto = new JMenuItem("Editar Produto");
        JMenuItem excludeProduto = new JMenuItem("Excluir Produto");
        JMenuItem searchProduto = new JMenuItem("Buscar Produto");
        JMenuItem listProduto = new JMenuItem("Listar produtos");

        menuProduto.add(addProduto);
        menuProduto.add(editProduto);
        menuProduto.add(excludeProduto);
        menuProduto.add(searchProduto);
        menuProduto.add(listProduto);

        JMenu menuVenda = new JMenu("Vendas");
        JMenuItem addVenda = new JMenuItem("Adicionar Venda");
        JMenuItem excludeVenda = new JMenuItem("Excluir Venda");
        JMenuItem reportVenda = new JMenuItem("Relatório das Vendas");
        JMenuItem searchVenda = new JMenuItem("Buscar Venda");
        JMenuItem listVenda = new JMenuItem("Listar Vendas");

        menuVenda.add(addVenda);
        menuVenda.add(excludeVenda);
        menuVenda.add(reportVenda);
        menuVenda.add(searchVenda);
        menuVenda.add(listVenda);

        menuBar.add(menuProduto);
        menuBar.add(menuVenda);

        setJMenuBar(menuBar);

        // Adicionando ação para as opções de Gerenciamento de produtos
        addProduto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorProdutos.createItem();
                atualizarRelatorios();
            }
        });

        editProduto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorProdutos.editItem();
                atualizarRelatorios();
            }
        });

        listProduto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorProdutos.listarItens();
                atualizarRelatorios();
            }
        });

        excludeProduto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorProdutos.excludeItem();
                atualizarRelatorios();
            }
        });

        searchProduto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorProdutos.buscarItemPorID();
            }
        });

        // Adicionando ação para as opções de Gerenciamento de vendas
        addVenda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorVendas.novaVenda();
                atualizarRelatorios();
            }
        });

        listVenda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorVendas.listarVendas();
            }
        });

        excludeVenda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorVendas.excluirVenda();
                atualizarRelatorios();
            }
        });

        searchVenda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorVendas.buscarVendaPorID();
            }
        });

        reportVenda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorVendas.relatorioVendas();
                atualizarRelatorios();
            }
        });

        // Configuração do layout principal
        JLabel headerLabel = new JLabel("Gerenciamento do Java Café", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JPanel relatorioPanel = new JPanel();
        relatorioPanel.setLayout(new BoxLayout(relatorioPanel, BoxLayout.Y_AXIS));
        relatorioPanel.setBorder(BorderFactory.createTitledBorder("Relatórios Total das Vendas"));


        valorHistoricoVendaLabel = new JLabel("Valor de venda total (bruto): R$ 0,00", JLabel.CENTER);
        valorHistoricoLucroLabel = new JLabel("Valor de lucro total: R$ 0,00", JLabel.CENTER);
        relatorioPanel.add(valorHistoricoVendaLabel);
        relatorioPanel.add(valorHistoricoLucroLabel);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        botoesPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton addVendaButton = new JButton("Adicionar Venda");
        JButton addProdutoButton = new JButton("Adicionar Produto");
        JButton listProdutoButton = new JButton("Listar Produtos");
        JButton listVendaButton = new JButton("Listar Vendas");

        botoesPanel.add(addVendaButton);
        botoesPanel.add(addProdutoButton);
        botoesPanel.add(listProdutoButton);
        botoesPanel.add(listVendaButton);

        addVendaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorVendas.novaVenda();
                atualizarRelatorios();
            }
        });

        addProdutoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorProdutos.createItem();
                atualizarRelatorios();
            }
        });

        listProdutoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorProdutos.listarItens();
                atualizarRelatorios();
            }
        });

        listVendaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorVendas.listarVendas();
            }
        });

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(relatorioPanel, BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);

        add(panel);
        atualizarRelatorios();
    }

    private void atualizarRelatorios() {
        double valorHistoricoVenda = 0.0;
        double valorHistoricoLucro = 0.0;

        String sql = "SELECT SUM(valorVenda) AS totalVenda, SUM(valorLucro) AS totalLucro FROM vendas";

        try (Connection connection = ConnectionDB.getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                valorHistoricoVenda = rs.getDouble("totalVenda");
                valorHistoricoLucro = rs.getDouble("totalLucro");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        valorHistoricoVendaLabel.setText(String.format("Valor de venda total (bruto): R$ %.2f", valorHistoricoVenda));
        valorHistoricoLucroLabel.setText(String.format("Valor de lucro total: R$ %.2f", valorHistoricoLucro));
    }
}
