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

### Context function

Let's leave example-land for a minute and talk about the structure that is imposed by L=tn syntax. There are two types of functions, regular ones and ones that produce a context, called context functions. As can be seen in the initial example, a context is like a lambda starting directly after the function. The differecne from a lambda is that it is more closely tied to the concept of a list and provides shortcuts like that an index is always fetchable from within a context.

A L=tn program can be thought of as a string of functions optimised for a flow like `list.map{ it * 2 }.filter{ it < 10}.flatMap{ listOf(0, it)}`. The previous is written as `M*2F<10P(0av)t` (TODO check precedence && fix list append function... also maybe context key for function values/index nearer surface). To support this with as few characters as possible the paranthesis are scrapped in favor of ending a context when a new context creator is found.

That was only half the truth (a quarter of it in fact). A context can be ended in 4 ways:

* New context creator ends the context.
* Special end function character: `;`
* The context creator is within paranthesis, then the context exists until the closing paranthesis. `M(F>4)
* End of the program

Since the language is built around the context functions and lists the syntax becomes more awkward if you do not use them. The context function is part of a larger internal structure, lets call it FunctionContext. A FunctionContext can contain the following (everything is optional):

`listProvider---configurationValues---contextFunction---contextFunctionInputs`

And it really is that entire thing you end in the 4 ways outlined above.


    
### TODO

Credit to https://oeis.org/ for being a great resource
