package i5.httpServer;


/**
 * Exception thrown by the (@see i5.httpServer.HttpRequest#parseHeader) method,
 * if it finds a http protocol version other than 1.0 and 1.1
 *
 * @author 		Holger Janﬂen
 * @version $Revision: 1.1 $, $Date: 2013/11/21 02:00:54 $
 */
public class UnsupportedProtocolVersionException extends Exception
{
	

	public UnsupportedProtocolVersionException ( String protocol ) {
		super ( "The stated protocol version " + protocol + " is not supported by this server!" );
	}
}

