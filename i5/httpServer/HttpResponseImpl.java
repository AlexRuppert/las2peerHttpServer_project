package i5.httpServer;

import java.util.Hashtable;
import java.util.Enumeration;

import java.io.StringWriter;
import java.io.Writer;
import java.io.OutputStream;



/**
 * Class for generating a valid http response.
 *
 * The default response is a STATUS_OK (200) response with content type text/plain.
 * The StringWriter collects all data to be returns to the requesting client.
 *
 * After successfull processing the collected data will be written to the socket
 * output by the HttpServer.
 *
 * @author Holger Janßen
 * @version $Revision: 1.1 $, $Date: 2013/11/21 02:00:54 $
 */

public class HttpResponseImpl implements HttpResponse
{
	
	// 1XX ? information messages
	
	/** Attributes **/
	private int iReturnStatus = STATUS_OK;
	private String sContentType = "text/plain";
	private String sRedirectionLocation = null;
	
	private StringWriter sw = new StringWriter ();
	
	// private boolean bFixed = false;
	
	
	private HttpRequest request = null;
	
	private Hashtable htHeaders = new Hashtable();
	
	private int iProtocolVersion = HttpRequest.HTTP_VERSION_1_0;
	
	
	public HttpResponseImpl ( HttpRequest request ) {
		this.request = request;
		
		if ( request != null )
			iProtocolVersion = request.getProtocolVersion();
	}
	
	public HttpResponseImpl ( int status ) {
		iReturnStatus = status;
		//bFixed = true;
	}
	
