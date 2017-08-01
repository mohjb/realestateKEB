package aswan2017;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;

//%><%aswan2017.TL.run(request, response, session, out, pageContext);%><%!
/**Created by moh on 14/7/17.*/
public class App {

static final String AppNm="aswan2017.App",UploadPth="/aswan/uploads/";

public static @TL.Op(usrLoginNeeded=false) Domain.Usr login
	(@TL.Op(prmName="un")String un
	,@TL.Op(prmName="pw")String pw,TL tl){
	Domain d=Domain.loadDomain0();
	Domain.Usr u=d.allUsrs.get( un );
	if(u!=null && pw!=null && pw.equals( u.propStr( "pw" ) )){
		tl.usr=u;
		tl.h.s( "usr",u );
		return u;
	}
	return null;}

 public static @TL.Op boolean logout(TL tl){
	Domain.Usr u=tl.usr;tl.h.s("usr",tl.usr=null);
	Object[]a=TL.DB.stack(tl,null,false,true);
	a[1]=null;
	TL.DB.close( (java.sql.Connection)a[0],tl );
	tl.h.getSession().setMaxInactiveInterval( 0 );
	return u!=null;}

/**http-get-method , poll-server
 * , of param"lastPoll" is present, then call lastPoll
 * , if param"updateCols" present then call updateCols
 * , if param"putEntities" present then call putEntities
 * , if param"getIds" present then call getIds
 * , if param"getEntities" present then call getEntities
 */
 public static @TL.Op(urlPath = "*") Map poll
 (@TL.Op(prmName="getLogs")List getLogs
	,@TL.Op(prmName="writeObjs")List writeObjs
	,@TL.Op(prmName="getIds")List getIds
	,TL tl) {
 	if(tl.usr==null)return null;
	Map m=new HashMap();try{
	if( getLogs!=null){List<Map>a=new LinkedList<>();
		m.put("getLogs",a);
		for (Object o:getLogs) {
			Map x=(Map)o;
			a.add(getLog(x,tl));
		}
	}
	if( writeObjs!=null)
		m.put("writeObjs",writeObjs(writeObjs,tl));
	if( getIds!=null){List a=new LinkedList<>();
		m.put("getIds",a);
		for (Object o:getIds)
			a.add(ObjHead.factory(toInt( o)));
	}}catch ( Exception ex ){tl.error( ex,"App.poll" );}
	return m;}

