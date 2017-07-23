package aswan2017;

/**
 * Created by moh on 14/7/17.
 */
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;

//%><%aswan2017.TL.run(request, response, session, out, pageContext);%><%!
/**
 * Created by Moh'd on 2/22/2017.
 */
public class App {
//public static class TL//public class App
static final String SsnNm="App",UploadPth="/aswan/uploads/";


 public static @TL.Op Map login
	(@TL.Op(prmName="un")String un
	,@TL.Op(prmName="pw")String pw,TL tl){return null;}

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
		,@TL.Op(prmName="getDistinct")List distinct
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
		m.put("updates",updateDomains(update,tl));
	if( distinct!=null){List a=new LinkedList<>();
		m.put("getDistinct",a);
		for (Object o:distinct) {
			Map x=(Map)o;
			a.add(distinct(x,tl));
		}
	}
	return m;}
/**all cases domain must be specified, or usr is in only one domain
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
 * 3.param domain , req usr
 * */
static Map getLog(Map p,TL tl){
	try{Map pg=(Map)p.get("pagenation");
		Object ref=pg!=null?pg.get("ref"):null;
		if(ref!=null){
			Map ps=(Map)tl.s("pagenation");
			ps=ps!=null?(Map)ps.get(ref):null;
			ResultSet rs=ps==null?null:(ResultSet)ps.get(ref);
			if(rs==null){pg.put("closed",true);return p;}
			return list(p,rs,pg,ps,tl);}
		ref=p.get("from");
		Long to,from=ref instanceof Number?((Number)ref).longValue():TL.Util.parseDate(String.valueOf(ref)).getTime();
		ref=p.get("to");
		to=ref instanceof Number?((Number)ref).longValue():TL.Util.parseDate(String.valueOf(ref)).getTime();//Double.NaN;
		ref=p.get("domain");
		StringBuilder sql=new StringBuilder("select * from `").append
			(ref==null?ObjProperty.dbtName:ObjHead.dbtName).append("`");
		Object[]where=null;//TL.DB.Tbl.Cols.where(sql,TL.DB.Tbl.where())
		if(from!=null && to!=null)TL.DB.Tbl.Cols.where(sql
			,where=TL.DB.Tbl.where(
				TL.Util.lst(ObjProperty.C.logTime,">="),from
				, TL.Util.lst(ObjProperty.C.logTime,"<="),to
			));
		else if(from!=null )TL.DB.Tbl.Cols.where(sql
			,where=TL.DB.Tbl.where(
					TL.Util.lst(ObjProperty.C.logTime,">="),from
			));
		else if(to!=null )TL.DB.Tbl.Cols.where(sql
			,where=TL.DB.Tbl.where(
				TL.Util.lst(ObjProperty.C.logTime,"<="),to
			));
		else {tl.log("aswan2017.App.getLog:no from nor to",p);
			return p;}
		return list(p,sql.toString(),where,tl);
	}catch(Exception ex){tl.error(ex,"getLog");}
	return p;}
static List updateDomains(List rows,TL tl){
	List x=new LinkedList();
	ObjProperty d=new ObjProperty();
	for(Object o:rows)try{
		Map m=(Map)o;
		d.fromMap(m);
		d.logTime=tl.now;
		d.save();
		m.put(ObjProperty.C.logTime.toString(),d.logTime);
		x.add(m);
	}catch(Exception ex){tl.error(ex,"updateDomn");}
	return x;}
//static void putEntities(List putEntities,TL tl){}
//static List getEntities(List getEntities,TL tl){return null;}
static List distinct(Map p,TL tl){return null;}

