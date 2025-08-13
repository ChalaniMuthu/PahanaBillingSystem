package DAO;

import Model.Book;
import java.util.List;

public interface BookDAO {
    List<Book> getAll();
    Book getById(int id);
    boolean add(Book book);
}