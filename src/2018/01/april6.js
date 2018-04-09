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
	,txtNode:{path:[{id:1,key:'key2'},{id:1,key:'key1'}]//path
		,txt:{id:3,key:'key3',owner:'moh',group:'moh',parent:2,perm:47
			,txt:'hi',meta:{protoId:3,typ:'node',yes:true}}//txt
		,children:[{id:4,key:'key4'},{id:5,key:'key5'}]//children
		}//txtNode
	}//main

	return p;//function mainFactoryCallback(message) {return p;}
}])
.controller('mainCtrl',function mainCtrlController($scope,main ) {
	if(!main || !main.usr)
		return location.hash='#!/login'
	if(main){
		$scope.main=main
		$scope.dt=dt
		$scope.txtNode=main.txtNode
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
.directive('txtnode',function($compile){
	var linker=function(scope,element,attrs){
		var x=scope.txtNode=main.txtNode
		,b=['<h1>{{txtNode.txt.id}}|{{txtNode.txt.key}}</h1><h3>Path</h3>'],c,a=x.path;
		function btn(p){b.push('<button>',p.id,'|',p.key,'</button>');}
		for(var i in a){b.push(' / ');btn(a[i])}
		b.push('<h3>Node</h3>'
			,'Id:<input ng-moedl="txtNode.txt.id"/>'
			,' , Key:<input ng-model="txtNode.txt.key"/>'
			,' , owner:<input ng-model="txtNode.txt.owner"/>'
			,' , group:<input ng-model="txtNode.txt.group"/>'
			,' , perm:<input ng-model="txtNode.txt.perm"/>'
			,' , meta:<input ng-model="txtNode.txt.meta"/>'
			,' , txt:<input ng-model="txtNode.txt.txt"/>');
		b.push('<h3>Children</h3>');
		a=x.children
		for(var i in a){b.push('<br/>');btn(a[i]);}
		element.innerHTML=b.join('')
/*

.directive('tmplt',function($compile){
    var t1='<div>{{rootDirectory}}{{content.data }}</div>'
    var linker=function(scope,element,attrs){
        scope.rootDirectory='images/';
        element.innerHTML=t1//.html(t1)//.show();
        $compile(element.contents())(scope);
    }//linker
    return {restrict:'E',link:linker, scope:{content:'='}};
})
.directive('txtNodeTxt',function($compile){
	var t1='Id:<input ng-moedl="txtNode.txt.id"/> , Key:<input ng-model="txtNode.txt.key"/> , owner:<input ng-model="txtNode.txt.owner"/> , group:<input ng-model="txtNode.txt.group"/> , perm:<input ng-model="txtNode.txt.perm"/> , meta:<input ng-model="txtNode.txt.meta"/> , txt:<input ng-model="txtNode.txt.txt"/>'
	var linker=function(scope,element,attrs){
		element.innerHTML=t1
		$compile(element.contents())(scope);
	}//linker
	return {restrict:'E',link:linker, scope:{content:'='}};})
.directive('txtNodeChildren',function($compile){
	var linker=function(scope,element,attrs){
		var a=txtNode.children,b=[],c;
		for(var i in a){c=a[i];b.push('<br/><button>'+c.key+'@'+c.id+'</button>');}
		element.innerHTML=b.join('')
		$compile(element.contents())(scope);
	}//linker
	return {restrict:'E',link:linker, scope:{content:'='}};})
*/
		$compile(element.contents())(scope);
	}//linker
	return {restrict:'E',link:linker, scope:{content:'='}};})
