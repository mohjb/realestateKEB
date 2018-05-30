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

		enum Ranks{r1("الأول","1st"),
			r2("الثاني","2nd"),
			r3("الثالث","3rd"),
			r4("الرابع","4th");protected String en,ar;
			Ranks(String a,String e){ar=a;en=e;}
			public Map lang(Map m){m.put("ar",ar);m.put("en",en );return m;}
			public static Map def(){Map a=Util.mapCreate();for(Ranks p:ranks)a.put(p.name(),p.m(null));return a;}
			public Map m(Map m){if(m==null)m=Util.mapCreate();return Util.mapSet(m,"name",name(),"ar",ar,"en",en);}
		}

		enum Contrct{
			all(0,"إجمالي العقود","Total"),
			c1(1,"عقود مسجلة","Registered"),
			c2(2,"وكالات عقارية","Agent")
			;
			public static Map def(){Map a=Util.mapCreate();for(Contrct p:contrcts)a.put(p.name(),p.m(null));return a;}
			public Map m(Map m){if(m==null)m=Util.mapCreate();return Util.mapSet(m,"name",name(),"ar",ar,"en",en,"v",v);}

			protected String ar,en;public int v;
			Contrct(int i,String p,String e){ar=p;en=e;v=i;}
			public Map lang(Map m){m.put("ar",ar);m.put("en",en );return m;}
		};//Contrct

		public static Term[]terms=Term.values();
		public static Ranks[]ranks=Ranks.values();
		public static Contrct[]contrcts=Contrct.values();
		public static Statistics[]sttstcs=Statistics.values();

		enum Term{
			aggregate(1,"إجمالي","إجمالي الفترة" ,"Aggregate","aggregate","1")
			, annual(1,"سنة","سنوي" ,"Annual","Year","`y`")
			, nineMonths(1,"9شهور","9شهور" ,"Nine Months","Nine Months","`y`")//	=9months
			, semiAnnual(2,"النصف","نصف سنوي" ,"Semi-Annual","Half","concat(`y`,'h',ceil(`m`/6))")
			, quarterly(4,"الربع","ربع سنوي" ,"Quarterly","Quarter","concat(`y`,'q',ceil(`m`/3))")
			, monthly(12,"شهر","شهري" ,"Monthly","Month","concat(`y`,'m',`m`)")
			, weekly(52,"اسبوع","اسبوعي" ,"Weekly","Week","concat(`y`,'w',`w`)");
			public int base;protected String ar,lbl,en,enLbl,sql;
			Term(int b,String a,String lbl,String e,String el,String s){base=b;ar=a;this.lbl=lbl;en=e;enLbl=el;sql=s;}
			public Map lang(Map m){m.put("ar",ar);m.put("en",en );return m;}
			public Map lbl(Map m){m.put("arLbl",lbl);m.put("enLbl",enLbl );return m;}
			public static Map def(){Map a=Util.mapCreate();for(Term p:terms)a.put(p.name(),p.m(null));return a;}
			public Map m(Map m){if(m==null)m=Util.mapCreate();return Util.mapSet(m,"name",name(),"ar",ar,"lbl",lbl,"en",en,"enLbl",enLbl,"base",base,"sql",sql);}
		}//enum Term

		enum Statistics{
			count("count(*)","عدد","Count")
			,amount("sum(`"+DataTbl.C.price+"`)","إجمالي قيمة التداول","Total Price")
			,avg("sum(`"+DataTbl.C.price+"`)/sum(`"+DataTbl.C.area+"`)","متوسط السعر","Average Price")
			,max("max(`"+DataTbl.C.price+"`/`"+DataTbl.C.area+"`)","أعلى سعر متر","Maximum Price of 1 square meter")
			,min("min(`"+DataTbl.C.price+"`/`"+DataTbl.C.area+"`)","أقل سعر متر","Minimum Price of 1 square meter")
			,maxPrice("max(`"+DataTbl.C.price+"`)","أعلى سعر","Maximum Price")
			,minPrice("min(`"+DataTbl.C.price+"`)","أقل سعر","Minimum Price")
			,avgPrice("avg(`"+DataTbl.C.price+"`)","متوسط السعر","Average Price")
			,avgLand("avg(`"+DataTbl.C.area+"`)","متوسط المساحة","Average Area")
			,SumLand("sum(`"+DataTbl.C.area+"`)","إجمالي المساحة","Total Area")
			,maxLand("max(`"+DataTbl.C.area+"`)","أكبر مساحة","Largest Area")
			,minLand("min(`"+DataTbl.C.area+"`)","أصغر مساحة","Smallest Area");
			
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
			
			public String sql;protected String ar,en;
			Statistics(String p,String l,String e){sql=p;ar=l;en=e;}
			public Map lang(Map m){m.put("ar",ar);m.put("en",en );return m;}

			public static Map def(){Map a=Util.mapCreate();for(Statistics p:sttstcs)a.put(p.name(),p.m(null));return a;}
			public Map m(Map m){if(m==null)m=Util.mapCreate();return Util.mapSet(m,"name",name(),"ar",ar,"en",en,"sql",sql);}
		}//enum S//sttstcs
	}//enum Lbl

	@HttpMethod(usrLoginNeeded = false,useClassName = false) public static Map get(
		@HttpMethod(prmName = "from") Integer from,
		@HttpMethod(prmName = "to") Integer to,
		@HttpMethod(prmName = "typ") Integer pTyp,
		@HttpMethod(prmName = "gov") Integer pGov,
		@HttpMethod(prmName = "contract") Lbl.Contrct contrct,
		@HttpMethod(prmName = "term") Lbl.Term term,
		@HttpMethod(prmName = "sttstcs") Object ss,
		@HttpMethod(prmName = "showDefs")Boolean showDefs,
		//@HttpMethod(prmName = "op") String op,
		//@HttpMethod(prmName = "lang") Lang lang,
		//@HttpMethod(prmName = "reversedXAxis")boolean reversedXAxis,
		TL tl)//GenericServlet srvlt
		throws Exception
	{Map m=null;
		Lbl.Statistics[]sttstcs=ss instanceof Lbl.Statistics[]
			?(Lbl.Statistics[])ss:Lbl.Statistics.parse(ss); 
		if(sttstcs==null)
			return m;
		try
		{Map<LookupTbl.Col,Map<Integer,Map<Lang,LookupTbl>>>
				lookup=LookupTbl.lookup();
			int[]minmaxYear=DataTbl.minmaxYear();
			if(showDefs==null)showDefs=false;
			m=Util.mapCreate("defs",!showDefs?showDefs:Util.mapCreate(
			"Lang",Lang.values()
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
					 ,"namesGovs",namesGovs(tl)
					 ,"minmaxYear",minmaxYear
					 ,"lookup",lookup
				,"term",term,"showDefs",showDefs,"contrct",contrct,"sttstcs",sttstcs
			);
			//boolean devuser=tl.h.var("devuser",false);
			String op=null;
			if("resetLookup".equals( op )) {
				tl.h.a( LookupTbl.class, null );
				//tl.out("application-scope has been reset for entry 'lookup' hashmap, tl.h.a( LookupTbl.class, null ); ");
			}

		//Map<Integer,Map<Lang,LookupTbl>>govs=lookup.get(LookupTbl.Col.gov);//typs=lookup.get(LookupTbl.Col.type),
		//if(govs==null)govs=new HashMap<Integer,Map<Lang,LookupTbl>>();
			//if(typs==null)typs=new HashMap<Integer,Map<Lang,LookupTbl>>();
			//LookupTbl gov=govs.get(pGov).get(Lang.en);// replace iGov to gov//	, typ=typs.get(Util.parseInt(Prm.typ.v(p),0)).get(Lang.en)

			if(from>to)
			{int tmp=from;
				from=to;
				to=tmp;
			}Util.mapSet(m,"from",from,"to",to);

			boolean allGovs=pGov==null||pGov==0//gov.code==0//"0".equals()
				//,aggGovs="a".equals(Prm.gov.v(p))
				,allTyps=pTyp==null||0==pTyp;

			LookupTbl.Col col_Gov_or_name=allGovs?LookupTbl.Col.gov:LookupTbl.Col.name;

			if(term==null)//Lbl.Term term=Lbl.Term.annual;if(Prm.terms.v(p)!=null) try{term=Lbl.Term.valueOf(Prm.terms.v(p));}catch(Exception ex){tl.error(ex,"parse term");}
				term= Lbl.Term.annual;

			if(contrct==null)//Lbl.Contrct contrct=Prm.contract.v(p)==null?Lbl.Contrct.all:Lbl.Contrct.valueOf(Prm.contract.v(p));
				contrct=Lbl.Contrct.all;

			Util.mapSet(m,"allGovs",allGovs,"allTyps",allTyps,"col_Gov_or_name",col_Gov_or_name);

			if(sttstcs.length>0)
			try
			{	StringBuilder sql=new StringBuilder("select ")
					.append(allGovs?DataTbl.C.gov:DataTbl.C.name)
					.append( ",").append(term.sql).append(" as t");//if(term!=Lbl.Term.aggregate)

				for(Lbl.Statistics x:sttstcs)
					sql.append(",").append(x.sql);

				sql.append("from ").append(DataTbl.dbtName)
					.append(" where `y`>=? and `y`<=? ");
				if(contrct!=Lbl.Contrct.all)
					sql.append("and `").append(DataTbl.C.contract).append("`=? ");
				if(!allGovs)
					sql.append("and `").append(DataTbl.C.gov).append("`=? ");
				if(term==Lbl.Term.nineMonths)
					sql.append("and month(`"+DataTbl.C.d+"`)<=9");
				sql	.append(" group by ").append(col_Gov_or_name)
					.append(",t");//if(term!=Lbl.Term.aggregate)
				//sql.append(" order by ").append(col_Gov_or_name);if(term!=Lbl.Term.aggregate)sql.append(",t");

				String s=sql.toString();Util.mapSet(m,"sql",s);

				PreparedStatement ps=Sql.p(s);// System.out.println(packageName+"/2012/03/05/"+jspName+":ps="+ps);
				{int i=1;ps.setObject(i++,from);//p[Prm.from	.ordinal()]
					ps.setObject(i++,to);//p[Prm.to				.ordinal()]
					if(contrct!=Lbl.Contrct.all)
						ps.setObject(i++,contrct.v);
					if(!allGovs)	ps.setObject(i++,pGov);
					if(!allTyps)	ps.setObject(i++,pTyp);
				}

				Map mn=null,ms=null,data=new HashMap();
				Util.mapSet(m,"data",data);//row=0;//reset(tbl);
				tl.log(packageName,"ps:",ps);
				ResultSet rs=ps.executeQuery();
				int c,nm,row=0;String yr;
				while(rs.next())
				{c=1;row++;nm=rs.getInt(c++);
					yr=rs.getString(c++);
					tl.log("row",row);
					mn=(Map)data.get(nm);
					if(mn==null)
						data.put(nm,mn=new HashMap());
					ms=(Map)mn.get(yr);
					if(ms==null)
						mn.put(yr,ms=new HashMap());
					for(Lbl.Statistics x:sttstcs)try{
						ms.put(x,rs.getDouble(c++));
					}catch(Exception ex){
						tl.error(ex,packageName,"get:errI:Statistic=",x,",theBigMap=",m,",nm=",nm,",yr=",yr,",c=",c,",row=",row);
						}
					tl.log("row=",row,":ms=",ms);
				}
				tl.log("ResultSet done:no.rows=",row,",theBigMap=",m);
			}//if(nullCol<0)
			catch(Throwable x){
				String end=tl.logo(jspName+":if(nullCol<0) Throwable:",x);
				tl.error(x,end);
			}//catch
			tl.log(packageName,"get:return:",m);
		}finally{TL.Exit();}
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
				String sql="select min(year("+C.d+
					           ")) , max(year("+C.d+")) from "+dbtName;
				tl.log("App.DataTbl.minmaxYear:sql=",sql);
				Object[ ]a=Sql.q1row(sql);// group by year(`"+C.d+"`)");
				if(a!=null){int[]x={((Number)a[0]).intValue()
					,((Number)a[1]).intValue()};r=x;}
				h.a(str,r);}catch(Exception x)
			{tl.error(x,"App.DataTbl.minmaxYear:");
				if(r==null){int[]a={2000,2015};r=a;}
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
			) ENGINE=MyISAM AUTO_INCREMENT=305 DEFAULT CHARSET=utf8;
			 */
		}//C


			@Override public  CI[]columns(){return C.values();}
			@Override public Object[]wherePK(){Object[]a={C.no,no};return a;}
			@Override public List creationDBTIndices(TL tl){return null;}

			public interface ILang{String lang();public Map lang(Map m);}

			public static Map<Col,Map<Integer,Map<Lang,LookupTbl>>>lookup(){
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

	//	public Json.Output jsonOutput( Json.Output o, String ind, String path ) throws IOException {}

		public static Map<String,Map<Integer,Map<String,Map>>> sLookup(){
			TL p=TL.tl();TL.H h=p.h;
			Object o=h.a(packageName+LookupTbl.class);
			Map<String,Map<Integer,Map<String,Map>>>m=o==null?null:(
				Map<String,Map<Integer,Map<String,Map>>>)o;
			if(m==null)try{LookupTbl l=new LookupTbl();
				h.a(packageName+LookupTbl.class,m=new HashMap
					<String,Map<Integer,Map<String,Map>>>());
				for(Sql.Tbl i:l.query(Sql.Tbl.where())){
					p.log("App.LookupTbl.lookup:1:",i.toJson());
					Map<Integer,Map<String,Map>>n=m.get(l.col.name());
					if(n==null)
						m.put(l.col.name(),n=new HashMap<Integer,Map<String,Map>>());
					Map<String,Map>ln=n.get(l.code);
					if(ln==null)
						n.put(l.code,ln=new HashMap<String,Map>(  ));
					//LookupTbl t=ln.get( l.lang.name() );
					//if(t==null || t.no>l.no )ln.put(l.lang, l.copy());
				}//for
			}//ifm==null
			catch(Exception x){p.error(x,"App.LookupTbl.lookup:ex:");
				if(m==null)
					m=new HashMap<String,Map<Integer,Map<String,Map>>>();
			}return m;}

			public static Map def(){Map a=Util.mapCreate("no","Integer"
				,"col","Col"
				,"code","Integer"
				,"text","String"
				,"lang","Lang"
				,"Col",Col.values()
			);
				return a;}

		/*	public Map m(Map m){if(m==null)m=Util.mapCreate();
				return Util.mapSet(m,"no",no
					,"col",col
					,"code",code
					,"text",text
					,"lang",lang==null?null:lang.name());}*/
		}//class LookupTbl

%><%@ include file="realestate1805.jsp"%><%!
}//class Realestate201805
%>
