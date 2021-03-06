package mApp2017;

import com.sun.java.browser.plugin2.DOM;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;

//%><%mApp2017.TL.run(request, response, session, out, pageContext);%><%!
/**Created by moh on 14/7/17.*/
public class App {
static final String packageName="mApp2017",AppNm=packageName+".App",UploadPth="/aswan/uploads/";

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
 * , if param"updateCols" present then call updateCols
 * , if param"putEntities" present then call putEntities
 * , if param"getIds" present then call getIds
 */
 public static @TL.Op(urlPath = "*") Map poll
 (@TL.Op(prmName="getLogs")List getLogs
	,@TL.Op(prmName="getIds")List getIds
	,@TL.Op(prmName="writeObjs")List writeObjs
  ,@TL.Op(prmName="newEntries")List newEntries
	,TL tl) {
 	if(tl.usr==null)return null;
	Map m=new HashMap();try{
		if( newEntries!=null)
			m.put("newEntries",newEntries(newEntries,tl));
		if( writeObjs!=null)
			m.put("writeObjs",writeObjs(writeObjs,tl));
		if( getIds!=null){List a=new LinkedList<>();
			m.put("getIds",a);
			for (Object o:getIds)
			{	ObjHead x=ObjHead.factory(toInt( o));
				a.add(x.proto==null?o:x.hasViewAccess()?x:null);}
		}
		if( getLogs!=null){List<Map>a=new LinkedList<>();
			m.put("getLogs",a);
			for (Object o:getLogs) {
				Map x=(Map)o;
				a.add(getLog(x,tl));
			}
		}
	}catch ( Exception ex ){tl.error( ex,"App.poll" );}
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
		boolean propTbl=true;
		ref=p.get("from");if(ref==null){ref=p.get("From");if(ref!=null)propTbl=false;}
		Long to,from=ref==null?null:ref instanceof Number?((Number)ref).longValue():TL.Util.parseDate(String.valueOf(ref)).getTime();
		ref=p.get("to");if(ref==null){ref=p.get("To");if(ref!=null&& propTbl)propTbl=false;}
		to=ref==null?null:ref instanceof Number?((Number)ref).longValue():TL.Util.parseDate(String.valueOf(ref)).getTime();//Double.NaN;
		List w=new LinkedList();//ref=p.get("domain");
		if(from==null&&to==null) {
			p.put("msg",AppNm+".getLog:no parameter 'from' nor 'to'");
			return p;}
		if(from!=null){
			w.add( TL.Util.lst(ObjProperty.C.logTime,Tbl.Co.ge));w.add(from );}
		if(to!=null){
			w.add( TL.Util.lst(ObjProperty.C.logTime,Tbl.Co.le));w.add(to );}
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
				w.add(TL.Util.lst( s,Tbl.Co.in));
				w.add(l );
			}
		}
		Object[]where=new Object[w.size()];
		w.toArray( where );
		Tbl t=propTbl?new ObjProperty(  ):new ObjHead( );
		p= listLog(p,t.sql(t.columns(),where),where,tl);
		return p;
	}catch(Exception ex){tl.error(ex,"getLog");}
	return p;}

static Map listLog(Map m,String sql,Object[]where,TL tl){
	ResultSet rs=null;
	try{rs=TL.DB.R(sql, where);}catch(Exception ex){tl.error(ex
			,AppNm,".list(sql",sql,":where=",where,m);}
	m=listLog(m,rs,null,null,tl);
	return m; }

/**
 * pg is pagenation, a js-obj from client-http-request of the
 * ps is pagenation, a js-obj from session
 * */
static Map listLog(Map m,ResultSet rs,Map pg,Map ps,TL tl){
	if(tl.usr==null)return null;
	Object o=ps!=null?ps.get("properties"):null;if(o==null)o=m.get("From");if(o==null)o=m.get("To");
	//if(o==null){		if(ps!=null)			o=ps.get("properties");		//if(o==null&&pg!=null) o=ps.get("properties");	}
	boolean properties=o==null||o==Boolean.TRUE;
	Tbl t=properties?new ObjProperty():new ObjHead();
	if(m==null)m=new HashMap();
	List a=new LinkedList();
	m.put("a",a);
	try{boolean b=false;
		Field[]f=t.fields();
		while((b=rs.next()) && a.size()<1000)
		{	t.load(rs,f);
			if(tl.usr.hasAccess( Domain.Oper.view.name(),t.id))
				a.add(t.asMap());
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
				ps.put("properties",properties);
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
					pgs.remove(ref);//catch (Exception ex){tl.error(ex,"App.list:close:",pg,rs);}
			}
		 }
			TL.DB.close(rs,tl);
		}
	}catch(Exception ex){tl.error(ex,"listLog",pg,rs);}
	return m;}

/** new , create in database new-entry:
 * 1.property
 * 2.head
 *   1.domain
 *   2.usr
 *   3.role/lock
 *   4.proto
 *3. object
 */
