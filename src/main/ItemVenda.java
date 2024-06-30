package main;

public class ItemVenda {
    private ItemMenu itemMenu;
    private int quantidade;

    // Construtor
    public ItemVenda(ItemMenu itemMenu, int quantidade) {
        this.itemMenu = itemMenu;
        this.quantidade = quantidade;
    }

    // Getters
    public ItemMenu getItemMenu() {
        return itemMenu;
    }

    public String getItemMenuNome() {
        return itemMenu.getNome();
    }

    public int getQuantidade() {
        return quantidade;
    }

    // Método para calcular o subtotal do item
    public double getSubtotal() {
        return itemMenu.getPrecoVenda() * quantidade;
    }
}
