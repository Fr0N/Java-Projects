package softuni.io.commands;

import softuni.anotations.Alias;
import softuni.anotations.Inject;
import softuni.contracts.repositoryContracts.Database;
import softuni.exceptions.InvalidCommandException;

@Alias("show")
public class ShowWantedCourseCommand extends Command {

    @Inject
    private Database studentRepository;

    public ShowWantedCourseCommand(String input, String[] data) {
        super(input, data);
    }

    @Override
    public void execute() throws Exception {
        String[] data = this.getData();
        if (data.length != 2 && data.length != 3) {
            throw new InvalidCommandException(this.getInput());
        }

        if (data.length == 2) {
            String courseName = data[1];
            this.studentRepository.getStudentsByCourse(courseName);
        } else if (data.length == 3) {
            String courseName = data[1];
            String studentName = data[2];
            this.studentRepository.getStudentsMarkInCourse(courseName, studentName);
        }
    }
}
