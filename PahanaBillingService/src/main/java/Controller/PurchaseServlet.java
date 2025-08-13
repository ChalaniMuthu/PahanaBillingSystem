package Controller;

import DAO.BookDAO;
import DAO.CashierDAO;
import DAO.PurchaseDAO;
import DAOImpl.BookDAOImpl;
import DAOImpl.CashierDAOImpl;
import DAOImpl.PurchaseDAOImpl;
import Model.Book;
import Model.Cashier;
import Model.Purchase;
import Model.PurchaseItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseServlet {
    private final PurchaseDAO purchaseDAO = new PurchaseDAOImpl();
    private final BookDAO bookDAO = new BookDAOImpl();
    private final CashierDAO cashierDAO = new CashierDAOImpl();

    public void processPurchase() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            List<PurchaseItem> items = new ArrayList<>();
            double totalPrice = 0.0;

            // Select books and quantities
            while (true) {
                System.out.print("Enter Book ID (0 to finish): ");
                int bookId = Integer.parseInt(reader.readLine().trim());
                if (bookId == 0) break;
                Book book = bookDAO.getById(bookId);
                if (book == null) {
                    System.out.println("Book not found.");
                    continue;
                }
                System.out.print("Enter Quantity (≤ " + book.getQuantity() + "): ");
                int quantity = Integer.parseInt(reader.readLine().trim());
                if (quantity <= 0 || quantity > book.getQuantity()) {
                    System.out.println("Invalid quantity. Must be > 0 and ≤ " + book.getQuantity() + ".");
                    continue;
                }
                double subtotal = book.getPrice() * quantity;
                items.add(new PurchaseItem(0, 0, bookId, quantity, subtotal));
                totalPrice += subtotal;
                System.out.println("Added: " + book.getName() + ", Quantity: " + quantity + ", Subtotal: " + subtotal);
            }

            if (items.isEmpty()) {
                System.out.println("No books selected.");
                return;
            }

            // Enter cashier ID
            System.out.print("Enter Cashier ID: ");
            int cashierId = Integer.parseInt(reader.readLine().trim());
            Cashier cashier = cashierDAO.getById(cashierId);
            if (cashier == null) {
                System.out.println("Cashier not found.");
                return;
            }

            // Enter customer details
            System.out.print("Enter Customer Name: ");
            String customerName = reader.readLine().trim();
            if (customerName.isEmpty()) {
                System.out.println("Error: Customer name is required.");
                return;
            }

            System.out.print("Enter Customer Email: ");
            String customerEmail = reader.readLine().trim();
            if (customerEmail.isEmpty()) {
                System.out.println("Error: Customer email is required.");
                return;
            }

            System.out.print("Enter Customer Address: ");
            String customerAddress = reader.readLine().trim();
            if (customerAddress.isEmpty()) {
                System.out.println("Error: Customer address is required.");
                return;
            }

            System.out.print("Enter Customer Phone: ");
            String customerPhone = reader.readLine().trim();
            if (customerPhone.isEmpty()) {
                System.out.println("Error: Customer phone is required.");
                return;
            }

            // Create and process purchase
            Purchase purchase = new Purchase(0, cashierId, customerName, customerEmail, customerAddress, customerPhone, totalPrice, new Date(), items);
            boolean success = purchaseDAO.addPurchase(purchase);
            System.out.println("Purchase successful: " + success + " (Total: " + totalPrice + ")");
            if (success) {
                System.out.println("Purchase Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(purchase.getPurchaseDate()));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        promptBack();
    }

    private void promptBack() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Press Enter to go back to menu...");
            reader.readLine();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Main method with menu-driven interface
    public static void main(String[] args) {
        PurchaseServlet servlet = new PurchaseServlet();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Process Purchase");
            System.out.println("2. Get Purchase By ID");
            System.out.println("3. Get All Purchases");
            System.out.println("4. Update Purchase");
            System.out.println("5. Delete Purchase");
            System.out.println("6. Exit");
            System.out.print("Enter choice (1-6): ");

            try {
                int choice = Integer.parseInt(reader.readLine().trim());
                switch (choice) {
                    case 1:
                        servlet.processPurchase();
                        break;
                    case 2:
                        servlet.getPurchaseById();
                        break;
                    case 3:
                        servlet.getAllPurchases();
                        break;
                    case 4:
                        servlet.updatePurchase();
                        break;
                    case 5:
                        servlet.deletePurchase();
                        break;
                    case 6:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter 1-6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number (1-6).");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public void getPurchaseById() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter Purchase ID: ");
            int id = Integer.parseInt(reader.readLine().trim());
            Purchase purchase = purchaseDAO.getById(id);
            if (purchase != null) {
                System.out.println("Purchase Details: id=" + purchase.getId() + ", cashier_id=" + purchase.getCashierId() +
                        ", customer_name=\"" + purchase.getCustomerName() + "\", customer_email=\"" + purchase.getCustomerEmail() +
                        "\", customer_address=\"" + purchase.getCustomerAddress() + "\", customer_phone=\"" + purchase.getCustomerPhone() +
                        "\", total_price=" + purchase.getTotalPrice() + ", purchase_date=" +
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(purchase.getPurchaseDate()));
                for (PurchaseItem item : purchase.getItems()) {
                    System.out.println("  Item: book_id=" + item.getBookId() + ", quantity=" + item.getQuantity() + ", subtotal=" + item.getSubtotal());
                }
            } else {
                System.out.println("Purchase not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        promptBack();
    }

    public void getAllPurchases() {
        List<Purchase> purchases = purchaseDAO.getAll();
        if (purchases != null && !purchases.isEmpty()) {
            System.out.println("All Purchases:");
            for (Purchase p : purchases) {
                System.out.println("Purchase: id=" + p.getId() + ", cashier_id=" + p.getCashierId() +
                        ", customer_name=\"" + p.getCustomerName() + "\", customer_email=\"" + p.getCustomerEmail() +
                        "\", customer_address=\"" + p.getCustomerAddress() + "\", customer_phone=\"" + p.getCustomerPhone() +
                        "\", total_price=" + p.getTotalPrice() + ", purchase_date=" +
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(p.getPurchaseDate()));
                for (PurchaseItem item : p.getItems()) {
                    System.out.println("  Item: book_id=" + item.getBookId() + ", quantity=" + item.getQuantity() + ", subtotal=" + item.getSubtotal());
                }
            }
        } else {
            System.out.println("No purchases found.");
        }
        promptBack();
    }

    public void updatePurchase() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter Purchase ID to Update: ");
            int id = Integer.parseInt(reader.readLine().trim());
            Purchase existingPurchase = purchaseDAO.getById(id);
            if (existingPurchase == null) {
                System.out.println("Purchase not found.");
                return;
            }

            List<PurchaseItem> items = new ArrayList<>();
            double totalPrice = 0.0;

            // Select new books and quantities
            while (true) {
                System.out.print("Enter Book ID (0 to finish): ");
                int bookId = Integer.parseInt(reader.readLine().trim());
                if (bookId == 0) break;
                Book book = bookDAO.getById(bookId);
                if (book == null) {
                    System.out.println("Book not found.");
                    continue;
                }
                System.out.print("Enter Quantity (≤ " + book.getQuantity() + "): ");
                int quantity = Integer.parseInt(reader.readLine().trim());
                if (quantity <= 0 || quantity > book.getQuantity()) {
                    System.out.println("Invalid quantity. Must be > 0 and ≤ " + book.getQuantity() + ".");
                    continue;
                }
                double subtotal = book.getPrice() * quantity;
                items.add(new PurchaseItem(0, id, bookId, quantity, subtotal));
                totalPrice += subtotal;
                System.out.println("Added: " + book.getName() + ", Quantity: " + quantity + ", Subtotal: " + subtotal);
            }

            // Enter updated details (keep existing if not provided)
            System.out.print("Enter Cashier ID (" + existingPurchase.getCashierId() + "): ");
            int cashierId = Integer.parseInt(reader.readLine().trim());
            if (cashierId == 0) cashierId = existingPurchase.getCashierId(); // Default to existing
            Cashier cashier = cashierDAO.getById(cashierId);
            if (cashier == null) {
                System.out.println("Cashier not found, using existing: " + existingPurchase.getCashierId());
                cashierId = existingPurchase.getCashierId();
            }

            System.out.print("Enter Customer Name (" + existingPurchase.getCustomerName() + "): ");
            String customerName = reader.readLine().trim();
            if (customerName.isEmpty()) customerName = existingPurchase.getCustomerName();

            System.out.print("Enter Customer Email (" + existingPurchase.getCustomerEmail() + "): ");
            String customerEmail = reader.readLine().trim();
            if (customerEmail.isEmpty()) customerEmail = existingPurchase.getCustomerEmail();

            System.out.print("Enter Customer Address (" + existingPurchase.getCustomerAddress() + "): ");
            String customerAddress = reader.readLine().trim();
            if (customerAddress.isEmpty()) customerAddress = existingPurchase.getCustomerAddress();

            System.out.print("Enter Customer Phone (" + existingPurchase.getCustomerPhone() + "): ");
            String customerPhone = reader.readLine().trim();
            if (customerPhone.isEmpty()) customerPhone = existingPurchase.getCustomerPhone();

            // Create and process updated purchase
            Purchase updatedPurchase = new Purchase(id, cashierId, customerName, customerEmail, customerAddress, customerPhone, totalPrice, new Date(), items);
            boolean success = purchaseDAO.updatePurchase(updatedPurchase);
            System.out.println("Update successful: " + success + " (Total: " + totalPrice + ")");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        promptBack();
    }

    public void deletePurchase() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter Purchase ID to Delete: ");
            int id = Integer.parseInt(reader.readLine().trim());
            boolean success = purchaseDAO.deletePurchase(id);
            System.out.println("Delete successful: " + success);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        promptBack();
    }
}