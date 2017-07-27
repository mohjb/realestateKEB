package aswan2017;

/**Created by moh on 14/7/17.*/
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;
import aswan2017.TL.DB.Tbl;

//%><%aswan2017.TL.run(request, response, session, out, pageContext);%><%!
/**
 * Created by Moh'd on 2/22/2017.
 */
public class App {

static final String SsnNm="App",UploadPth="/aswan/uploads/";

public static @TL.Op Map login
	(@TL.Op(prmName="un")String un
	,@TL.Op(prmName="pw")String pw,TL tl){
	Domain d=Domain.loadDomain0();
	Domain.Usr u=d.allUsrs.get( un );
	if(u!=null && pw!=null && pw.equals( u.propStr( "pw" ) )){
		tl.usr=u;
		tl.h.s( "usr",u );
		Map m=u.asMap();
		TL.Util.mapSet( m
			,"have",u.have
			,"props",u.props );
		return m;
	}
	return null;}

/**http-get-method , poll-server
 * , of param"lastPoll" is present, then call lastPoll
 * , if param"updateCols" present then call updateCols
 * , if param"putEntities" present then call putEntities
 * , if param"getIds" present then call getIds
 * , if param"getEntities" present then call getEntities
 */
 public static @TL.Op(urlPath = "*") Map poll
 (@TL.Op(prmName="getLogs")List getLogs
	,@TL.Op(prmName="updates")List update
	//,@TL.Op(prmName="getDistinct")List distinct
	,TL tl)
 {if(tl.usr==null)return null;
	Map m=new HashMap();
	if( getLogs!=null){List<Map>a=new LinkedList<>();
		m.put("getLogs",a);
		for (Object o:getLogs) {
			Map x=(Map)o;
			a.add(getLog(x,tl));
		}
	}
	if( update!=null)
		m.put("updates",update(update,tl));
	/*if( distinct!=null){List a=new LinkedList<>();
		m.put("getDistinct",a);
		for (Object o:distinct) {
			Map x=(Map)o;
			a.add(distinct(x,tl));
		}
	}*/
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
			return list(p,rs,pg,ps,tl);}
		ref=p.get("from");
		Long to,from=ref instanceof Number?((Number)ref).longValue():TL.Util.parseDate(String.valueOf(ref)).getTime();
		ref=p.get("to");
		to=ref instanceof Number?((Number)ref).longValue():TL.Util.parseDate(String.valueOf(ref)).getTime();//Double.NaN;

		ref=p.get("headLog");
		boolean headLog=ref instanceof Boolean?((Boolean)ref)
			:ref==null?false:"true".equalsIgnoreCase( ref.toString() );

		ref=p.get("domain");
		//StringBuilder sql=new StringBuilder("select * from `").append(headLog?ObjHead.dbtName:ObjProperty.dbtName).append("`");
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
		Tbl t=headLog?new ObjHead( 0,0,0,0 ):new ObjProperty( 0 );
		return list(p,t.sql(null,where),where,tl);
	}catch(Exception ex){tl.error(ex,"getLog");}
	return p;}

static List update(List rows,TL tl){
	List x=new LinkedList();
	ObjProperty d=new ObjProperty(0);
	for(Object o:rows)try{
		Map m=(Map)o;
		d.fromMap(m);
		d.logTime=tl.now;
		d.save();
		m.put(ObjProperty.C.logTime.toString(),d.logTime);
		x.add(m);
	}catch(Exception ex){tl.error(ex,"updateDomn");}
	return x;}

