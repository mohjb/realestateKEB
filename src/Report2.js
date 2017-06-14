
var myApp = angular.module('myApp', ['ngSanitize']);
Lix={
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
	,mohjb           : {code:44,name:'mohjb         '}
}

myApp.controller("myController", [
	"$scope","$http",
	function($scope,$http) {//
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
		$scope.app=$http.get($scope.jspName/*, { transformResponse: [function (data) {
				//console.log('cntrlr:$http.get.transformResponse:$scope=',$scope,' ,data=',data,' ,arguments=',arguments)
				if(!myApp.serverResponses)
					myApp.serverResponses=[]
				myApp.serverResponses.push(data)
				return data; }] }*/).
			then
			(function(response)
				{	console.log('cntrlr:$http.get.then:$scope='
						,$scope,' ,response=',response)
					var data=response.data
					/*if(typeof data =='string'){
						try{
							data=JSON.parse(data)
						}catch(ex){
							console.error('cntrlr:$http.get.then:parse response.data',ex);}
					}*/
					if(!myApp.serverResponses)
						 myApp.serverResponses=[data]//$scope.dict]
					else myApp.serverResponses.push(data)
					$scope.dict=data

					$scope.minmaxYears=$scope.range($scope.from=$scope.dict.minmaxYear[0],$scope.to=$scope.dict.minmaxYear[1])
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
			var params=$scope.getQueryParams()
			console.log('cntrlrOnclk',$scope,params)
			$http.post($scope.jspName,params).then(
				function (response) {var data=response.data
					console.log('cntrlr:onClk:$http.post.then:$scope='
						,$scope,' ,arguments=',arguments)
					myApp.serverResponses.push(data)
					$scope.data=data['return']
					$scope.yrsChange();
				}
				, function myError(response) {
					console.log('cntrlr:onClk:$http.post.then:error:$scope='
						,$scope,' ,arguments=',arguments)
				}
			)
		}

		$scope.switchLang=function(){
			var ar=$scope.lang=='ar';
			$scope.lang=(ar=!ar)?'ar':'en';
			document.body.dir=ar?'rtl':'ltr';
			document.styleSheets[1].rules[0].style.display=ar?'':'none'
			document.styleSheets[1].rules[1].style.display=ar?'none':''
		}
	}
]);