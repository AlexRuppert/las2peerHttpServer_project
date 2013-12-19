package i5.httpServer;

import java.io.BufferedReader;
import java.util.Enumeration;

public interface HttpRequest {

	public final static int METHOD_POST = 10;
	public final static int METHOD_GET = 20;
	public final static int METHOD_HEAD = 30;
	public final static int METHOD_PUT = 40;
	public final static int METHOD_DELETE = 50;
	public final static int METHOD_TRACE = 60;
	public final static int METHOD_OPTIONS = 70;
	public final static int METHOD_CONNECT = 80;
	public final static int METHOD_UNKNOWN = -100;
	public final static int HTTP_VERSION_1_0 = -10;
	public final static int HTTP_VERSION_1_1 = -11;
	public final static int HTTP_VERSION_UNKNOWN = -12;

	/**
	 * returns the http request method
	 *
	 * @return   an int
	 *
	 */
	public abstract int getMethod();

	/**
	 * returns the address of the requesting socket.
	 *
	 * @return   a String
	 *
	 */
	public abstract String getRemoteAddress();

	/**
	 * is this request of method POST?
	 *
	 * @return   a boolean
	 *
	 */
	public abstract boolean isPostRequest();

	/**
	 * is this request of method GET?
	 *
	 * @return   a boolean
	 *
	 */
	public abstract boolean isGetRequest();

	/**
	 * is this request of method HEAD?
	 *
	 * @return   a boolean
	 *
	 */
	public abstract boolean isHeadRequest();

	/**
	 * Returns true if the request is a DELETE request.
	 * 
	 * @return true if the request method is DELETE.
	 */
	public abstract boolean isDeleteRequest();

	/**
	 * Returns true if the request is a PUT request.
	 * 
	 * @return true if the request method is PUT. 
	 */
	public abstract boolean isPutRequest();

	/**
	 * returns the value of a header field
	 *
	 * @param    fieldName           a  String
	 *
	 * @return   a String
	 *
	 */
	public abstract String getHeaderField(String fieldName);

	/**
	 * Is the given header field defined in this request
	 *
	 * @param    fieldName           a  String
	 *
	 * @return   a boolean
	 *
	 */
	public abstract boolean hasHeaderField(String fieldName);

	/**
	 * returns the names of all defined header fields as an Enumeration of strings.
	 *
	 * @return   an Enumeration
	 *
	 */
	public abstract Enumeration getHeaderFieldNames();

	/**
	 * returns http or null
	 *
	 * @return   a String
	 *
	 */
	public abstract String getProtocol();

	/**
	 * returns the hostname of the request defnied in the url (if definied)
	 *
	 * @return   a String
	 *
	 */
	public abstract String getHost();

	/**
	 * returns the port number of the request definied in the requested url
	 * returns -1, if no port has been defnied!
	 *
	 * @return   an int
	 *
	 */
	public abstract int getPort();

	/**
	 * returns the requested path on this server
	 *
	 * @return   a String
	 *
	 */
	public abstract String getPath();

	/**
	 * returns the query string (i.e. everything after the first '?' in the url)
	 * of this request
	 *
	 * @return   a String
	 *
	 */
	public abstract String getQueryString();

	/**
	 * returns the requested url
	 *
	 * @return   a String
	 *
	 */
	public abstract String getUrl();

	/**
	 * returns the content stream of this request, the content length if not sero
	 *
	 * @return   an InputStream
	 *
	 */
	public abstract BufferedReader getContentReader();

	/**
	 * return the content length of the request's body
	 *
	 * @return   an int
	 *
	 */
	public abstract int getContentLength();

	/**
	 * returns the content type of the (POST/PUT) request, if defined
	 *
	 * @return   a String
	 *
	 */
	public abstract String getContentType();

	/**
	 * returns the complete content of the request as string.
	 *
	 * @return   a String / null on errors or empty content
	 *
	 */
	public abstract String getContentString();

	/**
	 * returns the protocol version of the request
	 *
	 * @return   an int
	 *
	 */
	public abstract int getProtocolVersion();

	/**
	 * returns a variable defined via the query string of the request.
	 * The query string will be parsed on the first call of this method or on
	 * (@see #getGetVarNames)
	 *
	 * @param    name                value of the var as String or null if not defined
	 *
	 * @return   a String
	 *
	 */
	public abstract String getGetVar(String name);

	/**
	 * returns a variable defined via the POST content of the request.
	 * The content will be parsed on the first call of this method or on
	 * (@see #getPostVarNames)
	 *
	 * @param    name                value of the var as String or null if not defined
	 *
	 * @return   a String
	 *
	 */
	public abstract String getPostVar(String name);

	/**
	 * returns the names of all known get variables as enumeration of Strings
	 *
	 * @return   an Enumeration
	 *
	 */
	public abstract Enumeration getGetVarNames();

	/**
	 * returns the names of all known post variables as enumeration of Strings
	 *
	 * @return   an Enumeration
	 *
	 */
	public abstract Enumeration getPostVarNames();

}