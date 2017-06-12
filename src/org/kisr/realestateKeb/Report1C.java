package org.kisr.realestateKeb;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import javax.servlet.*;
import org.kisr.realestateKeb.App.LookupTbl;
import org.kisr.realestateKeb.App.DataTbl;


public class Report1C {

	final static String jspName="report1C.jsp";//,chartName="chart.jsp";

	static void reset(String[][]a){for(int i=0;i<a.length;i++)for(int j=0;j<a[i].length;j++)a[i][j]="";}

	static String filterNum(String p)
	{if(p==null)return p;String r=p.replaceAll(",","");
		if(r.endsWith(".0"))r=r.substring(0,r.length()-2);return r;}

	static void htmlTable(String[][]a
		,String ttl//,LookupTbl.Col col,int coli//strsJ
		,String trAttribs,int yr,Lbl.Term term,Chart.Model mod
		,int[]pStatistics
		,boolean reversedXAxis
	)throws Exception
	{final int termBase=term.base, Ix=0;
		Chart.Model.Chrt c0=mod.chrts.get(0), v=mod.newChrt(ttl);
		v.var=c0.newVar(ttl);
		TL.out("\n<tr ",trAttribs,"><th rowspan=\"",a.length,"\">",ttl
				,"<img src=\"",Chart.jspName,"?id=",v.id);//cats="+cats+"&series="
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
			for (int j = 0; j < a[i].length; j++) {
				TL.out(j % termBase == 0//quart&&j%4==0
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
			}TL.out("</tr>");
		}
	}//htmlTable()

	static Map<Integer,Integer>namesGovs(TL tl)throws java.io.IOException,java.sql.SQLException
	{final String nmg="realestateKeb.lookup.namesGovs";
		Map<Integer,Integer>a=(Map<Integer,Integer>)tl.h.a(nmg);
		if(a==null)
		{Object[][]o=TL.DB.q("select distinct `"
				+DataTbl.C.name+"`,`"
				+DataTbl.C.gov+"` from "
				+DataTbl.Name);
			tl.h.a(nmg,a=new HashMap<Integer,Integer>());
			for(int i=0;i<o.length;i++)
			{Object on=o[i][0],og=o[i][1];
				int n=on instanceof Number
					?((Number)on).intValue()
					:TL.Util.parseInt(String.valueOf(on),-1)
					,gov=og instanceof Number
					?((Number)og).intValue()
					:TL.Util.parseInt(String.valueOf(og),0);
				//if(n>=0 && n<o.length)
					a.put(n,gov);//a[n]=gov;
			}
		}
		return a;
	}

 /**these are html-post-parameter names, so that the names are centeralized/have one spelling*/
		enum Prm{from,to,from2,to2
			,typ,contract,gov,terms;//S;
			String v(String[]a){return a[ordinal()];}};


		enum Lbl{;

			enum Ranks implements LookupTbl.ILang{الأول
					           ("1st"),
				الثاني
						("2nd"),
				الثالث
						("3rd"),
				الرابع
						("4th");protected String en;Ranks(String e){en=e;}
				public String lang(){return TL.tl().lang==TL.Lang.ar?name():en;}
				public Map lang(Map m){m.put("ar",name());m.put("en",en );return m;}
			}//enum Ranks	   String[]ranks=new String[]{"الأول","الثاني","الثالث","الرابع"};

			enum Contrct implements LookupTbl.ILang {//Null
				all(0,"إجمالي العقود","Total"),
				c1(1,"عقود مسجلة","Registered"),
				c2(2,"وكالات عقارية","Agent")
				;

				protected String ar,en;public int v;
				//Contrct(){str=name();}
				Contrct(int i,String p,String e){ar=p;en=e;v=i;}
				//@Override public String toString(){return str==null?name():str;}
				public String lang(){return TL.tl().lang==TL.Lang.ar?ar:en;}
				public Map lang(Map m){m.put("ar",ar);m.put("en",en );return m;}
			};//Contrct

			public static Term[]terms=Term.values();
			public static Ranks[]ranks=Ranks.values();
			public static Contrct[]contrcts=Contrct.values();
			public static Statistics[]sttstcs=Statistics.values();

