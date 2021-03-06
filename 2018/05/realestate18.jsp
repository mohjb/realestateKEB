<%@page 
import="java.io.*
,java.sql.*
,java.util.*
,java.net.URL
,java.util.Date
,javax.servlet.*
,javax.servlet.http.*
,java.lang.annotation.*
,java.lang.reflect.Field
,java.lang.reflect.Method
,org.apache.commons.fileupload.FileItem
,org.apache.commons.fileupload.disk.DiskFileItemFactory
,org.apache.commons.fileupload.servlet.ServletFileUpload
,com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource"
contentType="text/json; charset=utf-8" pageEncoding="UTF8"
%><%Realestate201805.service(request,response);%><%! //<? 

/**
 * Created by Vaio-PC on 5/29/2018.
 */
public static class Realestate201805 {

	final static String packageName="/realestateKeb/2018/",jspName="realestate18.jsp";

	public enum Lang{ar,en}

	enum Lbl{;

		enum Ranks{r1(14),
			r2(15),
			r3(16),
			r4(17),
			;Ranks(int i){lblNo=i;}public int lblNo;
			public static Map def(){Map a=Util.mapCreate();for(Ranks p:ranks)a.put(p.name(),p.m(null));return a;}
			public Map m(Map m){if(m==null)m=Util.mapCreate();return Util.mapSet(m,"name",name(),"lblNo",lblNo);}

	}

		enum Contrct{
			all(0,18),
			c1(1,19),
			c2(2,20)			;
			public static Map def(){Map a=Util.mapCreate();for(Contrct p:contrcts)a.put(p.name(),p.m(null));return a;}
			public Map m(Map m){if(m==null)m=Util.mapCreate();return Util.mapSet(m,"name",name(),"lblNo",lblNo,"v",v);}

			public int v,lblNo;
			Contrct(int i,int lNo){lblNo=lNo;v=i;}
		};//Contrct

		public static Term[]terms=Term.values();
		public static Ranks[]ranks=Ranks.values();
		public static Contrct[]contrcts=Contrct.values();
		public static Statistics[]sttstcs=Statistics.values();

		enum Term{
			aggregate(1,22,21,"1")
			, annual(1,24,23,"`y`")
			, nineMonths(1,25,25,"`y`")//	=9months 
			, semiAnnual(2,27,26,"concat(`y`,'h',ceil(`m`/6))")
			, quarterly(4,29,28,"concat(`y`,'q',ceil(`m`/3))")
			, monthly(12,31,30,"concat(`y`,'m',`m`)")
			, weekly(52,33,32,"concat(`y`,'w',`w`)");
			public int base,lblNo,lblNo2;protected String sql;
			Term(int b,int n1,int n2,String s){base=b;lblNo=n1;lblNo2=n2;sql=s;}
			public static Map def(){Map a=Util.mapCreate();for(Term p:terms)a.put(p.name(),p.m(null));return a;}
			public Map m(Map m){if(m==null)m=Util.mapCreate();return Util.mapSet(m,"name",name(),"lblNo",lblNo,"lblNo2",lblNo2,"base",base);}//,"sql",sql
		}//enum Term

		enum Statistics{
			count("count(*)",34)//"عدد","Count")
			,amount("sum(`"+DataTbl.C.price+"`)",35)
			,avgPric1("sum(`"+DataTbl.C.price+"`)/sum(`"+DataTbl.C.area+"`)",36)
			,maxPric1("max(`"+DataTbl.C.price+"`/`"+DataTbl.C.area+"`)",37)
			,minPric1("min(`"+DataTbl.C.price+"`/`"+DataTbl.C.area+"`)",38)
			,maxPrice("max(`"+DataTbl.C.price+"`)",51)
			,minPrice("min(`"+DataTbl.C.price+"`)",52)
			,avgPrice("avg(`"+DataTbl.C.price+"`)",50)
			,avgLand("avg(`"+DataTbl.C.area+"`)",39)
			,SumLand("sum(`"+DataTbl.C.area+"`)",40)
			,maxLand("max(`"+DataTbl.C.area+"`)",41)
			,minLand("min(`"+DataTbl.C.area+"`)",42);

