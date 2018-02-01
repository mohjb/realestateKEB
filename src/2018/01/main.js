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
function cs(p){
	var ss=document.styleSheets
	for(var i in ss)try{
		var x=ss[i],r=x.rules//console.log(i,ss[i])
		for(var j in r)try{
			var y=r[j],t=y.selectorText//console.log(j,ss[i].rules[j])
			if(t==p)
				return y.style
		}catch(ex){}
	}catch(ex){}
}
xUrl='index.jsp';//2017.11.jsp

(window.xa=app=(angular||{})
.module('app', ['ngSanitize','angular-md5','ui.router'] ))
.factory('app', ['$http','md5', function appFactory($http,md5) {
	var p=window.xa||{}
	p.lsReset =function appLsReset(){
		p.ls={usr:{
				moh:{entityId:'user-moh',un:'moh',pw:'6f8f57715090da2632453988d9a1501b'
					,firstName:'Mohammad',lastName:'Buhamad'
					,tel:'99876454',email:'mohamadjb@gmail.com'
					,level:'fullAccess',notes:'',created:'2017/06/20T21:05',f:1
					,lastModified:'2017/06/20T21:05'}
				,be:{entityId:'user-be',un:'be',pw:'92eb5ffee6ae2fec3ad71c777531578f'
					,firstName:'Secratery',lastName:'x',email:'bohamaem@gmail.com'
					,level:'user',notes:'',created:'2017/07/06T16:00',f:0
					,lastModified:'2017/07/06T21:05'}}
			,apps:[
				{title:'test'
				,JspUrls:{index:{title:'index'
					,assets:[],jspIncludes:[],jspOpMethods:[],jspTbls:[]}}
				,AngularUrls:{index:{title:'index',states:[],controllers:[],templates:[]
					,directives:[],filters:[],services:[],resources:[],forms:[]}}}]
			,dbs:{'test':{tbls:[]}},
		}// ls // localStorage
	};
	p.selected={app:0
		,JspUrl:0,AngularUrl:0
		,db:0,dbt:0,asset:0,jspInclude:0,jspOpMethod:0,jspTbl:0
		,state:0,controller:0,controllerMember:0,template:0
		,directive:0,filter:0,service:0,resource:0
		}
	p.selectionEnts={app:[{ent:'JspUrl',def:'index'},{ent:'AngularUrl',def:'index'}]
		,JspUrl:['asset','jspInclude','jspOpMethod','jspTbl']
		,AngularUrl:['state','controller','controllerMember','template'
			,'directive','filter','service','resource']}
	p.lsReset();
	p.newApp=function(appName){
		var r=p.selected.app={title:appName
			,JspUrls:{index:p.newJspUrl('index')}
			,AngularUrls:{index:p.newAngularUrl('index')}}
			//$http.post(op:'JsonStorage.set',app:appName,key:'app',var:r)//newApp
		return p.ls.apps[appName]=r;}
	p.newJspUrl=function(nm){
		var r=p.selected.JspUrl={title:nm
			,assets:[],jspIncludes:[],jspOpMethods:[],jspTbls:[]}
			//$http.post(op:'JsonStorage.set',app:appName,key:'app',var:r)//newApp
		return p.selected.app.JspUrls[nm]=r;}
	p.newAngularUrl=function(nm){
		var r=p.selected.app.AngularUrls[nm]={title:appName,states:[],controllers:[],templates:[]
			,directives:[],filters:[],services:[],resources:[],forms:[]}
			//$http.post(op:'JsonStorage.set',app:appName,key:'app',var:r)//newApp
		return p.selected.AngularUrl=r;}
	p.newEnt=function(ent,nm,ap){
		if(ent=='app');
		else if(ent=='JspUrl');
		else if(ent=='AngularUrl');
		else{
			var r={title:nm,0:0}
			ap[ent].push(r);
			p.selected[ent]=r;
		return r;}}
	p.selectedChanged=function(ent,newSelection){
		 console.log('selectedChanged:',ent,newSelection);
		 var s=p.selected,a=s.app;
		 /*if(ent=='app'){
			p.selectedChanged('JspUrl',s.JspUrl=a.JspUrl['index']);
			p.selectedChanged('AngularUrl',s.AngularUrl=a.AngularUrl['index']);}
		 else */
		 if(ent in p.selectionEnts//ent=='JspUrl' || ent=='AngularUrl'
			)for(var i in p.selectionEnts[ent])
			{var n=p.selectionEnts[ent][i];
				s[n.ent||n]=0;
				if(n.def)
					p.selectedChanged(n.ent,s[n.ent]=a[n.ent][n.def]);
			}
		}
	p.logout=function appLogout(state){p.usr=0;
		cs('.onUsrF').display='none'
		cs('.onLoggedIn').display='none'
		cs('.onLoggedOut').display='';}
	p.dt=dt;p.lsSaved=0//new Date().getTime()
	p.lsSave=function(forgein){
		localStorage.dev201801=
			forgein==undefined
			?JSON.stringify(p.ls)
			:forgein;
		p.lsSaved=new Date().getTime();}
	p.lsLoad=function(forgein){
		var s=forgein!=undefined
			?forgein
			:localStorage.dev201801
		if(typeof(s)=='string')
			try{s=JSON.parse(s)
			}catch(ex){
				console.log('app.lsLoad:json-parse:error',ex)}
		if(s)
			p.lsInitEntitiesCache(p.ls=s)
		if(forgein!=undefined)
			p.lsSave(typeof(forgein)=='string'
				?forgein
				:JSON.stringify(forgein))
	}
	p.chng=function(x){
		if(x && x.entityId){
			var d=new Date(),n=d.getTime();
			x.lastModified=n
			console.log('app.chng',x,arguments)//if entityId.startsWith('') ) p.compute( )
		}
	}
	p.blur=function(x){
		if(x && x.entityId){
			console.log('app.blur',x,arguments)
			var z=p.entities[x.entityId]
			if(!z)
				p.entities[x.entityId]=x
			if( p.lsSaved<x.lastModified)
				p.lsSave();
		}else
			console.log('app.blur:no unique id found:',x)
	}
	p.newUsr=function(){
		var un=prompt('Please Enter User-Name')
		,pw,u,d=new Date(),n=d.getTime();
		if(!un)return null;
		if(p.ls.usr[un] && !p.ls.usr[un].deleted ){
			alert('can not use user-name:'+un);return null;}
		pw=prompt('Please enter password')
		if(!pw)return null;
		pw=md5.createHash(pw)
		p.ls.usr[un]=u={entityId:'user-'+un
			,un:un,pw:pw,usr:app.usr.entityId
			,firstName:'user '+Object.keys(p.ls.usr).length+1
			,lastName:'-',email:'',notes:'',level:'',f:0,tel:''
			,created:n,lastModified:n}
		p.entities[u.entityId]=u//{o:u,lastModified:n,chng:0}
		p.lsSave();
		return u;}
	

	/*
	p.serverIntrvl=setInterval(function(){
		var x=p.lastServerCheck.getTime()
		,n=new Date().getTime();
		if(x+120000<=n)
		{console.log('app:polling server');
			p.lsLoad('');}
	},10000)
	p.lastServerCheck=new Date();*/p.lsLoad()
	return function appFactoryCallback(message) {return p;}
}])
.config(function appConfig($stateProvider,$urlRouterProvider){
	console.log('config',this,arguments);
	$urlRouterProvider.otherwise('/login');
	$stateProvider
 .state('login',{
	  url:'/login',controller:'loginCtrl',
	templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:login');
		return $timeout(function () 
			{var x='template.login'
				,t=did(x)
				,cnn=t.innerHTML;
				//console.log('config:func3:login',cnn);
				return cnn;
			}, 100);}
	})
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
	
 .state('usrs',{
	  url:'/usrs',controller:'usrsCtrl',
	templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:usrs');
		return $timeout(function () 
			{var x='template.usrs'
				,t=did(x)
				,cnn=t.innerHTML;
				return cnn;
			}, 100);}})
 .state('dbs',{url:'/dbs'
	,controller:function dbsCtrlController($scope,app ) {
		if(app instanceof Function)app=app();if(!app || !app.usr)return location.hash='#!/login'
		$scope.app=app;console.log('dbsCntrl:' )}
	,templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:dbs');return $timeout(function () 
			{var x='template.dbs',t=did(x),cnn=t.innerHTML;return cnn;}, 100);}})
 .state('uiRouterState',{url:'/uiRouterState'
	,controller:function uiRouterStateCtrlController($scope,app ) {
		if(app instanceof Function)app=app();if(!app || !app.usr)return location.hash='#!/login'
		$scope.app=app;console.log('uiRouterStateCntrl:' )},
	templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:uiRouterState');return $timeout(function () 
			{var x='template.uiRouterState',t=did(x),cnn=t.innerHTML;return cnn;}, 100);}})
 .state('templates',{url:'/templates'
	,controller:function templatesCtrlController($scope,app ) {
		if(app instanceof Function)app=app();if(!app || !app.usr)return location.hash='#!/login'
		$scope.app=app;console.log('templatesCntrl:' )},
	templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:templates');
		return $timeout(function () 
			{var x='template.templates',t=did(x),cnn=t.innerHTML;return cnn;}, 100);}})
 .state('controllers',{url:'/controllers'
	,controller:function controllerCtrlController($scope,app ){
		if(app instanceof Function)app=app();if(!app || !app.usr)return location.hash='#!/login';$scope.app=app;console.log('controllersCntrl:' )}
	,templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:controllers');return $timeout(function () 
			{var x='template.controllers',t=did(x),cnn=t.innerHTML;return cnn;}, 100);}})
 .state('directives',{url:'/directives'
	,controller:function directivesCtrlController($scope,app ){
		if(app instanceof Function)app=app();if(!app || !app.usr)return location.hash='#!/login';$scope.app=app;console.log('directivesCntrl:' )}
	,templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:directives');return $timeout(function () 
			{var x='template.directives',t=did(x),cnn=t.innerHTML;return cnn;}, 100);}})