 /**op methods:
	* create new domain
	* create new usr
	* create new role
	* create new lock
	* create new proto
	* add Usr member to role
	* add operation  to role
	* add resource   to role
	* add Usr member to lock
	* add operation  to lock
	* add resource   to lock
	* delete
	*	Usr member from role
	*	operation  from role
	*	resource   from role
	*	Usr member from lock
	*	operation  from lock
	*	resource   from lock

	*	domain
	*	usr
	*	role
	*	lock
	*	proto

	* edit
	*	domain
	*	usr
	*	role
	*	lock
	*	proto
	*
 * /

/ **
 * param:pagenation
 *	cases
 *		first request and results over 1023rows, then, include in return pagenation:{ref:<int>,page:<int>}
 *		later requests, in request included pagenation:{ref:<int>,page:<int:optional>}
 *		last response, includes pagenation:{ref:<int>,page:<int>,count:<int>,closed:true}
 *		case, reqest include pagenation:{ref:<int>,cancel:true}
 *
 * cases for dates
 * 1.param "from" //afterDate
 *
 * 2.param "to" //beforeDate
 *
 * 3.param "from"-"to" dates
 *
 *
 * cases for entityship
 * 0. no entityship, (in cases only dates, or pagenation ref)
 * 1.params entity+id+col
 *
 * 2.param entity+id+col , req usr
 *
 * 3.param parent , getLog of all children and descendants
 * */
static Map getLog(Map p,TL tl){
	try{Map pg=(Map)p.get("pagenation");
		Object ref=pg!=null?pg.get("ref"):null;
		if(ref!=null){
			Map ps=(Map)tl.h.s("pagenation");
			ps=ps!=null?(Map)ps.get(ref):null;
			ResultSet rs=ps==null?null:(ResultSet)ps.get(ref);
			if(rs==null){pg.put("closed",true);return p;}
			return listLog(p,rs,pg,ps,tl);}
		ref=p.get("from");
		Long to,from=ref instanceof Number?((Number)ref).longValue():TL.Util.parseDate(String.valueOf(ref)).getTime();
		ref=p.get("to");
		to=ref instanceof Number?((Number)ref).longValue():TL.Util.parseDate(String.valueOf(ref)).getTime();//Double.NaN;

		ref=p.get("headLog");
		boolean headLog=ref instanceof Boolean?((Boolean)ref)
			:ref==null?false:"true".equalsIgnoreCase( ref.toString() );

		ref=p.get("domain");
		List w=new LinkedList();

		if(from==null&&to==null) {
			p.put("msg","aswan2017.App.getLog:no parameter 'from' nor 'to'");
			return p;}
		if(from!=null){
			w.add( TL.Util.lst(ObjProperty.C.logTime,Tbl.Cols.M.ge));w.add(from );}
		if(to!=null){
			w.add( TL.Util.lst(ObjProperty.C.logTime,Tbl.Cols.M.le));w.add(to );}
		Tbl.CI[]a= {
			ObjProperty.C.id
			,ObjProperty.C.uid
			,ObjProperty.C.n//PropertyName
			,ObjHead.C.domain
			,ObjHead.C.proto
			,ObjHead.C.parent };
		for(Tbl.CI s:a) {
			ref = p.get( s+"List" );
			if ( ref != null ) {
				List l = ( List ) ref;
				w.add(TL.Util.lst( s,Tbl.Cols.M.in));
				w.add(l );
			}
		}
		Object[]where=new Object[w.size()];
		w.toArray( where );
		Tbl t=headLog?new ObjHead(  ):new ObjProperty( );
		p= listLog(p,t.sql(t.columns(),where),where,tl);
		return p;
	}catch(Exception ex){tl.error(ex,"getLog");}
	return p;}

static Map listLog(Map m,String sql,Object[]where,TL tl){
	String n=ObjHead.C.logTime+".noMax";
		tl.h.r( n,true );
		ResultSet rs=null;
		try{rs=TL.DB.R(sql, where);}catch(Exception ex){tl.error(ex
			,"aswan2017.App.list(sql",sql,":where=",where,m);}
		tl.h.r( n,null );
		m=listLog(m,rs,null,null,tl);
	return m; }

/**
 * pg is pagenation, a js-obj from client-http-request of the
 * ps is pagenation, a js-obj from session
 * */
static Map listLog(Map m,ResultSet rs,Map pg,Map ps,TL tl){
	Object o=m.get("headLog");if(o==null){if(ps!=null)
		o=ps.get("headLog");if(o==null&&pg!=null)
		o=ps.get("headLog");}
	boolean headLog=o instanceof Boolean?((Boolean)o)
		:o==null?false:"true".equalsIgnoreCase( o.toString() );

	Tbl d=headLog?new ObjHead()
		:new ObjProperty();//ObjProperty d=new ObjProperty(0);
	if(m==null)m=new HashMap();
	List a=new LinkedList();
	m.put("a",a);
	try{boolean b=false;
		Field[]f=d.fields();
		while((b=rs.next()) && a.size()<1000)
		{	d.load(rs,f);
			if(tl.usr.hasAccess( "view",d.id))//headLog?((ObjHead)d).id:((ObjProperty)d).id )
				a.add(d.asMap());
		}
		if(b){
			if(pg==null)pg=new HashMap();
			m.put("pagenation",pg);
			Object ref=pg.get("ref");
			if(ref==null)
				pg.put("ref",ref=new Date().getTime());
			Map pgs=(Map)tl.h.s("pagenation");
			if(pgs==null)
				tl.h.s("pagenation", pgs = new HashMap());
			if(ps==null) {
				pgs.put(ref, ps = new HashMap());
				ps.put("ref",ref);
				ps.put("rs",rs);
				ps.put("headLog",headLog);
			}
			o=ps.get("page");
			o=o==null?1:toInt(o)+1;
			ps.put("page",o);
			pg.put("page",o);
			o=pg.get("count");
			o=o==null?1:toInt(o)+a.size();
			pg.put("count",o);
			pg.put("count",o);
		}else{if(pg!=null){
			pg.put("closed",true);
			m.put("pagenation",pg);
			Object ref=pg.get("ref");
			if(ref!=null){
				Map pgs=(Map)tl.h.s("pagenation");
				if(pgs!=null)//try
					pgs.remove(ref);
				//catch (Exception ex){tl.error(ex,"App.list:close:",pg,rs);}
			}
		 }
			TL.DB.close(rs,false);
		}
	}catch(Exception ex){tl.error(ex,"listLog",pg,rs);}
	return m;}


static List writeObjs(List rows,TL tl){
	if(tl.usr==null)return null;
	List x=new LinkedList();
	ObjProperty p0=null;
	ObjHead h,h0=null;Tbl.CI clt=null;
	for(Object o:rows)try
	{Map m=o instanceof Map?(Map)o:null;
	 Tbl t=null;
	 if(m!=null)
	 {	Object id=m.get( ObjProperty.C.id.name() );
		if(id!=null ){Object pn=m.get( ObjProperty.C.n.name() );
		 if ( pn==null ){
			if(h0==null){
				h0=new ObjHead(  );h0.uid=tl.usr.id;}
			t=h0;clt=ObjHead.C.logTime;//cid=ObjHead.C.id;
		 }else {if(p0==null){
			p0=new ObjProperty(0);p0.uid=tl.usr.id;}
			t=p0;clt=ObjProperty.C.logTime;}//cid=ObjProperty.C.id;
		}
		if(t!=null){t.fromMap(m);
		 //h=ObjHead.all.get( t.id );
		 if(tl.usr.hasAccess( t==p0?
			"writeProperty":"writeObj",t.id )){//TODO: investigate what are the circumstances for other operations ,other than "view"
			t.save();
			m.put(clt.toString(),t.logTime);
			x.add(m);
		}}
	 }
	}catch(Exception ex){tl.error(ex,"writeObjs");}
	return x;}

static void staticInit(){
	TL.registerOp( App.class);
}

static{staticInit();}

 public static Integer toInt(Object o){
	if(o instanceof String && TL.Util.isNum( (String)o ))
		return new Integer( (String)o);
	if(o instanceof Integer)return (Integer)o;
	if(o instanceof Number)return ((Number)o).intValue();
	if(o==null)return null;
	String s=o.toString();
	return TL.Util.isNum( s )?new Integer( s ):null;}

public static TL.DB.Tbl tbl(Class<? extends TL.DB.Tbl>c){
	if(c==ObjProperty.class)
		return ObjProperty.sttc;
	if(c==ObjHead.class)
		return ObjHead.sttc;
	try{return c.newInstance();}
	catch ( Exception ex ){TL.tl().error( ex,"aswan2017.App.tbl:" );}
	return ObjHead.sttc; }

public static abstract class Tbl extends TL.DB.Tbl{
	@F public Integer no, /**user that made the change*/uid;
	@F(max=true) public Date logTime;//cancelled:lastModified
	@F(group=true) public Integer /**Object Id*/id;
Tbl(){TL tl=TL.tl();uid=tl.usr==null?0:tl.usr.id;}
Tbl(Integer id){this();this.id=id;}

	abstract CI ciId();
	abstract CI ciUid();
	abstract CI ciLogTime();
	abstract CI[]groupBy();
	@Override public Object pkv(){return no;}