			static Statistics[]parse(Object p){
				if(p==null)return null;String s=p.toString().toLowerCase();
				List<Statistics>l=new LinkedList<>();
				List<Integer> i=new LinkedList<>();
				for(Statistics x:sttstcs) {
					int j=s.indexOf(x.toString().toLowerCase());
					if(j!=-1){int n=i.size();
						if(n<1){l.add(x);i.add(j);}
						else if(j<i.get(0)){l.add(0,x);i.add(0,j);}
						else if(j>i.get(n-1)){l.add(n,x);i.add(n,j);}
						else{
							while(--n>0)if(i.get(n)<j)
							{l.add(n,x);i.add(n,j);n=-1;}
						}
					}
				}Statistics[]a=new Statistics[l.size()];
				return l.toArray(a);}

			public String sql;public int lblNo;
			Statistics(String p,int i){sql=p;lblNo=i;}

			public static Map def(){Map a=Util.mapCreate();for(Statistics p:sttstcs)a.put(p.name(),p.m(null));return a;}
			public Map m(Map m){if(m==null)m=Util.mapCreate();return Util.mapSet(m,"name",name(),"lblNo",lblNo);}//,"sql",sql
		}//enum S//sttstcs
	}//enum Lbl

 @HttpMethod(usrLoginNeeded = false,useClassName = false) public static Map get(
	@HttpMethod(prmName = "from") Integer from,
	@HttpMethod(prmName = "to") Integer to,
	@HttpMethod(prmName = "typ") Integer pTyp,
	@HttpMethod(prmName = "gov") Integer pGov,
	@HttpMethod(prmName = "contract") Lbl.Contrct contrct,
	@HttpMethod(prmName = "term") Lbl.Term term,
	@HttpMethod(prmName = "sttstcs") Lbl.Statistics[]sttstcs,
	@HttpMethod(prmName = "showDefs")Boolean showDefs,
	@HttpMethod(prmName = "showLookup")Boolean showLookup,
	@HttpMethod(prmName = "showNamesGovs")Boolean showNamesGovs,
	//@HttpMethod(prmName = "op") String op,
	//@HttpMethod(prmName = "reversedXAxis")boolean reversedXAxis,
	TL tl)
	throws Exception
 {Map m=null;
	if(sttstcs==null)
		return m;
 try{int[]minmaxYear=DataTbl.minmaxYear();
	if(showDefs==null)showDefs=true;
	m=Util.mapCreate(!showDefs?"showDefs":"defs",!showDefs?showDefs:Util.mapCreate(
		"showDefs","boolean","showLookup","boolean","showNamesGovs","boolean"
		,"Lang",Lang.values()
		,"Lbl",Util.mapCreate(
			"Ranks", Lbl.Ranks.def()
			,"Contrct", Lbl.Contrct.def()
			,"Term", Lbl.Term.def()
			,"Statistics", Lbl.Statistics.def()
		)
		,"sttstcs","[<str:StatisticsName>,,, order-of-items and number-of-items is as required by client]"
		,"DataTbl",DataTbl.def()
		,"LookupTbl",LookupTbl.def()
		,"lookup","Map<Col,Map<Integer:code,Map<Lang,LookupTbl>>>"
		,"data","Map<int:place,Map<str:term ,Map<str:sttstc ,Double>>>"
	)//defs
	,"minmaxYear",minmaxYear
	,"term",term,"contrct",contrct,"sttstcs",sttstcs
	);
	//boolean devuser=tl.h.var("devuser",false);
	String op=null;
	if("resetLookup".equals( op ))
		tl.h.a( LookupTbl.class, null );//tl.out("application-scope has been reset for entry 'lookup' hashmap, tl.h.a( LookupTbl.class, null ); ");
	if(showNamesGovs==null)showNamesGovs=false;
	if(showNamesGovs)
		m.put("namesGovs",namesGovs(tl));
	else
		m.put("showNamesGovs",showNamesGovs);
	if(showLookup==null)showLookup=false;
	if(showLookup)
		m.put("lookup",LookupTbl.lookup());//Map<LookupTbl.Col, Map<Integer, Map<Lang, LookupTbl>>>lookup=;
	else
		m.put("showLookup",showLookup);
	if(from==null)
		from=minmaxYear[1];
	if(to==null)
		to=minmaxYear[1];
	if(from>to)
	{int tmp=from;
		from=to;
		to=tmp;
	}Util.mapSet(m,"from",from,"to",to);

	boolean allGovs=pGov==null||pGov==0 //gov.code==0//"0".equals()
		, aggGovs = pGov!=null&& 7 == pGov //,aggGovs="a".equals(Prm.gov.v(p))
		,allTyps=pTyp==null||0==pTyp;

	LookupTbl.Col col_Gov_or_name=allGovs?LookupTbl.Col.gov:LookupTbl.Col.name;

	if(term==null)
		term= Lbl.Term.annual;

	if(contrct==null)
		contrct=Lbl.Contrct.all;

	Util.mapSet(m,"allGovs",allGovs, "aggGovs", aggGovs,"allTyps",allTyps
		,"col_Gov_or_name",col_Gov_or_name,"gov",pGov,"typ",pTyp);

	if(sttstcs.length>0)
	try{
		StringBuilder sql=new StringBuilder("select ")
			.append(aggGovs ? 1 : allGovs?DataTbl.C.gov:DataTbl.C.name)
			.append( ",").append(term.sql).append(" as t");//if(term!=Lbl.Term.aggregate)

		for(Lbl.Statistics x:sttstcs)
			sql.append(",").append(x.sql);

		sql.append("from ").append(DataTbl.dbtName)
			.append(" where `y`>=? and `y`<=? ");
		if(contrct!=Lbl.Contrct.all)
			sql.append("and `").append(DataTbl.C.contract).append("`=? ");
		if(!allGovs && ! aggGovs)
			sql.append("and `").append(DataTbl.C.gov).append("`=? ");
		if(term==Lbl.Term.nineMonths)
			sql.append("and month(`"+DataTbl.C.d+"`)<=9");
		sql	.append(" group by ").append(aggGovs ? 1 : col_Gov_or_name)
			.append(",t");//if(term!=Lbl.Term.aggregate)
		//sql.append(" order by ").append(col_Gov_or_name);if(term!=Lbl.Term.aggregate)sql.append(",t");

		String s=sql.toString();
		if(showDefs)((Map)m.get("defs")).put("sql",s);

		PreparedStatement ps=Sql.p(s);// System.out.println(packageName+"/2012/03/05/"+jspName+":ps="+ps);
		int i = 1;
		ps.setObject(i++, from);//p[Prm.from	.ordinal()]
		ps.setObject(i++, to);//p[Prm.to				.ordinal()]
		if(contrct != Lbl.Contrct.all)
			ps.setObject(i++, contrct.v);
		if(! allGovs && ! aggGovs) ps.setObject(i++, pGov);
		if(! allTyps) ps.setObject(i++, pTyp);


		Map mn=null,ms=null,data=new HashMap();
		Util.mapSet(m,"data",data);//row=0;//reset(tbl);
		tl.log(packageName,"ps:",ps);
		ResultSet rs=ps.executeQuery();
		int c,nm;i=0;String yr;
		while(rs.next())
		{i++;c=1;nm=rs.getInt(c++);
			yr=rs.getString(c++);
			tl.log("row",i);
			mn=(Map)data.get(nm);
			if(mn==null)
				data.put(nm,mn=new HashMap());
			ms=(Map)mn.get(yr);
			if(ms==null)
				mn.put(yr,ms=new HashMap());
			for(Lbl.Statistics x:sttstcs)try{
				ms.put(x,rs.getDouble(c++));
			}catch(Exception ex){
				tl.error(ex,packageName,"get:errI:Statistic=",x,",theBigMap=",m,",nm=",nm,",yr=",yr,",c=",c,",row=",i);
				}
			tl.log("row=",i,":ms=",ms);
		}
		tl.log("ResultSet done:no.rows=",i,",theBigMap=",m);
	}//if(nullCol<0)
	catch(Throwable x){
		String end=tl.logo(jspName+":if(nullCol<0) Throwable:",x);
		tl.error(x,end);
	}//catch
 }//try
 catch(Throwable x){
	String end=tl.logo(jspName+":if(nullCol<0) Throwable:",x);
	tl.error(x,end);
 }//catch

	tl.log(packageName,"get:return:",m);
	return m;
 }//service


static Map<Integer,Integer>namesGovs(TL tl)throws java.io.IOException,java.sql.SQLException
{final String nmg=packageName+"lookup.namesGovs";
	Map<Integer,Integer>a=(Map<Integer,Integer>)tl.h.a(nmg);
	if(a==null)
	{Object[][]o=Sql.q("select distinct `"
			+DataTbl.C.name+"`,`"
			+DataTbl.C.gov+"` from "
			+DataTbl.dbtName);
		tl.h.a(nmg,a=new HashMap<Integer,Integer>());
		for(int i=0;i<o.length;i++)
		{Object on=o[i][0],og=o[i][1];
			int n=on instanceof Number
				?((Number)on).intValue()
				:Util.parseInt(String.valueOf(on),-1)
				,gov=og instanceof Number
					?((Number)og).intValue()
					:Util.parseInt(String.valueOf(og),0);
			a.put(n,gov);//a[n]=gov;
		}
	}
	return a;}

