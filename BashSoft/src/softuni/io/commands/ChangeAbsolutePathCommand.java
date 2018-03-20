package softuni.io.commands;

import softuni.anotations.Alias;
import softuni.anotations.Inject;
import softuni.contracts.DirectoryManager;
import softuni.exceptions.InvalidCommandException;

@Alias("cdabs")
public class ChangeAbsolutePathCommand extends Command{

    @Inject
    private DirectoryManager ioManager;

    public ChangeAbsolutePathCommand(String input, String[] data) {
        super(input, data);
    }

    @Override
    public void execute() throws Exception {
        String[] data = this.getData();
        if (data.length != 2) {
            throw new InvalidCommandException(this.getInput());
        }

        String absolutePath = data[1];
        this.ioManager.changeCurrentDirAbsolutePath(absolutePath);
    }
}
