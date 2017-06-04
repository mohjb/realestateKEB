package org.kisr.realestateKeb;



import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.net.URL;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;


public class TL
{
    @Override public String toString(){
        String s=null; try{s=new Json.Output().o(this).toString();}
        catch(Exception x){s=super.toString();tl().error("TL.toString:"+s,x);}
        return s;}

    enum context{;enum DB{
        pool("dbpool-realestateKeb")
        ,reqCon("javax.sql.PooledConnection")
        ,server0("localhost")
        ,server("localhost")
        ,dbName("realestate")
        ,un("root")
        ,pw("","qwerty","root");
        String str,a[];DB(String...p){str=p[0];a=p;}
        public static int len(){int r=0;
            for(DB i:values())
                if( r<i.a.length)
                    r=i.a.length;
            return r;}}
    }//context

    //TL member variables
    public String ip;
    public App.Usr usr;
    public App.Ssn ssn;
    public Map<String,Object>json;

    public Map response;
    public Date now;//,sExpire;

    /**the static/class variable "tl"*/ static ThreadLocal<TL> tl=new ThreadLocal<TL>();
    static boolean LogOut=false;//tlLog=true;
    public boolean logOut=LogOut;

    public TL(GenericServlet s,HttpServletRequest r,HttpServletResponse n,PrintWriter o){
        h.srvlt=s;h.req=r;h.rspns=n;h.out=o;}


    /**sets a new TL-instance to the localThread*/

    public static TL Enter(GenericServlet s,HttpServletRequest r,HttpServletResponse n,PrintWriter o)
            throws IOException
    {TL p;tl.set(p=new TL(s,r,n,o));p.onEnter();return p;}
    public H h=new H();
    public class H{
        public final String CommentHtml[]={"\n<!--","-->\n"},CommentJson[]={"\n/*","\n*/"};
        public String comments[]=CommentJson;
        public HttpServletRequest req;
        public HttpServletResponse rspns;PrintWriter out;
        public GenericServlet srvlt;
        public HttpServletRequest getRequest(){return req;}
        public HttpSession getSession(){return req.getSession();}
        public PrintWriter getOut() throws IOException{if(out==null)out=rspns.getWriter();return out;}
        public ServletContext getServletContext(){return srvlt.getServletContext();}

        /**get a request-scope attribute*/
        public Object r(Object n)
        {return req!=null?req.getAttribute(String.valueOf(n)):null;}

        /**set a request-scope attribute*/
        public Object r(Object n,Object v)
        {if(req!=null)req.setAttribute(String.valueOf(n),v);return v;}

        /**get a session-scope attribute*/
        public Object s(Object n)
        {return req!=null?
                req.getSession().getAttribute(String.valueOf(n))
                :null;}

        /**set a session-scope attribute*/
        public Object s(Object n,Object v)
        {   req.getSession().setAttribute(String.valueOf(n),v)
            ;return v;}

        /**get an application-scope attribute*/
        public  Object a(Object n)
        {return req!=null?getServletContext().getAttribute(String.valueOf(n))
                :null;}

        /**set an application-scope attribute*/
        public void a(Object n,Object v)
        {getServletContext().setAttribute(String.valueOf(n),v);}


        /**get variable, a variable is considered
         1: a parameter from the http request
         2: if the request-parameter is not null then set it in the session with the attribute-name pn
         3: if the request-parameter is null then get pn attribute from the session
         4: if both the request-parameter and the session attribute are null then return null
         @parameters String pn Parameter/attribute Name
         HttpSession ss the session to get/set the attribute
         HttpServletRequest rq the http-request to get the parameter from.
         @return variable value.*/
        public  Object var(String pn)
        {HttpSession ss=getSession();HttpServletRequest rq=getRequest();
            Object r=null;try{Object css=ss.getAttribute(pn);String csr=req(pn);
            if(csr!=null&&!csr.equals(css))ss.setAttribute(pn,r=csr);
            else if(css!=null)r=css;}catch(Exception ex){ex.printStackTrace();}return r;}

        public Number var(String pn,Number r)
        {Object x=var(pn);return x==null?r:x instanceof Number?(Number)x:Double.parseDouble(x.toString());}

         String var(String pn,String r)
        {Object x=var(pn);return x==null?r:String.valueOf(x);}

         boolean var(String pn,boolean r)
        {Object x=var(pn);return x==null?r:x instanceof
                Boolean?(Boolean)x:Boolean.parseBoolean(x.toString());}

/////////////////////////////// */


        public String req(String n)
        {if(json!=null )
        {Object o=json.get(n);if(o!=null)return o.toString();}
            HttpServletRequest q=getRequest();
            String r=q.getParameter(n);
            if(r==null)r=q.getHeader(n);
            if(logOut)log("TL.req(",n,"):",r);
            return r;}

        public int req(String n,int defval)
        {String s=req(n);
            int r=Util.parseInt(s, defval);
            return r;}

        public Date req(String n,Date defval)
        {String s=req(n);if(s!=null)
            defval=Util.parseDate(s);//(s, defval);
            return defval;}

        public double req(String n,double defval)
        {String s=req(n);if(s!=null)
            try{defval=Double.parseDouble(s);//(s, defval);
            }catch(Exception x){}
            return defval;}

/*
 public static Object req(String n,Object defval)
 {String s=req(n);if(s!=null)
  try{	Class c=(Class<T>) defval.getClass();
	if(c.isEnum()){
		for(Object  o:c.getEnumConstants())
			if(s.equalsIgnoreCase(o.toString()))
				return o;
  }}catch(Exception x){}
  return defval;}*/


        public <T>T req(String n,T defval)
        {String s=req(n);if(s!=null)
            try{	Class<T> c=(Class<T>) defval.getClass();
                if(c.isEnum()){
                    for(T o:c.getEnumConstants())
                        if(s.equalsIgnoreCase(o.toString()))
                            return o;
                }}catch(Exception x){}
            return defval;}

    }//class H

    public static class Util{//utility methods

        public static Map<Object, Object> mapCreate(Object...p)
        {Map<Object, Object> m=p.length>1?new HashMap():null;return maPSet(m,p);}

        public static Map<Object, Object> mapSet(Map<Object, Object> m,Object...p){return maPSet(m,p);}