	public static class DataTbl extends Sql.Tbl{
		public static final String dbtName="d";
		@Override public String getName(){return dbtName;}//public DataTbl(){super(dbtName);}

		@F public Integer no,y,m,w,name;
		@F public String block;
		@F public Float area;
		@F public Integer typ;
		@F public Float price;
		@F public Integer gov;
		@F public String notes;
		@F public Integer contract;
		@F public Date d;
		@F public Integer sector;

		public enum C implements CI{no,y,m,w,name,block
			,area,typ,price,gov,notes,contract,d,sector;
			public Field f(){return Co.f(name(), DataTbl.class);}
			public String getName(){return name();}
			public Class getType(){return f().getType();}
/*CREATE TABLE `d` (
  `no` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `n` int(11) NOT NULL,
  `w` int(11) NOT NULL,
  `gov` int(11) NOT NULL,
  `area` decimal(65,3) NOT NULL,
  `price` decimal(65,3) NOT NULL,
  `contract` int(11) NOT NULL,
  `name` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `d` date NOT NULL,
  `block` text,
  `notes` text,
  PRIMARY KEY (`no`),
  KEY `dcgtn` (`d`,`contract`,`gov`,`type`,`name`),
  KEY `ndcgt` (`name`,`d`,`contract`,`gov`,`type`),
  KEY `tdcgn` (`type`,`d`,`contract`,`gov`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;*/
		}//C


