import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BookStoreApp extends JFrame {

    private final BookStore store = new BookStore();
    private final CardLayout cardLayout = new CardLayout();//flipping between screens
    private final JPanel mainPanel = new JPanel(cardLayout);

    private Customer currentCustomer;

    private final LoginPanel loginPanel = new LoginPanel();
    private final OwnerStartPanel ownerStartPanel = new OwnerStartPanel();
    private final OwnerBooksPanel ownerBooksPanel = new OwnerBooksPanel();
    private final OwnerCustomersPanel ownerCustomersPanel = new OwnerCustomersPanel();
    private final CustomerStartPanel customerStartPanel = new CustomerStartPanel();
    private final CustomerCostPanel customerCostPanel = new CustomerCostPanel();

    public BookStoreApp() {
        super("Bookstore App");

        DataStore.loadBooks(store, "books.txt");
        DataStore.loadCustomers(store, "customers.txt");

        mainPanel.add(loginPanel, "login");
        mainPanel.add(ownerStartPanel, "ownerStart");
        mainPanel.add(ownerBooksPanel, "ownerBooks");
        mainPanel.add(ownerCustomersPanel, "ownerCustomers");
        mainPanel.add(customerStartPanel, "customerStart");
        mainPanel.add(customerCostPanel, "customerCost");

        add(mainPanel);
        cardLayout.show(mainPanel, "login");

        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DataStore.saveBooks(store.getBooks(), "books.txt");
                DataStore.saveCustomers(store.getCustomers(), "customers.txt");
                dispose();
                System.exit(0);
            }
        });
    }

    private void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }

    // ---------------- LOGIN ----------------
    class LoginPanel extends JPanel {
        public LoginPanel() {
            setLayout(new GridBagLayout());
            JTextField username = new JTextField(15);
            JPasswordField password = new JPasswordField(15);
            JButton login = new JButton("Login");

            add(new JLabel("Username")); add(username);
            add(new JLabel("Password")); add(password);
            add(login);

            login.addActionListener(e -> {
                String u = username.getText();
                String p = new String(password.getPassword());

                if (u.equals("admin") && p.equals("admin")) {
                    ownerBooksPanel.refreshTable();
                    ownerCustomersPanel.refreshTable();
                    showPanel("ownerStart");
                } else {
                    Customer c = store.loginCustomer(u, p);
                    if (c != null) {
                        currentCustomer = c;
                        customerStartPanel.refreshForCustomer();
                        showPanel("customerStart");
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid login");
                    }
                }
            });
        }
    }

    // ---------------- OWNER START ----------------
    class OwnerStartPanel extends JPanel {
        public OwnerStartPanel() {
            JButton books = new JButton("Books");
            JButton customers = new JButton("Customers");
            JButton logout = new JButton("Logout");

            add(books);
            add(customers);
            add(logout);

            books.addActionListener(e -> //When user clicks login → run this code
            {
                ownerBooksPanel.refreshTable();
                showPanel("ownerBooks");
            });

            customers.addActionListener(e -> {
                ownerCustomersPanel.refreshTable();
                showPanel("ownerCustomers");
            });

            logout.addActionListener(e -> showPanel("login"));
        }
    }

    // ---------------- BOOKS ----------------
    class OwnerBooksPanel extends JPanel {
        private DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Book Name", "Book Price"}, 0);
        private JTable table = new JTable(model);
        private JTextField name = new JTextField(10);
        private JTextField price = new JTextField(10);

        public OwnerBooksPanel() {
            setLayout(new BorderLayout());

            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel top = new JPanel();
            JButton add = new JButton("Add");
            top.add(new JLabel("Name"));
            top.add(name);
            top.add(new JLabel("Price"));
            top.add(price);
            top.add(add);
            add(top, BorderLayout.NORTH);

            JPanel bottom = new JPanel();
            JButton delete = new JButton("Delete");
            JButton back = new JButton("Back");
            bottom.add(delete);
            bottom.add(back);
            add(bottom, BorderLayout.SOUTH);

            add.addActionListener(e -> {
                try {
                    store.addBook(name.getText(), Double.parseDouble(price.getText()));
                    refreshTable();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input");
                }
            });

            delete.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) store.deleteBook(row);
                refreshTable();
            });

            back.addActionListener(e -> showPanel("ownerStart"));
        }

        public void refreshTable() {
            model.setRowCount(0);
            for (Book b : store.getBooks())
                model.addRow(new Object[]{b.getName(), b.getPrice()});
        }
    }

    // ---------------- CUSTOMERS ----------------
    class OwnerCustomersPanel extends JPanel {
        private DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Username", "Password", "Points"}, 0);
        private JTable table = new JTable(model);
        private JTextField user = new JTextField(10);
        private JTextField pass = new JTextField(10);

        public OwnerCustomersPanel() {
            setLayout(new BorderLayout());

            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel top = new JPanel();
            JButton add = new JButton("Add");
            top.add(new JLabel("Username"));
            top.add(user);
            top.add(new JLabel("Password"));
            top.add(pass);
            top.add(add);
            add(top, BorderLayout.NORTH);

            JPanel bottom = new JPanel();
            JButton delete = new JButton("Delete");
            JButton back = new JButton("Back");
            bottom.add(delete);
            bottom.add(back);
            add(bottom, BorderLayout.SOUTH);

            add.addActionListener(e -> {
                store.addCustomer(user.getText(), pass.getText());
                refreshTable();
            });

            delete.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) store.deleteCustomer(row);
                refreshTable();
            });

            back.addActionListener(e -> showPanel("ownerStart"));
        }

        public void refreshTable() {
            model.setRowCount(0);
            for (Customer c : store.getCustomers())
                model.addRow(new Object[]{c.getUsername(), c.getPassword(), c.getPoints()});
        }
    }

    // ---------------- CUSTOMER VIEW ----------------
    class CustomerStartPanel extends JPanel {

    private DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Book Name", "Book Price", "Select"}, 0) {

        @Override
        public Class<?> getColumnClass(int col) {
            return col == 2 ? Boolean.class : Object.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 2;
        }
    };

    private JTable table = new JTable(model);
    private JLabel label = new JLabel();

    public CustomerStartPanel() {
        setLayout(new BorderLayout());

        add(label, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton buy = new JButton("Buy");
        JButton redeem = new JButton("Redeem points and Buy");
        JButton logout = new JButton("Logout");

        bottom.add(buy);
        bottom.add(redeem);
        bottom.add(logout);
        add(bottom, BorderLayout.SOUTH);

        buy.addActionListener(e -> process(false));
        redeem.addActionListener(e -> process(true));
        logout.addActionListener(e -> showPanel("login"));
    }

    public void refreshForCustomer() {
        label.setText("Welcome " + currentCustomer.getUsername()
                + " | Points: " + currentCustomer.getPoints()
                + " | Status: " + currentCustomer.getStatus());

        model.setRowCount(0);

        for (Book b : store.getBooks()) {
            model.addRow(new Object[]{b.getName(), b.getPrice(), false});
        }
    }

    private void process(boolean redeem) {
        double total = 0;

        // Calculate selected books
        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean selected = (Boolean) model.getValueAt(i, 2);
            if (selected != null && selected) {
                total += (Double) model.getValueAt(i, 1);
            }
        }

        // ❗ MUST SELECT BOOK
        if (total == 0) {
            JOptionPane.showMessageDialog(null, "Please select at least one book");
            return;
        }

        double finalCost = total;

        // REDEEM LOGIC (CORRECT)
        if (redeem) {
            double discount = currentCustomer.getPoints() / 100.0;
            finalCost = total - discount;

            if (finalCost < 0) finalCost = 0;

            int pointsUsed = (int)(discount * 100);
            currentCustomer.redeemPoints(pointsUsed);
        }

        // EARN POINTS (CORRECT)
        int earnedPoints = (int)(finalCost * 10);
        currentCustomer.addPoints(earnedPoints);

        // UPDATE STATUS handled in Customer class automatically

        customerCostPanel.setInfo(finalCost, currentCustomer);

        // Refresh UI
        refreshForCustomer();

        showPanel("customerCost");
    }
}
    // ---------------- COST SCREEN ----------------
    class CustomerCostPanel extends JPanel {
        JLabel cost = new JLabel();
        JLabel info = new JLabel();

        public CustomerCostPanel() {
            add(cost);
            add(info);
            JButton logout = new JButton("Logout");
            add(logout);

            logout.addActionListener(e -> showPanel("login"));
        }

        public void setInfo(double c, Customer cust) {
            cost.setText("Total: " + c);
            info.setText("Points: " + cust.getPoints() + " Status: " + cust.getStatus());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookStoreApp().setVisible(true));
    }
}