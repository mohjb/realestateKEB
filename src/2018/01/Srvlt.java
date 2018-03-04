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
import dev201801.TL.Json;

public class Srvlt extends javax.servlet.http.HttpServlet {

/**
 * Created by Vaio-PC on 2/23/2018.
 * Created by Vaio-PC on 1/26/2018.
 * Created by Vaio-PC on 18/01/2018.
 *Created by moh on 14/7/17.*/

static final String packageName= "dev201801",SrvltName=packageName+".Srvlt",UrlPrefix="";

static Map<String,Method>mth=new HashMap<String,Method>();

static void staticInit(){
	registerMethods( Srvlt.class);
	registerMethods( Stor .class);
	registerMethods( Perm .class);
	if(!DB.Tbl.registered.contains(Stor.class))DB.Tbl.registered.add(Stor.class);
	if(!DB.Tbl.registered.contains(Perm.class))DB.Tbl.registered.add(Perm.class); }

static{staticInit();}

/**clientOutput
 * in Stor-table the main app-entery has key="app",
 * and in this entry there is a javaObjectStream of a Map
 * and in this map there are the keys: serverFiles,clientFiles,dbts
 * with clientFiles is associated a List, having maps, each map
 * may have key "content" text , and , the map may have key "include" list
 * */
@HttpMethod
public void get( @HttpMethod(prmLoadByUrl = true)Stor page){

}

@HttpMethod
public Stor login( @HttpMethod(prmLoadByUrl = true)Stor j
	, @HttpMethod(prmName = "pw")String pw, TL tl){
	if(j!=null&&j.typ==Stor.ContentType.usr&&j.val instanceof Map )
	{Map m=(Map)j.val;Object o=m.get("pw");
		if(pw!=null&&o instanceof String)
		{o=Util.md5( (String)o );
			if(pw.equals( o )){
				tl.h.s("usr",tl.usr=j);
				return j;
			}}}
	return null; }

@HttpMethod
public boolean logout( @HttpMethod(prmUrlPart = true)String app
	, @HttpMethod(prmUrlRemaining = true)String usr, TL tl){
	if(tl!=null&&tl.usr!=null&&tl.usr.key.equals( usr )){
		tl.h.s("usr",tl.usr=null);
		tl.h.getSession().setMaxInactiveInterval( 1 );
		return true;
	}
	return false;}

/**
 *	db-tbl ORM wrapper
 *pk is app+key
 */
public static class Stor extends DB.Tbl</**primary key type*/String> {
	public static final String dbtName="Stor";
	@Override public String getName(){return dbtName;}
	@Override public int pkcn(){return 2;}
	@Override public CI pkc(int i){return i==0?C.app:C.key;}
	@Override public CI[]pkcols(){C[]a={C.app,C.key};return a;}
	public static enum ContentType{txt,json,key,num,real,date, bytes, serverSideJs,usr,javaObjectStream;}
	@F public String app,key;@F public ContentType typ;@F public Object val;@F public Date logTime,lastModified;

	@Override public String pkv(int i){return i==0?app:key;}
	@Override public String[]pkv(String[]v){app=v[0];key=v[1];return v;}
	@Override public String[]pkvals(){String[]a={app,key};return a;}
	public Stor(){}
	public Stor(String appName){this(appName,"keysList");}
	public Stor(String appName,String key){app=appName;this.key=key;}
	public Stor(String appName,String key,ContentType c,Object v){this(appName,key);typ=c;val=v;}
	@Override public Object[]wherePK(){
		Object[]a={C.app,app,C.key,key};
		return a;}

	public enum C implements CI{app,key,typ, val, logTime,lastModified;
		@Override public Field f(){return Co.f(name(), Stor.class);}
		@Override public String getName(){return name();}
		@Override public Class getType(){return String.class;}
	}//C
	@Override public C[]columns(){return C.values();}

	@Override public List creationDBTIndices(TL tl){
		return Util.lst(Util.lst(
			"varchar(255) NOT NULL DEFAULT 'home' "//app \u1F3E0 ??
			,"varchar(255) NOT NULL DEFAULT 'home' "//key
			,"enum('txt','json','key','num','real','date','bytes','serverSideJs','usr','javaObjectStream') NOT NULL DEFAULT 'txt' "//typ
			,"blob"//val
			,"timestamp onupdate current_timestamp default current_timestamp"//logTime
			,"timestamp"//lastModified
			),Util.lst("unique(`app`,`key`)")
		);//val
			/*
			CREATE TABLE `Stor` (
			`app` varchar(255) NOT NULL DEFAULT '??',
			`key` varchar(255) NOT NULL DEFAULT '??',
			`typ` enum('txt','json','key','num','real','date','bytes','serverSideJs','usr','javaObjectStream') NOT NULL DEFAULT 'txt',
			`val` blob ,
			`logTime` timestamp onupdate current_timestamp default current_timestamp,
			`lastModified` timestamp,
			unique(`app`,`key`),
			key(`lastModified`,`logTime`),
			key(`logTime`,`lastModified`)
			) ENGINE=InnoDB DEFAULT CHARSET=utf8;
??  lock with ink pen Unicode code point: U+1F50F
??  closed lock with key Unicode code point: U+1F510
??  key Unicode code point: U+1F511
??  lock Unicode code point: U+1F512
??  open lock Unicode code point: U+1F513
			*/
	}

	static{registered.add(Stor.class);}

	public static Stor loadBy(String app,String key){
		Stor j=(Stor)loadWhere(Stor.class,where( C.app,app,C.key,key ));
		return j;}

	public static Stor prmLoadByUrl(TL tl,String url){
		int i=url.indexOf('/');
		String app=url.substring(0,i),key=url.substring(i+1);
		Stor j=loadBy( app,key );
		return j;}

	//Perm perm(TL tl){Perm p=Perm.loadBy(app,key,tl.usr==null?null:tl.usr.key);return p;}

	Perm perm(TL tl,Perm.Act a)throws Perm.Exceptn{
		Perm p=Perm.loadBy(app,key,tl.usr==null?null:tl.usr.key);
		if(p==null||!p.has(a))
			throw new Perm.Exceptn(tl,this,a,p);
		return p;}

	Perm perm(Perm.Act a)throws Perm.Exceptn{return perm(TL.tl(),a);}

	@HttpMethod public static List<String>
	listApps() throws Exception {//Perm p=perm
		return DB.q1colTList(
			sql(cols( Co.distinct,C.app ),null,dbtName)
			,String.class );}

	@HttpMethod public static Map//List<String>
	listKeys( @HttpMethod(prmUrlPart = true)String appName, TL tl)throws Exception{
		Stor j=new Stor(appName);
		Map m=Util.mapCreate();
		for(DB.Tbl t:j.query(j.genSql(Perm.Act.get,null,null).toString()
			,where(C.key,tl!=null&&tl.usr!=null?tl.usr.key:null
				,Perm.C.act,Perm.Act.get),false))
		{String s=j.typ.toString();
			List n=(List)m.get(s);
			if(n==null)
				m.put(s,n=Util.lst());
			n.add(j.key);}
		return m; }

	@HttpMethod public static List
	poll( @HttpMethod(prmUrlPart = true)String appName, @HttpMethod(prmBody = true)long logTime, TL tl)throws Exception{
		Stor j=new Stor(appName);
		List l=Util.lst(  );
		for(DB.Tbl t:j.query(j.genSql(Perm.Act.get,null,logTime).toString()
			,where(C.key,tl!=null&&tl.usr!=null?tl.usr.key:null
				,Perm.C.act,Perm.Act.get,C.logTime,logTime),true))
			l.add( t );
		return l;}

	StringBuilder genSql( Perm.Act a, List keysIn,Long logTime_poll){
		StringBuilder sql = new StringBuilder("select ");
		DB.Tbl.Co.generate(sql, columns());
		sql.append(" from `").append(dbtName)
			.append("`,`").append(Perm.dbtName)
			.append("` where `").append(dbtName).append("`.`").append(C.app)
			.append("`=`").append(Perm.dbtName).append("`.`").append(C.app)
			.append("` and `").append(Perm.dbtName).append("`.`").append(Perm.C.key);
		if(keysIn == null)
			sql.append("`=`").append(dbtName).append("`.`").append(C.key).append('`');
		else {
			sql.append("` in");
			DB.Tbl.Co.genList(sql, keysIn);
		}
		sql.append(" and `").append(Perm.dbtName).append("`.`")
			.append(Perm.C.usr).append("`=? and `").append(Perm.dbtName).append("`.`")
			.append(Perm.C.act).append("`&")
			.append(Math.floor(Math.pow(2,1+a.ordinal( ))));
		if(logTime_poll!=null)
			sql.append( " and `"+C.logTime+"`>=?" );
		return sql;}

