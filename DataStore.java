import java.io.*;
import java.util.ArrayList;

public class DataStore 
{
    public static void loadBooks(BookStore store, String fileName) {
        File file = new File(fileName);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file)))//Opens file, Reads it line by line, 
            //Automatically closes file when done
        {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    String name = parts[0];
                    double price = Double.parseDouble(parts[1]);
                    store.addBook(name, price);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCustomers(BookStore store, String fileName) {
        File file = new File(fileName);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    int points = Integer.parseInt(parts[2]);
                    store.getCustomers().add(new Customer(username, password, points));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveBooks(ArrayList<Book> books, String fileName) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (Book b : books) {
                pw.println(b.getName() + ";" + b.getPrice());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveCustomers(ArrayList<Customer> customers, String fileName) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (Customer c : customers) {
                pw.println(c.getUsername() + ";" + c.getPassword() + ";" + c.getPoints());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}