package i5.httpServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedOutputStream;

import java.net.SocketException;

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * Simple Http server understanding basic http 1.0 and 1.1
 *
 * atm only POST and GET is implemented
 *
 * only single threaded processing
 *
 * @author Holger Janßen
 * @version $Revision: 1.1 $, $Date: 2013/11/21 02:00:54 $
 */

public class HttpServer extends Thread
{
	
	/**
	 * subclass implementing the handling of incoming socket connection
	 * in a single thread
	 *
	 */
	protected class SocketRequestHandler implements Runnable {
		
		protected Socket socket;
		protected InputStream input;
		protected BufferedReader reader;
		
		protected RequestHandler handler;
		
		protected String[] header;
		
		protected HttpRequestImpl request;
		protected HttpResponseImpl response;
		
		protected boolean enableXOriginResSharing = false;
		protected String allowedOrigin = null;
		protected int preflightRequestMaxAge = -1;
		
		
		/**
		 * Generates a new Socket request handler
		 *
		 * @param    socket              a  Socket
		 * @param    handler             the http request handler building a HttpResponse
		 * 								 from the generated HttpRequest
		 *
		 * @exception   IOException
		 *
		 */
		public SocketRequestHandler ( Socket socket, RequestHandler handler ) throws IOException {
			this(socket, handler, null, -1);
		}
		
		
		/**
		 * Generates a new Socket request handler with support for Cross-Origin Resource Sharing
		 *
		 * @param    socket              a  Socket
		 * @param    handler             the http request handler building a HttpResponse
		 * 								 from the generated HttpRequest
		 *
		 * @exception   IOException
		 *
		 */
		public SocketRequestHandler ( Socket socket, RequestHandler handler, String allowedOrigin, int preflightRequestMaxAge) throws IOException {
			this.socket = socket;
			this.handler = handler;
			
			input = socket.getInputStream();
			reader = new BufferedReader ( new InputStreamReader ( input ) );
			
			this.allowedOrigin = allowedOrigin;
			this.preflightRequestMaxAge = preflightRequestMaxAge;
			if (this.allowedOrigin != null && this.preflightRequestMaxAge >= 0)
				this.enableXOriginResSharing = true;
		}
		
		
		
		/**
		 * try to read the header of a http request
		 *
		 * @exception   MalformedHeaderException 	IO error reading the header
		 *
		 */
		protected void readHeader () throws MalformedHeaderException {
			try {
				StringBuffer sbRead = new StringBuffer ();
				
				// read the complete header (until first empty line)
				String line = null;
				boolean emptyRead = false;
				while ( ! emptyRead && (line = reader.readLine()) != null) {
					// readLine does not return the line termination char!
					if ( line == null || line.equals( "" ) )
						emptyRead = true;
					else
						sbRead.append ( line ).append("\n");
				}
				
				// remove last newline
				String header = sbRead.substring( 0, Math.max(0, sbRead.length()-1) );
				
				// protocol compliance modifications
				// join lines beginning with whitespaces to the line before
				header = header.replaceAll ( "\n[\\t ]+", " " );
				
				// collapse multiple whitespaces
				header = header.replaceAll ( "[ \\t]+", " " );
				
				// remove trailing whitespaces
				header = header.replaceAll ( "\\s\n", "\n" );
				header = header.replaceAll ( "\\s+$", "" );
				
				// split header fileds
				this.header = header.split ( "\n" );
			} catch (IOException e) {
				throw new MalformedHeaderException ( "Unable to read a complete HTTP header from the input!", e );
			}
		}
		
		
		/**
		 * generate a {@link HttpRequestImpl} instance from the read header and hand it over
		 * to the {@link RequestHandler} in charge to get a {@link HttpResponseImpl}
		 *
		 * @exception   IOException
		 *
		 */
		public void handleRequest () throws IOException {
			request = new HttpRequestImpl ( reader, socket );
			response = null;
			
			try {
				request.parseHeader ( header );
			} catch (InvalidMethodException e) {
				response = new HttpResponseImpl ( request, HttpResponse.STATUS_NOT_IMPLEMENTED );
			} catch (MalformedHeaderException e) {
				response = new HttpResponseImpl ( request, HttpResponse.STATUS_BAD_REQUEST );
			} catch (UnsupportedProtocolVersionException e) {
				response = new HttpResponseImpl ( request, HttpResponse.STATUS_HTTP_VERSION_NOT_SUPPORTED );
			}

			if ( response == null )	{
				if ( request.getMethod() == HttpRequest.METHOD_POST
					|| request.getMethod() == HttpRequest.METHOD_GET
					|| request.getMethod() == HttpRequest.METHOD_DELETE
					|| request.getMethod() == HttpRequest.METHOD_HEAD
					|| request.getMethod() == HttpRequest.METHOD_PUT)
					 {
					response = new HttpResponseImpl ( request );

					try {
						handler.processRequest ( request, response );
						//add Cross-Origin Resource Sharing header fields
						if (enableXOriginResSharing)
							addXOriginResSharingHeaderFields();
					} catch ( Exception e ) {
						response = new HttpResponseImpl ( request, HttpResponse.STATUS_INTERNAL_SERVER_ERROR );
						
						e.printStackTrace();
					}
				} else if (request.getMethod() == HttpRequest.METHOD_OPTIONS && enableXOriginResSharing) {
					//this is a Preflight Access Control Request
					response = new HttpResponseImpl(request, HttpResponse.STATUS_OK);
					addXOriginResSharingHeaderFields();
				}
				else
					response = new HttpResponseImpl ( request, HttpResponse.STATUS_NOT_IMPLEMENTED );
			}
		}
		
