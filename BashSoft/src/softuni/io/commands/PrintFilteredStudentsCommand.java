package softuni.io.commands;

import softuni.anotations.Alias;
import softuni.anotations.Inject;
import softuni.contracts.repositoryContracts.Database;
import softuni.exceptions.InvalidCommandException;

@Alias("filter")
public class PrintFilteredStudentsCommand extends Command{

    @Inject
    private Database studentRepository;

    public PrintFilteredStudentsCommand(String input, String[] data) {
        super(input, data);
    }

    @Override
    public void execute() throws Exception {
        String[] data = this.getData();
        if (data.length != 3 && data.length != 4) {
            throw new InvalidCommandException(this.getInput());
        }

        String course = data[1];
        String filter = data[2];

        if (data.length == 3) {
            this.studentRepository.printFilteredStudents(course, filter, null);
            return;
        }

        Integer numberOfStudents = Integer.valueOf(data[3]);

        if (data.length == 4) {
            this.studentRepository.printFilteredStudents(course, filter, numberOfStudents);
        }
    }
}