		enum Term implements LookupTbl.ILang{
			aggregate(1,"إجمالي","إجمالي الفترة" ,"Aggregate","aggregate")
			, annual(1,"سنة","سنوي" ,"Annual","Year")
			, nineMonths(1,"9شهور","9شهور" ,"Nine Months","Nine Months")//	=9months
			, semiAnnual(2,"النصف","نصف سنوي" ,"Semi-Annual","Half")
			, quarterly(4,"الربع","ربع سنوي" ,"Quarterly","Quarter")
			, monthly(12,"شهر","شهري" ,"Monthly","Month")
			, weekly(52,"اسبوع","اسبوعي" ,"Weekly","Week");
			public int base;protected String ar,lbl,en,enLbl;
			Term(int b,String a,String lbl,String e,String el){base=b;ar=a;this.lbl=lbl;en=e;enLbl=el;}
			public String lang(){return TL.tl().lang==TL.Lang.ar?ar:en;}
			public String lbl(){return TL.tl().lang==TL.Lang.ar?lbl:enLbl;}
			public Map lang(Map m){m.put("ar",ar);m.put("en",en );return m;}
			public Map lbl(Map m){m.put("arLbl",lbl);m.put("enLbl",enLbl );return m;}
		}//enum Term


		enum Statistics implements LookupTbl.ILang{
		count("count(*)","عدد","Count")
			,amount("format(sum(`"+DataTbl.C.price+"`),3)","إجمالي قيمة التداول","Total Price")
			,avgMtr("format(sum(`"+DataTbl.C.price+"`)/sum(`"+DataTbl.C.area+"`),1)","متوسط السعر","Average Price")
			,maxMtr("format(max(`"+DataTbl.C.price+"`/`"+DataTbl.C.area+"`),4)","أعلى سعر متر","Maximum Price of 1 square meter")
			,minMtr("format(min(`"+DataTbl.C.price+"`/`"+DataTbl.C.area+"`),4)","أقل سعر متر","Minimum Price of 1 square meter")
			,avgLand("format(avg(`"+DataTbl.C.area+"`),1)","متوسط المساحة","Average Area")
			,SumLand("format(sum(`"+DataTbl.C.area+"`),3)","إجمالي المساحة","Total Area")
			,maxLand("format(max(`"+DataTbl.C.area+"`),1)","أكبر مساحة","Largest Area")
			,minLand("format(min(`"+DataTbl.C.area+"`),1)","أصغر مساحة","Smallest Area");

			public String sql;protected String ar,en;
			Statistics(String p,String l,String e){sql=p;ar=l;en=e;}
			public String lang(){return TL.tl().lang==TL.Lang.ar?ar:en;}
			public Map lang(Map m){m.put("ar",ar);m.put("en",en );return m;}
		}//enum S//sttstcs
        }//enum Lbl