		/**
		 * add additional fields to the header to enable Cross-Origin Resource Sharing
		 *
		 * @param request
		 * @param response
		 */
		protected void addXOriginResSharingHeaderFields() {
			//extract requested headers
			String requestedHeaders = request.getHeaderField("Access-Control-Request-Headers");
			response.setHeaderField("Access-Control-Allow-Origin", allowedOrigin);
			//just reply all requested headers
			if (requestedHeaders != null)
				response.setHeaderField("Access-Control-Allow-Headers", requestedHeaders);
			response.setHeaderField("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, HEAD, OPTIONS");
			response.setHeaderField("Access-Control-Max-Age", "" + preflightRequestMaxAge);
		}
		
		
		/**
		 * write the response back to the socket
		 *
		 * @exception   IOException
		 *
		 */
		public void writeResponse () throws IOException {
			// keep alive not yet implemented
			if ( request != null && request.hasHeaderField( "connection" ) )
				response.setHeaderField ( "Connection", "close" );
			
			OutputStream out = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter ( new BufferedOutputStream (out ) );
			
			if ( response.getProtocolVersion() == HttpRequest.HTTP_VERSION_1_1 )
				osw.write( "HTTP/1.1 " );
			else
				osw.write ( "HTTP/1.0 " );
	
			osw.write ( ""+response.getStatus() );
			osw.write ( " " );
			osw.write ( HttpResponseImpl.getStatusMessage( response.getStatus() ) );
			osw.write ( "\r\n" );
			
			osw.write ( response.getHeaders () );
			
			osw.write ( "\r\n" );
			
			osw.write ( response.getContent() );
			
			osw.flush();
			
			socket.close();
		}
		
		
		
		/**
		 * When an object implementing interface <code>Runnable</code> is used
		 * to create a thread, starting the thread causes the object's
		 * <code>run</code> method to be called in that separately executing
		 * thread.
		 * <p>
		 * The general contract of the method <code>run</code> is that it may
		 * take any action whatsoever.
		 *
		 * @see     java.lang.Thread#run()
		 */
		public void run() {
			try {
				readHeader ();
				handleRequest();
				writeResponse ();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (MalformedHeaderException e ) {
				e.printStackTrace();
			}
		}
		
	} // nested class RequestHandler
	
	
	
	
	
	private RequestHandler standardHandler;
	
	private String sHandlerClass = null;
	
	public static final int DEFAULT_HTTP_PORT = 8080;
	
	private int iPort = DEFAULT_HTTP_PORT;
	
	private int iSocketTimeout = 60* 1000;  // default: 1 minute
	
	private boolean bStopped = false;
	
	private boolean bIsRunning = false;
	
	private String sAllowedOrigin = null;
	private int iPreflightRequestMaxAge = 60; // default: 1 minute
	
	
	private ThreadGroup tgRequestHandlers = new ThreadGroup ( "request handlers" );
	
	protected ServerSocket serverSocket;
	
	
	/**
	 * generates a new instance of the server
	 *
	 * @param    sHandlerClass       a  String
	 *
	 */
	public HttpServer ( String handlerClass ) {
		super ();
		
		//setDaemon( true );
		
		this.sHandlerClass = handlerClass;
	}
	