static Map list(Map m,String sql,Object[]where,TL tl){
	try{ResultSet rs=TL.DB.R(sql, where);
		m=list(m,rs,null,null,tl);
	}catch(Exception ex){tl.error(ex
			,"aswan2017.App.list(sql",sql,":where=",where,m);}
	return m;
/*
static Map list(Map m,Map pg,TL tl){
	if(pg==null)return m;
	Object ref=pg.get("pagenation");
	if(ref==null)return m;
	Map pgs=(Map)tl.h.s("pagenation");
	if(pgs==null)return m;
	Map ps=(Map)pgs.get(ref);
	if(ps==null)return m;
	ResultSet rs=(ResultSet)ps.get("rs");
	if(rs==null)return m;
	return list(m,rs,pg,ps,tl);}


static List getIds(List rows,TL tl){
	ObjProperty d=new ObjProperty(0);
	List x=new LinkedList();
	for(Object o:rows)try{
		Map m=(Map)o;
		d.fromMap( ( Map ) o );
		ObjProperty p=new ObjProperty(0);
		p.fromMap(m);
		//ObjProperty.C[]c=d.columns();
		//Field[]a=ObjProperty.fields(ObjProperty.class);//.fields();
		Object [ ] w = d.where(ObjProperty.C.id,d.id);
		String sql=d.sql(d.columns(),w, Tbl.Cols.cols(ObjProperty.C.id,ObjProperty.C.n));
		try{ResultSet rs=TL.DB.R(sql.toString(), w);}
		catch(Exception ex){tl.error(ex,"aswan2017.App.ObjProperty.getIds:where=",w);}
		for(Tbl t:d.query(d.where()))
			x.add(t.asMap());
	}catch(Exception ex){tl.error(ex,"updateCols");}
	return x;}
*/
}

/**
 * pg is pagenation, a js-obj from client-http-request of the
 * ps is pagenation, a js-obj from session
 * */
