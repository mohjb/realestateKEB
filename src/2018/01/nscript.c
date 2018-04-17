/******************************************************************************

                            Online C Compiler.
                Code, Compile, Run and Debug C program online.
Write your code in this editor and press "Run" button to compile and execute it.

*******************************************************************************/

#include <stdio.h>

//array and obj are linkedListEntry
//enum NTyp{undefined,nul,false,true,num,str,array,obj;}


//Node

typedef struct {
    enum { NUL,NUM,STR,ARY,OBJ,FNC } type ;
    struct{N*n;N*x;N*refVis}inbound;
    N*meta;/*context-info/parent/unit-domain ; 
		prototype (or hostObjectClass); 
		member-name; 
		annotations ; attribs ; 
		visualizations; 
		auth/perm; 
		listeners / storage-stations */
    union {
		struct {enum{U,I,D}p;//  precision
			union{
				ulong u;
				long i;
				double d;
		}}num;
        struct{uint n;char *s;}str;
        struct {uint n;
            N[]a;
        } ary;
        struct {  
            uint n;char*k;//key of key/value pair
            N v;
            N* x;//next key/value pair
        } obj;
        struct {  
            uint n;char *k;//function name
            N*rtn;//linkedlist, 1st item is the return type, next items are throwable types
            N*prm;//linkedlist of params names
            N*body;//code-json
        } fnc;
    };
} N;
/* C-Lang functions
init (num, str, ary, obj, fnc)
assignMember(num, str, ary, obj, fnc)
dispose/deleteMember
equals,lt,le,gt,ge
toString/toJsonString
concat (str,ary,obj)
splice (str,ary,obj)
indexOf (str,ary,obj)
at (str,ary,obj)
str:
	startsWith
	endsWith
	lastIndexOf
	parse
	substring(alias to splice)
	

arithmetic:
	add,sub,mul,div,mod,bit-and,bit-or,bit-xor
isTrue,isAnd,isOr
*/

/*
operations constructs:
	arithmetic	add,sub,mul,div,mod,power ,visualsControls
	bitwise     band,bor,xor ,visualsControls
	comparison  eq,neq,lt,le,gt,ge,and,or ,visualsControls
	control     if,ifelse,else,while,until,switch,return,break,tryCatchFinally ,visualsControls
	literal     bool,num,str,ary,obj,func,block,var(alias for obj.set member) ,visualsControls
	str         ListSeq0::= visualsControls
					sublist,len,splice,indexOf,setAt(assignment)
					,member(dot-notation)
					,concat(entry,obj,ary)
					,listOps
					,list(inbound,inboundRef,metaKeys,metaMember,cntrlPnts,visuals)//categories
	ary         ListSeq::=ListSeq0 in addition with 
					delete(entry),setAt(assignment),setter,getter
					,LList(nextEntry,prevEntry)
	obj			ListX::= ListSeq + listKeys 
	func        def,call,get(name,returnType,throwsList,paramsList,body)
				,threadAlloc,threadDealloc,threadStart,threadIntrupt,currentThread
				,sync,getContinuation,resumeContinuation
				,ListX
				,funcMenu(operations constructs)
visualsControls::=
	list/access: ControlPoints , visualComponents
Host Objects
	Sys
	AST(Json Ed)
		Monitor
		Window
		ControlPoints
			(rulers,grids,coordSys,coordSysAxess , 2D/3D/nD visualAccess-(Points/Shapes) )
		Component(cntrlpnts,borders,aspects/views)
		RefLink
		NodeVisualization(renderer)
			SVG
				coloring
				shapes
			,Css
				rules
			,HtmlRenderer
				text
				layout
			OpenGL
				modeling, layers,
	Thread
	Continuation
	Engine(StackFrame ; DebuggerContext ; Triggers/EventListeners )
	File(Stream)
	Socket(Stream)
	UDP
	Http
	Touch
	Proc
	NativeArray
	Date
	DyLib
	Compiler(assembler and Disassembler)
		
Knowledge
	FSM
		Json-parser
		Xml-parser
		js-parser
		java-parser
		c-parser
	Generator/TranslatorClasses
	(Prolog/query/resolver)(relations/coefficients , goals ; Associations )
	semantics gui
		arrows, balloon, text path, design-layout /sections, 
		nested-coordSystems: nested-offset, nested-angles, container fractions
	mind-maps, and multi-focal graphs
	focus structure
	API mindmaps
		oop , uml(use-cases , flowcharts , er) , c
		opengl
		os
		tcp , http , stations
		fsm
		layers of knowledge, and dependencies/accumulated know-how
			, skills, trial-and-error, machine-learning
		
	conversation aspects
	planning and idea-plan reproduction
	project management/task time-table
*/

