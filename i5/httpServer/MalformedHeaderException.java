package i5.httpServer;


/**
 * Exception thrown on problems with the header of an http request.
 * Thrown by {@link i5.httpServer.HttpRequest#parseHeader} and
 * {@link i5.httpServer.HttpServer#SocketRequestHandler#readHeader}.
 *
 * @author Holger Janﬂen
 * @version $Revision: 1.1 $, $Date: 2013/11/21 02:00:54 $
 */


public class MalformedHeaderException extends Exception
{
	
	public MalformedHeaderException ( String message ) {
		super ( message );
	}
	
	
	public MalformedHeaderException (String message, Exception cause ) {
		super ( message, cause );
	}
	
}

