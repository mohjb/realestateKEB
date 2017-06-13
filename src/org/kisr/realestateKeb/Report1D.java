package org.kisr.realestateKeb;

import java.io.PrintWriter;
import java.util.*;
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
	String op,method;LookupTbl t=new LookupTbl();
	Map<LookupTbl.Col,Map<Integer,Map<TL.Lang,LookupTbl>>> lookup;
	Map<Integer,Integer>namesGovs;
	int[]minmaxYear;
	List<Number>sttstcs;//
	Prm[]prmA;
}//E
 public static enum L{
	zero             // 0
	,dir             // 1
	,title           // 2
	,from            // 3
	,to              // 4
	,Statistics      // 5
	,Terms           // 6
	,ContractType    // 7
	,gov             // 8
	,sector          // 9
	,realestateType  // 10
	,Query           // 11
	,AreaName        // 12
	,Desc            // 13
	,first           // 14
	,second          // 15
	,third           // 16
	,fourth          // 17
	,total           // 18
	,Registered      // 19
	,Agent           // 20
	,Agg             // 21
	,AggPeriod       // 22
	,Annual          // 23
	,nineMonths      // 24
	,Half            // 25
	,SemiAnnual      // 26
	,Quarter         // 27
	,Quarterly       // 28
	,Month           // 29
	,Monthly         // 30
	,Week            // 31
	,Weekly          // 32
	,Count           // 33
	,TotalPrice      // 34
	,AvgPrice        // 35
	,MaxPrice        // 36
	,MinPrice        // 37
	,AvgArea         // 38
	,TotalArea       // 39
	,MaxArea         // 40
	,MinArea         // 41
	,kisr            // 42
	,ted             // 43
	,mohjb           // 44
}

static List<Map<String,String>>toJson(Lbl.Term[]a){
 	List l=new LinkedList();
 	for ( Lbl.Term t :Lbl.terms )
        l.add( t.lbl(  t.lang( TL.Util.mapCreate( "name",t.name()
                ,"base",t.base ) )));
 	return l;}

static List<Map<String,String>>toJson(Lbl.Ranks[]a){
	List l=new LinkedList();
	for ( Lbl.Ranks t :a )
        l.add( t.lang( new HashMap(  ) ) );//TL.Util.mapCreate( "name",t.name(),"en",t.en)
	return l;}

static List<Map<String,String>>toJson(Lbl.Contrct[]a){
	List l=new LinkedList();
	for ( Lbl.Contrct t :Lbl.contrcts )
		l.add( t.lang( TL.Util.mapCreate( "name",t.name()
				,"v",t.v ) ));
	return l;}

static List<Map<String,String>>toJson(Lbl.Statistics[]a){
	List l=new LinkedList();
	for ( Lbl.Statistics t :Lbl.sttstcs )
		l.add( t.lang( TL.Util.mapCreate( "name",t.name() ) ));
	return l;}

