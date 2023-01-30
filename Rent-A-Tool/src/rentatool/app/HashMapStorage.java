package rentatool.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rentatool.rental_items.Chainsaw;
import rentatool.rental_items.Jackhammer;
import rentatool.rental_items.Ladder;
import rentatool.rental_items.Tool;
import rentatool.rental_items.ToolEnums.*;

/**
 * This is an implementation of the CrudOps interface using a HashMap<Code, Tool>
 * data structure as the application's tool storage system. This implementation
 * was designed to demonstrate that Rent-A-Tool storage systems can range
 * from databases (default SQLite) to data structures, so long as they implement
 * the different CRUD operations defined in the CrudOps interface.
 * 
 * @author CSGarcia1191
 *
 */
public class HashMapStorage implements StorageSystem {

	// Storage system for HashMapStorage instances
	private HashMap<Code, Tool> storage;
	
	/**
	 * This constructor initializes a new storage system defined as a
	 * HashMap<Code, Tool> data structure. Similar to the SQLiteDB
	 * database, this StorageSystem has Tool codes as "primary" keys.
	 * These keys are then mapped to Tool objects.
	 */
	public HashMapStorage() {
		storage =  new HashMap<Code, Tool>();
		storage.put(Code.CHNS, new Chainsaw(Code.CHNS, Brand.STIHL));
		storage.put(Code.LADW, new Ladder(Code.LADW, Brand.WERNER));
		storage.put(Code.JAKD, new Jackhammer(Code.JAKD, Brand.DEWALT));
		storage.put(Code.JAKR, new Jackhammer(Code.JAKR, Brand.RIDGID));
	}

	/**
	 * Adds the passed-in Tool object to the storage system. First,
	 * the method checks if the passed-in Tool object is null. If
	 * not, then it checks if the storage system already contains
	 * the Tool. If not, then it adds the Tool object. If the Tool
	 * is null or the storage already contains the Tool, a warning
	 * message is displayed on the console and the method exits.
	 */
	@Override
	public void addTool(Tool tool) {
		if (tool == null) {
			System.out.println("The passed-in Tool object was null. No tool was added to the storage system.");
			return;
		}
		
		if (storage.containsKey(tool.getCode())) {
			System.out.println(String.format("A tool with code %s already exists.", tool.getCode()));
			return;
		}
		
		// All pre-conditions met, adding Tool to the storage system
		storage.put(tool.getCode(), tool);
	}

	/**
	 * Retrieves the Tool associated with the passed-in code
	 * from the storage system. First, the method checks if
	 * the passed-in code is null. If not, then it checks if
	 * the storage contains the passed-in code. If so, it
	 * returns the associated Tool object. If the code is
	 * null or the storage does not contain the code, a
	 * warning message is displayed on the console and the
	 * method returns null.
	 */
	@Override
	public Tool getTool(Code code) {
		if (code == null) {
			System.out.println("The passed-in Code was null. No Tool could be retrieved.");
			return null;
		}
		
		if (!storage.containsKey(code)) {
			System.out.println(String.format("No Tool with code %s was found. Nothing to return.", code));
			return null;
		}
		
		// All pre-conditions met, retrieving Tool from the storage system
		return storage.get(code);
	}

	/**
	 * Removes the Tool associated with the passed-in code
	 * from the storage system. First, the method checks
	 * if the passed-in code is null. If not, then it
	 * checks if the storage contains the passed-in code.
	 * If so, it removes the associated Tool object. If
	 * the code is null or the storage does not contain
	 * the code, a warning message is displayed on the
	 * console and the method exits.
	 */
	@Override
	public void removeTool(Code code) {
		if (code == null) {
			System.out.println("The passed-in Code was null. No tool was removed from the storage system.");
			return;
		}
		
		if (!storage.containsKey(code)) {
			System.out.println(String.format("No Tool with code %s was found. Nothing to remove.", code));
			return;
		}
		
		// All pre-conditions met, removing Tool from the storage system
		storage.remove(code);
	}

