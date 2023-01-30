package rentatool.rental_items;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;

import rentatool.app.InvalidCheckoutArgumentException;
import rentatool.rental_items.ToolEnums.Brand;
import rentatool.rental_items.ToolEnums.Code;
import rentatool.rental_items.ToolEnums.Type;

public class RentalAgreement {
		
	private Tool tool;
	private Code code;
	private Type type;
	private Brand brand;
	private BigDecimal discountAmount, finalCharge, preDiscountCharge;
	private float dailyRentalCharge;
	private int totalChargeableDays, discountPercent, rentalDays;
	private LocalDate checkoutDate, dueDate;
	
	/**
	 * This constructor will set all of the RentalAgreement's properties. If a
	 * new value is needed, then the caller must create a new RentalAgreement
	 * instance since the old one should no longer be "valid".
	 * 
	 * @param tool The Tool instance this RentalAgreement is being generated for
	 * @param rentalDays The number of days the tool is being rented for
	 * @param discountPercent Whole number representing discount percentage
	 * @param checkoutDate The date the Tool is checked out
	 * @throws InvalidCheckoutArgumentException if any arguments are invalid
	 */
	public RentalAgreement(Tool tool, int rentalDays, int discountPercent, LocalDate checkoutDate) throws InvalidCheckoutArgumentException {
		// Ensures valid values are passed into the constructor
		if (tool == null || rentalDays < 1 || discountPercent < 0 || discountPercent > 100 || checkoutDate == null) {
			throw new InvalidCheckoutArgumentException("An invalid argument was passed into the RentalAgreement constructor.");
		}
		
		this.tool = tool;
		this.code = tool.getCode();
		this.type = tool.getType();
		this.brand = tool.getBrand();
		this.dailyRentalCharge = tool.getDailyCharge();
		this.rentalDays = rentalDays;
		this.discountPercent = discountPercent;
		this.checkoutDate = checkoutDate;
		this.dueDate = checkoutDate.plusDays(rentalDays);
		this.totalChargeableDays = calculateChargeableDays();
		this.preDiscountCharge = calculatePreDiscountCharge();
		this.discountAmount = calculateDiscountAmount();
		this.finalCharge = calculateFinalCharge();
	}
	
	/**
	 * Calculates the total number of chargeable days for a tool's rental period
	 * 
	 * Note: LocalDate instances are immutable according to the javadocs. This means LocalDate instance methods that
	 * modify the instance actually return a new copy of the instance after the modification has been applied
	 * 
	 * @return int number of days that are chargeable for the Tool instance
	 */
	public int calculateChargeableDays() {
		// Holiday dates can vary throughout the years and thus are calculated below as needed. 
		LocalDate july4thDate;
		LocalDate laborDayDate;
		int calendarYearDifference = dueDate.getYear() - checkoutDate.getYear();
		int totalHolidays = 0, totalWeekdays = 0, chargeableDays = 0;
		
		// First calculate chargeable holidays
		// Calculate number of holidays in 1st year
		july4thDate = calculateObservedHolidayDate(Month.JULY, checkoutDate.getYear()); // July 4th of checkout year
		totalHolidays += dateExistsInRange(july4thDate, checkoutDate, dueDate) ? 1 : 0;
		laborDayDate = calculateObservedHolidayDate(Month.SEPTEMBER, checkoutDate.getYear()); // Labor Day of checkout year
		totalHolidays += dateExistsInRange(laborDayDate, checkoutDate, dueDate) ? 1 : 0;

		// Calculate number of holidays in remaining years 
		if (calendarYearDifference > 0) {
			// in middle years
			if (calendarYearDifference > 1) {
				totalHolidays += (2 * (calendarYearDifference - 1));
			}
			
			// in final year
			july4thDate = calculateObservedHolidayDate(Month.JULY, dueDate.getYear()); // July 4th of due date year
			totalHolidays += dueDate.isBefore(july4thDate) ? 0 : 1;
			laborDayDate = calculateObservedHolidayDate(Month.SEPTEMBER, dueDate.getYear()); // Labor Day of due date year
			totalHolidays += dueDate.isBefore(laborDayDate) ? 0 : 1;
		}
		
		// Calculate chargeable weekdays and weekends
		LocalDate finalWeekDate;
		for (int remaining = (rentalDays % 7) - 1; remaining >= 0; remaining--) {
			finalWeekDate = dueDate.minusDays(remaining);
			if ((finalWeekDate.getDayOfWeek() == DayOfWeek.SATURDAY || finalWeekDate.getDayOfWeek() == DayOfWeek.SUNDAY)) {
				// add chargeable weekend day directly to chargeableDays if weekends are chargeable
				if (tool.isChargeOnWeekends()) {
					chargeableDays++;
				}
			} else {
				totalWeekdays++; 
			}
		}
		
		// For weekdays, add 5 * (# of total full weeks)
		totalWeekdays += (5 * (rentalDays / 7));
		
		// For weekends (if chargeable), add 2 * (# of total full weeks) directly to chargeableDays
		if (tool.isChargeOnWeekends()) {
			chargeableDays += (2 * (rentalDays / 7));
		}
		
		// Determine how totalHolidays and totalWeekdays should be added to chargeableDays,
		// especially since totalWeekdays may potentially include holidays
		if (tool.isChargeOnWeekdays()) {
			chargeableDays += totalWeekdays;
			if (!tool.isChargeOnHolidays()) {
				chargeableDays -= totalHolidays;
			}
		} else if (tool.isChargeOnHolidays()) {
			chargeableDays += totalHolidays;
		}
		
		return chargeableDays;
	}
	
