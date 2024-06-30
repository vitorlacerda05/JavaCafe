package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfaceUsuario extends JFrame {
    private GerenciadorProdutos gerenciadorProdutos;
    private GerenciadorVendas gerenciadorVendas;
    private JPanel mainPanel;

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
            }
        });
        
        editProduto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorProdutos.editItem();
            }
        });

        listProduto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorProdutos.listarItens();
            }
        });
        
        excludeProduto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerenciadorProdutos.excludeItem();
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
            }
        });

        panel.add(mainPanel, BorderLayout.CENTER);
        add(panel);
    }
}
