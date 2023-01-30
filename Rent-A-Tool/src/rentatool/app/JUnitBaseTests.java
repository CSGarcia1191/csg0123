package rentatool.app;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import rentatool.app.InvalidCheckoutArgumentException;
import rentatool.app.Main;
import rentatool.rental_items.RentalAgreement;
import rentatool.rental_items.Tool;
import rentatool.rental_items.ToolEnums.Code;
import rentatool.app.Database;
import rentatool.app.SQLiteDB;

/**
 * This TestSuite contains 6 base tests for the Rent-A-Tool application.
 * The first test case, test1(), is the only case that should result in
 * an exception being thrown from an invalid user input for
 * 'Discount percent'. Specifically, this test case should throw an
 * InvalidCheckoutArgumentException exception.
 * 
 * The remaining 5 test cases are grouped together under the nested
 * class "RentalAgreementTests". Each of these tests validates the
 * the Rental Agreement generated after user input by comparing
 * the expected Rental Agreement string against the actual
 * Rental Agreement string (produced by calling the instance method
 * printRentalAgreement()).
 * 
 * An SQLiteDB instance will be used for all 5 nested tests. Therefore,
 * the Nested class uses the @BeforeAll annotation to open the database
 * connection and an @AfterAll annotation to close it.
 * 
 * @author CSGarcia1191
 *
 */
@DisplayName("Base Tests")
class JUnitBaseTests {
	
	/*
	 * Tool code:		JAKR
	 * Checkout date:	9/3/15
	 * Rental days:		5
	 * Discount:		101	(this causes an InvalidCheckoutArgumentException exception)
	 * 
	 * Note: Of the 6 base test cases, this one should throw an exception.
	 */
	@Test
	@DisplayName("Test 1 - Exception Case")
	void test1() {
		// For this test, the newline character '\n' is equivalent to the user pressing the Enter key
		
		StringBuilder inputData = new StringBuilder();//"n\nJAKR\n5\n101\n9/3/15\n";
		// 1) clerk is asked if they want to process a Return
		inputData.append("no\n"); // Answer: no
		
		// checkout process now begins
		
		// 2) clerk is asked for Tool's code
		inputData.append("JAKR\n"); // input: JAKR
		// 3) clerk is asked for rental day count
		inputData.append("5\n"); // input: 5
		// 4) clerk is asked for discount percent
		inputData.append("101\n"); // input: 101
		// 5) clerk is asked for checkout date
		inputData.append("9/3/15\n"); // input: 9/3/15
		
		// Using SQLiteDB instance
		assertThrows(InvalidCheckoutArgumentException.class,
				() -> Main.runRentAToolApp(new ByteArrayInputStream(inputData.toString().getBytes()), new SQLiteDB()));
	}

	// This Nested class is defined with TestInstance.Lifecycle.PER_CLASS setting.
	// This is so that the nested class will declare "mySQLiteDB" once,
	// and can make use of the @BeforeAll and @AfterAll annotations. With this in
	// place, mySQLiteDB will now only be initialized once and that same instance
	// will be used for all of the nested class's test cases.
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	@Nested
	@DisplayName("Rental Agreement Tests")
	class RentalAgreementTests {
		Database mySQLiteDB;
		
		// Open a connection to the SQLite database before running all tests
		@BeforeAll
		void initDatabase() {
			mySQLiteDB = new SQLiteDB();
		}
		
		/*
		 * Tool code:		LADW
		 * Checkout date:	7/2/20
		 * Rental days:		3
		 * Discount:		10
		 */
		@Test
		@DisplayName("Test 2")
		void test2() {
			Tool ladder = mySQLiteDB.getTool(Code.LADW);
			assertNotNull(ladder, "The ladder tool should exist in the SQLite database by default");
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
			LocalDate checkoutDate = LocalDate.parse("7/2/20", formatter);
			RentalAgreement rentalAgreement = null;
			try {
				rentalAgreement = new RentalAgreement(ladder, 3, 10, checkoutDate);
			} catch (InvalidCheckoutArgumentException e) {
				fail("Inavlid RentalAgreement argument was used. Failing test.");
			}
			
			// Creating Strings representing the expected output from printing a Rental Agreement
			StringBuilder expectedOutput = new StringBuilder();
			expectedOutput.append("Tool code: LADW\n");
			expectedOutput.append("Tool type: Ladder\n");
			expectedOutput.append("Tool brand: Werner\n");
			expectedOutput.append("Rental days: 3\n");
			expectedOutput.append("Check out date: 07/02/20\n");
			expectedOutput.append("Due date: 07/05/20\n");
			expectedOutput.append("Daily rental charge: $1.99\n");
			expectedOutput.append("Charge days: 2\n");
			expectedOutput.append("Pre-discount charge: $3.98\n");
			expectedOutput.append("Discount percent: 10%\n");
			expectedOutput.append("Discount amount: $0.40\n");
			expectedOutput.append("Final charge: $3.58");
			
			assertEquals(expectedOutput.toString(), rentalAgreement.printRentalAgreement());
		}
		