 public static String prefix(CI t,CI lt){//return this==logTime?"max(`":"`";
	String r="`";
	if(t==lt && TL.tl().h.r(t+".noMax")==null)
		r="max(`";
	return r;}

 public static String suffix(CI t,CI lt){
	String r="`";
	if(t==lt && TL.tl().h.r(t+".noMax")==null)
		r="`)";
	return r;}


 public boolean exists(){CI[]groupBy=groupBy();
	Object[]where=new Object[groupBy.length*2];
	for(int i=0;i<groupBy.length;i++)
		where[i*2+1]=v((CI)(where[i*2]=groupBy[i]));
	return exists( where,groupBy,getName() );}
 public static boolean exists(Object[]where,CI[]groupBy,String dbtName){
	boolean b=false;
	int n=0;
	try{n=count( where,dbtName );}catch ( Exception ex ){}
	b=n>0;
	return b;
 }

 @Override Tbl load(){return loadBy(ciId(),id);}
 @Override Tbl loadBy(CI c,Object v){ResultSet rs=null;try{
 	rs=TL.DB.r( sql(columns(),where( c,v ),groupBy()) ,v );
	if(rs.next())load( rs );
	}catch(Exception x){ TL.tl().error(x,
		"App.Tbl(",this,").loadBy(",c,",",v,")");}
		finally {
		TL.DB.close( rs,false );
 }
	return this;}//loadBy

 @Override public Tbl save() throws Exception{
	TL tl=TL.tl();String n=ciLogTime()+".noMax";
	tl.h.r( n,true );if(uid==null)
		uid=tl.usr==null?0:tl.usr.id;
	super.save();tl.h.r( n,null );
	return this;}

	public static List ids(java.util.Collection<? extends Tbl>a){
		if(a==null)return null;
		List l=new LinkedList<Integer>();// have.size() );
		for(Tbl t:a)l.add( t.id );
		return l;}
	public abstract TL.Json.Output jsonOutput(TL.Json.Output o
		,String ind,String path)throws java.io.IOException;

}//abstract class App.Tbl

/** db-tbl ORM wrapper * Id_Name_Val_Usr_LogTime oI.N.V.U.LT * */
public static class ObjProperty extends Tbl {//implements Serializable
	public static final String dbtName="ObjProperty";
	@Override public String getName(){return dbtName;}
	@Override public TL.Json.Output jsonOutput(TL.Json.Output o
		,String ind,String path)throws java.io.IOException{
		return o.oForm( this,ind,path );}

	@F(group=true) public String /**propertyName*/n;
	@F(json=true) public Object /**propertyValue*/v;
	public enum C implements CI{no,uid,logTime,id,n,v;
		public Class<? extends Tbl>cls(){return ObjProperty.class;}
		@Override public Field f(){return Cols.f(name(), cls());}
		@Override public String prefix(){return Tbl.prefix(this,logTime);}
		@Override public String suffix(){return Tbl.suffix( this,logTime );}
	}//C
	@Override public CI pkc(){return C.no;}
	@Override public C[]columns(){return C.values();}
	@Override public List creationDBTIndices(TL tl){
		return TL.Util.lst(
			TL.Util.lst("int(24) PRIMARY KEY NOT NULL AUTO_INCREMENT"//no
				,"int(8) NOT NULL DEFAULT 1"//uid
				,"timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"//logTime
				,"int(8) NOT NULL DEFAULT 1"//id
				,"varchar(255) NOT NULL default '-'"//propertyName
				,"text"//propertyValue
			)
			,TL.Util.lst(
				TL.Util.lst(C.logTime)
				,TL.Util.lst(C.id,C.n,C.logTime)
				,TL.Util.lst(C.n,TL.Util.lst( C.v,255),C.logTime)
				,TL.Util.lst(C.uid,C.id,C.logTime) )
		);
/*
CREATE TABLE `ObjProperty` (
  `no` int(24) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  -- `proto` int(8) NOT NULL,
  `logTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `uid` int(6) not null default -1 , -- `usr` varchar(255) DEFAULT NULL,
  `id` varchar(255) DEFAULT NULL,
  `n` varchar(255) DEFAULT NULL,
  `v` text,  --`lastModified` timestamp NOT NULL ,
  key(`proto`,`logTime`),
  key(`proto`,`id`,`logTime`),
  key(`proto`,`n`,`logTime`),
  key(`uid`,`proto`,`logTime`), -- key(`usr`,`domain`,`entity`,`id`,`col`,`logTime`)
  key(`proto`,`n`,`v`(250),`logTime`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
*/
	}
	static{registered.add(ObjProperty.class);}
	public ObjProperty(){super();}
	public ObjProperty(Integer id){super(id);}
	public ObjProperty(Integer id,String pn,Object val){super(id);n=pn;v=val;}

	public static ObjProperty sttc=new ObjProperty();
	CI ciId(){return C.id;}
	CI ciUid(){return C.uid;}
	CI ciLogTime(){return C.logTime;}
	CI[]groupBy(){return cols( C.id,C.n );}
	public static ObjHead loadObj(ObjHead o){
		if(o==null)return o;
		if(o.props==null)
			o.props=new HashMap<String,ObjProperty>();
		ObjProperty p=new ObjProperty( 0 );Object[]where=Tbl.where( C.id,o.id);
		for (TL.DB.Tbl t: p.query( p.sql(p.columns(),where,p.groupBy() ),where,true  ) )
		{ p=(ObjProperty)t;
			o.props.put(p.n,p);}
		return o;}

