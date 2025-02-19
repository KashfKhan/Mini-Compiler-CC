import java.util.*;

public class NFA_to_DFA {
    public static class DFA {
        public Set<Set<Integer>> states;
        public Map<Set<Integer>, Map<Character, Set<Integer>>> transitions;
        public Set<Integer> startState;
        public Set<Set<Integer>> finalStates;

        public DFA() {
            states = new HashSet<>();
            transitions = new HashMap<>();
            finalStates = new HashSet<>();
        }

        public void display() {
            for (Map.Entry<Set<Integer>, Map<Character, Set<Integer>>> entry : transitions.entrySet()) {
                for (Map.Entry<Character, Set<Integer>> trans : entry.getValue().entrySet()) {
                    System.out.println(entry.getKey() + " --" + trans.getKey() + "--> " + trans.getValue());
                }
            }
        }
    }

    public static Set<Integer> epsilonClosure(shortThomp nfa, Set<Integer> states) {
        Stack<Integer> stack = new Stack<>();
        Set<Integer> closure = new HashSet<>(states);
        stack.addAll(states);

        while (!stack.isEmpty()) {
            int state = stack.pop();
            for (shortThomp.Trans t : shortThomp.nfa.transitions) {
                if (t.state_from == state && t.trans_symbol == 'E' && !closure.contains(t.state_to)) {
                    closure.add(t.state_to);
                    stack.push(t.state_to);
                }
            }
        }
        return closure;
    }

    public static DFA convertNFAtoDFA(shortThomp nfa) {
        DFA dfa = new DFA();
        Map<Set<Integer>, Map<Character, Set<Integer>>> dfaTransitions = new HashMap<>();
        Queue<Set<Integer>> queue = new LinkedList<>();

        Set<Integer> startClosure = epsilonClosure(nfa, new HashSet<>(Collections.singleton(0)));
        dfa.startState = startClosure;
        queue.add(startClosure);
        dfa.states.add(startClosure);

        while (!queue.isEmpty()) {
            Set<Integer> currentDFAState = queue.poll();
            dfaTransitions.putIfAbsent(currentDFAState, new HashMap<>());

            for (char symbol : getAlphabet(nfa)) {
                Set<Integer> newState = new HashSet<>();
                for (int state : currentDFAState) {
                    for (shortThomp.Trans t : nfa.transitions) {
                        if (t.state_from == state && t.trans_symbol == symbol) {
                            newState.add(t.state_to);
                        }
                    }
                }
                newState = epsilonClosure(nfa, newState);

                if (!dfa.states.contains(newState)) {
                    dfa.states.add(newState);
                    queue.add(newState);
                }
                dfaTransitions.get(currentDFAState).put(symbol, newState);
            }
        }
        dfa.transitions = dfaTransitions;
        return dfa;
    }

    private static Set<Character> getAlphabet(shortThomp nfa) {
        Set<Character> alphabet = new HashSet<>();
        for (shortThomp.Trans t : nfa.getTransitions()) {
            if (t.trans_symbol != 'E') {
                alphabet.add(t.trans_symbol);
            }
        }
        return alphabet;
    }
}