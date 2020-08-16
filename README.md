# L=tn

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
    
The above is written as `M*2F<10P(0av)t` (TODO trim all whitespace check precedence && fix list append function, add some static variables... also maybe context key for function values/index nearer surface). To support this with as few characters as possible the paranthesis are optional in favor of ending a context under certain predefined circumstances.

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

## Types

L=tn uses the following types:

* ANY - No restrictions, can be anything
* STRING - The language does not differentiate between chars and strings.
* NUMBER - The language does not make a big fuzz about the difference of Integer and Doubles, It converts happily from and to.
* BOOL - Everything can be interpreted as a boolean
* LIST_TYPE - Lists
  
A program resolves step by step and uses its type system to decide what it can resolve. 

    // β - is the toInt function, it turns anything to an int
    1+"hi there"β
  
If the `+` and `β` above have the same precedence, the `+` should be executed first, but it is skipped since the type of its second argument is of the wrong type. So the string will be converted to an int and then the addition will happen.  

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

Output: List (Input list if true, empty list if false)

| Input   | Code | Output |
|---------|------|--------|
| 1, 2, 3 | A>0  | 1, 2, 3   |
| 1, 2, 3 | A>1  | [empty list] |

#### All

Character: `Ä`

Inputs: List, Boolean

Output: List (Input list if true, empty list if false)

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
todo
Credit to https://oeis.org/ for being a great resource

## Interpreter flags
todo