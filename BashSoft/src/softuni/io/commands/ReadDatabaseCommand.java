package softuni.io.commands;

import softuni.anotations.Alias;
import softuni.anotations.Inject;
import softuni.contracts.repositoryContracts.Database;
import softuni.exceptions.InvalidCommandException;

@Alias("readdb")
public class ReadDatabaseCommand extends Command {

    @Inject
    private Database studentRepository;

    public ReadDatabaseCommand(String input, String[] data) {
        super(input, data);
    }

    @Override
    public void execute() throws Exception {
        String[] data = this.getData();
        if (data.length != 2) {
            throw new InvalidCommandException(this.getInput());
        }

        String fileName = data[1];
        this.studentRepository.loadData(fileName);
    }
}
