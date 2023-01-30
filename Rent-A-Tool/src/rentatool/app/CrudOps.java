package rentatool.app;

import rentatool.rental_items.Tool;
import rentatool.rental_items.ToolEnums.Attribute;
import rentatool.rental_items.ToolEnums.Code;

/**
 * This interface was created to allow the application to use CRUD
 * operations on instances of the different types of tool storage
 * systems available i.e. the HashMapStore and the SQLiteDB
 * classes. These operations should also be accessible by all
 * other classes outside of the app package. For example, if
 * this project is imported by another java project that wants
 * to create its own storage system for the Rent-A-Tool app,
 * that project should be able to access this interface to
 * implement the CRUD operations used through the application.
 * 
 * When running the application, a new instance of a class that implements
 * this interface is initialized and passed into the application. This
 * instance (current default is SQLiteDB instance) is the storage that
 * gets used during program execution.
 *  
 * @author CSGarcia1191
 *
 */
public interface CrudOps {
	
	public void addTool(Tool tool); // create
	public Tool getTool(Code code); // read
	public void updateTool(Code code, Attribute attr, Object value); // update
	public void removeTool(Code code); // delete
	
}
