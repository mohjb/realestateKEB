main=JsonStorage=function JsonStorage(app,key,typ,val){
	this.cp(app,key,typ,val);}

JsonStorage.prototype={
	ContentType:['txt','json','key','num','real','date','bytes','serverSideJs','javaObjectStream']
	,ct:{txt:0,json:1,key:2,num:3,real:4,date:5,bytes:6,serverSideJs:7,javaObjectStream:8}
	,lsTypJson:{json:1,num:1,date:1,bytes:1,serverSideJs:1,javaObjectStream:1}
	// json.stringify:json,num,date,bytes,serverSideJs,javaObjectStream
	//plain-text:txt,key,real
	,isTypJson:function(){var t=this;return t.lsTypJson[t.typ];}
	,save:function(){var t=this,y=t.isTypJson();localStorage[t.app+'.'+t.key]=y?JSON.stringify(t.val):t.val;return t;}
	,load:function(){var t=this,y=t.isTypJson(),s=localStorage[t.app+'.'+t.key];t.val=y?JSON.parse(s):s;}
	,setVal:function(p){var t=this;t.val=p;return this;}
	//,getApp:function(){return aps[this.app];}
	,cp:function cp(app,key,typ,val){
		var t=this;if(typeof(app)=='string')
		{t.app=app;t.key=key;t.typ=typ;t.val=val;}
		else {t.app=app.app;t.key=app.key;t.typ=app.typ;t.val=app.val;}}
	,toJson:function(){var t=this;return {app:t.app,key:t.key,typ:t.typ,val:t.val};}	
	,toJsonString:function(){return JSON.stringify(this.toJson());}

	,serverStore:function JsonStorageProtoStore(v,typ,onSucc){
		var t=this,s;if(v!=undefined)t.val=v;
		if(typ!=undefined) t.typ=typ;
		t.save()
		s=JSON.stringify({op:'JsonStorage.store',store:{app:t.app,key:t.key,typ:t.typ,val:t.val}});
		main.http.post('index.jsp',s).then(
            function(response){
                var x=response.data,r=x&&x['return'];
				console.log('JsonStorage.serverStore.http.post:response:',r,x,response,arguments)
				if(onSucc)
					onSucc(t,r)
            },function(response){
                console.log('JsonStorage.serverStore.http.post:error:',response,arguments);
                //p.lastServerCheck=new Date();
	});return t;}
	,serverGet:function JsonStorageProtoGet(){
		var t=this,s=JSON.stringify({op:'JsonStorage.get',app:t.app,key:t.key});
		 main.http.post('index.jsp',s).then(
            function(response){
                var x=response.data,r=x&&x['return'];
				console.log('JsonStorage.serverGet.http.post:response:',r,x,response,arguments)
            },function(response){
                console.log('JsonStorage.serverGet.http.post:error:',response,arguments);
            })}
//,serverGetKeys:function JsonStorageProtoGetKeys(){}
//,serverListKeys:function JsonStorageProtoListKeys(){}
}

JsonStorageApp=function JsonStorageApp(app,keysList,keys){
	var t=this;if(typeof(app)=='string')
	{t.app=app;main.aps[app]=t;t.val=keysList||{};t.keys=keys||{};}
	else{t.app=app.app;t.val=app.val||{};t.keys=app.keys||{};}}

JsonStorageApp.prototype=new JsonStorage('appProto','keysList','json','');

//JsonStorageApp.prototype.apsList=[];JsonStorageApp.prototype.aps={}
//JsonStorageApp.prototype.http=function(){return this._http;}

JsonStorageApp.prototype.generateKeysList=function JsonStorageAppProtoGenerateKeysList(){
	var t=this,l=t.keys,a={},x,y
	for (var i in l){x=l[i];y=a[x.typ];
		if(!y)y=a[x.typ]=[];
		y.push(x.key)
	}return a;}//JSON.stringify(a)
