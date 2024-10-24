import java.sql.*;
import java.util.Scanner;

public class DBManager {

    // Properties storing connection data
    Scanner input = new Scanner(System.in);
    private static Connection conn;
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String user = "root";
    private static final String password = "";
    private static final String databasename = "trialStoreDB";
    private static final String url = "jdbc:mysql://localhost:3306/" + databasename;

    public DBManager() {
        conn = null;
        try {
            conn = (Connection) DriverManager.getConnection(url, user, password); // Carries out the connection
            if (conn != null) {
                System.out.println("Connected to the database");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("An error happened while connecting.");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (conn != null) {
                conn.close();   //Disconnection
                System.out.println("Disconnected from the database " + databasename);
            }
        } catch (SQLException e) {
            System.err.println("Error disconnecting" + e);
        }

    }

    // NEW TABLE
    public void createTable(String tableName) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql // Uses the tableName parameter, previously asked from the user
                    = "CREATE TABLE " + tableName + "("
                    + " Article_key SERIAL PRIMARY KEY,"
                    + " Name TEXT, "
                    + " Price INT)";
            st.executeUpdate(sql);
            System.out.println("Table created " + tableName);
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    // ADD ROW
    public void addProduct(String name, int price) throws SQLException {
        String sql
                = "INSERT INTO articles (Name, Price) values ('"
                + name
                + "', '"
                + price
                + "')";
        Statement st = null;
        try {
            st = conn.createStatement();

            int num = st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            System.out.println("Number of added products: " + num);
            ResultSet results = st.getGeneratedKeys();
            results.next();
            int id = results.getInt(1);
            // We asign an ID to the product
            System.out.println("Added the article with ID " + id);
            results.close();
        } finally {
            if (st != null) {
                st.close();
            }
        }

    }

    // SEE TABLE
    public void seeArticles() throws SQLException {
        String sql = "SELECT * FROM articles ORDER BY Article_key";
        Statement st = null;
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            int articles = 0; // Created to check if there are articles through an if block
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                int price = rs.getInt(3);
                int facturerKey = rs.getInt(4);
                System.out.println("Article ID: " + id + " - Article name: " + name + " - Price: " + price + " - Article key: " + facturerKey);
                articles++;
            }
            if (articles == 0) {
                System.out.println("No articles found in the table");
            }
            rs.close();
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    // SEE ROW
    public void seeArticle(String articleName) throws SQLException {
        String sql = "SELECT * FROM articles WHERE Name = '" + articleName + "'";
        Statement st = null;
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            int articles = 0;
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                int price = rs.getInt(3);
                int facturerKey = rs.getInt(4);
                System.out.println("Article ID: " + id + " - Article name: " + name + " - Price: " + price + " - Article key: " + facturerKey);
                articles++;
            }
            if (articles == 0) {
                System.out.println("No articles found with that name");
            }
            rs.close();
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    // EDIT ONE OR ALL FIELDS
    public void editArticle(int articleID) throws SQLException {
        String sql = "SELECT * FROM articles WHERE Article_key = '" + articleID + "'";
        Statement st = null; // First it checks whether the article we want exists
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            int articles = 0;
            while (rs.next()) {
                articles++;
            }
            if (articles == 0) // Checking article existence
            {
                System.out.println("No article with that ID");
            } else // If it exists, it'll be modified
            {
                System.out.println("Article found. Do you with to modify one or every field?"
                        + "\n1) One field"
                        + "\n2) Every field");
                int option = input.nextInt();
                input.nextLine();
                switch (option) {
                    case 1: //ONE FIELD
                        System.out.println("Modify one field. Which field do you wish to modify?"
                                + "\n1) Name"
                                + "\n2) Price");
                        int option2 = input.nextInt();
                        input.nextLine();
                        if (option2 == 1) //NAME
                        {
                            System.out.println("Modify the name of an article"
                                    + "\nEnter a new name:");
                            String newName = input.nextLine();
                            sql = "UPDATE `articles` SET `Name` = '" + newName + "' WHERE `articles`.`Article_key` = " + articleID;
                            st = null;
                            try {
                                st = conn.createStatement();
                                int num = st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                                System.out.println("Modified articles: " + num);
                            } finally {
                                if (st != null) {
                                    st.close();
                                }
                            }
                        } else if (option2 == 2) //PRICE
                        {
                            System.out.println("Modify the price of the article"
                                    + "\nEnter a new price:");
                            int newPrice = input.nextInt();
                            input.nextLine();
                            sql = "UPDATE `articles` SET `Price` = '" + newPrice + "' WHERE `articles`.`Article_key` = " + articleID;
                            st = null;
                            try {
                                st = conn.createStatement();
                                int num = st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                                System.out.println("Modified articles: " + num);
                            } finally {
                                if (st != null) {
                                    st.close();
                                }
                            }
                        }
                        break;

                    case 2:  //EVERY FIELD
                        System.out.println("Modify name and price"
                                + "\nEnter new name:");
                        String newName = input.nextLine();
                        System.out.println("Enter new price:");
                        int newPrice = input.nextInt();
                        input.nextLine();

                        sql = "UPDATE `articles` SET `Name` = '" + newName + "', `Price` = '" + newPrice + "' WHERE `articles`.`Article_key` = " + articleID;
                        st = null;
                        try {
                            st = conn.createStatement();
                            int num = st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                            System.out.println("Modified articles: " + num);
                        } finally {
                            if (st != null) {
                                st.close();
                            }
                        }
                        break;

                }
                rs.close();
            }
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    // DELETE ROW
    public void deleteArticle() throws SQLException {
        System.out.println("Enter an article ID from the next list: \n");
        seeArticles(); // First it'll show the available articles to delete
        int id = input.nextInt();
        input.nextLine(); // Then it'll ask the ID of the article we want to delete

        String sql = "DELETE FROM articles WHERE Article_key = " + id;
        Statement st = null;
        try {
            st = conn.createStatement();
            int num = st.executeUpdate(sql);
            System.out.println("Number of deleted articles: " + num);
            System.out.println("Successfully deleted the article with ID: " + id);
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    // DELETE TABLE
    public void deleteTable(String table) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql = "DROP TABLE IF EXISTS " + table;
            st.executeUpdate(sql);
            System.out.println("Deleted table " + table);
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    public static void main(String[] args) throws SQLException {

        DBManager connection = null;
        Scanner input = new Scanner(System.in);
        int menu = 1;

        try {
            connection = new DBManager(); // Carries out the connection
            System.out.println("Successfully accessed the database " + databasename);

            while (menu != 0) { // Showing the menu
                System.out.println("\n-------------------------------"
                        + "\nChoose one option:"
                        + "\n1) Create table"
                        + "\n2) Add a product"
                        + "\n3) See articles"
                        + "\n4) Search one article"
                        + "\n5) Edit article"
                        + "\n6) Delete article"
                        + "\n7) Delete table"
                        + "\n0) Exit"
                        + "\n-------------------------------");
                menu = input.nextInt();
                input.nextLine();

                switch (menu) {
                    case 1:
                        System.out.println("\nOption 1: Create table");
                        System.out.println("\nEnter the name for the new table:");
                        String name = input.nextLine();

                        connection.createTable(name);
                        break;

                    case 2:
                        System.out.println("\nOption 2: Add product");
                        System.out.println("\nEnter the name of the product you want to add:");
                        name = input.nextLine();
                        System.out.println("\nEnter the product's price:");
                        int price = input.nextInt();
                        input.nextLine();

                        connection.addProduct(name, price); // They are passed as parameters
                        break;

                    case 3:
                        System.out.println("Option 3: See articles");
                        System.out.println("Showing available products");

                        connection.seeArticles();
                        break;
                    case 4:
                        System.out.println("Option 4: Search for an article for its name");
                        System.out.println("Enter the article's name:");
                        name = input.nextLine();

                        connection.seeArticle(name);
                        break;
                    case 5:
                        System.out.println("Option 5: Modify an article by its ID");
                        // Before doing so, we'll show the available products so the user can choose one
                        System.out.println("Choose an article you want to modify from the next list and enter its ID:");
                        connection.seeArticles();
                        int id = input.nextInt();
                        input.nextLine();

                        connection.editArticle(id);
                        break;
                    case 6:
                        System.out.println("Option 6: Delete an article by its ID");
                        //same as option 5), but in this case I tried inserting the
                        //select query in the method itself, in stead of calling seeArticles() out of the method

                        connection.deleteArticle();
                        break;
                    case 7:
                        System.out.println("Option 7: Delete table by its name");
                        System.out.println("Enter the name of the table you wish to delete:");
                        name = input.nextLine();

                        connection.deleteTable(name);
                        break;
                    case 0:
                        System.out.println("Option 0: Exiting the program");
                        break;
                    default:
                        System.out.println("Wrong option, enter a number between 0 and 7");
                        break;
                }
            }
            System.out.println("Program ended. Disconnecting from the database.");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


}
