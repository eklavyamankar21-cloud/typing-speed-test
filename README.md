Keystroke — Typing Speed Test

A desktop typing speed test built in Java. Type the sentence shown on screen and get live feedback on your speed and accuracy as you type — letters turn green when correct and red when wrong, and your stats update in real time.

What you need before running this

- Java Development Kit (JDK), version 17 or higher
  Check if you already have it by opening a terminal and running: java -version and javac -version
  If nothing shows up, download a JDK from https://adoptium.net (Eclipse Temurin is a good free option)
- Any code editor works, but VS Code with the "Extension Pack for Java" is a good option if you don't already have a setup

How to get the project

Option 1 — Download without Git
1. Go to the repository page on GitHub
2. Click the green "Code" button, then "Download ZIP"
3. Extract the ZIP file anywhere on your computer

Option 2 — Clone with Git (if you have Git installed)
git clone https://github.com/eklavyamankar21-cloud/typing-speed-test.git

How to run it

1. Open a terminal in the folder containing TypingSpeedTestGUI.java
   In VS Code, you can open the folder directly, then open a terminal with Ctrl + backtick
2. Compile the code:
   javac TypingSpeedTestGUI.java
3. Run it:
   java TypingSpeedTestGUI
4. A window will open. Click into the text box and start typing the sentence shown above it. Your time, speed (WPM), and accuracy update live as you type. Click "New Text" to try a different sentence.

How it works, briefly

A basic console program can only read a full line of text after you press Enter, so it can't show feedback while you're typing. This project uses a Swing GUI instead, which can detect every individual keystroke as it happens. That's what makes the live color feedback and real-time stats possible.

Words per minute is calculated using the standard convention of 5 characters equal to 1 word. Accuracy is calculated by comparing what you typed to the target sentence, character by character.

Built as a learning project to practice core Java concepts (Scanner, String handling, timers) and later Swing GUI development (event listeners, styled text, layout).
