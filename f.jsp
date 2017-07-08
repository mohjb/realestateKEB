<%@ page import="java.io.*,java.util.Date" %><%!

static StringBuilder f(File f,StringBuilder b)throws IOException{
	if(b==null)b=new StringBuilder();BufferedReader r=null;
	try{r=new BufferedReader(new FileReader(f));String s;
		while ((s = r.readLine()) != null)
			b.append(s);
	}finally{if(r!=null)r.close();}return b;}

static String f(File f)throws IOException{
	StringBuilder b=null;
	b=f(f,b);
	return b.toString();}

static void f(File f,String s)throws IOException{
	FileWriter x=null;try{x=new FileWriter(f);
	x.write(s);}finally{if(x!=null)x.close();}}

static String FN="./aswan20170704.json",AN="aswan20170704";

static String f(ServletContext a,File f,long lm)throws IOException{
	String s= f(f);Object[]x={s,lm};a.setAttribute(AN,x);return s;}

static String f(ServletContext a,File f,String s,long lm)throws IOException{
	try{f(f,s);}catch(Exception ex){}
	Object[]x={s,lm};
	a.setAttribute(AN,x);
	return s;}

static String f(BufferedReader r)throws IOException{
	String s;StringBuilder b=new StringBuilder();
    while ((s = r.readLine()) != null)
        b.append(s).append('\n');
    s = b.toString();
	return s;
}

%><%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", -1); 
String method=request.getMethod(),postData="",appData="";
Object o=application.getAttribute(AN);
Date nowd=new Date();File f=new File(FN);
long lm=0,now=nowd.getTime();
if(o==null){
	if(f.exists())
		appData=f(application,f,lm=f.lastModified());
}else{
	Object[]a=(Object[])o;
	appData=(String)a[0];
	lm=((Number)a[1]).longValue();
	long x=f.lastModified();
	if(x>lm)
		appData=f(application,f,lm=x);
}

if("POST".equals(method))
{    postData = f(request.getReader());
	// // get header lastModified
	if(postData.trim().length()>0)
		f(application,f,postData,now);
}
response.setContentType("text/json;charset:utf-8");
out.print(appData);
%>
