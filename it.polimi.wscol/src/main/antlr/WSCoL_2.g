header {
package it.polimi.WSCoL;
}
class WSCoLLexer extends Lexer;

options
    {
        charVocabulary='\3'..'\377';
        k = 8;
        //importVocab=XPath;
    }

    tokens
    {
        KW_IF = "if";
        KW_ELSE = "else";
        KW_ELSEIF = "elseif";
        KW_OR = "or";
        KW_AND = "and";
        KW_TRUE = "true";
        KW_FALSE = "false";
        KW_FORALL = "forall";
        KW_EXISTS = "exists";
        KW_IN = "in";
        KW_LET = "let"; // For making alias
        KW_STORE = "store"; // For creating historical variable
        KW_RETRIEVE = "retrieve"; // For reading historical variable
        KW_RESP_TIME = "Resp_Time"; // For access to special historical variable Resp_Time
        KW_MAX = "max";
        KW_MIN = "min";
        KW_AVG = "avg";
        KW_SUM = "sum";
        KW_PRODUCT = "product";
        KW_RETURN_NUMBER="returnNum";
        KW_RETURN_BOOL="returnBool";
        KW_RETURN_STRING="returnString";
    }

WS
    :
        ('\n' | ' ' | '\t' | '\r')+
        {
            $setType(Token.SKIP);
        }
    ;

protected
DIGIT
    :
        ('0'..'9')
    ;

protected
SINGLE_QUOTE_STRING
    :
        '\''! (~('\''))* '\''!
    ;

protected
DOUBLE_QUOTE_STRING
    :
        '"'! (~('"'))* '"'!
    ;

LITERAL
    :
        SINGLE_QUOTE_STRING | DOUBLE_QUOTE_STRING
    ;

NUMBER
    :
        (DIGIT)+ ('.' (DIGIT)+)?
    ;


IDENTIFIER

    options
    {
        testLiterals=true;
    }

    :
        ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'-'|'_'|'0'..'9'|'.')*
    ;

SEMI: ';' ;

LEFT_PAREN
    : '('  ;

RIGHT_PAREN
    : ')'  ;

LEFT_BRACKET
    : '['  ;

RIGHT_BRACKET
    : ']'  ;
LEFT_CURLY
    : '{'  ;

RIGHT_CURLY
    : '}'  ;
PIPE
    : '|'  ;

DOT
    : '.'  ;

DOT_DOT
    : ".." ;

AT
    : '@'  ;

COMMA
    : ','  ;

DOUBLE_COLON
    : "::" ;

COLON
    : ":"  ;

SLASH
    : '/'  ;

DOUBLE_SLASH
    : '/' '/' ;

DOLLAR_SIGN
    : '$'  ;

PLUS
    : '+'  ;

MINUS
    : '-'  ;

PERCENTAGE
    : '%'
    ;


DOUBLE_EQUALS
    :   "==" ;

EQUALS
    : '='  ;

NOT_EQUALS
    : "!=" ;

LT
    : '<'  ;

LTE
    : "<=" ;

GT
    : '>'  ;

GTE
    : ">=" ;

STAR
    : '*'  ;

AND: "&&";

OR: "||";

NOT: '!';

KW_LR_IMPLIES :"==>";

KW_RL_IMPLIES : "<==";

KW_2WIMPLIES :"<==>";

NEWLINE: ("\r\n"
    | '\r'
    |'\n'
    )
    { newline();
      $setType(Token.SKIP);
    }
    ;

class WSCoLParser extends Parser;

options {
    buildAST=true;
    k=8;
}

{
    class Symbol {
        String name;
        public Symbol(String name) {
            this.name = name;
        }
    }
    java.util.Hashtable<String,Symbol> symbols = new java.util.Hashtable<String,Symbol>();

    private java.util.List<String> errors = new java.util.LinkedList<String>();

    @Override
    public void reportError(RecognitionException e) {
        errors.add(e.toString());
        super.reportError(e);
    }

    @Override
    public void reportError(String e) {
        errors.add(e);
        super.reportError(e);
    }

    public java.util.List<String> getErrors() {
        return errors;
    }
}

