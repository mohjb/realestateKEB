package mApp2017;

/**
 * Created by moh on 20/7/17.
 */

import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.URLDecoder;
import java.net.Socket;
import java.net.ServerSocket;

import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Created by mbohamad on 19/07/2017.
 */

public class Dbg{
static final String Name=App.packageName+".Dbg";
//////////////////////////////////////////////////////////////////////

public static class Req implements HttpServletRequest {
	Ssn ssn;//=new Ssn();
	String contentType="text/json"
			,protocolVersion=""
			,uri="",bodyData//,data
			,queryString=""
					 ;//,method="POST";//
	PC pc;Req(PC p){pc=p;}Req(PC p,String data){pc=p;init(data);}
	InputStream inps;// from Http.Response.data
	BufferedReader bufr;
	long contentLength;// from Http.Response
	boolean chunkedTransfer;// from Http.Response
	boolean keepAlive;      // from Http.Response
	List<String> cookieHeaders;// from Http.Response
	Method method=Method.GET;// from Http.Response.requestMethod
	/**
	 * HTTP Request methods, with the ability to decode a <code>String</code> back
	 * to its enum value.
	 */
	public enum Method {
		GET,
		PUT(true),
		POST(true),
		DELETE,
		HEAD,
		OPTIONS,
		TRACE,
		CONNECT,
		PATCH,
		PROPFIND,
		PROPPATCH,
		MKCOL,
		MOVE,
		COPY,
		LOCK,
		UNLOCK;Method(){this(false);}
		boolean bodyData;Method(boolean bData){bodyData=bData;}
		public static Method lookup(String method,Method defVal) {
			if (method != null)try {
				defVal= valueOf(method);
			} catch (IllegalArgumentException e) {}
			return defVal;
		}
	}
	GzipUsage gzipUsage = GzipUsage.DEFAULT;// from Http.Response
	static enum GzipUsage {DEFAULT,ALWAYS,NEVER;}// from Http.Response


	//public void addCookieHeader(String cookie) {cookieHeaders.add(cookie);}

	Req init( String data) {contentLength=(bodyData=data).length();
		inps = new ByteArrayInputStream(data.getBytes());
		return this;}

	Req init( String mimeType, InputStream data, long totalBytes) {
		contentType= mimeType;
		if (data == null) {
			inps = new ByteArrayInputStream(new byte[0]);
			contentLength = 0L;
		} else {
			inps = data;
			contentLength = totalBytes;
		}
		chunkedTransfer = contentLength < 0;
		keepAlive = true;
		cookieHeaders = new ArrayList(10);
		return this;}

	public void setUseGzip(boolean useGzip) {gzipUsage = useGzip ? GzipUsage.ALWAYS : GzipUsage.NEVER;}
	// If a Gzip usage has been enforced, use it.
	// Else decide whether or not to use Gzip.
	public boolean useGzipWhenAccepted() {
		if (gzipUsage == GzipUsage.DEFAULT)
			return contentType != null && (contentType.toLowerCase().contains("text/") || contentType.toLowerCase().contains("/json"));
		else
			return gzipUsage == GzipUsage.ALWAYS;
	}

	public void close() throws IOException {
		if (inps!= null)
			inps.close();
	}

	public void closeConnection(boolean close) {
		if (close)
			headers.put("connection", "close");
		else
			headers.remove("connection");
	}

	public boolean isCloseConnection() {
		return "close".equals(getHeader("connection"));
	}
	public void setKeepAlive(boolean useKeepAlive) {this.keepAlive = useKeepAlive;}

	String findSessionCookie(){for(String s:cookieHeaders)if(s!=null&&s.indexOf( "session" )!=-1)return s;return null;}

	/**
	 * Decodes the sent headers and loads the data into Key/value pairs
	 */
	void initFromInputStream()throws Exception{
		readHeadersFromInputStream();
		bodyData=readBodyData();//if(method.bodyData)
		decodeParms( bodyData );}