	@HttpMethod public static Stor
	get(@HttpMethod(prmLoadByUrl=true)Stor j)throws Perm.Exceptn {
		Perm p=j.perm(Perm.Act.get);
		return j;}

	@HttpMethod public static List<Stor>
	getKeys(@HttpMethod(prmUrlPart = true)String appName,
	    @HttpMethod(prmBody = true)List<String>keys)throws Perm.Exceptn{
		List<Stor>l=new LinkedList<>(  );
		Stor j=new Stor(appName);//Perm p=j.perm(Perm.Act.getKeys);
		for(DB.Tbl t:j.query( j.genSql(Perm.Act.get,keys,null).toString(),null,true))
			l.add( (Stor ) t );
		return l;}

	@HttpMethod public static Stor
	store( @HttpMethod(prmBody = true)Stor j , TL tl)throws Exception{
	if(j!=null){Perm p=j.perm(tl,Perm.Act.set);
		if(j.typ==ContentType.usr&&j.val instanceof Map){
			Map m=(Map)j.val;Object o=m.get("pw");
			if(o instanceof String){o=Util.md5( (String)o );
				m.put("pw",o);}
		}//do md5 of pw
		j.save();
		if(j.typ==ContentType.serverSideJs ){
			javax.script.ScriptEngine e =eng(j.app,false,tl);
			if(e!=null){
				Object o=e.eval( j.val.toString() );//engine.put( key ,o);
				Map jm=(Map)e.get( "JsonStorageApp");
				jm.put( j.key,o );//List jl=(List)e.get( "JsonStorageApp.JsonStorages");jl.add( j );
			} } }return j;
	/*
	@HttpMethod
	public static Stor
	set( @HttpMethod(prmUrlPart = true)String appName
		, @HttpMethod(prmUrlPart = true)String key
		, @HttpMethod(prmHeader="typ")ContentType typ
		, @HttpMethod(prmBody = true)Object val
		, TL tl)throws Exception
	{	Stor j=new Stor(appName,key,typ,val);
		return store(j,tl);}

	mysql> insert into tst values
	(1,'a'),(0,'')
	,(2,'b'),(3,'a,b')
	,(4,'c'),(5,'a,c'),(6,'b,c'),(7,'a,b,c')
	,(8,'d'),(9,'a,d'),(10,'b,d'),(11,'b,d,a'),(12,'c,d'),(13,'c,d,a'),(14,'c,d,b'),(15,'a,b,c,d')
	,(16,'e'),(17,'a,e'),(18,'e,b'),(19,'e,b,a'),(20,'c,e'),(21,'a,c,e'),(22,'b,c,e'),(23,'e,a,b,c')
	,(24,'d,e'),(25,'a,d,e'),(26,'b,d,e'),(27,'b,d,a,e'),(28,'c,d,e'),(29,'c,d,a,e'),(30,'c,d,b,e'),(31,'a,b,c,d,e')
	,(32,'f'),(33,'a,f'),(34,'b,f'),(35,'a,b,f'),(36,'c,f'),(37,'a,c,f'),(38,'b,c,f'),(39,'a,b,c,f')
	,(40,'d,f'),(41,'a,d,f'),(42,'b,d,f'),(43,'b,d,a,f'),(44,'c,d,f'),(45,'c,d,a,f'),(46,'c,d,b,f'),(47,'a,b,c,d,f')
	,(48,'e,f'),(49,'a,e,f'),(50,'e,f,b'),(51,'e,f,b,a'),(52,'c,e,f'),(53,'a,c,e,f'),(54,'b,c,e,f'),(55,'e,f,a,b,c')
	,(56,'d,e,f'),(57,'a,d,e,f'),(58,'b,d,e,f'),(59,'b,d,a,e,f'),(60,'c,d,e,f'),(61,'c,d,a,e,f'),(62,'c,d,b,e,f')
	,(63,'a,b,c,d,e,f')
	*/}

	static javax.script.ScriptEngine eng(String appName,boolean createIfNotInit,TL tl){
		String en="ScriptEngine.JavaScript."+appName;
		javax.script.ScriptEngine e =(javax.script.ScriptEngine)tl.h.s( en );
		if(e==null&& createIfNotInit) {
			javax.script.ScriptEngineManager man=(javax.script.ScriptEngineManager)tl.h.a( "ScriptEngineManager" );
			if(man==null )
				tl.h.a( "ScriptEngineManager",man=new javax.script.ScriptEngineManager() );
			tl.h.s( en, e = man.getEngineByName( "JavaScript" ) );
			e.put( "tl",tl );
			Stor j=new Stor();
			Map jm=Util.mapCreate(  );//List jl=TL.Util.lst(  );
			for(DB.Tbl t:j.query( where(C.app,appName,C.typ	,ContentType.serverSideJs.toString()) ))
				try{jm.put( j.key,e.eval( j.val.toString() ));}catch ( Exception ex ){
					jm.put( j.key,Util.mapCreate("sourceCode", j.val,"eval_Exception",ex ) );}
			e.put( "JsonStorageApp",jm);//e.put( "JsonStorageApp.JsonStorages",jl);
		}else if(e!=null)
			e.put( "tl",tl);
		return e; }

	@HttpMethod public static Object
	call(@HttpMethod(prmUrlPart = true)String appName,
		@HttpMethod(prmUrlRemaining = true)String m,
		@HttpMethod(prmBody = true)List args,
		TL tl) throws Exception{// javax.script.ScriptException
		Stor j=loadBy(appName,m);
		Perm p=j.perm(tl,Perm.Act.call);
		javax.script.ScriptEngine e=eng(appName,true,tl);
		e.put( "member",m);
		if(args!=null)e.put(  "args",args);
		return e.eval( "JsonStorageApp[member]"+(args==null?"":".call(args)") ); }

	@HttpMethod public static Object
	eval( @HttpMethod(prmUrlPart = true)String appName,
		@HttpMethod(prmBody = true)String src,
		TL tl) throws Exception{// javax.script.ScriptException
		Stor j=loadBy(appName,"keysList");
		j.perm(Perm.Act.eval);
		javax.script.ScriptEngine e=eng(appName,true,tl);
		e.put( "src",src);
		return e.eval( src ); }

	/**loads one row from the table*/
	@Override DB.Tbl load(ResultSet rs,CI[]a)throws Exception{
		int c=0;for(CI f:a)if(f!=C.val )v(f,rs.getObject(++c));else
			switch ( typ ){
				case bytes:
					val =rs.getBytes( ++c );break;
				case date:
					val =rs.getDate( ++c );break;
				case num:
					val =rs.getLong( ++c );break;
				case real:
					val =rs.getDouble( ++c );break;
				case javaObjectStream:java.io.ObjectInputStream p=
					                      new ObjectInputStream( rs.getBinaryStream( ++c ) );
					val =p.readObject();break;
				case usr:case json: val =Json.Prsr.parseItem(rs.getCharacterStream( ++c ) );break;
				default://case txt: case key:
					val =rs.getString( ++c );break;
			}
		return this;}

	/**store this entity in the dbt , if pkv is null , this method uses the max+1 of pk-col*/
	@Override public DB.Tbl save() throws Exception{
		CI[] cols = columns();
		StringBuilder sql = new StringBuilder( "replace into`" ).append( getName() ).append( "`( " );
		Co.generate( sql, cols );//.toString();
		sql.append( ")values(" ).append( Co.prm.txt );//Co.m(cols[0]).txt
		for ( int i = 1; i < cols.length; i++ )
			sql.append( "," ).append( Co.prm.txt );//Co.m(cols[i]).txt
		sql.append( ")" );//int x=
		Object[] vals = vals();
		if(typ==ContentType.javaObjectStream) {
			//PreparedStatement ps=DB.P( sql.toString(),vals() ); ps.setBinaryStream( C.val.ordinal()+1,stream );
			ByteArrayOutputStream b=new ByteArrayOutputStream(  );
			ObjectOutputStream o=new ObjectOutputStream( b );
			o.writeObject( val );o.flush();o.close();b.close();
			vals[C.val.ordinal()]=b.toByteArray(); }
		else if(typ==ContentType.json||typ==ContentType.usr)
			vals[C.val.ordinal()]=Json.Output.out( val );
		vals[C.typ.ordinal()]=(typ==null?ContentType.txt:typ).toString();
		DB.X( sql.toString(), vals );
		TL.tl().log( "save", this );//log(nw?DB.Tbl.Log.Act.New:DB.Tbl.Log.Act.Update);
		return this;}//save


