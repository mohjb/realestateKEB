<%@page 
import="java.io.
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
%><%! //<?

public class S extends HttpServlet {

	final static String packageName="/realestateKeb/2018/",jspName="realestate18.jsp";

	//static void reset(Double[][]a){for(int i=0;i<a.length;i++)for(int j=0;j<a[i].length;j++)a[i][j]=null;}

	static String filterNum(String p)
	{if(p==null)return p;String r=p.replaceAll(",","");
		if(r.endsWith(".0"))r=r.substring(0,r.length()-2);return r;}

	
	static Object jsonTable(Double[][]a
		,String ttl
		,String trAttribs,int yr,Lbl.Term term,Model mod
		,int[]pStatistics
		,boolean reversedXAxis
	)throws Exception
	{final int termBase=term.base, Ix=0;
		Model.Chrt c=mod.newChrt(ttl);
		c.var=c.newVar(ttl);
		if(reversedXAxis)
			for(int j=a[Ix].length-1;j>=0;j--)
			{String s1=filterNum(a[Ix][j]);
				double d=Double.NaN;
				try{d=Double.parseDouble(s1);}catch(Exception x){}
				v.var.series.add(d);
			}
		else for(int j=0;j<a[Ix].length;j++)
		{String s1=filterNum(a[Ix][j]);
			double d=Double.NaN;
			try{d=Double.parseDouble(s1);}catch(Exception x){}
			v.var.series.add(d);
		}
		TL.out("\"/></th>");
		for(int i=0;i<a.length;i++) {
			if (i > 0)
				TL.out("\n<tr ", trAttribs, ">");
			String lbl = Lbl.sttstcs[pStatistics[i + 1]].lang();
			TL.out("<th>", lbl, "</th>");
			for (int j = 0; j < a[i].length; j++)
				TL.out(j % termBase == 0 //quart&&j%4==0
							? "<td style=\"background-color:#e0e0f0\""
							: "<td"
						, " title=\"", ttl, ".\n", lbl, ".\n"
						, term == Lbl.Term.annual ? String.valueOf(yr + j)
							: term == Lbl.Term.semiAnnual || term == Lbl.Term.quarterly
							? (String.valueOf(yr + j / termBase) + ".\n" + term.ar + " " + Lbl.ranks[j % termBase])
							: (String.valueOf(yr + j / termBase) + ".\n" + term.ar + " " + ((j % termBase) + 1))
						, "\">"
						, (a[i][j] == null || a[i][j].trim().length() < 1
							? "&nbsp;"
							: a[i][j]
						), "</td>\n"
				);
			//TL.out("</tr>");
		}return m;
	}//jsonTable()

	static Map<Integer,Integer>namesGovs(TL tl)throws java.io.IOException,java.sql.SQLException
	{final String nmg=packageName+"lookup.namesGovs";
		Map<Integer,Integer>a=(Map<Integer,Integer>)tl.h.a(nmg);
		if(a==null)
		{Object[][]o=TL.DB.q("select distinct `"
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
				//if(n>=0 && n<o.length)
					a.put(n,gov);//a[n]=gov;
			}
		}
		return a;
	}

 /**these are html-post-parameter names, so that the names are centeralized/have one spelling*/
		enum Prm{from,to//,from2,to2
			,typ,contract,gov,terms;//S;
			String v(String[]a){return a[ordinal()];}
			public static List def(){Prm[]x=values();List a=new LinkedList(x.length);for(Prm p:x)a.add(p.name());return a;}
			}


		enum Lbl{;

			enum Ranks implements LookupTbl.ILang{r1("الأول","1st"),
				r2("الثاني","2nd"),
				r3("الثالث","3rd"),
				r4("الرابع","4th");protected String en,ar;Ranks(String a,String e){ar=a;en=e;}
				//public String lang(){return TL.tl().lang==Lang.ar?name():en;}
				public Map lang(Map m){m.put("ar",ar);m.put("en",en );return m;}
				public static Map def(){Map a=Util.mapCreate();for(Ranks p:ranks)a.put(p.name(),m(null));return a;}
				public Map m(Map m){if(m==null)m=Util.mapCreate();return Util.mapSet(m,"name",name(),"ar",ar,"en",en);}
			}