imaginaryTokenDefinitions :
    RECOVERY
    CONDITION
    COMPLETE
    STRATEGY
    IF
    ELSEIF
    ELSE
    ACTION
    STEP
    RULES
    EXTERNAL_VARIABLE
    INTERNAL_VARIABLE
    PARAM_LIST
    METHOD
    FORALL
    EXISTS
    SERIES
    CONCAT
    WSDL <AST=it.polimi.monitor.nodes.SimpleAST>
    WM  <AST=it.polimi.monitor.nodes.SimpleAST>
    INPUTS <AST=it.polimi.monitor.nodes.SimpleAST>
    OUTPUTS
    XPATH
    VAR
    LET
    STORE
    RETRIEVE
    MAX
    MIN
    AVG
    SUM
    PRODUCT
    RETURN_NUMBER
    RETURN_BOOL
    RETURN_STRING
    ALIAS_NAME
    PROCESS_ID
    USER_ID
    INSTANCE_ID
    LOCATION
    ASSERTION_TYPE
    HISTORICAL_VARIABLE
    NUMBER_OF_RESULTS
    RESP_TIME
    ;

analyzer
    : rules | recovery
    ;

recovery!:
            cs:complete_strategy
            {
                #recovery = #([RECOVERY, "Recovery"], #cs);
            }
            | s:strategy
            {
                #recovery = #([RECOVERY, "Recovery"], #s);
            }
;

complete_strategy
    :
        ifStrategy (elseIfStrategy)* (elseStrategy)?
    ;

ifStrategy!
    :
        KW_IF c:condition s:strategy
        {
            #ifStrategy = #([IF, "If","it.polimi.recovery.nodes.IfStatementNode"],    #c, #s);
        }
    ;

elseIfStrategy!
    :
        KW_ELSEIF c:condition s:strategy
        {
            #elseIfStrategy = #([ELSEIF, "ElseIf","it.polimi.recovery.nodes.IfStatementNode"], #c, #s);
        }
    ;

elseStrategy
    :
        KW_ELSE s:strategy
        {
            #elseStrategy = #([ELSE, "Else","it.polimi.recovery.nodes.IfStatementNode"], #s);
        }
    ;

condition! :
         LEFT_PAREN! r:rules RIGHT_PAREN!
         {
        #condition = #([CONDITION, "Condition"], #r);
         }
;

strategy!
    : LEFT_CURLY! s:steps RIGHT_CURLY!
    {
        #strategy = #([STRATEGY, "Strategy","it.polimi.recovery.nodes.StrategyNode"], #s);
    }
    ;

steps
    :
    rec_step ( KW_OR^<AST=it.polimi.recovery.nodes.OrNode> rec_step )*
    ;

rec_step!
    : a:actions
    {
        #rec_step = #([STEP,"RecoveryStep","it.polimi.recovery.nodes.RecoveryStepNode"], #a);
    }
    ;

actions
    : action (KW_AND^<AST=it.polimi.recovery.nodes.AndNode> action)*
    ;

action!
    :
    m:IDENTIFIER<AST=it.polimi.recovery.nodes.SimpleAST> LEFT_PAREN (l:list)? RIGHT_PAREN
    {
        #action = #([ACTION, "Action","it.polimi.recovery.nodes.ActionNode"], #m,  #([PARAM_LIST, "Param_List","it.polimi.monitor.nodes.ParameterListNode"], #l));
    }
    ;


