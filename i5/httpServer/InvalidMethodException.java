package i5.httpServer;

/**
 * Exception thrown by (@see i5.httpServer.HttpRequest#parseHeader) on
 * invalid request methods.
 *
 * @author Holger Janﬂen
 * @version $Revision: 1.1 $, $Date: 2013/11/21 02:00:54 $
 */


public class InvalidMethodException extends MalformedHeaderException
{
	
	public InvalidMethodException ( String method ) {
		super ("Requested method " + method + " is no valid http method!" );
	}
	
}

