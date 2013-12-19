package i5.httpServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;



/**
 * Class containing all information about a single http request at the
 * (@see i5.httpServer.HttpServer).
 *
 * @author Holger Janßen
 * @version $Revision: 1.1 $, $Date: 2013/11/21 02:00:54 $
 */


public class HttpRequestImpl implements HttpRequest {
	private int iMethod = METHOD_UNKNOWN;
	private Hashtable htHeader = new Hashtable();
	private Hashtable htPostVars = null;
	private Hashtable htGetVars  = null;
	
	
	private BufferedReader contentReader = null;
	
	private String contentString = null;
	
	private boolean bContentRead = false;
	
	private String sUrl         = null;
	private String sProtocol    = null;
	private String sHost        = null;
	private int    iPort        = -1;
	private String sPath        = null;
	private String sQueryString = null;
	
	private int    iContentLength = 0;
	private int    iProtocolVersion = HTTP_VERSION_UNKNOWN;
	
	private Socket incomingSocket = null;
	private String[] _localHeader;
		
	
	/**
	 * Standard Constructor
	 *
	 * @param    socket              the accepted socket corresponding to this request
	 *
	 */
	public HttpRequestImpl ( BufferedReader input, Socket socket ) throws IOException {
		incomingSocket = socket;
		
		contentReader = input;
	}
	
