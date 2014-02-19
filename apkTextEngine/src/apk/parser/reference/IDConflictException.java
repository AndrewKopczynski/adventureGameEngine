package apk.parser.reference;

/** Thrown when there are conflicting IDs.
 * Used to prevent entities and actors from being created
 * if they try to use an ID that's already taken.
 */
public class IDConflictException extends Exception
{
	private static final long serialVersionUID = 48392482960501L;

      public IDConflictException() {}

      public IDConflictException(String message)
      {
         super(message);
      }
 }
