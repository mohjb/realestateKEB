<%@page 
import="java.io.File
,java.io.Writer
,java.io.IOException
,java.io.OutputStream
,java.io.StringWriter
,java.io.OutputStreamWriter
,java.io.PrintWriter
,java.io.Reader
,java.io.FileReader
,java.lang.annotation.Annotation
,java.lang.reflect.Field
,java.lang.reflect.Method
,java.util.Map
,java.util.List
,java.util.Iterator
,java.util.Collection
,java.util.HashMap
,java.util.LinkedList
,java.util.Enumeration
,java.util.Date
,javax.servlet.ServletConfig
,javax.servlet.ServletContext
,javax.servlet.http.Cookie
,javax.servlet.http.HttpServletRequest
,javax.servlet.http.HttpServletResponse
,javax.servlet.http.HttpSession
,javax.servlet.jsp.PageContext
,org.apache.commons.fileupload.FileItem
,org.apache.commons.fileupload.disk.DiskFileItemFactory
,org.apache.commons.fileupload.servlet.ServletFileUpload"
contentType="text/html; charset=utf-8" pageEncoding="UTF8"
%><%TL.run(request, response, session, out, pageContext);%><%! // <?

/**
 * Created by Vaio-PC on 11/22/2017.
 * Created by mbohamad on 19/07/2017.*/
public static class TL
{public static final String
	packageName="2017.12",Name=packageName+".TL"
	,UploadPth="/aswan/uploads/";

public static void run(HttpServletRequest request,HttpServletResponse response,HttpSession session,Writer out,PageContext pc)throws IOException{
	TL tl=null;try
	{tl=TL.Enter(request,response,session,out,pc);
		tl.h.r("contentType","text/json");//tl.logOut=tl.var("logOut",false);
		Method op=ops.get(tl.h.req("op"));//Prm.op.toString()));
		if(op==null)
			op=mth.get(tl.h.req.getMethod());
		if(op==null) {String p=tl.h.req.getContextPath();
			for (String s : url.keySet())
				if ( p.startsWith(s) || "*".equals(s) ) {//s == null || s.length() < 1 ||
					op = url.get(s);
					if(!"*".equals(s))break;
				}
		}
		Op opAnno=op==null?null:op.getAnnotation( Op.class );
		tl.log("jsp:version2017.02.09.17.10:op=",op,opAnno);
		if(tl.usr==null&& (opAnno==null || opAnno.usrLoginNeeded() ) )//&&!tl.h.logOut TO DO: AFTER TESTING DEVELOPMENT, REMOVE tl.h.logOut
			op=null;
		Object retVal=null;
		if(op!=null){
			Class[]prmTypes=op.getParameterTypes();
			Class cl=op.getDeclaringClass();
			Annotation[][]prmsAnno=op.getParameterAnnotations();
			int n=prmsAnno==null?0:prmsAnno.length,i=-1;
			Object[]args=new Object[n];
			for(Annotation[]t:prmsAnno)try{
				Op pp=t.length>0&&t[0] instanceof Op?(Op)t[0]:null;
				Class c=prmTypes[++i];
				String nm=pp!=null?pp.prmName():"arg"+i;//t.getName();
				Object o=null;
				args[i]=o=TL.class.equals(c)?tl//:Map.class.isAssignableFrom(c) &&(nm.indexOf("p")!=-1) &&(nm.indexOf("r")!=-1) &&(nm.indexOf("m")!=-1)?tl.json
					          :tl.h.req(nm,c);
			}catch(Exception ex){tl.error(ex,Name,".run:arg:i=",i);}
			retVal=n==0?op.invoke(cl)
				:n==1?op.invoke(cl,args[0])
				:n==2?op.invoke(cl,args[0],args[1])
				:n==3?op.invoke(cl,args[0],args[1],args[2])
				:n==4?op.invoke(cl,args[0],args[1],args[2],args[3])
				:n==5?op.invoke(cl,args[0],args[1],args[2],args[3],args[4])
				:n==6?op.invoke(cl,args[0],args[1],args[2],args[3],args[4],args[5])
				:n==7?op.invoke(cl,args[0],args[1],args[2],args[3],args[4],args[5],args[6])
				:op.invoke(cl,args);
			//Op pp=op.getAnnotation(Op.class);
			if(opAnno!=null && opAnno.nestJsonReq() && tl.json!=null){
				tl.json.put("return",retVal);retVal=tl.json;}
		}
		// else TL.Util.mapSet(tl.response,"msg","Operation not authorized ,or not applicable","return",false);
		if(tl.h.r("responseDone")==null)
		{if(tl.h.r("responseContentTypeDone")==null)
			response.setContentType(String.valueOf(tl.h.r("contentType")));
			Json.Output o=tl.getOut();
			//o.restrictedAccess=true;o.accessViolation=0;
			o.o(retVal);
			//o.restrictedAccess=false;
			tl.log(Name,".run:xhr-response:",tl.jo().o(retVal).toString());}
		tl.getOut().flush();
	}catch(Exception x){
		if(tl!=null){
			tl.error(x,Name,":");
			tl.getOut().o(x);
		}else
			x.printStackTrace();
	}finally{TL.Exit();}
}//run op servlet.service

public H h=new H();
public Map ssn,usr;//DB.Tbl.Usr//public DB.Tbl.Ssn ssn;
public Map<String,Object>json;//accessing request in json-format
public Date now;//public long seqObj,seqProp;

/**wrapping JspWriter or any other servlet writer in "out" */
Json.Output out,/**jo is a single instanceof StringWriter buffer*/jo;

/**Created by moh on 14/7/17.*/

enum context{ROOT("c:/apache-tomcat-8.0.15/webapps/ROOT/"
	,"/Users/moh/Google Drive/air/apache-tomcat-8.0.30/webapps/ROOT/"
	,"/public_html/i1io/"
	,"D:\\apache-tomcat-8.0.15\\webapps\\ROOT\\"
);
	String str,a[];context(String...p){str=p[0];a=p;}

	static File getRealPathFile(TL t,String path){
		String real=t.h.getServletContext().getRealPath(path);
		File f=null,x=null;
		boolean b=true;
		try{if(real!=null)f= new File(real);
			else{int i=0;
				while( i<ROOT.a.length && (b=(x==null|| !x.exists())) )
					try{
						x=new File(ROOT.a[i++]);
					}catch(Exception ex){}//t.error
				f=b?null:new File(x,path);
			}
		}catch(Exception ex){
			t.error(ex,Name,".context.getRealPath:",path);}
		return f;//new File(real==null?"./"+path:real);
		}