static List newEntries(List rows,TL tl){
	if(tl.usr==null)return null;List list=new LinkedList();
	ObjHead h=null,x,y;ObjProperty p=null,po;
	for(Object o:rows)try
	{Map m=o instanceof Map?(Map)o:null;
		if(m==null){
			list.add( TL.Util.mapCreate( "return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","expected json-object item " ,"item",o) ) );
			continue;}
		Object prps=m.get( "props"),n=m.get( ObjProperty.C.n.name() );
		Map props=prps instanceof Map?(Map)prps:null;
		if(props!=null || n==null){
			h=new ObjHead(  );h.fromMap( m );//if(h==null)//h.id=h.proto=h.parent=h.domain=null;h.props=null;
			if(h.proto!=null && h.parent!=null){
				if(h.domain==null)
					h.domain=tl.usr.domain;
				x=ObjHead.factory(h.proto); y=ObjHead.factory(h.parent);
				if( x.proto==null || y.proto==null ){ // !x.exists() || !y.exists()
					//TODO //tl.usr.hasAccess(Domain.Oper.moveToProto.name(),Domain.Proto.Proto.get().id);
					tl.log("proto or parent not found");
					m.put("return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","proto or parent not found" ) );
					continue;
				}else
				if(!( tl.usr.hasAccess(Domain.Oper.newChild.name(),h.parent)
				 && tl.usr.hasAccess(Domain.Oper.newChild.name(),h.proto)))
					m.put("return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","no access operation 'newChild'" ) );
				else
				{	if(x instanceof Domain && tl.usr.hasAccess(Domain.Oper.newDomain.name(),h.domain))
					{	Domain d=Domain.initNew();
							m.put( ObjHead.C.id.name(),d.id );
							list.add( TL.Util.mapSet( m,
								ObjHead.C.id.name(),x.id
								,ObjHead.C.logTime.name(),x.logTime
								,"return",TL.Util.mapCreate(
									"statusCode",+1
									,"statusMsg","created new Domain")));
					}else
					{	//TODO: in Usr.hasAccess add checking parents
						if(y.children==null)
							y.children=new HashMap<>();
						h.id=h.maxPlus1( ObjHead.C.id );
						//m.put( ObjHead.C.id.name(),h.id );
						int c=x instanceof Domain.Usr?2:x instanceof Domain.Role?3:1;
						if(c>1){
							prps=props.get(c==2?"un":"name");
							String s=prps instanceof String?(String)prps:null;
							if(c==2){
								if(s!=null && !Domain.allUsrs.containsKey(s)){
									Domain.Usr u=h.domain().new Usr(h.id,h.parent,h.proto);
									x=u;
									u.domain().usrs.put(s,u);
									Domain.allUsrs.put(s,u);
								}else{c=0;
									m.put("return",TL.Util.mapCreate(
										"statusCode",-1,"statusMsg","user name used" ) );}
							}else if(c==3){
								if(s!=null && !h.domain().roles.containsKey(s)){
									Domain.Role u=h.domain().new Role(h.id,h.parent,h.proto);
									x=u;
									u.domain().roles.put(s,u);
									//u.init();
								}else{c=0;
									m.put("return",TL.Util.mapCreate(
										"statusCode",-1,"statusMsg","role name used" ) );}
							}
							if(c>0){// misleading because , props and children are always null, the reason is, h is always newly instantiated
								x.props=h.props;
								x.children=h.children;
							}
						}else x=h;
						if(c>0){
							y.children.put(x.id,x);
							ObjHead.all.put(x.id,x);x.logTime=tl.now;
							x.no=null;x.save();
							list.add( TL.Util.mapSet( m,
								ObjHead.C.id.name(),x.id
								,ObjHead.C.logTime.name(),x.logTime
								,"return",TL.Util.mapCreate(
									"statusCode",+1
									,"statusMsg","created new ObjHead" )));
							if(props !=null){for(Object k:props.keySet())
								{	prps=props.get(k);
									x.setProps( k,prps );
								}
								if(c==3)
									((Domain.Role)x).init();
							}
						}
					}
				}
			}
		}else if(n!=null ){
			//if(p==null)
				p=new ObjProperty(  );
			p.id=null;//p.n=n;
			p.fromMap( m );
			if(p.id==null)
				m.put("return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","expected 'id'" ) );
			else {
				h = ObjHead.factory( p.id );
				if ( h == null || h.proto == null )
					m.put( "return", TL.Util.mapCreate( "statusCode", -1, "statusMsg", "'id' not found" ) );
				else if ( p.n == null )
					m.put( "return", TL.Util.mapCreate( "statusCode", -1, "statusMsg", "expected n <propertyName>" ) );
				else
				{po=h.props==null?null:h.props.get( p.n );
					if(po!=null)
						m.put( "return", TL.Util.mapCreate( "statusCode", -1, "statusMsg", "can not use propertyName" ) );
					else if ( tl.usr.hasAccess( Domain.Oper.newProperty.name(), p.id ) ) {
						p.no = null;
						p.logTime = tl.now;
						p.save();
						list.add( TL.Util.mapSet( m
							, ObjProperty.C.id.name(), p.id
							, ObjProperty.C.logTime.name(), p.logTime
							, "return", TL.Util.mapCreate( "statusCode", +1, "statusMsg", "created new ObjProperty") ) );
					} else
						m.put("return", TL.Util.mapCreate( "statusCode", -1, "statusMsg", "no access operation 'newProperty'" ) );
				}
			}
		}else
			m.put("return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","item unknown") );
	}catch(Exception ex){tl.error(ex,"newEntries");
		list.add(TL.Util.mapCreate("return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","exception","exception",ex ) ));
	}
	return list;}

