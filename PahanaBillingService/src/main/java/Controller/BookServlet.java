package Controller;

import DAO.BookDAO;
import DAOImpl.BookDAOImpl;
import Model.Book;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class BookServlet {
    private final BookDAO bookDAO = new BookDAOImpl();

    // Simulate adding a book with interactive input and validation
    public void addBook() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            Book book = new Book();

            System.out.print("Enter Image Path (e.g., C:\\Images\\book1.jpg): ");
            String imagePath = reader.readLine().trim();
            if (imagePath.isEmpty()) {
                System.out.println("Error: Image path is required.");
                return;
            }
            book.setImagePath(imagePath);

            System.out.print("Enter Name: ");
            String name = reader.readLine().trim();
            if (name.isEmpty()) {
                System.out.println("Error: Name is required.");
                return;
            }
            book.setName(name);

            System.out.print("Enter Author: ");
            String author = reader.readLine().trim();
            if (author.isEmpty()) {
                System.out.println("Error: Author is required.");
                return;
            }
            book.setAuthor(author);

            while (true) {
                System.out.print("Enter ISBN (13 digits): ");
                String isbn = reader.readLine().trim();
                if (isbn.length() != 13 || !isbn.matches("\\d{13}")) {
                    System.out.println("Error: ISBN must be exactly 13 digits.");
                    continue;
                }
                if (isIsbnDuplicate(isbn)) {
                    System.out.println("Error: ISBN already exists.");
                    continue;
                }
                book.setIsbn(isbn);
                break;
            }

            while (true) {
                System.out.print("Enter Price (> 0): ");
                try {
                    int price = Integer.parseInt(reader.readLine().trim());
                    if (price <= 0) {
                        System.out.println("Error: Price must be greater than 0.");
                        continue;
                    }
                    book.setPrice(price);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid number.");
                }
            }

            while (true) {
                System.out.print("Enter Quantity (>= 0): ");
                try {
                    int quantity = Integer.parseInt(reader.readLine().trim());
                    if (quantity < 0) {
                        System.out.println("Error: Quantity must be 0 or greater.");
                        continue;
                    }
                    book.setQuantity(quantity);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid number.");
                }
            }

            boolean success = bookDAO.add(book);
            System.out.println("Add successful: " + success);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        promptBack();
    }

    // Simulate updating a book with interactive input and validation
    public void updateBook() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter ID of Book to Update: ");
            int id = Integer.parseInt(reader.readLine().trim());
            Book existingBook = bookDAO.getById(id);
            if (existingBook == null) {
                System.out.println("Book not found.");
                promptBack();
                return;
            }

            Book book = new Book();
            book.setId(id);

            System.out.print("Enter Image Path (" + existingBook.getImagePath() + "): ");
            String imagePath = reader.readLine().trim();
            book.setImagePath(imagePath.isEmpty() ? existingBook.getImagePath() : imagePath);

            System.out.print("Enter Name (" + existingBook.getName() + "): ");
            String name = reader.readLine().trim();
            if (name.isEmpty()) {
                book.setName(existingBook.getName());
            } else {
                book.setName(name);
            }

            System.out.print("Enter Author (" + existingBook.getAuthor() + "): ");
            String author = reader.readLine().trim();
            if (author.isEmpty()) {
                book.setAuthor(existingBook.getAuthor());
            } else {
                book.setAuthor(author);
            }

            while (true) {
                System.out.print("Enter ISBN (" + existingBook.getIsbn() + "): ");
                String isbn = reader.readLine().trim();
                if (!isbn.isEmpty() && (isbn.length() != 13 || !isbn.matches("\\d{13}"))) {
                    System.out.println("Error: ISBN must be exactly 13 digits.");
                    continue;
                }
                if (!isbn.isEmpty() && isIsbnDuplicate(isbn, id)) {
                    System.out.println("Error: ISBN already exists.");
                    continue;
                }
                book.setIsbn(isbn.isEmpty() ? existingBook.getIsbn() : isbn);
                break;
            }

            while (true) {
                System.out.print("Enter Price (" + existingBook.getPrice() + "): ");
                try {
                    String priceInput = reader.readLine().trim();
                    if (priceInput.isEmpty()) {
                        book.setPrice(existingBook.getPrice());
                        break;
                    }
                    int price = Integer.parseInt(priceInput);
                    if (price <= 0) {
                        System.out.println("Error: Price must be greater than 0.");
                        continue;
                    }
                    book.setPrice(price);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid number.");
                    book.setPrice(existingBook.getPrice()); // Default to existing if invalid
                    break;
                }
            }

            while (true) {
                System.out.print("Enter Quantity (" + existingBook.getQuantity() + "): ");
                try {
                    String quantityInput = reader.readLine().trim();
                    if (quantityInput.isEmpty()) {
                        book.setQuantity(existingBook.getQuantity());
                        break;
                    }
                    int quantity = Integer.parseInt(quantityInput);
                    if (quantity < 0) {
                        System.out.println("Error: Quantity must be 0 or greater.");
                        continue;
                    }
                    book.setQuantity(quantity);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid number.");
                    book.setQuantity(existingBook.getQuantity()); // Default to existing if invalid
                    break;
                }
            }

            boolean success = bookDAO.update(book);
            System.out.println("Update successful: " + success);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        promptBack();
    }

    // Simulate deleting a book
    public void deleteBook() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter ID of Book to Delete: ");
            int id = Integer.parseInt(reader.readLine().trim());
            Book book = bookDAO.getById(id);
            if (book == null) {
                System.out.println("Book not found.");
            } else {
                boolean success = bookDAO.delete(id);
                System.out.println("Delete successful: " + success + " (Record removed from database)");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        promptBack();
    }

    // Simulate getting a book by ID
    public void getBookById() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter ID: ");
            int id = Integer.parseInt(reader.readLine().trim());
            Book book = bookDAO.getById(id);
            if (book != null) {
                System.out.println("Book Details: id=" + book.getId() + ", image_path=\"" + escapeJson(book.getImagePath()) + "\", name=\"" + escapeJson(book.getName()) + "\", author=\"" + escapeJson(book.getAuthor()) + "\", isbn=\"" + escapeJson(book.getIsbn()) + "\", price=" + book.getPrice() + ", quantity=" + book.getQuantity());
            } else {
                System.out.println("Book not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        promptBack();
    }

    // Simulate getting all books
    public void getAllBooks() {
        List<Book> books = bookDAO.getAll();
        if (books != null && !books.isEmpty()) {
            System.out.println("All Books:");
            for (Book b : books) {
                System.out.println("id=" + b.getId() + ", image_path=\"" + escapeJson(b.getImagePath()) + "\", name=\"" + escapeJson(b.getName()) + "\", author=\"" + escapeJson(b.getAuthor()) + "\", isbn=\"" + escapeJson(b.getIsbn()) + "\", price=" + b.getPrice() + ", quantity=" + b.getQuantity());
            }
        } else {
            System.out.println("No books found.");
        }
        promptBack();
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\"", "\\\"").replace("\n", "\\n");
    }

    private boolean isIsbnDuplicate(String isbn) {
        List<Book> books = bookDAO.getAll();
        for (Book b : books) {
            if (b.getIsbn() != null && b.getIsbn().equals(isbn)) {
                return true;
            }
        }
        return false;
    }

    private boolean isIsbnDuplicate(String isbn, int excludeId) {
        List<Book> books = bookDAO.getAll();
        for (Book b : books) {
            if (b.getId() != excludeId && b.getIsbn() != null && b.getIsbn().equals(isbn)) {
                return true;
            }
        }
        return false;
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
        BookServlet servlet = new BookServlet();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Book");
            System.out.println("2. Get Book By ID");
            System.out.println("3. Get All Books");
            System.out.println("4. Update Book");
            System.out.println("5. Delete Book");
            System.out.println("6. Exit");
            System.out.print("Enter choice (1-6): ");

            try {
                int choice = Integer.parseInt(reader.readLine().trim());
                switch (choice) {
                    case 1:
                        servlet.addBook();
                        break;
                    case 2:
                        servlet.getBookById();
                        break;
                    case 3:
                        servlet.getAllBooks();
                        break;
                    case 4:
                        servlet.updateBook();
                        break;
                    case 5:
                        servlet.deleteBook();
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
}