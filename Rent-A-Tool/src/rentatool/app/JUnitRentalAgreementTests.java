package rentatool.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

import rentatool.rental_items.Chainsaw;
import rentatool.rental_items.Jackhammer;
import rentatool.rental_items.Ladder;
import rentatool.rental_items.RentalAgreement;
import rentatool.rental_items.ToolEnums.Brand;
import rentatool.rental_items.ToolEnums.Code;

/**
 * This TestSuite contains tests for the different methods available
 * on RentalAgreement instances. There will be 1 nested class per
 * calculation method under the nested "Calculation Tests" class, a
 * nested class for constructor tests, and 1 printRentalAgreement()
 * test at the top level class.
 * 
 * No StorageSystem instance is needed for testing RentalAgreement
 * instances. However, the TestSuite will use a @BeforeAll hook
 * to initialize a DateTimeFormatter object which is used in all
 * classes (parent level and nested). This hook will also initialize
 * the four default tool instances (CHNS, JAKD, JAKD, and LADW) for
 * testing throughout all classes.
 * 
 * @author CSGarcia1191
 *
 */
@DisplayName("RentalAgreement Tests")
class JUnitRentalAgreementTests {
	static DateTimeFormatter formatter;
	static Chainsaw testCHNS;
	static Jackhammer testJAKD, testJAKR;
	static Ladder testLADW;
	
	LocalDate checkoutDate;
	RentalAgreement ra;
	
	@BeforeAll
	static void initDateformatter() {
		// Initialize date format
		formatter = DateTimeFormatter.ofPattern("M/d/yy");
		
		// Initialize test Tools
		testCHNS = new Chainsaw(Code.CHNS, Brand.STIHL);
		testJAKD = new Jackhammer(Code.JAKD, Brand.DEWALT);
		testJAKR = new Jackhammer(Code.JAKR, Brand.RIDGID);
		testLADW = new Ladder(Code.LADW, Brand.WERNER);
	}
	
	@Nested
	@DisplayName("Constructor Tests")
	class ConstructorTests {		
		@BeforeEach
		void initCheckoutDate() {
			checkoutDate = LocalDate.parse("6/1/22", formatter); // Wednesday (due Wed, 06/08/22)
		}
		
		@Nested
		@DisplayName("Invalid Arguments")
		class InvalidArguments {
			@Test
			@DisplayName("Null Tool Arg")
			void nullToolArg() {
				assertThrows(InvalidCheckoutArgumentException.class,
						() -> new RentalAgreement(null, 7, 10, checkoutDate));
			}
			
			@Test
			@DisplayName("0 Rental Days")
			void zeroRentalDay() {
				assertThrows(InvalidCheckoutArgumentException.class,
						() -> new RentalAgreement(testCHNS, 0, 10, checkoutDate));
			}
			
			@Nested
			@DisplayName("Invalid Discount Percents")
			class InvalidDiscountPercents {
				@Test
				@DisplayName("Less Than 0")
				void lessThan0() {
					assertThrows(InvalidCheckoutArgumentException.class,
							() -> new RentalAgreement(testJAKD, 10, -1, checkoutDate));
				}
				
				@Test
				@DisplayName("More Than 100")
				void moreThan100() {
					assertThrows(InvalidCheckoutArgumentException.class,
							() -> new RentalAgreement(testJAKR, 10, 101, checkoutDate));
				}
			}
		}
		
		@Test
		@DisplayName("Null Checkout Date Arg")
		void nullCheckoutDateArg() {
			assertThrows(InvalidCheckoutArgumentException.class,
					() -> new RentalAgreement(testLADW, 10, 10, null));
		}
		
		@Test
		@DisplayName("All Valid Arguments")
		void allValidArguments() {
			ra = new RentalAgreement(testLADW, 10, 10, checkoutDate);
			assertNotNull(ra);
		}
	}
	
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	@Nested
	@DisplayName("Calculation Tests")
	class CalculationTests {
		@Nested
		@DisplayName("Calculate Chargeable Days")
		class CalculateChargeableDays {
			@Nested
			@DisplayName("Within One Week Period")
			class WithinOneWeekPeriod {
				@ParameterizedTest
				@EnumSource(value = Code.class)
				@DisplayName("Has No Holidays")
				void hasNoHolidays(Code code) {
					checkoutDate = LocalDate.parse("6/1/22", formatter); // Wednesday
					if (code == Code.CHNS) {
						ra = new RentalAgreement(testCHNS, 6, 0, checkoutDate); // due Tue, 06/07/22
						assertEquals(4, ra.calculateChargeableDays());
					} else if (code == Code.JAKD) {
						ra = new RentalAgreement(testJAKD, 4, 0, checkoutDate); // due Sun, 06/05/22
						assertEquals(2, ra.calculateChargeableDays());
					} else if (code == Code.JAKR) {
						ra = new RentalAgreement(testJAKR, 3, 0, checkoutDate); // due Sat, 06/04/22
						assertEquals(2, ra.calculateChargeableDays());
					} else if (code == Code.LADW) {
						ra = new RentalAgreement(testLADW, 7, 0, checkoutDate); // due Wed, 06/08/22
						assertEquals(7, ra.calculateChargeableDays());
					} else {
						fail(String.format("Need to implement assertion for code %s.", code.toString()));
					}
				}
				