	/*static String getRealPath(TL t,String path){
		String real=t.h.getServletContext().getRealPath(path);
		boolean b=true;
		try{File f=null;
			if(real==null){int i=0;
				while( i<ROOT.a.length && (b=(f==null|| !f.exists())) )
					try{
						f=new File(ROOT.a[i++]);
					}catch(Exception ex){}//t.error
				real=(b?"./":f.getCanonicalPath())+path;
			}
		}catch(Exception ex){
			t.error(ex,Name,".context.getRealPath:",path);}
		t.log("TL.context.getRealPath:",real);
		return real==null?"./"+path:real;}*/
	static int getContextIndex(TL t){
		try{File f=null;
			int i=ROOT.a.length-1;
			while( i>=0 )
			{	f=new File(ROOT.a[i]);
				if(f!=null && f.exists())
					return i;i--;
			}
		}catch(Exception ex){
			t.error(ex,Name,".context.getContextIndex:");}
		return -1;}
	//***/static Map<DB,String> getContextPack(TL t,List<Map<DB,String>>a){return null;}
}//context
//TL member variables


/** annotation to designate a java method as an ajax/xhr entry point of execution*/
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Op{
	boolean useClassName() default false;
	//boolean caseSensitive() default true;
	boolean nestJsonReq() default true;//if false , then only the returned-value from the method call is json-stringified as a response body, if true the returned-value is set in the json-request with prop-name "return"
	boolean usrLoginNeeded() default true;
	String httpMethod() default "";
	String urlPath() default "\n"; //if no method name match from parameters, then this string is matched with the requested url, "*" means method will match any request path
	String prmName() default "";
}//Op
static Map<String,Method>
	ops=new HashMap<String,Method>(),
	mth=new HashMap<String,Method>(),
	url=new HashMap<String,Method>();

public static void registerOp(Class p){
	Method[]b=p.getMethods();
	String cn=p.getSimpleName();
	for(Method m:b){
		Op op= m.getAnnotation(Op.class);
		if(op!=null)
		{	String s=m.getName();
			ops.put(op.useClassName()?cn+"."+s:s,m);
			s=op.urlPath();if(!"\n".equals(s))
				url.put(s,m);
			s=op.httpMethod();if(!"".equals(s))
				mth.put(s,m);
		}
	}
}//registerOp

/**the static/class variable "tl"*/ static ThreadLocal<TL> tl=new ThreadLocal<TL>();
public static final String CommentHtml[]={"\n<!--","-->\n"},CommentJson[]={"\n/*","\n*/"};
public TL(HttpServletRequest r,HttpServletResponse n,Writer o){h.req=r;h.rspns=n;out=new Json.Output(o);}
public Json.Output jo(){if(jo==null)try{jo=new Json.Output();}catch(Exception x){error(x,"moh.TL.jo:IOEx:");}return jo;}
public Json.Output getOut() throws IOException {return out;}

/**sets a new TL-instance to the localThread*/
public static TL Enter(HttpServletRequest r,HttpServletResponse response,HttpSession session,Writer out,PageContext pc) throws IOException{
	TL p;if(ops==null || ops.size()==0)registerOp(App.class);//App.staticInit();
	tl.set(p=new TL(r,response,out!=null?out:response.getWriter()));//Class c=App.class;c=App.Prop.class;c=App.Obj.class;
	p.onEnter();
	return p;}
private void onEnter()throws IOException {
	h.ip=h.getRequest().getRemoteAddr();
	now=new Date();
	try{Object o=h.req.getContentType();
		o=o==null?null
			:o.toString().contains("json")?Json.Prsr.parse(h.req)
			:o.toString().contains("part")?h.getMultiParts():null;
		json=o instanceof Map<?, ?>?(Map<String, Object>)o:null;//req.getParameterMap() ;
		h.logOut=h.var("logOut",h.logOut);
		o=h.s("usr");usr=o instanceof Map?(Map)o:null;
		if(h.getSession().isNew()){
			//App.Domain.loadDomain0();
			log("new session",this);
		}
	}catch(Exception ex){error(ex,Name,".onEnter");}
	//if(pages==null){rsp.setHeader("Retry-After", "60");rsp.sendError(503,"pages null");throw new Exception("pages null");}
	if(h.logOut)out.w(h.comments[0]).w(Name).w(".tl.onEnter:\n").o(this).w(h.comments[1]);
}//onEnter
private void onExit(){usr=null;h.ip=null;now=null;h.req=null;json=null;out=jo=null;h.rspns=null;}
/**unsets the localThread, and unset local variables*/
public static void Exit()//throws Exception
{TL p=TL.tl();if(p==null)return;p.onExit();tl.set(null);}

public static class Util{//utility methods
	public static Map<Object, Object> mapCreate(Object...p)
	{Map<Object, Object> m=new HashMap<Object,Object>();//null;
		return p.length>0?maPSet(m,p):m;}
	public static Map<Object, Object> mapSet(Map<Object, Object> m,Object...p){return maPSet(m,p);}
	public static Map<Object, Object> maPSet(Map<Object, Object> m,Object[]p)
	{for(int i=0;i<p.length;i+=2)m.put(p[i],p[i+1]);return m;}
	public final static java.text.SimpleDateFormat
		dateFormat=new java.text.SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
	public static Integer[]parseInts(String s){
		java.util.Scanner b=new java.util.Scanner(s),
			c=b.useDelimiter("[\\s\\.\\-/\\:A-Za-z,]+");
		List<Integer>l=new LinkedList<Integer>();
		while(c.hasNextInt()){
			//if(c.hasNextInt())else c.skip();
			l.add(c.nextInt());
		}c.close();b.close();
		Integer[]a=new Integer[l.size()];l.toArray(a);
		return a;}
	static Date parseDate(String s){
		Integer[]a=parseInts(s);int n=a.length;
		if(n<2){long l=Long.parseLong(s);
			Date d=new Date(l);
			return d;}
		java.util.GregorianCalendar c=new java.util.GregorianCalendar();
		c.set(n>0?a[0]:0,n>1?a[1]-1:0,n>2?a[2]:0,n>3?a[3]:0,n>4?a[4]:0);
		return c.getTime();}
	/**returns a format string of the date as yyyy/MM/dd hh:mm:ss*/
	public static String formatDate(Date p)
	{return p==null?"":dateFormat.format(p);}
	static String format(Object o)throws Exception
	{if(o==null)return null;StringBuilder b=new StringBuilder("\"");
		String a=o.getClass().isArray()?new String((byte[])o):o.toString();
		for(int n=a.length(),i=0;i<n;i++)
		{	char c=a.charAt(i);if(c=='\\')b.append('\\').append('\\');
		else if(c=='"')b.append('\\').append('"');
		else if(c=='\n')b.append('\\').append('n');//.append("\"\n").p(indentation).append("+\"");
		else if(c=='\r')b.append('\\').append('r');
		else if(c=='\t')b.append('\\').append('t');
		else if(c=='\'')b.append('\\').append('\'');
		else b.append(c);}return b.append('"').toString();}
	/**return the integer-index of the occurrence of element-e in the array-a, or returns -1 if not found*/
	public static int indexOf(Object[]a,Object e){int i=a.length;while(--i>-1&&(e!=a[i])&&(e==null||!e.equals(a[i])));return i;}
	static boolean eq(Object a,Object e)
	{if(a==e||(a!=null&&a.equals(e)))return true;//||(a==null&&e==null)
		return (a==null)?false:a.getClass().isArray()?indexOf((Object[])a,e)!=-1:false;}
	public static List<Object>lst(Object...p){List<Object>r=new LinkedList<Object>();for(Object o:p)r.add(o);return r;}
	public static boolean isNum(String v){
		int i=-1,n=v!=null?v.length():0;
		char c='\0';
		boolean b=n>0;
		while(b&& c!='.'&& i+1<n)
		{c=++i<n?v.charAt(i):'\0';
			b= Character.isDigit(c)||c=='.';
		}
		if(c=='.') while(b&& i+1<n)
		{c=++i<n?v.charAt(i):'\0';
			b= Character.isDigit(c);
		};
		return b;
	}
	public static int parseInt(String v,int dv)
	{if(isNum(v) )try{dv=Integer.parseInt(v);}
	catch(Exception ex){//changed 2016.06.27 18:28
		TL.tl().error(ex, Name,".Util.parseInt:",v,dv);
	}return dv;}
	public static <T>T parse(String s,T defval){
		if(s!=null)try{
			Class<T> ct=(Class<T>) defval.getClass();
			Class c=ct;
			boolean b=c==null?false:c.isEnum();
			if(!b){c=ct.getEnclosingClass();b=c==null?false:c.isEnum();}
			if(b){
				for(Object o:c.getEnumConstants())
					if(s.equalsIgnoreCase(o.toString()))
						return (T)o;
			}}catch(Exception x){//changed 2016.06.27 18:28
			TL.tl().error(x, Name,".Util.<T>T parse(String s,T defval):",s,defval);}
		return defval;}
	public static Object parse(String s,Class c){
		if(s!=null)try{if(String.class.equals(c))return s;
		else if(Number.class.isAssignableFrom(c)||c.isPrimitive()) {
			if (Integer.class.equals(c)|| "int"   .equals(c.getName())) return new Integer(s);
			else if (Double .class.equals(c)|| "double".equals(c.getName())) return new Double(s);
			else if (Float  .class.equals(c)|| "float" .equals(c.getName())) return new Float(s);
			else if (Short  .class.equals(c)|| "short" .equals(c.getName())) return new Short(s);
			else if (Long   .class.equals(c)|| "long"  .equals(c.getName())) return new Long(s);
			else if (Byte   .class.equals(c)|| "byte"  .equals(c.getName())) return new Byte(s);
		}///else return new Integer(s);}
		else if(Boolean.class.equals(c)||(c.isPrimitive()&&"boolean".equals(c.getName())))return new Boolean(s);
		else if(Date.class.equals(c))return parseDate(s);
		else if(Character.class.isAssignableFrom(c)||(c.isPrimitive()&&"char".equals(c.getName())))
			return s.length()<1?'\0':s.charAt(0);
			//else if(URL.class.isAssignableFrom(c))try {return new URL("file:" +TL.tl().h.getServletContext().getContextPath()+'/'+s);}catch (Exception ex) {TL.tl().error(ex,Name,".Util.parse:URL:p=",s," ,c=",c);}
			boolean b=c==null?false:c.isEnum();
			if(!b){Class ct=c.getEnclosingClass();b=ct==null?false:ct.isEnum();if(b)c=ct;}
			if(b){
				for(Object o:c.getEnumConstants())
					if(s.equalsIgnoreCase(o.toString()))
						return o;
			}
			return Json.Prsr.parse(s);
		}catch(Exception x){//changed 2016.06.27 18:28
			TL.tl().error(x, Name,".Util.<T>T parse(String s,Class):",s,c);}
		return s;}

