
function d(){
 var rows=document.getElementById('b').
	firstElementChild.rows
 ,no=document.getElementById('no')
 ,o={ y:document.getElementById('y')
	, m:document.getElementById('m')
	, w:document.getElementById('w')
	, d:document.getElementById('day')
	}
 , frx=function frx(r,x){return r&&r.children&&r.children[x]?r.children[x].innerText:''}
 , fno=function fno(p){return ++no.value}
 //, fd=function fd(p){return '"'+o.y.value+'/'+o.m.value+'/'+o.d.value+'"'}
 , f11=function f11(r,x){return '"'+frx(r,x)+'"';}
 , f05=function f05(r,x){return frx(r,x).replace(/,/g,'');}//area
 , map=[fno,	'y','m','w',	 9,[f05,4], [f05,8],0,	 1,	 5,	'd',[f11,3],[f11,10], 2 ,6 ,7]	//fd
 //		00		1	02	03		04	05		06		07	08	09	10	11		12		 13	 14 15
 , b=['<textarea>insert into `realestate`.`d` values'];/*sql table `realestate`.`d`(
00: no int(11) PK 
01: y int(11) 
02: m int(11) 
03: w int(11) 
04: gov int(11) 
05: area decimal(65,3) 
06: price decimal(65,3) 
07: contract int(11) 
08: name int(11) 
09: type int(11) 
10: d date 
11: block text 
12: notes text)
 */
 for(var i in rows)
 {var r=rows[i];//if(i==0)b.push(i>0?'(':'\n,(');//<tr>console.log('d:r=',r);
	if(r.cells.length==4){
		
		
		
	}else
	for(var c in map)
	{var x=map[c],t=typeof(x);//console.log('d:c=',c);
		b.push(c>0?' , ':(i>0?'\n,(':'('));//\n\t<td>
		if(x instanceof Function)
			b.push(x(r));//console.log('d:func',x);
		else if(x instanceof Array && x.length>0 && x[0] instanceof Function)
			b.push(x[0](r,x[1]));//console.log('d:func',x);
		else if(t=='string' )
			b.push(o[x].value);//console.log('d:str',x);
		else
			b.push(frx(r,x));
		//b.push(',');//</td>
	}//for c in cells
	//b.push(')');//</tr>
 }//for i in rows
 b.push('</textarea>');
 document.getElementById('d').innerHTML=b.join('');
}//function d

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

function resetAreasList(){
  var i,a=document.getElementById('area').value.split('\n');a.hash={};
  for(i=0;i<a.length;i++){
	a[i]=a[i].split(',');
	var h=a[i][2]=histogram(a[i][0]);
	a.hash[h.hash]=h;h.area=a[i]
  }return a;}

function f(){
 var x={weeks:document.getElementById('x').value.split('\n'),wkIx:-1,a:0
 ,row:0,c:0,i:0,x:0
 ,b:['<table border="1">\n']
 ,m:parseInt(document.getElementById('max').value)};
 function matchHeading(){
	if(x.a[x.i]==f.head[0]){
	document.getElementById('max').
	value=x.m=(x.a[x.i+1]==f.head[1])?8:7;
	var j=detectRow(x.m-1);x.i=j;}}

 //function findNextNum(i){while(i+x.i<x.a.length && !f.regexNum.test(x.a[i+x.i]))i++;return i;}

 function findNextPatt(i){
  var 	num=f.regexNum,
	txt=f.regexTxt,
	//patt:mw83,863h,msa7a,w9f,nw3el38ar,thmn,m7ftha,mla7that
	patt=[any,any,num,txt,txt,num,num],
	q=patt.length,j=0,b= j >=q,n=x.a.length;i--
	while(!b && (b?i:++i)+x.i<n )
		for( j=0;j<q && j+i+x.i<n 
		&& (b=patt[j].test( x.a[j+i+x.i ] )) ;j++);
	return b&&j>=q?x.i+i:-1;}

 function detectRow(i){//m= 7 or 8
	var j=findNextPatt(i);
	return j==-1?j:j-(x.m==7?1:2);
	}//function detectRow
 f.x=x;
 
 while ( x.wkIx+1<x.weeks.length && x.i>=0 )
 {x.a=x.weeks[++x.wkIx].trim().split('\t');x.i=0;
	x.b.push('<tr><td>',x.a[x.i++],'</td><td>',x.a[x.i++],'</td><td>',x.a[x.i++],'</td><td>',x.a[x.i++],'</td></tr>\n');
	matchHeading();
 while ( x.i<x.a.length && x.i>=0 )
 {var j=detectRow( x.m-2 ),n=j-x.i,n2=n;if(n<x.m-1)
	{console.log('f:n(',n,')<x.m-1(',x.m-1,')row=',x.row);n=x.m;}
	else if(n>x.m)
	{console.log('f:n(',n,')>x.m(',x.m,')row=',x.row);n=n==x.m*2?x.m:x.m-1;}
	x.b.push('<tr><td>');
		x.b.push(x.m==8?1:2);
		x.b.push('</td><td>');
	x.x=getArea(x.a[x.i])
		x.b.push(x.x?x.x[1]:'');
		x.b.push('</td>\n');

	for(x.c=0; x.c<n ;x.c++){
		if(x.c==(x.m==8?3:2)){
			x.b.push('<td>');
			var w=x.a[x.i+x.c]+x.a[x.i+x.c+1],
			v=getType(w);
			x.b.push(v?v[1]:'');
			x.b.push('</td>\n');}
		x.b.push('<td>');
		x.b.push(x.a[x.i+x.c]);
		x.b.push('</td>\n');
		if(x.c==0 && x.m==7)
			x.b.push('<td></td>\n');
	}//for x.c
	x.b.push('</tr>\n');
	x.row++;
	//if(x.a[x.i].indexOf(f.govStr)!=-1 || x.a[x.i]==f.head[0])
	x.i+=n;
	if(n2!=n)
		matchHeading();
 }//while
 }//while wkIx
 x.b.push('</table>\n');

 document.getElementById('b').innerHTML=x.b.join('');
 d();
}//function f