				@ParameterizedTest
				@EnumSource(value = Code.class)
				@DisplayName("Has 1 Holiday")
				void has1Holiday(Code code) {
					checkoutDate = LocalDate.parse("7/1/22", formatter); // Friday
					if (code == Code.CHNS) {
						ra = new RentalAgreement(testCHNS, 4, 0, checkoutDate); // due Tue, 07/05/22
						assertEquals(2, ra.calculateChargeableDays());
					} else if (code == Code.JAKD) {
						ra = new RentalAgreement(testJAKD, 3, 0, checkoutDate); // due Mon, 07/04/22
						assertEquals(0, ra.calculateChargeableDays());
					} else if (code == Code.JAKR) {
						ra = new RentalAgreement(testJAKR, 6, 0, checkoutDate); // due Thur, 07/07/22
						assertEquals(3, ra.calculateChargeableDays());
					} else if (code == Code.LADW) {
						ra = new RentalAgreement(testLADW, 7, 0, checkoutDate); // due Fri, 07/08/22
						assertEquals(6, ra.calculateChargeableDays());
					} else {
						fail(String.format("Need to implement assertion for code %s.", code.toString()));
					}
				}
			}
			
			@Nested
			@DisplayName("Three Month Period")
			class ThreeMonthPeriod {
				@ParameterizedTest
				@EnumSource(mode = Mode.EXCLUDE, names = {"JAKD"}, value = Code.class)
				@DisplayName("Has No Holidays")
				void hasNoHolidays(Code code) {
					checkoutDate = LocalDate.parse("4/1/22", formatter); // Wednesday (due Thur, 06/30/22)
					if (code == Code.CHNS) {
						ra = new RentalAgreement(testCHNS, 90, 0, checkoutDate);
						assertEquals(64, ra.calculateChargeableDays());
					} else if (code == Code.JAKR) {
						ra = new RentalAgreement(testJAKR, 90, 0, checkoutDate);
						assertEquals(64, ra.calculateChargeableDays());
					} else if (code == Code.LADW) {
						ra = new RentalAgreement(testLADW, 90, 0, checkoutDate);
						assertEquals(90, ra.calculateChargeableDays());
					} else {
						fail(String.format("Need to implement assertion for code %s.", code.toString()));
					}
				}
				
				@ParameterizedTest
				@EnumSource(mode = Mode.EXCLUDE, names = {"JAKD"}, value = Code.class)
				@DisplayName("Has One Holiday")
				void hasOneHoliday(Code code) {
					checkoutDate = LocalDate.parse("5/1/22", formatter); // Sunday (due Sat, 07/30/22)
					if (code == Code.CHNS) {
						ra = new RentalAgreement(testCHNS, 90, 0, checkoutDate);
						assertEquals(65, ra.calculateChargeableDays());
					} else if (code == Code.JAKR) {
						ra = new RentalAgreement(testJAKR, 90, 0, checkoutDate);
						assertEquals(64, ra.calculateChargeableDays());
					} else if (code == Code.LADW) {
						ra = new RentalAgreement(testLADW, 90, 0, checkoutDate);
						assertEquals(89, ra.calculateChargeableDays());
					} else {
						fail(String.format("Need to implement assertion for code %s.", code.toString()));
					}
				}
				
				@ParameterizedTest
				@EnumSource(mode = Mode.EXCLUDE, names = {"JAKD"}, value = Code.class)
				@DisplayName("Has Two Holidays")
				void hasTwoHolidays(Code code) {
					checkoutDate = LocalDate.parse("7/1/22", formatter); // Friday (due Thur, 09/29/22)
					if (code == Code.CHNS) {
						ra = new RentalAgreement(testCHNS, 90, 0, checkoutDate);
						assertEquals(64, ra.calculateChargeableDays());
					} else if (code == Code.JAKR) {
						ra = new RentalAgreement(testJAKR, 90, 0, checkoutDate);
						assertEquals(62, ra.calculateChargeableDays());
					} else if (code == Code.LADW) {
						ra = new RentalAgreement(testLADW, 90, 0, checkoutDate);
						assertEquals(88, ra.calculateChargeableDays());
					} else {
						fail(String.format("Need to implement assertion for code %s.", code.toString()));
					}
				}
			}
			