	public static Integer toInt(Object o){
		if(o instanceof String && TL.Util.isNum( (String)o ))
			return new Integer( (String)o);
		if(o instanceof Integer)return (Integer)o;
		if(o instanceof Number)return ((Number)o).intValue();
		if(o==null)return null;
		String s=o.toString();
		return TL.Util.isNum( s )?new Integer( s ):null;}

	public static Date toDate(Object o){
		if(o instanceof String && TL.Util.isNum( (String)o ))
			return new Date(new Integer( (String)o));
		if(o instanceof Integer)return new Date((Integer)o);
		if(o instanceof Number)return new Date(((Number)o).intValue());
		if(o==null)return null;//new Date(0);
		String s=o.toString();
		return TL.Util.parseDate( s );}

}//class Util

public class H{
	public boolean logOut=false;
	public String ip;

	public String comments[]=CommentJson;
	public HttpServletRequest req;//App a;
	public HttpServletResponse rspns;
	public HttpServletRequest getRequest(){return req;}
	public HttpSession getSession(){return req.getSession();}
	public ServletContext getServletContext(){return getSession().getServletContext();}
	Map getMultiParts(){
		Map<Object,Object>m=null;
		if( ServletFileUpload.isMultipartContent(req))try
		{DiskFileItemFactory factory=new DiskFileItemFactory();
			factory.setSizeThreshold(40000000);//MemoryThreshold);
			String path=App.UploadPth;//app(this).getUploadPath();
			File f,uploadDir=TL.context.getRealPathFile(TL.this, path);//getServletContext().getRealPath(path);
			if( ! uploadDir.exists() )
				uploadDir.mkdirs();//mkDir();
			factory.setRepository(uploadDir);
			ServletFileUpload upload=new ServletFileUpload(factory);
			List<FileItem> formItems=upload.parseRequest(req);
			if(formItems!=null && formItems.size()>0 )
			{	m=new HashMap<Object,Object>();
				for(FileItem item:formItems)
				{	String fieldNm=item.getFieldName();
					boolean fld=item.isFormField();//mem=item.isInMemory(),
					if(fld)
					{String v=item.getString();
						Object o=v;
						if(fieldNm.indexOf("json")!=-1)
							o=Json.Prsr.parse(v);
						m.put(fieldNm, o);
					}else{
						long sz=item.getSize();
						if(sz>0){
							String ct=item.getContentType()
								,nm=item.getName();
							int count=0;
							f=new File(uploadDir,nm);
							while(f.exists())
								f=new File(uploadDir,(count++)+'.'+nm);
							m.put(fieldNm,Util.mapCreate(//"name",fieldNm,
								"contentType",ct,"size",sz
								,"fileName",path+f.getName()
							));
							item.write(f);
						}//if sz > 0
					}//if isField else
				}//for(FileItem item:formItems)
			}//if(formItems!=null && formItems.size()>0 )
		}catch(Exception ex){
			error(ex,Name,".getMultiParts");}
		//if(ServletFileUpload.isMultipartContent(req))
		return m;
	}//Map getMultiParts()

	/**get a request-scope attribute*/
	public Object r(Object n){return req.getAttribute(String.valueOf(n));}
	/**set a request-scope attribute*/
	public Object r(Object n,Object v){req.setAttribute(String.valueOf(n),v);return v;}
	/**get a session-scope attribute*/
	public Object s(Object n){return getSession().getAttribute(String.valueOf(n));}
	/**set a session-scope attribute*/
	public Object s(Object n,Object v){getSession().setAttribute(String.valueOf(n),v);return v;}
	/**get an application-scope attribute*/
	public Object a(Object n){return getServletContext().getAttribute(String.valueOf(n));}
	/**set an application-scope attribute*/
	public void a(Object n,Object v){getServletContext().setAttribute(String.valueOf(n),v);}
	/**get variable, a variable is considered
	 1: a parameter from the http request
	 2: if the request-parameter is not null then set it in the session with the attribute-name pn
	 3: if the request-parameter is null then get pn attribute from the session
	 4: if both the request-parameter and the session attribute are null then return null
	 @parameters String pn Parameter/attribute Name
	 HttpSession ss the session to get/set the attribute
	 HttpServletRequest rq the http-request to get the parameter from.
	 @return variable value.*/
	public Object var(String pn)
	{HttpSession ss=getSession();
		Object r=null;try{Object sVal=ss.getAttribute(pn);String reqv=req(pn);
		if(reqv!=null&&!reqv.equals(sVal)){ss.setAttribute(pn,r=reqv);//logo(Name,".var(",pn,")reqVal:sesssion.set=",r);
		}
		else if(sVal!=null){r=sVal; //logo(Name,".var(",pn,")sessionVal=",r);
		}}catch(Exception ex){ex.printStackTrace();}return r;}
	public Number var(String pn,Number r)
	{Object x=var(pn);return x==null?r:x instanceof Number?(Number)x:Double.parseDouble(x.toString());}
	public String var(String pn,String r)
	{Object x=var(pn);return x==null?r:String.valueOf(x);}
	public boolean var(String pn,boolean r)
	{Object x=var(pn);return x==null?r:x instanceof Boolean?(Boolean)x:Boolean.parseBoolean(x.toString());}
	/**mostly used for enums , e.g. "enum Screen"*/
	public <T>T var(String n,T defVal) {
		String r=req(n);
		if(r!=null)
			s(n,defVal=Util.parse(r,defVal));
		else{
			Object s=s(n);
			if(s==null)
				s(n,defVal);
			else{Class c=defVal.getClass();
				if(c.isAssignableFrom(s.getClass()))
					defVal=(T)s;//s(n,defVal=(T)s); //changed 2016.07.18
				else
					log(Name,".var(",n,",<T>",defVal,"):defVal not instanceof ssnVal:",s);//added 2016.07.18
			}
		}return defVal;
	}

	public Object reqo(String n){
		if(json!=null )
		{Object o=json.get(n);if(o!=null)return o;}
		String r=req.getParameter(n);
		if(r==null)r=req.getHeader(n);
		if(logOut)log(Name,".reqo(",n,"):",r);
		return r;}

