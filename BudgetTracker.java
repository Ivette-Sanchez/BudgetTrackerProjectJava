import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI; // for customizing the buttons
import java.awt.*; // for layouts/colors/fonts
import java.io.*; // files save/load
import java.net.*; //for the api
import java.util.Scanner; 
import org.json.*; // needed for the API
import java.time.LocalDate; // for working with current date

// this is the main class that extends JFrame--> creating the GUI app
public class BudgetTracker extends JFrame {
    // defining models for storing the list of income/expenses
    private DefaultListModel<String> incomeListModel;
    private DefaultListModel<String> expenseListModel;

    // defining lists to display income/expenses 
    private JList<String> incomeList;
    private JList<String> expenseList;

    // defining text fields for user input
    private JTextField descriptionField;
    private JTextField amountField;
    private JTextField categoryField;
    private JTextField dateField;

    // defining labels to display total income/expense
    private JLabel totalIncomeLabel;
    private JLabel totalExpensesLabel;
    private JLabel balanceLabel; // to display current balance

    private JComboBox<String> typeComboBox; //dropdown for the type selection --> income or expense
    
    private double balance = 0.0; // start balance off at 0 and used to track balance 

    private final String PIXABAY_API_KEY = "50069243-762d1946fc96d88573d6006d0"; // my API key from PIXABAY for gettng pictures API