	/**
	 * This is a helper method to determine if the given targetDate is between
	 * the given startDate and endDate. If the target date is after the
	 * startDate and is equal to the endDate, then this evaluates to true.
	 * 
	 * Note: If the startDate and endDate are not at least 1 day apart,
	 * the method will return false.
	 * 
	 * @param targetDate The target date to check.
	 * @param startDate The start of the time period to check
	 * @param endDate The end of the time period to check
	 * @return true if the targetDate is within range, false otherwise
	 */
	private static boolean dateExistsInRange(LocalDate targetDate, LocalDate startDate, LocalDate endDate) {
		if (startDate.isBefore(targetDate) && (targetDate.isBefore(endDate) || targetDate.isEqual(endDate))) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Calculates what the observed date is for a holiday (July 4th and Labor Day)
	 * for a given holiday's month and year. Only the months JULY and SEPTEMEBER
	 * are considered valid holiday months for Rent-A-Tool's calendar year.
	 * 
	 * @param holidayMonth The Month enum of the holiday (either JULY or SEPTEMBER)
	 * @param holidayYear The year to calculate the observed holiday date for.
	 * @return LocalDate representing the observed date for a given Month and year.
	 * If an invalid Month enum is passed in, the method returns null.
	 */
	public LocalDate calculateObservedHolidayDate(Month holidayMonth, int holidayYear) {
		LocalDate observedDate = null;
		
		switch (holidayMonth) {
			case JULY : // July 4th
				observedDate = checkoutDate.withYear(holidayYear).withMonth(7).withDayOfMonth(4);
				
				// Adjusting when the 4th falls on a weekend
				if (observedDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
					observedDate = observedDate.minusDays(1);
				} else if (observedDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
					observedDate = observedDate.plusDays(1);
				}
				
				break;
				
			case SEPTEMBER : // Labor Day
				observedDate = checkoutDate
								.withYear(holidayYear)
								.withMonth(9)
								.with(TemporalAdjusters.dayOfWeekInMonth(1, DayOfWeek.MONDAY));
				
				break;
				
			default :
				System.out.println("The method calculateObservedHolidayDate was called with a value other than JULY or SEPTEMBER. Returning a null LocalDate");
		}
		
		return observedDate;
	}
	
	/**
	 * Calculates the discountAmount using the instance's discountPercent and preDiscountCharge
	 * properties.
	 * 
	 * @return BigDecimal representing the discountAmount with cents rounded half up.
	 */
	public BigDecimal calculateDiscountAmount() {
		// Step 1: convert "discountPercent" to float
		// Step 2: multiply preDiscountCharge * step 1 float for a new double value
		// Step 3: round step2 double to nearest half_up cent and return new BigDecimal
		
		float discountPercentAsFloat = discountPercent / 100f;
		BigDecimal calculatedDiscountAmount = preDiscountCharge.multiply(BigDecimal.valueOf(discountPercentAsFloat));
		
		return calculatedDiscountAmount.setScale(2, RoundingMode.HALF_UP);
	}
	
	/**
	 * Calculates the preDiscountCharge using the instance's dailyRentalCharge and
	 * totalChargeableDays properties. The dailyRentalCharge is multiplied by
	 * totalChargeableDays.
	 * 
	 * @return BigDecimal representing the preDiscountCharge with cents rounded half up.
	 */
	public BigDecimal calculatePreDiscountCharge() {
		BigDecimal dailyRentalChargeBD = BigDecimal.valueOf(dailyRentalCharge);
		BigDecimal totalChargeableDaysBD = BigDecimal.valueOf(totalChargeableDays);
		BigDecimal preDiscountCharge = dailyRentalChargeBD.multiply(totalChargeableDaysBD);
		return preDiscountCharge.setScale(2, RoundingMode.HALF_UP);
	}
	
	/**
	 * Calculates the finalCharge using the instance's preDiscountCharge and
	 * discountAmount properties. The discountAmount amount is subtracted
	 * from the preDiscountCharge.
	 * 
	 * @return BigDecimal representing the finalCharge.
	 */
	public BigDecimal calculateFinalCharge() {
		return preDiscountCharge.subtract(discountAmount);
	}
	
	/**
	 * Formats all of the instance's properties into a String
	 * and prints the result to System.out.
	 * The string is formatted using String.format and is built
	 * using StringBuilder.
	 * 
	 * @return formatted String value representing this RentalAgreement.
	 */
	public String printRentalAgreement() {
		StringBuilder output = new StringBuilder();
		output.append(String.format("Tool code: %s\n", code.toString()));
		output.append(String.format("Tool type: %s\n", type.toString()));
		output.append(String.format("Tool brand: %s\n", brand.toString()));
		output.append(String.format("Rental days: %d\n", rentalDays));
		output.append(String.format("Check out date: %tD\n", checkoutDate));
		output.append(String.format("Due date: %tD\n", dueDate));
		output.append(String.format("Daily rental charge: $%,.2f\n", dailyRentalCharge));
		output.append(String.format("Charge days: %d\n", totalChargeableDays));
		output.append(String.format("Pre-discount charge: $%,.2f\n", preDiscountCharge));
		output.append(String.format("Discount percent: %d%%\n", discountPercent));
		output.append(String.format("Discount amount: $%,.2f\n", discountAmount));
		output.append(String.format("Final charge: $%,.2f", finalCharge));
		
		System.out.println(output.toString());
		
		return output.toString();
	}

	// Only generating Getters as all attributes should only be set via the RentalAgreement constructor
	
	public Tool getTool() {
		return tool;
	}

	public Code getCode() {
		return code;
	}

	public Type getType() {
		return type;
	}

	public Brand getBrand() {
		return brand;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public BigDecimal getFinalCharge() {
		return finalCharge;
	}

	public BigDecimal getPreDiscountCharge() {
		return preDiscountCharge;
	}

	public float getDailyRentalCharge() {
		return dailyRentalCharge;
	}

	public int getTotalChargeableDays() {
		return totalChargeableDays;
	}

	public int getDiscountPercent() {
		return discountPercent;
	}

	public int getRentalDays() {
		return rentalDays;
	}

	public LocalDate getCheckoutDate() {
		return checkoutDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}
	
	
}