	public String req(String n){
		Object o=reqo(n);
		String r=o instanceof String?(String)o:o!=null?o.toString():null;
		return r;}

	public int req(String n,int defval)
	{Object o=reqo(n);
		if(o instanceof Integer)defval=(Integer)o;
		else if(o instanceof Number)defval=((Number)o).intValue();
		else if(o!=null){
			String s=o instanceof String?(String)o:(o.toString());
			defval=Util.parseInt(s, defval);}
		return defval;}

	public Date req(String n,Date defval)
	{Object o=req(n);
		if(o instanceof Date)defval=(Date)o;
		else if(o instanceof Number)defval=new Date(((Number)o).longValue());
		else if(o!=null)defval=Util.parseDate(o instanceof String?(String)o:(o.toString()));
		return defval;}

	public double req(String n,double defval)
	{Object o=reqo(n);
		if(o instanceof Double)defval=(Double)o;
		else if(o instanceof Number)defval=((Number)o).doubleValue();
		else if(o!=null){
			String s=o instanceof String?(String)o:(o.toString());
			if(Util.isNum( s ))
				defval=new Double(s);}
		return defval;}//{String s=req(n);if(s!=null)	try{defval=Double.parseDouble(s);}catch(Exception x){}	return defval;}

	public <T>T req(String n,T defVal)
	{Object o=reqo(n);if(o instanceof String)
		defVal=Util.parse((String)o,defVal);
	else if( defVal.getClass( ).isInstance( o )) {//o instanceof T
		T o1 = ( T ) o;
		defVal=o1;
	}else if(o!=null)defVal=Util.parse( o.toString(),defVal );
		return defVal;}

	public Object req(String n,Class c)
	{Object o=reqo(n);
		if(c.isInstance( o ))return o;
		else if (o !=null){
			String s=o instanceof String?(String)o:o.toString();
			o=Util.parse(s,c);}
		return o;}

}//class H

/**get the TL-instance for the current Thread*/
public static TL tl(){Object o=tl.get();return o instanceof TL?(TL)o:null;}

////////////////////////////////
public String logo(Object...a){String s=null;
	if(a!=null&&a.length>0)
		try{Json.Output o=tl().jo().clrSW();
			for(Object i:a)o.o(i);
			s=o.toStrin_();
			h.getServletContext().log(s);//CHANGED 2016.08.17.10.00
			if(h.logOut){out.flush().
				w(h.comments[0]//"\n/*"
				).w(s).w(h.comments[1]//"*/\n"
			);}}catch(Exception ex){ex.printStackTrace();}return s;}
/**calls the servlet log method*/
public void log(Object...s){logA(s);}
public void logA(Object[]s){try{
	jo().clrSW();
	for(Object t:s)jo.w(String.valueOf(t));
	String t=jo.toStrin_();
	h.getServletContext().log(t);
	if(h.logOut)out.flush().w(h.comments[0]).w(t).w(h.comments[1]);
}catch(Exception ex){ex.printStackTrace();}}

public void error(Throwable x,Object...p){try{
	String s=jo().clrSW().w("error:").o(p,x).toString();
	h.getServletContext().log(s);
	if(h.logOut)out.w(h.comments[0]//"\n/*
	).w("error:").w(s.replaceAll("<", "&lt;"))
		            .w("\n---\n").o(x).w(h.comments[1] );
	if(x!=null)x.printStackTrace();}
catch(Exception ex){ex.printStackTrace();}}


public Json.Output o(Object...a)throws IOException{if(out!=null&&out.w!=null)for(Object s:a)out.w.write(s instanceof String?(String)s:String.valueOf(s));return out;}
public static class Json{
	public static class Output
	{ public Writer w;//public boolean restrictedAccess=false;int accessViolation=0;
		public boolean initCache=false,includeObj=false,comment=false;
		Map<Object, String> cache;
		public static void out(Object o,Writer w,boolean initCache,boolean includeObj)
			throws IOException{Json.Output t=new Json.Output(w,initCache,includeObj);t.o(o);if(t.cache!=null){t.cache.clear();t.cache=null;}}
		public static String out(Object o,boolean initCache,boolean includeObj){StringWriter w=new StringWriter();
			try{out(o,w,initCache,includeObj);}catch(Exception ex){TL.tl().log("Json.Output.out",ex);}return w.toString();}
		public static String out(Object o){StringWriter w=new StringWriter();try{out(o,w,
			false,false);}catch(Exception ex){TL.tl().log("Json.Output.out",ex);}return w.toString();}
		public Output(){w=new StringWriter();}
		public Output(Writer p){w=p;}
		public Output(Writer p,boolean initCache,boolean includeObj)
		{w=p;this.initCache=initCache;this.includeObj=includeObj;}
		public Output(boolean initCache,boolean includeObj){this(new StringWriter(),initCache,includeObj);}
		public Output(String p)throws IOException{w=new StringWriter();w(p);}
		public Output(OutputStream p)throws Exception{w=new OutputStreamWriter(p);}
		public String toString(){return w==null?null:w.toString();}
		public String toStrin_(){String r=w==null?null:w.toString();clrSW();return r;}
		public Output w(char s)throws IOException{if(w!=null)w.write(s);return this;}
		public Output w(String s)throws IOException{if(w!=null)w.write(s);return this;}
		public Output p(String s)throws IOException{return w(s);}
		public Output p(char s)throws IOException{return w(s);}
		public Output p(long s)throws IOException{return w(String.valueOf(s));}
		public Output p(int s)throws IOException{return w(String.valueOf(s));}
		public Output p(boolean s)throws IOException{return w(String.valueOf(s));}
		public Output o(Object...a)throws IOException{return o("","",a);}
		public Output o(Object a,String indentation)throws IOException{return o(a,indentation,"");}
		public Output o(String ind,String path,Object[]a)throws IOException
		{for(Object i:a)o(i,ind,path);return this;}
		public Output o(Object a,String ind,String path)throws IOException
		{if(cache!=null&&a!=null&&((!includeObj&&path!=null&&path.length()<1)||cache.containsKey(a)))
		{Object p=cache.get(a);if(p!=null){o(p.toString());o("/*cacheReference*/");return this;}}
			final boolean c=comment;
			if(a==null)w("null"); //Object\n.p(ind)
			else if(a instanceof String)oStr(String.valueOf(a),ind);
			else if(a instanceof Boolean||a instanceof Number)w(a.toString());
				//else if(a instanceof App.Tbl)((App.Tbl)a).jsonOutput(this,ind,path);
				//else if(a instanceof TL.DB.Tbl)((App.Tbl)a).jsonOutput(this,ind,path);//oDbTbl((TL.DB.Tbl)a,ind,path);
			else if(a instanceof Map<?,?>)oMap((Map)a,ind,path);
			else if(a instanceof Collection<?>)oCollctn((Collection)a,ind,path);
			else if(a instanceof Object[])oArray((Object[])a,ind,path);
			else if(a.getClass().isArray())oarray(a,ind,path);
			else if(a instanceof java.util.Date)oDt((java.util.Date)a,ind);
			else if(a instanceof Iterator<?>)oItrtr((Iterator)a,ind,path);
			else if(a instanceof Enumeration<?>)oEnumrtn((Enumeration)a,ind,path);
			else if(a instanceof Throwable)oThrbl((Throwable)a,ind);
				//else if(a instanceof ResultSet)oResultSet(( ResultSet)a,ind,path);
				//else if(a instanceof ResultSetMetaData)oResultSetMetaData((ResultSetMetaData)a,ind,path);
			else if(a instanceof TL)oTL((TL)a,ind,path);
			else if(a instanceof ServletContext)oSC((ServletContext)a,ind,path);
			else if(a instanceof ServletConfig )oSCnfg((ServletConfig)a,ind,path);
			else if(a instanceof HttpServletRequest)oReq((HttpServletRequest)a,ind,path);
			else if(a instanceof HttpSession)oSession((HttpSession)a,ind,path);
			else if(a instanceof Cookie )oCookie((Cookie)a,ind,path);
			else if(a instanceof java.util.UUID)w("\"").p(a.toString()).w(c?"\"/*uuid*/":"\"");
			else{w("{\"class\":").oStr(a.getClass().getName(),ind)
				     .w(",\"str\":").oStr(String.valueOf(a),ind)
				     .w(",\"hashCode\":").oStr(Long.toHexString(a.hashCode()),ind);
				if(c)w("}//Object&cachePath=\"").p(path).w("\"\n").p(ind);
				else w("}");}return this;}