	@Override public Json.Output jsonOutput(Json.Output o,String ind,String path)throws IOException{
		o.w("{\"").w(C.app.name()).w("\":").oStr(app,ind)
			.w(",\"").w(C.key.name()).w("\":").oStr(key,ind)
			.w(",\"").w(C.typ.name()).w("\":");
		if(typ==null)o.w( "null" );
		else o.oStr(typ.toString(),ind);
		o.w( ",\"").w( C.logTime     .toString()).w("\":" ).p( logTime==null?null:logTime.getTime() )
		 .w( ",\"").w( C.lastModified.toString()).w("\":" ).p( lastModified==null?null:lastModified.getTime() );
		return o.w(",\"val\":").o( val,ind,path).w('}');}

}//class Stor

/** Many2Many Permissions for Stor
 *	db-Table Wrapper
 pk is app+key+usr
 */
public static class Perm extends DB.Tbl<String> {
	public Perm(){}
	//	public Perm(String app,String key,String usr,Act[]p){super();}
	public Perm(String app,String key,String usr,Act [] p){
		this.app=app;this.key=key;this.usr=usr;addActs(p);}

	public void addActs(Act [] p){
		if(act==null)
			act=new HashSet<Act>();
		for(Act i:p)act.add(i);}

	public void remActs(Act[]p){
		if(act!=null)
			for(Act i:p)act.remove(i);}

	public void remAct(Act p){
		if( act!=null)act.remove(p);}

	public boolean has( String act){ return has(Act.a(act));}
	public boolean has(Act p){ return act!=null&&act.contains(p);}

	public static Set<Act>s(Act...p){
		Set<Act>s=new HashSet<Act>();
		for(Act i:p)s.add(i);
		return s;}

	public static Set<Act>sa(Act[]p){
		Set<Act>s=new HashSet<Act>();
		for(Act i:p)s.add(i);
		return s;}

	public static Act[]a(Act...p){return p;}

	/**the url is <UrlPrefix>/<app>/<usr>/<key> , but this method does NOT assume the input has the UrlPrefix, instead , the service method removes the UrlPrefix*/
	public static Perm prmLoadByUrl(TL tl,String url){
		int i=url.indexOf('/'),i2=url.indexOf('/',i+1);
		String app=url.substring(0,i),usr=url.substring(i+1,i2),key=url.substring(i2+1);
		Perm j=loadBy( app,key,usr );
		return j;}

	public static Perm loadBy(String app,String key,String usr){
		Perm r=(Perm)loadWhere(Perm.class,where(C.app,app,C.key,key,C.usr,usr));
		return r;}

	public final static String dbtName="Perm";
	@Override public String getName(){return dbtName;}
	@Override public int pkcn(){return 3;}
	@Override public CI pkc(int i){return i==0?C.app:i==1?C.key:C.usr;}
	@Override public CI[]pkcols(){CI[]a={C.app,C.key,C.usr};return a;}

	@F public String app,key,usr;
	@F public Set<Act>act;
	@F public Date logTime,lastModified;

	@Override public String pkv(int i){return i==0?app:i==1?key:usr;}

	@Override public String[]pkvals(){String[]a={app,key,usr};return a;}
	@Override public String[]pkv(String[]v){app=v[0];key=v[1];usr=v[2];return v;}

	@Override public Object[]wherePK(){
		Object[]a={C.app,app,C.key,key,C.usr,usr};
		return a;}

	public Stor stor(){return Stor.loadBy(app,key);}
	public Stor usr(){return Stor.loadBy(app,usr);}

	/**Actions*/
	public enum Act{listApps,create,set,get,call,eval//listKeys,getKeys,
		,permSet,permGet,permlistByUsr,permListByKey,permCreate,permDelete,permAddAct,permRemAct,permOtherUsrs;
		public static Act a(String s){Act r=null;try{r=valueOf(s);}catch(Exception ex){}return r;}}

	public enum C implements CI{app,key,usr,act,logTime,LastModified;
		@Override public Field f(){return Co.f( name(),Perm.class );}
		@Override public String getName(){return name();}
		@Override public Class getType(){return act==this?Act.class:String.class;}
	}//C

	public static class Exceptn extends IllegalAccessException{
		public Exceptn(TL tl,Stor s,Act a,Perm p){super(tl+","+s+","+a+","+p);}}

	@Override public C[]columns(){return C.values();}

	@Override public List creationDBTIndices(TL tl){
		StringBuilder acts=new StringBuilder("set(");
		for(Act i:Act.values()){if(i.ordinal()>0)acts.append(',');
			acts.append('\'').append(i.toString()).append('\'');
		}acts.append(')');
		return Util.lst(Util.lst(
			"varchar(255) NOT NULL DEFAULT 'home' "//app \u1F3E0 ??
			,"varchar(255) NOT NULL DEFAULT 'home' "//key
			,"varchar(255) NOT NULL DEFAULT 'home' "//usr
			,acts.toString()//act
			,"timestamp onupdate current_timestamp default current_timestamp"//logTime
			,"timestamp"//lastModified
			),Util.lst("unique(`app`,`key`)",
			"key(`lastModified`,`logTime`)" ,
			"key(`usr`,`app`)",
			"key(`logTime`,`lastModified`)")
		);//val
		/*CREATE TABLE `Perm` (
			`app` varchar(255) NOT NULL DEFAULT 'home',
			`key` varchar(255) NOT NULL DEFAULT 'home',
			`usr` varchar(255) NOT NULL DEFAULT 'home',
			`act` set('listApps','listKeys','set','create','get','getKeys','call','eval'
				,'permSet','permGet','permlistByUsr','permListByKey','permCreate','permDelete'),
			`logTime` timestamp onupdate current_timestamp default current_timestamp,
			`lastModified` timestamp,
			unique(`app`,`key`,`usr`),
			key(`lastModified`,`logTime`),
			key(`usr`,`app`),
			key(`logTime`,`lastModified`)
			) ENGINE=InnoDB DEFAULT CHARSET=utf8;
			??  lock with ink pen Unicode code point: U+1F50F
			??  closed lock with key Unicode code point: U+1F510
			??  key Unicode code point: U+1F511
			??  lock Unicode code point: U+1F512
			??  open lock Unicode code point: U+1F513*/}

	static{registered.add(Stor.class);}
	public static Perm sttc=new Perm( );

	List<String>actsAsList(){return actsAsList(new LinkedList<String>());}

	List<String>actsAsList(List<String>l){
		for(Act i:act)l.add(i.toString());
		return l;}

	boolean chckOtherUsr(TL tl){
		if(tl==null)tl=TL.tl();
		return tl==null
			||tl.usr==null
			||tl.usr.key.equals( usr )
			||act.contains( Act.permOtherUsrs ); }

	boolean checkOtherUsr(TL tl) throws Exceptn {
		if(chckOtherUsr (tl))return true;
		else throw new Exceptn(tl,stor(),Act.permOtherUsrs,this); }

	/**based on the usr, list all the keys */
	@HttpMethod static public List<List<String>>
	byUsr(@HttpMethod(prmLoadByUrl= true)Perm p,TL tl)throws Exception {
		p.stor().perm(Act.permlistByUsr);p.checkOtherUsr( tl );
		List<List<String>> l = new LinkedList<List<String>>();List<String>x;
		for(DB.Tbl t : p.query(where(C.app, p.app, C.usr, p.usr)))
		{	l.add(x=new LinkedList<>());
			x.add(p.key);p.actsAsList(x);}
		return l;}

	/**based on key, list all usrs
	 *
	 * */
	@HttpMethod static public List<List<String>>
	usrsOfKey(@HttpMethod(prmLoadByUrl = true)Perm p,TL tl)throws Exception {
		p.stor().perm(Act.permListByKey);
		List<List<String>> l = new LinkedList<List<String>>();List<String>x;
		for(DB.Tbl t : p.query(where(C.app, p.app, C.key, p.key)))
		{	l.add(x=new LinkedList<>());
			x.add(p.usr);p.actsAsList(x);}
		return l;}

	@HttpMethod static public DB.Tbl
	create(@HttpMethod(prmBody = true)Perm p)throws Exception {
		p.stor().perm(Act.permCreate);
		return p.save();}