	/**
	 * generates a new instance if the server
	 *
	 * @param    handlerClass        a  String
	 * @param    port                an int
	 *
	 */
	public HttpServer ( String handlerClass, int port ) {
		this ( handlerClass );
		iPort = port;
	}
	
	/**
	 * generates a new instance if the server
	 *
	 * @param	handlerClass			a  String
	 * @param	xDomainResSharingOrigin	a  String
	 *
	 */
	public HttpServer ( String handlerClass, String xDomainResSharingOrigin ) {
		this ( handlerClass );
		sAllowedOrigin = xDomainResSharingOrigin;
	}
	
	/**
	 * generates a new instance if the server
	 *
	 * @param	handlerClass			a  String
	 * @param	port					an int
	 * @param	xDomainResSharingOrigin	a  String
	 *
	 */
	public HttpServer ( String handlerClass, int port, String xDomainResSharingOrigin ) {
		this ( handlerClass );
		iPort = port;
		sAllowedOrigin = xDomainResSharingOrigin;
	}
	
	/**
	 * generates a new instance if the server
	 *
	 * @param	handlerClass			a  String
	 * @param	xDomainResSharingOrigin	a  String
	 * @param	preflightRequestMaxAge	an int
	 *
	 */
	public HttpServer ( String handlerClass, String xDomainResSharingOrigin, int preflightRequestMaxAge ) {
		this ( handlerClass );
		sAllowedOrigin = xDomainResSharingOrigin;
		iPreflightRequestMaxAge = preflightRequestMaxAge;
	}
	
	/**
	 * generates a new instance if the server
	 *
	 * @param	handlerClass			a  String
	 * @param	port					an int
	 * @param	xDomainResSharingOrigin	a  String
	 * @param	preflightRequestMaxAge	an int
	 *
	 */
	public HttpServer ( String handlerClass, int port, String xDomainResSharingOrigin, int preflightRequestMaxAge ) {
		this ( handlerClass );
		iPort = port;
		sAllowedOrigin = xDomainResSharingOrigin;
		iPreflightRequestMaxAge = preflightRequestMaxAge;
	}
	

	/**
	 * Sets the socket timeout if the Thread is not running
	 *
	 * @param    timeoutMs           a  int
	 *
	 */
	public void setSocketTimeout ( int timeoutMs ) {
		if ( bIsRunning )
			return;
		
		iSocketTimeout = timeoutMs;
	}
	
	/**
	 * returns the port number of this server
	 *
	 * @return   an int
	 *
	 */
	public int getPort () {
		return iPort;
	}
	
	
	/**
	 * returns the socket timeout value in ms
	 *
	 * @return   an int
	 *
	 */
	public int getSocketTimeout () {
		return iSocketTimeout;
	}
	
	
	/**
	 *
	 * @return   the current default request handler
	 *
	 */
	public RequestHandler getHandler () {
		return this.standardHandler;
	}

	
	
