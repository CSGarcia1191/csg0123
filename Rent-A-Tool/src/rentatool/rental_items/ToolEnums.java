package rentatool.rental_items;

/**
 * This ToolEnums class is used to enforce valid Tool
 * Codes, Types, Brands, and Attributes used with
 * the Rent-A-Tool application. Since there are many
 * CRUD methods defined throughout the application
 * that are accessible by the outside world,
 * setting these Enums acts as a mechanism to ensure
 * that only valid Tools can be created, read,
 * updated, and deleted.
 * 
 * @author CSGarcia1191
 *
 */
public final class ToolEnums {
	public enum Attribute {
		CODE("Code"), TYPE("Type"), BRAND("Type"),
		DAILYCHARGE("Daily charge"),
		CHARGEONWEEKDAYS("Weekday charge"),
		CHARGEONWEEKENDS("Weekday charge"),
		CHARGEONHOLIDAYS("Holiday charge"),
		CHECKEDOUT("Checked out");
		
		private final String str;
		
		Attribute(String str) {
			this.str = str;
		}
		
		@Override
		public String toString() {
			return str;
		}
	}
	
	public enum Code {
		CHNS, LADW, JAKD, JAKR;
	}
	
	public enum Type {
		CHAINSAW("Chainsaw"), LADDER("Ladder"), JACKHAMMER("Jackhammer");
		
		private final String str;
		
		Type(String str) {
			this.str = str;
		}
		
		@Override
		public String toString() {
			return str;
		}
	}
	
	public enum Brand {
		STIHL("Stihl"), WERNER("Werner"), DEWALT("DeWalt"), RIDGID("Ridgid");
		
		private final String str;
		
		Brand(String str) {
			this.str = str;
		}
		
		@Override
		public String toString() {
			return str;
		}
	}
}
