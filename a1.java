import java.io.*;
import java.util.*;

public class a1 {
    static HashMap<String, List<String>> symbol_table = new HashMap<>();
    private Set<String> datatypes;
    private Set<String> arithmetic_operations;
    private Set<String> input;
    private Set<String> output;
    private Set<String> keywords;
    private Set<String> specialSymbols;
    private Set<String> constants;
    private static int entries;
    private boolean check;
    private boolean checkForMain;

    public a1() {
        symbol_table = new HashMap<>();
        datatypes = new HashSet<>(Arrays.asList("int", "float", "double", "char", "boolean"));
        // Added '^' for power operator.
        arithmetic_operations = new HashSet<>(Arrays.asList("+", "-", "*", "/", "%", "^"));
        input = new HashSet<>(
                Arrays.asList("Scanner", "System.in", "nextInt()", "nextLine()", "BufferedReader", "readLine()"));
        keywords = new HashSet<>(Arrays.asList("new", "if", "else", "return", "for", "while"));
        specialSymbols = new HashSet<>(Arrays.asList("=", ";", "(", ")", "{", "}", "[", "]"));
        output = new HashSet<>(Arrays.asList("System.out.print", "System.out.println", "System.out.printf"));
        constants = new HashSet<>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
        entries = 0;
        check = false;
        checkForMain = true;
    }

    // Getters
    public Set<String> getDatatypes() {
        return datatypes;
    }

    public Set<String> getOperations() {
        return arithmetic_operations;
    }

    public Set<String> getInput() {
        return input;
    }

