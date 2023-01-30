package rentatool.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import rentatool.rental_items.Chainsaw;
import rentatool.rental_items.Jackhammer;
import rentatool.rental_items.Ladder;
import rentatool.rental_items.Tool;
import rentatool.rental_items.ToolEnums.*;

/**
 * This class is used to establish connections to an SQLite database file called "rentatool.db".
 * The class uses the SQLite JDBC Driver (version 3.40, Xerial project) to establish
 * connections. This Driver is a library located under the lib folder (sqlite-jdbc-3.40.0.0.jar)
 * and was downloaded directly from Xerial's github. Since SQLite is a cross-platform serverless
 * database, it's easy to include with the Rent-A-Tool project to demonstrate the application's
 * SQL capabilities.
 * 
 * @author CSGarcia1191
 *
 */
public class SQLiteDB extends Database {

	// Property that will hold the connection to the SQLite database
	private static Connection connection;
	
	/**
	 * Public constructor that enables the application to grab a connection to the SQLite database.
	 * Once the connection is initialized, it will persist throughout the program's execution.
	 * The only time a NEW connection is initialized is when there has been no prior connection OR
	 * the prior connection has been closed. A new connection will delete any Tool table that may
	 * have previously existed in the database and will create a new one from scratch.
	 */
	public SQLiteDB() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection("jdbc:sqlite:rentatool.db");
				// Since a new connection is initialized, delete existing Tool tables
				// and create a fresh one.
				deleteTable();
				createTable();
			}
		} catch (SQLException e) {
			System.out.println("Could not connect to SQLite DB.");
		}
	}
	
	// Closes the database connection if it exists and is not already closed 
	@Override
	void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			} else {
				System.out.println("SQLite DB connection is already closed!");
			}
		} catch (SQLException e) {
			System.out.println("Could not close the SQLite DB connection.");
		}
	}

	/**
	 * Creates a table named "Tool" with the Tool code as the primary key
	 * Four tools are inserted by default:
	 * 1) Code: CHNS, Type: Chainsaw,   Brand: Stihl
	 * 2) Code: LADW, Type: Ladder,     Brand: Werner
	 * 3) Code: JAKD, Type: Jackhammer, Brand: DeWalt
	 * 4) Code: JAKR, Type: Jackhammer, Brand: Ridgid
	 * Creates table via a "CREATE TABLE IF NOT EXISTS tool" statement
	 */
	@Override
	void createTable() {
		try {
			if (connection != null && !connection.isClosed()) {
				Statement statement = connection.createStatement();
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS tool("
						+ "code VARCHAR PRIMARY KEY UNIQUE," // setting this property as UNIQUE allows for use of "OR IGNORE in insert statements"
						+ "type VARCHAR,"
						+ "brand VARCHAR,"
						+ "dailyCharge FLOAT,"
						+ "chargeOnWeekdays BOOLEAN,"
						+ "chargeOnWeekends BOOLEAN,"
						+ "chargeOnHolidays BOOLEAN,"
						+ "checkedOut BOOLEAN);");
				statement.executeUpdate("INSERT OR IGNORE INTO tool values('CHNS', 'Chainsaw', 'Stihl', 1.49, true, false, true, false);");
				statement.executeUpdate("INSERT OR IGNORE INTO tool values('LADW', 'Ladder', 'Werner', 1.99, true, true, false, false);");
				statement.executeUpdate("INSERT OR IGNORE INTO tool values('JAKD', 'Jackhammer', 'DeWalt', 2.99, true, false, false, false);");
				statement.executeUpdate("INSERT OR IGNORE INTO tool values('JAKR', 'Jackhammer', 'Ridgid', 2.99, true, false, false, false);");
				statement.close();
			} else {
				System.out.println("There is currently no valid database connection. Could not create table.");
			}
		} catch (SQLException e) {
			System.out.println("There was an issue creating the database table.");
		}
	}

	/**
	 * Deletes table via a "DROP TABLE IF EXISTS tool" statement
	 */
	@Override
	void deleteTable() {
		try {
			if (connection != null && !connection.isClosed()) {
				Statement statement = connection.createStatement();
				statement.executeUpdate("DROP TABLE IF EXISTS tool;");
				// Closing the Statement
				statement.close();
			} else {
				System.out.println("There is currently no valid database connection. Could not delete table.");
			}
		} catch (SQLException e) {
			System.out.println("There was an issue deleting the database table.");
		}
	}

	/**
	 * Adds the passed-in Tool to the database by:
	 * 1) Checks if Tool code already exists via a "SELECT 1 FROM tool WHERE code = ?" statement
	 * 2) If Tool does not exist, adds Tool via a "INSERT INTO tool" statement
	 */
	@Override
	public void addTool(Tool tool) {
		if (tool == null) {
			System.out.println("The passed-in Tool object was null. No tool was added to the database table.");
			return;
		}
				
		try {
			if (connection != null && !connection.isClosed()) {
				String statementSQL = "SELECT 1 FROM tool WHERE code = ?;";
				PreparedStatement ps = connection.prepareStatement(statementSQL);
				ps.setString(1, tool.getCode().toString());
				ResultSet resultSet = ps.executeQuery();
				if (resultSet.next()) {
					System.out.println(String.format("A tool with code %s already exists.", tool.getCode()));
				} else {
					statementSQL = "INSERT INTO tool values(?, ?, ?, ?, ?, ?, ?, ?);";
					ps.close(); // Closing the initial PreparedStatement
					ps = connection.prepareStatement(statementSQL);
					ps.setString(1, tool.getCode().toString());
					ps.setString(2, tool.getType().toString());
					ps.setString(3, tool.getBrand().toString());
					ps.setFloat(4, tool.getDailyCharge());
					ps.setBoolean(5, tool.isChargeOnWeekdays());
					ps.setBoolean(6, tool.isChargeOnWeekends());
					ps.setBoolean(7, tool.isChargeOnHolidays());
					ps.setBoolean(8, tool.isCheckedOut());
					ps.executeUpdate();
				}
				
				// Close the ResultSet and PreparedStatement
				resultSet.close();
				ps.close();
			} else {
				System.out.println("There is currently no valid database connection. Could not add tool to the database table.");
			}
		} catch (SQLException e) {
			System.out.println("There was an issue adding the tool to the database table.");
		}
	}

	/**
	 * Returns an instance of the Tool type associated with the passed-in code.
	 * The database stores all the properties of a tool as a record.
	 * The record is queried for via a "SELECT * FROM tool WHERE code = ?" statement.
	 * A new instance of that Tool type is then created and returned.
	 */
	@Override
	public Tool getTool(Code code) {
		if (code == null) {
			System.out.println("The passed-in Code was null. No Tool could be retrieved.");
			return null;
		}
		
		Tool toolToReturn = null;
		
		try {
			if (connection != null && !connection.isClosed()) {
				String selectSQL = "SELECT * FROM tool WHERE code = ?;";
				PreparedStatement ps = connection.prepareStatement(selectSQL);
				ps.setString(1, code.toString());
				ResultSet resultSet = ps.executeQuery();
				
				if (resultSet.next()) {
					Type toolType = Type.valueOf(resultSet.getString("type").toUpperCase());
					if (toolType == Type.CHAINSAW) { // Creating matching Chainsaw instance
						toolToReturn = new Chainsaw(
								Code.valueOf(resultSet.getString("code")),
								Brand.valueOf(resultSet.getString("brand").toUpperCase()),
								resultSet.getFloat("dailyCharge"),
								resultSet.getBoolean("chargeOnWeekdays"),
								resultSet.getBoolean("chargeOnWeekends"),
								resultSet.getBoolean("chargeOnHolidays"),
								resultSet.getBoolean("checkedOut"));
					} else if (toolType == Type.JACKHAMMER) { // Creating matching Jackhammer instance
						toolToReturn = new Jackhammer(
								Code.valueOf(resultSet.getString("code")),
								Brand.valueOf(resultSet.getString("brand").toUpperCase()),
								resultSet.getFloat("dailyCharge"),
								resultSet.getBoolean("chargeOnWeekdays"),
								resultSet.getBoolean("chargeOnWeekends"),
								resultSet.getBoolean("chargeOnHolidays"),
								resultSet.getBoolean("checkedOut"));
					} else if (toolType == Type.LADDER) { // Creating matching Ladder instance
						toolToReturn = new Ladder(
								Code.valueOf(resultSet.getString("code")),
								Brand.valueOf(resultSet.getString("brand").toUpperCase()),
								resultSet.getFloat("dailyCharge"),
								resultSet.getBoolean("chargeOnWeekdays"),
								resultSet.getBoolean("chargeOnWeekends"),
								resultSet.getBoolean("chargeOnHolidays"),
								resultSet.getBoolean("checkedOut"));
					} else {
						System.out.println(String.format("Issue returning tool of type %s.", toolType));
						System.out.println("The tool was found in the database but there is no instance class that can represent it. Returning a null object.");
					}
				} else {
					System.out.println(String.format("No matching Tool found with Code %s.", code));
				}
				
				// Close the ResultSet and PreparedStatement
				resultSet.close();
				ps.close();
			} else {
				System.out.println("There is currently no valid database connection. Could not retrieve tool from the database table.");
			}
		} catch (SQLException e) {
			System.out.println("There was an issue retrieving the tool from the database table.");
		}
		
		return toolToReturn;
	}

	/**
	 * Removes the Tool record with the matching Tool code from the database.
	 * First, the method checks if Tool code currently exists in the database
	 * via a "SELECT 1 FROM tool WHERE code = ?" statement. If it does not,
	 * then a warning message is printed to the console. Otherwise, the
	 * removal happens via a "DELETE FROM tool WHERE code = ?" statement.
	 */
	@Override
	public void removeTool(Code code) {
		if (code == null) {
			System.out.println("The passed-in Code was null. No tool was removed from the database table.");
			return;
		}
		
		try {
			if (connection != null && !connection.isClosed()) {
				String statementSQL = "SELECT 1 FROM tool WHERE code = ?;";
				PreparedStatement ps = connection.prepareStatement(statementSQL);
				ps.setString(1, code.toString());
				ResultSet resultSet = ps.executeQuery();
				if (!resultSet.next()) {
					System.out.println(String.format("No Tool with code %s was found. Nothing to remove.", code));
				} else {
					statementSQL = "DELETE FROM tool WHERE code = ?;";
					ps = connection.prepareStatement(statementSQL);
					ps.setString(1, code.toString());
					ps.executeUpdate();
				}
				
				// Close the ResultSet and PreparedStatement
				resultSet.close();
				ps.close();
			} else {
				System.out.println("There is currently no valid database connection. Could not remove tool from the database table.");
			}
		} catch (SQLException e) {
			System.out.println("There was an issue removing the tool from the database table.");
		}
	}

	/**
	 * Update the Tool record in the database that matches the passed-in code.
	 * The passed-in attribute is updated with the passed-in value for the
	 * Tool record that was found.
	 * This update happens via a "UPDATE tool SET code = ? WHERE code = ?" statement
	 * 
	 * Note: If the passed-in value is not an instance of the passed-in attribute,
	 * then no update occurs and a warning message is displayed in the console.
	 */
	@Override
	public void updateTool(Code code, Attribute attribute, Object value) {
		if (code == null || attribute == null || value == null) {
			System.out.println("A passed-in parameter was null. Please pass in non-null parameters.");
			return;
		}
		
		String updateSQL = null; // Will be updated if valid Enums combinations are found
				
		if (attribute.equals(Attribute.CODE)) {
			if (value instanceof Code) {
				updateSQL = "UPDATE tool SET code = ? WHERE code = ?;";
			} else {
				System.out.println("Invalid Code value passed in. Not updating tool.");
			}
		} else if (attribute.equals(Attribute.TYPE)) {
			if (value instanceof Type) {
				updateSQL = "UPDATE tool SET type = ? WHERE code = ?;";
			} else {
				System.out.println("Invalid Type value passed in. Not updating tool.");
			}
		} else if (attribute.equals(Attribute.BRAND)) {
			if (value instanceof Brand) {
				updateSQL = "UPDATE tool SET brand = ? WHERE code = ?;";
			} else {
				System.out.println("Invalid Brand value passed in. Not updating tool.");
			}
		} else if (attribute.equals(Attribute.DAILYCHARGE)) {
			if (value instanceof Float) {
				updateSQL = "UPDATE tool SET dailyCharge = ? WHERE code = ?;";
			} else {
				System.out.println("Invalid Float value passed in. Not updating tool.");
			}
		} else if (attribute.equals(Attribute.CHARGEONWEEKDAYS)) {
			if (value instanceof Boolean) {
				updateSQL = "UPDATE tool SET chargeOnWeekdays = ? WHERE code = ?;";
			} else {
				System.out.println("Invalid Boolean value passed in. Not updating tool.");
			}
		} else if (attribute.equals(Attribute.CHARGEONWEEKENDS)) {
			if (value instanceof Boolean) {
				updateSQL = "UPDATE tool SET chargeOnWeekends = ? WHERE code = ?;";
			} else {
				System.out.println("Invalid Boolean value passed in. Not updating tool.");
			}
		} else if (attribute.equals(Attribute.CHARGEONHOLIDAYS)) {
			if (value instanceof Boolean) {
				updateSQL = "UPDATE tool SET chargeOnHolidays = ? WHERE code = ?;";
			} else {
				System.out.println("Invalid Boolean value passed in. Not updating tool.");
			}
		} else if (attribute.equals(Attribute.CHECKEDOUT)) {
			if (value instanceof Boolean) {
				updateSQL = "UPDATE tool SET checkedOut = ? WHERE code = ?;";
			} else {
				System.out.println("Invalid Boolean value passed in. Not updating tool.");
			}
		} else {
			System.out.println("Invalid Attribute passed in. Not updating tool.");
		}
		
		// Only prepare statement if a valid attribute instance was passed-in
		if (updateSQL != null) {
			try {
				if (connection != null && !connection.isClosed()) {
					PreparedStatement ps = connection.prepareStatement(updateSQL);
					if (value instanceof Boolean) {
						ps.setBoolean(1, (boolean) value);
					} else if (value instanceof Float) {
						ps.setFloat(1, (float) value);
					} else {
						ps.setString(1, value.toString());
					}
					ps.setString(2, code.toString());
					ps.executeUpdate();
					// Close the PreparedStatment
					ps.close();
				} else {
					System.out.println("There is currently no valid database connection. Could not update tool in the database table.");
				}
			} catch (SQLException e) {
				System.out.println("There was an issue updating the tool in the database table.");
			}
		}
	}

	/**
	 * Prints out a String representation of all the records in the table.
	 * Retrieves the records to print via a "SELECT * FROM tool" statement
	 * The records are looped through and printed out one by one.
	 */
	@Override
	public void printStoredTools() {
		Statement statement;
		try {
			ArrayList<String> toolsList = new ArrayList<String>();
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM tool;");
			
			while (resultSet.next()) {
				toolsList.add(String.format("Code: %s, Type: %s, Brand: %s\nDaily Charge: $%,.2f\nCharge On Weekdays: %s\nCharge on Weekends: %s\nCharge on Holidays: %s\nChecked Out: %s\n\n",
						resultSet.getString("code"),
						resultSet.getString("type"),
						resultSet.getString("brand"),
						resultSet.getFloat("dailyCharge"),
						resultSet.getBoolean("chargeOnWeekdays") ? "Yes" : "No",
						resultSet.getBoolean("chargeOnWeekends") ? "Yes" : "No",
						resultSet.getBoolean("chargeOnHolidays") ? "Yes" : "No",
						resultSet.getBoolean("checkedOut") ? "Yes" : "No"));
			}
			
			// Close the ResultSet and PreparedStatment
			resultSet.close();
			statement.close();
			
			Collections.sort(toolsList);
			System.out.println(String.join("", toolsList));
		} catch (SQLException e) {
			System.out.println("There was an issue printing the database table.");
		}
	}
}
