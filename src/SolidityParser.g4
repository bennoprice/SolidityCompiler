parser grammar SolidityParser;

options { tokenVocab = SolidityLexer; }

program 
	: VERSION contract EOF
	;

contract
    : CONTRACT typeId=IDENTIFIER CURLY_OPEN feature+ CURLY_CLOSE
	;

feature
    : attribute
    | method
    ;

attribute
    : typeId=IDENTIFIER visibility objectId=IDENTIFIER SEMICOLON
    ;

method
    : FUNCTION objectId=IDENTIFIER PARENT_OPEN params? PARENT_CLOSE accessibility returnType? CURLY_OPEN (expr SEMICOLON)+ CURLY_CLOSE
    ;

returnType
    : RETURNS PARENT_OPEN typeId=IDENTIFIER PARENT_CLOSE
    ;

params
    : formal (COMMA formal)*
    ;

args
    : expr (COMMA expr)*
    ;

formal
    : typeId=IDENTIFIER objectId=IDENTIFIER
    ;

visibility
    : PRIVATE
    ;

accessibility
    : INTERNAL
    | PUBLIC PACKED
    ;

expr
    : objectId=IDENTIFIER PARENT_OPEN args? PARENT_CLOSE            # Dispatch
    | expr op=(MULT_OPERATOR | DIV_OPERATOR) expr                   # MultiplyDivide
    | expr op=(PLUS_OPERATOR | MINUS_OPERATOR) expr                 # AddSubtract
    | expr op=(LESS_EQ_OPERATOR | LESS_OPERATOR | EQ_OPERATOR) expr # LessEqual
    | NOT_OPERATOR expr                                             # Negate
    | expr QUESTION expr COLON expr                                 # Ternary
    | objectId=IDENTIFIER ASSIGN_OPERATOR expr                      # Assignment
    | typeId=IDENTIFIER objectId=IDENTIFIER ASSIGN_OPERATOR expr    # Declaration
    | PARENT_OPEN expr PARENT_CLOSE                                 # Brackets
    | RETURN expr                                                   # Return
    | objectId=IDENTIFIER                                           # ID
    | INT_CONST                                                     # Integer
    | TRUE                                                          # True
    | FALSE                                                         # False
    ;

error
    : ERROR { Utilities.lexError(); }
    ;