	void readHeadersFromInputStream()throws Exception {//BufferedReader in	, Map<String, String> pre//, Map<String, List<String>> parms, Map<String, String> headers
		try {BufferedReader in=getReader();
			// Read the request line
			String line = in.readLine();
			if (line == null)
				return;

			StringTokenizer st = new StringTokenizer(line);
			if (!st.hasMoreTokens())
				throw new Exception( "BAD REQUEST: Syntax error. ");//Rsp.Status.BAD_REQUEST,

			method=Method.lookup( st.nextToken(),Method.GET );//method=;//pre.put("method",);

			if (!st.hasMoreTokens())
				throw new Exception("BAD REQUEST: Missing URI. ");//Status.BAD_REQUEST,


			uri = st.nextToken();//String

			// Decode parameters from the URI
			int qmi = uri.indexOf('?');
			if (qmi >= 0) {
				decodeParms(queryString=uri.substring(qmi + 1));
				uri = urlDecode(uri.substring(0, qmi));
			} else
				uri = urlDecode(uri);

			// If there's another token, its protocol version,
			// followed by HTTP headers.
			// NOTE: this now forces header names lower case since they are
			// case insensitive and vary by client.
			if (st.hasMoreTokens()) {
				protocolVersion = st.nextToken();
			} else {
				protocolVersion = "HTTP/1.1";
				TL.tl().log("no protocol version specified, strange. Assuming HTTP/1.1.");//NanoHTTPD.LOG.log(Level.FINE,
			}
			line = in.readLine();
			while (line != null && !line.trim().isEmpty()) {
				int p = line.indexOf(':');
				if (p >= 0) {
					String hname=line.substring(0, p).trim().toLowerCase(Locale.US)
							,hval=line.substring(p + 1).trim();
					if("cookie".equals(hval))
						cookieHeaders.add( hval );else
						headers.put(hname, hval);
				}
				line = in.readLine();
			}

			//uri;//pre.put("uri",);
		} catch (IOException ioe) {
			throw new Exception( "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage(), ioe);//Status.INTERNAL_ERROR,
		}
	}

	String readBodyData(){
		StringBuilder b=new StringBuilder(  );
		try {
			BufferedReader in = getReader();
			String line = in.readLine();
			if(line!=null){b.append( line );
				in.readLine();}
			while (line!=null ) {
				b.append( '\n' ).append( line );
				in.readLine();}
		}catch ( Exception ex ){}
		return b.toString();}

	void decodeParms(String parms){
		StringTokenizer st = new StringTokenizer(parms, "&");
		while (st.hasMoreTokens()) {
			String e = st.nextToken();
			int sep = e.indexOf('=');
			String key = null;
			String value = null;

			if (sep >= 0) {
				key = urlDecode(e.substring(0, sep)).trim();
				value = urlDecode(e.substring(sep + 1));
			} else {
				key = urlDecode(e).trim();
				value = "";
			}

			String[] values=null,a = prms.get(key);
			int n=a==null?0:a.length;
			values=new String[n+1];
			if(n>0)
				System.arraycopy( a,0,values,0,n );
			values[n]=value;
			prms.put(key, values);
		}
	}


	/**
	 * Find the byte positions where multipart boundaries start. This reads a
	 * large block at a time and uses a temporary buffer to optimize (memory
	 * mapped) file access.
	 */
	private int[] getBoundaryPositions(ByteBuffer b, byte[] boundary) {
		int[] res = new int[0];
		if (b.remaining() < boundary.length) {
			return res;
		}

		int search_window_pos = 0;
		byte[] search_window = new byte[4 * 1024 + boundary.length];

		int first_fill = (b.remaining() < search_window.length) ? b.remaining() : search_window.length;
		b.get(search_window, 0, first_fill);
		int new_bytes = first_fill - boundary.length;

		do {
			// Search the search_window
			for (int j = 0; j < new_bytes; j++) {
				for (int i = 0; i < boundary.length; i++) {
					if (search_window[j + i] != boundary[i])
						break;
					if (i == boundary.length - 1) {
						// Match found, add it to results
						int[] new_res = new int[res.length + 1];
						System.arraycopy(res, 0, new_res, 0, res.length);
						new_res[res.length] = search_window_pos + j;
						res = new_res;
					}
				}
			}
			search_window_pos += new_bytes;

			// Copy the end of the buffer to the start
			System.arraycopy(search_window, search_window.length - boundary.length, search_window, 0, boundary.length);

			// Refill search_window
			new_bytes = search_window.length - boundary.length;
			new_bytes = (b.remaining() < new_bytes) ? b.remaining() : new_bytes;
			b.get(search_window, boundary.length, new_bytes);
		} while (new_bytes > 0);
		return res;
	}


	/**
	 * Decode percent encoded <code>String</code> values.
	 *
	 * @param str the percent encoded <code>String</code>
	 * @return expanded form of the input, for example "foo%20bar" becomes
	 * "foo bar"
	 */
	public static String urlDecode( String str ) {
		String decoded = null;
		try {
			decoded = URLDecoder.decode( str, "UTF8" );
		} catch ( UnsupportedEncodingException ex ) {
			//NanoHTTPD.LOG.log( Level.WARNING, "Encoding not supported, ignored", ignored );
			TL.tl().error(ex,"Encoding not supported, ignored");
		}
		return decoded; }



	//"{op:'query',sql:'select d,count(*),min(no),max(no) from d group by y,m,w'}"
	HashMap<String,Object> attribs=new HashMap<String,Object>();
	HashMap<String,String> headers=new HashMap<String,String>();
	HashMap<String,String[]>prms=new HashMap<String,String[]>();
	@Override public AsyncContext getAsyncContext() {return null;}
	@Override public Object getAttribute(String p) {return attribs.get(p);}
	@Override public Enumeration<String> getAttributeNames() {return new Enumeration<String>() {
		java.util.Iterator<String>i=attribs.keySet().iterator();
		@Override	public boolean hasMoreElements() {	return i.hasNext();}
		@Override	public String nextElement() {return i.next();}};}
	@Override public String getCharacterEncoding() {return "utf8";}
	@Override public int getContentLength() {return bodyData==null?(int)contentLength:bodyData.length();}
	@Override public long getContentLengthLong() {return bodyData==null?0:bodyData.length();}
	@Override public String getContentType() {p("Req.getContentType:",contentType);return contentType;}
	@Override public String getContextPath() {return uri;}
	@Override public DispatcherType getDispatcherType() {return null;}
	@Override public ServletInputStream getInputStream() throws IOException
	{return new ServletInputStream() {int i=0;
		@Override public int read() throws IOException{return bodyData.charAt(i++);}
		@Override public void setReadListener(ReadListener p){}
		@Override public boolean isReady() {return true;}
		@Override public boolean isFinished() {return i>=bodyData.length();}};}//new java.io.ByteArrayInputStream(data.getBytes());}//(new java.io.StringReader(data));//StringBufferInputStream(data);
	@Override public String getLocalAddr() {return null;}
	@Override public String getLocalName() {return null;}
	@Override public int getLocalPort() {return 0;}
	@Override public Locale getLocale() {return null;}
	@Override public Enumeration<Locale> getLocales() {return null;}
	@Override public String getParameter(String p){String[]a= prms.get(p);return a!=null&&a.length>0?a[0]:null;}
	@Override public Map<String, String[]> getParameterMap() {return prms;}
	@Override public Enumeration<String> getParameterNames() {return new Enumeration<String>() {
		java.util.Iterator<String>i=prms.keySet().iterator();
		@Override	public boolean hasMoreElements() {	return i.hasNext();}
		@Override	public String nextElement() {return i.next();}};}
	@Override public String[] getParameterValues(String p) {return prms.get(p);}
	@Override public String getProtocol() {return protocolVersion;}
	@Override public BufferedReader getReader() throws IOException {
		return bufr!=null?bufr:(bufr=new BufferedReader( new InputStreamReader(inps )));}////new BufferedReader(new java.io.CharArrayReader(data.toCharArray()));
	@Override public String getRealPath(String p) {return null;}
	@Override public String getRemoteAddr() {return "127.0.0.1";}
	@Override public String getRemoteHost() {return null;}
	@Override public int getRemotePort() {return 0;}
	@Override public RequestDispatcher getRequestDispatcher(String p) {return null;}
	@Override public String getScheme() {return null;}
	@Override public String getServerName() {return null;}
	@Override public int getServerPort() {return 0;}
	@Override public ServletContext getServletContext() {return null;}
	@Override public boolean isAsyncStarted() {return false;}
	@Override public boolean isAsyncSupported() {return false;}
	@Override public boolean isSecure() {return false;}
	@Override public void removeAttribute(String p) {attribs.remove(p);}
	@Override public void setAttribute(String p, Object p2) {attribs.put(p, p2);}
	@Override public void setCharacterEncoding(String p) throws UnsupportedEncodingException {}
	@Override public AsyncContext startAsync() throws IllegalStateException {return null;}
	@Override public AsyncContext startAsync(ServletRequest p, ServletResponse p2) throws IllegalStateException {return null;}
	@Override public boolean authenticate(HttpServletResponse p) throws IOException, ServletException {return false;}
	@Override public String changeSessionId() {return null;}
	@Override public String getAuthType() {return null;}
	@Override public Cookie[] getCookies() {return null;}
	@Override public long getDateHeader(String p) {return 0;}
	@Override public String getHeader(String p) {return headers.get(p);}
	@Override public Enumeration<String> getHeaderNames() {return new Enumeration<String>() {
		java.util.Iterator<String>i=headers.keySet().iterator();
		@Override	public boolean hasMoreElements() {	return i.hasNext();}
		@Override	public String nextElement() {return i.next();}};}
	@Override public Enumeration<String> getHeaders(String p) {return null;}
	@Override public int getIntHeader(String p) {return 0;}
	@Override public String getMethod() {return method==null?"":method.name();}
	@Override public Part getPart(String p) throws IOException, ServletException {return null;}
	@Override public Collection<Part> getParts() throws IOException, ServletException {return null;}
	@Override public String getPathInfo() {return null;}
	@Override public String getPathTranslated() {return null;}
	@Override public String getQueryString() {return "/adoqs/xhr.jsp";}
	@Override public String getRemoteUser() {return null;}
	@Override public String getRequestURI() {return null;}
	@Override public StringBuffer getRequestURL() {return null;}
	@Override public String getRequestedSessionId() {return null;}
	@Override public String getServletPath() {return null;}
	@Override public HttpSession getSession() {return ssn;}
	@Override public HttpSession getSession(boolean p) {return ssn;}
	@Override public java.security.Principal getUserPrincipal() {return null;}
	@Override public boolean isRequestedSessionIdFromCookie() {return false;}
	@Override public boolean isRequestedSessionIdFromURL() {return false;}
	@Override public boolean isRequestedSessionIdFromUrl() {return false;}
	@Override public boolean isRequestedSessionIdValid() {return false;}
	@Override public boolean isUserInRole(String p) {return false;}
	@Override public void login(String p, String p2) throws ServletException {}
	@Override public void logout() throws ServletException {}
	@Override public <T extends HttpUpgradeHandler> T upgrade(Class<T> p) throws IOException, ServletException {return null;}

}//class Req

//////////////////////////////////////////////////////////////////////

public static class Rsp implements HttpServletResponse{
	String contentType="";
	Sos sos;
	static final String Name=Dbg.Name+".Rsp";
	PC pc;
	Rsp(PC p){pc=p;}
	HashMap<String,String> headers=new HashMap<String,String>();

