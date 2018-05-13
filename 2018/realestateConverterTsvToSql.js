function toTbl(weeks,output){
	var b=['<table border="1">']
	for(var iw in weeks){var w=weeks[iw]||{}
		for(var it in w.transactions){
			var t=w.transactions[it]||{},ix=0
			b.push('<tr>')
			for(var ic in t)try{
				var c=t[ic]||{};ix++
				if(typeof(c) =='string')
					b.push('<td>',c,'</td>\n')
				else if( ic==6 || ic==9)
					b.push('<td>',c[1],'</td>\n')
				else 
				{b.push('<td>')
					try{
					b.push(JSON.stringify(c))
					}catch(ex){
						console.error(ex,'ic=',ic,c,'it=',it,t,'iw=',iw,w);b.push('area:',c);}
				 b.push('</td>')
				}
			}catch(ex){
				console.error(ex,'ic=',ic,c,'it=',it,t,'iw=',iw,w);}
			while(++ix<15)
				b.push('<td></td>')
			try{b.push('<td> -- ',t&&t[6]&&t[6][0],'\t',t&&t[9]&&t[9][0],'</td>')
			}catch(ex){
				console.error(ex,'it=',it,t,'iw=',iw,w);}
			b.push('</tr>')
		}
	}
	b.push('</table>')
	output.innerHTML=b.join('');
}

function f(){
	function iterate(x,z,d,c){
		var dn=d.length,
		i={x:x,z:s.indexOf(d,x),i:-1}
		while(i.z!=-1&&i.z<z){i.i++
			c(i);
			i.x=i.z+dn;
			i.z=s.indexOf(d,i.x);
		}if(i.z>z || i.z==-1 )
			i.z=z;
		i.i++;
		c(i)
	}//function iterate
 var /**currentWeek*/cw=0,weeks=[],sn=no.value	//declaring 7 closure variables
	,delimiter={a:'\n'//bi:{					//( almost like globalVars)
		,b:'الموقع	المساحة	الوصف	نوع العقار	الثمن	المحافظة	ملاحظه	'
		,c:'الموقع	القطعه	المساحة	الوصف	نوع العقار	الثمن	محافظة	ملاحظات	'
		,d:'محافظ'//'محافظة'
		,e:'\t'}
	,def=['year','month','week','date']
	,s=x.value,slen=s.length							//a (nested closure-scope 1)
 weeks.a=iterate(0,slen,delimiter.a,function(b){		//b (nested closure-scope 2)
	iterate(b.x,b.z,delimiter.b,function(c){			//c (nested closure-scope 3)
		if(c.i==0)// allocating a new week
		{	/**currentWeek*/cw=
			{type:1,gov:{}
				,/**currentTransaction*/ct:[]
				,countTrailingEmptyCells:0
				,preColumn910:true};
			cw.transactions=[cw.ct];}
		else 
			{cw.type=c.i+1;cw.govNm=null;}
		iterate(c.x,c.z,delimiter.c,function(d){		//d (nested closure-scope 4)
			iterate(d.x,d.z,delimiter.d,function(e){	//e (nested closure-scope 5)
				iterate(e.x,e.z,delimiter.e,function(f){//f (nested closure-scope 6)
					var v=s.substring(f.x,f.z)
					if(c.i==0&&d.i==0)
					{	if(e.i==0)
						{var col=def[f.i] || f.i
							if(def[f.i]||v!='')cw[col]=v
							if(col=='week')//(add a new entry in the top scope variable)
								weeks[cw.week]=cw;		//The Database
						}
						else if(f.i==0)
						 cw.gov[cw.govNm=v.indexOf('حول')!=-1?'حولي':v.replace('ة ','')
							.replace('ه ','').replace(':','').replace('-','').trim()]=[]//cw.ct
					}else if ( e.i==0)//ci==1 ||
					{var fr=v&&v.length?getArea(v):null
						if(fr){
							if(cw.transactions.length==1&&cw.transactions[0].length<1)
								cw.transactions.shift()
							cw.transactions.push(cw.ct=//create a new transaction
								[++sn, cw.year, cw.month, cw.week, cw.date,cw.type,fr])
							if(cw.type==2)
								cw.ct.push('')
							if( cw.gov[cw.govNm])
								cw.gov[cw.govNm].push(cw.ct)
							cw.preColumn910=true;
							return cw.ct;
						}var bool=true
						if(cw.ct.length>13 )
						{if(! (bool=(v!='' && v)))
							{	if(++cw.countTrailingEmptyCells%400==1)
									console.log(cw.ct[6],cw.ct[9],cw.ct,cw.countTrailingEmptyCells)
							}
							if(cw.ct.length==14||cw.ct.length==19)
								console.log(cw.ct[6],cw.ct[9],cw.ct[13],cw.ct)
						}if(bool)
							cw.ct.push(v)
					}
					else if(e.i==1 && f.i==0)
					{cw.govNm=v
						if(!cw.gov[cw.govNm])
							cw.gov[cw.govNm]=[cw.ct]
					}
					if(cw.ct.length==11&&cw.preColumn910)
					{var z=cw.ct,z910=z[9]+z[10]
						,z9=getType(z910);
						z[9]=z9? z9: z910;
						z.pop();cw.preColumn910=false
					}
					return v;
				})
				return e
			})
			return d;
		})
		return c;
	})
	return b;
 })//weeks.a
 toTbl(weeks,document.body);//.getElementById('b')
 return weeks;//return the root of the tree( the head of all the structured data)
}//function f