f.head=['الموقع',
'القطعه',
'المساحة',
'الوصف',
'نوع العقار',
'الثمن',
f.govStr='محافظة',
'ملاحظات']



f.regexNum=/\d{1,2}(,\d{3})*(\.\d+)?/;
f.regexTxt=/[^\d]+/;
trace=false;
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

function fixY(){
 var	s=document.getElementById('x').value
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
	b.push(s.substring(j))
	//return b;
  document.getElementById('x').value=b.join('');
}

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

function getType_backlog(){
	var b=[],i,w,t,y,v,
	s=document.getElementById('x').value,
	a=s.split('\n');
	for(i=0;i<a.length;i++){
		w=a[i];
		v=getType(w);
		if(! v){
			t=w.split('\t');
			y=t[1]+t[0];
			v=getType(y);}
		if(v)b.push(v[1]);else
			b.push('');
		b.push('\t');
		b.push(w);
		b.push('\n');
	}//for
 document.getElementById('x').value=b.join('')
}//function getType_backlog

function getArea_backlog(){
	var b=[],i,w,t,y,v,
	s=document.getElementById('x').value,
	a=s.split('\n');
	for(i=0;i<a.length;i++){
		w=a[i];
		v=getArea(w);
		if(v)b.push(v[1]);else
			b.push('');
		b.push('\t');
		b.push(w);
		b.push('\n');
	}//for
 document.getElementById('x').value=b.join('')
}//function getArea_backlog

onload=function(){
	area=resetAreasList();}


function test1(){a=x.value.split('\n').
map(function(b,bi){
	return b.split('الموقع	المساحة	الوصف	نوع العقار	الثمن	المحافظة	ملاحظه	').
	map(function(c,ci){
		return c.split('الموقع	القطعه	المساحة	الوصف	نوع العقار	الثمن	محافظة	ملاحظات	').
		map(function(d,di){	return d.split('محافظة').
			map(function(e,ei){return e.split('\t')
			})
		})
	})
})
}

function test2(){a=x.value.split('\n').
map(function(b,bi){
	return b.split('الموقع	المساحة	الوصف	نوع العقار	الثمن	المحافظة	ملاحظه	').
	map(function(c,ci){
		return c.split('الموقع	القطعه	المساحة	الوصف	نوع العقار	الثمن	محافظة	ملاحظات	').
		map(function(d,di){	return e.split('\t')
		})
	})
})
}

