#L=ten

L=ten (read as the english word 'listen', with the 't' extra silent) is an instrumental programming language for humanity if we ever hope to reach the stars. The most useful things with languages are list manipulation, and opaqueness of syntax, both of which L=ten brings to the table with anthropomorphic confidence.

## Overview

The main building blocks in the language can be thought of as functions. These constructs can have different number of inputs and are then referred to as Nilad (0 inputs), Monad (1 input),  (2 inputs), Triad (3 inputs ) etc. These functions then is combined into a larger structure called a chime. It will be clearer with an example:

Let's take a program that takes a list of integers and multiplies them with 2 and outputs them, it would look like this(it consists of a single chime):

    _M*2

To give a comparison this is equal to this structure in a non esoteric language such as kotlin:

    list.map { it * 2 }    

The `_` is the current list nilad, which yields the current list. It takes no input argument, its task is simple as L=ten uses a stack based structure to store its results and intermediate results.

Next in line is `M` which is the map dyad, recognizable from many other languages, it takes a list as first argument, and a value of type `ANY` as second argument and returns a list. It is a context dyad, which means that it creates a context in which further code is written in. Each chime can only have one context function.

The `*` is the multiplication dyad, it takes 2 numbers and returns their product.

`2` is a number literal.

That's all well and good, but how do the above fit together!? 

All functions in L=ten are infix, meaning they take the first input from in front of them, and subsequent inputs is supplied after the function call.

The reason that the multiplication works is that the first argument to all functions have a default value. The default value to the multiplication dyad is `VALUE_THEN_INDEX`. This means that the multiplication dyad attempts to draw its value from the context it is written in (In this case the mapping context). Attempting to use a context dependant default value outside a context is an error and will result in an exception.

With this information we can rewrite the above program, since all context creating functions take the current list as a default value, simplified it simply becomes:

    M*2
    
This program stores its result on the stack. When a program finishes it prints the value on the stack.