	@Override public  CI[]columns(){return C.values();}
	@Override public Object[]wherePK(){Object[]a={C.no,no};return a;}
	@Override public List creationDBTIndices(TL tl){return null;}

	/**read from the database the minimum-year-intValue and the maximum-year-intValue,
		where index 0 is minimum , and index 1 is a maximum*/
	public static int[]minmaxYear()throws java.sql.SQLException
	{	TL tl=TL.tl();TL.H h=tl.h;final String str=packageName+".minmaxYear";
		int[]r=(int[])h.a(str);
		if(r==null)try{
			String sql="select min(`y`) , max(`y`) from "+dbtName;
			tl.log("App.DataTbl.minmaxYear:sql=",sql);
			Object[ ]a=Sql.q1row(sql);// group by year(`"+C.d+"`)");
			if(a!=null){int[]x={((Number)a[0]).intValue()
				,((Number)a[1]).intValue()};r=x;}
			h.a(str,r);}catch(Exception x)
		{tl.error(x,"App.DataTbl.minmaxYear:");
			if(r==null){int[]a={2000,2018};r=a;}
		}return r;}

	public static Map def(){return Util.mapCreate("no","Integer"
		,"y","Integer"
		,"m","Integer"
		,"w","Integer"
		,"name","Integer"
		,"block","String"
		,"area","Float"
		,"typ","Integer"
		,"price","Float"
		,"gov","Integer"
		,"notes","String"
		,"contract","Integer"
		,"d","Date"
		,"sector","Integer");}


	@Override public Json.Output jsonOutput( 
		Json.Output o, String ind, String path ) throws IOException {
		return o.w("{\"class\":\"DataTbl\",\"no\":").p(String.valueOf(no))
		.w(",\"y\":").p(String.valueOf(y))
		.w(",\"m\":").p(String.valueOf(m))
		.w(",\"name\":").p(String.valueOf(name))
		.w(",\"block\":").oStr( block,"")
		.w(",\"area\":").p(String.valueOf(area))//if(area==null)o.w("null");else o.p( area);
		.w(",\"typ\":").p(String.valueOf(typ))
		.w(",\"price\":").p(String.valueOf(price))//if( price==null)o.w("null");else o.p( price);
		.w(",\"gov\":").p(String.valueOf(gov))//if( gov==null)o.w("null");else o.p( gov);
		.w(",\"notes\":").oStr( notes,"")
		.w(",\"contract\":").p(String.valueOf(contract))
		.w(",\"d\":").oDt( d,"")
		.w(",\"sector\":").p(String.valueOf(sector))
		.w('}');}//jsonOutput

}//class DataTbl

/**lookup*/
public static class LookupTbl extends Sql.Tbl {//implements LookupTbl.ILang
	public static final String dbtName="t";
	@Override public String getName(){return dbtName;}//public LookupTbl(){super(dbtName);}

