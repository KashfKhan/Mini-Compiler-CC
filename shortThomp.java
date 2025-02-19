import java.util.*;

public class shortThomp {
    public static class Trans {
        public int state_from, state_to;
        public char trans_symbol;

        public Trans(int v1, int v2, char sym) {
            this.state_from = v1;
            this.state_to = v2;
            this.trans_symbol = sym;
        }
    }

    public static class NFA {
        public ArrayList<Integer> states;
        public ArrayList<Trans> transitions;
        public int final_state;

        public NFA() {
            this.states = new ArrayList<>();
            this.transitions = new ArrayList<>();
            this.final_state = 0;
        }

        public NFA(int size) {
            this();
            this.setStateSize(size);
        }

        public NFA(String token) { // Supports multi-character tokens
            this();
            this.setStateSize(token.length() + 1);
            this.final_state = token.length();
            for (int i = 0; i < token.length(); i++) {
                this.transitions.add(new Trans(i, i + 1, token.charAt(i)));
            }
        }

        public void setStateSize(int size) {
            for (int i = 0; i < size; i++)
                this.states.add(i);
        }

        public void display() {
            for (Trans t : transitions) {
                System.out.println("(" + t.state_from + ", " + t.trans_symbol + ", " + t.state_to + ")");
            }
        }

        public void combineNFAs(List<NFA> nfas) {
            if (nfas.isEmpty())
                return;

            int offset = 1; // New initial state
            this.states.clear();
            this.transitions.clear();

            // Set up new initial state
            this.states.add(0);

            // Combine all NFAs
            for (NFA nfa : nfas) {
                for (int state : nfa.states) {
                    this.states.add(state + offset);
                }
                for (Trans t : nfa.transitions) {
                    this.transitions.add(new Trans(t.state_from + offset, t.state_to + offset, t.trans_symbol));
                }
                this.transitions.add(new Trans(0, offset, 'E')); // ε-transition from new start to each NFA's start
                offset += nfa.states.size();
            }

            this.final_state = offset - 1; // Set final state to the last NFA's final state
        }

        public List<Trans> getTransitions() {
            return this.transitions;
        }
    }

    public static boolean isAlphaNumeric(char c) {
        return Character.isLetterOrDigit(c);
    }

    public static boolean validRegEx(String regex) {
        return !regex.isEmpty();
    }

    public static NFA concat(NFA nfa1, NFA nfa2) {
        NFA result = new NFA(nfa1.states.size() + nfa2.states.size());
        result.transitions.addAll(nfa1.transitions);
        result.transitions.add(new Trans(nfa1.final_state, nfa1.states.size(), 'E'));
        for (Trans t : nfa2.transitions) {
            result.transitions
                    .add(new Trans(t.state_from + nfa1.states.size(), t.state_to + nfa1.states.size(), t.trans_symbol));
        }
        result.final_state = nfa1.states.size() + nfa2.final_state;
        return result;
    }

    public static NFA union(NFA nfa1, NFA nfa2) {
        NFA result = new NFA(nfa1.states.size() + nfa2.states.size() + 2);
        result.transitions.add(new Trans(0, 1, 'E'));
        for (Trans t : nfa1.transitions) {
            result.transitions.add(new Trans(t.state_from + 1, t.state_to + 1, t.trans_symbol));
        }
        result.transitions.add(new Trans(nfa1.final_state + 1, result.states.size() - 1, 'E'));
        result.transitions.add(new Trans(0, nfa1.states.size() + 1, 'E'));
        for (Trans t : nfa2.transitions) {
            result.transitions.add(new Trans(t.state_from + nfa1.states.size() + 1, t.state_to + nfa1.states.size() + 1,
                    t.trans_symbol));
        }
        result.transitions.add(new Trans(nfa2.final_state + nfa1.states.size() + 1, result.states.size() - 1, 'E'));
        result.final_state = result.states.size() - 1;
        return result;
    }

    public static NFA compile(String regex) {
        if (!validRegEx(regex)) {
            System.out.println("Invalid Regular Expression Input.");
            return new NFA();
        }

        Stack<NFA> operands = new Stack<>();
        boolean concatFlag = false;

        Set<String> keywords = new HashSet<>(Arrays.asList("int", "bool", "char", "return", "for"));
        Set<Character> specialChars = new HashSet<>(Arrays.asList('=', '/', '%', '+', '-', '*', '[', ']', '{', '}'));

        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);

            // Handle multi-character keywords
            if (Character.isLetter(c)) {
                StringBuilder keyword = new StringBuilder();
                while (i < regex.length() && Character.isLetter(regex.charAt(i))) {
                    keyword.append(regex.charAt(i));
                    i++;
                }
                i--; // Adjust index after loop
                String token = keyword.toString();
                if (keywords.contains(token)) {
                    operands.push(new NFA(token)); // Full word as token
                } else {
                    for (char ch : token.toCharArray()) {
                        operands.push(new NFA(ch + ""));
                        if (concatFlag)
                            operands.push(concat(operands.pop(), operands.pop()));
                        else
                            concatFlag = true;
                    }
                }
            }
            // Handle special characters
            else if (specialChars.contains(c)) {
                operands.push(new NFA(c + ""));
                if (concatFlag)
                    operands.push(concat(operands.pop(), operands.pop()));
                else
                    concatFlag = true;
            }
        }

        return operands.pop();
    }

    public static void main(String[] args) {
        System.out.println("NFA for '=':");
        NFA nfa1 = compile("=");
        nfa1.display();

        System.out.println("\nNFA for 'int':");
        NFA nfa2 = compile("int");
        nfa2.display();

        System.out.println("\nNFA for 'bool':");
        NFA nfa3 = compile("bool");
        nfa3.display();

        System.out.println("\nNFA for '+'");
        NFA nfa4 = compile("[");
        nfa4.display();
    }
}