        public static Map<Object, Object> maPSet(Map<Object, Object> m,Object[]p)
        {for(int i=0;i<p.length;i+=2)m.put(p[i],p[i+1]);return m;}

        public final static java.text.SimpleDateFormat
                dateFormat=new java.text.SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        public static Integer[]parseInts(String s){
            java.util.Scanner c=new java.util.Scanner(s).
                    useDelimiter("[\\s\\.\\-/\\:A-Za-z,]+");
            List<Integer>l=new LinkedList<Integer>();
            while(c.hasNextInt()){
                //if(c.hasNextInt())else c.skip();
                l.add(c.nextInt());
            }
            Integer[]a=new Integer[l.size()];l.toArray(a);
            return a;}

        static Date parseDate(String s){
            Integer[]a=parseInts(s);int n=a.length;
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

        public static List lst(Object...p){List r=new LinkedList();for(Object o:p)r.add(o);return r;}

        public static int parseInt(String v,int dv)
        {if(v!=null)try{dv=Integer.parseInt(v);}finally{}return dv;}

    }//class util

//static{log("TL.static:version 2015.10.22.08.08,9.31,13.42");}

    private void onEnter()throws IOException
    {ip=h.getRequest().getRemoteAddr();
        now=new Date();
        try{Object o=h.req.getContentType();
            o=o==null?null:o.toString().indexOf("json")!=-1?Json.Parser.parse(h.req):null;
            json=o instanceof Map<?, ?>?(Map<String, Object>)o:null;//req.getParameterMap() ;
            response=TL.Util.mapCreate(//"msg",0 ,
                    "return",false , "op",h.req("op"),"req",o);
            App.Ssn.onEnter();
        }catch(Exception ex){error("TL.onEnter",ex);}
        //if(pages==null){rsp.setHeader("Retry-After", "60");rsp.sendError(503,"pages null");throw new Exception("pages null");}
        if(logOut)new Json.Output(h.getOut()).o(h.comments[0],this,h.comments[1]);
        //else log(new Json.Output().o(this).toString());
    }//onEnter

    private void onExit(){usr=null;ssn=null;ip=null;now=null;h.srvlt=null;h.rspns=null;h.req=null;response=json=null;}

    /**unsets the JSP PageContext to the localThread*/
    public static void Exit()throws Exception{TL p=TL.tl();
        DB.close((Connection)p.h.getRequest().getAttribute(context.DB.reqCon.str));
        p.onExit();tl.set(null);}

    /**get the TL-instance for the current Thread*/
    public static TL tl(){Object o=tl.get();return o instanceof TL?(TL)o:null;}

    /**get a pooled jdbc-connection for the current Thread, calling the function dbc()*/
    static Connection dbc()throws SQLException
    {TL p=tl();Object o=p.h.getRequest().getAttribute(context.DB.reqCon.str);
        if(o==null||!(o instanceof Connection))
        {p.h.getRequest().setAttribute(context.DB.reqCon.str,o=DB.c());}
        return (Connection)o;}


    ////////////////////////////////
    public String logo(Object...a){String s=null;
        if(a!=null&&a.length>0)
            try{TL p=this;
                Json.Output o=new Json.Output();
                for(Object i:a)o.o(i);
                s=o.toString();
                if(p.logOut){p.h.getOut().flush();
                    out(p.h.comments[0]//"\n/*"
                            ,s,p.h.comments[1]//"*/\n"
                    );}}catch(Exception ex){ex.printStackTrace();}return s;}

    /**calls the servlet log method*/

    public void log(Object...s){logA(s);}
    public void logA(Object[]s){try{//TL p=tl();
        StringBuilder b=new StringBuilder();//builder towards the log
        if(logOut){h.getOut().flush();
            out(h.comments[0]);
        }
        GenericServlet g=h.srvlt;
        for(Object t:s){b.append(t);//g.log(String.valueOf(t));
            if(logOut)out(t);}
        if(logOut)out(h.comments[1]);//"*/\n");
        g.log(b.toString());
    }catch(Exception ex){ex.printStackTrace();}}

    /**calls the servlet log method*/
    public void log(String s,Throwable x){try{
        h.srvlt.log(s,x);
        if(logOut){h.getOut().flush();
            out(h.comments[0]//"\n/*"
                    ,s,"\n---\n",Json.Output.out(x),h.comments[1]//"*/\n"
            );}}catch(Exception ex){ex.printStackTrace();}}

    /**calls the servlet log method*/
    public void error(String s){try{TL p=tl();
        h.srvlt.log("error:"+s);
        if(p.logOut)out(h.comments[0]//"\n/*"
                ,"error:",s,h.comments[1]//"*/\n"
        );}catch(Exception ex){ex.printStackTrace();}}

    public void error(String s,Throwable x){try{TL p=tl();
        h.srvlt.log("error:"+s,x);
        if(p.logOut)out(h.comments[0]//"\n/*
                ,"error:",s.replaceAll("<", "&lt;"),"\n---\n",x,h.comments[1]//"*/\n"
        );x.printStackTrace();}
    catch(Exception ex){ex.printStackTrace();}}

    public static class DB
    {//public static boolean dbLog=false;
        /**returns a jdbc pooled Connection.
         uses MysqlConnectionPoolDataSource with a database from the enum context.DB.url.str,
         sets the pool as an application-scope attribute named context.DB.pool.str
         when first time called, all next calls uses this context.DB.pool.str*/
        public static synchronized Connection c()throws SQLException
        { TL tl=tl();TL.H h=tl.h;Connection r=(Connection)h.r(context.DB.reqCon.str);if(r!=null)return r;
            MysqlConnectionPoolDataSource d=(MysqlConnectionPoolDataSource)h.a(context.DB.pool.str);
            r=d==null?null:d.getPooledConnection().getConnection();try{
            for(int i=0,n=context.DB.len();r==null&&i<n;i++)try
            {	d=new MysqlConnectionPoolDataSource();
                context.DB x=context.DB.un;
                d.setUser(x.a[Math.min(x.a.length-1,i)]);
                x=context.DB.pw;
                d.setPassword(x.a[Math.min(x.a.length-1,i)]);
                x=context.DB.server;
                d.setServerName(x.a[Math.min(x.a.length-1,i)]);
                d.setDatabaseName(context.DB.dbName.str);
                r=d.getPooledConnection().getConnection();
                h.a(context.DB.pool.str,d);
                h.r(context.DB.reqCon.str,r);
                if(tl.logOut)tl.log("new "+context.DB.pool.str+":"+d);
            }catch(Exception e){tl.log("TL.DB.MysqlConnectionPoolDataSource:",e);}//CHANGED:2015.10.23.16.06:Throwable
        }catch(Throwable e){tl.error("TL.DB.MysqlConnectionPoolDataSource:",e);}//ClassNotFoundException
            if(tl.logOut)tl.log(context.DB.pool.str+":"+d);
            if(r==null)try
            {r=java.sql.DriverManager.getConnection
                    ("jdbc:mysql://"+context.DB.server.str
                                    +"/"+context.DB.dbName.str
                            ,context.DB.un.str,context.DB.pw.str);
                h.r(context.DB.reqCon.str,r);
            }catch(Throwable e){tl.error("TL.DB.DriverManager:",e);}
            return r;}

