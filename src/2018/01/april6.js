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
	}

	return p;//function mainFactoryCallback(message) {return p;}
}])
.controller('mainCtrl',function mainCtrlController($scope,main ) {
	if(!main || !main.usr)
		return location.hash='#!/login'
	if(main){
		$scope.main=main
		$scope.dt=dt


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
.directive('tmplt',function($compile){
    var t1='<div>{{rootDirectory}}{{content.data }}</div>'
    var linker=function(scope,element,attrs){
        scope.rootDirectory='images/';
        element.html(t1).show();
        $compile(element.contents())(scope);
    }//linker
    return {restrict:'E',link:linker, scope:{content:'='}};
})
