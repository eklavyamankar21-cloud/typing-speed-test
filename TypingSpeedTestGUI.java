import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.util.Random;

public class TypingSpeedTestGUI extends JFrame {

    private static final String[] SAMPLE_TEXTS = {
        "The quick brown fox jumps over the lazy dog while the world keeps turning.",
        "Java is a versatile language used for many kinds of software across many platforms.",
        "Practice makes perfect when it comes to typing speed and accuracy alike.",
        "Consistency and accuracy matter more than raw speed in the long run.",
        "Programming rewards patience, curiosity, and a willingness to keep debugging.",
        "A good typist trusts their fingers and rarely glances down at the keyboard.",
        "Small steady habits compound into skills that once looked impossible to reach."
    };

    private String targetText;
    private JTextPane targetPane;
    private JTextArea inputArea;
    private JLabel timeLabel;
    private JLabel wpmLabel;
    private JLabel accuracyLabel;
    private JLabel statusLabel;

    private long startTime = -1;
    private boolean finished = false;
    private Timer swingTimer; // javax.swing.Timer, NOT java.util.Timer

    public TypingSpeedTestGUI() {
        super("Keystroke - Typing Speed Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 420);
        setLocationRelativeTo(null);

        buildUI();
        startNewTest();
    }

    private void buildUI() {
        Color bg = new Color(0x1c, 0x1b, 0x19);
        Color panelBg = new Color(0x24, 0x22, 0x20);
        Color ink = new Color(0xec, 0xe6, 0xda);
        Color dim = new Color(0x94, 0x8d, 0x80);
        Color ribbon = new Color(0xd9, 0x8e, 0x3b);

        getContentPane().setBackground(bg);
        setLayout(new BorderLayout(12, 12));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Title ---
        JLabel title = new JLabel("Keystroke");
        title.setFont(new Font("Serif", Font.BOLD, 26));
        title.setForeground(ink);
        add(title, BorderLayout.NORTH);

        // --- Stats row ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        statsPanel.setBackground(bg);
        timeLabel = makeStatLabel("0.0s", ribbon);
        wpmLabel = makeStatLabel("0 wpm", ink);
        accuracyLabel = makeStatLabel("100%", ink);
        statsPanel.add(wrapStat("TIME", timeLabel, panelBg));
        statsPanel.add(wrapStat("SPEED", wpmLabel, panelBg));
        statsPanel.add(wrapStat("ACCURACY", accuracyLabel, panelBg));

        // --- Target text pane ---
        targetPane = new JTextPane();
        targetPane.setEditable(false);
        targetPane.setFont(new Font("Monospaced", Font.PLAIN, 16));
        targetPane.setBackground(panelBg);
        targetPane.setForeground(dim);
        targetPane.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // --- Input area ---
        inputArea = new JTextArea(3, 40);
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBackground(new Color(0x16, 0x15, 0x13));
        inputArea.setForeground(ink);
        inputArea.setCaretColor(ribbon);
        inputArea.setBorder(BorderFactory.createLineBorder(new Color(0x3a, 0x37, 0x33)));

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(bg);
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(targetPane), BorderLayout.CENTER);
        centerPanel.add(inputArea, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom controls ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(bg);
        statusLabel = new JLabel("Ready when you are.");
        statusLabel.setForeground(dim);
        JButton restartButton = new JButton("New Text >>");
        restartButton.addActionListener(e -> startNewTest());
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(restartButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Listen for every keystroke ---
        inputArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { onTyping(); }
            public void removeUpdate(DocumentEvent e) { onTyping(); }
            public void changedUpdate(DocumentEvent e) { onTyping(); }
        });

        // --- Timer that ticks every 100ms to refresh live stats ---
        swingTimer = new Timer(100, e -> updateLiveStats());
    }