	boolean chunkedTransfer;//from Http.Response
	Status status;//from Http.Response

	/**
	 * Some HTTP response status codes
	 */
	public enum Status{//} implements IStatus
		SWITCH_PROTOCOL(101, "Switching Protocols"),

		OK(200, "OK"),
		CREATED(201, "Created"),
		ACCEPTED(202, "Accepted"),
		NO_CONTENT(204, "No Content"),
		PARTIAL_CONTENT(206, "Partial Content"),
		MULTI_STATUS(207, "Multi-Status"),

		REDIRECT(301, "Moved Permanently"),
		/**
		 * Many user agents mishandle 302 in ways that violate the RFC1945 spec
		 * (i.e., redirect a POST to a GET). 303 and 307 were added in RFC2616 to
		 * address this. You should prefer 303 and 307 unless the calling user agent
		 * does not support 303 and 307 functionality
		 */
		@Deprecated
		FOUND(302, "Found"),
		REDIRECT_SEE_OTHER(303, "See Other"),
		NOT_MODIFIED(304, "Not Modified"),
		TEMPORARY_REDIRECT(307, "Temporary Redirect"),

		BAD_REQUEST(400, "Bad Request"),
		UNAUTHORIZED(401, "Unauthorized"),
		FORBIDDEN(403, "Forbidden"),
		NOT_FOUND(404, "Not Found"),
		METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
		NOT_ACCEPTABLE(406, "Not Acceptable"),
		REQUEST_TIMEOUT(408, "Request Timeout"),
		CONFLICT(409, "Conflict"),
		GONE(410, "Gone"),
		LENGTH_REQUIRED(411, "Length Required"),
		PRECONDITION_FAILED(412, "Precondition Failed"),
		PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
		UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
		RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
		EXPECTATION_FAILED(417, "Expectation Failed"),
		TOO_MANY_REQUESTS(429, "Too Many Requests"),

		INTERNAL_ERROR(500, "Internal Server Error"),
		NOT_IMPLEMENTED(501, "Not Implemented"),
		SERVICE_UNAVAILABLE(503, "Service Unavailable"),
		UNSUPPORTED_HTTP_VERSION(505, "HTTP Version Not Supported");

		private final int requestStatus;

		private final String description;

		Status(int requestStatus, String description) {
			this.requestStatus = requestStatus;
			this.description = description;
		}

		public static Status lookup(int requestStatus) {
			for (Status status : Status.values()) {
				if (status.requestStatus == requestStatus) {
					return status;
				}
			}
			return null;
		}

		public String getDescription() {        return "" + this.requestStatus + " " + this.description;}

	}

	PrintWriter out=new PrintWriter(new SrvltWrtr());//System.out//strW);StringWriter strW=new StringWriter(); //TODO: with the server, must not initialize out, instead should remove the member-variable
	@Override public void flushBuffer() throws IOException {if(out!=null)out.flush();if(sos!=null)sos.flush();}
	@Override public int getBufferSize() {p(Name,".getBufferSize:0");return 0;}
	@Override public String getCharacterEncoding() {p(Name,".getCharacterEcoding");return null;}
	@Override public String getContentType() {p(Name,".getContentType:",contentType);return contentType;}
	@Override public Locale getLocale() {p(Name,".getLocale");return null;}
	@Override public ServletOutputStream getOutputStream() throws IOException {p(Name,".getOutputStream");return sos;}
	@Override public PrintWriter getWriter() throws IOException {p(Name,".getWriter");return out;}
	@Override public boolean isCommitted() {p(Name,".isCommited");return false;}
	@Override public void reset() {p(Name,".reset");}
	@Override public void resetBuffer() {p(Name,".resetBuffer");}
	@Override public void setBufferSize(int p) {p(Name,".setBufferSize:",p);}
	@Override public void setCharacterEncoding(String p) {p(Name,".setCharacterEncoding:",p);}
	@Override public void setContentLength(int p) {p(Name,".setContentLength:",p);}
	@Override public void setContentLengthLong(long p) {p(Name,".setContentLengthLong:",p);}
	@Override public void setContentType(String p) {p(Name,".setContentType:",p);contentType=p;}
	@Override public void setLocale(Locale p) {p(Name,".setLocale:",p);}
	@Override public void addCookie(Cookie p) {p(Name,".addCookie:",p);}
	@Override public void addDateHeader(String p, long p2) {p(Name,".addDateHeader:",p,",",p2);}
	@Override public void addHeader(String p, String p2) {p(Name,".addHeader:",p,",",p2);}
	@Override public void addIntHeader(String p, int p2) {p(Name,".addIntHeader:",p,",",p2);}
	@Override public boolean containsHeader(String p) {p(Name,".containsHeader:",p);return false;}
	@Override public String encodeRedirectURL(String p) {p(Name,".ecodeRedirectURL:",p);return null;}
	@Override public String encodeRedirectUrl(String p) {p(Name,".encodeRedirectUrl:",p);return null;}
	@Override public String encodeURL(String p) {p(Name,".encodeURL:",p);return null;}
	@Override public String encodeUrl(String p) {p(Name,".encodeUrl:",p);return null;}
	@Override public String getHeader(String p) {p(Name,".getHeader:",p);return null;}
	@Override public Collection<String> getHeaderNames() {p(Name,".getHeaderNames");return null;}
	@Override public Collection<String> getHeaders(String p) {p(Name,".getHeaders:",p);return null;}
	@Override public int getStatus() {p(Name,".getStatus");return 0;}
	@Override public void sendError(int p) throws IOException {p(Name,".sendError:",p);}
	@Override public void sendError(int p, String p2) throws IOException {p(Name,".sendError:",p,",",p2);}
	@Override public void sendRedirect(String p) throws IOException {p(Name,".sendRedirect:",p);}
	@Override public void setDateHeader(String p, long p2) {p(Name,".setDateHeader:",p,",",p2);}
	@Override public void setHeader(String p, String p2) {p(Name,".setHeader:",p,",",p2);}
	@Override public void setIntHeader(String p, int p2) {p(Name,".setIntHeader:",p,",",p2);}
	@Override public void setStatus(int p) {p(Name,".setStatus:",p);}
	@Override public void setStatus(int p, String p2) {p(Name,".setStatus:",p,",",p2);}

