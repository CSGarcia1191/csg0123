
### by CSGarcia1191

# Rent-A-Tool Application

## Introduction

This project is my implementation for a point-of-sale application I call "Rent-A-Tool".  
The application is used to checkout the available tools in the app's Storage system.  

## Usage

If you'd like to use this application, you will need to run the Main.class file
(located in the rentatool.app package) as a Java Application.  


The Rent-A-Tool application will first print out all the tools available for rental  
for your reference. Then, you will be greeted with message welcoming you as a  
Rent-A-Tool associate.

### Prompts

The Rent-A-Tool application will prompt you with the following:  

1. Would you like to return a tool?
    **Note:** *Valid responses include: Yes, yes, Y, y, No, no, N, n*
    
   If **yes**:
   * prompts you to enter the Tool Code of the tool you want to return.
   * prompts you if you'd like to return another tool.
   If **no**:
   * presents you with a thank you message
   * prompts you to enter the information of the tool you want to checkout.
    
2. Please provide the following information to process a tool rental  
    Prompts you for:  
    * Tool Code
        Prints friendly try again message if code is invalid or checked out 
    * Rental day count
        Prints friendly try again message if number is invalid  
    * Discount percent
        * Prints friendly try again message if input is not a number
        * Throws InvalidCheckoutArgumentException if not in the range [0, 100]
   * Checkout date
        Prints friendly try again message if date is not in MM/dd/yy format  
        **Note:** *M/d/yy is also considered valid*  
            
3. Generates and prints out a Rental Agreement  

4. Would you like to process another tool?  
    **Note:** *Valid responses include: Yes, yes, Y, y, No, no, N, n*
    
   If **yes**, the application repeats from prompt 1.  
   If **no**, Rent-A-Tool presents you with a thank you message and terminates.  

## Implementation

The project is structured across 2 different packages, rentatool.app and rentatool.rental_items.  

The **rentatool.app** package consists of the Main class that runs the application, the files  
needed for creating the application's tool storage system, and JUnit5 java files which all begin  
with "JUnit" and end in "Tests".  

The **rentatool.rental_items** package consists of all the java files used to define the different  
tool objects made available by the Rent-A-Tool application. The package also contains a class file  
called "RentalAgreement.java", which rental agreements are generated from upon tool checkout.  

For storing tools, Rent-A-Tool uses a child instance of its defined StorageSystem interface  
(located in the rentatool.app package). The project currently has 2 types of StorageSystem  
child classes called SQLiteDB and HashMapStorage.  All StorageSystems must implement the CRUD  
operations defined by CrudOps.java (at a minimum) in order to function with the Rent-A-Tool app.  

By default, the application uses an SQLiteDB instance for storing its tools. The SQLiteDB class  
creates an SQLite database called "rentatool.db" by leveraging the [SQLite JDBC version 3.40](https://github.com/xerial/sqlite-jdbc).  
A portable jar file of this SQLite JDBC has been included under this project's lib folder.  

The HashMapStorage class implements StorageSystem and uses a HashMap<ToolEnums.Code, Tool> object  
to store tools in. HashMapStorage was implemented to showcase Rent-A-Tool's ability to  
integrate with different types of StorageSystems.  

## Tests

There are 4 TestSuites located under the **rentatool.app** package:  

* JUnitBaseTests.java contains 6 minimum base test cases for the application
* JUnitHashMapStorageTests.java contains 33 test cases for the HashMapStorage class
* JUnitSQLiteDBTests.java contains 35 test cases for the SQLiteDB class
* JUnitRentalAgreementTest.java contains 51 test cases for the RentalAgremeent class  
    **Note:** *Each TestSuite file's naming convention is to start with "JUnit" and end in "Tests.java".*  
    Convention: `JUnit<nameOfClassBeingTestedHere>Tests.java`  