			enum Contrct implements LookupTbl.ILang {//Null
				all(0,"إجمالي العقود","Total"),
				c1(1,"عقود مسجلة","Registered"),
				c2(2,"وكالات عقارية","Agent")
				;
				public static Map def(){Map a=Util.mapCreate();for(Contrct p:contrcts)a.put(p.name(),m(null));return a;}
				public Map m(Map m){if(m==null)m=Util.mapCreate();return Util.mapSet(m,"name",name(),"ar",ar,"en",en,"v",v);}

				protected String ar,en;public int v;
				//Contrct(){str=name();}
				Contrct(int i,String p,String e){ar=p;en=e;v=i;}
				//public String lang(){return TL.tl().lang==Lang.ar?ar:en;}
				public Map lang(Map m){m.put("ar",ar);m.put("en",en );return m;}
			};//Contrct

			public static Term[]terms=Term.values();
			public static Ranks[]ranks=Ranks.values();
			public static Contrct[]contrcts=Contrct.values();
			public static Statistics[]sttstcs=Statistics.values();

		enum Term implements LookupTbl.ILang{
			aggregate(1,"إجمالي","إجمالي الفترة" ,"Aggregate","aggregate","1")
			, annual(1,"سنة","سنوي" ,"Annual","Year","`y`")
			, nineMonths(1,"9شهور","9شهور" ,"Nine Months","Nine Months","`y`")//	=9months
			, semiAnnual(2,"النصف","نصف سنوي" ,"Semi-Annual","Half","concat(`y`,'h',ceil(`m`/6))")
			, quarterly(4,"الربع","ربع سنوي" ,"Quarterly","Quarter","concat(`y`,'q',ceil(`m`/3))")
			, monthly(12,"شهر","شهري" ,"Monthly","Month","concat(`y`,'m',`m`)")
			, weekly(52,"اسبوع","اسبوعي" ,"Weekly","Week","concat(`y`,'w',`w`)");
			public int base;protected String ar,lbl,en,enLbl,sql;
			Term(int b,String a,String lbl,String e,String el,String s){base=b;ar=a;this.lbl=lbl;en=e;enLbl=el;sql=s;}
			//public String lang(){return TL.tl().lang==Lang.ar?ar:en;}
			//public String lbl(){return TL.tl().lang==Lang.ar?lbl:enLbl;}
			public Map lang(Map m){m.put("ar",ar);m.put("en",en );return m;}
			public Map lbl(Map m){m.put("arLbl",lbl);m.put("enLbl",enLbl );return m;}
			public static Map def(){Map a=Util.mapCreate();for(Term p:terms)a.put(p.name(),m(null));return a;}
			public Map m(Map m){if(m==null)m=Util.mapCreate();return Util.mapSet(m,"name",name(),"ar",ar,"lbl",lbl,"en",en,"enLbl",enLbl,"base",base,"sql",sql);}
		}//enum Term


		enum Statistics implements LookupTbl.ILang{
		count("count(*)","عدد","Count")
			,amount("sum(`"+DataTbl.C.price+"`)","إجمالي قيمة التداول","Total Price")
			,avgMtr("sum(`"+DataTbl.C.price+"`)/sum(`"+DataTbl.C.area+"`)","متوسط السعر","Average Price")
			,maxMtr("max(`"+DataTbl.C.price+"`/`"+DataTbl.C.area+"`)","أعلى سعر متر","Maximum Price of 1 square meter")
			,minMtr("min(`"+DataTbl.C.price+"`/`"+DataTbl.C.area+"`)","أقل سعر متر","Minimum Price of 1 square meter")
			,avgLand("avg(`"+DataTbl.C.area+"`)","متوسط المساحة","Average Area")
			,SumLand("sum(`"+DataTbl.C.area+"`)","إجمالي المساحة","Total Area")
			,maxLand("max(`"+DataTbl.C.area+"`)","أكبر مساحة","Largest Area")
			,minLand("min(`"+DataTbl.C.area+"`)","أصغر مساحة","Smallest Area");