	@HttpMethod static public DB.Tbl
	addAct(@HttpMethod(prmBody = true)Perm p)throws Exception {
		p.stor().perm(Act.permAddAct);
		return null;}//p.save();}

	@HttpMethod static public DB.Tbl
	remAct(@HttpMethod(prmBody = true)Perm p)throws Exception {
		p.stor().perm(Act.permRemAct);
		return null;}

	@HttpMethod static public boolean
	delete(@HttpMethod(prmBody = true)Perm p)throws Exception{
		p.stor().perm(Act.permDelete);
		p.delete();return true;}

	//@HttpMethod static public boolean has(@HttpMethod(prmName = "perm",prmInstance = true)Perm p,@HttpMethod(prmName = "act")Act a)throws Exception {return p.has(a);}

}//class Perm


public static void debugService( HttpServletRequest request,HttpServletResponse response)throws IOException{
	Srvlt s=new Srvlt();
	s.service(request,response);
}

/** annotation to designate a java method as an ajax/xhr entry point of execution*/
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

@Override public void service(HttpServletRequest request,HttpServletResponse response){TL tl=null;try
{tl=TL.Enter(request,response);//,session,out);
	tl.h.r("contentType","text/json");//tl.logOut=tl.var("logOut",false);
	String hm=tl.h.req.getMethod();
	Method op=mth.get(hm);
	if(op==null)
		for (String s : mth.keySet())
			if ( s.equalsIgnoreCase( hm ) )
				op = mth.get(s);
	HttpMethod httpMethodAnno =op==null?null:op.getAnnotation( HttpMethod.class );
	tl.log("jsp:version2017.02.09.17.10:op=",op, httpMethodAnno );
	if(tl.usr==null&& (httpMethodAnno ==null || httpMethodAnno.usrLoginNeeded() ) )
		op=null;
	Object retVal=null;
	if(op!=null){
		Class[]prmTypes=op.getParameterTypes();
		Class cl=op.getDeclaringClass();Class[]ca={TL.class,String.class};
		Annotation[][]prmsAnno=op.getParameterAnnotations();
		int n=prmsAnno==null?0:prmsAnno.length,i=-1,urlIndx=UrlPrefix.length();
		Object[]args=new Object[n];
		for(Annotation[]t:prmsAnno)try{
			HttpMethod pp=t.length>0&&t[0] instanceof HttpMethod ?(HttpMethod )t[0]:null;
			Class prmClss=prmTypes[++i];
			String nm=pp!=null?pp.prmName():"arg"+i;//t.getName();
			Object o=null;
			if(pp!=null && pp.prmUrlPart()) {
				String u=tl.h.req.getRequestURI();
				int j=u.indexOf('/',urlIndx);
				args[i] =u.indexOf(urlIndx,j==-1?u.length():j);
				urlIndx=j+1;
			}else if(pp!=null && pp.prmUrlRemaining()) {
				String u=tl.h.req.getRequestURI();
				args[i] =u.indexOf(urlIndx+1);
			}else if(pp!=null && pp.prmLoadByUrl())
				args[i]=cl.getMethod( "prmLoadByUrl",ca ).invoke( tl,tl.h.req.getRequestURI() );
			else if(DB.Tbl.class.isAssignableFrom(prmClss))
			{DB.Tbl f=(DB.Tbl)prmClss.newInstance();args[i]=f;
				if(pp!=null && pp.prmBody())
					f.fromMap(tl.json);
				else {
					o = tl.json.get(nm);
					if(o instanceof Map) f.fromMap((Map) o);
					else if(o instanceof List) f.vals(((List) o).toArray());
					else if(o instanceof Object[]) f.vals((Object[]) o);
				else f.readReq("");
			}}else if(pp!=null && pp.prmBody())
				args[i]=prmClss.isAssignableFrom( String.class )
					?readString( tl.h.req.getReader() )
					:tl.bodyData;
			else
				args[i]=o=TL.class.equals(prmClss)?tl//:Map.class.isAssignableFrom(c) &&(nm.indexOf("p")!=-1) &&(nm.indexOf("r")!=-1) &&(nm.indexOf("m")!=-1)?tl.json
					:tl.h.req(nm,prmClss);
		}catch(Exception ex){tl.error(ex,SrvltName,".run:arg:i=",i);}
		retVal=n==0?op.invoke(cl)
			:n==1?op.invoke(cl,args[0])
			:n==2?op.invoke(cl,args[0],args[1])
			:n==3?op.invoke(cl,args[0],args[1],args[2])
			:n==4?op.invoke(cl,args[0],args[1],args[2],args[3])
			:n==5?op.invoke(cl,args[0],args[1],args[2],args[3],args[4])
			:n==6?op.invoke(cl,args[0],args[1],args[2],args[3],args[4],args[5])
			:n==7?op.invoke(cl,args[0],args[1],args[2],args[3],args[4],args[5],args[6])
			:op.invoke(cl,args);
		if( httpMethodAnno !=null && httpMethodAnno.nestJsonReq() && tl.json!=null){
			tl.json.put("return",retVal);retVal=tl.json;}
	}
	// else TL.Util.mapSet(tl.response,"msg","Operation not authorized ,or not applicable","return",false);
	if(tl.h.r("responseDone")==null)
	{if(tl.h.r("responseContentTypeDone")==null)
		response.setContentType(String.valueOf(tl.h.r("contentType")));
		Json.Output o=tl.getOut();
		o.o(retVal);
		tl.log(SrvltName,".run:xhr-response:",tl.jo().o(retVal).toString());}
	tl.getOut().flush();
}catch(Exception x){
	if(tl!=null){
		tl.error(x,SrvltName,":");
		try{tl.getOut().o(x);}catch(IOException iox){}
	}else
		x.printStackTrace();
}finally{TL.Exit();}
}//run op servlet.service

 static String readString(BufferedReader r) throws IOException {
	StringBuilder b=new StringBuilder();int c=0;
	String line=r.readLine();
	while(line!=null){
		if(c++>0)b.append( '\n' );
		b.append( line );
		line=r.readLine();}
	return b.toString();}

 public static void registerMethods(Class p){
	Method[]b=p.getMethods();
	String cn=p.getSimpleName();
	for(Method m:b){
		HttpMethod h = m.getAnnotation(HttpMethod.class);
		if( h !=null)
		{	String s=m.getName();
			mth.put( h.useClassName()?cn+"."+s:s,m);
		}}}//registerOp

/** * Created by mbohamad on 19/07/2017.*/
public static class TL{
public static final String TlName=Srvlt.packageName+".TL";
	public TL(HttpServletRequest r,HttpServletResponse n,Writer o){h.req=r;h.rspns=n;out=new Json.Output(o);}

	public H h=new H();
	public Map<String,Object> /**accessing request in json-format*/json;
	public Object bodyData;
	public Date now;
	Stor usr;
	/**wrapping JspWriter or any other servlet writer in "out" */
	Json.Output out,/**jo is a single instanceof StringWriter buffer*/jo;

//TL member variables

	/**the static/class variable "tl"*/ static ThreadLocal<TL> tl=new ThreadLocal<TL>();
	public static final String CommentHtml[]={"\n<!--","-->\n"},CommentJson[]={"\n/*","\n*/"};

