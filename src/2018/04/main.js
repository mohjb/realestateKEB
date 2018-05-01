xUrl='/txtSrvlt/';//2017.11.jsp
(window.xa=(angular||{})
.module('main', ['ngSanitize','angular-md5','ui.router','ng.jsoneditor'] ))
.factory('main', ['$http','md5', function mainFactory($http,md5) {
	var p=main={//window.xa||{}
	dbNames:['test']
	,dbs:{test:{dbName:'test',comments:''
		,tbls:{test:{dbtName:'test',pkc:['test']
			,indx:[],fk:[],comments:''
			,cols:[{name:'test',clss:'text'}]}}}}
/*

List DB_names()throws SQLException

colTypes
int DB_create(@HttpMethod(prmUrlPart= true) String dbName)throws SQLException
int DB_drop(@HttpMethod(prmUrlPart= true) String dbName)throws SQLException

int DB_rename(@HttpMethod(prmUrlPart= true) String dbName
	,@HttpMethod(prmName= "newName") String newName)throws SQLException

MTbl DBTbl_create(@HttpMethod(prmUrlPart = true) String dbName
		,@HttpMethod(prmUrlPart=true) String dbtName
		,@HttpMethod(prmName="cols") List cols//<Map<String,String>>
		,@HttpMethod(prmName = "pk") List pk//<String>
		,@HttpMethod(prmName = "fk") Map fk//<String,Map<String,String>>
		,TL tl)

int DBTbl_drop(@HttpMethod(prmUrlPart = true) String dbName
		,@HttpMethod(prmUrlPart = true) String dbtName)


int	DBTbl_rename(@HttpMethod(prmLoadByUrl = true) MTbl t
		,@HttpMethod(prmName = "newName") String newName)throws SQLException

int DBTblCol_alter(@HttpMethod(prmUrlPart= true) String dbName
		,@HttpMethod(prmUrlPart= true) String dbtName
		,@HttpMethod(prmUrlPart= true) String col
	    ,@HttpMethod(prmUrlPart = true) String cm
		,@HttpMethod(prmBody = true) String def
		)throws Exception

Object[]get(@HttpMethod(prmLoadByUrl = true) MTbl.Row t)

Sql.Tbl insert(@HttpMethod(prmBody = true) MTbl.Row t) throws Exception

int update(@HttpMethod(prmLoadByUrl = true) MTbl t
	,@HttpMethod(prmBody = true) Map set)throws Exception

int	delete(@HttpMethod(prmLoadByUrl = true) MTbl.Row t
	,@HttpMethod(prmBody = true) List where) throws SQLException

List<Object[]>query(@HttpMethod(prmLoadByUrl = true) MTbl t
	,@HttpMethod(prmName= "cols") List cols
	,@HttpMethod(prmName = "where") List where
	,@HttpMethod(prmName = "groupBy") List groupBy
	,@HttpMethod(prmName = "orderBy") List orderBy) throws SQLException


List<Object[]>qry(@HttpMethod(prmName = "query") String sql
		,@HttpMethod(prmName = "where")List where)//,@HttpMethod(prmName = "page") String page,@HttpMethod(prmName = "pageSize") int pageSize

//each command is a method call where the list-item is a map which should have 
//	method:<str> , url:<str> , body:<map or str or list>
List cmnds(@HttpMethod(prmBody = true)List commands)
*/
	,DB_names:function(clb){
		console.log('main.DB_names',arguments);
		$http({method:'MTbl.DB_names',url:xUrl})
		.then(function (respond){console.log('main.DB_names.then',arguments);
				p.dbNames=respond.data['return']
				clb(p.dbNames);
			}
			,function (respond){
				console.log('main.DB_names.then:err',arguments);})
		}
	,DB_get:function(n,clb){console.log('main.DB_get',arguments);
		$http({method:'MTbl.DB_get',url:xUrl+n})
		.then(function (respond){console.log('main.DB_get.then:',n,arguments);
				p.dbs[n]=respond.data['return']
				clb(p.dbs[n]);
			}
			,function (respond){
				console.log('main.DB_get.then:err:',n,arguments);})
		}
	,dbColTypes:function(clb){console.log('main.dbColTypes',arguments);
		$http({method:'MTbl.colTypes',url:xUrl})
		.then(function (respond){console.log('main.dbColTypes.then:',arguments);
				p.colTypes=respond.data['return']
				clb(p.colTypes);
			}
			,function (respond){
				console.log('main.dbColTypes.then:err:',arguments);})
		}
	,DB_drop:function(n,clb){console.log('main.DB_drop',arguments);
		var i=p.dbNames.indexOf(n);if(i==-1)return;
		$http({method:'MTbl.DB_drop',url:xUrl+n})
		.then(function (respond){console.log('main.DB_drop.then:',n,arguments);
				if(!respond || !respond.data || !respond.data['return']|| respond.data['return']<1)
					return clb(n,respond);
				delete p.dbs[n]
				delete p.dbNames[i]
				clb(p.dbs[n]);
			}
			,function (respond){
				console.log('main.DB_drop.then:err:',n,arguments);})
		}
	,DB_create:function(dn,clb){console.log('main.DB_create',arguments);
		if(p.dbs[dn])
			return;
		$http({method:'MTbl.DB_create',url:xUrl+dn})
		.then(function (respond){console.log('main.DB_create.then:',dn,arguments);
				var d=p.dbs[dn]=respond.data['return'];
				if(d){
					if(!d.dbName)
						d.dbName=dn
					if(!d.tbls)
						d.tbls=[]
					if(!d.comments)
						d.comments='db:'+(new Date)+':'+dn
				}
				clb(d);
			}
			,function (respond){
				console.log('main.DB_create.then:err:',dn,arguments);})
		}
	,DB_rename:function(dn,newName,clb){console.log('main.DB_rename',arguments);
		var i=p.dbNames.indexOf(dn),d=p.dbs[dn];if(!d)
			return;
		$http({method:'MTbl.DB_rename',url:xUrl+dn,data:{newName:newName}})
		.then(function (respond){console.log('main.DB_rename.then:',dn,arguments);
				var r=respond.data['return'];
				delete p.dbs[dn];
				p.dbs[d.dbName=p.dbNames[i]=newName]=d;
				for(var tn in d.tbls)
					d.tbls[tn].dbName=newName;
				clb(r,d);}
			,function (respond){
				console.log('main.DB_rename.then:err:',dn,arguments);})
		}
	,DBTbl_create:function(dbName,dbtName,cols,pk,fk,inds,clb){console.log('main.DBTbl_create',arguments);
		$http({method:'MTbl.DBTbl_create',url:xUrl+dbName+'/'+dbtName,data:{cols:cols,pk:pk,fk:fk,inds:inds}})
		.then(function (respond){console.log('main.DBTbl_create.then:',dbName,arguments);
				var t=p.dbName[dbName].tbls[dbtName]=respond.data['return'];
				if(t){
					if(!t.dbName)
						t.dbName=dbName
					if(!t.dbtName)
						t.dbtName=dbtName
					if(!t.cols)
						t.cols=cols
					if(!t.pk)
						t.pk=pk
					if(!t.fk)
						t.fk=fk
					if(!t.inds)
						t.inds=inds
					if(!t.comments)
						t.comments='dbTbl:'+(new Date)+':`'+dbName+'`.`'+dbtName+'`'
				}
				clb(t);
			}
			,function (respond){
				console.log('main.DBTbl_create.then:err:',dbName,arguments);})
		}
	,DBTbl_drop:function(tbl,clb){console.log('main.DBTbl_drop',arguments);
		$http({method:'MTbl.DBTbl_drop',url:xUrl+tbl.dbName+'/'+tbl.dbtName})
		.then(function (respond){console.log('main.DBTbl_drop.then:',tbl,arguments);
				if(!respond || !respond.data || !respond.data['return']|| respond.data['return']<1)
					return clb(tbl,respond);
				delete p.dbs[tbl.dbName].tbls[tbl.dbtName]
				clb(respond.data['return'],tbl);
			}
			,function (respond){
				console.log('main.DBTbl_drop.then:err:',tbl,arguments);})
		}
	,DBTbl_rename:function(tbl,newName,clb){console.log('main.DBTbl_rename',arguments);
		$http({method:'MTbl.DBTbl_rename',url:xUrl+tbl.dbName+'/'+tbl.dbtName,data:{newName:newName}})
		.then(function (respond){console.log('main.DBTbl_rename.then:',tbl,arguments);
				var r=respond.data['return'];
				delete p.dbs[tbl.dbName].tbls[tbl.dbtName];
				p.dbs[tbl.dbName].tbls[tbl.dbtName=newName]=tbl;
				clb(r,tbl);}
			,function (respond){
				console.log('main.DBTbl_rename.then:err:',tbl,arguments);})
		}
	,DBTblCol_alter:function(tbl,col,cm,def,clb){console.log('main.DBTblCol_alter',arguments);
		$http({method:'MTbl.DBTblCol_alter',url:xUrl+tbl.dbName+'/'+tbl.dbtName+'/'+col+'/'+cm,data:{def:def}})
		.then(function (respond){console.log('main.DBTblCol_alter.then:',tbl,arguments);
				var r=respond.data['return'];//if cm == add , drop , change
				clb(r,tbl);}
			,function (respond){
				console.log('main.DBTblCol_alter.then:err:',tbl,arguments);})
		}
	,DBTblRow_get:function(tbl,pkv,clb){console.log('main.DBTblRow_get',arguments);
		$http({method:'MTbl.get',url:xUrl+tbl.dbName+'/'+tbl.dbtName+'/'+pkv.join('/')})
		.then(function (respond){console.log('main.DBTblRow_get.then:',tbl,arguments);
				var r=respond.data['return'];
				clb(r,tbl,pkv);}
			,function (respond){
				console.log('main.DBTblRow_get.then:err:',tbl,arguments);})
		}
	,nm2col:function(nm,tbl){
		for(var i in tbl.cols){var c=tbl.cols[i]
			if(nm==c.name)
				return c;
			}
			}
	,vals2pkv:function(tbl,vals){
		var r=[];for(var i in tbl.pkc){
			var c=p.nm2col(tbl.pkc[i]);r.push(vals[c.i]);}
		return r;}
	,DBTblRow_insert:function(tbl,vals,clb){console.log('main.DBTblRow_get',arguments);
		$http({method:'MTbl.get',url:xUrl+tbl.dbName+'/'+tbl.dbtName+'/'+p.vals2pkv(tbl,vals).join('/'),data:vals})
		.then(function (respond){console.log('main.DBTblRow_get.then:',tbl,arguments);
				var r=respond.data['return'];
				clb(r,tbl,pkv);}
			,function (respond){
				console.log('main.DBTblRow_get.then:err:',tbl,arguments);})
		}
	,DBTblRow_update:function(tbl,row,chngs,clb){console.log('main.DBTblRow_update',arguments);
		$http({method:'MTbl.update',url:xUrl+tbl.dbName+'/'+tbl.dbtName+'/'+p.vals2pkv(tbl,row).join('/'),data:chngs})
		.then(function (respond){console.log('main.DBTblRow_update.then:',tbl,arguments);
				var r=respond.data['return'];
				clb(r,tbl,row,chngs);}
			,function (respond){
				console.log('main.DBTblRow_update.then:err:',tbl,arguments);})
		}
	,DBTblRow_delete:function(tbl,row,clb){console.log('main.DBTblRow_delete',arguments);
		$http({method:'MTbl.delete',url:xUrl+tbl.dbName+'/'+tbl.dbtName+'/'+p.vals2pkv(tbl,row).join('/')})
		.then(function (respond){console.log('main.DBTblRow_delete.then:',tbl,arguments);
				var r=respond.data['return'];
				clb(r,tbl,row);}
			,function (respond){
				console.log('main.DBTblRow_delete.then:err:',tbl,arguments);})
		}
	,DBTblRow_query:function(tbl,cols,where,groupBy,orderBy,clb){console.log('main.DBTblRow_query',arguments);
		$http({method:'MTbl.query',url:xUrl+tbl.dbName+'/'+tbl.dbtName
			,data:{cols:cols,where:where,groupBy:groupBy,orderBy:orderBy}})
		.then(function (respond){console.log('main.DBTblRow_query.then:',tbl,arguments);
				var r=respond.data['return'];
				clb(r,tbl,cols,where,groupBy,orderBy);}
			,function (respond){
				console.log('main.DBTblRow_query.then:err:',tbl,arguments);})
		}
	,DB_qry:function(dbn,query,where,clb){console.log('main.DBTblRow_qry',arguments);
		$http({method:'MTbl.qry',url:xUrl+dbn,data:{query:query,where:where}})
		.then(function (respond){console.log('main.DBTblRow_qry.then:',tbl,arguments);
				var r=respond.data['return'];
				clb(r,tbl,cols,where,groupBy,orderBy);}
			,function (respond){
				console.log('main.DBTblRow_qry.then:err:',tbl,arguments);})
		}
	,DB_cmnds:function(dbn,cmnds){console.log('main.DB_cmnds',arguments);
		$http({method:'MTbl.cmnds',url:xUrl+dbn,data:cmnds})
		.then(function (respond){console.log('main.DB_cmnds.then:',tbl,arguments);
				var r=respond.data['return'];
				clb(r,cmnds);}
			,function (respond){
				console.log('main.DB_cmnds.then:err:',tbl,arguments);})
		}
	}//main
	return p;}])