		/*
		 * Tool code:		CHNS
		 * Checkout date:	7/2/15
		 * Rental days:		5
		 * Discount:		25
		 */
		@Test
		@DisplayName("Test 3")
		void test3() {
			Tool chainsaw = mySQLiteDB.getTool(Code.CHNS);
			assertNotNull(chainsaw, "The chainsaw tool should exist in the SQLite database by default");
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
			LocalDate checkoutDate = LocalDate.parse("7/2/15", formatter);
			RentalAgreement rentalAgreement = new RentalAgreement(chainsaw, 5, 25, checkoutDate);
			assertNotNull(rentalAgreement, "The Rental Agreement for chainsaw CHNS should not be null");
			
			StringBuilder expectedOutput = new StringBuilder();
			expectedOutput.append("Tool code: CHNS\n");
			expectedOutput.append("Tool type: Chainsaw\n");
			expectedOutput.append("Tool brand: Stihl\n");
			expectedOutput.append("Rental days: 5\n");
			expectedOutput.append("Check out date: 07/02/15\n");
			expectedOutput.append("Due date: 07/07/15\n");
			expectedOutput.append("Daily rental charge: $1.49\n");
			expectedOutput.append("Charge days: 3\n");
			expectedOutput.append("Pre-discount charge: $4.47\n");
			expectedOutput.append("Discount percent: 25%\n");
			expectedOutput.append("Discount amount: $1.12\n");
			expectedOutput.append("Final charge: $3.35");
			
			assertEquals(expectedOutput.toString(), rentalAgreement.printRentalAgreement());
		}
		
		/*
		 * Tool code:		JAKD
		 * Checkout date:	9/3/15
		 * Rental days:		6
		 * Discount:		0
		 */
		@Test
		@DisplayName("Test 4")
		void test4() {
			Tool jackhammer = mySQLiteDB.getTool(Code.JAKD);
			assertNotNull(jackhammer, "The jackhammer tool should exist in the SQLite database by default");
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
			LocalDate checkoutDate = LocalDate.parse("9/3/15", formatter);
			RentalAgreement rentalAgreement = new RentalAgreement(jackhammer, 6, 0, checkoutDate);
			assertNotNull(rentalAgreement, "The Rental Agreement for jackhammer JAKD should not be null");
			
			StringBuilder expectedOutput = new StringBuilder();
			expectedOutput.append("Tool code: JAKD\n");
			expectedOutput.append("Tool type: Jackhammer\n");
			expectedOutput.append("Tool brand: DeWalt\n");
			expectedOutput.append("Rental days: 6\n");
			expectedOutput.append("Check out date: 09/03/15\n");
			expectedOutput.append("Due date: 09/09/15\n");
			expectedOutput.append("Daily rental charge: $2.99\n");
			expectedOutput.append("Charge days: 3\n");
			expectedOutput.append("Pre-discount charge: $8.97\n");
			expectedOutput.append("Discount percent: 0%\n");
			expectedOutput.append("Discount amount: $0.00\n");
			expectedOutput.append("Final charge: $8.97");
			
			assertEquals(expectedOutput.toString(), rentalAgreement.printRentalAgreement());
		}
		
		/*
		 * Tool code:		JAKR
		 * Checkout date:	7/2/15
		 * Rental days:		9
		 * Discount:		0
		 */
		@Test
		@DisplayName("Test 5")
		void test5() {
			Tool jackhammer = mySQLiteDB.getTool(Code.JAKR);
			assertNotNull(jackhammer, "The jackhammer tool should exist in the SQLite database by default");
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
			LocalDate checkoutDate = LocalDate.parse("7/2/15", formatter);
			RentalAgreement rentalAgreement = new RentalAgreement(jackhammer, 9, 0, checkoutDate);
			assertNotNull(rentalAgreement, "The Rental Agreement for jackhammer JAKR should not be null");
			
			StringBuilder expectedOutput = new StringBuilder();
			expectedOutput.append("Tool code: JAKR\n");
			expectedOutput.append("Tool type: Jackhammer\n");
			expectedOutput.append("Tool brand: Ridgid\n");
			expectedOutput.append("Rental days: 9\n");
			expectedOutput.append("Check out date: 07/02/15\n");
			expectedOutput.append("Due date: 07/11/15\n");
			expectedOutput.append("Daily rental charge: $2.99\n");
			expectedOutput.append("Charge days: 5\n");
			expectedOutput.append("Pre-discount charge: $14.95\n");
			expectedOutput.append("Discount percent: 0%\n");
			expectedOutput.append("Discount amount: $0.00\n");
			expectedOutput.append("Final charge: $14.95");
			
			assertEquals(expectedOutput.toString(), rentalAgreement.printRentalAgreement());
		}
		
		/*
		 * Tool code:		JAKR
		 * Checkout date:	7/2/20
		 * Rental days:		4
		 * Discount:		50
		 */
		@Test
		@DisplayName("Test 6")
		void test6() {
			Tool jackhammer = mySQLiteDB.getTool(Code.JAKR);
			assertNotNull(jackhammer, "The jackhammer tool should exist in the SQLite database by default");
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
			LocalDate checkoutDate = LocalDate.parse("7/2/20", formatter);
			RentalAgreement rentalAgreement = new RentalAgreement(jackhammer, 4, 50, checkoutDate);
			assertNotNull(rentalAgreement, "The Rental Agreement for jackhammer JAKR should not be null");
			
			StringBuilder expectedOutput = new StringBuilder();
			expectedOutput.append("Tool code: JAKR\n");
			expectedOutput.append("Tool type: Jackhammer\n");
			expectedOutput.append("Tool brand: Ridgid\n");
			expectedOutput.append("Rental days: 4\n");
			expectedOutput.append("Check out date: 07/02/20\n");
			expectedOutput.append("Due date: 07/06/20\n");
			expectedOutput.append("Daily rental charge: $2.99\n");
			expectedOutput.append("Charge days: 1\n");
			expectedOutput.append("Pre-discount charge: $2.99\n");
			expectedOutput.append("Discount percent: 50%\n");
			expectedOutput.append("Discount amount: $1.50\n");
			expectedOutput.append("Final charge: $1.49");
			
			assertEquals(expectedOutput.toString(), rentalAgreement.printRentalAgreement());
		}
		
		// Close the connection to the SQLite database after all tests have been ran
		@AfterAll
		void shutdownDatabase() {
			mySQLiteDB.closeConnection();
		}
	}
}
