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
 public static class E{
	TL tl;
	String op,method;LookupTbl t=new LookupTbl();LookupTbl.Lang lang;
	Map<LookupTbl.Col,Map<Integer,Map<LookupTbl.Lang,LookupTbl>>> lookup;
	Map<Integer,Integer>namesGovs;
	int[]minmaxYear;
	List<Integer>sttstcs;
	Prm[]prmA;
}//E

static List<Map<String,String>>toJson(Lbl.Term[]a){
 	List l=new LinkedList();
 	for ( Lbl.Term t :Lbl.terms ){
 		l.add( TL.Util.mapCreate( "name",t.name()
	        ,"base",t.base
			    ,"ar",t.ar
			    ,"lbl",t.lbl
	    ) );
    }
 	return l;}

static List<String>toJson(Lbl.Ranks[]a){
	List l=new LinkedList();
	for ( Lbl.Term t :Lbl.terms ){
		l.add( t.name());
	}return l;}

static List<Map<String,String>>toJson(Lbl.Contrct[]a){
	List l=new LinkedList();
	for ( Lbl.Contrct t :Lbl.contrcts ){
		l.add( TL.Util.mapCreate( "name",t.name()
				,"v",t.v
				,"str",t.str
		) );
	}
	return l;}

static List<Map<String,String>>toJson(Lbl.Statistics[]a){
	List l=new LinkedList();
	for ( Lbl.Statistics t :Lbl.sttstcs ){
		l.add( TL.Util.mapCreate( "name",t.name()
				,"lbl",t.label
		) );
	}
	return l;}


	public static void gGet(TL tl,E e)throws Exception{
		if ("resetLookup".equals(e.op)) {
			tl.h.a(LookupTbl.class, null);
			tl.out("application-scope has been reset for entry 'lookup' hashmap, tl.h.a( LookupTbl.class, null ); ");
		} else if ("get".equals(e.op) || e.op == null)
			TL.Util.mapSet(tl.response
					, "lookup", e.lookup
					, "minmaxYear", e.minmaxYear
					, "terms", toJson(Lbl.Term.values())
					, "ranks", toJson(Lbl.ranks)
					, "contrcts", toJson(Lbl.contrcts)
					, "options", "allGovs,allTyps,aggGovs"
					, "Statistics", toJson(Lbl.sttstcs)
					, "jspName", jspName
					, "namesGovs", e.namesGovs
					, "Prms", e.prmA
			);
	}


	public static void query(TL tl,E e)throws Exception{
	String p[] = new String[e.prmA.length];
		int nullCol = -1;

		for (int i = 0; i < p.length; i++) {
			p[i] = tl.h.req(e.prmA[i].toString());
			if (p[i] == null) nullCol = i;
		}
		{//int[] statistics = null;
			Object o = tl.json.get("sttstcs");
			if (o instanceof List) {
				e.sttstcs=(List<Integer>)o;
				/*
				List<Number> l = (List) o;int n = l.size();
				tl.h.s("statistics", statistics = new int[n]);
				for (int i = 0; i < l.size(); i++)
					statistics[i] = l.get(i).intValue();*/
			} else
			{e.sttstcs = new LinkedList<>(  );
				e.sttstcs.add( Lbl.Statistics.avgMtr .ordinal());
				e.sttstcs.add( Lbl.Statistics.count .ordinal());
			}
		}
		e. lang=LookupTbl.Lang.ar;{Object o=tl.h.var( "lang" );
			if(o!=null)try{e.lang=LookupTbl.Lang.valueOf( o.toString() );}catch(Exception ex){}}


		Map<Integer, Map<LookupTbl.Lang,LookupTbl>> typs = e.lookup.get(LookupTbl.Col.type)
				, govs = e.lookup.get(LookupTbl.Col.gov);
		LookupTbl gov = govs.get(TL.Util.parseInt(Prm.gov.v(p), 0)).get( e.lang )
			, typ = typs.get(TL.Util.parseInt(Prm.typ.v(p), 0)).get( e.lang );

		int from = TL.Util.parseInt(Prm.from.v(p), e.minmaxYear[0])
			, to = TL.Util.parseInt(Prm.to.v(p), e.minmaxYear[1])
			, from2 = TL.Util.parseInt(Prm.from2.v(p), 1)
			, to2 = TL.Util.parseInt(Prm.to2.v(p), 52);

		if (from > to) {
			int tmp = from;
			from = to;
			to = tmp;
			String temp = p[0];
			p[0] = p[1];
			p[1] = temp;
		}

		boolean aggGovs = "a".equals(Prm.gov.v(p)) || "7".equals(Prm.gov.v(p))
				, allGovs = gov.code == 0 ||aggGovs
				, allTyps = "0".equals(Prm.typ.v(p));
		LookupTbl.Col col_Gov_or_name = allGovs
			? LookupTbl.Col.gov : LookupTbl.Col.name;

		Lbl.Term term = Lbl.Term.annual;
		if (Prm.terms.v(p) != null)
			try {term = Lbl.Term.valueOf(Prm.terms.v(p));}
			catch (Exception ex) {tl.error("parse term", ex);}

		Lbl.Contrct contrct = Prm.contract.v(p) == null
			? Lbl.Contrct.all : Lbl.Contrct.valueOf(Prm.contract.v(p));

		if (nullCol < 0) try {
			List<Map<Object, Object>> l = new LinkedList<Map<Object, Object>>();
			tl.response.put("l", l);

			StringBuilder sql = new StringBuilder("select ")
					.append(allGovs ? DataTbl.C.gov : DataTbl.C.name).append(",");
			switch (term) {
				case semiAnnual:
					sql.append("(year(`")
							.append(DataTbl.C.d).append("`)-").append(from).append(")* 2+ceiling(month(`")
							.append(DataTbl.C.d).append("`)/6)-1 as t");
					break;
				case quarterly:
					sql.append("(year(`")
							.append(DataTbl.C.d).append("`)-").append(from).append(")* 4+ceiling(month(`")
							.append(DataTbl.C.d).append("`)/3)-1 as t");
					break;
				case monthly:
					sql.append("(year(`")
							.append(DataTbl.C.d).append("`)-").append(from).append(")*12+month(`")
							.append(DataTbl.C.d).append("`)-1 as t");
					break;
				case weekly:
					sql.append("(year(`")
							.append(DataTbl.C.d).append("`)-").append(from).append(")*52+week (`")
							.append(DataTbl.C.d).append("`)-1 as t");
					break;
				default:
					sql.append("year(`").append(DataTbl.C.d).append("`)-")
							.append(from).append(" as t");
			}
			for (int i = 0; i < e.sttstcs.size(  ); i++)
				sql.append(",").append(Lbl.sttstcs[e.sttstcs.get( i )].sql);

			sql.append("from ").append(DataTbl.Name)
					.append(" where year(`").append(DataTbl.C.d)
					.append("`)>=? and year(`").append(DataTbl.C.d).append("`)<=? ");
			if (contrct != Lbl.Contrct.all)
				sql.append("and `").append(DataTbl.C.contract).append("`=? ");
			if (!allGovs)
				sql.append("and `").append(DataTbl.C.gov).append("`=? ");
			if (term == Lbl.Term.nineMonths)
				sql.append("and month(`" + DataTbl.C.d + "`)<=9");
			sql.append(" group by ").append(col_Gov_or_name);
			if (term != Lbl.Term.aggregate) sql.append(",t");

			PreparedStatement ps = TL.DB.p(sql.toString());// System.out.println("realestateKeb/2012/03/05/"+jspName+":ps="+ps);
			{int i = 1;
				ps.setObject(i++, from);
				ps.setObject(i++, to);
				if (contrct != Lbl.Contrct.all)
					ps.setObject(i++, p[Prm.contract.ordinal()]);
				if (!allGovs) ps.setObject(i++, p[Prm.gov.ordinal()]);
				if (!allTyps) ps.setObject(i++, p[Prm.typ.ordinal()]);
			}
			int row = 0, currentName = -1
				, base = term == Lbl.Term.aggregate ? 1 : (to - from + 1) * term.base;
			List<Map<Object,Object>>data=new LinkedList<Map<Object,Object>>();
			Object[][]tbl=null;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int nm = rs.getInt(1), yr = rs.getInt(2);
				if (nm != currentName) {
					data.add(TL.Util.mapCreate("nm", currentName
							, "ttl", e.lookup.get(col_Gov_or_name).get(currentName).get( e.lang ).text
							, "tbl", tbl =new Object[base][e.sttstcs.size()])
					);
					currentName = nm;
				}
				for (int c = 0; c < e.sttstcs.size(); c++)
					try {
						tbl[yr][ c]= rs.getObject(c + 3);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
			}
		}//if(nullCol<0)
		catch (Throwable x) {
			tl.response.put("endX", tl.logo(jspName + ":if(nullCol<0) Throwable:", x));
		}//catch
		tl.response.put("end", tl.logo(tl));
}



	public static void service(GenericServlet srvlt
	,final javax.servlet.http.HttpServletRequest request
	, final javax.servlet.http.HttpServletResponse response)
	throws Exception {response.setContentType("text/html; charset=utf-8");TL tl=null;
	try {
		tl = TL.Enter(srvlt, request, response, response.getWriter());//out);//
		tl.logOut = tl.h.var("logOut", true);
		tl.h.comments = tl.h.CommentJson;
		if (request.getCharacterEncoding() == null)
			request.setCharacterEncoding("UTF-8");
		E e = new E();
		e.tl = tl;
		tl.h.r("e", e);
		e.op = tl.h.req("op");
		e.method = request.getMethod();
		String prefix=tl.h.var("prefix","");
		e.t.readReq(prefix);

		e.lookup = LookupTbl.lookup();
		e.namesGovs = namesGovs(tl);
		e.minmaxYear = DataTbl.minmaxYear();

		e.prmA = Prm.values();
		if (e.t.no == null) {
			if ("GET".equals(e.method))
				gGet(tl, e);
			else if ("POST".equals(e.method))
				query(tl, e);
		}	else //if (e.t.no != null)
		{	if ("GET".equals(e.method))
				tl.response.put("data",TL.DB.q("select * from t where no=?",e.t.no));
			else if ("PUT".equals(e.method))
				tl.response.put("data",e.t.save());//TL.DB.x("insert into t values(?, `" +tl.h.req("col")+"`=? where no=?",tl.h.req("value"),e.t.no));
			else if ("POST".equals(e.method))
				tl.response.put("data",e.t.save());//TL.DB.x("update t set `"+tl.h.req("col")+"`=? where no=?",tl.h.req("value"),e.t.no));
			//else if ("DELETE".equals(e.method)) tl.response.put("data",e.t.delete());;
		}
		new Json.Output(tl.h.getOut()).o(tl.response);
	}catch(Exception ex){if(tl!=null)
			tl.error("Report1D",ex);
	}finally {
		TL.Exit();
	}
}//service



}//class Report1D
