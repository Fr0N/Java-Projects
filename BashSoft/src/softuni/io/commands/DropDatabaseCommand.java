package softuni.io.commands;

import softuni.anotations.Alias;
import softuni.contracts.repositoryContracts.Database;
import softuni.exceptions.InvalidCommandException;
import softuni.io.OutputWriter;

@Alias("dropdb")
public class DropDatabaseCommand extends Command{

    private Database repository;

    public DropDatabaseCommand(String input, String[] data) {
        super(input, data);
    }

    @Override
    public void execute() throws Exception {
        String[] data = this.getData();
        if (data.length != 1) {
            throw new InvalidCommandException(this.getInput());
        }

        this.repository.unloadData();
        OutputWriter.writeMessageOnNewLine("Database dropped!");
    }
}