	/**
	 * Update the Tool in the storage system that matches the passed-in code.
	 * The passed-in attribute is updated with the passed-in value for the
	 * Tool that was found. This update happens by using the tools setter
	 * methods, and then replacing the tool in the storage. If any
	 * pre-conditions are not met, the method does not perform any updates
	 * and the method exits.
	 * 
	 * Pre-conditions:
	 * 1) Passed-in objects are non-null
	 * 2) The associated Tool was found in the storage system
	 * 3) The passed-in attribute and value parameters are of matching Types
	 * 
	 * Note: If the passed-in value is not an instance of the passed-in attribute,
	 * then no update occurs and a warning message is displayed on the console.
	 */
	@Override
	public void updateTool(Code code, Attribute attribute, Object value) {
		if (code == null || attribute == null || value == null) {
			System.out.println("A passed-in parameter was null. Please pass in non-null parameters.");
			return;
		}
		
		Tool tool = storage.get(code);
		
		// Extra safety measure.
		// All Codes are associated with a tool placed in storage by default, so tool should
		// not be null. One scenario where this piece of code could execute is if a future
		// developer adds a new Tool Code to the ToolEnums file, but does not create an
		// associated Tool child class or does not update the constructor to include the new
		// Tool as part of the default insertions.
		if (tool == null) {
			System.out.println(String.format("No Tool with code %s was found. Nothing to update.", code));
			return;
		}
		
		// First 2 pre-conditions met. Moving on to check for
		// pre-condition #3; matching attribute and value parameter
		// types. If pre-condition #3 is met, that means a valid
		// attribute update is being requested, so update the tool.
		if (attribute.equals(Attribute.CODE)) {
			// Tool Code should not be able to get updated unless a future developer
			// has manually added a new Code to the ToolEnums file and has yet to
			// assign it any tool in storage.
			if (value instanceof Code && !storage.containsKey(value)) {
				tool.setCode((Code) value);
				storage.put((Code) value, tool);
				storage.remove(code); // Remove old tool entry reference from the storage
			} else {
				System.out.println("Invalid Code value passed in. Not updating tool.");
			}
		} else if (attribute.equals(Attribute.TYPE)) {
			if (value instanceof Type) {
				tool.setType((Type) value);
				storage.put(code, tool);
			} else {
				System.out.println("Invalid Type value passed in. Not updating tool.");
			}
		} else if (attribute.equals(Attribute.BRAND)) {
			if (value instanceof Brand) {
				tool.setBrand((Brand) value);
				storage.put(code, tool);
			} else {
				System.out.println("Invalid Brand value passed in. Not updating tool.");
			}
		}  else if (attribute.equals(Attribute.DAILYCHARGE)) {
			if (value instanceof Float) {
				tool.setDailyCharge((float) value);
				storage.put(code, tool);
			} else {
				System.out.println("Invalid Float value passed in. Not updating tool.");
			}
		} else if (attribute.equals(Attribute.CHARGEONWEEKDAYS)) {
			if (value instanceof Boolean) {
				tool.setChargeOnWeekdays((boolean) value);
				storage.put(code, tool);
			} else {
				System.out.println("Invalid Boolean value passed in. Not updating tool.");
			}
		} else if (attribute.equals(Attribute.CHARGEONWEEKENDS)) {
			if (value instanceof Boolean) {
				tool.setChargeOnWeekends((boolean) value);
				storage.put(code, tool);
			} else {
				System.out.println("Invalid Boolean value passed in. Not updating tool.");
			}
		} else if (attribute.equals(Attribute.CHARGEONHOLIDAYS)) {
			if (value instanceof Boolean) {
				tool.setChargeOnHolidays((boolean) value);
				storage.put(code, tool);
			} else {
				System.out.println("Invalid Boolean value passed in. Not updating tool.");
			}
		} else if (attribute.equals(Attribute.CHECKEDOUT)) {
			if (value instanceof Boolean) {
				tool.setCheckedOut((boolean) value);
				storage.put(code, tool);
			} else {
				System.out.println("Invalid Boolean value passed in. Not updating tool.");
			}
		} else {
			// Pre-condition #3 was not met, so no update happens
			System.out.println("Invalid Attribute passed in. Not updating tool.");
		}
	}

	/**
	 * Prints out a String representation of all the entries in the storage system.
	 * The entries are looped through and printed out one by one.
	 */
	@Override
	public void printStoredTools() {
		if (!storage.isEmpty()) {
			ArrayList<String> toolsList = new ArrayList<String>();
			Tool currentTool;
			for (Map.Entry<Code, Tool> entry : storage.entrySet()) {
				currentTool = entry.getValue();
				toolsList.add(String.format("Code: %s, Type: %s, Brand: %s\nDaily Charge: $%,.2f\nCharge On Weekdays: %s\nCharge on Weekends: %s\nCharge on Holidays: %s\nChecked Out: %s\n\n",
						entry.getKey(),
						currentTool.getType(),
						currentTool.getBrand(),
						currentTool.getDailyCharge(),
						currentTool.isChargeOnWeekdays() ? "Yes" : "No",
						currentTool.isChargeOnWeekends() ? "Yes" : "No",
						currentTool.isChargeOnHolidays() ? "Yes" : "No",
						currentTool.isCheckedOut() ? "Yes" : "No"));
			}
			
			Collections.sort(toolsList);
			System.out.println(String.join("", toolsList));
		} else {
			System.out.println("There is no valid storage system to print.");
		}
	}
}