        /**returns a jdbc-PreparedStatement, setting the variable-length-arguments parameters-p, calls dbP()*/
        public static PreparedStatement p(String sql,Object...p)throws SQLException{return P(sql,p);}

        /**returns a jdbc-PreparedStatement, setting the values array-parameters-p, calls TL.dbc() and log()*/
        public static PreparedStatement P(String sql,Object[]p)throws SQLException
        {return P(sql,p,true);}//tl().dbParamsOddIndex

        public static PreparedStatement P(String sql,Object[]p,boolean odd)throws SQLException
        {Connection c=dbc();TL tl=tl();
            PreparedStatement r=c.prepareStatement(sql);if(tl.logOut)
            tl.log("TL("+(tl())+").DB.P(sql="+sql+",p="+p+",odd="+odd+")");
            if(odd){if(p.length==1)
                r.setObject(1,p[0]);else
                for(int i=1;p!=null&&i<p.length;i+=2)
                    r.setObject(i/2+1,p[i]);//if(tl.logOut)TL.log("dbP:"+i+":"+p[i]);
            }else
                for(int i=0;p!=null&&i<p.length;i++)
                {r.setObject(i+1,p[i]);if(tl.logOut)tl.log("dbP:"+i+":"+p[i]);}
            if(tl.logOut)tl.log("dbP:sql="+sql+":n="+(p==null?-1:p.length)+":"+r);return r;}

        /**returns a jdbc-ResultSet, setting the variable-length-arguments parameters-p, calls dbP()*/
        public static ResultSet r(String sql,Object...p)throws SQLException{return P(sql,p,true).executeQuery();}

        /**returns a jdbc-ResultSet, setting the values array-parameters-p, calls dbP()*/
        public static ResultSet R(String sql,Object[]p)throws SQLException{
            PreparedStatement x=P(sql,p,true);
            ResultSet r=x.executeQuery();
            return r;}

        /**closes the resultSet-r and the statement, but DOES-NOT close the connection*/
        public static void closeRS(ResultSet r)//,Connection c
        {if(r!=null)try{Statement s=r.getStatement();r.close();s.close();}catch(Exception e){e.printStackTrace();}}
        public void close(ResultSet r){if(r!=null)try{Statement s=r.getStatement();r.close();close(s);}catch(Exception e){e.printStackTrace();}}
        public static void close(Statement s){try{Connection c=s.getConnection();s.close();close(c);}catch(Exception e){e.printStackTrace();}}
        public static void close(Connection c){
            try{if(c!=null){
                tl().h.r("java.sql.Connection",null);
                c.close();}
            }catch(Exception e){e.printStackTrace();}}

        /**returns a string or null, which is the result of executing sql,
         calls dpR() to set the variable-length-arguments parameters-p*/
        public static String q1str(String sql,Object...p)throws SQLException{return q1Str(sql,p);}
        public static String q1Str(String sql,Object[]p)throws SQLException
        {String r=null;ResultSet s=null;try{s=R(sql,p);r=s.next()?s.getString(1):null;}finally{closeRS(s);}return r;}//CHANGED:2015.10.23.16.06:closeRS ; CHANGED:2011.01.24.04.07 ADDED close(s,dbc());

        public static String newUuid()throws SQLException{return q1str("select uuid();");}

        /**returns an java obj, which the result of executing sql,
         calls dpR() to set the variable-length-arguments parameters-p*/
//public static Object q1obj(String sql,Object...p)throws Exception{ResultSet s=null;try{s=R(sql,p);return s.next()?s.getObject(1):null;}finally{close(s,dbc());}}
        public static Object q1obj(String sql,Object...p)throws SQLException{return q1Obj(sql,p);}
        public static Object q1Obj(String sql,Object[]p)throws SQLException
        {ResultSet s=null;try{
            s=R(sql,p);
            return s.next()?s.getObject(1):null;
        }finally{closeRS(s);}}

        /**returns an integer or df, which the result of executing sql,
         calls dpR() to set the variable-length-arguments parameters-p*/
        public static int q1int(String sql,int df,Object
                ...p)throws SQLException{return q1Int(sql,df,p);}

        public static int q1Int(String sql,int df,Object[]p)throws SQLException
        {ResultSet s=null;try{s=R(sql,p);return s.next()?s.getInt(1):df;}finally{closeRS(s);}}//CHANGED:2015.10.23.16.06:closeRS ;

        /**returns a double or df, which is the result of executing sql,
         calls dpR() to set the variable-length-arguments parameters-p*/
        public static double q1dbl(String sql,double df,Object...p)throws SQLException
        {ResultSet s=null;try{s=R(sql,p);return s.next()?s.getDouble(1):df;}finally{closeRS(s);}}//CHANGED:2015.10.23.16.06:closeRS ;

        /**returns as an array of rows of arrays of columns of values of the results of the sql
         , calls dbL() setting the variable-length-arguments values parameters-p*/
        public static Object[][]q(String sql,Object...p)throws SQLException{return Q(sql,p);}

        public static Object[][]Q(String sql,Object...p)throws SQLException
        {List<Object[]>r=L(sql,p);Object b[][]=new Object[r.size()][];r.toArray(b);r.clear();return b;}

        /**return s.getMetaData().getColumnCount();*/
        public static int cc(ResultSet s)throws SQLException{return s.getMetaData().getColumnCount();}

        /**calls L()*/
        public static List<Object[]> l(String sql,Object...p)throws SQLException{return L(sql,p);}