static Map list(Map m,ResultSet rs,Map pg,Map ps,TL tl){
	Object o=m.get("headLog");if(o==null){if(ps!=null)
		o=ps.get("headLog");if(o==null&&pg!=null)
		o=ps.get("headLog");}
	boolean headLog=o instanceof Boolean?((Boolean)o)
		:o==null?false:"true".equalsIgnoreCase( o.toString() );

	Tbl d=headLog?new ObjHead( 0 ,0,0,0)
		:new ObjProperty(0);//ObjProperty d=new ObjProperty(0);
	if(m==null)m=new HashMap();
	List a=new LinkedList();
	m.put("a",a);
	try{boolean b=false;
		Field[]f=d.fields();
		while((b=rs.next()) && a.size()<1000)
		{	d.load(rs,f);
			if(tl.usr.hasAccess( "view",headLog?((ObjHead)d).id:((ObjProperty)d).id ))
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
			o=o==null?1:((Number)o).intValue()+1;
			ps.put("page",o);
			pg.put("page",o);
			o=pg.get("count");
			o=o==null?1:((Number)o).intValue()+a.size();
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
	}catch(Exception ex){tl.error(ex,"list",pg,rs);}
	return m;}

static{TL.registerOp( App.class);}
public static Tbl tbl(Class<? extends Tbl>c){
	if(c==ObjProperty.class)
		return ObjProperty.sttc;
	if(c==ObjHead.class)
		return ObjHead.sttc;
	try{return c.newInstance();}
	catch ( Exception ex ){TL.tl().error( ex,"aswan2017.App.tbl:" );}
	return ObjHead.sttc;
}

/**
 * Proto_Id_Name_Val_Usr_LogTime P.I.N.V.U.LT)
 * */
public static class ObjProperty extends Tbl {//implements Serializable
	public static final String dbtName="ObjProperty";
	@Override public String getName(){return dbtName;}
	@F public Integer no, /**user that made the change*/uid;
	@F(max=true) public Date logTime;//cancelled:lastModified
	@F(group=true) public Integer /**Object Id*/id;
	@F(group=true) public String /**propertyName*/n;
	@F(json=true) public Object /**propertyValue*/v;
	public enum C implements CI{no,uid,logTime,id,n,v;
		@Override public Class<? extends Tbl>cls(){return ObjProperty.class;}
		@Override public Class<? extends TL.Form>clss(){return cls();}
		@Override public String text(){return name();}
		@Override public Field f(){return Cols.f(name(), cls());}
		@Override public Tbl tbl(){return Tbl.tbl(cls());}
		@Override public void save(){tbl().save(this);}
		@Override public Object load(){return tbl().load(this);}
		@Override public Object value(){return val(tbl());}
		@Override public Object value(Object v){return val(tbl(),v);}
		@Override public Object val(TL.Form f){return f.v(this);}
		@Override public Object val(TL.Form f,Object v){return f.v(this,v);}
		public String prefix(){String r="`";
			if(this==logTime && TL.tl().h.r(this+".noMax")==null)
				r="max(`";
			return r;}

		public String suffix(){String r="`";
			if(this==logTime && TL.tl().h.r(this+".noMax")==null)
				r="`)";
			return r;}
	}//C
	@Override public CI pkc(){return C.no;}
	@Override public Object pkv(){return no;}
	@Override public C[]columns(){return C.values();}
	@Override public List creationDBTIndices(TL tl){
		return TL.Util.lst(
			TL.Util.lst("int(24) PRIMARY KEY NOT NULL AUTO_INCREMENT"//no
				//,"int(8) NOT NULL DEFAULT 1"//proto
				,"int(8) NOT NULL DEFAULT 1"//uid
				,"timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"//logTime
				,"varchar(255) NOT NULL default '-'"//id
				,"varchar(255) NOT NULL default '-'"//propertyName
				,"text"//propertyValue
			)
			,TL.Util.lst(
				TL.Util.lst(C.id,C.logTime)
				,TL.Util.lst(C.logTime,C.id)
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
public static ObjProperty sttc=new ObjProperty( 0 );
	@Override public Tbl save() throws Exception{
		TL tl=TL.tl();tl.h.r( C.logTime+".noMax",true );
		super.save();tl.h.r( C.logTime+".noMax",null );
		return this;}
	public static ObjHead loadObj(ObjHead o){
		if(o==null)return o;
		if(o.props==null)
			o.props=new HashMap<String,ObjProperty>();
		ObjProperty p=new ObjProperty( 0 );
		for (Tbl t: p.query( Tbl.where( C.id,o.id) ,true  ) )
		{ p=(ObjProperty)t;
			o.props.put(p.n,p);}
		return o;}

	Map<String,Object>loadMap(){
		Map<String,Object>m=new HashMap<String,Object>();
		//Object[]where=Tbl.where( C.id,id );Tbl.Itrtr i=new Tbl.Itrtr(sql(null,where,this).toString(),where,false);
		for (Tbl t: query(Tbl.where( C.id,id )) )//TODO:might want to put oldest and newest logTime
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
	public ObjProperty(Integer id){this.id=id;}
}//class ObjProperty

/**
 * for Domain&Proto vs userRole Access-Control
 * */
public static class ObjHead extends Tbl {
	public static final String dbtName="ObjHead";
	@Override public String getName(){return dbtName;}
	@F public Integer no,id,/**parent-object*/parent
	,/**the super-prototype*/proto
	,/**belongs to which domain/company/realm/accessControlContext */domain
	,/**user that made the change*/uid;
	@F(max=true) public Date logTime;
	public enum C implements CI{no,id,parent,proto,domain,uid,logTime;
		@Override public Class<? extends Tbl>cls(){return ObjHead.class;}
		@Override public Class<? extends TL.Form>clss(){return cls();}
		@Override public String text(){return name();}
		@Override public Field f(){return Cols.f(name(), cls());}
		@Override public Tbl tbl(){return Tbl.tbl(cls());}
		@Override public void save(){tbl().save(this);}
		@Override public Object load(){return tbl().load(this);}
		@Override public Object value(){return val(tbl());}
		@Override public Object value(Object v){return val(tbl(),v);}
		@Override public Object val(TL.Form f){return f.v(this);}
		@Override public Object val(TL.Form f,Object v){return f.v(this,v);}
		public String prefix(){//return this==logTime?"max(`":"`";
			String r="`";
			if(this==logTime && TL.tl().h.r(this+".noMax")==null)
				r="max(`";
			return r;}

		public String suffix(){
			String r="`";
			if(this==logTime && TL.tl().h.r(this+".noMax")==null)
				r="`)";
			return r;}
	}//C
	@Override public CI pkc(){return C.no;}
	@Override public Object pkv(){return no;}
	@Override public C[]columns(){return C.values();}
	@Override public List creationDBTIndices(TL tl){
		return TL.Util.lst(TL.Util.lst(
			"int(8) PRIMARY KEY NOT NULL AUTO_INCREMENT"//no
			,"int(8) NOT NULL DEFAULT 1"//id
			,"int(8) NOT NULL DEFAULT 1"//parent
			,"int(8) NOT NULL DEFAULT 1"//proto
			,"int(8) NOT NULL DEFAULT 1"//domain
			,"INT(6) DEFAULT NULL"//uid
			,"timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"//logTime
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
	public static ObjHead sttc=new ObjHead( 0,0,0,0 );

	@Override public Tbl save() throws Exception{
		TL tl=TL.tl();tl.h.r( C.logTime+".noMax",true );
		super.save();tl.h.r( C.logTime+".noMax",null );
		return this;}

	/** all protos in all domains*/
	static Map<Integer,ObjHead>all=new HashMap<Integer,ObjHead>();
	/**roles is the list of access control, if a user doesnt have any of the roles then the user has no access
	 *,if a user is in locks ,even if the user has a role for access, the user is locked-out and has no access*/
	//Map<String,Domain.Role>roles=new HashMap<String,Domain.Role>();//,locks=new HashMap<String,Domain.Role>();
	Map<Integer,ObjHead>children;//,sub,descendents;
	Map<String,ObjProperty>props;
	ObjHead(Integer id,Integer parent,Integer proto,Integer domain){this.id=id;this.parent=parent;this.proto=proto;this.domain=domain;}
	ObjHead parent(){return all==null?null:all.get(parent);}
	ObjHead proto(){return all==null?null:all.get(proto);}
	Domain domain(){return  all==null?null: (Domain)(all.get(domain));}//Domain.domains
	//isDomain::= if id and/or proto refers to Domain
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
			if(p==null){
				p=new ObjProperty( id );
				p.n=n;
				props.put( n,p );
			}
			p.v=a[i+1];
			TL tl=TL.tl();
			p.uid=tl.usr.id;
			p.logTime=tl.now;
			p.save();
		}}

	public String propStr(String pn){
		Object p=propo( pn );
		String v=p instanceof String?(String)p:p==null?null:p.toString();
		return v;}
	public Integer propInt(String pn){
		Object p=propo( pn );
		Integer v=p instanceof Integer?(Integer)p:
			p instanceof Number?((Number)p).intValue():
			p==null?null:TL.Util.parseInt( p.toString(),-1 );
		return v;}
	public Number propNum(String pn){
		Object p=propo( pn );
		Number v=p instanceof Number?(Number)p
		:p==null?null:Double.parseDouble( p.toString() );
		return v;}

 public boolean exists(){return exists( id );}
 public static boolean exists(Integer id){
 	boolean b=false;
 	int n=0;
 	try{n=count( where(C.id,id  ),dbtName );}catch ( Exception ex ){}
 	b=n>0;
	return b;
 }

}//class ObjHead

 public static class Domain extends ObjHead{
	public enum Proto{Role,Usr,Proto,Lock,Membership;
	ObjHead get(){Domain d0=domains.get( 0 );
		for(ObjHead o:d0.children.values()){
			ObjProperty p=o.props==null?null:o.props.get( "name" );
			if(p!=null&&name().equals( p.v ))
				return o;}
		return null;}
	}//Integer rolesDeclarations,usersDeclarations,protosDeclarations,locksDelarations;
	public enum Oper{all,view,create,edit,delete,app}
	Domain(Integer id,Integer parent,Integer proto){super(id,parent,proto,id);}//Domain(){this(0,0);}

	public static Map<Integer,Domain>domains=new HashMap<Integer,Domain>();
	public static Map<String,Usr>allUsrs=new HashMap<String,Usr>();
	public Map<Integer,Role>locks=new HashMap<Integer,Role>();
	public Map<String,Role>roles=new HashMap<String,Role>();
	public Map<String,Usr>usrs=new HashMap<String,Usr>();

  public Domain loadDomain(){
	load();
	loadProps();
	domains.put( id,this );
	all.put( id,this );
	ObjHead o=new ObjHead( 0,id,id,id );
	Map<Integer,ObjHead>prots=new HashMap<Integer,ObjHead>();
	for(Tbl t:o.query( o.where( C.domain,id ,C.parent,id) ,true)) {
		o = ( ObjHead ) t;
		if ( o.id == o.proto ) {
			o.loadProps();
			String n = o.propStr( "name" );
			if ( n != null ) {
				Proto pn = Proto.valueOf( n );
				switch ( pn ) {
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
		children.put( o.id, o );
	}//TODO:should use BreadthFirst-queueing to load objects and their sub-proto`s, and queueing again to load their children
	for(ObjHead x:children.values()) if((o=prots.get( x.proto ))!=null){
		if(o instanceof Usr){
			Usr u=new Usr(x.id,x.parent,x.proto);
			if(x.props!=null)u.props=x.props;
			else x.loadProps();
			all.put( u.id,u );
			children.put( u.id,u );
			usrs.put( u.un(),u );
			allUsrs.put( u.un(),u );
		}
		else if(o instanceof Role){
			//boolean rol=Proto.Role.name().equals( o.propStr( "name" ) );
			Role r=new Role( x.id,x.parent,x.proto );
			r.props=x.props;
			r.lock=(( Role ) o).lock;
			all.put( r.id,r );
			children.put( r.id,r );
			if(r.lock)locks.put( r.id,r );else{
				String n=x.propStr( "name" );
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
		ObjHead o=new ObjHead(0,0,0,0);
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
	if(d==null){
		d=new Domain( id,0,0 );
		if(d.exists()){
			d.loadDomain();
			domains.put( d.id,d );
			all.put( d.id,d );
	}else d=null;}return d;}

  public static Domain loadDomain0(){
	Domain d=domains.get( 0 );
	if(d==null){
		if(exists(0))
			d.loadDomain();
		else d=initNew();
		domains.put( d.id,d );
		all.put( d.id,d );
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
		d.save();int c=d.count( null );
		/*if(d.id!=n || c<2)
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
			o = new ObjHead( id, d.id, id, d.id );
			o.save();m.put( prt,o );
			all.put( o.id,o );
			o.loadProps();
			ObjProperty p = new ObjProperty( o.id );
			p.n = "name";
			p.v = prt.name();
			o.props.put( p.n, p );
			p.save();
			x=o.id;
		}
		Usr u = tl.usr;
		//create user admin admin
		if(n==0 || u==null) {
			o = m.get( Proto.Usr );
			u = d.new Usr( ++x, d.id, o.id );
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


  public class Role extends ObjHead{
	Role(Integer id,Integer parent, Integer proto){super(id,parent,proto,Domain.this.id);}
	Map<Integer,Usr>members;boolean lock;
	List<Object>operations;
	Map<Integer,ObjHead>resources;//Id;Map<String,String>resourcesProps;

	void init(){String name=propStr( "name" );if(props!=null)
		for(ObjProperty p:props.values()){
			Object v=p.v;
			if(p.n.startsWith( "member" ))
			{	Integer i=v instanceof Integer
				?(Integer)v:v instanceof Number?((Number)v).intValue()
				:v!=null?TL.Util.parseInt( v.toString(),-1 ):-1;
				Usr u=usrs.get( i );
				if(u==null){
					v=all.get( i );
					if(v instanceof Usr)
						u=(Usr)v;}
				if(u!=null){
					u.have.put(name,this);
					members.put( u.id,u );}
			}
			else if(p.n.startsWith( "resource" )){
				Integer i=v instanceof Integer
				?(Integer)v:v instanceof Number?((Number)v).intValue()
				:v!=null?TL.Util.parseInt( v.toString(),-1 ):-1;
				ObjHead o=all.get( i );
				if(o!=null){
					//o.roles.put(name,this);
					resources.put( o.id,o );}
			}
			else if(p.n.startsWith( "operation" ))
				operations.add( v );
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