			@Nested
			@DisplayName("One Year Period")
			class OneYearPeriod {
				@ParameterizedTest
				@EnumSource(mode = Mode.EXCLUDE, names = {"JAKD"}, value = Code.class)
				@DisplayName("Has One Holiday")
				void hasOneHoliday(Code code) {
					checkoutDate = LocalDate.parse("7/3/15", formatter); // Friday (due Sun, 07/03/16)
					// Note: 2016 was a leap year, so using 366 instead of 365 days
					if (code == Code.CHNS) {
						ra = new RentalAgreement(testCHNS, 366, 0, checkoutDate);
						assertEquals(260, ra.calculateChargeableDays());
					} else if (code == Code.JAKR) {
						ra = new RentalAgreement(testJAKR, 366, 0, checkoutDate);
						assertEquals(259, ra.calculateChargeableDays());
					} else if (code == Code.LADW) {
						ra = new RentalAgreement(testLADW, 366, 0, checkoutDate);
						assertEquals(365, ra.calculateChargeableDays());
					} else {
						fail(String.format("Need to implement assertion for code %s.", code.toString()));
					}
				}
				
				@ParameterizedTest
				@EnumSource(mode = Mode.EXCLUDE, names = {"JAKD"}, value = Code.class)
				@DisplayName("Has Two Holidays")
				void hasNoHolidays(Code code) {
					checkoutDate = LocalDate.parse("8/1/22", formatter); // Monday (due Tue, 08/01/23)
					if (code == Code.CHNS) {
						ra = new RentalAgreement(testCHNS, 365, 0, checkoutDate);
						assertEquals(261, ra.calculateChargeableDays());
					} else if (code == Code.JAKR) {
						ra = new RentalAgreement(testJAKR, 365, 0, checkoutDate);
						assertEquals(259, ra.calculateChargeableDays());
					} else if (code == Code.LADW) {
						ra = new RentalAgreement(testLADW, 365, 0, checkoutDate);
						assertEquals(363, ra.calculateChargeableDays());
					} else {
						fail(String.format("Need to implement assertion for code %s.", code.toString()));
					}
				}
				
				@ParameterizedTest
				@EnumSource(mode = Mode.EXCLUDE, names = {"JAKD"}, value = Code.class)
				@DisplayName("Has Three Holidays")
				void hasThreeHolidays(Code code) {
					checkoutDate = LocalDate.parse("9/05/15", formatter); // Sunday (due Tue, 09/05/16)
					// Note: 2016 was a leap year, so using 366 instead of 365 days
					if (code == Code.CHNS) {
						ra = new RentalAgreement(testCHNS, 366, 0, checkoutDate);
						assertEquals(261, ra.calculateChargeableDays());
					} else if (code == Code.JAKR) {
						ra = new RentalAgreement(testJAKR, 366, 0, checkoutDate);
						assertEquals(258, ra.calculateChargeableDays());
					} else if (code == Code.LADW) {
						ra = new RentalAgreement(testLADW, 366, 0, checkoutDate);
						assertEquals(363, ra.calculateChargeableDays());
					} else {
						fail(String.format("Need to implement assertion for code %s.", code.toString()));
					}
				}
			}
		}
		
		@TestInstance(TestInstance.Lifecycle.PER_CLASS)
		@Nested
		@DisplayName("Calculate Observed Holiday Dates")
		class CalculateObservedHolidayDates {
			LocalDate calculatedDate;
			
			// Need an arbitrary RentalAgreement instance to test the
			// calculateObservedHolidayDate method.
			@BeforeAll
			void initArbitraryRentalAgreement() {
				ra = new RentalAgreement(testCHNS, 10, 0, checkoutDate);
			}
			
			@Nested
			@DisplayName("July 4th")
			class July4th {
				@Test
				@DisplayName("On a Weekday")
				void onAWeekday() {
					calculatedDate = ra.calculateObservedHolidayDate(Month.JULY, 2022);
					assertEquals(4, calculatedDate.getDayOfMonth());
					assertEquals("7/4/22", calculatedDate.format(formatter).toString());
				}
				