        /**returns a new linkedList of the rows of the results of the sql
         ,each row/element is an Object[] of the columns
         ,calls dbR() and dbcc() and dbclose(ResultSet,TL.dbc())*/
        public static List<Object[]> L(String sql,Object[]p)throws SQLException
        {ResultSet s=null;List<Object[]> r=null;try{s=R(sql,p);Object[]a;r=new LinkedList<Object[]>();
            int cc=cc(s);while(s.next()){r.add(a=new Object[cc]);
                for(int i=0;i<cc;i++){a[i]=s.getObject(i+1);
                }}return r;}finally{closeRS(s);//CHANGED:2015.10.23.16.06:closeRS ;
            if(tl().logOut)try{tl().log(new Json.Output("TL.DB.L:sql=")
                    .o(sql).w(",prms=").o(p).w(",return=").o(r).toString());}catch(IOException x){tl().error("TL.DB.List:"+sql,x);}}}

        public static List<Object> q1colList(String sql,Object...p)throws SQLException
        {ResultSet s=null;List<Object> r=null;try{s=R(sql,p);r=new LinkedList<Object>();
            while(s.next())r.add(s.getObject(1));return r;}
        finally{closeRS(s);if(tl().logOut)
            try{tl().log(new Json.Output("TL.DB.q1colList:sql=")//CHANGED:2015.10.23.16.06:closeRS ;
                    .o(sql).w(",prms=").o(p).w(",return=").o(r).toString());}catch(IOException x){tl().error("TL.DB.q1colList:"+sql,x);}}}

        public static Object[] q1col(String sql,Object...p)throws SQLException
        {List<Object> l=q1colList(sql,p);Object r[]=new Object[l.size()];l.toArray(r);l.clear();return r;}

        /**returns a row of columns of the result of sql
         ,calls dbR(),dbcc(),and dbclose(ResultSet,TL.dbc())*/
        public static Object[] q1row(String sql,Object...p)throws SQLException{return q1Row(sql,p);}
        public static Object[] q1Row(String sql,Object[]p)throws SQLException
        {ResultSet s=null;try{s=R(sql,p);Object[]a=null;int cc=cc(s);if(s.next())
        {a=new Object[cc];for(int i=0;i<cc;i++)try{a[i]=s.getObject(i+1);}
        catch(Exception ex){tl().error("TL.DB.q1Row:"+sql,ex);a[i]=s.getString(i+1);}}
            return a;}finally{closeRS(s);}}//CHANGED:2015.10.23.16.06:closeRS ;

        /**returns the result of (e.g. insert/update/delete) sql-statement
         ,calls dbP() setting the variable-length-arguments values parameters-p
         ,closes the preparedStatement*/
        public static int x(String sql,Object...p)throws SQLException{return X(sql,p);}

        public static int X(String sql,Object[]p)throws SQLException
        {int r=-1;try{PreparedStatement s=P(sql,p,false);r=s.executeUpdate();s.close();return r;}
        finally{if(tl().logOut)try{
            tl().log(new Json.Output("TL.DB.x:sql=").o(sql).w(",prms=").o(p).w(",return=").o(r).toString());}
        catch(IOException x){tl().error("TL.DB.X:"+sql,x);}}}

        public static void q2json(String sql,Object...p)throws SQLException
        {ResultSet s=null;try{s=R(sql,p);try{(new Json.Output()).o(s);}catch (IOException e) {e.printStackTrace();}}
        finally{closeRS(s);if(tl().logOut)try{tl().log(new Json.Output(
                "TL.DB.L:q2json=").o(sql).w(",prms=").o(p).toString());
        }catch(IOException x){tl().error("TL.DB.q1json:"+sql,x);}}}

        /**return a list of maps , each map has as a key a string the name of the column, and value obj*/
        static List<Map<String,Object>>json(String sql,Object...p) throws SQLException{return Lst(sql,p);}
        static List<Map<String,Object>>Lst(String sql,Object[ ]p) throws SQLException{
            List<Map<String,Object>>l=new LinkedList
                    < Map < String ,Object>>();ItTbl i=new ItTbl(sql,p);
            List<String>cols=new LinkedList<String>();
            for(int j=1;j<=i.row.cc;j++)cols.add(i.row.m.getColumnLabel(j));
            for(ItTbl.ItRow w:i){Map<String,Object>m=
                    new HashMap<String,Object>();l.add(m);
                for(Object o:w)m.put(cols.get(w.col-1),o);
            }return l;}

        public static class ItTbl implements Iterator<ItTbl.ItRow>,Iterable<ItTbl.ItRow>{
            ItRow row=new ItRow();

            public ItRow getRow(){return row;}

            public static ItTbl it(String sql,Object...p){return new ItTbl(sql,p);}

            public ItTbl(String sql,Object[]p){
                try {init(TL.DB.R(sql, p));}
                catch (Exception e) {tl().logo("TL.DB.ItTbl.<init>:Exception:sql=",sql,",p=",p," :",e);}}

            public ItTbl(ResultSet o) throws SQLException{init(o);}

            public ItTbl init(ResultSet o) throws SQLException
            {row.rs=o;row.m=o.getMetaData();row.row=row.col=0;
                row.cc=row.m.getColumnCount();return this;}

            @Override public boolean hasNext(){
                boolean b=false;try {if(b=row!=null&&row.rs!=null&&row.rs.next())row.row++;
                else TL.DB.closeRS(row.rs);//CHANGED:2015.10.23.16.06:closeRS ;  //,row.rs.getStatement().getConnection());
                }catch (SQLException e) {e.printStackTrace();}return b;}

            @Override public ItRow next() {if(row!=null)row.col=0;return row;}
            @Override public void remove(){throw new UnsupportedOperationException();}

            @Override public Iterator<ItRow>iterator(){return this;}

            public class ItRow implements Iterator<Object>,Iterable<Object>{
                ResultSet rs;int cc,col,row;ResultSetMetaData m;
                public int getCc(){return cc;}
                public int getCol(){return col;}
                public int getRow(){return row;}
                @Override public Iterator<Object>iterator(){return this;}

                @Override public boolean hasNext(){return col<cc;}

                @Override public Object next(){
                    try {return rs==null?null:rs.getObject(++col);}
                    catch (SQLException e) {e.printStackTrace();}
                    return null;}

