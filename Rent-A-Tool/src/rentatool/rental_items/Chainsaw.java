package rentatool.rental_items;

import rentatool.rental_items.ToolEnums.Brand;
import rentatool.rental_items.ToolEnums.Code;
import rentatool.rental_items.ToolEnums.Type;

public class Chainsaw extends Tool{
	/*
	 *  At a minimum, to create a Chainsaw tool the system will need a Code and a Brand.
	 *  Remaining default values:
		// type = Type.CHAINSAW
		// dailyCharge = 1.49f
		// chargeOnWeekdays = true
		// chargeOnWeekends = false
		// chargeOnHolidays = true
		// checkedOut = false
	 */
	public Chainsaw(Code code, Brand brand) {
		this(code, brand, 1.49f, true, false, true, false);
	}
	
	// The system can create a Chainsaw with a custom dailyCharge
	public Chainsaw(Code code, Brand brand, float dailyCharge) {
		this(code, brand, dailyCharge, true, false, true, false);
	}
	
	// The system can create a Chainsaw with a custom dailyCharge and customized chargeable days
	public Chainsaw(Code code, Brand brand, float dailyCharge, boolean chargeOnWeekdays,
			boolean chargeOnWeekends, boolean chargeOnHolidays, boolean checkedOut) {
		super(code, Type.CHAINSAW, brand, dailyCharge, chargeOnWeekdays, chargeOnWeekends, chargeOnHolidays, checkedOut);
	}
}
