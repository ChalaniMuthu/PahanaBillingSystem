package DAOImpl;

import DAO.BookDAO;
import DAO.PurchaseDAO;
import Model.Book;
import Model.Cashier;
import Model.Purchase;
import Model.PurchaseItem;
import Util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseDAOImpl implements PurchaseDAO {
    private final BookDAO bookDAO = new BookDAOImpl(); // Assuming BookDAOImpl exists

    @Override
    public boolean addPurchase(Purchase purchase) {
        String purchaseQuery = "INSERT INTO purchases (cashier_id, customer_name, customer_email, customer_address, customer_phone, total_price, purchase_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String itemQuery = "INSERT INTO purchase_items (purchase_id, book_id, quantity, subtotal) VALUES (?, ?, ?, ?)";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Start transaction

            // Validate cashier (placeholder, integrate with CashierDAO if available)
            // Cashier cashier = cashierDAO.getById(purchase.getCashierId());
            // if (cashier == null) {
            //     con.rollback();
            //     return false;
            // }

            // Insert into purchases
            try (PreparedStatement psPurchase = con.prepareStatement(purchaseQuery, Statement.RETURN_GENERATED_KEYS)) {
                psPurchase.setInt(1, purchase.getCashierId());
                psPurchase.setString(2, purchase.getCustomerName());
                psPurchase.setString(3, purchase.getCustomerEmail());
                psPurchase.setString(4, purchase.getCustomerAddress());
                psPurchase.setString(5, purchase.getCustomerPhone());
                psPurchase.setDouble(6, purchase.getTotalPrice());
                psPurchase.setTimestamp(7, new Timestamp(purchase.getPurchaseDate().getTime()));
                psPurchase.executeUpdate();

                // Get the generated purchase ID
                int purchaseId;
                try (ResultSet rs = psPurchase.getGeneratedKeys()) {
                    if (rs.next()) {
                        purchaseId = rs.getInt(1);
                    } else {
                        con.rollback();
                        return false;
                    }
                }

                // Insert into purchase_items and update book quantities
                try (PreparedStatement psItem = con.prepareStatement(itemQuery)) {
                    for (PurchaseItem item : purchase.getItems()) {
                        Book book = bookDAO.getById(item.getBookId());
                        if (book == null || book.getQuantity() < item.getQuantity()) {
                            con.rollback();
                            return false;
                        }
                        item.setPurchaseId(purchaseId);
                        item.setSubtotal(book.getPrice() * item.getQuantity());
                        psItem.setInt(1, purchaseId);
                        psItem.setInt(2, item.getBookId());
                        psItem.setInt(3, item.getQuantity());
                        psItem.setDouble(4, item.getSubtotal());
                        psItem.addBatch();

                        // Update book quantity
                        String updateBookQuery = "UPDATE book SET quantity = quantity - ? WHERE id = ?";
                        try (PreparedStatement psUpdateBook = con.prepareStatement(updateBookQuery)) {
                            psUpdateBook.setInt(1, item.getQuantity());
                            psUpdateBook.setInt(2, item.getBookId());
                            psUpdateBook.executeUpdate();
                        }
                    }
                    psItem.executeBatch();
                }

                con.commit();
                return true;
            }
        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Purchase getById(int id) {
        Purchase purchase = null;
        String query = "SELECT * FROM purchases WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    purchase = new Purchase(
                            rs.getInt("id"),
                            rs.getInt("cashier_id"),
                            rs.getString("customer_name"),
                            rs.getString("customer_email"),
                            rs.getString("customer_address"),
                            rs.getString("customer_phone"),
                            rs.getDouble("total_price"),
                            rs.getTimestamp("purchase_date"),
                            getPurchaseItems(id)
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return purchase;
    }

    @Override
    public List<Purchase> getAll() {
        List<Purchase> purchases = new ArrayList<>();
        String query = "SELECT * FROM purchases";
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Purchase purchase = new Purchase(
                        rs.getInt("id"),
                        rs.getInt("cashier_id"),
                        rs.getString("customer_name"),
                        rs.getString("customer_email"),
                        rs.getString("customer_address"),
                        rs.getString("customer_phone"),
                        rs.getDouble("total_price"),
                        rs.getTimestamp("purchase_date"),
                        getPurchaseItems(rs.getInt("id"))
                );
                purchases.add(purchase);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return purchases;
    }

    @Override
    public boolean updatePurchase(Purchase purchase) {
        String purchaseQuery = "UPDATE purchases SET cashier_id = ?, customer_name = ?, customer_email = ?, customer_address = ?, customer_phone = ?, total_price = ?, purchase_date = ? WHERE id = ?";
        String deleteItemsQuery = "DELETE FROM purchase_items WHERE purchase_id = ?";
        String itemQuery = "INSERT INTO purchase_items (purchase_id, book_id, quantity, subtotal) VALUES (?, ?, ?, ?)";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Start transaction

            // Update purchases
            try (PreparedStatement psPurchase = con.prepareStatement(purchaseQuery)) {
                psPurchase.setInt(1, purchase.getCashierId());
                psPurchase.setString(2, purchase.getCustomerName());
                psPurchase.setString(3, purchase.getCustomerEmail());
                psPurchase.setString(4, purchase.getCustomerAddress());
                psPurchase.setString(5, purchase.getCustomerPhone());
                psPurchase.setDouble(6, purchase.getTotalPrice());
                psPurchase.setTimestamp(7, new Timestamp(purchase.getPurchaseDate().getTime()));
                psPurchase.setInt(8, purchase.getId());
                if (psPurchase.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }

                // Delete existing items
                try (PreparedStatement psDeleteItems = con.prepareStatement(deleteItemsQuery)) {
                    psDeleteItems.setInt(1, purchase.getId());
                    psDeleteItems.executeUpdate();

                    // Revert previous book quantity updates
                    List<PurchaseItem> oldItems = getPurchaseItems(purchase.getId());
                    for (PurchaseItem item : oldItems) {
                        String revertBookQuery = "UPDATE book SET quantity = quantity + ? WHERE id = ?";
                        try (PreparedStatement psRevertBook = con.prepareStatement(revertBookQuery)) {
                            psRevertBook.setInt(1, item.getQuantity());
                            psRevertBook.setInt(2, item.getBookId());
                            psRevertBook.executeUpdate();
                        }
                    }

                    // Insert new items and update book quantities
                    try (PreparedStatement psItem = con.prepareStatement(itemQuery)) {
                        for (PurchaseItem item : purchase.getItems()) {
                            Book book = bookDAO.getById(item.getBookId());
                            if (book == null || book.getQuantity() < item.getQuantity()) {
                                con.rollback();
                                return false;
                            }
                            item.setPurchaseId(purchase.getId());
                            item.setSubtotal(book.getPrice() * item.getQuantity());
                            psItem.setInt(1, purchase.getId());
                            psItem.setInt(2, item.getBookId());
                            psItem.setInt(3, item.getQuantity());
                            psItem.setDouble(4, item.getSubtotal());
                            psItem.addBatch();

                            // Update book quantity
                            String updateBookQuery = "UPDATE book SET quantity = quantity - ? WHERE id = ?";
                            try (PreparedStatement psUpdateBook = con.prepareStatement(updateBookQuery)) {
                                psUpdateBook.setInt(1, item.getQuantity());
                                psUpdateBook.setInt(2, item.getBookId());
                                psUpdateBook.executeUpdate();
                            }
                        }
                        psItem.executeBatch();
                    }
                }

                con.commit();
                return true;
            }
        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean deletePurchase(int id) {
        String deleteItemsQuery = "DELETE FROM purchase_items WHERE purchase_id = ?";
        String deletePurchaseQuery = "DELETE FROM purchases WHERE id = ?";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Start transaction

            // Get existing items to revert book quantities
            List<PurchaseItem> items = getPurchaseItems(id);
            if (items.isEmpty()) {
                con.rollback();
                return false;
            }

            // Revert book quantities
            for (PurchaseItem item : items) {
                String revertBookQuery = "UPDATE book SET quantity = quantity + ? WHERE id = ?";
                try (PreparedStatement psRevertBook = con.prepareStatement(revertBookQuery)) {
                    psRevertBook.setInt(1, item.getQuantity());
                    psRevertBook.setInt(2, item.getBookId());
                    psRevertBook.executeUpdate();
                }
            }

            // Delete items and purchase
            try (PreparedStatement psDeleteItems = con.prepareStatement(deleteItemsQuery)) {
                psDeleteItems.setInt(1, id);
                psDeleteItems.executeUpdate();

                try (PreparedStatement psDeletePurchase = con.prepareStatement(deletePurchaseQuery)) {
                    psDeletePurchase.setInt(1, id);
                    if (psDeletePurchase.executeUpdate() == 0) {
                        con.rollback();
                        return false;
                    }
                }
            }

            con.commit();
            return true;
        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<PurchaseItem> getPurchaseItems(int purchaseId) {
        List<PurchaseItem> items = new ArrayList<>();
        String query = "SELECT * FROM purchase_items WHERE purchase_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, purchaseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new PurchaseItem(
                            rs.getInt("id"),
                            rs.getInt("purchase_id"),
                            rs.getInt("book_id"),
                            rs.getInt("quantity"),
                            rs.getDouble("subtotal")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}