	/**
	 * Constructor
	 *
	 * Without InputStream, just for Junit Testing!
	 *
	 */
	protected HttpRequestImpl () {
	}
	
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getMethod()
	 */
	@Override
	public int getMethod () {
		return iMethod;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getRemoteAddress()
	 */
	@Override
	public String getRemoteAddress () {
		return incomingSocket.getRemoteSocketAddress().toString();
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#isPostRequest()
	 */
	@Override
	public boolean isPostRequest () {
		return iMethod == METHOD_POST;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#isGetRequest()
	 */
	@Override
	public boolean isGetRequest () {
		return iMethod == METHOD_GET;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#isHeadRequest()
	 */
	@Override
	public boolean isHeadRequest () {
		return iMethod == METHOD_HEAD;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#isDeleteRequest()
	 */
	@Override
	public boolean isDeleteRequest() {
		return iMethod == METHOD_DELETE;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#isPutRequest()
	 */
	@Override
	public boolean isPutRequest() {
		return iMethod == METHOD_PUT;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getHeaderField(java.lang.String)
	 */
	@Override
	public String getHeaderField ( String fieldName ) {
		return (String) htHeader.get ( fieldName.toLowerCase() );
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#hasHeaderField(java.lang.String)
	 */
	@Override
	public boolean hasHeaderField ( String fieldName ) {
		return getHeaderField(fieldName) != null;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getHeaderFieldNames()
	 */
	@Override
	public Enumeration getHeaderFieldNames () {
		return htHeader.keys ();
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getProtocol()
	 */
	@Override
	public String getProtocol () {
		return sProtocol;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getHost()
	 */
	@Override
	public String getHost () {
		return sHost;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getPort()
	 */
	@Override
	public int getPort () {
		return iPort;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getPath()
	 */
	@Override
	public String getPath () {
		return sPath;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getQueryString()
	 */
	@Override
	public String getQueryString () {
		return sQueryString;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getUrl()
	 */
	@Override
	public String getUrl () {
		return sUrl;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getContentReader()
	 */
	@Override
	public BufferedReader getContentReader () {
		if ( iContentLength > 0 )
			return contentReader;
		else
			return null;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getContentLength()
	 */
	@Override
	public int getContentLength () {
		return iContentLength;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getContentType()
	 */
	@Override
	public String getContentType () {
		return getHeaderField( "Content-Type" );
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getContentString()
	 */
	@Override
	public String getContentString() {
		try {
			if ( !bContentRead )
				readContentAsString();
		} catch (TimeoutException e) {
		} catch (IOException e) {
		}
		
		return this.contentString;
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getProtocolVersion()
	 */
	@Override
	public int getProtocolVersion () {
		return iProtocolVersion;
	}
		
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getGetVar(java.lang.String)
	 */
	@Override
	public String getGetVar ( String name ) {
		if ( htGetVars == null )
			decodeGetVars();
		
		return (String) htGetVars.get ( name );
	}
		
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getPostVar(java.lang.String)
	 */
	@Override
	public String getPostVar ( String name ) {
		if ( htGetVars == null )
			decodePostVars();
		
		return (String) htPostVars.get ( name );
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getGetVarNames()
	 */
	@Override
	public Enumeration getGetVarNames () {
		if ( htGetVars == null )
			decodeGetVars();
		
		return htGetVars.keys ();
	}
	
	/* (non-Javadoc)
	 * @see i5.httpServer.HttpRequest#getPostVarNames()
	 */
	@Override
	public Enumeration getPostVarNames () {
		if ( htPostVars == null )
			decodePostVars ();
		
		return htPostVars.keys();
	}
	
	public String toString()
	{
		String s="";
		for (int i = 0; i < _localHeader.length; i++) {
			s+=_localHeader[i]+"\n";
		}
		return s;
	}
	
	
	public static void saveFile(String path,String text)throws Exception 
	{		   
		File file = new File(path);
		FileOutputStream fop = new FileOutputStream(file);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		// get the content in bytes
		byte[] contentInBytes = text.getBytes();

		fop.write(contentInBytes);
		fop.flush();
		fop.close();
	}
	
	
	/**
	 * parses the header of the request and thows the corresponding exceptions
	 *
	 * @param    header              a  String[]
	 *
	 * @exception   InvalidMethodException
	 * @exception   MalformedHeaderException
	 * @exception   UnsupportedProtocolVersionException
	 *
	 */
	void parseHeader ( String[] header )
		throws InvalidMethodException, MalformedHeaderException, UnsupportedProtocolVersionException
	{
		// first line:
		_localHeader = header.clone();
		Pattern pFirstLine = Pattern.compile( "([A-Z]+) (.*) HTTP/([0-9])\\.([0-9])" );
		Matcher mFirstLine = pFirstLine.matcher( header[0] );
		
		if ( ! mFirstLine.matches() )
			throw new MalformedHeaderException ( "Firstline '" + header [0] + "' cannot be matched to a valid request!" );
			
		String method = mFirstLine.group( 1 );
		
		sUrl = mFirstLine.group ( 2 );
		String mayor = mFirstLine.group ( 3 );
		String minor = mFirstLine.group ( 4 );
		
		// set request method
		iMethod = getRequestMethod ( method );
		
		// set protocol verion
		if ( mayor.equals ( "1" ) )
			if ( minor.equals ( "1" ) )
				iProtocolVersion = HTTP_VERSION_1_1;
			else if ( minor.equals ( "0" ) )
				iProtocolVersion = HTTP_VERSION_1_0;
		
		if ( iProtocolVersion == HTTP_VERSION_UNKNOWN )
			throw new UnsupportedProtocolVersionException ( mayor + "." + minor );
		
		// parse request String
		// match http:// (abs url)
		extractUriParts(sUrl);
				
				
		// parse header fields
		for ( int i = 1; i < header.length; i++ ) {
			String[] asSplit = header[i].split( ":", 2 );
			
			if ( asSplit.length < 2 )
				// malformed header field: no ":" contained
				throw new MalformedHeaderException ( "Header Line " + i + " does not contain a :!" );
			
			// turn to lower case (since the header fiels are case insensitive)
			asSplit[0] = asSplit[0].toLowerCase();
			
			asSplit[1] = asSplit[1];//.toLowerCase(); - BAAAD Idea, if yomething is send Base64 encoded
			
			// remove leading whitespaces
			asSplit[1] = asSplit[1].replaceAll ( "^\\s+", "" );
			
			htHeader.put ( asSplit[0], asSplit[1] );
			
			if ( asSplit[0].equals ( "content-length" ) ) {
				try {
					iContentLength = Integer.valueOf( asSplit[1] ).intValue();
				} catch (NumberFormatException e) {
					throw new MalformedHeaderException ( "Content-Length does not contain a valid integer format!" );
				}
			}
		}
		
		checkRequest();
	}

	private void extractUriParts(String url) throws MalformedHeaderException {
		Pattern pRequest = Pattern.compile ( "(https?)://([^:/?]+)(:([0-9]+))?(/[^?]*)?(\\?(.*))?" );
		Matcher mRequest = pRequest.matcher ( url );
		if ( mRequest.matches() ) {
			sProtocol = mRequest.group ( 1 );
			sHost = mRequest.group ( 2 );
			if ( mRequest.group ( 3 ) != null )
				iPort = Integer.valueOf( mRequest.group(4) ).intValue();
			sPath = mRequest.group ( 5);
			sQueryString = mRequest.group (7 );
		} else {
			// match /  ( abs server path )
			pRequest = Pattern.compile ( "(/[^?]*)(\\?(.*))?" );
			mRequest = pRequest.matcher ( url );
			if ( mRequest.matches() ) {
				sPath = mRequest.group ( 1 );
				sQueryString = mRequest.group ( 3 );
			} else
				throw new MalformedHeaderException ( "No absolute uri or absolute server path requested!" );
		}
		try {
			if(sQueryString!=null)
				sQueryString=new String (sQueryString.getBytes(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Checks further valitity of this request
	 *
	 * @exception   MalformedHeaderException
	 *
	 */
	private void checkRequest () throws MalformedHeaderException {
		if ( getProtocolVersion() == HttpRequest.HTTP_VERSION_1_1 ) {
			if (! hasHeaderField( "Host" ))
				throw new MalformedHeaderException ( "Http 1.1 request lacks host header field!" );
			else if ( getHost() != null ) {
				if ( getPort() > 0 ) {
					if ( ! getHeaderField("Host").equals ( getHost() + ":" + getPort() ) )
						throw new MalformedHeaderException ( "Host header field does not fit host in the requested url!" );
				} else {
					if ( ! getHeaderField("Host").equals ( getHost() ) )
						throw new MalformedHeaderException ( "Host header field does not fit host in the requested url!" );
				}
			}
		}
	}

	
	/**
	 * Decodes the query string into a hashtable containing the mapping
	 * var name -> value, which will be stored in htGetVars.
	 *
	 * Will be executed maximal once per request.
	 *
	 */
	private void decodeGetVars() {
		if ( htGetVars != null ) return;
		
		htGetVars = new Hashtable();
		
		if ( sQueryString == null ) return ;
		
		String[] asParts = sQueryString.split( "&" );
		
		for ( int i = 0; i< asParts.length; i++ ) {
			String[] asDecode = asParts[i].split( "=", 2 );
			
			// decode urlencoding
			asDecode[1] = decodeUrlEncoded( asDecode[1] );
			
			htGetVars.put ( asDecode[0], asDecode[1] );
		}
	}
	
	/**
	 * Tries to interpret the content as a post sending of form fields.
	 * This does only have an effect, if we have content,
	 * the content is readable as a String
	 * (i.e. has not been read as bytearray etc.) and the content-type of
	 * the request is text/url-encoded.
	 *
	 * After successfull completion the post variables are stored as a mapping
	 * fieldname -> value into the Hashtable htPostVars.
	 *
	 * Will be executed at most once per request.
	 */
	private void decodePostVars () {
		if ( htPostVars != null ) return;
		
		htPostVars = new Hashtable ();

		if ( ! "application/x-www-form-urlencoded".equals(getContentType()) )
			return;
		
		try {
			if ( contentString == null && !bContentRead )
				readContentAsString ();
		} catch (IOException e) {
			// hmm, what to do?
			return;
		} catch ( TimeoutException e ) {
			// hm, whate to do?
			
			e.printStackTrace();
			
			return;
		}
		
		if ( contentString == null )
			return;
		
		String[] asParts = contentString.split( "&" );
		
		for ( int i = 0; i< asParts.length; i++ ) {
			String[] asDecode = asParts[i].split( "=", 2 );
			
			// decode urlencoding
			if ( asDecode.length < 2 )
					continue;
			asDecode[1] = decodeUrlEncoded( asDecode[1] );
			
			htPostVars.put ( asDecode[0], asDecode[1] );
		}
	}
	
	/**
	 * Tries to read the content body of the request into a String
	 *
	 * after successfull completion, the content ist stored in te attribute
	 * contentString
	 *
	 * @exception   UnsupportedEncodingException
	 * @exception   IOException
	 *
	 */
	private void readContentAsString () throws UnsupportedEncodingException, IOException, TimeoutException {
		if ( bContentRead ) return;
		bContentRead = true;
		String sEncoding = getHeaderField( "Content-Encoding" );
		if ( sEncoding == null ) {
			try {
				String sContentType = getContentType();
				String[] asTypeParse = sContentType.split ( ";", 2 );
				String[] asCharsetParse = asTypeParse[1].split ( "=", 2 );
				if ( asCharsetParse[0].toLowerCase().indexOf( "charset" ) >= 0 ) {
					sEncoding = asCharsetParse[1];
					sEncoding = sEncoding.replaceAll( "\\s+", "" );
				}
			} catch ( NullPointerException e ) {
				//occurs, if the header field is not ste or does not contain
				// a charset information
			} catch ( ArrayIndexOutOfBoundsException e ) {
				// occurs, if split "fails"
			}
		}
		int iDesired = getContentLength();
		char[] read = new char[ iDesired ];
		int iRead = 0;
		int iWaited = 0;
		String s = "";
		while ( s.trim().getBytes().length < iDesired) {
			int iNowRead = contentReader.read ( read, iRead, iDesired-iRead );
			s = new String(read);
			iRead += iNowRead;
			if ( iNowRead == 0 ) {
				// Check if accidentially removed a control symbol with .trim()
				if (s.trim().getBytes().length + 1 >= iDesired) {
					break;
				}
				iWaited += 200;
				try {
					Thread.sleep ( 200 );
				} catch (InterruptedException e) {
				}
				
				if ( iWaited >= 10*1000 )
					throw new TimeoutException ();
			} else if ( iNowRead == -1 ) {
				throw new IOException ( "Content stream has ended before content length has been reached!" );
			} else {
				iWaited = 0;
			}
		}	
		contentString = (new String (read)).trim();
	}
	
	
	/**
	 * decodes a String encodec in url format back to a normal String
	 * (i.e. secuences like %xy will be replaces with the corresponding character)
	 *
	 * @param    s                   a  String
	 *
	 * @return   a String
	 *
	 */
	public static String decodeUrlEncoded (String s ) 
	{
		try {
			return URLDecoder.decode(s,"UTF-8");
		} catch (Exception e) {
			StringBuffer b = new StringBuffer ( s.replaceAll( "\\+", " ") );
			
			int i=0;
			while ( i<b.length()) {
				if ( b.charAt( i ) == '%' ) {
					char c1 = b.charAt( i+1 );
					char c2 = b.charAt( i+2 );
					
					char res = (char) (charHexVal(c1) * 16 + charHexVal(c2));
					
					b.replace( i, i+3, ""+res );
				}
				i++;
			}
			
			return b.toString();
		}


		
	}
	
	
	
	/**
	 * returns the hexadecimal value of on hex digit
	 *
	 * @param    c                   a  char
	 *
	 * @return   a byte
	 *
	 */
	public static byte charHexVal ( char c ) {
		if ( c <= '9' && c >= '0' )
			return (byte) ((byte) c  - (byte) '0');
		else if ( c>='a' && c<='f')
			return (byte) (10 + (byte) c - (byte) 'a' );
		else if ( c>='A' && c<='F')
			return (byte) (10 + (byte) c - (byte) 'A' );
		else
			return 0;
	}
	
	/* matching method name -> method constant */
	private static Hashtable<String, Integer> htMethods = null;
	
	/**
	 * Matches a string name of a http method to the corresponding constant
	 *
	 * @param    method              a  String
	 *
	 * @return   an int
	 *
	 */
	public static int getRequestMethod ( String method ) throws InvalidMethodException {
		if ( htMethods == null ) {
			htMethods = new Hashtable<String, Integer> ( 8 );
			htMethods.put ( "POST",    new Integer ( METHOD_POST ) );
			htMethods.put ( "GET" ,    new Integer ( METHOD_GET  ) );
			htMethods.put ( "HEAD" ,   new Integer ( METHOD_HEAD  ) );
			htMethods.put ( "PUT" ,    new Integer ( METHOD_PUT  ) );
			htMethods.put ( "DELETE" , new Integer ( METHOD_DELETE  ) );
			htMethods.put ( "TRACE" ,  new Integer ( METHOD_TRACE  ) );
			htMethods.put ( "OPTIONS", new Integer ( METHOD_OPTIONS  ) );
			htMethods.put ( "CONNECT", new Integer ( METHOD_CONNECT  ) );
		}
		
		Integer res = htMethods.get ( method );
		
		if ( res == null )
			throw new InvalidMethodException (method);
		
		return res.intValue();
	}
	
//	
//	// package protected setters
//	void setMethod(int method) {
//		iMethod = method;
//	}
//	
//	void setUrl(String url) throws MalformedHeaderException {
//		sUrl = url;
//		extractUriParts(url);
//	}
//	
//	void setContentString(String content) {
//		bContentRead = true;
//		contentString = content;
//		iContentLength = content.getBytes().length;
//	}
//	
//	void setProtocolVersion(int version) {
//		switch(version) {
//		case HTTP_VERSION_1_0:
//		case HTTP_VERSION_1_1: iProtocolVersion = version; break;
//		default: iProtocolVersion = HTTP_VERSION_UNKNOWN; break;
//		}
//	}
//	
//	void setHeaderFields(Map<String, String> headerFields
}

