
var myApp = angular.module('myApp', ['ngSanitize']);
Lix={//Label Index
	 zero            : {code:0 ,name:'zero          '}
	,dir             : {code:1 ,name:'dir           '}
	,title           : {code:2 ,name:'title         '}
	,from            : {code:3 ,name:'from          '}
	,to              : {code:4 ,name:'to            '}
	,statistics      : {code:5 ,name:'statistics    '}
	,term            : {code:6 ,name:'term          '}
	,ContractType    : {code:7 ,name:'ContractType  '}
	,gov             : {code:8 ,name:'gov           '}
	,sector          : {code:9 ,name:'sector        '}
	,realestateType  : {code:10,name:'realestateType'}
	,Query           : {code:11,name:'Query         '}
	,AreaName        : {code:12,name:'AreaName      '}
	,Desc            : {code:13,name:'Desc          '}
	,first           : {code:14,name:'first         '}
	,second          : {code:15,name:'second        '}
	,third           : {code:16,name:'third         '}
	,fourth          : {code:17,name:'fourth        '}
	,total           : {code:18,name:'total         '}
	,Registered      : {code:19,name:'Registered    '}
	,Agent           : {code:20,name:'Agent         '}
	,Agg             : {code:21,name:'Agg           '}
	,AggPeriod       : {code:22,name:'AggPeriod     '}
	,Annual          : {code:23,name:'Annual        '}
	,nineMonths      : {code:24,name:'nineMonths    '}
	,Half            : {code:25,name:'Half          '}
	,SemiAnnual      : {code:26,name:'SemiAnnual    '}
	,Quarter         : {code:27,name:'Quarter       '}
	,Quarterly       : {code:28,name:'Quarterly     '}
	,Month           : {code:29,name:'Month         '}
	,Monthly         : {code:30,name:'Monthly       '}
	,Week            : {code:31,name:'Week          '}
	,Weekly          : {code:32,name:'Weekly        '}
	,Count           : {code:33,name:'Count         '}
	,TotalPrice      : {code:34,name:'TotalPrice    '}
	,AvgPrice        : {code:35,name:'AvgPrice      '}
	,MaxPrice        : {code:36,name:'MaxPrice      '}
	,MinPrice        : {code:37,name:'MinPrice      '}
	,AvgArea         : {code:38,name:'AvgArea       '}
	,TotalArea       : {code:39,name:'TotalArea     '}
	,MaxArea         : {code:40,name:'MaxArea       '}
	,MinArea         : {code:41,name:'MinArea       '}
	,kisr            : {code:42,name:'kisr          '}
	,ted             : {code:43,name:'ted           '}
	,mohjb           : {code:44,name:'mohjb         '}}

