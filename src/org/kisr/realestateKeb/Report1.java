package org.kisr.realestateKeb;

import javax.servlet.*;

import org.kisr.realestateKeb.App.LookupTbl;
import org.kisr.realestateKeb.App.DataTbl;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Map;

public class Report1 {

    final static String jspName="report1.jsp",chartName="chart.jsp";

    static void reset(String[][]a){for(int i=0;i<a.length;i++)for(int j=0;j<a[i].length;j++)a[i][j]="";}

    static String filterNum(String p)
    {if(p==null)return p;String r=p.replaceAll(",","");
        if(r.endsWith(".0"))r=r.substring(0,r.length()-2);return r;}

    static void htmlTable(String[][]a
            ,String ttl//,LookupTbl.Col col,int coli//strsJ
            ,String trAttribs,int yr,Term term,Chart.Model mod
    )throws Exception
    {	//String n=LookupTbl.lookup().get(col).get(coli).text;//strs[strsI][strsJ];
        final int termBase=term.base,oneSqrMtrPriceIndex=1;
        String[]oneSqrMtrPrice=a[oneSqrMtrPriceIndex];
        Chart.Model.Chrt c0=mod.chrts.get(0), v=mod.newChrt(ttl);
        v.var=c0.newVar(ttl);
        TL.out("\n<tr ",trAttribs,"><th rowspan=\"",a.length,"\">",ttl
                ,"<img src=\"",chartName,"?id=",v.id);//cats="+cats+"&series="
        for(int j=oneSqrMtrPrice.length-1;j>=0;j--)
        {String s1=filterNum(oneSqrMtrPrice[j]);
            double d=Double.NaN;
            try{d=Double.parseDouble(s1);}catch(Exception x){}
            v.var.series.add(d);
        }
        TL.out("\"/></th>");
        for(int i=0;i<a.length;i++)
        {	if(i>0)
            TL.out("\n<tr ",trAttribs,">"
            ,"<th>",Lbl.sttscs[i],"</th>");
            for(int j=0;j<a[i].length;j++)
                TL.out(j%termBase==0//quart&&j%4==0
                                ?"<td style=\"background-color:#e0e0f0\""
                                :"<td"
                        ," title=\"",ttl,".\n",Lbl.sttscs[i],".\n"
                        , term==Term.y?String.valueOf(yr+j)
                                :term==Term.s||term==Term.q
                                ?(String.valueOf(yr+j/termBase)+".\n"+term.ar+" "+Lbl.ranks[j%termBase])
                                :(String.valueOf(yr+j/termBase)+".\n"+term.ar+" "+((j%termBase)+1))
                        ,"\">"
                        ,(a[i][j]==null||a[i][j].trim().length()<1
                                ?"&nbsp;"
                                :a[i][j]
                        ),"</td>\n"
                );
            TL.out("</tr>");
        }
    }//htmlTable()

    /**these are html-post-parameter names, so that the names are centeralized/have one spelling*/
    enum Prm{from,to//,from2,to2//,pTyp
        ,type,contract//,prop_typ,prop_desc,typ
        ,gov,terms;
        String v(String[]a){return a[ordinal()];}
    };


    enum Lbl{;
        enum S{
            عدد
            ,avgPrive("متوسط السعر")
            ,meanArea("متوسط المساحة")
            ,minArea("أصغر مساحة")
            ,maxArea("أكبر مساحة")
            ,totalVal("إجمالي قيمة التداول")
            ,min1MtrPrice("أقل سعر متر")
            ,max1MtrPrice("أعلى سعر متر")
            ;
            String str;
            S(){str=name();}
            S(String p){str=p;}
            @Override public String toString(){return str==null?name():str;}
        }
        enum Ranks{الأول
            ,
            الثاني
            ,
            الثالث
            ,
            الرابع
        }//enum Ranks

        enum Contrct{//Null
            all(0,"إجمالي العقود"),
            c1(1,"عقود مسجلة"),
            c2(2,"وكالات عقارية")
            ;

            public String str;public int v;
            Contrct(){str=name();}
            Contrct(int i,String p){str=p;v=i;}
            //@Override public String toString(){return str==null?name():str;}
        };//strs

        public static S[]sttscs=S.values();
        public static Ranks[]ranks=Ranks.values();
        public static Contrct[]contrcts=Contrct.values();
    }//enum Lbl

