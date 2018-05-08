package dev201801;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.net.URL;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

/**
 * Created by Vaio-PC on 3/29/2018.
 */
public class TxtSrvlt extends HttpServlet {

public static class Txt extends Sql.Tbl {//<Integer>
	public static final String dbtName = "Txt";
	@F public int id=-1,parent,/*code from enum P*/ perm;
	@F public String key, txt,owner,group;
	@F public Map meta;
	@F public Date logTime;

	public enum C implements Sql.Tbl.CI {
		id,parent,perm,key, txt, owner,group,meta,logTime;
		@Override public Field f() {return Co.f(name(), Txt.class);}
		@Override public String getName() {return name();}
		@Override public Class getType() {return f().getType();}
	}//enum C

	public enum P {non(0),read(1),call(3),permRead(7)
		,readSub(15),write(31),createSub(63)
		,permWrite(127),delete(255),eval(511)
		,a(0),b(0),c(0),d(0),e(0),f(0);
		P(int p){bits=p;}public int bits;
		public enum Act{read,call,permRead,readSub
			,write,createSub,permWrite,delete,eval}
	}//enum P

	@Override public String getName() { return dbtName; }
	@Override public C[] columns() { return C.values(); }
	@Override public Object[]wherePK(){
		Object[]a={C.id,id};return a;}//,C.key,key

	public Txt() {this(null);}
	public Txt(String key) {this(0,0,key);}
	public Txt(String key, String txt) {this(0,0,key);this.txt = txt;}

	public Txt(String key, String txt,String ownr, String grp,Map m,Date lt,int prm) {
		this(0,0,key,lt);this.txt = txt;owner=ownr;group=grp;perm=prm;meta=m;}

	public Txt(int id,int parent,String key) {
		this(id,parent,key,null);}

	public Txt(int id,int parent,String key,Date lt) {
		this.id=id;this.parent=parent;this.key=key;logTime=lt==null?new Date():lt;}

	/**store this entity in the dbt, gets max id +1 if id is less than 1 , and then logs to table `L` */
	@Override public Sql.Tbl create() throws Exception{
		if(id<1)try {id=maxPlus1( C.id,dbtName );} catch ( Exception e ) {
			e.printStackTrace();
		}L.l(this);
		return super.create();
		/*public static Txt create(int parent,String key,TL tl){
		return create(parent,key,tl,null,null,0);}

	public static synchronized Txt create(int parent,String key,TL tl,String txt,Map m,int perm){
		String usr=tl.usrUn();int pk=-1;
		try {pk=maxPlus1( C.id,dbtName );
		Txt x=new Txt(pk,parent,key,tl.now);
		x.owner=usr;x.group=tl.usrGroup();
		x.txt=txt;x.meta=m;
		x.perm=perm;
		x.create();
		return x;
		} catch ( Exception e ) {
			e.printStackTrace();
		}return null;}*/
	}//save

	Object jt(){try {
		Object j = Json.Prsr.parse(txt);
		return j;
	}catch(Exception ex){
		ex.printStackTrace();
	}return null;}

	static final String TblPk[]={"int(10)"," Primary Key"," NOT NULL"," auto_increment"};
	static final List TblColDefinition;

	static {TblColDefinition= Util.lst(
		TblPk[0]+ TblPk[1]+ TblPk[2]+ TblPk[3]//id
		, "int(10) NOT NULL DEFAULT 0 "//parent
		, "int(10) NOT NULL DEFAULT 47 "//perm
		,"varchar(255) NOT NULL "//key
		, "text NOT NULL "//txt
		, "varchar(127) NOT NULL DEFAULT 'home' "//owner
		, "varchar(127)NOT NULL DEFAULT 'home' "//group
		,"text"//meta
		, "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"//logTime
	);if(! registered.contains(Txt.class))
		registered.add(Txt.class);}
	@Override public List creationDBTIndices(TL tl) {
		return Util.lst(TblColDefinition
			, Util.lst("unique(`"+C.parent+"`,`"+C.key+"`)"
				,Util.lst(C.group,C.owner,C.id)
				,Util.lst(C.parent,C.id)
				,Util.lst(C.logTime))
			,Util.lst(
				Util.lst(1,0,0xff,"users","{}","moh","super",null,tl.now),
				Util.lst(2,0,255,"apps","{}","moh","super",null,tl.now),
				Util.lst(3,1,255,"moh","{pw:'"+Util.md5("m")+"'}","moh","super",null,tl.now)
			)
			,L.class
		);}

	public static class L extends Sql.Tbl{//<Integer>
		public static final String dbtName="TxtLog";
		@F public int id,parent, perm;//bit pattern:world+group+owner * dsxwr
		@F public String key, txt,owner,group;
		@F public Map meta;
		@F public Date logTime;

		public enum C implements Sql.Tbl.CI {
			id,parent,perm,key, txt, owner,group,meta,logTime;
			@Override public Field f() {return Co.f(name(), L.class);}
			@Override public String getName() {return name();}
			@Override public Class getType() {return f().getType();}
		}//enum C

		@Override public String getName(){return dbtName;}
		@Override public C[]columns(){return C.values();}
		@Override public Object[]wherePK(){Object[]a={C.id,id,C.logTime,logTime};return a;}

		public L(){logTime=new Date() ;}
		public L(String key){this.key=key;logTime=new Date() ;}
		public L(int id,int prnt,int prm,String key,String txt, String ownr,String grp,Map m,Date lt) {
			this.id=id;parent=prnt;perm=prm;this.key = key;this.txt = txt;owner=ownr;group=grp;perm=prm;logTime=lt;meta=m;}

		public static void l(int id,int prnt,int perm,String key,String v,String owner,String group,Map m){
			L l=new L(id,prnt,perm,key,v,owner,group,m,new Date());try{l.save();}catch(Exception x){}}
		public static void l(Txt x){l(x.id,x.parent,x.perm,x.key,x.txt,x.owner,x.group,x.meta);}

		@Override public List creationDBTIndices(TL tl){
			List l=new LinkedList();l.addAll(TblColDefinition);
			l.set(0, TblPk[0]+ TblPk[2]);
			return Util.lst(l
				,Util.lst("unique(`"+C.id+"`,`"+C.logTime+"`)"
					,Util.lst(C.group,C.owner,C.logTime)
					,Util.lst(C.parent,C.id,C.logTime)
					,Util.lst(C.logTime,C.id,C.key)) );}

		@Override public Json.Output jsonOutput(Json.Output o
			,String ind,String path,boolean closeBrace)throws java.io.IOException{
			String i2=ind+'\t';//id parent perm key txt meta owner group
			try
			{o.w("{id:").p(id).w(",parent:").p(parent)
				 .w(",perm:").p(perm)
				 .w(",key:").p(key)
				 .w(",owner:").p(owner)
				 .w(",group:").p(group)
				 .w(",logTime:").oDt(logTime,i2)
				 .w(",txt:").p(txt)
				 .w(",meta:").oMap(meta,i2,o.comment?path+'.'+"meta":path);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if(closeBrace){
				if(o.comment)
					o.w("}//Txt.L&cachePath=\"").p(path).w("\"\n").p(ind);
				else o.w('}');}
			return o; }
	}//class Log

	/**works in 3 approaches based on the param-key string-FORMAT
	 * 1- if param-key is a integer-format then load the int pk
	 * 2- if param-key has a bar-char
	 *	  , then before the bar would be parent int-id
	 *		  and saved in the session as attrib Txt.parent
	 *	  , and after the bar is the member-key
	 * 3- otherwise, parent is read from the session
	 *	  , and member-key is set from param-key
	 *
	 * Caution&Warning: parent is IMPLICIT
	 * */
	public static Txt loadBy(String key) {
		if(key!=null&&key.startsWith(UrlPrefix))
			key=key.substring(UrlPrefix.length());
		if(key!=null && key.contains("/")){
			TL tl=TL.tl();
			Map m=(Map)tl.h.r("urlParts");
			Object o=m==null?null:m.get("url");
			String ux=o==null?null:o instanceof String?(String)o:null;
			o=m==null?null:m.get("x");
			Txt x=o instanceof Txt?(Txt)o:null;
			if(m==null||x==null||!key.equals(ux)){
				tl.h.r("urlParts",m=parseCreate(key));
				x=(Txt)m.get("x");
				return x;
			}else if(x!=null)
				return x;//else
		}
		return Util.isNum( key )
			       ? loadBy( Util.parseInt( key,0 ) )
			       : loadBy( key, 0 );
		/*int j=key==null?-1:key.indexOf( '/' ),j1=j==-1?j:key.indexOf( '/' ),p=0;
		if(j!=-1&&(j<j1||j1==-1)){
		TL.tl().h.s("Txt.parent",p=Util.parseInt( key.substring( 0,j ),0 ) );
		key=key.substring( j+1,j1==-1?key.length():j1 ); }
		else p=( Integer ) TL.tl().h.var("Txt.parent",0 );
		return loadBy( key, p );

		if(key!=null && key.indexOf('/')!=-1) {
			String[] a = key.split("/");
			Txt x = null;
			int n = a == null ? 0 : a.length, i = 0;
			tl.h.r("urlParts",m=Util.mapCreate("a",a,"n",n,"i",i));
			if(n > 0 )
			{if( Util.isNum(a[0]))
				x = loadBy(Util.parseInt(a[i++], 0));
			 else
				x=loadBy(a[i++],0);}
			for( ; x!=null&&i < n ; i++){
				Txt t=loadBy(a[i],x.id);
				if(t!=null)
					x=t;else
				{Util.mapSet(m,"x",x,"i",i);
					return x;}
			}Util.mapSet(m,"x",x,"i",i);
			return x;}*/
	}

	public static Txt loadBy(String key,int parent) {
		Txt j = (Txt) loadWhere(Txt.class, where(C.key, key,C.parent,parent));
		return j;}
	public static Txt loadBy(int id) {
		Txt j = (Txt) loadWhere(Txt.class, where(C.id, id));
		return j;}

	public static Txt prmLoadByUrl(TL tl, String url) {
		Txt j = loadBy(url);
		return j;}

	//@HttpMethod public static List listKeys() throws Exception {List l = Sql.L(sql(cols(C.id,C.key,C.logTime,C.owner,C.group,C.perm),null,dbtName),null);return l;}//TODO: check permission

	@HttpMethod public static List
	poll(@HttpMethod(prmBody = true) long logTime, TL tl) throws Exception {//TODO: check permission
		Txt j = new Txt();
		List l = Util.lst();
		for(Object x : j.query(where(Util.lst(Co.gt, C.logTime), logTime)))
			l.add(j.clone());
		return l;}

	/**
	 * output to the client , Txt-Prm is loaded based on a key from the value of the url
	 * if the Txt-Prm is null then null is returned
	 * if the Txt-Prm txt-field is not a json-array/list
	 * , then the Txt-Prm txt-field is outputted, and return execution.
	 * if the Txt-Prm txt-field is a json-array/list L , then:
	 * the output is in the same order as the list-elements
	 * the list elements can be one of two
	 * 1- element is not a map, outputted as is
	 * 2- json-object m, and the object m can have three cases
	 * 2.2-having property ref and call
	 * 2.1-having property ref
	 * 2.3-having property eval , and optionally having ref
	 * 2.4-not having ref nor eval, in such a case the json-object m / list-element will be outputted
	 */
	@HttpMethod(useClassName = false,usrLoginNeeded = false)public static Txt
	get(@HttpMethod(prmLoadByUrl = true) Txt prm, TL tl) throws Exception {
		if(prm == null) return prm;
		Object js = prm.jt();
		if(js instanceof Map) {
			Map m = (Map) js;
			js = m.get("clientOutput");}
		if(js instanceof List) {
			List l = (List) js;
			for(Object element : l) {
				boolean b = false;
				if(element instanceof Map) {
					Map m = (Map) element;
					Object ref = m.get("ref");
					if(ref != null) {
						js = m.get("call");
						if(b = js != null)
							tl.o(call((String) js, (String) ref, (List) m.get("args"), tl));
					}
					if(! b) {
						js = m.get("eval");
						if(b = js != null)
							tl.o(eval((String) ref, (String) js, tl));
					}
					if(! b) {
						Txt x = loadBy((String) ref);
						if(b = x != null)
							tl.o(x.txt);
					}
				}
				if(! b)
					tl.o(element);
			}
		} else
			tl.o(prm.txt);
		tl.h.r("responseDone", true);
		return prm; }

	@HttpMethod public static List<Txt>
	getKeys(@HttpMethod(prmLoadByUrl= true) Txt j,
			@HttpMethod(prmBody = true) List<String> keys) {//TODO: check permission
		List<Txt> l = new LinkedList<>();
		if(j!=null)l.add(j);
		if(keys!=null)for(String k:keys)l.add(loadBy(k));
		return l;}

	@HttpMethod public static Map
	load(@HttpMethod(prmLoadByUrl= true) Txt p,TL tl) throws SQLException {//TODO: check permission
		Map m=new HashMap();List l=new LinkedList();Txt j=p.id==0?p:loadBy(p.parent);
		m.put("txt",p);
		m.put("path",l);
		if(p!=j && j!=null && j.id!=0)
			l.add(Util.lst(j.id,j.key));
		while(j!=null&& j.id!=0){
			j.id=j.parent;j.load();//j=loadBy(j.parent);
			l.add(Util.lst(j.id,j.key));}
	//public Txt child(String key){ return loadBy( key,id );}//m.put("children",l=new LinkedList());for(Sql.Tbl t:p.query(where(C.parent,p.id)))l.add(Util.lst(p.id,p.key));
		Object[]where=where( C.parent,p.id );
		m.put("children", Sql.L(sql( cols(C.id,C.key ),where,dbtName) ,where));
		return m;}

	//@Override public Sql.Tbl update(CI...p) throws Exception {return update(p);}

	@Override public Sql.Tbl update(CI[]p) throws Exception {
		L.l(this);return super.update(p);}

	@Override public int delete() throws SQLException {
		L.l(this);return super.delete();}

	@HttpMethod public static Txt
	update( @HttpMethod(prmLoadByUrl = true) Txt p
		,@HttpMethod(prmBody = true) Map m, TL tl) throws Exception {
		boolean b=false;if(tl.usr!=null)//TODO: check permission
		{	for ( Object o:m.keySet() ){
				Object v=m.get( o );
				C c=C.valueOf( o.toString() );
				if(v!=null&&c!=null)
				{p.v(c,v);b=true;}
			}if(b)
				p.save();
		}return p;}

	static Map parseCreate(String url){
		Map m=null;
		if(url!=null && url.indexOf('/')!=-1) {
			String[] a = url.split("/");
			Txt x = null;
			int n = a == null ? 0 : a.length, i = 0;//always element at i is not processed
			m = Util.mapCreate("a", a, "n", n, "i", i,"url",url);
			if(n > 0) {
				if(Util.isNum(a[0]))
					x = loadBy(Util.parseInt(a[i++], 0));
				else
					x = loadBy(a[i++], 0);}
			for( ; x != null && i < n ; i++) {
				Txt t = loadBy(a[i], x.id);
				if(t != null)
					x = t;
				else {
					Util.mapSet(m, "x", x, "i", i);
					return m;}}
			Util.mapSet(m, "x", x, "i", i);
		}return m!=null?m:Util.mapCreate("url",url);}

	static Map parseCreate(Map m){
		Object o=m==null?null:m.get("url");
		String url=o instanceof String?(String)o:null;
		o=m==null?null:m.get("a");
		//if(o==null)m.put("a",);
		String[]a=o instanceof String[]?(String[])o:null;
		o=m==null?null:m.get("x");
		Txt x=o instanceof Txt?(Txt)o:null;
		o=m.get("i");
		int n = a == null ? 0 : a.length
			, i = o instanceof Integer?(Integer)o:0;//always element at i is not processed
		if(x==null && n>0) {
			if(Util.isNum(a[0]))
				x = loadBy(Util.parseInt(a[i++], 0));
			else
				x = loadBy(a[i++], 0);
			for( ; x != null && i < n ; i++) {
				Txt t = loadBy(a[i], x.id);
				if(t != null)
					x = t;
				else {
					Util.mapSet(m, "x", x, "i", i);
					return m;}}
			Util.mapSet(m, "x", x, "i", i);
		}return m;}

	@HttpMethod public static Txt
	create(@HttpMethod(prmLoadByUrl = true) Txt prnt
		,  @HttpMethod(prmUrlPart = true) String key
		,  @HttpMethod(prmBody = true) Txt x
		, TL tl) throws Exception {
		if(tl.usr!=null)//TODO: check permission
		{	String k=tl.h.req.getRequestURI().substring(UrlPrefix.length()).trim();
			if((k==null||k.trim().length()<1)&&x.key!=null&&x.key.length()>0)
				k=x.key.trim();
			if(k!=null&&k.length()>0)
			{Txt y=loadBy(k,x.parent);
				if(y!=null)
					return null;
				x.key=k;}
			x.create();}
		return x;}

