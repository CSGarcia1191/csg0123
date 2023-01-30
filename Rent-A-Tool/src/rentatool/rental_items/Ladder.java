package rentatool.rental_items;

import rentatool.rental_items.ToolEnums.Brand;
import rentatool.rental_items.ToolEnums.Code;
import rentatool.rental_items.ToolEnums.Type;

public class Ladder extends Tool {
	/*
	 *  At a minimum, to create a Ladder tool the system will need a Code and a Brand.
	 *  Remaining default values:
		// type = Type.LADDER
		// dailyCharge = 1.99f
		// chargeOnWeekdays = true
		// chargeOnWeekends = true
		// chargeOnHolidays = false
		// checkedOut = false
	 */
	public Ladder(Code code, Brand brand) {
		this(code, brand, 1.99f, true, true, false, false);
	}
	
	// The system can create a Ladder with a custom dailyCharge
	public Ladder(Code code, Brand brand, float dailyCharge) {
		this(code, brand, dailyCharge, true, true, false, false);
	}
	
	// The system can create a Ladder with a custom dailyCharge and customized chargeable days
	public Ladder(Code code, Brand brand, float dailyCharge, boolean chargeOnWeekdays,
			boolean chargeOnWeekends, boolean chargeOnHolidays, boolean checkedOut) {
		super(code, Type.LADDER, brand, dailyCharge, chargeOnWeekdays, chargeOnWeekends, chargeOnHolidays, checkedOut);
	}
}
