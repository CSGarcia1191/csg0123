package rentatool.rental_items;

import rentatool.rental_items.ToolEnums.*;

/**
 * An abstract class that lays out the blueprint for all
 * children Tool classes (Chainsaw, Jackhammer, Ladder).
 * The application should not be allowed to create
 * instances of this class. This restriction is in place
 * so that the creation and retrieval of Tool instance
 * types is reinforced. Thus, it is made abstract.
 * 
 * @author CSGarcia1191
 *
 */
public abstract class Tool {

	private Code code;
	private Type type;
	private Brand brand;
	private float dailyCharge;
	private boolean chargeOnWeekdays;
	private boolean chargeOnWeekends;
	private boolean chargeOnHolidays;
	private boolean checkedOut;
	
	public Tool(Code code, Type type, Brand brand, float dailyCharge, boolean chargeOnWeekdays,
			boolean chargeOnWeekends, boolean chargeOnHolidays, boolean checkedOut) {
		this.code = code;
		this.type = type;
		this.brand = brand;
		this.dailyCharge = dailyCharge;
		this.chargeOnWeekdays = chargeOnWeekdays;
		this.chargeOnWeekends = chargeOnWeekends;
		this.chargeOnHolidays = chargeOnHolidays;
		this.checkedOut = checkedOut;
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}
	
	public float getDailyCharge() {
		return dailyCharge;
	}

	public void setDailyCharge(float dailyCharge) {
		this.dailyCharge = dailyCharge;
	}

	public boolean isChargeOnWeekdays() {
		return chargeOnWeekdays;
	}

	public void setChargeOnWeekdays(boolean chargeOnWeekdays) {
		this.chargeOnWeekdays = chargeOnWeekdays;
	}

	public boolean isChargeOnWeekends() {
		return chargeOnWeekends;
	}

	public void setChargeOnWeekends(boolean chargeOnWeekends) {
		this.chargeOnWeekends = chargeOnWeekends;
	}

	public boolean isChargeOnHolidays() {
		return chargeOnHolidays;
	}

	public void setChargeOnHolidays(boolean chargeOnHolidays) {
		this.chargeOnHolidays = chargeOnHolidays;
	}

	public String toString() {
		return String.format(
				"Code: %s\nType: %s\nBrand: %s\nDaily Charge: %f\nCharge On Weekdays: %s\nCharge on Weekends: %s\nCharge on Holidays: %s\n",
				code, type, brand,
				dailyCharge,
				chargeOnWeekdays ? "Yes" : "No",
				chargeOnWeekends ? "Yes" : "No",
				chargeOnHolidays ? "Yes" : "No");
	}
	
	public boolean isCheckedOut() {
		return this.checkedOut;
	}
	
	public void setCheckedOut(boolean checkedOut) {
		this.checkedOut = checkedOut;
	}

	// The following hashCode() and equals() methods were generated using Eclipse's
	// "Generate hashCode() and equals()..." option.
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((brand == null) ? 0 : brand.hashCode());
		result = prime * result + (chargeOnHolidays ? 1231 : 1237);
		result = prime * result + (chargeOnWeekdays ? 1231 : 1237);
		result = prime * result + (chargeOnWeekends ? 1231 : 1237);
		result = prime * result + (checkedOut ? 1231 : 1237);
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + Float.floatToIntBits(dailyCharge);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tool other = (Tool) obj;
		if (brand != other.brand)
			return false;
		if (chargeOnHolidays != other.chargeOnHolidays)
			return false;
		if (chargeOnWeekdays != other.chargeOnWeekdays)
			return false;
		if (chargeOnWeekends != other.chargeOnWeekends)
			return false;
		if (checkedOut != other.checkedOut)
			return false;
		if (code != other.code)
			return false;
		if (Float.floatToIntBits(dailyCharge) != Float.floatToIntBits(other.dailyCharge))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
}
