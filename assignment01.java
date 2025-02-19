import java.io.*;
import java.util.*;

public class assignment01 {
    static HashMap<String, List<String>> symbol_table = new HashMap<>();
    private Set<String> datatypes;
    private Set<String> arithmetic_operations;
    private Set<String> input;
    private Set<String> output;
    private Set<String> keywords;
    private Set<String> specialSymbols;
    private Set<String> constants;
    private static int entries;
    private boolean check = false;

    public assignment01() {
        symbol_table = new HashMap<>();
        datatypes = new HashSet<>(Arrays.asList("int", "float", "double", "char", "bool"));
        arithmetic_operations = new HashSet<>(Arrays.asList("+", "-", "*", "/", "%"));
        input = new HashSet<>(
                Arrays.asList("Scanner", "System.in", "nextInt()", "nextLine()", "BufferedReader", "readLine()"));
        keywords = new HashSet<>(Arrays.asList("new", "if", "else", "return", "for", "while"));
        specialSymbols = new HashSet<>(Arrays.asList("=", ";", "(", ")", "{", "}", "[", "]"));
        output = new HashSet<>(Arrays.asList("System.out.print", "System.out.println", "System.out.printf"));
        constants = new HashSet<>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
        entries = 0;
    }

    // To read a file line by line and tokenize it
    public void read_file(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                tokenize(line);
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }

    // Actual function which tokenizes the program
    public void tokenize(String program) {
        if (check) {
            if (program.contains("*/")) {
                check = false;
                program = program.substring(program.indexOf("*/") + 2);
            } else {
                return;
            }
        }

        if (program.contains("//")) {
            program = program.substring(0, program.indexOf("//"));
        }

        if (program.contains("/*")) {
            check = true;
            program = program.substring(0, program.indexOf("/*"));
        }

        String[] parts = program.split("\\s+|(?=[-+*/=;(){}])|(?<=[-+*/=;(){}])");

        for (String token : parts) {
            if (token.isEmpty())
                continue;

            else if (keywords.contains(token)) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Keyword"));
            }

            else if (datatypes.contains(token)) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Datatype"));
            }

            else if (arithmetic_operations.contains(token)) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Arithmetic Operator"));
            }

            else if (input.contains(token)) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Input"));
            }

            else if (output.contains(token)) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Output"));
            }

            else if (specialSymbols.contains(token)) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Special Symbol"));
            }

            else if (constants.contains(token) || token.matches("\\d+")) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Integer Constant"));
            }

            else if (token.matches("\\d+\\.\\d+")) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Decimal Constant"));
            }

            else if (token.matches("^[a-z]+$")) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Identifier"));
            }

            entries++;
        }
    }

    // Function to print the token
    public void print_tokens() {
        for (Map.Entry<String, List<String>> entry : symbol_table.entrySet()) {
            System.out.println(entry.getKey() + " : " + String.join(", ", entry.getValue()));
        }
    }

    public static HashMap<String, List<String>> getSymbolTable() {
        return symbol_table;
    }

    public static void main(String[] args) {
        assignment01 lexer = new assignment01();

        lexer.read_file("program.txt");

        // lexer.tokenize(program);
        lexer.print_tokens();

        regularExpressions.mapTokensToRegex(getSymbolTable());
        regularExpressions.printRegexMappings();
        // regularExpressions.printNFAs();
    }
}