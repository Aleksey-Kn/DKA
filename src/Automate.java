import java.util.*;
import java.util.stream.Collectors;

public class Automate {
    static class Rules{
        private final Optional<LinkedList<Character>> newMagazineSymbol;
        private final String state;

        Rules(String s){
            String[] strings = Arrays.stream(s.split(",")).map(String::trim).toArray(String[]::new);
            state = strings[0];
            if(strings[1].charAt(0) == 'e')
                newMagazineSymbol = Optional.empty();
            else {
                if(strings[1].length() == 1)
                    newMagazineSymbol = Optional.of(new LinkedList<>(List.of(strings[1].charAt(0))));
                else
                    newMagazineSymbol = Optional.of(new LinkedList<>(List.of(strings[1].charAt(1), strings[1].charAt(0))));
            }
        }
    }

    static class Moment{
        private final String state;
        private final char terminal;
        private final char magazine;

        Moment(String s){
            String[] strings = s.split(",");
            state = strings[0].trim();
            terminal = strings[1].trim().charAt(0);
            magazine = strings[2].trim().charAt(0);
        }

        Moment(String state, char terminal, char magazine){
            this.terminal = terminal;
            this.magazine = magazine;
            this.state = state;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Moment moment = (Moment) o;
            return terminal == moment.terminal && magazine == moment.magazine && state.equals(moment.state);
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, terminal, magazine);
        }
    }

    private final Set<Character> terminal;
    private final Map<Moment, Rules> regulations = new HashMap<>();
    private final String startState;
    private final Set<String> endState;
    private final char zero;

    public Automate(Set<Character> terminal, Set<String> state, Set<Character> magazineAlphabet, Set<String> stringRules,
                    String startState, char zeroMagazine, Set<String> endState){
        this.terminal = terminal.stream().map(Character::toLowerCase).collect(Collectors.toSet());
        this.startState = startState;
        this.endState = endState;
        zero = zeroMagazine;

        String[] t;
        Moment key;
        Rules value;
        for(String r: stringRules){
            t = r.split("->");
            key = new Moment(t[0]);
            value = new Rules(t[1]);
            if(!state.contains(key.state))
                throw new IllegalArgumentException("State not exist: " + key.state);
            if(!state.contains(value.state))
                throw new IllegalArgumentException("State not exist: " + value.state);
            if(!terminal.contains(key.terminal) && key.terminal != 'e')
                throw new IllegalArgumentException("Terminal not exist: " + key.terminal);
            if(!magazineAlphabet.contains(key.magazine))
                throw new IllegalArgumentException("Magazine symbol not exist: " + key.magazine);
            if(value.newMagazineSymbol.isPresent() && !magazineAlphabet.containsAll(value.newMagazineSymbol.get()))
                throw new IllegalArgumentException("Magazine symbol not exist: " + value.newMagazineSymbol.get());
            regulations.put(key, value);
        }
    }

    public boolean canCreate(String lang, StringBuilder cause, LinkedHashSet<String> logs){
        char[] language = lang.toCharArray();
        cause.delete(0, cause.length());
        logs.clear();
        for(char c: language){
            if(!terminal.contains(c)){
                cause.append("Language exist unacceptable symbol");
                return false;
            }
        }
        String nowState = startState;
        Stack<Character> stack = new Stack<>();
        stack.add(zero);
        Moment moment;
        Rules rules;
        String tempForLog;
        for(int i = 0; i < language.length; i++){
            tempForLog = Arrays.toString(stack.toArray());
            logs.add(nowState + ": " + lang.substring(i) + " / " +
                    new StringBuilder(tempForLog.substring(1, tempForLog.length() - 1)).reverse());
            moment = new Moment(nowState, language[i], stack.pop());
            if(regulations.containsKey(moment)){
                rules = regulations.get(moment);
                nowState = rules.state;
                rules.newMagazineSymbol.ifPresent(stack::addAll);
            } else {
                cause.append("The final state is unattainable");
                return false;
            }
        }
        while (!stack.isEmpty()) {
            tempForLog = Arrays.toString(stack.toArray());
            logs.add(nowState + ": e / " + new StringBuilder(tempForLog.substring(1, tempForLog.length() - 1)).reverse());
            moment = new Moment(nowState, 'e', stack.pop());
            if (regulations.containsKey(moment)) {
                rules = regulations.get(moment);
                nowState = rules.state;
                rules.newMagazineSymbol.ifPresent(stack::addAll);
            } else break;
        }
        logs.add(nowState + ": e / " + Arrays.toString(stack.toArray()));
        if(endState.contains(nowState)) {
            if(stack.empty()) {
                return true;
            } else {
                cause.append("Automate memory is not empty");
                return false;
            }
        } else {
            cause.append("End of the chain, but the end state has not been reached");
            return false;
        }
    }
}