/*
\.state\('([^']+)',\{\s*url:'/[^']*',controller:'Ctrl'\s*,\s*templateProvider\s*:\s*function\s*\(\$timeout,\s*\$stateParams\)\s*\{\s*console\.log\('config:\$stateProvider:templateProvider-function:'\);\s*return\s*\$timeout\(function\s*\(\)\s*\{\s*var\s*x\s*='template\.[^']*'\s*,t=did\(x\)\s*,cnn=t\.innerHTML;\s*return cnn;\s*\},\s*100\);\}\}\)
 .state\('\1',{url:'/\1'\n	,controller:function \1CtrlController\($scope,app \){\n		if\(app instanceof Function\)app=app\(\);if\(!app || !app.usr\)return location.hash='#!/login';$scope.app=app;console.log\('\1Cntrl:' \)}	,templateProvider: function \($timeout, $stateParams\) {\n		console.log\('config:$stateProvider:templateProvider-function:\1'\);return $timeout\(function \(\) \n			{var x='template.\1',t=did\(x\),cnn=t.innerHTML;return cnn;}, 100\);}}\)
 */
 .state('filters',{url:'/filters'
	,controller:function filtersCtrlController($scope,app ){
		if(app instanceof Function)app=app();if(!app || !app.usr)return location.hash='#!/login';$scope.app=app;console.log('filtersCntrl:' )}
	,templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:filters');return $timeout(function () 
			{var x='template.filters',t=did(x),cnn=t.innerHTML;return cnn;}, 100);}})
  .state('services',{url:'/services'
	,controller:function servicesCtrlController($scope,app ){
		if(app instanceof Function)app=app();if(!app || !app.usr)return location.hash='#!/login';$scope.app=app;console.log('servicesCntrl:' )}	,templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:services');return $timeout(function () 
			{var x='template.services',t=did(x),cnn=t.innerHTML;return cnn;}, 100);}})
  .state('resources',{url:'/resources'
	,controller:function resourcesCtrlController($scope,app ){
		if(app instanceof Function)app=app();if(!app || !app.usr)return location.hash='#!/login';$scope.app=app;console.log('resourcesCntrl:' )}	,templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:resources');return $timeout(function () 
			{var x='template.resources',t=did(x),cnn=t.innerHTML;return cnn;}, 100);}})
  .state('forms',{url:'/forms'
	,controller:function formsCtrlController($scope,app ){
		if(app instanceof Function)app=app();if(!app || !app.usr)return location.hash='#!/login';$scope.app=app;console.log('formsCntrl:' )}	,templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:forms');return $timeout(function () 
			{var x='template.forms',t=did(x),cnn=t.innerHTML;return cnn;}, 100);}})
  .state('assets',{url:'/assets'
	,controller:function assetsCtrlController($scope,app ){
		if(app instanceof Function)app=app();if(!app || !app.usr)return location.hash='#!/login';$scope.app=app;console.log('assetsCntrl:' )}	,templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:assets');return $timeout(function () 
			{var x='template.assets',t=did(x),cnn=t.innerHTML;return cnn;}, 100);}})
  .state('opMethods',{url:'/opMethods'
	,controller:function opMethodsCtrlController($scope,app ){
		if(app instanceof Function)app=app();if(!app || !app.usr)return location.hash='#!/login';$scope.app=app;console.log('opMethodsCntrl:' )}	,templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:opMethods');return $timeout(function () 
			{var x='template.opMethods',t=did(x),cnn=t.innerHTML;return cnn;}, 100);}})
 .state('tlDbTbl',{url:'/tlDbTbl'
	,controller:function tlDbTblCtrlController($scope,app ){
		if(app instanceof Function)app=app();if(!app || !app.usr)return location.hash='#!/login';$scope.app=app;console.log('tlDbTblCntrl:' )}	,templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:tlDbTbl');return $timeout(function () 
			{var x='template.tlDbTbl',t=did(x),cnn=t.innerHTML;return cnn;}, 100);}})
  .state('jspDeclarations',{url:'/jspDeclarations'
	,controller:function jspDeclarationsCtrlController($scope,app ){
		if(app instanceof Function)app=app();if(!app || !app.usr)return location.hash='#!/login';$scope.app=app;console.log('jspDeclarationsCntrl:' )}	,templateProvider: function ($timeout, $stateParams) {
		console.log('config:$stateProvider:templateProvider-function:jspDeclarations');return $timeout(function () 
			{var x='template.jspDeclarations',t=did(x),cnn=t.innerHTML;return cnn;}, 100);}})
})//config
.controller('loginCtrl',function loginCtrlController($scope,app,md5,$state,$rootScope){
 console.log('app.controller:loginCtrl:($scope'
 		,$scope,',app',app,',md5',md5
 		,',$state',$state,',$rootScope',$rootScope
 		,',arguments',arguments,',this',this,')')
	$scope.un=''
	$scope.pw=''
	$scope.msg=''
	if(app instanceof Function)app=app();
	$scope.app=app;
	$scope.clk=
	function calek(){
		console.log('loginCtrl:clk',arguments,this);
		var u=app.ls.usr[$scope.un]
		if(!md5){console.error('controller:loginCtrl:function-clk:param-md5 not defined');
			md5={createHash:function loginCtrlDummyMd5(){} } }
		if(u && u.pw == md5.createHash($scope.pw)){
			app.usr=u;
			//TODO: DbLog.newEntry(login)
			$scope.msg='login successful,\n'+(new Date)
			cs('.onLoggedIn').display=''
			cs('.onLoggedOut').display='none'
			cs('.onUsrF').display=app.usr.f?'':'none';//did('logoNotLog').style.display='none';did('logoLoggedin').style.display='grid'
			location.hash='#!/main';//$state.go('main')
		}
		else alert($scope.msg='invalid login \n,'+(new Date))
	}
	if(app.usr)
		location.hash='#!/main'
	})
