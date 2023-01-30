package rentatool.app;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

import rentatool.app.Database;
import rentatool.app.SQLiteDB;
import rentatool.rental_items.RentalAgreement;
import rentatool.rental_items.Tool;
import rentatool.rental_items.ToolEnums.*;

public class Main {

	public static void main(String[] args) {
		// By default, when the Rent-A-Tool application is ran, it uses System.in as the input stream,
		// and an SQLiteDB instance as the storage system
		runRentAToolApp(System.in, new SQLiteDB());
	}
	
	/**
	 * This method launches the Rent-A-Tool application. All user input is handled here.
	 * 
	 * @param inputStream The input to stream to read from during program execution.
	 * If the application is ran by the main method, this will be System.in.
	 * If the application is ran by a test class, a ByteArrayInputStream object is
	 * used instead to simulate user input during test runs.
	 * @param storage The StorageSystem to store tools in during program execution.
	 * If the application is ran by the main method, this will be an SQLiteDB instance.
	 * This can also be a HashMapStorage instance.
	 */
	public static void runRentAToolApp(InputStream inputStream, StorageSystem storage) {
		final StorageSystem toolStorage = storage;
		
		// For reference, display the initial tools available for rental to the clerk
		System.out.println("----------------------------    Available Tools   ------------------------------\n");
		toolStorage.printStoredTools();
		
		System.out.println("----------------------------    Rent-A-Tool Checkout   ------------------------------\n");
		System.out.println("Welcome, Rent-A-Tool associate!");
		
		Scanner scanner = new Scanner(inputStream);
		
		StartOfApp:
		// Run the point-of-sale application until the clerk has no more tools to process through checkout.
		while (true) {
			// Object reference variables to be used while running the application
			Code code;
			String codeStr;
			Tool tool;
			
			// Updated to true when the clerk wants to return a tool. Is reset to false once the tool is returned
			boolean toolReturnRequested = false;
			
			// First, ask clerk if they'd like to process any tool returns
			while (true) {
				System.out.println("Would you like to return a tool? (Yes/No): ");
				try {
					String answer = scanner.nextLine();
					if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
						toolReturnRequested = true; 
					} else if (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase("n")) {
						System.out.println("Okay thank you, continuing on to checkout...");
					} else if (!answer.equalsIgnoreCase("no") && !answer.equalsIgnoreCase("n")) {
						System.out.println("Please enter either yes or no.");
						continue;
					}
				} catch (Exception e) { // Handles NoSuchElementExcpetion, IllegalStateException, and any other unexpected Exceptions
					System.out.println("There was an unexpected issue with the system. Please restart the application or contact Support for further help.");
					scanner.close();
					ifDatabaseThenCloseConection(toolStorage);
					throw e;
				}
				
				break; // Clerk provided a valid yes/no response. Moving on to process the tool code
			}
			
			// Process clerk's input for Tool code
			if (!toolReturnRequested) {
				System.out.println("Please provide the following information to process a tool rental");
			}
			while (true) {
				System.out.print("Tool code: ");
				codeStr = scanner.nextLine();
				try {
					code = Code.valueOf(codeStr.toUpperCase().trim()); // throws IllegalArgumentException if the input doesn't match an existing Code Enum
					tool = toolStorage.getTool(code);
					
					// Process tool return if requested
					if (toolReturnRequested) {
						toolStorage.updateTool(code, Attribute.CHECKEDOUT, false);
						toolReturnRequested = false;
						System.out.println("Thank you. Tool has been returned!");
						continue StartOfApp; // jump back to start of program to ask clerk if they want to process another tool return
					} else if (tool.isCheckedOut()) {
						System.out.println("Sorry, that tool is currently checked out. Please try another code.");
						continue;
					}
				} catch (IllegalArgumentException e) {
					System.out.println("Could not find tool associated with this code. Please try another code.");
					continue;
				} catch (Exception e) { // Handles NoSuchElementExcpetion, IllegalStateException, and any other unexpected Exceptions
					System.out.println("There was an unexpected issue with the system. Please restart the checkout application");
					scanner.close();
					ifDatabaseThenCloseConection(toolStorage);
					throw e;
				}
				
				break; // Valid tool code processed. Moving on to ask clerk for the Rental day count
			}
			
			// Process clerk's input for Rental day count
			int rentalDays;
			while (true) {
				System.out.print("Rental day count: ");
				try {
					rentalDays = scanner.nextInt();
					scanner.nextLine(); // Consumes the newline character if the input was an integer
					if (rentalDays < 1) {
						throw new InvalidCheckoutArgumentException("Number of rental days must be greater than 0. Please restart the application and try again.\n"); // The exception error handling will consume the newline character
					}
				} catch (InputMismatchException e) { // Handles cases where input was not an integer
					scanner.nextLine(); // Consumes the newline character for InputMismatchException exceptions
					System.out.println("Number of rental days needs to be a whole number. Please enter a valid number of rental days.");
					continue;
				} catch (InvalidCheckoutArgumentException e) { // Handles cases where the input integer was < 1
					System.out.println(e.getMessage());
					scanner.close();
					ifDatabaseThenCloseConection(toolStorage);
					throw e;
				} catch (Exception e) { // Handles NoSuchElementExcpetion, IllegalStateException, and any other unexpected Exceptions
					scanner.nextLine(); // Consumes the newline character for the caught Exception
					System.out.println("There was an unexpected issue with the system. Please restart the application or contact Support for further help.");
					scanner.close();
					ifDatabaseThenCloseConection(toolStorage);
					throw e;
				}
				
				break; // Valid number of rental days processed. Moving on to ask clerk for the Discount percent
			}
			
			// Process clerk's input for Discount percent
			int discountPercent;
			while (true) {
				System.out.print("Discount percent (do not include '%' symbol): ");
				try {
					discountPercent = scanner.nextInt();
					scanner.nextLine(); // Consumes the newline character if the input was an integer
					if (discountPercent < 0 || discountPercent > 100) {
						throw new InvalidCheckoutArgumentException("Discount percent needs to be in the range 0-100. Please restart the application and try again.\n"); // The exception error handling will consume the newline character
					}
				} catch (InputMismatchException e) { // Handles cases where input was not an integer (includes the case when input contains a '%' character)
					scanner.nextLine(); // Consumes the newline character for InputMismatchException exceptions
					System.out.println("Discount percent needs to be a whole number and should not include the '%' symbol. Please enter a valid discount percent.");
					continue;
				} catch (InvalidCheckoutArgumentException e) { // Handles cases where the input integer was < 0 or > 100
					System.out.println(e.getMessage());
					scanner.close();
					ifDatabaseThenCloseConection(toolStorage);
					throw e;
				} catch (Exception e) { // Handles NoSuchElementExcpetion, IllegalStateException, and any other unexpected Exceptions
					scanner.nextLine(); // Consumes the newline character for the caught Exception
					System.out.println("There was an unexpected issue with the system. Please restart the application or contact Support for further help.");
					scanner.close();
					ifDatabaseThenCloseConection(toolStorage);
					throw e;
				}
				
				break; // Valid discount percent processed. Moving on to ask clerk for the Checkout date
			}
			
			// Process clerk's input for Checkout date
			LocalDate checkoutDate;
			while (true) {
				System.out.print("Checkout date (MM/dd/yy): ");
				try {
					String checkoutDateStr = scanner.nextLine();
					// Enforces M/d/yy format (0-padding input not needed by clerk for month or day). Throws DateTimeParseException otherwise
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
					checkoutDate = LocalDate.parse(checkoutDateStr, formatter);
				} catch (DateTimeParseException e) { // Handles cases where input was not in M/d/yy format
					System.out.println("Please provide a valid date following the format MM/dd/yy.");
					continue;
				} catch (Exception e) { // Handles NoSuchElementExcpetion, IllegalStateException, and any other unexpected Exceptions
					System.out.println("There was an unexpected issue with the system. Please restart the application or contact Support for further help.");
					scanner.close();
					ifDatabaseThenCloseConection(toolStorage);
					throw e;
				}
				
				System.out.println("Tool was successfully checked out! Generating the rental agreement...\n");
				break; // Valid checkout date processed. Moving on to printing the Rental Agreement
			}
			
			// Generate and print Rental Agreement
			RentalAgreement rentalAgreement = new RentalAgreement(toolStorage.getTool(code), rentalDays, discountPercent, checkoutDate);
			rentalAgreement.printRentalAgreement();
			
			// Update tool's checkout status in the database
			toolStorage.updateTool(code, Attribute.CHECKEDOUT, true);
			
			// Ask clerk if they'd like to process another tool through checkout
			while (true) {
				System.out.println("Would you like to process another tool? (Yes/No): ");
				try {
					String answer = scanner.nextLine();
					if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
						continue StartOfApp;
					} else if (!answer.equalsIgnoreCase("no") && !answer.equalsIgnoreCase("n")) {
						System.out.println("Please enter either yes or no.");
						continue;
					}
				} catch (Exception e) { // Handles NoSuchElementExcpetion, IllegalStateException, and any other unexpected Exceptions
					System.out.println("There was an unexpected issue with the system. Please restart the application or contact Support for further help.");
					scanner.close();
					ifDatabaseThenCloseConection(toolStorage);
					throw e;
				}
				
				break; // Clerk answered no (no further items to process through checkout). Shutting down the application
			}
			
			break;
		}
		
		scanner.close();
		ifDatabaseThenCloseConection(toolStorage);
		System.out.println("Thank you for using the Rent-A-Tool Checkout application!");
	}
	
	/**
	 * Helper method for closing connections on StorageSystems that are Database instances.
	 * 
	 * @param toolStorage The StorageSystem to potentially close a connection on. This is
	 * the StorageSystem that is used throughout program execution.
	 */
	private static void ifDatabaseThenCloseConection(StorageSystem toolStorage) {
		if (toolStorage instanceof Database) {
			((Database) toolStorage).closeConnection();
		}
	}
}