function getArea(s){//2nd version of the func
	function mtch(x,y)
	{var c=0,epsilon=0.99
		for(var i in x)
		  if(i!='len' && i!='hash' && y[i])//(i==' ') ||(y[i]&& y[i][1] == x[i][1])
			c++;
		return c>= epsilon 
		  * Math.max( x.len , y.len )
	}//function mtch
	var x=histogram(s),p;
	p=area.hash[x.hash];if(p)
		return p.area;
	for(var i=0;i<area.length;i++)
	{	p=area[i];
		if(s==p[0])
			return p;
		if(mtch(x,p[2]))
			return p;//a second return-statement and not merged (i.e. or) two cases, just for breakpoints for debugging 
	}//for
  if(trace)console.log('getArea:not found:',s,',row=',f.x.row,',weekIx=',f.x.weekIx);
}//getArea2

function getType(s){
  if( ! window.pType ){
	var p=window.pType=[],
	v=document.getElementById('ptyp').value,
	a=v.split('\n'),i,t;
	for( i=0;i<a.length;i++){
		t=a[i].split(',');
		p[acw(t[0])]=t;p[t[1]]=t;
	}//for
  }// if( ! window.pType)
 var w=acw(s),
 x=pType[w];if(!x && trace)
	console.log('getType:not found:',w,',row=',f.x.row,',weekIx',f.x.wkIx);
 return x;
}//function getType

function ac(x){//Arabic Categorization of char-x
		/*var q=[[' ','\n','\t','-','\r',','],
			['ا','أ','إ','آ'],
			['ى','ي'],
			['ة','ه'],
			['و','ؤ']]
	  for(var i=0;i<q.length;i++)
		for(var j=0;j<q[i].length;j++)
			if(x==q[i][j])
				return q[i][0];*/
	  if(!ac.q){
		var q=[[' ','\n','\t','-','\r',','],
			['ا','أ','إ','آ'],
			['ى','ي'],
			['ة','ه'],
			['و','ؤ']]
		var o=ac.q={};//lookup for chars
		for(var i=0;i<q.length;i++)
			for(var j=0;j<q[i].length;j++)
				o[q[i][j]]=q[i];
	  }var lu=ac.q,q=lu[x];
	  if(q)
		return q[0];
	  return x;
	}//function ac

function acw(s){
	var b=[],c;for(var i=0;i<s.length;i++){
		c=ac(s[i]);if(c!=' ')
		b.push(c);
	}return b.join('');
	}// function acw(s)

function histogram(s){
	  var i,h={len:0,hash:''},x,c,n= s.length;
	  for(i=0;i< n ;i++){
		c=s[i];
		c=ac(c);
		x=h[c];
		if(x)
			x[1]++;
		else if(c!=' ')
		{h.len++;h.hash+=c;h[c]=[c,1];}
	}return h;}//function histogram

trace=false;

onload=function(){
	area=resetAreasList();}

function resetAreasList(){
  var i,a=document.getElementById('area').value.split('\n');a.hash={};
  for(i=0;i<a.length;i++){
	a[i]=a[i].split(',');
	var h=a[i][2]=histogram(a[i][0]);
	a.hash[h.hash]=h;h.area=a[i]
  }return a;}

