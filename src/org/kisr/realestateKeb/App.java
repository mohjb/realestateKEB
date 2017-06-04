package org.kisr.realestateKeb;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import org.kisr.realestateKeb.TL.DB.Tbl;

public class App {


    /**represents a row in the `usr` mysql table ,
     * a sub class from TL.DB.Tbl,
     * hence has built-in methods to operate with
     * the mysql-table, like querying and and updating*/
    public static class Usr extends Tbl{
        public static final String Name="Usr";
        /**the attribute-name  in the session*/
        public final static String prefix=Name;

        //public Usr(){super(Name);}
        @Override public String getName(){return Name;}
        @F public Integer uid;
        public enum Gender{male,female}
        public enum Flags{eu,auth,admin}
        @F public Flags flags;
        @F public String un;
        @F(prmPw=true) public String pw;
        @F public String full,tel,email;
        @F public Date dob;
        @F public Gender gender;
        @F public URL img;

        /**load uid by un,pw , load from mysql*/
        public Integer loadUid()throws Exception{
            Object o=obj(C.uid, where(C.un,un,C.pw,pw));
            if(o==null)uid=null;else
            if(o instanceof Integer)uid=(Integer)o;else
            if(o instanceof Number)uid=((Number)o).intValue();
            else uid=Integer.parseInt(o.toString());
            return uid;}

        /**returns null if the credentials are invalid,
         * the credentials are username and password
         * which are read from the http-request
         * parameters "un" , "pw" */
        public static Usr login()throws Exception{//String un,String pw
            Usr u=new Usr();TL p=TL.tl();
            u.readReq(prefix+".");
            try{u.loadUid();}catch(Exception x){p.error("App.Usr.login:loadUid",x);}
            p.log("Xhr.Usr.login:u=",u);//n=",u.un," ,pw=",u.pw);
            if(u!=null&&u.uid!=null)u.load();
            else u=null;
            return u;}

        /**update the member variables , load from the mysql table*/
        public void onLogin()throws Exception{
            TL tl=TL.tl();TL.H h=tl.h;
            tl.usr=this;
            Ssn n=tl.ssn=new Ssn();
            n.sid=null;//0;
            n.uid=uid;
            n.auth=n.dt=
                    n.last=new Date();
            n.save();
            n.usr=this;
            h.s(Ssn.SessionAttrib,n);
            Object o=h.s(StrSsnTbls);
            Map<Class<? extends Tbl>,Tbl>
                    tbls=o instanceof Map?(Map)o:null;
            if(tbls==null)h.s(StrSsnTbls,tbls=new
                    java.util.HashMap<Class<? extends Tbl>,Tbl>());
            tbls.put(Usr.class, tl.usr);
            tbls.put(Ssn.class, n);}

        /**update the member variables , save to the mysql table*/
        public void onSignup()throws Exception{onLogin();save();}

        public enum C implements CI{uid,flags,un,pw,full,tel,email,dob,gender,img;
            public Class<? extends Tbl>cls(){return Usr.class;}
            public Class<? extends TL.Form>clss(){return cls();}
            public String text(){return name();}
            public Field f(){return Cols.f(name(), cls());}
            public Tbl tbl(){return Tbl.tbl(cls());}
            public void save(){tbl().save(this);}
            public Object load(){return tbl().load(this);}
            public Object value(){return val(tbl());}
            public Object value(Object v){return val(tbl(),v);}
            public Object val(TL.Form f){return f.v(this);}
            public Object val(TL.Form f,Object v){return f.v(this,v);}
        }//C