struct StrBuff{uint n,i,j;char*buf;StrBuff* src,dst;FILE*fp;}

StrBuff*StrBuff_init(int n){}

StrBuff*StrBuff_init_fileName(char*fileName){}
StrBuff*StrBuff_init_FileP(FILE*fp){}
StrBuff*StrBuff_readFile(StrBuff*t,uint len){}

StrBuff*StrBuff_init_str(char*s,uint n){}
StrBuff*StrBuff_dispose(StrBuff*t){}
char StrBuff_charAt(StrBuff*t,uint offset){}
uint StrBuff_length(StrBuff*t){}
char StrBuff_shift(StrBuff*t){}
char StrBuff_unshift(StrBuff*t,char p){}
char StrBuff_push(StrBuff*t,char p){return 0;}
//StrBuff*StrBuff_splice(StrBuff*t,uint offset,uint len, uint srcLen,char*src,uint srcOffset){return t;}
StrBuff*StrBuff_concat(StrBuff*t, uint srcLen,char*src){return t;}
StrBuff*StrBuff_append(StrBuff*t, StrBuff*p){return t;}
StrBuff*StrBuff_substring(StrBuff*t,uint offset,uint len){return t;}
StrBuff*StrBuff_trim(StrBuff*t,uint offset,uint len){return t;}


/*
json parser that generates struct-N
*/
struct JsonPrsr{
	StrBuff*buf,comment;
	char c;uint _row,_col;
	N*cache;}

JsonPrsr*JsonPrsr_read(JsonPrsr*t){return t;}

JsonPrsr*JsonPrsr_init(StrBuff*b){return 0;}
JsonPrsr*JsonPrsr_init_fn(char*fileName){return 0;}
JsonPrsr*JsonPrsr_init_str(char*p,uint n){return 0;}
N*JsonPrsr_parse(JsonPrsr*t){return 0;}
void JsonPrsr_skipRWS(JsonPrsr*t){}
void JsonPrsr_skipRWSx(JsonPrsr*t){}
void JsonPrsr_parseItem(JsonPrsr*t){}
void JsonPrsr_extractStringLiteral(JsonPrsr*t){}
void JsonPrsr_extractIdentifier(JsonPrsr*t){}
void JsonPrsr_extractDigits(JsonPrsr*t){}
void JsonPrsr_extractArray(JsonPrsr*t){}
void JsonPrsr_extractObject(JsonPrsr*t){}
void JsonPrsr_skipWhiteSpace(JsonPrsr*t){}
void JsonPrsr_skipComments(JsonPrsr*t){}
void JsonPrsr_peek(JsonPrsr*t){}
void JsonPrsr_rc(JsonPrsr*t){}
void JsonPrsr_nlRC(JsonPrsr*t){}
void JsonPrsr_incCol(JsonPrsr*t){}
void JsonPrsr_setEof(JsonPrsr*t){}
void JsonPrsr_nxtC(JsonPrsr*t,char h){}
void JsonPrsr_bNxt(JsonPrsr*t){}
void JsonPrsr_nxt(JsonPrsr*t){}
void JsonPrsr_next(JsonPrsr*t,uint n){}
void JsonPrsr_buff(JsonPrsr*t){}
void JsonPrsr_buffC(JsonPrsr*t,char c){}
StrBuff*JsonPrsr_consume(JsonPrsr*t){return 0;}
void JsonPrsr_consume_to_N(JsonPrsr*t,N*p){}

/*
void skipRWS
void skipRWSx(...)
Object parse()
Object parseItem()
String extractStringLiteral()
Object extractIdentifier()
Object extractDigits()
List<Object> extractArray()
Map<Object,Object> extractObject()
void skipWhiteSpace()
boolean skipComments()


char read()//read a char from the rdr

char peek()

int _row,_col;String rc()

void nlRC(){_col=1;_row++;}
void incCol(){_col++;}
char setEof()
char nxt(char h)
char bNxt()
char nxt()
String next(int n)
char buff()
buff(char p)
String consume()

*/

int main()
{
    printf("Hello World");
	N n;
    return 0;
}