	Map<String,Object>loadMap(){
		Map<String,Object>m=new HashMap<String,Object>();
		for (TL.DB.Tbl t: query(Tbl.where( C.id,id )) )//TODO:might want to put oldest and newest logTime
		{ObjProperty p=(ObjProperty)t;
			m.put(p.n,p.v);}
		ObjHead h=ObjHead.all!=null?ObjHead.all.get(id):null;
		if(h!=null)m.put("$",TL.Util.mapCreate("id",id
			,"parent",h.parent,"proto",h.proto,"domain",h.domain));
		else m.put("$",TL.Util.mapCreate("id",id));
		return m;}

	static List<Integer>idsByPName(String pn,Object pv,TL tl)throws Exception{
		return TL.DB.q1colTList("select `"+C.id
			+"`,max(`"+C.logTime+"`) from `"+dbtName+"` where `"
			+C.n+"`=? and `"+C.v+"`=? group by `"
			+C.id +"`,`"+C.n +"`",Integer.class,pn,pv);}
	static List<Integer>idsByPName(String pn,TL tl)throws Exception{
		return TL.DB.q1colTList("select `"+C.id
			+"`,max(`"+C.logTime+"`) from `"+dbtName+"` where `"
			+C.n+"`=? group by `" +C.id +"`,`"+C.n +"`",Integer.class,pn);}
}//class ObjProperty

/** db-tbl ORM wrapper * for Domain&Proto vs userRole Access-Control, also field parent-obj* */
public static class ObjHead extends Tbl {
	public static final String dbtName="ObjHead";
	@Override public String getName(){return dbtName;}
	@F public Integer /**parent-object*/parent
	,/**the super-prototype*/proto
	,/**belongs to which domain/company/realm/accessControlContext */domain;
	CI ciId(){return C.id;}
	CI ciUid(){return C.uid;}
	CI ciLogTime(){return C.logTime;}
	CI[]groupBy(){return cols( C.id );}
	public enum C implements CI{no,uid,logTime,id,parent,proto,domain;
		@Override public Field f(){return Cols.f(name(), ObjHead.class);}
		@Override public String prefix(){return Tbl.prefix(this,logTime);}
		@Override public String suffix(){return Tbl.suffix( this,logTime );}
	}//C
	@Override public CI pkc(){return C.no;}
	@Override public C[]columns(){return C.values();}
	@Override public List creationDBTIndices(TL tl){
		return TL.Util.lst(TL.Util.lst(
			"int(8) PRIMARY KEY NOT NULL AUTO_INCREMENT"//no
			,"INT(6) Not Null DEFAULT 1"//uid
			,"timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"//logTime
			,"int(8) NOT NULL DEFAULT 1"//id
			,"int(8) NOT NULL DEFAULT 1"//parent
			,"int(8) NOT NULL DEFAULT 1"//proto
			,"int(8) NOT NULL DEFAULT 1"//domain
			),TL.Util.lst(TL.Util.lst(C.id ,C.logTime)
			,TL.Util.lst(C.logTime)
			,TL.Util.lst(C.domain,C.proto ,C.logTime)
			,TL.Util.lst(C.parent ,C.logTime)
			,TL.Util.lst(C.uid,C.domain,C.logTime))
		);
/*
must create foundational prototypes(classes)
domain(Def::= name:<str>,proto:<int>,super:<int:superclass-prototype>)
Usr(Def)
Role(Def::=name:<str> , proto:<int>, super:<int:superclass-prototype> ,
(Resource:array
			deleted_in_log[array:id , or *] , domain[array:id , or *]
			, role[array:id , or *], lock[array:id , or *]
			, proto[array:id , or *], id[array:id , or *], prop[array:str , or *]
			,allRsrcLevels ,allinRLevel
		, ops:array<str>
			view, create, edit, delete, undelete, close, unclose,login,chngPw
		)
	, all-and-any classes&objects are locked, and roles allow user-access)
UsrRoleMembership(Role in domain, granting access defined inside the role)
UsrsLocked( excluding usr/s rights from Resource(id and/or prop,,,ect)&ops
    ,overwrite any role grants )
Contract(Def)
Overhead(Def)
Profile(Def)
Session(Def)
Account(Def)
AccEntry(Def)
AccEntTrans(Def)
Architecture(selection of prototypes and properties
	, example uasages are:
		template data-entry, e.g. new check
			, new contract default fields values
			, default vals of fields of new profile of different jobCats
		, creating new company-branch
		,pipedream:code-base like for new application
		)
-- prototype, domain, UsrRole-AccessControl
CREATE TABLE `ObjHead` (
  `no` int(24) NOT NULL,
  `domain` int(8) NOT NULL,
  `proto` int(8) NOT NULL,
  `logTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `uid` int(8) not null default -1 , -- `usr` varchar(255) DEFAULT NULL,
  `n` varchar(255) DEFAULT NULL,
  `v` text,  --`lastModified` timestamp NOT NULL ,
  key(`domain`,`proto`,`logTime`),
  key(`domain`,`proto`,`n`,`logTime`),
  key(`n`,`domain`,`proto`,`logTime`),
  key(`uid`,`domain`,`proto`,`logTime`),
  key(`domain`,`proto`,`n`,`v`(250),`logTime`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
*/
	}

