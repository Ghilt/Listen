# L=tn &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ![create](images/ListnLogo_small.png)

L=tn (read as the english word 'listen') is an instrumental programming language for humanity if we ever hope to reach the stars. The most useful things with languages are list manipulation, and opaqueness of syntax, both of which L=tn brings to the table with anthropomorphic confidence.



## Overview

We are going to start with a really simple example. Let's take a program that takes a list of integers and multiplies them with 2 and outputs them, it would look like this:

    _M*2

To give a comparison this is equal to this structure in a non esoteric language such as kotlin:

    list.map { it * 2 }    

#### Breakdown of example

The `_` is the current list nilad, which returns the current list. It takes no input argument, its task is simple as L=tn uses a stack based structure to store its results and intermediate results.

Next in line is `M` which is the map function, recognizable from many other languages, it takes a list as first argument, and a value of type `ANY` as second argument and returns a list. It is a context function, which means that it creates a context in which further code is written in. See the next section for more information about what a 'context function' is.

The `*` is the multiplication function, it takes 2 numbers and returns their product.

`2` is a number literal.

#### That's all well and good, but how do the above fit together!? 

All functions in L=tn are infix, meaning they take the first input from in front of them, and subsequent inputs is supplied after the function call.

The reason that the multiplication works is that the first argument to all functions have a default value. The default value to the multiplication dyad is `VALUE_THEN_INDEX`. This means that the multiplication dyad attempts to draw its value from the context it is written in (In this case the mapping context). Attempting to use a context dependant default value outside a context is an error and will result in an exception.

With this information we can rewrite the above program, since all context creating functions take the current list as a default value, simplified it becomes:

    M*2
    
This program stores its result on the stack. When a program finishes it prints the value on the stack. 

### Functions

Functions are the bread and butter of the language. You cannot create your own, your programs are a sequence of the predefiend functions. All functions are a single character.

Let's examine the following program which only holds regular functions: 

    2-3+4*5a" apples"

It has 4 functions, 4 number literals and 1 string literal. It evaluates from left to right if all functions have the same precedence value. The precedence values of the functions above are what you would expect. Each function's precedence value is listed in the function list section.

When evaluating this program first the multiplication gets executed, and the result is: `2-3+20a" apples"`. Which after the subtraction becomes `-1+20a" apples"`. After the addition is performed we are left with the `a` function, which is the string append function. So the final result becomes a string `"19 apples"`.

### Context function and Function context

Let's leave example-land for a minute and talk about the structure that is imposed by L=tn. The two types of functions, regular ones which was just discussed and ones that provide a context, they are called context functions. As can be seen in the initial example, a context is like a lambda-scope starting directly after the function. The differecne from a lambda is that the context is more closely tied to the concept of a list and provides shortcuts like that an index is always fetchable from within a context.

