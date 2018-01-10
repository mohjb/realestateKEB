
function dragover_handler(ev) {
  console.log("dragOver");
  // Prevent default select and drag behavior
  ev.preventDefault();
}

 function dragend_handler(ev) {
	console.log("dragEnd");
	// Remove all of the drag data
	var dt = ev.dataTransfer;
	if (dt.items)
		// Use DataTransferItemList interface to remove the drag data
		for (var i = 0; i < dt.items.length; i++)
			dt.items.remove(i);
	else
		// Use DataTransfer interface to remove the drag data
		ev.dataTransfer.clearData();
 }

 function drop_handler(ev) {
	console.log("Drop");
	ev.preventDefault();
	// If dropped items aren't files, reject them
	var b=ev.dataTransfer.items,a=b?b:dt.files;
	for (var i=0; i < a.length; i++)
		if(!b || b[i].kind == "file")
			read_file(b?a[i].getAsFile():a[i])

	/*if (dt.items) {
		// Use DataTransferItemList interface to access the file(s)
		for (var i=0; i < dt.items.length; i++) {
		  if (dt.items[i].kind == "file") {
			var f = dt.items[i].getAsFile();
			console.log("... file-item[" + i + "].name = " + f.name);
			read_file(f)
		  }
		}
	} else {
		// Use DataTransfer interface to access the file(s)
		for (var i=0; i < dt.files.length; i++) {
		  console.log("... file[" + i + "].name = " + dt.files[i].name);
		  read_file(dt.files[i])
		}
	}*/
 }

 function read_file(f){
	console.log("read_file = " ,f);
	var reader = new FileReader();
	var name = f.name;
	reader.onload = reader_onload
	if(rABS) reader.readAsBinaryString(f);
	else reader.readAsArrayBuffer(f);
 }//read_file


	function sheetjsw(data, cb, readtype) {
		pending = true;
		//opts.on.workstart();
		var scripts = document.getElementsByTagName('script');
		var dropsheetPath;
		for (var i = 0; i < scripts.length; i++)
			if (scripts[i].src.indexOf('dropsheet') != -1)
				dropsheetPath = scripts[i].src.split('dropsheet.js')[0];
		var worker = new Worker(dropsheetPath + 'sheetjsw.js');
		worker.onmessage = function(e) {
			switch(e.data.t) {
				case 'ready': break;
				case 'e': pending = false; console.error(e.data.d); break;
				case 'xlsx':
					pending = false;
					//opts.on.workend();
					cb(JSON.parse(e.data.d)); break;
			}
		}
		worker.postMessage({d:data,b:readtype,t:'xlsx'});
	}

 function reader_onload(e) {
	var data = e.target.result;
	var wb, arr;
	var readtype = {type: rABS ? 'binary' : 'base64' };
	if(!rABS) {console.error("fixdata not implemented" );
		arr = data//fixdata(data);
		data = btoa(arr);
	}

	if(e.target.result.length > 1e6) //opts.errors.large(e.target.result.length, function(e) { if(e) doit(); });else
		console.log("reader_onload: e.target.result.length > 1e6 : ",e.target.result.length);
	try {console.log("doit " );
			//if(useworker)sheetjsw(data, process_wb, readtype); else
			{	wb = XLSX.read(data, readtype);
				process_wb(wb);}
		} catch(e) { console.log(e); }// opts.errors.failed(e);
 }

var rABS = typeof FileReader !== 'undefined' 
	&& FileReader.prototype 
	&& FileReader.prototype.readAsBinaryString;
