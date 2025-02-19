import java.util.*;

public class regularExpressions {
    private static final Map<String, String> categoryToRegex = new HashMap<>();
    private static final List<String> tokenToRegex = new ArrayList<>();
    private static final Map<String, shortThomp.NFA> tokenToNFA = new HashMap<>();

    static {
        categoryToRegex.put("Keyword", "\\b(if|else|return|for|while|new)\\b");
        categoryToRegex.put("Datatype", "\\b(int|float|double|char|bool)\\b");
        categoryToRegex.put("Arithmetic Operator", "[-+*/%]");
        categoryToRegex.put("Input",
                "\\b(Scanner|System.in|nextInt\\(\\)|nextLine\\(\\)|BufferedReader|readLine\\(\\))\\b");
        categoryToRegex.put("Output", "\\b(System.out.print|System.out.println|System.out.printf)\\b");
        categoryToRegex.put("Special Symbol", "[=;(){}\\[\\]]");
        categoryToRegex.put("Integer Constant", "\\b\\d+\\b");
        categoryToRegex.put("Decimal Constant", "\\b\\d+\\.\\d+\\b");
        categoryToRegex.put("Identifier", "\\b[a-zA-Z_][a-zA-Z0-9_]*\\b");
    }

    public static void mapTokensToRegex(Map<String, List<String>> symbolTable) {
        for (Map.Entry<String, List<String>> entry : symbolTable.entrySet()) {
            List<String> tokenInfo = entry.getValue();
            String token = tokenInfo.get(0);
            String category = tokenInfo.get(1);

            String regexDisplay = token;

            if (categoryToRegex.containsKey(category)) {
                String fullRegex = categoryToRegex.get(category);

                if (category.equals("Keyword") || category.equals("Datatype")) {
                    regexDisplay = token;
                } else if (category.equals("Identifier")) {
                    regexDisplay = categoryToRegex.get("Identifier").replace("\\b", "");
                } else if (category.equals("Special Symbol") || category.equals("Arithmetic Operator")) {
                    regexDisplay = token;
                } else if (category.equals("Integer Constant")) {
                    regexDisplay = "\\d+";
                } else if (category.equals("Decimal Constant")) {
                    regexDisplay = "\\d+\\.\\d+";
                } else {
                    regexDisplay = fullRegex.replace("\\b", "");
                }

                // // Generate NFA using shortThomp
                // shortThomp.NFA nfa = shortThomp.compile(regexDisplay);
                // tokenToNFA.put(token, nfa);
            }

            tokenToRegex.add(String.format("Token: %-10s -> Regex: %s", token, regexDisplay));
        }
    }

    public static void printRegexMappings() {
        for (String mapping : tokenToRegex) {
            System.out.println(mapping);
        }
    }

    // public static void printNFAs() {
    // for (Map.Entry<String, shortThomp.NFA> entry : tokenToNFA.entrySet()) {
    // System.out.println("NFA for token: " + entry.getKey());
    // entry.getValue().display();
    // }
    // }
}