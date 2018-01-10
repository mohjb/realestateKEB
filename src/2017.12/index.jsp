<%@page contentType="text/html; charset=utf-8" pageEncoding="UTF8"
import="java.nio.file.Files,java.io.*,
 java.nio.file.Files,
 java.util.Map,
 java.util.List,
 java.util.zip.ZipEntry,
 java.util.zip.ZipInputStream"
%><%@ include file="include_tl.jsp"%><%! // <?


public static class App {

/**
 * Created by Vaio-PC on 12/10/2017.
 */

static final String packageName= "2017.12"
	,AppNm=packageName+".App"
	,UploadPth="/aswan/uploads"
	,JsonPath="/json/";

////////////////////////////////////////////////////////////////////
//json files and directory-based archives

static File mostRecent(File dir){
	File[]a=dir.listFiles();File r=null;
	if(a!=null)
		for(File f:a)
			if(r==null || r.lastModified()<f.lastModified())
				r=f;
	return r;
}

@TL.Op(usrLoginNeeded = false,urlPath="*") public static
List
listFiles(TL tl)throws Exception
{List l=TL.Util.lst();
	File dir=TL.context.getRealPathFile(tl,UploadPth+JsonPath);
	if( ! dir.exists())
		dir.mkdirs();
	File[]a=dir.listFiles();
	if(a!=null)
		for(File d:a)
			if(d.isDirectory())
		{File f=mostRecent(d);
			if(f!=null)
			l.add(TL.Util.mapCreate("fileName",d.getName()
				,"size",f.length()
				,"lastModified",f.lastModified()//long
			));
		}
	return l;
}

@TL.Op(usrLoginNeeded = false) public static
boolean
putFile(
	@TL.Op(prmName="fileName")String fileName
	,@TL.Op(prmName="content")String content
	,TL tl)throws Exception
{File f=TL.context.getRealPathFile(tl,
		UploadPth+JsonPath+fileName+'/'+tl.now.getTime());
	byte[]ba=content.getBytes();f.getParentFile().mkdirs();
	Files.write(f.toPath(),ba);
	return true;
}

@TL.Op(usrLoginNeeded = false) public static
Map
getFile(
	   @TL.Op(prmName="fileName")String fileName
	   ,TL tl)throws Exception
{File d=TL.context.getRealPathFile(tl,UploadPth+JsonPath+fileName)
	 ,f=mostRecent(d);
	byte[]ba=Files.readAllBytes(f.toPath());
	String content=new String(ba);
	Map m=TL.Util.mapCreate("fileName",fileName
		,"content",content
		,"size",f.length()
		,"lastModified",f.lastModified());
	return m;
}


////////////////////////////////////////////////////////////////////
//flat files operations

@TL.Op public static
List
listFiles_flat(TL tl)throws Exception
{List l=TL.Util.lst();
	File dir=TL.context.getRealPathFile(tl,UploadPth+JsonPath);
	File[]a=dir.listFiles();
	if( ! dir.exists())
		dir.mkdirs();
	if(a!=null)
		for(File f:a)
			l.add(TL.Util.mapCreate("fileName",f.getName()
				,"size",f.length()
				,"lastModified",f.lastModified()//long
			));
	return l;
}

@TL.Op public static
boolean
putFile_flat(
	            @TL.Op(prmName="fileName")String fileName
	            ,@TL.Op(prmName="content")String content
	            ,TL tl)throws Exception
{Map m=TL.Util.mapCreate();
	File f=TL.context.getRealPathFile(tl,UploadPth+JsonPath+fileName);
	byte[]ba=content.getBytes();
	Files.write(f.toPath(),ba);
	return true;
}

@TL.Op public static
Map
getFile_flat(
	@TL.Op(prmName="fileName")String fileName
	,TL tl)throws Exception
{File f=TL.context.getRealPathFile(tl,UploadPth+JsonPath+fileName);
	byte[]ba=Files.readAllBytes(f.toPath());
	String content=new String(ba);
	Map m=TL.Util.mapCreate("fileName",fileName
		,"content",content
		,"size",f.length()
		,"lastModified",f.lastModified());
	return m;
}

////////////////////////////////////////////////////////////////////
//archived files in zip files

@TL.Op public static
boolean
putFile_zip(
	@TL.Op(prmName="fileName")String fileName
	,@TL.Op(prmName="content")String content
	,TL tl)throws Exception
{
	Map<String, String> env = new java.util.HashMap<>();
	env.put("create", "true");
	java.nio.file.Path path = java.nio.file.Paths.get("test.zip");
	try (java.nio.file.FileSystem fs = 
	java.nio.file.FileSystems.newFileSystem(
	java.net.URI.create("jar:" + path.toUri()), env))
	{
		java.nio.file.Path nf = fs.getPath("new.txt");
		try (Writer writer = Files.newBufferedWriter(nf
			, java.nio.charset.StandardCharsets.UTF_8
			, java.nio.file.StandardOpenOption.CREATE)) {
			writer.write("hello");
		}
	}


	return false;
}

@TL.Op public static
List
listFiles_zip(TL tl)throws Exception
{List l=TL.Util.lst();
	File dir=TL.context.getRealPathFile(tl,UploadPth+JsonPath);
	Map x=null;
	File[]a=dir.listFiles();
	if( ! dir.exists())
		dir.mkdirs();
	if(a!=null)
		for(File d:a)if(d.isDirectory()){x=mostRecent_zip(d,null);
			if(x!=null)
			l.add(TL.Util.mapSet(x,"fileName",d.getName()));
		}
	return l;
}

@TL.Op public static
Map
getFile_zip(
	@TL.Op(prmName="fileName")String fileName
	,TL tl)throws Exception
{File d=TL.context.getRealPathFile(tl,UploadPth+JsonPath+fileName);
	Map r=null,x=mostRecent_zip(d,null);
	ZipInputStream zis =new ZipInputStream(new FileInputStream(d));
	ZipEntry ze = zis.getNextEntry();
	while(ze!=null && r==null){
		String s=ze.getName();
		if(s.equals(x.get("fileName")))
		{r=x;
			ByteArrayOutputStream b=new ByteArrayOutputStream();
			byte[] buffer = new byte[1024*4];
			int len;
			while ((len = zis.read(buffer)) > 0)
				b.write(buffer, 0, len);
			x.put("content",new String(b.toByteArray()));
		}
		ze = zis.getNextEntry();
	}
	//zis.closeEntry();
	zis.close();
	return r;
}

static Map mostRecent_zip(File zipFile,Map r)throws Exception{
	ZipInputStream zis =
		new ZipInputStream(new FileInputStream(zipFile));
	//get the zipped file list entry
	ZipEntry ze = zis.getNextEntry();
	long lm=0,x;
	while(ze!=null){
		x=ze.getLastModifiedTime().toMillis();
		if(x>lm)
		{if(r==null)r =TL.Util.mapCreate("fileName", ze.getName()
				,"size",ze.getSize()
				,"lastModified",ze.getLastModifiedTime().toMillis());
			else TL.Util.mapSet(r,"fileName", ze.getName()
				,"size",ze.getSize()
				,"lastModified",ze.getLastModifiedTime().toMillis());
			lm=x;}
		ze = zis.getNextEntry();
	}
	//zis.closeEntry();
	zis.close();
	return r;
}

}//class App


%>