    private JLabel makeStatLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Monospaced", Font.BOLD, 22));
        label.setForeground(color);
        return label;
    }

    private JPanel wrapStat(String caption, JLabel valueLabel, Color panelBg) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(panelBg);
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        JLabel captionLabel = new JLabel(caption, SwingConstants.CENTER);
        captionLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        captionLabel.setForeground(new Color(0x94, 0x8d, 0x80));
        wrapper.add(captionLabel, BorderLayout.NORTH);
        wrapper.add(valueLabel, BorderLayout.CENTER);
        return wrapper;
    }

    private void startNewTest() {
        targetText = SAMPLE_TEXTS[new Random().nextInt(SAMPLE_TEXTS.length)];
        finished = false;
        startTime = -1;
        inputArea.setText("");
        inputArea.setEnabled(true);
        timeLabel.setText("0.0s");
        wpmLabel.setText("0 wpm");
        accuracyLabel.setText("100%");
        statusLabel.setText("Ready when you are.");
        swingTimer.stop();
        renderTarget("");
        inputArea.requestFocusInWindow();
    }

    /** Called on every keystroke via the DocumentListener. */
    private void onTyping() {
        if (finished) return;

        if (startTime == -1) {
            startTime = System.nanoTime();
            statusLabel.setText("Typing...");
            swingTimer.start();
        }

        String typed = inputArea.getText();
        renderTarget(typed);
        updateLiveStats();

        if (typed.length() >= targetText.length()) {
            finish();
        }
    }

    /** Repaints the target text with green/red highlighting based on what's typed so far. */
    private void renderTarget(String typed) {
        StyledDocument doc = targetPane.getStyledDocument();

        SimpleAttributeSet pending = new SimpleAttributeSet();
        StyleConstants.setForeground(pending, new Color(0x94, 0x8d, 0x80));

        SimpleAttributeSet correct = new SimpleAttributeSet();
        StyleConstants.setForeground(correct, new Color(0x7e, 0xa6, 0x7a));

        SimpleAttributeSet incorrect = new SimpleAttributeSet();
        StyleConstants.setForeground(incorrect, new Color(0xb8, 0x5c, 0x4a));
        StyleConstants.setBackground(incorrect, new Color(0x3a, 0x25, 0x20));

        try {
            doc.remove(0, doc.getLength()); // clear existing content

            for (int i = 0; i < targetText.length(); i++) {
                String ch = String.valueOf(targetText.charAt(i));
                AttributeSet style;
                if (i < typed.length()) {
                    style = (typed.charAt(i) == targetText.charAt(i)) ? correct : incorrect;
                } else {
                    style = pending;
                }
                doc.insertString(doc.getLength(), ch, style);
            }
        } catch (BadLocationException ex) {
            // Shouldn't happen since we're always inserting at doc.getLength()
            ex.printStackTrace();
        }
    }

    /** Refreshes elapsed time, WPM, and accuracy — called by the swing Timer and on each keystroke. */
    private void updateLiveStats() {
        if (startTime == -1) return;

        double elapsedSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0;
        String typed = inputArea.getText();

        double wordsTyped = typed.length() / 5.0;
        double elapsedMinutes = elapsedSeconds / 60.0;
        double wpm = elapsedMinutes > 0 ? wordsTyped / elapsedMinutes : 0;

        double accuracy = calculateAccuracy(targetText, typed);

        timeLabel.setText(String.format("%.1fs", elapsedSeconds));
        wpmLabel.setText(String.format("%.0f wpm", wpm));
        accuracyLabel.setText(String.format("%.0f%%", accuracy));
    }

    private double calculateAccuracy(String target, String typed) {
        int maxLength = Math.max(target.length(), typed.length());
        if (maxLength == 0) return 100.0;

        int minLength = Math.min(target.length(), typed.length());
        int matches = 0;
        for (int i = 0; i < minLength; i++) {
            if (target.charAt(i) == typed.charAt(i)) matches++;
        }
        return (matches / (double) maxLength) * 100.0;
    }

    private void finish() {
        finished = true;
        inputArea.setEnabled(false);
        swingTimer.stop();
        updateLiveStats();
        statusLabel.setText("Done - press \"New Text\" to go again.");
    }

    public static void main(String[] args) {
        // Run on the Swing event thread, as is standard practice
        SwingUtilities.invokeLater(() -> {
            TypingSpeedTestGUI app = new TypingSpeedTestGUI();
            app.setVisible(true);
        });
    }
}