/** delete, 1.property , 2.head
 * */

/**
 * update database
 * cases:
 * 1. update 1.head/ 2.property

 *      3.move child
 *      4. link head to parent or proto from another domain


 *
 * update database
 * cases:
 * 1. update head
 * 2. update property / new property
 * 3. new head / new object head&properties
 * 4. delete: 1.head , 2.property
 * 5. new: 1.domain , 2.usr , 3.role , 4.proto
 * 6. Move child
 * 7. parent or proto from another domain
 * */
 static List writeObjs(List entries,TL tl){
	if(tl.usr==null)return null;
	List list=new LinkedList();ObjProperty p0=null,pr=null;//p0 is the original&registered property, pr is a temp for reading the new requested changes
	ObjHead h0=null,hr=null,hx;//h0 is the original&registered head, hr is a temp for reading the requested changes, hx is a temp for scratch work
	Tbl.CI clt=null;
	for(Object o:entries)try
	{Map m=o instanceof Map?(Map)o:null;
	 if(m==null){
		list.add( TL.Util.mapCreate( "return",TL.Util.mapCreate(
			"statusCode",-1,"statusMsg","expected json-object item " ,"item",o) ) );
		continue;}
	Tbl t=null;p0=null;h0=null;
	Object id=m.get( ObjProperty.C.id.name() ),n=null;
	if(id!=null )
	{h0=ObjHead.factory( toInt( id ) );
		if(h0==null || h0.proto==null)
		{tl.log( AppNm,".writeObjs:obj not found:",m );
			m.put("return",TL.Util.mapCreate( "statusCode"
				,-1,"statusMsg","proto or parent not found" ) );
			continue;
		}else n=m.get( ObjProperty.C.n.name() );
		if ( n==null )
		{t=h0;clt=ObjHead.C.logTime;}
		else
		{t=p0=h0.props==null?null:h0.props.get( n );
			if(t==null)
			{tl.log(AppNm,".writeObjs:property not found:",m);
				m.put("return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","proto or parent not found" ) );
				continue;}
			clt=ObjProperty.C.logTime;}
	}
	if(t==null)
	{m.put("return",TL.Util.mapCreate( "statusCode"
		,-1,"statusMsg","object not found" ) );
		continue;}
	if(t==h0 )// instanceof ObjHead
	{   if(hr==null)
			hr=new ObjHead(  );
		hr.set( h0 );
		t=hr;
	}
	else{
		if(pr==null)
			pr=new ObjProperty( h0.id,p0.n,p0.v );
		else
		{pr.no=null;pr.logTime=tl.now;pr.uid=tl.usr.id;
			pr.id=p0.id;pr.n=p0.n;pr.v=p0.v;
		}
		t=pr;
	}
	t.fromMap(m);/////////////////////////vital step/////reading the requested changes
	if(t==hr ){
		if(h0.proto!=hr.proto
		&&(!tl.usr.hasAccess(Domain.Oper.moveFromProto.name() ,hr.proto )
		|| !tl.usr.hasAccess(Domain.Oper.moveToProto.name() ,h0.proto )))
		{m.put("return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","no access move proto" ) );
			continue;}
		if( h0.parent!=hr.parent
		&&(!tl.usr.hasAccess(Domain.Oper.moveFromParent.name() ,hr.parent )
		|| !tl.usr.hasAccess(Domain.Oper.moveToParent.name() ,h0.parent )))
		{m.put("return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","no access move parent" ) );
			continue;}
		if( h0.domain!=hr.domain
		&&(!tl.usr.hasAccess(Domain.Oper.moveFromDomain.name() ,hr.domain )
		|| !tl.usr.hasAccess(Domain.Oper.moveToDomain.name() ,h0.domain )))
		{m.put("return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","no access move domain" ) );
			continue;}
	}
	if(!(t!=null && tl.usr.hasAccess(
		t instanceof ObjProperty
		?Domain.Oper.writeProperty.name()
		:Domain.Oper.writeObject.name()
		,t.id )))
	{m.put("return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","no access write" ) );
		continue;}//TODO: investigate what are the circumstances for other operations ,other than "view"
	hx=ObjHead.factory(h0.domain);
	Domain d=hx instanceof Domain?(Domain)hx:null;
		/*TODO: check cases of what is going to be changed:
		  head:
			1. head is domain
			2. head is proto
			1. change of domain of head
			2. change of parent of head
			3. change of proto of head
		  property:
			if proto is Role
				1. change of prop member
				2. change of prop resource
				3. change of prop operation
			if proto is Usr
				4. change of prop un

			in such cases, must update intermediatery maps, children, roles, users, allUsers, have, locks, all, domains
		*/
	if(t==hr )//hr is the new changes
	{
		if(hr.domain!=h0.domain){//TODO: investigate,diagnose,design,and implement all cases
			hx=ObjHead.factory(hr.domain);
			Domain d2=hx instanceof Domain?(Domain ) hx:null;
			if(h0.domain==h0.id || h0==d){
				if(d.usrs.size()>1 || d.roles.size()>1 || d.locks.size()>0){
					m.put("return",TL.Util.mapCreate( "statusCode"
						,-1,"statusMsg","domain not empty" ) );
					continue;
				}
				Domain.domains.remove( h0.id );
				hx=new ObjHead(  );hx.set(h0);
				ObjHead.all.put( hx.id,hx );
			}else if(hr.domain==hr.id || hx==hr){
				if(!tl.usr.hasAccess(Domain.Oper.newDomain.name() ,tl.usr.id )){
					m.put("return",TL.Util.mapCreate( "statusCode"
						,-1,"statusMsg","no access create new domain" ) );
					continue;
				}
				hx=ObjHead.factory( h0.parent );
				if(hx!=null && hx.children!=null) {
					hx.children.remove( h0.id );
					if(h0 instanceof Domain.Usr ){}else//TODO investigate,diagnose,design,and implement all cases
					if(h0 instanceof Domain.Role){}//TODO investigate,diagnose,design,and implement all cases
				}
				d2=Domain.initNew( hr.id );
				t=d2;
			}else{//TODO investigate,diagnose,design,and implement all cases
				if(!ObjHead.exists( Tbl.where(ObjHead.C.id,hr.domain ),ObjHead.dbtName)){
					m.put("return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","domain not found" ) );
					continue;
				}
			}
		}
		if(hr.parent!=h0.parent)
		{hx=ObjHead.factory( hr.parent );
			if(hx==null || hx.proto==null|| hx==h0 || hr.parent==h0.id ){
				m.put("return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","parent not found" ) );
				continue;
			}
			if(hx.children==null)
				hx.children=new HashMap<>(  );
			hx.children.put( h0.id,h0 );
			hx=ObjHead.factory(h0.parent);
			if(hx!=null && hx.children!=null)
				hx.children.remove( h0.id );
		}
		if(hr.proto!=h0.proto){
			hx=ObjHead.factory( hr.proto );
			if(hx==null||hx.proto==null){
				m.put("return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","proto not found" ) );
				continue;
			}
			//TODO: cases , from/to role/usr/domain/objhead
			if(hx instanceof Domain){}//TODO investigate,diagnose,design,and implement all cases
			if(hx instanceof Domain.Usr){}//TODO investigate,diagnose,design,and implement all cases
			if(hx instanceof Domain.Role){}//TODO investigate,diagnose,design,and implement all cases
		}
		t=h0;
	}else if(pr.v!=p0.v)// t==pr && p0!=null
	{	//if(h0 instanceof Domain){}
		if(h0 instanceof Domain.Usr && "un".equals( p0.n ) && pr.v!=null){
			String nm=pr.v.toString();
			if(Domain.allUsrs.containsKey( nm )){
				m.put("return",TL.Util.mapCreate( "statusCode",-1,"statusMsg","user-name not unique" ) );
				continue;
			}
			Domain.Usr u=(Domain.Usr)h0;
			String on=u.un();
			if(d!=null)
				d.usrs.remove( on );
			Domain.allUsrs.remove( on );
			Domain.allUsrs.put( nm,u );
			if(d!=null)
				d.usrs.put( nm ,u );
			p0.v=nm;
			tl.log( AppNm,".writeObjs:usr rename:old=",on," ,new un:",nm );
		}
		if(h0 instanceof Domain.Role){
			Domain.Role role=(Domain.Role)h0;
			String nm=role.propStr( "name" );
			if("name".equals( p0.n ) &&d!=null){
				String nv=pr.v.toString();
				if(!role.lock){
					d.roles.remove( nm );
					d.roles.put( nv ,role );
					for(Domain.Usr z:role.members.values()){
						z.have.remove( nm );
						z.have.put( nv,role );
					}
				}else for(Domain.Usr z:role.members.values()){
					if(z.locks!=null)z.locks.remove( nm );
					else z.locks=new HashMap<>(  );
					z.locks.put( nv,role );
				}tl.log( AppNm,".writeObjs:role rename:old=",nm," ,new-name:",nv );
			}else if(pr.n.startsWith( "operation" ))
			{role.operations.remove( p0.v );role.operations.add( pr.v );
			 tl.log( AppNm,".writeObjs:operation overwrite:old=",p0.v," ,current:",pr.v );
			}
			else if(pr.n.startsWith( "resource" )){
				hx=ObjHead.factory( toInt(pr.v) );
				if(hx!=null && hx.proto!=null ){
					role.resources.put( hx.id,hx );
					hx=role.resources.get( p0.v );
					if(hx!=null)
						role.resources.remove( hx.id );//
					tl.log( AppNm,".writeObjs:role-resource overwrite:old=",p0.v," ,current:",pr.v );
				}else {
					m.put( "return", TL.Util.mapCreate( "statusCode", -1, "statusMsg", "resource not found" ) );
					continue;
				}
			}
			else if(pr.n.startsWith( "member" )){
				hx=ObjHead.factory( toInt( pr.v) );
				if(hx instanceof Domain.Usr){
					Domain.Usr w=role.members.get( p0.v );
					if(w!=null)
					{role.members.remove( w.id );
						if( w.have!=null)
							w.have.remove( nm );
					}
					w=(Domain.Usr)hx;
					role.members.put( w.id,w );
					if(w.have==null)
						w.have=new HashMap<>(  );
					w.have.put( nm,role );
					tl.log( AppNm,".writeObjs:role-member overwrite:old=",p0.v," ,current:",pr.v );
				}else {
					m.put( "return", TL.Util.mapCreate( "statusCode", -1, "statusMsg", "member not found" ) );
					continue;
				}
			}
		}
		t=p0;p0.v=pr.v;
	}
	t.uid=tl.usr.id;t.logTime=tl.now;
	t.no=null;
	t.save();
	list.add( TL.Util.mapSet( m
		, ObjProperty.C.logTime.name(), t.logTime
		, "return", TL.Util.mapCreate(
			"statusCode", +1, "statusMsg", "saved") ) );
	}catch(Exception ex){tl.error(ex,"writeObjs");
		list.add(TL.Util.mapCreate("return",TL.Util.mapCreate(
			"statusCode",-1
			,"statusMsg","exception"
			,"exception",ex
			,"item",o ) ));
	}
	return list;}

