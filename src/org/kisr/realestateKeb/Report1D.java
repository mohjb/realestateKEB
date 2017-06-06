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
/**
 * Created by mbohamad on 05/06/2017.
 */
public class Report1D extends Report1C {

	final static String jspName="report1D.jsp";//,chartName="chart.jsp";


static List<Map<String,String>>toJson(Lbl.Term[]a){return null;}
static List<Map<String,String>>toJson(Lbl.Ranks[]a){return null;}
static List<Map<String,String>>toJson(Lbl.Contrct[]a){return null;}
static List<Map<String,String>>toJson(Lbl.Statistics[]a){return null;}


public static void service(GenericServlet srvlt
	,final javax.servlet.http.HttpServletRequest request
	, final javax.servlet.http.HttpServletResponse response)
	throws Exception
{response.setContentType("text/html; charset=utf-8");
	try
	{TL tl=TL.Enter(srvlt,request,response,response.getWriter());//out);//
		tl.logOut=tl.h.var("logOut",true);tl.h.comments=tl.h.CommentJson;
		if (request.getCharacterEncoding() == null)
			request.setCharacterEncoding("UTF-8");
		String op=tl.h.req("op");

		Map<LookupTbl.Col,Map<Integer,LookupTbl>> lookup=LookupTbl.lookup();
		Map<Integer,Integer>namesGovs=namesGovs(tl);
		int[]minmaxYear=DataTbl.minmaxYear();
		Lbl.Statistics[ ]StatisticsA=Lbl.Statistics.values();
		Prm[]prmA=Prm.values();
		if("resetLookup".equals( op )) {
			tl.h.a( LookupTbl.class, null );
			tl.out("application-scope has been reset for entry 'lookup' hashmap, tl.h.a( LookupTbl.class, null ); ");
		}else if("get".equals( op )||op==null)
			TL.Util.mapSet( tl.response
				, "lookup", lookup
				, "minmaxYear", minmaxYear
				, "terms", toJson( Lbl.Term.values() )
				, "ranks", toJson( Lbl.ranks )
				, "contrcts", toJson( Lbl.contrcts )
				, "options", "allGovs,allTyps,aggGovs"
				, "Statistics", toJson( StatisticsA )
				, "jspName", jspName
				, "namesGovs", namesGovs
				, "Prms", prmA
			);
		else if("post".equals( op ) ) {
			String p[]=new String[prmA.length];
			int nullCol=-1;int[]statistics=null;
			for(int i=0;i<p.length;i++)
			{	p[i]=tl.h.req(prmA[i].toString());
				if(p[i]==null)nullCol=i;
			}
			{Object o=tl.json.get( "statistics" );
				if(o instanceof List){List<Number>l=(List)o;
					int n=l.size();
					tl.h.s("statistics",statistics=new int[n]);
					for(int i=0;i<l.size();i++)
						statistics[i]=l.get( i ).intValue();
				}else
					statistics=(int[])tl.h.s("statistics");
			}

			Map<Integer,LookupTbl>typs=lookup.get(LookupTbl.Col.type)
				,govs=lookup.get(LookupTbl.Col.gov);
			LookupTbl gov=govs.get(TL.Util.parseInt(Prm.gov.v(p),0))
				, typ=typs.get(TL.Util.parseInt(Prm.typ.v(p),0));

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

			boolean allGovs=gov.code==0
				,aggGovs="a".equals(Prm.gov.v(p))
				,allTyps="0".equals(Prm.typ.v(p));
			LookupTbl.Col col_Gov_or_name=allGovs?LookupTbl.Col.gov:LookupTbl.Col.name;

			Lbl.Term term=Lbl.Term.annual;
			if(Prm.terms.v(p)!=null)
				try{term=Lbl.Term.valueOf(Prm.terms.v(p));
				}catch(Exception ex){tl.error("parse term",ex);}

			Lbl.Contrct contrct=Prm.contract.v(p)==null?Lbl.Contrct.all:Lbl.Contrct.valueOf(Prm.contract.v(p));

			if(nullCol<0)try
			{	List<Map<Object,Object>>l=new LinkedList<Map<Object,Object>>();
				tl.response.put( "l",l );

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
					default:sql.append( "year(`").append(DataTbl.C.d).append("`)-")
						.append(from).append(" as t");
				}
				for(int i=0;i<=statistics.length;i++)
					sql.append(",").append(StatisticsA[statistics[i]].sql);

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
				{int i=1;ps.setObject(i++,from);
					ps.setObject(i++,to);
					if(contrct!=Lbl.Contrct.all)
						ps.setObject(i++,p[Prm.contract	.ordinal()]);
					if(!allGovs)	ps.setObject(i++,p[Prm.gov		.ordinal()]);
					if(!allTyps)	ps.setObject(i++,p[Prm.typ		.ordinal()]);
				}
				int row=0,currentName=-1,base=term==Lbl.Term.aggregate ? 1:(to-from+1)*term.base;
				Object tbl[][]=null;
				ResultSet rs=ps.executeQuery();
				while(rs.next())
				{int nm=rs.getInt(1),yr=rs.getInt(2);
					if(nm!=currentName)
					{l.add( TL.Util.mapCreate("ix",currentName
						,"title",lookup.get(col_Gov_or_name).get(currentName).text
						,"tbl",tbl=new Object[base][statistics.length]) );
						currentName=nm;
					}
					for(int c=0;c<statistics.length;c++)try{
						tbl[yr][c]=rs.getObject(c+3);}catch(Exception ex){
						ex.printStackTrace();}
				}
			}//if(nullCol<0)
			catch(Throwable x){
				tl.response.put( "endX",tl.logo(jspName+":if(nullCol<0) Throwable:",x));
			}//catch
			tl.response.put( "end",tl.logo(tl));
		}//if op post
		//tl.logo(tl.response);
		new Json.Output( tl.h.getOut() ).o( tl.response );
	}
		catch(Exception ex){}
	finally {
		TL.Exit();
	}
}//service

}//class Report1D