                @Override public void remove(){throw new UnsupportedOperationException();}

                public int nextInt(){
                    try {return rs==null?-1:rs.getInt(++col);}
                    catch (SQLException e) {e.printStackTrace();}
                    return -1;}
                public String nextStr(){
                    try {return rs==null?null:rs.getString(++col);}
                    catch (SQLException e) {e.printStackTrace();}
                    return null;}

            }//ItRow

        }//ItTbl

        /**represents one entity , one row from a table in a relational database*/
        public abstract static class Tbl extends Form{
            static final String StrSsnTbls="TL.DB.Tbl.tbls";
            //public Map<Class<? extends App.Sql>,App.Sql>tbls;
            public static Tbl tbl(Class<? extends Tbl>p){
                TL tl=tl();H h=tl.h;Object o=h.s(StrSsnTbls);
                Map<Class<? extends Tbl>,Tbl>tbls=o instanceof Map?(Map)o:null;
                if(tbls==null)h.s(StrSsnTbls,tbls=new HashMap<Class<? extends Tbl>,Tbl>());
                Tbl r=tbls.get(p);if(r==null)try {tbls.put(p, r=p.newInstance());}
                //catch (InstantiationException ex) {}catch(IllegalAccessException ex){}
                catch(Exception ex){tl.error("TL.DB.Tbl.tbl(Class<TL.DB.Tbl>"+p+"):Exception:", ex);}
                return r;}

// public void onLogout(){}

            /**Sql-Column Interface, for enum -items that represent columns in sql-tables
             * the purpose of creating this interface is to centerlize
             * the definition of the names of columns in java source code*/
            public interface CI extends FI{
                //**per column, get the primary key column*/public CI pkc();
                //**per column, get the primary key value */public Object pkv();
                /**per column, load from the sql/db table
                 * the value of this column, store the value
                 * in the F field and return the value*/Object load();
                /**per column, save into the db-table
                 * the value from the member field */public void save();
                //public StringBuilder where(StringBuilder b);
                //void save(<T>newVal);
                //String tblNm();
                public Class<? extends Tbl>cls();
                public Tbl tbl();
            }//interface CI

            public static CI[]cols(CI...p){return p;}
            public static CI[]orderBy(CI...p){return p;}//static Col[]groupBy(Col...p){return p;}
            public static Object[]where(Object...p){return p;}

            public abstract CI pkc();
            public abstract Object pkv();
            public abstract CI[]columns();

            @Override public FI[]flds(){return columns();}

            /**where[]={col-name , param}*/
            public int count(Object[]where) throws Exception{
                StringBuilder sql=new StringBuilder(
                        "select count(*) from `")
                        .append(getName())
                        .append("` where `")
                        .append(where[0])
                        .append("`=").append(Cols.M.m(where[0]).txt);//where[0]instanceof CI?m((CI)where[0]):'?');
                return DB.q1int(sql.toString(),-1,where[0],where[1]);}

            /**where[]={col-name , param}*/public
            int maxPlus1(CI col) throws Exception{
                StringBuilder sql=new StringBuilder(
                        "select max(`"+col+"`)+1 from `")
                        .append(getName()).append("`");
                return DB.q1int(sql.toString(),1);}

            /**returns one object from the db-query*/
            public Object obj(CI col,Object[]where) throws Exception{
                StringBuilder sql=new StringBuilder("select `")
                        .append(col).append("` from `")
                        .append(getName()).append('`');
                Cols.where(sql, where);
                return DB.q1Obj(sql.toString(),where);}

            /**returns one string*/
            public String select(CI col,Object[]where) throws Exception{
                StringBuilder sql=new StringBuilder("select `")
                        .append(col).append("` from `")
                        .append(getName()).append('`');
                Cols.where(sql, where);
                return DB.q1Str(sql.toString(),where);}

            /**returns one column, where:array of two elements:1st is column param, 2nd value of param*/
            Object[]column(CI col,Object...where) throws Exception{
                return DB.q1col("select `"+col+"` from `"+getName()
                                +"` where `"+where[0]+"`="
                                +Cols.M.m(where[0]).txt//(where[0]instanceof CI?m((CI)where[0]):Cols.M.prm)
                        ,where[0],where[1]);}//at

            /**returns a table*/
            public Object[][]select(CI[]col,Object[]where)throws Exception{
                StringBuilder sql=new StringBuilder("select ");
                Cols.generate(sql,col);
                sql.append(" from `").append(getName()).append('`');
                Cols.where(sql,where);
                return DB.Q(sql.toString(), where);}

/*public List<Map<String,Object>>select
	(CI[]cols
	,Object[]where
	,CI[]orderBy
	,CI[]groupBy)throws Exception{
	StringBuilder sql=new StringBuilder("select ");
	Cols.generate(sql,cols);
	sql.append(" from `").append(name).append('`');
	Cols.where(sql,where);
	if(orderBy!=null)for(int i=0;i<orderBy.length;i++)
		sql.append(i==0?" order by `":",`")
			.append(orderBy[i]).append("`");
	if(groupBy!=null)for(int i=0;i<groupBy.length;i++)
		sql.append(i==0?" group by `":",`")
			.append(groupBy[i]).append("`");
	return DB.Lst(sql.toString(), where);}

/*
int replace(CI[]cols,CI[]values,Object...prms)throws Exception{}
int insert(CI[]cols,Object...prms)throws Exception{}
int insert(CI[]cols,Object...prms)throws Exception{
	StringBuilder sql=new StringBuilder("insert into `")
	.append(name).append("` values(");
	Cols.generate(sql, cols);sql.append(')');
	//TL tl=TL.tl();//tl.dbParamsOddIndex=false;
	int i=TL.DB.X(sql.toString(), prms);
	//tl.dbParamsOddIndex=true;
	return i;}//insert

int replace(CI[]cols,CI[]values,Object...prms)throws Exception{
	StringBuilder sql=new StringBuilder
		("replace into `").append(name).append("` values(");
	Cols.generate(sql, cols);sql.append(')');
	//TL tl=TL.tl();tl.dbParamsOddIndex=false;
	int i=DB.X(sql.toString(), prms);
	//tl.dbParamsOddIndex=true;
	return i;}* /

Object[]load(Object pk)throws Exception{CI[]cols=cols();return load(pk,cols);}

Object[]load(Object pk,CI[]cols)throws Exception{
	StringBuilder sql=new StringBuilder("select ");
	Cols.generate(sql, cols).toString();
	sql.append(" from `").append(name).append('`');
	Object[]w=where(cols[0],pk);
	Cols.where(sql,w);
	Object[]a=DB.q1row//( sql.toString(), cols.length, w );
		(sql.toString(),pk);
	return a;}

Tbl load(){
	CI pkc=pkc(),c[]=columns();Object pkv=pkv();
	StringBuilder b=new StringBuilder("select ");
	Cols.generate(b,c);
	b.append(" from `").append(name)
	.append("` where `").append(pkc)
	.append("`=").append(Cols.M.m(pkc).txt);try{
	Object[]a=DB.q1row(b.toString(),pkv);
	int i=0;for(CI f:c)v(f,a[i++]);}
	catch(Exception x){error("DB.Tbl("+this+").load():pkv="+pkv,x);}return this;}

//Object load(CI c){return load(pkv()).v(c);}//load

//int sav(Object...vals)throws Exception{return save(cols(),vals,vals[0]==null);}
public int save(Object[]vals)throws Exception{return save(cols(),vals,vals[0]==null);}

*/

