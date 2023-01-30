package rentatool.rental_items;

import rentatool.rental_items.ToolEnums.Brand;
import rentatool.rental_items.ToolEnums.Code;
import rentatool.rental_items.ToolEnums.Type;

public class Jackhammer extends Tool {
	
	/*
	 *  At a minimum, to create a Jackhammer tool the system will need a Code and a Brand.
	 *  Remaining default values:
		// type = Type.JACKHAMMER
		// dailyCharge = 2.99f
		// chargeOnWeekdays = true
		// chargeOnWeekends = false
		// chargeOnHolidays = false
		// checkedOut = false
	 */
	public Jackhammer(Code code, Brand brand) {
		this(code, brand, 2.99f, true, false, false, false);
	}
	
	// The system can create a Jackhammer with a custom dailyCharge
	public Jackhammer(Code code, Brand brand, float dailyCharge) {
		this(code, brand, dailyCharge, true, false, false, false);
	}
	
	// The system can create a Jackhammer with a custom dailyCharge and customized chargeable days
	public Jackhammer(Code code, Brand brand, float dailyCharge, boolean chargeOnWeekdays,
			boolean chargeOnWeekends, boolean chargeOnHolidays, boolean checkedOut) {
		super(code, Type.JACKHAMMER, brand, dailyCharge, chargeOnWeekdays, chargeOnWeekends, chargeOnHolidays, checkedOut);
	}
}