static Map<String,Map<Integer,Map<String,String>>>
toJson(Map<LookupTbl.Col,Map<Integer,Map<TL.Lang,LookupTbl>>>a){
	Map<String,Map<Integer,Map<String,String>>>m=new
HashMap<String,Map<Integer,Map<String,String>>>();
	for ( LookupTbl.Col col :a.keySet() )
	{Map<Integer,Map<TL.Lang,LookupTbl>>a2=a.get( col );
		String k2=col.toString();
		Map<Integer,Map<String,String>> m2=m.get(k2);
		if(m2==null)
			m.put( k2,m2=new HashMap<Integer,Map<String,String>>() );
		for(Integer i:a2.keySet()){
			Map<TL.Lang,LookupTbl>a3=a2.get( i );//String k3=i.toString();
			Map<String,String>m3=m2.get( i );
			if(m3==null)
				m2.put( i, m3=new HashMap<String,String>() );
			for(TL.Lang lng:a3.keySet()){
				LookupTbl t=a3.get( lng );
				String k4=String.valueOf( lng );
				m3.put( k4,t==null?"":t.text );
				m3.put( "code",t==null?"":String.valueOf( t.code ));
			}
		}
	}
	return m;}

	public static void gGet(TL tl,E e)throws Exception{
		if ("resetLookup".equals(e.op)) {
			tl.h.a(LookupTbl.class, null);
			tl.out("application-scope has been reset for entry 'lookup' hashmap, tl.h.a( LookupTbl.class, null ); ");
		} else if ("get".equals(e.op) || e.op == null)
			TL.Util.mapSet(tl.response
					, "lookup", toJson( e.lookup)
					, "minmaxYear", e.minmaxYear
					, "terms", toJson(Lbl.terms)
					, "ranks", toJson(Lbl.ranks)
					, "contrcts", toJson(Lbl.contrcts)
					//, "options", TL.Util.lst( "allGovs","allTyps","aggGovs")
					, "Statistics", toJson(Lbl.sttstcs)
					, "jspName", jspName
					, "namesGovs", e.namesGovs
					//, "Prms", e.prmA
			);
	}


	public static void query(TL tl,E e)throws Exception{try {
		String p[] = new String[ e.prmA.length ];
		int nullCol = -1;

		for ( int i = 0; i < p.length; i++ ) {
			p[ i ] = tl.h.req( e.prmA[ i ].toString() );
			if ( p[ i ] == null ) nullCol = i;
		}
		{try{
			Object o = tl.json==null?tl.h.req("sttstcs"):tl.json.get( "sttstcs" );
			if ( o instanceof Collection ) {
				//if ( o instanceof List< ? > ){List<?>l=(List)o;if(l. (elements are Number) ) e.sttstcs=(List<Number>)o;}if ( e.sttstcs == null )
					e.sttstcs = new LinkedList< Number >( ( Collection ) o );
			}else if ( o instanceof Object[] ) {
				e.sttstcs = new LinkedList< Number >();Object[]a=(Object[] )o;
				for ( Object v: a)
					e.sttstcs.add(
							v instanceof Number
							?(Number)v
							: TL.Util.parseInt(
								v==null?null
								:v.toString() ,0 ) );
			}
			else if(o!=null){
				String s=o.toString().trim();
				if(s.startsWith( "[" )||s.startsWith( "{" ))
				{s=s.endsWith( "]" )||s.endsWith( "}" )
					?s.substring( 1,s.length()-1 ):s.substring( 1 );}
				e.sttstcs=new LinkedList<Number>( TL.Util.parseIntsl( s ) );
			}}catch(Exception ex){}
			if(e.sttstcs==null) {
				e.sttstcs = new LinkedList();
				e.sttstcs.add( Lbl.Statistics.avgMtr.ordinal() );
				e.sttstcs.add( Lbl.Statistics.count.ordinal() );
			}
		}int n=e.sttstcs.size();

		Map< Integer, Map< TL.Lang, LookupTbl > > typs = e.lookup.get( LookupTbl.Col.type ), govs = e.lookup.get( LookupTbl.Col.gov );
		LookupTbl gov = govs.get( TL.Util.parseInt( Prm.gov.v( p ), 0 ) ).get( tl.lang ), typ = typs.get( TL.Util.parseInt( Prm.typ.v( p ), 0 ) ).get( tl.lang );

		int from = TL.Util.parseInt( Prm.from.v( p ), e.minmaxYear[ 0 ] ), to = TL.Util.parseInt( Prm.to.v( p ), e.minmaxYear[ 1 ] )
			;//	, from2 = TL.Util.parseInt( Prm.from2.v( p ), 1 ), to2 = TL.Util.parseInt( Prm.to2.v( p ), 52 );

		if ( from > to ) {
			int tmp = from;
			from = to;
			to = tmp;
			String temp = p[ 0 ];
			p[ 0 ] = p[ 1 ];
			p[ 1 ] = temp;
		}

		boolean aggGovs = "a".equals( Prm.gov.v( p ) )
				|| "7".equals( Prm.gov.v( p ) )
			, allGovs = gov.code == 0 || aggGovs
			, allTyps = "0".equals( Prm.typ.v( p ) );
		LookupTbl.Col col_Gov_or_name = allGovs
			? LookupTbl.Col.gov : LookupTbl.Col.name;

		Lbl.Term term = Lbl.Term.annual;
		if ( Prm.terms.v( p ) != null )
			try {
				term = Lbl.Term.valueOf( Prm.terms.v( p ) );
			} catch ( Exception ex ) {
				tl.error( "parse term", ex );
			}

		Lbl.Contrct contrct = Prm.contract.v( p ) == null
				                      ? Lbl.Contrct.all : Lbl.Contrct.valueOf( Prm.contract.v( p ) );
		tl.log( jspName,":query:read sttstcs: version 2017.06.13.18.20:",new Date()
				,':',e.sttstcs ," ,nullCol=",nullCol);
		if ( nullCol < 0 ) try {
			StringBuilder sql = new StringBuilder( "select " )
					                    .append( allGovs ? DataTbl.C.gov : DataTbl.C.name ).append( "," );
			switch ( term ) {
				case semiAnnual:
					sql.append( "(year(`" )
							.append( DataTbl.C.d ).append( "`)-" ).append( from ).append( ")* 2+ceiling(month(`" )
							.append( DataTbl.C.d ).append( "`)/6)-1 as t" );
					break;
				case quarterly:
					sql.append( "(year(`" )
							.append( DataTbl.C.d ).append( "`)-" ).append( from ).append( ")* 4+ceiling(month(`" )
							.append( DataTbl.C.d ).append( "`)/3)-1 as t" );
					break;
				case monthly:
					sql.append( "(year(`" )
							.append( DataTbl.C.d ).append( "`)-" ).append( from ).append( ")*12+month(`" )
							.append( DataTbl.C.d ).append( "`)-1 as t" );
					break;
				case weekly:
					sql.append( "(year(`" )
							.append( DataTbl.C.d ).append( "`)-" ).append( from ).append( ")*52+week (`" )
							.append( DataTbl.C.d ).append( "`)-1 as t" );
					break;
				default:
					sql.append( "year(`" ).append( DataTbl.C.d ).append( "`)-" )
							.append( from ).append( " as t" );
			}
			for ( int i = 0; i < n; i++ )
				sql.append( "," ).append( Lbl.sttstcs[ e.sttstcs.get( i ).intValue() ].sql );

			sql.append( "from " ).append( DataTbl.Name )
					.append( " where year(`" ).append( DataTbl.C.d )
					.append( "`)>=? and year(`" ).append( DataTbl.C.d ).append( "`)<=? " );
			if ( contrct != Lbl.Contrct.all )
				sql.append( "and `" ).append( DataTbl.C.contract ).append( "`=? " );
			if ( !allGovs )
				sql.append( "and `" ).append( DataTbl.C.gov ).append( "`=? " );
			if ( term == Lbl.Term.nineMonths )
				sql.append( "and month(`" + DataTbl.C.d + "`)<=9" );
			sql.append( " group by " ).append( col_Gov_or_name );
			if ( term != Lbl.Term.aggregate ) sql.append( ",t" );

			PreparedStatement ps = TL.DB.p( sql.toString() );// System.out.println("realestateKeb/2012/03/05/"+jspName+":ps="+ps);
			{
				int i = 1;
				ps.setObject( i++, from );
				ps.setObject( i++, to );
				if ( contrct != Lbl.Contrct.all )
					ps.setObject( i++, p[ Prm.contract.ordinal() ] );
				if ( !allGovs ) ps.setObject( i++, p[ Prm.gov.ordinal() ] );
				if ( !allTyps ) ps.setObject( i++, p[ Prm.typ.ordinal() ] );
			}
			int row = 0, currentName = -1, base = term == Lbl.Term.aggregate ? 1 : (to - from + 1) * term.base;
			List< Map< Object, Object > > data = new LinkedList< Map< Object, Object > >();
			tl.response.put( "return", data );
			Object[][] tbl = null;//new Object[ base ][ n ];
			Map<Integer,Map<TL.Lang,LookupTbl>>x=e.lookup.get( col_Gov_or_name );
			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				int nm = rs.getInt( 1 ), yr = rs.getInt( 2 );
				if ( nm != currentName )
					data.add( TL.Util.mapCreate( "nm", currentName=nm
						, "ttl", x.get( currentName=nm ).get( tl.lang ).text
						, "tbl", tbl = new Object[ n ][ base ] ) );

				for ( int c = 0; c < n; c++ )
					try {
						tbl[ c ][ yr ] = rs.getObject( c + 3 );
					} catch ( Exception ex ) {
						tl.error( jspName+":query:resultSet.next:for cols:line287:",ex );//ex.printStackTrace();
					}
			}
		}//if(nullCol<0)
		catch ( Throwable x ) {
			tl.response.put( "queryEndX:2:", tl.logo( jspName + ":if(nullCol<0) Throwable:", x ) );
		}//catch
	}catch  ( Throwable x ) {
		tl.response.put( "queryEndX:1:", tl.logo( jspName , x ) );
	}//catch
		tl.response.put("queryEnd", tl.logo(tl));
}



	public static void service(GenericServlet srvlt
	,final javax.servlet.http.HttpServletRequest request
	, final javax.servlet.http.HttpServletResponse response)
	throws Exception {response.setContentType("text/html; charset=utf-8");TL tl=null;
	try {
		tl = TL.Enter(srvlt, request, response, response.getWriter());//out);//
		tl.logOut = tl.h.var("logOut", false);
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
			if ("POST".equals(e.method)||"query".equals( e.op ))
				query(tl, e);
			else//if ("GET".equals(e.method))
				gGet(tl, e);
		}	else //if (e.t.no != null)
		{	if ("GET".equals(e.method))
				tl.response.put("return",TL.DB.q("select * from t where no=?",e.t.no));
			else if ("PUT".equals(e.method))
				tl.response.put("return",e.t.save());//TL.DB.x("insert into t values(?, `" +tl.h.req("col")+"`=? where no=?",tl.h.req("value"),e.t.no));
			else if ("POST".equals(e.method))
				tl.response.put("return",e.t.save());//TL.DB.x("update t set `"+tl.h.req("col")+"`=? where no=?",tl.h.req("value"),e.t.no));
			//else if ("DELETE".equals(e.method)) tl.response.put("return",e.t.delete());;
		}
		new Json.Output(tl.h.getOut()).o(tl.response);
	}catch(Exception ex){if(tl!=null)
			tl.error("Report1D",ex);
	}finally {
		TL.Exit();
	}
}//service



}//class Report1D
