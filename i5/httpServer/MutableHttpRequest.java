package i5.httpServer;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple mutable data object implementation of the {@link HttpRequest} interface.
 * Objects of this class can be used to create or alter HttpRequests, e.g. for processing in a proxy.
 *  
 * @author Patrick Schlebusch
 */
public class MutableHttpRequest implements HttpRequest {
	private int method;
	//private String url;
	private int protocolVersion;
	private String content;
	
//	private String remoteAddress;
	
	private Map<String, String> headerFields;
	private Map<String, String> postVariables;
	private Map<String, String> getVariables;
	
	private String contentType;
	
	private String protocol;
	private String host;
	private int port;
	private String path;
	private String queryString;
	
	/**
	 * Creates a new MutableHttpRequest to a given URL.
	 * 
	 * @param url the URL .
	 * 
	 * @throws MalformedHeaderException if the url string is malformed.
	 */
	public MutableHttpRequest(String url) throws MalformedHeaderException {
		port = 80;
		method = METHOD_GET;
		//this.url = url;
		extractUriParts(url);
		protocolVersion = HTTP_VERSION_1_1;
		content = "";
		
		headerFields = new HashMap<String, String>();
		postVariables = new HashMap<String, String>();
		getVariables = new HashMap<String, String>();
		
		contentType = "text/plain";
	}

	/**
	 * Creates a mutable copy of the given request.
	 * 
	 * @param req the request to copy.
	 * 
	 * @throws MalformedHeaderException if the request header is malformed.
	 */
	public MutableHttpRequest(HttpRequest req) throws MalformedHeaderException {
		setMethod(req.getMethod());
		setUrl(req.getUrl());
		setProtocolVersion(req.getProtocolVersion());
		setContentString(req.getContentString());
		
		headerFields = new HashMap<String, String>();
		for(Enumeration e=req.getHeaderFieldNames(); e.hasMoreElements();) {
			String fieldName = (String) e.nextElement();
			headerFields.put(fieldName, req.getHeaderField(fieldName));
		}
		
		postVariables = new HashMap<String, String>();
		for(Enumeration e=req.getPostVarNames(); e.hasMoreElements();) {
			String varName = (String) e.nextElement();
			postVariables.put(varName, req.getPostVar(varName));
		}
		
		getVariables = new HashMap<String, String>();
		for(Enumeration e=req.getGetVarNames(); e.hasMoreElements(); ) {
			String varName = (String) e.nextElement();
			getVariables.put(varName, req.getGetVar(varName));
		}
	}

	@Override
	public int getMethod() {
		return method;
	}

	@Override
	public String getRemoteAddress() {
		return null;
	}

	@Override
	public boolean isPostRequest() {
		return method == METHOD_POST;
	}

	@Override
	public boolean isGetRequest() {
		return method == METHOD_GET;
	}

	@Override
	public boolean isHeadRequest() {
		return method == METHOD_HEAD;
	}

	@Override
	public boolean isDeleteRequest() {
		return method == METHOD_DELETE;
	}

	@Override
	public boolean isPutRequest() {
		return method == METHOD_PUT;
	}

	@Override
	public String getHeaderField(String fieldName) {
		return headerFields.get(fieldName);
	}

	@Override
	public boolean hasHeaderField(String fieldName) {
		return headerFields.containsKey(fieldName);
	}