	@HttpMethod public static Txt
	txt( @HttpMethod(prmLoadByUrl= true) Txt x
		,@HttpMethod(prmBody = true) String v, TL tl) throws Exception {
		if(tl.usr!=null)//TODO: check permission
		{x.txt=v;x.save();}
		return x;}

	@HttpMethod public static Txt
	meta( @HttpMethod(prmLoadByUrl= true) Txt x
		,@HttpMethod(prmBody = true) Map m, TL tl) throws Exception {
		if(tl.usr!=null)//TODO: check permission
		{x.meta=m;x.save();}
		return x;}

	@HttpMethod public static Txt
	delete(@HttpMethod(prmLoadByUrl = true) Txt x, TL tl) throws Exception {
		x.delete();//TODO: check permission
		return x;}

	static javax.script.ScriptEngine eng(String key, boolean createIfNotInit, TL tl) {
		String en = "ScriptEngine.JavaScript." + key;
		javax.script.ScriptEngine e = (javax.script.ScriptEngine) tl.h.s(en);
		if(e == null && createIfNotInit) {
			javax.script.ScriptEngineManager man = (javax.script.ScriptEngineManager) tl.h.a("ScriptEngineManager");
			if(man == null)
				tl.h.a("ScriptEngineManager", man = new javax.script.ScriptEngineManager());
			tl.h.s(en, e = man.getEngineByName("JavaScript"));
			Txt j = loadBy(key);
			Map jm = Util.mapCreate();
			e.put("key", key);
			e.put("txt", j.txt);
		}
		e.put("tl", tl);
		return e;}

	@HttpMethod public static Object
	call(@HttpMethod(prmUrlPart = true) String m,
		@HttpMethod(prmUrlRemaining = true) String key,
		@HttpMethod(prmBody = true) List args,
		TL tl) throws Exception {//TODO: check permission
		javax.script.ScriptEngine e = eng(key, true, tl);
		e.put("m", m);
		if(args != null) e.put("args", args);
		return e.eval(key + "[m]" + (args == null ? "" : ".call(args)"));}

	@HttpMethod	public static Object
	eval(@HttpMethod(prmUrlRemaining = true) String key,
	     @HttpMethod(prmBody = true) String src,
	     TL tl) throws Exception {// javax.script.ScriptException //TODO: check permission
		Txt j = loadBy(key);
		javax.script.ScriptEngine e = eng(key, true, tl);
		e.put("src", src);
		return e.eval(src);
	}//eval

	@HttpMethod	public static Object
	prop(@HttpMethod(prmLoadByUrl= true) Txt x,
	     @HttpMethod(prmName= "prop") String prop,
		 @HttpMethod(prmName= "val") Object val,
	     TL tl) throws Exception {// javax.script.ScriptException //TODO: check permission
		if(x==null||prop==null||prop.length()==0)return false;
		if(x.meta==null)x.meta=new HashMap();
		String[]a=prop.split(".");
		String mmbr=a[0];
		Object o=x.meta,u=null;
		for(int i=0;i<a.length-1;i++){
			if(o instanceof Map)
				u=((Map)o).get(mmbr);
				if(u==null)
					((Map)o).put(mmbr,u=new HashMap<>());
			else if(o instanceof List )
			{   int j=Util.parseInt(mmbr,-1);
				u=((List)o).get(j);
				if(u==null)
					((List)o).set(j,u=new HashMap<>());
			}o=u;
			mmbr=a[i];
		}
		if(o!=null) {
			if(o instanceof Map)
				((Map)o).put(mmbr,val);
			else if(o instanceof List )
			{   int j=Util.parseInt(mmbr,-1);
				((List)o).set(j,val);
			}
			return x.update(cols(C.meta));
		}return false;
	}//eval

	//each command is a method call where the list-item is a map which should have
//	method:<str> , url:<str> , body:<map or str or list>
	@HttpMethod static public List
	cmnds(@HttpMethod(prmBody = true) List p,TL tl){
		List l=new LinkedList();Object r=null;
		for(Object o:p)try{r=o;
			if(o instanceof Map){
				Map m=(Map)o;
				String mt=(String)m.get("method")
					,url=(String)m.get("url");
				String[]au=url.substring(UrlPrefix.length()).split("/");//b=m.get("body");
				//if("listKeys".equals(mt)) r=listKeys();else
				if("poll".equals(mt))
					r=poll(Util.parseDate(tl.bodyData.toString()).getTime(),tl);
				else if("get".equals(mt))
					r=get(loadBy(url),tl);
				else if("getKeys".equals(mt))
					r=getKeys(loadBy(url),(List)m.get("body"));
				else if("load".equals(mt))
					r=load(loadBy(url),tl);
				else if("update".equals(mt))
					r=update(loadBy(url),(Map)m.get("body"),tl);
				else if("create".equals(mt))
				{Map m2=parseCreate(url);
					Txt x=(Txt)m.get("x");
					Object o2=m.get("i");
					int i=o2 instanceof Integer?(Integer)o2:-1,n=-1;
					o2=m.get("n");if(o2 instanceof Integer)
						n= (Integer)o2;
					o2=m.get("a");
					String[]a=o2 instanceof String[]?(String[])o2:null;
					String nm=a!=null&&++i<n?a[i]:null;
					r=nm==null?null:create(x,nm,(Txt)new Txt().fromMap((Map)m.get("body")),tl);
				}
				else if("txt".equals(mt))
					r=txt(loadBy(url),(String)m.get("body"),tl);
				else if("meta".equals(mt))
					r=meta(loadBy(url),(Map)m.get("body"),tl);
				else if("delete".equals(mt))
					r=delete(loadBy(url),tl);
				else if("call".equals(mt))
					r=call(au[0],au[1],(List)m.get("body"),tl);
				else if("eval".equals(mt))
					r=eval(url,(String)m.get("body"),tl);
				else if("prop".equals(mt))
					r=prop(loadBy(url),(String)m.get("prop"),m.get("val"),tl);
				m.put("return",r);
				r=m;
			}
		}catch(Exception ex){r=ex;}
		finally {
			l.add(r);
		}
		return l;}

}//Txt

/**Meta-Table*/
public static class MTbl extends Sql.Tbl {
	public static Map</**database-name*/String,Map</**table-name*/String,MTbl>>dbs=new HashMap<String,Map<String,MTbl>>();

	public static Object prmLoadByUrl(TL tl,String url){
		if(url!=null&&url.startsWith(UrlPrefix))
			url=url.substring(UrlPrefix.length());
		String[]a=url.split("/");
		Object o= initWithUrl(a);
		if(o instanceof Sql.Tbl)
			((Sql.Tbl)o).load();
		return o;}

		/**the names are written in 3 methods:DBTbl_create,fromMap,initFromDb*/
	public String dbName,dbtName="MTbl",comment;
	@Override public String getName(){return dbtName;}
	@Override public Object[]wherePK(){
		Object[]a=new Object[pkc.length*2];
		for(int i=0;i<pkc.length;i++)
			a[i*2]=pkc[i];
		return a;}

/*	@Override public int pkcn(){return pkc==null?0:pkc.length;}
	@Override public CI pkc(int i){return pkc==null||i>=pkc.length?null:pkc[i];}
	@Override public CI[]pkcols(){return pkc;}
	@Override public Integer pkv(int i){return null;}//pkc!=null&&pkc.length>0?pkc[0]:null;
	@Override public Integer[]pkvals(){return null;}
	@Override public Integer[]pkv(Integer[]v){return null;}*/

	/**indicies of the columns which are the primary key in cols,
	 * if the table has compound keys(more than one column as a primary key),
	 * then this array is of length greater than one,
	 * if the the primary key is only one column then this array is of length one
	 * */C[]pkc;
	C[]cols;
	I[]indices;/**forgein keys*/Map<String,Fk>fk;
	/*
	//need a way to capture indicies and the columns of each index, e.g. unique ,btree, ect
		List<Map<String,Map<String,String>>>
		=>
		<index-type><index-name><index-columns>
		//need a way to capture info about columns for table creation
		boolean notNull;
		int length;//when applicable
		String Default;//when applicable
		boolean autoIcrement;//when applicable
		onupdate,ondelete,forgein-key,primary-key
	*/
	C col(String colName){
		for ( C c:cols )if(c.name!=null && c.name.equals( colName ))return c;
		return null;}

	public static class Fk{String col,fTbl,fCol;public Fk(String c,String t,String f){col=c;fTbl=t;fCol=f;}}
	public static class C implements CI,Json.Output.JsonOutput{
		String name,type,comment,creation;
		Class clss;int i;boolean notNull;

		public C(String name,String type,String comment,int i){
			this.name=name;this.type=type;this.comment=comment;this.i=i;
			clss= Typ.t( type ).c;}

		public C(String name,String type,int i,String comment,String creation){
			this.name=name;this.type=type;this.i=i;
			this.comment=comment;this.creation=creation;
			clss= Typ.t( type ).c;}

		@Override public Field f(){return null;}
		@Override public String getName(){return name;}
		@Override public Class getType(){return clss;}
		@Override public String toString() {
			try {TL tl = TL.tl();
				return tl.jo().clrSW().o( this ).toStrin_();
			} catch ( Exception ex ) {}
			return "{\"name\":\""+name+"\",\"type\":\""+type+"\",\"i\":"+i+",\"comment\":\""+comment+"\",\"creation\":\""+creation+"\"}";
		}
		public Map asMap(){return Util.mapCreate( "name",name,"clss",type,"comment",comment,"i",i,"creation",creation );}
		@Override public Json.Output jsonOutput( Json.Output o, String ind, String path ) throws IOException {
			return jsonOutput( o,ind,path,true ); }

		public Json.Output jsonOutput( Json.Output o
		, String ind, String path,boolean closeBrace ) throws IOException {
			o.w( "{\"name\":" ).oStr( name, "" )
				.w( ",\"type\":" ).oStr( type,"" )
				.w( ",\"i\":" ).p( i );
			if(comment!=null)
				o.w( ",\"comment\":" ).oStr( comment,"" );
			if(creation!=null)
				o.w( ",\"creation\":" ).oStr( creation,"" );
			return closeBrace?o.w( '}' ):o; }
	}//C

	/**to capture info about db-table-indices*/public static class I extends C{
		C[]cols;List<Object[]>a;
		enum X{TABLE_CAT,TABLE_SCHEM,TABLE_NAME
			,NON_UNIQUE(Boolean.class)
			,INDEX_QUALIFIER,INDEX_NAME
			,TYPE(Short.class)
			,ORDINAL_POSITION(Short.class)
			,COLUMN_NAME
			,ASC_OR_DESC //"A" or "D"
			,CARDINALITY(Integer.class)
			,PAGES(Integer.class)
			,FILTER_CONDITION;
		Class clss;X(){clss=String.class;}X(Class c){clss=c;}
		};
		static final X[]x=X.values();
		//public I(C[]cols,List<Object[]>a,String...p){super(p[0],p[1],0,p[2],p[3]);this.cols=cols;this.a=a;}
		public I(String name,int i){
			super(name,null,i,null,null);a=new LinkedList<>();}

		@Override public String toString() {
			try {TL tl = TL.tl();
				return tl.jo().clrSW().o( this ).toStrin_();
			} catch ( Exception ex ) {}
			return "{\"name\":\""+name+"\",\"type\":\""+type
					   +"\",\"i\":"+i+",\"comment\":\""+comment
					   +"\",\"creation\":\""+creation+"\",\"cols\":\""+cols+"\"}";}

		public Map asMap(){return Util.mapCreate( "name",name
			,"type",type,"comment",comment,"i",i,"creation",creation,"cols",cols );}

		@Override public Json.Output jsonOutput( Json.Output o
		, String ind, String path ) throws IOException {
			jsonOutput( o,ind,path,false );
			o.w( ",\"cols\":[");boolean comma=false;
			for ( C c:cols ){if(comma)o.w( ',' );else comma=true;
				o.w( '`' ).w( c.name==null?"null":
				c.name.replaceAll( "\n","_newline_" )
				.replaceAll( "`","_backTic_" )).w( '`' );}
			return o.w( "]}" );}
	}//class I

	@Override public C[]columns(){return cols;}

	@Override public Json.Output jsonOutput( Json.Output o, String ind, String path ) throws IOException {
		o.w( "{\"dbName\":").oStr( dbName ,ind).w( ",\"dbtName\":").oStr( dbtName ,ind).w( ",\"cols\":{");
		for(C c:cols)c.jsonOutput( (c.i==0?o:o.w( ',' )).oStr( c.name,"" ).w( ':' ),ind,path );
		o.w( "},\"pk\":[" );boolean comma=false;
		for ( C c:pkc ){if(comma)o.w( ',' );else comma=true;
			o.oStr( c.name,"" );}
		o.w( "],\"indices\":[" );comma=false;
		if(indices!=null)for ( I c:indices ){if(comma)o.w( ',' );else comma=true;
			c.jsonOutput( o,ind,path );}
		return o.w( "]}" ); }

	public static List asListOfMaps( C[]a){List l=Util.lst(  );for ( C c:a )l.add( c.asMap() );return l; }
	public static List asListOfNames(C[]a){List l=Util.lst(  );for ( C c:a )l.add( c.name );return l; }
	//public static Map asMaps(Map<C,Map<String,String>>a){Map m=Util.mapCreate(  );for ( C c:a.keySet() )m.put( c.name,a.get( c ) );return m; }


	@Override public List creationDBTIndices(TL tl){return null;}
	public enum Typ {Array(Types.ARRAY,Object[].class),
		BIGINT				(Types.BIGINT				,Long.class		, "length" ),
		BIT					(Types.BIT					,Boolean.class	, "length" ),
		BINARY				(Types.BINARY				,Boolean.class),
		BLOB				(Types.BLOB					,byte[].class	, "length" ),
		BOOLEAN				(Types.BOOLEAN				,Boolean.class),
		CHAR				(Types.CHAR					,String.class	, "length" ),
		CLOB				(Types.CLOB					,String.class	, "length" ),
		DATALINK			(Types.DATALINK				,String.class),
		DATE				(Types.DATE					,Date.class),
		DECIMAL				(Types.DECIMAL				,Double.class	, "length" ),
		DISTINCT			(Types.DISTINCT				,String.class),
		DOUBLE				(Types.DOUBLE				,Double.class	, "length" ),
		FLOAT				(Types.FLOAT				,Double.class	, "length" ),
		INTEGER				(Types.INTEGER				,Integer.class	, "length" ),
		JAVA_OBJECT			(Types.JAVA_OBJECT			,Object.class),
		LONGNVARCHAR		(Types.LONGNVARCHAR			,String.class	, "length" ),
		LONGVARBINARY		(Types.LONGVARBINARY		,String.class	, "length" ),
		LONGVARCHAR			(Types.LONGVARCHAR			,String.class	, "length" ),
		NCHAR				(Types.NCHAR				,String.class	, "length" ),
		NCLOB				(Types.NCLOB				,String.class	, "length" ),
		NULL				(Types.NULL					,String.class),
		NUMERIC				(Types.NUMERIC				,Double.class	, "length" ),
		NVARCHAR			(Types.NVARCHAR				,String.class	, "length" ),
		OTHER				(Types.OTHER				,String.class),
		REAL				(Types.REAL					,Double.class	, "length" ),
		REF					(Types.REF					,String.class),
		REF_CURSOR			(Types.REF_CURSOR			,String.class),
		ROWID				(Types.ROWID				,String.class),
		SMALLINT			(Types.SMALLINT				,Integer.class	, "length" ),
		SQLXML				(Types.SQLXML				,Map.class),
		STRUCT				(Types.STRUCT				,Map.class),
		TIME				(Types.TIME					,Date.class),
		TIME_WITH_TIMEZONE	(Types.TIME_WITH_TIMEZONE	,Date.class),
		TIMESTAMP			(Types.TIMESTAMP			,Date.class),
		TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE ,Date.class),
		TINYINT				(Types.TINYINT				,Integer.class	, "length" ),
		VARBINARY			(Types.VARBINARY			,String.class	, "length" ),
		VARCHAR				(Types.VARCHAR				,String.class	, "length" )
		;//java.sql.Types t;
		int it;String txt;
		Class c;String[]options;
		public static Map<Integer,Typ>mi;
		public static Map<String,Typ>ms;
		public static Map<Integer,Class>ci;
		public static Map<String,Class>cs;
		//Typ(java.sql.Types t,Class c){this.t=t;this.c=c;}
		Typ(int t, Class c){it=t;this.c=c;}
		Typ(int t, Class c, String...options){it=t;this.c=c;this.options=options;}