JsonStorageApp.prototype.newKey=function JsonStorageAppProtoNewKey(key,typ,val){
	var t=this,r=t.keys[key],n;if(r)return r;
	r=t.keys[key]=new JsonStorage(t.app,key&&key.key?key.key:key,key&&key.typ?key.typ:typ,key&&key.val?key.val:val);
	n=t.val[r.typ]
	if(!n)n=t.val[r.typ]=[];
	n.push(r)
	//t.val=t.generateKeysList();
	return r;}

JsonStorageApp.prototype.refreshAppsList=function JsonStorageAppProtoRefreshAppsList(){
	var t=this,s=JSON.stringify({op:'JsonStorage.listApps'});
	 main.http.post('index.jsp',s).then(
		function(response){
			var x=response.data,r=x&&x['return'];
			console.log('JsonStorageApp.refreshAppsList.http.post:response:',r,x,response,arguments)
			main.apsList=r
		},function(response){
			console.log('JsonStorageApp.refreshAppsList.http.post:error:',response,arguments);
		})}
JsonStorageApp.prototype.serverGetKeys=function JsonStorageAppProtoServerGetKeys(keysList){
	var t=this,o={op:'JsonStorage.getKeys',app:t.app,keys:keysList},s;
	if(!keysList){o.keys=keysList=[]
		for(var i in t.val)for(var j in t.val[i])
			keysList.push(t.val[i][j]);}
	s=JSON.stringify(o);
	main.http.post('index.jsp',s).then(
		function(response){
			var x=response.data,r=x&&x['return'];
			console.log('JsonStorageApp.serverGetKeys.http.post:response:',r,x,response,arguments)
			if(!t.keys)t.keys={}
			for(var i in r){var x=r[i],k=x&&x.key;
				t.keys[k]=new JsonStorage(x)
				.save();}
		},function(response){
			console.log('JsonStorageApp.serverGetKeys.http.post:error:',response,arguments);
		})}
JsonStorageApp.prototype.serverListKeys=function JsonStorageAppProtoServerListKeys(){
	var t=this,s=JSON.stringify({op:'JsonStorage.listKeys',app:t.app});
	 main.http.post('index.jsp',s).then(
		function(response){
			var x=response.data,r=x&&x['return'];
			console.log('JsonStorageApp.serverListKeys.http.post:response:',r,x,response,arguments)
			apsList=r
		},function(response){
			console.log('JsonStorageApp.serverListKeys.http.post:error:',response,arguments);
		})}
JsonStorageApp.prototype.serverCreate=function JsonStorageAppProtoServerCreate(app){
	var t=this,s=JSON.stringify({op:'JsonStorage.store',app:t.toJson()});
	 t.serverStore(t.val,t.typ,function(tx,r){main.aps[t.app]=t.save();main.apsList.push(t.app);})}

JsonStorageApp.prototype.serverMember=function JsonStorageAppProtoServerMember(member,args){
	var t=this,s=JSON.stringify({op:'JsonStorage.member',app:t.app,member:member,args:args});
	 main.http.post('index.jsp',s).then(
		function(response){
			var x=response.data,r=x&&x['return'];
			console.log('JsonStorageApp.serverMember.http.post:response:',r,x,response,arguments)
		},function(response){
			console.log('JsonStorageApp.serverMember.http.post:error:',response,arguments);
		})}
JsonStorageApp.prototype.serverEval=function JsonStorageAppProtoServerEval(src){
	var t=this,s=JSON.stringify({op:'JsonStorage.eval',app:t.app,src:src});
	 main.http.post('index.jsp',s).then(
		function(response){
			var x=response.data,r=x&&x['return'];
			console.log('JsonStorageApp.serverEval.http.post:response:',r,x,response,arguments)
		},function(response){
			console.log('JsonStorageApp.serverEval.http.post:error:',response,arguments);
		})}
//JsonStorageApp.prototype.loadAllApp=function JsonStorageAppProtoLoadAllThisAppKeys(){}

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