	@F public Integer no;
	@F public Col col;
	@F public Integer code;
	@F public String text;
	@F public Lang lang;

	LookupTbl copy(){return new LookupTbl().set(no, col, code, text,lang);}

	LookupTbl set(Integer n,Col c,Integer d,String x,Lang lng){no=n;col=c;code=d;text=x;lang=lng;return this;}

	public enum Col{gov,type,name,label,sector;}

	public enum C implements CI{no,col,code,text,lang;
		public Field f(){return Co.f(name(),LookupTbl.class );}
		public String getName(){return name();}
		public Class getType(){return f().getType();}
		/*
		CREATE TABLE `t` (
		  `no` int(6) NOT NULL AUTO_INCREMENT,
		  `col` set('gov','type','name','label','sector') NOT NULL DEFAULT 'label',
		  `code` int(4) NOT NULL,
		  `text` text NOT NULL,
		  `lang` set('ar','en') NOT NULL DEFAULT 'ar',
		  PRIMARY KEY (`no`),
		  KEY `cc` (`col`,`code`)
		) ENGINE=MyISAM DEFAULT CHARSET=utf8;
		 */
	}//C


	@Override public  CI[]columns(){return C.values();}
	@Override public Object[]wherePK(){Object[]a={C.no,no};return a;}
	@Override public List creationDBTIndices(TL tl){return null;}

	public static Map<Col,Map<Integer,Map<Lang,LookupTbl>>>
	lookup(){
		TL p=TL.tl();TL.H h=p.h;Object o=h.a(LookupTbl.class);
		Map<Col,Map<Integer,Map<Lang,LookupTbl>>>m=o==null?null:(
		Map<Col,Map<Integer,Map<Lang,LookupTbl>>>)o;
		if(m==null)try{LookupTbl l=new LookupTbl();
			h.a(LookupTbl.class,m=new HashMap<Col,Map<Integer,Map<Lang,LookupTbl>>>());
			for(Sql.Tbl i:l.query(Sql.Tbl.where())){
				p.log("App.LookupTbl.lookup:1:",i.toJson());
				Map<Integer,Map<Lang,LookupTbl>>n=m.get(l.col);
				if(n==null)
					m.put(l.col,n=new HashMap<Integer,Map<Lang,LookupTbl>>());
				Map<Lang,LookupTbl>ln=n.get(l.code);
				if(ln==null)
					n.put(l.code,ln=new HashMap<Lang,LookupTbl>(  ));
				LookupTbl t=ln.get( l.lang );
				if(t==null || t.no>l.no )//|| t.lang!=l.lang
					ln.put(l.lang, l.copy());
			}//for
			//p.log("App.LookupTbl.lookup:ex:",m);
		}//ifm==null
		catch(Exception x){p.error(x,"App.LookupTbl.lookup:ex:");
			if(m==null)
				m=new HashMap<Col,Map<Integer,Map<Lang,LookupTbl>>>();
		}return m;}

	public static Map def(){Map a=Util.mapCreate("no","Integer"
		,"col","Col"
		,"code","Integer"
		,"text","String"
		,"lang","Lang"
		,"Col",Col.values()
	);
		return a;}

	@Override public Json.Output jsonOutput( 
		Json.Output o, String ind, String path )
		throws IOException 
	{
		o.w("{\"class\":\"LookupTbl\",\"no\":");
		if(no==null)
			o.w("null");else 
			o.p(no);
		o.w(",\"col\":");
		if(col==null)
			o.w("null");else 
			o.oStr(col.toString(),"");
		o.w(",\"code\":");
		if(code==null)
			o.w("null");else 
			o.p( code);
		o.w(",\"text\":");
		if(text==null)
			o.w("null");else 
			o.oStr( text,"");
		o.w(",\"lang\":");
		if(lang==null)
			o.w("null");else 
			o.oStr( lang.toString(),"");
		o.w('}');return o;
	}

}//class LookupTbl


%><%@ include file="realestate1805.jsp"%><%!
}//class Realestate201805
%>
