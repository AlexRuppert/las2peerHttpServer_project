package i5.httpServer;


/**
 * Exception thrown at the request for an not implemented Request method
 *
 * @author: Holger Janﬂen
 * @version $Revision: 1.1 $, $Date: 2013/11/21 02:00:54 $
 */
public class NotImplementedException extends RuntimeException
{
	
	public NotImplementedException ( String message ) {
		super ( message );
	}
	
}