		public Output oStr(String a,String indentation)throws IOException
		{final boolean m=comment;if(a==null)return w(m?" null //String\n"+indentation:"null");
			w('"');for(int n=a.length(),i=0;i<n;i++)
		{char c=a.charAt(i);if(c=='\\')w('\\').w('\\');
		else if(c=='"')w('\\').w('"');
		else if(c=='\n'){w('\\').w('n');if(m)w("\"\n").p(indentation).w("+\"");}
		else if(c=='\r')w('\\').w('r');
		else if(c=='\t')w('\\').w('t');
		else if(c=='\'')w('\\').w('\'');
		else p(c);}return w('"');}
		public Output oDt(java.util.Date a,String indentation)throws IOException
		{if(a==null)return w(comment?" null //Date\n":"null");
			//w("{\"class\":\"Date\",\"time\":0x").p(Long.toHexString( a.getTime()));//.w(",\"str\":").oStr(a.toString(),indentation);
			w("0x").p(Long.toHexString( a.getTime()));//if(comment)w("}//Date\n").p(indentation);else w("}");
			return this;}
		public Output oThrbl(Throwable x,String indentation)throws IOException
		{w("{\"message\":").oStr(x.getMessage(),indentation).w(",\"stackTrace\":");
			try{StringWriter sw=new StringWriter();
				x.printStackTrace(new PrintWriter(sw));
				oStr(sw.toString(),indentation);}catch(Exception ex)
			{TL.tl().log("Json.Output.x("+x+"):",ex);}return w("}");}
		public Output oEnumrtn(Enumeration a,String ind,String path)throws IOException
		{final boolean c=comment;
			if(a==null)return c?w(" null //Enumeration\n").p(ind):w("null");
			boolean comma=false;String i2=c?ind+"\t":ind;
			if(c)w("[//Enumeration\n").p(ind);else w("[");
			if(c&&path==null)path="";if(c&&path.length()>0)path+=".";int i=0;
			while(a.hasMoreElements()){if(comma)w(" , ");else comma=true;
				o(a.nextElement(),i2,c?path+(i++):path);}
			return c?w("]//Enumeration&cachePath=\"").p(path).w("\"\n").p(ind):w("]");}
		public Output oItrtr(Iterator a,String ind,String path)throws IOException
		{final boolean c=comment;if(a==null)return c?w(" null //Iterator\n").p(ind):w("null");
			boolean comma=false;String i2=c?ind+"\t":ind;
			if(c){w("[//").p(a.toString()).w(" : Itrtr\n").p(ind);
				if(path==null)path="";if(path.length()>0)path+=".";}
			else w("[");int i=0;
			while(a.hasNext()){if(comma)w(" , ");else comma=true;o(a.next(),i2,c?path+(i++):path);}
			return c?w("]//Iterator&cachePath=\"").p(path).w("\"\n").p(ind):w("]");}
		public Output oArray(Object[]a,String ind,String path)throws IOException
		{final boolean c=comment;
			if(a==null)return c?w(" null //array\n").p(ind):w("null");
			String i2=c?ind+"\t":ind;
			if(c){w("[//array.length=").p(a.length).w("\n").p(ind);
				if(path==null)path="";if(path.length()>0)path+=".";}else w("[");
			for(int i=0;i<a.length;i++){if(i>0)w(" , ");o(a[i],i2,c?path+i:path);}
			return c?w("]//cachePath=\"").p(path).w("\"\n").p(ind):w("]");}
		public Output oarray(Object a,String ind,String path)throws IOException
		{final boolean c=comment;
			if(a==null)return c?w(" null //array\n").p(ind):w("null");
			int n= java.lang.reflect.Array.getLength(a);String i2=c?ind+"\t":ind;
			if(c){w("[//array.length=").p(n).w("\n").p(ind);
				if(path==null)path="";if(path.length()>0)path+=".";}else w("[");
			for(int i=0;i<n;i++){if(i>0)w(" , ");o( java.lang.reflect.Array.get(a,i),i2,c?path+i:path);}
			return c?w("]//cachePath=\"").p(path).w("\"\n").p(ind):w("]");}
		public Output oCollctn(Collection o,String ind,String path)throws IOException
		{if(o==null)return w("null");final boolean c=comment;
			if(c){w("[//").p(o.getClass().getName()).w(":Collection:size=").p(o.size()).w("\n").p(ind);
				if(cache==null&&initCache)cache=new HashMap<Object, String>();
				if(cache!=null)cache.put(o,path);
				if(c&&path==null)path="";if(c&&path.length()>0)path+=".";
			}else w("[");
			Iterator e=o.iterator();int i=0;
			if(e.hasNext()){o(e.next(),ind,c?path+(i++):path);
				while(e.hasNext()){w(",");o(e.next(),ind,c?path+(i++):path);}}
			return c?w("]//").p(o.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind) :w("]");}
		public Output oMap(Map o,String ind,String path) throws IOException
		{if(o==null)return w("null");final boolean c=comment;
			if(c){w("{//").p(o.getClass().getName()).w(":Map\n").p(ind);
				if(cache==null&&initCache)cache=new HashMap<Object, String>();
				if(cache!=null)cache.put(o,path);}else w("{");
			Iterator e=o.keySet().iterator();Object k,v;
			//if(o instanceof Store.Obj)w("uuid:").o(((Store.Obj)o).uuid);
			if(e.hasNext()){k=e.next();v=o.get(k);//if(o instanceof Store.Obj)w(",");
				o(k,ind,c?path+k:path);w(":");o(v,ind,c?path+k:path);}
			while(e.hasNext()){k=e.next();v=o.get(k);w(",");
				o(k,ind,c?path+k:path);w(":");o(v,ind,c?path+k:path);}
			if(c) w("}//")
				.p(o.getClass().getName())
				.w("&cachePath=\"")
				.p(path)
				.w("\"\n")
				.p(ind);else w("}");
			return this;}
		public Output oReq(HttpServletRequest r,String ind,String path)throws IOException
		{final boolean c=comment;try{boolean comma=false,c2;//,d[]
			String k,i2=c?ind+"\t":ind,ct;int j;Enumeration e,f;
			(c?w("{//").p(r.getClass().getName()).w(":HttpServletRequest\n").p(ind):w("{"))
				.w("\"dt\":").oDt(TL.tl().now,i2)//new java.util.Date()
				.w(",\"AuthType\":").o(r.getAuthType(),i2,c?path+".AuthTyp":path)
				.w(",\"CharacterEncoding\":").o(r.getCharacterEncoding(),i2,c?path+".CharacterEncoding":path)
				.w(",\"ContentLength\":").o(r.getContentLength(),i2,c?path+".ContentLength":path)
				.w(",\"ContentType\":").o(ct=r.getContentType(),i2,c?path+".ContentType":path)
				.w(",\"ContextPath\":").o(r.getContextPath(),i2,c?path+".ContextPath":path)
				.w(",\"Method\":").o(r.getMethod(),i2,c?path+".Method":path)
				.w(",\"PathInfo\":").o(r.getPathInfo(),i2,c?path+".PathInfo":path)
				.w(",\"PathTranslated\":").o(r.getPathTranslated(),i2,c?path+".PathTranslated":path)
				.w(",\"Protocol\":").o(r.getProtocol(),i2,c?path+".Protocol":path)
				.w(",\"QueryString\":").o(r.getQueryString(),i2,c?path+".QueryString":path)
				.w(",\"RemoteAddr\":").o(r.getRemoteAddr(),i2,c?path+".RemoteAddr":path)
				.w(",\"RemoteHost\":").o(r.getRemoteHost(),i2,c?path+".RemoteHost":path)
				.w(",\"RemoteUser\":").o(r.getRemoteUser(),i2,c?path+".RemoteUser":path)
				.w(",\"RequestedSessionId\":").o(r.getRequestedSessionId(),i2,c?path+".RequestedSessionId":path)
				.w(",\"RequestURI\":").o(r.getRequestURI(),i2,c?path+".RequestURI":path)
				.w(",\"Scheme\":").o(r.getScheme(),i2,c?path+".Scheme":path)
				.w(",\"UserPrincipal\":").o(r.getUserPrincipal(),i2,c?path+".UserPrincipal":path)
				.w(",\"Secure\":").o(r.isSecure(),i2,c?path+".Secure":path)
				.w(",\"SessionIdFromCookie\":").o(r.isRequestedSessionIdFromCookie(),i2,c?path+".SessionIdFromCookie":path)
				.w(",\"SessionIdFromURL\":").o(r.isRequestedSessionIdFromURL(),i2,c?path+".SessionIdFromURL":path)
				.w(",\"SessionIdValid\":").o(r.isRequestedSessionIdValid(),i2,c?path+".SessionIdValid":path)
				.w(",\"Locales\":").oEnumrtn(r.getLocales(),ind,c?path+".Locales":path)
				.w(",\"Attributes\":{");
			comma=false;
			e=r.getAttributeNames();while(e.hasMoreElements())
				try{k=e.nextElement().toString();if(comma)w(",");else comma=true;
					o(k).w(":").o(r.getAttribute(k),i2,c?path+"."+k:path);
				}catch(Throwable ex){TL.tl().error(ex,"HttpRequestToJsonStr:attrib");}
			w("}, \"Headers\":{");comma=false;e=r.getHeaderNames();
			while(e.hasMoreElements())try
			{k=e.nextElement().toString();
				if(comma)w(",");else comma=true;o(k).w(":[");
				f=r.getHeaders(k);c2=false;j=-1;while(f.hasMoreElements())
			{if(c2)w(",");else c2=true;o(f.nextElement(),i2,c?path+".Headers."+k+"."+(++j):path);}
				w("]");
			}catch(Throwable ex){TL.tl().error(ex,"Json.Output.oReq:Headers");}
			w("}, \"Parameters\":").oMap(r.getParameterMap(),i2,c?path+".Parameters":path)
				.w(",\"Session\":").o(r.getSession(false),i2,c?path+".Session":path)
				.w(", \"Cookies\":").o(r.getCookies(),i2,c?path+".Cookies":path);
			//if(ct!=null&&ct.indexOf("part")!=-1)w(", \"Parts\":").o(r.getParts(),i2,c?path+".Parts":path);
			//AsyncContext =r.getAsyncContext();
			//long =r.getDateHeader(arg0)
			//DispatcherType =r.getDispatcherType()
			//String =r.getLocalAddr()
			//String =r.getLocalName()
			//int =r.getLocalPort()
			//int =r.getRemotePort()
			//RequestDispatcher =r.getRequestDispatcher(String)
			//StringBuffer r.getRequestURL()
			//String r.getServerName()
			//int r.getServerPort()
			//ServletContext =r.getServletContext()
			//String r.getServletPath()
			//boolean r.isAsyncStarted()
			//boolean r.isAsyncSupported()
			//boolean r.isUserInRole(String)
		}catch(Exception ex){TL.tl().error(ex,"Json.Output.oReq:Exception:");}
			if(c)w("}//").p(r.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);
			else w("}");
			return this;}
		Output oSession(HttpSession s,String ind,String path)throws IOException
		{final boolean c=comment;try{if(s==null)w("null");else
		{String i2=c?ind+"\t":ind;
			(c?w("{//").p(s.getClass().getName()).w(":HttpSession\n").p(ind):w("{"))
				.w("{\"isNew\":").p(s.isNew()).w(",sid:").oStr(s.getId(),ind)
				.w(",\"CreationTime\":").p(s.getCreationTime())
				.w(",\"MaxInactiveInterval\":").p(s.getMaxInactiveInterval())
				.w(",\"attributes\":{");Enumeration e=s.getAttributeNames();boolean comma=false;
			while(e.hasMoreElements())
			{Object k=e.nextElement().toString();if(comma)w(",");else comma=true;
				o(k,i2).w(":").o(s.getAttribute(String.valueOf(k)),i2,c?path+".Attributes."+k:path);
			}w("}");}}catch(Exception ex){TL.tl().error(ex,"Json.Output.Session:");}
			if(c)w("}//").p(s.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);
			else w("}");
			return this;}
		public Output oCookie(Cookie y,String ind,String path)throws IOException
		{final boolean c=comment;try{(c?w("{//")
			.p(y.getClass().getName()).w(":Cookie\n").p(ind):w("{"))
			.w("\"Comment\":").o(y.getComment())
			.w(",\"Domain\":").o(y.getDomain())
			.w(",\"MaxAge\":").p(y.getMaxAge())
			.w(",\"Name\":").o(y.getName())
			.w(",\"Path\":").o(y.getPath())
			.w(",\"Secure\":").p(y.getSecure())
			.w(",\"Version\":").p(y.getVersion())
			.w(",\"Value\":").o(y.getValue());
		}catch(Exception ex){TL.tl().error(ex,"Json.Output.Cookie:");}
			if(c)try{w("}//").p(y.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);
			}catch(Exception ex){TL.tl().error(ex,"Json.Output.Cookie:");}else w("}");
			return this;}

