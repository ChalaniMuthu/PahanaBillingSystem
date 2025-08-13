package DAO;

import Model.Purchase;
import java.util.List;

public interface PurchaseDAO {
    boolean addPurchase(Purchase purchase);
    Purchase getById(int id);
    List<Purchase> getAll();
    boolean updatePurchase(Purchase purchase);
    boolean deletePurchase(int id);
}