		public static Typ t(String p){p=p==null?null:p.toUpperCase();
			if(ms==null)initMaps();
			Typ t=ms==null?null:ms.get(p);
			return t;//p==null?null:ms.get(p.toUpperCase());//String u=p.toUpperCase();if("INT".equals(u)) return INTEGER;for(Typ x:values()){String s=x.name();boolean b=s.equals(u);//equalsIgnoreCase if(b)return x;}return null;
			}
		public static Typ t(int p){
			return mi.get(p);//for(Typ x:values())if(x.it==p)return x;return null;
		}
		public static void initMaps(){
			Typ[]a=values();int n=a.length;
			if(ms==null){ms=new HashMap<String,Typ>(n);//if(mi==null)
				mi=new HashMap<Integer,Typ>(n);//if(cs==null)
				cs=new HashMap<String,Class>(n);//if(ci==null)
				ci=new HashMap<Integer,Class>(n);
				ms.put("INT",INTEGER);
				ms.put("TEXT",LONGVARCHAR);
				for(Typ x:a){
					ms.put( x.name(),x  );mi.put( x.it,x  );
					cs.put( x.name(),x.c);ci.put( x.it,x.c);}}}
	}//enum Typ

	public static Map<String,MTbl>initFromDb(String dbName){
		Map<String,MTbl>tbls=new HashMap<String,MTbl>();//List<MTbl>lt=new LinkedList<>(  );
		List l=new LinkedList<C>(  );
		try {Connection c = Sql.c();
			java.sql.DatabaseMetaData databaseMetaData = c.getMetaData();
			ResultSet result = databaseMetaData.getTables(dbName//schema
				, null, null, null );
			while ( result.next() ) {MTbl m=new MTbl();
				m.dbName=result.getString( "TABLE_CAT" );
				if(m.dbName==null)m.dbName=result.getString( "TABLE_SCHEM" );if(m.dbName==null)m.dbName=dbName;
				m.dbtName=result.getString("TABLE_NAME" );
				m.comment=result.getString("REMARKS" );
				tbls.put( m.dbtName,m);
			}
			result.close();
			for(MTbl m:tbls.values())try
			{	result = databaseMetaData.getColumns(
				null, null, m.dbtName, null );
				while ( result.next() ) {
					C col = new C(result.getString( "COLUMN_NAME" )
						,result.getString( "TYPE_NAME" )
						,result.getString( "REMARKS" )
						,l.size());
					l.add( col );
					// col.columnType = result.getInt(5); //java.sql.Types.
				}
				result.close();
				m.cols = new C[ l.size() ];
				l.toArray( m.cols );l.clear();

				result = databaseMetaData.getPrimaryKeys( null, null, m.dbtName );
				while ( result.next() )
					l.add( m.col( result.getString("COLUMN_NAME" ) ) );
				result.close();
				m.pkc = new C[ l.size() ];
				l.toArray( m.pkc );l.clear();

				result = databaseMetaData.getImportedKeys(null,m.dbName, m.dbtName );
				while ( result.next() ){
					if(m.fk==null)m.fk=new HashMap<String,Fk>(  );
					Fk x=new Fk(
						result.getString( "PKCOLUMN_NAME" )
						,result.getString( "PKTABLE_NAME" )
						,result.getString( "FKCOLUMN_NAME" ) );
					m.fk.put( x.col,x);
				}
				result.close();

				result =databaseMetaData.getIndexInfo(null,m.dbName, m.dbtName,false,false );

				Map</**index-name*/String,I>mi=new HashMap<>();
				Object[]a=null;

				while ( result.next() ){
					a=new Object[I.x.length];
					for(int i=0;i<I.x.length;i++)
						a[i]=I.x[i].clss==Integer.class?
							result.getInt( I.x[i].toString())
							:I.x[i].clss==Short.class?
							result.getShort( I.x[i].toString())
							:I.x[i].clss==Boolean.class?
							result.getBoolean( I.x[i].toString())
							:result.getString( I.x[i].toString())
							;

					I i=mi.get(a[I.X.INDEX_NAME.ordinal()]);
					if(i==null){
						i=new I( (String)a[I.X.INDEX_NAME.ordinal()],(Integer)a[I.X.ORDINAL_POSITION.ordinal()]);//null,new LinkedList<Object[]>(),"","","","");
						mi.put(i.name,i);}
					i.a.set((Integer)a[I.X.ORDINAL_POSITION.ordinal()],a);
				}
				result.close();
				m.indices=new I[mi.size()];int j=0;
				for(I i:mi.values()) {
					m.indices[j++] = i;
					i.cols=new C[i.a.size()];
					for(int k=0;k<i.cols.length;k++)
						i.cols[k]=m.col((String)i.a.get(
							k
							-1
						)[I.X.COLUMN_NAME.ordinal()]);
				}

			}catch ( SQLException e ) {
				TL.tl().error( e,"MTbl[]initFromDb",m ); }
		} catch ( SQLException e ) {
			TL.tl().error( e,"MTbl[]initFromDb" ); }
		dbs.put(dbName,tbls);
		return tbls;}

	public static Map dbAsMap(String dbName){
		Map</**table-name*/String,MTbl>db=dbs.get( dbName );
		Map m=null;
		if(db!=null){
			m=Util.mapCreate( "dbName",dbName );
			for(MTbl i:db.values())
				m.put( i.dbtName,i.asMap(  ) );
		}return m;}

	@Override public Map asMap(){//String dbtName
		Map m,r= Util.mapCreate( "dbtName",dbtName,"dbName",dbtName
			, "cols",asListOfMaps( cols )
			, "pk", asListOfNames( pkc)
			, "fk", m=Util.mapCreate());
		for(Fk f:fk.values())
			m.put(f.col,Util.lst(f.fTbl,f.fCol));
		return r;
	}

	public MTbl colsFromList(List l){
		cols=new C[l.size()];int i=-1;
		for(Object k:l){
			Map x=(Map)k;
			cols[++i]=new C((String)x.get( "name" )
				,(String)x.get( "type" )
				,(String)x.get( "comment" )
				,(int)x.get( "i" ));}
		return this;}

	public MTbl pkcFromList(List l){
		pkc=new C[l.size()];int i=-1;
		for(Object k:l)pkc[++i]=col((String)k);
		return this;}

	public MTbl indsFromList(List l){return this;}
	public MTbl fkcFromList(List l){return this;}

	@Override public Sql.Tbl fromMap(Map m){
		dbName=((String)m.get( "dbName" )).replaceAll( "`"," " ).replaceAll( ";"," " );
		dbtName=((String)m.get( "dbtName" )).replaceAll( "`"," " ).replaceAll( ";"," " );
		colsFromList((List)m.get( "cols" ));

		pkcFromList( (List)m.get( "pk" ));

		/*Map x=(Map)m.get( "fk" );
		fk=new HashMap<>(  );
		for(Object o:x.keySet()){
			C c=col( (String)o );
			//fk.put( c,(Map)x.get( o ) );
		}*/
		return this;}

	StringBuilder dbn(StringBuilder b){
		return b.append('`').append(dbName ).append( "`.`").append(dbtName ).append( "`" );}

	int createTable(){
		StringBuilder b=dbn(new StringBuilder( "create table ")).append( "(" );
		for ( C c:cols ){if(c.i>0)b.append( ',' );
			Typ t= Typ.t( c.type );
			b.append( '`' ).append( c.name ).append( "` " ).append( c.type ).append( ' ' ).append( t.txt );
		}
		int i=-1;b.append( ",primary key(" );
		for ( C c:pkc ){if(++i>0)b.append( ',' );
			b.append( '`' ).append( c.name ).append( "` " ); }
		//for ( C c:fk){}
		b.append( ");" );i=-1;
		try {
			i= Sql.x( b.toString() );
		}catch ( Exception ex ){}
		return i;
	}

	static Object initWithUrl(String[]a){
		int n=a.length;
		Map<String,MTbl> d=n>0?dbs.get(a[0]):null;
		if(d==null||n<2)
			return d;
		MTbl m=d.get(a[1]);
		if(m==null||n<3)
			return m;
		Row r=m.new Row(a);
		return r;}

	public class Row extends Sql.Tbl {
		@F public Object[]vals;
		@Override public String getName(){return dbtName;}
		@Override public Object[]wherePK(){Object[]a=new Object[pkc.length*2];
			int i=-1;for(C c:pkc){
				a[++i*2]=c;a[i*2+1]=vals[c.i];}
			return a;}
		/*		@Override public int pkcn(){return vals==null?0:vals.length;}
				@Override public CI pkc(int i){return pkc==null?null:pkc[i];}
				@Override public CI[] pkcols() {return cols;}
				@Override public Integer pkv(int i){return vals!=null&&vals.length>pkc[0].i
			&&!(vals[pkc[0].i]instanceof Integer)?(Integer ) vals[pkc[0].i]:null;}
				@Override public Object[] pkvals() { return vals;}
				@Override public Object[]pkv(Object[]v){return vals!=null
			&&vals.length>pkc[0].i?(Object[])(vals[pkc[0].i]=v):null;}
		*/
		public Row(){}
		public Row(String[]url){setPk(url);}

		/**set the primay keys*/
		public Row setPk(String[]url){
			int i=2;
			for(C c:pkc)
				v(c,url[i++]);
			return this;}

		@Override public C[]columns(){return cols;}
		@Override public List creationDBTIndices(TL tl){return null;}
		public MTbl tbl(){return MTbl.this;}

		//@Override public Object[]vals(){return vals;}
		@Override public Object[]valsForSql(){return vals;}
		@Override public Sql.Tbl vals (Object[]p){vals=p;return this;}

		/**return indexof param-column */
		public int cix(CI p){
			if(p instanceof C)return ((C)p).i;
			int i=0;for ( C c:cols )if(c.getName().equals(p.getName()))return i;else i++;return -1;}

		/**return indexof param-column */
		public int cix(String p){int i=0;
			for ( C c:cols )
				if(c.name!=null&&c.name.equals( p ))
					return i;else i++;
			return -1;}

		@Override public Sql.Tbl v(CI p, Object v){vals[cix(p)]=v;return this;}
		@Override public Object v(CI p){return vals[cix(p)];}

	}//class Row


	@HttpMethod static public List
	DB_names()throws SQLException{
		return Sql.q1colList( "show databases" );}

	@HttpMethod static public Typ[]
	colTypes(){return Typ.values();}

	@HttpMethod static public /*List<Map<String,Object>>*/Map</**table-name*/String,MTbl>
	DB_get(@HttpMethod(prmUrlPart = true) String dbName){
		Map</**table-name*/String,MTbl>d=initFromDb( dbName );
		dbs.put(dbName,d);
		return d;}

	@HttpMethod static public int
	DB_create(@HttpMethod(prmUrlPart= true) String dbName)throws SQLException{
		int i= Sql.x( "create database `"+dbName.replaceAll( "`"," " )+"`" );
		if(i>0)dbs.put(dbName,new HashMap</**table-name*/String,MTbl>());
		return i;}

	@HttpMethod static public int
	DB_rename(@HttpMethod(prmUrlPart= true) String dbName,@HttpMethod(prmName= "newName") String newName)throws SQLException{
		newName=newName.replaceAll( "`"," " ).replaceAll( ";"," " );
		dbName=dbName.replaceAll( "`"," " ).replaceAll( ";"," " );
		if(dbs.containsKey(newName))
			return -2;
		int i= Sql.x( "rename database `"+ dbName+"` to `"+newName+"`" );
		if(i>0){Map</**table-name*/String,MTbl>m=dbs.get(dbName);
			dbs.remove(dbName);
			dbs.put(newName,m);
			for(MTbl t:m.values())
				t.dbName=newName;}
		return i;}

	@HttpMethod static public int
	DB_drop(@HttpMethod(prmUrlPart= true) String dbName)throws SQLException{
		if(dbs.get(dbName)==null||dbs.get(dbName).size()>0)return -2;
		int i= Sql.x( "drop database `"+dbName.replaceAll( "`"," " )+"`" );
		if(i>0)dbs.remove( dbName );
		return i;}

	@HttpMethod static public MTbl
	DBTbl_create(@HttpMethod(prmUrlPart = true) String dbName
		,@HttpMethod(prmUrlPart=true) String dbtName
		,@HttpMethod(prmName="cols") List cols//<Map<String,String>>
		,@HttpMethod(prmName = "pk") List pk//<String>
		,@HttpMethod(prmName = "indices") List inds//<String>
		,@HttpMethod(prmName = "fk") List fk//<String,Map<String,String>>
		,TL tl){
		MTbl m=new MTbl();
		m.fromMap(tl.json  );
		m.colsFromList(cols);
		m.pkcFromList(pk);
		m.fkcFromList(fk);
		m.indsFromList(inds);
		m.dbName=dbName.replaceAll( "`"," " ).replaceAll( ";"," " );
		m.dbtName=dbtName.replaceAll( "`"," " ).replaceAll( ";"," " );
		m.createTable();
		return m;}

	@HttpMethod static public int
	DBTbl_drop(@HttpMethod(prmUrlPart = true) String dbName
		,@HttpMethod(prmUrlPart = true) String dbtName) throws Exception{
		if(dbs.get(dbName).get(dbtName).count(null)>0)return -2;
		return  Sql.x( "drop table `"+dbName.replaceAll( "`"," " )
			+"`.`"+dbtName.replaceAll( "`"," " )+"`" );}

	@HttpMethod static public int
	DBTbl_rename(@HttpMethod(prmLoadByUrl = true) MTbl t
		,@HttpMethod(prmName = "newName") String newName)throws SQLException{
		int x=  Sql.x( "rename table `"+t.dbName.replaceAll( "`"," " )+"`.`"
		+t.dbtName.replaceAll( "`"," " )+"` to `"
		+newName.replaceAll( "`"," " )+"`" );
		if(x>0){Map m=dbs.get(t.dbName);
		m.remove(t.dbtName);
		m.put(t.dbtName=newName,t);}
		return x;}

	@HttpMethod static public int
	DBTblCol_alter(@HttpMethod(prmUrlPart= true) String dbName
		,@HttpMethod(prmUrlPart= true) String dbtName
		,@HttpMethod(prmUrlPart= true) String col
		,@HttpMethod(prmUrlPart = true) String cm
		,@HttpMethod(prmBody = true) String def//,@HttpMethod(prmBody = true) Map def//<String,String>
		)throws Exception{MTbl t=dbs.get(dbName).get(dbtName);
		if(col==null || cm==null || def==null
			   || cm.indexOf("`")!=-1
			   || cm.indexOf(";")!=-1
			   || col.indexOf("`")!=-1
			//|| col.indexOf(";")!=-1
			//|| def.indexOf("`")!=-1
			|| def.indexOf(";")!=-1
			)return -1;
		return Sql.x("alter table `"+t.dbName+"`.`"+t.dbtName+"` "+cm+" column `"+col+"` "+def);
		/**
		 * map def::=
		 * clss
		 * name
		 * length*
		 * notNull
		 * default
		 * on update
		 * coallation
		 *
		 *
		 *

		 @HttpMethod static public int
		 DBTblCol_drop(@HttpMethod(prmLoadByUrl = true) MTbl t
		 ,@HttpMethod(prmUrlPart= true) String col){
		 return -1;}

		 @HttpMethod static public int
		 DBTblCol_insert(@HttpMethod(prmLoadByUrl = true) MTbl t
		 ,@HttpMethod(prmUrlPart= true) String col
		 ,@HttpMethod(prmName = "beforeCol") String beforeCol
		 ,@HttpMethod(prmName = "def") Map def)//<String,String>
		 {
		 return -1;}

		 @HttpMethod static public int
		 DBTblCol_add(@HttpMethod(prmLoadByUrl = true) MTbl t
		 ,@HttpMethod(prmUrlPart= true) String col
		 ,@HttpMethod(prmBody = true) String def
		 //,@HttpMethod(prmBody = true) Map def//<String,String>
		 )throws Exception{
		 return Sql.x("alter table `"+t.dbName+"`.`"+t.dbtName+"` add column `"+col+"` "+def);}

		 @HttpMethod static public int
		 DBTblCol_rename(@HttpMethod(prmLoadByUrl = true) MTbl t
		 ,@HttpMethod(prmUrlPart= true) String col
		 ,@HttpMethod(prmName = "newName") String newName){
		 Sql.x("rename ");
		 return -1;}

		  * */}

	@HttpMethod static public Object[]
	get(@HttpMethod(prmLoadByUrl = true) MTbl.Row t){
		return t.vals;}

	@HttpMethod static public Sql.Tbl
	insert(@HttpMethod(prmBody = true) MTbl.Row t) throws Exception {
		return t.create();}