    public Set<String> getOutput() {
        return output;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    // Reads a file line by line and tokenizes it.
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

    // Tokenizes a given line (handles comments, spaces, and symbols).
    public void tokenize(String program) {
        // This is for handling single + multiline comments
        if (check) {
            if (program.contains("*/")) {
                check = false;
                program = program.substring(program.indexOf("*/") + 2);
            } else {
                return;
            }
        }

        // To check for scope of identifiers
        if (program.matches(".*\\bmain\\s*\\(.*\\).*")) {
            checkForMain = false;
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
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Keyword", "-"));
            } else if (datatypes.contains(token)) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Datatype", "-"));
            } else if (arithmetic_operations.contains(token)) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Arithmetic Operator", "-"));
            } else if (input.contains(token)) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Input", "-"));
            } else if (output.contains(token)) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Output", "-"));
            } else if (specialSymbols.contains(token)) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Special Symbol", "-"));
            } else if (constants.contains(token) || token.matches("\\d+")) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Integer Constant", "-"));
            } else if (token.matches("\\d+\\.\\d+")) {
                symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Decimal Constant", "-"));
            } else if (token.matches("^[a-z]+$")) {
                if (checkForMain) {
                    symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Identifier", "Global"));
                } else {
                    symbol_table.put(String.valueOf(entries), Arrays.asList(token, "Identifier", "Local"));
                }
            }
            entries++;
        }
    }

    // Prints the token table.
    public void print_tokens() {
        for (Map.Entry<String, List<String>> entry : symbol_table.entrySet()) {
            System.out.println(entry.getKey() + " : " + String.join(", ", entry.getValue()));
        }
    }

    public static HashMap<String, List<String>> getSymbolTable() {
        return symbol_table;
    }

    // Validate tokens using a DFA built from a master token regular expression.
    public void validateTokens(TokenValidator validator) {
        System.out.println("\nToken Validation Results:");
        for (Map.Entry<String, List<String>> entry : symbol_table.entrySet()) {
            String token = entry.getValue().get(0);
            if (validator.validate(token)) {
                System.out.println("Token '" + token + "' is valid.");
            } else {
                System.out.println("ERROR: Token '" + token + "' is invalid!");
            }
        }
    }

    public static void main(String[] args) {
        // -----------------------------
        // Part 1: Lexical Analysis Testing
        // -----------------------------
        String fileName = "program.ba";

        if (!fileName.endsWith(".ba")) {
            System.out.println("Error: Only .ba files are allowed.");
            return;
        }

        a1 lexer = new a1();
        lexer.read_file(fileName); // Ensure a test file "program.txt" exists
        System.out.println("Lexer Tokens:");
        lexer.print_tokens();

        // -----------------------------
        // Part 2: RE -> NFA -> DFA Conversion and Testing (Example DFA for (a|b)*abb)
        // -----------------------------

        String masterTokenRegex = "(" +
        // Identifiers
                "((a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z)" +
                "((a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z))*)" + "|" +
                // Integers (whole numbers)
                "((0|1|2|3|4|5|6|7|8|9)((0|1|2|3|4|5|6|7|8|9))*)" + "|" +
                // Floating-point numbers: digits before and after a decimal point
                "(\\d+\\.\\d+)" + "|" +
                // Operators: +, -, *, /, %, ^
                "(\\+|\\-|\\*|\\/|%|\\^|=)" + "|" +
                // Parentheses: ( and )
                "(\\(|\\))" + "|" +
                // Braces: { }
                "(\\{|\\})" + "|" +
                // Semicolon: ;
                "(;)" +
                ")";

        String regex = "(a|b)*abb"; // Sample regular expression for demonstration.
        System.out.println("\nConverting Regular Expression to NFA for regex: " + regex);
        RegexToNFA converter = new RegexToNFA(regex);
        NFA nfa = converter.build();
        System.out.println("NFA constructed. (Start state id: " + nfa.start.id + ")");

        System.out.println("\nConverting NFA to DFA...");
        NFAToDFAConverter nfaToDFA = new NFAToDFAConverter(nfa);
        DFA dfa = nfaToDFA.convert();
        System.out.println("DFA constructed with " + dfa.states.size() + " states.");
        dfa.printTransitionTable();

        // -----------------------------
        // Part 3: Token Validation Using a Master RE DFA
        // -----------------------------
        // Build a master regular expression that recognizes:
        // 1. Identifiers: one or more lowercase letters.
        // 2. Integers: one or more digits.
        // 3. Operators: +, -, /, %, ^
        // Since our RegexToNFA supports only literals, grouping, alternation,
        // concatenation and Kleene star,
        // we must "expand" the character classes.

        // Because our simple parser does not handle escape sequences, we remove them:
        masterTokenRegex = masterTokenRegex.replace("\\", "");

        System.out.println("\nBuilding Token Validation DFA with master regex: " + masterTokenRegex);
        TokenValidator validator = new TokenValidator(masterTokenRegex);
        lexer.validateTokens(validator);

        errorChecker errorHandler = new errorChecker(lexer);
        errorHandler.readFile(fileName);
        errorHandler.printErrors();
    }
}

/*
 * ===============================
 * RE to NFA and NFA to DFA Classes
 * ===============================
 */

// A state in the NFA.
class State {
    int id;
    boolean isFinal;
    Map<String, List<State>> transitions = new HashMap<>();

    public State(int id) {
        this.id = id;
        this.isFinal = false;
    }

    public void addTransition(String symbol, State state) {
        transitions.computeIfAbsent(symbol, k -> new ArrayList<>()).add(state);
    }
}

// An NFA with a designated start and end state.
class NFA {
    State start;
    State end;

    public NFA(State start, State end) {
        this.start = start;
        this.end = end;
    }
}

// Converts a regular expression to an NFA using Thompson's construction.
// Supports literals, alternation (|), concatenation (implicit), Kleene star (*)
// and grouping with parentheses.
class RegexToNFA {
    private String regex;
    private int pos;
    private static int stateCount = 0;

    public RegexToNFA(String regex) {
        this.regex = regex;
        this.pos = 0;
    }

    // Builds the NFA for the complete regular expression.
    public NFA build() {
        NFA nfa = expression();
        nfa.end.isFinal = true; // Mark the end state as final.
        return nfa;
    }