    enum Term{y(1,"سنة","سنوي")
        ,n(1,"9شهور","9شهور"
        )//	=9months
        ,s(2,"النصف","نصف سنوي")
        ,q(4,"الربع","ربع سنوي")
        ,m(12,"شهر","شهري")
        ,w(52,"اسبوع","اسبوعي");
        public int base;public String ar,l;
        Term(int b,String a,String lbl){base=b;ar=a;l=lbl;}

    }//enum Term

    public static void service(GenericServlet srvlt
            ,final javax.servlet.http.HttpServletRequest request
            , final javax.servlet.http.HttpServletResponse response)
            throws Exception
    {PrintWriter out = response.getWriter(); //new PrintWriter(System.out);
        response.setContentType("text/html; charset=utf-8");
        try{TL tl=TL.Enter(srvlt,request,response,out);//tl.logOut=TL.DB.dbLog=true;tl.comments=TL.CommentHtml;
            if (request.getCharacterEncoding() == null)
                request.setCharacterEncoding("UTF-8");


            Map<LookupTbl.Col,Map<Integer,LookupTbl>> lookup=LookupTbl.lookup();
            Prm[]pa=Prm.values();
            String p[]=new String[pa.length];
            int nullCol=-1;
            for(int i=0;i<p.length;i++){
                tl.log(jspName,":load Prms(",i,":",pa[i],")=",p[i]=tl.h.req(pa[i].toString()));
                if(p[i]==null)nullCol=i;}


            int[]minmaxYear=DataTbl.minmaxYear();

            int from=TL.Util.parseInt(Prm.from.v(p),minmaxYear[0])
                    ,to=TL.Util.parseInt(Prm.to.v(p),minmaxYear[1]);

            if(from>to){int tmp=from;from=to;to=tmp;String temp=p[0];p[0]=p[1];p[1]=temp;}

            Term term=Prm.terms.v(p)==null?Term.y:Term.valueOf(Prm.terms.v(p));
            Lbl.Contrct contrct=Prm.contract.v(p)==null?Lbl.Contrct.all:Lbl.Contrct.valueOf(Prm.contract.v(p));

            boolean allGovs="0".equals(Prm.gov.v(p))
                    ,allTyps="0".equals(Prm.type.v(p));
            LookupTbl.Col col=allGovs
                    ?LookupTbl.Col.gov
                    :LookupTbl.Col.name;

            Map<Integer,LookupTbl>typs=lookup.get(LookupTbl.Col.type)
                    ,govs=lookup.get(LookupTbl.Col.gov);
            int n2=typs.size();

/*tl.logo( jspName,":checkpoint1: minmax=",minmaxYear
	," ,p=",p," ,nullCol=",nullCol
	," from=",from," ,to=",to
	," ,allGovs=",allGovs
	," ,allTyps=",allTyps
	," ,term=",term
	," ,contrct=",contrct);*/


            tl.out("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n"
                ,"<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n"
            ,"<head>\r\n"
            ,"\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r\n"
            ,"\t<title>Real-Estate</title>\r\n"
            ,"</head>\r\n"
            ,"<body dir=\"rtl\" style=\"background-image:url(../../../bg.jpg); background-repeat:repeat-x\">\r\n"
            ," <div><table width=\"99%\" onclick=\"window.location='index.jsp'\"><tr>\r\n"
            ,"\t\t<td><img style=\"border:3px black double\" src=\"../../../kbu-logo.png\"/></td>\r\n"
            ,"\t\t<td><h1>Ø¨ÙŠØ§Ù†Ø§Øª ØªØ¯Ø§ÙˆÙ„ Ø§Ù„Ø¹Ù‚Ø§Ø± Ù�ÙŠ Ø¯ÙˆÙ„Ø© Ø§Ù„ÙƒÙˆÙŠØª</h1></td>\r\n"
            ,"\t\t<td><img src=\"../../../kisrlogo.gif\"/></td></tr>\r\n"
            ,"\t</table>\r\n"
            ,"\t<div style=\"border:3px black double\">\r\n"
            ,"\t\t<form method=\"post\" action=\""
            ,jspName
            ,"\">\r\n"
            ,"\t\t\t<table width=\"75%\" border=\"0\" align=\"center\">\r\n"
            ,"\t\t\t  <tr><th>Ù…Ù†</th><th>Ø§Ù„Ù‰</th>\r\n"
            ,"\t\t\t\t<td rowspan=\"2\">");
            for(Term t:Term.values()){
                tl.out("\r\n"
                ,"\t\t\t\t\t<input type=\"radio\" name=\""
                ,Prm.terms
                ,"\" value=\""
                ,t
                ,'"'
                ,' '
                , term==t?"checked":""
                ,'/'
                ,'>'
                ,t.l
                ,"<br/>");
            }
            tl.out("\r\n"
            ,"\t\t\t\t</td>\r\n"
            ,"\t\t\t\t<td rowspan=\"2\">");

            for(Lbl.Contrct i:Lbl.contrcts)//String s=String.valueOf(i);
            {
                tl.out("\t\t\t<input type=\"radio\" name=\""
                ,Prm.contract
                ,"\" \r\n"
                ,"\t\t\t\t\t\tvalue=\""
                ,i
                ,'"'
                ,' '
                , i==contrct?"checked":""
                ,'/'
                ,'>'
                , i.str
                ,"<br/>");

            }
            tl.out("\t\t\t</td>\r\n"
            ,"\t\t\t\t<th>Ù…Ø­Ø§Ù�Ø¸Ø©</th>\r\n"
            ,"\t\t\t\t<th>Ù†ÙˆØ¹ Ø§Ù„Ø¹Ù‚Ø§Ø±</th>\r\n"
            ,"\t\t\t\t"
            ,"\r\n"
            ,"\t\t\t\t<td rowspan=\"2\"><input type=\"submit\" value=\"Ø¨Ø­Ø«\"/></td>\r\n"
            ,"\t\t\t  </tr>\r\n"
            ,"\t\t\t  <tr>\r\n"
            ,"\t\t\t\t<td>\r\n"
            ,"\t\t\t\t\t<select name=\""
            ,Prm.from
            ,'"'
            ,' '
            ,'>');

            for(int i=minmaxYear[0];i<=minmaxYear[1];i++)
            {	String s=String.valueOf(i);
                tl.out("\r\n"
                ,"\t\t\t\t\t\t<option "
                , s.equals(Prm.from.v(p))?" selected":""
                ,'>'
                ,s
                ,"</option>");

            }
            tl.out("\r\n"
            ,"\t\t\t\t\t</select>\r\n"
            ,"\t\t\t\t</td>\r\n"
            ,"\t\t\t\t<td><select name=\""
            ,Prm.to
            ,'"'
            ,' '
            ,'>');

            for(int i=minmaxYear[0];i<=minmaxYear[1];i++)
            {	String s=String.valueOf(i);
                tl.out("\r\n"
                ,"\t\t\t\t\t<option "
                , s.equals(Prm.to.v(p))
                        ||(Prm.to.v(p)==null&&i==minmaxYear[1])
                        ?" selected":""
                ,'>'
                , s
                ,"</option>");

            }
            tl.out("\t\t\t</select>\r\n"
            ,"\t\t\t\t</td>\r\n"
            ,"\t\t\t\t<td><select name=\""
            ,Prm.gov
            ,"\" ><option value=\"0\" "
            , allGovs?"selected":""

            ,'>'
            , govs.get(0).text
            ,"</option>");

            int iGov=col.ordinal();
            for ( int i = 1 ; i < govs . size ( ) ;i++)//if(i!=1)
            {	LookupTbl g=govs.get(i);
                String s=String.valueOf(g.code);//i
                boolean b= !allGovs&&
                        (	(s==null&&Prm.gov.v(p)==null)
                                ||(s!=null&&s.equals(Prm.gov.v(p))
                        )
                        );
                if(b)
                    iGov=i;
                tl.out("\r\n"
                ,"\t\t<option value=\""
                ,s
                ,'"'
                ,' '
                , b?"selected":""
                ,'>'
                , govs.get(i).text
                ,"</option>");

            }

            tl.out("\r\n"
            ,"\t\t\t\t\t</select>\r\n"
            ,"\t\t\t\t</td>\r\n"
            ,"\t\t\t\t<td><select name=\""
            ,Prm.type
            ,'"'
            ,' '
            ,'>');

            for(int i=0;i < typs.size() ;i++)
            {	LookupTbl g=typs.get(i);
                String s=String.valueOf(g.code);
                tl.out("\r\n"
                ,"\t\t<option value=\""
                ,s
                ,'"'
                ,' '
                ,
                        (	s==null &&
                                Prm.type.v(p)==null
                        )
                                ||(
                                s!=null &&
                                        s.equals(Prm.type.v(p))
                        )
                                ?"selected=\"true\""
                                :""

                ,'>'
                , g.text
                ,"\r\n"
                ,"\t\t</option>");

            }
            tl.out("\r\n"
            ,"\t\t\t\t\t</select>\r\n"
            ,"\t\t\t\t</td>\r\n"
            ,"\t\t\t  </tr>\r\n"
            ,"\t\t\t</table>\r\n"
            ,"\t\t</form>\r\n"
            ,"\t</div>\r\n"
            ," </div><hr/>\r\n"
            ," <table border=\"1\" rules=\"all\">");

            if(term!=Term.y&&term!=Term.n)
            {
                tl.out("\r\n"
                ,"\t<colgroup span=\"2\" style=\" width:250px; border:3px black double\"/>");

                for(int yr=from;yr<=to;yr++)
                {
                    tl.out("\r\n"
                    ,"\t<colgroup span=\""
                    ,term.base
                    ,"\" style=\" border:3px black double\"/>");

                }
            }
            tl.out("\r\n"
            ,"  <tr>\r\n"
            ,"\t<th width=\"200px\"");

            if(term!=Term.y&&term!=Term.n){
                tl.out(" rowspan=\"2\"");
            }
            tl.out('>');

            if(allGovs){
                tl.out("Ù…Ø­Ø§Ù�Ø¸Ø©");
            }else{
                tl.out("Ù…Ù†Ø·Ù‚Ø©");
            }
            tl.out("</th>\r\n"
            ,"\t<th");

            if(term!=Term.y&&term!=Term.n){
                tl.out(" rowspan=\"2\"");
            }

            tl.out(">ÙˆØµÙ�</th>");

            for(int yr=from;yr<=to;yr++)
            {
                tl.out("\r\n"
                ,"\t <th");
                if(term!=Term.y&&term!=Term.n)
                {
                    tl.out(" colspan=\""
                    ,term.base
                    ,'"');

                }
                tl.out(">Ø³Ù†Ø© "
                ,yr
                ,"</th>");

            }
            tl.out("\r\n"
            ,"  </tr>");

            if(term!=Term.y&&term!=Term.n)
            {
                tl.out("\r\n"
                ,"  <tr>");

                for(int i=0;i<(to-from+1)*term.base;i++)
                {
                    tl.out("\r\n"
                    ,"\t<th>"
                    ,term.ar
                    ,' '
                    ,
                            term==Term.s||term==Term.q
                                    ?Lbl.ranks[ i%term.base]
                                    :((i%term.base)+1)
                    ,"\r\n"
                    ,"\t</th>");

                }
                tl.out("\r\n"
                ,"  </tr>");

            }

            if(nullCol<0)try
            {LinkedList<Object>prms=null;
                Chart.Model mod=new Chart.Model();
                tl.h.s(jspName+Chart.Model.class,mod);
                mod.termBase=to;mod.termInc=-term.base;
                Chart.Model.Chrt c0=mod.newChrt(lookup.get(
                        LookupTbl.Col.gov).get(allGovs?0:iGov) .text);

                //tl.logo(jspName,"nullCol<0");
                StringBuilder sql=new StringBuilder("select ")
                        .append(allGovs?DataTbl.C.gov:DataTbl.C.name).append(",");
                switch(term){
                    case s:sql.append( "(year(`").append(DataTbl.C.d).append("`)-").append(from).append(")* 2+ceiling(month(`").append(DataTbl.C.d).append("`)/6)-1 as t");break;
                    case q:sql.append( "(year(`").append(DataTbl.C.d).append("`)-").append(from).append(")* 4+ceiling(month(`").append(DataTbl.C.d).append("`)/3)-1 as t");break;
                    case m:sql.append( "(year(`").append(DataTbl.C.d).append("`)-").append(from).append(")*12+month(`").append(DataTbl.C.d).append("`)-1 as t");break;
                    case w:sql.append( "(year(`").append(DataTbl.C.d).append("`)-").append(from).append(")*52+week (`").append(DataTbl.C.d).append("`)-1 as t");break;
                    default:sql.append( "year(`").append(DataTbl.C.d).append("`)-").append(from).append(" as t");}
                sql.append(",count(*)")
                        .append(",format(sum(`").append(DataTbl.C.price).append("`)")
                        .append("/sum(`").append(DataTbl.C.area).append("`),1)")
                        .append(",format(avg(`").append(DataTbl.C.area).append("`),1) ")
                        .append(",format(min(`").append(DataTbl.C.area).append("`),1)")
                        .append(",format(max(`").append(DataTbl.C.area ).append("`),1)")
                        .append(",format(sum(`").append(DataTbl.C.price).append("`),3)")
                        .append(",format(min(`"
                        ).append(DataTbl.C.price).append("`/`"
                ).append(DataTbl.C.area ).append("`),4)")
                        .append(",format(max(`"
                        ).append(DataTbl.C.price).append("`/`"
                ).append(DataTbl.C.area ).append("`),4) ")
                        .append("from ").append(DataTbl.Name) //+DBInfo[5]+
                        .append(" where year(`"  ).append(DataTbl.C.d)
                        .append("`)>=? and year(`").append(DataTbl.C.d).append("`)<=?");
                if(contrct!=Lbl.Contrct.all)
                    sql.append("and `").append(DataTbl.C.contract).append("`=? ");
                if(!allGovs)
                    sql.append("and `").append(DataTbl.C.gov).append("`=? ");
                if(term==Term.n)
                    sql.append(" and month(`"+DataTbl.C.d+"`)<=9");
                sql.append(" group by ").append(
                        allGovs?DataTbl.C.gov:DataTbl.C.name).append(",t");
                prms=new LinkedList<Object>();//tl.log(jspName,"sql:",sql);
                prms.add(0);prms.add(p[Prm.from		.ordinal()]);
                prms.add(0);prms.add(p[Prm.to		.ordinal()]);
                if(contrct!=Lbl.Contrct.all){prms.add(0);prms.add(p[Prm.contract.ordinal()]);}
                if(!allGovs){prms.add(0);prms.add(p[Prm.gov		.ordinal()]);}
                if(!allTyps){prms.add(0);prms.add(p[Prm.type	.ordinal()]);}

                int row=0,currentName=(to-from+1)*term.base;
                String tbl[][]=new String[ Lbl.sttscs.length ][];
                for(int i=0;i<tbl.length;i++)
                    tbl[i]=new String[currentName];
                currentName=-1;
                reset(tbl);Object[]prmA=new Object[prms.size()];
                prms.toArray(prmA);
                for(TL.DB.ItTbl.ItRow ir:new TL.DB.ItTbl(sql.toString(), prmA))
                {	int nm=ir.nextInt(),yr=ir.nextInt();//rs.getInt(1),yr=rs.getInt(2);
                    if(nm!=currentName)
                    {	if(row++>0)
                    {	htmlTable
                            (tbl,lookup.get(col).get(currentName).text
                                    ,"style=\"background-color:"+
                                            ((row%2)==0?"#eef":"#f8f8ff")+"\""
                                    ,from,term,mod);
                        reset(tbl);
                    }//tl.log(jspName,":for-db-iterator:currentName=",currentName,",nm=",nm);
                        currentName=nm;
                    }
                    for(int c=0;c<tbl.length;c++)
                        //try{rs.getString(c+3);}catch(Exception ex){TL.error(jspName+":setPrms:",ex);}
                        tbl[c][yr]=ir.nextStr();
                    //tl.logo(jspName,":for-db-iterator:tbl(c=[0-",c,"), yr=",yr,")=",tbl);
                }
                if(row>0)htmlTable
                        (tbl,lookup.get(col).get(currentName).text
                                ,"style=\"background-color:"+
                                        ((row%2)==0?"#eef":"#f8f8ff")+"\""
                                ,from,term,mod);

                tl.out("</table>");

                //if(series!=null)
                {c0.legend=true;c0.chartType=Chart.CT.Bar;
                    tl.out("\r\n"
                    ,"\t<img src=\""
                    ,chartName
                    ,"?id="
                    , c0.id
                    ,"&width=1000"
                    ,'"'
                    ,'/'
                    ,'>');
                }
            }//if(nullCol<0)
            catch(Throwable x){String end=tl.logo(jspName+":if(nullCol<0) Throwable:",x);tl.error(end);

                tl.out("<script>serverErrorTL="
                ,end
                ,"</script>");
            }

            tl.out("</body><script>");
            String end=tl.logo(TL.tl());
            tl.out("\r\n"
            ,"serverTL="
            ,end
            ,"  \r\n"
            ,"</script>\r\n"
            ,"</html>");
        }finally{TL.Exit();}
    }//service
}//class Report1
