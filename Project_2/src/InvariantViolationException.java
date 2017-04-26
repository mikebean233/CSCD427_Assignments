/**
 * Created by michael on 4/25/17.
 */
public class InvariantViolationException extends Exception{
	public static InvariantViolationException buildException(ViolationType type, int limit){
		String message = "An invariant violation was detected: A node had ";

		message += (type == ViolationType.CHILD_OVERFILL || type == ViolationType.KEY_OVERFILL) ? " greater than " : " fewer than ";
		message += limit;
		message += (type == ViolationType.CHILD_OVERFILL || type == ViolationType.CHILD_UNDERFILL) ? " children" : " keys";

		return new InvariantViolationException(message);
	}

	private InvariantViolationException(String message){
		super(message);
	}

	public enum ViolationType {
		KEY_UNDERFILL,
		KEY_OVERFILL,
		CHILD_UNDERFILL,
		CHILD_OVERFILL
	}
}