	/**
	 * initialized the handler instance
	 *
	 */
	protected void initializeHandler () {
		try {
			Class clHandler = Class.forName( sHandlerClass );

			standardHandler = (RequestHandler) clHandler.newInstance();
		} catch (Exception e) {
			System.err.println ( "Unable to instantiate handler " + sHandlerClass + "!" );
			e.printStackTrace();
			return;
		}
	}
	
	
	/**
	 * open the server socket
	 *
	 */
	protected void openServerSocket () {
		System.out.println("Starting HTTP-Server on port " + iPort );
		
		try {
			serverSocket = new ServerSocket ( iPort );
		} catch (IOException e) {
			System.err.println ( "Unable to generate server socket on port " + iPort + "!" );
			throw new UnableToStartServerException ( e );
		}
				
		try {
			serverSocket.setSoTimeout( iSocketTimeout );
		} catch (SocketException e) {
			throw new UnableToStartServerException ( e );
		}
		
		register( this );
	}
	
	
	/**
	 * The main part of the server thread: the listening loop
	 *
	 * @exception   InterruptedException
	 *
	 */
	protected void startListeningLoop () throws InterruptedException {
		bIsRunning = true;
		
		while ( !isInterrupted() && ! bStopped ) {
			try {
				Socket incoming = serverSocket.accept();
				
				Thread handlerThread = new Thread ( tgRequestHandlers, new SocketRequestHandler ( incoming, standardHandler, sAllowedOrigin, iPreflightRequestMaxAge ));
				handlerThread.start();
			} catch ( SocketTimeoutException e ) {
			} catch (IOException e) {
				System.err.println("Error getting request socket!");
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * close the server socket and stop all left handler threads
	 *
	 */
	protected void tidyUp () {
		release ( this );

		try {
			if ( serverSocket != null )
				serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// try friendly
		tgRequestHandlers.interrupt();
		try {
			Thread.sleep( 2000 );
		} catch (InterruptedException e) {
		}
		
		// ok, then kill everything left
		tgRequestHandlers.stop();
		
		System.out.println ( "Server at port " + iPort + " has been stopped!" );
	}
	
	
	
	
	
	/**
	 * starts the serverSocket, listens for incoming connections
	 * and handles them.
	 *
	 * atm only single threaded!
	 *
	 */
	public void run () throws UnableToStartServerException {
		try {
			initializeHandler();
			openServerSocket();
			startListeningLoop();
		} catch ( InterruptedException ie ) {
			ie.printStackTrace();
		} catch ( ThreadDeath d ) {
			d.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			tidyUp();
		}
	}

	
	
	
	/**
	 * method to start the server.
	 *
	 * understandable commandline parameters:
	 * 	-h 	standard handler for http requests
	 *  -p  port for the server socket
	 *
	 * @param    argv                a  String[]
	 *
	 */
	public static void main ( String[] argv ) {
		boolean hasError = false;
		
		String sHandlerClass = null;
		int iPort = DEFAULT_HTTP_PORT;
		
		for ( int i = 0; i<argv.length; i++ ) {
			if ( argv[i].equals ( "-h" ) ) {
				i++;
				sHandlerClass = argv[i];
			} else if ( argv[i].equals ( "-p" ) ) {
				i++;
				iPort = Integer.valueOf( argv[i] ).intValue();
			} else {
				System.err.println ( "unknown parameter: " + argv[i] );
				hasError = true;
			}
		}
		
		
		if ( sHandlerClass == null ) {
			System.err.println ( "No RequestHandler given (via -h)!" );
			hasError = true;
		}
		
		if ( ! hasError ) {
			System.out.println( "Starting server at port " + iPort + "!" );
			HttpServer server = new HttpServer ( sHandlerClass, iPort );
			server.start ();
		}
	}
	
	
	/**
	 * container to store all running server instances in this vm
	 **/
	private static Hashtable htServers = new Hashtable();
	
	/**
	 * registers a server for getting it via (@see #getServer)
	 *
	 * @param    server              a  HttpServer
	 *
	 */
	protected static void register ( HttpServer server ) {
		if ( server.isAlive() )
			htServers.put ( new Integer ( server.getPort() ), server );
	}
	
	/**
	 * releases a previously registered server
	 *
	 * @param    server              a  HttpServer
	 *
	 */
	protected static void release ( HttpServer server ) {
		if ( ! server.isAlive() )
			htServers.remove( new Integer ( server.getPort() ) ) ;
	}
	
	/**
	 * returns the server running at the given port
	 *
	 * @param    port                an int
	 *
	 * @return   a HttpServer
	 *
	 */
	public static HttpServer getServer ( int port ) {
		return (HttpServer) htServers.get ( new Integer ( port ) );
	}
	
	/**
	 * returns the ports of all servers managed by this class
	 *
	 * @return   an int[]
	 *
	 */
	public static int[] getServerPorts () {
		Enumeration ports = htServers.keys();
		
		int[] result = new int [ htServers.size() ];
		
		for ( int i = 0; i< result.length; i++ ) {
			result[i] = ((Integer) ports.nextElement()).intValue();
		}
		
		return result;
	}
	
	
	/**
	 * Stops the server via an interrupt and a seperate
	 * stopped flag.
	 *
	 * The stopped flag became necessary since in the http connector
	 * of the las, this thread refused to set the interupted status
	 * after calling the interrupt method.
	 *
	 */
	public synchronized void stopServer () {
		super.interrupt();
		bStopped = true;
	}

	/**
	 * Sets the port the server should listen on. <br />
	 * <b>Note:</b> The port can only be set if the server is not running.
	 * 
	 * @param httpPort the port number
	 */
	public void setPort(int httpPort) {
		if(bIsRunning) throw new IllegalStateException("Cannot change port, if server is already running!");
		
		iPort = httpPort;
	}
	
	
}

