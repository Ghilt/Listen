# L=tn

L=tn (read as the english word 'listen') is an instrumental programming language for humanity if we ever hope to reach the stars. The most useful things with languages are list manipulation, and opaqueness of syntax, both of which L=tn brings to the table with anthropomorphic confidence.

## Overview

We are going to start wit ha really simple example. Let's take a program that takes a list of integers and multiplies them with 2 and outputs them, it would look like this:

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

Lets examine the following program which only holds regular functions: 

    2-3+4*5a" apples"

It has 4 functions, 4 number literals and 1 string literal. It evaluates from left to right if all functions have the same precedence value. The precedence values of the functions above are what you would expect. Each function's precedence value is listed in the function list section.

What happens when evaluating this program is that first the multiplication gets executed and the result is: `2-3+20a" apples"`. Which after the subtraction becomes `-1+20a" apples"`. After the addition is performed wer are left with the `a` function, which is the string append function. So the final result becomes a string `"19 apples"`.

### Context function and Function context

Let's leave example-land for a minute and talk about the structure that is imposed by L=tn syntax. The two types of functions, regular ones which just discussed and ones that produce a context, they are called context functions. As can be seen in the initial example, a context is like a lambda starting directly after the function. The differecne from a lambda is that it is more closely tied to the concept of a list and provides shortcuts like that an index is always fetchable from within a context.

A L=tn program can be thought of as a string of functions optimised for a flow like `list.map{ it * 2 }.filter{ it < 10}.flatMap{ listOf(0, it)}`. The previous is written as `M*2F<10P(0av)t` (TODO check precedence && fix list append function, add some static variables... also maybe context key for function values/index nearer surface). To support this with as few characters as possible the paranthesis are scrapped in favor of ending a context when a new context creator is found.

That was only half the truth (a quarter of it in fact). A context can be ended in 4 ways:

* New context creator ends the context.
* Special end function character: `;`
* The context creator is within paranthesis, then the context exists until the closing paranthesis. `M(F>4)
* End of the program

Since the language is built around the context functions and lists the syntax becomes more awkward if you do not use them. The context function is part of a larger internal structure, lets call it FunctionContext. A FunctionContext can contain the following (everything is optional):

`listProvider---contextLessFunctions---contextFunction---contextFunctionInputs`

And it really is that entire thing you end in the 4 ways outlined above.

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

#### Configuration values

Some context functions take configuration values to modify their behavior. The values are then taken in order from after the list provider. If the context function requires two configuration values it will use the first two values and discard the rest. Regular functions can not have any configuration values.

## Types

L=tn uses the following types:

* ANY - No restrictions, can be anything
* STRING - The language does not differentiate between chars and strings.
* NUMBER - The langauge does not make a bigg fuzz about the difference of Integer and Doubles, It converts happily from and to.
* BOOL - Everything can be interpreted as a boolean
* LIST_TYPE - Lists
  
## Context Functions
todo
## Functions
todo
Credit to https://oeis.org/ for being a great resource