	static{registered.add(ObjHead.class);}
	public static ObjHead sttc=new ObjHead( );
	public static ObjHead factory(Integer id){
		if(id==null)return null;
		ObjHead p,x,o=all.get( id );
		if(o==null){
			x=o=new ObjHead( id,0,0,0 );
			o.loadBy( C.id,id );
			p=o.parent==id?null:o.parent();
			if(p!=null && p.children!=null ){
				x=p.children.get( id );
				if(x!=null && x.id==id)
					return x;
				else
					x=o;
			}
			o.loadProps();
			Domain d=o.domain();
			if(d==null){
				d=Domain.loadDomain( o.domain );
				x=all.get( id );
				if(x!=null)
					return x;
			}
			while(x.proto!=x.id && x instanceof ObjHead)
			{p=x.proto();
				if(p==null)
					p=factory(x.proto);
				x=p;
			}
			if(x instanceof Domain || x.id==x.domain){
				d=Domain.loadDomain( id );o=d;
				d.props=o.props;
				Domain.domains.put( id,d );
			}else if(x instanceof Domain.Usr){
				String un=o.propStr( "un" );
				if(un!=null){
					Domain.Usr u=d.new Usr(o.id,o.parent,o.proto);
					u.props=o.props;
					o=u;if(d.usrs!=null && !d.usrs.containsKey( un ))
					d.usrs.put( un,u );
					if(d.allUsrs!=null && ! d.allUsrs.containsKey( un ))
					d.allUsrs.put( un,u );
				}
			}else if(x instanceof Domain.Role){
				String roleName=o.propStr( "name" );
				if(roleName!=null){
					Domain.Role u=d.new Role(o.id,o.parent,o.proto);
					u.props=o.props;
					o=u;if(d.roles!=null &&!d.roles.containsKey( roleName ))
					d.roles.put( roleName,u );
				}
			}
			//else if(x instanceof ObjHead) all.put( id,o );
			all.put( id,o);
			p=o.parent();
			if(p!=null ){
				if(p.children!=null){
					x=p.children.get( id );
					if(x==null || x.id!=id)
						p.children.put( id,o );
					else
						;//tl.log
				}
				else{
					p.children=new HashMap<>(  );
					p.children.put( id,o );
				}
			}
			try {List<Integer>chldrn = TL.DB.
				q1colTList( o.sql( cols( C.id,C.logTime )
					, where( C.parent, id ),o.groupBy() )
					, Integer.class, id );
				if(chldrn!=null&&chldrn.size()>0){
					if ( o.children==null )
						o.children=new HashMap<>(  );
				for(Integer i:chldrn){
					x=all.get( i );
					o.children.put( i,x!=null?x:o );
				}}
			}catch ( Exception ex ){}
		}
		return o;
	}

	/** all protos in all domains*/
	public static Map<Integer,ObjHead>all=new HashMap<Integer,ObjHead>();
	/* *roles is the list of access control, if a user doesnt have any of the roles then the user has no access
	 *,if a user is in locks ,even if the user has a role for access, the user is locked-out and has no access* /
	//Map<String,Domain.Role>roles=new HashMap<String,Domain.Role>();//,locks=new HashMap<String,Domain.Role>();*/
	public Map<Integer,ObjHead>children;//,sub,descendents;
	public Map<String,ObjProperty>props;
	ObjHead(Integer id,Integer parent,Integer proto,Integer domain){
		super(id);this.parent=parent;this.proto=proto;this.domain=domain;}
	public ObjHead(){this(0,0,0,0);}
	ObjHead parent(){return all==null?null:all.get(parent);}
	ObjHead proto(){return all==null?null:all.get(proto);}
	Domain domain(){return  all==null?null: (Domain)(all.get(domain));}//Domain.domains

	boolean isInstanceOf(ObjHead p){ObjHead o=this,q=null;
		while(p!=o && q!=o && o!=null)
		{q=o;o=o.proto();}
		return o==p;}
	boolean isInstanceOf(Integer p){ObjHead o=this,q=null;
		while(o!=null&&p!=o.id && q!=o && o!=null)
		{q=o;o=o.proto();}
		return o!=null&&o.id==p;}
	boolean isDescendentOf(ObjHead p){ObjHead o=this,q=null;
		while(p!=o && q!=o && o!=null)
		{q=o;o=o.parent();}
		return o==p;}
	boolean isDescendentOf(Integer p){ObjHead o=this,q=null;
		while(o!=null&&p!=o.id && q!=o && o!=null)
		{q=o;o=o.parent();}
		return o!=null&&o.id==p;}
	ObjHead loadProps(){return ObjProperty.loadObj( this );}

	public Object propo(String pn){
		ObjProperty p=props==null?null:props.get( pn );
		Object v=p==null?p:p.v;
		return v;}

	public void setProps(Object...a)throws Exception{
		if(a==null||a.length<2)return;
		if(props==null)props=new HashMap<>(  );
		for(int i=0;i<a.length;i+=2){
			String n=String.valueOf( a[i] );
			ObjProperty p=props.get( n );
			if(p==null)
				props.put( n,p=new ObjProperty( id,n,a[i+1] ) );
			else p.v=a[i+1];
			TL tl=TL.tl();
			p.uid=tl.usr==null?0:tl.usr.id;
			p.logTime=tl.now;
			p.save();
		}}

	public String propStr(String pn){
		Object p=propo( pn );
		String v=p instanceof String?(String)p:p==null?null:p.toString();
		return v;}
	public Integer propInt(String pn){
		Object p=propo( pn );
		String s=p instanceof String?(String)p:null;
		Integer v=s!=null
		?(	TL.Util.isNum( (String)p )
			?TL.Util.parseInt( (String)p,-1 ):null
		 ):p==null?null
		:p instanceof Integer?(Integer)p
		:p instanceof Number?((Number)p).intValue()
		:null;//:TL.Util.parseInt( p.toString(),-1 );
		return v;}
	public Number propNum(String pn){
		Object p=propo( pn );
		Number v=p instanceof Number?(Number)p
		:p==null?null:Double.parseDouble( p.toString() );
		return v;}

	public boolean hasAccess(){Domain.Usr u=TL.tl().usr;return u!=null&&u.hasAccess( "view",id );}
	//public boolean hasAccess(Object operation,Domain.Usr u){return u.hasAccess( op,id );}

