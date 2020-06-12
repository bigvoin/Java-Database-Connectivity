import java.sql.*;

public class Column {

    /** Global Variables for the class */
    private String c_name;
    private String c_type;
    private boolean isnull;
    private boolean primaryKey = false;
    private boolean hasRef = false;
    private String ref = "";

    /**
     * A column constructor, representing a single column from a table
     * @param set ResultSet from table metadata
     * @throws SQLException
     */
    public Column(ResultSet rs) throws SQLException {

        c_name = rs.getString("COLUMN_NAME"); // gets the name of the column
        c_type = rs.getString("TYPE_NAME"); // gets the data type of the column
        isnull = rs.getString("NULLABLE").equals("1"); // gets if the value is null
    }

    /**
     * Get method for name
     * @return the name of the column
     */
    public String getName() { return c_name; }

    /**
     * Get method fo type   
     * @return the type of the column
     */
    public String getType() { return c_type; }

    /**
     * Check if the column has primary key
     * @return if the column has primary key
     */
    public boolean isPrimary() { return primaryKey; }

    /**
     * Not Null
     * @return if the column sue can be NULL
     */
    public boolean isNullable() { return isnull; }

    /**
     * Foreign key 
     * @return if the column has a reference in oder table
     */
    public boolean hasReference() { return hasRef; }

    /**
     * Get the Reference
     * @return the reference of the column
     */
    public String getReference() { return ref; }

    /**
     * Set the primary key of the column to true
     */
    public void setPrimary() { this.primaryKey = true; }

    /**
     * Set method, changing the reference of the column with the parm
     * and seting the hasReference to true
     * @param reference column reference
     */
    public void setReference(String ref) {
        this.ref = ref;
        hasRef = true;
    }

    @Override
    public String toString() {  // toString method for the foreach loops
        String s = "" + c_name + "" + " " + c_type + (isnull ? "" : " NOT NULL") +
        (primaryKey ? " PRIMARY KEY" : "") + (!ref.equals("") ?  " " + ref : "");
        return s;
    }

}