	@HttpMethod static public int
	update(@HttpMethod(prmLoadByUrl = true) MTbl t
		,@HttpMethod(prmBody = true) Map set//,@HttpMethod(prmName = "where") List where
	)throws Exception{
		int n=set==null?0:set.size();
		if(n<1)return 0;
		CI[]c=new CI[n];int i=0;
		for(Object o:set.keySet())
			t.v(c[i++]=t.col(o.toString()),set.get(o));
		t.update(c);
		return n;
	}

	@HttpMethod static public int
	delete(@HttpMethod(prmLoadByUrl = true) MTbl.Row t//,@HttpMethod(prmBody = true) Object[]where
	) throws SQLException {
		return t.delete();}

	@HttpMethod static public List<Object[]>
	query(@HttpMethod(prmLoadByUrl = true) MTbl t
		,@HttpMethod(prmName= "cols") List cols
		,@HttpMethod(prmName = "where") List where
		,@HttpMethod(prmName = "groupBy") List groupBy
		,@HttpMethod(prmName = "orderBy") List orderBy) throws SQLException {
		Object[]wa=t.w2c(where);
		return Sql.L(sql(t.a2c(cols),wa,t.a2c(groupBy),t.a2c(orderBy),t.dbtName,t.dbName),wa);}

	@HttpMethod static public List<Object[]>
	qry(@HttpMethod(prmName = "query") String sql
		,@HttpMethod(prmName = "where")List where//,@HttpMethod(prmName = "page") String page,@HttpMethod(prmName = "pageSize") int pageSize
	) throws SQLException {return Sql.L(sql,where==null?null:where.toArray());}


	CI[] a2c(List p){
		CI[]c=new CI[p==null?0:p.size()];
		for(int i=0;i<c.length;i++){
			Object o=p.get(i);if(o!=null)
				c[i]=col(o.toString());}
		return c;}

	Object[] w2c(List p){Object[]a=new Object[p==null?0:p.size()];
		for(int i = 0 ; i < a.length; i+=2) {
			CI c=col(p.get(i).toString());
			a[i]=c;a[i+1]=p.get(i+1);}
		return a;}

	//each command is a method call where the list-item is a map which should have
//	method:<str> , url:<str> , body:<map or str or list>
	@HttpMethod static public List
	cmnds(@HttpMethod(prmBody = true) List p,TL tl){
		List l=new LinkedList();Object r=null;
		for(Object o:p)try{r=o;
			if(o instanceof Map){
				Map m=(Map)o;
				String mt=(String)m.get("method")
					,url=(String)m.get("url");
				String[]au=url.substring(UrlPrefix.length()).split("/");//b=m.get("body");
				if("update".equals(mt))
					r=update((MTbl)prmLoadByUrl(tl,url),(Map)m.get("set"));//,(List)m.get("where")
				else if("qry".equals(mt))
					r=qry((String)m.get("query"),(List)m.get("where"));
				else if("get".equals(mt))
					r=get((MTbl.Row)prmLoadByUrl(tl,url));
				else if("insert".equals(mt)){
					MTbl.Row t=(MTbl.Row)prmLoadByUrl(tl,url);
					t.fromMap((Map)m.get("body"));
					r=insert(t);}
				else if("delete".equals(mt))
					r=delete( (MTbl.Row)prmLoadByUrl(tl,url) );
				else if("query".equals(mt)){
					MTbl t=(MTbl)prmLoadByUrl(tl,url);
					r=query(t,(List)m.get("cols"),(List)m.get("where"),(List)m.get("groupBy"),(List)m.get("orderBy"));}
				else if("DB_names".equals(mt))
					r=DB_names();
				else if("DB_get".equals(mt))
					r=DB_get((String)m.get("dbName"));
				else if("DB_get".equals(mt))
					r=DB_rename(au[0],(String)m.get("newName"));
				//else if("colTyps".equals(mt)) r=colTypes();
				else if("DB_create".equals(mt))
					r=DB_create(au[0]);
				else if("DB_drop".equals(mt))
					r=DB_drop(au[0]);
				else if("DBTbl_create".equals(mt))
					r=DBTbl_create(au[0],au[1]
						,(List)m.get("cols")
						,(List)m.get("pk")
						,(List)m.get("indices")
						,(List)m.get("fk"),tl);
				else if("DBTbl_drop".equals(mt))
					r=DBTbl_drop(au[0],au[1]);
				else if("DBTbl_rename".equals(mt))
					r=DBTbl_rename((MTbl)prmLoadByUrl(tl,url),(String)m.get("newName"));
				else if("DBTblCol_alter".equals(mt))
					r=DBTblCol_alter(au[0],au[1],au[2],au[3],(String)m.get("body"));
				//else if("DBTblCol_drop".equals(mt)) r=DBTblCol_drop((MTbl)prmLoadByUrl(tl,url),au[2]);
				//else if("DBTblCol_insert".equals(mt)) r=DBTblCol_insert((MTbl)prmLoadByUrl(tl,url),au[2],(String)m.get("beforeCol"),(Map)m.get("def"));
				//else if("DBTblCol_add".equals(mt)) r=DBTblCol_add((MTbl)prmLoadByUrl(tl,url),au[2],tl.bodyData);//m=tl.json
				//else if("DBTblCol_rename".equals(mt)) r=DBTblCol_rename((MTbl)prmLoadByUrl(tl,url),au[2],(String)m.get("newName"));
				m.put("return",r);
				r=m;
/*
List DB_names()throws SQLException

colTypes
int DB_create(@HttpMethod(prmUrlPart= true) String dbName)throws SQLException
int DB_drop(@HttpMethod(prmUrlPart= true) String dbName)throws SQLException

MTbl DBTbl_create(@HttpMethod(prmUrlPart = true) String dbName
		,@HttpMethod(prmUrlPart=true) String dbtName
		,@HttpMethod(prmName="cols") List cols//<Map<String,String>>
		,@HttpMethod(prmName = "pk") List pk//<String>
		,@HttpMethod(prmName = "fk") Map fk//<String,Map<String,String>>
		,TL tl)

int DBTbl_drop(@HttpMethod(prmUrlPart = true) String dbName
		,@HttpMethod(prmUrlPart = true) String dbtName)

int	DBTbl_rename(@HttpMethod(prmLoadByUrl = true) MTbl t
		,@HttpMethod(prmName = "newName") String newName)throws SQLException

int DBTblCol_drop(@HttpMethod(prmLoadByUrl = true) MTbl t
	,@HttpMethod(prmUrlPart= true) String col)

int DBTblCol_insert(@HttpMethod(prmLoadByUrl = true) MTbl t
	,@HttpMethod(prmUrlPart= true) String col
	,@HttpMethod(prmName = "beforeCol") String beforeCol
	,@HttpMethod(prmName = "def") Map def)

int DBTblCol_add(@HttpMethod(prmLoadByUrl = true) MTbl t
	,@HttpMethod(prmUrlPart= true) String col
	,@HttpMethod(prmBody = true) Map def)

int DBTblCol_alter(@HttpMethod(prmLoadByUrl = true) MTbl t
	,@HttpMethod(prmUrlPart= true) String col
	,@HttpMethod(prmBody = true) Map def)

Object[]get(@HttpMethod(prmLoadByUrl = true) MTbl.Row t)

Sql.Tbl insert(@HttpMethod(prmBody = true) MTbl.Row t) throws Exception

int update(@HttpMethod(prmLoadByUrl = true) MTbl t,@HttpMethod(prmName = "set") List set)

int	delete(@HttpMethod(prmLoadByUrl = true) MTbl.Row t
	,@HttpMethod(prmBody = true) List where) throws SQLException

List<Object[]>query(@HttpMethod(prmLoadByUrl = true) MTbl t
	,@HttpMethod(prmName= "cols") List cols
	,@HttpMethod(prmName = "where") List where
	,@HttpMethod(prmName = "groupBy") List groupBy
	,@HttpMethod(prmName = "orderBy") List orderBy) throws SQLException

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@HttpMethod public static List
	listKeys() throws Exception

	@HttpMethod public static List
	poll(@HttpMethod(prmBody = true) long logTime, TL tl) throws Exception

	/**
	 * output to the client , Txt-Prm is loaded based on a key from the value of the url
	 * if the Txt-Prm is null then null is returned
	 * if the Txt-Prm txt-field is not a json-array/list
	 * , then the Txt-Prm txt-field is outputted, and return execution.
	 * if the Txt-Prm txt-field is a json-array/list L , then:
	 * the output is in the same order as the list-elements
	 * the list elements can be one of two
	 * 1- element is not a map, outputted as is
	 * 2- json-object m, and the object m can have three cases
	 * 2.2-having property ref and call
	 * 2.1-having property ref
	 * 2.3-having property eval , and optionally having ref
	 * 2.4-not having ref nor eval, in such a case the json-object m / list-element will be outputted
	 * /
				@HttpMethod(useClassName = false,usrLoginNeeded = false)public static Txt
				get(@HttpMethod(prmLoadByUrl = true) Txt prm, TL tl) throws Exception

				@HttpMethod public static List<Txt>
				getKeys(@HttpMethod(prmLoadByUrl= true) Txt j,
					@HttpMethod(prmBody = true) List<String> keys)

				@HttpMethod public static Map
				load(@HttpMethod(prmLoadByUrl= true) Txt p,TL tl) throws SQLException


				@HttpMethod public static Txt
				update( @HttpMethod(prmLoadByUrl = true) Txt p
					,@HttpMethod(prmBody = true) Map m, TL tl) throws Exception

				@HttpMethod public static Txt
				create( @HttpMethod(prmBody = true) Txt x, TL tl) throws Exception

				@HttpMethod public static Txt
				txt( @HttpMethod(prmLoadByUrl= true) Txt x
					,@HttpMethod(prmBody = true) String v, TL tl) throws Exception

				@HttpMethod public static Txt
				meta( @HttpMethod(prmLoadByUrl= true) Txt x
					,@HttpMethod(prmBody = true) Map m, TL tl) throws Exception

				@HttpMethod public static Txt
				delete(@HttpMethod(prmLoadByUrl = true) Txt x, TL tl) throws Exception


				@HttpMethod public static Object
				call(@HttpMethod(prmUrlPart = true) String m,
					@HttpMethod(prmUrlRemaining = true) String key,
					@HttpMethod(prmBody = true) List args,
					TL tl) throws Exception
				@HttpMethod	public static Object
				eval(@HttpMethod(prmUrlRemaining = true) String key,
					@HttpMethod(prmBody = true) String src,
					TL tl) throws Exception

				@HttpMethod	public static Object
				prop(@HttpMethod(prmLoadByUrl= true) Txt x,
					@HttpMethod(prmName= "prop") String prop,
					@HttpMethod(prmName= "val") Object val,
					TL tl) throws Exception

				//each command is a method call where the list-item is a map which should have
//	method:<str> , url:<str> , body:<map or str or list>
				@HttpMethod static public List
				cmnds(@HttpMethod(prmBody = true) List p,TL tl)
*/			}
		}catch(Exception ex){r=ex;}
		finally {
			l.add(r);
		}
		return l;}

}//class MTbl

/**
 * Created by Vaio-PC on 2/23/2018.
 * Created by Vaio-PC on 1/26/2018.
 * Created by Vaio-PC on 18/01/2018.
 * Created by moh on 14/7/17.
 */

static final String packageName = "dev201801"
	, SrvltName = packageName + ".TxtSrvlt"
	, UrlPrefix = "/txtSrvlt/";

static Map<String, Method> mth = new HashMap<String, Method>();


static void staticInit() {
	registerMethods(TxtSrvlt.class);
	registerMethods(Txt.class);
	registerMethods(MTbl.class);
	if(! Sql.Tbl.registered.contains(Txt.class))
		 Sql.Tbl.registered.add(Txt.class);
	}

static {staticInit();}

public static void registerMethods(Class p) {
	Method[] b = p.getMethods();
	String cn = p.getSimpleName();
	for(Method m : b) {
		HttpMethod h = m.getAnnotation(HttpMethod.class);
		if(h != null) {
			String s = m.getName();
			mth.put(h.useClassName() ? cn + "." + s : s, m);
		}
	}
}//registerHttpMethod

//need to do a forgot password recovery method
@HttpMethod(usrLoginNeeded = false) public static Map
login(@HttpMethod(prmLoadByUrl = true) Txt j
	, @HttpMethod(prmName = "pw") String pw, TL tl)throws Exception {
	if(j==null)
		j=Txt.loadBy(tl.h.req.getRequestURI().substring(UrlPrefix.length()),1);
	if(j != null ){Object js=j.jt();
		if( js instanceof Map) {
			Map m = (Map) js;
			Object dbPw = m.get("pw");
			if(pw != null && dbPw instanceof String) {
				String prmPw = Util.b64d(pw);
				prmPw = Util.md5(prmPw);
				if(prmPw.equals(dbPw)) {
					Map x=new HashMap();x.putAll(m);
					m.put("Txt",j);//m.put("key",j.key);
					tl.h.s("usr", tl.usr =m);
					//addOnEnterRequestListener( tl,Txt.class );
					return x;}}}}
	return null;}

@HttpMethod public static boolean
logout(@HttpMethod(prmUrlRemaining = true) String usr, TL tl) {
	if(tl != null && tl.usr != null ) {//&& ((Txt)tl.usr.get("Txt")).key.equals(usr)
		tl.h.s("usr", tl.usr = null);
		tl.h.getSession().setMaxInactiveInterval(1);
		return true;}
	return false;}

/**
 * annotation to designate a java method as an ajax/xhr entry point of execution
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpMethod {
	boolean useClassName() default true;
	boolean nestJsonReq() default true;//if false , then only the returned-value from the method call is json-stringified as a response body, if true the returned-value is set in the json-request with prop-name "return"
	boolean usrLoginNeeded() default true;
	String prmName() default "";
	boolean prmUrlPart() default false;
	boolean prmUrlRemaining() default false;
	boolean prmLoadByUrl() default false;
	boolean prmBody() default false;
}//HttpMethod

@Override public void service(HttpServletRequest request, HttpServletResponse response) {
	TL tl = null;
	Object retVal = null;
	try {
		tl = TL.Enter(request, response);
		tl.h.r("contentType", "text/json");
		String hm = tl.h.req.getMethod();
		Method op = mth.get(hm);
		if(op == null)
			for(String s : mth.keySet())
				if(s.equalsIgnoreCase(hm))
					op = mth.get(s);
		HttpMethod httpMethodAnno = op == null ? null : op.getAnnotation(HttpMethod.class);
		tl.log("jsp:version2017.02.09.17.10:op=", op, httpMethodAnno);
		if(tl.usr == null && (httpMethodAnno == null || httpMethodAnno.usrLoginNeeded()))
			op = null;
		if(op != null) {
			Class[] prmTypes = op.getParameterTypes();
			Class cl = op.getDeclaringClass();
			Annotation[][] prmsAnno = op.getParameterAnnotations();
			int n = prmsAnno == null ? 0 : prmsAnno.length, i = - 1,urli=-1,urln=0;
			Object[] args = new Object[n];
			String url=tl.h.req.getRequestURI();
			url=url.substring(UrlPrefix.length());
			String[] urla = null;Map urlm=null;

			if(url!=null && url.indexOf('/')!=-1) {
				urla = url.split("/");
				urln = urla == null ? 0 : urla.length;
				tl.h.r("urlParts",urlm=Util.mapCreate("a",urla,"n",urln,"i",urli=0,"url",url));
				}

			for(Annotation[] t : prmsAnno) try {
				HttpMethod pp = t.length > 0 && t[0] instanceof HttpMethod ? (HttpMethod) t[0] : null;
				Class prmClss = prmTypes[++ i];
				String nm = pp != null ? pp.prmName() : "arg" + i;//t.getName();
				Object o = null;
				if(pp != null && pp.prmUrlPart()) {
					args[i]=urla==null?url:urla[urli++];
					if(urlm!=null)
						urlm.put("i",urli);
				} else if(pp != null && pp.prmUrlRemaining()) {
					if(urla!=null&&urli<urln) {
						StringBuilder b = new StringBuilder(urla[urli++]);
						while(urli<urln){b.append('/').append(urla[urli++]);}
						args[i] = b.toString();//url.indexOf(urlIndx + 1);
						urlm.put("i",urli);
					}
				} else if(pp != null && pp.prmLoadByUrl()) {
					Class[] ca = {TL.class , String.class};
					Method//m=cl.getMethod( "prmLoadByUrl", ca );if(m==null)
						m = prmClss.getMethod("prmLoadByUrl", ca);
					args[i] = m == null ? null : m.invoke(prmClss, tl, url);
				} else if(Sql.Tbl.class.isAssignableFrom(prmClss)) {
					Sql.Tbl f = (Sql.Tbl) prmClss.newInstance();
					args[i] = f;
					if(pp != null && pp.prmBody())
						f.fromMap(tl.json);
					else {
						o = tl.json.get(nm);
						if(o instanceof Map) f.fromMap((Map) o);
						else if(o instanceof List) f.vals(((List) o).toArray());
						else if(o instanceof Object[]) f.vals((Object[]) o);
						else f.readReq("");}
				}
				else if(pp != null && pp.prmBody())
					args[i] = prmClss.isAssignableFrom(String.class)
						 ? Util.readString(tl.h.req.getReader())
						 : tl.bodyData;
				else
					args[i] = o = TL.class.equals(prmClss) ? tl
						: tl.h.req(nm, prmClss);
			} catch(Exception ex) {
				tl.error(ex, SrvltName, ".service:arg:i=", i);
			}
			retVal = n == 0 ? op.invoke(cl)
				: n == 1 ? op.invoke(cl, args[0])
				: n == 2 ? op.invoke(cl, args[0], args[1])
				: n == 3 ? op.invoke(cl, args[0], args[1], args[2])
				: n == 4 ? op.invoke(cl, args[0], args[1], args[2], args[3])
				: n == 5 ? op.invoke(cl, args[0], args[1], args[2], args[3], args[4])
				: op.invoke(cl, args);
			if(httpMethodAnno != null && httpMethodAnno.nestJsonReq() && tl.json != null) {
				tl.json.put("return", retVal);
				retVal = tl.json;
			}
		}
		// else Util.mapSet(tl.response,"msg","Operation not authorized ,or not applicable","return",false);
		if(tl.h.r("responseDone") == null) {
			if(tl.h.r("responseContentTypeDone") == null)
				response.setContentType(String.valueOf(tl.h.r("contentType")));
			Json.Output o = tl.getOut();
			o.o(retVal);
			tl.log(SrvltName, ".run:xhr-response:", tl.jo().o(retVal).toString());
		}
		tl.getOut().flush();
	} catch(Exception x) {
		if(tl != null) {
			tl.error(x, SrvltName, ":");
			try {
				tl.getOut().o(x);
			} catch(IOException iox) {
			}
		} else
			x.printStackTrace();
	} finally {
		TL.Exit();
	}
}//run op servlet.service

/** * Created by mbohamad on 19/07/2017.*/
static class TL{
	public static final String TlName=Srvlt.packageName+".TL";
	public TL(HttpServletRequest r,HttpServletResponse n,Writer o){h.req=r;h.rspns=n;out=new Json.Output(o);}