static void staticInit(){ TL.registerOp( App.class); }

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
	catch ( Exception ex ){TL.tl().error( ex,AppNm,".tbl:" );}
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
 public abstract CI[]colmns();

 @Override public Object pkv(){return no;}

 public boolean exists(){CI[]groupBy=groupBy();
	Object[]where=new Object[groupBy.length*2];
	for(int i=0;i<groupBy.length;i++)
		where[i*2+1]=v((CI)(where[i*2]=groupBy[i]));
	return exists( where,getName() );}

public static boolean exists(Object[]where,String dbtName){return exists(where,null,dbtName);}

 public static boolean exists(Object[]where,CI[]groupBy,String dbtName){
	boolean b=false;
	int n=0;
	try{n=count( where,groupBy,dbtName );}catch ( Exception ex ){}
	b=n>0;
	return b;
 }

 Tbl load(){return loadBy(ciId(),id);}//@Override

 @Override Tbl loadBy(CI c,Object v){ResultSet rs=null;try{
 	rs=TL.DB.r( sql(colmns(),where( c,v ),groupBy()) ,v );
	if(rs.next())
		load( rs );
	}catch(Exception x){ TL.tl().error(x,
		"App.Tbl(",this,").loadBy(",c,",",v,")");}
	finally {
		TL.DB.close( rs );
    }
	return this;}//loadBy

 @Override public Tbl save() throws Exception{
	TL tl=TL.tl();if(uid==null)
		uid=tl.usr==null?0:tl.usr.id;
	super.save();
	return this;}

 public static List ids(java.util.Collection<? extends Tbl>a){
	if(a==null)return null;
	List l=new LinkedList<Integer>();// have.size() );
	for(Tbl t:a)l.add( t.id );
	return l;}

}//abstract class App.Tbl

