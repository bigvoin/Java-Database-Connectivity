import java.sql.*;
import java.util.*;

public class Main extends DbBasic {

    private Main(String dbName) {super( dbName);} // gets the connection from DbBasic

    private ArrayList<Table> tables = new ArrayList<>(); // list of tables

    private void mainMethod() throws SQLException {

        DatabaseMetaData data = con.getMetaData(); // gets the metadata from the connected table
        ResultSet tableMetadata = data.getTables(null, null, null, new String[] { "TABLE" }); // gets all the tables from the data

         //Create a arraylist for the tables and add the name of the table and the data from the database
        while(tableMetadata.next()) 
        {
            String name = tableMetadata.getString("TABLE_NAME");
            tables.add(new Table(name, data));
        }

        this.sort(); // sort the tables in the right order
        

        // Print the create table statements
        for(Table table : tables) 
        {
            String statement = table.getCreateTable();
            System.out.println(statement);
        }

        System.out.println();

        // Create all the insert statements and print them 
        for (Table table : tables) 
        {
             ArrayList<String> insert = table.getInsertSatements(con.createStatement());
            for(String statement : insert)
            {
                System.out.println(statement);
            }
            System.out.println();
    
        }
        this.close(); // close the database connection
    }

    public void sort()
    {
        // Sort the tables to make sure they get created in the right order
        for(int i = 0; i < tables.size(); i++) 
        {
            Table first = tables.get(i); // creating tables 
            if(first.hasForeignKey() == false) // check for foreign key
                continue;
            for(int j = i + 1; j < tables.size(); j++) 
            {
                Table second = tables.get(j);
                if(second.hasForeignKey() == false) 
                    continue;

                if(first.getrefs().contains(second.getName())) // check if the first table references have the second table
                {
                    Collections.swap(tables, i, j); // swap the tables in the array list
                }
            }
        }
    }

    public static void main(String[] args) 
    {
        try {
            Main driver = new Main("University.db");
            driver.mainMethod();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
}// end of the Main 