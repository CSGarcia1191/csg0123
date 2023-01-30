package rentatool.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import rentatool.rental_items.Chainsaw;
import rentatool.rental_items.Jackhammer;
import rentatool.rental_items.Ladder;
import rentatool.rental_items.Tool;
import rentatool.rental_items.ToolEnums.Attribute;
import rentatool.rental_items.ToolEnums.Brand;
import rentatool.rental_items.ToolEnums.Code;
import rentatool.rental_items.ToolEnums.Type;

/**
 * This TestSuite contains tests for the different methods available
 * on SQLiteDB instances. There will be 1 nested class per method.
 * 
 * The TestSuite will use both a @BeforeEach annotation and
 * an @AfterEach to initialize new StorageSystem instances
 * per test case so that there is a fresh, non-altered
 * StorageSystem used for accurate testing. The @AfterAll
 * will make use of the closeConnection() method to make
 * sure that any altered/existing tool tables are deleted.
 * 
 * The @BeforeEach hook will also be used to reassign standard
 * output to point to a test output stream variable called
 * "outputTestStream", and the @AfterAll hook will then be used
 * to restore standard out's original reference.
 * 
 * @author CSGarcia1191
 *
 */
@DisplayName("SQLiteDB Tests")
class JUnitSQLiteDBTests {

	static SQLiteDB mySQLiteDB; // StorageSystem initialized per test
	
	private final PrintStream standardOutStream = System.out; // Save current System.out reference so it can be restored after each test
	private final ByteArrayOutputStream outputTestStream = new ByteArrayOutputStream(); // Output stream to use for testing
	
	// Before each test case:
	// 1) Create a new HashMap<Code, Tool> storage system
	// 2) Reassign the standard output to the test output stream
	@BeforeEach
	void prepStorageAndSystemOut() {
		mySQLiteDB = new SQLiteDB();
		System.setOut(new PrintStream(outputTestStream));
	}
	
	// Restore the standard output to the original System.out stream after each test
	@AfterEach
	void restoreSystemOut() {
		mySQLiteDB.closeConnection();
		System.setOut(new PrintStream(standardOutStream));
	}
	
	@Nested
	@DisplayName("Database Specific Tests")
	class DatabaseSpecificTests {
		@Test
		@DisplayName("Create Default Table Test")
		void createDefaultTableTest() {
			// Table should already be created by @BeforeEach hook. Use Assertions to assert that each default tool exists.
			assertNotNull(mySQLiteDB.getTool(Code.CHNS));
			assertNotNull(mySQLiteDB.getTool(Code.JAKD));
			assertNotNull(mySQLiteDB.getTool(Code.JAKR));
			assertNotNull(mySQLiteDB.getTool(Code.LADW));
			
			// Assert that the existing tool table is overridden by calling the createTable() method.
			// The current table contains the CHNS and JAKD tools (per above assertions). Remove them
			// from the table, assume the removals were successful, call createTable(), and then
			// assert that the tools exist again in the database table.
			mySQLiteDB.removeTool(Code.CHNS);
			mySQLiteDB.removeTool(Code.JAKD);
			assumeTrue(mySQLiteDB.getTool(Code.CHNS) == null);
			assumeTrue(mySQLiteDB.getTool(Code.JAKD) == null);
			
			// Run assertions
			mySQLiteDB.createTable();
			assertNotNull(mySQLiteDB.getTool(Code.CHNS));
			assertNotNull(mySQLiteDB.getTool(Code.JAKD));
		}
		