	public static class Sos extends ServletOutputStream{
		java.io.OutputStream o;StringBuilder sb=new StringBuilder();
		Sos(java.io.OutputStream p){p("\n------------------------------\nSos.<init>:",o=p);}
		@Override public boolean isReady() {return true;}
		@Override public void setWriteListener(WriteListener p) {}//super.setWriteListener(p);}
		@Override public void write(int p) throws IOException {sb.append((char)p);if(o!=null)o.write( p );}//p("Sos.write(int:",p,"):",(char)p);}//o.write(p);
		@Override public void flush() throws IOException {p("Dbg.Sos.flush:",sb.toString());sb.setLength(0);if(o!=null)o.flush();}//super.flush();}//o.flush();
		@Override public void close() throws IOException {p("Dbg.Sos.close:",sb.toString());sb.setLength(0);if(o!=null)o.close();}//super.close();}//o.close();
		@Override public void write(byte[] p) throws IOException {sb.append(new String(p));if(o!=null)o.write(p);}//p("Sos.write(byte[]):",new String(p));super.write(p);}//o.write(p);}
		@Override public void write(byte[] a, int b, int c) throws IOException {sb.append(new String(a,b,c));if(o!=null)o.write(a,b,c);}//p("Sos.write(byte[]:",a,",int:",b,",int:",c,"):",new String(a, b, c));super.write(a, b, c);}//o.write(a, b, c);}
		public void p(Object...p){for(Object o:p)try{print(String.valueOf( o ));}catch ( Exception ex ){}}
		@Override public void print(String p) throws IOException {sb.append(p);if(o!=null)o.write(p.getBytes());}
		@Override public void print(int p) throws IOException {p(p);}
		@Override public void print(boolean p) throws IOException {p(p);}
		@Override public void print(char p) throws IOException {p(p);}
		@Override public void print(double p) throws IOException {p(p);}
		@Override public void print(float p) throws IOException {p(p);}
		@Override public void print(long p) throws IOException {p(p);}
		@Override public void println(int p) throws IOException {p(p,'\n');}
		@Override public void println(boolean p) throws IOException {p(p,'\n');}
		@Override public void println() throws IOException {p('\n');}
		@Override public void println(char p) throws IOException {p(p,'\n');}
		@Override public void println(double p) throws IOException {p(p,'\n');}
		@Override public void println(float p) throws IOException {p(p,'\n');}
		@Override public void println(long p) throws IOException {p(p,'\n');}
		@Override public void println(String p) throws IOException {p(p,'\n');}
	}//class Sos

	public static class SrvltWrtr extends java.io.Writer{
		SrvltWrtr(){p("SrvltWrtr.<init>");}StringBuilder sb=new StringBuilder();
		@Override public void flush() throws IOException {p("SrvltWrtr.flush:",sb.toString());sb.setLength(0);}
		@Override public void close() throws IOException {p("SrvltWrtr.close:",sb.toString());sb.setLength(0);}
		//@Override public String toString() {String s=sb.toString();p("SrvltWrtr.toString:",s);return s;}
		@Override public void write(char[] cbuf, int off, int len) throws IOException {sb.append(cbuf, off, len);}
		@Override public void write(char[] cbuf) throws IOException {sb.append(cbuf);}
		@Override public void write(String p) throws IOException {sb.append(p);}
		@Override public void write(String p, int off, int len) throws IOException {sb.append(p, off, len);}
		//p("SrvltWrtr.write(char[]",cbuf,",off=",off,",len=",len,"):",String.valueOf(cbuf,off,len));
		@Override public void write(int p){sb.append((char)p);}
		@Override public Writer append(CharSequence p) throws IOException {sb.append(p);return this;}
		@Override public Writer append(CharSequence p, int off, int len) throws IOException {sb.append(p, off, len);return this;}
		@Override public Writer append(char p) throws IOException {sb.append(p);return this;}
		//@Override public Writer append(String p)  {sb.append(p);super.;return this;}
		//@Override public Writer append(String p, int off, int len) throws IOException {sb.append(p, off, len);return this;}
	}//public static class SrvltWrtr extends java.io.Writer
}//class Rsp

//////////////////////////////////////////////////////////////////////

public static class PC extends javax.servlet.jsp.PageContext{
	Req q=new Req(this);Rsp p=new Rsp(this);
	SrvltContxt a=null;Socket socket;


	@Override public void forward(String arg0) throws ServletException, IOException {}
	@Override public Exception getException() {return null;}
	@Override public Object getPage() {return null;}
	@Override public ServletRequest getRequest(){return q;}
	@Override public ServletResponse getResponse(){return p;}
	@Override public ServletConfig getServletConfig(){return Srvlt.sttc.getServletConfig();}
	@Override public ServletContext getServletContext(){return Srvlt.sttc.getServletContext();}
	@Override public HttpSession getSession(){return q.ssn;}
	@Override public void handlePageException(Exception arg0) throws ServletException, IOException{}
	@Override public void handlePageException(Throwable arg0) throws ServletException, IOException{}
	@Override public void include(String arg0) throws ServletException, IOException{}
	@Override public void include(String arg0, boolean arg1) throws ServletException, IOException{}
	@Override public void initialize(Servlet arg0, ServletRequest arg1, ServletResponse arg2, String arg3, boolean arg4, int arg5,
	                                 boolean arg6) throws IOException, IllegalStateException, IllegalArgumentException{}
	@Override public void release(){}
	@Override public Object findAttribute(String n){Object o=q.getAttribute(n);if(o==null)o=q.ssn.getAttribute(n);if(o==null)o=a.getAttribute(n);return o;}
	@Override public Object getAttribute(String n){return findAttribute(n);}
	@Override public Object getAttribute(String n, int arg1){return null;}
	@Override public Enumeration<String> getAttributeNamesInScope(int arg0){return null;}
	@Override public int getAttributesScope(String arg0) {TL.tl().log("Dbg.PC.getAttributesScope:not implemented:return null");return 0;}

