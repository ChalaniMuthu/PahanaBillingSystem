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
                if (isEmailDuplicate(email, 0)) { // 0 indicates new cashier (no ID yet)
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
                if (isPhoneDuplicate(phone, 0)) { // 0 indicates new cashier
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
        promptBack();
    }

    // Simulate editing a cashier with interactive input and validation
    public void editCashier() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter ID of Cashier to Edit: ");
            int id = Integer.parseInt(reader.readLine().trim());
            Cashier existingCashier = cashierDAO.getById(id);
            if (existingCashier == null) {
                System.out.println("Cashier not found.");
                promptBack();
                return;
            }

            Cashier cashier = new Cashier();
            cashier.setId(id);

            System.out.print("Enter Full Name (" + existingCashier.getFullName() + "): ");
            String fullName = reader.readLine().trim();
            cashier.setFullName(fullName.isEmpty() ? existingCashier.getFullName() : fullName);

            while (true) {
                System.out.print("Enter Email (" + existingCashier.getEmail() + "): ");
                String email = reader.readLine().trim();
                email = email.isEmpty() ? existingCashier.getEmail() : email;
                if (!email.contains("@")) {
                    System.out.println("Error: Email must contain '@'.");
                    continue;
                }
                if (isEmailDuplicate(email, id)) { // Exclude current cashier's ID
                    System.out.println("Error: Email already exists.");
                    continue;
                }
                cashier.setEmail(email);
                break;
            }

            while (true) {
                System.out.print("Enter Phone Number (" + existingCashier.getPhoneNumber() + "): ");
                String phone = reader.readLine().trim();
                phone = phone.isEmpty() ? existingCashier.getPhoneNumber() : phone;
                if (!phone.matches("^0\\d{9}$") || phone.length() != 10) {
                    System.out.println("Error: Phone must be 10 digits and start with 0.");
                    continue;
                }
                if (isPhoneDuplicate(phone, id)) { // Exclude current cashier's ID
                    System.out.println("Error: Phone number already exists.");
                    continue;
                }
                cashier.setPhoneNumber(phone);
                break;
            }

            System.out.print("Enter Address (" + existingCashier.getAddress() + "): ");
            String address = reader.readLine().trim();
            cashier.setAddress(address.isEmpty() ? existingCashier.getAddress() : address);

            System.out.print("Enter Gender (" + existingCashier.getGender() + "): ");
            String gender = reader.readLine().trim();
            if (gender.isEmpty()) {
                cashier.setGender(existingCashier.getGender());
            } else if (gender.equals("Male") || gender.equals("Female") || gender.equals("Other")) {
                cashier.setGender(gender);
            } else {
                System.out.println("Invalid gender. Keeping previous value: " + existingCashier.getGender());
                cashier.setGender(existingCashier.getGender());
            }

            System.out.print("Enter Password (" + existingCashier.getPassword() + "): ");
            String password = reader.readLine().trim();
            cashier.setPassword(password.isEmpty() ? existingCashier.getPassword() : password);

            System.out.print("Enter Status (" + existingCashier.isStatus() + "): ");
            String status = reader.readLine().trim();
            cashier.setStatus(status.isEmpty() ? existingCashier.isStatus() : Boolean.parseBoolean(status));

            boolean success = cashierDAO.update(cashier);
            System.out.println("Update successful: " + success);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        promptBack();
    }

    // Simulate getting a cashier by ID (updated to show all statuses)
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
        promptBack();
    }

    // Simulate getting all cashiers (updated to show all statuses)
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
        promptBack();
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\"", "\\\"").replace("\n", "\\n");
    }

    private boolean isEmailDuplicate(String email, int excludeId) {
        List<Cashier> cashiers = cashierDAO.getAll();
        for (Cashier c : cashiers) {
            if (c.getId() != excludeId && c.getEmail() != null && c.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPhoneDuplicate(String phone, int excludeId) {
        List<Cashier> cashiers = cashierDAO.getAll();
        for (Cashier c : cashiers) {
            if (c.getId() != excludeId && c.getPhoneNumber() != null && c.getPhoneNumber().equals(phone)) {
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
        CashierServlet servlet = new CashierServlet();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Cashier");
            System.out.println("2. Edit Cashier");
            System.out.println("3. Get Cashier By ID");
            System.out.println("4. Get All Cashiers");
            System.out.println("5. Exit");
            System.out.print("Enter choice (1-5): ");

            try {
                int choice = Integer.parseInt(reader.readLine().trim());
                switch (choice) {
                    case 1:
                        servlet.addCashier();
                        break;
                    case 2:
                        servlet.editCashier();
                        break;
                    case 3:
                        servlet.getCashierById();
                        break;
                    case 4:
                        servlet.getAllCashiers();
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter 1-5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number (1-5).");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}