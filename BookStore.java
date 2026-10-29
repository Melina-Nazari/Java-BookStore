import java.util.ArrayList;

public class BookStore
{
    private ArrayList<Book> books;
    private ArrayList<Customer> customers;

    public BookStore() {
        books = new ArrayList<>();
        customers = new ArrayList<>();
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public void addBook(String name, double price) {
        if (findBook(name) == null) {
            books.add(new Book(name, price));
        }
    }

    public void deleteBook(int index) 
    {
        if (index >= 0 && index < books.size())//If index is invalid: nothing happens, program does NOT crash
        {
            books.remove(index);
        }
    }

    public void addCustomer(String username, String password) {
        if (findCustomer(username) == null) {
            customers.add(new Customer(username, password, 0));
        }
    }

    public void deleteCustomer(int index) {
        if (index >= 0 && index < customers.size()) {
            customers.remove(index);
        }
    }

    public Customer loginCustomer(String username, String password) {
        for (Customer c : customers)//Go through each customer in the list
        {
            if (c.getUsername().equals(username) && c.getPassword().equals(password)) {
                return c;
            }
        }
        return null;
    }

    public Book findBook(String name) {
        for (Book b : books) {
            if (b.getName().equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;
    }

    public Customer findCustomer(String username) {
        for (Customer c : customers)
        {
            if (c.getUsername().equalsIgnoreCase(username)) {
                return c;
            }
        }
        return null;
    }
}