	@Override public javax.servlet.jsp.el.ExpressionEvaluator getExpressionEvaluator(){return null;}
	@Override public javax.servlet.jsp.JspWriter getOut(){TL.tl().log("Dbg.PC.getOut:not implemented:return null");return null;}
	@Override public javax.servlet.jsp.el.VariableResolver getVariableResolver(){return null;}
	@Override public void removeAttribute(String arg0){TL.tl().log("Dbg.PC.removeAttribute a:not implemented:return null");}
	@Override public void removeAttribute(String arg0, int arg1){TL.tl().log("Dbg.PC.removeAttribute a,b:not implemented:return null");}
	@Override public void setAttribute(String arg0, Object arg1) {TL.tl().log("Dbg.PC.setAttribute a,b:not implemented:return null");}
	@Override public void setAttribute(String arg0, Object arg1, int arg2) {TL.tl().log("Dbg.PC.setAttribute a,b,c:not implemented:return null");}
	@Override public javax.el.ELContext getELContext(){TL.tl().log("Dbg.PC.getELContext:not implemented:return null");return null;}
//public static class ELContext{}
}//class PC

//////////////////////////////////////////////////////////////////////

public static class Srvlt extends GenericServlet{
	static final String Name=Dbg.Name+".Srvlt";
	@Override public void service(ServletRequest q, ServletResponse p)throws ServletException, IOException {
		p(Name,".service:",q,",",p);}

	PC pc=null;//new PC();
	static Srvlt sttc=new Srvlt();

	@Override public void log(String message, Throwable t) {log(message);t.printStackTrace();}//super.log(message, t);}
	@Override public void log(String msg) {p("log:",msg);}//super.log(msg);
	@Override public ServletContext getServletContext() {return SrvltContxt.sttc();}//pc.a;super.getServletContext()
	@Override public String getServletName() {return Name;}//super.getServletName();
}//class Srvlt

/** servlet that serves files*/
public static class FSrvlt extends Srvlt{
	FSrvlt sttc=new FSrvlt();
	@Override public void service(ServletRequest q,
	ServletResponse p)throws ServletException, IOException {
		p(Name,".FSrvlt.service:",q,",",p);}}

//////////////////////////////////////////////////////////////////////

public static class Ssn implements HttpSession {
	HashMap<String,Object> attribs=new HashMap<String,Object>();long expir;boolean newlySsn=true;
	public static Map<String,Ssn>sessions=new HashMap<String,Ssn>();

	@Override public Object getAttribute(String p){return attribs.get(p);}
	@Override public Enumeration<String> getAttributeNames() {return new Enumeration<String>() {
		java.util.Iterator<String>i=attribs.keySet().iterator();
		@Override	public boolean hasMoreElements() {	return i.hasNext();}
		@Override	public String nextElement() {return i.next();}};}
	@Override public long getCreationTime() {return 0;}
	@Override public String getId() {return null;}
	@Override public long getLastAccessedTime() {return 0;}
	@Override public int getMaxInactiveInterval() {return 0;}
	@Override public ServletContext getServletContext() {return SrvltContxt.sttc();}//
	@Override public HttpSessionContext getSessionContext() {return null;}
	@Override public Object getValue(String p){return null;}
	@Override public String[] getValueNames() {return null;}
	@Override public void invalidate(){}
	@Override public boolean isNew() {return newlySsn;}
	@Override public void putValue(String p, Object p2){}
	@Override public void removeAttribute(String p){}
	@Override public void removeValue(String p){}
	@Override public void setAttribute(String k, Object v){attribs.put(k, v);}
	@Override public void setMaxInactiveInterval(int p){}}

//////////////////////////////////////////////////////////////////////

public static class SrvltContxt implements ServletContext{//static SrvltContxt sttc;
	HashMap<String,Object> attribs=new HashMap<String,Object>();
	@Override public FilterRegistration.Dynamic addFilter( String arg0, String p2){return null;}
	@Override public FilterRegistration.Dynamic addFilter(String arg0, Filter p2){return null;}
	@Override public FilterRegistration.Dynamic addFilter(String arg0, Class<? extends Filter> p2){return null;}
	@Override public void addListener(String p){}
	@Override public <T extends EventListener > void addListener( T p){}
	@Override public void addListener(Class<? extends EventListener> p){}
	@Override public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0, String p2){return null;}
	@Override public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0, Servlet p2){return null;}
	@Override public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0, Class<? extends Servlet> p2){return null;}
	@Override public <T extends Filter> T createFilter(Class<T> p2)throws ServletException{return null;}
	@Override public <T extends EventListener> T createListener(Class<T> p2)throws ServletException{return null;}
	@Override public <T extends Servlet> T createServlet(Class<T> p2)throws ServletException{return null;}
	@Override public void declareRoles(String... p){}
	@Override public Object getAttribute(String p){return attribs.get(p);}
	@Override public Enumeration<String> getAttributeNames(){return new Enumeration<String>() {
		java.util.Iterator<String>i=attribs.keySet().iterator();
		@Override	public boolean hasMoreElements() {	return i.hasNext();}
		@Override	public String nextElement() {return i.next();}};}
	@Override public ClassLoader getClassLoader(){return null;}
	@Override public ServletContext getContext(String p){return null;}
	@Override public String getContextPath(){return null;}
	@Override public Set<SessionTrackingMode> getDefaultSessionTrackingModes(){return null;}
	@Override public int getEffectiveMajorVersion(){return 0;}
	@Override public int getEffectiveMinorVersion(){return 0;}
	@Override public Set<SessionTrackingMode> getEffectiveSessionTrackingModes(){return null;}
	@Override public FilterRegistration getFilterRegistration(String p){return null;}
	@Override public Map<String, ? extends FilterRegistration> getFilterRegistrations(){return null;}
	@Override public String getInitParameter(String p){return null;}
	@Override public Enumeration<String> getInitParameterNames(){return null;}
	@Override public javax.servlet.descriptor.JspConfigDescriptor getJspConfigDescriptor(){return null;}
	@Override public int getMajorVersion(){return 0;}
	@Override public int getMinorVersion(){return 0;}
	@Override public String getMimeType(String p){
		final String def="application/octet-stream";
		Map m=(Map)attribs.get( "mapFileExt2MimeType" );
		if(m==null) {m=TL.Util.mapCreate(
				"woff","application/font-woff"
				,"woff2","application/font-woff2"
				,"jar"  ,"application/java-archive"
				,"js"   ,"application/javascript"
				,"json" ,"application/json"
				,"exe"  ,def
				,"pdf"  ,"application/pdf"
				,"7z"   ,"application/x-7z-compressed"
				,"tgz"  ,"application/x-compressed"
				,"gz"   ,"application/x-gzip"
				,"tar"  ,"application/x-tar"
				,"xhtml","application/xhtml+xml"
				,"zip"  ,"application/zip"
				,"mp3"  ,"audio/mpeg"
				,"gif"  ,"image/gif"
				,"jpg"  ,"image/jpeg"
				,"jpeg" ,"image/jpeg"
				,"png"  ,"image/png"
				,"svg"  ,"image/svg+xml"
				,"ico"  ,"image/x-icon"
				,"css"  ,"text/css"
				,"csv"  ,"text/csv"
				,"htm"  ,"text/html; charset=utf-8"
				,"html" ,"text/html; charset=utf-8"
				,"txt"  ,"text/plain"
				,"text" ,"text/plain"
				,"log"  ,"text/plain"
				,"xml"  ,"text/xml" );
		}p=p==null?null:(String)m.get(p);
		return p==null?def:p;}
	@Override public RequestDispatcher getNamedDispatcher(String p){return null;}
	@Override public String getRealPath(String p){return TL.context.getRealPath( TL.tl(),p );}
	@Override public RequestDispatcher getRequestDispatcher(String p){return null;}
	@Override public java.net.URL getResource(String arg0) throws java.net.MalformedURLException {return null;}
	@Override public InputStream getResourceAsStream( String p){return null;}
	@Override public Set<String> getResourcePaths(String p){return null;}
	@Override public String getServerInfo(){return null;}
	@Override public Servlet getServlet(String p2)throws ServletException{return null;}
	@Override public String getServletContextName(){return null;}
	@Override public Enumeration<String> getServletNames(){return null;}
	@Override public ServletRegistration getServletRegistration(String p){return null;}
	@Override public Map<String, ? extends ServletRegistration> getServletRegistrations(){return null;}
	@Override public Enumeration<Servlet> getServlets(){return null;}
	@Override public SessionCookieConfig getSessionCookieConfig(){return null;}
	@Override public String getVirtualServerName(){return null;}
	@Override public void log(String p){p("log:",p);}
	@Override public void log(Exception x, String p){x.printStackTrace();p(p);}
	@Override public void log(String p, Throwable x){log(p);x.printStackTrace();}
	@Override public void removeAttribute(String p){attribs.remove( p );}
	@Override public void setAttribute(String p, Object v){p(Name,".SrvltContxt.setAttribute:",p,",",v);attribs.put(p, v);}
	@Override public boolean setInitParameter(String arg0, String p2){return false;}
	@Override public void setSessionTrackingModes(Set<SessionTrackingMode> p){}

	Thread servrThread=null;

	static SrvltContxt sttc;static SrvltContxt sttc(){return sttc==null?new SrvltContxt():sttc;}
	private SrvltContxt(){if(sttc==null)sttc=this;}

	void startServer(int port){
		servrThread=new Thread( new Runnable() {public void run(){
			try{ServerSocket servrSocket = new ServerSocket( port );
				while(servrThread!=null){
					Socket socket=servrSocket.accept();
					PC pc=new PC();
					pc.q.inps=socket.getInputStream();
					pc.p.sos=new Rsp.Sos( socket.getOutputStream());
					pc.q.initFromInputStream(  );
					Servlet s=getServlet(pc.q.uri);
					if(s==null)
						s=new FSrvlt();
					s.service( pc.q,pc.p );
				}
			}catch(Exception ex )
			{p( "startServer:ex", ex );
			}}});
		servrThread.start();
	}//startServer

}//SrvltContxt