var useworker = typeof Worker !== 'undefined';
//contract-fields:c is field name, if f then obligatory field, if t=='w' then date, if t=='n' then number , if t=='s' then string, if t=c then currency/number in a string format that has commas and 'kd'
cf=
[{t:'s',c:'title'		,f:1}
,{t:'s',c:'contractor'	,f:1}
,{t:'w',c:'created'			}
,{t:'w',c:'lastModified'	}
,{t:'w',c:'started'		}
,{t:'w',c:'completed'	}
,{t:'s',c:'paid'		}
,{t:'s',c:'closed'		}
,{t:'c',c:'rate'		,f:1}
,{t:'n',c:'jn'			}
,{t:'n',c:'ppl'			,f:1}
,{t:'n',c:'month'		,f:1}
,{t:'c',c:'dl'			,f:1}
,{t:'c',c:'dm'			,f:1}
,{t:'c',c:'sv'			,f:1}
,{t:'c',c:'oh'			}
,{t:'c',c:'tc'			}
,{t:'c',c:'cm'			,f:1}
,{t:'c',c:'sp'			,f:1}
,{t:'c',c:'ni'			}
,{t:'s',c:'remarks'		}
,{t:'s',c:'eSignature',b64json:1}
]
 function structVal_diff(a,b){
	var same=1,c;
	for(var i=0 ; same && i<cf.length ; i++){
		c=cf[i];if(c.f)
		same=a[c.c]==b[c.c]
	}
	return !same;
 }

 function process_wb(wb, sheetidx)
 {	last_wb = wb;u=XLSX.utils;
	console.log("process_wb:sheetidx=",sheetidx ,',wb=',wb );
	var x=process_sheet.x={wb:wb,sheetIx:0,r:[],progress:
		{i:0,n:0
			,e:document.getElementById('progress')
			,p:document.getElementById('drop_zone_results')}}
	for(var i=0,a=wb.SheetNames;i<a.length;i++){
		var s=wb.Sheets[a[i]]
		,g=u.decode_range(s['!ref']);
		x.progress.n+=g.e.r-g.s.r+1;}
	x.intrvl=setInterval(process_sheet,100);
	if(x.progress.e){x.progress.e.max=x.progress.n;x.progress.e.value=x.progress.i;x.progress.p.innerHTML='-'}
	return x;
 }//process_wb

function process_sheet() {
 var x=process_sheet.x
 if(!x.activeSheet)
 {	if(x.sheetIx<x.wb.SheetNames.length )
	{	x.activeSheet=x.wb.Sheets[x.wb.SheetNames[x.sheetIx++]]
		x.cche={};
		x.g=u.decode_range(x.activeSheet['!ref'])
		x.c={r:x.g.s.r,c:0}
	}else {
		clearInterval(x.intrvl)
		console.log("process_sheet:end",x );
 }}
 if(x.activeSheet)
 {var c=x.c,r0=c.r; //for(c. r=g.s.r ; c.r<=g.e.r ; c.r++){
	for(c. c=x.g.s.c ; c.c<=x.g.e.c ; c.c++)
	{	var b=1,z={};//z is a prospect contract
		for(var i=0 ; b && i<cf.length ; i++)
		{	c.r=r0+i;
			var f=cf[i],
			n=u.encode_cell(c),
			o=x.activeSheet[n];
			if(o)
			{	var v=o.v 
				if(f.f)
					b=!!v
					&& (!x.cche[c.c]|| !x.cche[c.c][r0-i])
					&&((f.t!='n'&& f.t!='c')
						|| !isNaN
						( (	v.replace
						 &&	v.replace(/,|KD/g,'')
						  )
						  ||(!v.replace&&v)
					  ) );
				else if(f.t=='w' || f.t=='n' ||f.t=='c')
					b= (!isNaN((v.replace&&v
					.replace(/,|KD/g,''))||(!v.replace&&v))
					||v==''||v==undefined ) 
					&& (!x.cche[c.c]|| !x.cche[c.c][r0-i]);
				if(b && v!=undefined)
					z[f.c]=f.t=='w'?new Date(o.w):v;
				if(f.b64json)
				{	try{var v1=atob(v.replace(/[ \n\r\t…]/g,''))
						,j=z.json=JSON.parse(v1);
						if(j&&(j.deleted || j.closed))
						{	j.xl=z;z.deleted=j.deleted
							z=j;if(structVal_diff(z,z.xl))
								z.remarks=z.remarks
									?z.remarks+'\n---\n'
									+JSON.stringify(z.xl)
									:JSON.stringify(z.xl)
						}//if(z.json)
					}catch(ex){
						console.error('process_sheet:at',c,ex)}
				}//if(f.b64json)
			}else
				b=!f.f;
		}//for i
		c.r=r0;
		if(b)
		{	x.r.push(z);
			if(!x.cche[c.c])
				x.cche[c.c]={};
			x.cche[c.c][r0]=1;
			if(x.progress.e){
				x.progress.e.innerHTML=x.r.length+' contracts'
				var a=[];for(var i=0;i<x.r.length;i++){a.push(x.r[i].title)}
				x.progress.p.innerHTML=a.join('<br/>')
				}
		}
	}//for c.c //}
	if(++c.r>x.g.e.r)
		x.activeSheet=0;x.progress.i++
	if(x.progress.e)x.progress.e.value=x.progress.i;
 }//if(x.activeSheet)
}//process_sheet