/** db-tbl ORM wrapper * Id_Name_Val_Usr_LogTime oI.N.V.U.LT * */
public static class ObjProperty extends Tbl {//implements Serializable
	public static final String dbtName="ObjProperty";
	@Override public String getName(){return dbtName;}
	@F(group=true) public String /**propertyName*/n;
	@F(json=true) public Object /**propertyValue*/v;
	public enum C implements CI{no,uid,logTime,id,n,v;
		public Class<? extends Tbl>cls(){return ObjProperty.class;}
		@Override public Field f(){return Co.f(name(), cls());}
	}//C
	@Override public CI pkc(){return C.no;}
	@Override public C[]columns(){return C.values();}
	@Override  public CI[]colmns(){if(colmns==null) {
		C[] x = C.values();
		colmns= new CI[ x.length ];
		System.arraycopy( x, 0, colmns, 0, x.length );
		colmns[ C.logTime.ordinal() ] = Co.maxLogTime;
	}return colmns;}static CI[]colmns=null;
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
		ObjProperty p=new ObjProperty( 0 );
		for (TL.DB.Tbl t: p.query( p.colmns(),Tbl.where( C.id,o.id),p.groupBy() ,true  ) )
		{ p=(ObjProperty)t;
			o.props.put(p.n,p);}
		return o;}
	Map<String,Object>loadMap(){
		Map<String,Object>m=new HashMap<String,Object>();
		for (TL.DB.Tbl t: query(Tbl.where( C.id,id )) )//TODO:might want to put oldest and newest logTime
		{ObjProperty p=(ObjProperty)t;
			m.put(p.n,p.v);}
		ObjHead h0=ObjHead.all!=null?ObjHead.all.get(id):null;
		if(h0!=null)m.put("$",TL.Util.mapCreate("id",id
			,"parent",h0.parent,"proto",h0.proto,"domain",h0.domain));
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
		@Override public Field f(){return Co.f(name(), ObjHead.class);}
	}//C
	@Override public CI pkc(){return C.no;}
	@Override public C[]columns(){return C.values();}
	@Override  public CI[]colmns(){if(colmns==null) {
		C[] x = C.values();
		colmns= new CI[ x.length ];
		System.arraycopy( x, 0, colmns, 0, x.length );
		colmns[ C.logTime.ordinal() ] = Co.maxLogTime;
	}return colmns;}static CI[]colmns=null;

	/**param p is source, this is destination*/
	ObjHead set(ObjHead p){
		id=p.id;parent=p.parent;no=p.no;
		proto=p.proto;domain=p.domain;
		uid=p.uid;logTime=p.logTime;
		if(p.children==null)
			children=null;
		else{
			if(children==null)
				children=new HashMap<>(  );
			children.putAll(p.children);}
		return p;}

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
			,TL.Util.lst(C.proto,C.logTime)
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

	/**
	 * attempts to get reference of objHead or Domain.Usr or Domain.Role
	 * if id not found in "all" , attempts to load from db-tbl Objhead
	 * ,if not in db-tbl, then returns an objHead instance with `parent`==null and `proto`==null
	 * otherwise,
	 * loads props , and loads children
	 * some children are not completely loaded,
	 * such cases `children` would have the id as a key, but the value would be the parent objHead
	 * */
	public static ObjHead factory(Integer id){
		if(id==null)return null;
		ObjHead p,x,o=all.get( id );
		if(o==null){
			x=o=new ObjHead( id,null,null,TL.tl().usr.domain );
			o.loadBy( C.id,id );if(o.proto==null || o.parent==null)
				return o;
			p=o.parent==id?null:o.parent();
			if(p!=null && p.children!=null ){
				x=p.children.get( id );
				if(x==null || x.id!=id)//this case happens when not completely loaded
					p.children.put( id,o );//{all.put(id,x);return x;}
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
			if(x instanceof Domain || x.id==x.domain){//this shouldnt happen, cuz previously:d=Domain.loadDomain( o.domain );
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
				q1colTList( o.sql( cols( C.id,Co.maxLogTime)
					, where( C.parent, id ),o.groupBy() )
					, Integer.class, id );
				if(chldrn!=null&&chldrn.size()>0){
				 if ( o.children==null )
					o.children=new HashMap<>(  );
				 for(Integer i:chldrn){
					x=all.get( i );
					o.children.put( i,x );//x!=null?x:o// here happens the cases of not completely loading some children
				}}
			}catch ( Exception ex ){}
		}
		return o;
	}

	/** all protos in all domains */ // a different idea is to move member'all' to domain, and each domain would have its own id-sequence, and in cases where cross-domain links are required , the link would be represented as {class:'ObjHeadRef',id:<int:id>,domain:<int:domain>}, of course from jdbc  ObjProperty-v would be only a string, which is a problem
	public static Map<Integer,ObjHead>all=new HashMap<Integer,ObjHead>();

	/* *roles is the list of access control, if a user doesnt have any of the roles then the user has no access
	 *,if a user is in locks ,even if the user has a role for access, the user is locked-out and has no access* /
	//Map<String,Domain.Role>roles=new HashMap<String,Domain.Role>();//,locks=new HashMap<String,Domain.Role>();*/
	public Map<Integer,ObjHead>children;//,sub,descendents;
	public Map<String,ObjProperty>props;
	ObjHead(Integer id,Integer parent,Integer proto,Integer domain){
		super(id);this.parent=parent;this.proto=proto;this.domain=domain;}

	public ObjHead(){this(0,0,0,0);}
	ObjHead parent(){return all.get(parent);}//all==null?null:
	ObjHead proto(){return all.get(proto);}//all==null?null:
	Domain domain(){return (Domain)(all.get(domain));}// all==null?null: Domain.domains

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
		if(props==null)props=new HashMap<>(  );TL tl=TL.tl();
		for(int i=0;i<a.length;i+=2){
			String n=String.valueOf( a[i] );
			ObjProperty p=props.get( n );
			if(p==null)
				props.put( n,p=new ObjProperty( id,n,a[i+1] ) );
			else{ p.v=a[i+1];
			//TL tl=TL.tl();
			p.uid=tl.usr==null?0:tl.usr.id;}
			p.logTime=tl.now;
			p.save();
		}}

	public String propStr(String pn){
		Object p=propo( pn );
		String v=p instanceof String?(String)p:p==null?null:p.toString();
		return v;}

	public boolean hasViewAccess(){Domain.Usr u=TL.tl().usr;return u!=null&&u.hasAccess( Domain.Oper.view.name(),id ); }

	/**this is only for creating new objects in the database,
	 * where this method is synchronized so that only one thread
	 * runs at a time and allocate a new id number-value from
	 * the db-table-objHead and store it*/
	public synchronized ObjHead saveNewId()throws Exception{
		id=TL.DB.Tbl.maxPlus1( App.ObjHead.C.id, App.ObjHead.dbtName );
		if(proto==null)proto=id;
		if(domain==null)domain=TL.tl().usr.domain().id;
		if(parent==null)parent=domain;
		save();
		return this;}

	public TL.Json.Output jsonOutput(TL.Json.Output o,String ind,String path,boolean closeBrace)throws java.io.IOException{
		Domain.Usr u=TL.tl().usr;
		if (o.restrictedAccess &&( u==null || !u.hasAccess( Domain.Oper.view.name(),id )))
		{o.w( "\"noAccess\"" );o.accessViolation++;
			return o;}//throw new IOException( "no user access-control on object" );
		super.jsonOutput( o,ind,path,false );
		if(children!=null) //o.w(",\"children\":").oCollctn( children.keySet(),ind,o.comment?path+".children":path );
			jsonOutputIds(children.keySet()//values()
					, o.w(",\"children\":"),ind,o.comment?path+".children":path);
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
			for ( ObjHead p:l )if(p.hasViewAccess(  )){
				if(comma)o.w(',');else comma=true;
				o.o( p.id );
			}
			o.w("]");
		return o;}

	public TL.Json.Output jsonOutputIds(java.util.Set<Integer> ids
	,TL.Json.Output o,String ind,String path)throws java.io.IOException{
		o.w("[");Domain.Usr u=TL.tl().usr;
			boolean comma=false;
			for ( Integer pid:ids )if(u.hasAccess( Domain.Oper.view.name(), pid )){
				if(comma)o.w(',');else comma=true;
				o.o( pid );
			}
			o.w("]");
		return o;}

 App.ObjHead findChild( String pn,Object v){
	if(children==null)
		return null;
	for(App.ObjHead h:children.values()){
		Object o=h.propo( pn );
		if(o==v || (o!=null && o.equals( v )))
			return h;
	}return null;}

 App.ObjHead findChildByProto( Integer prot){
	if(children==null)
		return null;
	for(App.ObjHead h:children.values()){
		if(h.proto==prot)
			return h;
	}return null;}

 List<App.ObjHead>findChildrenByProto( Integer prot){
	if(children==null)
		return null;List l=new LinkedList(  );
	for(App.ObjHead h:children.values()){
		if(h.proto==prot)
			l.add( h );
	}return l;}

}//class ObjHead

 public static class Domain extends ObjHead{
	public enum Proto{Role,Usr,Proto,Lock;//,Membership;,Deleted,Screen,Component,AngularJs,AngularJsCtrl,AngularJsTemlplt,AngularJsState,AngularJsDirective,AngularJsFilter,AngularJsService,scriptJs,scriptJsServerSide,scriptJsServerSideRestEntry
	ObjHead get(){Domain d0=domains.get( 0 );if(d0!=null && d0.children!=null)
		for(Integer oid:d0.children.keySet()){ObjHead o=factory( oid );
			ObjProperty p=o.props==null?null:o.props.get( "name" );
			if(p!=null&&name().equals( p.v ))
				return o;}
		return null;}
	}

	public enum Oper{view,app//all,
		,writeObject,writeProperty
		,moveToDomain,moveToProto,moveToParent//,moveToDelete
		,moveFromDomain ,moveFromProto,moveFromParent
		,newProperty,newChild//,ArchiveView,ArchiveEntryEncrypt,JsFunc,JsCall
		,newDomain//,newUsr,newRole,newSubProto
	}

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
	for(TL.DB.Tbl t:o.query( colmns(),o.where( C.domain,id ,C.parent,id),groupBy() ,true)) {
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
	for(Integer xid:children.keySet()//values
			){ObjHead x=factory( xid );
		o=prots.get( x.proto );
		if(o!=null&&o!=x){
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
	}}
	for(Role r:roles.values())
		r.init(  );
	if(locks!=null)
		for(Role r:locks.values())
			r.init( );
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

  public static Domain initNew(){return initNew( null );}
  public static Domain initNew(Integer id){Domain d =null;TL tl=TL.tl();try
  {int n = id==null?Domain.maxPlus1( C.id ,dbtName):id,x=n;ObjHead o;
	d = new Domain( x, x, x );
	//d.uid=tl.usr==null?0:tl.usr.id;//d.id=n==1?n=0:n;
	domains.put( n,d );
	all.put( n,d );
	d.save();if(d.children==null)d.children=new HashMap<>(  );
	Map<Proto,ObjHead>m=new HashMap<Proto,ObjHead>();
	for(Proto prt:Proto.values()) { id=prt.ordinal()+n+1;
		o = new ObjHead( id, d.id, id, d.id );o.uid=d.uid;
		o.save();m.put( prt,o );d.children.put( o.id,o );
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
		u.save();String un=null;
		if (u.props ==null )
			u.props = new HashMap<>();
		ObjProperty p=u.props.get("un");
		if(p==null)u.setProps(
			"un",un="usr0"
			,"pw","6f8f57715090da2632453988d9a1501b"
		);else un=u.un();allUsrs.put(un,u);d.usrs.put(un,u);
	}else d.usrs.put( u.un(),u );
	//create role admin for admin
	o=m.get( Proto.Role);
	String rn="domain"+d.id+".usr0";
	Role r=d.new Role( ++x,d.id,o.id );
	r.save();
	r.setProps( "name",rn
		,"member1",u.id
		,"resource1",d.id
		,"resource2",m.get(Proto.Usr  ).id//Proto.Usr  .get().id
		,"resource3",m.get(Proto.Role ).id//Proto.Role .get().id
		,"resource4",m.get(Proto.Proto).id//Proto.Proto.get().id
		,"resource5",m.get(Proto.Lock ).id//Proto.Lock .get().id
	);
	for(Oper oper:Oper.values())
		r.setProps( "operation"+(oper.ordinal()+1) , oper.name());
	d.roles.put( rn,r );
	u.have.put( rn,r );//r.roles.put( rn,r );u.roles.put( rn,r );
  }catch ( Exception ex ){tl.error(ex,"App.Domain.initNew:");}
  return d;
	/*create Role,Usr,Lock,Proto
		//create a user
		//create a role, and add the new domain as resource, add user as member, add the default operations
		changePW
		createUsr
		login
		logout
		timeout
		changePW
		*/
	}

  public static List<Integer>loadDomainsIds(TL tl){
  	List<Integer>l=null;
	try {StringBuilder sql=new StringBuilder("select ");
		Co.generate(sql,cols(C.id,Co.maxLogTime));
		sql.append(" from `").append(dbtName ).append("` where `"+C.id+"`=`"+C.domain+"` group by ");
		Co.generate(sql,sttc.groupBy());
		l=TL.DB.q1colTList( sql.toString(),Integer.class);
	} catch ( SQLException e ) {
		tl.error( e,AppNm,".Domain.loadDomainsIds" );
	}
	return l;}

  @Override public TL.Json.Output jsonOutput(TL.Json.Output o,String ind,String path)throws java.io.IOException{
		try{int old=o.accessViolation;
			super.jsonOutput( o,ind,path ,false);
			if(old!=o.accessViolation)
			return o;}catch(Exception ex){return o;}
		if(usrs!=null)
			jsonOutput(usrs.values() , o.w(",\"usrs\":"),ind,o.comment?path+".usrs":path);
		if(roles!=null)
			jsonOutput(roles.values() , o.w(",\"roles\":"),ind,o.comment?path+".roles":path);
		if(locks!=null)
			jsonOutputIds(locks.keySet() , o.w(",\"locks\":"),ind,o.comment?path+".locks":path);
		if(o.comment)
			o.w("}//App.Domain&cachePath=\"").p(path).w("\"\n").p(ind);
		else o.w('}');
		return o;}//(o.comment?o.w("}//App.Domain&cachePath=\"").p(path).w("\"\n").p(ind):o.w('}'));

  public class Role extends ObjHead{
	Role(Integer id,Integer parent, Integer proto){super(id,parent,proto,Domain.this.id);}
	Map<Integer,Usr>members;boolean lock;
	List<Object>operations;
	Map<Integer,ObjHead>resources;//Id;Map<String,String>resourcesProps;

	void init(){String name=propStr( "name" );if(props!=null)
		for(ObjProperty p:props.values()){
			Object v=p.v;
			String s=v instanceof String?(String)v:null;
			Integer i=v instanceof Integer?(Integer)v:s!=null&&TL.Util.isNum( s )
				?new Integer( s) :v instanceof Number?((Number)v).intValue():null;
			if(p.n.startsWith( "member" ))
			{	if(i==null&&s==null)i=v instanceof Integer
				?(Integer)v:v instanceof Number?((Number)v).intValue()
				:null;
				if(i==null&&v!=null){s=v.toString();
					i=TL.Util.isNum( s ) ?new Integer( s):null;}
				v=all.get( i );
				Usr u=v instanceof Usr?(Usr)v:usrs.get( i );//if(u==null){//	v=all.get( i );//	if(v instanceof Usr)//		u=(Usr)v;}
				if(u!=null){
					if(lock)
					{	if(u.locks==null)
							u.locks=new HashMap<>();
						u.locks.put(name,this);}
					else
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
				if(!operations.contains( v ))operations.add( v );}
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
	boolean hasAccess(String operation,Integer resourceId){
		return hasAccess( resourceId,TL.Util.lst( operation ) );}

	boolean hasAccess(Integer resourceId,List operations){//,String resourcePN//PropertyName
		ObjHead c=all.get( resourceId );if(c==null||have==null)return false;
		for(Role r:have.values()){if(r!=null && r.resources!=null && r.operations!=null)
			for (ObjHead o: r.resources.values())
			{if((o.id==c.id || c.isInstanceOf( o ))
			 &&(r.operations.containsAll( operations ) ))  //||r.operations.contains( Oper.all.toString() )
			 {if(locks==null)return true;
				for(Role x:locks.values()){
					for (ObjHead z: x.resources.values())
					{if((z.id==c.id || c.isInstanceOf( z ))
					 &&(x.operations.containsAll( operations ) )) //||x.operations.contains( Oper.all.toString() )
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