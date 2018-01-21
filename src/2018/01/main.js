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
	p.lsInitEntitiesCache=function(a){
		var hitsCount=0
		function itrtOver(a,depth){if(a instanceof Object)
			for(var i in a){
				var o=a[i];
				if(o && o.entityId && !o.deleted)
				{p.entities[o.entityId]=o;
					hitsCount++}
				if(depth>0)
					itrtOver(o,depth-1)
			}}
		try{itrtOver(a,2);}
			catch(ex){
			console.log('app.lsInitEntitiesCache:error',a,ex)}
		return hitsCount;
	}
	p.lsReset =function appLsReset(){p.entities={};
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
			,dbs:{'test':{}},
		}// ls // localStorage
		p.lsInitEntitiesCache(p.ls)
	};p.lsReset();
	
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
			console.log('app.chng',x,arguments)
			if(x.entityId.startsWith('cntrct'))
				p.compute(x);
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
	/*var states=['login','main','cntrct','oh','profiles','cntrctsReport']
	for(var i in states){var s=states[i]
		console.log('config:for:',s);
		$stateProvider.state(s,{
		  url:'/'+s,controller:s+'Ctrl',
		templateProvider: function ($timeout, $stateParams) {
			console.log('config:func2:',s);
			return $timeout(function () 
				{var x='template.'+s
					,t=did(x)
					,cnn=t.innerHTML;
					console.log('config:func3:',s,cnn);
					return cnn;
				}, 100);}
		})
	}*/
	$stateProvider.state('login',{
		  url:'/login',controller:'loginCtrl',
		templateProvider: function ($timeout, $stateParams) {
			console.log('config:func2:login');
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
			console.log('config:func2:main');
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
			console.log('config:func2:usrs');
			return $timeout(function () 
				{var x='template.usrs'
					,t=did(x)
					,cnn=t.innerHTML;
					//console.log('config:func3:cntrctsReport',cnn);
					return cnn;
				}, 100);}
		})
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
		else $scope.msg='invalid login \n,'+(new Date)
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
	}else{
		$scope.usr=null;
		console.log('mainCntrl:version=',$scope.version='mainCntrl , no app')
	}
})


.controller('usrsCtrl',function profilesCtrlController($scope,app ) {
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