	@Override
	public Enumeration getHeaderFieldNames() {
		return Collections.enumeration(headerFields.keySet());
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getQueryString() {
		return queryString;
	}

	@Override
	public String getUrl() {
		return getProtocol()+"://"+getHost()+":"+getPort()+getPath()+"?"+getQueryString(); // TODO test
	}

	@Override
	public BufferedReader getContentReader() {
		return new BufferedReader(new StringReader(content));
	}

	@Override
	public int getContentLength() {
		return content.getBytes().length;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public String getContentString() {
		return content;
	}

	@Override
	public int getProtocolVersion() {
		return protocolVersion;
	}

	@Override
	public String getGetVar(String name) {
		return getVariables.get(name);
	}

	@Override
	public String getPostVar(String name) {
		return postVariables.get(name);
	}

	@Override
	public Enumeration getGetVarNames() {
		return Collections.enumeration(getVariables.keySet());
	}

	@Override
	public Enumeration getPostVarNames() {
		return Collections.enumeration(getVariables.keySet());
	}

	/**
	 * Sets the HTTP method of this request.
	 * 
	 * @param method the method number. (see constants defined in {@link HttpRequest})
	 */
	public void setMethod(int method) {
		this.method = method;
	}
	
	/**
	 * Sets the URL of this request.
	 * The different parts of the URL are parsed and can be queried separately.
	 * 
	 * @param url the URL string.
	 * 
	 * @throws MalformedHeaderException if the URL is malformed.
	 */
	public void setUrl(String url) throws MalformedHeaderException {
		//this.url = url;
		extractUriParts(url);
	}
	
	private void extractUriParts(String url) throws MalformedHeaderException {
		Pattern pRequest = Pattern.compile ( "(https?)://([^:/?]+)(:([0-9]+))?(/[^?]*)?(\\?(.*))?" );
		Matcher mRequest = pRequest.matcher ( url );
		if ( mRequest.matches() ) {
			protocol = mRequest.group ( 1 );
			host = mRequest.group ( 2 );
			if ( mRequest.group ( 3 ) != null )
				port = Integer.valueOf( mRequest.group(4) ).intValue();
			path = mRequest.group ( 5);
			queryString = mRequest.group (7 );
		} else {
			// match /  ( abs server path )
			pRequest = Pattern.compile ( "(/[^?]*)(\\?(.*))?" );
			mRequest = pRequest.matcher ( url );
			if ( mRequest.matches() ) {
				path = mRequest.group ( 1 );
				queryString = mRequest.group ( 3 );
			} else
				throw new MalformedHeaderException ( "No absolute uri or absolute server path requested!" );
		}
	}
	
	/**
	 * Sets the protocol version of this request.
	 * 
	 * @param version the protocol version number. See {@link HttpRequest} constants.
	 */
	public void setProtocolVersion(int version) {
		this.protocolVersion = version;
	}
	
	/**
	 * Sets the request content.
	 * 
	 * @param content the content string.
	 */
	public void setContentString(String content) {
		this.content = content;
	}
	
	/**
	 * Sets a header field to a given value. Preexisting values are overwritten.
	 * 
	 * @param name the name of the header field.
	 * @param value the value of the header field.
	 */
	public void setHeaderField(String name, String value) {
		headerFields.put(name, value);
	}
	
	/**
	 * Deletes a header field. If the field doesn't exist, nothing happens.
	 * 
	 * @param name the name of the header field.
	 */
	public void deleteHeaderField(String name) {
		headerFields.remove(name);
	}
	
	/**
	 * Sets a POST variable to a given value. Preexisting values are overwritten.
	 * 
	 * @param name the name of the variable.
	 * @param value the value of the variable.
	 */
	public void setPostVariable(String name, String value) {
		postVariables.put(name, value);
	}
	
	/**
	 * Deletes a POST variable. If it doesn't exist, nothing happens.
	 * 
	 * @param name the name of the variable.
	 */
	public void deletePostVariable(String name) {
		postVariables.remove(name);
	}
	
	/**
	 * Sets a GET variable to a given value. Preexisting values are overwritten.
	 * 
	 * @param name the name of the variable.
	 * @param value the value of the variable.
	 */
	public void setGetVariable(String name, String value) {
		getVariables.put(name, value);
	}
	
	/**
	 * Deletes a GET variable. If it doesn't exist, nothing happens.
	 * 
	 * @param name the name of the variable.
	 */
	public void deleteGetVariable(String name) {
		getVariables.remove(name);
	}
	
	/**
	 * Sets the content type of the request.
	 * 
	 * @param type the content type.
	 */
	public void setContentType(String type) {
		this.contentType = type;
	}

	/**
	 * Sets the destination host of the request.
	 *  
	 * @param host the destination host.
	 */
	public void setHost(String host) {
		this.host = host;
	}
}