	public static void service(GenericServlet srvlt
			,final javax.servlet.http.HttpServletRequest request
			, final javax.servlet.http.HttpServletResponse response)
			throws Exception
	{response.setContentType("text/html; charset=utf-8");
		//PrintWriter out = response.getWriter(); //new PrintWriter(System.out);
		try
		{TL tl=TL.Enter(srvlt,request,response,response.getWriter());//out);//
			 tl.logOut=tl.h.var("logOut",true);tl.h.comments=tl.h.CommentHtml;
			if (request.getCharacterEncoding() == null)
				request.setCharacterEncoding("UTF-8");
			String op=tl.h.req("op");
			boolean devuser=tl.h.var("devuser",false);

			Prm[]pa=Prm.values();
			String p[]=new String[pa.length];
			int nullCol=-1;
			for(int i=0;i<p.length;i++)
			{	p[i]=tl.h.req(pa[i].toString());
				//tl.log(jspName,":load Prms(",i,":",pa[i],")=",p[i]);
				if(p[i]==null)nullCol=i;
			}

			if("resetLookup".equals( op )) {
				tl.h.a( LookupTbl.class, null );
				tl.out("application-scope has been reset for entry 'lookup' hashmap, tl.h.a( LookupTbl.class, null ); ");
			}

			Map<LookupTbl.Col,Map<Integer,Map<TL.Lang,LookupTbl>>> lookup=LookupTbl.lookup();
			Map<Integer,Map<TL.Lang,LookupTbl>>typs=lookup.get(LookupTbl.Col.type)
					,govs=lookup.get(LookupTbl.Col.gov);
			if(govs==null)govs=new HashMap<Integer,Map<TL.Lang,LookupTbl>>();
			if(typs==null)typs=new HashMap<Integer,Map<TL.Lang,LookupTbl>>();
			LookupTbl gov=govs.get(TL.Util.parseInt(Prm.gov.v(p),0)).get(tl.lang)// replace iGov to gov
					, typ=typs.get(TL.Util.parseInt(Prm.typ.v(p),0)).get(tl.lang);

			int[]minmaxYear=DataTbl.minmaxYear();

			int from=TL.Util.parseInt(Prm.from.v(p),minmaxYear[0])
					,to=TL.Util.parseInt(Prm.to.v(p),minmaxYear[1])
					,from2=TL.Util.parseInt(Prm.from2.v(p),1)
					,to2=TL.Util.parseInt(Prm.to2.v(p),52);

			if(from>to)
			{int tmp=from;
				from=to;
				to=tmp;
				String temp=p[0];
				p[0]=p[1];
				p[1]=temp;
			}

			boolean allGovs=gov.code==0//"0".equals()
					,aggGovs="a".equals(Prm.gov.v(p))
					,allTyps="0".equals(Prm.typ.v(p));
			LookupTbl.Col col_Gov_or_name=allGovs?LookupTbl.Col.gov:LookupTbl.Col.name;

			Lbl.Term term=Lbl.Term.annual;
			if(Prm.terms.v(p)!=null)
				try{term=Lbl.Term.valueOf(Prm.terms.v(p));
				}catch(Exception ex){tl.error("parse term",ex);}
			boolean reversedXAxis=tl.h.var("realestateKeb.conf.reversedXAxis",false);
			/**edit heading parameters / user-inputs*/boolean stateEdit=tl.h.var("stateEdit",true);//jspName+".stateEdit"
			Lbl.Contrct contrct=Prm.contract.v(p)==null?Lbl.Contrct.all:Lbl.Contrct.valueOf(Prm.contract.v(p));

			tl.out("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n"
					,"<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n"
					,"<head>\r\n"
					,"\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r\n"
					,"\t<title>Real-Estate</title>\r\n"
					,"</head>\r\n"
					,"<body dir=\"rtl\" style=\"background-image:url(bg.jpg); background-repeat:repeat-x\">\r\n"
					," <div><table width=\"99%\" onclick=\"window.location='index.jsp'\"><tr>\r\n"
					,"\t\t<td><img style=\"border:3px black double\" src=\"kbu-logo.png\"/></td>\r\n"
					,"\t\t<td><h1>بيانات تداول العقار في دولة الكويت</h1></td>\r\n"
					,"\t\t<td><img src=\"kisrlogo.gif\"/></td></tr>\r\n"
					,"\t</table>\r\n"
					,"\t<div style=\"border:3px black double\">\r\n"
					,"\t\t<form method=\"post\" action=\""
			,jspName
					,"\">\r\n"
					,"\t\t\t<table width=\"75%\" border=\"0\" align=\"center\">\r\n"
					,"\t\t\t  <tr><th>من</th><th>الى</th>\r\n");
			if(stateEdit)
				tl.out("<th>متغيرات</th>");
			tl.out("<th>تفصيل</th>\n"
					,"\t<th>نوع العقد</th>\n"
					,"\t<th>محافظة</th>\n"
					,"\t<th>نوع العقار</th>\n"
					,"\t\t\t\t\t<td rowspan=\"2\">\n"
					,"\t\t<input type=\"hidden\" name=\"stateEdit\" value=\"",!stateEdit,"\"/>\n"
					,"\t<input type=\"submit\" value=\""
					,stateEdit?"بحث":"تغير"
					,"\"/></td>\n  </tr>\n  <tr>\n\t<td>");
			if(stateEdit){
				tl.out("<select name=\"",Prm.from,"\" >");
				for(int i=minmaxYear[0];i<=minmaxYear[1];i++) {
					//String s = String.valueOf(i);
					tl.out("\n\t\t\t<option ", (from==i//s.equals(Prm.from.v(p)
					?" selected":"" ),">",i,"</option>");
				}
				tl.out("</select><br/>\n\t\t<select name=\"",Prm.from2,"\" >");
				for(int i=1;i<=52;i++) {
					//String s = String.valueOf(i);
					tl.out("\n" ,
							"\t\t\t<option ",( i==from2//s.equals(Prm.from2.v(p)
					?" selected":""),">",i,"</option>");
				}
				tl.out("</select>");
			}else
				tl.out(from ,"<br/>", Prm.from2.v(p));

			tl.out("</td>\n\t<td>");

			if(stateEdit){
				tl.out("<select name=\"",Prm.to,"\" >");
				for(int i=minmaxYear[0];i<=minmaxYear[1];i++) {
					//String s = String.valueOf(i);
					tl.out("\n\t\t\t<option", (i==to//s.equals(Prm.to.v(p)
					?" selected":"" ),">",i,"</option>");
				}
				tl.out("</select><br/>\n" ,
						"\t\t<select name=\"",Prm.to2,"\" >");
				for(int i=1;i<=52;i++) {
					//String s = String.valueOf(i);
					tl.out("\n" ,
							"\t\t\t<option",( i==to2//s.equals(Prm.to2.v(p))
							?" selected>":">"),i,"</option>");
				}
				tl.out("</select>");
			}else
				tl.out(to ,"<br/>", to2);

			tl.out("</td>");

			int[]pStatistics=(int[])tl.h.s(jspName+".pStatistics");
			if(pStatistics==null)
			{int n=Lbl.sttstcs.length+1;
				tl.h.s(jspName+".pStatistics",pStatistics=new int[n]);
				pStatistics[0]=1;
				for(int i=1;i<n;i++)pStatistics[i]=i-1;
			}

			if(stateEdit){
				tl.out("<td><div style=\"color:white;background-color:#3f8;border:2px solid green\">");
				int n=pStatistics.length;
                Lbl.Statistics[]a=Lbl.sttstcs;
				if("Statistics.reorder".equals(op))
				{int i=TL.Util.parseInt(tl.h.req("i"),-1);
					if(i<n&&i>=0)
					{	if(i==0)
					 {if(pStatistics[0]>1)
						pStatistics[0]--;
					 }
					 else
					 {	if(i==pStatistics[0]+1)
						pStatistics[0]++;else
						{int t=pStatistics[i];
							pStatistics[i]=pStatistics[i-1];
							pStatistics[i-1]=t;
						}
					 }
					}
				}

				if( pStatistics[0]<1)
					pStatistics[0]=1;
				for(int i=1;i<n;i++) {
					if ( i == pStatistics[0] + 1 )
						tl.out("</div>\n\t\t\t<div style=\"color:grey;background-color:#333;border:2px solid black\">");
					if( i>1 )
						tl.out("\n\t\t\t<a href=\"", jspName ,"?op=Statistics.reorder&i=",i,"\">⬆</a>");
					else
						tl.out("<span style=\"font-size:24px; font-weight: 24px\">");
					int j=i>=pStatistics.length?-1:pStatistics[ i ];
					tl.out(a==null||j<0||j>=a.length ? null:a[ j ]	.lang());
					if( i==1 )
						tl.out("</span>") ;
					else if( i==pStatistics[0] )
						tl.out("<a href=\"",jspName,"?op=Statistics.reorder&i=0\">⬇</a>");
					if( i<n-1&&i!=pStatistics[0] )
						tl.out("<hr/>");
				}
				tl.out("</div></td>");
			}

			tl.out("<td>");
			if(stateEdit)
			{   Lbl.Term[]a=Lbl.terms;
				for(Lbl.Term t:a)//int i=0;i<a.length;i++)
				{	//if(stateEdit)
					tl.out("<input type=\"radio\" name=\""
							,Prm.terms,"\"\n\tvalue=\"" ,t,"\" "
							, (term==t?"checked":"" )
							,"/>");
					tl.out(t.lbl ,"<br/>\n");
				}
			}else
				tl.out(term==null?"-":term.lbl);
			tl.out("</td><td>");

			if(stateEdit)
			{	for(Lbl.Contrct i:Lbl.contrcts)//String s=String.valueOf(i);
				{	tl.out("\t\t\t<input type=\"radio\" name=\""
					,Prm.contract
					,"\" \r\n"
					,"\t\t\t\t\t\tvalue=\""
					,i
					,'"'
					,' '
					, i==contrct?"checked":""
					,'/'
					,'>'
					, i.ar
					,"<br/>");
				}
			}
			else
				tl.out(contrct==null?"-":contrct.ar);

			tl.out("</td><td line=\"line:264 ; gov-td ; wrong-to-close:line323 \">");
			//int iGov=allGovs?StrsIndx.gov.ordinal():StrsIndx.name.ordinal();
			List<List<Integer>>govsAgg=(List<List<Integer>>)tl.h.s(jspName+".govsAgg");
			{Object o=tl.h.s(jspName+".govsAgg.deactivate");
				if(o!=null)
					govsAgg=null;
			}
			if(stateEdit)
			{	if(("govsAgg.deactivate").equals(op))
					tl.h.s(jspName+".govsAgg.deactivate",(govsAgg=null)==null);
				if(("govsAgg.activate").equals(op))
				{tl.h.s(jspName+".govsAgg.deactivate",null);
					if((govsAgg=(List<List<Integer>>)tl.h.s(jspName+".govsAgg"))==null)
						tl.h.s(jspName+".govsAgg", govsAgg=new LinkedList<List<Integer>>());
				}
				if(devuser)
				{ tl.out("<table width\"100%\" border=\"0\"><tr>\n\t\t<td style=\"border:1px solid black\"><input type=\"radio\" name=\"govsAgg.active\" value=\"0\""
					,govsAgg==null?" checked"
							:" onchange=\"window.location='?op=govsAgg.deactivate'\""
					,"/>مبسط</td>\n"
					,"\t\t<td style=\"border:1px solid black\"><input type=\"radio\" name=\"govsAgg.active\" value=\"1\"<"
					,govsAgg!=null?" checked":" onchange=\"window.location='?op=govsAgg.activate'\""
					,"/>مجاميع مجموعات</td></tr></table>\n");
				}else
					tl.out("<input old-type=\"radio\" type=\"hidden\" name=\"govsAgg.active\" value=\"0\"/>");

				if(govsAgg==null)
				{   //Map<Integer,LookupTbl>govs=lookup.get(LookupTbl.Col.gov);
					//LookupTbl x=govs.get(0);
					tl.out("<select name=",Prm.gov," >");
					for(Map<TL.Lang,LookupTbl> t:govs.values())//int i=1;i<govs.size();i++)
					{//String s=String.valueOf(t.code);//boolean b=!allGovs && s.equals(Prm.gov.v(p));//(//(s==null&&Prm.gov.v(p)==null)||(s!=null&& ))//if(b)iGov=i;
						tl.out("\n<option value="
							,t.get( tl.lang ).code, (t==gov?" selected>":" >")//s.equals(Prm.gov.v(p))
							,t.get( tl.lang ).text,"</option>");
					}
					tl.out("</select>");
				}//if govsAgg
				else//govsAgg!=null
				{	if(("govsAgg.newGroup").equals(op)&&govsAgg!=null)
					govsAgg.add(new LinkedList<Integer>());
					int remGroup=("govsAgg.remGroup").equals(op)?TL.Util.parseInt(tl.h.req("i"),-1):-1;
					Map<Integer,Integer>ng=namesGovs(tl);//int[]ng=namesGovs(tl);
					//String[]ns=strs[0],gs=strs[StrsIndx.gov.ordinal()];
					Map<Integer,Map<TL.Lang,LookupTbl>>//gvs=lookup.get(LookupTbl.Col.gov),
							nms=lookup.get(LookupTbl.Col.name);
					for(int k=0,i ; k < govsAgg.size() ; k++ )
						if( remGroup != k )
						{	i=remGroup!=-1&&remGroup<k?k-1:k;
							String nm=Prm.gov.toString()+(i==0?"":i)
									,prm[]=request.getParameterValues(nm);
							List<Integer>l=govsAgg.get(i);
							if(prm!=null)
							{l.clear();
								for(String s:prm)
								{int x=TL.Util.parseInt(s,-1);
									if(x>=0)
										l.add(x);
								}
							}
							tl.out("<table line=\"299\"><tr><td>");

							tl.out("<select multiple size=\"10\" name=\"",nm,"\">");
							for(Map<TL.Lang,LookupTbl> t:govs.values())//int j=1;j<gs.length;j++)
							{tl.out("<optGroup label=\"",t.get( tl.lang ).text,"\" >");//gs[j]
								for(Integer ii:ng.keySet())//int ii=1;ii<ng.length;ii++)
									if(ng.get(ii).intValue()==t.get( tl.lang ).code)//ng[ii]==t.code)//j
									{tl.out("\n" ,
											"\t\t\t\t\t<option value=\"",ii ,"\"");
										if(l.contains(ii)){tl.out(" selected");}
										tl.out(">",nms.get(ii) ,"</option>");
									}tl.out("</optGroup>");
							}

							tl.out("</select>\n" ,
									"\t<select mXultiple name=\"X",nm,"\">\n" ,
									"\t\t\t\t");

							for(Map<TL.Lang,LookupTbl> t:nms.values())//int j=0;j<ns.length;j++)
							{tl.out("\n\t\t\t\t<option value=\"",t.get( tl.lang ).code,"\"");
								if(l.contains(t.get( tl.lang ).code))//j
									tl.out(" selected");
								tl.out(">", t.get( tl.lang ).text,"</option>");//ns[j]
							}
							tl.out("</select>\n\n\n\t\t\t\t</td>"
							,"<td><button onclick=\"window.location='?op=govsAgg.remGroup&i="
									,i,"'\">إزالة المجموعة</button></td>\n" ,
									"\t\t</tr></table line=\"317 ; closing:299\"><hr/>");
						}//for
					if(remGroup!=-1)govsAgg.remove(remGroup);
					tl.out("<span onclick=\"window.location='?op=govsAgg.newGroup'\">إضافة مجموعة جديدة</span>");
				}//if govsAgg
	/*  % >
	< / td line="line322; wrong-to-close:gov-td">
	< / t r>
	< / t able>< % */
			}//if stateEdit
			else tl.out(gov.text);//{int i=TL.Util.parseInt(Prm.gov.v(p),-1);tl.out(i==-1?"-":strs[StrsIndx.gov.ordinal()][i]);}//if stateEdit else

			tl.out("\n" ,
					"</td>\n" ,
					"<!-- here seems to be the problem / the problems seem to start starting from here! -->\n" ,
					"\t<td>");
			if(stateEdit)
			{   tl.out("<select name=\"",Prm.typ,"\" >");
				for(Map<TL.Lang,LookupTbl> t:typs.values())//int i=0;i<strs[StrsIndx.prop_typ.ordinal()].length;i++)
				{	tl.out("\n\t\t<option value=\""
					,t.get( tl.lang ).code,"\"");
					if(t==typ)
						tl.out(" selected=\"true\"");
					tl.out(">",t.get( tl.lang ).text,"</option>");
				}
				tl.out("</select>");
			} else//{int i=parseInt(Prm.typ.v(p),-1);tl.out(i==-1?"-":strs[StrsIndx.prop_typ.ordinal()][i]);}
				tl.out(typ==null?"-":typ.text);
			tl.out("</td></tr></table></form>");

			if(!stateEdit)
			{	tl.out("<hr/>\n<table border=\"1\" rules=\"all\">");
				if(term.base!=1){tl.out("\n<colgroup span=\"2\" style=\" width:250px; border:3px black double\"/>");
					for(int yr=from;yr<=to;yr++)tl.out(
							"\n<colgroup span=\"",term.base,"\" style=\" border:3px black double\"/>");
				}
				tl.out("<tr><th width=\"200px\"");
				if(term.base!=1)
					tl.out(" rowspan=\"2\"");
				tl.out(">"
				,allGovs?"محافظة":"منطقة"
				,"</th><th");
				if(term.base!=1)
					tl.out(" rowspan=\"2\"");
				tl.out(">وصف</th>");

				for(int yr = from; term==Lbl.Term.aggregate ?yr==from:yr<=to; yr++) {
					tl.out( "\n<th" );
					if ( term.base != 1 ) {
						tl.out( " colspan=\"", term.base, "\"" );
					}
					if ( term == Lbl.Term.aggregate ) {
						tl.out( term.lbl, " ", from, " - ", to );
					} else {
						tl.out( ">سنة ", yr, "</th>" );
					}
				}tl.out("</tr>");
				if(term.base!=1)
				{	tl.out("<tr>");
					for(int i=0;i<(to-from+1)*term.base;i++)
						tl.out("\n<th>"
						,term.ar
						," "
						,term==Lbl.Term.semiAnnual ||term==Lbl.Term.quarterly
								?Lbl.ranks[i%term.base].toString()
								:String.valueOf((i%term.base)+1)
						,"</th>");
					tl.out("</tr>");
				}

				//StringBuilder series=null;String cats=reversedXAxis?String.valueOf(to)+","+(-term.base):String.valueOf(from)+","+term.base;
				if(nullCol<0)try
				{	Chart.Model mod=new Chart.Model();
					tl.h.s(jspName+Chart.Model.class,mod);
					mod.termBase=reversedXAxis?to:from;mod.termInc=(reversedXAxis?-1:1)*term.base;
					Chart.Model.Chrt c0=mod.newChrt(col_Gov_or_name.name()//lookup.get(LookupTbl.Col.gov).get(allGovs?0:iGov).text
					);

					//TL.logo(jspName,"nullCol<0");
					StringBuilder sql=new StringBuilder("select ")
							.append(allGovs?DataTbl.C.gov:DataTbl.C.name).append(",");
					switch(term){
						case semiAnnual:sql.append( "(year(`")
								.append(DataTbl.C.d).append("`)-").append(from).append(")* 2+ceiling(month(`")
								.append(DataTbl.C.d).append("`)/6)-1 as t");break;
						case quarterly:sql.append( "(year(`")
								.append(DataTbl.C.d).append("`)-").append(from).append(")* 4+ceiling(month(`")
								.append(DataTbl.C.d).append("`)/3)-1 as t");break;
						case monthly:sql.append( "(year(`")
								.append(DataTbl.C.d).append("`)-").append(from).append(")*12+month(`")
								.append(DataTbl.C.d).append("`)-1 as t");break;
						case weekly:sql.append( "(year(`")
								.append(DataTbl.C.d).append("`)-").append(from).append(")*52+week (`")
								.append(DataTbl.C.d).append("`)-1 as t");break;
						default:sql.append( "year(`").append(DataTbl.C.d).append("`)-").append(from).append(" as t");}

					for(int i=1;i<=pStatistics[0];i++)
						sql.append(",").append(Lbl.sttstcs[pStatistics[i]].sql);

					sql.append("from ").append(DataTbl.Name)
							.append(" where year(`"  ).append(DataTbl.C.d)
							.append("`)>=? and year(`").append(DataTbl.C.d).append("`)<=? ");
					if(contrct!=Lbl.Contrct.all)
						sql.append("and `").append(DataTbl.C.contract).append("`=? ");
					if(!allGovs)
						sql.append("and `").append(DataTbl.C.gov).append("`=? ");
					if(term==Lbl.Term.nineMonths)
						sql.append("and month(`"+DataTbl.C.d+"`)<=9");
					sql.append(" group by ").append(col_Gov_or_name);
					if(term!=Lbl.Term.aggregate)sql.append(",t");

					PreparedStatement ps=TL.DB.p(sql.toString());// System.out.println("realestateKeb/2012/03/05/"+jspName+":ps="+ps);
					{int i=1;ps.setObject(i++,from);//p[Prm.from		.ordinal()]
						ps.setObject(i++,to);//p[Prm.to				.ordinal()]
						if(contrct!=Lbl.Contrct.all)
							ps.setObject(i++,p[Prm.contract	.ordinal()]);
						if(!allGovs)	ps.setObject(i++,p[Prm.gov		.ordinal()]);
						if(!allTyps)	ps.setObject(i++,p[Prm.typ		.ordinal()]);
					}
					int row=0,currentName=term==Lbl.Term.aggregate ? 1:(to-from+1)*term.base;//boolean hasData=false;
					String tbl[][]=new String[pStatistics[0]][currentName];//for(int i=0;i<tbl.length;i++)tbl[i]=new String[currentName];
					currentName=-1;reset(tbl);
					ResultSet rs=ps.executeQuery();
					while(rs.next())
					{int nm=rs.getInt(1),yr=rs.getInt(2);
						if(nm!=currentName)
						{if(row++>0)
						{htmlTable/*(tbl,out,allGovs?StrsIndx.gov.ordinal():StrsIndx.name.ordinal(),currentName
				 ,"style=\"background-color:"+((++row%2)==0?"#eef":"#f8f8ff")+"\""
				 ,from,term,series,cats,strs,pStatistics,Lbl.sttstcs,reversedXAxis);*/
								(tbl,lookup.get(col_Gov_or_name).get(currentName).get( tl.lang ).text
										,"style=\"background-color:"+((row%2)==0?"#eef":"#f8f8ff")+"\""
										,from,term,mod,pStatistics,reversedXAxis);
							reset(tbl);
						}currentName=nm;
						}
						for(int c=0;c<pStatistics[0]//sttscs.length
								;c++)try{tbl[c][yr]=rs.getString(c+3);}catch(Exception ex){ex.printStackTrace();}

					}
					if(row>0)htmlTable(tbl//allGovs?StrsIndx.gov.ordinal():StrsIndx.name.ordinal(),currentName
							,lookup.get(col_Gov_or_name).get(currentName).get( tl.lang ).text
							,"style=\"background-color:"+((++row%2)==0?"#eef":"#f8f8ff")+"\""
							,from,term,mod,pStatistics,reversedXAxis);

					tl.out("</table>");

					//if(series!=null)
					{c0.legend=true;c0.chartType=Chart.CT.Bar;
						tl.out("\r\n\t<img src=\""
						,Chart.jspName//chartName
						,"?id="
						, c0.id
						,"&width=1000\"/>");
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
		}finally{TL.Exit();}
	}//service

/*static {
 serverTL =
 { "ip":"127.0.0.1"
	, "usr":null
	, "ssn":null
	, "now":
	{
		"class":"Date"
		, "time":1496557527509
		, "str":"Sun Jun 04 09:25:27 AST 2017"
	}
	,"json":null
	, "response":
	{
		"op":null
		, "return":false
		, "req":null
	}
	,"Request":
	{
		"dt":
		{
			"class":"Date"
			, "time":1496557527509
			, "str":"Sun Jun 04 09:25:27 AST 2017"
		}
		,"AuthType":null
		, "CharacterEncoding":"UTF-8", "ContentLength":102
		, "ContentType":"application/x-www-form-urlencoded"
		, "ContextPath":""
		, "Method":"POST"
		, "PathInfo":null, "PathTranslated":null
		, "Protocol":"HTTP/1.1", "QueryString":null
		, "RemoteAddr":"127.0.0.1", "RemoteHost":"127.0.0.1", "RemoteUser":null
		, "RequestedSessionId":"024F2FE199B3AF9840BA6D367764DDF7"
		, "RequestURI":"/realestateKeb/report1C.jsp", "Scheme":"http", "UserPrincipal":null
		, "Secure":false, "SessionIdFromCookie":true, "SessionIdFromURL":false, "SessionIdValid":true
		, "Locales":
			[
				{"class":"java.util.Locale", "str":"en_US", "hashCode":"5c28fd9"}
				,{"class":"java.util.Locale", "str":"en", "hashCode":"5c146b7"}
				,{"class":"java.util.Locale", "str":"ar", "hashCode":"58aba2f"}
			]
		,"Attributes":{}
		,"Headers":
			{
			"host":
				["localhost:8080"]
				,"connection":["keep-alive"]
				,"content-length":["102"]
				,"cache-control":["max-age=0"]
				,"origin":["http://localhost:8080"]
				,"upgrade-insecure-requests":["1"]
				,"user-agent":
					["Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"]
				,"content-type":["application/x-www-form-urlencoded"]
				,"accept":["text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,* / * ;q=0.8"]
				,"referer":["http://localhost:8080/realestateKeb/report1C.jsp?op=Statistics.reorder&i=3"]
				,"accept-encoding":["gzip, deflate, br"]
				,"accept-language":["en-US,en;q=0.8,ar;q=0.6"]
				,"cookie":[
			"_ga=GA1.1.1498453122.1424777502; __utma=111872281.1498453122.1424777502.1485431370.1492604361.2; __utmz=111872281.1485431370.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); JSESSIONID=024F2FE199B3AF9840BA6D367764DDF7"]
			}
		,"Parameters":
		{
			"stateEdit":["0"]
				,"from":["2000"]
				,"from2":["1"]
				,"to":["2016"]
				,"to2":["52"]
				,"terms":["aggregate"]
				,"contract":["all"]
				,"govsAgg.active":["0"]
				,"gov":["0"]
				,"typ":["0"]
		}
		,"Session":
		{
			{
				"isNew":false
				, sid:"024F2FE199B3AF9840BA6D367764DDF7"
				, "CreationTime":1496556458851
				, "MaxInactiveInterval":1800
				, "attributes":
					{"report1C.jsp.pStatistics":[3, 2, 0, 1, 3, 4, 5, 6, 7, 8]
					}
			}
			,"Cookies":
			[
				{
					"Comment":null, "Domain":null, "MaxAge":-1, "Name":"_ga", "Path":null, "Secure":false
					, "Version":0, "Value":"GA1.1.1498453122.1424777502"
				}
				,{
					"Comment":null, "Domain":null, "MaxAge":-1, "Name":"__utma"
					, "Path":null, "Secure":false, "Version":0
					, "Value":"111872281.1498453122.1424777502.1485431370.1492604361.2"
				}
				,{
					"Comment":null, "Domain":null, "MaxAge":-1, "Name":"__utmz"
					, "Path":null, "Secure":false, "Version":0, "Value":"111872281.1485431370.1.1.utmcsr"
				}
				,{
					"Comment":null, "Domain":null, "MaxAge":-1, "Name":"JSESSIONID"
					, "Path":null, "Secure":false, "Version":0, "Value":"024F2FE199B3AF9840BA6D367764DDF7"
				}
			]
		}//Session
		,"application":
			{,"ContextPath":""
				   , "MajorVersion":3, "MinorVersion":1
			}
		,"config":{	}
		,"Page":{	}
		,"Response":
			{
				"class":"org.netbeans.modules.web.monitor.server.MonitorResponseWrapper"
				, "str":"org.netbeans.modules.web.monitor.server.MonitorResponseWrapper@69b15a1"
				, "hashCode":"69b15a1"
			}
	}//Req
 }//serverTL
}//static*/
}//class Report1C
