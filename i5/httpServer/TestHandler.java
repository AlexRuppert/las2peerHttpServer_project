package i5.httpServer;

import java.util.Enumeration;

/**
 * Basic implementation of the (@see i5.httpServer.RequestHandler) interface
 * for Testing purposes.
 *
 * @author 	Holger Janﬂen
 * @version $Revision: 1.1 $, $Date: 2013/11/21 02:00:54 $
 **/

public class TestHandler implements RequestHandler {
	
	/**
	 * Method processRequest
	 *
	 * @param    request             a  HttpRequest
	 * @param    response            a  HttpResponse
	 *
	 */
	public void processRequest(HttpRequest request, HttpResponse response) {
		
		response.setHeaderField ( "Service-Handler", "TestHandler" );
		response.println ( "You requested the following url: " );
		response.println ( request.getUrl() );
	
		response.println ( "\nSent get vars: ");
		Enumeration en = request.getGetVarNames();
		while ( en.hasMoreElements() ) {
			String name = (String) en.nextElement();
			
			response.print ( name  );
			response.print ( "\t:\t" );
			response.println ( request.getGetVar ( name ) );
		}
		
		response.println ( "\nSent post vars: ");
		en = request.getPostVarNames();
		while ( en.hasMoreElements() ) {
			String name = (String) en.nextElement();
			
			response.print ( name  );
			response.print ( "\t:\t" );
			response.println ( request.getPostVar ( name ) );
		}
		
		response.println ( "\nSet header fields: ");
		en = request.getHeaderFieldNames();
		while ( en.hasMoreElements() ) {
			String name = (String) en.nextElement();
			
			response.print ( name  );
			response.print ( "\t:\t" );
			response.println ( request.getHeaderField ( name ) );
		}
		
		
		response.println ( "Protocol-Version: " + request.getProtocolVersion() );
		
		response.println ( "Url-Host: " + request.getHost() );
		
		response.println ( "Content-Length: " + request.getContentLength() );
	}
	
	
	
}

