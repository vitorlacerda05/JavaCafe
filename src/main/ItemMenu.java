package main;

import java.util.Map;

public abstract class ItemMenu{
	
	// States
	private int id;
	private int codigo;
	private int tipo;
	private String nome; 
	private double precoCusto;
	private double precoVenda;
	private boolean disponivel;
	private int quantidade;
	private String descricao;
	
	public ItemMenu(int id, int codigo, int tipo, String nome, double precoCusto, double precoVenda, int quantidade, boolean disponivel, String descricao) { // Set dos parâmetros passados
		setID(id);
		setCodigo(codigo);
		setTipo(tipo);
		setNome(nome);
		setPrecoCusto(precoCusto);
		setPrecoVenda(precoVenda);
		setQuantidade(quantidade);
		setDisponivel(disponivel);
		setDescricao(descricao);
	}
	
	// Getter and setter
	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}
	
	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	
	
	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public double getPrecoCusto() {
		return precoCusto;
	}
	
	public void setPrecoCusto(double precoCusto) {
		this.precoCusto = precoCusto;
	}
	
	public double getPrecoVenda() {
		return precoVenda;
	}
	
	public void setPrecoVenda(double precoVenda) {
		this.precoVenda = precoVenda;
	}
	
	public boolean isDisponivel() {
		return disponivel;
	}

	public void setDisponivel(boolean disponivel) {
		this.disponivel = disponivel;
	}
	
	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public void exibirDetalhes(Map<Integer, ItemMenu> itens, int tempID) {
		// TODO Auto-generated method stub
	}
}