				@Test
				@DisplayName("On a Sunday")
				void onASunday() {
					calculatedDate = ra.calculateObservedHolidayDate(Month.JULY, 2021);
					assertEquals(5, calculatedDate.getDayOfMonth());
					assertEquals("7/5/21", calculatedDate.format(formatter).toString());
				}
				
				@Test
				@DisplayName("On a Saturday")
				void onASaturday() {
					calculatedDate = ra.calculateObservedHolidayDate(Month.JULY, 2020);
					assertEquals(3, calculatedDate.getDayOfMonth());
					assertEquals("7/3/20", calculatedDate.format(formatter).toString());
				}
			}
			
			@Nested
			@DisplayName("Labor Day")
			class LaborDay {
				@Test
				@DisplayName("In 2022")
				void in2022() {
					calculatedDate = ra.calculateObservedHolidayDate(Month.SEPTEMBER, 2022);
					assertEquals(5, calculatedDate.getDayOfMonth());
					assertEquals("9/5/22", calculatedDate.format(formatter).toString());
				}
				
				@Test
				@DisplayName("In 2021")
				void in2021() {
					calculatedDate = ra.calculateObservedHolidayDate(Month.SEPTEMBER, 2021);
					assertEquals(6, calculatedDate.getDayOfMonth());
					assertEquals("9/6/21", calculatedDate.format(formatter).toString());
				}
				
				@Test
				@DisplayName("In 2020")
				void in2020() {
					calculatedDate = ra.calculateObservedHolidayDate(Month.SEPTEMBER, 2020);
					assertEquals(7, calculatedDate.getDayOfMonth());
					assertEquals("9/7/20", calculatedDate.format(formatter).toString());
				}
			}
			
			@Test
			@DisplayName("Invalid Month Enum")
			void invalidMonthEnum() {
				PrintStream standardOutStream = System.out; // Save current System.out reference so it can be restored
				
				ByteArrayOutputStream outputTestStream = new ByteArrayOutputStream();
				System.setOut(new PrintStream(outputTestStream)); // Set test output stream
				
				calculatedDate = ra.calculateObservedHolidayDate(Month.JANUARY, 2023);
				assertEquals("The method calculateObservedHolidayDate was called with a value other than JULY or SEPTEMBER. Returning a null LocalDate", outputTestStream.toString().trim());
				assertNull(calculatedDate);
				
				System.setOut(new PrintStream(standardOutStream)); // Restore System.out
			}
		}
		
		@TestInstance(TestInstance.Lifecycle.PER_CLASS)
		@Nested
		@DisplayName("Calculate Discount Amounts")
		class CalculateDiscountAmounts {
			BigDecimal calculatedDiscount;
			
			@BeforeAll
			void initCheckoutDate() {
				checkoutDate = LocalDate.parse("6/1/22", formatter); // Arbitrary date
			}
			
			@Nested
			@DisplayName("Valid Percents")
			class ValidPercents {
				@Test
				@DisplayName("0 Percent")
				void zeroPercent() {
					ra = new RentalAgreement(testCHNS, 10, 0, checkoutDate);
					calculatedDiscount = ra.calculateDiscountAmount();
					assertEquals(0, calculatedDiscount.intValue());
				}
				
				@Test
				@DisplayName("50 Percent")
				void fiftyPercent() {
					ra = new RentalAgreement(testCHNS, 10, 50, checkoutDate);
					assumeTrue(ra.getPreDiscountCharge().floatValue() == 10.43f);
					calculatedDiscount = ra.calculateDiscountAmount();
					assertEquals(5.22f, calculatedDiscount.floatValue());
				}
				
				@Test
				@DisplayName("100 Percent")
				void oneHundredPercent() {
					ra = new RentalAgreement(testCHNS, 10, 100, checkoutDate);			
					calculatedDiscount = ra.calculateDiscountAmount();
					assumeTrue(ra.getPreDiscountCharge().floatValue() == 10.43f);
					assertEquals(10.43f, calculatedDiscount.floatValue());
				}
			}
			
			@Nested
			@DisplayName("Invalid Percents")
			class InvalidPercents {
				@Test
				@DisplayName("Under 0 Percent User Input")
				void under0PercentUserInput() {
					assertThrows(InvalidCheckoutArgumentException.class,
							() -> new RentalAgreement(testCHNS, 10, -1, checkoutDate));
				}
				
				@Test
				@DisplayName("Over 100 Percent User Input")
				void over100PercentUserInput() {
					assertThrows(InvalidCheckoutArgumentException.class,
							() -> new RentalAgreement(testCHNS, 10, 101, checkoutDate));
				}
			}
		}
		
