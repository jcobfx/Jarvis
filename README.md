# Jarvis Interpreter

First version of Jarvis interpreter made with Kotlin. 

For newer maintained versions look up my other repo: [JarvisCompiler](https://github.com/jcobfx/JarvisCompiler)

## Usage

All you need is clone this repo and run it in your IDE. Additionally you will need to specify path to Jarvis code file in command line parameters.

## Jarvis

### Comments

```
# line comment
x = 5 # comment
```

Comments start from \# and end at the end of line.
Everything you put after \# is ignored.

### Defining variables

```
x = 5
y = 5.5
z = "literal"
```

There is no way to declare variable writing only it's name.
All variables can be reassigned with value of same or any other type.
They cannot be made immutable. 
The only immutable vars are global defined in language (print, etc.) and vars declared in classes.

### Tuples

```
x = (1, 2.3, "4")
y = ("5" 6.7 8)
z = (9 "10", 11.12)
```

You can simply create tuple with parentheses `(`, `)`.
Commas between values are exceptional and can be skipped.
You can mix using commas and only spaces.

### Consumers

```
f => (param1 param2, param3)
temp = param2 + param3
return (param1, temp)
```

Consumer names are variables so you can change their value.
After name of variable you put special symbol `=>` (named feed) and then tuple containing parameters (they can only be identifier names) and end it with end of line.
Next you can optionally put code that will be your consumer's body.
You need to end consumer definition with return statement (can be empty, just `return`).
Inside consumer's body other consumers (or even classes) can be defined.

#### Feeding

```
("literal" 1 2.3) => f
x = (1, 2, 3) => f
```

To feed the consumer you need to give it tuple with values accepted by consumer.
Consumers' return values can be assigned to variables.

### Classes

```
c1 => (field1 field2)
return class

c2 => (f1)
f1 = f1 + f1
f => ()
return
return class

obj = (1) => c2

x = c2.f1
() => obj.f
```

Classes are similar to consumers but they need class keyword to be put after return `return class`.
Inside their body you can do same stuff as in consumer's body but every variable inside class immutable.
To make fields mutable you need to create object by feeding the class and assigning it to variable.
Mutating object's fields can only be done by creating proper mutating consumers inside class's body.
You can get variables from classes and objects using dot.

### This

```
c => (a)
f1 => ()
return a

f2 => ()
this.a = 5
return a

f3 => ()
a = 5
return a
return class
```

Keyword `this` makes it that you operate on variable in current environment (interpreter will not check parent envs).
In our example `f1` will return None because `a` in class definition is not defined.
`f2` return 5 because `a` is set inside consumer's body (do not change `a` in class `c`). 
Feeding `f3` will end with an error because `a = 5` try to mutate immutable variable inside class `c`.