		Output oTL(TL y,String ind,String path)throws IOException
		{final boolean c=comment;try{String i2=c?ind+"\t":ind;
			(c?w("{//").p(y.getClass().getName()).w(":PageContext\n").p(ind):w("{"))
				.w("\"ip\":").o(y.h.ip,i2,c?path+".ip":path)
				.w(",\"usr\":").o(y.usr,i2,c?path+".usr":path)//.w(",uid:").o(y.uid,i2,c?path+".uid":path)
				.w(",\"ssn\":").o(y.ssn,i2,c?path+".ssn":path)//.w(",sid:").o(y.sid,i2,c?path+".sid":path)
				.w(",\"now\":").o(y.now,i2,c?path+".now":path)
				.w(",\"json\":").o(y.json,i2,c?path+".json":path)
				//.w(",\"response\":").o(y.response,i2,c?path+".response":path)
				.w(",\"Request\":").o(y.h.getRequest(),i2,c?path+".request":path)
				//.w(",\"Session\":").o(y.getSession(false))
				.w(",\"application\":").o(y.h.getServletContext(),i2,c?path+".application":path)
			//.w(",\"config\":").o(y.req.getServletContext().getServletConfig(),i2,c?path+".config":path)
			//.w(",\"Page\":").o(y.srvlt,i2,c?path+".Page":path)
			//.w(",\"Response\":").o(y.rspns,i2,c?path+".Response":path)
			;
		}catch(Exception ex){TL.tl().error(ex,"Json.Output.oTL:");}
			if(c)try{w("}//").p(y.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);}
			catch(Exception ex){TL.tl().error(ex,"Json.Output.oTL:closing:");}
			else w("}");
			return this;}