function test3(){var hd,weeks,fsm,a
hd=0
weeks={}
fsm={delimiter:{a:'\n'//bi:{
		,b:'الموقع	المساحة	الوصف	نوع العقار	الثمن	المحافظة	ملاحظه	'
		,c:'الموقع	القطعه	المساحة	الوصف	نوع العقار	الثمن	محافظة	ملاحظات	'
		,d:'محافظة'
		,e:'\t'}
	,/*ci=*/0:
	{before:function(p){
			hd={year:null,month:null,week:null,date:null//,a:null
				,type:1,tr:[],trans:[]//,tri:-1
			}//hd
		}//,before:function
		,/*di=*/0:
		{	/*ei=0*/0:
			{def:['year','month','week','date']
				,/*f*/i:function(p,i){var col=fsm.c0.d0.e0.def[i];
						hd[col||i]=p}
				//,after:function(p){}
			}//e0
			,/*ei=1*/1:
			{/*f*/i:function(p,i){if(i==0)hd.govNm=p}//def:['govNm'],
				//,after:function(p){}
			}//e1
		}//d0
		,/*d*/i:{
			/*e*/i:{
				/*f*/i:function chk(p){}
			}//ei
		}//di
	}//c0
	,/*ci=*/1:
	{	before:function(p){
			hd.type=2}
		//di: fsm.bi.c0.di
	}//c1	}//bi
}//fsm
fsm[/*ci=*/1]./*d*/i=fsm[/*ci=*/0]./*d*/i


a=x.value.split(fsm.delimiter.a).						//a
map(function(b,bi){										//b
	var br=b.split(fsm.delimiter.b).
	map(function(c,ci){var cx=fsm[ci]					//c
		if(cx&&cx.before)
			cx.before(c,ci,cx)
		var cr= c.split(fsm.delimiter.c).
			map(function(d,di){							//d
				var dx=cx[di] || cx.i
				if(dx.before)
					dx.before(d)
				var dr= d.split(fsm.delimiter.d).
				map(function(e,ei){
					var ex=dx[ei] || dx.i
					if(ex.before)
						ex.before(e)
					var er= e.split(fsm.delimiter.e).	//e
					map(function(f,fi){					//f
						
						var fr=getArea(f)
						if(fr){var z=hd.tr
							hd.trans.push(z)
							hd.tr=[fr] // hd.tr=[hd.sn, hd.year, hd.month, hd.week, hd.date, fr]
							return z
						}
						hd.tr.push(f)
						return f;
					})
					return er
				})
				return dr;
			})
		return cr;
	})
	hd.trans.push(br);
	weeks[hd.weekNo]=hd;
	return br;
})
}//function test


function test(x,no){var hd=0,weeks=[],sn=no.value
	,delimiter={a:'\n'//bi:{
		,b:'الموقع	المساحة	الوصف	نوع العقار	الثمن	المحافظة	ملاحظه	'
		,c:'الموقع	القطعه	المساحة	الوصف	نوع العقار	الثمن	محافظة	ملاحظات	'
		,d:'محافظ.'//'محافظة'
		,e:'\t'}
	,def=['year','month','week','date']
weeks.a=x.value.split(delimiter.a).							//a
map(function(b,bi){											//b
	var br=b.split(delimiter.b).
	map(function(c,ci){										//c
		if(ci==0)
		{	hd={type:1,gov:{},tr:[]};hd.trans=[hd.tr];}
		else 
		{	hd.type=ci+1;hd.govNm=null;}
		var cr= c.split(delimiter.c).
			map(function(d,di){								//d
				//if(di==0);
				var dr= d.split(delimiter.d).
				map(function(e,ei){
					//if(ex.before);
					var er= e.split(delimiter.e).			//e
					map(function(f,fi){var count=0;			//f
						if(ci==0&&di==0)
						{	if(ei==0)
							{var col=def[fi] || fi
								hd[col]=f
								if(col=='week')
									weeks[hd.week]=hd;		//database
							}
							else if(fi==0)
							 hd.gov[	hd.govNm=f]=[]//hd.tr
						}else if ( ei==0)//ci==1 ||
						{var fr=f&&f.length?getArea(f):null
							if(fr){if(hd.trans.length==1&&hd.trans[0].length==1)hd.trans.shift()
								hd.trans.push(hd.tr=[++sn, hd.year, hd.month, hd.week, hd.date,hd.type,fr])
								if(hd.type==2)
									hd.tr.push('')
								if( hd.gov[hd.govNm])
									hd.gov[hd.govNm].push(hd.tr)
								return hd.tr
							}
							hd.tr.push(f)
						}else if(ei==1 && fi==0)
						{hd.govNm=f
							if(!hd.gov[hd.govNm])
								hd.gov[hd.govNm]=[hd.tr]
						}if(hd.tr.length==11){var z=hd.tr
							,z9=z&&z[ 9]?getType(z[9]+z[10]):null;
							if(z9)
							{	z[9]=z9;z.pop()}
							else{z[9]=z[9]+z[10];z.pop();}
						}if(hd.tr.length>13){
							if(!f){if(++count%400==1)
								console.log(hd.tr,count)
								if(f=='')
									hd.tr.pop()
							}if(hd.tr.length==15||hd.tr.length==20)
								console.log(hd.tr[14],hd.tr)}
						return f;
					})
					return er
				})
				return dr;
			})
		return cr;
	})
	return br;
})//a
return weeks;
}//function test
