package softuni.io.commands;

import softuni.contracts.Executable;
import softuni.exceptions.InvalidCommandException;

public abstract class Command implements Executable{
    private String input;
    private String[] data;

    public Command(String input, String[] data) {
        this.setInput(input);
        this.setData(data);
    }

    protected String getInput() {
        return input;
    }

    private void setInput(String input) {
        if (input == null || input.equals("")) {
            throw new InvalidCommandException(input);
        }

        this.input = input;
    }

    protected String[] getData() {
        return data;
    }

    private void setData(String[] data) {
        if (data == null || data.length < 1) {
            throw new InvalidCommandException(this.getInput());
        }

        this.data = data;
    }

    public abstract void execute() throws Exception;
}
