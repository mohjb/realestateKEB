package dev201803;

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
 * Created by Vaio-PC on 5/29/2018.
 */
public class Realestate201805 {

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
		@HttpMethod(prmName = "sttstcs") Lbl.Statistics[] sttstcs,
		@HttpMethod(prmName = "showDefs")Boolean showDefs,
		//@HttpMethod(prmName = "op") String op,
		//@HttpMethod(prmName = "lang") Lang lang,
		//@HttpMethod(prmName = "reversedXAxis")boolean reversedXAxis,
		TL tl)//GenericServlet srvlt
		throws Exception
	{Map m=null;
		if(sttstcs==null)
			return m;
		try {
			Map< LookupTbl.Col, Map< Integer, Map< Lang, LookupTbl > > >
					lookup = LookupTbl.lookup();
			int[] minmaxYear = DataTbl.minmaxYear();
			if ( showDefs == null ) showDefs = false;
			m = Util.mapCreate( "defs", !showDefs ? showDefs : Util.mapCreate(
				"Lang", Lang.values()
				, "Lbl", Util.mapCreate(
					"Ranks", Lbl.Ranks.def()
					, "Contrct", Lbl.Contrct.def()
					, "Term", Lbl.Term.def()
					, "Statistics", Lbl.Statistics.def()
				)
				, "sttstcs", "[<str:StatisticsName>,,, order and length as required by client]"
				, "DataTbl", DataTbl.def()
				, "LookupTbl", LookupTbl.def()
				, "lookup", "Map<Col,Map<Integer:code,Map<Lang,LookupTbl>>>"
				, "data", "Map<int:place,Map<str:term ,Map<str:sttstc ,Double>>>"
				)//defs
				, "namesGovs", namesGovs( tl )
				, "minmaxYear", minmaxYear
				, "lookup", lookup
				, "term", term, "showDefs", showDefs, "contrct", contrct, "sttstcs", sttstcs
			);
			//boolean devuser=tl.h.var("devuser",false);
			String op = null;
			if ( "resetLookup".equals( op ) ) {
				tl.h.a( LookupTbl.class, null );
			}

			if ( from == null ) from = minmaxYear[ 1 ];
			if ( to == null ) to = minmaxYear[ 1 ];
			if ( from > to ) {
				int tmp = from;
				from = to;
				to = tmp;
			}
			Util.mapSet( m, "from", from, "to", to );

			boolean allGovs = pGov == null || pGov == 0//gov.code==0//"0".equals()
					//,aggGovs="a".equals(Prm.gov.v(p))
				, allTyps = pTyp == null || 0 == pTyp;

			LookupTbl.Col col_Gov_or_name = allGovs ? LookupTbl.Col.gov : LookupTbl.Col.name;

			if ( term == null )//Lbl.Term term=Lbl.Term.annual;if(Prm.terms.v(p)!=null) try{term=Lbl.Term.valueOf(Prm.terms.v(p));}catch(Exception ex){tl.error(ex,"parse term");}
				term = Lbl.Term.annual;

			if ( contrct == null )//Lbl.Contrct contrct=Prm.contract.v(p)==null?Lbl.Contrct.all:Lbl.Contrct.valueOf(Prm.contract.v(p));
				contrct = Lbl.Contrct.all;

			Util.mapSet( m, "allGovs", allGovs, "allTyps", allTyps, "col_Gov_or_name", col_Gov_or_name );

			if ( sttstcs.length > 0 ) {
				StringBuilder sql = new StringBuilder( "select " )
					.append( allGovs ? DataTbl.C.gov : DataTbl.C.name )
					.append( "," ).append( term.sql ).append( " as t" );//if(term!=Lbl.Term.aggregate)

				for ( Lbl.Statistics x : sttstcs )
					sql.append( "," ).append( x.sql );

				sql.append( "from " ).append( DataTbl.dbtName )
						.append( " where `y`>=? and `y`<=? " );
				if ( contrct != Lbl.Contrct.all )
					sql.append( "and `" ).append( DataTbl.C.contract ).append( "`=? " );
				if ( !allGovs )
					sql.append( "and `" ).append( DataTbl.C.gov ).append( "`=? " );
				if ( term == Lbl.Term.nineMonths )
					sql.append( "and month(`" + DataTbl.C.d + "`)<=9" );
				sql.append( " group by " ).append( col_Gov_or_name )
						.append( ",t" );//if(term!=Lbl.Term.aggregate)
				//sql.append(" order by ").append(col_Gov_or_name);if(term!=Lbl.Term.aggregate)sql.append(",t");

				String s = sql.toString();
				Util.mapSet( m, "sql", s );

				PreparedStatement ps = Sql.p( s );// System.out.println(packageName+"/2012/03/05/"+jspName+":ps="+ps);
				int i = 1;
				ps.setObject( i++, from );//p[Prm.from	.ordinal()]
				ps.setObject( i++, to );//p[Prm.to				.ordinal()]
				if ( contrct != Lbl.Contrct.all )
					ps.setObject( i++, contrct.v );
				if ( !allGovs ) ps.setObject( i++, pGov );
				if ( !allTyps ) ps.setObject( i++, pTyp );

				int row = 0;
				Map mn = null, ms = null, data = new HashMap();
				Util.mapSet( m, "data", data );//row=0;//reset(tbl);
				tl.log( packageName, "ps:", ps );
				ResultSet rs = ps.executeQuery();
				while ( rs.next() ) {
					int c = 1, nm = rs.getInt( c++ );
					row++;
					String yr = rs.getString( c++ );
					mn = ( Map ) data.get( nm );
					if ( mn == null )
						data.put( nm, mn = new HashMap() );
					ms = ( Map ) mn.get( yr );
					if ( ms == null )
						mn.put( yr, ms = new HashMap() );
					for ( Lbl.Statistics x : sttstcs )
						try {
							ms.put( x, rs.getDouble( c++ ) );
						} catch ( Exception ex ) {
							tl.error( ex, packageName, "get:errI:Statistic=", x, ",theBigMap=", m, ",nm=", nm, ",yr=", yr, ",c=", c, ",row=", row );
						}
				}
			}//if(sttstcs.length > 0)
		}
			catch(Throwable x){
				String end=tl.logo(jspName+":if(nullCol<0) Throwable:",x);
				tl.error(x,end);
			}//catch
		return m;
	}//service "get" HttpMethod


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


		/**
		 * Created by Vaio-PC on 2/23/2018.
		 * Created by Vaio-PC on 1/26/2018.
		 * Created by Vaio-PC on 18/01/2018.
		 * Created by moh on 14/7/17.
		 */

		static final String SrvltName = packageName + ".jsp", UrlPrefix = ""; //packageName = "report20180526"

		static Map<String, Method> mth = new HashMap<String, Method>();


		static void staticInit() {
			registerMethods(Realestate201805.class);
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
			//boolean prmUrlRemaining() default false;
			boolean prmLoadByUrl() default false;
			boolean prmBody() default false;
		}//HttpMethod

		//@Override
		public static void service(HttpServletRequest request, HttpServletResponse response) {
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
					int n = prmsAnno == null ? 0 : prmsAnno.length, i = - 1;tl.h.urli=-1;
					Object[] args = new Object[n];

					for(Annotation[] t : prmsAnno) try {
						HttpMethod pp = t.length > 0 && t[0] instanceof HttpMethod ? (HttpMethod) t[0] : null;
						Class prmClss = prmTypes[++ i];
						String nm = pp != null ? pp.prmName() : "arg" + i;//t.getName();
						Object o = null;
						if(pp != null && pp.prmUrlPart())
							args[i]=tl.h.url[tl.h.urli++];
						else if(pp != null && pp.prmLoadByUrl()) {
							Class[] ca = {TL.class , String[].class};
							Method//m=cl.getMethod( "prmLoadByUrl", ca );if(m==null)
								m = prmClss.getMethod("prmLoadByUrl", ca);
							args[i] = m == null ? null : m.invoke(prmClss, tl,tl.h.url);
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
							args[i] = prmClss==String.class//prmClss.isAssignableFrom(String.class)
								? String.valueOf(tl.bodyTxt!=null?tl.bodyTxt:tl.bodyData)
								:prmClss==List.class?(tl.bodyData instanceof List
									?tl.bodyData:Util.lst(tl.bodyData))
								: tl.bodyData!=null?tl.bodyData:tl.bodyTxt;
						else
							args[i] = o = TL.class.equals(prmClss) ? tl
							: tl.h.req(nm, prmClss);
					} catch(Exception ex) {
						tl.error(ex, SrvltName, ".service:arg:i=", i);
					}if(tl.h.logOut)tl.logo(SrvltName, ".service:args:",n,args,op,cl);
					retVal = n == 0 ? op.invoke(cl)
						: n == 1 ? op.invoke(cl, args[0])
						: n == 2 ? op.invoke(cl, args[0], args[1])
						: n == 3 ? op.invoke(cl, args[0], args[1], args[2])
						: n == 4 ? op.invoke(cl, args[0], args[1], args[2], args[3])
						: n == 5 ? op.invoke(cl, args[0], args[1], args[2], args[3], args[4])
						: n == 6 ? op.invoke(cl, args[0], args[1], args[2], args[3], args[4], args[5])
						: n == 7 ? op.invoke(cl, args[0], args[1]
							, args[2], args[3], args[4], args[5], args[6])
						: n == 8 ? op.invoke(cl, args[0], args[1], args[2]
							, args[3], args[4], args[5], args[6], args[7])
						: n == 9 ? op.invoke(cl, args[0], args[1], args[2]
							, args[3], args[4], args[5], args[6], args[7], args[8])
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
			public static final String TlName=packageName+".TL";
			public TL(HttpServletRequest r,HttpServletResponse n,Writer o){h.req=r;h.rspns=n;out=new Json.Output(o);}

			public H h=new H();
			public Map<String,Object> /**accessing request in json-format*/json;
	public Object bodyData;public StringBuilder bodyTxt;
			public Date now;
			Map usr;//M
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
				TL p;//if(mth==null || mth.size()==0)Srvlt.staticInit();
				tl.set(p=new TL(r,response,response.getWriter()));
				p.onEnter();
				return p;}
			private void onEnter()throws IOException {
				h.ip=h.getRequest().getRemoteAddr();
				now=new Date();//seqObj=seqProp=now.getTime();
				try{Object o=h.req.getContentType();
					o=bodyData=o==null?null
						:o.toString().contains("json")?Json.Prsr.parse(h.req,bodyTxt=new StringBuilder())
						:o.toString().contains("part")?h.getMultiParts():null;
					json=o instanceof Map<?, ?>?(Map<String, Object>)o:null;//req.getParameterMap() ;
					h.logOut=h.var("logOut",h.logOut);
					if(h.getSession().isNew())
						Sql.Tbl.check(this);//Srvlt.Domain.loadDomain0();
					usr=(Map)h.s("usr");//M
					h.url=h.req.getRequestURI().substring( UrlPrefix.length() ).split("/");
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
				public boolean logOut=false;public int urli;
				public String ip,comments[]=CommentJson,url[];
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
						if(c.isArray()&&c.getComponentType().isEnum())
							o=Util.toEnumArray( c,o );
						else{
						String s=o instanceof String?(String)o:o.toString();
						o=Util.parse(s,c);}}
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
				,dbName("realestate","js4d00_realestate")
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
				else if(c.isArray()&&c.getComponentType().isEnum())
					return toEnumArray( c,s );
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

			public static Object[]toEnumArray(Class c,Object o){
				if(o==null)return null;
				Class ic=c.getComponentType();
				Object[]a=ic.getEnumConstants();
				List l=new LinkedList();
				boolean bl=o instanceof List,ba=o instanceof Object[];
				if(bl || ba){
					List pl=bl?(List)o:null;
					Object[]pa=ba?(Object[])o:null;
					int n=bl?pl.size():pa.length;
					for(int i = 0 ; i < n ; i++) {
						for(Object x:a) {
							Object z=bl?pl.get(i):pa[i];
							if(z.toString().indexOf(x.toString())!=-1)
							{l.add( x );break;}
					}}
				}
				else {
					String s=o.toString().toLowerCase();
					List<Integer> i=new LinkedList<>();
					for(Object x:a) {
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
					}
				}
				if(l.size()==0)
					return null;
				Object[]r=(Object[])java.lang.reflect.Array.newInstance(ic,l.size());
				int d=0;for(Object x:l)r[d++]=x;
				return r;
			}
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
				public interface CI{
					public Field f();
					public String getName();
					public Class getType();}//interface I

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
				public Tbl load(){return loadWhere(wherePK());}

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
			{ public interface JsonOutput{
				public Json.Output jsonOutput( Json.Output o, String ind, String path ) throws IOException ;}

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
					else if(a.getClass().isEnum())oStr(a.toString(),ind);
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

				public StringBuilder body,buff=new StringBuilder() ,lookahead=new StringBuilder();
				public Reader rdr;

				public String comments=null;
				public char c;Map<String,Object>cache=null;

				enum Literal{Undefined,Null};//,False,True

				public static Object parse(String p)throws Exception{
					return parse(new java.io.StringReader(p));}

				public static Object parse(HttpServletRequest p,StringBuilder bodyTxt)throws Exception{
			return parse(p.getReader(),bodyTxt);}

		public static Object parse(Reader p)throws Exception{return parse(p,null);}
		public static Object parse(Reader p,StringBuilder bodyTxt)throws Exception{
			if(p==null)return null;
			Prsr j=new Prsr();j.body=bodyTxt;j.rdr=p;j.nxt(j.c=j.read());
			return j.parse();}

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
			int h=-1;try{h=rdr.read();if(body!=null&&h!=-1)body.append((char)h);}
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
						}while( (b=(c==h ||Character.toUpperCase(c)
							                   ==Character.toUpperCase(h))
						)&& (++i)<pn );
					return b;}

				public boolean lookahead(String p){return lookahead(p,0);}

			}//Prsr
		}//class Json
//%>

public static void main(String[]args){
	dev201801.Dbg.Srvlt s=dev201801.Dbg.Srvlt.sttc;
	s.pc=new dev201801.Dbg.PC();
	s.pc.a=dev201801.Dbg.SrvltContxt.sttc();
	s.pc.q.ssn=new dev201801.Dbg.Ssn();
	String[]p={"get","?sttstcs=count amount&","{showDefs=true,sttstcs='avg count amount'}"};
	s.pc.q.init(p);

	Realestate201805.service( s.pc.q,s.pc.p );

}//main
}//class Realestate201805
