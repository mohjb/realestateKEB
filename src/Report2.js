
var myApp = angular.module('myApp', []);
myApp.controller("myController", [
	"$scope",
	function($scope) {//,$http
		$scope.jspName='Report2.jsp'
		$scope.title='Realestate Report 2'
		$scope.range=function(a,b,c){
			if(c==undefined)c=1;
			if(b==undefined){b=a;a=0;}
			var r=[];
			for(var i=a;i<=b;i+=c)
			r.push(i);
			return r;}
		$scope.minmaxYears=$scope.range(2000,2016)
		$scope.from=2015
		$scope.to=2016
		$scope.terms=[
			{name:'aggregate',lbl:'aggregate',ar:'aggregate',base:1}
			,{name:'annual',lbl:'annual',ar:'year',base:1}
			,{name:'nineMonths',lbl:'nineMonths',ar:'nineMonths',base:1}
			,{name:'semi-annual',lbl:'semi-annual',ar:'half',base:2}
			,{name:'quarterly',lbl:'quarterly',ar:'quarter',base:4}
			,{name:'monthly',lbl:'monthly',ar:'monthl',base:12}
			,{name:'weekly',lbl:'weekly',ar:'week',base:52}
		]
		$scope.term=$scope.terms[0]
		$scope.ranks=[1,2,3,4]
		$scope.govs=[
		     {code:0,no:0,col:'gov',lang:{en:'all',ar:'كل'}}
		    ,{code:1,no:1,col:'gov',lang:{en:'Capital'          ,ar:'العاصمة'}}
		    ,{code:2,no:2,col:'gov',lang:{en:'Hawalli'          ,ar:'حولي'}}
		    ,{code:3,no:3,col:'gov',lang:{en:'Mubarek Alkabir'  ,ar:'مبارك الكبير'}}
		    ,{code:4,no:4,col:'gov',lang:{en:'Farwaniya'        ,ar:'فروانية'}}
		    ,{code:5,no:5,col:'gov',lang:{en:'Ahmedi'           ,ar:'احمدي'}}
		    ,{code:6,no:6,col:'gov',lang:{en:'Jahra'            ,ar:'جهراء'}}
		    ,{code:7,no:7,col:'gov',lang:{en:'Total'            ,ar:'إجمالي'}}
		];$scope.gov=$scope.govs[0]
		$scope.Statistics=[{name:'count',lbl:'count'}
			,{name:'avgPrice',lbl:'avgPrice'}//1
			,{name:'ttlPrice',lbl:'ttlPrice'}//2
			,{name:'minPrice',lbl:'minPrice'}//3
			,{name:'maxPrice',lbl:'maxPrice'}//4
			,{name:'avgArea' ,lbl:'avgArea'}//5
			,{name:'ttlArea' ,lbl:'ttlArea'}//6
			,{name:'minArea' ,lbl:'minArea'}//7
			,{name:'maxArea' ,lbl:'maxArea'} // ix 8
		];
		$scope.sttstcs={selected:[1,2,6,0],unselected:[3,4,5,7,8]}
		$scope.data=[
			{nm:1,ttl:'gov',tbl:[
				 [1,2,3,4,5,6,7,8,9,0]//sttstcs 0
				,[2,3,4,5,6,7,8,9,0,1]//sttstcs 1
				,[3,4,5,6,7,8,9,0,1,2]//sttstcs 2
				,[4,5,6,7,8,9,0,1,2,3]//sttstcs 3
				,[5,6,7,8,9,0,1,2,3,4]//sttstcs 4
				,[6,7,8,9,0,1,2,3,4,5]//sttstcs 5
				,[7,8,9,0,1,2,3,4,5,6]//sttstcs 6
				,[8,9,0,1,2,3,4,5,6,7]//sttstcs 7
				,[9,0,1,2,3,4,5,6,7,8]//sttstcs 8
			]}
			,{nm:2,ttl:'dasma',tbl:[
				 [11,12,13,14,15,16,17,18,19,20]//sttstcs 0
				,[12,13,14,15,16,17,18,19,10,11]//sttstcs 1
				,[13,14,15,16,17,18,19,10,11,12]//sttstcs 2
				,[14,15,16,17,18,19,10,11,12,13]//sttstcs 3
				,[15,16,17,18,19,10,11,12,13,14]//sttstcs 4
				,[16,17,18,19,10,11,12,13,14,15]//sttstcs 5
				,[17,18,19,10,11,12,13,14,15,16]//sttstcs 6
				,[18,19,10,11,12,13,14,15,16,17]//sttstcs 7
				,[19,10,11,12,13,14,15,16,17,18]//sttstcs 8
			]},
			{nm:3,ttl:'Daaya',tbl:[
				 [1,2,3,4,5,6,7,8,9,0]//sttstcs 0
				,[2,3,4,5,6,7,8,9,0,1]//sttstcs 1
				,[3,4,5,6,7,8,9,0,1,2]//sttstcs 2
				,[4,5,6,7,8,9,0,1,2,3]//sttstcs 3
				,[5,6,7,8,9,0,1,2,3,4]//sttstcs 4
				,[6,7,8,9,0,1,2,3,4,5]//sttstcs 5
				,[7,8,9,0,1,2,3,4,5,6]//sttstcs 6
				,[8,9,0,1,2,3,4,5,6,7]//sttstcs 7
				,[9,0,1,2,3,4,5,6,7,8]//sttstcs 8
			]}
		]

		$scope.yrsChange=function(){
			  $scope.yrs=$scope.range($scope.from,$scope.to);
			  $scope.timeline=[]
			  for(var i in $scope.yrs)
			  for(var j=1;j<=$scope.term.base;j++){
			  $scope.timeline.push([i,j])
				}
							  }
		$scope.yrsChange();
		//	$scope.app=$http.get('test.json')
	}
]);