		Output oSC(ServletContext y,String ind,String path)
		{final boolean c=comment;try{String i2=c?ind+"\t":ind;(c?w("{//").p(y.getClass().getName()).w(":ServletContext\n").p(ind):w("{"))
			.w(",\"ContextPath\":").o(y.getContextPath(),i2,c?path+".ContextPath":path)
			.w(",\"MajorVersion\":").o(y.getMajorVersion(),i2,c?path+".MajorVersion":path)
			.w(",\"MinorVersion\":").o(y.getMinorVersion(),i2,c?path+".MinorVersion":path);
			if(c)
				w("}//").p(y.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);
			else w("}");
		}catch(Exception ex){TL.tl().error(ex,"Json.Output.ServletContext:");}
			return this;}

		Output oSCnfg(ServletConfig y,String ind,String path)throws IOException
		{final boolean c=comment;try{if(c)w("{//").p(y.getClass().getName()).w(":ServletConfiguration\n").p(ind);
		else w("{");
			//String getInitParameter(String)
			//Enumeration getInitParameterNames()
			//getServletContext()
			//String getServletName()	.w(",:").o(y.(),i2,c?path+".":path)
		}catch(Exception ex){TL.tl().error(ex,"Json.Output.ServletConfiguration:");}
			return c?w("}//").p(y.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind) :w("}");}
		Output oBean(Object o,String ind,String path)
		{final boolean c=comment;try{String i2=c?ind+"\t":ind,i3=c?i2+"\t":ind;Class z=o.getClass();
			(c?w("{//").p(z.getName()).w(":Bean\n").p(ind):w("{"))
				.w("\"str\":").o(o.toString(),i2,c?path+".":path)
//		.w(",:").o(o.(),i2,c?path+".":path)
			;Method[]a=z.getMethods();//added 2015.11.21
			for(Method m:a){String n=m.getName();
				if(n.startsWith("get")&&m.getParameterTypes().length==0)
					w("\n").w(i2).w(",").p(n).w(':').o(m.invoke(o), i3, path+'.'+n);}
			if(c)w("}//").p(o.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);
			else w("}");}catch(Exception ex){
			TL.tl().error(ex,"Json.Output.Bean:");}return this;}

		Output clrSW(){if(w instanceof StringWriter){((StringWriter)w).getBuffer().setLength(0);}return this;}
		Output flush() throws IOException{w.flush();return this;}
	} //class Output


	public static class Prsr {

		public StringBuilder buff=new StringBuilder() ,lookahead=new StringBuilder();
		public Reader rdr;public File f;public long fz,lm;public Object o;

		public String comments=null;
		public char c;Map<String,Object>cache=null;

		enum Literal{Undefined,Null};//,False,True

		public static Object parse(String p)throws Exception{
			Prsr j=new Prsr();j.rdr=new java.io.StringReader(p);return j.parse();}

		public static Object parse(HttpServletRequest p)throws Exception{
			Prsr j=new Prsr();j.rdr=p.getReader();return j.parse();}//public static Object parseItem(Reader p)throws Exception{Prsr j=new Prsr();j.rdr=p;return j.parseItem();}//public Object load(File f){long l=(this.f=f).lastModified();if( lm>=l)return o;lm=l;fz=f.length();try{rdr= new FileReader(f);o=parse();}catch(Exception ex){}return o;}

		/**skip Redundent WhiteSpace*/void skipRWS(){
			boolean b=Character.isWhitespace(c);
			while(b && c!='\0'){
				char x=peek();
				if(b=Character.isWhitespace(x))
					nxt();
			}
		}

		void skipRWSx(char...p){
			skipRWS();
			char x=peek();int i=-1,n=p==null?0:p.length;boolean b=false;
			do{
				if((b=++i<n)&&p[i]==x){
					b=false;nxt();}
			}while(b);
		}// boolean chk(){boolean b=Character.isWhitespace(c)||c=='/';while(b && c!='\0'){//Character.isWhitespace(c)||)char x=peek();if(c=='/' &&(lookahead("//") || lookahead("/*"))){	skipWhiteSpace();b=Character.isWhitespace(c);}else if(x=='/' &&(lookahead(x+"//") || lookahead(x+"/*") )){}else{	if(b=Character.isWhitespace(x))nxt();}}return false;}

		public Object parse()throws Exception{
			c=read();
			Object r=c!='\0'?parseItem():null;
			skipWhiteSpace();if(c!='\0')
			{LinkedList l=new LinkedList();l.add(r);
				while(c!='\0'){
					r=parseItem();
					l.add(r);
				}r=l;}
			return r;}

		public Object parseItem()throws Exception{
			Object r=null;int i;skipWhiteSpace();switch(c)
			{ case '"':case '`':case '\'':r=extractStringLiteral();break;
				case '0':case '1':case '2':case '3':case '4':
				case '5':case '6':case '7':case '8':case '9':
				case '-':case '+':case '.':r=extractDigits();break;
				case '[':r=extractArray();break;
				case '{':Map m=extractObject();
					r=m==null?null:m.get("class");
					if("date".equals(r)){r=m.get("time");
						r=new Date(((Number)r).longValue());}
					else r=m;break;
				case '|':/**raw text , using Designated-Boundary-Text (DBT), the Designated-Boundary-Text may have a length of zero or one or more
				 //|<Designated-Boundary-Text ,ending with | bar><then raw-text, until the same pattern repeats which is |<Designated-Boundary-Text>|
				 //e.g. ||text maybe containing newlines ,and/or backslashes ,and/or a single hash  ,and/or single-quotation ,and/or double-quotation, and/or back-tic||
				 //but in case the text(before-encoding) has two-consecutive-bar then the DBT length has to be longer than zero
				 //e.g. |-| I'm raw text with two consecutive bars ||, the end|-|
				 //or instead of a single dash, any specified text as Designated-Boundary-Text, then bar*/
					char h=c,z='\0';bNxt();
					while(c!=z && c!=h )
						bNxt();
					buff();
					String boundary=consume();
					boolean b=true;nxt();
					while(c!=z && b)
						if(c==h && lookahead(boundary))
							b=false;
						else bNxt();
					r=consume();lookahead.setLength(0);
					nxt();
					break;
	/*case '\''://quoted string that takes nl,cr,d-quots, and backslash is not an escape char, and only escaped by s-quot by a double-s-quot
		char h=c,z='\0';nxt();
		boolean b=true;
		while(c!=z && b)
			if(c==h && !lookahead("''"))
				b=false;
			else bNxt();
		r=consume();lookahead.setLength(0);
		nxt();
	break;
	case '%'://decode str to binary: ascii values from 32 to 255 are the same values, from 0 to 31 are mapped to 256-287
		break;
	case '$':
		//instanciate a new object from a referenced prototype, e.g. a control statement , or any other class
		break;
	case '&'://&<path>=<json in general>
		//set reference of object in cache // e.g. you could do: define a new ref to a new proto (X), and then set a new ref for a newly instanciated obj as a subclass from (X)
		//&=if:
		break;
	case '@':
		//refer to object from cache
		break;

	case 'function':break;//only a thought, cuz c is only a char
		*/
				case '(':nxt();
					//if(lookahead("sql:"));else//e.g. (sql:q2d(<sql-str>):params:<json-array of params>)
					//if(lookahead("R["));else
				{	skipRWS();//skipWhiteSpace();
					r=parseItem();
					skipWhiteSpace();
					if(c==')')
						nxt();
					else{LinkedList l=new LinkedList();
						l.add(r);
						while(c!=')' && c!='\0'){
							r=parseItem();
							l.add(r);
							skipWhiteSpace();
						}if(c==')')
							nxt();
						r=l;}}break;
				default:r=extractIdentifier();
			}skipRWS();//skipWhiteSpace();
			if(comments!=null&&((i=comments.indexOf("cachePath=\""))!=-1
				||(cache!=null&&comments.startsWith("cacheReference"))))
			{	if(i!=-1)
			{	if(cache==null)
				cache=new HashMap<String,Object>();
				int j=comments.indexOf("\"",i+=11);
				cache.put(comments.substring(i,j!=-1?j:comments.length()),r);
			}else
				r=cache.get(r);
				comments=null;
			}
			return r;}

