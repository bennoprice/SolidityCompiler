lexer grammar SolidityLexer;

/* Punctuation */

PERIOD      : '.';
COMMA       : ',';
SEMICOLON   : ';';
COLON       : ':';
QUESTION    : '?';

CURLY_OPEN      : '{';
CURLY_CLOSE     : '}';
PARENT_OPEN     : '(';
PARENT_CLOSE    : ')';

/* Operators */

PLUS_OPERATOR   : '+';
MINUS_OPERATOR  : '-';
MULT_OPERATOR   : '*';
DIV_OPERATOR    : '/';

LESS_OPERATOR       : '<';
LESS_EQ_OPERATOR    : '<=';
EQ_OPERATOR         : '==' ;
ASSIGN_OPERATOR     : '=' ;
NOT_OPERATOR        : '!';

/* Directives */

VERSION     : 'pragma solidity' .*? ';';

/* Keywords */

CONTRACT    : 'contract';
FUNCTION    : 'function';
PRIVATE     : 'private';
INTERNAL    : 'internal';
PUBLIC      : 'public';
PACKED      : 'packed';
RETURNS     : 'returns';
RETURN      : 'return';
TRUE        : 'true';
FALSE       : 'false';

/* Literals? */

IDENTIFIER  : (LETTER|UNDERSCORE) (LETTER|DIGIT|UNDERSCORE)*;
INT_CONST   : DIGIT+;

/* Fragments */

fragment UNDERSCORE     : '_';
fragment DIGIT          : [0-9];
fragment UPPER_LETTER   : [A-Z];
fragment LOWER_LETTER   : [a-z];
fragment LETTER         : UPPER_LETTER|LOWER_LETTER;

/* Comments */

COMMENT         : '//' .*? '\n' -> skip;
MULTI_COMMENT   : '/*' .*? '*/' -> skip;

WHITESPACE  : [ \n\f\r\t\u000B] -> skip;
ERROR       : . ;