			public String sql;protected String ar,en;
			Statistics(String p,String l,String e){sql=p;ar=l;en=e;}
			public String lang(){return TL.tl().lang==Lang.ar?ar:en;}
			public Map lang(Map m){m.put("ar",ar);m.put("en",en );return m;}
			
			public static Map def(){Map a=Util.mapCreate();for(Statistics p:sttstcs)a.put(p.name(),m(null));return a;}
			public Map m(Map m){if(m==null)m=Util.mapCreate();return Util.mapSet(m,"name",name(),"ar",ar,"en",en,"sql",sql);}
		}//enum S//sttstcs
        }//enum Lbl

	public static void xservice(GenericServlet srvlt
			,final javax.servlet.http.HttpServletRequest request
			, final javax.servlet.http.HttpServletResponse response)
			throws Exception
	{response.setContentType("text/html; charset=utf-8");
		//PrintWriter out = response.getWriter(); //new PrintWriter(System.out);
		try
		{TL tl=TL.Enter(request,response);//out);//,response.getWriter()
			 tl.logOut=tl.h.var("logOut",true);tl.h.comments=tl.h.CommentHtml;
			if (request.getCharacterEncoding() == null)
				request.setCharacterEncoding("UTF-8");
			
			Map<LookupTbl.Col,Map<Integer,Map<Lang,LookupTbl>>> lookup=LookupTbl.lookup();
			int[]minmaxYear=DataTbl.minmaxYear();
			Map m=Util.mapCreate(
				"tbl",Util.mapCreate(
					"a",String[][]
					,"model",Model
					,"term",Lbl.Term
					,"pStatistics",int[]
					,"reversedXAxis",boolean
					,"ttl",String)
				,"defs",Util.mapCreate(
					"Lang",Lang.def()
					,"Prm",Prm.def()
					,"Lbl",Util.mapCreate(
						"Ranks", Ranks.def()
						,"Contrct", Contrct.def()
						,"Term", Term.def()
						,"Statistics", Statistics.def()
					)
					,"pStatistics","[<int:Statistics.ord>,,, order and length as required by client]"
					,"Model",Model.def()
					,"DataTbl",DataTbl.def()
					,"LookupTbl",LookupTbl.def()
					,"lookup","Map<Col,Map<Integer,Map<Lang,LookupTbl>>>"
					,"a","String[][] //tbl"
				 )//defs
				 ,"namesGovs",namesGovs(tl)//Map<Integer,Integer>,"reversedXAxis","boolean"
					,"minmaxYear",minmaxYear
					,"lookup",lookup
				,"",0,"",0,"",0,"",0,"",0,"",0,"",0,"",0
			);
			String op=tl.h.req("op");
			boolean devuser=tl.h.var("devuser",false);

			Prm[]pa=Prm.values();
			String p[]=new String[pa.length];
			int nullCol=-1;
			for(int i=0;i<p.length;i++)
			{	p[i]=tl.h.req(pa[i].toString());
				//tl.log(jspName,":load Prms(",i,":",pa[i],")=",p[i]);
				if(p[i]==null)nullCol=i;
			}Util.mapSet(m,"p",p);

			if("resetLookup".equals( op )) {
				tl.h.a( LookupTbl.class, null );
				tl.out("application-scope has been reset for entry 'lookup' hashmap, tl.h.a( LookupTbl.class, null ); ");
			}

			Map<Integer,Map<Lang,LookupTbl>>typs=lookup.get(LookupTbl.Col.type)
					,govs=lookup.get(LookupTbl.Col.gov);
			if(govs==null)govs=new HashMap<Integer,Map<Lang,LookupTbl>>();
			if(typs==null)typs=new HashMap<Integer,Map<Lang,LookupTbl>>();
			LookupTbl gov=govs.get(Util.parseInt(Prm.gov.v(p),0)).get(tl.lang)// replace iGov to gov
					, typ=typs.get(Util.parseInt(Prm.typ.v(p),0)).get(tl.lang);

			int from=Util.parseInt(Prm.from.v(p),minmaxYear[0])
					,to=Util.parseInt(Prm.to.v(p),minmaxYear[1])
					//,from2=Util.parseInt(Prm.from2.v(p),1)
					;//,to2=Util.parseInt(Prm.to2.v(p),52);

			if(from>to)
			{int tmp=from;
				from=to;
				to=tmp;
				String temp=p[0];
				p[0]=p[1];
				p[1]=temp;
			}Util.mapSet(m,"from",from,"to",to);

			boolean allGovs=gov.code==0//"0".equals()
					//,aggGovs="a".equals(Prm.gov.v(p))
					,allTyps="0".equals(Prm.typ.v(p));

			LookupTbl.Col col_Gov_or_name=allGovs?LookupTbl.Col.gov:LookupTbl.Col.name;
			
			Lbl.Term term=Lbl.Term.annual;
			if(Prm.terms.v(p)!=null)
				try{term=Lbl.Term.valueOf(Prm.terms.v(p));
				}catch(Exception ex){tl.error("parse term",ex);}


			boolean reversedXAxis=tl.h.var(packageName+"conf.reversedXAxis",false);
			
			/**edit heading parameters / user-inputs*/boolean stateEdit=tl.h.var("stateEdit",true);//jspName+".stateEdit"
			Lbl.Contrct contrct=Prm.contract.v(p)==null?Lbl.Contrct.all:Lbl.Contrct.valueOf(Prm.contract.v(p));
			

			int[]pStatistics=(int[])tl.h.s(jspName+".pStatistics");
			if(pStatistics==null)
			{int n=Lbl.sttstcs.length+1;
				tl.h.s(jspName+".pStatistics",pStatistics=new int[n]);
				pStatistics[0]=1;
				for(int i=1;i<n;i++)pStatistics[i]=i-1;
			}

			Util.mapSet(m,"allGovs",allGovs,"allTyps",allTyps,"col_Gov_or_name",col_Gov_or_name
			,"term",term,"reversedXAxis",reversedXAxis,"contrct",contrct,"pStatistics",pStatistics);
Model mod=new Model();
			//if(!stateEdit)
			{	if(nullCol<0)try
				{//	tl.h.s(jspName+Model.class,mod);
					mod.termBase=reversedXAxis?to:from;
					mod.termInc=(reversedXAxis?-1:1)*term.base;
					//Util.mapSet(m,"model",mod);//Model.Chrt c0=mod.newChrt(col_Gov_or_name.name());//lookup.get(LookupTbl.Col.gov).get(allGovs?0:iGov).text

					//TL.logo(jspName,"nullCol<0");
					StringBuilder sql=new StringBuilder("select ")
						.append(allGovs?DataTbl.C.gov:DataTbl.C.name)
						.append( ",").append(term.sql).append(" as t");//if(term!=Lbl.Term.aggregate)

					for(int i=1;i<=pStatistics[0];i++)
						sql.append(",").append(Lbl.sttstcs[pStatistics[i]].sql);

					sql.append("from ").append(DataTbl.dbtName)
							.append(" where year(`"  ).append(DataTbl.C.d)
							.append("`)>=? and year(`").append(DataTbl.C.d).append("`)<=? ");
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

					PreparedStatement ps=TL.DB.p(s);// System.out.println(packageName+"/2012/03/05/"+jspName+":ps="+ps);
					{int i=1;ps.setObject(i++,from);//p[Prm.from	.ordinal()]
						ps.setObject(i++,to);//p[Prm.to				.ordinal()]
						if(contrct!=Lbl.Contrct.all)
							ps.setObject(i++,p[Prm.contract	.ordinal()]);
						if(!allGovs)	ps.setObject(i++,p[Prm.gov	.ordinal()]);
						if(!allTyps)	ps.setObject(i++,p[Prm.typ	.ordinal()]);
					}
					//int currentName=-1;//,row=term==Lbl.Term.aggregate ? 1:(to-from+1)*term.base;//boolean hasData=false;
					Map mn=null,ms=null,data=new HashMap();//Double tbl[][]=new Double[pStatistics[0]][row];//String tbl[][]=new String[pStatistics[0]][currentName];//for(int i=0;i<tbl.length;i++)tbl[i]=new String[currentName];
					Util.mapSet(m,"data",data);//row=0;//reset(tbl);
					ResultSet rs=ps.executeQuery();
					while(rs.next())
					{int nm=rs.getInt(1);String yr=rs.getString(2);
						mn=data.get(nm);
						if(mn==null)
							data.put(nm,mn=new HashMap());
						
						//if(nm!=currentName){//jsonTable(tbl,lookup.get(col_Gov_or_name).get(currentName).get( tl.lang ).text,"style=\"background-color:"+((row%2)==0?"#eef":"#f8f8ff")+"\"",from,term,mod,pStatistics,reversedXAxis);//tbl[][]=new Double[pStatistics[0]][tbl[0].length];//reset(tbl);currentName=nm;}
						for(int c=0;c<pStatistics[0]//sttscs.length
								;c++)try{tbl[c][yr]=rs.getString(c+3);}catch(Exception ex){ex.printStackTrace();}

					}

				}//if(nullCol<0)
				catch(Throwable x){
					String end=tl.logo(jspName+":if(nullCol<0) Throwable:",x);
					tl.error(end);
					tl.out("<script>serverErrorTL="
					,end
					,"</script>");
				}//catch
			}//if(!stateEdit)
			tl.out("</body><script>");
			String end=tl.logo(tl);
			tl.out("\r\n"
			,"serverTL="
			,end
			,"  \r\n"
			,"</script>\r\n"
			,"</html>");
			Util.mapSet(m,"model",mod.m());
		}finally{TL.Exit();}
		return m;
	}//service


public enum Lang{ar,en;public static List def(){List l=Util.lst();Lang[]a=values();for(Lang x:a)l.add(x.name());return l;}}  

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
            public Class<? extends Tbl>cls(){return DataTbl.class;}
            public Class<? extends TL.Form>clss(){return cls();}
            public Field f(){return Cols.f(name(), cls());}
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
                Object[ ]a=TL.DB.q1row(sql);// group by year(`"+C.d+"`)");
                if(a!=null){int[]x={((Number)a[0]).intValue()
                        ,((Number)a[1]).intValue()};r=x;}
                h.a(str,r);}catch(Exception x)
            {tl.error("App.DataTbl.minmaxYear:", x);
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
    public static class LookupTbl extends Tbl {//implements LookupTbl.ILang
        public static final String dbtName="t";
        @Override public String getName(){return dbtName;}//public LookupTbl(){super(dbtName);}

        @F public Integer no;
        @F public Col col;
        @F public Integer code;
        @F public String text;
        @F public Lang lang;

        LookupTbl copy(){return new LookupTbl().set(no, col, code, text,lang);}

        LookupTbl set(Integer n,Col c,Integer d,String x,Lang lng){no=n;col=c;code=d;text=x;lang=lng;return this;}

        public enum Col{gov,type,name,label,sector;public static List def(){Col[]a=values();List l=new LinkedList(l.length);for(Col c:a)l.add(c.name());return l;};
        public enum C implements CI{no,col,code,text,lang;
            public Class<? extends Tbl>cls(){return LookupTbl.class;}
            public Class<? extends TL.Form>clss(){return cls();}
            public Field f(){return Cols.f(name(), cls());}
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

        public static Map<Col,Map<Integer,Map<Lang,LookupTbl>>> lookup(){
            TL p=TL.tl();TL.H h=p.h;Object o=h.a(LookupTbl.class);
            Map<Col,Map<Integer,Map<Lang,LookupTbl>>>m=o==null?null:(
                    Map<Col,Map<Integer,Map<Lang,LookupTbl>>>)o;
            if(m==null)try{LookupTbl l=new LookupTbl();
                h.a(LookupTbl.class,m=new HashMap<Col,Map<Integer,Map<Lang,LookupTbl>>>());
                for(TL.DB.Tbl i:l.query(TL.DB.Tbl.where())){
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
            catch(Exception x){p.error("App.LookupTbl.lookup:ex:", x);
                if(m==null)
                    m=new HashMap<Col,Map<Integer,Map<Lang,LookupTbl>>>();
            }return m;}

	public static Map def(){Map a=Util.mapCreate("no","Integer"
			,"col","Col"
			,"code","Integer"
			,"text","String"
			,"lang","Lang"
			,"Col",Col.def()
			);
			return a;}

		public Map m(Map m){if(m==null)m=Util.mapCreate();
			return Util.mapSet(m,"no",no
			,"col",col
			,"code",code
			,"text",text
			,"lang",lang==null?null:lang.name());}
 }//class LookupTbl




    /**list of charts for one user paramaters-set
	//TODO: cancel:the first chart has vars of the first vars of every other chart
	*/
    public static class Model{//public class Chart

    //public final static String prefix=packageName+".conf.chart.";    final static String  prefix=packageName+"conf.",cPrefix=packageName+"chart.";

        /**data-model for one chart*/

        public List<Chrt>chrts=new LinkedList<Chrt>();
        public int termBase,termInc;

        public Chrt newChrt(String ttl){
            Chrt m=new Chrt( ttl);
            m.id=chrts.size();
            chrts.add(m);
            return m;}

        /**data-model for one chart*/
        public class Chrt{public int id;
            public String chartTitle=""
                    ,valLabel="",catLabel="",style="style1",by_row="";

            public Var head,var;

            Chrt(String ttl){chartTitle=ttl;}

            public Var newVar(String ttl){
                Var v=new Var(ttl);
                if(head==null)(head=var=v).id=0;
                else{v.id=var.id+1;v.prv=var;var.nxt=v;}
                return var=v;}

            public Var addVar(Var v){
                if(head==null)(head=var=v).id=0;
                else{v.id=var.id+1;v.prv=var;var.nxt=v;}
                return var=v;}

            public class Var{
				Lbl.Statistics sttstc;//int id;
				String ttl;Var prv,nxt;Var(String t){ttl=t;}
                Double[]series;//public List<Double>series=new LinkedList<Double>();
				public Map m(Map m){if(m==null)m=Util.mapCreate();
					return Util.mapSet(m,"ttl",ttl,"id",id,"series",series);}
            }//class Var
			
			public Map m(Map m){if(m==null)m=Util.mapCreate();
				return Util.mapSet(m,"id",id
				,"chartTitle",chartTitle
				,"valLabel",valLabel
				,"catLabel",catLabel
				,"style",style
				,"by_row",by_row
				,"list",list());}

			public Map list(){
				Var v=head;List m=v==null?null:new LinkedList();
				while(v!=null){
					m.add( v.m(null));
					v=v.nxt;}
				return m;}
        }//Chrt
		
		public static Map def(){Map a=Util.mapCreate("chrts","List<Chrt>"
			,"termBase","int"
			,"termInc","int"
			,"Chrt",Util.mapCreate("id","int"
				,"chartTitle","String"
				,"valLabel","String"
				,"catLabel","String"
				,"style","String"
				,"by_row","String"
				,"list","List<Var>"
				,"Var",Util.mapCreate("id","int"
					,"ttl","String"
					,"series","Double[]"
				)
			 )
			);
			return a;}

		public Map m(Map m){if(m==null)m=Util.mapCreate();
			return Util.mapSet(m,"chrts",chrts(),"termBase",termBase,"termInc",termInc);}

		public Map chrts(){List m=new LinkedList();for(Chrt c:chrts)m.add(c.m(null));return m;}

    }//class Model
	
%><%@ include file="realestate1805.jsp"%><%!
}//S HttpServlet
%>