    // expression -> term ('|' term)*
    private NFA expression() {
        NFA termNFA = term();
        while (more() && peek() == '|') {
            consume(); // consume '|'
            NFA nextTerm = term();
            termNFA = union(termNFA, nextTerm);
        }
        return termNFA;
    }

    // term -> factor+
    private NFA term() {
        NFA factorNFA = factor();
        while (more() && peek() != ')' && peek() != '|') {
            NFA nextFactor = factor();
            factorNFA = concatenate(factorNFA, nextFactor);
        }
        return factorNFA;
    }

    // factor -> base ('*')?
    private NFA factor() {
        NFA baseNFA = base();
        while (more() && peek() == '*') {
            consume(); // consume '*'
            baseNFA = kleeneStar(baseNFA);
        }
        return baseNFA;
    }

    // base -> character or '(' expression ')'
    private NFA base() {
        if (peek() == '(') {
            consume(); // consume '('
            NFA nfa = expression();
            if (more() && peek() == ')') {
                consume(); // consume ')'
            }
            return nfa;
        } else {
            char c = consume();
            return symbol(String.valueOf(c));
        }
    }

    private boolean more() {
        return pos < regex.length();
    }

    private char peek() {
        return regex.charAt(pos);
    }

    private char consume() {
        return regex.charAt(pos++);
    }

    // Create an NFA for a single literal symbol.
    private NFA symbol(String s) {
        State start = new State(stateCount++);
        State end = new State(stateCount++);
        start.addTransition(s, end);
        return new NFA(start, end);
    }

    // Concatenates two NFAs.
    private NFA concatenate(NFA first, NFA second) {
        first.end.addTransition("ε", second.start);
        return new NFA(first.start, second.end);
    }

    // Constructs the union (|) of two NFAs.
    private NFA union(NFA first, NFA second) {
        State start = new State(stateCount++);
        State end = new State(stateCount++);
        start.addTransition("ε", first.start);
        start.addTransition("ε", second.start);
        first.end.addTransition("ε", end);
        second.end.addTransition("ε", end);
        return new NFA(start, end);
    }

    // Applies the Kleene star (*) to an NFA.
    private NFA kleeneStar(NFA nfa) {
        State start = new State(stateCount++);
        State end = new State(stateCount++);
        start.addTransition("ε", nfa.start);
        start.addTransition("ε", end);
        nfa.end.addTransition("ε", nfa.start);
        nfa.end.addTransition("ε", end);
        return new NFA(start, end);
    }
}

// Represents a state in the DFA. Each DFA state corresponds to a set of NFA
// states.
class DFAState {
    int id;
    Set<State> nfaStates;
    boolean isFinal;
    Map<String, DFAState> transitions = new HashMap<>();

    public DFAState(int id, Set<State> nfaStates, boolean isFinal) {
        this.id = id;
        this.nfaStates = nfaStates;
        this.isFinal = isFinal;
    }
}

// The DFA structure containing its states and the start state.
class DFA {
    List<DFAState> states;
    DFAState startState;

    public DFA(DFAState startState, List<DFAState> states) {
        this.startState = startState;
        this.states = states;
    }

    // Prints the DFA transition table.
    public void printTransitionTable() {
        System.out.println("\nDFA Transition Table:");
        for (DFAState dfaState : states) {
            System.out.print("State " + dfaState.id + (dfaState.isFinal ? " (Final)" : "") + ": ");
            for (Map.Entry<String, DFAState> entry : dfaState.transitions.entrySet()) {
                System.out.print("[" + entry.getKey() + " -> " + entry.getValue().id + "] ");
            }
            System.out.println();
        }
    }

