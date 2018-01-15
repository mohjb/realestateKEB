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
}xUrl='index.jsp';//2017.11.jsp
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
		p.ls={rate:{entityId:"rate",lastModified:0,v:350},usr:{
				moh:{entityId:'user-moh',un:'moh',pw:'6f8f57715090da2632453988d9a1501b'
					,firstName:'Mohammad',lastName:'Buhamad'
					,tel:'99876454',email:'mohamadjb@gmail.com'
					,level:'fullAccess',notes:'',created:'2017/06/20T21:05',f:1
					,lastModified:'2017/06/20T21:05'}
				,be:{entityId:'user-be',un:'be',pw:'92eb5ffee6ae2fec3ad71c777531578f'
					,firstName:'Secratery',lastName:'x',email:'bohamaem@gmail.com'
					,level:'user',notes:'',created:'2017/07/06T16:00',f:0
					,lastModified:'2017/07/06T21:05'}}
			,contracts:[]
			,oh:[]
			,profiles:[]
		}// ls // localStorage
		p.lsInitEntitiesCache(p.ls)
	};p.lsReset();
	p.rate=function(){return p.ls.rate.v;}
	p.logout=function appLogout(state){p.usr=0;
		cs('.onUsrF').display='none'
		cs('.onLoggedIn').display='none'
		cs('.onLoggedOut').display='';}
	p.dt=dt;p.lsSaved=0//new Date().getTime()
	p.lsSave=function(forgein){
		localStorage.aswan20170704=
			forgein==undefined
			?JSON.stringify(p.ls)
			:forgein;
		p.lsSaved=new Date().getTime();}
	p.lsLoad=function(forgein){
		var s=forgein!=undefined
			?forgein
			:localStorage.aswan20170704
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
	p.compute=function(c){
		c.oh=(c.rate||0)*(c.ppl||0)*(c.month||0);
		c.tc=(c.dl||0)+(c.dm||0)+(c.sv||0)+(c.oh||0)
		c.ni=(c.sp||0)-((c.tc||0)+(c.cm||0))
	}
	p.cols={ppl:'number of People'
		,dl:'Direct Labor'		//sum
		,month:'month'
		,dm:'Direct Material'	//sum
		,sv:'Direct Supervisor'	//sum
		,oh:'Overhead'			//sum
		,cm:'Commession'		//sum
		,sp:'Sales Price'		//sum
		,ni:'Net Income'		//sum
		,tc:'Total Cost'		//sum
	}
	p.newCntrct=function(){
		var d=new Date(),n=d.getTime()
	  ,x= {lastModified:n,created:n
		,entityId:'cntrct-'+n
		,usr:(app.usr&&app.usr.entityId)
		,title:'Contract'+n
		,contractor:'Contractor'
		,jn:0,ppl:0,month:0
		,dl:0 // 'Direct Labor'
		,dm:0 // Direct Material
		,sv:0 // direct Supervisor
		,oh:0 // Over head
		,tc:0 //total cost
		,cm:0 // comession
		,sp:0 // sales-price
		,ni:0 // net-income ,goal:'dl'
		,rate:p.rate()
		}
		return x;
	}
	p.deleteCntrct=function(){
		if(confirm('Please confirm delete '+p.cntrct.title)){
		p.cntrct.deleted=new Date();
		p.lsSave();
		location.hash='#!/main';
	}}
	p.profileSelects={}
	p.profileFields=[
		{name:'Name',isArray:true}
		,{name:'CID',type:'integer'}
		,{name:'Nationality',isArray:true,type:'select'
			,select:p.profileSelects.Nationality= ["Afghanistan","Albania","Algeria","Andorra","Angola","Anguilla","Antigua & Barbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerbaijan","Bahamas"
			,"Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia","Bosnia & Herzegovina","Botswana","Brazil","British Virgin Islands"
			,"Brunei","Bulgaria","Burkina Faso","Burundi","Cambodia","Cameroon" ,"Canada","Cape Verde","Cayman Islands","Chad","Chile","China","Colombia","Congo","Cook Islands","Costa Rica"
			,"Cote D Ivoire","Croatia","Cruise Ship","Cuba","Cyprus","Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea"
			,"Estonia","Ethiopia","Falkland Islands","Faroe Islands","Fiji","Finland","France","French Polynesia","French West Indies","Gabon","Gambia","Georgia","Germany","Ghana"
			,"Gibraltar","Greece","Greenland","Grenada","Guam","Guatemala","Guernsey","Guinea","Guinea Bissau","Guyana","Haiti","Honduras","Hong Kong","Hungary","Iceland","India"
			,"Indonesia","Iran","Iraq","Ireland","Isle of Man","Israel","Italy","Jamaica","Japan","Jersey","Jordan","Kazakhstan","Kenya","Kuwait","Kyrgyz Republic","Laos","Latvia"
			,"Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macau","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Mauritania"
			,"Mauritius","Mexico","Moldova","Monaco","Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Namibia","Nepal","Netherlands","Netherlands Antilles","New Caledonia"
			,"New Zealand","Nicaragua","Niger","Nigeria","Norway","Oman","Pakistan","Palestine","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Poland","Portugal"
			,"Puerto Rico","Qatar","Reunion","Romania","Russia","Rwanda","Saint Pierre & Miquelon","Samoa","San Marino","Satellite","Saudi Arabia","Senegal","Serbia","Seychelles"
			,"Sierra Leone","Singapore","Slovakia","Slovenia","South Africa","South Korea","Spain","Sri Lanka","St Kitts & Nevis","St Lucia","St Vincent","St. Lucia","Sudan"
			,"Suriname","Swaziland","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand","Timor L'Este","Togo","Tonga","Trinidad & Tobago","Tunisia"
			,"Turkey","Turkmenistan","Turks & Caicos","Uganda","Ukraine","United Arab Emirates","United Kingdom"
			, "United States",  "United States Minor Outlying Islands"
			,"Uruguay","Uzbekistan","Venezuela","Vietnam","Virgin Islands (US)"
			,"Yemen","Zambia","Zimbabwe"]}
		,{name:'contactInfo',c:[{name:'telNo',type:'tel',isArray:true}
			,{name:'address',isArray:true}
			,{name:'email',type:'email',isArray:true}]}
		,{name:'PassportNo',isArray:true,type:'integer'}
		,{name:'type',type:'select',select:p.profileSelects.type=[//enum
			'operator'
			,'administrator'
			,'contractor']}// (only commision)
		,{name:'JobCategory',isArray:true,type:'select',select:p.profileSelects.JobCategory=["Mechanical Engineer"
			, "Electrical Engineer", "Instrument Engineer"
			, "Mechanical Supervisor", "Welder –TIG & ARC"
			, "Fabricator", "Valve Technician", "Instrument Technician"
			, "Machinist", "Electrician", "Pipe Fitter", "CNC Operator"
			, "Store Keeper", "General Manager", "Project Manager"
			, "Accountant", "Coordinator", "Secretary", "Receptionist"
			, "Draft man", "Lison Officer (Mandoop)", "Driver", "Care Taker"]}
		,{name:'PunchID',type:'integer'}
		,{name:'Date','type':'date',c:[{name:'Joining'},{name:'DOB'}]}
		,{name:'Size',type:'integer',c:[{name:'shoe'},{name:'overall'}]}
		,{name:'Expiry',type:'date',c:[
			{name:'Passport'}
			,{name:'CID'},{name:'Residency'}
			,{name:'Contract',desc:'(ministry of labor)'}
			,{name:'driverLicense'}]}
		,{name:'freeAttendance',type:'checkbox' ,authorization:'TopLevel,Auditor'}
		,{name:'BankAccountDetailes',isArray:true,authorization:'TopLevel,Auditor',c:[
			{name:'cash',type:'checkbox'}
			,{name:'bankName'}
			,{name:'accountNumber'}
			]}
		,{name:'Salary',type:'currency'
			,desc:'salary fields (each has change log)'
			,authorization:'TopLevel,Auditor'
			,c:[{name:'basic salary'}
			,{name:'mobile'}
			,{name:'fuel'}
			,{name:'Increment'}
			,{name:'CustomItems',isArray:true}]}
		,{name:'Attachments',type:'file',c:[
			 {name:'FacePic',isArray:true}
			,{name:'cid',isArray:true}
			,{name:'residency',isArray:true}
			,{name:'passport',isArray:true}
			,{name:'ministryLabor',isArray:true}
			,{name:'driverLicense',isArray:true}]}
		]//profileFields
	p.newProfile=function(){
		var d=new Date(),n=d.getTime()
		,x={entityId:'profile'+n,created:n,lastModified:n,usr:app.usr.entityId};
		p.entities[x.entityId]=x//{o:x,lm:n,chng:0};
		p.ls.profiles.push(x)
		for(var i in p.profileFields)
		{var u=p.profileFields[i];
			if(u.c){var z=x[u.name]={}
			  for(var j in u.c){var w=u.c[j]
				z[w.name]=w.isArray?[]:''
			}}else
				x[u.name]=u.isArray?[]:''
		}x.Name[0]=p.ls.profiles.length+'.Name'
		p.lsSave();return x;
	}/*
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
		}).state('cntrct',{
		  url:'/cntrct',controller:'cntrctCtrl',
		templateProvider: function ($timeout, $stateParams) {
			console.log('config:func2:cntrct');
			return $timeout(function () 
				{var x='template.cntrct'
					,t=did(x)
					,cnn=t.innerHTML;
					//console.log('config:func3:cntrct',cnn);
					return cnn;
				}, 100);}
		}).state('oh',{
		  url:'/oh',controller:'ohCtrl',
		templateProvider: function ($timeout, $stateParams) {
			console.log('config:func2:oh');
			return $timeout(function () 
				{var x='template.oh'
					,t=did(x)
					,cnn=t.innerHTML;
					//console.log('config:func3:oh',cnn);
					return cnn;
				}, 100);}
		}).state('profiles',{
		  url:'/profiles',controller:'profilesCtrl',
		templateProvider: function ($timeout, $stateParams) {
			console.log('config:func2:profiles');
			return $timeout(function () 
				{var x='template.profiles'
					,t=did(x)
					,cnn=t.innerHTML;
					//console.log('config:func3:profiles',cnn);
					return cnn;
				}, 100);}
		}).state('cntrctsReport',{
		  url:'/cntrctsReport',controller:'cntrctsReportCtrl',
		templateProvider: function ($timeout, $stateParams) {
			console.log('config:func2:cntrctsReport');
			return $timeout(function () 
				{var x='template.cntrctsReport'
					,t=did(x)
					,cnn=t.innerHTML;
					//console.log('config:func3:cntrctsReport',cnn);
					return cnn;
				}, 100);}
		}).state('usrs',{
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
		}).state('xlScreen',{
		  url:'/xl',controller:'xlCtrl',
		templateProvider: function ($timeout, $stateParams) {
			console.log('config:func2:xl');
			return $timeout(function () 
				{var x='template.xl'
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
		$scope.cntrcts=app.ls.contracts
		console.log('mainCntrl:version=',$scope.version='mainCntrl , app=',app )
	}else{
		$scope.usr=null;
		console.log('mainCntrl:version=',$scope.version='mainCntrl , no app')
	}
})
.controller('cntrctCtrl',function cntrctCtrlController($scope,app ) {
	if(app instanceof Function)
		app=app();
	
	if(!app || !app.usr)
		return location.hash='#!/login'
	if(app){
		$scope.app=app
		$scope.dt=dt
		$scope.c=app.cntrct
		console.log('cntrctCtrl:version=',$scope.version='cntrctCtrl , app=',app )
	}else{
		$scope.usr=null;
		console.log('cntrctCtrl:version=',$scope.version='cntrctCtrl , no app')
	}
})
.controller('ohCtrl',function ohCtrlController($scope,app ) {
	if(app instanceof Function)
		app=app();
	
	if(!app || !app.usr)
		return location.hash='#!/login'
	$scope.app=app
	$scope.dt=dt
	//$scope.c=app.cntrct
	console.log('ohCtrl:version=',$scope.version='ohCtrl , app=',app )
	
})
.controller('profilesCtrl',function profilesCtrlController($scope,app ) {
	if(app instanceof Function)
		app=app();
	
	if(!app || !app.usr)
		return location.hash='#!/login'
	$scope.app=app
	$scope.dt=dt
	var pf=$scope.pf=app.profileFields
	,ps=$scope.profileSelects=app.profileSelects
	,sp=$scope.sp=app.selectedProfile=(
		app.selectedProfile?app.selectedProfile
		:((app.ls.profiles&&app.ls.profiles.length)?app.ls.profiles[0]
		  :app.newProfile()
		 )
	)
	
	
	$scope.fld=function fld(path,af){//get field
		function pth(pi,prnt){
			var n=path[pi]
			,x=prnt[n];
			return pi+1>=path.length//pi+2>=pth.length?[x,x[path[pi+1]]]
				?x:pth(pi+1,path,x);}
		var a=path instanceof Array
		,n=a?path[0]:path;if(!af)af=pf;
		for(var i in af)
		{if(n==af[i].name)
			return a?pth(0,af[i]):af[i]
		}
	}
	
	$scope.chngSlct=function(){
		//if(!$scope.selectedProfile)$scope.selectedProfile=app.selectedProfile||app.ls.profiles[0]||{}
		var sp=$scope.sp=app.selectedProfile//$scope.keys=$scope.prepareKeys($scope.selectedProfile)
		sp.freeAttendance = ! ! sp.freeAttendance 
		console.log('profilesCtrl.chngProfileSlct:',sp,$scope);
	}
	$scope.chng=function(a,x){console.log('profilesCtrl.chng:',a,x);}
	$scope.blur=function(a,x){console.log('profilesCtrl.blur:',a,x);}
	console.log('profilesCtrl:version=',$scope.version='profilesCtrl , app=',app )
})
.controller('cntrctsReportCtrl',function cntrctsReportCtrlController($scope,app ) {
	if(app instanceof Function)
		app=app();
	if(!app || !app.usr)
		return location.hash='#!/login'
	$scope.app=app
	$scope.dt=dt;
	$scope.from=app.from;
	$scope.to=app.to;
	$scope.PType=[$scope.paid='all','Only Paid','Only not paid']
	$scope.search=function(d1,d2){
		var a=$scope.a=[],pt=$scope.paid
		,paid=0
		,f=app.from=d1||$scope.from
		,t=app.to=d2||$scope.to;//$scope.sum=0
		$scope.b=[{name:'dl',sum:0}
		,{name:'dm',sum:0}
		,{name:'sv',sum:0}
		,{name:'oh',sum:0}
		,{name:'tc',sum:0}
		,{name:'cm',sum:0}
		,{name:'sp',sum:0}
		,{name:'ni',sum:0}
		];
		if(f || t)
		for(var i in app.ls.contracts)try{
			var c=app.ls.contracts[i]
			,s=c.started
			,e=c.completed
			,z=f&&t /* for the sake of debugging ,if-else statements are used for checking time interval intersection (z) 
				, but if debugging isnt needed, alternativly can be written as:
			 z= (f&&t 
			 	 && (	  (s&&e &&( s<=t&&f<=e )) 
			 	 		||(s 	&&( f<=s&&s<=t )) 
			 	 		||(e 	&&( f<=e&&e<=t )) 
					)
				)
			  ||(f&&( 
						  (e&& f<=e) 
						||(s&& f<=s) 
					)
				)
			  ||(t&&(	  (s&& s<=t)
			  			||(e&& e<=t)
					)
			)*/
			if(c.deleted|| (pt!='all' &&((pt=='Only Paid'&&
				c.paid) || (pt=='Only not paid'&& !c.paid))))
				continue;
			if( s &&(!(s instanceof Date)) )
				s=new Date(s)
			if( e &&(!(e instanceof Date)) )
				e=new Date(e)
			if(z){
				if(s&&e)
					z=s<=t&&f<=e;else
				if(s)
					z=f<=s&&s<=t;else
				if(e)
					z=f<=e&&e<=t
				else 
					z=0;
			}else if(f ){
				if(e)
					z=f<=e;else
				if(s)
					z=f<=s
			}else if(t){
				if(s)
					z=s<=t;else
				if(e)
					z=e<=t
			}
			if(z){
				$scope.a.push(c);
				for(var j in $scope.b){
					var u=$scope.b[j],v=c[u.name]
					if(!isNaN(v))
						u.sum+=v;
				}
			}
		}catch(ex)//for i c
		{console.error('cntrctReport.search',ex);}
	}//function search
	console.log('cntrctsReportCtrl:version=',$scope.version='cntrctsReportCtrl , app=',app )
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
.controller('xlCtrl',function xlCtrlController($scope,$http,app ) {
	if(app instanceof Function)
		app=app();
	if(!app || !app.usr || !app.usr.f)
		return location.hash='#!/login'
	$scope.app=app;if(!app.xl){var d='default';app.xl={fileName:d,
		fileList:[{name:d,size:'?',lastModified:new Date().getTime()}]}
	}var xl=$scope.xl=app.xl//else {$scope.fileName=app.xl.fileName;$scope.fileList=app.xl.fileList}
	$scope.signature=function signature(c){
		var s=[],n=0,z=40,i=0,j=z
		,s2
		,s1=c;
		try{s1=JSON.stringify( c );}
		catch(ex){
			console.error("xlCtrl.signature:error:",ex);
			}
		s2=btoa( s1 )
		n=s2.length
		while(n-i>z)
		{s.push(s2.substring(i,j));
			i=j;j+=z;
		}s.push(s2.substring(i,n));
		return s.join('\n');
	}
	$scope.refreshFileList=function refreshFileList(x){
		var s={op:'listFiles'}
		$http.post(xUrl,s).then(
			function(response){
				var x=response.data['return'] ;xl.fileList=x// || response['return']
				console.log('xlCtrl.refreshFileList:post:response:',x,s,response,arguments,$scope)
			},function(response){
				console.log('xlCtrl.refreshFileList:post:error:',response,arguments);
			})//return ;
		}
	$scope.loadFile=function loadFile(x){
		var s={op:'getFile',fileName:x.fileName}
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
		}
	$scope.saveToServer=function saveToServer(filNm){
		if(!filNm)
			filNm=xl.fileName;
		else 
			xl.fileName=filNm;
		var s={op:'putFile',fileName:app.ls.fileName=filNm
			,content:JSON.stringify(app.ls),lastModified:app.lsSaved};
		s.size=s.content.length;
		$http.post(xUrl//,s
		).then(
			function(response){
				var x=response.data['return'] ;//p.lastServerCheck=new Date();
				if(x==true && xl.fileList){var a=xl.fileList,b=0
					for(var i=0;i<a.length && !b;i++)
						b=a[i]&&a[i].fileName&& a[i].fileName==s.fileName
					if(!b)
					{b=[s];for(var i=0;i<a.length ;i++)
							b.push(a[i]);
						xl.fileList=b;
					}
				}
				console.log('xlCtrl.saveToServer:post:response:',x,s,response,arguments,$scope)
			},function(response){
				console.log('xlCtrl.saveToServer:post:error:',response,arguments);
			})
		return alert ('not implemented yet');
	}
	$scope.uploadXL=function uploadXL(x){
		return alert ('not implemented yet');
		}
	console.log('xlCtrl:version=',$scope.version='xlCtrl , app=',app )
})
.service('LoadingInterceptor', ['$q', '$rootScope', '$log', 
function ($q, $rootScope, $log) {
    //'use strict';
 
    var xhrCreations = 0;
    var xhrResolutions = 0;
 
    function isLoading() {
        var x= xhrResolutions < xhrCreations;
		console.log('LoadingInterceptor.isLoading:xhrResolutions',xhrResolutions ,' < xhrCreations', xhrCreations,':',x);
		return x;
    }
 
    function updateStatus() {
        $rootScope.loading = isLoading();
		console.log('LoadingInterceptor.updateStatus:$rootScope.loading ',$rootScope.loading );
    }
 
    return {
        request: function (config) {
            xhrCreations++;
            updateStatus();
            return config;
        },
        requestError: function (rejection) {
            xhrResolutions++;
            updateStatus();
            $log.error('Request error:', rejection);
            return $q.reject(rejection);
        },
        response: function (response) {
            xhrResolutions++;
            updateStatus();
            return response;
        },
        responseError: function (rejection) {
            xhrResolutions++;
            updateStatus();
            $log.error('Response error:', rejection);
            return $q.reject(rejection);
        }
    };
}])
.filter('filtr',function(){
	return function(p,op1){
		function intgr(p)
		{var x=Math.abs(p),b="",s,i=1
			if(x<1000)
				return p;
			while(x>999)
			{s=Math.floor(x%1000).toString();
				if(s.length==1)s='00'+s;else if(s.length==2)s='0'+s;
				b=',<span class="num'+(i++)+'">'+s+'</span>'+b
				x=Math.floor(x/1000)
			}b='<span class="num'+i+'">'+x+'</span>'+b
			if(p<0)b='-'+b
			return b;}
		function num(p,s)
		{var x=Math.abs(p),b;
			if(s==undefined)
				s=Math.floor((x*1000)%1000);
			s=s.toString()
			if(s.length==1)s='00'+s;else if(s.length==2)s='0'+s;
			b=intgr(Math.floor(x))+'.<span class="num0">'+s+'</span>';
			if(p<0)b='-'+b
			return b;}
		if(!p)return p;
		switch(op1||0)
		{case 'integer':p= intgr(p);break;
			case "number":p= num(p);break;
			case 'currency':p= num(p)+'KD';break;
			default:var x=Math.abs(p),
			s=Math.floor((x*1000)%1000)
			p= s==0?intgr(p):num(p,s);
		}
		return p
	}
})
.filter('numberSpelling',function(){
 return function(v,op1){
	var r={ar:[],en:[]}
	,a={0:{ar:'صفر',en:'zero'},n:{ar:'سالب',en:'negative',en2:'minus'}
	,s:{ar:' ',en:' '},w:{ar:' و ',en:' '},point:{ar:' فاصلة ',en:' point '},frac:{ar:' من ألف ',en:' of a thousandth'}
	,1:{1:{ar:'واحد',en:'one'},2:{ar:'ثنين',en:'two'},3:{ar:'ثلاثة',en:'three'},4:{ar:'اربع',en:'four'}
			,5:{ar:'خمسة',en:'five'},6:{ar:'ستة',en:'six'},7:{ar:'سبعة',en:'seven'},8:{ar:'ثمانية',en:'eight'}
			,9:{ar:'تسعة',en:'nine'},10:{ar:'عشرة',en:'ten'},11:{ar:'إحدى عشر',en:'eleven'}
			,12:{ar:'إثنى عشر',en:'twelve'},13:{ar:'ثلاثة عشر',en:'thirteen'},14:{ar:'اربعة عشر',en:'fourteen'}
			,15:{ar:'خمسة عشر',en:'fifteen'},16:{ar:'ستة عشر',en:'sixteen'},17:{ar:'سبعة عشر',en:'seventeen'}
			,18:{ar:'ثمانية عشر',en:'eighteen'},19:{ar:'تسعة عشر',en:'ninteen'}
		,t:{2:{ar:'عشرون',en:'twenty'},3:{ar:'ثلاثون',en:'thirty'},4:{ar:'اربعون',en:'fourty'}
			,5:{ar:'خمسون',en:'fifty'},6:{ar:'ستون',en:'sixty'},7:{ar:'سبعون',en:'seventy'}
			,8:{ar:'ثمانون',en:'eighty'},9:{ar:'تسعون',en:'ninety'}}
		,100:{ar:'مئة',ar2:'مئتين',en:'hundred'}}
	, 2:{en:'thousand'		,ar:'ألف'		,ar2:'ألفين'	,ar3:'آلاف' }
	, 3:{en:'million'		,ar:'مليون'		,ar2:'مليونين'	,ar3:'ملاين' }
	, 4:{en:'billion'		,ar:'بليون'		,ar2:'بليونين'	,ar3:'بلاين' }
	, 5:{en:'trillion'		,ar:'ترليون'	,ar2:'ترليونين'	,ar3:'ترلاين'}
	, 6:{en:'Quadrillion'   ,ar:'كوادرليون' ,ar2:'كوادرليونين',ar3:'كوادرلاين'}
	/*, 7:{en:'Quintillion'   ,ar:'كوانتليون'	}
	, 8:{en:'Sextillion'	,ar:'سكستليون'	}
	, 9:{en:'Septillion'	,ar:'سبتليون'	}
	,10:{en:'Octillion'		,ar:'أوكتليون'	}
	,11:{en:'Nonillion'		,ar:'نونليون'	}
	,12:{en:'Decillion'		,ar:'ديسيليون'	}
	,13:{en:'Undecillion'	,ar:'أنديسليون'	}
	,14:{en:'Duodecillion'	,ar:'ديوديسيليون'	}
	,15:{en:'Tredecillion'	,ar:'تريديسيليون'	}
	,16:{en:'Quattuordecillion'	,ar:'كواتورديسيليون'}
	,17:{en:'Quinquadecillion'	,ar:'كوانكوديسيليون'}
	,18:{en:'Sedecillion'		,ar:'سيديسيليون'	}
	,19:{en:'Septendecillion'	,ar:'سيبتنديسيليون' }
	
	
	
	1000000000000000000000 ZWD
=
8.3669521967394E+17 KWD
	*/
	}//a
	function apnd(p){r.ar.push(p.ar);r.en.push(p.en);}
	function ones(v){
		if(v>19)
			tens(v);//if(x>0){apnd(a[' ']);apnd(a[1][x]);}
		else if(v>0){
			var x=Math.floor(v<20?v%100:v%10);
			apnd(a.s);apnd(a[1][x]);
		}
		return r;
	}
	function tens(v){
		var o=Math.floor(v%10),t=Math.floor(v/10)%10,x
		if(v>99){hnds(Math.floor(v/100)%10);if(o+t>0)apnd(a.w);}
		//en
		if(t){x=a[1].t[t]||a[1][t*10+o];r.en.push(x.en);if(o)r.en.push(a.s.en);}
		if(o && t!=1 ){x=a[1][o];r.en.push(x.en);}
		//ar
		if(o && t!=1 ){x=a[1][o];r.ar.push(x.ar);if(t)r.ar.push(a.w.ar);}
		if(t){x=a[1].t[t]||a[1][t*10+o];r.ar.push(x.ar);}
		return r;
	}
	function hnds(v){
		if(v==1)apnd(a[1][100])
		else if(v==2){
			r.en.push(a[1][v].en,a.w.en,a[1][100].en)
			r.ar.push(a[1][100].ar2)}
		else{apnd(a[1][v]);apnd(a.s);apnd(a[1][100]);}
	}
	function d3(v,base)
	{var x=Math.floor(v%1000)
	 if(v>999)
	 {	if(base==6){ // 19
			d3(Math.floor(v/1000),2)
			if(x==0){apnd(a.s);apnd(a[base])}
		}
		else d3(Math.floor(v/1000),base+1);
		if(x>0)apnd(a.w);
	 }if(x>0){
		var b=a[base],h=x%100//,t=Math.floor(v/10)%10//,o=x%10;
		if(x==1){
			if(base>1 && b && b.en)apnd(b);
			else{ apnd(a.s); apnd(a[1][1]);}
		}else if(b.ar2 && h==2 ){
			if(x>2)
			{	hnds(Math.floor(v/100)%10);
				r.ar.push(a.w.ar)
			}
			r.ar.push(b.ar2)
			r.en.push(a[1][x].en,a.w.en,b.en)
		}else
		{	ones(x);
			if(b.ar3 && h<11 ){//&& x>0  && x<11
				r.en.push(a.s.en,b.en)
				r.ar.push(a.s.ar,b.ar3)
			}
			else if(base>1)
			{apnd(a.s);apnd(b);}
	 }}
	 return r;
	}
	function f(v){
		if(!v)
			return a[0];
		var d,neg=v<0;
		if(v<0){v=Math.abs(v);apnd(a.n);apnd(a.s);}
		d=Math.floor(v*1000)%1000
		d3(Math.floor(v),1);
		if(d){apnd(a.point);d3(d,1);apnd(a.frac);}
		r.ar=r.ar.join('');
		r.en=r.en.join('')
			return r;
	}
	return f(v);
 }
})

function s2ab(s) {
	if(typeof ArrayBuffer !== 'undefined') {
		var buf = new ArrayBuffer(s.length);
		var view = new Uint8Array(buf);
		for (var i=0; i!=s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF;
		return buf;
	} else {
		var buf = new Array(s.length);
		for (var i=0; i!=s.length; ++i) buf[i] = s.charCodeAt(i) & 0xFF;
		return buf;
	}
}
function export_table_to_excel(id, type, fn) {
var wb = XLSX.utils.table_to_book(document.getElementById(id), {sheet:"Sheet JS"});
var wbout = XLSX.write(wb, {bookType:type, bookSST:true, type: 'binary'});
var fname = fn || 'test.' + type;
try {
	saveAs(new Blob([s2ab(wbout)],{type:"application/octet-stream"}), fname);
} catch(e) { if(typeof console != 'undefined') console.log(e, wbout); }
return wbout;
}
function doit(type, fn) { return export_table_to_excel('xltable', type || 'xlsx', fn); }