(window.xa=(angular||{})
.module('main', ['ngSanitize','angular-md5','ui.router'] ))
.factory('main', ['$http','md5', function mainFactory($http,md5) {
	var p=main={//window.xa||{}
	aps:{},http:$http
	,selected:{app:0}
	,lsReset :function mainLsReset(){
		p.apsList=['usrs','test']
		p.aps={test:p.selected.app=new JsonStorageApp({app:'test'
				//keysList:['test']//this is val
				,keys:{test:{app:'test',key:'test',typ:'txt',val:'test'}}})
			,usrs:{app:'usrs',val:{'json':['moh']},keys:{
				moh:{un:'moh',pw:'6f8f57715090da2632453988d9a1501b'
					,firstName:'Mohammad',lastName:'Buhamad'
					,tel:'99876454',email:'mohamadjb@gmail.com'
					,level:'fullAccess',notes:'',created:'2017/06/20T21:05',f:1
					,lastModified:'2017/06/20T21:05'
					,app:'usrs',key:'moh',typ:'json',val:0}}}
		}// ls // localStorage
	}

	,newApp:function(appName){
		var r=p.selected.app=new JsonStorageApp({key:appName, keys:{} ,val:[]})//val==keysList
		r.serverCreate();//p.selected.app._http=$http;
		return r;}

	,newKey:function(key,val,typ){
		if(!val && key && key.val){
			typ=key.typ;val=key.val;key=key.key;}
		var x=p.selected.app,r=x.newKey(x.app,key,typ,val);
		x.serverStore();r.serverStore();
		return r;}
	,selectedChanged:function(ent,newSelection){
		 console.log('selectedChanged:',ent,newSelection);
		 var s=p.selected,a=s.app;//if(ent=='app'){a._http=$http}// else 
		}
	,logout:function appLogout(state){p.usr=0;
		cs('.onUsrF').display='none'
		cs('.onLoggedIn').display='none'
		cs('.onLoggedOut').display='';}
	,dt:dt
	,lsSaved:0//new Date().getTime()
	
	,chng:function(x){if(x ){
			var d=new Date(),n=d.getTime();
			x.lastModified=n;p.selected.key=x;
			console.log('main.chng',x,arguments)//if entityId.startsWith('') ) p.compute( )
	}}
	,blur:function(x){if(x ){
			console.log('main.blur',x,arguments)
			if( p.lsSaved<x.lastModified)
				x.serverStore()
	}}//else console.log('main.blur:no unique id found:',x)

	,newUsr:function(){
		var un=prompt('Please Enter User-Name')
		,pw,u,d=new Date(),n=d.getTime(),usrs=p.aps.usrs.keys;
		if(!un)return null;
		if(usrs[un] && !usrs[un].deleted ){
			alert('can not use user-name:'+un);return null;}
		pw=prompt('Please enter password')
		if(!pw)return null;
		pw=md5.createHash(pw)
		usrs[un]=u={un:un,pw:pw//,usr:main.usr.entityId
			,firstName:'user '+Object.keys(p.aps.usrs).length+1
			,lastName:'-',email:'',notes:'',level:'',f:0,tel:''
			,created:n,lastModified:n}
		//TODO: re-implement ;p.lsSave('usrs',un);
		return u;}
	}//p


	/*
	p.serverIntrvl=setInterval(function(){
		var x=p.lastServerCheck.getTime()
		,n=new Date().getTime();
		if(x+120000<=n)
		{console.log('main:polling server');
			p.lsLoad('');}
	},10000)
	p.lastServerCheck=new Date();*/
	//TODO: re-implement ;p.lsLoad()
	p.lsReset();
	return p;//function mainFactoryCallback(message) {return p;}
}])
.controller('mainCtrl',function mainCtrlController($scope,main ) {
	if(!main || !main.usr)
		return location.hash='#!/login'
	if(main){
		$scope.main=main
		$scope.dt=dt
		$scope.rhinoEvalResult=''
		$scope.ct=JsonStorage.prototype.ContentType
		main.selectedChanged('app')
		console.log('mainCntrl:version=',$scope.version='mainCntrl , main=',main )
		$scope.onNew=function onNew(ent,obj){try{
			if(ent=='key'){if(obj.key=='keysList')return alert('keysList is a reserved-name, please enter any-other input');
				if(confirm(
			'Please confirm the creation of a new key:'+obj.key))
			{var p=main.selected.app;obj.app=p.app;
				p.newKey(obj).serverStore();
				p.serverStore();
			}}else{
				var r,nm=prompt("Please enter a new name for the new "+ent);
				if(nm ){
					if( main.aps[nm])return alert('app-name('+nm+') already used');
					r=main.newApp(nm);
			}}}catch(ex){
				console.error(ex);}}
		$scope.refresh=function refresh(ent){try{
			var nm=confirm("Please confirm reloading "+ent+'s');
			if(nm ){if(ent=='app')
				main.selected.app.refreshAppsList();
				else main.selected.app.serverGetKeys()
			}}catch(ex){
				console.error(ex);}}
		$scope.clkDelKey=function clkDelKey(j){try{
			var nm=confirm("Please confirm deleting key :"+j);
			if(nm ){
			}}catch(ex){
				console.error(ex);}}
		$scope.rhinoEvalRun=function rhinoEvalRun(j){try{
			var nm=confirm("Please confirm :"+j);
			if(nm ){
				$scope.rhinoEvalResult=main.selected.app.serverEval(j)
			}}catch(ex){
				console.error(ex);}}
		$scope.serverAccess=function serverAccess(j){try{
			var args=prompt("Please enter args array in json-format:"+j);
			if(args ){
				$scope.rhinoEvalResult=main.selected.app.serverMember(j.key,args)
			}}catch(ex){
				console.error(ex);}}
				
				
	}else{
		$scope.usr=null;
		console.log('mainCntrl:version=',$scope.version='mainCntrl , no app')
	}
})
.config(function mainConfig($stateProvider,$urlRouterProvider){
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

})//config
.controller('loginCtrl',function loginCtrlController($scope,main,md5,$state,$rootScope){
 console.log('main.controller:loginCtrl:($scope'
 		,$scope,',main',main,',md5',md5
 		,',$state',$state,',$rootScope',$rootScope
 		,',arguments',arguments,',this',this,')')
	$scope.un=''
	$scope.pw=''
	$scope.msg=''
	$scope.main=main;
	$scope.clk=
	function calek(){
		console.log('loginCtrl:clk',arguments,this);
		var usrs=main.aps.usrs.keys,u=usrs[$scope.un]
		if(!md5){console.error('controller:loginCtrl:function-clk:param-md5 not defined');
			md5={createHash:function loginCtrlDummyMd5(){} } }
		if(u && u.pw == md5.createHash($scope.pw)){
			main.usr=u;
			//TODO: DbLog.newEntry(login)
			$scope.msg='login successful,\n'+(new Date)
			cs('.onLoggedIn').display=''
			cs('.onLoggedOut').display='none'
			cs('.onUsrF').display=main.usr.f?'':'none';//did('logoNotLog').style.display='none';did('logoLoggedin').style.display='grid'
			location.hash='#!/main';//$state.go('main')
		}
		else alert($scope.msg='invalid login \n,'+(new Date))
	}
	if(main.usr)
		location.hash='#!/main'
	})
.controller('usrsCtrl',function usrsController($scope,main ) {
	if(!main || !main.usr || !main.usr.f)
		return location.hash='#!/login'
	$scope.main=main
	$scope.chng=function chng(x){
		if(!x)x=$scope.editUsr
		else $scope.editUsr=x
		x.f=!!x.f;}
	$scope.chng(main.usr);
	console.log('usrsCtrl:version=',$scope.version='usrsCtrl , main=',main )
})