rules
    :(rule)+
    {#rules =#([RULES,"Rules"],#rules);}
    ;

rule
    : sub_rule ( (KW_LR_IMPLIES^<AST=it.polimi.monitor.nodes.binary.LeftImpliesNode> | KW_RL_IMPLIES^<AST=it.polimi.monitor.nodes.binary.RightImpliesNode> | KW_2WIMPLIES^<AST=it.polimi.monitor.nodes.binary.DoubleImpliesNode>) sub_rule )* SEMI!
    ;


/*rule:
    sub_rule SEMI!
;*/

sub_rule:
    and_expression (OR^<AST=it.polimi.monitor.nodes.binary.LogicalOperatorNode> and_expression)*
;

and_expression:
    equals_expression (AND^<AST=it.polimi.monitor.nodes.binary.LogicalOperatorNode> equals_expression)*
;

equals_expression:
    relational_expression ( (DOUBLE_EQUALS^<AST=it.polimi.monitor.nodes.binary.OperatorNode> | NOT_EQUALS^<AST=it.polimi.monitor.nodes.binary.OperatorNode> ) relational_expression)?
;

relational_expression:
    operator_expression ( (GT^<AST=it.polimi.monitor.nodes.binary.RelationalOperatorNode> | GTE^<AST=it.polimi.monitor.nodes.binary.RelationalOperatorNode> | LT^<AST=it.polimi.monitor.nodes.binary.RelationalOperatorNode> | LTE^<AST=it.polimi.monitor.nodes.binary.RelationalOperatorNode>) operator_expression)?
;

operator_expression:
    basic_expression ( (PLUS^<AST=it.polimi.monitor.nodes.binary.ArithmeticOperatorNode> | MINUS^<AST=it.polimi.monitor.nodes.binary.ArithmeticOperatorNode> | STAR^<AST=it.polimi.monitor.nodes.binary.ArithmeticOperatorNode> | SLASH^<AST=it.polimi.monitor.nodes.binary.ArithmeticOperatorNode> | PERCENTAGE^<AST=it.polimi.monitor.nodes.binary.ArithmeticOperatorNode> ) basic_expression)*
;

basic_expression:
    (variable DOT) => dot_expression | variable | exists | forall | let | store | resp_time | avg | min | max | sum | product |KW_TRUE<AST=it.polimi.monitor.nodes.SimpleAST> | KW_FALSE<AST=it.polimi.monitor.nodes.SimpleAST> | NUMBER<AST=it.polimi.monitor.nodes.SimpleAST> | string_value
;

forall!:
    LEFT_PAREN  KW_FORALL DOLLAR_SIGN v:IDENTIFIER<AST=it.polimi.monitor.nodes.SimpleAST> KW_IN s:variable SEMI r:sub_rule  RIGHT_PAREN
    {
         if (symbols.get(v.getText())==null )
            symbols.put(v.getText(), new Symbol(v.getText()));
        else {
            inputState.guessing++;
            throw new it.polimi.exception.DuplicateIdentifierException();
        }
        #forall = #([FORALL, "Forall","it.polimi.monitor.nodes.complex.ForAllNode"], #([VAR, "Variable","it.polimi.monitor.nodes.AliasNode"], #v, #s), #r);
        {
            symbols.remove(v.getText());
        }
    }
;

exists!:
    LEFT_PAREN  KW_EXISTS  DOLLAR_SIGN v:IDENTIFIER<AST=it.polimi.monitor.nodes.SimpleAST> KW_IN s:variable SEMI r:sub_rule  RIGHT_PAREN
    {  if (symbols.get(v.getText())==null )
            symbols.put(v.getText(), new Symbol(v.getText()));
        else {
            inputState.guessing++;
            throw new it.polimi.exception.DuplicateIdentifierException();
        }
        #exists = #([EXISTS, "Existsl","it.polimi.monitor.nodes.complex.ExistsNode"], #([VAR, "Variable","it.polimi.monitor.nodes.AliasNode"], #v, #s), #r);
        {
            symbols.remove(v.getText());
        }

    }
;

dot_expression!:
    v:variable DOT m:IDENTIFIER<AST= it.polimi.monitor.nodes.SimpleAST> LEFT_PAREN (l:list)? RIGHT_PAREN
    {
        #dot_expression = #([METHOD, "Method","it.polimi.monitor.nodes.MethodNode"], #v, #m,  #([PARAM_LIST, "Param_List","it.polimi.monitor.nodes.ParameterListNode"], #l));
    // #dot_expression = #([METHOD, "Method"], #(#m, #v, #([PARAM_LIST, "Param_List"], #l)));
    }
;

let!:
    KW_LET DOLLAR_SIGN i:IDENTIFIER<AST= it.polimi.monitor.nodes.SimpleAST> EQUALS s:sub_rule
    {   if (symbols.get(i.getText())==null )
            symbols.put(i.getText(), new Symbol(i.getText()));
        else {
            inputState.guessing++;
            throw new it.polimi.exception.DuplicateIdentifierException();
        }
        #let = #([LET, "Letsl","it.polimi.monitor.nodes.LetNode"],  #i, #s);
    }
;

store!:
    KW_STORE  DOLLAR_SIGN i:IDENTIFIER<AST= it.polimi.monitor.nodes.SimpleAST> EQUALS x:sub_rule
    {  if (symbols.get(i.getText())==null )
            symbols.put(i.getText(), new Symbol(i.getText()));
        else {
            inputState.guessing++;
            throw new it.polimi.exception.DuplicateIdentifierException();
        }
        #store = #([STORE, "Storesl","it.polimi.monitor.nodes.StoreNode"],  #i, #x);
    }
;

resp_time:
     DOLLAR_SIGN KW_RESP_TIME
    {#resp_time = #([RESP_TIME, "Resp_time", "it.polimi.monitor.nodes.RespTimeNode"]);}
    ;

avg!:
    LEFT_PAREN KW_AVG DOLLAR_SIGN v:IDENTIFIER<AST=it.polimi.monitor.nodes.SimpleAST> KW_IN s:variable SEMI r:sub_rule  RIGHT_PAREN
        {
             if (symbols.get(v.getText())==null )
                symbols.put(v.getText(), new Symbol(v.getText()));
            else {
                inputState.guessing++;
                throw new it.polimi.exception.DuplicateIdentifierException();
            }
        #avg = #([AVG,"Avg","it.polimi.monitor.nodes.complex.AvgNode"], #([VAR, "Variable","it.polimi.monitor.nodes.AliasNode"], #v, #s), #r);
        {
            symbols.remove(v.getText());
        }
    }
;

sum!:
    LEFT_PAREN KW_SUM DOLLAR_SIGN v:IDENTIFIER<AST=it.polimi.monitor.nodes.SimpleAST> KW_IN s:variable SEMI r:sub_rule  RIGHT_PAREN
        { if (symbols.get(v.getText())==null )
                symbols.put(v.getText(), new Symbol(v.getText()));
            else {
                inputState.guessing++;
                throw new it.polimi.exception.DuplicateIdentifierException();
            }
        #sum = #([SUM, "Sum","it.polimi.monitor.nodes.complex.SumNode"], #([VAR, "Variable","it.polimi.monitor.nodes.AliasNode"], #v, #s), #r);
        {
            symbols.remove(v.getText());
        }
    }
;

min!:
    LEFT_PAREN KW_MIN DOLLAR_SIGN v:IDENTIFIER<AST=it.polimi.monitor.nodes.SimpleAST> KW_IN s:variable SEMI r:sub_rule  RIGHT_PAREN
        {  if (symbols.get(v.getText())==null )
            symbols.put(v.getText(), new Symbol(v.getText()));
        else {
            inputState.guessing++;
            throw new it.polimi.exception.DuplicateIdentifierException();
        }
        #min = #([MIN, "Min","it.polimi.monitor.nodes.complex.MinNode"], #([VAR, "Variable","it.polimi.monitor.nodes.AliasNode"], #v, #s), #r);
        {
            symbols.remove(v.getText());
        }
    }
;
max!:
    LEFT_PAREN KW_MAX DOLLAR_SIGN v:IDENTIFIER<AST=it.polimi.monitor.nodes.SimpleAST> KW_IN s:variable SEMI r:sub_rule  RIGHT_PAREN
        {  if (symbols.get(v.getText())==null )
            symbols.put(v.getText(), new Symbol(v.getText()));
        else {
            inputState.guessing++;
            throw new it.polimi.exception.DuplicateIdentifierException();
        }
        #max = #([MAX, "Max","it.polimi.monitor.nodes.complex.MaxNode"], #([VAR, "Variable","it.polimi.monitor.nodes.AliasNode"], #v, #s), #r);
        {
            symbols.remove(v.getText());
        }
    }
;

product!:
    LEFT_PAREN KW_PRODUCT DOLLAR_SIGN v:IDENTIFIER<AST=it.polimi.monitor.nodes.SimpleAST> KW_IN s:variable SEMI r:sub_rule  RIGHT_PAREN
        {  if (symbols.get(v.getText())==null )
            symbols.put(v.getText(), new Symbol(v.getText()));
        else {
            inputState.guessing++;
            throw new it.polimi.exception.DuplicateIdentifierException();
        }
        #product = #([PRODUCT, "Product","it.polimi.monitor.nodes.complex.ProductNode"], #([VAR, "Variable","it.polimi.monitor.nodes.AliasNode"], #v, #s), #r);
        {
            symbols.remove(v.getText());
        }
    }
;
variable:
    LEFT_PAREN! ( resp_time | ivar | evar | hvar ) RIGHT_PAREN! | ( ivar | evar | hvar )
;

ivar!:
    DOLLAR_SIGN v:IDENTIFIER<AST=it.polimi.monitor.nodes.SimpleAST>  (x:xpath_expression)?
    {
        #ivar = #([INTERNAL_VARIABLE, "BPEL_VAR","it.polimi.monitor.nodes.InternalVarNode"], #([VAR, "Variable","it.polimi.monitor.nodes.VariableNode"],#v), #x);
    }
;

evar!:
    r:returnType  LEFT_PAREN wsdl:string_value COMMA wm:string_value COMMA ins:string_value COMMA outs:xpath_expression RIGHT_PAREN
    {
        #evar = #([EXTERNAL_VARIABLE, "ExtVar","it.polimi.monitor.nodes.ExtVarNode"], (#r, #wsdl, #wm, #ins, #outs));
    }
;
returnType!:
    KW_RETURN_NUMBER  {#returnType=#([RETURN_NUMBER, "RETURN_NUMBER"]);} |
    KW_RETURN_BOOL {#returnType=#([RETURN_BOOL,"RETURN_BOOL"]);} |
    KW_RETURN_STRING {#returnType=#([RETURN_STRING,"RETURN_STRING"]);}
;
/*hvar!:
    KW_RETRIEVE  LEFT_PAREN p:string_value (COMMA u:string_value)? (COMMA i:NUMBER)? COMMA l:xpath_expression COMMA ass:NUMBER<AST=it.polimi.monitor.nodes.SimpleAST> COMMA DOLLAR_SIGN al:IDENTIFIER<AST=it.polimi.monitor.nodes.SimpleAST> (COMMA n:NUMBER<AST=it.polimi.monitor.nodes.SimpleAST>)? RIGHT_PAREN
    {
        #hvar = #([RETRIEVE, "Retrieve","it.polimi.monitor.nodes.RetrieveNode"], #([PROCESS_ID, "Process_ID","it.polimi.monitor.nodes.ProcessIdNode"], #p), #([ USER_ID, "User_ID","it.polimi.monitor.nodes.UserIdNode"],#u), #([INSTANCE_ID, "Instance_ID","it.polimi.monitor.nodes.InstanceIdNode"],#i), #([LOCATION, "Location","it.polimi.monitor.nodes.LocationNode"],#l), #([ ASSERTION_TYPE, "Assertion_Type","it.polimi.monitor.nodes.AssertionTypeNode"],#ass), #([HISTORICAL_VARIABLE,"H_VAR", "it.polimi.monitor.nodes.AliasNameNode"],#al),#([NUMBER_OF_RESULTS, "Number_Of_Result","it.polimi.monitor.nodes.NumMaxResultsNode"],#n));
    }
;*/
hvar!:
    KW_RETRIEVE  LEFT_PAREN p:string_value (COMMA u:string_value)? (COMMA i:NUMBER)? COMMA l:string_value COMMA ass:NUMBER<AST=it.polimi.monitor.nodes.SimpleAST> COMMA DOLLAR_SIGN al:IDENTIFIER<AST=it.polimi.monitor.nodes.SimpleAST> (COMMA n:NUMBER<AST=it.polimi.monitor.nodes.SimpleAST>)? RIGHT_PAREN
    {
        #hvar = #([RETRIEVE, "Retrieve","it.polimi.monitor.nodes.RetrieveNode"], #([PROCESS_ID, "Process_ID","it.polimi.monitor.nodes.ProcessIdNode"], #p), #([ USER_ID, "User_ID","it.polimi.monitor.nodes.UserIdNode"],#u), #([INSTANCE_ID, "Instance_ID","it.polimi.monitor.nodes.InstanceIdNode"],#i), #([LOCATION, "Location","it.polimi.monitor.nodes.LocationNode"],#l), #([ ASSERTION_TYPE, "Assertion_Type","it.polimi.monitor.nodes.AssertionTypeNode"],#ass), #([HISTORICAL_VARIABLE,"H_VAR", "it.polimi.monitor.nodes.AliasNameNode"],#al),#([NUMBER_OF_RESULTS, "Number_Of_Result","it.polimi.monitor.nodes.NumMaxResultsNode"],#n));
    }
;
/*   KW_RETRIEVE  LEFT_PAREN p:string_value COMMA (u:string_value)? COMMA (i:NUMBER)? COMMA l:xpath_expression COMMA ass:NUMBER COMMA DOLLAR_SIGN al:IDENTIFIER<AST=it.polimi.monitor.nodes.SimpleAST> COMMA n:NUMBER RIGHT_PAREN
    {  if (symbols.get(al.getText())==null )
            symbols.put(al.getText(), new Symbol(al.getText()));
        else {
            inputState.guessing++;
            throw new it.polimi.exception.DuplicateIdentifierException();
        }
//  #hvar = #([RETRIEVE, "Retrievel","it.polimi.monitor.nodes.RetrieveNode"], #([PROCESS_ID, "Process_ID"], #p), #([ USER_ID, "User_ID"],#u), #([INSTANCE_ID, "Instance_ID"],#i), #([LOCATION, "Location"],#l), #([ ASSERTION_TYPE, "Assertion_Type"],#ass), #([ALIAS_HISTORICAL_VARIABLE,"Alias_HVar"],#al),#([NUMBER_OF_RESULTS, "Number_Of_Result"],#n));
        #hvar = #([RETRIEVE, "Retrievel","it.polimi.monitor.nodes.RetrieveNode"], #p,#u,#i,#l,#ass ,#al, #n);
    }
;*/

list:
    sub_rule (COMMA! sub_rule)*
  //  { #list = #([PARAM_LIST, "PARAM_LIST"], #list); }
;

string_value:
    s1:sub_string_value (PLUS^<AST=it.polimi.monitor.nodes.binary.StringConcatenationNode> s2: sub_string_value)*
     //{#string_value = #([CONCAT,"String_value"], #s1, #s2);}
;


sub_string_value:
    IDENTIFIER<AST=it.polimi.monitor.nodes.StringAST> | LITERAL<AST=it.polimi.monitor.nodes.StringAST> | variable
;

serialized!:
    s1:string_value (PIPE s2:string_value)*
    {
        #serialized = #([SERIES,"Series"],  #s1, #s2);
    }
;

xpath_expression!:
    u:union_expr
    {
        #xpath_expression = #([XPATH, "XPath_Expression","it.polimi.monitor.nodes.XPathExpressionNode"], #u);
    }
;

location_path:
    absolute_location_path | relative_location_path
;

absolute_location_path:
    ( SLASH^<AST=it.polimi.monitor.nodes.SlashNode> | DOUBLE_SLASH^ ) ( ( AT | STAR | IDENTIFIER ) => i_relative_location_path | )
;

relative_location_path:
    i_relative_location_path
;

i_relative_location_path:
    step ( ( SLASH^<AST=it.polimi.monitor.nodes.SlashNode> | DOUBLE_SLASH^ ) step )*
;

step:
    ( // If it has an axis
        ( (IDENTIFIER DOUBLE_COLON | AT)=> axis | )( ( ( (ns:IDENTIFIER COLON)? ( id:IDENTIFIER<AST=it.polimi.monitor.nodes.SimpleAST> |
            STAR ) ) ) | special_step ) ( predicate )* ) | abbr_step ( predicate )*
;

special_step:
    {
        LT(1).getText().equals("processing-instruction")
    }?
        IDENTIFIER LEFT_PAREN ( IDENTIFIER )? RIGHT_PAREN
    | {
        LT(1).getText().equals("comment")
            || LT(1).getText().equals("text")
            || LT(1).getText().equals("node")
    }?
        IDENTIFIER LEFT_PAREN RIGHT_PAREN
;

axis:
    ( id:IDENTIFIER DOUBLE_COLON^ | AT )
;

// ----------------------------------------
//  Section 2.4
//   Predicates
// ----------------------------------------

// .... production [8] ....
//
predicate:
    LEFT_BRACKET^ predicate_expr RIGHT_BRACKET!
;

// .... production [9] ....
//
predicate_expr:
    expr
;

// .... production [12] ....
//
abbr_step:
    DOT | DOT_DOT
;

// .... production [13] ....
//
abbr_axis_specifier:
    ( AT )?
;


// ----------------------------------------
//  Section 3
//   Expressions
// ----------------------------------------

// ----------------------------------------
//  Section 3.1
//   Basics
// ----------------------------------------

// .... production [14] ....
//
expr:
    or_expr
;

// .... production [15] ....
//
primary_expr:
    variable_reference | LEFT_PAREN! expr RIGHT_PAREN! | literal | number | function_call
;

literal:
    lit:LITERAL^
;

number:
    NUMBER^
;

variable_reference:
    DOLLAR_SIGN^ IDENTIFIER
;

// ----------------------------------------
//  Section 3.2
//   Function Calls
// ----------------------------------------

// .... production [16] ....
//
function_call:
    IDENTIFIER LEFT_PAREN^ ( arg_list )? RIGHT_PAREN!
;

// .... production [16.1] ....
//
arg_list:
    argument ( COMMA argument )*
;

// .... production [17] ....
//
argument:
    expr
;

// ----------------------------------------
//  Section 3.3
//   Node-sets
// ----------------------------------------

// .... production [18] ....
//
union_expr:
    path_expr ( PIPE! path_expr )*
;

// .... production [19] ....
//

path_expr:
    // This is here to differentiate between the
    // special case of the first step being a NodeTypeTest
    // or just a normal filter-expr function call.
    // Is it a special nodeType 'function name'

    (IDENTIFIER LEFT_PAREN)=>{ LT(1).getText().equals("processing-instruction")
        || LT(1).getText().equals("comment")
        ||  LT(1).getText().equals("text")
        ||  LT(1).getText().equals("node")
    }?

    location_path | (IDENTIFIER LEFT_PAREN)=> filter_expr ( absolute_location_path )?
        | (DOT|DOT_DOT|SLASH|DOUBLE_SLASH|IDENTIFIER|AT)=> location_path | filter_expr ( absolute_location_path )?
;

// .... production [20] ....
//
filter_expr:
    primary_expr ( predicate )*
;


// ----------------------------------------
//  Section 3.4
//   Booleans
// ----------------------------------------

// .... production [21] ....
//
or_expr:
    and_expr ( KW_OR^ and_expr )*
;

// .... production [22] ....
//
and_expr:
    equality_expr ( KW_AND^ equality_expr )?
;

// .... production [23] ....
//
equality_expr:
    relational_expr ( ( EQUALS^ | NOT_EQUALS^ ) relational_expr )?
;

// .... production [24] ....
//
relational_expr:
    additive_expr ( ( LT^ | GT^ | LTE^ | GTE^ ) additive_expr )?
;

// ----------------------------------------
//  Section 3.5
//   Numbers
// ----------------------------------------

// .... production [25] ....
//
additive_expr:
    mult_expr ( ( PLUS^ | MINUS^ ) mult_expr )?
;

// .... production [26] ....
//
mult_expr:
    unary_expr ( ( STAR^ | DIV^ | MOD^ ) unary_expr )?
;

// .... production [27] ....
//
unary_expr:
    union_expr | MINUS unary_expr
;
