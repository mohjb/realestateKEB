package org.kisr.realestateKeb;

import javax.servlet.GenericServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.awt.Color;
import javax.imageio.ImageIO				;
import java.util.List;
import java.util.LinkedList					;
import org.jfree.chart.JFreeChart			;
import org.jfree.chart.ChartFactory			;
import org.jfree.chart.plot.PlotOrientation	;
import org.jfree.chart.plot.MultiplePiePlot	;
import org.jfree.chart.plot.CategoryPlot	;
import org.jfree.util.TableOrder			;

import org.jfree.data.category.DefaultCategoryDataset	;
import org.jfree.chart.renderer.category.CategoryItemRenderer	;

import org.jfree.data.category.CategoryDataset;

public class Chart {

    public static void service(
        GenericServlet srvlt
        ,HttpServletRequest request
        ,HttpServletResponse response)
    {TL tl=null;try{tl=TL.Enter(srvlt, request, response,null);//TL.H h=tl.h;
        response.setContentType("image/png");
        String reqPrefix=tl.h.req("prefix");
        if(reqPrefix==null)
            reqPrefix=Report1C.jspName;//"report1.jsp";
        Chart.Model mod=(Chart.Model)tl.h.s(reqPrefix+Chart.Model.class);

        String chartId=tl.h.req("id");
        Model.Chrt c=mod.chrts.get(TL.Util.parseInt(chartId, 0));

        int w=420,h=200;
        try{//int[]a=(int[])app(pageContext,C.sz.toString());
            int[]a=null;//intArrayConf(C.sz.toString());//parseSz(prmConf(pageContext,C.sz));
            int n=a==null?0:a.length;
            if(n>0){w=a[0]<=0?w:a[0];if(n>1)h=a[1];}
        }catch(Exception ex){ex.printStackTrace();}
        w=tl.h.req("width" , w);
        h=tl.h.req("height", h);

        Color[]colors=null;//getColors();

        //try{colors=(Color[])app(pageContext,C.colors.toString());}catch(Exception ex){ex.printStackTrace();}

        JFreeChart chart=c.chartType.cat(tl,c,c.catDataset());

        try{if(colors!=null)
        {if(c.chartType==CT.MultiplePie
                ||c.chartType==CT.MultiplePie3D)
        {MultiplePiePlot p=(MultiplePiePlot)chart.getPlot();}
        else
        {	CategoryPlot plot=chart.getCategoryPlot();
            CategoryItemRenderer r=plot.getRenderer();
            for(int i=0;i<colors.length;i++)
                r.setSeriesPaint(i, colors[i]);}}
        }catch(Exception ex){ex.printStackTrace();}

        if(chart!=null)
            ImageIO.write
                    (chart.createBufferedImage(w,h)
                            ,"png"
                            , response.getOutputStream()
                    )
                    ;
    }catch(Throwable ex){tl.error("org.kisr.realestateKeb.Chart:Throwable ex:", ex);}
    finally{try {TL.Exit();} catch (Exception e) {	e.printStackTrace();}}

    }//service

    final static String jspName="chart.jsp";



    //public final static String prefix="realestateKeb.conf.chart.";
    final static String prefix0="realestateKeb.", prefix=prefix0+"conf.",cPrefix=prefix0+"chart.";

    /**chart properties*/
    enum C{style,chartType,chartTitle
        ,valuesLabel,categoryLabel
        ,by_row,orientation
        ,cats,legend,colors,sz//,width,height
        ;public String toString(){String r=super.toString();return cPrefix+r;}
        ;public String str(){return super.toString();}
    }//enum C
    /**chart types*/
    public enum CT{
        //CategoryDataset
        Area(CategoryDataset.class,true)
        ,Bar(CategoryDataset.class,true)
        ,Bar3D(CategoryDataset.class,true)
        ,Line(CategoryDataset.class,true)
        ,Line3D(CategoryDataset.class,true)
        ,MultiplePie(CategoryDataset.class)
        ,MultiplePie3D(CategoryDataset.class)
        ,StackedArea(CategoryDataset.class)
        ,StackedBar(CategoryDataset.class)
        ,StackedBar3d(CategoryDataset.class)
        ,Waterfall(CategoryDataset.class)
        ;

        CT(Class p){c=p;pub=false;}
        public Class c;public boolean pub;

        CT(Class p, boolean b){c=p;pub=b;}