did=function(p){return document.getElementById(p);}
myApp.controller("myController", [
	"$scope","$http",
	function($scope,$http) {
		$scope.jspName='t.jsp'
		$scope.lang='ar'
		$scope.Lix=Lix
		$scope.label=function(ix,lng){
			var x=$scope.dict||0
			x=x.lookup||0
			x=x.label||0
			x=ix?x[ix.code]||ix:x
			x=x[lng||$scope.lang]||(ix&&ix.name)
			return x||'.';
		}
		$scope.lblTag=function(ix){
			return '<span class="langAr">'
			+$scope.label(ix,'ar')+'</span><span class="langEn">'
			+$scope.label(ix,'en')+'</span>';}

		$scope.range=function(a,b,c){
			if(c==undefined)c=1;
			if(b==undefined){b=a;a=0;}
			var r=[];
			for(var i=a;i<=b;i+=c)
			r.push(i);
			return r;}
        $scope.from=2000;$scope.to=2017;$scope.rowsBy='gov';
		$scope.yrsChange=function(){
			if( !$scope.term
			||$scope.term.name=='aggregate'
			|| !$scope.dict
			|| !$scope.dict.terms
			|| !$scope.dict.terms[0]
			|| $scope.term==$scope.dict.terms[0]  )
			    $scope.yrs=[$scope.from+' - '+$scope.to];
			    else $scope.yrs=$scope.range($scope.from,$scope.to);
			$scope.timeline=[];if($scope.term)
			for(var i in $scope.yrs)
			for(var j=1;j<=$scope.term.base;j++)
				$scope.timeline.push([i,j])
		}
		$scope.yrsChange();
		$scope.app=$http.get($scope.jspName).
			then(function(response){
					console.log('cntrlr:$http.get.then:$scope='
						,$scope,' ,response=',response)
					$scope.dict=response.data
					$scope.minmaxYears=$scope.range(
						$scope.from=$scope.dict.minmaxYear[0],
						$scope.to=$scope.dict.minmaxYear[1])
					$scope.gov=$scope.dict.lookup.gov[0]
					$scope.sttstcs={selected:[1,2,6,0],unselected:[3,4,5,7,8]}
					$scope.term=$scope.dict.terms[0]
					$scope.contract=$scope.dict.contrcts[0]
					$scope.typ=$scope.dict.lookup.type[0]
					$scope.sector=$scope.dict.lookup.sector[0]
				}//$http.get.response
				, function myError(response) {
					console.log('cntrlr:$http.get.then:error:$scope='
						,$scope,' ,arguments=',arguments)
				}
			)

		$scope.getQueryParams=function cntrlrQueryParams(){
			var p={
			 "from"		:$scope.from
			,"to"		:$scope.to
			,"gov"		:$scope.gov.code
			,"sttstcs"	:$scope.sttstcs.selected
			,"terms"	:$scope.term.name
			,"contract"	:$scope.contract.name
			,"typ"		:$scope.typ.code
			,"sector"	:$scope.sector.code
			,rowsBy:$scope.rowsBy||'gov'
			}
			return p;
		}
		$scope.onClk=function cntrlrOnclk(){
			var params=$scope.getQueryParams();
			did('btn').disabled=true
			did('imgWait').style.display=''
			did('dataTbl').style.display='none'
			console.log('cntrlrOnclk',$scope,params)
			$http.post($scope.jspName,params).then(
				function (response) {var data=response.data
					console.log('cntrlr:onClk:$http.post.then:$scope='
						,$scope,' ,arguments=',arguments)
					did('btn').disabled=false
					did('imgWait').style.display='none'
					did('dataTbl').style.display=''
					$scope.data=data['return']
					$scope.yrsChange();
				}
				, function myError(response) {
					console.log('cntrlr:onClk:$http.post.then:error:$scope='
						,$scope,' ,arguments=',arguments)
					did('btn').disabled=false
					did('imgWait').style.display='none'
				}
			)
		}

		$scope.switchLang=function(){
			var ar=$scope.lang!='ar';
			$scope.lang=ar?'ar':'en';
			document.body.dir=ar?'rtl':'ltr';
			document.styleSheets[1].rules[0].style.display=ar?'':'none'
			document.styleSheets[1].rules[1].style.display=ar?'none':''
		}

	}
]);
myApp.filter('fltr',function(){
	return function(p,op1){
		function intgr(p)
		{var x=Math.abs(p),b=[],s
			if(x<1000)
				return p;
			while(x>999)
			{s=Math.floor(x%1000).toString();
				if(s.length==1)s='00'+s;else if(s.length==2)s='0'+s;
				b.push(s,',')
				x=Math.floor(x/1000)
			}b.push(x)
			if(p<0)b.push('-')
			b.reverse();
			return b.join('')}
		function num(p)
		{var x=Math.abs(p),s=Math.floor((x*1000)%1000).toString(),b;
			if(s.length==1)s='00'+s;else if(s.length==2)s='0'+s;
			b=[s,'.',intgr(Math.floor(x))];
			if(p<0)b.push('-')
			return b.reverse().join()}
		if(!p)return p;
		switch(op1||0)
		{case 'integer':return intgr(p)
			case "number":return num(p);
			case 'currency':return num(p)+'KD'
			default:var x=Math.abs(p),
			s=Math.floor((x*1000)%1000)
			return s==0?intgr(p):num(p);
		}
	}
})
myApp.filter('filtr',function(){
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
			b=intgr(Math.floor(x))+'.<span class="num0">'+s+'</span>>';
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