		@Test
		@DisplayName("Delete Table Test")
		void deleteTableTest() {
			mySQLiteDB.deleteTable();
			assertNull(mySQLiteDB.getTool(Code.CHNS));
			assertEquals("There was an issue retrieving the tool from the database table.", outputTestStream.toString().trim());
			outputTestStream.reset();
			mySQLiteDB.printStoredTools();
			assertEquals("There was an issue printing the database table.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Close Connection Test")
		void closeConnectionTest() {
			// Close connection and run assertions
			mySQLiteDB.closeConnection();
			
			mySQLiteDB.createTable();
			assertEquals("There is currently no valid database connection. Could not create table.", outputTestStream.toString().trim());
			outputTestStream.reset();
			
			mySQLiteDB.deleteTable();
			assertEquals("There is currently no valid database connection. Could not delete table.", outputTestStream.toString().trim());
			outputTestStream.reset();
			
			// Arbitrary Tool arguments passed into CRUD instance methods to test that
			// operations could not happen because the connection is in fact closed.
			
			// Assert addition could not happen
			mySQLiteDB.addTool(new Chainsaw(Code.CHNS, Brand.WERNER));
			assertEquals("There is currently no valid database connection. Could not add tool to the database table.", outputTestStream.toString().trim());
			outputTestStream.reset();
			
			// Assert retrieval could not happen
			mySQLiteDB.getTool(Code.JAKD);
			assertEquals("There is currently no valid database connection. Could not retrieve tool from the database table.", outputTestStream.toString().trim());
			outputTestStream.reset();
			
			// Assert update could not happen
			mySQLiteDB.updateTool(Code.JAKR, Attribute.CHECKEDOUT, true);
			assertEquals("There is currently no valid database connection. Could not update tool in the database table.", outputTestStream.toString().trim());
			outputTestStream.reset();
			
			// Assert removal could not happen
			mySQLiteDB.removeTool(Code.JAKD);
			assertEquals("There is currently no valid database connection. Could not remove tool from the database table.", outputTestStream.toString().trim());
		}
	}
		
	@Nested
	@DisplayName("Add Tool Tests")
	class AddToolTests {
		// method signature: addTool(Tool tool)
		
