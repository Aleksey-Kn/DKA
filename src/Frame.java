import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
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
        JPanel workWithFile = new JPanel();
        add(workWithFile, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(300, 75));

        JLabel terminalLabel = new JLabel("Terminal symbol: ");
        settings.add(terminalLabel);
        JTextField terminals = new JTextField();
        settings.add(terminals);
        JLabel stateLabel = new JLabel("Set of state:");
        settings.add(stateLabel);
        JTextField states = new JTextField();
        settings.add(states);
        settings.add(new JLabel("Memory alphabet"));
        JTextField memoryAlphabet = new JTextField();
        settings.add(memoryAlphabet);
        JLabel startStateLabel = new JLabel("Start state: ");
        settings.add(startStateLabel);
        JTextField startState = new JTextField();
        settings.add(startState);
        settings.add(new JLabel("Zero symbol"));
        JTextField zeroSymbol = new JTextField();
        settings.add(zeroSymbol);
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
        JTextField indexField = new JTextField(5);
        buttonPanel.add(indexField);
        JButton removeIndex = new JButton("Remove index");
        buttonPanel.add(removeIndex);
        regulations.add(buttonPanel);
        JPanel setOfRules = new JPanel();
        setOfRules.setLayout(new BoxLayout(setOfRules, BoxLayout.Y_AXIS));
        regulations.add(setOfRules);
        ActionListener al = l -> {
            String s = rules.getText();
            if(!s.equals("")) {
                setOfRules.add(new JLabel(s));
                logicRulesSet.add(s);
                rules.setText("");
                setOfRules.updateUI();
            }
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
        removeIndex.addActionListener(l -> {
            setOfRules.remove(Integer.parseInt(indexField.getText()));
            logicRulesSet.remove(Integer.parseInt(indexField.getText()));
            indexField.setText("");
            setOfRules.updateUI();
        });

        JButton readFile = new JButton("Open file");
        workWithFile.add(readFile);
        readFile.addActionListener(l -> {
            String address = JOptionPane.showInputDialog("Enter file name");
            assert address != null;
            if(address.contains(".")){
                if(!address.split("\\.")[1].equals("dmpa")){
                    JOptionPane.showMessageDialog(null, "Extension of file must be \"dmpa\"");
                }
            } else {
                address += ".dmpa";
            }
            try {
                Scanner scanner = new Scanner(new File(address));
                rules.setText("");
                terminals.setText(scanner.nextLine());
                states.setText(scanner.nextLine());
                memoryAlphabet.setText(scanner.nextLine());
                startState.setText(scanner.nextLine());
                zeroSymbol.setText(scanner.nextLine());
                endStates.setText(scanner.nextLine());
                String temp;
                setOfRules.removeAll();
                logicRulesSet.clear();
                while (scanner.hasNextLine()){
                    temp = scanner.nextLine();
                    setOfRules.add(new JLabel(temp));
                    logicRulesSet.add(temp);
                }
                scanner.close();
                setOfRules.updateUI();
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Said fail not found");
            }
        });
        JButton saveFile = new JButton("Save to file");
        workWithFile.add(saveFile);
        saveFile.addActionListener(l -> {
            String address = JOptionPane.showInputDialog("Enter file name").split("\\.")[0].concat(".dmpa");
            try {
                FileWriter fileWriter = new FileWriter(address);
                fileWriter.write(terminals.getText() + "\n");
                fileWriter.write(states.getText() + "\n");
                fileWriter.write(memoryAlphabet.getText() + "\n");
                fileWriter.write(startState.getText() + "\n");
                fileWriter.write(zeroSymbol.getText() + "\n");
                fileWriter.write(endStates.getText() + "\n");
                for(String s: logicRulesSet)
                    fileWriter.write(s + "\n");
                fileWriter.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        });

        JLabel languageLabel = new JLabel("Write grammatical:");
        answer.add(languageLabel);
        JTextField language = new JTextField();
        answer.add(language);
        JButton run = new JButton("Run");
        run.setAlignmentX(Component.CENTER_ALIGNMENT);
        answer.add(run);
        JPanel log = new JPanel();
        log.setAlignmentX(Component.CENTER_ALIGNMENT);
        log.setLayout(new BoxLayout(log, BoxLayout.Y_AXIS));
        add(log, BorderLayout.CENTER);
        JLabel result = new JLabel();
        result.setFont(new Font("Calibri", Font.BOLD, 24));
        result.setText(" ");
        result.setAlignmentX(Component.CENTER_ALIGNMENT);
        answer.add(result);
        StringBuilder cause = new StringBuilder();
        LinkedHashSet<String> logString = new LinkedHashSet<>();
        run.addActionListener(l -> {
            try {
                Automate automate = new Automate(
                        Arrays.stream(terminals.getText().split(" "))
                                .filter(s -> !s.equals(""))
                                .map(s -> s.charAt(0))
                                .collect(Collectors.toSet()),
                        new TreeSet<>(java.util.List.of(states.getText().split(" "))),
                        Arrays.stream(memoryAlphabet.getText().split(" "))
                                .filter(s -> !s.equals(""))
                                .map(s -> s.charAt(0))
                                .collect(Collectors.toSet()),
                        new TreeSet<>(logicRulesSet),
                        startState.getText().trim(),
                        zeroSymbol.getText().trim().charAt(0),
                        new TreeSet<>(java.util.List.of(endStates.getText().split(" "))));
                boolean success = automate.canCreate(language.getText(), cause, logString);
                result.setForeground(success?
                        Color.GREEN: Color.ORANGE);
                    log.removeAll();
                    JLabel nowLabel;
                    for(String s: logString){
                        nowLabel = new JLabel(s);
                        nowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        log.add(nowLabel);
                    }
                    log.updateUI();
                    result.setText(success? "Can create": cause.toString());
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
