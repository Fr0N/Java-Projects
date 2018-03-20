package softuni.io.commands;

import softuni.anotations.Alias;
import softuni.anotations.Inject;
import softuni.contracts.repositoryContracts.Database;
import softuni.exceptions.InvalidCommandException;

@Alias("order")
public class PrintOrderedStudentsCommand extends Command{

    @Inject
    private Database studentRepository;

    public PrintOrderedStudentsCommand(String input, String[] data) {
        super(input, data);
    }

    @Override
    public void execute() throws Exception {
        String[] data = this.getData();
        if (data.length != 3 && data.length != 4) {
            throw new InvalidCommandException(this.getInput());
        }

        String course = data[1];
        String compareType = data[2];

        if (data.length == 3) {
            this.studentRepository.printOrderedStudents(course, compareType, null);
            return;
        }

        Integer numberOfStudents = Integer.valueOf(data[3]);

        if (data.length == 4) {
            this.studentRepository.printOrderedStudents(course, compareType, numberOfStudents);
        }
    }
}