            /**loads one row from the table*/
            Tbl load(ResultSet rs)throws Exception{return load(rs,fields());}

            /**loads one row from the table*/
            Tbl load(ResultSet rs,Field[]a)throws Exception{
                int c=0;for(Field f:a)v(f,rs.getObject(++c));
                return this;}

            /**loads one row from the table*/
            public Tbl load(Object pk){
                ResultSet r=null;
                try{r=DB.r("select * from `"+getName()+"` where `"+pkc()+"`="+Cols.M.prm.txt,pk);
                    if(r.next())load(r);
                    else {tl().error("TL.DB.Tbl("+this+").load(pk="+pk+"):resultset.next=false");nullify();}}
                catch(Exception x){tl().error("TL.DB.Tbl("+this+"):"+pk, x);}
                finally{DB.closeRS(r);}
                return this;}public Tbl nullify(){return nullify(fields());}public Tbl nullify(Field[]a){for(Field f:a)v(f,null);return this;}

            /**loads one row from the table*/
            Tbl load(){return load(pkv());}

            /**loads one object from column CI c ,from one row of primary-key value pkv ,from the table*/
            Object load(CI c){Object pkv=pkv();
                Object o=null;try{o=DB.q1obj("select `"+c+"` from `"
                        +getName()+"` where `"+pkc()+"`="+Cols.M.m(c).txt,pkv);
                    v(c,o);}
                catch(Exception x){tl().error("TL.DB.Tbl("+this+").load(CI "+c+"):"+pkv,x);}
                return o;}//load

            /**vals[0] is assumed to be the primary key value*/
            int save(CI[]cols,Object[]vals,boolean newId)throws Exception{
                if(newId){
                    int x=DB.q1int("select max(`"
                            +pkc()+"`)+1 from `"+getName()+"`",1);
                    vals[0]=x;}
                StringBuilder sql=new StringBuilder("replace into`")
                        .append(getName()).append("`( ");
                Cols.generate(sql, cols).toString();
                sql.append(")values(").append(Cols.M.m(cols[0]).txt);v(cols[0],vals[0]);
                for(int i=1;i<cols.length;i++)
                {sql.append(",").append(Cols.M.m(cols[i]).txt);v(cols[i],vals[i]);}
                sql.append(")");
                int x=DB.X( sql.toString(), vals );
                return x;}

            Tbl save(CI c,Object v) {//throws Exception
                int i=0;try{DB.x("replace into `"+getName()+"` (`"
                        +pkc()+"`,`"+c+"`) values("+Cols.M.m
                        (pkc()).txt+","+Cols.M.m(c).txt+")",pkv(),v);
                    v(c,v);}
                catch(Exception x){tl().error("TL.DB.Tbl("+this+").save("+c+","+v+")",x);}
                return this;}//save

            Tbl save(CI c){// throws Exception
                CI pkc=pkc();
                Object cv=v(c),pkv=pkv();try{
                    DB.x("replace into `"+getName()+"` (`"+pkc+
                            "`,`"+c+"`) values("+Cols.M.m(pkc).txt
                            +","+Cols.M.m(c).txt+")",pkv,cv);}
                catch(Exception x){tl().error("TL.DB.Tbl("+this+").save("+c+"):pkv="+pkv,x);}
                return this;}//save

            public Tbl save() throws Exception{
                Object pkv=pkv();CI pkc=pkc();
                if(pkv==null){
                    int x=DB.q1int("select max(`"
                            +pkc+"`)+1 from `"+getName()+"`",1);
                    v(pkc,pkv=x);
                    tl().log("TL.DB.Tbl(",toJson(),").save-new:max(",pkc,") + 1:",x);
                }CI[]cols=columns();
                StringBuilder sql=new StringBuilder("replace into`")
                        .append(getName()).append("`( ");
                Cols.generate(sql, cols).toString();
                sql.append(")values(").append(Cols.M.m(cols[0]).txt);//Cols.M.prm);
                for(int i=1;i<cols.length;i++)
                    sql.append(",").append(Cols.M.m(cols[i]).txt);
                sql.append(")");
                int x=DB.X( sql.toString(), vals() );
                return this;}//save

//public Tbl(String name){super(name);}
//@Override public String toString(){return toJson();}
/*
public class It implements Iterator<Tbl>,Iterable<Tbl>{
public List list;public int i=-1;public Tbl row;
public It(List p,Tbl q){list=p;row=q;i=-1;}
//@Override
public Iterator<Tbl>iterator(){return this;}

@Override public boolean hasNext(){return list!=null&&row!=null&& i+1<list.size();}

@Override public Tbl next(){
	try {row.v(row.pkc(),list.get(++i));row.load();}
	catch (Exception ex) {return row=null;}
	return row;}

@Override public void remove(){throw new UnsupportedOperationException();}

}//It

public It it(List p,Tbl q){
	It r=new It(p,q);
	return r;}*/

            public Itrtr query(Object[]where){
                Itrtr r=new Itrtr(where);
                return r;}

