package softuni.io.commands;

import softuni.anotations.Alias;
import softuni.anotations.Inject;
import softuni.contracts.contractsForModels.Course;
import softuni.contracts.contractsForModels.Student;
import softuni.contracts.repositoryContracts.Database;
import softuni.dataStructures.SimpleSortedList;
import softuni.exceptions.InvalidCommandException;
import softuni.io.OutputWriter;

import java.util.Comparator;

@Alias("display")
public class DisplayCommand extends Command{

    @Inject
    private Database studentRepository;

    public DisplayCommand(String input, String[] data) {
        super(input, data);
    }

    @Override
    public void execute() throws Exception {
        String[] data  = this.getData();
        if (data.length != 3) {
            throw new InvalidCommandException(this.getInput());
        }

        String entityToDisplay = data[1];
        String sortType = data[2];
        if (entityToDisplay.equalsIgnoreCase("students")) {
            Comparator<Student> studentComparator =
                    this.createStudentComparator(sortType);
            SimpleSortedList<Student> list =
                    this.studentRepository.getAllStudentsSorted(studentComparator);
            OutputWriter.writeMessageOnNewLine(
                    list.joinWith(System.lineSeparator()));
        } else if (entityToDisplay.equalsIgnoreCase("courses")) {
            Comparator<Course> courseComparator =
                    this.createCourseComparator(sortType);
            SimpleSortedList<Course> list =
                    this.studentRepository.getAllCoursesSorted(courseComparator);
            OutputWriter.writeMessageOnNewLine(
                    list.joinWith(System.lineSeparator()));
        } else {
            throw new InvalidCommandException(this.getInput());
        }
    }

    private Comparator<Student> createStudentComparator(String sortType) {
        if (sortType.equalsIgnoreCase("ascending")) {
            return (s1, s2) -> s1.compareTo(s2);
        } else if (sortType.equalsIgnoreCase("descending")) {
            return (s1, s2) -> s2.compareTo(s1);
        } else {
            throw new InvalidCommandException(this.getInput());
        }
    }

    private Comparator<Course> createCourseComparator(String sortType) {
        if (sortType.equalsIgnoreCase("ascending")) {
            return (s1, s2) -> s1.compareTo(s2);
        } else if (sortType.equalsIgnoreCase("descending")) {
            return (s1, s2) -> s2.compareTo(s1);
        } else {
            throw new InvalidCommandException(this.getInput());
        }
    }
}
