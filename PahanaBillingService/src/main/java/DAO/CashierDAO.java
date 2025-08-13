package DAO;

import Model.Cashier;
import java.util.List;

public interface CashierDAO {
    List<Cashier> getAll();
    Cashier getById(int id);
    boolean add(Cashier cashier);
}