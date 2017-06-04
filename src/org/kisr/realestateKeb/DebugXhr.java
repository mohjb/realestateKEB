package org.kisr.realestateKeb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.GenericServlet;
import javax.servlet.ReadListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.WriteListener;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

//import org.kisr.realestateKeb.TL;
//import org.kisr.realestateKeb.Xhr;

public class DebugXhr{
    static final String Name="org.kisr.edu.realestateKeb.DebugXhr";
    //////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////
    public static class Req implements HttpServletRequest{
        Ssn ssn=new Ssn();String data,contentType="text/json",method="POST",protocolVersion,uri;
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
        @Override public int getContentLength() {return data.length();}
        @Override public long getContentLengthLong() {return data.length();}
        @Override public String getContentType() {return contentType;}//p("Req.getContentType:"+contentType);
        @Override public DispatcherType getDispatcherType() {return null;}
        @Override public ServletInputStream getInputStream() throws IOException
        {return new ServletInputStream() {int i=0;
            @Override public int read() throws IOException{return data.charAt(i++);}
            @Override public void setReadListener(ReadListener p){}
            @Override public boolean isReady() {return true;}
            @Override public boolean isFinished() {return i>=data.length();}};}//new java.io.ByteArrayInputStream(data.getBytes());}//(new java.io.StringReader(data));//StringBufferInputStream(data);
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
        @Override public BufferedReader getReader() throws IOException {return new BufferedReader(new java.io.CharArrayReader(data.toCharArray()));}
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
        @Override public String getContextPath() {return null;}
        @Override public Cookie[] getCookies() {return null;}
        @Override public long getDateHeader(String p) {return 0;}
        @Override public String getHeader(String p) {return headers.get(p);}
        @Override public Enumeration<String> getHeaderNames() {return new Enumeration<String>() {
            java.util.Iterator<String>i=headers.keySet().iterator();
            @Override	public boolean hasMoreElements() {	return i.hasNext();}
            @Override	public String nextElement() {return i.next();}};}
        @Override public Enumeration<String> getHeaders(String p) {return null;}
        @Override public int getIntHeader(String p) {return 0;}
        @Override public String getMethod() {return method;}
        @Override public Part getPart(String p) throws IOException, ServletException {return null;}
        @Override public Collection<Part> getParts() throws IOException, ServletException {return null;}
        @Override public String getPathInfo() {return null;}
        @Override public String getPathTranslated() {return null;}
        @Override public String getQueryString() {return "/realestateKeb/xhr.jsp";}
        @Override public String getRemoteUser() {return null;}
        @Override public String getRequestURI() {return null;}
        @Override public StringBuffer getRequestURL() {return null;}
        @Override public String getRequestedSessionId() {return null;}
        @Override public String getServletPath() {return null;}
        @Override public HttpSession getSession() {return ssn;}
        @Override public HttpSession getSession(boolean p) {return ssn;}
        @Override public Principal getUserPrincipal() {return null;}
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
//////////////////////////////////////////////////////////////////////
    public static class Rsp implements HttpServletResponse{
        String contentType="";Sos sos;static final String Name=DebugXhr.Name+".Rsp";
        PrintWriter out=new PrintWriter(System.out);//strW);StringWriter strW=new StringWriter();
        @Override public void flushBuffer() throws IOException {if(out!=null)out.flush();if(sos!=null)sos.flush();}
        @Override public int getBufferSize() {p(Name+".getBufferSize:0");return 0;}
        @Override public String getCharacterEncoding() {p(Name+".getCharacterEcoding");return null;}
        @Override public String getContentType() {p(Name+".getContentType:"+contentType);return contentType;}
        @Override public Locale getLocale() {p(Name+".getLocale");return null;}
        @Override public ServletOutputStream getOutputStream() throws IOException {p(Name+".getOutputStream");return sos;}
        @Override public PrintWriter getWriter() throws IOException {p(Name+".getWriter");return out;}
        @Override public boolean isCommitted() {p(Name+".isCommited");return false;}
        @Override public void reset() {p(Name+".reset");}
        @Override public void resetBuffer() {p(Name+".resetBuffer");}
        @Override public void setBufferSize(int p) {p(Name+".setBufferSize:"+p);}
        @Override public void setCharacterEncoding(String p) {p(Name+".setCharacterEncoding:"+p);}
        @Override public void setContentLength(int p) {p(Name+".setContentLength:"+p);}
        @Override public void setContentLengthLong(long p) {p(Name+".setContentLengthLong:"+p);}
        @Override public void setContentType(String p) {p(Name+".setContentType:"+p);contentType=p;}
        @Override public void setLocale(Locale p) {p(Name+".setLocale:"+p);}
        @Override public void addCookie(Cookie p) {p(Name+".addCookie:"+p);}
        @Override public void addDateHeader(String p, long p2) {p(Name+".addDateHeader:"+p+","+p2);}
        @Override public void addHeader(String p, String p2) {p(Name+".addHeader:"+p+","+p2);}
        @Override public void addIntHeader(String p, int p2) {p(Name+".addIntHeader:"+p+","+p2);}
        @Override public boolean containsHeader(String p) {p(Name+".containsHeader:"+p);return false;}
        @Override public String encodeRedirectURL(String p) {p(Name+".ecodeRedirectURL:"+p);return null;}
        @Override public String encodeRedirectUrl(String p) {p(Name+".encodeRedirectUrl:"+p);return null;}
        @Override public String encodeURL(String p) {p(Name+".encodeURL:"+p);return null;}
        @Override public String encodeUrl(String p) {p(Name+".encodeUrl:"+p);return null;}
        @Override public String getHeader(String p) {p(Name+".getHeader:"+p);return null;}
        @Override public Collection<String> getHeaderNames() {p(Name+".getHeaderNames");return null;}
        @Override public Collection<String> getHeaders(String p) {p(Name+".getHeaders:"+p);return null;}
        @Override public int getStatus() {p(Name+".getStatus");return 0;}
        @Override public void sendError(int p) throws IOException {p(Name+".sendError:"+p);}
        @Override public void sendError(int p, String p2) throws IOException {p(Name+".sendError:"+p+","+p2);}
        @Override public void sendRedirect(String p) throws IOException {p(Name+".sendRedirect:"+p);}
        @Override public void setDateHeader(String p, long p2) {p(Name+".setDateHeader:"+p+","+p2);}
        @Override public void setHeader(String p, String p2) {p(Name+".setHeader:"+p+","+p2);}
        @Override public void setIntHeader(String p, int p2) {p(Name+".setIntHeader:"+p+","+p2);}
        @Override public void setStatus(int p) {p(Name+".setStatus:"+p);}
        @Override public void setStatus(int p, String p2) {p(Name+".setStatus:"+p+","+p2);}

