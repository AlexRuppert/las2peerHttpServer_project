package i5.httpServer;

/**
 * basic interface of an request Handler.
 * An Implementations must have a standard constructor!
 *
 * @author: Holger Jan0en
 * @version: $Revision: 1.1 $, $Date: 2013/11/21 02:00:54 $
 */
public interface RequestHandler
{
	
	public void processRequest ( HttpRequest request, HttpResponse response ) throws Exception ;
		
}

