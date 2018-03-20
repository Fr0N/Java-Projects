package softuni.io.commands;

import softuni.anotations.Alias;
import softuni.contracts.DirectoryManager;
import softuni.exceptions.InvalidCommandException;

@Alias("mkdir")
public class MakeDirectoryCommand extends Command {

    private DirectoryManager ioManager;

    public MakeDirectoryCommand(String input, String[] data) {
        super(input, data);
    }

    @Override
    public void execute() throws Exception {
        String[] data = this.getData();
        if (data.length != 2) {
            throw new InvalidCommandException(this.getInput());
        }

        String folderName = data[1];
        this.ioManager.createDirectoryInCurrentFolder(folderName);
    }
}