.controller('mainCtrl',function mainCtrlController($scope,app ) {
	if(app instanceof Function)
		app=app();
	
	if(!app || !app.usr)
		return location.hash='#!/login'
	if(app){
		$scope.app=app
		$scope.dt=dt
		console.log('mainCntrl:version=',$scope.version='mainCntrl , app=',app )
		$scope.onNew=function onNew(ent){try{
			var r,nm=prompt("Please enter a new for the new "+ent);
			if(nm ){var tr={App:"app",Apps:"app",app:"app",fltr:'filter'};
				if( app.ls.apps[nm])return alert('app-name('+nm+') already used');
				if(ent=='app')r=app.newApp(nm);
				else r=app.newEnt(ent,nm,p.selected.app)
				var s={op:'JsonStorage.set',app:nm,key:'app',val:r}
				$http.post(xUrl,s).then(
				function(response){
					var x=response.data['return'] 
					,s=x&&x.content
					,fn=app.ls.fileName=x.fileName
					//if(s&&typeof(s)=='string')x.content=s=JSON.parse(x.content)
					if(s)
					{	app.lsLoad(s);if(!app.ls.fileName&&fn)
						app.ls.fileName=fn;}
					console.log('xlCtrl.loadFile:post:response:',x,s,response,arguments,$scope)
				},function(response){
					console.log('xlCtrl.loadFile:post:error:',response,arguments);
				})//return ;
				
			}}catch(ex){
				console.error(ex);
				}}
	}else{
		$scope.usr=null;
		console.log('mainCntrl:version=',$scope.version='mainCntrl , no app')
	}
})


.controller('usrsCtrl',function usrsController($scope,app ) {
	if(app instanceof Function)
		app=app();
	if(!app || !app.usr || !app.usr.f)
		return location.hash='#!/login'
	$scope.app=app
	$scope.chng=function chng(x){
		if(!x)x=$scope.editUsr
		else $scope.editUsr=x
		x.f=!!x.f;}
	$scope.chng(app.usr);
	console.log('usrsCtrl:version=',$scope.version='usrsCtrl , app=',app )
})
