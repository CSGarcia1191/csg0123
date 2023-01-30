package rentatool.app;

/**
 * Making this class an interface, as opposed to abstract, lets the
 * application instantiate reference variables of StorageSystem
 * type, but not initialize them without a call to a constructor of
 * a child instance class (HashMapStorage, SQLiteDB). This
 * interface also extends the CrudOps operations to allow for
 * CRUD operations on StorageSystem child class instances.
 * 
 * @author CSGarcia1191
 *
 */
public interface StorageSystem extends CrudOps {
	
	// print whole storage system
	public void printStoredTools();
	
}
