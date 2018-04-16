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
        union{enum{U,I,D}p;//  precision
			ulong u;
			long i;
			double d;
			}num;
        char *str;
        struct {//uint i;
            N v;
            N* x;//next array item
        } ary;
        struct {  
            char *k;//key of key/value pair
            N v;
            N* x;//next key/value pair
        } obj;
        struct {  
            char *k;//function name
            N*rtn;//linkedlist, 1st item is the return type, next items are throwable types
            N*prm;//linkedlist of params names
            N* body;//code-json
        } fnc;
    };
} N;
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
int main()
{
    printf("Hello World");
	N n;
    return 0;
}