static List getIds(List rows,TL tl){
	ObjProperty d=new ObjProperty();
	List x=new LinkedList();
	for(Object o:rows)try{
		Map m=(Map)o;
		d.fromMap( ( Map ) o );
		ObjProperty p=new ObjProperty();
		p.fromMap(m);
		//ObjProperty.C[]c=d.columns();
		//Field[]a=ObjProperty.fields(ObjProperty.class);//.fields();
		Object [ ] w = d.where(ObjProperty.C.id,d.id);
		StringBuilder sql=/*new StringBuilder("select `")
.append(c[0])//no
.append("`,`").append(c[1])//domain
.append("`,max(`").append(c[2])//logTime
.append("`),`").append(c[3])//usr
.append( "`,`").append(c[4])//entity
.append( "`,`").append(c[5])//id
.append( "`,`").append(c[6])//col
.append( "`,`").append(c[7])//val
.append("` from `").append(p.getName()).append("` ");
		Object[]where=d.where(ObjProperty.C.proto,d.proto, ObjProperty.C.id,d.id);
		TL.DB.Tbl.Cols.where(sql, where);
		sql.append(" group by `proto`,`id`,`n`");
		*/
		d.sql(d.columns(),w, TL.DB.Tbl.Cols.cols(ObjProperty.C.id,ObjProperty.C.n));
		try{ResultSet rs=TL.DB.R(sql.toString(), w);}
		catch(Exception ex){tl.error(ex,"aswan2017.App.ObjProperty.getIds:where=",w);}
		for(TL.DB.Tbl t:d.query(d.where()))
			x.add(t.asMap());
	}catch(Exception ex){tl.error(ex,"updateCols");}
	return x;}
static Map list(Map m,String sql,Object[]where,TL tl){
	try{ResultSet rs=TL.DB.R(sql, where);
		m=list(m,rs,null,null,tl);
	}catch(Exception ex){tl.error(ex
			,"aswan2017.App.list(sql",sql,":where=",where,m);}
	return m;}
static Map list(Map m,Map pg,TL tl){
	if(pg==null)return m;
	Object ref=pg.get("pagenation");
	if(ref==null)return m;
	Map pgs=(Map)tl.s("pagenation");
	if(pgs==null)return m;
	Map ps=(Map)pgs.get(ref);
	if(ps==null)return m;
	ResultSet rs=(ResultSet)ps.get("rs");
	if(rs==null)return m;
	return list(m,rs,pg,ps,tl);}
/**
 * pg is pagenation, a js-obj from client-http-request of the
 * ps is pagenation, a js-obj from session
 * */
