package Controller;

import DAO.CashierDAO;
import DAOImpl.CashierDAOImpl;
import Model.Cashier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class CashierServlet {
    private final CashierDAO cashierDAO = new CashierDAOImpl();

    // Simulate adding a cashier with interactive input and validation
    public void addCashier() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            Cashier cashier = new Cashier();

            System.out.print("Enter Full Name: ");
            cashier.setFullName(reader.readLine());

            while (true) {
                System.out.print("Enter Email: ");
                String email = reader.readLine();
                if (!email.contains("@")) {
                    System.out.println("Error: Email must contain '@'.");
                    continue;
                }
                if (isEmailDuplicate(email)) {
                    System.out.println("Error: Email already exists.");
                    continue;
                }
                cashier.setEmail(email);
                break;
            }

            while (true) {
                System.out.print("Enter Phone Number: ");
                String phone = reader.readLine();
                if (!phone.matches("^0\\d{9}$") || phone.length() != 10) {
                    System.out.println("Error: Phone must be 10 digits and start with 0.");
                    continue;
                }
                if (isPhoneDuplicate(phone)) {
                    System.out.println("Error: Phone number already exists.");
                    continue;
                }
                cashier.setPhoneNumber(phone);
                break;
            }

            System.out.print("Enter Address: ");
            cashier.setAddress(reader.readLine());

            System.out.print("Enter Gender (Male/Female/Other): ");
            String gender = reader.readLine();
            if (gender.equals("Male") || gender.equals("Female") || gender.equals("Other")) {
                cashier.setGender(gender);
            } else {
                System.out.println("Invalid gender. Setting to 'Other'.");
                cashier.setGender("Other");
            }

            System.out.print("Enter Password: ");
            cashier.setPassword(reader.readLine());

            System.out.print("Enter Status (true/false): ");
            cashier.setStatus(Boolean.parseBoolean(reader.readLine().trim()));

            boolean success = cashierDAO.add(cashier);
            System.out.println("Add successful: " + success);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Simulate getting a cashier by ID
    public void getCashierById() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter ID: ");
            int id = Integer.parseInt(reader.readLine().trim());
            Cashier cashier = cashierDAO.getById(id);
            if (cashier != null) {
                System.out.println("Cashier Details: id=" + cashier.getId() + ", full_name=\"" + escapeJson(cashier.getFullName()) + "\", email=\"" + escapeJson(cashier.getEmail()) + "\", phone_number=\"" + escapeJson(cashier.getPhoneNumber()) + "\", address=\"" + escapeJson(cashier.getAddress()) + "\", gender=\"" + escapeJson(cashier.getGender()) + "\", password=\"" + escapeJson(cashier.getPassword()) + "\", status=" + cashier.isStatus());
            } else {
                System.out.println("Cashier not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Simulate getting all cashiers
    public void getAllCashiers() {
        List<Cashier> cashiers = cashierDAO.getAll();
        if (cashiers != null && !cashiers.isEmpty()) {
            System.out.println("All Cashiers:");
            for (Cashier c : cashiers) {
                System.out.println("id=" + c.getId() + ", full_name=\"" + escapeJson(c.getFullName()) + "\", email=\"" + escapeJson(c.getEmail()) + "\", phone_number=\"" + escapeJson(c.getPhoneNumber()) + "\", address=\"" + escapeJson(c.getAddress()) + "\", gender=\"" + escapeJson(c.getGender()) + "\", password=\"" + escapeJson(c.getPassword()) + "\", status=" + c.isStatus());
            }
        } else {
            System.out.println("No cashiers found.");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\"", "\\\"").replace("\n", "\\n");
    }

    private boolean isEmailDuplicate(String email) {
        List<Cashier> cashiers = cashierDAO.getAll();
        for (Cashier c : cashiers) {
            if (c.getEmail() != null && c.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPhoneDuplicate(String phone) {
        List<Cashier> cashiers = cashierDAO.getAll();
        for (Cashier c : cashiers) {
            if (c.getPhoneNumber() != null && c.getPhoneNumber().equals(phone)) {
                return true;
            }
        }
        return false;
    }

    // Main method with menu-driven interface
    public static void main(String[] args) {
        CashierServlet servlet = new CashierServlet();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Cashier");
            System.out.println("2. Get Cashier By ID");
            System.out.println("3. Get All Cashiers");
            System.out.println("4. Exit");
            System.out.print("Enter choice (1-4): ");

            try {
                int choice = Integer.parseInt(reader.readLine().trim());
                switch (choice) {
                    case 1:
                        servlet.addCashier();
                        break;
                    case 2:
                        servlet.getCashierById();
                        break;
                    case 3:
                        servlet.getAllCashiers();
                        break;
                    case 4:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter 1-4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number (1-4).");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}