		public String extractStringLiteral()throws Exception{
			char first=c;nxt();boolean b=c!=first&&c!='\0';
			while(b)
			{if(c=='\\'){nxt();switch(c)
			{case 'n':buff('\n');break;case 't':buff('\t');break;
				case 'r':buff('\r');break;case '0':buff('\0');break;
				case 'x':case 'X':buff( (char)
					java.lang.Integer.parseInt( next(2)//p.substring(offset,offset+2)
						,16));nxt();//next();
				break;
				case 'u':
				case 'U':buff( (char)
					java.lang.Integer.parseInt(
						next(4)//p.substring(offset,offset+4)
						,16));//next();next();next();//next();
					break;default:if(c!='\0')buff(c);}}
			else buff(c);
				nxt();b=c!=first&&c!='\0';
			}if(c==first)nxt();return consume();}

		public Object extractIdentifier(){
			while(!Character.isUnicodeIdentifierStart(c))
			{System.err.println("unexpected:"+c+" at row,col="+rc());nxt();return Literal.Null;}
			bNxt();
			while(c!='\0'&&Character.isUnicodeIdentifierPart(c))bNxt();
			String r=consume();
			return "true".equals(r)?new Boolean(true)
				:"false".equals(r)?new Boolean(false)
				:"null".equals(r)?Literal.Null:"undefined".equals(r)?Literal.Undefined:r;}

		public Object extractDigits(){
			if(c=='0')//&&offset+1<len)
			{char c2=peek();if(c2=='x'||c2=='X')
			{nxt();nxt();
				while((c>='A'&&c<='F')
					||(c>='a'&&c<='f')
					||Character.isDigit(c))bNxt();
				String s=consume();
				try{return Long.parseLong(s,16);}
				catch(Exception ex){}return s;}
			}boolean dot=c=='.';
			bNxt();//if(c=='-'||c=='+'||dot)bNxt();else{c=p.charAt(i);}
			while(c!='\0'&&Character.isDigit(c))bNxt();
			if(!dot&&c=='.'){dot=true;bNxt();}
			if(dot){while(c!='\0'&&Character.isDigit(c))bNxt();}
			if(c=='e'||c=='E')
			{dot=false;bNxt();if(c=='-'||c=='+')bNxt();
				while(c!='\0'&&Character.isDigit(c))bNxt();
			}else if(c=='l'||c=='L'||c=='d'||c=='D'||c=='f'||c=='F')bNxt();
			String s=consume();//p.substring(i,offset);
			if(!dot)try{return Long.parseLong(s);}catch(Exception ex){}
			try{return Double.parseDouble(s);}catch(Exception ex){}return s;}

		public List<Object> extractArray()throws Exception{
			if(c!='[')return null;
			nxt();char x=0;
			LinkedList<Object> l=new LinkedList<Object>();
			Object r=null;
			skipWhiteSpace();
			if(c!='\0'&&c!=']')
			{	r=parseItem();
				l.add(r);
			}if(c!='\0'&&c!=']')
				skipRWSx(']',',');//skipRWS();x=peek();if(x==']'||x==',') nxt();//skipWhiteSpace();
			while(c!='\0'&&c!=']')
			{	if(c!=','&&!Character.isWhitespace(c))//throw new IllegalArgumentException
				System.out.println("Array:"+rc()+" expected ','");
				nxt();
				r=parseItem();
				l.add(r);
				skipRWSx(']',',');//skipRWS();x=peek();if(x==']'||x==',')nxt();//skipWhiteSpace();
			}if(c==']')
				nxt();
			skipRWS();
			return l;}

		public Map<Object,Object> extractObject()throws Exception{
			final char bo='{',bc='}';
			if(c==bo)nxt();
			else return null;
			skipWhiteSpace();
			HashMap<Object,Object> r=new HashMap<Object,Object>();
			Object k,v;Boolean t=new Boolean(true);
			while(c!='\0'&&c!=bc)
			{v=t;
				k=parseItem();//if(c=='"'||c=='\''||c=='`')k=extractStringLiteral();else k=extractIdentifier();
				skipWhiteSpace();
				if(c==':'||c=='='){//||Character.isWhitespace(c)
					nxt();
					v=parseItem();
					skipWhiteSpace();
				}//else if(c==','){nxt();
				if(c!='\0'&&c!=bc){
					if(c!=',')
						System.out.print(//throw new IllegalArgumentException(
							"Object:"+rc()+" expected '"+bc+"' or ','");
					nxt();
					skipWhiteSpace();
				}
				r.put(k,v);
			}
			if(c==bc)
				nxt();
			skipRWS();
			return r;}

		public void skipWhiteSpace(){
			boolean b=false;do{
				while(b=Character.isWhitespace(c))nxt();
				b=b||(c=='/'&&skipComments());}while(b);}

		public boolean skipComments(){
			char c2=peek();if(c2=='/'||c2=='*'){nxt();nxt();
				StringBuilder b=new StringBuilder();if(c2=='/')
				{while(c!='\0'&&c!='\n'&&c!='\r')bNxt();
					if(c=='\n'||c=='\r'){nxt();if(c=='\n'||c=='\r')nxt();}
				}else
				{while(c!='\0'&&c2!='/'){bNxt();if(c=='*')c2=peek();}
					if(c=='*'&&c2=='/'){b.deleteCharAt(b.length()-1);nxt();nxt();}
				}comments=b.toString();return true;}return false;}

		/**read a char from the rdr*/
		char read(){
			int h=-1;try{h=rdr.read();}
			catch(Exception ex){TL.tl().error(ex, "fm.tl.json.prsr.read");}
			char c= h==-1?'\0':(char)h;
			return c;}

		public char peek(){
			char c='\0';
			int n=lookahead.length();
			if(n<1){
				c=read();
				lookahead.append(c);}
			else c=lookahead.charAt(0);
			return c;}

		public int _row,_col;String rc(){return "("+_row+','+_col+')';}
		public void nlRC(){_col=1;_row++;}public void incCol(){_col++;}
		//boolean eof,mode2=false;
		public char setEof(){return c='\0';}

		/**update the instance-vars (if needed):c,row,col,eof*/
		public char nxt(char h){
			if(h=='\0'||h==-1||c=='\0')return setEof();
				//if(c=='\0')return setEof();//c='\0';
			else c=h;
			if(c=='\n')
				nlRC();
			else incCol();
			return c;}

		/**put into the buffer the current c , and then call nxt()*/
		public char bNxt(){buff();return nxt();}

		/**read from the reader a char and store the read char into member-variable c, @returns member-variable c*/
		public char nxt(){
			char h='\0';
			if(c=='\0')return setEof();//=h;
			if(lookahead.length()>0){
				h=lookahead.charAt(0);
				lookahead.deleteCharAt(0);
			}else h=read();
			c=nxt(h);
			return c;}

		/**this method works differently than next(), in particular how char c is read and buffered*/
		public String next(int n)
		{String old=consume(),retVal=null;while(n-->0)buff(nxt());retVal=consume();buff.append(old);return retVal;}

		public char buff(){return buff(c);}
		char buff(char p){buff.append(p);return p;}

		/**empty the member-variable buff , @returns what was stored in buff*/
		public String consume(){String s=buff.toString();buff.replace(0, buff.length(), "");return s;}

		public boolean lookahead(String p,int offset){
			int i=0,pn=p.length()-offset,ln=lookahead.length();
			boolean b=false;char c=0,h=0;if(pn>0)
				do{h=p.charAt(i+offset);
					if(i<ln)
						c=lookahead.charAt(i);
					else{
						c=read();
						lookahead.append(c);
					}
				}while( (b=(c==h ||
					Character.toUpperCase(c)==
					Character.toUpperCase(h))
				)&& (++i)<pn );
			return b;}

		public boolean lookahead(String p){return lookahead(p,0);}

	}//Prsr
}//class Json

}//class TL

%>