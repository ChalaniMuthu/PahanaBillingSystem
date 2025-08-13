package DAOImpl;

import DAO.CashierDAO;
import Model.Cashier;
import Util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CashierDAOImpl implements CashierDAO {

    @Override
    public List<Cashier> getAll() {
        List<Cashier> cashiers = new ArrayList<>();
        String query = "SELECT * FROM cashier WHERE status = 1";
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Cashier c = new Cashier(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("gender"),
                        rs.getString("password"),
                        rs.getBoolean("status")
                );
                cashiers.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cashiers;
    }

    @Override
    public Cashier getById(int id) {
        Cashier cashier = null;
        String query = "SELECT * FROM cashier WHERE id = ? AND status = 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cashier = new Cashier(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getString("address"),
                            rs.getString("gender"),
                            rs.getString("password"),
                            rs.getBoolean("status")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cashier;
    }

    @Override
    public boolean add(Cashier cashier) {
        String query = "INSERT INTO cashier (full_name, email, phone_number, address, gender, password, status) VALUES (?,?,?,?,?,?,1)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, cashier.getFullName());
            ps.setString(2, cashier.getEmail());
            ps.setString(3, cashier.getPhoneNumber());
            ps.setString(4, cashier.getAddress());
            ps.setString(5, cashier.getGender());
            ps.setString(6, cashier.getPassword());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}