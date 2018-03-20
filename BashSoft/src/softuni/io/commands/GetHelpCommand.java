package softuni.io.commands;

import softuni.anotations.Alias;
import softuni.exceptions.InvalidCommandException;
import softuni.io.OutputWriter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Alias("help")
public class GetHelpCommand extends Command{
    public GetHelpCommand(String input, String[] data) {
        super(input, data);
    }

    @Override
    public void execute() throws Exception {
        String[] data = this.getData();
        if (data.length != 1) {
            throw new InvalidCommandException(this.getInput());
        }

        this.displayHelp();
    }

    private void displayHelp() {
        try (BufferedReader reader = new BufferedReader(new FileReader("resourses\\getHelp.txt"))) {
            while (true) {
                String line = reader.readLine();

                if (line == null) {
                    break;
                }

                OutputWriter.writeMessageOnNewLine(line);
            }

            OutputWriter.writeEmptyLine();
        } catch (FileNotFoundException e) {
            OutputWriter.displayException(e.getMessage());
        } catch (IOException e) {
            OutputWriter.displayException(e.getMessage());
        }
    }
}
