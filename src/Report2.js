
var myApp = angular.module('myApp', ['ngSanitize']);
Lix={//Label Index
	 zero			: {code:0 ,name:'zero		  '}
	,dir			 : {code:1 ,name:'dir		   '}
	,title		   : {code:2 ,name:'title		 '}
	,from			: {code:3 ,name:'from		  '}
	,to			  : {code:4 ,name:'to			'}
	,statistics	  : {code:5 ,name:'statistics	'}
	,term			: {code:6 ,name:'term		  '}
	,ContractType	: {code:7 ,name:'ContractType  '}
	,gov			 : {code:8 ,name:'gov		   '}
	,sector		  : {code:9 ,name:'sector		'}
	,realestateType  : {code:10,name:'realestateType'}
	,Query		   : {code:11,name:'Query		 '}
	,AreaName		: {code:12,name:'AreaName	  '}
	,Desc			: {code:13,name:'Desc		  '}
	,first		   : {code:14,name:'first		 '}
	,second		  : {code:15,name:'second		'}
	,third		   : {code:16,name:'third		 '}
	,fourth		  : {code:17,name:'fourth		'}
	,total		   : {code:18,name:'total		 '}
	,Registered	  : {code:19,name:'Registered	'}
	,Agent		   : {code:20,name:'Agent		 '}
	,Agg			 : {code:21,name:'Agg		   '}
	,AggPeriod	   : {code:22,name:'AggPeriod	 '}
	,Annual		  : {code:23,name:'Annual		'}
	,nineMonths	  : {code:24,name:'nineMonths	'}
	,Half			: {code:25,name:'Half		  '}
	,SemiAnnual	  : {code:26,name:'SemiAnnual	'}
	,Quarter		 : {code:27,name:'Quarter	   '}
	,Quarterly	   : {code:28,name:'Quarterly	 '}
	,Month		   : {code:29,name:'Month		 '}
	,Monthly		 : {code:30,name:'Monthly	   '}
	,Week			: {code:31,name:'Week		  '}
	,Weekly		  : {code:32,name:'Weekly		'}
	,Count		   : {code:33,name:'Count		 '}
	,TotalPrice	  : {code:34,name:'TotalPrice	'}
	,AvgPrice		: {code:35,name:'AvgPrice	  '}
	,MaxPrice		: {code:36,name:'MaxPrice	  '}
	,MinPrice		: {code:37,name:'MinPrice	  '}
	,AvgArea		 : {code:38,name:'AvgArea	   '}
	,TotalArea	   : {code:39,name:'TotalArea	 '}
	,MaxArea		 : {code:40,name:'MaxArea	   '}
	,MinArea		 : {code:41,name:'MinArea	   '}
	,kisr			: {code:42,name:'kisr		  '}
	,ted			 : {code:43,name:'ted		   '}
	,mohjb		   : {code:44,name:'mohjb		 '}}

did=function(p){return document.getElementById(p);}
myApp.controller("myController", [
	"$scope","$http",
	function($scope,$http) {
		$scope.jspName='../../t.jsp'
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

myApp.filter('numberSpelling',function(){
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
	,19:{en:'Septendecillion'	,ar:'سيبتنديسيليون' }*/
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
		var o=Math.floor(v%10),t=Math.floor(v/10)%10
		if(v>99){hnds(Math.floor(v/100)%10);if(o+t>0)apnd(a.w);}
		//en
		if(t){r.en.push(a[1].t[t].en);if(o)r.en.push(a.s.en);}
		if(o)r.en.push(a[1][o].en);
		//ar
		if(o){r.ar.push(a[1][o].ar);if(t)r.ar.push(a.w.ar);}
		if(t)r.ar.push(a[1].t[t].ar)
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