        public static class Sos extends ServletOutputStream{
            java.io.OutputStream o;
            Sos(java.io.OutputStream p){o=p;}
            @Override public boolean isReady() {return true;}
            @Override public void setWriteListener(WriteListener p) {}
            @Override public void write(int p) throws IOException {o.write(p);}
            @Override public void flush() throws IOException {super.flush();o.flush();}
            @Override public void close() throws IOException {super.close();o.close();}
            @Override public void write(byte[] p) throws IOException {super.write(p);o.write(p);}
            @Override public void write(byte[] a, int b, int c) throws IOException {
                super.write(a, b, c);o.write(a, b, c);}
        }//class Sos
    }//class Rsp

    //////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////
    public static class Srvlt extends GenericServlet{
        static final String Name=DebugXhr.Name+".Srvlt";
        @Override public void service(ServletRequest q, ServletResponse
                p)throws ServletException, IOException {p(Name+".service:"+q+","+p);}
        Req q=new Req();Rsp p=new Rsp();SrvltContxt a=new SrvltContxt();
        @Override public void log(String message, Throwable t) {p(message);t.printStackTrace();}//super.log(message, t);}
        @Override public void log(String msg) {p(msg);}//super.log(msg);
        @Override public ServletContext getServletContext() {return a;}//super.getServletContext()
        @Override public String getServletName() {return Name;}//super.getServletName();
    }//class Srvlt

