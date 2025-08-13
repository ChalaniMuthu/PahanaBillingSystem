package DAOImpl;

import DAO.BookDAO;
import Model.Book;
import Util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAOImpl implements BookDAO {

    @Override
    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM book";
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Book b = new Book(
                        rs.getInt("id"),
                        rs.getString("image_path"),
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getInt("price"),
                        rs.getInt("quantity")
                );
                books.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public Book getById(int id) {
        Book book = null;
        String query = "SELECT * FROM book WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    book = new Book(
                            rs.getInt("id"),
                            rs.getString("image_path"),
                            rs.getString("name"),
                            rs.getString("author"),
                            rs.getString("isbn"),
                            rs.getInt("price"),
                            rs.getInt("quantity")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return book;
    }

    @Override
    public boolean add(Book book) {
        String query = "INSERT INTO book (image_path, name, author, isbn, price, quantity) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, book.getImagePath());
            ps.setString(2, book.getName());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getIsbn());
            ps.setInt(5, book.getPrice());
            ps.setInt(6, book.getQuantity());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Book book) {
        String query = "UPDATE book SET image_path = ?, name = ?, author = ?, isbn = ?, price = ?, quantity = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, book.getImagePath());
            ps.setString(2, book.getName());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getIsbn());
            ps.setInt(5, book.getPrice());
            ps.setInt(6, book.getQuantity());
            ps.setInt(7, book.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM book WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows deleted: " + rowsAffected); // Debug output
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}