This is a java compiler for Mini Pascal code. The code generated is MIPS assembly.

To use, at the command line type: java -jar minipascalc.jar <path to the file to compile> <OPTIONAL output location>

Notes:
* This compiler is not complete. Please see the following.
* In its current iteration, the compiler will skip processing of arrays and real numbers.
* The generated MIPS assembly uses psuedo instructions for relational expressions, ex. blt, bge, ...