    // constructor to set up main window
    public BudgetTracker() { 
        setTitle("Budget Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1450, 700);
        setLocationRelativeTo(null); // centering window on screen
        setBackground(Color.BLACK); 

        // welcome screen setup (first screen to pop up)
        JPanel welcomePanel = createWelcomePanel();

        // show the welcome screen first
        add(welcomePanel);
        setVisible(true);
    }

    // creating the actual welcome panel
    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(new Color(41, 41, 41));
        welcomePanel.setLayout(new BorderLayout()); // setting the layout manager--> BorderLayout (positioning in NORTH, SOUTH, CENTER)
    
        // creating a label with the welcome message --> centered horizontally
        JLabel welcomeLabel = new JLabel("Welcome to Budget Tracker!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeLabel.setForeground(Color.WHITE);
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
    
        // creating the start button using createButton method
        JButton startButton = createButton("Log My Entry", Color.BLACK);
        // an action listener to the button so when pressed--> lead to main page 
        startButton.addActionListener(e -> {
            // switching to main tracker UI
            getContentPane().removeAll(); // clearing the welcome screen 
            getContentPane().setLayout(new BorderLayout());
            setupMainTrackerUI(); // ^ calling the method to set up/show main budget tracker interface
            revalidate();  // refresh the layout
            repaint();     // redraw the window --> without these it the main page wsnt appearing
        });
    
        welcomePanel.add(startButton, BorderLayout.SOUTH); // adding the start button at the bottom 
    
        return welcomePanel;
    }
    
    // method to set up/display the main page
    private void setupMainTrackerUI() {

        // initializing list models to store income/expense entries
        incomeListModel = new DefaultListModel<>();
        expenseListModel = new DefaultListModel<>();

        // create JList componenets bound to the models --> display income/expenses
        incomeList = new JList<>(incomeListModel);
        expenseList = new JList<>(expenseListModel);

        // creating text fields to enter category/sate
        categoryField = new JTextField(10);
        dateField = new JTextField(10);

        // setting font
        Font font = new Font("Monaco", Font.PLAIN, 14);
        incomeList.setFont(font);
        expenseList.setFont(font);

        // styling the actual lists
        incomeList.setBackground(new Color(41, 41, 41));
        expenseList.setBackground(new Color(41, 41, 41));
        incomeList.setForeground(Color.WHITE);
        expenseList.setForeground(Color.WHITE);

        // wrapping the lists in scroll panes
        JScrollPane incomeScroll = new JScrollPane(incomeList);
        JScrollPane expenseScroll = new JScrollPane(expenseList);

        // creating/configuring the income panel with green border
        JPanel incomePanel = new JPanel();
        incomePanel.setLayout(new BorderLayout());
        incomePanel.setBackground(new Color(41, 41, 41));
        incomePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GREEN, 2),
            "Income",
            0,
            0,
            new Font("Monaco", Font.BOLD, 14),
            Color.GREEN));
        incomePanel.add(incomeScroll, BorderLayout.CENTER);

        // same as above but for expenses
        JPanel expensePanel = new JPanel();
        expensePanel.setLayout(new BorderLayout());
        expensePanel.setBackground(new Color(41, 41, 41));
        expensePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.RED, 2),
            "Expenses",
            0,
            0,
            new Font("Monaco", Font.BOLD, 14),
            Color.RED));
        expensePanel.add(expenseScroll, BorderLayout.CENTER);

        // creating a panel to hold both income/expense side by side
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(1, 2, 20, 0)); 
        listPanel.setBackground(new Color(41, 41, 41));
        listPanel.add(incomePanel);
        listPanel.add(expensePanel);

        add(listPanel, BorderLayout.CENTER); // adding this list panel to center

        // creating input fields/dropdown for adding new entries
        descriptionField = new JTextField(20);
        amountField = new JTextField(10);
        typeComboBox = new JComboBox<>(new String[]{"Income", "Expense"});

        // creating all the buttons + styling
        JButton addButton = createButton("Add Entry", new Color(0, 123, 255));
        JButton saveButton = createButton("Save Entries", new Color(40, 167, 69));
        JButton loadButton = createButton("Load Entries", new Color(255, 193, 7));
        JButton deleteButton = createButton("Delete Entry", new Color(220, 53, 69));
        JButton editButton = createButton("Edit Entry", new Color(108, 117, 125));
        JButton summaryButton = createButton("Summary Report", new Color(123, 104, 238));
        JButton startNewMonthButton = createButton("Start New Month", new Color(255, 87, 34));
        
        // create labels to diplay balance/ different totals
        totalIncomeLabel = new JLabel("Total Income: $0.00");
        totalIncomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalIncomeLabel.setForeground(Color.GREEN);

        totalExpensesLabel = new JLabel("Total Expenses: $0.00");
        totalExpensesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalExpensesLabel.setForeground(Color.RED);

        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        balanceLabel.setForeground(Color.WHITE);

        // attaching event listeners to handle button clicks for each
        addButton.addActionListener(e -> addEntry());
        saveButton.addActionListener(e -> saveEntries());
        loadButton.addActionListener(e -> loadEntries());
        deleteButton.addActionListener(e -> deleteEntry());
        editButton.addActionListener(e -> editEntry());
        summaryButton.addActionListener(e -> generateSummaryReport());
        startNewMonthButton.addActionListener(e -> startNewMonth());

        // creating input panels for top section --> adding new entries
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(138, 127, 127));
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10)); // left-align, horizontal gap 15px, vertical gap 10px --> for reference
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryField);
        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        inputPanel.add(dateField); 
        inputPanel.add(new JLabel("Type:"));
        inputPanel.add(typeComboBox);   
        inputPanel.add(addButton);
        inputPanel.add(startNewMonthButton);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10)); // padding around panel
        add(inputPanel, BorderLayout.NORTH); // place at top ^

        // creating bottom panel for my other action buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(41, 41, 41));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        bottomPanel.add(saveButton);
        bottomPanel.add(loadButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(editButton);
        bottomPanel.add(totalIncomeLabel);
        bottomPanel.add(totalExpensesLabel);
        bottomPanel.add(balanceLabel);
        bottomPanel.add(summaryButton);
        add(bottomPanel, BorderLayout.SOUTH); // place at bottom

        loadEntries(); // for the loading entries when the app starts
    }

    // for styled buttons
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text); // create JButton with specified text
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 16));  
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // this is so when the mouse hovers over button --> turn to hand instead of pointer


        button.setUI(new BasicButtonUI() { //customize the buttons UI --> if i remove this section ==>buttons lose color
            @Override
            public void paint(Graphics g, JComponent c) {
                super.paint(g, c);
            }
        });
        return button; // return the fully configured button
    }



    private void addEntry() {
        // getting inputs for each textfields (or dropdown for type)
        String description = descriptionField.getText().trim();
        String amountText = amountField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();
        String categoryName = categoryField.getText().trim();
        String date = dateField.getText().trim();
    
        // cheacking if any text field is empty --> if so show error
        if (description.isEmpty() || amountText.isEmpty() || categoryName.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter description, amount, category, and date.", "Input Error", JOptionPane.ERROR_MESSAGE); // pop up message
            return;
        }
    
        try {
            // parse amount string to a double number
            double amount = Double.parseDouble(amountText);

            // creating a new category object with what was entered for category 
            Category category = new Category(categoryName);

            // creatubgg a transaction object with desc, amount, category,date
            Transaction transaction = new Transaction(description, amount, category, date);
    
            // getting a formatted string of the full transaction for display
            String formattedEntry = transaction.toString();
    
            //checking if entry is an expense 
            if ("Expense".equals(type)) {
                amount = -Math.abs(amount); //^ make sure its negative for expenses
                expenseListModel.addElement(formattedEntry); // add the formatted entr to the expense list
            } else { // for income +
                amount = Math.abs(amount); // make sure positive
                incomeListModel.addElement(formattedEntry); // ^^
            }
    
            balance += amount; // update balance
            updateBalanceLabel(); // ^ updating the balance label as well on UI
    
            // clearing input fields ready for next inputs
            descriptionField.setText("");
            amountField.setText("");
            categoryField.setText("");
            dateField.setText("");
    
            fetchAndShowImage(description); // fetching/showing image related to description
    
        } 
        catch (NumberFormatException e) { // if amounts is not a number --> error message
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void fetchAndShowImage(String query) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private ImageIcon imageIcon; // variable for storing image brought in

            @Override
            protected Void doInBackground() {
                try {
                    // buuilding the API URL with the query + API jey --> URL encoded
                    String apiUrl = "https://pixabay.com/api/?key=" + PIXABAY_API_KEY + "&q=" + URLEncoder.encode(query, "UTF-8") + "&image_type=photo&per_page=3";
                    
                    // converting string A{I URL to a URL object
                    URL url = new URI(apiUrl).toURL();

                    // opening and HTTP connection to the API URL
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET"); // setting HTTP method to GET

                    // read the API response using a scanner
                    Scanner scanner = new Scanner(conn.getInputStream());
                    StringBuilder json = new StringBuilder();
                    while (scanner.hasNext()) {
                        json.append(scanner.nextLine()); // append each line of the response
                    }
                    scanner.close();

                    // parse the JSON response
                    JSONObject response = new JSONObject(json.toString());
                    JSONArray hits = response.getJSONArray("hits");
                   
                    // checking id atleast one image is found
                    if (hits.length() > 0) {
                        //^ if so get the URL of the first image and convert to an imageIcon
                        String imageUrl = hits.getJSONObject(0).getString("previewURL");
                        imageIcon = new ImageIcon(new URI(imageUrl).toURL());
                    }
                } 
                catch (IOException | URISyntaxException e) {
                    // if something goes wrong/no image --> error message
                    System.out.println("Failed to fetch image: " + e.getMessage());
                }
                return null;
            }

            @Override 
            // after background work finishes
            protected void done() {
                if (imageIcon != null) {
                    JLabel imageLabel = new JLabel(imageIcon); // creates label with fetched image
                    JOptionPane.showMessageDialog(BudgetTracker.this, imageLabel, "Related Image", JOptionPane.PLAIN_MESSAGE); // showing image in a popup 
                }
            }
        };
        worker.execute(); // start background worker
    }


    private void saveEntries() {
        // getting the new filename based on current month/ year
        String monthYear = LocalDate.now().getMonth().toString() + "_" + LocalDate.now().getYear();
        String newFileName = "entries_" + monthYear + ".txt"; // creating the finename
    
        try (PrintWriter writer = new PrintWriter(new FileWriter(newFileName))) {
            // loop through income entries and write each to file
            for (int i = 0; i < incomeListModel.size(); i++) {
                writer.println("INCOME:" + incomeListModel.get(i));
            }
            // ^ same for expense entries
            for (int i = 0; i < expenseListModel.size(); i++) {
                writer.println("EXPENSE:" + expenseListModel.get(i));
            }
            // write current balance at end of file
            writer.println("BALANCE:" + balance);

            //shpw success message popup
            JOptionPane.showMessageDialog(this, "Entries saved successfully!");
        } 
        catch (IOException e) { // if error --> pop up error message
            JOptionPane.showMessageDialog(this, "Error saving entries.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void loadEntries() {
        // getting current month and year dynamically
        String monthYear = LocalDate.now().getMonth().toString() + "_" + LocalDate.now().getYear();
        String fileName = "entries_" + monthYear + ".txt"; // build filename based on month/year

        File file = new File(fileName);
        if (!file.exists()) return; // if file doesnt exist just exit

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // clear existing income/expense lists --> reset balance
            incomeListModel.clear();
            expenseListModel.clear();
            balance = 0.0;

            // read each line from file
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("INCOME:")) { // if line starts with "income" add to income list
                    incomeListModel.addElement(line.substring(7));
                } 
                else if (line.startsWith("EXPENSE:")) { // if starts with "expense" add to expense list
                    expenseListModel.addElement(line.substring(8));
                } 
                else if (line.startsWith("BALANCE:")) { // if starts with balance parse and set the balance
                    balance = Double.parseDouble(line.substring(8));
                }
            }

            updateBalanceLabel(); // update displayed balance on app
            JOptionPane.showMessageDialog(this, "Entries loaded successfully!");// pop up success message
        
        } 
        catch (IOException | NumberFormatException e) { // if error reading from file--> error message
            JOptionPane.showMessageDialog(this, "Error loading entries.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEntry() {
        // check if an income entry is selected
        if (incomeList.getSelectedIndex() != -1) { 
            String selected = incomeList.getSelectedValue();
            double amount = extractAmount(selected);
            balance -= amount; // subtract amount selected from balance
            incomeListModel.remove(incomeList.getSelectedIndex()); // ^ remove selected item from whole income list
        } 
        //check if expense entry is selected
        else if (expenseList.getSelectedIndex() != -1) { //check 
            String selected = expenseList.getSelectedValue();
            double amount = extractAmount(selected);
            balance += amount; // add amount back to balance
            expenseListModel.remove(expenseList.getSelectedIndex()); // remove selected entry from expense list
        } 
        else { // if nothing is selected--> popup warining message
            JOptionPane.showMessageDialog(this, "Please select an entry to delete.");
            return;
        }

        updateBalanceLabel(); // update balance label in app
    }

    private double extractAmount(String entry) { // helper function to get number amount from entry string
        try {
            // find the "$" symbol and get substring after it and remove commas
            String amountStr = entry.substring(entry.lastIndexOf("$") + 1);
            return Double.parseDouble(amountStr);
        } 
        catch (Exception e) {
            return 0; // if parsing fails
        }
    }

    // to edit full description and amount
    private void editEntry() {
        // have to determing whic list is being selected --> income/expense
        JList<String> selectedList = incomeList.getSelectedIndex() != -1 ? incomeList : (expenseList.getSelectedIndex() != -1 ? expenseList : null);
        // get correct list model based on selected list 
        DefaultListModel<String> model = selectedList == incomeList ? incomeListModel : (selectedList == expenseList ? expenseListModel : null);

        // if nothing is selected -->warning popup
        if (selectedList == null || model == null) {
            JOptionPane.showMessageDialog(this, "Please select an entry to edit.");
            return;
        }

        int selectedIndex = selectedList.getSelectedIndex();
        String selected = selectedList.getSelectedValue();
        // extract original amount from entry
        double originalAmount = extractAmount(selected);

        // prompt user to edit the description
        String newDescription = JOptionPane.showInputDialog(this, "Edit description:", selected.split(" : ")[0]);
        if (newDescription == null || newDescription.trim().isEmpty()) return;

        // prompt user to edit the amount
        String newAmountStr = JOptionPane.showInputDialog(this, "Edit amount:", String.format("%.2f", originalAmount));
        if (newAmountStr == null || newAmountStr.trim().isEmpty()) return;

        try {
            //parse new amount
            double newAmount = Double.parseDouble(newAmountStr);
            
            // adjustong the balance depending on income or expense
            if (selectedList == expenseList) {
                newAmount = Math.abs(newAmount); // check if positive
                balance += originalAmount - newAmount;
            } 
            else {
                newAmount = Math.abs(newAmount);
                balance += newAmount - originalAmount;
            }

            // build the updated entry string
            String updatedEntry = newDescription + " : $" + String.format("%.2f", newAmount);
            model.set(selectedIndex, updatedEntry); // update entry in the list
            updateBalanceLabel(); // update balance in app
        } 
        catch (NumberFormatException e) { // if new amount invalid --> error message
            JOptionPane.showMessageDialog(this, "Invalid amount entered.");
        }
    }


    private void updateBalanceLabel() { // not only for balance but just totals in general --> using helpers
        balanceLabel.setText("Balance: $" + String.format("%.2f", balance)); // update balance label with current balance 
    
        //calculate total income/expense using helper methods
        double totalIncome = calculateTotal(incomeListModel);
        double totalExpenses = calculateTotal(expenseListModel);
    
        // update total income/expense label
        totalIncomeLabel.setText("Total Income: $" + String.format("%.2f", totalIncome));
        totalExpensesLabel.setText("Total Expenses: $" + String.format("%.2f", totalExpenses));
    }

    //helper function to add up amounts in list --> sums
    private double calculateTotal(DefaultListModel<String> model) { // Helper function for above ^
        double total = 0;
        // loop over each item in list model
        for (int i = 0; i < model.size(); i++) {
            total += extractAmount(model.get(i)); // extract amount from each entry and add to total
        }
        // return total sum
        return total;
    }    

    private void generateSummaryReport() {
        // calc total income/expenses
        double totalIncome = calculateTotal(incomeListModel);
        double totalExpenses = calculateTotal(expenseListModel);
        
        // calc current balance
        double balance = totalIncome - totalExpenses;

        // use string builder to creat the report text
        StringBuilder report = new StringBuilder();
        report.append("Total Income: $").append(String.format("%.2f", totalIncome)).append("\n");
        report.append("Total Expenses: $").append(String.format("%.2f", totalExpenses)).append("\n");
        report.append("Balance: $").append(String.format("%.2f", balance)).append("\n");
        report.append("Number of Income Entries: ").append(incomeListModel.size()).append("\n");
        report.append("Number of Expense Entries: ").append(expenseListModel.size()).append("\n");

        // show summary report as a pop up box
        JOptionPane.showMessageDialog(this, report.toString(), "Summary Report", JOptionPane.INFORMATION_MESSAGE);
    }

    // method to start a new month (reset the data + create a new file)
    private void startNewMonth() {
        // get current month/ year to generate a new file name
        String monthYear = LocalDate.now().getMonth().toString() + "_" + LocalDate.now().getYear(); // this is why we cant create two files in 1 month--> cant create 2 files with same name 
        String newFileName = "entries_" + monthYear + ".txt";

        // create the new file and reset the balance/ entries
        File newFile = new File(newFileName);
        try {
            if (newFile.createNewFile()) { // try to create the new file and output message popup if successful or not
                JOptionPane.showMessageDialog(this, "Starting new month! New file created: " + newFileName);
            } 
            else {
                JOptionPane.showMessageDialog(this, "Failed to create new file.");
            }
        } 
        catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating new file.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // clear current data (reset income/expense lists and balance)
        incomeListModel.clear();
        expenseListModel.clear();
        balance = 0.0;
        updateBalanceLabel(); // update balance label on app

        // clear other fields like description, amount, etc
        descriptionField.setText("");
        amountField.setText("");
        categoryField.setText("");
        dateField.setText("");
        typeComboBox.setSelectedIndex(0);
    }
    public static void main(String[] args) {
        // ensure the BudgetTracker GUI runs on the Swing event dispatch thread
        SwingUtilities.invokeLater(BudgetTracker::new);
    }
}
