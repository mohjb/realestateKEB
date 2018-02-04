<%@ page import="java.io.*
,java.lang.annotation.Annotation
,java.lang.reflect.Field
,java.lang.reflect.Method
,java.net.URL
,java.sql.*
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
,org.apache.commons.fileupload.servlet.ServletFileUpload
,com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource"
%><%TL.run(request,response,session,out,pageContext);%><%!
/** * Created by mbohamad on 19/07/2017.*/
public static class TL
{public static final String Name=App.packageName+".TL";

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
			Class cl=op.getDeclaringClass();Class[]ca={TL.class,String.class};
			Annotation[][]prmsAnno=op.getParameterAnnotations();
			int n=prmsAnno==null?0:prmsAnno.length,i=-1;
			Object[]args=new Object[n];
			for(Annotation[]t:prmsAnno)try{
				Op pp=t.length>0&&t[0] instanceof Op?(Op)t[0]:null;
				Class c=prmTypes[++i];
				String nm=pp!=null?pp.prmName():"arg"+i;//t.getName();
				Object o=null;
				if(pp.prmInstance())
					args[i]=cl.getMethod( "prmInstance",ca ).invoke( cl,tl,pp.prmName() );
				else if(TL.DB.Tbl.class.isAssignableFrom(c))
				{TL.DB.Tbl f=(TL.DB.Tbl)c.newInstance();args[i]=f;
					o=tl.json.get(nm);
					if(o instanceof Map)f.fromMap((Map)o);
					else if(o instanceof List)f.vals(((List)o).toArray());
					else if(o instanceof Object[])f.vals((Object[])o);
					else f.readReq("");
				}else
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
			o.restrictedAccess=true;o.accessViolation=0;
			o.o(retVal);
			o.restrictedAccess=false;
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
public Map<String,Object> ssn,usr//DB.Tbl.Usr//public DB.Tbl.Ssn ssn;
,/**accessing request in json-format*/json;//
public Date now;//public long seqObj,seqProp;
//boolean doNew_appTblRow;
/**wrapping JspWriter or any other servlet writer in "out" */
Json.Output out,/**jo is a single instanceof StringWriter buffer*/jo;

//	%><%TL.run(request, response, session, out, pageContext);%><%! // <?
/**Created by moh on 14/7/17.*/
public static class App {
	static final String packageName= "dev201801",AppNm=packageName+".App";

	static void staticInit(){ TL.registerOp( App.class);  TL.registerOp( JsonStorage.class);  TL.registerOp( MetaTbl.class); }

	static{staticInit();}

	/**
	 * in JsonStorage-table the main app-entery has key="app",
	 * and in this entry there is a javaObjectStream of a Map
	 * and in this map there are the keys: serverFiles,clientFiles,dbts
	 * with clientFiles is associated a List, having maps, each map
	 * may have key "content" text , and , the map may have key "include" list
	 * */
	@Op public void clientOutput(@Op(prmName = "app")String app,@Op(prmName = "file")String file){

	}

	/**
	 * in JsonStorage-table the main app-entery has key="app",
	 * and in this entry there is a javaObjectStream of a Map
	 * and in this map there are the keys: serverFiles,clientFiles,dbts
	 * with serverFiles is associated a List, having maps, each map represents a file&content like a jsp-include
	 * */
	@Op public void generateJsp(@Op(prmName = "app")String app,@Op(prmName = "url")String url){}

	/**
	 *	db-tbl ORM wrapper
	 */
	public static class JsonStorage extends TL.DB.Tbl</**primary key type*/String> {
		public static final String dbtName="JsonStorage";
		@Override public String getName(){return dbtName;}
		@Override public CI pkc(){return C.key;}
		public static enum ContentType{txt,jsonObj,jsonArray,key,num,real,date,b64,javaObjectStream;}
		@F public String app,key;@F ContentType typ;@F public Object value;

		@Override public String pkv(){return key;}
		@Override public String pkv(String v){return key=v;}

		public enum C implements CI{app,key, value;
			@Override public Field f(){return Co.f(name(), JsonStorage.class);}
			@Override public String getName(){return name();}
			@Override public Class getType(){return String.class;}
		}//C
		@Override public C[]columns(){return C.values();}

		@Override public List creationDBTIndices(TL tl){
			return TL.Util.lst(TL.Util.lst(
				"varchar(255) PRIMARY KEY NOT NULL DEFAULT '-' "//app
				,"varchar(255) PRIMARY KEY NOT NULL DEFAULT '-' "//key
				,"enum('txt','jsonObj','jsonArray','key','num','real','date','b64','javaObjectStream') NOT NULL DEFAULT 'txt' "//typ
				,"blob"));//value
			/*
			CREATE TABLE `JsonStorage` (
			`key` varchar(255) PRIMARY KEY NOT NULL DEFAULT '-',
			`value` text NOT NULL DEFAULT '-'
			) ENGINE=InnoDB DEFAULT CHARSET=utf8;
			*/
		}

		static{registered.add(JsonStorage.class);}
		public static JsonStorage sttc=new JsonStorage( );

		public static JsonStorage loadBy(String app,String key){
			JsonStorage j=new JsonStorage(),x=null;
			for(TL.DB.Tbl i:j.query(where( C.app,app,C.key,key )))
				x=j;
			return x;}

		public static Object prmInstance(TL tl,String prmName){
			Object o=tl.json.get( prmName )
				,app=tl.json.get("app")
				,key=tl.json.get("key");
			JsonStorage j=key instanceof JsonStorage
				              ?(JsonStorage)key
				              :app instanceof String && key instanceof String
					               ?loadBy( (String)app,(String)key )
					               :null;
			if(j!=null && key instanceof String)
				tl.json.put( "key",j );
			return j!=null && "key".equals( prmName )?j:o;}

		@Op public static String
		JspApp_create(@Op(prmName = "app") String appName,@Op(prmName = "keys") Map<String,Object>keys){return null;}

		//@Op public static String JspApp_generateJspFile_and_clientFiles(String appName){return null;}

		@Op public static String JspApp_generateJspFile_and_clientFiles(@Op(prmName = "app") String appName){return null;}

		@Op public static List<String>
		JspApp_listApps() throws SQLException {
			return DB.q1colTList("select distinct `"+C.app+"` from `"+dbtName+"`",String.class);}

		@Op public static List<String>
		JspApp_listKeys(@Op(prmName = "app") String appName) throws SQLException {
			return DB.q1colTList("select distinct `"+C.key+"` from `"+dbtName+"` where `"+C.app+"`=?",String.class,appName);}

	//@Op public static Map<String,Map<String,Object>>JspApp_getKeys(String appName,List<String>keys){return null;}
	@Op public static void JspApp_getKeys(@Op(prmName = "app") String appName
		,@Op(prmName = "keys") List<String>keys,TL tl) throws SQLException {
		StringBuilder b=new StringBuilder("select * from `" +dbtName
			+"` where `"+C.app+"`=? and `key` in (");
		boolean comma =false;
		for(String k:keys){
			if(comma)b.append(',');else comma=true;
			b.append('`').append(k).append('`');}
		DB.q2json(b.append(')').toString(),appName);
		tl.h.r("responseDone",true);}

	@Op public static int JspApp_setKeys(@Op(prmName
		= "keys") List<Map>keys,TL tl) throws Exception {
		int r=0;JsonStorage j=new JsonStorage();
		for(Map m:keys)try{
			j.fromMap(m).save();r++;}catch(Exception ex){
			tl.error(ex,"JspApp_setKeys",m);}
		return r;}

	}//class JsonStorage

	/**
	 *	db-Table Wrapper , with Integer primary-key
	 */
	public static class MetaTbl extends TL.DB.Tbl<Integer> {
		public static Map</**database-name*/String,Map</**table-name*/String,MetaTbl>>dbs=new HashMap<String,Map<String,MetaTbl>>();

		public static Object prmInstance(TL tl,String prmName){
			Object o=tl.json.get( prmName )
				,d=tl.json.get("dbName")
				,t=tl.json.get("dbtName");
			Map</**table-name*/String,MetaTbl>
				db=d instanceof String
					   ?dbs.get((String)d):(Map)d;
			if(db!=null && d instanceof String)
				tl.json.put("dbName",db);
			MetaTbl j=t instanceof String&&db!=null
				          ?db.get((String)t):(MetaTbl ) t;
			if(j!=null && t instanceof String)
				tl.json.put( "dbtName",j );
			return "dbName".equals( prmName )
				       ?(db==null?d:db)
				       :"dbtName".equals( prmName )
					        ?(j!=null?j:t)
					        :o;}

		public String dbName,dbtName="MetaTbl",comment;
		@Override public String getName(){return dbtName;}
		@Override public CI pkc(){return pkc[0];}//cols==null||pkc==null||pkc.length<1||pkc[0]>=cols.length?null:cols[pkc[0]];
		/**indicies of the columns which are the primary key in cols,
		 * if the table has compound keys(more than one column as a primary key),
		 * then this array is of length greater than one,
		 * if the the primary key is only one column then this array is of length one
		 * */C[]pkc;
		C[]cols;
		I[]indices;// **forgein keys*/Map<C,Map<String,String>>fk;
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

		@Override public Integer pkv(){return null;}//pkc!=null&&pkc.length>0?pkc[0]:null;
		@Override public Integer pkv(Integer v){return null;}//pkc!=null&&pkc.length>0?pkc[0]:null;

		public class C implements CI,Json.Output.JsonOutput{
			String name,type,comment,creation;Class clss;int i;

			public C(String name,String type,String comment,int i){
				this.name=name;this.type=type;this.comment=comment;this.i=i;
				clss=T.t( type ).c;}

			public C(String name,String type,int i,String comment,String creation){
				this.name=name;this.type=type;this.i=i;
				this.comment=comment;this.creation=creation;
				clss=T.t( type ).c;}

			@Override public Field f(){return null;}
			@Override public String getName(){return name;}
			@Override public Class getType(){return clss;}
			@Override public String toString() {
				try {TL tl = TL.tl();
					return tl.jo().clrSW().o( this ).toStrin_();
				} catch ( Exception ex ) {}
				return "{\"name\":\""+name+"\",\"type\":\""+type+"\",\"i\":"+i+",\"comment\":\""+comment+"\",\"creation\":\""+creation+"\"}";
			}
			public Map asMap(){return TL.Util.mapCreate( "",name,"",type,"comment",comment,"i",i,"creation",creation );}
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

		/**to capture info about db-table-indices*/public class I extends C{
			C[]cols;public I(C[]cols,String...a){super(a[0],a[1],0,a[2],a[3]);}
			@Override public String toString() {
				try {TL tl = TL.tl();
					return tl.jo().clrSW().o( this ).toStrin_();
				} catch ( Exception ex ) {}
				return "{\"name\":\""+name+"\",\"type\":\""+type
					       +"\",\"i\":"+i+",\"comment\":\""+comment
					       +"\",\"creation\":\""+creation+"\",\"cols\":\""+cols+"\"}";
			}
			public Map asMap(){return TL.Util.mapCreate( "name",name
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

		public static List asListOfMaps( C[]a){List l=TL.Util.lst(  );for ( C c:a )l.add( c.asMap() );return l; }
		public static List asListOfNames(C[]a){List l=TL.Util.lst(  );for ( C c:a )l.add( c.name );return l; }
		public static Map asMaps(Map<C,Map<String,String>>a){
			Map m=TL.Util.mapCreate(  );
			for ( C c:a.keySet() )m.put( c.name,a.get( c ) );
			return m; }


		@Override public List creationDBTIndices(TL tl){return null;}
		public enum T{Array(Types.ARRAY,Object[].class),
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
			public static Map<Integer,T>mi;
			public static Map<String,T>ms;
			public static Map<Integer,Class>ci;
			public static Map<String,Class>cs;
			//T(java.sql.Types t,Class c){this.t=t;this.c=c;}
			T(int t,Class c){it=t;this.c=c;}
			T(int t,Class c,String...options){it=t;this.c=c;this.options=options;}

			public static T t(String p){
				for(T x:values())if(x.name().equals( p ))return x;
				return null;}
			public static T t(int p){
				for(T x:values())if(x.it==p)return x;
				return null;}
			public static void initMaps(){
				T[]a=values();int n=a.length;
				if(ms==null){ms=new HashMap<String,T>(n);//if(mi==null)
					mi=new HashMap<Integer,T>(n);//if(cs==null)
					cs=new HashMap<String,Class>(n);//if(ci==null)
					ci=new HashMap<Integer,Class>(n);
					for(T x:a){
						ms.put( x.name(),x  );mi.put( x.it,x  );
						cs.put( x.name(),x.c);ci.put( x.it,x.c);}}}
		}//enum T

		public static Map<String,MetaTbl>initFromDb(String dbName){
			Map<String,MetaTbl>tbls=new HashMap<String,MetaTbl>();//List<MetaTbl>lt=new LinkedList<>(  );
			List l=new LinkedList<C>(  );
			try {Connection c = DB.c();
				java.sql.DatabaseMetaData databaseMetaData = c.getMetaData();
				ResultSet result = databaseMetaData.getTables(
					null, null, null, null );
				while ( result.next() ) {MetaTbl m=new MetaTbl();
					m.dbName=result.getString( "TABLE_CAT" );
					m.dbName=result.getString( "TABLE_SCHEM" );
					m.dbtName=result.getString("TABLE_NAME" );
					m.comment=result.getString("REMARKS" );
					tbls.put( m.dbtName,m);
				}
				result.close();
				for(MetaTbl m:tbls.values())try
				{	result = databaseMetaData.getColumns(
					null, null, m.dbtName, null );
					while ( result.next() ) {
						C col = m.new C(result.getString( "COLUMN_NAME" )
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

					result = databaseMetaData.getImportedKeys(null,null, m.dbtName );
					while ( result.next() ){
						//if(m.fk==null)m.fk=new HashMap<C,Map<String,String>>(  );
						String fk=result.getString( "FKCOLUMN_NAME" )
							,tbl=result.getString( "PKTABLE_NAME" )
							,clm=result.getString( "PKCOLUMN_NAME" );
						Map<String,String>x=new HashMap<String,String>( );
						x.put( "table",tbl );
						x.put( "column",clm );
						x.put( "fk",fk );
						l.add( fk );//m.fk.put( m.col(fk),x);
					}
					result.close();
				}catch ( SQLException e ) {
					TL.tl().error( e,"MetaTbl[]initFromDb",m ); }
			} catch ( SQLException e ) {
				TL.tl().error( e,"MetaTbl[]initFromDb" ); }
			dbs.put(dbName,tbls);
			return tbls;}

		//public static MetaTbl initFromJson(String dbt,Map json){return null;}
		//public static MetaTbl initDbFromJson(String dbName,Map json){return null;}
		//public static MetaTbl initFromMap(String dbName,String dbtName,List<Map<String,String>>cols){ MetaTbl m=new MetaTbl ();m.dbName=dbName;m.dbtName=dbtName;return m;}

		public static Map dbAsMap(String dbName){
			Map</**table-name*/String,MetaTbl>db=dbs.get( dbName );
			Map m=null;
			if(db!=null){
				m=TL.Util.mapCreate( "dbName",dbName );
				for(MetaTbl i:db.values())
					m.put( i.dbtName,i.asMap(  ) );
			}return m;}

		@Override public Map asMap(){//String dbtName
			return TL.Util.mapCreate( "dbtName",dbtName,"dbName",dbtName
				, "cols",asListOfMaps( cols )
				, "pk", asListOfNames( pkc)
				//, "fk", asMaps( fk)
			);}

		@Override public TL.DB.Tbl fromMap(Map m){
			dbName=(String)m.get( "dbName" );
			dbtName=(String)m.get( "dbtName" );
			List l=(List)m.get( "cols" );
			cols=new C[l.size()];int i=-1;
			for(Object k:l){
				Map x=(Map)k;
				cols[++i]=new C((String)x.get( "name" )
					               ,(String)x.get( "type" )
					               ,(String)x.get( "comment" )
					               ,(int)x.get( "i" ));}
			l=(List)m.get( "pk" );
			pkc=new C[l.size()];i=-1;
			for(Object k:l)pkc[++i]=col((String)k);
		/*Map x=(Map)m.get( "fk" );
		//fk=new HashMap<>(  );
		for(Object o:x.keySet()){
			C c=col( (String)o );
			//fk.put( c,(Map)x.get( o ) );
		}*/
			return this;
		}

		int createTable(){
			StringBuilder b=new StringBuilder( "create table `")
				                .append(dbName ).append( "`.`")
				                .append(dbtName ).append( "`(" );
			for ( C c:cols ){if(c.i>0)b.append( ',' );
				T t=T.t( c.type );
				b.append( '`' ).append( c.name ).append( "` " ).append( c.type ).append( ' ' ).append( t.txt );
			}
			int i=-1;b.append( ",primary key(" );
			for ( C c:pkc ){if(++i>0)b.append( ',' );
				b.append( '`' ).append( c.name ).append( "` " ); }
			//for ( C c:fk){}
			b.append( ");" );i=-1;
			try {
				i=TL.DB.x( b.toString() );
			}catch ( Exception ex ){}
			return i;
		}

		public class Row extends TL.DB.Tbl<Integer> {
			@F public Object[]vals;
			@Override public String getName(){return dbtName;}
			@Override public CI pkc(){return pkc[0];}
			@Override public Integer pkv(){return vals!=null&&vals.length>pkc[0].i
				                                      &&!(vals[pkc[0].i]instanceof Integer)?(Integer ) vals[pkc[0].i]:null;}
			@Override public Integer pkv(Integer v){return vals!=null
				                                               &&vals.length>pkc[0].i?(Integer)(vals[pkc[0].i]=v):null;}
			@Override public C[]columns(){return cols;}
			@Override public List creationDBTIndices(TL tl){return null;}
			public MetaTbl tbl(){return MetaTbl.this;}

			@Override public Object[]vals(){return vals;}
			@Override public DB.Tbl vals ( Object[]p){vals=p;return this;}

			//@Override public static Field[]fields(Class<?> c){return null;}
			//@Override public static List<Field>fields(Class<?> c,List<Field>l){return null;}
			public int cix(CI p){int i=-1;for ( C c:cols )if(c==p)return i;return -1;}
			public int cix(String p){int i=-1;for ( C c:cols )if(c.name!=null&&c.name.equals( p ))return i;return -1;}

			@Override public DB.Tbl v(CI p,Object v){vals[cix(p)]=v;return this;}
			@Override public Object v(CI p){return vals[cix(p)];}
			@Override DB.Tbl v(Field p,Object v){throw new IllegalArgumentException( "Field is not implemented" );}
			@Override Object v(Field p){throw new IllegalArgumentException( "Field is not implemented" );}
		}//class Row


		@Op static public List</*String*/Object>
		DB_names()throws SQLException{
			return TL.DB.q1colList( "show databases" );}

		@Op static public T[]
		colTypes(){return T.values();}

		@Op static public /*List<Map<String,Object>>*/Map</**table-name*/String,MetaTbl>
		DB_get(@Op(prmName = "dbName") String dbName){
			return initFromDb( dbName );}

		@Op static public int
		DB_create(@Op(prmName = "dbName") String dbName)throws SQLException{
			int i= TL.DB.x( "create database `"+dbName.replaceAll( "`"," " )+"`" );
			if(i>0)dbs.put(dbName,new HashMap</**table-name*/String,MetaTbl>());
			return i;}

		@Op static public int
		DB_drop(@Op(prmName = "dbName") String dbName)throws SQLException{
			int i= TL.DB.x( "drop database `"+dbName.replaceAll( "`"," " )+"`" );
			if(i>0)dbs.remove( dbName );
			return i;}

		@Op static public MetaTbl
		DBTbl_create(@Op(prmName = "dbName") String dbName
			            ,@Op(prmName="dbtName") String dbtName
			            ,@Op(prmName="cols") List<Map<String,String>>cols
			            ,@Op(prmName = "pk") List<String>pk
			            ,@Op(prmName = "fk") Map<String,Map<String,String>>fk
			            ,TL tl){
			MetaTbl m=new MetaTbl();
			m.fromMap(tl.json  );
			m.createTable();
			return m;
		}

		@Op static public int
		DBTbl_drop(@Op(prmName = "dbName") String dbName
			          ,@Op(prmName = "dbtName") String dbtName) throws SQLException{
			return  TL.DB.x( "drop table `"+dbName.replaceAll( "`"," " )+"`.`"+dbtName.replaceAll( "`"," " )+"`" );}

		@Op static public int
		DBTbl_rename(@Op(prmName = "dbName") String dbName
			            ,@Op(prmName = "dbtName") String dbtName
			            ,@Op(prmName = "newName") String newName)throws SQLException{
			return  TL.DB.x( "rename table `"+dbName.replaceAll( "`"," " )+"`.`"+dbtName.replaceAll( "`"," " )+"` to `"+newName.replaceAll( "`"," " )+"`" );}

		@Op static public int
		DBTblCol_drop(@Op(prmName = "dbName") String dbName
			             ,@Op(prmName = "dbtName") String dbtName
			             ,@Op(prmName = "col") String col){
			return -1;}

		@Op static public int
		DBTblCol_insert(@Op(prmName = "dbName") String dbName
			               ,@Op(prmName = "dbtName") String dbtName
			               ,@Op(prmName = "col") String col
			               ,@Op(prmName = "beforeCol") String beforeCol
			               ,@Op(prmName = "def") Map<String,String>def){
			return -1;}

		@Op static public int
		DBTblCol_add(@Op(prmName = "dbName") String dbName
			            ,@Op(prmName = "dbtName") String dbtName
			            ,@Op(prmName = "") String col
			            ,@Op(prmName = "") Map<String,String>def){
			return -1;}

		@Op static public int
		DBTblCol_alter(@Op(prmName = "dbName") String dbName
			              ,@Op(prmName = "dbtName") String dbtName
			              ,@Op(prmName = "col") String col
			              ,@Op(prmName = "def") Map<String,String>def){
			return -1;}

		@Op static public int
		DBTblCol_rename(@Op(prmName = "dbName") String dbName
			               ,@Op(prmName = "dbtName") String dbtName
			               ,@Op(prmName = "col") String col
			               ,@Op(prmName = "newName") String newName){
			return -1;}

		@Op static public Object[]
		DBTRow_get(@Op(prmName = "dbName") String dbName
			          ,@Op(prmName = "dbtName") String dbtName
			          ,@Op(prmName = "pkv") Object[]pkv){
			return null;}

		@Op static public int
		DBTRow_insert(@Op(prmName = "dbName") String dbName
			             ,@Op(prmName = "dbtName") String dbtName
			             ,@Op(prmName = "vals") Object[]vals){
			return -1;}

		@Op static public int
		DBTRow_update(@Op(prmName = "dbName") String dbName
			             ,@Op(prmName = "dbtName") String dbtName
			             ,@Op(prmName = "set") Object[]set
			             ,@Op(prmName = "where") Object[]where){
			return -1;}

		@Op static public int
		DBTRow_delete(@Op(prmName = "dbName") String dbName
			             ,@Op(prmName = "dbtName") String dbtName
			             ,@Op(prmName = "where") Object[]where){
			return -1;}

		@Op static public List<Object[]>
		DBT_query(@Op(prmName = "dbName") String dbName
			         ,@Op(prmName = "dbtName") String dbtName
			         ,@Op(prmName = "where") Object[]where
			         ,@Op(prmName = "groupBy") Object[]groupBy
			         ,@Op(prmName = "orderBy") Object[]orderBy){
			return null;}

	}//class MetaTbl Table.Wrapper.Integer
}//class App



enum context{ROOT(
	                 "C:\\apache-tomcat-8.0.15\\webapps\\ROOT\\"
	                 ,"/Users/moh/Google Drive/air/apache-tomcat-8.0.30/webapps/ROOT/"
	                 ,"/public_html/i1io/"
	                 ,"D:\\apache-tomcat-8.0.15\\webapps\\ROOT\\"
);
	String str,a[];context(String...p){str=p[0];a=p;}
	enum DB{
		pool("dbpool-"+Name)
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
			t.error(ex,Name,".context.getRealPath:",path);}
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
			t.error(ex,Name,".context.getContextIndex:");}
		return -1;}
	//***/static Map<DB,String> getContextPack(TL t,List<Map<DB,String>>a){return null;}
}//context
//TL member variables


/** annotation to designate a java method as an ajax/xhr entry point of execution*/
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Op{
	boolean useClassName() default true;
	//boolean caseSensitive() default true;
	boolean nestJsonReq() default true;//if false , then only the returned-value from the method call is json-stringified as a response body, if true the returned-value is set in the json-request with prop-name "return"
	boolean usrLoginNeeded() default false;//true;
	String httpMethod() default "";
	String urlPath() default "\n"; //if no method name match from parameters, then this string is mathed with the requested url, "*" means method will match any request path
	String prmName() default "";
	boolean prmInstance() default false;
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
			if(!"\n".equals(op.urlPath()))
				url.put(op.urlPath(),m);
			if(!"".equals(op.httpMethod()))
				mth.put(op.urlPath(),m);
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
	TL p;if(ops==null || ops.size()==0)App.staticInit();
	tl.set(p=new TL(r,response,out!=null?out:response.getWriter()));//Class c=App.class;c=App.Prop.class;c=App.JsonStorage.class;
	if(App.JsonStorage.sttc==null)
		p.log( Name,".Enter:App.JsonStorage.sttc=",App.JsonStorage.sttc );
	//if(App.MetaTbl.sttc==null)p.log( Name,".Enter:App.MetaTbl.sttc=",App.MetaTbl.sttc );
	p.onEnter();
	return p;}
private void onEnter()throws IOException {
	h.ip=h.getRequest().getRemoteAddr();
	now=new Date();//seqObj=seqProp=now.getTime();
	try{Object o=h.req.getContentType();
		o=o==null?null
			  :o.toString().contains("json")?Json.Prsr.parse(h.req)
				   :o.toString().contains("part")?h.getMultiParts():null;
		json=o instanceof Map<?, ?>?(Map<String, Object>)o:null;//req.getParameterMap() ;
		h.logOut=h.var("logOut",h.logOut);
		if(h.getSession().isNew()){
			DB.Tbl.check(this);
			//App.Domain.loadDomain0();
		}
		usr=(Map)h.s("usr");//(App.JsonStorage)
	}catch(Exception ex){error(ex,Name,".onEnter");}
	//if(pages==null){rsp.setHeader("Retry-After", "60");rsp.sendError(503,"pages null");throw new Exception("pages null");}
	if(h.logOut)out.w(h.comments[0]).w(Name).w(".tl.onEnter:\n").o(this).w(h.comments[1]);
}//onEnter
private void onExit(){usr=null;h.ip=null;now=null;h.req=null;json=null;out=jo=null;h.rspns=null;}//ssn=null;
/**unsets the localThread, and unset local variables*/
public static void Exit()//throws Exception
{TL p=TL.tl();if(p==null)return;
	DB.close(p);//changed 2017.7.17
	p.onExit();tl.set(null);}

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
		else if(URL.class.isAssignableFrom(c))try {return new URL(
			                                                         "file:" +TL.tl().h.getServletContext().getContextPath()+'/'+s);}
		catch (Exception ex) {TL.tl().error(ex,Name,".Util.parse:URL:p=",s," ,c=",c);}
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
			String path="";//App.UploadPth;//app(this).getUploadPath();
			String real=TL.context.getRealPath(TL.this, path);//getServletContext().getRealPath(path);
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

/**get a pooled jdbc-connection for the current Thread, calling the function dbc()*/
Connection dbc()throws SQLException {
	TL p=this;//Object s=context.DB.reqCon.str,o=p.s(s);
	Object[]a=DB.stack(p,null);//o instanceof Object[]?(Object[])o:null;
	//o=a==null?null:a[0];
	if(a[0]==null)//o==null||!(o instanceof Connection))
		a[0]=DB.c();
	return (Connection)a[0];}

public static class DB {
	/**returns a jdbc pooled Connection.
	 uses MysqlConnectionPoolDataSource with a database from the enum context.DB.url.str,
	 sets the pool as an application-scope attribute named context.DB.pool.str
	 when first time called, all next calls uses this context.DB.pool.str*/
	public static synchronized Connection c()throws SQLException {
		TL t=tl();
		Object[]p=null,a=stack(t,null);//Object[])t.s(context.DB.reqCon.str);
		Connection r=(Connection)a[0];//a ==null?null:
		if(r!=null)return r;
		MysqlConnectionPoolDataSource d=(MysqlConnectionPoolDataSource)t.h.a(context.DB.pool.str);
		r=d==null?null:d.getPooledConnection().getConnection();
		if(r!=null)
			a[0]=r;//changed 2017.07.14
		else try
		{try{int x=context.getContextIndex(t);
			t.log(Name,".DB.c:1:getContextIndex:",x);
			if(x!=-1)
			{	p=c(t,x,x,x,x);t.log(Name,".DB.c:1:c2:",p);
				r=(Connection)p[1];
				return r;}
		}catch(Exception e){t.log(Name,".DB.MysqlConnectionPoolDataSource:1:",e);}
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
						}catch(Exception e){t.log(Name,".DB.MysqlConnectionPoolDataSource:",idb,",",isr,",",iun,ipw,t.h.logOut?p[2]:"",",",e);}
		}catch(Throwable e){t.error(e,Name,".DB.MysqlConnectionPoolDataSource:throwable:");}//ClassNotFoundException
		if(t.h.logOut)t.log(context.DB.pool.str+":"+(p==null?null:p[0]));
		if(r==null)try
		{r=java.sql.DriverManager.getConnection
			                          ("jdbc:mysql://"+context.DB.server.str
				                           +"/"+context.DB.dbName.str
				                          ,context.DB.un.str,context.DB.pw.str
			                          );Object[]b={r,null};
			t.h.s(context.DB.reqCon.str,b);
		}catch(Throwable e){t.error(e,Name,".DB.DriverManager:");}
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
		TL t=tl();Connection c=t.dbc();
		PreparedStatement r=c.prepareStatement(sql);if(t.h.logOut)
			t.log(Name,"("+t+").DB.P(sql="+sql+",p="+p+",odd="+odd+")");
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
		push(r,tl());
		return r;}
	static Object[]stack(TL tl,Connection c){return stack(tl,c,true);}
	static Object[]stack(TL tl,Connection c,boolean createIfNotExists){
		return stack(tl,c,createIfNotExists,false);}
	static Object[]stack(TL tl,Connection c,boolean createIfNotExists,boolean deleteArray){
		if(tl==null)tl=tl();Object o=context.DB.reqCon.str;
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
	}catch (Exception ex){tl.error(ex,Name,".DB.push");}}

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
	public static void close(ResultSet r){close(r,tl(),false);}
	public static void close(ResultSet r,boolean closeC){close(r,tl(),closeC);}
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
		TL t=tl();ResultSet s=null;List<Object[]> r=null;try{s=R(sql,p);Object[]a;r=new LinkedList<Object[]>();
			int cc=cc(s);while(s.next()){r.add(a=new Object[cc]);
				for(int i=0;i<cc;i++){a[i]=s.getObject(i+1);
				}}return r;}finally{close(s,t);//CHANGED:2015.10.23.16.06:closeRS ;
			if(t.h.logOut)try{t.log(t.jo().w(Name).w(".DB.L:sql=").o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}
			catch(IOException x){t.error(x,Name,".DB.List:",sql);}}}

	public static List<Integer[]>qLInt(String sql,Object...p)throws SQLException{return qLInt(sql,p);}//2017.07.14
	public static List<Integer[]>QLInt(String sql,Object[]p)throws SQLException{//2017.07.14
		TL tl=tl();
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
			if(tl.h.logOut)try{tl.log(tl.jo().w(Name).w(".DB.Lt:sql=")
				                          .o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}
			catch(IOException x){tl.error(x,Name,".DB.Lt:",sql);}
		}
	}

	public static List<Object> q1colList(String sql,Object...p)throws SQLException
	{ResultSet s=null;List<Object> r=null;try{s=R(sql,p);r=new LinkedList<Object>();
		while(s.next())r.add(s.getObject(1));return r;}
	finally{TL t=tl();close(s,t);if(t.h.logOut)
		try{t.log(t.jo().w(Name).w(".DB.q1colList:sql=")//CHANGED:2015.10.23.16.06:closeRS ;
			          .o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}catch(IOException x){t.error(x,Name,".DB.q1colList:",sql);}}}
	public static <T>List<T> q1colTList(String sql,Class<T>t,Object...p)throws SQLException
	{ResultSet s=null;List<T> r=null;try{s=R(sql,p);r=new LinkedList<T>();//Class<T>t=null;
		while(s.next())r.add(
			s.getObject(1,t)
			//s.getObject(1)
		);return r;}
	finally{TL tl=tl();close(s,tl);if(tl.h.logOut)
		try{tl.log(tl.jo().w(Name).w(".DB.q1colList:sql=")//CHANGED:2015.10.23.16.06:closeRS ;
			           .o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}catch(IOException x){tl.error(x,Name,".DB.q1colList:",sql);}}}
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
	catch(Exception ex){tl().error(ex,Name,".DB.q1Row:",sql);a[i]=s.getString(i+1);}}
		return a;}finally{close(s);}}//CHANGED:2015.10.23.16.06:closeRS ;
	/**returns the result of (e.g. insert/update/delete) sql-statement
	 ,calls dbP() setting the variable-length-arguments values parameters-p
	 ,closes the preparedStatement*/
	public static int x(String sql,Object...p)throws SQLException{return X(sql,p);}
	public static int X(String sql,Object[]p)throws SQLException {
		int r=-1;try{PreparedStatement s=P(sql,p,false);r=s.executeUpdate();s.close();return r;}
		finally{TL t=tl();if(t.h.logOut)try{
			t.log(t.jo().w(Name).w(".DB.x:sql=").o(sql).w(",prms=").o(p).w(",return=").o(r).toStrin_());}
		catch(IOException x){t.error(x,Name,".DB.X:",sql);}}}
	/**output to tl.out the Json.Output.oRS() of the query*/
	public static void q2json(String sql,Object...p)throws SQLException{
		ResultSet s=null;
		TL tl=tl();
		try{
			s=R(sql,p);
			try{
				tl.getOut() .o(s); // (new Json.Output()) // TODO:investigate where the Json.Output.w goes
			}catch (IOException e) {e.printStackTrace();}
		}
		finally
		{close(s,tl);
			if(tl.h.logOut)try{
				tl.log(tl.jo().w(Name).w(".DB.L:q2json=")
					       .o(sql).w(",prms=").o(p).toStrin_());
			}catch(IOException x){tl.error(x,Name,".DB.q1json:",sql);}
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
			try {init(TL.DB.R(sql, p));}
			catch (Exception e) {tl().logo(Name,".DB.ItTbl.<init>:Exception:sql=",sql,",p=",p," :",e);}}
		public ItTbl(ResultSet o) throws SQLException{init(o);}
		public ItTbl init(ResultSet o) throws SQLException
		{row.rs=o;row.m=o.getMetaData();row.row=row.col=0;
			row.cc=row.m.getColumnCount();return this;}
		static final String ErrorsList=Name+".DB.ItTbl.errors";
		@Override public boolean hasNext(){
			boolean b=false;try {if(b=row!=null&&row.rs!=null&&row.rs.next())row.row++;
			else TL.DB.close(row.rs);//CHANGED:2015.10.23.16.06:closeRS ; 2017.7.17
			}catch (SQLException e) {//e.printStackTrace();
				TL t=TL.tl();//changed 2016.06.27 18:05
				final String str=Name+".DB.ItTbl.next";
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
					final String str=Name+".DB.ItTbl.ItRow.next";
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

		public Json.Output jsonOutput(TL.Json.Output o,String ind,String path)throws java.io.IOException{return jsonOutput( o,ind,path,true );}
		public Json.Output jsonOutput(TL.Json.Output o,String ind,String path,boolean closeBrace)throws java.io.IOException{
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
					o.w("}//TL.DB.Tbl&cachePath=\"").p(path).w("\"\n").p(ind);
				else o.w('}');}
			return o; }

		public String toJson(){Json.Output o= tl().jo().clrSW();try {jsonOutput(o, "", "");}catch (IOException ex) {}return o.toString();}

		public Tbl readReq(String prefix){
			TL t=tl();CI[]a=columns();for(CI f:a){
				String s=t.h.req(prefix==null||prefix.length()<1?prefix+f:f.toString());
				Class <?>c=s==null?null:f.getType();
				Object v=null;try {
					if(s!=null)v=Util.parse(s,c);
					v(f,v);//f.set(this, v);
				}catch (Exception ex) {// IllegalArgumentException,IllegalAccessException
					t.error(ex,Name,".DB.Tbl.readReq:t=",this," ,field="
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
			}catch (Exception ex) {tl().error(ex,Name,".DB.Tbl.v(",this,",",p,",",v,")");}
			return this;}

		Object v(Field p){//this is beautiful(tear running down cheek)
			try{return p.get(this);}
			catch (Exception ex) {//IllegalArgumentException,IllegalAccessException
				tl().error(ex,Name,".DB.Tbl.v(",this,",",p,")");return null;}}

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
		//public interface CI extends FI{}//interface CI//			public String prefix();public String suffix();

		public static CI[]cols(CI...p){return p;}
		public static Object[]where(Object...p){return p;}
		public abstract CI pkc();
		public abstract PK pkv();
		public abstract PK pkv(PK v);

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
				TL.DB.Tbl.Co.where(sql, where);
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
				TL.DB.Tbl.Co.where(sql, where);
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
			String dtn=getName();Object o=tl.h.a(Name+":db:show tables");
			if(o==null)
				try {o=TL.DB.q1colList("show tables");
					tl.h.a(Name+":db:show tables",o);
				} catch (SQLException ex) {
					tl.error(ex, Name+".DB.Tbl.checkTableCreation:check-pt1:",dtn);}
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
				tl.log(Name,".DB.Tbl.checkTableCreation:before:sql=",sql);
				int r=TL.DB.x(sql.toString());
				tl.log(Name,".DB.Tbl.checkTableCreation:executedSql:",dtn,":returnValue=",r);
				b=an>2?(List)a.get(2):b;if(an>2)
					for(Object bo:b){
						List c=(List)bo;
						Object[]p=new Object[c.size()];
						c.toArray(p);
						vals(p);
						try {save();} catch (Exception ex) {
							tl.error(ex, Name,".DB.Tbl.checkTableCreation:insertion",c);}
					}
			}
			} catch (SQLException ex) {
				tl.error(ex, Name,".DB.Tbl.checkTableCreation:errMain:",dtn);}
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
		/**loads one row from the table*/
		public Tbl load(PK pk){
			ResultSet r=null;TL t=tl();
			try{r=DB.r(sql(cols(Co.all), where(pkc()))
				,pk);
				if(r.next())load(r);
				else{t.error(null,Name,".DB.Tbl(",this,").load(pk=",pk,"):resultset.next=false");nullify();}}
			catch(Exception x){t.error(x,Name,".DB.Tbl(",this,"):",pk);}
			finally{DB.close(r,t);}
			return this;}
		public Tbl nullify(){return nullify(columns());}
		public Tbl nullify(CI[]a){for(CI f:a)v(f,null);return this;}
		// /**loads one row from the table*/ Tbl load(){return load(pkv());}

		/**loads one row using column CI c */
		Tbl loadBy(CI c,Object v){
			try{Object[]a=DB.q1row(sql(cols(Co.all),where(c)),v);
				vals(a);}
			catch(Exception x){tl().error(x,Name,".DB.Tbl(",this,").loadBy(",c,",",v,")");}
			return this;}//loadBy

		Tbl save(CI c){
			CI pkc=pkc();
			Object cv=v(c);PK pkv=pkv();TL t=TL.tl();
			if(cv instanceof Map)try
			{String j=t.jo().clrSW().o(cv).toString();cv=j;}
			catch (IOException e) {t.error(e,Name,".DB.Tbl.save(CI:",c,"):");}
			try{DB.x("insert into `"+getName()+"` (`"+pkc+
				         "`,`"+c+"`) values(?"//+Co.m(pkc).txt
				         +",?"//+Co.m(c).txt
				         +")",pkv,cv);
				//Integer k=(Integer)pkv;
				//TL.DB.Tbl.Log.log( TL.DB.Tbl.Log.Entity.valueOf(getName()), k, TL.DB.Tbl.Log.Act.Update, TL.Util.mapCreate(c,v(c)) );
			}catch(Exception x){tl().error(x
				,Name,".DB.Tbl(",this,").save(",c,"):pkv=",pkv);}
			return this;}//save

		/**store this entity in the dbt , if pkv is null , this method uses the max+1 of pk-col*/
		public Tbl save() throws Exception{
			PK pkv=pkv();CI pkc=pkc();boolean nw=pkv==null;//Map old=asMap();
			if(nw){throw new UnsupportedOperationException();/*
				int x=DB.q1int(//"select max(`" +pkc+"`)+1 from `"+getName()+"`"
					sql("max(`"+pkc+"`)+1",null,null,null)
					,1);
				//v(pkc,pkv=x);
				tl().log(Name,".DB.Tbl(",toJson(),").save-new:max(",pkc,") + 1:",x);*/
			}CI[]cols=columns();
			StringBuilder sql=new StringBuilder("insert into`").append(getName()).append("`( ");
			Co.generate(sql, cols);//.toString();
			sql.append(")values(").append(Co.prm.txt);//Co.m(cols[0]).txt
			for(int i=1;i<cols.length;i++)
				sql.append(",").append(Co.prm.txt);//Co.m(cols[i]).txt
			sql.append(")");//int x=
			DB.X( sql.toString(), vals() ); //TODO: investigate vals() for json columns
			tl().log("save",this);//log(nw?TL.DB.Tbl.Log.Act.New:TL.DB.Tbl.Log.Act.Update);
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
			//log(TL.DB.Tbl.Log.Act.Update,old);
			return this;
		}//readReq_save
		public Tbl readReq_saveNew() throws Exception{
			PK pkv=pkv();readReq("");if(pkv()==null&&pkv!=null)pkv(pkv);
			return save();//log(TL.DB.Tbl.Log.Act.Update,old);
		}//readReq_save

		//void log(TL.DB.Tbl.Log.Act act){	Map val=asMap();Integer k=(Integer)pkv();TL.DB.Tbl.Log.log( TL.DB.Tbl.Log.Entity.valueOf(getName()), k, act, val);}
		public int delete() throws SQLException{
			PK pkv=pkv();
			int x=TL.DB.x("delete from `"+getName()+"` where `"+pkc()+"`=?", pkv);
			//log(TL.DB.Tbl.Log.Act.Delete);
			return x;}
		/**retrieve from the db table all the rows that match
		 * the conditions in < where > , create an iterator
		 * , e.g.<code>for(Tbl row:query(
		 * 		Tbl.where( CI , < val > ) ))</code>*/
		public Itrtr query(Object[]where){
			Itrtr r=new Itrtr(where);
			return r;}
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
					tl().error(x,Name,".DB.Tbl(",this,").Itrtr.<init>:where=",where);}
			}
			public Itrtr(Object[]where){a=columns();
				try{rs=DB.R(sql(cols(Co.all),where), where);}
				catch(Exception x){tl().error(x,Name,".DB.Tbl(",this,").Itrtr.<init>:where=",where);}}
			@Override public Iterator<Tbl>iterator(){return this;}
			@Override public boolean hasNext(){boolean b=false;
				try {b = rs!=null&&rs.next();} catch (SQLException x)
				{tl().error(x,Name,".DB.Tbl(",this,").Itrtr.hasNext:i=",i,",rs=",rs);}
				if(!b&&rs!=null){DB.close(rs);rs=null;}
				return b;}
			@Override public Tbl next(){i++;Tbl t=Tbl.this;TL tl=TL.tl();
				if(makeClones)try{
					t=t.getClass().newInstance();}catch(Exception ex){
					tl.error(ex,Name,".DB.Tbl(",this,").Itrtr.next:i=",i,":",rs,":makeClones");
				}
				try{t.load(rs,a);}catch(Exception x){
					tl.error(x,Name,".DB.Tbl(",this,").Itrtr.next:i=",i,":",rs);
					close(rs,tl);rs=null;
				}
				return t;}
			@Override public void remove(){throw new UnsupportedOperationException();}
		}//Itrtr
		/**Class for Utility methods on set-of-columns, opposed to operations on a single column*/
		public enum Co implements CI {//Marker ,sql-preparedStatement-parameter
			uuid("uuid()")
			,now("now()")
			,count("count(*)")
			,all("*")
			,prm("?")
			,password("password(?)")
			,Null("null")
			,lt("<"),le("<="),ne("<>"),gt(">"),ge(">=")
			,or("or"),like("like"),in("in"),maxLogTime("max(`logTime`)")//,and("and"),prnthss("("),max("max(?)")
			;String txt;
			Co(String p){txt=p;}
			@Override public Field f(){return null;}
			@Override public String getName(){return name();}
			@Override public Class getType(){return String.class;}
			public static Field f(String name,Class<? extends Tbl>c){
				//for(Field f:fields(c))if(name.equals(f.getName()))return f;return null;
				Field r=null;try{r=c.getField(name);}catch(Exception x)
				{tl().error(x,Name,".DB.Tbl.f(",name,c,"):");}
				return r;}

			/**generate Sql into the StringBuilder*/
			public static StringBuilder generate(StringBuilder b,CI[]col){
				return generate(b,col,",");}

			static StringBuilder generate(StringBuilder b,CI[]col,String separator){
				if(separator==null)separator=",";
				for(int n=col.length,i=0;i<n;i++){
					if(i>0)b.append(separator);
					if(col[i] instanceof Co)
						b.append(((Co)col[i]).txt);
					else
						b.append("`").append(col[i]).append("`");}
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
							b.append(" (");boolean comma=false;
							for(Object z:l){
								if(comma)b.append( ',' );else comma=true;
								if(z instanceof Number)
									b.append( z );else
									b.append( '\'' )
										.append(
											(z instanceof String?(String)z:z.toString()
											).replaceAll( "'","''" )
										)
										.append( '\'' );
							}b.append(")");
						}else if(o instanceof Co)//o!=null)//if(ln==2 && )
						{	Co m=(Co)o;o=l.get(0);
							if(o instanceof CI || o instanceof Co)
								b.append('`').append(o).append('`');
							else
								tl().log(Name,".DB.Tbl.Co.where:unknown where-clause item:o=",o);
							b.append(m.txt).append("?");
						}else
							tl().log(Name,".DB.Tbl.Co.where:unknown where-clause item: o=",o);
					}
					else tl().error(null,Name,".DB.Tbl.Col.where:for:",o);
					i++;
				}//for
				return b;}
		}//enum Co

		/**output to jspOut one row of json of this row*/
		public void outputJson(){try{TL.tl().getOut().o(this);}catch(IOException x){tl().error(x,"moh.TL.DB.Tbl.outputJson:IOEx:");}}
		/**output to jspOut rows of json that meet the 'where' conditions*/
		public void outputJson(Object...where){try{
			TL.Json.Output o=TL.tl().getOut();
			o.w('[');boolean comma=false;
			for(Tbl i:query(where)){
				if(comma)o.w(',');else comma=true;
				i.outputJson();}
			o.w(']');
		} catch (IOException e){tl().error(e,Name,".DB.Tbl.outputJson:");}
		}//outputJson(Object...where)
		public static List<Class<? extends Tbl>>registered=new LinkedList<Class<? extends Tbl>>();
		static void check(TL tl){
			for(Class<? extends Tbl>c:registered)try
			{String n=c.getName(),n2=".checkDBTCreation."+n;
				if( tl.h.a(n2)==null){
					Tbl t=c.newInstance();
					t.checkDBTCreation(tl);
					tl.h.a(n2,tl.now);
				}}catch(Exception ex){tl.error( ex,Name,".DB.Tbl.check" );}
		}

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