	public HttpResponseImpl ( HttpRequest request, int status ) {
		this (request);
		iReturnStatus = status;
		
		//bFixed = true;
	}
	
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#setStatus(int)
	 */
	@Override
	public void setStatus ( int status ) {
		//if ( bFixed )
		//	throw new IllegalAccessException ();
		
		iReturnStatus = status;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#getStatus()
	 */
	@Override
	public int getStatus () {
		return iReturnStatus;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#setContentType(java.lang.String)
	 */
	@Override
	public void setContentType ( String contentType )  {
		//if ( bFixed )
		//	throw new IllegalAccessException ();
		
		sContentType = contentType;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#getContentType()
	 */
	@Override
	public String getContentType () {
		return sContentType;
	}
	
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#redirect(java.lang.String)
	 */
	@Override
	public void redirect ( String location )  {
		//if ( bFixed )
		//	throw new IllegalAccessException ();
		
		
		throw new NotImplementedException( "redirection has not been implemented yet!" );
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#isRedirected()
	 */
	@Override
	public boolean isRedirected () {
		return sRedirectionLocation != null;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#print(java.lang.String)
	 */
	@Override
	public void print( String s )  {
		//if ( bFixed )
		//	throw new IllegalAccessException ();
		sw.write( s );
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#print(java.lang.Object)
	 */
	@Override
	public void print ( Object o ) {
		print ( o.toString () ) ;
	}
		
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#print(byte)
	 */
	@Override
	public void print ( byte b ) {
		sw.write ( b );
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#print(int)
	 */
	@Override
	public void print ( int i ) {
		sw.write ( i );
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#print(boolean)
	 */
	@Override
	public void print ( boolean b ) {
		sw.write ( ""+b );
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#print(char)
	 */
	@Override
	public void print ( char c ) {
		sw.write ( c );
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#println(java.lang.String)
	 */
	@Override
	public void println ( String s )  {
		//if ( bFixed )
		//	throw new IllegalAccessException ();
		print ( s + "\n" );
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#println(java.lang.Object)
	 */
	@Override
	public void println ( Object o ) {
		print ( o.toString () + "\n" );
	}
	
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#clearContent()
	 */
	@Override
	public void clearContent () {
		sw = new StringWriter ();
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#setHeaderField(java.lang.String, java.lang.String)
	 */
	@Override
	public void setHeaderField ( String field, String value )  {
		//if ( bFixed )
		//	throw new IllegalAccessException ();
		if ( field == null || field.equals ("") || "".equals ( value ) )
			return;
		
		if ( value == null )
			htHeaders.remove( field );
		else
			htHeaders.put ( field, value );
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#getHeaderField(java.lang.String)
	 */
	@Override
	public String getHeaderField ( String field ) {
		return (String) htHeaders.get ( field );
	}
	
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#getProtocolVersion()
	 */
	@Override
	public int getProtocolVersion ( ) {
		return iProtocolVersion;
	}
	
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpResponse#getOutputWriter()
	 */
	@Override
	public Writer getOutputWriter () {
		return sw;
	}
	
	
	
	/**
	 * Method getHeaders
	 *
	 * @return   a String
	 */
	String getHeaders() {
		StringBuffer result = new StringBuffer ("" );
		
		if ( getContentLength() >= 0 )
			htHeaders.put ( "Content-Length", ""+getContentLength() );
		
		htHeaders.put ( "Content-Type", sContentType );
		
		Enumeration keys = htHeaders.keys();
		while ( keys.hasMoreElements() ) {
			String field = (String) keys.nextElement();
			String value = (String) htHeaders.get ( field );
			
			result.append ( field ).append(": ").append( value ) .append( "\r\n" );
		}
				
		return new String ( result );
	}
	
	
	/**
	 * returns the (current) content length of this response
	 *
	 * @return   a long
	 *
	 */
	long getContentLength () {
		return sw.getBuffer().length();
	}
	
	
	/**
	 * returns the current content as string
	 *
	 * @return   a String
	 *
	 */
	String getContent () {
		return sw.toString();
	}
	
	
	public static String urlEncode ( String s ) {
		String result = s;
		
		return s;
	}

	
	
	
	/**
	 * returns the message corresponding to a http status code
	 *
	 * @param    status              an int
	 *
	 * @return   a String
	 *
	 */
	public static String getStatusMessage ( int status ) {
		if ( status >= 500 ) {
			if ( status == STATUS_INTERNAL_SERVER_ERROR           ) return "Internal Server Error";
			else if ( status == STATUS_NOT_IMPLEMENTED            ) return "Not Implemented";
			else if ( status == STATUS_BAD_GATEWAY                ) return "Bad Gateway";
			else if ( status == STATUS_SERVICE_UNAVAILABLE        ) return "Service Unavailable";
			else if ( status == STATUS_GATEWAY_TIMEOUT            ) return "Gateway Timeout";
			else if ( status == STATUS_HTTP_VERSION_NOT_SUPPORTED ) return "HTTP Version Not Supported";
		} else if ( status >= 400 ) {
			if ( status == STATUS_BAD_REQUEST              		     ) return "Bad Request";
			else if ( status == STATUS_UNAUTHORIZED                  ) return "Unauthorized";
			else if ( status == STATUS_PAYMENT_REQUIRED              ) return "Payment Required";
			else if ( status == STATUS_FORBIDDEN                     ) return "Forbidden";
			else if ( status == STATUS_NOT_FOUND                     ) return "Not Found";
			else if ( status == STATUS_METHOD_NOT_ALLOWED            ) return "Method Not Allowed";
			else if ( status == STATUS_NOT_ACCEPTABLE                ) return "Not Acceptable";
			else if ( status == STATUS_PROXY_AUTH_REQUIRED           ) return "Proxy Authentication Required";
			else if ( status == STATUS_REQUEST_TIMEOUT               ) return "Request Timeout";
			else if ( status == STATUS_CONFLICT                      ) return "Conflict";
			else if ( status == STATUS_GONE                          ) return "Gone";
			else if ( status == STATUS_LENGTH_REQUIRED               ) return "Length Required";
			else if ( status == STATUS_PRECONDITION_FAILED           ) return "Precondition Failed";
			else if ( status == STATUS_REQUEST_ENTITY_TOO_LONG       ) return "Request Entity Too Long";
			else if ( status == STATUS_REQUEST_URI_TOO_LONG          ) return "Request-URI Too Long";
			else if ( status == STATUS_UNSUPPORTED_MEDIA_TYPE        ) return "Unsupported Media Type";
			else if ( status == STATUS_REQUEST_RANGE_NOT_SATISFIABLE ) return "Requested Range Not Satisfiable";
			else if ( status == STATUS_EXPECTATION_FAILED            ) return "Expectation Failed";
		} else if ( status >= 300 ) {
			if ( status == STATUS_MULTIPLE_CHOICES)    return "Multiple Choices";
			else if ( status == STATUS_MOVED_PERM)     return "Moved Permanently";
			else if ( status == STATUS_FOUND)          return "Found";
			else if ( status == STATUS_SEE_OTHER )     return "See Other";
			else if ( status == STATUS_NOT_MODIFIED )  return "Not Modified";
			else if ( status == STATUS_USE_PROXY )     return "Use Proxy";
			else if ( status == STATUS_TEMP_REDIRECT ) return "Temporary Redirect";
		} else if ( status >= 200 ) {
			if ( status == STATUS_OK )                  return "OK";
			else if ( status == STATUS_CREATED )        return "Created";
			else if ( status == STATUS_ACCEPTED )	    return "Accepted";
			else if ( status == STATUS_NON_AUTH )       return "Non-Authoritative Information";
			else if ( status == STATUS_NO_CONTENT )     return "No Content";
			else if ( status == STATUS_RESET_CONTENT)   return "Reset Content";
			else if ( status == STATUS_PARTIAL_CONTENT) return "Partial Content";
		} else {
			if ( status ==  STATUS_CONTINUE )      return "Continue";
			else if ( status == STATUS_SWITCHING ) return "Switching Protocols";
		}
		
		throw new IllegalArgumentException();
	}
	
	
}