static Map list(Map m,ResultSet rs,Map pg,Map ps,TL tl){
	ObjProperty d=new ObjProperty();
	if(m==null)m=new HashMap();
	List a=new LinkedList();
	m.put("a",a);
	try{boolean b=false;
		Field[]f=d.fields();
		while((b=rs.next()) && a.size()<1000)
		{	d.load(rs,f);
			a.add(d.asMap());
		}
		if(b){
			if(pg==null)pg=new HashMap();
			m.put("pagenation",pg);
			Object ref=pg.get("ref");
			if(ref==null)
				pg.put("ref",ref=new Date().getTime());
			Map pgs=(Map)tl.s("pagenation");
			if(pgs==null)
				tl.s("pagenation", pgs = new HashMap());
			if(ps==null) {
				pgs.put(ref, ps = new HashMap());
				ps.put("ref",ref);
				ps.put("rs",rs);
			}
			Object o=ps.get("page");
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
				Map pgs=(Map)tl.s("pagenation");
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
/**
 * Proto_Id_Name_Val_Usr_LogTime P.I.N.V.U.LT)
 * */
public static class ObjProperty extends TL.DB.Tbl {//implements Serializable
	public static final String dbtName="ObjProperty";
	@Override public String getName(){return dbtName;}
	@F public Integer no, /**user that made the change*/uid;
	@F(max=true) public Date logTime;//cancelled:lastModified
	@F(group=true) public Integer /**Object Id*/id;
	@F(group=true) public String /**propertyName*/n;
	@F(json=true) public Object /**propertyValue*/v;
	public enum C implements CI{no,uid,logTime,id,n,v;
		@Override public Class<? extends TL.DB.Tbl>cls(){return ObjProperty.class;}
		@Override public Class<? extends TL.Form>clss(){return cls();}
		@Override public String text(){return name();}
		@Override public Field f(){return Cols.f(name(), cls());}
		@Override public TL.DB.Tbl tbl(){return TL.DB.Tbl.tbl(cls());}
		@Override public void save(){tbl().save(this);}
		@Override public Object load(){return tbl().load(this);}
		@Override public Object value(){return val(tbl());}
		@Override public Object value(Object v){return val(tbl(),v);}
		@Override public Object val(TL.Form f){return f.v(this);}
		@Override public Object val(TL.Form f,Object v){return f.v(this,v);}
		public String prefix(){return this==logTime?"max(`":"`";}
		public String suffix(){return this==logTime?"`)":"`";}
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
				,TL.Util.lst(C.n,C.v,C.logTime)
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


	Map<String,ObjProperty>loadObj(){
		Map<String,ObjProperty>m=new HashMap<String,ObjProperty>();
		//Object[]where=;TL.DB.Tbl.Itrtr i=new TL.DB.Tbl.Itrtr(sql(columns(),where),where,true);
		for (TL.DB.Tbl t: query( TL.DB.Tbl.where( C.id,id) ,true  ) )
		{ObjProperty p=(ObjProperty)t;
			m.put(p.n,p);}
		return m;}

	Map<String,Object>loadMap(){
		Map<String,Object>m=new HashMap<String,Object>();
		//Object[]where=TL.DB.Tbl.where( C.id,id );TL.DB.Tbl.Itrtr i=new TL.DB.Tbl.Itrtr(sql(null,where,this).toString(),where,false);
		for (TL.DB.Tbl t: query(TL.DB.Tbl.where( C.id,id )) )//TODO:might want to put oldest and newest logTime
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
/**
 * for Domain&Proto vs userRole Access-Control
 * */
public static class ObjHead extends TL.DB.Tbl {//implements Serializable
	public static final String dbtName="ObjHead";
	@Override public String getName(){return dbtName;}
	@F public Integer id,/**parent-object*/parent
	,/**the super-prototype*/proto
	,/**belongs to which domain/company/realm/accessControlContext */domain
	,/**user that made the change*/uid;
	@F(max=true) public Date logTime;
	public enum C implements CI{id,parent,proto,domain,uid,logTime;
		@Override public Class<? extends TL.DB.Tbl>cls(){return ObjHead.class;}
		@Override public Class<? extends TL.Form>clss(){return cls();}
		@Override public String text(){return name();}
		@Override public Field f(){return Cols.f(name(), cls());}
		@Override public TL.DB.Tbl tbl(){return TL.DB.Tbl.tbl(cls());}
		@Override public void save(){tbl().save(this);}
		@Override public Object load(){return tbl().load(this);}
		@Override public Object value(){return val(tbl());}
		@Override public Object value(Object v){return val(tbl(),v);}
		@Override public Object val(TL.Form f){return f.v(this);}
		@Override public Object val(TL.Form f,Object v){return f.v(this,v);}
	}//C
	@Override public CI pkc(){return C.id;}
	@Override public Object pkv(){return id;}
	@Override public C[]columns(){return C.values();}
	@Override public List creationDBTIndices(TL tl){
		return TL.Util.lst(TL.Util.lst(
		"int(8) PRIMARY KEY NOT NULL AUTO_INCREMENT"//id
			,"int(8) NOT NULL DEFAULT 1"//parent
			,"int(8) NOT NULL DEFAULT 1"//proto
			,"int(8) NOT NULL DEFAULT 1"//domain
			,"INT(6) DEFAULT NULL"//uid
			,"timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"//logTime
		),TL.Util.lst(TL.Util.lst(C.domain,C.proto ,C.logTime)
			,TL.Util.lst(C.parent ,C.logTime)
			,TL.Util.lst(C.logTime)
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

	/** all protos in all domains*/
	static Map<Integer,ObjHead>all;
	Map<Integer,ObjHead>sub,children;//,descendents;
	Map<String,Object>props=new HashMap<String,Object>();

	ObjHead(Integer id,Integer parent,Integer proto,Integer domain){this.id=id;this.parent=parent;this.proto=proto;this.domain=domain;}

	ObjHead parent(){return all==null?null:all.get(parent);}
	ObjHead proto(){return all==null?null:all.get(proto);}
	Domain domain(){return  Domain.domains==null?null: Domain.domains.get(domain);}

	//isDomain::= if proto refers to Domain
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

}//class ObjHead

public static class Domain extends ObjHead{
	Map<String,ObjHead>roles;//key:String is n=Name and proto=<id:proto isDescendentOf Role>
	Map<String,Usr>usrs;

	/**as returned from method domains_protos_ids, a heirarchy of prototype chains*/
	static Map<Integer,Domain>domains;
	static Map<Integer,Usr>usrsAll;

	/**as returned from method loadRoot*/
	static Domain domain0;

	Domain(Integer id,Integer parent,Integer proto,Integer domain ){super(id,parent,proto,domain);}//Domain(){this(0,0);}

	//static Map domainDef(int did){return null;}static List<Map>rolesDef(List<String>roleNames){return null;}

	/**
	 * obj no=0, has properties(references to abstract classes):
	 *{Role:<proto-int>
	 * ,Lock:<int:proto>
	 * ,Usr:<int:proto>
	 * ,UsrRole:<int:proto>
	 *
	 *,,,<otherApp-protos>
	 *	*	{	id:<int>,proto:<int>,domain:<int>
	 *	*		,roleName:<str>
	 *	*		,usrs:[<uid>,,,]
	 *	*		,ops:[<str>,,,]
	 *	*		,resources:[ {id:<str >},,, ]
	 *	*	},,,
	 * *** /// --- protos:[<int:proto>,,,] 	* ,domains:[]
	 *}
	 *
	 * other domains will have similar properties,
	 * and
	 * */
	static Map loadRoot(TL tl){
		domain0=new Domain(0,0);domain0.proto=0;

		domain0.props=domain0.loadObj();

		if(domains==null)
			domains_protos_ids(tl);
		Domain root=domains.get(0);

		//load all ObjHead that have domain equal to this.id , they should be prototypes, then each proto should have prop name
		//then each proto has sub-proto, but at the first proto level,
		// look for n='Name' and v= 'User','Role','MemberShip','Lock','usrs','locks','roles',
		// within each role in roles find 'memberships',
		//load roles , load users , load protos , load memberships ,load locks
		return domain0.props;
	}
	/**load from dbTbl-ObjHead
	 *
	 * 	Map<String,ObjProperty>loadObj(){
	 Map<String,ObjProperty>m=new HashMap<String,ObjProperty>();
	 Object[]where=TL.DB.Tbl.where( C.id,id);
	 TL.DB.Tbl.Itrtr i=new TL.DB.Tbl.Itrtr(
	 sql(null,where,this).toString()
	 ,where,true);
	 for (TL.DB.Tbl t: i )
	 {ObjProperty p=(ObjProperty)t;
	 m.put(p.n,p);}
	 return m;}

	 * */
	static Map<Integer,Domain > loadAll(TL tl) {
		domains=new HashMap<Integer,Domain>();
		all=new HashMap<Integer,ObjHead>();
		StringBuilder sql=new StringBuilder(
"select `id`,`parent`,`proto',`domain`,`uid`,max(`logTime`) from `")
			.append(dbtName ).append( "` group by `id`" );
		try{
			for(Integer[]x:a0) {
				Integer dId=x[0],pi=x[1],id=x[2];
				Map<Integer,ObjHead>desc=null;
				Domain dmx,dom=domains.get(dId);
				if(dom==null)
					domains.put(dId,dom=new Domain(id,dId));
				ObjHead pnode,node=dom.props==null?null:dom.props.get( id );
				if(node==null){
					node=all.get(id);
					if(node==null){
						all.put(id,node=new ObjHead(id));
						node.id=id;
						node.proto=pi;
						dom.protos.put(id,node);//node[2]=new HashMap<Integer,Object[]>();//node[3]=new HashMap<Integer,Object[]>();
					}else{
						dom.props.put(id,node);
						if(node[2]!=null)
						{//desc=(Map<Integer,Object[]>)node[2];
							//for(Object[]:desc.values())console.log()
							tl.log("line2567:parent loaded after children:",node[2],node[3]);
						}
					}
				}
				pnode=dom.props.get(pi);
				if(pnode==null){
					pnode=all.get(pi);
					if(pnode==null)
					{	 all.put(pi,pnode=new Object[4]);
						pnode[0]=pi;//pnode[2]=desc=new HashMap<Integer,Object[]>( );
					}else
					{	 for(Integer dx:domains.keySet()){
						dmx=domains.get( dx );
						if(dmx.props.containsKey( pi ))
						{	tl.log();//must tell all the subProtos , the parent is in another domain, by changing node[1] from pnode to {dId,pnode}
							Object[]aux={pnode,dId,dx};
							node[1]=aux;
							break;
						}
					}
					}
				}if(node[1]==null)
					node[1]=pnode;
				else if(node[1] instanceof Number)
					tl.log("line2589");
				else
					tl.log("line2591");
				if(pnode[2] instanceof Map)
					desc=(Map<Integer,Object[]>)pnode[2];
				else tl.log("line2595");//desc=null;
				if(desc==null)
					pnode[2]=desc=new HashMap<Integer,Object[]>( );
				desc.put( id,node );
			}//for
			for(Integer x:all.keySet()){
				Object[]node=all.get(x);
				Map<Integer,Object[]>sub=(Map<Integer,Object[]>)node[2];
				Map<Integer,Object[]>desc=(Map<Integer,Object[]>)node[3];
				if(sub!=null){
					if(desc==null)
						node[3]=desc=new HashMap<Integer,Object[]>();
					domains_protos_ids(tl,0,desc,sub);
				}}
		}catch(Exception ex){}
		return domains;}
	private void domains_protos_ids(TL tl,int depth,Map<Integer,Object[]>node,Map<Integer,Object[]>desc ){//,Map<Integer,Object[]>all,Map<Integer,Map<Integer,Object[]>>domains
		depth++;for(Integer i:desc.keySet()){
			Object[]x=desc.get(i);
			if(depth>1)node.put( i,x );
			if(x[2] instanceof Map)
				domains_protos_ids(tl,depth,node,(Map<Integer,Object[]>)x[2]);
		}}

	public class Usr extends ObjHead{
		Map<String,Map>roles,resources;Map props;Usr(Integer id,Integer domain){super(id,domain);}
		public String un(){return(String)(props.get( "un" ));}
		public Integer uid(){return(Integer)(props.get( "uid" ));}
		public Domain domain(){return Domain.this;}
		boolean hasAccess(
			int resourceProto
			,String resourceId
			,Integer resourcePN//PropertyName
			,String operation){return false;}

		/**
		 * for param-user load:
		 * list-roles(ops,resources,usrs)
		 * list-protos(def)
		 * list-locks()
		 * */
		Map<String,List<Map<String,ObjHead>>>loadURPL(int uid,TL tl){
			Map<String,List<Map<String,ObjHead>>>m=new HashMap<String,List<Map<String,ObjHead>>>();
			List<Map<String,ObjHead>>l=objProp(uid,"usrMembership",tl);
			m.put("roles",l);
			l=objProp(uid,"usrLock",tl);
			m.put("locks",l);
			return m;}
	}//class Usr
}//Domain


}//class App
