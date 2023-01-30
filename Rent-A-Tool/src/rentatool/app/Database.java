package rentatool.app;

/**
 * An abstract class that lays out the blueprint for any
 * type of database that may be integrated with the
 * Rent-A-Tool system. Currently, the app has a custom
 * SQLite database class named SQLiteDB which gets used
 * by default as the storage system of choice. SQLiteDB
 * is a child of this abstract Database class.
 * 
 * @author CSGarcia1191
 *
 */
abstract class Database implements StorageSystem {

	// These methods should only be available to
	// classes within the storage package for
	// security. Only child classes should
	// be able to invoke these methods.
	abstract void createTable();
	abstract void deleteTable();
	abstract void closeConnection();
	
}