		@TestInstance(TestInstance.Lifecycle.PER_CLASS)
		@Nested
		@DisplayName("Calculate Pre-Discount Charges")
		class CalculatePreDiscountCharges {			
			@BeforeAll
			void initCheckoutDate() {
				checkoutDate = LocalDate.parse("6/1/22", formatter); // Wednesday (due Wed, 06/08/22)
			}
			
			@Test
			@DisplayName("Chainsaw Tool")
			void chainsawTool() {
				ra = new RentalAgreement(testCHNS, 7, 50, checkoutDate);
				assumeTrue(ra.getTotalChargeableDays() == 5);
				assumeTrue(ra.getDailyRentalCharge() == 1.49f);
				assertEquals(7.45f, ra.calculatePreDiscountCharge().floatValue());
			}
			
			@Test
			@DisplayName("Jackhammer Tool")
			void jackhammerTool() {
				ra = new RentalAgreement(testJAKR, 7, 50, checkoutDate);
				assumeTrue(ra.getTotalChargeableDays() == 5);
				assumeTrue(ra.getDailyRentalCharge() == 2.99f);
				assertEquals(14.95f, ra.calculatePreDiscountCharge().floatValue());
			}
			
			@Test
			@DisplayName("Ladder Tool")
			void ladderTool() {
				ra = new RentalAgreement(testLADW, 7, 50, checkoutDate);
				assumeTrue(ra.getTotalChargeableDays() == 7);
				assumeTrue(ra.getDailyRentalCharge() == 1.99f);
				assertEquals(13.93f, ra.calculatePreDiscountCharge().floatValue());
			}
		}
		
		@TestInstance(TestInstance.Lifecycle.PER_CLASS)
		@Nested
		@DisplayName("Calculate Final Charges")
		class CalculateFinalCharges {			
			@BeforeAll
			void initCheckoutDate() {
				checkoutDate = LocalDate.parse("6/1/22", formatter); // Wednesday (due Wed, 06/08/22)
			}
			
			@Test
			@DisplayName("Chainsaw Tool")
			void chainsawTool() {
				ra = new RentalAgreement(testCHNS, 7, 50, checkoutDate);
				assumeTrue(ra.getTotalChargeableDays() == 5);
				assumeTrue(ra.getDailyRentalCharge() == 1.49f);
				assumeTrue(ra.getDiscountAmount().floatValue() == 3.73f);
				assertEquals(3.72f, ra.calculateFinalCharge().floatValue());
			}
			
			@Test
			@DisplayName("Jackhammer Tool")
			void jackhammerTool() {
				ra = new RentalAgreement(testJAKR, 7, 50, checkoutDate);
				assumeTrue(ra.getTotalChargeableDays() == 5);
				assumeTrue(ra.getDailyRentalCharge() == 2.99f);
				assumeTrue(ra.getDiscountAmount().floatValue() == 7.48f);
				assertEquals(7.47f, ra.calculateFinalCharge().floatValue());
			}
			
			@Test
			@DisplayName("Ladder Tool")
			void ladderTool() {
				ra = new RentalAgreement(testLADW, 7, 50, checkoutDate);
				assumeTrue(ra.getTotalChargeableDays() == 7);
				assumeTrue(ra.getDailyRentalCharge() == 1.99f);
				assumeTrue(ra.getDiscountAmount().floatValue() == 6.97f);
				assertEquals(6.96f, ra.calculateFinalCharge().floatValue());
			}
		}
	}
	
	@Test
	@DisplayName("Print Rental Agreement Test")
	void printRentalAgreementTest() {
		checkoutDate = LocalDate.parse("6/1/22", formatter); // Wednesday (due Wed, 06/08/22)
		
		StringBuilder expectedOutput = new StringBuilder();
		expectedOutput.append("Tool code: CHNS\n");
		expectedOutput.append("Tool type: Chainsaw\n");
		expectedOutput.append("Tool brand: Stihl\n");
		expectedOutput.append("Rental days: 7\n");
		expectedOutput.append("Check out date: 06/01/22\n");
		expectedOutput.append("Due date: 06/08/22\n");
		expectedOutput.append("Daily rental charge: $1.49\n");
		expectedOutput.append("Charge days: 5\n");
		expectedOutput.append("Pre-discount charge: $7.45\n");
		expectedOutput.append("Discount percent: 50%\n");
		expectedOutput.append("Discount amount: $3.73\n");
		expectedOutput.append("Final charge: $3.72");
		
		ra = new RentalAgreement(new Chainsaw(Code.CHNS, Brand.STIHL), 7, 50, checkoutDate);
		
		assertEquals(expectedOutput.toString(), ra.printRentalAgreement());
	}
}
