(window.xa=(angular||{})
.module('main', ['ngSanitize','ui.router','gridshore.c3js.chart'] ))
.factory('main', ['$http', function mainFactory($http,md5,c3chart) {
	var p=main={
		tsv:''
		,list:{}
		,chart:0
		,addChart:function(a){
			console.log('main.addChart',a);
			main.chart=a;
			return a;}
	}//main
	return p;
	}//function mainFactory
	]
	)//factory

.controller('mainCtrl',function mainCtrlController($scope,main ) {
	if(!main )//|| !main.usr
		return location.hash='#!/login'
	if(main){
		$scope.main=main;main.scope=$scope
		$scope.sector=0
		$scope.indiList=0
		$scope.selected=main.selected
		$scope.generateArray=function(a,b,c){var r=[];for(var i=a;i<=b;i++)r.push(c!=undefined?c:i);return r;}

		$scope.yrs=$scope.generateArray($scope.yrsFrom=2004,$scope.yrsTo=2018)

		$scope.setMode=function(x){
			console.log('mode',x)
			$scope.mode=x;
		}
		$scope.clkSector=function(x){
			console.log('clkSector',x)
			$scope.sector=x;
		}
		$scope.chk=function(x){
			console.log('chk',x)
			if(x.checked)
			{	main.list[x[0]]=x
				if(main.chart)
					main.chart.load({columns:[[x.name].concat(x.series)]})
			}
			else
			{	delete main.list[x[0]]
				if(main.chart)
					main.chart.unload({ids:[x.name]})
			}
		}

		$scope.onLoadTsv=function(){
			function initVars(p,i,o){
				for(;i< p.lines.length;i++)
				{	var s=p.lines[i],cntryNm
					, c=s.trim().split('\t')
					if(c&&c.length>0){//o.a.push(c)
						var inx=p.indices.id[c[4]],cntry,yr
						if(!inx)
							console.error('no indx',c)
						else{if(!inx.cntry)
								inx.cntry={}
							cntry=inx.cntry[cntryNm=p.countries.id[c[3]][2]]
							if(!cntry)
							{	cntry=inx.cntry[cntryNm]={}
								if(cntryNm=='KWT')
								{var a=[];a.length=$scope.yrsTo-$scope.yrsFrom+1
									inx.series=a.fill(null)//$scope.generateArray(,,null)
							}}
							yr=cntry[c[5]]
							if(!yr)
								yr=cntry[c[5]]={}
							if(yr.a)
								console.error('var ',c)
							yr.a=c
							yr.rank =c[1];
							yr.value=c[2]
							if(cntryNm=='KWT')
								inx.series[c[5]-$scope.yrsFrom]=c[1]
						}//o.id[c[0]]=c
					}
				}
				
				console.log('done',p)
				main.tsv=p;}
			function initIndices(p,i,o){
				p.countries.code={}
				p.indices.code={}
				p.sectors.code={}
				for(var c in p.sectors.id)
				{var x=p.sectors.id[c];
					p.sectors.code[x[2]]=x
				}
				for(var c in p.countries.id)
				{var x=p.countries.id[c];
					p.countries.code[x[2]]=x
				}
				for(; i < p.lines.length;i++)
				{ var s=p.lines[i]
					, c=s.trim().split('\t'),d
					if(c&&c.length>0){
						if(c.length<2){
							nm=c[0];//}else if(o.h.length==0){
							if(nm=='vars')
							{	s=p.lines[++i];
								p[nm]=o={h:s.trim().split('\t')}//,a:[],id:{}
								return initVars(p,++i,o)
							}
						}
						else{c.name=c[1];c.description=c[2]
							o.a.push(c)
							o.id[c[0]]=c
							try{c.m=JSON.parse(c[3]);
								o.code[d=c.iCode=c.m['Series code']||c.m.code]=c
								if(!c.m.description)
									c.m.description=c.description
							}catch(ex){
								o.code[d=c.iCode=c[0]]=c
							}
							var sctr=p.sectors.id[c[5]],parnt=o.id[c[4]]
							c.sector=sctr;c.parent=parnt;
							if(parnt)
							{if(!parnt.c)
									parnt.c=[]
								parnt.c.push(c)
								sctr.indisIds[c[0]]=c
							}else if(sctr)
							{	if(!sctr.indisIds)
									sctr.indisIds={}
								if(!sctr.c)
									sctr.c={}
								sctr.c[c[0]]=sctr.indisIds[c[0]]=c
							}else 
								console.error('index no sector',c)}}}}
			console.log('loaded preTsv ',this,arguments)
			var p={},s=p.tsv=preTsv&&(preTsv.textContent||preTsv.data),nm,o
			p.lines=s.split('\n')
			for(var i in p.lines)
			{ s=p.lines[i]
				var c=s.trim().split('\t')
				if(c&&c.length>0){
					if(c.length<2){
						if(nm=c[0])
						p[nm]=o={h:[],a:[],id:{}}
					}else if(o.h.length==0){
						o.h=c;
						if(nm=='indices')
							return initIndices(p,++i,o)
					}
					else{c.name=c[1];
						o.a.push(c)
						o.id[c[0]]=c
					}
				}
			}
			console.log('loaded preTsv:done',p)
		}

	}else{
		$scope.usr=null;
		console.log('mainCntrl:version=',$scope.version='mainCntrl , no app')
	}
})

function nxtSibling(t,nm){
	var n=t.nextSibling
	while(n&&n.nodeName!=nm)
		n=n.nextSibling
	return n;}
