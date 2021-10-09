import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.stream.Collectors;

public class Frame extends JFrame {
    Frame(){
        super("Automate");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(200, 100, 700, 500);

        final LinkedList<String> logicRulesSet = new LinkedList<>();

        JPanel settings = new JPanel();
        settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));
        add(settings, BorderLayout.WEST);
        JPanel regulations = new JPanel();
        regulations.setLayout(new BoxLayout(regulations, BoxLayout.Y_AXIS));
        add(regulations, BorderLayout.EAST);
        JPanel answer = new JPanel();
        answer.setLayout(new BoxLayout(answer, BoxLayout.Y_AXIS));
        add(answer, BorderLayout.SOUTH);
        JPanel buttonPanel = new JPanel();

        JLabel terminalLabel = new JLabel("Terminal symbol: ");
        settings.add(terminalLabel);
        JTextField terminals = new JTextField();
        settings.add(terminals);
        JLabel stateLabel = new JLabel("Set of state:");
        settings.add(stateLabel);
        JTextField states = new JTextField();
        settings.add(states);
        JLabel startStateLabel = new JLabel("Start state: ");
        settings.add(startStateLabel);
        JTextField startState = new JTextField();
        settings.add(startState);
        JLabel endStateLabel = new JLabel("End states: ");
        settings.add(endStateLabel);
        JTextField endStates = new JTextField();
        settings.add(endStates);

        JLabel rulesLabel = new JLabel("Next rules: ");
        regulations.add(rulesLabel);
        JTextField rules = new JTextField();
        regulations.add(rules);
        JButton addRules = new JButton("Add rules");
        buttonPanel.add(addRules);
        JButton deleteRules = new JButton("Delete all rules");
        buttonPanel.add(deleteRules);
        JButton removeLastRules = new JButton("Delete last");
        buttonPanel.add(removeLastRules);
        regulations.add(buttonPanel);
        JPanel setOfRules = new JPanel();
        setOfRules.setLayout(new BoxLayout(setOfRules, BoxLayout.Y_AXIS));
        regulations.add(setOfRules);
        ActionListener al = l -> {
            setOfRules.add(new JLabel(rules.getText()));
            logicRulesSet.add(rules.getText());
            rules.setText("");
            setOfRules.updateUI();
        };
        removeLastRules.addActionListener(l -> {
            setOfRules.remove(setOfRules.getComponentCount() - 1);
            logicRulesSet.removeLast();
            setOfRules.updateUI();
        });
        rules.addActionListener(al);
        addRules.addActionListener(al);
        deleteRules.addActionListener(l -> {
            setOfRules.removeAll();
            logicRulesSet.clear();
            setOfRules.updateUI();
        });

        JLabel languageLabel = new JLabel("Write language:");
        answer.add(languageLabel);
        JTextField language = new JTextField();
        answer.add(language);
        JButton run = new JButton("Run");
        run.setAlignmentX(Component.CENTER_ALIGNMENT);
        answer.add(run);
        JLabel result = new JLabel();
        result.setText(" ");
        result.setAlignmentX(Component.CENTER_ALIGNMENT);
        answer.add(result);
        StringBuilder stringBuilder = new StringBuilder();
        run.addActionListener(l -> {
            try {
                Automate automate = new Automate(
                        new TreeSet<>(Arrays.stream(terminals.getText().split(" "))
                                .filter(s -> !s.equals(""))
                                .map(s -> s.charAt(0))
                                .collect(Collectors.toSet())),
                        new TreeSet<>(java.util.List.of(states.getText().split(" "))),
                        new TreeSet<>(logicRulesSet),
                        startState.getText().trim(),
                        new TreeSet<>(java.util.List.of(endStates.getText().split(" "))));
                boolean success = automate.canCreate(language.getText().toCharArray(), stringBuilder);
                result.setForeground(success?
                        Color.GREEN: Color.ORANGE);
                result.setText(success? "Can create": stringBuilder.toString());
            } catch (Exception e){
                result.setForeground(Color.RED);
                result.setText(e.getMessage());
                e.printStackTrace();
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new Frame();
    }
}