		/*public Output oDbTbl(TL.DB.Tbl p,String ind,String path)throws IOException{
			if(p instanceof App.Tbl)return oAppTbl((App.Tbl)p,ind,path);
			return comment?w("}//TL.DB.Tbl:pkc=").o(p.pkc()).w(':')
				.w(p.getClass().toString()).w("&cachePath=\"").p(path).w("\"\n").p(ind):w('}');}
		public Output oAppTbl(App.Tbl o,String ind,String path)throws IOException{
			if(o instanceof App.ObjProperty){App.ObjProperty p=(App.ObjProperty)o;

			}else
			if(o instanceof App.ObjHead)return oObjHead((App.ObjHead)o,ind,path);
			return (comment?w("}//App.Tbl&cachePath=\"").p(path).w("\"\n").p(ind):w('}'));}*/
	@Override public TL.Json.Output jsonOutput(TL.Json.Output o,String ind,String path)throws java.io.IOException{
		return jsonOutput( o,ind,path,true );}

	public TL.Json.Output jsonOutput(TL.Json.Output o,String ind,String path,boolean closeBrace)throws java.io.IOException{
		Domain.Usr u=TL.tl().usr;
		if (u==null || !u.hasAccess( "view",id ))
		{o.w( "noAccess" );return null;}
		//if(o.comment)o.w("{//TL.Form:").w('\n').p(ind);else//.w(p.getClass().toString())
		 o.w('{');
		Field[]a=fields();String i2=ind+'\n';
		o.w("\"class\":").oStr(getClass().getSimpleName(),ind);//w("\"name\":").oStr(p.getName(),ind);
		for(Field f:a)
		{	o.w(',').oStr(f.getName(),i2).w(':')
				.o(v(f),ind,o.comment?path+'.'+f.getName():path);
			if(o.comment)o.w("//").w(f.toString()).w("\n").p(i2);
		}

		if(children!=null) //o.w(",\"children\":").oCollctn( children.keySet(),ind,o.comment?path+".children":path );
			jsonOutput(children.values() , o.w(",\"children\":"),ind,o.comment?path+".children":path);
		if(props!=null) {o.w(",\"props\":{");//.oCollctn( p.props.values(),ind,w.comment?path+".props":path );
			boolean comma=false;
			for ( App.ObjProperty p:props.values() ){
				if(comma)o.w(',');else comma=true;
				o.oStr(p.n,ind)
					.w(":{\"v\":")
					.o( p.v,ind,o.comment?path+".props."+p.n:path )
					.w(",\"uid\":").p( p.uid )
					.w(",\"logTime\":").p( p.logTime==null?0:p.logTime.getTime() )
				.w( '}' );
			}
			o.w("}");
		}
		if(closeBrace){
			if(o.comment)
				o.w("}//App.ObjHead&cachePath=\"").p(path).w("\"\n").p(ind);
			else o.w('}');}
		return o;
	}

	public TL.Json.Output jsonOutput(java.util.Collection<? extends ObjHead> l
	,TL.Json.Output o,String ind,String path)throws java.io.IOException{
		o.w("[");
			boolean comma=false;
			for ( ObjHead p:l )if(p.hasAccess(  )){
				if(comma)o.w(',');else comma=true;
				o.o( p.id );
			}
			o.w("]");
		return o;}

}//class ObjHead

 public static class Domain extends ObjHead{
	public enum Proto{Role,Usr,Proto,Lock;//,Membership;
	ObjHead get(){Domain d0=domains.get( 0 );
		for(ObjHead o:d0.children.values()){
			ObjProperty p=o.props==null?null:o.props.get( "name" );
			if(p!=null&&name().equals( p.v ))
				return o;}
		return null;}
	}//Integer rolesDeclarations,usersDeclarations,protosDeclarations,locksDelarations;
	public enum Oper{all,view,create,edit,delete,app}
	Domain(Integer id,Integer parent,Integer proto){super(id,parent,proto,id);}

	public static Map<Integer,Domain>domains=new HashMap<Integer,Domain>();
	public static Map<String,Usr>allUsrs=new HashMap<String,Usr>();
	public Map<Integer,Role>locks=new HashMap<Integer,Role>();
	public Map<String,Role>roles=new HashMap<String,Role>();
	public Map<String,Usr>usrs=new HashMap<String,Usr>();

