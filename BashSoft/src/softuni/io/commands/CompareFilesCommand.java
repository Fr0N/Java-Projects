package softuni.io.commands;


import softuni.anotations.Alias;
import softuni.anotations.Inject;
import softuni.contracts.ContentComparer;
import softuni.exceptions.InvalidCommandException;

@Alias("cmp")
public class CompareFilesCommand extends Command{

    @Inject
    private ContentComparer tester;

    public CompareFilesCommand(String input, String[] data) {
        super(input, data);
    }

    @Override
    public void execute() throws Exception {
        String[] data = this.getData();
        if (data.length != 3) {
            throw new InvalidCommandException(this.getInput());
        }

        String firstPath = data[1];
        String secondPath = data[2];
        this.tester.compareContent(firstPath, secondPath);
    }
}
