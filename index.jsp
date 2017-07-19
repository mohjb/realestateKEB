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
{Map m=new HashMap();
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
			return list(p,rs,pg,ps,tl);
		}
		ref=p.get("from");
		Long to,from=ref instanceof Number?((Number)ref).longValue():null;
		ref=p.get("to");
		to=ref instanceof Number?((Number)ref).longValue():null;//Double.NaN;
		StringBuilder sql=new StringBuilder("select * from `").append(PINVULt.dbtName).append("`");
		Object[]where=null;//TL.DB.Tbl.Cols.where(sql,TL.DB.Tbl.where())
		if(from!=null && to!=null)TL.DB.Tbl.Cols.where(sql
				,where=TL.DB.Tbl.where(
						TL.Util.lst(PINVULt.C.logTime,">="),from
						, TL.Util.lst(PINVULt.C.logTime,"<="),to
				));
		else if(from!=null )TL.DB.Tbl.Cols.where(sql
				,where=TL.DB.Tbl.where(
						TL.Util.lst(PINVULt.C.logTime,">="),from
				));
		else if(to!=null )TL.DB.Tbl.Cols.where(sql
				,where=TL.DB.Tbl.where(
						TL.Util.lst(PINVULt.C.logTime,"<="),to
				));
		else {tl.log("aswan2017.App.getLog:no from nor to",p);
			return p;}
		return list(p,sql.toString(),where,tl);
	}catch(Exception ex){tl.error(ex,"getLog");}
	return p;}
static List updateDomains(List rows,TL tl){
	List x=new LinkedList();
	PINVULt d=new PINVULt();
	for(Object o:rows)try{
		Map m=(Map)o;
		d.fromMap(m);
		d.logTime=tl.now;
		d.save();
		m.put(PINVULt.C.logTime.toString(),d.logTime);
		x.add(m);
	}catch(Exception ex){tl.error(ex,"updateDomn");}
	return x;}