  public Domain loadDomain(){
	loadBy(C.id,id);
	loadProps();
	domains.put( id,this );
	all.put( id,this );
	ObjHead o=new ObjHead( 0,id,id,id );
	Map<Integer,ObjHead>prots=new HashMap<Integer,ObjHead>();
	Object[]where=o.where( C.domain,id ,C.parent,id);
	for(TL.DB.Tbl t:o.query( sql(columns(),where,groupBy()),where ,true)) {
		o = ( ObjHead ) t;if(o.id==id)continue;
		if ( o.id == o.proto ) {
			o.loadProps();
			String n = o.propStr( "name" );
			if ( n != null ) {
				Proto pn =null;try{pn= Proto.valueOf( n );}catch(Exception ex){}
				if(pn!=null)switch ( pn ) {
					case Usr:
						Usr u = new Usr( o.id, o.parent, o.proto );
						u.props = o.props;o=u;//usrs.put( (o = u).id, u );allUsrs.put( u.id, u );u.init( null );
						break;
					case Role:case Lock:
						Role r=new Role( o.id, o.parent, o.proto );
						r.props = o.props;o=r;r.lock=pn==Proto.Lock;
						break;
					//case Membership:break;case Proto:break;
				}
				prots.put(o.id,o);
			}
		}
		if ( !all.containsKey( o.id ) )
			all.put( o.id, o );
		if(children==null)children=new HashMap<Integer,ObjHead>( );
		children.put( o.id, o );
	}//TODO:should use BreadthFirst-queueing to load objects and their sub-proto`s, and queueing again to load their children
	for(ObjHead x:children.values()) if((o=prots.get( x.proto ))!=null&&o!=x){
		if(o instanceof Usr){
			Usr u=new Usr(x.id,x.parent,x.proto);
			if(x.props!=null)u.props=x.props;
			else u.loadProps();
			all.put( u.id,u );
			children.put( u.id,u );
			usrs.put( u.un(),u );
			allUsrs.put( u.un(),u );
		}
		else if(o instanceof Role){
			//boolean rol=Proto.Role.name().equals( o.propStr( "name" ) );
			Role r=new Role( x.id,x.parent,x.proto );
			if(x.props!=null)r.props=x.props;
			else r.loadProps();
			r.lock=(( Role ) o).lock;
			all.put( r.id,r );
			children.put( r.id,r );
			if(r.lock)locks.put( r.id,r );else{
				String n=r.propStr( "name" );if(n==null) try {
					r.setProps( "name", n="role"+new Date());
				} catch ( Exception e ) {
					TL.tl().error(e,"App.Domain.loadDomain:",id,":set name");
				}
				roles.put( n,r );
			}//r.init();
		}
	}
	for(Role r:roles.values())
		r.init(  );
	// load heads that belong to this domain , o.domain=this.id
	// load children , o.parent=this.id
	// load sub-proto`s , o.proto=this.id
	// get children-protos that o.proto=o.id
	// get props of children-protos
	// get id,Integer, children-proto prop-name:Usr,Role,Lock,UsrRoleMembership
	// get usrs, sup/childrent of Usr , also register usr.id in domain0.allUsrs
	// get roles, children of Role
	// foreach role in roles, get props: usrRoleMembership ,resourcesId,ops

	return this;
	/*public static Domain loadDomain0(){
		domains=new HashMap<Integer,Domain>();
		all=new HashMap<Integer,ObjHead>();
		Domain d=new Domain(0,0,0);//domains.put( d.id,d );
		String sql=d.sql(Tbl.Cols.cols(Tbl.Cols.M.all),null)
			+" where `"+C.domain+"`=`"+C.id+"` group by `"+C.id+"`";
		for(Tbl t:d.query( sql,null,true )){
			d=(Domain)t;
			domains.put(d.id,d);
			all.put(d.id,d);
		}
		ObjHead o=new ObjHead( );
		Object[]where=d.where(ObjHead.C.parent,0);
		sql=o.sql(Tbl.Cols.cols(Tbl.Cols.M.all),where,Tbl.Cols.cols(ObjHead.C.id));
		for(Domain d0:domains.values()){where[1]=d0.id;
		for(Tbl t:o.query( sql,where,true )){
			o=(ObjHead)t;
			all.put(d.id,d);
		}}
		return domains.get(0);}*/
  }//loadDomain

  public static Domain loadDomain(Integer id){
	Domain d=domains.get( id );
	if(d==null&&exists(where( C.id,id ),cols(C.id),dbtName))
		d=new Domain( id,0,0 ).loadDomain();
	return d;}

  public static Domain loadDomain0(){
	Domain d=domains.get( 0 );
	if(d==null){
		d=Domain.loadDomain(0);
		if(d==null)
			d=initNew();
	}
	return d;
  }

	public static Domain initNew(){Domain d =null;TL tl=TL.tl();try
	  {d = new Domain( 0, 0, 0 );
		d.uid=tl.usr==null?0:tl.usr.id;
		int n = d.maxPlus1( C.id ),x=n;
		d.id=n==1?n=0:n;
		domains.put( n,d );
		all.put( n,d );
		d.save();/*int c=d.count( null );
		if(d.id!=n || c<2)
		{tl.log( "App.Domain.initNew:after d.save, domain.id!=n : n=",n," ,d.id=",d.id );
			d.id=n;
			if(c==1 && !d.exists()){
				TL.DB.x( "update `"+ObjHead.dbtName+"` set id=0" );// and uid=0
				tl.log( "App.Domain.initNew:if(c==1 && !d.exists())TL.DB.x( \"update `\"+ObjHead.dbtName+\"` set id=0\" );" );
			}
		}
		if(!d.exists())
			tl.log( "App.Domain.initNew:after d.save, !d.exists()" );*/
		ObjHead o=null;
		Map<Proto,ObjHead>m=new HashMap<Proto,ObjHead>();
		for(Proto prt:Proto.values()) {int id=prt.ordinal()+n+1;
			o = new ObjHead( id, d.id, id, d.id );o.uid=d.uid;
			o.save();m.put( prt,o );
			all.put( o.id,o );
			if(o.props==null)o.props=new HashMap<String,ObjProperty>(  );//o.loadProps();
			ObjProperty p = new ObjProperty( o.id );
			p.n = "name";p.uid=d.uid;
			p.v = prt.name();
			o.props.put( p.n, p );
			p.save();
			x=o.id;
		}
		Usr u = tl.usr;
		//create user admin admin
		if(n==0 || u==null) {
			o = m.get( Proto.Usr );
			u = d.new Usr( ++x, d.id, o.id );u.uid=d.uid;
			u.save();
			if (u.props ==null )
				u.props = new HashMap<>();
			ObjProperty p=u.props.get("un");
			if(p==null)u.setProps(
				"un","admin"
				,"pw","6f8f57715090da2632453988d9a1501b"
			);
		}
		//create role admin for admin
		o=m.get( Proto.Role);
		String rn="domain"+d.id+".admin";
		Role r=d.new Role( ++x,d.id,o.id );
		r.save();
		r.setProps( "name",rn
			,"member1",u.id
			,"resource1",d.id
			,"operation1",Oper.all.toString()
		);
		d.roles.put( rn,r );
		u.have.put( rn,r );//r.roles.put( rn,r );u.roles.put( rn,r );
	  }catch ( Exception ex ){tl.error(ex,"App.Domain.initNew:");}
	  return d;
	  /*two cases:1.new domain0 , 2. domain0 exists

		create Role,Usr,Lock,Proto
		//create a user
		//create a role, and add the new domain as resource, add user as member, add the default operations
		changePW
		createUsr

		domain
		usrs
		roles
		locks
		protos

		 view
		 create
		 edit
		 delete

		 login
		 logout
		 timeout
		 changePW
		*/
		}