	public Json.Output jo(){if(jo==null)try{jo=new Json.Output();}catch(Exception x){error(x,TlName,".jo:IOEx:");}return jo;}
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
			if(h.getSession().isNew()){
				DB.Tbl.check(this);//Srvlt.Domain.loadDomain0();
			}
			usr=(Srvlt.Stor)h.s("usr");//(Srvlt.Stor)
		}catch(Exception ex){error(ex,TlName,".onEnter");}
		//if(pages==null){rsp.setHeader("Retry-After", "60");rsp.sendError(503,"pages null");throw new Exception("pages null");}
		if(h.logOut)out.w(h.comments[0]).w(TlName).w(".tl.onEnter:\n").o(this).w(h.comments[1]);
	}//onEnter
	private void onExit(){usr=null;h.ip=null;now=null;h.req=null;json=null;out=jo=null;h.rspns=null;}//ssn=null;
	/**unsets the localThread, and unset local variables*/
	public static void Exit()//throws Exception
	{TL p=TL.tl();if(p==null)return;
		DB.close(p);//changed 2017.7.17
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
				error(ex,TlName,".h.getMultiParts");}
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
			if(reqv!=null&&!reqv.equals(sVal)){ss.setAttribute(pn,r=reqv);//logo(TlName,".h.var(",pn,")reqVal:sesssion.set=",r);
			}
			else if(sVal!=null){r=sVal; //logo(TlName,".h.var(",pn,")sessionVal=",r);
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
	/**get a pooled jdbc-connection for the current Thread, calling the function dbc()*/
	Connection dbc()throws SQLException {
		TL p=this;//Object s=context.DB.reqCon.str,o=p.s(s);
		Object[]a=DB.stack(p,null);//o instanceof Object[]?(Object[])o:null;
		//o=a==null?null:a[0];
		if(a[0]==null)//o==null||!(o instanceof Connection))
			a[0]=DB.c();
		return (Connection)a[0];}
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
			t.error(ex,SrvltName,".context.getRealPath:",path);}
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
			t.error(ex,SrvltName,".context.getContextIndex:");}
		return -1;}
	//***/static Map<DB,String> getContextPack(TL t,List<Map<DB,String>>a){return null;}
}//context

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
			TL.tl().error(x, SrvltName,".Util.<T>T parse(String s,T defval):",s,defval);}
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
		else if(URL.class.isAssignableFrom(c))try
		{return new URL("file:" +TL.tl().h.getServletContext().getContextPath()+'/'+s);}
		catch (Exception ex) {TL.tl().error(ex,SrvltName,".Util.parse:URL:p=",s," ,c=",c);}
			boolean b=c==null?false:c.isEnum();
			if(!b){Class ct=c.getEnclosingClass();b=ct==null?false:ct.isEnum();if(b)c=ct;}
			if(b){
				for(Object o:c.getEnumConstants())
					if(s.equalsIgnoreCase(o.toString()))
						return o;
			}
			return Json.Prsr.parse(s);
		}catch(Exception x){//changed 2016.06.27 18:28
			TL.tl().error(x, SrvltName,".Util.<T>T parse(String s,Class):",s,c);}
		return s;}

	public static String md5(String s){
		if(s!=null)try{java.security.MessageDigest m=
			               java.security.MessageDigest.getInstance("MD5");
			//m.update(s.getBytes());
			String r=new String(m.digest(s.getBytes()));
			return r;
		}catch(Exception x){//changed 2016.06.27 18:28
			TL.tl().error(x, SrvltName,".Util.md5(String s):",s);}
		return "";}

}//class Util

public static class DB {
	/**returns a jdbc pooled Connection.
	 uses MysqlConnectionPoolDataSource with a database from the enum context.DB.url.str,
	 sets the pool as an application-scope attribute named context.DB.pool.str
	 when first time called, all next calls uses this context.DB.pool.str*/
	public static synchronized Connection c()throws SQLException {
		TL t=TL.tl();
		Object[]p=null,a=stack(t,null);//Object[])t.s(context.DB.reqCon.str);
		Connection r=(Connection)a[0];//a ==null?null:
		if(r!=null)return r;
		MysqlConnectionPoolDataSource d=(MysqlConnectionPoolDataSource)t.h.a(context.DB.pool.str);
		r=d==null?null:d.getPooledConnection().getConnection();
		if(r!=null)
			a[0]=r;//changed 2017.07.14
		else try
		{try{int x=context.getContextIndex(t);
			t.log(SrvltName,".DB.c:1:getContextIndex:",x);
			if(x!=-1)
			{	p=c(t,x,x,x,x);t.log(SrvltName,".DB.c:1:c2:",p);
				r=(Connection)p[1];
				return r;}
		}catch(Exception e){t.log(SrvltName,".DB.MysqlConnectionPoolDataSource:1:",e);}
			String[]dba=context.DB.dbName.a
				,sra=context.DB.server.a
				,una=context.DB.un.a
				,pwa=context.DB.pw.a;//CHANGED: 2016.02.18.10.32
			for(int idb=0;r==null&&idb<dba.length;idb++)
				for(int iun=0;r==null&&iun<una.length;iun++)
					for(int ipw=0;r==null&&ipw<pwa.length;ipw++)//n=context.DB.len()
						for(int isr=0;r==null&&isr<sra.length;isr++)try
						{	p=c(t,idb,iun,ipw,isr);
							r=(Connection)p[1];
							if(t.h.logOut)t.log("new "+context.DB.pool.str+":"+p[0]);
						}catch(Exception e){t.log(SrvltName,".DB.MysqlConnectionPoolDataSource:",idb,",",isr,",",iun,ipw,t.h.logOut?p[2]:"",",",e);}
		}catch(Throwable e){t.error(e,SrvltName,".DB.MysqlConnectionPoolDataSource:throwable:");}//ClassNotFoundException
		if(t.h.logOut)t.log(context.DB.pool.str+":"+(p==null?null:p[0]));
		if(r==null)try
		{r=java.sql.DriverManager.getConnection
			                          ("jdbc:mysql://"+context.DB.server.str
				                           +"/"+context.DB.dbName.str
				                          ,context.DB.un.str,context.DB.pw.str
			                          );Object[]b={r,null};
			t.h.s(context.DB.reqCon.str,b);
		}catch(Throwable e){t.error(e,SrvltName,".DB.DriverManager:");}
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
		Object[]a={d,r,ss};//,b={r,null};t.s(context.DB.reqCon.str,b);
		stack(t,r);
		return a;}
	/**returns a jdbc-PreparedStatement, setting the variable-length-arguments parameters-p, calls dbP()*/
	public static PreparedStatement p( String sql, Object...p)throws SQLException{return P(sql,p);}
	/**returns a jdbc-PreparedStatement, setting the values array-parameters-p, calls TL.dbc() and log()*/
	public static PreparedStatement P(String sql,Object[]p)throws SQLException{return P(sql,p,true);}
	public static PreparedStatement P(String sql,Object[]p,boolean odd)throws SQLException {
		TL t=TL.tl();Connection c=t.dbc();
		PreparedStatement r=c.prepareStatement(sql);if(t.h.logOut)
			t.log(SrvltName,"("+t+").DB.P(sql="+sql+",p="+p+",odd="+odd+")");
		if(odd){if(p.length==1)
			r.setObject(1,p[0]);else
			for(int i=1,n=p.length;p!=null&&i<n;i+=2)if((!(p[i] instanceof List)) ) // ||!(p[i-1] instanceof List)||((List)p[i-1]).size()!=2||((List)p[i-1]).get(1)!=Tbl.Co.in )
				r.setObject(i/2+1,p[i]);//if(t.logOut)TL.log("dbP:"+i+":"+p[i]);
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
	}catch (Exception ex){tl.error(ex,SrvltName,".DB.push");}}

