package i5.httpServer;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import javax.net.ServerSocketFactory;
import java.net.SocketException;

import java.io.IOException;




/**
 * Simple derivation of the {@link HttpServer} implementing a HTTPS server
 * using JSSE SSL sockets
 *
 * @author Holger Janﬂen
 * @version $Revision: 1.1 $, $Data$
 */


public class HttpsServer extends HttpServer
{
	
	/** The default port for the https socket */
	public static final int DEFAULT_HTTPS_PORT = 8090;
	
	
	
	/**
	 * Constructor
	 *
	 * @param    keyStore            (file) name of the keystore for the server certificate
	 * @param    passwd              password for the keystore
	 * @param    handlerClass        class name of the handler class for http requests
	 * @param    port                post number for the server socket
	 *
	 */
	public HttpsServer ( String keyStore, String passwd, String handlerClass, int port) {
		this ( handlerClass, port );
		
		// set the keystore in the vm
		System.setProperty( "javax.net.ssl.keyStore", keyStore );
		System.setProperty ( "javax.net.ssl.keyStorePassword", passwd );
	}
	
	/**
	 * Constructor
	 *
	 * @param    keyStore            (file) name of the keystore for the server certificate
	 * @param    passwd              password for the keystore
	 * @param    handlerClass        class name of the handler class for http requests
	 * @param    port                post number for the server socket
	 * @param	 xDomainResSharingOrigin	domain from which cross-origin calls are allowed
	 *
	 */
	public HttpsServer ( String keyStore, String passwd, String handlerClass, int port, String xDomainResSharingOrigin) {
		this ( handlerClass, port, xDomainResSharingOrigin );
		
		// set the keystore in the vm
		System.setProperty( "javax.net.ssl.keyStore", keyStore );
		System.setProperty ( "javax.net.ssl.keyStorePassword", passwd );
	}
	
	/**
	 * Constructor
	 *
	 * @param    keyStore            (file) name of the keystore for the server certificate
	 * @param    passwd              password for the keystore
	 * @param    handlerClass        class name of the handler class for http requests
	 * @param    port                post number for the server socket
	 * @param	 xDomainResSharingOrigin	domain from which cross-origin calls are allowed
	 * @param	 preflightRequestMaxAge		max age of a preflight request
	 *
	 */
	public HttpsServer ( String keyStore, String passwd, String handlerClass, int port, String xDomainResSharingOrigin, int preflightRequestMaxAge) {
		this ( handlerClass, port, xDomainResSharingOrigin, preflightRequestMaxAge );
		
		// set the keystore in the vm
		System.setProperty( "javax.net.ssl.keyStore", keyStore );
		System.setProperty ( "javax.net.ssl.keyStorePassword", passwd );
	}
	
	/**
	 * Constructor
	 *
	 * @param    keyStore            (file) name of the keystore for the server certificate
	 * @param    passwd              password for the keystore
	 * @param    handlerClass        class name of the handler class for http requests
	 *
	 */
	public HttpsServer ( String keyStore, String passwd, String handlerClass ) {
		this ( keyStore, passwd, handlerClass, DEFAULT_HTTPS_PORT );
	}
	
	/**
	 * Constructor
	 *
	 * @param    keyStore            (file) name of the keystore for the server certificate
	 * @param    passwd              password for the keystore
	 * @param    handlerClass        class name of the handler class for http requests
	 * @param	 xDomainResSharingOrigin	domain from which cross-origin calls are allowed
	 *
	 */
	public HttpsServer ( String keyStore, String passwd, String handlerClass, String xDomainResSharingOrigin ) {
		this ( keyStore, passwd, handlerClass, DEFAULT_HTTPS_PORT, xDomainResSharingOrigin );
	}
	
	/**
	 * Constructor
	 *
	 * @param    keyStore            (file) name of the keystore for the server certificate
	 * @param    passwd              password for the keystore
	 * @param    handlerClass        class name of the handler class for http requests
	 * @param	 xDomainResSharingOrigin	domain from which cross-origin calls are allowed
	 * @param	 preflightRequestMaxAge		max age of a preflight request
	 *
	 */
	public HttpsServer ( String keyStore, String passwd, String handlerClass, String xDomainResSharingOrigin, int preflightRequestMaxAge ) {
		this ( keyStore, passwd, handlerClass, DEFAULT_HTTPS_PORT, xDomainResSharingOrigin, preflightRequestMaxAge);
	}
	
	/**
	 * Constructor
	 *
	 * @param    handlerClass        class name of the handler class for http requests
	 *
	 */
	public HttpsServer ( String handlerClass ) {
		this ( handlerClass, DEFAULT_HTTPS_PORT );
	}
	
	/**
	 * Constructor
	 *
	 * @param    handlerClass        class name of the handler class for http requests
	 * @param	 xDomainResSharingOrigin	domain from which cross-origin calls are allowed
	 *
	 */
	public HttpsServer ( String handlerClass, String xDomainResSharingOrigin ) {
		this ( handlerClass, DEFAULT_HTTPS_PORT, xDomainResSharingOrigin );
	}
	