	@Override public TL.Json.Output jsonOutput(TL.Json.Output o,String ind,String path)throws java.io.IOException{
		if(super.jsonOutput( o,ind,path ,false)==null)return o;
		if(usrs!=null) //o.w(",\"usrs\":").oCollctn( App.Tbl.ids(usrs.values( )),ind,o.comment?path+".usrs":path );
			jsonOutput(usrs.values() , o.w(",\"usrs\":"),ind,o.comment?path+".usrs":path);

		if(roles!=null)//o.w(",\"roles\":").oCollctn( App.Tbl.ids(roles.values()),ind,o.comment?path+".roles":path );
			jsonOutput(roles.values() , o.w(",\"roles\":"),ind,o.comment?path+".roles":path);
		if(locks!=null)//o.w(",\"locks\":").oCollctn( locks.keySet(),ind,o.comment?path+".locks":path );
			jsonOutput(locks.values() , o.w(",\"locks\":"),ind,o.comment?path+".locks":path);
		if(o.comment)
			o.w("}//App.Domain&cachePath=\"").p(path).w("\"\n").p(ind);
		else o.w('}');
		return o;}//(o.comment?o.w("}//App.Domain&cachePath=\"").p(path).w("\"\n").p(ind):o.w('}'));

	/*public TL.Json.Output oUsr(App.Domain.Usr p,String ind,String path)throws IOException{
		if(p.have!=null)w(",\"have\":").oCollctn( p.haveKeys(),ind,w.comment?path+".have":path );
		if(p.locks!=null)w(",\"locks\":").oCollctn( p.locks.keySet(),ind,w.comment?path+".locks":path );
		return (w.comment?w("}//App.Domain.Usr&cachePath=\"").p(path).w("\"\n").p(ind):w('}'));}
	public TL.Json.Output oRole(App.Domain.Role p,String ind,String path)throws IOException{
		if(p.operations!=null)w(",\"operations\":").oCollctn( p.operations,ind,w.comment?path+".operations":path );
		if(p.resources!=null)w(",\"resources\":").oCollctn( p.resources.keySet(),ind,w.comment?path+".resources":path );
		if(p.members!=null)w(",\"members\":").oCollctn( p.members.keySet(),ind,w.comment?path+".members":path );
		return (w.comment?w("}//App.Domain.Role&cachePath=\"").p(path).w("\"\n").p(ind):w('}'));}*/


  public class Role extends ObjHead{
	Role(Integer id,Integer parent, Integer proto){super(id,parent,proto,Domain.this.id);}
	Map<Integer,Usr>members;boolean lock;
	List<Object>operations;
	Map<Integer,ObjHead>resources;//Id;Map<String,String>resourcesProps;

	void init(){String name=propStr( "name" );if(props!=null)
		for(ObjProperty p:props.values()){
			Object v=p.v;
			String s=v instanceof String?(String)v:null;
			Integer i=s!=null&&TL.Util.isNum( s )?new Integer( s):null;
			if(p.n.startsWith( "member" ))
			{	if(i==null&&s==null)i=v instanceof Integer
				?(Integer)v:v instanceof Number?((Number)v).intValue()
				:null;
				if(i==null&&v!=null){s=v.toString();
					i=TL.Util.isNum( s ) ?new Integer( s):null;}
				v=all.get( i );Usr u=v instanceof Usr?(Usr)v:usrs.get( i );
				//if(u==null){
				//	v=all.get( i );
				//	if(v instanceof Usr)
				//		u=(Usr)v;}
				if(u!=null){
					u.have.put(name,this);
					if(members==null)
						members=new HashMap<Integer,Usr>();
					members.put( u.id,u );}
			}
			else if(p.n.startsWith( "resource" )){
				if(i==null&&s==null)i=v instanceof Integer
				?(Integer)v:v instanceof Number?((Number)v).intValue() :null;
				if(i==null&&v!=null){s=v.toString();
					i=TL.Util.isNum( s ) ?new Integer( s):null;}
				ObjHead o=all.get( i );
				if(o!=null){
					if(resources==null)resources=new HashMap<Integer,ObjHead>();
					resources.put( o.id,o );}
			}
			else if(p.n.startsWith( "operation" ))
			{if(operations==null)operations=new LinkedList<>(  );
				operations.add( v );}
		}
	}//init

  }//class Role


  public class Usr extends ObjHead{
	Map<String,Role>have=new HashMap<String,Domain.Role>()
		,locks;//=new HashMap<String,Domain.Role>();

	Usr(Integer id,Integer parent,Integer proto){
		super(id,parent,proto,Domain.this.id);}

	public String un(){return propStr("un");}

	public Domain domain(){return Domain.this;}

	boolean hasAccess(String operation,Integer resourceId){//,String resourcePN//PropertyName
		ObjHead c=all.get( resourceId );if(c==null)return false;
		for(Role r:have.values()){
			for (ObjHead o: r.resources.values())
			{if((o.id==c.id || c.isInstanceOf( o ))
			 &&(r.operations.contains( operation )
			  ||r.operations.contains( Oper.all.toString() )))
			 {if(locks==null)return true;
				for(Role x:locks.values()){
					for (ObjHead z: x.resources.values())
					{if((z.id==c.id || c.isInstanceOf( z ))
					 &&(x.operations.contains( operation )
					  ||x.operations.contains( Oper.all.toString() )))
					    return false;
					}
				}
				return true;
			 }
			}
		}
		return false;
		/*
			/**
		 * for param-user load:
		 * list-roles(ops,resources,usrs)
		 * list-protos(def)
		 * list-locks()
		 * * /
		//Usr init(){return this;}
		*/
	}

  }//class Usr

 }//class Domain

}//class App
