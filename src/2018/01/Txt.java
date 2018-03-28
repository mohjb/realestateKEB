package dev201803;
import java.sql.SQLException;
import java.util.Date;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dev201801.Srvlt;
import dev201801.Srvlt.HttpMethod;
import dev201801.TL.Json;
import dev201801.Srvlt.TL;
import dev201801.Srvlt.DB;
import dev201801.Srvlt.Util;

public class Txt extends DB.Tbl<String>{
	public static final String dbtName="Txt";
	public enum C implements DB.Tbl.CI{key,txt,logTime;
		@Override public Field f(){return Co.f(name(), Txt.class);}
		@Override public String getName(){return name();}
		@Override public Class getType(){return String.class;}
	}//enum C

		@F public String key,txt;
		@F public Date logTime;
	public String getName(){return dbtName;}
	@Override public C[]columns(){return C.values();}
	@Override public int pkcn(){return 1;}
	@Override public CI pkc(int i){return C.key;}
	@Override public CI[]pkcols(){C[]a={C.key};return a;}
	@Override public String pkv(int i){return key;}
	@Override public String[]pkv(String[]v){key=v[0];return v;}
	@Override public String[]pkvals(){String[]a={key};return a;}
	public Txt(){}
	public Txt(String key){this.key=key;}
	public Txt(String key,String txt){this.key=key;this.txt=txt;}


	@Override public List creationDBTIndices(TL tl){
		return Util.lst(Util.lst(
			"text  Primary Key NOT NULL DEFAULT 'home' "//key
			,"text NOT NULL DEFAULT 'home' "//txt
			,"timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"//logTime
			),Util.lst("key(`logTime`)")
		);}

	static{if(!registered.contains(Txt.class))registered.add(Txt.class);}

	public static Txt loadBy(String key){
		Txt j=(Txt)loadWhere(Txt.class,where(C.key,key ));
		return j;}

	public static Txt prmLoadByUrl(TL tl,String url){
		Txt j=loadBy( url );
		return j;}

	@HttpMethod public static List<String>
	listKeys( TL tl)throws Exception{
		Txt j=new Txt();
		List l=null;
		//for(DB.Tbl t:j.query(j.genSql(null,null).toString(),null			,false)) l.add(j.key)
		return l; }

	@HttpMethod public static List//propfind
	poll( @HttpMethod(prmBody = true)long logTime, TL tl)throws Exception{
		Txt j=new Txt();
		List l=Util.lst(  );
		//for(DB.Tbl t:j.query(j.genSql(null,logTime).toString(),where(C.logTime,logTime),true)) l.add( t );
		for ( Object x:j.query( where( Util.lst( Co.gt,C.logTime),logTime ) ) )
			l.add( j.clone() );
		return l;}

	StringBuilder genSql( List keysIn,Long logTime_poll){
		StringBuilder sql = new StringBuilder("select ");
		DB.Tbl.Co.generate(sql, columns());
		sql.append(" from `").append(dbtName)
			.append("` where `").append(C.key);
		if(keysIn == null)
			sql.append("`=`").append(dbtName).append("`.`").append(C.key).append('`');
		else
			DB.Tbl.Co.genList(sql.append("` in"), keysIn);
		if(logTime_poll!=null)
			sql.append( " and `"+C.logTime+"`>?" );
		return sql;}

	/**
	 * output to the client , Txt-Prm is loaded based on a key from the value of the url
	 * if the Txt-Prm is null then null is returned
	 * if the Txt-Prm txt-field is not a json-array/list
	 * , then the Txt-Prm txt-field is outputted, and return execution.
	 * if the Txt-Prm txt-field is a json-array/list L , then:
	 * the output is in the same order as the list-elements
	 * the list elements can be one of two
	 * 1- element is not a map, outputted as is
	 * 2- json-object m, and the object m can have three cases
	 *  2.2-having property ref and call
	 *  2.1-having property ref
	 *  2.3-having property eval , and optionally having ref
	 *  2.4-not having ref nor eval, in such a case the json-object m / list-element will be outputted
	 */
	@HttpMethod(useClassName = false) public static Txt
	get(@HttpMethod(prmLoadByUrl=true)Txt prm,TL tl)throws Exception {
		if(prm==null)return prm;
		Object js=Json.Prsr.parse( prm.txt );
		if(js instanceof List){List l=(List)js;
			for(Object element:l)
			{   boolean b=false;
				if(element instanceof Map)
				{Map m=(Map)element;Object ref=m.get( "ref" );
					if(ref!=null){
						js=m.get( "call" );
						if(b= js!=null)
							tl.o( call((String)js,(String)ref,(List)m.get("args"),tl) );
					}if(!b){
						js=m.get( "eval" );
						if(b= js!=null)
							tl.o( eval( (String)ref,(String)js,tl ) );
					}if(!b){
						Txt x=loadBy( (String)ref );
						if(b= x!=null)
							tl.o( x.txt );
					}
				}if(!b)
					tl.o( element );
			}
		}else
			tl.o( prm.txt );
		tl.h.r( "responseDone",true );
		return prm; }