	//public static void close(Connection c){close(c,tl());}
	public static void close(Connection c,TL tl){
		try{if(c!=null){
			List<ResultSet>a=stack(tl,false);
			if(a==null||a.size()<1)
				tl.h.s(context.DB.reqCon.str,a=null);
			if(a==null)
				c.close();}
		}catch(Exception e){e.printStackTrace();}}
	public static void close(TL tl){
		try{Object[]a=stack(tl,null,false);
			Connection c=a==null?null:(Connection) a[0];
			if(c!=null)close(c,tl);
		}catch(Exception e){e.printStackTrace();}}
	public static void close(ResultSet r){close(r,TL.tl(),false);}
	public static void close(ResultSet r,boolean closeC){close(r,TL.tl(),closeC);}
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
		}catch(Exception e){e.printStackTrace();}
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
			if(t.h.logOut)try{t.log(t.jo().w(SrvltName).w(".DB.L:sql=").o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}
			catch(IOException x){t.error(x,SrvltName,".DB.List:",sql);}}}

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
			if(tl.h.logOut)try{tl.log(tl.jo().w(SrvltName).w(".DB.Lt:sql=")
				                          .o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}
			catch(IOException x){tl.error(x,SrvltName,".DB.Lt:",sql);}
		}
	}

	public static List<Object> q1colList(String sql,Object...p)throws SQLException
	{ResultSet s=null;List<Object> r=null;try{s=R(sql,p);r=new LinkedList<Object>();
		while(s.next())r.add(s.getObject(1));return r;}
	finally{TL t=TL.tl();close(s,t);if(t.h.logOut)
		try{t.log(t.jo().w(SrvltName).w(".DB.q1colList:sql=")//CHANGED:2015.10.23.16.06:closeRS ;
			          .o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}catch(IOException x){t.error(x,SrvltName,".DB.q1colList:",sql);}}}
	public static <T>List<T> q1colTList(String sql,Class<T>t,Object...p)throws SQLException
	{ResultSet s=null;List<T> r=null;try{s=R(sql,p);r=new LinkedList<T>();//Class<T>t=null;
		while(s.next())r.add(
			s.getObject(1,t)
			//s.getObject(1)
		);return r;}
	finally{TL tl=TL.tl();close(s,tl);if(tl.h.logOut)
		try{tl.log(tl.jo().w(SrvltName).w(".DB.q1colList:sql=")//CHANGED:2015.10.23.16.06:closeRS ;
			           .o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}catch(IOException x){tl.error(x,SrvltName,".DB.q1colList:",sql);}}}
	public static Object[] q1col(String sql,Object...p)throws SQLException
	{List<Object> l=q1colList(sql,p);Object r[]=new Object[l.size()];l.toArray(r);l.clear();return r;}
	public static <T>T[] q1colT(String sql,Class<T>t,Object...p)throws SQLException
	{List<T> l=q1colTList(sql,t,p);T[]r=(T[]) java.lang.reflect.Array.newInstance(t,l.size());l.toArray(r);l.clear();return r;}
	/**returns a row of columns of the result of sql
	 ,calls dbR(),dbcc(),and dbclose(ResultSet,TL.dbc())*/
	public static Object[] q1row(String sql,Object...p)throws SQLException{return q1Row(sql,p);}
	public static Object[] q1Row(String sql,Object[]p)throws SQLException
	{ResultSet s=null;try{s=R(sql,p);Object[]a=null;int cc=cc(s);if(s.next())
	{a=new Object[cc];for(int i=0;i<cc;i++)try{a[i]=s.getObject(i+1);}
	catch(Exception ex){TL.tl().error(ex,SrvltName,".DB.q1Row:",sql);a[i]=s.getString(i+1);}}
		return a;}finally{close(s);}}//CHANGED:2015.10.23.16.06:closeRS ;
	/**returns the result of (e.g. insert/update/delete) sql-statement
	 ,calls dbP() setting the variable-length-arguments values parameters-p
	 ,closes the preparedStatement*/
	public static int x(String sql,Object...p)throws SQLException{return X(sql,p);}
	public static int X(String sql,Object[]p)throws SQLException {
		int r=-1;try{PreparedStatement s=P(sql,p,false);r=s.executeUpdate();s.close();return r;}
		finally{TL t=TL.tl();if(t.h.logOut)try{
			t.log(t.jo().w(SrvltName).w(".DB.x:sql=").o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}
		catch(IOException x){t.error(x,SrvltName,".DB.X:",sql);}}}
	/**output to tl.out the Json.Output.oRS() of the query*/
	public static void q2json(String sql,Object...p)throws SQLException{
		ResultSet s=null;
		TL tl=TL.tl();
		try{
			s=R(sql,p);
			try{
				tl.getOut() .o(s); // (new Json.Output()) // TODO:investigate where the Json.Output.w goes
			}catch (IOException e) {e.printStackTrace();}
		}
		finally
		{close(s,tl);
			if(tl.h.logOut)try{
				tl.log(tl.jo().w(SrvltName).w(".DB.L:q2json=")
					       .o(sql).w(",prms=").o(p).toStrin_());
			}catch(IOException x){tl.error(x,SrvltName,".DB.q1json:",sql);}
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
			try {init(DB.R(sql, p));}
			catch (Exception e) {TL.tl().logo(SrvltName,".DB.ItTbl.<init>:Exception:sql=",sql,",p=",p," :",e);}}
		public ItTbl(ResultSet o) throws SQLException{init(o);}
		public ItTbl init(ResultSet o) throws SQLException
		{row.rs=o;row.m=o.getMetaData();row.row=row.col=0;
			row.cc=row.m.getColumnCount();return this;}
		static final String ErrorsList=SrvltName+".DB.ItTbl.errors";
		@Override public boolean hasNext(){
			boolean b=false;try {if(b=row!=null&&row.rs!=null&&row.rs.next())row.row++;
			else DB.close(row.rs);//CHANGED:2015.10.23.16.06:closeRS ; 2017.7.17
			}catch (SQLException e) {//e.printStackTrace();
				TL t=TL.tl();//changed 2016.06.27 18:05
				final String str=SrvltName+".DB.ItTbl.next";
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
					final String str=SrvltName+".DB.ItTbl.ItRow.next";
					t.error(e,str);
					List l=(List)t.json.get(ErrorsList);//t.response
					if(l==null)t.json.put(ErrorsList,l=new LinkedList());//t.response
					l.add(Util.lst(str,row,col,e));
				}//.printStackTrace();}
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
	public abstract static class Tbl<PK> implements Json.Output.JsonOutput {

// /**encapsulating Html-form fields, use annotation Form.F for defining/mapping member-variables to html-form-fields*/ public abstract static class Form{

		@Override public String toString(){return toJson();}

		/**get table name*/public abstract String getName();

		public Json.Output jsonOutput(Json.Output o,String ind,String path)throws java.io.IOException{return jsonOutput( o,ind,path,true );}
		public Json.Output jsonOutput(Json.Output o,String ind,String path,boolean closeBrace)throws java.io.IOException{
			//if(o.comment)o.w("{//TL.Form:").w('\n').p(ind);else//.w(p.getClass().toString())
			o.w('{');
			CI[]a=columns();//Field[]a=fields();
			String i2=ind+'\n';
			o.w("\"class\":").oStr(getClass().getSimpleName(),ind);//w("\"name\":").oStr(p.getName(),ind);
			for(CI f:a)
			{	o.w(',').oStr(f.getName(),i2).w(':')
					 .o(v(f),ind,o.comment?path+'.'+f.getName():path);
				if(o.comment)o.w("//").w(f.toString()).w("\n").p(i2);
			}
			if(closeBrace){
				if(o.comment)
					o.w("}//DB.Tbl&cachePath=\"").p(path).w("\"\n").p(ind);
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
					t.error(ex,SrvltName,".DB.Tbl.readReq:t=",this," ,field="
						,f+" ,c=",c," ,s=",s," ,v=",v);}}
			return this;}

		public abstract CI[]columns();//public abstract FI[]flds();

		public Object[]vals(){
			CI[]a=columns();//Field[]a=fields();
			Object[]r=new Object[a.length];
			int i=-1;
			for(CI f:a){i++;
				r[i]=v(a[i]);
			}return r;}

		public Tbl vals (Object[]p){
			int i=-1;CI[]a=columns();//Field[]a=fields();
			for(CI f:a)
				v(f,p[++i]);
			return this;}

		public Map asMap(){
			return asMap(null);}

		public Map asMap(Map r){
			CI[]a=columns();//Field[]a=fields();
			if(r==null)r=new HashMap();
			int i=-1;
			for(CI f:a){i++;
				r.put(f.getName(),v(a[i]));
			}return r;}

		public Tbl fromMap (Map p){
			CI[]a=columns();//Field[]a=fields();
			for(CI f:a)
				v(f,p.get(f.getName()));
			return this;}

	/*	public Field[]fields(){return fields(getClass());}
		public static Field[]fields(Class<?> c){
			List<Field>l=fields(c,null);
			int n=l==null?0:l.size();
			Field[]r=new Field[n];
			if(n>0)l.toArray(r);
			return r;}
		public static List<Field>fields(Class<?> c,List<Field>l){
			//this is beautiful(tear running down cheek)
			Class s=c==null?c:c.getSuperclass();
			if(s!=null&&Tbl.class .isAssignableFrom( s))
				l=fields( s,l );
			Field[]a=c.getDeclaredFields();
			if(l==null)l=new LinkedList<Field>();
			for(Field f:a){F i=f.getAnnotation(F.class);
				if(i!=null)l.add(f);}
			return l;}*/

		public Tbl v(CI p,Object v){return v(p.f(),v);}//this is beautiful(tear running down cheek)

		public Object v(CI p){return v(p.f());}//this is beautiful(tear running down cheek)

		Tbl v(Field p,Object v){//this is beautiful(tear running down cheek)
			try{Class <?>t=p.getType();
				if(v!=null && !t.isAssignableFrom( v.getClass() ))//t.isEnum()||t.isAssignableFrom(URL.class))
					v=Util.parse(v instanceof String?(String)v:String.valueOf(v),t);
				p.set(this,v);
			}catch (Exception ex) {TL.tl().error(ex,SrvltName,".DB.Tbl.v(",this,",",p,",",v,")");}
			return this;}

		Object v(Field p){//this is beautiful(tear running down cheek)
			try{return p.get(this);}
			catch (Exception ex) {//IllegalArgumentException,IllegalAccessException
				TL.tl().error(ex,SrvltName,".DB.Tbl.v(",this,",",p,")");return null;}}

		/**Field annotation to designate a java member for use in a Html-Form-field/parameter*/
		@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
		public @interface F{	boolean prmPw() default false;boolean group() default false;boolean max() default false;boolean json() default false; }

		/**Interface for enum-items from different forms and sql-tables ,
		 * the enum items represent a reference Column Fields for identifing the column and selection.*/
		public interface CI{public Field f();public String getName();public Class getType();}//interface I

//}//public abstract static class Form

		/**Sql-Column Interface, for enum -items that represent columns in sql-tables
		 * the purpose of creating this interface is to centerlize
		 * the definition of the names of columns in java source code*/

		public Object[]wherePK(){Object[]c=pkcols(),v=pkvals(),a=new Object[c.length+v.length];
			for(int i=0;i<c.length;i++){a[i*2]=c[i];a[i*2+1]=v[i];}
			return a;}

		public static CI[]cols(CI...p){return p;}
		public static Object[]where(Object...p){return p;}
		public abstract CI pkc(int i);public abstract CI[]pkcols();public abstract int pkcn();
		public abstract PK pkv(int i);public abstract PK[]pkvals();
		public abstract PK[]pkv(PK[]v);
		public PK[]pka(PK...p){return p;}//static

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
				DB.Tbl.Co.where(sql, where);
			if(groupBy!=null && groupBy.length>0){
				sql.append(" group by ");
				Co.generate(sql,groupBy);}
			if(orderBy!=null && orderBy.length>0){
				sql.append(" order by ");
				Co.generate(sql,orderBy);}
			return sql.toString();}

		public static String sql(CI[]cols,Object[]where,CI[]groupBy,CI[]orderBy,String dbtName){
			//if(cols==null)cols=columns();
			StringBuilder sql=new StringBuilder("select ");
			Co.generate( sql,cols );//sql.append(cols);
			sql.append(" from `").append(dbtName).append("` ");
			if(where!=null&&where.length>0)
				DB.Tbl.Co.where(sql, where);
			if(groupBy!=null && groupBy.length>0){
				sql.append(" group by ");
				Co.generate(sql,groupBy);}
			if(orderBy!=null && orderBy.length>0){
				sql.append(" order by ");
				Co.generate(sql,orderBy);}
			return sql.toString();}
		/** returns a list of 3 lists,
		 * the 1st is a list for the db-table columns-CI
		 * the 2nd is a list for the db-table-key-indices
		 * the 3rd is a list for row insertion
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
				try {o=DB.q1colList("show tables");
					tl.h.a(SrvltName+":db:show tables",o);
				} catch (SQLException ex) {
					tl.error(ex, SrvltName+".DB.Tbl.checkTableCreation:check-pt1:",dtn);}
			List l=(List)o;
			try{if(o==null||(!l.contains( dtn )&&!l.contains( dtn.toLowerCase()))){
				StringBuilder sql= new StringBuilder("CREATE TABLE `").append(dtn).append("` (\n");
				CI[]ci=columns();int an,x=0;
				List a=creationDBTIndices(tl),b=(List)a.get(0);
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
				tl.log(SrvltName,".DB.Tbl.checkTableCreation:before:sql=",sql);
				int r=DB.x(sql.toString());
				tl.log(SrvltName,".DB.Tbl.checkTableCreation:executedSql:",dtn,":returnValue=",r);
				b=an>2?(List)a.get(2):b;if(an>2)
					for(Object bo:b){
						List c=(List)bo;
						Object[]p=new Object[c.size()];
						c.toArray(p);
						vals(p);
						try {save();} catch (Exception ex) {
							tl.error(ex, SrvltName,".DB.Tbl.checkTableCreation:insertion",c);}
					}
			}
			} catch (SQLException ex) {
				tl.error(ex, SrvltName,".DB.Tbl.checkTableCreation:errMain:",dtn);}
		}//checkTableCreation
		/**where[]={col-name , param}*/
		public int count(Object[]where) throws Exception{return count(where,null,getName());}
		public static int count(Object[]where,CI[]groupBy,String name) throws Exception{
			String sql=sql(cols(Co.count),where,groupBy,null,name);//new StringBuilder("select count(*) from `").append(getName()).append("` where `").append(where[0]).append("`=").append(Co.m(where[0]).txt);//where[0]instanceof CI?m((CI)where[0]):'?');
			return DB.q1int(sql,-1,where[0],where[1]);}
		public int maxPlus1(CI col) throws Exception{
			String sql=sql("max(`"+col+"`)+1",null,null,null);
			return DB.q1int(sql,1);}
		public static int maxPlus1(CI col,String dbtn) throws Exception{
			String sql="SELECT max(`"+col+"`)+1 from `"+dbtn+"`";
			return DB.q1int(sql,1);}
		// /**returns one object from the db-query*/ /**where[]={col-name , param}*/public Object obj(CI col,Object[]where) throws Exception{return DB.q1Obj(sql(cols(col),where),where);}
		/**returns one string*/
		public String select(CI col,Object[]where) throws Exception{
			String sql=sql(cols(col),where);
			return DB.q1Str(sql,where);}
		// /**returns one column, where:array of two elements:1st is column param, 2nd value of param*/Object[]column(CI col,Object...where) throws Exception{ return DB.q1col(sql(cols(col),where),where[0],where[1]);}//at
		/**returns a table*/
		public Object[][]select(CI[]col,Object[]where)throws Exception{
			return DB.Q(sql(col,where), where);}
		/**loads one row from the table*/
		Tbl load(ResultSet rs)throws Exception{return load(rs,columns());}
		/**loads one row from the table*/
		Tbl load(ResultSet rs,CI[]a)throws Exception{
			int c=0;for(CI f:a)v(f,rs.getObject(++c));
			return this;}
		/**loads one row from the table* /
		 public Tbl loadPK(PK...pk){
		 ResultSet r=null;TL t=TL.tl();
		 try{r=DB.r(sql(cols(Co.all), where(pkc(0)))
		 ,pk);
		 if(r.next())load(r);
		 else{t.error(null,SrvltName,".DB.Tbl(",this,").load(pk=",pk,"):resultset.next=false");nullify();}}
		 catch(Exception x){t.error(x,SrvltName,".DB.Tbl(",this,"):",pk);}
		 finally{DB.close(r,t);}
		 return this;}*/
		/**loads one row from the table*/
		public Tbl load(){
			ResultSet r=null;TL t=TL.tl();Object[]pk=wherePK();
			try{r=DB.r(sql(cols(Co.all),pk),pk);
				if(r.next())load(r);
				else{t.error(null,SrvltName,".DB.Tbl(",this,").load(pk=",pk,"):resultset.next=false");nullify();}}
			catch(Exception x){t.error(x,SrvltName,".DB.Tbl(",this,"):",pk);}
			finally{DB.close(r,t);}
			return this;}

		public Tbl nullify(){return nullify(columns());}
		public Tbl nullify(CI[]a){for(CI f:a)v(f,null);return this;}
		// /**loads one row from the table*/ Tbl load(){return load(pkv());}

		/**loads one row using column CI c */
		Tbl loadBy(CI c,Object v){
			try{Object[]a=DB.q1row(sql(cols(Co.all),where(c)),v);
				vals(a);}
			catch(Exception x){TL.tl().error(x,SrvltName,".DB.Tbl(",this,").loadBy(",c,",",v,")");}
			return this;}//loadBy

		/**loads one row based on the where clause */
		Tbl loadWhere(Object[]where){
			try{Object[]a=DB.q1row(sql(cols(Co.all),where),where);
				vals(a);}
			catch(Exception x){TL.tl().error(x,SrvltName,".DB.Tbl(",this,").loadWhere(",where,")");}
			return this;}//loadBy

		/**loads one row based on the where clause */
		public static Tbl loadWhere(Class<? extends Tbl>c,Object[]where){
			Tbl t=null,z;
			try{z=c.newInstance();
				z.loadWhere( where );t=z;
			}catch(Exception x){TL.tl().error(x,SrvltName,".DB.Tbl(",t,").loadWhere(",c,",",where,")");}
			return t;}//loadBy

		Tbl save(CI c){int pkn=pkcn();if(pkn==1) {
			CI pkc = pkc(0);
			Object cv = v( c );
			PK pkv = pkv(0);
			TL t = TL.tl();
			if ( cv instanceof Map ) try {
				String j = t.jo().clrSW().o( cv ).toString();
				cv = j;
			} catch ( IOException e ) {
				t.error( e, SrvltName, ".DB.Tbl.save(CI:", c, "):" ); }
			try {
				DB.x( "insert into `" + getName() + "` (`" + pkc +
					      "`,`" + c + "`) values(?"//+Co.m(pkc).txt
					      + ",?"//+Co.m(c).txt
					      + ")", pkv, cv );
				//Integer k=(Integer)pkv;
				//DB.Tbl.Log.log( DB.Tbl.Log.Entity.valueOf(getName()), k, DB.Tbl.Log.Act.Update, TL.Util.mapCreate(c,v(c)) );
			} catch ( Exception x ) {
				TL.tl().error( x, SrvltName,".DB.Tbl(",
					this, ").save(", c, "):pkv=", pkv ); }
		}else{
			CI[]pkc = pkcols();
			Object[]a=new Object[pkc.length+1];
			a[0]=v( c );
			PK[]pkv = pkvals();
			TL t = TL.tl();
			//if ( a[0] instanceof Map ) try { String j = t.jo().clrSW().o( a[0] ).toString();a[0] = j; } catch ( IOException e ) { t.error( e, SrvltName, ".DB.Tbl.save(CI:", c, "):" ); }
			try {StringBuilder b=new StringBuilder( "insert into `" )
				                     .append( getName() ).append("` (`" ).append( c.toString() );
				for(CI k:pkc)
					b.append( "`,`" ).append( k.toString() );
				b.append( "`) values(?");
				for(int i=1;i<a.length;i++){a[i]=pkv[i-1];
					b.append( ",?" );}
				DB.X( b.append( ')' ).toString(),a );
			} catch ( Exception x ) {
				TL.tl().error( x, SrvltName, ".DB.Tbl(",
					this, ").save(", c, "):pkv=", pkv ); }
		}return this;}//save

		/**store this entity in the dbt , if pkv is null , this method uses the max+1 of pk-col*/
		public Tbl save() throws Exception{
			CI[] cols = columns();
			StringBuilder sql = new StringBuilder( "replace into`" ).append( getName() ).append( "`( " );
			Co.generate( sql, cols );//.toString();
			sql.append( ")values(" ).append( Co.prm.txt );//Co.m(cols[0]).txt
			for ( int i = 1; i < cols.length; i++ )
				sql.append( "," ).append( Co.prm.txt );//Co.m(cols[i]).txt
			sql.append( ")" );//int x=
			DB.X( sql.toString(), vals() ); //TODO: investigate vals() for json columns
			TL.tl().log( "save", this );//log(nw?DB.Tbl.Log.Act.New:DB.Tbl.Log.Act.Update);
			return this;}//save

		public Tbl readReq_save() throws Exception{
			Map old=asMap();
			readReq("");
			Map val=asMap();
			for(CI c:columns()){String n=c.getName();
				Object o=old.get(n),v=val.get(n);
				if(o==v ||(o!=null && o.equals(v)))
				{val.remove(n);old.remove(n);}
				else save(c);}
			//log(DB.Tbl.Log.Act.Update,old);
			return this;
		}//readReq_save
		public Tbl readReq_saveNew() throws Exception{
			//PK pkv=pkv(0);
			readReq("");//if(pkv(0)==null&&pkv!=null)pkv(pkv);
			return save();//log(DB.Tbl.Log.Act.Update,old);
		}//readReq_save

		//void log(DB.Tbl.Log.Act act){	Map val=asMap();Integer k=(Integer)pkv();DB.Tbl.Log.log( DB.Tbl.Log.Entity.valueOf(getName()), k, act, val);}
		public int delete() throws SQLException{int pkn=pkcn();if(pkn==1) {
			PK pkv=pkv(0);
			int x=DB.x("delete from `"+getName()+"` where `"+pkc(0)+"`=?", pkv);
			//log(DB.Tbl.Log.Act.Delete);
			return x;
		}else{int x=-1;CI[]pkc=pkcols();PK[]pkv=pkvals();
			StringBuilder b=new StringBuilder( "delete from `" )
				                .append( getName() ).append("` where `" ).append( pkc[0] ).append( "`=?" );
			for(int i=1;i<pkc.length;i++)
				b.append( " and `" ).append( pkc[i] ).append( "`=?" );
			DB.X( b.toString(),pkv );
			return x;}}

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
				try{rs=DB.R(sql, where);}
				catch(Exception x){
					TL.tl().error(x,SrvltName,".DB.Tbl(",this,").Itrtr.<init>:where=",where);}
			}
			public Itrtr(Object[]where){a=columns();
				try{rs=DB.R(sql(cols(Co.all),where), where);}
				catch(Exception x){TL.tl().error(x,SrvltName,".DB.Tbl(",this,").Itrtr.<init>:where=",where);}}
			@Override public Iterator<Tbl>iterator(){return this;}
			@Override public boolean hasNext(){boolean b=false;
				try {b = rs!=null&&rs.next();} catch (SQLException x)
				{TL.tl().error(x,SrvltName,".DB.Tbl(",this,").Itrtr.hasNext:i=",i,",rs=",rs);}
				if(!b&&rs!=null){DB.close(rs);rs=null;}
				return b;}
			@Override public Tbl next(){i++;Tbl t=Tbl.this;TL tl=TL.tl();
				if(makeClones)try{
					t=t.getClass().newInstance();}catch(Exception ex){
					tl.error(ex,SrvltName,".DB.Tbl(",this,").Itrtr.next:i=",i,":",rs,":makeClones");
				}
				try{t.load(rs,a);}catch(Exception x){
					tl.error(x,SrvltName,".DB.Tbl(",this,").Itrtr.next:i=",i,":",rs);
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
			,or("or"),like("like"),in("in")//,and("and"),prnthss("("),max("max(?)")
			;String txt;
			Co(String p){txt=p;}
			@Override public Field f(){return null;}
			@Override public String getName(){return name();}
			@Override public Class getType(){return String.class;}
			public static Field f(String name,Class<? extends Tbl>c){
				//for(Field f:fields(c))if(name.equals(f.getName()))return f;return null;
				Field r=null;try{r=c.getField(name);}catch(Exception x)
				{TL.tl().error(x,SrvltName,".DB.Tbl.f(",name,c,"):");}
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
					if(o instanceof Co)b.append(o);else
					if(o instanceof CI)
						b.append('`').append(o).append("`=")
							.append('?');//Co.m(o).txt
					else if(o instanceof List){List l=(List)o;
						o=l.size()>1?l.get(1):null;
						if(o ==Co.in && i+1<n && where[i+1] instanceof List){
							b.append('`').append(l.get(0)).append("` ").append(o);
							l=(List)where[i+1];
							genList(b,l);
						}else if(o instanceof Co)//o!=null)//if(ln==2 && )
						{	Co m=(Co)o;o=l.get(0);
							if(o instanceof CI || o instanceof Co)
								b.append('`').append(o).append('`');
							else
								TL.tl().log(SrvltName,".DB.Tbl.Co.where:unknown where-clause item:o=",o);
							b.append(m.txt).append("?");
						}else
							TL.tl().log(SrvltName,".DB.Tbl.Co.where:unknown where-clause item: o=",o);
					}
					else TL.tl().error(null,SrvltName,".DB.Tbl.Col.where:for:",o);
					i++;
				}//for
				return b;}
		}//enum Co

		/**output to jspOut one row of json of this row*/
		public void outputJson(){try{TL.tl().getOut().o(this);}catch(IOException x){TL.tl().error(x,"moh.DB.Tbl.outputJson:IOEx:");}}
		/**output to jspOut rows of json that meet the 'where' conditions*/
		public void outputJson(Object...where){try{
			Json.Output o=TL.tl().getOut();
			o.w('[');boolean comma=false;
			for(Tbl i:query(where)){
				if(comma)o.w(',');else comma=true;
				i.outputJson();}
			o.w(']');
		} catch (IOException e){TL.tl().error(e,SrvltName,".DB.Tbl.outputJson:");}
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
				tl.error( ex,SrvltName,".DB.Tbl.check" );} }

		public static boolean exists(Object[]where,String dbtName){return exists(where,null,dbtName);}

		public static boolean exists(Object[]where,CI[]groupBy,String dbtName){
			boolean b=false;
			int n=0;
			try{n=count( where,groupBy,dbtName );}catch ( Exception ex ){}
			b=n>0;
			return b;
		}
	}//class Tbl
}//class DB

}//Srvlt