A L=tn program can be thought of as a string of functions optimised for a flow like 

    list.map{ it * 2 }.filter{ it < 10}.flatMap{ listOf(0, it)}`
    
The above is written as `M*2F<10P(0av)t`. To support this with as few characters as possible the paranthesis are optional in favor of ending a context under certain predefined circumstances.

A context can be ended in 4 ways:

* New context creator function ends the context. `M*2F>8`
* Special end function character: `;`
* The context creator is within paranthesis, then the context exists until the closing paranthesis. `(M*4)*2`
* End of the program

The context function is part of a larger internal structure, lets call it FunctionContext. A FunctionContext can contain the following (everything is optional):

`listProvider---contextLessFunctions---contextFunction---contextFunctionInputs`

It really is that entire thing you end in the 4 ways outlined above.

The list provider above is a bit of a special hack for optimising the brevity of context functions. It was stated before that all functions are infix, context functions are special in that their first input is always the list provider of the function context. There can be values/functions in between the list provider and the context function.  

    _M*2
    _1+2+3M*2
    
The above programs gives the same result. The reason is that the values between a list provider and a context function are used as configuration values to the context function. So the longer program above becomes -> `_6M*2` and then the value `6` is thrown away, because the map function does not take any configuration value.

This is in stark contrast to the following:

    _M*2"A string"
    
The above code will throw an exception. The reason is that it supplies 2 inputs in the context of the context function. And the Map function only takes one input (of type ANY) and hence it violently protests. So the takeaway is this, configuration values are optional and not checked whilst the inputs from the context are checked for both type and for the correct number of them.

When a function context is resolved, the result is put on the stack in form of a list. If it is a single value, it is put in a list first and then put on the stack. Let's examine the following program:

    M*2M*2M*2

It will multiply the input to the program by two 3 times. The resulting stack will have 4 lists, the original one and then the 3 results of the 3 function contexts.

The final thing to say about function contexts is that if the first one of a program will add the current list provider function (`_`) silently at the start. This has the effect that if you do not want this optimization you have to cancel out of it by starting your program with the end-function character (`;`).

#### Configuration values

Some context functions take configuration values to modify their behavior. The values are then taken in order from after the list provider. If the context function requires two configuration values it will use the first two values and discard the rest. Regular functions can not have any configuration values.

#### Inner functions

The function context can have inner functions in both the contextless part and in the context function's context. An inner function is just like a regular function context, except that it resolves its value to the outer function and not to the stack. An inner function is defined by being inside paranthesis. So `M(M*2)` means 'map every item in the input list to every item in the input list mutliplied by 2'. Examine the following valid code: `((M*2)((M*2)M*2)M*2)M*2`, it runs, and is innefficient, and throws away all the results of the inner functions since they are all ultimately part of the configuration values of a mapping functions, and mapping functions do not use any configuration.

### Default values

A note about default values as they are documented a bit carelessly. Default values follow this pattern generally (exceptions exist)

| First input   | Default value |
|---------|------|
| Integer | Current value then index  |
| Double | Current value  |
| Any | Current value  |
| List | Current value then current list  |

Meaning: If a function requires a list as first input, then if it is in a context, it checks if the current value of this context is a list and uses that. If it is not a list, it defaults to the current list on top of the stack.

Integer and Double are not differentiated in the type system, but the actual nature of the numbers are taken into account when assigning default values. E.g. the round function does not default to index, as indexes always are integers.

### Control flow

There are four characters which are special control flow commands.

`(` - Start Inner Function

`)` - End Inner Function

Inner functions are useful if you need to manually override precedence of functions.

`;` - End function context

Ending functions are done when you need to use configuration values of succeeding function contexts.  

`,` - No operation

Integer literals after one another in a program needs to be separated to not become one integer(`1,2,3` vs `123`).

## Types

L=tn uses the following types:

* ANY - No restrictions, can be anything
* STRING - The language does not differentiate between chars and strings.
* NUMBER - The language does not make a big fuzz about the difference of Integer and Doubles, It converts happily from and to.
* BOOL - Everything can be interpreted as a boolean
* LIST_TYPE - Lists, all lists and values are immutable
  
A program resolves step by step and uses its type system to decide what it can resolve. 

    // § - is the toInt function, it turns anything to an int
    1+"hi there"§
  
If the `+` and `§` above have the same precedence, the `+` should be executed first, but it is skipped since the type of its second argument is of the wrong type. So the string will be converted to an int and then the addition will happen.  

# Function index

| Name                   | Symbol | Input | Output | Default input |
|------------------------|--------|-------|--------|---------------|
| currentListNilad       | [_](#information-functions)               |                | List   |  [Default values](#default-values)        |
| secondCurrentListNilad | [~](#information-functions)               |                | List   |  .            |
| currentListCountNilad  | [q](#information-functions)               |                | Number |  .            |
| indexNilad             | [i](#information-functions)               |                | Number |  .            |
| valueNilad             | [v](#information-functions)               |                | Any    |  todo, etc    |
| peekStaticStackNilad   | [p](#storage-functions)                   |                | Any    |               |
| popStaticStackNilad    | [€](#storage-functions)                   |                | Any    |               |
| storeOnStaticStackMonad| [:](#storage-functions)                   | Any            | Any    |               |
| loadFromStaticMapMonad | [?](#storage-functions)                   | Any            | Any    |               |
| storeOnStaticMapDyad   | [¨](#storage-functions)                   | Any            | Any    |               |
| sumMonad               | [Σ](#mathematical-support-functions)      | List           | Number |               |
| productMonad           | [Π](#mathematical-support-functions)      | List           | Number |               |
| isPrimeMonad           | [m](#mathematical-support-functions)      | Number         | Boolean|               |
| absoluteValueMonad     | [h](#mathematical-support-functions)      | Number         | Number |               |
| signMonad              | [j](#mathematical-support-functions)      | Number         | Number |               |
| floorMonad             | [u](#mathematical-support-functions)      | Number         | Number |               |
| roundMonad             | [ö](#mathematical-support-functions)      | Number         | Number |               |
| toIntMonad             | [§](#mathematical-support-functions)      | Any            | Number |               |
| additionDyad           | [+](#mathematical-functions)              | Number, Number | Number |               |
| subtractionDyad        | [-](#mathematical-functions)              | Number, Number | Number |               |
| multiplicationDyad     | [*](#mathematical-functions)              | Number, Number | Number |               |
| divisionDyad           | [/](#mathematical-functions)              | Number, Number | Number |               |
| wholeDivisionDyad      | [¤](#mathematical-functions)              | Number, Number | Number |               |
| moduloDyad             | [%](#mathematical-functions)              | Number, Number | Number |               |
| moduloMathematicalDyad | [£](#mathematical-functions)              | Number, Number | Number |               |
| powerDyad              | [^](#mathematical-functions)              | Number, Number | Number |               |
| minDyad                | [{](#mathematical-functions)              | Number, Number | Number |               |
| maxDyad                | [}](#mathematical-functions)              | Number, Number | Number |               |
| notMonad               | [!](#boolean-functions)                   | Any            | Boolean|               |
| smallerThanDyad        | [<](#boolean-functions)                   | Number, Number | Boolean|               |
| largerThanDyad         | [>](#boolean-functions)                   | Number, Number | Boolean|               |
| equalToDyad            | [=](#boolean-functions)                   | Any, Any       | Boolean|               |
| notEqualToDyad         | [≠](#boolean-functions)                   | Any, Any       | Boolean|               |
| andDyad                | [&](#boolean-functions)                   | Any, Any       | Boolean|               |
| orDyad                 |[\|](#boolean-functions)                   | Any, Any       | Boolean|               |
| lengthMonad            | [l](#list-functions)                      | List           | Number |               |
| distinctMonad          | [d](#list-functions)                      | List           | List   |               |
| removeDistinctMonad    | [n](#list-functions)                      | List           | List   |               |
| listByIndexMonad       | [$](#list-functions)                      | Number         | List   |               |
| reverseListMonad       | [r](#list-functions)                      | List           | List   |               |
|removeConsecutiveElementsMonad |[c](#list-functions)                | List           | List   |               |
| createListOfValueMonad | [@](#list-functions)                      | Any            | List   |               |
| intToDigitListMonad    | [α](#list-functions)                      | Number         | List   |               |
| takeDyad               | [\[](#list-functions)                     | List           | List   |               |
| dropDyad               | [\]](#list-functions)                     | List           | List   |               |
| elementByIndexDyad     | [e](#list-functions)                      | Number         | Any    |               |
| appendToListDyad       | ['](#list-functions)                      | Any, Any            | List   |          |
| appendListDyad         | [γ](#list-functions)                      | List, List          | List   |          |
| zipDyad                | [z](#list-functions)                      | List, List          | List   |          |
| padTriad               | [#](#list-functions)                      | List, Any, Number   | List   |          |
| growEntriesTriad       | [g](#list-functions)                      | List, Number, Number| List   |          |
| toUpperCaseMonad       | [k](#string-functions)                    | String        | String |                |
| toLowerCaseMonad       | [w](#string-functions)                    | String        | String |                |
| isUpperCaseMonad       | [y](#string-functions)                    | String        | Boolean|                |
| stringToListMonad      | [t](#string-functions)                    | String        | List   |                |
| appendToStringDyad     | [a](#string-functions)                    | Any, Any      | String |                |
| joinToStringDyad       | [s](#string-functions)                    | List, String  | String |                |
| alphabetGenerationDyad | [b](#generation-functions)                | Number, Number| List   |                |
| oeisGenerationDyad     | [o](#generation-functions)                | Number, Number| List   |                |
| obliterateDyad         | [x](#other-functions)                     | Any, Any      | Any    |                |
| ifBranchTriad          | [f](#other-functions)                     | Any, Any, Any | Any    |                |
| _________Context functions______________________________________                                             |
| pipeMonad              | [Ö](#context-functions)                   | List          | List  |                 |
| chunkMonad             | [C](#context-functions)                   | List          | List  |                 |
| windowMonad            | [W](#context-functions)                   | List          | List  |                 |
| filterDyad             | [F](#context-functions)                   | List, Boolean | List  |                 |
| filterSectionedDyad    | [S](#context-functions)                   | List, Boolean | List  |                 |
| filterWithNeighborsDyad| [N](#context-functions)                   | List, Boolean | List  |                 |
| allDyad                | [A](#context-functions)                   | List, Boolean | List  |                 |
| anyDyad                | [Ä](#context-functions)                   | List, Boolean | List  |                 |
| mapDyad                | [M](#context-functions)                   | List, Any     | List  |                 |
| flatMapDyad            | [P](#context-functions)                   | List, List    | List  |                 |
| extendEntriesDyad      | [E](#context-functions)                   | List, Number  | List  |                 |
| groupedStringListDyad  | [G](#context-functions)                   | List, Boolean | List  |                 |
| zipInsertionDyad       | [Z](#context-functions)                   | List, Any     | List  |                 |

## Context Functions

All context functions are capital letters and no regular functions are capital letters. All context functions have the same precedence, and due to the L=tn program structure, they do not compete with regular functions. 

#### Filter

Character: `F`

Inputs: List, Boolean

Output: List

| Input   | Code | Output |
|---------|------|--------|
| 1, 2, 3 | F>1  | 2, 3   |
| "a", "b", "c" | F="b"\|="c"  | "b", "c"   |

#### FilterSectioned

Character: `S`

Inputs: List, Boolean

Output: List

| Input   | Code | Output |
|---------|------|--------|
| 1, 2, 0, 3 | S≠0  | [1, 2], [3]  |

#### FilterWithNeighbors

Character: `N`

Inputs: List, Boolean

| Configuration value   | Type | Default |
|-----------------------|------|---------|
| neighborhoodSizeLeft  | Int  | 1       |
| neighborhoodSizeRight | Int  | 1       |

Output: List

| Input   | Code | Output |
|---------|------|--------|
| 1, 2, 0, 3 | N=0  | 2, 0, 3   |
| 0, -1, 1, 2, 0, 3, 0, 4 | 1,0N=0  | 0, 2, 0, 3, 0  |

#### All

Character: `A`

Inputs: List, Boolean

Output: List (Returns input list if true, empty list if false)

| Input   | Code | Output |
|---------|------|--------|
| 1, 2, 3 | A>0  | 1, 2, 3   |
| 1, 2, 3 | A>1  | [empty list] |

#### Any

Character: `Ä`

Inputs: List, Boolean

Output: List (Returns input list if true, empty list if false)

| Input   | Code | Output |
|---------|------|--------|
| 1, 2, 3 | Ä>2  | 1, 2, 3   |
| 1, 2, 3 | Ä>3  | [empty list]  |


#### Map

Character: `M`

Inputs: List, Any

Output: List

| Input   | Code | Output |
|---------|------|--------|
| 1, 2, 3 | M+100  | 2, 3   |
| "a", "b", "c" | M=a"!"  | "b!", "c!"   |

#### FlatMap

Character: `P`

Inputs: List, List

Output: List

| Input   | Code | Output |
|---------|------|--------|
| [1 , 2], [2, 3], [3, 4] | Pv  | 1, 2, 2, 3, 3, 4  |

#### ExtendEntries

Character: `E`

Inputs: List, Number

Output: List

| Input   | Code | Output |
|---------|------|--------|
| 1, 2, 3 | Ev-1  | 1, 2, 2, 3, 3, 3  |

#### GroupedStringList

Character: `G`

Inputs: List, Boolean

| Configuration value   | Type | Default |
|-----------------------|------|---------|
| toggle  | Boolean  | false       |

Output: List

| Input   | Code | Output |
|---------|------|--------|
| 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4 | G>2&<8  | 1, 2, 34567, 8, 9, 0, 1, 2, 34 |
| 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4 | 1G=3\|=5\|=7  | 1, 2, 345, 6, 7890123, 4 |

#### ZipInsertion

Character: `Z`

Inputs: List, Any

Output: List

| Input   | Code | Output |
|---------|------|--------|
| 1, 2, 3 | Z+1  | [1, 2], [2, 3], [3, 4] |
| 1, 2, 3 | Z"ok"  | [1, "ok"], [2, "ok"], [3, "ok"] |

### Context functions without context

This section contains functions which are context functions but they take no context argument. In essence, they could've been normal functions, but are context functions for convenience.

#### Window

Character: `W`

Inputs: List

| Configuration value   | Type | Default |
|-----------------------|------|---------|
| windowSize  | Int  | 3       |
| stepSize  | Int  | 1       |
| partialWindows  | Boolean  | false      |

Output: List

| Input   | Code | Output |
|---------|------|--------|
| 1, 2, 3, 4, 5 | 3,2W  | [1, 2, 3], [3, 4, 5] |

#### Chunked

Character: `W`

Inputs: List

| Configuration value   | Type | Default |
|-----------------------|------|---------|
| chunkSize  | Int  | 3       |

Output: List

| Input   | Code | Output |
|---------|------|--------|
| 1, 2, 3, 4, 5, 6, 7, 8, 9 | 4C  | [1, 2, 3, 4], [5, 6, 7, 8], [9] |

#### Pipe

Character: `Ö`

Inputs: List

Output: List

| Input   | Code | Output |
|---------|------|--------|
| 1, 2, 3 | Ö  | 1, 2, 3 |

## Functions

Disclaimer: The precedence values for the functions are in a bit of a mess and deserves to be looked at/reworked. They will not be listed in this section, you are referred to the source if you need them.

### Information functions

These functions mostly take no input and just return a value. Some of them needs to be inside a context to not throw exceptions at you.

| Name      | Symbol | Comment |
|-----------|--------|---------|
| currentList | _   |   This refers to the stack where all results of the function context chain are stored  |
| secondCurrentList | ~  |  Convenience function   |
| currentListCount | q   |  Returns how many lists are on the stack   |
| getListByIndex | $   | This function takes a Number input and fetches that list from the stack.   |
| index | i   |  Needs context   |
| value | v   |  Needs context   |

### Mathematical functions

These all behave as one would expect. Precedence values mimic the ones for Java.

Inputs: Number, Number

Output: Number

Their default input is the current value then index if the list that value is not a number. There is one exception to this, see table below.


| Name      | Symbol | Comment |
|-----------|--------|---------|
| addition | +   |     |
| subtraction | -   |  Default input is 0 (Reason: To work better with number literals)  |
| multiplication | *   |     |
| division | /   |     |
| wholeNumberDivision | ¤   |     |
| modulo | %   |  Programming standard, negative numbers possible in output   |
| mathematicalModulo | £   | Mathsy standard, second input decides sign of output   |
| power | ^   |     |
| minimum | {   |     |
| maximum | }   |     |

### Mathematical support functions

These are all functions that take one input.

| Name      | Symbol | Input | Output |
|-----------|--------|-------|--------|
| sum | Σ   |  List (throws exception if it is not a list of numbers)   | Number  |
| product | Π   |  List (throws exception if it is not a list of numbers)   | Number  |
| isPrime | m   |  Number   | Boolean  |
| absoluteValue | h   |  Number   | Number  |
| sign | j   |  Number   | Number  |
| floor | u  |  Number   | Number  |
| round | ö  |  Number   | Number  |
| toInt | §  |  Any   | Number (text is parsed to int, lists return size, true is 1 and others are 0)  |


### Boolean functions

These functions return a boolean value (true or false). 

Their default input for first input is value of current item unless stated otherwise.

| Name      | Symbol | Required input |
|-----------|--------|---------|
| smallerThan | <   | Number, default is value then index  |
| greaterThan | >   | Number, default is value then index   |
| equal | =   | Any |
| notEqual | ≠   | Any |
| and | &   | Any |
| or | \|   | Any |
| not | !   | Any, (does not take 2 inputs) |

### Storage functions

These functions can be used as variables. There are no local variables, only static storage which is persisted across the program's lifespan. These storage functions have very high precedence to play nicely with inner functions,

If you attempt to load before having stored anything you will get 0 as result.

#### Static stack

| Name      | Symbol | Input | Output |
|-----------|--------|-------|--------|
| storeOnStaticStack | :   | Any  | Any (returns the value just stored)   |
| popStaticStack     |  p  |  -   |   Any (removes value from on top of static stack)     |
| peekStaticStack     |  €  |  -   |   Any (value on top of static stack)     |

| Input   | Code | Output |
|---------|------|--------|
| 1, 2, 3 | 900:M+p  | 901, 902, 903  |

#### Static map

Save some value under a key, the key is of type Any.

| Name      | Symbol | Input | Output |
|-----------|--------|-------|--------|
| storeInStaticMap | ?   | Any, Any (second input is key, to enable use of default values easier)  | Any (returns the previously stored value under that key)   |
| loadFromStaticMap | ¨   | Any  | Any  |

| Input   | Code | Output |
|---------|------|--------|
| 1, 2 | M¨"myVar"x(M"myVar"?)  | [1, 1], [2, 2]  |
| 1, 2, 3 | 900¨"myVar"M¨"myVar"  | 900, 1, 2  |

### List functions

Functions pertaining to list manipulation.

| Name      | Symbol | Input | Output | Comment |
|-----------|--------|-------|--------| --------|
| length |  l   | List  | Number  |      |
| distinct |  d   | List  | List  |   Returns a list containing only distinct elements   |
| removeDistinct |  n   | List  | List  |   Returns a list with all distinct elements filtered out  |
| reverseList |  r   | List  | List  |     |
| removeConsecutiveEqualElements |  r   | List  | List  |  1,2,2,3,3,1,1,1 ->  1,2,3,1  |
| toList |  @   | Any  | List  |  Wraps input in a list   |
| intToDigitList |  α   | Number  | List  |  12300 -> 1,2,3,0,0   |
| take |  [   | List, Number  | List  |   |
| drop |  ]   | List, Number  | List  |   |
| elementByIndex |  e   | List, Number  | Any  |   |
| appendToList |  '   | Any, Any  | List  | First input is not wrapped in a list if it already is a list. Second input is always wrapped in a new list. |
| appendList |  γ   | List, List  | List  |   |
| zip |  z   | List, List  | List  | Produces a list of lists. [1,2,3],[4,5,6] -> [1,4],[2,5],[3,6]   |
| pad |  #   | List, Any, Number(length)  | List  | If length it negative then pad from the end.  [1,2,3],"x",2 -> "x","x",1,2,3 |
| growEntries |  g   | List, Number(length), Number(step)  | List  | Requires list of numbers.  [0,20,30],2,1 -> 0,1,2,20,21,22,30,31,32 |

### String functions

Functions pertaining to string manipulation.

| Name      | Symbol | Input | Output | Comment |
|-----------|--------|-------|--------| --------|
| toUpperCase |  k   | String  | String  |      |
| toLowerCase |  w   | String  | String  |      |
| isUpperCase |  y   | String  | Boolean  |      |
| stringToList |  t   | String  | List  |      |
| appendToString |  a   | Any  | Any  |  toString on the arguments before append.   |
| joinToString |  s   | List  | String(separator)  |  Turns a list into a string, runs toString on elements of list |

### Generation functions

These functions generate lists of predefined values.

| Name      | Symbol | Input | Output |
|-----------|--------|-------|--------|
| alphabetGeneration |  b   | Number (index of alphabet), Number (length) | List  |

Alphabets available are the following:

        "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
        "abcdefghijklmnopqrstuvwxyz",
        "ABCDEFGHIJKLMNOPQRSTUVWXYZÅÄÖ",
        "abcdefghijklmnopqrstuvwxyzåäö",
        "ΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩ",
        "αβγδεζηθικλμνξοπρςτυφχψω",
        "АБЦДЕФГХИЙКЛМНОПQРСТУВЩХЫЗ",
        "абцдефгхийклмноп́рстувщхыз"
        
| Name      | Symbol | Input | Output |
|-----------|--------|-------|--------|
| oeisGeneration |  b   | Number (id on https://oeis.org/), Number (length) | List  |

Credit to https://oeis.org/ for being a great resource. The sequences can be downloaded from here https://oeis.org/wiki/Welcome#Compressed_Versions


### Other functions

| Name      | Symbol | Input | Output |
|-----------|--------|-------|--------|
| obliterate |  x   | Any, Any | Any  |

Obliterate is useful when you are using the static storage functions as they often return values you do not want. Obliterate simply takes to values in and returns the second one. the first value is 'obliterated'.

| Name      | Symbol | Input | Output |
|-----------|--------|-------|--------|
| ifBranch |  f   | Any, Any, Any | Any  |

Example of if else function:

| Input   | Code | Output |
|---------|------|--------|
| -1, 13, -9 | Mv>0f"above zero""below zero"  | "below zero", "above zero", "below zero"  |

## Interpreter flags

When you run the interpreter by default it expects a path to a file containing a program, and then one or more input lists. You can modify this behavior with the following flags. 

    -f read the next input from a file
    -c read program directly from command line
    -s change the separator for the lists going forward. The default is ','