	@HttpMethod public static List<Txt>//connect
	getKeys(@HttpMethod(prmUrlPart = true)String appName,
	        @HttpMethod(prmBody = true)List<String>keys){
		List<Txt>l=new LinkedList<>(  );
		Txt j=new Txt(appName);//Perm p=j.perm(Perm.Act.getKeys);
		//for(DB.Tbl t:j.query( j.genSql(keys,null).toString(),null,true)) l.add( (Txt ) t );
		return l;}

@Override public DB.Tbl save() throws Exception {	L.l( key,txt );	return super.save();}

@Override public int delete() throws SQLException {L.l( key,txt );	return super.delete();}

@HttpMethod(useClassName = false) public static Txt
	put( @HttpMethod(prmUrlRemaining= true)String key
		,@HttpMethod(prmBody= true)String v, TL tl)throws Exception{
		Txt x=new Txt(key,v);x.save();return x;}

	@HttpMethod public static Txt
	delete( @HttpMethod(prmLoadByUrl= true)Txt x , TL tl)throws Exception{
		x.delete();return x;}

	static javax.script.ScriptEngine eng(String key,boolean createIfNotInit,TL tl){
		String en="ScriptEngine.JavaScript."+key;
		javax.script.ScriptEngine e =(javax.script.ScriptEngine)tl.h.s( en );
		if(e==null&& createIfNotInit) {
			javax.script.ScriptEngineManager man=(javax.script.ScriptEngineManager)tl.h.a( "ScriptEngineManager" );
			if(man==null )
				tl.h.a( "ScriptEngineManager",man=new javax.script.ScriptEngineManager() );
			tl.h.s( en, e = man.getEngineByName( "JavaScript" ) );
			Txt j=loadBy( key );
			Map jm=Util.mapCreate(  );
			e.put( "key",key);e.put( "txt",j.txt);
		}
			e.put( "tl",tl);
		return e; }

	@HttpMethod public static Object//trace
	call(@HttpMethod(prmUrlPart= true)String m,
		@HttpMethod(prmUrlRemaining = true)String key,
		@HttpMethod(prmBody = true)List args,
		TL tl) throws Exception{
		javax.script.ScriptEngine e=eng(key,true,tl);
		e.put( "m",m);
		if(args!=null)e.put(  "args",args);
		return e.eval( key+"[m]"+(args==null?"":".call(args)") ); }

	@HttpMethod public static Object
	eval( @HttpMethod(prmUrlRemaining= true)String key,
		@HttpMethod(prmBody = true)String src,
		TL tl) throws Exception{// javax.script.ScriptException
		Txt j=loadBy(key);
		javax.script.ScriptEngine e=eng(key,true,tl);
		e.put( "src",src);
		return e.eval( src ); }

//public static class S extends dev201801.Srvlt {}

public static class L extends DB.Tbl<String>{
	public static final String dbtName="TxtLog";
	public enum C implements DB.Tbl.CI{key,txt,logTime;
		@Override public Field f(){return Co.f(name(), L.class);}
		@Override public String getName(){return name();}
		@Override public Class getType(){return String.class;}
	}//enum C

		@F public String key,txt;
		@F public Date logTime;
	public String getName(){return dbtName;}
	@Override public C[]columns(){return C.values();}
	@Override public int pkcn(){return 2;}
	@Override public CI pkc(int i){return i==0?C.key:C.logTime;}
	@Override public CI[]pkcols(){C[]a={C.key,C.logTime};return a;}
	@Override public String pkv(int i){return i==0?key:String.valueOf( logTime);}
	@Override public String[]pkv(String[]v){key=v[0];logTime=Util.parse( v[1],new Date() );return v;}
	@Override public String[]pkvals(){String[]a={key,String.valueOf( logTime)};return a;}
	public L(){logTime=new Date() ;}
	public L(String key){this.key=key;logTime=new Date() ;}
	public L(String key,String txt){this.key=key;logTime=new Date() ;this.txt=txt;}

	public static void l(String key,String v){L l=new L(key,v);try{l.save();}catch(Exception x){}}
	public static void l(Txt x){l(x.key,x.txt);}

	@Override public List creationDBTIndices(TL tl){
		return Util.lst(Util.lst(
			"text   NOT NULL DEFAULT 'home' "//key
			,"text NOT NULL DEFAULT 'home' "//txt
			,"timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"//logTime
			),Util.lst("unique(`key`,`logTime`)")
		);}

	static{if(!registered.contains(L.class))registered.add(L.class);}

}//class Log

}//class Txt
