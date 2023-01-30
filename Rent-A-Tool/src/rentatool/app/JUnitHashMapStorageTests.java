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

import rentatool.app.HashMapStorage;
import rentatool.rental_items.Jackhammer;
import rentatool.rental_items.Ladder;
import rentatool.rental_items.Tool;
import rentatool.rental_items.ToolEnums.Attribute;
import rentatool.rental_items.ToolEnums.Brand;
import rentatool.rental_items.ToolEnums.Code;
import rentatool.rental_items.ToolEnums.Type;


/**
 * This TestSuite contains tests for the different methods available
 * on HashMapStorage instances. There will be 1 nested class
 * per method.
 * 
 * The TestSuite will use a @BeforeEach annotation to initialize
 * a new StorageSystem instance per test case so that there is
 * a fresh, non-altered StorageSystem used for accurate testing.
 * 
 * The @BeforeEach hook will also be used to reassign standard
 * output to point to a test output stream variable called
 * "outputTestStream", and the @AfterAll hook will then be used
 * to restore standard out's original reference.
 * 
 * @author CSGarcia1191
 *
 */
@DisplayName("HashMapStorage Tests")
class JUnitHashMapStorageTests {
	
	static HashMapStorage myHashMapStorage; // StorageSystem initialized per test
	
	private final PrintStream standardOutStream = System.out; // Save current System.out reference so it can be restored after each test
	private final ByteArrayOutputStream outputTestStream = new ByteArrayOutputStream(); // Output stream to use for testing
	
	// Before each test case:
	// 1) Create a new HashMap<Code, Tool> storage system
	// 2) Reassign the standard output to the test output stream
	@BeforeEach
	void prepStorageAndSystemOut() {
		myHashMapStorage = new HashMapStorage();
		System.setOut(new PrintStream(outputTestStream));
	}
	
	// Restore the standard output to the original System.out stream after each test
	@AfterEach
	void restoreSystemOut() {
		System.setOut(new PrintStream(standardOutStream));
	}
	
	// Test that storage instances get created with the default tools
	@Test
	@DisplayName("Default Storage Created")
	void defaultStorageCreated() {
		assertNotNull(myHashMapStorage.getTool(Code.CHNS));
		assertNotNull(myHashMapStorage.getTool(Code.JAKD));
		assertNotNull(myHashMapStorage.getTool(Code.JAKR));
		assertNotNull(myHashMapStorage.getTool(Code.LADW));
	}
	
	@Nested
	@DisplayName("Add Tool Tests")
	class AddToolTests {
		// method signature: addTool(Tool tool)
		
