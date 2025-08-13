package Model;

public class PurchaseItem {
    private int id;
    private int purchaseId;
    private int bookId;
    private int quantity;
    private double subtotal;

    public PurchaseItem() {}

    public PurchaseItem(int id, int purchaseId, int bookId, int quantity, double subtotal) {
        this.id = id;
        this.purchaseId = purchaseId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPurchaseId() { return purchaseId; }
    public void setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}