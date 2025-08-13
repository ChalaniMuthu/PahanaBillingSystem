package Model;

import java.util.Date;
import java.util.List;

public class Purchase {
    private int id;
    private int cashierId;
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private String customerPhone;
    private double totalPrice;
    private Date purchaseDate;
    private List<PurchaseItem> items;

    public Purchase() {}

    public Purchase(int id, int cashierId, String customerName, String customerEmail, String customerAddress, String customerPhone, double totalPrice, Date purchaseDate, List<PurchaseItem> items) {
        this.id = id;
        this.cashierId = cashierId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
        this.customerPhone = customerPhone;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
        this.items = items;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCashierId() { return cashierId; }
    public void setCashierId(int cashierId) { this.cashierId = cashierId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }
    public List<PurchaseItem> getItems() { return items; }
    public void setItems(List<PurchaseItem> items) { this.items = items; }
}