//////////////////////////////////////////////////////////////////////

public static void p(Object...p){for(Object s:p)System.out.print(s);System.out.println();}
public static void pa(String...p){for(String s:p)System.out.print(s);
/*
+----+-----+---------------------+----+--------+-------+--------+
| no | uid | logTime             | id | parent | proto | domain |
+----+-----+---------------------+----+--------+-------+--------+
|  1 |   0 | 2017-08-05 10:09:52 |  0 |      0 |     0 |      0 |
|  2 |   0 | 2017-08-05 10:10:32 |  1 |      0 |     1 |      0 |
|  3 |   0 | 2017-08-05 10:15:20 |  2 |      0 |     2 |      0 |
|  4 |   0 | 2017-08-05 10:15:27 |  3 |      0 |     3 |      0 |
|  5 |   0 | 2017-08-05 10:15:31 |  4 |      0 |     4 |      0 |
|  6 |   0 | 2017-08-05 10:16:44 |  5 |      0 |     2 |      0 |
|  7 |   0 | 2017-08-05 10:18:02 |  6 |      0 |     1 |      0 |
|  8 |   5 | 2017-08-11 10:45:03 |  7 |      7 |     7 |      7 |
|  9 |   5 | 2017-08-11 10:45:37 |  8 |      7 |     8 |      7 |
| 10 |   5 | 2017-08-11 10:46:45 |  9 |      7 |     9 |      7 |
| 11 |   5 | 2017-08-11 10:46:51 | 10 |      7 |    10 |      7 |
| 12 |   5 | 2017-08-11 10:46:56 | 11 |      7 |    11 |      7 |
| 13 |   5 | 2017-08-11 11:01:52 | 12 |      7 |     8 |      7 |
+----+-----+---------------------+----+--------+-------+--------+
13 rows in set (0.12 sec)


+----+-----+---------------------+----+-------------+----------------------------------+
| no | uid | logTime             | id | n           | v                                |
+----+-----+---------------------+----+-------------+----------------------------------+
|  1 |   0 | 2017-08-05 10:15:01 |  1 | name        | Role                             |
|  2 |   0 | 2017-08-05 10:15:25 |  2 | name        | Usr                              |
|  3 |   0 | 2017-08-05 10:15:29 |  3 | name        | Proto                            |
|  4 |   0 | 2017-08-05 10:15:34 |  4 | name        | Lock                             |
|  5 |   0 | 2017-08-04 17:24:38 |  5 | un          | usr0                             |
|  6 |   0 | 2017-08-04 17:24:38 |  5 | pw          | 6f8f57715090da2632453988d9a1501b |
|  7 |   0 | 2017-08-04 17:24:38 |  6 | name        | domain0.usr0                     |
|  8 |   0 | 2017-08-04 17:24:38 |  6 | member1     | 5                                |
|  9 |   0 | 2017-08-04 17:24:38 |  6 | resource1   | 0                                |
| 10 |   0 | 2017-08-04 17:24:38 |  6 | resource2   | 2                                |
| 11 |   0 | 2017-08-04 17:24:38 |  6 | resource3   | 1                                |
| 12 |   0 | 2017-08-04 17:24:38 |  6 | resource4   | 3                                |
| 13 |   0 | 2017-08-04 17:24:38 |  6 | resource5   | 4                                |
| 14 |   0 | 2017-08-04 17:24:38 |  6 | operation01 | all                              |
| 15 |   0 | 2017-08-04 17:24:38 |  6 | operation11 | view                             |
| 16 |   0 | 2017-08-04 17:24:38 |  6 | operation21 | writeObject                      |
| 17 |   0 | 2017-08-04 17:24:38 |  6 | operation31 | writeProperty                    |
| 18 |   0 | 2017-08-04 17:24:38 |  6 | operation41 | moveToDelete                     |
| 19 |   0 | 2017-08-04 17:24:38 |  6 | operation51 | app                              |
| 20 |   0 | 2017-08-04 17:24:38 |  6 | operation61 | newSubProto                      |
| 21 |   0 | 2017-08-04 17:24:38 |  6 | operation71 | newChild                         |
| 22 |   0 | 2017-08-04 17:24:38 |  6 | operation81 | newDomain                        |
| 23 |   5 | 2017-08-11 10:46:37 |  8 | name        | Role                             |
| 24 |   5 | 2017-08-11 10:46:48 |  9 | name        | Usr                              |
| 25 |   5 | 2017-08-11 10:46:53 | 10 | name        | Proto                            |
| 26 |   5 | 2017-08-11 10:51:04 | 11 | name        | Lock                             |
| 27 |   5 | 2017-08-11 09:26:23 | 12 | name        | domain7.usr0                     |
| 28 |   5 | 2017-08-11 09:26:23 | 12 | member1     | 5                                |
| 29 |   5 | 2017-08-11 09:26:23 | 12 | resource1   | 7                                |
| 30 |   5 | 2017-08-11 09:26:23 | 12 | resource2   | 9                                |
| 31 |   5 | 2017-08-11 09:26:23 | 12 | resource3   | 8                                |
| 32 |   5 | 2017-08-11 09:26:23 | 12 | resource4   | 10                               |
| 33 |   5 | 2017-08-11 09:26:23 | 12 | resource5   | 11                               |
| 34 |   5 | 2017-08-11 09:26:23 | 12 | operation1  | view                             |
| 35 |   5 | 2017-08-11 09:26:23 | 12 | operation2  | app                              |
| 36 |   5 | 2017-08-11 09:26:23 | 12 | operation3  | writeObject                      |
| 37 |   5 | 2017-08-11 09:26:23 | 12 | operation4  | writeProperty                    |
| 38 |   5 | 2017-08-11 09:26:23 | 12 | operation5  | moveToDomain                     |
| 39 |   5 | 2017-08-11 09:26:23 | 12 | operation6  | moveToProto                      |
| 40 |   5 | 2017-08-11 09:26:23 | 12 | operation7  | moveToParent                     |
| 41 |   5 | 2017-08-11 09:26:23 | 12 | operation8  | moveFromDomain                   |
| 42 |   5 | 2017-08-11 09:26:23 | 12 | operation9  | moveFromProto                    |
| 43 |   5 | 2017-08-11 09:26:23 | 12 | operation10 | moveFromParent                   |
| 44 |   5 | 2017-08-11 09:26:23 | 12 | operation11 | newProperty                      |
| 45 |   5 | 2017-08-11 09:26:23 | 12 | operation12 | newChild                         |
| 46 |   5 | 2017-08-11 09:26:23 | 12 | operation13 | newDomain                        |
+----+-----+---------------------+----+-------------+----------------------------------+
46 rows in set (0.04 sec)

 2017.08.14 test-cases:

*/}