		@Test
		@DisplayName("Null Tool")
		void addNullTool() {
			myHashMapStorage.addTool(null);
			assertEquals("The passed-in Tool object was null. No tool was added to the storage system.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Existent Tool")
		void addExistentTool() {
			Tool testTool = myHashMapStorage.getTool(Code.CHNS);
			// Assume that the Tool with code CHNS exists in storage. If not, then skip this test.
			assumeTrue(testTool != null);
			
			// Run assertion
			myHashMapStorage.addTool(testTool);
			assertEquals("A tool with code CHNS already exists.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Add Tool Happy Path")
		void addToolHappyPath() {
			// Assume that the removal of default Tool with code JAKR was successful. If not, then skip this test.
			myHashMapStorage.removeTool(Code.JAKR);
			assumeTrue(myHashMapStorage.getTool(Code.JAKR) == null);
			
			// Happy path, run assertions
			// Note: Tool equals() method is implemented
			Tool testJackhammer = new Jackhammer(Code.JAKR, Brand.RIDGID);
			myHashMapStorage.addTool(testJackhammer);
			assertNotNull(myHashMapStorage.getTool(Code.JAKR));
			assertEquals(testJackhammer, myHashMapStorage.getTool(Code.JAKR));
		}
	}
	
	@Nested
	@DisplayName("Get Tool Tests")
	class GetToolTests {
		// method signature: getTool(Code code)
		
		@Test
		@DisplayName("Null Tool Code")
		void getNullTool() {
			assertNull(myHashMapStorage.getTool(null));
			assertEquals("The passed-in Code was null. No Tool could be retrieved.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Non-Existent Tool")
		void getNonExistentTool() {
			// Assume Tool with code JAKD exists in storage. If it does not, then skip this test.
			assumeTrue(myHashMapStorage.getTool(Code.JAKD) != null);
			myHashMapStorage.removeTool(Code.JAKD);
			
			// Run assertions
			assertNull(myHashMapStorage.getTool(Code.JAKD));
			assertEquals("No Tool with code JAKD was found. Nothing to return.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Get Tool Happy Path")
		void getToolHappyPath() {
			Tool storedLadder = myHashMapStorage.getTool(Code.LADW); // should have default Ladder property values
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
			myHashMapStorage.removeTool(null);
			assertEquals("The passed-in Code was null. No tool was removed from the storage system.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Non-Existent Tool")
		void removeNonExistentTool() {
			// Use Assumptions to assume that a tool previously existed in storage.
			// If an assumption is not true, then skip this test.
			assumeTrue(myHashMapStorage.getTool(Code.CHNS) != null);
			myHashMapStorage.removeTool(Code.CHNS);
			assumeTrue(myHashMapStorage.getTool(Code.CHNS) == null);
			
			// Run assertion
			outputTestStream.reset(); // discards the output from calling myHashMapStorage.getTool(Code.CHNS) on a non-existent tool
			myHashMapStorage.removeTool(Code.CHNS);
			assertEquals("No Tool with code CHNS was found. Nothing to remove.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Remove Tool Happy Path")
		void removeToolHappyPath() {
			// Assume Tool with code CHNS exists in storage. If it does not, then skip this test.
			assumeTrue(myHashMapStorage.getTool(Code.CHNS) != null);
			
			// Run assertion
			myHashMapStorage.removeTool(Code.CHNS);
			assertTrue(myHashMapStorage.getTool(Code.CHNS) == null);
		}
	}
	
	@Nested
	@DisplayName("Update Tool Attribute Tests")
	class UpdateToolAttributeTests {
		// method signature: updateTool(Code code, Attribute attribute, Object value)
		
		@Test
		@DisplayName("All Null Arguments Update")
		void updateWithAllNullArgs() {
			myHashMapStorage.updateTool(null, null, null);
			assertEquals("A passed-in parameter was null. Please pass in non-null parameters.", outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Some Null Arguments Update")
		void updateWithSomeNullArgs() {
			myHashMapStorage.updateTool(null, Attribute.CODE, null);
			assertEquals("A passed-in parameter was null. Please pass in non-null parameters.", outputTestStream.toString().trim());
		}
		
		@Nested
		@DisplayName("Update Code Attribute")
		class UpdateCodeAttribute {
			@Test
			@DisplayName("With Existing Code")
			void withExisting() {
				myHashMapStorage.updateTool(Code.JAKR, Attribute.CODE, Code.JAKD);			
				assertEquals("Invalid Code value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Non-Code")
			void withNonCode() {
				myHashMapStorage.updateTool(Code.JAKR, Attribute.CODE, "InvalidCodeTest");			
				assertEquals("Invalid Code value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Same Code")
			void withSameCode() {
				myHashMapStorage.updateTool(Code.JAKR, Attribute.CODE, Code.JAKR);			
				assertEquals("Invalid Code value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
		}
		
		@Nested
		@DisplayName("Update Type Attribute")
		class UpdateTypeAttribute {
			@Test
			@DisplayName("With Non-Type")
			void withNonType() {
				myHashMapStorage.updateTool(Code.JAKD, Attribute.TYPE, "InvalidTypeTest");			
				assertEquals("Invalid Type value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Valid Value")
			void withValidType() {
				Tool testJackhammer = myHashMapStorage.getTool(Code.JAKD);
				// Assume Tool with code JAKD exists in storage and that its type is JACKHAMMER.
				// If not, then skip this test.
				assumeTrue(testJackhammer != null);
				assumeTrue(testJackhammer.getType() == Type.JACKHAMMER);

				// Run assertion
				myHashMapStorage.updateTool(Code.JAKD, Attribute.TYPE, Type.CHAINSAW); // update Tool reference
				assertEquals(Type.CHAINSAW, testJackhammer.getType());
			}
		}
		
		@Nested
		@DisplayName("Update Brand Attribute")
		class UpdateBrandAttribute {
			@Test
			@DisplayName("With Non-Brand")
			void withNonBrand() {
				myHashMapStorage.updateTool(Code.CHNS, Attribute.BRAND, Type.LADDER);			
				assertEquals("Invalid Brand value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Valid Value")
			void withValidBrand() {
				Tool testChainsaw = myHashMapStorage.getTool(Code.CHNS);
				// Assume Tool with code CHNS exists in storage and that its brand is STIHL.
				// If not, then skip this test.
				assumeTrue(testChainsaw != null);
				assumeTrue(testChainsaw.getBrand() == Brand.STIHL);

				// Run assertion
				myHashMapStorage.updateTool(Code.CHNS, Attribute.BRAND, Brand.DEWALT); // update Tool reference
				assertEquals(Brand.DEWALT, testChainsaw.getBrand());
			}
		}
		
		@Nested
		@DisplayName("Update DailyCharge Attribute")
		class UpdateDailyChargeAttribute {
			@Test
			@DisplayName("With Invalid Value")
			void withInvalidValue() {
				myHashMapStorage.updateTool(Code.JAKR, Attribute.DAILYCHARGE, 1);			
				assertEquals("Invalid Float value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Valid Value")
			void withValidValue() {
				Tool testChainsaw = myHashMapStorage.getTool(Code.CHNS);
				// Assume Tool with code CHNS exists in storage and that its dailyCharge is 1.49.
				// If not, then skip this test.
				assumeTrue(testChainsaw != null);
				assumeTrue(testChainsaw.getDailyCharge() == 1.49f);

				// Run assertion
				myHashMapStorage.updateTool(Code.CHNS, Attribute.DAILYCHARGE, 1.23f); // update Tool reference
				assertEquals(1.23f, testChainsaw.getDailyCharge());
			}
		}
		
		@Nested
		@DisplayName("Update Charge On Weekdays Attribute")
		class UpdateChargeOnWeekdaysAttribute {
			@Test
			@DisplayName("With Invalid Value")
			void withInvalidValue() {
				myHashMapStorage.updateTool(Code.LADW, Attribute.CHARGEONWEEKDAYS, "false");		
				assertEquals("Invalid Boolean value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Valid Value")
			void withValidValue() {
				Tool testLadder = myHashMapStorage.getTool(Code.LADW);
				// Assume Tool with code LADW exists in storage and that its chargeOnWeekdays is true.
				// If not, then skip this test.
				assumeTrue(testLadder != null);
				assumeTrue(testLadder.isChargeOnWeekdays() == true);

				// Run assertion
				myHashMapStorage.updateTool(Code.LADW, Attribute.CHARGEONWEEKDAYS, false); // update Tool reference
				assertFalse(testLadder.isChargeOnWeekdays());
			}
		}
		
		@Nested
		@DisplayName("Update Charge On Weekends Attribute")
		class UpdateChargeOnWeekendsAttribute {
			@Test
			@DisplayName("With Invalid Value")
			void withInvalidValue() {
				myHashMapStorage.updateTool(Code.LADW, Attribute.CHARGEONWEEKENDS, 12345);		
				assertEquals("Invalid Boolean value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Valid Value")
			void withValidValue() {
				Tool testLadder = myHashMapStorage.getTool(Code.LADW);
				// Assume Tool with code LADW exists in storage and that its chargeOnWeekends is true.
				// If not, then skip this test.
				assumeTrue(testLadder != null);
				assumeTrue(testLadder.isChargeOnWeekends() == true);

				// Run assertion
				myHashMapStorage.updateTool(Code.LADW, Attribute.CHARGEONWEEKENDS, false); // update Tool reference
				assertFalse(testLadder.isChargeOnWeekends());
			}
		}
		
		@Nested
		@DisplayName("Update Charge On Holidays Attribute")
		class UpdateChargeOnHolidaysAttribute {
			@Test
			@DisplayName("With Invalid Value")
			void withInvalidValue() {
				myHashMapStorage.updateTool(Code.LADW, Attribute.CHARGEONHOLIDAYS, "true");		
				assertEquals("Invalid Boolean value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Valid Value")
			void withValidValue() {
				Tool testLadder = myHashMapStorage.getTool(Code.LADW);
				// Assume Tool with code LADW exists in storage and that its chargeOnHolidays is false.
				// If not, then skip this test.
				assumeTrue(testLadder != null);
				assumeTrue(testLadder.isChargeOnHolidays() == false);

				// Run assertion
				myHashMapStorage.updateTool(Code.LADW, Attribute.CHARGEONHOLIDAYS, true); // update Tool reference
				assertTrue(testLadder.isChargeOnHolidays());
			}
		}
		
		@Nested
		@DisplayName("Update Checked Out Attribute")
		class UpdateCheckedOutAttribute {
			@Test
			@DisplayName("With Invalid Value")
			void withInvalidValue() {
				myHashMapStorage.updateTool(Code.LADW, Attribute.CHECKEDOUT, "true");		
				assertEquals("Invalid Boolean value passed in. Not updating tool.", outputTestStream.toString().trim());
			}
			
			@Test
			@DisplayName("With Valid Value")
			void withValidValue() {
				Tool testLadder = myHashMapStorage.getTool(Code.LADW);
				// Assume Tool with code LADW exists in storage and that its checkedOut is false.
				// If not, then skip this test.
				assumeTrue(testLadder != null);
				assumeTrue(testLadder.isCheckedOut() == false);

				// Run assertion
				myHashMapStorage.updateTool(Code.LADW, Attribute.CHECKEDOUT, true); // update Tool reference
				assertTrue(testLadder.isCheckedOut());
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
			myHashMapStorage.printStoredTools();
			assertEquals(testStr.toString(), outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Some Tools Removed")
		void sometoolsRemoved() {
			// Assume JAKD and LADW tools exist in storage before removing them and printing the remaining stored tools
			assumeTrue(myHashMapStorage.getTool(Code.JAKD) != null);
			assumeTrue(myHashMapStorage.getTool(Code.LADW) != null);
			myHashMapStorage.removeTool(Code.JAKD);
			myHashMapStorage.removeTool(Code.LADW);
			
			// Run assertion
			StringBuilder testStr = new StringBuilder();
			testStr.append("Code: CHNS, Type: Chainsaw, Brand: Stihl\nDaily Charge: $1.49\nCharge On Weekdays: Yes\nCharge on Weekends: No\nCharge on Holidays: Yes\nChecked Out: No\n\n");
			testStr.append("Code: JAKR, Type: Jackhammer, Brand: Ridgid\nDaily Charge: $2.99\nCharge On Weekdays: Yes\nCharge on Weekends: No\nCharge on Holidays: No\nChecked Out: No");
			myHashMapStorage.printStoredTools();
			assertEquals(testStr.toString(), outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Some Tool Defaults Updated")
		void sometoolDefaultsUpdated() {
			// Assume CHNS and JAKR tools exist in storage before updating them and printing the remaining stored tools
			assumeTrue(myHashMapStorage.getTool(Code.CHNS) != null);
			assumeTrue(myHashMapStorage.getTool(Code.JAKR) != null);
			myHashMapStorage.updateTool(Code.CHNS, Attribute.TYPE, Type.LADDER); // changing TYPE from Chainsaw to Ladder
			myHashMapStorage.updateTool(Code.JAKR, Attribute.DAILYCHARGE, 200f); // changing DAILYCHARGE from 2.99 to 200
			
			// Run assertion
			StringBuilder testStr = new StringBuilder();
			testStr.append("Code: CHNS, Type: Ladder, Brand: Stihl\nDaily Charge: $1.49\nCharge On Weekdays: Yes\nCharge on Weekends: No\nCharge on Holidays: Yes\nChecked Out: No\n\n");
			testStr.append("Code: JAKD, Type: Jackhammer, Brand: DeWalt\nDaily Charge: $2.99\nCharge On Weekdays: Yes\nCharge on Weekends: No\nCharge on Holidays: No\nChecked Out: No\n\n");
			testStr.append("Code: JAKR, Type: Jackhammer, Brand: Ridgid\nDaily Charge: $200.00\nCharge On Weekdays: Yes\nCharge on Weekends: No\nCharge on Holidays: No\nChecked Out: No\n\n");
			testStr.append("Code: LADW, Type: Ladder, Brand: Werner\nDaily Charge: $1.99\nCharge On Weekdays: Yes\nCharge on Weekends: Yes\nCharge on Holidays: No\nChecked Out: No");
			myHashMapStorage.printStoredTools();
			assertEquals(testStr.toString(), outputTestStream.toString().trim());
		}
		
		@Test
		@DisplayName("Empty Storage")
		void emptyStorage() {
			// Assume the 4 default tools exist in storage before removing them all and calling printStoredTools() on the empty storage
			assumeTrue(myHashMapStorage.getTool(Code.CHNS) != null);
			assumeTrue(myHashMapStorage.getTool(Code.JAKD) != null);
			assumeTrue(myHashMapStorage.getTool(Code.JAKR) != null);
			assumeTrue(myHashMapStorage.getTool(Code.LADW) != null);
			myHashMapStorage.removeTool(Code.CHNS);
			myHashMapStorage.removeTool(Code.JAKD);
			myHashMapStorage.removeTool(Code.JAKR);
			myHashMapStorage.removeTool(Code.LADW);
			
			// Run assertion
			myHashMapStorage.printStoredTools();
			assertEquals("There is no valid storage system to print.", outputTestStream.toString().trim());
		}
	}
}
