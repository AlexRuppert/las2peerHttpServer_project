package i5.httpServer;

/**
 * UnableToStartServerException.java
 *
 * @author Holger Janﬂen
 * @version $Revision: 1.1 $, $Date: 2013/11/21 02:00:54 $
 */

public class UnableToStartServerException extends RuntimeException
{
	
	public UnableToStartServerException ( Exception cause ) {
		super (cause );
	}
	
}

