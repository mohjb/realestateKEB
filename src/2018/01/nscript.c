/******************************************************************************

                            Online C Compiler.
                Code, Compile, Run and Debug C program online.
Write your code in this editor and press "Run" button to compile and execute it.

*******************************************************************************/

#include <stdio.h>

//array and obj are linkedListEntry
//enum NTyp{undefined,nul,false,true,num,str,array,obj;}
typedef enum { NUL,NUM,STR,ARY,OBJ,FNC } Ntype;

//Node

typedef struct {
    Ntype typ;          // typ is separate.
    uint gc;
    union {
        float num;
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
assignment  alias for setAt
arth        add,sub,mul,div,mod,power
bitwise     band,bor,xor
comparison  eq,neq,lt,le,gt,ge,and,or
control     if,ifelse,else,while,until,switch,return,break,tryCatchFinally
literal     bool,num,str,ary,obj,func,block,var
str         concat,sub,len,splin,indexOf,delete
ary         concat,sub,len,splin,indexOf,delete,setAt,member
obj         concat,delete,setAt,member
func        def,call
*/
int main()
{
    printf("Hello World");

    return 0;
}
