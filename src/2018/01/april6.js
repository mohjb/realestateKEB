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
xUrl='/txtSrvlt/';//2017.11.jsp

(window.xa=(angular||{})
.module('main', ['ngSanitize','angular-md5','ui.router'] ))
.factory('main', ['$http','md5', function mainFactory($http,md5) {
	var p=main={//window.xa||{}

	chng:function(x){if(x ){
			var d=new Date(),n=d.getTime();
			x.lastModified=n;p.selected.key=x;
			console.log('main.chng',x,arguments)//if entityId.startsWith('') ) p.compute( )
	}}
	,blur:function(x){if(x ){
			console.log('main.blur',x,arguments)
			if( p.lsSaved<x.lastModified)
				x.serverStore()
	}}//else console.log('main.blur:no unique id found:',x)
	/*,txtNode:{path:[{id:1,key:'key2'},{id:1,key:'key1'}]//path
		,txt:{id:3,key:'key3',owner:'moh',group:'moh',parent:2,perm:47
			,txt:'hi',meta:{protoId:3,typ:'node',yes:true}}//txt
		,children:[{id:4,key:'key4'},{id:5,key:'key5'}]//children
		}//txtNode*/
	,txt:{0:{id:0,parent:0,key:'root',owner:'moh',group:'moh',perm:0
			,txt:'',meta:{path:[0],children:[1,2]}}
		,1:{id:1,parent:0,key:'users',owner:'moh',group:'moh',perm:47
			,txt:'',meta:{path:[0],children:[3]}}
		,2:{id:2,parent:0,key:'apps',owner:'moh',group:'moh',perm:47
			,txt:'',meta:{path:[0],children:[4]}}
		,3:{id:3,parent:1,key:'moh',owner:'moh',group:'moh',perm:47
			,txt:'',meta:{path:[1,0],children:[]}}
		,4:{id:4,parent:2,key:'app1',owner:'moh',group:'moh',perm:47
			,txt:'',meta:{path:[2,0],children:[5,6]}}
		,5:{id:5,parent:4,key:'key1',owner:'moh',group:'moh',perm:47
			,txt:'',meta:{path:[4,2,0],children:[]}}
		,6:{id:6,parent:4,key:'key2',owner:'moh',group:'moh',perm:47
			,txt:'',meta:{path:[4,2,0],children:[]}}
	}//Txt
	,selected:{id:4}
	,load:function load(id,onload,ref){
		var x=main.txt[id];if(onload){
			if(onload instanceof Function)
				onload(x,ref);
			else 
				onload.onload(x,ref)}
		return x;}
	/*//,children:{0:[1,2],1:[3],2:[4],3:[],4:[5,6],5:[],6:[]}
	,loadNode:function loadNode(id,onload,ref){
		var x=main.load(id)
		return x;}*/
	}//main
	return p;//function mainFactoryCallback(message) {return p;}
}])
.controller('mainCtrl',function mainCtrlController($scope,main ) {
	if(!main )//|| !main.usr
		return location.hash='#!/login'
	if(main){
		$scope.main=main
		$scope.dt=dt
		$scope.selected=main.selected
		$scope.txt=main.load(main.selected.id)
		$scope.clk=function(id,prnt,ky){
			main.load(id,function(x,ref){main.selected.id=id;$scope.txt=x;});}
		$scope.onNewChild=function onNewChild(prnt){
			var y='',p=$scope.txt,path=[p.id]
			,x={id:id,parent:id,key:ky,owner:'moh',group:'moh',perm:47
				,txt:'',meta:{path:path,children:[]}};
			while(main.txt[x.id])
				x.id++
			function c(k,x){
				var a=x.meta.children;if(a)for(var i in a){
					var b=main.txt[a[i]];if(b.key==k)return b;}
				return null;}
			while((y=prompt('Please enter a unique key')) && c(y,p));
			if(y){x.key=y;
				p.meta.children.push(x.id)
				var a=p.meta.path
				for(var i in a)path.push(a[i]);
				main.txt[x.id]=x;
				main.selected.id=x.id;$scope.txt=x;
				return x
			}return null
		}//onNewChild
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