public static void main(String[]args)throws Exception {
	Dbg.Srvlt s = Srvlt.sttc;
	s.pc = new PC();
	s.pc.a = SrvltContxt.sttc();
	s.pc.q.ssn = new Ssn();
	TL tl = TL.Enter( s.pc.q, s.pc.p, s.pc.q.ssn, s.pc.p.getWriter(), s.pc );

	int mp1 = TL.DB.Tbl.maxPlus1( App.ObjHead.C.id, App.ObjHead.dbtName );
	List< Integer > domains = App.Domain.loadDomainsIds( tl );
	App.Domain d1 =domains != null && domains.size() > 1? App.Domain.loadDomain( domains.get( 1 ) ): App.Domain.initNew();
	App.Domain.Role role = null,role2=null,pRole=null;
	App.Domain.Usr usrB = null,pUsr=null;
	App.ObjHead proto=null,protoT=null,t1=null,t2=null;//,test2=null,test1_7=null,test1_8=null,test1_9=null;//List<App.ObjHead>test=new LinkedList<>();

	if ( d1 != null ){ usrB = d1.usrs.get( "be" );
		if(usrB==null){
			pUsr=(App.Domain.Usr)d1.findChild( "name","Usr" );
			//mp1 = TL.DB.Tbl.maxPlus1( App.ObjHead.C.id, App.ObjHead.dbtName );
			usrB=(App.Domain.Usr)d1.new Usr(null,d1.id,pUsr.id).saveNewId();
			usrB.setProps( "un","be"
				,"pw","6f8f57715090da2632453988d9a1501b");
			pRole=(App.Domain.Role)d1.findChild( "name","Role" );
			role=(App.Domain.Role)d1.new Role( null,d1.id,pRole.id ).saveNewId();
			role.setProps( "name", "domainD1Role.usrB"
				,"member1",usrB.id
				,"resource1",usrB.id
			,"operation1",App.Domain.Oper.view.name()
			,"operation2",App.Domain.Oper.newProperty.name()
			,"operation3",App.Domain.Oper.writeProperty.name()
			);d1.roles.put( role.propStr( "name" ),role );role.init();

			mp1 = TL.DB.Tbl.maxPlus1( App.ObjHead.C.id, App.ObjHead.dbtName );
			role2=d1.new Role( mp1,d1.id,pRole.id );role2.save();
			role2.setProps( "name", "domainD1Role.DataEntry"
				,"member1",usrB.id
				,"resource1",d1.findChild( "name","Proto" ).id
			,"operation1",App.Domain.Oper.view.name()
			,"operation2",App.Domain.Oper.newChild.name()
			,"operation3",App.Domain.Oper.newProperty.name()
			,"operation4",App.Domain.Oper.writeProperty.name()
			);d1.roles.put( role2.propStr( "name" ),role2 );role2.init();
		}
		for ( App.Domain.Role r:d1.roles.values() )
			if(r.members.containsValue( usrB )) {
				//the usr should have two roles , the first role has operation only view, the other role is more than view
				role=r;break;
			}
		for( App.ObjHead h:d1.children.values()){
			if("Proto".equals(h.propStr( "name" ))){proto=h;break;}}
		for( App.ObjHead h:d1.children.values()){
			if("Role".equals(h.propStr( "name" ))){pRole=(App.Domain.Role)h;break;}}
		for( App.ObjHead h:d1.children.values()){
			if("protoT".equals(h.propStr( "name" ))){protoT=h;break;}}
		//for( App.ObjHead h:d1.children.values()){if(h.proto==protoT.id){if(test1==null)test1=h;else if()test2=h;} }
		if(protoT!=null&& protoT.children!=null)
		{	t1=protoT.children.get(0);
			t2=protoT.children.get(1);}
	}
	int tn=protoT==null||protoT.children==null?0:protoT.children.size();
 //Map map=TL.Util.mapCreate("mp1", mp1,"d1",d1,"usrB",usrB,"role",role,"protoT",protoT//,"test1",test1//,"test2",test2,"test1_7",test1_7,"test1_8",test1_8,"test1_9",test1_9);
 TL.Exit();

 String P1=",{proto:"
	,P2=d1==null?",parent:0,domain:0,props:{url:'"
		:",parent:"+d1.id+",domain:"+d1.id+",props:{url:'"
	,P3=P1+(proto==null?0:proto.id)+P2;int urlCounter=1;
 String[]requests= {
	"{op:'App.login',un:'usr0',pw:'6f8f57715090da2632453988d9a1501b',logOut:true}"
	,"{op:'App.poll'" +
		",newEntries:[0" +
			(d1!=null?"":P1+"0,parent:0,ref:'1.1 d1',props:{url:'domain1',description:'// 1.1 create domain d1'}}") +//Role 12,Usr 13, Proto 14,Lock 15,role 16
			(protoT!=null?"":P3+"proto-T',name:'protoT',description:'//1.2 create subclass of proto for testing, protoT in d1'}}" )+
			(t1!=null?"":P3+"test-instance1',description:'//1.3 create test-instance1 subclass of proto-T , with test properties'}}" )+
			(t2!=null?"":P3+"test-instance2',description:'//1.4 create test-instance2 subclass of proto-T , with test properties'}}" )+
			(role!=null?"":P1+"12"+P2+"role-20',description:'//1.5 create role with limited access ,to proto-T'" +
				",name:'domain1.role.view',resource:[],member:[],operation:['view']}}" )+
			(usrB!=null?"":P1+"3"+P2+"o-1.6',description:'//1.6 create user with limited access'}}" )+
			(tn>2?"":P1+"3"+P2+"o-1.7',description:'//1.7 new obj, then with writeObjs change to domain'}}" )+
			(tn>3?"":P1+"3"+P2+"o-1.8',description:'//1.8 new obj, then with writeObjs change to role'}}" )+
			(tn>4?"":P1+"2"+P2+"usr-2',description:'//1.9 new obj, then with writeObjs change to usr'" +
				",un:'be' ,pw:'6f8f57715090da2632453988d9a1501b',email:'x'}}" )+
		 "]" +
		",writeObjs:[0 " +
			/*"{description:'3.1 write props in test-instance',id:'"+t1.id+",n:'test',v:0}}"+
			"{description:'3.2 change head parent',id:'"+t1.id+",parent:"+d1.id+",proto:"+t1.proto+",domain:"+t1.domain+"}"+
			"{description:'3.3 change head proto' ,id:'"+t1.id+",parent:"+d1.id+",proto:"+t2.id+",domain:"+t1.domain+"}"+
			"{description:'3.4 change head domain',id:'"+t1.id+",parent:"+d1.id+",proto:"+t2.id+",domain:0}"+
			"{description:'3.5 change un',id:"+usrB.id+",n:'un',v:'Be'}" +
			"{description:'3.6 change role name',id:"+role.id+",n:'name',v:'role.Normal'}" +
			"{description:'3.7 change member',id:"+role.id+",n:'member1',v:0}" +
			"{description:'3.8 change resource',id:"+role.id+",n:'',v:}" +
			"{description:'3.9 change operation',id:"+role.id+",n:'',v:}" +
			"{description:'3.10change domain',id:"+role.id+",n:'',v:}" +
			//4 test cases that are intentionally-invalid requests
			"{description:'4.1  non-existing id',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.2  non-existing proto',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.3  non-existing parent',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.4  non-existing domain',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.5  non-existing property',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.6  parent same as id',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.7  proto no access moveToProto',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.8  proto no access moveFromProto',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.9  proto no access moveToProto nor moveFromProto',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.10 parent no access moveToParent',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.11 parent no access moveFromParent',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.12 parent no access moveToParent nor moveFromParent',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.13 domain no access moveToDomain',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.14 domain no access moveFromDomain',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.15 domain no access moveToDomain nor moveFromDomain',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
			"{description:'4.16 no access writeObj',id:"+d1.id+",parent:"+d1.parent+",proto:"+d1.proto+",domain:"+d1.domain+"}" +
			"{description:'4.17 no access writeProperty',id:"+d1.id+",n:'test',v:'ok'}" +
			"{description:'4.18 no access newDomain, domain same as id',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.id+"}" +
			"{description:'4.19 property un of usr, already used',id:"+usrB.id+",n:'un',v:'usr0'}" +
			"{description:'4.20 same value prop',id:"+t1.id+",n:'test',v:0}" +
			"{description:'4.21 same value head: proto,parent,domain',id:"+t1.id+",parent:"+t1.parent+",proto:"+t1.proto+",domain:"+t1.domain+"}" +
		*/"]" +
		",getIds:[ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,14]" +
		",getLogs:[" +
			" {from:50,to:1501856678000} ,{From:50,To:1501856678000}" +
			",{from:50,to:1501856678000,idList:[0,1,2,13],uidList:[0,1,2],nList:['a','b']}" +
			",{From:50,To:1501856678000,idList:[0,1,2,13],domainList:[0,1,2],protoList:[0,1,2,3],parentList:[0,1,2,3]}" +
			"]" +

	"}"
	,"{op:'App.logout'}"

	,"{op:'App.login',un:'be',pw:'6f8f57715090da2632453988d9a1501b'}"
	,"{op:'App.poll',newEntries:[" +/*
		//test cases that are intentionally-invalid requests
		"{proto:,parent:"+d1.id+",domain:"+d1.id+",props:{url:'o-"+(urlCounter++)+"',description:'//2.1	no access newDomain'}}" +
		P3+"o-"+(urlCounter++)+"',description:'//2.2	no access newChild Usr'}}" +
		P3+"o-"+(urlCounter++)+"',description:'//2.3	no access newChild Role'}}" +
		P3+"o-"+(urlCounter++)+"',description:'//2.4	no access newChild lock'}}" +
		P3+"o-"+(urlCounter++)+"',description:'//2.5	no access newChild'}}" +
		P3+"o-"+(urlCounter++)+"',description:'//2.6	no access subProto'}}" +
		P3+"o-"+(urlCounter++)+"',description:'//2.7	no access newProperty'}}" +
		P3+"o-"+(urlCounter++)+"',description:'//2.8	new usr, existsing un'}}" +
		P3+"o-"+(urlCounter++)+"',description:'//2.9	new role, existsing name'}}" +
		P3+"o-"+(urlCounter++)+"',description:'//2.10new prop, pre-existsing'}}" +*/
		"]" +
	",writeObjs:[ " +/*
		//4 test cases that are intentionally-invalid requests
		 "{proto:,parent:"+d1.id+",domain:"+d1.id+",props:{url:'o-"+(urlCounter++)+"',description:'4.1  non-existing id'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.2  non-existing proto'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.3  non-existing parent'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.4  non-existing domain'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.5  non-existing property'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.6  parent same as id'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.7  proto no access moveToProto'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.8  proto no access moveFromProto'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.9  proto no access moveToProto nor moveFromProto'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.10 parent no access moveToParent'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4."+d1.id+" parent no access moveFromParent'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.12 parent no access moveToParent nor moveFromParent'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.13 domain no access moveToDomain'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.14 domain no access moveFromDomain'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.15 domain no access moveToDomain nor moveFromDomain'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.16 no access writeObj'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.17 no access writeProperty'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.18 no access newDomain, domain same as id'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.19 property un of usr, already used'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.20 same value prop'}}" +
		P3+"o-"+(urlCounter++)+"',description:'4.21 same value head: proto,parent,domain'}}" +*/
		"]" +
	"}"
	};
	for(String p:requests){
		s.pc.q.init(p);
		TL.run( s.pc.q,s.pc.p,s.pc.q.ssn,s.pc.p.getWriter(),s.pc );s.pc.q.ssn.newlySsn=false;
	}
 }

}//class Dbg