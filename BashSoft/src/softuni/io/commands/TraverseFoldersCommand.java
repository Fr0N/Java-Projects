package softuni.io.commands;

import softuni.anotations.Alias;
import softuni.anotations.Inject;
import softuni.contracts.DirectoryManager;
import softuni.exceptions.InvalidCommandException;

@Alias("ls")
public class TraverseFoldersCommand extends Command {

    @Inject
    private DirectoryManager ioManager;

    public TraverseFoldersCommand(String input, String[] data) {
        super(input, data);
    }

    @Override
    public void execute() throws Exception {
        String[] data = this.getData();
        if (data.length != 1 && data.length != 2) {
            throw new InvalidCommandException(this.getInput());
        }

        if (data.length == 1) {
            this.ioManager.traverseDirectory(0);
        } else {
            int depth = Integer.parseInt(data[1]);
            this.ioManager.traverseDirectory(depth);
        }
    }
}