        public CI pkc(){return C.uid;}
        public Object pkv(){return uid;}
        public C[]columns(){return C.values();}

/*

 CREATE TABLE `usr` (
  `uid` int(6) NOT NULL AUTO_INCREMENT,
  `flags` set('eu','auth','admin') not null default 'eu',
  `un` varchar(255) NOT NULL,
  `pw` varchar(255) NOT NULL,
  `full` text ,
  `tel` text ,
  `email` text ,
  `dob` date,
  `gender` set('male','female'),
  `img` text ,
  PRIMARY KEY (`uid`),
  KEY `uk` (`un`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

insert into usr values
(1,'admin','admin',password('admin'),'admin','admin','admin','1/1/1','male','admin'),
(2,'auth','auth',password('auth'),'auth','auth','auth','1/1/1','male','auth'),
(3,'eu','eu',password('eu'),'eu','eu','eu','1/1/1','male','eu');
*/
    }//class Usr

    public static class Ssn extends Tbl implements Serializable{
        public static final String Name="session";
        static final String SessionAttrib="App."+Name;
        Usr usr;

        @Override public String getName(){return Name;}//public  Ssn(){super(Name);}
        @F public Integer sid,uid;
        @F public Date dt,auth,last;

        public void onLogout()throws Exception{
            TL tl=TL.tl();TL.H h=tl.h;tl.ssn=null;tl.usr=null;
            h.s(Ssn.SessionAttrib,null);
            h.getSession().setMaxInactiveInterval(1);}

        public static void onEnter(){
            TL tl=TL.tl();Object o=tl.h.s(Ssn.SessionAttrib);
            if(o instanceof Ssn){
                Ssn n=(Ssn)o;
                tl.ssn=n;tl.usr=n.usr;
                tl.ssn.last=tl.now;
                n.save(C.last);}}

        public enum C implements CI{sid,uid,dt,auth,last;
            public Class<? extends Tbl>cls(){return Ssn.class;}
            public Class<? extends TL.Form>clss(){return cls();}
            public String text(){return name();}
            public Field f(){return Cols.f(name(), cls());}
            public Tbl tbl(){return Tbl.tbl(cls());}
            public void save(){tbl().save(this);}
            public Object load(){return tbl().load(this);}
            public Object value(){return val(tbl());}
            public Object value(Object v){return val(tbl(),v);}
            public Object val(TL.Form f){return f.v(this);}
            public Object val(TL.Form f,Object v){return f.v(this,v);}

        }//C

        @Override public CI pkc(){return C.sid;}
        @Override public Object pkv(){return sid;}
        @Override public C[]columns(){return C.values();}

/*

 CREATE TABLE `ssn` (
  `sid` int(6) NOT NULL AUTO_INCREMENT,
  `uid` int(6) NOT NULL ,
  `dt` timestamp not null,
  `auth` timestamp,
  `last` timestamp not null,
  PRIMARY KEY (`sid`),
  KEY `kDt` (`dt`),
  KEY `kAuth` (`auth`),
  KEY `kLast` (`last`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
*/
    }//class Ssn

    public static class DataTbl extends Tbl{
        public static final String Name="d";
        @Override public String getName(){return Name;}//public DataTbl(){super(Name);}

        @F public Integer no,y,m,w,name;
        @F public String block;
        @F public Float area;
        @F public Integer typ;
        @F public Float price;
        @F public Integer gov;
        @F public String notes;
        @F public Integer contract;
        @F public Date d;

//@Override public Cat query(Object[]where){return super.query(where);}

        public enum C implements CI{no,y,m,w,name,block
            ,area,typ,price,gov,notes,contract,d;
            public Class<? extends Tbl>cls(){return DataTbl.class;}
            public Class<? extends TL.Form>clss(){return cls();}
            public String text(){return name();}
            public Field f(){return Cols.f(name(), cls());}
            public Tbl tbl(){return Tbl.tbl(cls());}
            public void save(){tbl().save(this);}
            public Object load(){return tbl().load(this);}
            public Object value(){return val(tbl());}
            public Object value(Object v){return val(tbl(),v);}
            public Object val(TL.Form f){return f.v(this);}
            public Object val(TL.Form f,Object v){return f.v(this,v);}

        }//C

        @Override public CI pkc(){return C.no;}
        @Override public Object pkv(){return no;}
        @Override public C[]columns(){return C.values();}

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