		@Test
		@DisplayName("Null Tool")
		void addNullTool() {
			mySQLiteDB.addTool(null);
			assertEquals("The passed-in Tool object was null. No tool was added to the database table.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Existent Tool")
		void addExistentTool() {
			Tool testTool = mySQLiteDB.getTool(Code.CHNS);
			// Assume that the Tool with code CHNS exists in storage. If not, then skip this test.
			assumeTrue(testTool != null);
			
			// Run assertion
			mySQLiteDB.addTool(testTool);
			assertEquals("A tool with code CHNS already exists.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Add Tool Happy Path")
		void addToolHappyPath() {
			// Assume that the removal of default Tool with code JAKR was successful. If not, then skip this test.
			mySQLiteDB.removeTool(Code.JAKR);
			assumeTrue(mySQLiteDB.getTool(Code.JAKR) == null);
			
			// Happy path, run assertions
			// Note: Tool equals() method is implemented
			Tool testJackhammer = new Jackhammer(Code.JAKR, Brand.RIDGID);
			mySQLiteDB.addTool(testJackhammer);
			assertNotNull(mySQLiteDB.getTool(Code.JAKR));
			assertEquals(testJackhammer, mySQLiteDB.getTool(Code.JAKR));
		}
	}
	
	@Nested
	@DisplayName("Get Tool Tests")
	class GetToolTests {
		// method signature: getTool(Code code)
		
		@Test
		@DisplayName("Null Tool Code")
		void getNullTool() {
			assertNull(mySQLiteDB.getTool(null));
			assertEquals("The passed-in Code was null. No Tool could be retrieved.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Non-Existent Tool")
		void getNonExistentTool() {
			// Assume Tool with code JAKD exists in storage. If it does not, then skip this test.
			assumeTrue(mySQLiteDB.getTool(Code.JAKD) != null);
			mySQLiteDB.removeTool(Code.JAKD);
			
			// Run assertions
			assertNull(mySQLiteDB.getTool(Code.JAKD));
			assertEquals("No matching Tool found with Code JAKD.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Get Tool Happy Path")
		void getToolHappyPath() {
			Tool storedLadder = mySQLiteDB.getTool(Code.LADW); // should have default Ladder property values
			// Assume Tool with code LADW exists in storage. If it does not, then skip this test.
			assumeTrue(storedLadder != null);
			
			// Run assertion
			// Note: Tool equals() method is implemented
			assertEquals(new Ladder(Code.LADW, Brand.WERNER), storedLadder); // confirm tools are the same
		}
	}
	
	@Nested
	@DisplayName("Remove Tool Tests")
	class RemoveToolTests {
		// method signature: removeTool(Code code)
		
		@Test
		@DisplayName("Null Tool Code")
		void removeNullTool() {
			mySQLiteDB.removeTool(null);
			assertEquals("The passed-in Code was null. No tool was removed from the database table.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Non-Existent Tool")
		void removeNonExistentTool() {
			// Use Assumptions to assume that a tool previously existed in storage.
			// If an assumption is not true, then skip this test.
			assumeTrue(mySQLiteDB.getTool(Code.CHNS) != null);
			mySQLiteDB.removeTool(Code.CHNS);
			assumeTrue(mySQLiteDB.getTool(Code.CHNS) == null);
			
			// Run assertion
			outputTestStream.reset(); // discards the output from calling mySQLiteDB.getTool(Code.CHNS) on a non-existent tool
			mySQLiteDB.removeTool(Code.CHNS);
			assertEquals("No Tool with code CHNS was found. Nothing to remove.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Remove Tool Happy Path")
		void removeToolHappyPath() {
			// Assume Tool with code CHNS exists in storage. If it does not, then skip this test.
			assumeTrue(mySQLiteDB.getTool(Code.CHNS) != null);
			
			// Run assertion
			mySQLiteDB.removeTool(Code.CHNS);
			assertTrue(mySQLiteDB.getTool(Code.CHNS) == null);
		}
	}
	
	@Nested
	@DisplayName("Update Tool Attribute Tests")
	class UpdateToolAttributeTests {
		// method signature: updateTool(Code code, Attribute attribute, Object value)
		
		@Test
		@DisplayName("All Null Arguments Update")
		void updateWithAllNullArgs() {
			mySQLiteDB.updateTool(null, null, null);
			assertEquals("A passed-in parameter was null. Please pass in non-null parameters.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Some Null Arguments Update")
		void updateWithSomeNullArgs() {
			mySQLiteDB.updateTool(null, Attribute.CODE, null);
			assertEquals("A passed-in parameter was null. Please pass in non-null parameters.", outputTestStream.toString().trim());
		}
		
		@Nested
		@DisplayName("Update Code Attribute")
		class UpdateCodeAttribute {
			@Test
			@DisplayName("With Existing Code")
			void withExisting() {
				mySQLiteDB.updateTool(Code.JAKR, Attribute.CODE, Code.JAKD);			
				assertEquals("There was an issue updating the tool in the database table.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Non-Code")
			void withNonCode() {
				mySQLiteDB.updateTool(Code.JAKR, Attribute.CODE, "InvalidCodeTest");			
				assertEquals("Invalid Code value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Same Code")
			void withSameCode() {
				mySQLiteDB.updateTool(Code.JAKR, Attribute.CODE, Code.JAKR);			
				assertEquals("", outputTestStream.toString().trim());
			}
		}
		
		@Nested
		@DisplayName("Update Type Attribute")
		class UpdateTypeAttribute {
			@Test
			@DisplayName("With Non-Type")
			void withNonType() {
				mySQLiteDB.updateTool(Code.JAKD, Attribute.TYPE, "InvalidTypeTest");			
				assertEquals("Invalid Type value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Valid Value")
			void withValidType() {
				Tool testJackhammer = mySQLiteDB.getTool(Code.JAKD);
				// Assume Tool with code JAKD exists in storage and that its type is JACKHAMMER.
				// If not, then skip this test.
				assumeTrue(testJackhammer != null);
				assumeTrue(testJackhammer.getType() == Type.JACKHAMMER);

				// Run assertion
				mySQLiteDB.updateTool(Code.JAKD, Attribute.TYPE, Type.CHAINSAW); // updates Tool in database
				assertEquals(Type.CHAINSAW, mySQLiteDB.getTool(Code.JAKD).getType()); // confirm update is reflected in database
			}
		}
		
		@Nested
		@DisplayName("Update Brand Attribute")
		class UpdateBrandAttribute {
			@Test
			@DisplayName("With Non-Brand")
			void withNonBrand() {
				mySQLiteDB.updateTool(Code.CHNS, Attribute.BRAND, Type.LADDER);			
				assertEquals("Invalid Brand value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Valid Value")
			void withValidBrand() {
				Tool testChainsaw = mySQLiteDB.getTool(Code.CHNS);
				// Assume Tool with code CHNS exists in storage and that its brand is STIHL.
				// If not, then skip this test.
				assumeTrue(testChainsaw != null);
				assumeTrue(testChainsaw.getBrand() == Brand.STIHL);

				// Run assertion
				mySQLiteDB.updateTool(Code.CHNS, Attribute.BRAND, Brand.DEWALT); // updates Tool in database
				assertEquals(Brand.DEWALT, mySQLiteDB.getTool(Code.CHNS).getBrand()); // confirm update is reflected in database
			}
		}
		
		@Nested
		@DisplayName("Update DailyCharge Attribute")
		class UpdateDailyChargeAttribute {
			@Test
			@DisplayName("With Invalid Value")
			void withInvalidValue() {
				mySQLiteDB.updateTool(Code.JAKR, Attribute.DAILYCHARGE, 1);			
				assertEquals("Invalid Float value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Valid Value")
			void withValidValue() {
				Tool testChainsaw = mySQLiteDB.getTool(Code.CHNS);
				// Assume Tool with code CHNS exists in storage and that its dailyCharge is 1.49.
				// If not, then skip this test.
				assumeTrue(testChainsaw != null);
				assumeTrue(testChainsaw.getDailyCharge() == 1.49f);

				// Run assertion
				mySQLiteDB.updateTool(Code.CHNS, Attribute.DAILYCHARGE, 1.23f); // updates Tool in database
				assertEquals(1.23f, mySQLiteDB.getTool(Code.CHNS).getDailyCharge()); // confirm update is reflected in database
			}
		}
		
		@Nested
		@DisplayName("Update Charge On Weekdays Attribute")
		class UpdateChargeOnWeekdaysAttribute {
			@Test
			@DisplayName("With Invalid Value")
			void withInvalidValue() {
				mySQLiteDB.updateTool(Code.LADW, Attribute.CHARGEONWEEKDAYS, "false");		
				assertEquals("Invalid Boolean value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Valid Value")
			void withValidValue() {
				Tool testLadder = mySQLiteDB.getTool(Code.LADW);
				// Assume Tool with code LADW exists in storage and that its chargeOnWeekdays is true.
				// If not, then skip this test.
				assumeTrue(testLadder != null);
				assumeTrue(testLadder.isChargeOnWeekdays() == true);

				// Run assertion
				mySQLiteDB.updateTool(Code.LADW, Attribute.CHARGEONWEEKDAYS, false); // updates Tool in database
				assertFalse(mySQLiteDB.getTool(Code.LADW).isChargeOnWeekdays()); // confirm update is reflected in database
			}
		}
		
		@Nested
		@DisplayName("Update Charge On Weekends Attribute")
		class UpdateChargeOnWeekendsAttribute {
			@Test
			@DisplayName("With Invalid Value")
			void withInvalidValue() {
				mySQLiteDB.updateTool(Code.LADW, Attribute.CHARGEONWEEKENDS, 12345);		
				assertEquals("Invalid Boolean value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Valid Value")
			void withValidValue() {
				Tool testLadder = mySQLiteDB.getTool(Code.LADW);
				// Assume Tool with code LADW exists in storage and that its chargeOnWeekends is true.
				// If not, then skip this test.
				assumeTrue(testLadder != null);
				assumeTrue(testLadder.isChargeOnWeekends() == true);

				// Run assertion
				mySQLiteDB.updateTool(Code.LADW, Attribute.CHARGEONWEEKENDS, false); // updates Tool in database
				assertFalse(mySQLiteDB.getTool(Code.LADW).isChargeOnWeekends()); // confirm update is reflected in database
			}
		}
		
		@Nested
		@DisplayName("Update Charge On Holidays Attribute")
		class UpdateChargeOnHolidaysAttribute {
			@Test
			@DisplayName("With Invalid Value")
			void withInvalidValue() {
				mySQLiteDB.updateTool(Code.LADW, Attribute.CHARGEONHOLIDAYS, "true");		
				assertEquals("Invalid Boolean value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Valid Value")
			void withValidValue() {
				Tool testLadder = mySQLiteDB.getTool(Code.LADW);
				// Assume Tool with code LADW exists in storage and that its chargeOnHolidays is false.
				// If not, then skip this test.
				assumeTrue(testLadder != null);
				assumeTrue(testLadder.isChargeOnHolidays() == false);

				// Run assertion
				mySQLiteDB.updateTool(Code.LADW, Attribute.CHARGEONHOLIDAYS, true); // updates Tool in database
				assertTrue(mySQLiteDB.getTool(Code.LADW).isChargeOnHolidays()); // confirm update is reflected in database
			}
		}
		
		@Nested
		@DisplayName("Update Checked Out Attribute")
		class UpdateCheckedOutAttribute {
			@Test
			@DisplayName("With Invalid Value")
			void withInvalidValue() {
				mySQLiteDB.updateTool(Code.LADW, Attribute.CHECKEDOUT, "true");		
				assertEquals("Invalid Boolean value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Valid Value")
			void withValidValue() {
				Tool testLadder = mySQLiteDB.getTool(Code.LADW);
				// Assume Tool with code LADW exists in storage and that its checkedOut is false.
				// If not, then skip this test.
				assumeTrue(testLadder != null);
				assumeTrue(testLadder.isCheckedOut() == false);

				// Run assertion
				mySQLiteDB.updateTool(Code.LADW, Attribute.CHECKEDOUT, true); // updates Tool in database
				assertTrue(mySQLiteDB.getTool(Code.LADW).isCheckedOut()); // confirm update is reflected in database
			}
		}
	}
	
	@Nested
	@DisplayName("Print Stored Tools Tests")
	class PrintToolTests {
		// method signature: printStoredTools()
		
		@Test
		@DisplayName("Default Stored Tools")
		void defaultStoredTools() {
			StringBuilder testStr = new StringBuilder();
			testStr.append("Code: CHNS, Type: Chainsaw, Brand: Stihl\nDaily Charge: $1.49\nCharge On Weekdays: Yes\nCharge on Weekends: No\nCharge on Holidays: Yes\nChecked Out: No\n\n");
			testStr.append("Code: JAKD, Type: Jackhammer, Brand: DeWalt\nDaily Charge: $2.99\nCharge On Weekdays: Yes\nCharge on Weekends: No\nCharge on Holidays: No\nChecked Out: No\n\n");
			testStr.append("Code: JAKR, Type: Jackhammer, Brand: Ridgid\nDaily Charge: $2.99\nCharge On Weekdays: Yes\nCharge on Weekends: No\nCharge on Holidays: No\nChecked Out: No\n\n");
			testStr.append("Code: LADW, Type: Ladder, Brand: Werner\nDaily Charge: $1.99\nCharge On Weekdays: Yes\nCharge on Weekends: Yes\nCharge on Holidays: No\nChecked Out: No");
			mySQLiteDB.printStoredTools();
			assertEquals(testStr.toString(), outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Some Tools Removed")
		void sometoolsRemoved() {
			// Assume JAKD and LADW tools exist in storage before removing them and printing the remaining stored tools
			assumeTrue(mySQLiteDB.getTool(Code.JAKD) != null);
			assumeTrue(mySQLiteDB.getTool(Code.LADW) != null);
			mySQLiteDB.removeTool(Code.JAKD);
			mySQLiteDB.removeTool(Code.LADW);
			
			// Run assertion
			StringBuilder testStr = new StringBuilder();
			testStr.append("Code: CHNS, Type: Chainsaw, Brand: Stihl\nDaily Charge: $1.49\nCharge On Weekdays: Yes\nCharge on Weekends: No\nCharge on Holidays: Yes\nChecked Out: No\n\n");
			testStr.append("Code: JAKR, Type: Jackhammer, Brand: Ridgid\nDaily Charge: $2.99\nCharge On Weekdays: Yes\nCharge on Weekends: No\nCharge on Holidays: No\nChecked Out: No");
			mySQLiteDB.printStoredTools();
			assertEquals(testStr.toString(), outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Some Tool Defaults Updated")
		void sometoolDefaultsUpdated() {
			// Assume CHNS and JAKR tools exist in storage before updating them and printing the remaining stored tools
			assumeTrue(mySQLiteDB.getTool(Code.CHNS) != null);
			assumeTrue(mySQLiteDB.getTool(Code.JAKR) != null);
			mySQLiteDB.updateTool(Code.CHNS, Attribute.TYPE, Type.LADDER); // changing TYPE from Chainsaw to Ladder
			mySQLiteDB.updateTool(Code.JAKR, Attribute.DAILYCHARGE, 200f); // changing DAILYCHARGE from 2.99 to 200
			
			// Run assertion
			StringBuilder testStr = new StringBuilder();
			testStr.append("Code: CHNS, Type: Ladder, Brand: Stihl\nDaily Charge: $1.49\nCharge On Weekdays: Yes\nCharge on Weekends: No\nCharge on Holidays: Yes\nChecked Out: No\n\n");
			testStr.append("Code: JAKD, Type: Jackhammer, Brand: DeWalt\nDaily Charge: $2.99\nCharge On Weekdays: Yes\nCharge on Weekends: No\nCharge on Holidays: No\nChecked Out: No\n\n");
			testStr.append("Code: JAKR, Type: Jackhammer, Brand: Ridgid\nDaily Charge: $200.00\nCharge On Weekdays: Yes\nCharge on Weekends: No\nCharge on Holidays: No\nChecked Out: No\n\n");
			testStr.append("Code: LADW, Type: Ladder, Brand: Werner\nDaily Charge: $1.99\nCharge On Weekdays: Yes\nCharge on Weekends: Yes\nCharge on Holidays: No\nChecked Out: No");
			mySQLiteDB.printStoredTools();
			assertEquals(testStr.toString(), outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Empty Storage")
		void emptyStorage() {
			// Assume the 4 default tools exist in storage before removing them all and calling printStoredTools() on the empty storage
			assumeTrue(mySQLiteDB.getTool(Code.CHNS) != null);
			assumeTrue(mySQLiteDB.getTool(Code.JAKD) != null);
			assumeTrue(mySQLiteDB.getTool(Code.JAKR) != null);
			assumeTrue(mySQLiteDB.getTool(Code.LADW) != null);
			mySQLiteDB.removeTool(Code.CHNS);
			mySQLiteDB.removeTool(Code.JAKD);
			mySQLiteDB.removeTool(Code.JAKR);
			mySQLiteDB.removeTool(Code.LADW);
			
			// Run assertion
			mySQLiteDB.printStoredTools();
			assertEquals("", outputTestStream.toString().trim());
		}
	}
}
