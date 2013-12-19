package i5.httpServer;

import java.io.Writer;

public interface HttpResponse {

	public static final int STATUS_CONTINUE = 100; // Continue (Http/1.1)
	public static final int STATUS_SWITCHING = 101; // Switching Protocols (Http/1.1)
	// 2XX ? Success
	public static final int STATUS_OK = 200; // OK
	public static final int STATUS_CREATED = 201; // Created
	public static final int STATUS_ACCEPTED = 202; // Accepted
	public static final int STATUS_NON_AUTH = 203; // Non-Authoritative Information (HTTP/1.1)
	public static final int STATUS_NO_CONTENT = 204; // No Content
	public static final int STATUS_RESET_CONTENT = 205; // Reset Content (HTTP/1.1)
	public static final int STATUS_PARTIAL_CONTENT = 206; // Partial Content (HTTP/1.1)
	// 3XX ? Redirection ? the requested document is to be found at some other location
	public static final int STATUS_MULTIPLE_CHOICES = 300; // Multiple Choices (HTTP/1.1)
	public static final int STATUS_MOVED_PERM = 301; // Moved Permanently
	public static final int STATUS_FOUND = 302; // Found
	public static final int STATUS_SEE_OTHER = 303; // See Other (HTTP/1.1)
	public static final int STATUS_NOT_MODIFIED = 304; // Not Modified
	public static final int STATUS_USE_PROXY = 305; // Use Proxy (HTTP/1.1)
	public static final int STATUS_TEMP_REDIRECT = 307; // Temporary Redirect (HTTP/1.1)
	// 4XX ? Error of the client - e.g. errornous requests
	public static final int STATUS_BAD_REQUEST = 400; // Bad Request
	public static final int STATUS_UNAUTHORIZED = 401; // Unauthorized
	public static final int STATUS_PAYMENT_REQUIRED = 402; // Payment Required (Unused) (HTTP/1.1)
	public static final int STATUS_FORBIDDEN = 403; // Forbidden
	public static final int STATUS_NOT_FOUND = 404; // Not Found
	public static final int STATUS_METHOD_NOT_ALLOWED = 405; // Method Not Allowed (HTTP/1.1)
	public static final int STATUS_NOT_ACCEPTABLE = 406; // Not Acceptable (HTTP/1.1)
	public static final int STATUS_PROXY_AUTH_REQUIRED = 407; // Proxy Authentication Required (HTTP/1.1)
	public static final int STATUS_REQUEST_TIMEOUT = 408; // Request Timeout (HTTP/1.1)
	public static final int STATUS_CONFLICT = 409; // Conflict (HTTP/1.1)
	public static final int STATUS_GONE = 410; // Gone (HTTP/1.1)
	public static final int STATUS_LENGTH_REQUIRED = 411; // Length Required (HTTP/1.1)
	public static final int STATUS_PRECONDITION_FAILED = 412; // Precondition Failed (HTTP/1.1)
	public static final int STATUS_REQUEST_ENTITY_TOO_LONG = 413; // Request Entity Too Long (HTTP/1.1)
	public static final int STATUS_REQUEST_URI_TOO_LONG = 414; // Request-URI Too Long (HTTP/1.1)
	public static final int STATUS_UNSUPPORTED_MEDIA_TYPE = 415; // Unsupported Media Type (HTTP/1.1)
	public static final int STATUS_REQUEST_RANGE_NOT_SATISFIABLE = 416; // Requested Range Not Satisfiable (HTTP/1.1)
	public static final int STATUS_EXPECTATION_FAILED = 417; // Expectation Failed (HTTP/1.1)
	// 5XX ? Error of the server
	public static final int STATUS_INTERNAL_SERVER_ERROR = 500; // Internal Server Error
	public static final int STATUS_NOT_IMPLEMENTED = 501; // Not Implemented
	public static final int STATUS_BAD_GATEWAY = 502; // Bad Gateway
	public static final int STATUS_SERVICE_UNAVAILABLE = 503; // Service Unavailable
	public static final int STATUS_GATEWAY_TIMEOUT = 504; // Gateway Timeout (HTTP/1.1)
	public static final int STATUS_HTTP_VERSION_NOT_SUPPORTED = 505; // HTTP Version Not Supported (HTTP/1.1)
	/** HTTP Status Messages **/
	public static final String CODE_202_MESSAGE = "OK";
	public static final String CODE_404_MESSAGE = "forbidden";
	public static final String CODE_500_MESSAGE = "Internal Server Error";

	public abstract void setStatus(int status);

	/**
	 * returns the currently set returns status of the response
	 *
	 * @return   an int
	 *
	 */
	public abstract int getStatus();

	/**
	 * sets the type of the content to return. Default is text/plain
	 *
	 * @param    contentType         a  String
	 *
	 */
	public abstract void setContentType(String contentType);

	/**
	 * returns the currently set content type
	 *
	 * @return   a String
	 *
	 */
	public abstract String getContentType();

	/**
	 * sends a redirect to the given location as response
	 *
	 * @param    location            a  String
	 *
	 */
	public abstract void redirect(String location);

	/**
	 * returns true, if a redirections has been set
	 *
	 * @return   a boolean
	 *
	 */
	public abstract boolean isRedirected();

	/**
	 * prints a string to the return result
	 *
	 * @param    s                   a  String
	 *
	 */
	public abstract void print(String s);

	/**
	 * prints an object to the response (uses the toString method of the given object)
	 *
	 * @param    o                   an Object
	 *
	 */
	public abstract void print(Object o);

	/**
	 * prints a byte to the response
	 *
	 * @param    b                   a  byte
	 *
	 */
	public abstract void print(byte b);

	/**
	 * prints an int to the response
	 *
	 * @param    i                   an int
	 *
	 */
	public abstract void print(int i);

	/**
	 * prints a boolean to the response
	 *
	 * @param    b                   a  boolean
	 *
	 */
	public abstract void print(boolean b);

	/**
	 * Prints a character to the response.
	 *
	 * @param    c                   a  char
	 *
	 */
	public abstract void print(char c);

	/**
	 * prints a String followed by a newline to the response
	 *
	 * @param    s                   a  String
	 *
	 */
	public abstract void println(String s);

	/**
	 * prints an object followed by a new line to the response
	 *
	 * @param    o                   an Object
	 *
	 */
	public abstract void println(Object o);

	/**
	 * discards all previously written content
	 *
	 */
	public abstract void clearContent();

	/**
	 * Sets a http header field to send to the client. Any set header with the same
	 * name willl be overwritten
	 *
	 * @param    field               a  String
	 * @param    value               a  String
	 *
	 */
	public abstract void setHeaderField(String field, String value);

	/**
	 * Returns the current value of a header field of the http response
	 *
	 * @param    field               a  String
	 *
	 * @return   a String
	 *
	 */
	public abstract String getHeaderField(String field);

	/**
	 * Returns the version of the currently used http connection.
	 * (refer to the HttpRequest.HTTP_VERSION_* constants.
	 *
	 * @return   an int
	 *
	 */
	public abstract int getProtocolVersion();

	/**
	 * returns an OutputWriter to which output can be written directly.
	 *
	 * @return   a Writer
	 *
	 */
	public abstract Writer getOutputWriter();

}