    // Simulates the DFA on an input string.
    public boolean matches(String input) {
        DFAState current = startState;
        for (int i = 0; i < input.length(); i++) {
            String symbol = String.valueOf(input.charAt(i));
            if (current.transitions.containsKey(symbol)) {
                current = current.transitions.get(symbol);
            } else {
                return false;
            }
        }
        return current.isFinal;
    }
}

// Converts an NFA to a DFA using the subset construction algorithm.
class NFAToDFAConverter {
    NFA nfa;

    public NFAToDFAConverter(NFA nfa) {
        this.nfa = nfa;
    }

    public DFA convert() {
        Map<Set<State>, DFAState> dfaStatesMap = new HashMap<>();
        List<DFAState> dfaStatesList = new ArrayList<>();
        Queue<Set<State>> queue = new LinkedList<>();
        int dfaIdCounter = 0;

        // Start with the epsilon-closure of the NFA's start state.
        Set<State> startSet = epsilonClosure(Collections.singleton(nfa.start));
        boolean isFinal = containsFinal(startSet, nfa.end);
        DFAState startDFAState = new DFAState(dfaIdCounter++, startSet, isFinal);
        dfaStatesMap.put(startSet, startDFAState);
        dfaStatesList.add(startDFAState);
        queue.add(startSet);

        while (!queue.isEmpty()) {
            Set<State> currentSet = queue.poll();
            DFAState currentDFA = dfaStatesMap.get(currentSet);

            // Determine all possible symbols (other than ε) from the current set.
            Set<String> symbols = new HashSet<>();
            for (State s : currentSet) {
                for (String symbol : s.transitions.keySet()) {
                    if (!symbol.equals("ε")) {
                        symbols.add(symbol);
                    }
                }
            }

            // For each symbol, compute the move and then the ε-closure.
            for (String symbol : symbols) {
                Set<State> moveSet = move(currentSet, symbol);
                Set<State> closureSet = epsilonClosure(moveSet);
                if (closureSet.isEmpty())
                    continue;
                DFAState dfaState = dfaStatesMap.get(closureSet);
                if (dfaState == null) {
                    boolean isFinalState = containsFinal(closureSet, nfa.end);
                    dfaState = new DFAState(dfaIdCounter++, closureSet, isFinalState);
                    dfaStatesMap.put(closureSet, dfaState);
                    dfaStatesList.add(dfaState);
                    queue.add(closureSet);
                }
                currentDFA.transitions.put(symbol, dfaState);
            }
        }
        return new DFA(startDFAState, dfaStatesList);
    }

    // Computes the ε-closure of a set of states.
    private Set<State> epsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>(states);
        Stack<State> stack = new Stack<>();
        stack.addAll(states);
        while (!stack.isEmpty()) {
            State state = stack.pop();
            List<State> epsilonTransitions = state.transitions.get("ε");
            if (epsilonTransitions != null) {
                for (State s : epsilonTransitions) {
                    if (!closure.contains(s)) {
                        closure.add(s);
                        stack.push(s);
                    }
                }
            }
        }
        return closure;
    }

    // Returns the set of states reachable from 'states' on input 'symbol'.
    private Set<State> move(Set<State> states, String symbol) {
        Set<State> result = new HashSet<>();
        for (State state : states) {
            List<State> transitions = state.transitions.get(symbol);
            if (transitions != null) {
                result.addAll(transitions);
            }
        }
        return result;
    }

    // Checks if the set of states contains the NFA's designated final state.
    private boolean containsFinal(Set<State> states, State finalState) {
        return states.contains(finalState);
    }
}

// TokenValidator builds a DFA from a master token regular expression and uses
// it to validate tokens.
class TokenValidator {
    DFA dfa;

    public TokenValidator(String masterRegex) {
        RegexToNFA converter = new RegexToNFA(masterRegex);
        NFA nfa = converter.build();
        NFAToDFAConverter nfaToDFA = new NFAToDFAConverter(nfa);
        this.dfa = nfaToDFA.convert();
    }

    public boolean validate(String token) {
        return dfa.matches(token);
    }
}