public Json.Output o(Object...a)throws IOException{if(out!=null&&out.w!=null)for(Object s:a)out.w.write(s instanceof String?(String)s:String.valueOf(s));return out;}
public static class Json{
	public static class Output
	{ public interface JsonOutput{ public Json.Output jsonOutput( Json.Output o, String ind, String path ) throws IOException ;}

		public Writer w;public boolean restrictedAccess=false;int accessViolation=0;
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
			else if(a instanceof JsonOutput)((JsonOutput)a).jsonOutput(this,ind,path);//oDbTbl((TL.DB.Tbl)a,ind,path);
				//else if(a instanceof TL.DB.Tbl)((TL.DB.Tbl)a).jsonOutput(this,ind,path);//oDbTbl((TL.DB.Tbl)a,ind,path);
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
				if(n.startsWith("get")&&m.getParameterTypes().length==0)//.getParameterCount()
					w("\n").w(i2).w(",").p(n).w(':').o(m.invoke(o), i3, path+'.'+n);}
			if(c)w("}//").p(o.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind);
			else w("}");}catch(Exception ex){TL.tl().error(ex,"Json.Output.Bean:");}return this;}
		Output oResultSet(ResultSet o,String ind,String path)
		{final boolean c=comment;try{String i2=c?ind+"\t":ind;
			TL.DB.ItTbl it=new TL.DB.ItTbl(o);
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
			if(c)w("]//").p(o.getClass().getName()).w("&cachePath=\"").p(path).w("\"\n").p(ind)
				     ;else w("]");}catch(Exception ex){TL.tl().error(ex,"Json.Output.ResultSetMetaData:");}return this;}
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
			Prsr j=new Prsr();j.rdr=p.getReader();return j.parse();}

		public static Object parseItem(Reader p)throws Exception{
			Prsr j=new Prsr();j.rdr=p;return j.parseItem();}

		public Object load(File f){
			long l=(this.f=f).lastModified();
			if( lm>=l)return o;
			lm=l;fz=f.length();
			try{rdr= new FileReader(f);
				o=parse();
			}catch(Exception ex){}
			return o;}

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
			return r;
		}

		public Object parseItem()throws Exception{
			Object r=null;int i;skipWhiteSpace();switch(c)
			{ case '"':case '`':r=extractStringLiteral();break;
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
				if(c!='\0'&&c!=bo){
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