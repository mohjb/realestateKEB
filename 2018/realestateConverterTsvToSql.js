
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
 x=pType[w];if(!x)
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


function test(){a=x.value.split('\n').
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

function test(){a=x.value.split('\n').
map(function(b,bi){
	return b.split('الموقع	المساحة	الوصف	نوع العقار	الثمن	المحافظة	ملاحظه	').
	map(function(c,ci){
		return c.split('الموقع	القطعه	المساحة	الوصف	نوع العقار	الثمن	محافظة	ملاحظات	').
		map(function(d,di){	return e.split('\t')
		})
	})
})
}
	
function test(){
hd=0
weeks={}
fsm={//bi:{
	c0:{before:function(p){
			hd={year:null,month:null,weekNo:null,date:null//,a:null
				,type:1
				,govNm:null,govNo:null,gov:{}
				,mw83:null,tr:[],trans:[]//,tri:-1
			}//hd
		}//,before:function
		,d0:{
			e0:{def:['year','month','weekNo','date']
				,f0:{
					i:function(p,i){var col=fsm.c0.d0.e0.def[i];
						hd[col||i]=p}
				}//f0
				//,after:function(p){}
			}//e0
			,e1:{def:['govNm']
				,f0:{i:function(p){}
				}//f0
				,after:function(p){
					
				}
			}//e1
		}//d0
		,di:{
			ei:{
				fi:{
					i:function chk(p){}
				}
			}//ei
		}//di
	}//c0
	,c1:{
		before:function(p){hd.type=2}
		//di: fsm.bi.c0.di
	}//c1	}//bi
}//fsm
fsm.c1.di=fsm.c0.di

//a: level0
a=x.value.split('\n').
map(function(b,bi){
	var r=b.split('الموقع	المساحة	الوصف	نوع العقار	الثمن	المحافظة	ملاحظه	').
	map(function(c,ci){var xc=fsm['c'+ci]||fsm.ci
		if(xc.before)
			xc.before(c,ci,xc)

		var r= c.split('الموقع	القطعه	المساحة	الوصف	نوع العقار	الثمن	محافظة	ملاحظات	').
			map(function(d,di){													//level 3
				if(i==0);
				var r= d.split('محافظة').
				map(function(e,ei){return e.split('\t').							//level 4
					map(function(f,fi){											//level 5
						function chkArea(e,i,a){
							var r=getArea(e);if(r){
							var x=[i,r];for(var j=1;j<=7;j++)x.push(a[i+j])
							//ax.push(x);
							return x;}}
						var r=chkArea(f)
						if(!hd.a){
							
						}
						return r
					})
				})
				return r;
			})
		return r;
	})
	hd.trans.push(r);
	weeks[hd.weekNo]=hd;
	return r;
})
}