.controller('mainCtrl',function mainCtrlController($scope,main ) {
	if(!main )//|| !main.usr
		return location.hash='#!/login'
	if(main){
		$scope.main=main
		$scope.dt=dt
		$scope.dbNames=main.dbNames
		$scope.db=main.dbs.test;
		$scope.tbl=$scope.db.tbls.test
		/*typs:[{name:<str>},,,]
		dbs={
		 <dbName>:
			{dbName:<str>,comment:<str>,
				tbls:{
					<dbtName>:{
						dbtName:<str>
						,pkc:[<str:col>,,,]
						,indcs:[[<str:keyName>,[<cols>,,,] ],,,]
						,fkc:[[<str:col>,<fTbl:str>,<fCol:str>],,,]
						,cols:[
							{
								name:<str>
								,clss:<str>
								,creatn:<str>
								,i:<uint>
							}
						]//cols
					}//tbl
					,,,
				}//tbls
			}//db
			,,,
		}//dbs
		db
		tbl
		*/
		
		$scope.chngDb=function (x){
			console.log('chgDb',x,arguments);
			$scope.db=main.dbs[$scope.dbn]
			}
		$scope. clkDbNew=function (x){
			console.log('clkDbNew',x,arguments);}
		$scope. clkDbRename=function (x){
			console.log('clkDbRename',x,arguments);}
		$scope. clkDbDelete=function (x){
			console.log('clkDbDelete',x,arguments);}
		$scope. clkDbReload=function (x){
			console.log('clkDbReload',x,arguments);
			main.DB_names(
				function (x){
					console.log('$scope.clkDbReload:ok',x,arguments);}
				)
			}
		$scope. clkDbLoad=function (x){console.log('clkDbLoad',x,arguments);
			main.DB_get(x,
				function (x){$scope.db=x;$scope.tbl=0
					console.log('$scope.clkDbLoad:ok',x,arguments);}
				)}
		
		$scope.chngTbl=function (x){
			console.log('chgTbl',x,arguments);}
		$scope. clkTblNew=function (x){
			console.log('clkTblNew',x,arguments);}
		$scope. clkTblRename=function (x){
			console.log('clkTblRename',x,arguments);}
		$scope. clkTblDelete=function (x){
			console.log('clkTblDelete',x,arguments);}
		
		$scope.chngCol=function (x){
			console.log('chgCol',x,arguments);}
		$scope. clkColNew=function (x){
			console.log('clkColNew',x,arguments);}
		$scope. clkColRename=function (x){
			console.log('clkColRename',x,arguments);}
		$scope. clkColDelete=function (x){
			console.log('clkColDelete',x,arguments);}
		
		$scope.chngPkc=function (x){
			console.log('chgPkc',x,arguments);}
		$scope. clkPkcNew=function (x){
			console.log('clkPkcNew',x,arguments);}
		$scope. clkPkcRename=function (x){
			console.log('clkPkcRename',x,arguments);}
		$scope. clkPkcDelete=function (x){
			console.log('clkPkcDelete',x,arguments);}
		
		$scope.chngIndx=function (x){
			console.log('chgIndx',x,arguments);}
		$scope. clkIndxNew=function (x){
			console.log('clkIndxNew',x,arguments);}
		$scope. clkIndxRename=function (x){
			console.log('clkIndxRename',x,arguments);}
		$scope. clkIndxDelete=function (x){
			console.log('clkIndxDelete',x,arguments);}
		
		$scope.chngFk=function (x){
			console.log('chgFk',x,arguments);}
		$scope. clkFkNew=function (x){
			console.log('clkFkNew',x,arguments);}
		$scope. clkFkRename=function (x){
			console.log('clkFkRename',x,arguments);}
		$scope. clkFkDelete=function (x){
			console.log('clkFkDelete',x,arguments);}
		
	}else{
		$scope.usr=null;
		console.log('mainCntrl:version=',$scope.version='mainCntrl , no app')
	}
})
.config(function mainConfig($stateProvider,$urlRouterProvider){
	console.log('config',this,arguments);
	$urlRouterProvider.otherwise('/main');
	$stateProvider

 .state('main',{
	  url:'/main',controller:'mainCtrl',
	templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:main');
		return $timeout(function ()
			{var x='template.main'
				,t=did(x)
				,cnn=t.innerHTML;
				//console.log('config:func3:main',cnn);
				return cnn;
			}, 100);}
	})
})//config

function did(p){return document.getElementById(p);}

function dt(p){var d=p;
		if(p==undefined)
			return '-';
		else if(!(p instanceof Date))//if(typeof(d)=='number')
			try{d=new Date(p);}
			catch(ex){
				console.error('function dt:formatDate:error:',ex)
			}
		if( d instanceof Date)return d.getFullYear()+'-'+
			(1+d.getMonth())+'-'+
			d.getDate()+'T'+
			d.getHours()+':'+
			d.getMinutes()+':'+
			d.getSeconds()+'.'+
			d.getMilliseconds();
		else
			return '-';
	}