        JFreeChart cat(TL tl,Model.Chrt c,CategoryDataset dataset)
        {JFreeChart chart=null;
            switch(this){
                case Bar3D:
                    chart=ChartFactory.createBarChart3D(c.chartTitle,c.catLabel
                            ,c.valLabel,dataset,c.orientation,c.legend,false,false);
                    break;
                case Line3D:chart=
                        ChartFactory.createLineChart3D
                                (c.chartTitle//chartTitle!=null?chartTitle:(new java.util.Date()).toString()//java.lang.String title,
                                        ,c.catLabel//	java.lang.String categoryAxisLabel,
                                        ,c.valLabel//"val"//	java.lang.String valueAxisLabel,
                                        ,dataset//	CategoryDataset dataset,
                                        ,c.orientation//PlotOrientation orientation,
                                        ,c.legend//true//		boolean legend,
                                        ,false//		boolean tooltips,
                                        ,false)//	boolean urls)
                ;break;
                case Bar:chart=
                        ChartFactory.createBarChart(c.chartTitle,c.catLabel
                                ,c.valLabel,dataset,c.orientation,c.legend,false,false)
                ;break;
                case Area:chart=
                        ChartFactory.createAreaChart(c.chartTitle,c.catLabel
                                ,c.valLabel,dataset,c.orientation,c.legend,false,false)
                ;break;
                case MultiplePie:chart=
                        ChartFactory.createMultiplePieChart(c.chartTitle,dataset,
                                tl.h.req("by_row")!=null?TableOrder.BY_ROW:TableOrder.BY_COLUMN
                                ,c.legend,false,false)//Creates a chart that displays multiple pie plots.
                ;break;
                case MultiplePie3D:chart=
                        ChartFactory.createMultiplePieChart3D(c.chartTitle,dataset
                                ,tl.h.req("by_row")!=null?TableOrder.BY_ROW:TableOrder.BY_COLUMN
                                , c.legend,false,false)//Creates a chart that displays multiple pie plots.
                ;break;
                case StackedArea:chart=
                        ChartFactory.createStackedAreaChart(c.chartTitle,c.catLabel
                                ,c.valLabel,dataset,c.orientation,c.legend,false,false)
                ;break;
                case StackedBar:chart=
                        ChartFactory.createStackedBarChart(c.chartTitle,c.catLabel,c.valLabel
                                ,dataset,c.orientation,c.legend,false,false)
                ;break;
                case StackedBar3d:chart=
                        ChartFactory.createStackedBarChart3D(c.chartTitle,c.catLabel,c.valLabel
                                ,dataset,c.orientation,c.legend,false,false)
                ;break;
                case Waterfall:chart=
                        ChartFactory.createWaterfallChart(c.chartTitle,c.catLabel,c.valLabel
                                ,dataset,c.orientation,c.legend,false,false)
                ;break;
                case Line:default:chart=
                        ChartFactory.createLineChart(c.chartTitle,c.catLabel,c.valLabel
                                ,dataset,c.orientation,c.legend,false,false);

            }//switch

            return chart;

        }//cat


    }//enum CT


    /**list of charts for one user paramaters-set*/
    public static class Model{
        public List<Chrt>chrts=new LinkedList<Chrt>();
        public int termBase,termInc;

        public Chrt newChrt(String ttl){
            Chrt m=new Chrt( ttl);
            m.id=chrts.size();
            chrts.add(m);
            return m;}

        /**data-model for one chart*/
        public class Chrt{
            public CT chartType= CT.Line;public int id;
            public PlotOrientation orientation=PlotOrientation.VERTICAL;
            public String chartTitle=""
                    ,valLabel="",catLabel="",style="style1",by_row="";
            public boolean legend;

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

            public class Var{String ttl;int id;Var prv,nxt;Var(String t){ttl=t;}
                public List<Double>series=new LinkedList<Double>();
            }//class Var

            CategoryDataset catDataset()
            {DefaultCategoryDataset dataset=new DefaultCategoryDataset();
                Chrt c=this;Model mod=Model.this;
                Chrt.Var v=head==null?var:head;
                while(v!=null)
                {int x=0,n=c.var.series.size();
                    while(x<n)
                    {String ch= mod.termInc<0
                            ?(mod.termInc==-1?String.valueOf(mod.termBase-x)
                            :mod.termInc==-4?String.valueOf(mod.termBase-(x/4))+"-"+((x%4)+1)//:cats[1]==-4?String.valueOf(cats[0]-(x/4))+"-"+((x%4)+1)
                            :String.valueOf(mod.termBase-(x/mod.termInc))+"-"+((x%(-mod.termInc))+1)
                    )
                            :(mod.termInc==1?String.valueOf(mod.termBase+x)
                            :String.valueOf(mod.termBase+(x/mod.termInc))+"-"+((x%mod.termInc)+1)
                    );
                        dataset.addValue(v.series.get(x++),v.ttl,ch);
                    }v=head!=null?v.nxt:null;
                }return dataset;
            }

        }//Chrt
    }//class Model

}//class Chart