	/**
	 * Constructor
	 *
	 * @param    handlerClass        class name of the handler class for http requests
	 * @param	 xDomainResSharingOrigin	domain from which cross-origin calls are allowed
	 * @param	 preflightRequestMaxAge		max age of a preflight request
	 *
	 */
	public HttpsServer ( String handlerClass, String xDomainResSharingOrigin, int preflightRequestMaxAge) {
		this ( handlerClass, DEFAULT_HTTPS_PORT, xDomainResSharingOrigin, preflightRequestMaxAge);
	}
	
	/**
	 * Constructor
	 *
	 * @param    handlerClass        class name of the handler class for http requests
	 * @param    port                post number for the server socket
	 *
	 */
	public HttpsServer ( String handlerClass, int port ) {
		super ( handlerClass, port );
		
		if ( System.getProperty ( "javax.net.ssl.keyStore" ) == null )
			throw new RuntimeException ( "No Keystore is set!" );
	}
	
	/**
	 * Constructor
	 *
	 * @param    handlerClass        class name of the handler class for http requests
	 * @param    port                post number for the server socket
	 * @param	 xDomainResSharingOrigin	domain from which cross-origin calls are allowed
	 *
	 */
	public HttpsServer ( String handlerClass, int port, String xDomainResSharingOrigin) {
		super ( handlerClass, port, xDomainResSharingOrigin);
		
		if ( System.getProperty ( "javax.net.ssl.keyStore" ) == null )
			throw new RuntimeException ( "No Keystore is set!" );
	}
	/**
	 * Constructor
	 *
	 * @param    handlerClass        class name of the handler class for http requests
	 * @param    port                post number for the server socket
	 * @param	 xDomainResSharingOrigin	domain from which cross-origin calls are allowed
	 * @param	 preflightRequestMaxAge		max age of a preflight request
	 *
	 */
	public HttpsServer ( String handlerClass, int port, String xDomainResSharingOrigin, int preflightRequestMaxAge ) {
		super ( handlerClass, port, xDomainResSharingOrigin, preflightRequestMaxAge);
		
		if ( System.getProperty ( "javax.net.ssl.keyStore" ) == null )
			throw new RuntimeException ( "No Keystore is set!" );
	}
	
	
	
	/**
	 * override the server socket opening of the base http server to
	 * open an ssl server socket.
	 *
	 * no further adaptions needed!
	 *
	 */
	protected void openServerSocket () {
		System.out.println ( "Starting simple https server at port " + getPort() );
		
		try {
			ServerSocketFactory ssocketFactory = SSLServerSocketFactory.getDefault();

			serverSocket = (SSLServerSocket) ssocketFactory.createServerSocket( getPort() );
		} catch (IOException e) {
			System.err.println ( "Unable to generate server socket on port " + getPort() + "!" );
			throw new UnableToStartServerException ( e );
		}
				
		try {
			serverSocket.setSoTimeout( getSocketTimeout() );
		} catch (SocketException e) {
			throw new UnableToStartServerException ( e );
		}
		
		HttpServer.register( this );
	}
	
	
	
	
	/**
	 * start a server
	 *
	 * @param    argv                a  String[]
	 *
	 */
	public static void main ( String[] argv ) {
		boolean hasError = false;
		
		String sHandlerClass = null;
		int iPort = DEFAULT_HTTPS_PORT;

		String sKeystore = System.getProperty( "javax.net.ssl.keyStore" );
		String sKeyPass = System.getProperty ( "javax.net.ssl.keyStorePassword" );
		
		
		for ( int i = 0; i<argv.length; i++ ) {
			if ( argv[i].equals ( "-h" ) ) {
				i++;
				sHandlerClass = argv[i];
			} else if ( argv[i].equals ( "-p" ) ) {
				i++;
				iPort = Integer.valueOf( argv[i] ).intValue();
			} else if ( argv[i].equals ( "-k" ) ) {
				i++;
				sKeystore = argv[i];
				
				i++;
				sKeyPass = argv[i];
			} else {
				System.err.println ( "unknown parameter: " + argv[i] );
				hasError = true;
			}
		}
		
		
		if ( sHandlerClass == null ) {
			System.err.println ( "No RequestHandler given (via -h)!" );
			hasError = true;
		}
		
		if ( sKeystore == null || sKeyPass == null) {
			System.err.println ( "Incomplete keystore definition for SSL!" );
			System.err.println("Define a keystore via system properties javax.net.ssl.keyStore and javax.net.ssl.keyStorePassword or the -k command line parameter!");
			hasError = true;
		}
		
		if ( ! hasError ) {
			System.out.println( "Starting server at port " + iPort + "!" );
			HttpServer server = new HttpServer ( sHandlerClass, iPort );
			server.start ();
		}
	}
	
	
}


