import java.util.*;
import java.io.*;

public class errorChecker {
    private List<String> errors;
    private Stack<int[]> bracesStack;
    private Set<String> declaredIdentifiers;
    private a1 lexer;

    public errorChecker(a1 lexer) {
        this.errors = new ArrayList<>();
        this.bracesStack = new Stack<>();
        this.declaredIdentifiers = new HashSet<>();
        this.lexer = lexer;
    }

    public void readFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                checkSyntax(line, lineNumber);
            }
            checkUnclosedBraces();
        } catch (IOException e) {
            errors.add("Error reading the file: " + e.getMessage());
        }
    }

    private void checkSyntax(String line, int lineNumber) {
        line = line.replaceAll("//.*", "").replaceAll("/\\*.*?\\*/", "");

        String[] tokens = line.split("\\s+|(?=[{}();=+*/-])|(?<=[{}();=+*/-])");

        String lastDatatype = null;

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if (token.isEmpty())
                continue;

            // To handle braces
            if (token.equals("{") || token.equals("(")) {
                bracesStack.push(new int[] { token.charAt(0), lineNumber });
            } else if (token.equals("}") || token.equals(")")) {
                if (bracesStack.isEmpty()) {
                    errors.add("Unmatched closing brace '" + token + "' at line " + lineNumber);
                } else {
                    int[] lastBrace = bracesStack.pop();
                    char openBrace = (char) lastBrace[0];
                    int openLine = lastBrace[1];

                    if (!isMatchingBrace(openBrace, token.charAt(0))) {
                        errors.add("Mismatched brace '" + token + "' at line " + lineNumber +
                                ", expected closing for '" + openBrace + "' from line " + openLine);
                    }
                }
            }

            // To check the datatypes
            if (lexer.getDatatypes().contains(token)) {
                lastDatatype = token;
                continue;
            }

            // Handle variable declarations
            if (lastDatatype != null && token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                declaredIdentifiers.add(token);
                lastDatatype = null;
                continue;
            }

            // To handle numbers
            if (token.matches("\\d+(\\.\\d+)?")) {
                continue;
            }

            // To check for invalid operation combinations
            if (i > 0 && lexer.getOperations().contains(token) && lexer.getOperations().contains(tokens[i - 1])) {
                errors.add("Invalid operator combination '" + tokens[i - 1] + token + "' at line " + lineNumber);
            }

            // To check for undeclared variables
            if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*") &&
                    !declaredIdentifiers.contains(token) &&
                    !lexer.getKeywords().contains(token) &&
                    !lexer.getInput().contains(token) &&
                    !lexer.getOutput().contains(token) &&
                    !lexer.getOperations().contains(token)) {
                errors.add("Undefined variable '" + token + "' at line " + lineNumber);
            }
        }
    }

    private boolean isMatchingBrace(char open, char close) {
        return (open == '{' && close == '}') || (open == '(' && close == ')');
    }

    private void checkUnclosedBraces() {
        while (!bracesStack.isEmpty()) {
            int[] braceInfo = bracesStack.pop();
            char brace = (char) braceInfo[0];
            int lineNumber = braceInfo[1];
            errors.add("Unclosed brace '" + brace + "' detected at line " + lineNumber);
        }
    }

    // Function to print the errors
    public void printErrors() {
        if (errors.isEmpty()) {
            System.out.println("No syntax errors found.");
        } else {
            System.out.println("\nSyntax Errors Found:");
            errors.forEach(System.out::println);
        }
    }
}