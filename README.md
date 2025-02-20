# Lexical Analyzer and Token Validator

## Overview
This project is a lexical analyzer and token validator for a custom programming language. The program reads a source file, tokenizes it, builds a symbol table, and validates tokens using a deterministic finite automaton (DFA). Additionally, it integrates error checking to identify syntax-related issues.

## Features
- **Lexical Analysis**: Reads source code and identifies keywords, datatypes, operators, identifiers, and constants.
- **Symbol Table Generation**: Stores tokens with their classification and scope.
- **Regular Expression to NFA/DFA Conversion**: Converts regular expressions into NFAs and optimizes them into DFAs for token validation.
- **Error Checking**: Identifies syntax errors and provides detailed feedback.

## File Structure
- `a1.java`: Main class responsible for lexical analysis, symbol table creation, and token validation.
- `errorChecker.java`: Handles error detection in the provided source code.

## Keywords and Rules

### 1. Keywords
Reserved words recognized in the language:
```
new, if, else, return, for, while
```

### 2. Datatypes
Supported primitive data types:
```
int, float, double, char, boolean
```

### 3. Arithmetic Operators
Basic arithmetic operations including power:
```
+, -, *, /, %, ^
```

### 4. Input Operations
Methods for user input handling:
```
Scanner, System.in, nextInt(), nextLine(), BufferedReader, readLine()
```

### 5. Output Operations
Methods for displaying output:
```
System.out.print, System.out.println, System.out.printf
```

### 6. Special Symbols
Punctuation and control symbols:
```
=, ;, (, ), {, }, [, ]
```

### 7. Constants
Recognized numeric constants:
```
0, 1, 2, 3, 4, 5, 6, 7, 8, 9
```

### 8. Identifiers
Identifiers follow the rule:
```
Must start with a lowercase letter and may contain additional lowercase letters.
```

## Program Execution
1. **Compile and Run**
   ```sh
   javac a1.java
   java a1
   ```
2. **Input File Format**
   - The program reads a `.ba` file containing source code.
   - Ensure the file is correctly formatted before execution.

## Symbol Table
The program maintains a symbol table storing each token with the following attributes:
- **Token Value**: The actual token from the source code.
- **Type**: Classification such as Keyword, Identifier, Operator, etc.
- **Scope**: Global or Local (for identifiers).

## Token Validation
- A master regular expression is used to validate tokens.
- Tokens are checked against DFA transitions to determine validity.

## Error Checking
- The `errorChecker` class reads the source code and identifies syntax errors.
- Reports missing semicolons, unmatched braces, and incorrect identifiers.

## Example Execution
Given the following input file (`program.ba`):
```java
int x = 5;
float y = 3.14;
if (x > 0) {
    System.out.println("Positive");
}
```
**Output:**
```
Lexer Tokens:
0 : int, Datatype, -
1 : x, Identifier, Global
2 : =, Special Symbol, -
3 : 5, Integer Constant, -
4 : ;, Special Symbol, -
...
Token Validation Results:
Token 'int' is valid.
Token 'x' is valid.
...
```

## Conclusion
This project provides a fundamental lexical analyzer with token validation and error handling for a custom language. The system ensures correctness through DFA-based validation and reports errors efficiently.
