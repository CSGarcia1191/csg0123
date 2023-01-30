package rentatool.app;

/**
 * This Exception class was created for invalid argument input during
 * a tool checkout process.
 * 
 * If the clerk inputs an invalid integer for "Rental day count" or
 * "Discount percent", this exception is thrown.
 * 
 * For rental agreements, if an invalid value is passed into the
 * RentalAgremeent constructor, this exception will also be thrown.
 * 
 * When this exception is thrown, it terminates the application.
 * 
 * @author CSGarcia1191
 *
 */
public class InvalidCheckoutArgumentException extends RuntimeException {
	public InvalidCheckoutArgumentException(String errorMessage) {
		super(errorMessage);
	}
}