//static void putEntities(List putEntities,TL tl){}
//static List getEntities(List getEntities,TL tl){return null;}
static List distinct(Map p,TL tl){return null;}
static StringBuilder sql(StringBuilder sql,Object[]where,TL.DB.Tbl t) {
	PINVULt d=new PINVULt();
	Field[]a=t.fields(t.getClass());//.fields();
	if(sql==null)sql=new StringBuilder();
	sql.append("select `")
			.append(a[0])//no
			.append("`,`").append(a[1])//domain
			.append("`,max(`").append(a[2])//logTime
			.append("`),`").append(a[3])//usr
			.append( "`,`").append(a[4])//entity
			.append( "`,`").append(a[5])//id
			.append( "`,`").append(a[6])//col
			.append( "`,`").append(a[7])//val
			.append("` from `").append(t.getName()).append("` ");
	TL.DB.Tbl.Cols.where(sql, where);
	sql.append(" group by `proto`,`id`,`n`");
	return sql;
}
static List getIds(List rows,TL tl){
	PINVULt d=new PINVULt();
	List x=new LinkedList();
	for(Object o:rows)try{
		Map m=(Map)o;d.fromMap( ( Map ) o );
		PINVULt p=new PINVULt();p.fromMap(m);
		PINVULt.C[]c=d.columns();
		Field[]a=PINVULt.fields(PINVULt.class);//.fields();
		StringBuilder sql=new StringBuilder("select `")
				                  .append(c[0])//no
				                  .append("`,`").append(c[1])//domain
				                  .append("`,max(`").append(c[2])//logTime
				                  .append("`),`").append(c[3])//usr
				                  .append( "`,`").append(c[4])//entity
				                  .append( "`,`").append(c[5])//id
				                  .append( "`,`").append(c[6])//col
				                  .append( "`,`").append(c[7])//val
				                  .append("` from `").append(p.getName()).append("` ");
		Object[]where=d.where(PINVULt.C.proto,d.proto, PINVULt.C.id,d.id);
		TL.DB.Tbl.Cols.where(sql, where);
		sql.append(" group by `proto`,`id`,`n`");
		try{ResultSet rs=TL.DB.R(sql.toString(), where);}
		catch(Exception ex){tl.error(ex,"aswan2017.App.PINVULt.getIds:where=",where);}
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
	PINVULt d=new PINVULt();
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
public static class PINVULt extends TL.DB.Tbl {//implements Serializable
	public static final String dbtName="PINVULt";
	@Override public String getName(){return dbtName;}
	@F public Integer no;
	@F(group=true) public Integer proto;
	@F public Integer /**user that made the change*/uid;
	@F(max=true) public Date logTime;//cancelled:lastModified
	@F(group=true) public String /**Object Id*/id,/**propertyName*/n;
	@F(json=true) public Object /**propertyValue*/v;
	public enum C implements CI{no,proto,uid,logTime,id,n,v;
		@Override public Class<? extends TL.DB.Tbl>cls(){return PINVULt.class;}
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
	@Override public CI pkc(){return C.no;}
	@Override public Object pkv(){return no;}
	@Override public C[]columns(){return C.values();}
	@Override public List creationDBTIndices(TL tl){
		return TL.Util.lst(
				TL.Util.lst("int(24) PRIMARY KEY NOT NULL AUTO_INCREMENT"//no
						,"int(8) NOT NULL DEFAULT 1"//proto
						,"int(8) NOT NULL DEFAULT 1"//uid
						,"timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"//logTime
						,"varchar(255) NOT NULL default '-'"//id
						,"varchar(255) NOT NULL default '-'"//propertyName
						,"text"//propertyValue
				)
				,TL.Util.lst(TL.Util.lst(C.proto,C.logTime)
						,TL.Util.lst(C.proto,C.id,C.logTime)
						,TL.Util.lst(C.proto,C.n,C.logTime)
						,TL.Util.lst(C.uid,C.proto,C.logTime) )
		);
/*
CREATE TABLE `PINVULt` (
  `no` int(24) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `proto` int(8) NOT NULL,
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
	static{registered.add(PINVULt.class);}
	/**
	 * uidMembership
	 * domainMember
	 *
	 * */
}//class PINVULt
/**
 * for Domain&Proto vs userRole Access-Control
 * in dbTbl-PINVULt(domain=0 and col=uid and val=<uid>) will be noted-in-this-javadoc as the0domains
 *  //-- cancelled-this-line: in the json-col dbTbl-usr.json , a property"domains" , will be noted-in-this-javadoc as usrDomains --//
 *
 * Prototype_Domain_UserRole_AccessControl
 * */
public static class ProDURAC extends TL.DB.Tbl {//implements Serializable
	public static final String dbtName="ProDoRAC";
	@Override public String getName(){return dbtName;}//public	Ssn(){super(Name);}
	@F public Integer no,domain,/**user that made the change*/uid;
	@F(group=true) public Integer proto,/**Object id / dbRow pk*/id;
	@F(group=true) public String /**propertyName*/n;
	@F public Date logTime;
	@F(json=true) public Object /**propertyValue*/v;
	public enum C implements CI{no,domain,uid,proto,id,n,logTime,v;//,lastModified;
		@Override public Class<? extends TL.DB.Tbl>cls(){return ProDURAC.class;}
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
	@Override public CI pkc(){return C.no;}
	@Override public Object pkv(){return no;}
	@Override public C[]columns(){return C.values();}
	@Override public List creationDBTIndices(TL tl){
		return TL.Util.lst(
				TL.Util.lst("int(24) PRIMARY KEY NOT NULL AUTO_INCREMENT"//no
						,"int(8) NOT NULL DEFAULT 1"//proto
						,"int(8) NOT NULL DEFAULT 1"//domain
						,"timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"//logTime
						,"INT(6) DEFAULT NULL"//uid
						,"varchar(255) NOT NULL default '-'"//id
						,"varchar(255) NOT NULL default '-'"//propertyName
						,"text"//propertyValue
				)
				,TL.Util.lst(TL.Util.lst(C.domain,C.proto ,C.logTime)
						,TL.Util.lst(C.domain,C.proto,C.n ,C.logTime)
						,TL.Util.lst(C.domain,C.proto,C.id,C.logTime)
						,TL.Util.lst(C.uid,C.domain,C.proto,C.n,C.logTime)
						,TL.Util.lst(C.domain,C.proto,C.n,C.v,C.logTime) )
				,TL.Util.lst(C.n,C.v,C.domain,C.proto ,C.logTime)
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
CREATE TABLE `ProDURAC` (
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
	static{registered.add(ProDURAC.class);}
	List<Map<String,ProDURAC>>objProp(int id,String pn,TL tl){
		List<Map<String,ProDURAC>>lm=new LinkedList<Map<String,ProDURAC>>();
		List<ProDURAC>l=new LinkedList<ProDURAC>();
		ProDURAC p=this;//new ProDURAC();
		//domain=0, col="usrMembership" , val=uid ,
		Object[]where=TL.DB.Tbl.where(C.n,pn,C.v,uid);
		TL.DB.Tbl.Itrtr it=new TL.DB.Tbl.Itrtr(
			sql(null,where,this).toString()
			,where,true);
		for(TL.DB.Tbl t:it)
		{	p=(ProDURAC) t;
			l.add(p);
		}
		for(ProDURAC i:l)
			lm.add(i.loadObjct());
		return lm;}
	Map<String,ProDURAC>loadObjct(){
		Map<String,ProDURAC>m=new HashMap<String,ProDURAC>();
		Object[]where=TL.DB.Tbl.where( C.proto,proto );
		TL.DB.Tbl.Itrtr i=new TL.DB.Tbl.Itrtr(
			sql(null,where,this).toString()
			,where,true);
		for (TL.DB.Tbl t: i )
		{ProDURAC p=(ProDURAC)t;
			m.put(p.n,p);
		}
		return m;}
	Map<String,Object>loadObj(){
		Map<String,Object>m=new HashMap<String,Object>();
		Object[]where=TL.DB.Tbl.where( C.proto,proto );
		TL.DB.Tbl.Itrtr i=new TL.DB.Tbl.Itrtr(
			sql(null,where,this).toString()
			,where,false);
		for (TL.DB.Tbl t: i )//TODO:might want to put oldest and newest logTime
		{ProDURAC p=(ProDURAC)t;
			m.put(p.n,p.v);}
		m.put("$",TL.Util.mapCreate(
			"domain",domain,"proto",proto ,"id",id));
		return m;}

}//class ProDURAC
public static class Domain extends ProDURAC{
	Map<String,Map>roles,protos;
	Map<String,Usr>usrs;
	Map<Integer,Object[]>props;
	/**as returned from method domains_protos_ids, a heirarchy of prototype chains*/
	static Map<Integer,Domain>domains;
	/**all nodes ,i.e. all protos, and nodes*/
	static Map<Integer,Object[]>all;
	/**as returned from method loadRoot*/
	static Domain domain0;
	public Domain domain(){return domains!=null?domains.get(domain):null;}
	/** */
	static Map<Integer,Usr>usrsAll;
	//static Map domainDef(int did){return null;}
	static List<Map>rolesDef(List<String>roleNames){return null;}
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
	Map loadRoot(TL tl){
		id=0;//new Integer(0);
		Map m=loadObj();
		return m;
	}
	/** load from dbTbl roles
	 * a role is found by propertyName:"roleName"
	 * a role object is defined by the following three rules:
	 *	1- proto referes to the role-proto
	 *	2- a role obj must have a propertyName"roleName" ( and only a role obj must have a propertyName"roleName")
	 *	3- the properties:roleName,
	 * */
	/**load from dbTbl
	 * 1st map-level is for domains, key-in-the-map is the domain-int and the data-from-the-key is a 2ndLevelMap
	 * 2ndLevelMap keys are all id's within the specified domain
	 * at level 3 are nodes( array of four)
	 *		index0 is id
	 *		, index1 is ref to (proto-node) or (3element array:ref to proto-node , domain-id, proto-domain-id)
	 *		, index2 is a Map of direct sub protos, where the key is subProto-id-int and val is subProto-node
	 *		, index3 is a map of all descendants,where the key is an subProto-id-int and val is subProto-node
	 * */
	Map<Integer,Domain > domains_protos_ids(TL tl) {
		domains=new HashMap<Integer,Domain>();//Map<Integer,Map<Integer,Object[]>>
		all=new HashMap<Integer,Object[]>();//Map<Integer,Object[]>
		StringBuilder sql=new StringBuilder("select `domain`,`proto',`id`,max(`logTime`) from `")
				                  .append(dbtName ).append( "` group by `proto`,`id`" );
		try{List<Integer[]>a0=TL.DB.qLInt( sql.toString() );//lt(sql.toString(),Integer.class);
			for(Integer[]x:a0) {
				Integer dId=x[0],pi=x[1],id=x[2];
				Map<Integer,Object[]>desc=null;
				Domain dmx,dom=domains.get(dId);
				if(dom==null)
					domains.put(dId,dom=new Domain());//HashMap<Integer,Object[]>());
				Object[]pnode,node=dom.props.get( id );
				if(node==null){
					node=all.get(id);
					if(node==null){
						all.put(id,node=new Object[4]);
						node[0]=id;
						node[1]=pi;
						dom.props.put(id,node);//node[2]=new HashMap<Integer,Object[]>();//node[3]=new HashMap<Integer,Object[]>();
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

public class Usr {
	Map<String,Map>roles,resources;Map props;
	public String un(){return(String)(props.get( "un" ));}
	public Integer uid(){return(Integer)(props.get( "uid" ));}
	public Domain domain(){return Domain.this;}
	boolean hasAccess(
		int resourceProto
		,String resourceId
		,Integer resourcePN//PropertyName
		,String operation){
		return false;
	}
	/**
	 * for param-user load:
	 * list-roles(ops,resources,usrs)
	 * list-protos(def)
	 * list-locks()
	 * */
	Map<String,List<Map<String,ProDURAC>>>loadURPL(int uid,TL tl){
		Map<String,List<Map<String,ProDURAC>>>m=new HashMap<String,List<Map<String,ProDURAC>>>();
		List<Map<String,ProDURAC>>l=objProp(uid,"usrMembership",tl);
		m.put("roles",l);
		l=objProp(uid,"usrLock",tl);
		m.put("locks",l);
		return m;}
}//class Usr
}//Domain
}//class App