	public H h=new H();
	public Map<String,Object> /**accessing request in json-format*/json;
	public Object bodyData;
	public Date now;
	Map usr;//Txt
	/**wrapping JspWriter or any other servlet writer in "out" */
	Json.Output out,/**jo is a single instanceof StringWriter buffer*/jo;

//TL member variables

	/**the static/class variable "tl"*/ static ThreadLocal<TL> tl=new ThreadLocal<TL>();
	public static final String CommentHtml[]={"\n<!--","-->\n"},CommentJson[]={"\n/*","\n*/"};

	public Json.Output jo(){if(jo==null)try{jo=new Json.Output();}catch(Exception x){
		error(x,TlName,".jo:IOEx:");
	}return jo;}
	public Json.Output getOut() throws IOException {return out;}

	/**sets a new TL-instance to the localThread*/
	public static TL Enter(HttpServletRequest r,HttpServletResponse response) throws IOException{
		TL p;if(mth==null || mth.size()==0)Srvlt.staticInit();
		tl.set(p=new TL(r,response,response.getWriter()));
		p.onEnter();
		return p;}
	private void onEnter()throws IOException {
		h.ip=h.getRequest().getRemoteAddr();
		now=new Date();//seqObj=seqProp=now.getTime();
		try{Object o=h.req.getContentType();
			o=bodyData=o==null?null
				:o.toString().contains("json")?Json.Prsr.parse(h.req)
				:o.toString().contains("part")?h.getMultiParts():null;
			json=o instanceof Map<?, ?>?(Map<String, Object>)o:null;//req.getParameterMap() ;
			h.logOut=h.var("logOut",h.logOut);
			if(h.getSession().isNew())
				Sql.Tbl.check(this);//Srvlt.Domain.loadDomain0();
			usr=(Map)h.s("usr");//Txt
		}catch(Exception ex){
			error(ex,TlName,".onEnter");
		}
		//if(pages==null){rsp.setHeader("Retry-After", "60");rsp.sendError(503,"pages null");throw new Exception("pages null");}
		if(h.logOut)out.w(h.comments[0]).w(TlName).w(".tl.onEnter:\n").o(this).w(h.comments[1]);
	}//onEnter
	private void onExit(){usr=null;h.ip=null;now=null;h.req=null;json=null;out=jo=null;h.rspns=null;}//ssn=null;
	/**unsets the localThread, and unset local variables*/
	public static void Exit()//throws Exception
	{TL p=TL.tl();if(p==null)return;
		Sql.close(p);//changed 2017.7.17
		p.onExit();tl.set(null);}

	public class H{
		public boolean logOut=false;
		public String ip;

		public String comments[]=CommentJson;
		public HttpServletRequest req;//Srvlt a;
		public HttpServletResponse rspns;
		public HttpServletRequest getRequest(){return req;}
		public HttpSession getSession(){return req.getSession();}
		public ServletContext getServletContext(){return getSession().getServletContext();}
		Map getMultiParts(){
			Map<Object,Object>m=null;
			if( ServletFileUpload.isMultipartContent(req))try
			{DiskFileItemFactory factory=new DiskFileItemFactory();
				factory.setSizeThreshold(40000000);//MemoryThreshold);
				String path="";//Srvlt.UploadPth;//app(this).getUploadPath();
				String real=context.getRealPath(TL.this, path);//getServletContext().getRealPath(path);
				File f=null,uploadDir;
				uploadDir=new File(real);
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
				error(ex,TlName,".h.getMultiParts");
			}
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
		public Object var(String pn){
			HttpSession ss=getSession();
			Object r=null;try{Object sVal=ss.getAttribute(pn);String reqv=req(pn);
				if(reqv!=null&&!reqv.equals(sVal)){ss.setAttribute(pn,r=reqv);//logo(TlName,".h.var(",pn,")reqVal:sesssion.set=",r);
				}
				else if(sVal!=null){r=sVal; //logo(TlName,".h.var(",pn,")sessionVal=",r);
				}}catch(Exception ex){
				ex.printStackTrace();
			}return r;}
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
						log(TlName,".h.var(",n,",<T>",defVal,"):defVal not instanceof ssnVal:",s);//added 2016.07.18
				}
			}return defVal;
		}

		public Object reqo(String n){
			if(json!=null )
			{Object o=json.get(n);if(o!=null)return o;}
			String r=req.getParameter(n);
			if(r==null)r=req.getHeader(n);
			if(logOut)log(TlName,".h.reqo(",n,"):",r);
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

		public Date req(String n,Date defval){
			Object o=req(n);
			if(o instanceof Date)defval=(Date)o;
			else if(o instanceof Number)defval=new Date(((Number)o).longValue());
			else if(o!=null)defval=Util.parseDate(o instanceof String?(String)o:(o.toString()));
			return defval;}

		public double req(String n,double defval) {
			Object o=reqo(n);
			if(o instanceof Double)defval=(Double)o;
			else if(o instanceof Number)defval=((Number)o).doubleValue();
			else if(o!=null){
				String s=o instanceof String?(String)o:(o.toString());
				if(Util.isNum( s ))
					defval=new Double(s);}
			return defval;}

		public <T>T req(String n,T defVal) {
			Object o=reqo(n);if(o instanceof String)
				defVal=Util.parse((String)o,defVal);
			else if( defVal.getClass( ).isInstance( o )) {//o instanceof T
				T o1 = ( T ) o;
				defVal=o1;
			}else if(o!=null)defVal=Util.parse( o.toString(),defVal );
			return defVal;}

		public Object req(String n,Class c) {
			Object o=reqo(n);
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
				);}}catch(Exception ex){
				ex.printStackTrace();
			}return s;}
	/**calls the servlet log method*/
	public void log(Object...s){logA(s);}
	public void logA(Object[]s){try{
		jo().clrSW();
		for(Object t:s)jo.w(String.valueOf(t));
		String t=jo.toStrin_();
		h.getServletContext().log(t);
		if(h.logOut)out.flush().w(h.comments[0]).w(t).w(h.comments[1]);
	}catch(Exception ex){
		ex.printStackTrace();
	}}

	public void error(Throwable x,Object...p){try{
		String s=jo().clrSW().w("error:").o(p,x).toString();
		h.getServletContext().log(s);
		if(h.logOut)out.w(h.comments[0]//"\n/*
		).w("error:").w(s.replaceAll("<", "&lt;"))
			.w("\n---\n").o(x).w(h.comments[1] );
		if(x!=null)x.printStackTrace();}
	catch(Exception ex){
		ex.printStackTrace();
	}}
	public Json.Output o(Object...a)throws IOException{if(out!=null&&out.w!=null)for(Object s:a)out.w.write(s instanceof String?(String)s:String.valueOf(s));return out;}
	/**get a pooled jdbc-connection for the current Thread, calling the function dbc()*/
	Connection dbc()throws SQLException {
		TL p=this;//Object s=context.Sql.reqCon.str,o=p.s(s);
		Object[]a= Sql.stack(p,null);//o instanceof Object[]?(Object[])o:null;
		//o=a==null?null:a[0];
		if(a[0]==null)//o==null||!(o instanceof Connection))
			a[0]= Sql.c();
		return (Connection)a[0];}

	public String usrUn(){String s=null;if(usr!=null) {
		Txt x = ( Txt ) usr.get( "Txt" );
		if(x!=null)s=x.key;
	}return s;} //usrUn()

	public String usrGroup(){String s=null;if(usr!=null) {
		Txt x = ( Txt ) usr.get( "Txt" );
		if(x!=null){Map m=(Map)x.jt();
			s=(String)m.get( "group" );
		}
	}return s;} //usr/group()
}//class TL

enum context{ROOT(
	"C:\\apache-tomcat-8.0.15\\webapps\\ROOT\\"
	,"/Users/moh/Google Drive/air/apache-tomcat-8.0.30/webapps/ROOT/"
	,"/public_html/i1io/"
	,"D:\\apache-tomcat-8.0.15\\webapps\\ROOT\\"
);
	String str,a[];context(String...p){str=p[0];a=p;}
	enum DB{
		pool("dbpool-"+SrvltName)
		,reqCon("javax.sql.PooledConnection")
		,server("localhost","216.227.216.46")//,"216.227.220.84"
		,dbName("aswan","js4d00_aswan")
		,un("root","js4d00_theblue")
		,pw("qwerty","theblue","")
		;String str,a[];DB(String...p){str=p[0];a=p;}
	}
	static String getRealPath(TL t,String path){
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
			t.error(ex,SrvltName,".context.getRealPath:",path);
		}
		return real==null?"./"+path:real;}
	static int getContextIndex(TL t){
		try{File f=null;
			int i=ROOT.a.length-1;
			while( i>=0 )
			{	f=new File(ROOT.a[i]);
				if(f!=null && f.exists())
					return i;i--;
			}
		}catch(Exception ex){
			t.error(ex,SrvltName,".context.getContextIndex:");
		}
		return -1;}
	//***/static Map<Sql,String> getContextPack(TL t,List<Map<Sql,String>>a){return null;}
}//context

static class Util{//utility methods
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
		char c=n>0?v.charAt(0):'\0';
		boolean b=n>0;
		if(n>2&&c=='0'){c=v.charAt(1);
		if(c=='X'||c=='x'){i=1;
			while(b && (++i)<n){
				c=v.charAt(i);
				b=(c>='0'&&c<='9') || (c>='A'&&c<='F')  || (c>='a'&&c<='f') ;
			}
			return b;}}
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
		TL.tl().error(ex, SrvltName,".Util.parseInt:",v,dv);
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
			TL.tl().error(x, SrvltName,".Util.<T>T parse(String s,T defval):",s,defval);
		}
		return defval;}
	public static Object parse(String s,Class c){
		if(s!=null)try
		{	if(String.class.equals(c))return s;
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
		else if(URL.class.isAssignableFrom(c))try
		{return new URL("file:" +TL.tl().h.getServletContext().getContextPath()+'/'+s);}
		catch (Exception ex) {
			TL.tl().error(ex,SrvltName,".Util.parse:URL:p=",s," ,c=",c);
		}
			boolean b=c==null?false:c.isEnum();
			if(!b){Class ct=c.getEnclosingClass();b=ct==null?false:ct.isEnum();if(b)c=ct;}
			if(b){
				for(Object o:c.getEnumConstants())
					if(s.equalsIgnoreCase(o.toString()))
						return o;
			}
			return Json.Prsr.parse(s);
		}catch(Exception x){//changed 2016.06.27 18:28
			TL.tl().error(x, SrvltName,".Util.<T>T parse(String s,Class):",s,c);
		}
		return s;}

	public static String md5(String s){
		if(s!=null)try{java.security.MessageDigest m=
			java.security.MessageDigest.getInstance("MD5");
			//m.update(s.getBytes());
			String r=java.util.Base64.getEncoder().encodeToString(m.digest(s.getBytes()));
			return r;
		}catch(Exception x){//changed 2016.06.27 18:28
			TL.tl().error(x, SrvltName,".Util.md5(String s):",s);
		}
		return "";}

	public static String b64d(String s){
		if(s!=null)try{
			byte[]m=java.util.Base64.getDecoder().decode( s );
			String r= new String(m,"UTF-8");
			return r;
		}catch(Exception x){//changed 2016.06.27 18:28
			TL.tl().error(x, SrvltName,".Util.b64d(String s):",s);
		}
		return "";}

	public static String b64e(String s){
		if(s!=null)try{
			byte[]m=s.getBytes();
			String r=java.util.Base64.getEncoder().encodeToString( m );
			return r;
		}catch(Exception x){//changed 2016.06.27 18:28
			TL.tl().error(x, SrvltName,".Util.b64e(String s):",s);
		}
		return "";}

	static String readString(BufferedReader r) throws IOException {
		StringBuilder b = new StringBuilder();
		int c = 0;
		String line = r.readLine();
		while(line != null) {
			if(c++ > 0) b.append('\n');
			b.append(line);
			line = r.readLine();
		}
		return b.toString();
	}//readString
}//class Util

