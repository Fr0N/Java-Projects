package softuni.io;

import softuni.contracts.Interpreter;
import softuni.contracts.Reader;
import softuni.staticData.SessionData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputReader implements Reader{
    private final String END_COMMAND = "quit";

    private Interpreter interpreter;

    public InputReader(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public void readCommands() throws IOException, InterruptedException {
        OutputWriter.writeMessage(String.format("%s > ", SessionData.currentPath));

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = reader.readLine().trim();

        while (!input.equals(END_COMMAND)) {
            this.interpreter.interpretCommand(input);
            OutputWriter.writeMessage(String.format("%s > ", SessionData.currentPath));

            input = reader.readLine().trim();
        }

        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        for (Thread thread : threads) {
            if (!thread.getName().equals("main") && !thread.isDaemon()) {
                thread.join();
            }
        }
    }
}