        /**read from the database the minimum-year-intValue and the maximum-year-intValue,
         where index 0 is minimum , and index 1 is a maximum*/
        public static int[]minmaxYear()throws java.sql.SQLException
        {	TL tl=TL.tl();TL.H h=tl.h;final String str="realestateKeb.minmaxYear";
            int[]r=(int[])h.a(str);
            if(r==null)try{
                String sql="select min(year("+C.d+
                        ")) , max(year("+C.d+")) from "+Name;
                tl.log("App.DataTbl.minmaxYear:sql=",sql);
                Object[ ]a=TL.DB.q1row(sql);// group by year(`"+C.d+"`)");
                if(a!=null){int[]x={((Number)a[0]).intValue()
                        ,((Number)a[1]).intValue()};r=x;}
                h.a(str,r);}catch(Exception x)
            {tl.error("App.DataTbl.minmaxYear:", x);
                if(r==null){int[]a={2000,2015};r=a;}
            }return r;}

    }//class DataTbl

    /**lookup*/
    public static class LookupTbl extends Tbl{
        public static final String Name="t";
        @Override public String getName(){return Name;}//public LookupTbl(){super(Name);}

        @F public Integer no;
        @F public Col col;
        @F public Integer code;
        @F public String text;

        LookupTbl copy(){return new LookupTbl().set(no, col, code, text);}

        LookupTbl set(Integer n,Col c,Integer d,String x){no=n;col=c;code=d;text=x;return this;}

        public enum Col{gov,type,name,label,labelEn};
        public enum C implements CI{no,col,code,text;
            public Class<? extends Tbl>cls(){return LookupTbl.class;}
            public Class<? extends TL.Form>clss(){return cls();}
            public String text(){return name();}
            public Field f(){return Cols.f(name(), cls());}
            public Tbl tbl(){return Tbl.tbl(cls());}
            public void save(){tbl().save(this);}
            public Object load(){return tbl().load(this);}
            public Object value(){return val(tbl());}
            public Object value(Object v){return val(tbl(),v);}
            public Object val(TL.Form f){return f.v(this);}
            public Object val(TL.Form f,Object v){return f.v(this,v);}

        }//C

        @Override public CI pkc(){return C.no;}
        @Override public Object pkv(){return no;}
        @Override public C[]columns(){return C.values();}
/*
CREATE TABLE `t` (
  `no` int(6) NOT NULL AUTO_INCREMENT,
  `col` set('gov','type','name','label','labelEn') NOT NULL DEFAULT 'label',
  `code` int(4) NOT NULL,
  `text` text NOT NULL,
  PRIMARY KEY (`no`),
  KEY `cc` (`col`,`code`)
) ENGINE=MyISAM AUTO_INCREMENT=305 DEFAULT CHARSET=utf8;
 */


        public static Map<Col,Map<Integer,LookupTbl>> lookup(){
            TL p=TL.tl();TL.H h=p.h;Object o=h.a(LookupTbl.class);
            Map<Col,Map<Integer,LookupTbl>>m=o==null?null:(
                    Map<Col,Map<Integer,LookupTbl>>)o;
            if(m==null)try{LookupTbl l=new LookupTbl();
                h.a(LookupTbl.class,m=new HashMap<Col,Map<Integer,LookupTbl>>());
                for(TL.DB.Tbl i:l.query(TL.DB.Tbl.where())){
                    p.log("App.LookupTbl.lookup:1:",i.toJson());
                    Map<Integer,LookupTbl>n=m.get(l.col);
                    if(n==null)
                        m.put(l.col,n=new HashMap<Integer,LookupTbl>());
                    LookupTbl t=n.get(l.code);
                    if(t==null || t.no>l.no)
                        n.put(l.code, l.copy());
                }//for
                //p.log("App.LookupTbl.lookup:ex:",m);
            }//ifm==null
            catch(Exception x){p.error("App.LookupTbl.lookup:ex:", x);
                if(m==null)
                    m=new HashMap<Col,Map<Integer,LookupTbl>>();
            }return m;}

    }//class LookupTbl

}//class App