static class Sql {
	/**returns a jdbc pooled Connection.
	 uses MysqlConnectionPoolDataSource with a database from the enum context.Sql.url.str,
	 sets the pool as an application-scope attribute named context.Sql.pool.str
	 when first time called, all next calls uses this context.Sql.pool.str*/
	public static synchronized Connection c()throws SQLException {
		TL t=TL.tl();
		Object[]p=null,a=stack(t,null);//Object[])t.s(context.Sql.reqCon.str);
		Connection r=(Connection)a[0];//a ==null?null:
		if(r!=null)return r;
		MysqlConnectionPoolDataSource d=(MysqlConnectionPoolDataSource)t.h.a(context.DB.pool.str);
		r=d==null?null:d.getPooledConnection().getConnection();
		if(r!=null)
			a[0]=r;//changed 2017.07.14
		else try
		{try{int x=context.getContextIndex(t);
			t.log(SrvltName,".Sql.c:1:getContextIndex:",x);
			if(x!=-1)
			{	p=c(t,x,x,x,x);t.log(SrvltName,".Sql.c:1:c2:",p);
				r=(Connection)p[1];
				return r;}
		}catch(Exception e){
			t.log(SrvltName,".Sql.MysqlConnectionPoolDataSource:1:",e);
		}
			String[]dba=context.DB.dbName.a
				,sra=context.DB.server.a
				,una=context.DB.un.a
				,pwa=context.DB.pw.a;//CHANGED: 2016.02.18.10.32
			for(int idb=0;r==null&&idb<dba.length;idb++)
				for(int iun=0;r==null&&iun<una.length;iun++)
					for(int ipw=0;r==null&&ipw<pwa.length;ipw++)//n=context.Sql.len()
						for(int isr=0;r==null&&isr<sra.length;isr++)try
						{	p=c(t,idb,iun,ipw,isr);
							r=(Connection)p[1];
							if(t.h.logOut)t.log("new "+context.DB.pool.str+":"+p[0]);
						}catch(Exception e){
							t.log(SrvltName,".Sql.MysqlConnectionPoolDataSource:",idb,",",isr,",",iun,ipw,t.h.logOut?p[2]:"",",",e);
						}
		}catch(Throwable e){
			t.error(e,SrvltName,".Sql.MysqlConnectionPoolDataSource:throwable:");
		}//ClassNotFoundException
		if(t.h.logOut)t.log(context.DB.pool.str+":"+(p==null?null:p[0]));
		if(r==null)try
		{r=java.sql.DriverManager.getConnection
			("jdbc:mysql://"+context.DB.server.str
				+"/"+context.DB.dbName.str
				,context.DB.un.str,context.DB.pw.str
			);Object[]b={r,null};
			t.h.s(context.DB.reqCon.str,b);
		}catch(Throwable e){
			t.error(e,SrvltName,".Sql.DriverManager:");
		}
		return r;}
	public static synchronized Object[]c(TL t,int idb,int iun,int ipw,int isr) throws SQLException{
		MysqlConnectionPoolDataSource d=new MysqlConnectionPoolDataSource();
		String ss=null,s=context.DB.dbName.a[Math.min(context.DB.dbName.a.length-1,idb)];
		if(t.h.logOut)ss="\ndb:"+s;
		d.setDatabaseName(s);d.setPort(3306);
		s=context.DB.server.a[Math.min(context.DB.server.a.length-1,isr)];
		if(t.h.logOut)ss+="\nsrvr:"+s;
		d.setServerName(s);
		s=context.DB.un.a[Math.min(context.DB.un.a.length-1,iun)];if(t.h.logOut)ss+="user:"+s;
		d.setUser(s);
		s=context.DB.pw.a[Math.min(context.DB.pw.a.length-1,ipw)];if(t.h.logOut)ss+="\npw:"+s;
		d.setPassword(s);
		Connection r=d.getPooledConnection().getConnection();
		t.h.a(context.DB.pool.str,d);
		Object[]a={d,r,ss};//,b={r,null};t.s(context.Sql.reqCon.str,b);
		stack(t,r);
		return a;}
	/**returns a jdbc-PreparedStatement, setting the variable-length-arguments parameters-p, calls dbP()*/
	public static PreparedStatement p( String sql, Object...p)throws SQLException{return P(sql,p);}
	/**returns a jdbc-PreparedStatement, setting the values array-parameters-p, calls TL.dbc() and log()*/
	public static PreparedStatement P(String sql,Object[]p)throws SQLException{return P(sql,p,true);}
	public static PreparedStatement P(String sql,Object[]p,boolean odd)throws SQLException {
		TL t=TL.tl();Connection c=t.dbc();
		PreparedStatement r=c.prepareStatement(sql);if(t.h.logOut)
			t.log(SrvltName,"("+t+").Sql.P(sql="+sql+",p="+p+",odd="+odd+")");
		if(odd){if(p.length==1)
			r.setObject(1,p[0]);else
			for(int i=1,n=p.length;p!=null&&i<n;i+=2)if((!(p[i] instanceof List)) ) // ||!(p[i-1] instanceof List)||((List)p[i-1]).size()!=2||((List)p[i-1]).get(1)!=Tbl.Co.in )
				r.setObject(i/2+1,p[i]);//if(t.logOut)TL.log("dbP:"+i+":"+p[i]);//TODO: recursive case with Co.or and Co.and
		}else
			for(int i=0;p!=null&&i<p.length;i++)
			{r.setObject(i+1,p[i]);if(t.h.logOut)t.log("dbP:"+i+":"+p[i]);}
		if(t.h.logOut)t.log("dbP:sql="+sql+":n="+(p==null?-1:p.length)+":"+r);return r;}

	/**returns a jdbc-ResultSet, setting the variable-length-arguments parameters-p, calls dbP()*/
	public static ResultSet r( String sql, Object...p)throws SQLException{return R(sql,p);}//changed 2017.7.17
	/**returns a jdbc-ResultSet, setting the values array-parameters-p, calls dbP()*/
	public static ResultSet R(String sql,Object[]p)throws SQLException{
		PreparedStatement x=P(sql,p,true);
		ResultSet r=x.executeQuery();
		push(r,TL.tl());
		return r;}
	static Object[]stack(TL tl,Connection c){return stack(tl,c,true);}
	static Object[]stack(TL tl,Connection c,boolean createIfNotExists){
		return stack(tl,c,createIfNotExists,false);}
	static Object[]stack(TL tl,Connection c,boolean createIfNotExists,boolean deleteArray){
		if(tl==null)tl=TL.tl();Object o=context.DB.reqCon.str;
		Object[]a=(Object[])tl.h.s(o);
		if(deleteArray)
			tl.h.s(o,a=null);
		else if(a==null&&createIfNotExists)
		{Object[]b={c,null};
			tl.h.s(o,a=b);}
		return a;}
	static List<ResultSet>stack(TL tl){return stack(tl,true);}
	static List<ResultSet>stack(TL tl,boolean createIfNotExists){
		Object[]a=stack(tl,null,createIfNotExists);
		List<ResultSet>l=a==null||a.length<2?null:(List<ResultSet>)a[1];
		if(l==null&&createIfNotExists)
			a[1]=l=new LinkedList<ResultSet>();
		return l;}
	static void push(ResultSet r,TL tl){try{//2017.07.14
		List<ResultSet>l=stack(tl);//if(l==null){stack(tl,null)[1]=l=new LinkedList<ResultSet>();l.add(r);}else
		if(!l.contains(r))
			l.add(r);
	}catch (Exception ex){
		tl.error(ex,SrvltName,".Sql.push");
	}}

