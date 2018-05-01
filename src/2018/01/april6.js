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
.module('main', ['ngSanitize','angular-md5','ui.router','ng.jsoneditor'] ))
.factory('main', ['$http','md5', function mainFactory($http,md5) {
	var p=main={//window.xa||{}
	txt:{0:{id:0,parent:0,key:'root',owner:'moh',group:'moh',perm:0
			,txt:'',meta:{path:[[0,'root']],children:[[1,'users'],[2,'apps']]}}
		,1:{id:1,parent:0,key:'users',owner:'moh',group:'moh',perm:47
			,txt:'',meta:{path:[[0,'root']],children:[[3,'moh']]}}
		,2:{id:2,parent:0,key:'apps',owner:'moh',group:'moh',perm:47
			,txt:'',meta:{path:[[0,'root']],children:[[4,'app1']]}}
		,3:{id:3,parent:1,key:'moh',owner:'moh',group:'moh',perm:47
			,txt:'',meta:{path:[[1,'users'],[0,'root']],children:[]}}
		,4:{id:4,parent:2,key:'app1',owner:'moh',group:'moh',perm:47
			,txt:'',meta:{path:[[2,'apps'],[0,'root']],children:[[5,'key1'],[6,'key2']]}}
		,5:{id:5,parent:4,key:'key1',owner:'moh',group:'moh',perm:47
			,txt:'',meta:{path:[[4,'app1'],[2,'apps'],[0,'root']],children:[]}}
		,6:{id:6,parent:4,key:'key2',owner:'moh',group:'moh',perm:47
			,txt:'',meta:{path:[[4,'app1'],[2,'apps'],[0,'root']],children:[]}}
	}//Txt
	,selected:{id:4}
	,q:{ load:{m:{},q:[]}//,q:function(x,clb){var t=main.ls.q.load,n=t.m[x.id];if(!n){n={};}}
		,save:{m:{},q:[]}
		,intvl:0,trgt:new Date()
	}//q
	,load:function load(id,clb){
		var x=main.txt[id];
		if(!x)
			x=main.ls.load(id);
		if(!x){var q=main.q.load,n=q.m[id];
			if(n)
			{	n.clb.push(clb)
				return;}
			n={id:id,clb:[clb]}
			q.q.push(q.m[id]=n);
			q=main.q;
			if(!q.intrvl){
				q.intrvl=setInterval(function(){
					var i=q.qu.shift();$http
					.send({method:'Txt.load',url:xUrl+i.id})
					.then(function(respond){
						console.log('main.load.http.then:',i,arguments)
						var y=respond['return'];
						main.txt[y.id]=y
						main.ls.save(y)
						if(clb)
							clb(y);}
					 ,function(respond){
						console.log('main.load.http.then:err:',y,arguments)})
				},2)
		}}else clb(x);
		return x;}
	
	,save:function save(x,clb){
		var q=p.q.save,i=q.m[x.id];
		p.ls.save(x);
		if(i!=undefined)return;
		q.m[x.id]=q.q.length;q.q.push({x:x,clb:clb});
		if(!q.intrvl){
			q.intrvl=setInterval(function(){
				var x=q.q.shift();delete q.q.m[x.id];$http
				.send({method:'Txt.update',url:xUrl+x.id,data:JSON.stringify(x.x)})
				.then(function(respond){
						x.clb(x.x,respond['return']);},
					function(respond){
						x.clb(x.x,respond,'error')});
				if(q.q.length<1)clearInterval(q.intrvl);
			},2);}
		return x;}

	,ls:{offline:true,prefix:'TxtSrvlt'
		,load:function(id,clb){
			var x=JSON.parse(LocalStorage[p.ls.prefix+id]);
			if(x && clb)
				clb(x);
			return x;}

		,save:function(x){
			x.meta.lastModified=new Date();
			LocalStorage[p.ls.prefix+x.id]=JSON.stringify(x);}
	}//ls
	}//main
	var b={q:[1,2,3,4],clb:function(x)
		{   if(x){
				if(x.id==0)
				{	p.txt={};
					b.q=[];for(var i in x.meta.children)
						b.q.push(x.meta.children[i])
				}
				p.txt[x.id]=x;
				var c=b.q.shift();
				if(c)
					p.ls.load(c,b.clb);
			}//if x
		}//function clb
	}//b,x
	b.x=p.ls.load(0,b.clb);
	return p;
	}//function mainFactory
	]
	)//factory

.controller('mainCtrl',function mainCtrlController($scope,main ) {
	if(!main )//|| !main.usr
		return location.hash='#!/login'
	if(main){
		$scope.main=main
		$scope.dt=dt
		$scope.jsoneditorOptions={
			
			editorClass:[{lbl:'Text',code:'text'},{lbl:'JsonEditor',code:'json'},{lbl:'TinyMCE',code:'html'}
		,{lbl:'Blockly',code:'blockly'},{lbl:'Svg edit',code:'svg'},{lbl:'Html Img',code:'hImg'}
		,{lbl:'TincyMCE Pallette editor',code:'htmlPalete'},{lbl:'Svg editor pallette editor',code:'svgPalete'}
		,{lbl:'Blockly pallette editor',code:'blocklyPalete'},{lbl:'TincyMCE/AngularJS Pallette editor',code:'angPalete'}
		,{lbl:'Tinymce media file manager',code:'htmlMediaFM'},{lbl:'Svg editor media file manager',code:'svgMediaFM'}
		,{lbl:'Fragments list',code:'fragList'},{lbl:'API(Java/JS/NativeScript) components pallette',code:'apiPalette'}
		,{lbl:'Dia',code:'dia'},{lbl:'Visio templates',code:'diaPalette'},{lbl:'DB explorer',code:'dbExplorer'}
		,{lbl:'Uml' ,code:'uml'},{lbl:'use cases',code:'umlUC'},{lbl:'ER',code:'umlER'},{lbl:'flowchart',code:'umlFC'}
		,{lbl:'FSM',code:'fsm'},{lbl:'Mindmap',code:'mm'},{lbl:'dewaar',code:'dewaar'}
		,{lbl:'conversation structure',code:'conversationStruct'},{lbl:'SheetJS components pallette',code:'sheetJS'}
		,{lbl:'D3 charts',code:'d3'},{lbl:'Leaflet',code:'leaflet'},{lbl:'Html5 js Canvas',code:'canvas'}
		,{lbl:'WebGL',code:'webGL'},{lbl:'Maths/statistics/R formulas component pallette',code:'formulas'}
		,{lbl:'PivotTable.js',code:'pivot'}]
						
			,mode:'tree'}
		$scope.jsoneditorOptions.schema= {"properties": {"editorClass": {"enum":
			$scope.jsoneditorOptions. editorClass}}}
		$scope.selected=main.selected
		$scope.txt=main.load(main.selected.id)
		$scope.clk=function(id,prnt,ky){
			main.load(id,function(x,ref){main.selected.id=id;$scope.txt=x;
				$scope.editorClass=x.meta.editorClass||'text'
			});}
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

		$scope.clkSave=function clkSave(x){
			main.save(x,function(){console.log('mainCtrl.clkSave:',x,arguments);})

		}//clkSave
		
		$scope.editorClass='text'
		$scope.editorClassChanged=function editorClassChanged(){

			console.log('editorClassChanged:',arguments)
			$scope.txt.meta.editorClass=$scope.editorClass;
		}

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