            public class Itrtr implements Iterator<Tbl>,Iterable<Tbl>{
                public ResultSet rs=null;public int i=0;Field[]a;
                public Itrtr(Object[]where){a=fields();
                    StringBuilder sql=new StringBuilder("select * from `"+getName()+"`");
                    if(where!=null&&where.length>0)
                        Cols.where(sql, where);
                    try{rs=DB.R(sql.toString(), where);}
                    catch(Exception x){tl().error("TL.DB.Tbl("+this+").Itrtr.<init>:where="+where,x);}}

                @Override public Iterator<Tbl>iterator(){return this;}

                @Override public boolean hasNext(){boolean b=false;
                    try {b = rs!=null&&rs.next();} catch (SQLException x)
                    {tl().error("TL.DB.Tbl("+this+").Itrtr.hasNext:i="+i+",rs="+rs,x);}
                    if(!b&&rs!=null){DB.closeRS(rs);rs=null;}
                    return b;}

                @Override public Tbl next(){i++;/*
	try {int c=0;for(Field f:fields())try{v(f,rs.getObject(++c));}catch(Exception x)
	{TL.error("App.Sql("+this+").I2.next:i="+i+",c="+c+",rs="+rs,x);}}catch(Exception x)
	{TL.error("App.Sql("+this+").I2.next:i="+i+":"+rs, x);rs=null;}*/
                    try{load(rs,a);}catch(Exception x){tl().error("TL.DB.Tbl("
                            +this+").Itrtr.next:i="+i+":"+rs, x);rs=null;}
                    return Tbl.this;}

                @Override public void remove(){throw new UnsupportedOperationException();}

            }//Itrtr


            /**Class for Utility methods on set-of-columns, opposed to operations on a single column*/
            public static class Cols {//Marker ,sql-preparedStatement-parameter

                public enum M implements CI{
                    uuid("uuid()")
                    ,now("now()")
                    ,count("count(*)")
                    ,all("*")
                    ,prm("?")
                    ,password("password(?)")
                    ,Null("null")
                    ;String txt;
                    private M(String p){txt=p;}
                    public String text(){return txt;}
                    public Class<? extends Tbl>cls(){return Tbl.class;}
                    public Class<? extends Form>clss(){return cls();}
                    public Tbl tbl(){return null;}
                    public Field f(){return null;}
                    public Object value(){return null;}
                    public Object value(Object p){return null;}
                    public Object val(Form f){return null;}
                    public Object val(Form f,Object p){return null;}
                    public Object load(){return null;}
                    public void save(){}
                    public static M m(Object p){return p instanceof CI?m((CI)p):p instanceof Field?m((Field)p):prm;}
                    public static M m(CI p){return m(p.f());}
                    public static M m(Field p){
                        return p.getAnnotation(F.class).prmPw()?password:prm;}

                }//enum M

//public static StringBuilder where(StringBuilder b,Field f){M m=m(f);b.append("`").append(f.getName()).append("`=").append(m);return b;}

                public static Field f(String name,Class<? extends Tbl>c){
                    //for(Field f:fields(c))if(name.equals(f.getName()))return f;return null;
                    Field r=null;try{r=c.getField(name);}catch(Exception x)
                    {tl().error("TL.DB.Tbl.f("+name+","+c+"):",x);}
                    return r;}

                /**generate Sql into the StringBuilder*/
                public static StringBuilder generate(StringBuilder b,CI[]col){
                    return generate(b,col,",");}

                static StringBuilder generate(
                        StringBuilder b,CI[]col,String separator){
                    if(separator==null)separator=",";
                    for(int n=col.length,i=0;i<n;i++){
                        if(i>0)b.append(separator);
                        if(col[i] instanceof Cols.M)
                            //b.append(((Col)col[i]).name);
                            b.append(col[i]);
                            //else if(col[i] instanceof Tbl)b.append('\'').append(col[i]).append('\'');
                            //else if(col[i] instanceof String)b.append(col[i]);
                        else b.append("`").append(col[i]).append("`");}
                    return b;}

                static StringBuilder where(
                        StringBuilder b,Object[]where){b.append(" where ");
                    for(int n=where.length,i=0;i<n;i++){Object o=where[i];
                        if(i>0)b.append(" and ");
                        if(o instanceof Cols.M)b.append(o);else
                        if(o instanceof CI)//((CI)where[i]).where(b);
                            b.append('`').append(o).append("`=")
                                    .append(Cols.M.m(o).txt);
                        else tl().error("TL.DB.Tbl.Col.where:for:"+o);
                        i++;
                    }//for
                    return b;}

            }//class Cols

            public static class Creator{


                /**this annotation is only for creation of mySQL tables, does not play any role in the life-cycle of the java-member*/
                @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
                public @interface DbF{

                    boolean PrimaryKey() default false;
                    boolean AutoIncrement() default false;
                    boolean NotNull() default true;

                    /**column type*/
                    typ Type() default typ.memberTyp;

                    public enum typ{memberTyp,varchar,integer,decimal,date,timestamp,text,blob};//set

                    int TypeSz() default 255;
                    int TypeSz2() default -1;

                    /**format of each str: <str:name><optional(if not ommited then must be preceeded by a space):int:order of column in the key><optional(must be preceeded by space)str=unique */
                    String[]IndexKey() default {""};

	/* *Foreign key: mustbe name of a java class (extends Tbl) ,the db-col type will be the same as the pk of the referred-tbl ; dream:(a uuid will be used in the db-col)* /
	instead of a anno attrib, instead, the member has to be of a type that is a sub-class of Tbl
	//String dbFK() default "";*/
                }//@interface DbF


                /** to execute a sql create-table statement if needed when the table doesnt exist in the db or if the tbl has a different structure the the class*/
                public static void create(Class<? extends Tbl>tbl){}

                public static void create(String name,CI[]a){}

                public static StringBuilder generateCol(StringBuilder b,CI c){

                    return b;}

                public static StringBuilder generateColType(StringBuilder b,CI c){
 /*
	decimal
	integer
	varchar
	text
	date
	timestamp
	set
	auto_increment
	uuid
 */Field f=c.f();Class z=f.getType();
                    return b;}

