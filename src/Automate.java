import java.util.*;
import java.util.stream.Collectors;

public class Automate {
    static class Rules{
        private final char terminal;
        private final String state;

        Rules(String s){
            s = s.trim();
            terminal = s.charAt(0);
            state = s.substring(1);
        }
    }

    private final Set<Character> terminal;
    private final Map<String, Set<Rules>> regulations = new HashMap<>();
    private final String startState;
    private final Set<String> endState;

    public Automate(Set<Character> terminal, Set<String> state, Set<String> stringRules, String startState,
                    Set<String> endState){
        this.terminal = terminal.stream().map(Character::toLowerCase).collect(Collectors.toSet());
        this.startState = startState;
        this.endState = endState;

        String[] t;
        String key;
        Rules value;
        for(String r: stringRules){
            t = r.split("->");
            key = t[0].trim();
            value = new Rules(t[1]);
            regulations.putIfAbsent(key, new HashSet<>());
            regulations.get(key).add(value);
            if(!state.contains(key))
                throw new IllegalArgumentException("Use not exist non terminal symbol: " + key);
            if(!this.terminal.contains(value.terminal) || !state.contains(value.state)) {
                throw new IllegalArgumentException("Incorrect rules: " + r);
            }
        }
    }

    public boolean canCreate(char[] language, StringBuilder cause){
        cause.delete(0, cause.length());
        for(char c: language){
            if(!terminal.contains(c)){
                cause.append("Language exist unacceptable symbol");
                return false;
            }
        }
        String nowState = startState;
        boolean wasSearch;
        for(char c: language){
            if(nowState.equals("")){
                cause.append("The final state is unattainable");
                return false;
            }
            wasSearch = false;
            for(Rules nowVal: regulations.get(nowState)){
                if(c == nowVal.terminal){
                    nowState = nowVal.state;
                    wasSearch = true;
                    break;
                }
            }
            if(!wasSearch){
                cause.append("The final state is unattainable");
                return false;
            }
        }
        if(endState.contains(nowState))
            return true;
        else {
            cause.append("End of the chain, but the end state has not been reached");
            return false;
        }
    }

    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        Automate automate = new Automate(Set.of('0', '1'), Set.of("q0", "q1", "q2"),
                Set.of("q0 -> 1q0", "q0 -> 0q1", "q1 -> 1q2"),
                "q0", Set.of("q2"));
        System.out.println(automate.canCreate("11101".toCharArray(), stringBuilder)?
                "Language can create": stringBuilder.toString());
    }
}
