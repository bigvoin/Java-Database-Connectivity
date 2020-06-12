import java.sql.*;
import java.util.*;

public class Table {

    /** Global Variables for the class */
    private String name;
    private HashMap<String, Column> columns = new HashMap<>();
    private boolean hasForeign = false;
    private ArrayList<String> refs = new ArrayList<>();;
    private DatabaseMetaData data;

    /**
     * Table constructor, wich creates all the columns in it
     * @param name The name of the table
     * @param metadata the data from the database
     * @throws SQLException 
     */

    public Table(String name, DatabaseMetaData data) throws SQLException
    {

        this.name = name;
        this.data = data;
   
        /**
         * generates the columns, the primary and foreing keys
         */
        try {
            this.generateColumns();
            this.generatePrimary();
            this.generateForeign();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates the columns of the give table and store them in hashmap
     * @throws SQLException
     */
    public void generateColumns() throws SQLException
    {
        ResultSet columnData = data.getColumns(null, null, name, null);
        while (columnData.next()) {
            String columnName = columnData.getString("COLUMN_NAME");
            Column column = new Column(columnData);

            columns.put(columnName, column);
        }
    }

    /**
     * Generates the Primary key of the table and place it in the Hash Map and set it to the column
     * @throws SQLException
     */
    public void generatePrimary() throws SQLException
    {
        ResultSet primaryKeys = data.getPrimaryKeys(null, null, name);
        while (primaryKeys.next()) {
            String primaryKey = primaryKeys.getString("COLUMN_NAME");

            if(columns.containsKey(primaryKey)) {
                columns.get(primaryKey).setPrimary();
            }
        }
    }

    /**
     * Generates the foreign key of each column and set the reference table on the correct column
     * @throws SQLException
     */
    public void generateForeign() throws SQLException
    {
        ResultSet foreignKeys = data.getImportedKeys(null, null, name);
        while(foreignKeys.next())
        {
            String key = foreignKeys.getString("FKCOLUMN_NAME");
            if(columns.containsKey(key)) 
            {
                String foreignTable = foreignKeys.getString("PKTABLE_NAME");
                String foreignKey = foreignKeys.getString("PKCOLUMN_NAME");
                columns.get(key).setReference(foreignTable + "(" + foreignKey + ")");
                refs.add(foreignTable);
                hasForeign = true;
            }
        }
    }

    /**
     * Method that returns the name of the table
     */
    public String getName() { return name; }

    /**
     * Method taht returns the column of the table
     * @param column name
     * @return
     */
    public Column getColumn(String column) { return columns.get(column); }

    /**
     * Returns if the table has a foreign key
     * @return
     */
    public boolean hasForeignKey() { return hasForeign; }

    /**
     * Check if the column value can not be Null and return the string
     */
    public String nullable(Column c)
    {
        if(c.isNullable()==false)
         {
             return " NOT NULL";
         }
         else {
             return "";
         }
    }

    /**
     * Create the tables as iterrating over the columns
     * @return string with the right data to create a table in SQL format
     */
    public String getCreateTable() 
    {
        String statement = "CREATE TABLE " + name;
        statement += " (\n"; // next line
        Collection<Column> columns = this.columns.values();

        // add the name of the column the type and check if the value can't be NULL
        for(Column column : columns) 
        {
            statement += "    " + column.getName() + " " + column.getType()
                    +this.nullable(column) + ",\n";
        }

        // check for column with primary key
        Column[] array = new Column[columns.size()];
        array = columns.toArray(array);
        statement += "    PRIMARY KEY (";
        for(int i = 0; i < columns.size(); i++) 
        {
            Column column = array[i];

            if(column.isPrimary()) 
            {
                statement += column.getName() + ", ";
            }
        }

        statement = statement.substring(0, statement.lastIndexOf(", ")); // removing unwanded ,
        statement += "),\n"; // next line

        // check for the foreign key and show the connection between the tables
        for(Column column : columns) 
        {
            if(column.hasReference()) 
            {
                statement += "    FOREIGN KEY (" + column.getName() + ") REFERENCES " + column.getReference() + ",\n";
            }
        }

        statement = statement.substring(0, statement.lastIndexOf(",")); // remove unwanted ,
        statement += "\n);"; // next line

        return statement; // return the final string
    }


    /**
     * Method that returns array list of strings, that represents insert statements of the table
     * iterrating over the columns of each table to get the values, check for different types 
     */
    public ArrayList<String> getInsertSatements(Statement statement) throws SQLException 
    {
        ResultSet data = statement.executeQuery("SELECT * FROM " + name); // executing the query to get the data from all columns
        Collection<Column> cols = columns.values(); // create a collection to get only the values from the hasmap
        ArrayList<String> statements = new ArrayList<>(); // list of statements

        while (data.next())  // iterrates over each column data
        {
            String insert = "INSERT INTO " + name + " VALUES (";
            //iterrates over the columns
            for (Column column : cols) 
            {
                if (column.getType().toLowerCase().contains("varchar")) //check for varchar
                {
                    String value = data.getString(column.getName());
                    insert += ("'" + value + "', ");

                } 
                else if (column.getType().toLowerCase().contains("int")) // check for integer
                {
                    int value = data.getInt(column.getName());
                    insert += (value) + ", ";
                }
            }

            insert = insert.substring(0, insert.lastIndexOf(",")); //remove unwanted ,
            insert += ");";

            statements.add(insert); // add each statement to the array list
        }

        return statements; // return the final array list
    }

    /**
     * Get the reference tables that each column has
     * @return array list of strings
     */
    public ArrayList<String> getrefs() { return refs; }

    @Override
    public String toString() {  // toString method for the foreach loops
        String s = "";

        Collection<Column> columns = this.columns.values();
        for(Column column : columns) {
            s += column.toString() + "\n";
        }

        return s;
    }
}