                StringBuilder getMmbrType(StringBuilder b,Class z){
                    if( Integer.class.isAssignableFrom(z) || Long.class.isAssignableFrom(z) );else
                    if( Float.class.isAssignableFrom(z) || Double.class.isAssignableFrom(z) );else
                    if( String.class.isAssignableFrom(z) );
                    return b;}

// LinkedList<String>listKeys(Class<? extends Tbl>tbl){return null;}
// LinkedList<CI>listColsOfKey(Class<? extends Tbl>tbl,String key){return null;}

            }//class Creator

        }//class Tbl

    }//class DB

    public static void out(Object...p){try{if(p!=null&&p.length>0)Out(tl().h.getOut(),p);}catch(Exception ex){ex.printStackTrace();}}//throws IOException

    public static void out(PrintWriter out,Object...p)throws IOException{Out(out,p);}

    public static void Out(PrintWriter out,Object[]p)throws IOException{if(p!=null&&p.length>0){for(Object o:p)out.write(String.valueOf(o));}}

    public abstract static class Form{
//public final String name;
//public abstract Fld[]fields();
//public abstract Sql tbl();

        //public Form(String name){this.name=name;}
        @Override public String toString(){return toJson();}
        public abstract String getName();
        public String toJson(){Json.Output o= new Json.Output();
            try {o.oForm(this, "", "").toString();}
            catch (IOException ex) {}return o.toString();}

        public Writer toJson(Writer w){
            Json.Output o= new Json.Output(w);
            try {o.oForm(this, "", "").toString();}
            catch (IOException ex) {}return w;}

        public String[]prmsReq(String prefix){return prmsReq(prefix,fields());}

        public static String[]prmsReq (String prefix,Field[]a){
            String[]r=new String[a.length];int i=-1;
            for(Object e:a)r[++i]=tl().h.req(prefix+e);
            return r;}

        public static Object parse(String p,Class c){
            if(c.isEnum()){
                for(Object i:c.getEnumConstants())
                    if(i!=null&&i.toString().equals(p))
                        return i;}else
            if(c.isAssignableFrom(Float.class))return Float.parseFloat(p);else
            if(c.isAssignableFrom(Double.class))return Double.parseDouble(p);else
            if(c.isAssignableFrom(Integer.class))return Integer.parseInt(p);else
            if(c.isAssignableFrom(URL.class))try {return new URL("file:" +TL.tl().h.getServletContext().getContextPath()+'/'+p);}
            catch (Exception ex) {tl().error("TL.Form.parse:URL:p="+p+" ,c="+c,ex);}else
            if(c.isAssignableFrom(String.class))return p;else
            if(c.isAssignableFrom(Date.class))try {return Util.parseDate(p);}//Util.dateFormat.parse(p);}
            catch (Exception ex) {tl().error("TL.Form.parse:Date:p="+p+" ,c="+c,ex);}
            return null;}

        public Form readReq(String prefix){
            FI[]a=flds();TL tl=tl();TL.H h=tl.h;for(FI f:a){
                String s=h.req(prefix+f);
                Class c=s==null?null:f.f().getType();
                Object v=null;try {
                    if(s!=null)v=parse(s,c);
                    v(f,v);//f.set(this, v);
                }catch (Exception ex) {// IllegalArgumentException,IllegalAccessException
                    tl().error("TL.Form.readReq:t="+this+" ,field="
                            +f+" ,c="+c+" ,s="+s+" ,v="+v,ex);}}
            return this;}

        public abstract FI[]flds();

        public Object[]vals(){
            Field[]a=fields();
            Object[]r=new Object[a.length];
            int i=-1;
            for(Field f:a){i++;
                r[i]=v(a[i]);
            }return r;}

        public Form vals (Object[]p){
            Field[]a=fields();int i=-1;
            for(Field f:a)
                v(f,p[++i]);
            return this;}

        public Field[]fields(){return fields(getClass());}

        public static Field[]fields(Class c){
            Field[]a=c.getDeclaredFields();
            List<Field>l=new LinkedList<Field>();
            for(Field f:a){F i=f.getAnnotation(F.class);//getDeclaredAnnotation
                if(i!=null)l.add(f);}
            //if(this instanceof Sql){};//<enum>.values() =:= c.getEnumConstants()
            Field[]r=new Field[l.size()];
            l.toArray(r);
            return r;}

        public Form   v(FI p,Object v){return v(p.f(),v);}
        public Object v(FI p){return v(p.f());}

        public Form v(Field p,Object v){
            try{Class t=p.getType();
                //boolean b=v!=null&&p.isEnumConstant();
                if(v!=null && !t.isAssignableFrom( v.getClass() ))//t.isEnum()||t.isAssignableFrom(URL.class))
                    v=parse(v instanceof String?(String)v:String.valueOf(v),t);
                p.set(this,v);
            }catch (Exception ex) {tl().error("TL.Form.v("+this+","+p+","+v+")",ex);}
            return this;}

        public Object v(Field p){
            try{return p.get(this);}
            catch (Exception ex) {//IllegalArgumentException,IllegalAccessException
                tl().error("TL.Form.v("+this+","+p+")",ex);return null;}}


        /**Field annotation to designate a java member for use in a Html-Form-field/parameter*/
        @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
        public @interface F{	boolean prmPw() default false; }


        /**Interface for enum-items from different forms and sql-tables ,
         * the enum items represent a reference Column Fields for identifing the column and selection.*/
        public interface FI{//<T>
            public String text();
            //public String tblNm();
            public Class<? extends Form>clss();
            public Field f();
            public Object value();//<T>
            public Object value(Object p);//<T>
            public Object val(Form f);
            public Object val(Form f,Object p);
        }//interface I

/*public static Form form(String varName){
 Object o=TL.s(varName);
 if(o==null){Form r=new Form(varName);return r;}
  return null;}

 public static Form form(Class<? extends App.Form>p){
	final String T="App.Sql.forms";Object o=TL.s(T);
	Map<Class<? extends App.Sql>,App.Sql>tbls=o instanceof Map?(Map)o:null;
	if(tbls==null)TL.s(T,tbls=new HashMap<Class<? extends App.Sql>,App.Sql>());
	App.Sql r=tbls.get(p);if(r==null)try {tbls.put(p, r=p.newInstance());}
	//catch (InstantiationException ex) {}catch(IllegalAccessException ex){}
	catch(Exception ex){TL.error("TL.form(Class<App.Sql>"+p+"):Exception:", ex);}
	return r;}*/

    }//public abstract static class Form

}//class TL