function fixY(){
	var s=document.getElementById('x').value
	b=[],
	j=0,
	i=s.indexOf(' ');
	while(i!=-1)
	{	if(s.charCodeAt(i+2)==1612)
		{	b.push(s.substring(j,i));
			b.push('ي');
			b.push(s.charAt(i+1));
			j=i+3;
		}
		i=s.indexOf(' ',i+1);
	}
	b.push(s.substring(j))//return b;
  document.getElementById('x').value=b.join('');
}


function f_old(){var /**currentWeek*/cw=0,weeks=[],sn=no.value	//declaring 5 closure variables
	,delimiter={a:'\n'//bi:{								//( almost like globalVars)
		,b:'الموقع	المساحة	الوصف	نوع العقار	الثمن	المحافظة	ملاحظه	'
		,c:'الموقع	القطعه	المساحة	الوصف	نوع العقار	الثمن	محافظة	ملاحظات	'
		,d:'محافظ'//'محافظة'
		,e:'\t'}
	,def=['year','month','week','date']
weeks.a=x.value.split(delimiter.a).							//a (nested closure-scope 1)
map(function(b,bi){											//b (nested closure-scope 2)
	var br=b.split(delimiter.b).
	map(function(c,ci){										//c (nested closure-scope 3)
		if(ci==0)// allocating a new week
		{	/**currentWeek*/cw=
			{type:1,gov:{}
				,/**currentTransaction*/ct:[]
				,countTrailingEmptyCells:0
				,preColumn910:true};
			cw.transactions=[cw.ct];}
		else 
		{	cw.type=ci+1;cw.govNm=null;}
		var cr= c.split(delimiter.c).
			map(function(d,di){								//d (nested closure-scope 4)
				var dr= d.split(delimiter.d).
				map(function(e,ei){
					var er= e.split(delimiter.e).			//e (nested closure-scope 5)
					map(function(f,fi){						//f (nested closure-scope 6)
						if(ci==0&&di==0)
						{	if(ei==0)
							{var col=def[fi] || fi
								if(def[fi]||f!='')cw[col]=f
								if(col=='week')//(add a new entry in the top scope variable)
									weeks[cw.week]=cw;		//The Database
							}
							else if(fi==0)
							 cw.gov[cw.govNm=f.indexOf('حول')!=-1?'حولي':f.replace('ة ','')
								.replace('ه ','').replace(':','').replace('-','').trim()]=[]//cw.ct
						}else if ( ei==0)//ci==1 ||
						{var fr=f&&f.length?getArea(f):null
							if(fr){
								if(cw.transactions.length==1&&cw.transactions[0].length==1)
									cw.transactions.shift()
								cw.transactions.push(cw.ct=//allocate a new transaction
									[++sn, cw.year, cw.month, cw.week, cw.date,cw.type,fr])
								if(cw.type==2)
									cw.ct.push('')
								if( cw.gov[cw.govNm])
									cw.gov[cw.govNm].push(cw.ct)
								cw.preColumn910=true;return cw.ct;
							}var bool=true
							if(cw.ct.length>13 )
							{	if(! (bool=(f!='' && f)))
								{	if(++cw.countTrailingEmptyCells%400==1)
										console.log(cw.ct[6],cw.ct[9],cw.ct,cw.countTrailingEmptyCells)
								}
								if(cw.ct.length==14||cw.ct.length==19)
									console.log(cw.ct[6],cw.ct[9],cw.ct[13],cw.ct)
							}if(bool)
								cw.ct.push(f)
						}
						else if(ei==1 && fi==0)
						{cw.govNm=f
							if(!cw.gov[cw.govNm])
								cw.gov[cw.govNm]=[cw.ct]
						}
						if(cw.ct.length==11&&cw.preColumn910)
						{var z=cw.ct,z910=z[9]+z[10]
							,z9=getType(z910);
							z[9]=z9? z9: z910;
							z.pop();cw.preColumn910=false
						}
						return f;
					})
					return er
				})
				return dr;
			})
		return cr;
	})
	return br;
})//weeks.a
toTbl(weeks,document.getElementById('b'));
return weeks;//return the root of the tree( the head of all the structured data)
}//function test

/*error inclined:

104037	2016	4	16	17/04/2016	1	87	392	بيت	خاص200,000.00	5				-- جابر العل 1	خ

105646	2016	8	35	28/08/2016	1	106	3	500	3	200,000.00	3	{}	ابو الحصانيه الشريط الساحلي	10	750	ارض	خاص	625,000.00	4	-- صباح الناصر	ارض	خاص

*/