    //////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////
    public static class Ssn implements HttpSession{
        HashMap<String,Object> attribs=new HashMap<String,Object>();long expir;
        @Override public Object getAttribute(String p){return attribs.get(p);}
        @Override public Enumeration<String> getAttributeNames() {return new Enumeration<String>() {
            java.util.Iterator<String>i=attribs.keySet().iterator();
            @Override	public boolean hasMoreElements() {	return i.hasNext();}
            @Override	public String nextElement() {return i.next();}};}
        @Override public long getCreationTime() {return 0;}
        @Override public String getId() {return null;}
        @Override public long getLastAccessedTime() {return 0;}
        @Override public int getMaxInactiveInterval() {return 0;}
        @Override public ServletContext getServletContext() {return null;}
        @Override public HttpSessionContext getSessionContext() {return null;}
        @Override public Object getValue(String p){return null;}
        @Override public String[] getValueNames() {return null;}
        @Override public void invalidate(){}
        @Override public boolean isNew() {return false;}
        @Override public void putValue(String p, Object p2){}
        @Override public void removeAttribute(String p){}
        @Override public void removeValue(String p){}
        @Override public void setAttribute(String k, Object v){attribs.put(k, v);}
        @Override public void setMaxInactiveInterval(int p){}}
    //////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////
    public static class SrvltContxt implements ServletContext{
        HashMap<String,Object> attribs=new HashMap<String,Object>();
        @Override public Dynamic addFilter(String arg0, String p2){return null;}
        @Override public Dynamic addFilter(String arg0, Filter p2){return null;}
        @Override public Dynamic addFilter(String arg0, Class<? extends Filter> p2){return null;}
        @Override public void addListener(String p){}
        @Override public <T extends EventListener> void addListener(T p){}
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
        @Override public JspConfigDescriptor getJspConfigDescriptor(){return null;}
        @Override public int getMajorVersion(){return 0;}
        @Override public String getMimeType(String p){return null;}
        @Override public int getMinorVersion(){return 0;}
        @Override public RequestDispatcher getNamedDispatcher(String p){return null;}
        @Override public String getRealPath(String p){return null;}
        @Override public RequestDispatcher getRequestDispatcher(String p){return null;}
        @Override public URL getResource(String arg0) throws MalformedURLException{return null;}
        @Override public InputStream getResourceAsStream(String p){return null;}
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
        @Override public void log(String p){System.out.println(p);}
        @Override public void log(Exception x, String p){x.printStackTrace();System.out.println(p);}
        @Override public void log(String p, Throwable x){System.out.println(p);x.printStackTrace();}
        @Override public void removeAttribute(String p){}
        @Override public void setAttribute(String p, Object v){p(Name+".SrvltContxt.setAttribute:"+p+","+v);attribs.put(p, v);}
        @Override public boolean setInitParameter(String arg0, String p2){return false;}
        @Override public void setSessionTrackingModes(Set<SessionTrackingMode> p){}}

    //////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////
    public static void p(String p){System.out.println(p);}

    public static void main(String[]args)
    {p("DebugXhr.main:begin");
        int i=-1;String[]data={
            "{from:2000,to:2016,from2:1,to2:53,contract:'all',gov:0,terms:'aggregate',typ:0,contract:'all',stateEdit:false,logOut:true}"
            ,"{id:1}","{id:2}","{id:3}","{id:4}","{id:5}"
            ,"{id:6}","{id:0,width:1000,legend:true}"
    };//data[]
        Srvlt s=new Srvlt();p("DebugXhr.main:new Srvlt");
        try {//s.q.c
            s.q.data=data[++i];
            Report1C.service(s,s.q,s.p);
            p("DebugXhr.main:done Report1.service");
            Chart.Model mod=(Chart.Model)s.q.ssn.attribs.get(Report1C.jspName+Chart.Model.class);
            p("DebugXhr.main:mod="+mod);
            if(mod!=null){
                for(Chart.Model.Chrt chrt:mod.chrts)try{
                    s.q.data=data[++i];s.p.sos=new Rsp.Sos(new java.io.FileOutputStream("chart"+i+".png"));//s.q.data
                    p("DebugXhr.main:for(Chart.Model.Chrt chrt:mod.chrts):s.q.data=data[i="+i+"]="+s.q.data+" : chrt="+chrt);
                    Chart.service(s, s.q, s.p);s.p.sos.close();
                    p("DebugXhr.main:for(Chart.Model.Chrt chrt:mod.chrts):s.q.data=data[i="+i+"]="+s.q.data+" : chrt="+chrt+":done");
                }//for(Chart.Model.Chrt chrt:mod.chrts)
                catch(Exception x){x.printStackTrace();}
            }//if(mod!=null)
        }catch (Exception e) {e.printStackTrace();}
        //p("DebugXhr.main:String x=s.p.strW.toString(); = ");
        //String x=s.p.strW.toString();
        //p(x);
        p("DebugXhr.main:end");
    }//main

}//class DebugXhr