	//public static void close(Connection c){close(c,tl());}
	public static void close(Connection c,TL tl){
		try{if(c!=null){
			List<ResultSet>a=stack(tl,false);
			if(a==null||a.size()<1)
				tl.h.s(context.DB.reqCon.str,a=null);
			if(a==null)
				c.close();}
		}catch(Exception e){
			e.printStackTrace();
		}}
	public static void close(TL tl){
		try{Object[]a=stack(tl,null,false);
			Connection c=a==null?null:(Connection) a[0];
			if(c!=null)close(c,tl);
		}catch(Exception e){
			e.printStackTrace();
		}}
	public static void close(ResultSet r){close(r,TL.tl(),false);}
	//public static void close(ResultSet r,boolean closeC){close(r,TL.tl(),closeC);}
	public static void close(ResultSet r,TL tl){close(r,tl,false);}
	public static void close(ResultSet r,TL tl,boolean closeC){
		if(r!=null)try{
			Statement s=r.getStatement();
			Connection c=closeC?s.getConnection():null;
			List<ResultSet>l=stack(tl,false);
			if(l!=null){l.remove(r);
				if( l.size()<1 )
					l=null;}
			r.close();s.close();
			if(l==null&&closeC)close(c,tl);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**returns a string or null, which is the result of executing sql,
	 calls dpR() to set the variable-length-arguments parameters-p*/
	public static String q1str(String sql,Object...p)throws SQLException{return q1Str(sql,p);}
	public static String q1Str(String sql,Object[]p)throws SQLException
	{String r=null;ResultSet s=null;try{s=R(sql,p);r=s.next()?s.getString(1):null;}finally{close(s);}return r;}//CHANGED:2015.10.23.16.06:closeRS ; CHANGED:2011.01.24.04.07 ADDED close(s,dbc());
	public static String newUuid()throws SQLException{return q1str("select uuid();");}
	/**returns an java obj, which the result of executing sql,
	 calls dpR() to set the variable-length-arguments parameters-p*/
	public static Object q1obj(String sql,Object...p)throws SQLException{return q1Obj(sql,p);}
	public static Object q1Obj(String sql,Object[]p)throws SQLException {
		ResultSet s=null;try{s=R(sql,p);return s.next()?s.getObject(1):null;}finally{close(s);}}
	public static <T>T q1(String sql,Class<T>t,Object[]p)throws SQLException {
		ResultSet s=null;try{s=R(sql,p);return s.next()?s.getObject(1,t):null;}finally{close(s);}}
	/**returns an integer or df, which the result of executing sql,
	 calls dpR() to set the variable-length-arguments parameters-p*/
	public static int q1int(String sql,int df,Object...p)throws SQLException{return q1Int(sql,df,p);}
	public static int q1Int(String sql,int df,Object[]p)throws SQLException
	{ResultSet s=null;try{s=R(sql,p);return s.next()?s.getInt(1):df;}finally{close(s);}}//CHANGED:2015.10.23.16.06:closeRS ;
	/**returns a double or df, which is the result of executing sql,
	 calls dpR() to set the variable-length-arguments parameters-p*/
	public static double q1dbl(String sql,double df,Object...p)throws SQLException
	{ResultSet s=null;try{s=R(sql,p);return s.next()?s.getDouble(1):df;}finally{close(s);}}//CHANGED:2015.10.23.16.06:closeRS ;
	/**returns as an array of rows of arrays of columns of values of the results of the sql
	 , calls dbL() setting the variable-length-arguments values parameters-p*/
	public static Object[][]q(String sql,Object...p)throws SQLException{return Q(sql,p);}
	public static Object[][]Q(String sql,Object[]p)throws SQLException
	{List<Object[]>r=L(sql,p);Object b[][]=new Object[r.size()][];r.toArray(b);r.clear();return b;}
	/**return s.getMetaData().getColumnCount();*/
	public static int cc(ResultSet s)throws SQLException{return s.getMetaData().getColumnCount();}
	/**calls L()*/
	public static List<Object[]> l(String sql,Object...p)throws SQLException{return L(sql,p);}
	/**returns a new linkedList of the rows of the results of the sql
	 ,each row/element is an Object[] of the columns
	 ,calls dbR() and dbcc() and dbclose(ResultSet,TL.dbc())*/
	public static List<Object[]> L(String sql,Object[]p)throws SQLException {
		TL t=TL.tl();ResultSet s=null;List<Object[]> r=null;try{s=R(sql,p);Object[]a;r=new LinkedList<Object[]>();
			int cc=cc(s);while(s.next()){r.add(a=new Object[cc]);
				for(int i=0;i<cc;i++){a[i]=s.getObject(i+1);
				}}return r;}finally{close(s,t);//CHANGED:2015.10.23.16.06:closeRS ;
			if(t.h.logOut)try{t.log(t.jo().w(SrvltName).w(".Sql.L:sql=").o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}
			catch(IOException x){
				t.error(x,SrvltName,".Sql.List:",sql);
			}}}

	public static List<Integer[]>qLInt(String sql,Object...p)throws SQLException{return qLInt(sql,p);}//2017.07.14
	public static List<Integer[]>QLInt(String sql,Object[]p)throws SQLException{//2017.07.14
		TL tl=TL.tl();
		ResultSet s=null;
		List< Integer[]> r=null;
		try{s=R(sql,p);
			Integer[]a;
			r=new LinkedList<Integer[]>();
			int cc=cc(s);
			while(s.next()){
				r.add(a=new Integer[cc]);
				for(int i=0;i<cc;i++)
					a[i]=s.getInt(i+1);
			}return r;
		}finally
		{close(s,tl);
			if(tl.h.logOut)try{tl.log(tl.jo().w(SrvltName).w(".Sql.Lt:sql=")
				.o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}
			catch(IOException x){
				tl.error(x,SrvltName,".Sql.Lt:",sql);
			}
		}
	}

	public static List<Object> q1colList(String sql,Object...p)throws SQLException {
		ResultSet s=null;List<Object> r=null;try{s=R(sql,p);r=new LinkedList<Object>();
		while(s.next())r.add(s.getObject(1));return r;}
	finally{TL t=TL.tl();close(s,t);if(t.h.logOut)
		try{t.log(t.jo().w(SrvltName).w(".Sql.q1colList:sql=")//CHANGED:2015.10.23.16.06:closeRS ;
			 .o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}catch(IOException x){
			t.error(x,SrvltName,".Sql.q1colList:",sql);
		}}}

	public static <T>List<T> q1colTList(String sql,Class<T>t,Object...p)throws SQLException {
		ResultSet s=null;List<T> r=null;try{s=R(sql,p);r=new LinkedList<T>();//Class<T>t=null;
		while(s.next())r.add(
			s.getObject(1,t));return r;}
	finally{TL tl=TL.tl();close(s,tl);if(tl.h.logOut)
		try{tl.log(tl.jo().w(SrvltName).w(".Sql.q1colList:sql=")//CHANGED:2015.10.23.16.06:closeRS ;
			.o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}catch(IOException x){
			tl.error(x,SrvltName,".Sql.q1colList:",sql);
		}}}

	public static Object[] q1col(String sql,Object...p)throws SQLException
	{List<Object> l=q1colList(sql,p);Object r[]=new Object[l.size()];l.toArray(r);l.clear();return r;}
	public static <T>T[] q1colT(String sql,Class<T>t,Object...p)throws SQLException
	{List<T> l=q1colTList(sql,t,p);T[]r=(T[]) java.lang.reflect.Array.newInstance(t,l.size());l.toArray(r);l.clear();return r;}
	/**returns a row of columns of the result of sql
	 ,calls dbR(),dbcc(),and dbclose(ResultSet,TL.dbc())*/
	public static Object[] q1row(String sql,Object...p)throws SQLException{return q1Row(sql,p);}
	public static Object[] q1Row(String sql,Object[]p)throws SQLException {
		ResultSet s=null;try{s=R(sql,p);Object[]a=null;int cc=cc(s);if(s.next())
	{a=new Object[cc];for(int i=0;i<cc;i++)try{a[i]=s.getObject(i+1);}
	catch(Exception ex){
		TL.tl().error(ex,SrvltName,".Sql.q1Row:",sql);a[i]=s.getString(i+1);
	}}
		return a;}finally{close(s);}}//CHANGED:2015.10.23.16.06:closeRS ;
	/**returns the result of (e.g. insert/update/delete) sql-statement
	 ,calls dbP() setting the variable-length-arguments values parameters-p
	 ,closes the preparedStatement*/
	public static int x(String sql,Object...p)throws SQLException{return X(sql,p);}
	public static int X(String sql,Object[]p)throws SQLException {
		int r=-1;try{PreparedStatement s=P(sql,p,false);r=s.executeUpdate();s.close();return r;}
		finally{TL t=TL.tl();if(t.h.logOut)try{
			t.log(t.jo().w(SrvltName).w(".Sql.x:sql=").o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}
		catch(IOException x){
			t.error(x,SrvltName,".Sql.X:",sql);
		}}}
	/**output to tl.out the Json.Output.oRS() of the query*/
	public static void q2json(String sql,Object...p)throws SQLException{
		ResultSet s=null;
		TL tl=TL.tl();
		try{
			s=R(sql,p);
			try{
				tl.getOut() .o(s); // (new Json.Output()) // TODO:investigate where the Json.Output.w goes
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		finally
		{close(s,tl);
			if(tl.h.logOut)try{
				tl.log(tl.jo().w(SrvltName).w(".Sql.L:q2json=")
					 .o(sql).w(",prms=").o(p).toStrin_());
			}catch(IOException x){
				tl.error(x,SrvltName,".Sql.q1json:",sql);
			}
		}
	}
	/**return a list of maps , each map has as a key a string the name of the column, and value obj*/
	static List<Map<String,Object>>json(String sql,Object...p) throws SQLException{return Lst(sql,p);}
	static List<Map<String,Object>>Lst(String sql,Object[ ]p) throws SQLException{
		List<Map<String,Object>>l=new LinkedList< Map < String ,Object>>();ItTbl i=new ItTbl(sql,p);
		List<String>cols=new LinkedList<String>();
		for(int j=1;j<=i.row.cc;j++)cols.add(i.row.m.getColumnLabel(j));
		for(ItTbl.ItRow w:i){Map<String,Object>m= new HashMap<String,Object>();l.add(m);
			for(Object o:w)m.put(cols.get(w.col-1),o);
		}return l;}
	public static class ItTbl implements Iterator<ItTbl.ItRow>,Iterable<ItTbl.ItRow>{
		public ItRow row=new ItRow();
		public ItRow getRow(){return row;}
		public static ItTbl it(String sql,Object...p){return new ItTbl(sql,p);}
		public ItTbl(String sql,Object[]p){
			try {init(Sql.R(sql, p));}
			catch (Exception e) {
				TL.tl().logo(SrvltName,".Sql.ItTbl.<init>:Exception:sql=",sql,",p=",p," :",e);
			}}
		public ItTbl(ResultSet o) throws SQLException{init(o);}
		public ItTbl init(ResultSet o) throws SQLException {
			row.rs=o;row.m=o.getMetaData();row.row=row.col=0;
			row.cc=row.m.getColumnCount();return this;}
		static final String ErrorsList=SrvltName+".Sql.ItTbl.errors";
		@Override public boolean hasNext(){
			boolean b=false;try {if(b=row!=null&&row.rs!=null&&row.rs.next())row.row++;
			else Sql.close(row.rs);//CHANGED:2015.10.23.16.06:closeRS ; 2017.7.17
			}catch (SQLException e) {//e.printStackTrace();
				TL t=TL.tl();//changed 2016.06.27 18:05
				final String str=SrvltName+".Sql.ItTbl.next";
				t.error(e,str);
				List l=(List)t.json.get(ErrorsList);//t.response
				if(l==null)t.json.put(ErrorsList,l=new LinkedList());//t.response
				l.add(Util.lst(str,row!=null?row.row:-1,e));
			}return b;}
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
				catch (SQLException e) {//changed 2016.06.27 18:05
					TL t=TL.tl();
					final String str=SrvltName+".Sql.ItTbl.ItRow.next";
					t.error(e,str);
					List l=(List)t.json.get(ErrorsList);//t.response
					if(l==null)t.json.put(ErrorsList,l=new LinkedList());//t.response
					l.add(Util.lst(str,row,col,e));
				}//.printStackTrace();}
				return null;}
			@Override public void remove(){throw new UnsupportedOperationException();}
			public int nextInt(){
				try {return rs==null?-1:rs.getInt(++col);}
				catch (SQLException e) {
					e.printStackTrace();
				}
				return -1;}
			public String nextStr(){
				try {return rs==null?null:rs.getString(++col);}
				catch (SQLException e) {
					e.printStackTrace();
				}
				return null;}
		}//ItRow
	}//ItTbl
	/**represents one entity , one row from a table in a relational database*/
	public abstract static class Tbl implements Json.Output.JsonOutput {//<PK>
		// /**encapsulating Html-form fields, use annotation Form.F for defining/mapping member-variables to html-form-fields*/ public abstract static class Form{
		@Override public String toString(){return toJson();}

		/**get table name*/public abstract String getName();

		public Json.Output jsonOutput(Json.Output o,String ind,String path)throws java.io.IOException{return jsonOutput( o,ind,path,true );}
		public Json.Output jsonOutput(Json.Output o,String ind,String path,boolean closeBrace)throws java.io.IOException{
			//if(o.comment)o.w("{//TL.Form:").w('\n').p(ind);else//.w(p.getClass().toString())
			o.w('{');
			CI[]a=columns();//Field[]a=fields();
			String i2=ind+'\t';
			o.w("\"class\":").oStr(getClass().getSimpleName(),ind);//w("\"name\":").oStr(p.getName(),ind);
			for(CI f:a)try
			{	o.w(',').oStr(f.getName(),i2).w(':')
				.o(v(f),ind,o.comment?path+'.'+f.getName():path);
				if(o.comment)o.w("//").w(f.toString()).w("\n").p(i2);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if(closeBrace){
				if(o.comment)
					o.w("}//Sql.Tbl&cachePath=\"").p(path).w("\"\n").p(ind);
				else o.w('}');}
			return o; }

		public String toJson(){Json.Output o= TL.tl().jo().clrSW();try {jsonOutput(o, "", "");}catch (IOException ex) {}return o.toString();}

		public Tbl readReq(String prefix){
			TL t=TL.tl();CI[]a=columns();for(CI f:a){
				String s=t.h.req(prefix==null||prefix.length()<1?prefix+f:f.toString());
				Class <?>c=s==null?null:f.getType();
				Object v=null;try {
					if(s!=null)v=Util.parse(s,c);
					v(f,v);//f.set(this, v);
				}catch (Exception ex) {// IllegalArgumentException,IllegalAccessException
					t.error(ex,SrvltName,".Sql.Tbl.readReq:t=",this," ,field="
						,f+" ,c=",c," ,s=",s," ,v=",v);
				}}
			return this;}

		public abstract CI[]columns();//public abstract FI[]flds();

		public Object[]valsForSql(){
			CI[]a=columns();
			Object[]r=new Object[a.length];
			int i=-1;
			for(CI f:a){i++;
				r[i]=valForSql(a[i]);
			}return r;/*
		public Object[]_vals(){
			CI[]a=columns();//Field[]a=fields();
			Object[]r=new Object[a.length];
			int i=-1;
			for(CI f:a){i++;
				r[i]=v(a[i]);
			}return r;}*/}

		public Object valForSql(CI f){
			Object o=v(f);
			if(o instanceof Map)
				o=Json.Output.out( o );
			return o;}

		public Tbl vals (Object[]p){
			int i=-1;CI[]a=columns();//Field[]a=fields();
			for(CI f:a)
				v(f,p[++i]);
			return this;}

		public Map asMap(){ return asMap(null);}

		public Map asMap(Map r){
			CI[]a=columns();//Field[]a=fields();
			if(r==null)r=new HashMap();
			int i=-1;
			for(CI f:a){i++;
				r.put(f.getName(),v(a[i]));
			}return r;}

		public Tbl fromMap (Map p){
			CI[]a=columns();//Field[]a=fields();
			for(CI f:a){String n=f.getName();
				if(p.containsKey(n))
					v(f,p.get(n));}
			return this;}

		public Tbl v(CI p,Object v){return v(p.f(),v);}//this is beautiful(tear running down cheek)

		public Object v(CI p){return v(p.f());}//this is beautiful(tear running down cheek)

		Tbl v(Field p,Object v){//this is beautiful(tear running down cheek)
			try{Class <?>t=p.getType();
				if(v!=null && !t.isAssignableFrom( v.getClass() ))//t.isEnum()||t.isAssignableFrom(URL.class))
					v=Util.parse(v instanceof String?(String)v:String.valueOf(v),t);
				p.set(this,v);
			}catch (Exception ex) {
				TL.tl().error(ex,SrvltName,".Sql.Tbl.v(",this,",",p,",",v,")");
			}
			return this;}

		Object v(Field p){//this is beautiful(tear running down cheek)
			try{return p.get(this);}
			catch (Exception ex) {//IllegalArgumentException,IllegalAccessException
				TL.tl().error(ex,SrvltName,".Sql.Tbl.v(",this,",",p,")");return null;
			}}

		/**Field annotation to designate a java member for use in a dbTbl-column/field*/
		@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
		public @interface F{}

		/**Interface for enum-items from different forms and sql-tables ,
		 * the enum items represent a reference Column Fields for identifing the column and selection.*/
		public interface CI{public Field f();public String getName();public Class getType();}//interface I

//}//public abstract static class Form

		/**Sql-Column Interface, for enum -items that represent columns in sql-tables
		 * the purpose of creating this interface is to centerlize
		 * the definition of the names of columns in java source code*/

		public abstract Object[]wherePK();//{Object[]c=pkcols(),v=pkvals(),a=new Object[c.length+v.length];for(int i=0;i<c.length;i++){a[i*2]=c[i];a[i*2+1]=v[i];}return a;}

		public static CI[]cols(CI...p){return p;}
		public static Object[]where(Object...p){return p;}
		//public abstract CI pkc(int i);public abstract CI[]pkcols();public abstract int pkcn();
		//public abstract PK pkv(int i);public abstract PK[]pkvals();
		//public abstract PK[]pkv(PK[]v);
		//public PK[]pka(PK...p){return p;}//static

		public String sql(CI[]cols,Object[]where){
			return sql(cols,where,null,null,getName());}

		public static String sql(CI[]cols,Object[]where,String name){
			return sql( cols, where,null,null,name);}//StringBuilder sql,

		public String sql(CI[]cols,Object[]where,CI[]groupBy){
			return sql(cols,where,groupBy,null,getName());}

		public String sql(String cols,Object[]where,CI[]groupBy,CI[]orderBy) {
			StringBuilder sql=new StringBuilder("select ");
			sql.append(cols);//Co.generate(sql,cols);
			sql.append(" from `").append(getName()).append("` ");
			if(where!=null&&where.length>0)
				Co.where(sql, where);
			if(groupBy!=null && groupBy.length>0){
				sql.append(" group by ");
				Co.generate(sql,groupBy);}
			if(orderBy!=null && orderBy.length>0){
				sql.append(" order by ");
				Co.generate(sql,orderBy);}
			return sql.toString();}

		public static String sql(CI[]cols,Object[]where,CI[]groupBy,CI[]orderBy,String dbtName){
			return sql(cols,where,groupBy,orderBy,dbtName,null);}

		public static String sql(CI[]cols,Object[]where,CI[]groupBy,CI[]orderBy,String dbtName,String dbn){
			//if(cols==null)cols=columns();
			StringBuilder sql=new StringBuilder("select ");
			Co.generate( sql,cols );//sql.append(cols);
			if(dbn==null)
				sql.append(" from `").append(dbtName).append("` ");
			else sql.append(" from `").append(dbn).append("`.`").append(dbtName).append("` ");
			if(where!=null&&where.length>0)
				Co.where(sql, where);
			if(groupBy!=null && groupBy.length>0){
				sql.append(" group by ");
				Co.generate(sql,groupBy);}
			if(orderBy!=null && orderBy.length>0){
				sql.append(" order by ");
				Co.generate(sql,orderBy);}
			return sql.toString();}
		/** returns a list of 3 lists,(only the first is mandatory ,the rest are optional)
		 * the 1st is a list for the db-table columns-CI
		 * the 2nd is a list for the db-table-key-indices
		 * the 3rd is a list for row insertion
		 * 4th element is a Class<Sql.Tbl> , a dependency that will be created before this table.
		 *
		 * the 1st list, the definition of the column is a string
		 * , e.i. varchar(255) not null
		 * or e.i. int(18) primary key auto_increment not null
		 * the 2nd list of the db-table key-indices(optional)
		 * each dbt-key-index can be a CI or a list , if a list
		 * each item has to be a List
		 * ,can start with a prefix, e.i. unique , or key`ix1`
		 * , the items of this list should be a CI
		 * ,	or the item can be a list that has as the 1st item the CI
		 * and the 2nd item the length of the index
		 * the third list is optional, for each item in this list
		 * is a list of values to be inserted into the created table
		 */
		public abstract List creationDBTIndices(TL tl);
		public void checkDBTCreation(TL tl){
			String dtn=getName();Object o=tl.h.a(SrvltName+":db:show tables");
			if(o==null)
				try {o= Sql.q1colList("show tables");
					tl.h.a(SrvltName+":db:show tables",o);
				} catch (SQLException ex) {
					tl.error(ex, SrvltName+".Sql.Tbl.checkTableCreation:check-pt1:",dtn);
				}
			List l=(List)o;
			try{if(o==null||(!l.contains( dtn )&&!l.contains( dtn.toLowerCase()))){
				List a=creationDBTIndices(tl),b=(List)a.get(0);
				if(a.size()>3){
					Class<Tbl> c=(Class)a.get( 3 );
					try{Tbl t=c.newInstance();
						t.checkDBTCreation( tl );
					}catch(Exception ex){
						ex.printStackTrace();
					}}
				StringBuilder sql= new StringBuilder("CREATE TABLE `").append(dtn).append("` (\n");
				CI[]ci=columns();int an,x=0;
				for(CI i:ci){
					if(x>0 )
						sql.append("\n,");
					sql.append('`').append(i).append('`')
						.append(String.valueOf(b.get(x)) );
					x++;}
				an=a.size();b=an>1?(List)a.get(1):b;
				if(an>1)for(Object bo:b)
				{sql.append("\n,");x=0;
					if(bo instanceof CI)
						sql.append("KEY(`").append(bo).append("`)");
					else if(bo instanceof List)
					{	List bl=(List)bo;x=0;boolean keyHeadFromList=false;
						for(Object c:bl){
							boolean s=c instanceof String;
							if(x<1 && !s&& !keyHeadFromList)
								sql.append("KEY(");
							if(x>0)
								sql.append(',');//in the list
							if(s){sql.append((String)c);if(x==0){x--;keyHeadFromList=true;}}
							else {l=c instanceof List?(List)c:null;
								sql.append('`').append(
									l==null?String.valueOf(c)
										:String.valueOf(l.get(0))
								).append("`");
								if(l!=null&&l.size()>1)
									sql.append('(').append(l.get(1)).append(')');
							}x++;
						}sql.append(")");
					}else
						sql.append(bo);
				}
				sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8 ;");
				tl.log(SrvltName,".Sql.Tbl.checkTableCreation:before:sql=",sql);
				int r= Sql.x(sql.toString());
				tl.log(SrvltName,".Sql.Tbl.checkTableCreation:executedSql:",dtn,":returnValue=",r);
				b=an>2?(List)a.get(2):b;if(an>2)
					for(Object bo:b){
						List c=(List)bo;
						Object[]p=new Object[c.size()];
						c.toArray(p);
						vals(p);
						try {save();} catch (Exception ex) {
							tl.error(ex, SrvltName,".Sql.Tbl.checkTableCreation:insertion",c);
						} } } } catch (SQLException ex) {
				tl.error(ex, SrvltName,".Sql.Tbl.checkTableCreation:errMain:",dtn);
			}
		}//checkTableCreation
		/**where[]={col-name , param}*/
		public int count(Object[]where) throws Exception{return count(where,null,getName());}
		public static int count(Object[]where,CI[]groupBy,String name) throws Exception{
			String sql=sql(cols(Co.count),where,groupBy,null,name);//new StringBuilder("select count(*) from `").append(getName()).append("` where `").append(where[0]).append("`=").append(Co.m(where[0]).txt);//where[0]instanceof CI?m((CI)where[0]):'?');
			return Sql.q1int(sql,-1,where[0],where[1]);}
		public int maxPlus1(CI col) throws Exception{
			String sql=sql("max(`"+col+"`)+1",null,null,null);
			return Sql.q1int(sql,1);}
		public static int maxPlus1(CI col,String dbtn) throws Exception{
			String sql="SELECT max(`"+col+"`)+1 from `"+dbtn+"`";
			return Sql.q1int(sql,1);}
		// /**returns one object from the db-query*/ /**where[]={col-name , param}*/public Object obj(CI col,Object[]where) throws Exception{return Sql.q1Obj(sql(cols(col),where),where);}
		/**returns one string*/
		public String select(CI col,Object[]where) throws Exception{
			String sql=sql(cols(col),where);
			return Sql.q1Str(sql,where);}
		// /**returns one column, where:array of two elements:1st is column param, 2nd value of param*/Object[]column(CI col,Object...where) throws Exception{ return Sql.q1col(sql(cols(col),where),where[0],where[1]);}//at
		/**returns a table*/
		public Object[][]select(CI[]col,Object[]where)throws Exception{
			return Sql.Q(sql(col,where), where);}
		/**loads one row from the table*/
		Tbl load(ResultSet rs)throws Exception{return load(rs,columns());}
		/**loads one row from the table*/
		Tbl load(ResultSet rs,CI[]a)throws Exception{
			int c=0;for(CI f:a)if(f.getType().isAssignableFrom( Map.class ))
					v(f,Json.Prsr.parse( rs.getCharacterStream(++c)));
				else v(f,rs.getObject(++c));
			return this;}

		/**loads one row from the table*/
		public Tbl load(){/*
			ResultSet r=null;TL t=TL.tl();Object[]pk=wherePK();
			try{r=Sql.r(sql(cols(Co.all),pk),pk);
				if(r.next())load(r);
				else{t.error(null,SrvltName,".Sql.Tbl(",this,").load(pk=",pk,"):resultset.next=false");nullify();}}
			catch(Exception x){
				t.error(x,SrvltName,".Sql.Tbl(",this,"):",pk);
			}
			finally{Sql.close(r,t);}
			return this;*/return loadWhere(wherePK());}

		public Tbl nullify(){return nullify(columns());}
		public Tbl nullify(CI[]a){for(CI f:a)v(f,null);return this;}
		// /**loads one row from the table*/ Tbl load(){return load(pkv());}

		/**loads one row using column CI c */
		Tbl loadBy(CI c,Object v){
			try{Object[]a= Sql.q1row(sql(cols(Co.all),where(c)),v);
				vals(a);}
			catch(Exception x){
				TL.tl().error(x,SrvltName,".Sql.Tbl(",this,").loadBy(",c,",",v,")");
			}
			return this;}//loadBy

		/**loads one row based on the where clause */
		Tbl loadWhere(Object[]where){
			ResultSet rs=null;
			try{rs= Sql.R( sql(cols(Co.all),where),where );
				if(rs.next()){
					load(rs);
					return this;
				}
			}
			catch(Exception x){
				TL.tl().error(x,SrvltName,".Sql.Tbl(",this,").loadWhere(",where,")");
			}finally {
				close(rs);
			}return null;}//loadBy

		/**loads one row based on the where clause */
		public static Tbl loadWhere(Class<? extends Tbl>c,Object[]where){
			Tbl t=null;
			try{t=c.newInstance().loadWhere( where );
			}catch(Exception x){
				TL.tl().error(x,SrvltName,".Sql.Tbl(",t,").loadWhere(",c,",",where,")");
			}
			return t;}//loadBy

		/**store this entity in the dbt */
		public Tbl create() throws Exception{
			CI[] cols = columns();
			StringBuilder sql = new StringBuilder( "insert into`" ).append( getName() ).append( "`( " );
			Co.generate( sql, cols );//.toString();
			sql.append( ")values(" ).append( Co.prm.txt );//Co.m(cols[0]).txt
			for ( int i = 1; i < cols.length; i++ )
				sql.append( "," ).append( Co.prm.txt );//Co.m(cols[i]).txt
			sql.append( ")" );//int x=
			Sql.X( sql.toString(), valsForSql() );
			TL.tl().log( "create", this );//log(nw?Sql.Tbl.Log.Act.New:Sql.Tbl.Log.Act.Update);
			return this;}//save


		//public Tbl update(CI...c) throws Exception{return update(c);}

		/**store this entity in the dbt , if pkv is null , this method uses the max+1 of pk-col*/
		public Tbl update(CI[]c) throws Exception{
			StringBuilder sql = new StringBuilder( "update`" )
				.append( getName() ).append( "` set `" )
				.append( c[0]).append( "`=?" );
			Object[]p=wherePK(),a=new Object[c.length+p.length/2];
			for(CI x:c)
				if(x==c[0])sql.append( " , `" ).append( x ).append( "`=?" );
			//for()

			Sql.X( sql.toString(), valsForSql() );
			TL.tl().log( "update", this );//log(nw?Sql.Tbl.Log.Act.New:Sql.Tbl.Log.Act.Update);
			return this;}//save

		/**store this entity in the dbt , if pkv is null , this method uses the max+1 of pk-col*/
		public Tbl save() throws Exception{
			CI[] cols = columns();
			StringBuilder sql = new StringBuilder( "replace into`" ).append( getName() ).append( "`( " );
			Co.generate( sql, cols );//.toString();
			sql.append( ")values(" ).append( Co.prm.txt );//Co.m(cols[0]).txt
			for ( int i = 1; i < cols.length; i++ )
				sql.append( "," ).append( Co.prm.txt );//Co.m(cols[i]).txt
			sql.append( ")" );//int x=
			Sql.X( sql.toString(), valsForSql() );
			TL.tl().log( "save", this );//log(nw?Sql.Tbl.Log.Act.New:Sql.Tbl.Log.Act.Update);
			return this;}//save

		//void log(Sql.Tbl.Log.Act act){	Map val=asMap();Integer k=(Integer)pkv();Sql.Tbl.Log.log( Sql.Tbl.Log.Entity.valueOf(getName()), k, act, val);}
		public int delete() throws SQLException{
			int x=-1;Object[]where=wherePK();
			StringBuilder b=new StringBuilder( "delete from `" )
				.append( getName() ).append("`" );
			Co.where( b,where );
			x= Sql.X( b.toString(),where );
			return x;}

		/**retrieve from the db table all the rows that match
		* the conditions in < where > , create an iterator
		* , e.g.<code>for(Tbl row:query(
		* 		Tbl.where( CI , < val > ) ))</code>*/
		public Itrtr query(Object[]where){
			Itrtr r=new Itrtr(where);
			return r;}
		public Itrtr query(String sql,Object[]where,boolean makeClones){
			return new Itrtr(sql,where,makeClones);}
		public Itrtr query(Object[]where,boolean makeClones){return query(columns(),where,null,makeClones);}
		public Itrtr query(CI[]cols,Object[]where,CI[]groupBy,boolean makeClones){//return query(sql(cols,where,groupBy),where,makeClones);}//public Itrtr query(String sql,Object[]where,boolean makeClones){
			Itrtr r=new Itrtr(sql(cols,where,groupBy),where,makeClones);
			return r;}
		public class Itrtr implements Iterator<Tbl>,Iterable<Tbl>{
			public ResultSet rs=null;public int i=0;CI[]a;boolean makeClones=false;
			public Itrtr(String sql,Object[]where,boolean makeClones){
				this.makeClones=makeClones;a=columns();
				try{rs= Sql.R(sql, where);}
				catch(Exception x){
					TL.tl().error(x,SrvltName,".Sql.Tbl(",this,").Itrtr.<init>:where=",where);
				}
			}
			public Itrtr(Object[]where){a=columns();
				try{rs= Sql.R(sql(cols(Co.all),where), where);}
				catch(Exception x){
					TL.tl().error(x,SrvltName,".Sql.Tbl(",this,").Itrtr.<init>:where=",where);
				}}
			@Override public Iterator<Tbl>iterator(){return this;}
			@Override public boolean hasNext(){boolean b=false;
				try {b = rs!=null&&rs.next();} catch (SQLException x) {
					TL.tl().error(x,SrvltName,".Sql.Tbl(",this,").Itrtr.hasNext:i=",i,",rs=",rs);
				}
				if(!b&&rs!=null){
					Sql.close(rs);rs=null;}
				return b;}
			@Override public Tbl next(){i++;Tbl t=Tbl.this;TL tl=TL.tl();
				if(makeClones)try{
					t=t.getClass().newInstance();}catch(Exception ex){
					tl.error(ex,SrvltName,".Sql.Tbl(",this,").Itrtr.next:i=",i,":",rs,":makeClones");
				}
				try{t.load(rs,a);}catch(Exception x){
					tl.error(x,SrvltName,".Sql.Tbl(",this,").Itrtr.next:i=",i,":",rs);
					close(rs,tl);rs=null;
				}
				return t;}
			@Override public void remove(){throw new UnsupportedOperationException();}
		}//Itrtr
		/**Class for Utility methods on set-of-columns, opposed to operations on a single column*/
		public enum Co implements CI {//Marker ,sql-preparedStatement-parameter
			all("*")
			,prm("?")
			,Null("null")
			,now("now()")
			,uuid("uuid()")
			,count("count(*)")
			,distinct("distinct")
			,password("password(?)")
			,lt("<"),le("<="),ne("<>"),gt(">"),ge(">=")
			,or("or"),like("like"),in("in"),and("and")//,prnthss("("),max("max(?)")
			;String txt;
			Co(String p){txt=p;}
			@Override public Field f(){return null;}
			@Override public String getName(){return name();}
			@Override public Class getType(){return String.class;}
			public static Field f(String name,Class<? extends Tbl>c){
				//for(Field f:fields(c))if(name.equals(f.getName()))return f;return null;
				Field r=null;try{r=c.getField(name);}catch(Exception x) {
					TL.tl().error(x,SrvltName,".Sql.Tbl.f(",name,c,"):");
				}
				return r;}

			/**generate Sql into the StringBuilder*/
			public static StringBuilder generate(StringBuilder b,CI[]col){ return generate(b,col,",");}

			static StringBuilder generate(StringBuilder b,CI[]col,String separator){
				if(separator==null)separator=",";
				for(int n=col.length,i=0;i<n;i++){
					if(i>0)b.append(separator);
					if(col[i] instanceof Co)
					{	b.append(((Co)col[i]).txt);
						if(col[i] ==Co.distinct && i+1<n)
							b.append(" `").append(col[++i]).append("`");
					}else
						b.append("`").append(col[i]).append("`");}
				return b;}

			public static StringBuilder genList(StringBuilder b,List l){
				b.append(" (");boolean comma=false;
				for(Object z:l){
					if(comma)b.append( ',' );else comma=true;
					if(z instanceof Number)
						b.append( z );else
						b.append( '\'' ).append(
							(z instanceof String?(String)z:z.toString()
							).replaceAll( "'","''" )
						)
							.append( '\'' );
				}b.append(")");
				return b;}

			static StringBuilder where(StringBuilder b,Object[]where){
				if(where==null || where.length<1)return b;
				b.append(" where ");
				for(int n=where.length,i=0;i<n;i++){Object o=where[i];
					if(i>0)b.append(" and ");
					where(b,o,i+1<n ?where[i+1]:null);
					i++;
				}//for //where(b,Co.and,where);
				return b;}

			/**
			* in the case of Co.and and Co.or
			* the even-prm is Co.or or Co.and , and the odd-prm is a list
			* */
			static StringBuilder where(StringBuilder b,Object o,Object o1){
				if(o==null )return b;
				if((o==Co.and || o==Co.or )&& o1 instanceof List){
					List l=(List)o1;int c=0;b.append( '(' );
					for(Object e:l){
						if(c++>0)//b.append( " or " );
							b.append( ' ' ).append( o ).append( ' ' );
						where(b,e,c<l.size() ?l.get( c ):null);
					}b.append( ')' );
				}else
				if(o instanceof Co)b.append(o);else
				if(o instanceof CI)
					b.append('`').append(o).append("`=")
						.append('?');//Co.m(o).txt
				else if(o instanceof List){List l=(List)o;
					o=l.size()>1?l.get(1):null;
					if(o ==Co.in && o1 instanceof List){
						b.append('`').append(l.get(0)).append("` ").append(o);
						l=(List)o1;
						genList(b,l);
					}else if(o instanceof Co)//o!=null)//if(ln==2 && )
					{	Co m=(Co)o;o=l.get(0);
						if(o instanceof CI || o instanceof Co)
							b.append('`').append(o).append('`');
						else
							TL.tl().log(SrvltName,".Sql.Tbl.Co.where:unknown where-clause item:o=",o);
						b.append(m.txt).append("?");
					}else
						TL.tl().log(SrvltName,".Sql.Tbl.Co.where:unknown where-clause item: o=",o);
				}
				else TL.tl().error(null,SrvltName,".Sql.Tbl.Col.where:for:",o);
				return b;}
		}//enum Co

		/**output to jspOut one row of json of this row*/
		public void outputJson(){try{TL.tl().getOut().o(this);}catch(IOException x){
			TL.tl().error(x,"moh.Sql.Tbl.outputJson:IOEx:");
		}}
		/**output to jspOut rows of json that meet the 'where' conditions*/
		public void outputJson(Object...where){try{
			Json.Output o=TL.tl().getOut();
			o.w('[');boolean comma=false;
			for(Tbl i:query(where)){
				if(comma)o.w(',');else comma=true;
				i.outputJson();}
			o.w(']');
		} catch (IOException e){
			TL.tl().error(e,SrvltName,".Sql.Tbl.outputJson:");
		}
		}//outputJson(Object...where)
		public static List<Class<? extends Tbl>>registered=new LinkedList<Class<? extends Tbl>>();
		static void check(TL tl){
			for(Class<? extends Tbl>c:registered)try
			{String n=c.getName(),n2=SrvltName+".checkDBTCreation."+n;
				if( tl.h.a(n2)==null){
					Tbl t=c.newInstance();
					t.checkDBTCreation(tl);
					tl.h.a(n2,tl.now);
				}}catch(Exception ex){
				tl.error( ex,SrvltName,".Sql.Tbl.check" );
			} }

		public static boolean exists(Object[]where,String dbtName){return exists(where,null,dbtName);}

		public static boolean exists(Object[]where,CI[]groupBy,String dbtName){
			boolean b=false;
			int n=0;
			try{n=count( where,groupBy,dbtName );}catch ( Exception ex ){}
			b=n>0;
			return b;
		}
	}//class Tbl
}//class Sql

static class Json{
	public static class Output
	{ public interface JsonOutput{ public Json.Output jsonOutput( Json.Output o, String ind, String path ) throws IOException ;}

		public Writer w;
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
			else if(a instanceof JsonOutput)((JsonOutput)a).jsonOutput(this,ind,path);//oDbTbl((Sql.Tbl)a,ind,path);
				//else if(a instanceof Sql.Tbl)((Sql.Tbl)a).jsonOutput(this,ind,path);//oDbTbl((Sql.Tbl)a,ind,path);
			else if(a instanceof Map<?,?>)oMap((Map)a,ind,path);
			else if(a instanceof Collection<?>)oCollctn((Collection)a,ind,path);
			else if(a instanceof Object[])oArray((Object[])a,ind,path);
			else if(a.getClass().isArray())oarray(a,ind,path);
			else if(a instanceof java.util.Date)oDt((java.util.Date)a,ind);
			else if(a instanceof Iterator<?>)oItrtr((Iterator)a,ind,path);
			else if(a instanceof Enumeration<?>)oEnumrtn((Enumeration)a,ind,path);
			else if(a instanceof Throwable)oThrbl((Throwable)a,ind);
			else if(a instanceof ResultSet)oResultSet(( ResultSet)a,ind,path);
			else if(a instanceof ResultSetMetaData)oResultSetMetaData((ResultSetMetaData)a,ind,path);
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
			//if(o instanceof Store.JsonStorage)w("uuid:").o(((Store.JsonStorage)o).uuid);
			if(e.hasNext()){k=e.next();v=o.get(k);//if(o instanceof Store.JsonStorage)w(",");
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
				//.w(",\"ssn\":").o(y.ssn,i2,c?path+".ssn":path)//.w(",sid:").o(y.sid,i2,c?path+".sid":path)
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
				if(n.startsWith("get")&&m.getParameterTypes().length==0)//.getParameterCount()
					w("\n").w(i2).w(",").p(n).w(':').o(m.invoke(o), i3, path+'.'+n);}
			if(c)w("}//").p(o.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);
			else w("}");}catch(Exception ex){TL.tl().error(ex,"Json.Output.Bean:");}return this;}
		Output oResultSet(ResultSet o,String ind,String path)
		{final boolean c=comment;try{String i2=c?ind+"\t":ind;
			Sql.ItTbl it=new Sql.ItTbl(o);
			(c?w("{//").p(o.getClass().getName()).w(":ResultSet\n").p(ind):w("{"))
				.w("\"h\":").oResultSetMetaData(it.row.m,i2,c?path+".h":path)
				.w("\n").p(ind).w(",\"a\":").o(it,i2,c?path+".a":path);
			if(c)w("}//").p(o.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);
			else w("}");}catch(Exception ex){TL.tl().error(ex,"Json.Output.ResultSet:");}return this;}
		Output oResultSetMetaData(ResultSetMetaData o,String ind,String path)
		{final boolean c=comment;try{String i2=c?ind+"\t":ind;
			int cc=o.getColumnCount();
			if(c)w("[//").p(o.getClass().getName()).w(":ResultSetMetaData\n").p(ind);
			else w("[");
			for(int i=1;i<=cc;i++){
				if(i>1){if(c)w("\n").p(i2).w(",");else w(",");}
				w("{\"name\":").oStr(o.getColumnName( i ),i2)
					.w(",\"label\":").oStr(o.getColumnLabel( i ),i2)
					.w(",\"width\":").p(o.getColumnDisplaySize( i ))
					.w(",\"className\":").oStr(o.getColumnClassName( i ),i2)
					.w(",\"type\":").oStr(o.getColumnTypeName( i ),i2).w("}");
			}//for i<=cc
			if(c)w("]//").p(o.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);
			else w("]");}catch(Exception ex){TL.tl().error(ex,"Json.Output.ResultSetMetaData:");}return this;}
		public Output clrSW(){if(w instanceof StringWriter){((StringWriter)w).getBuffer().setLength(0);}return this;}
		public Output flush() throws IOException{w.flush();return this;}
	} //class Output


	public static class Prsr {

		public StringBuilder buff=new StringBuilder() ,lookahead=new StringBuilder();
		public Reader rdr;

		public String comments=null;
		public char c;Map<String,Object>cache=null;

		enum Literal{Undefined,Null};//,False,True

		public static Object parse(String p)throws Exception{
			return parse(new java.io.StringReader(p));}

		public static Object parse(HttpServletRequest p)throws Exception{
			return parse(p.getReader());}

		public static Object parse(Reader p)throws Exception{
			Prsr j=new Prsr();j.rdr=p;j.nxt(j.c=j.read());return j.parse();}//public static Object parseItem(Reader p)throws Exception{ Prsr j=new Prsr();j.rdr=p;j.nxt(j.c=j.read());return j.parseItem();}

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
					b=false;nxt();
				}
			}while(b);
		}// boolean chk(){boolean b=Character.isWhitespace(c)||c=='/';while(b && c!='\0'){//Character.isWhitespace(c)||)char x=peek();if(c=='/' &&(lookahead("//") || lookahead("/*"))){	skipWhiteSpace();b=Character.isWhitespace(c);}else if(x=='/' &&(lookahead(x+"//") || lookahead(x+"/*") )){}else{	if(b=Character.isWhitespace(x))nxt();}}return false;}

		public Object parse()throws Exception{
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
				case '(':nxt();
				{
					skipRWS();//skipWhiteSpace();
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
					java.lang.Integer.parseInt(
						next(2)//p.substring(offset,offset+2)
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
					 :"null".equals(r)?Literal.Null
						:"undefined".equals(r)?Literal.Undefined
							:r;}

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
			catch(Exception ex){TL.tl().error(ex, "TL.Json.Prsr.read");}
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

}//TxtSrvlt
