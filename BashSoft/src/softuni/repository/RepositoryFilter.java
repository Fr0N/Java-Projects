package softuni.repository;

import softuni.contracts.DataFilter;
import softuni.io.OutputWriter;
import softuni.staticData.ExceptionMessages;

import java.util.HashMap;
import java.util.function.Predicate;

public class RepositoryFilter implements DataFilter{
    public void printFilteredStudents(
            HashMap<String, Double> studentsWithMarks,
            String filterType,
            Integer numberOfStudents) {
        Predicate<Double> filter = createFilter(filterType);

        filterAndTake(filter, studentsWithMarks, numberOfStudents);
    }

    private Predicate<Double> createFilter(String filterType) {
        switch (filterType) {
            case "excellent":
                return mark -> mark >= 5.0;
            case "average":
                return mark -> 3.5 <= mark && mark < 5.0;
            case "poor":
                return mark -> mark < 3.5;
            default:
                return null;
        }
    }

    private void filterAndTake(
            Predicate<Double> filter,
            HashMap<String, Double> studentsWithMarks,
            Integer numberOfStudents) {
        if (filter == null) {
            OutputWriter.displayException(ExceptionMessages.INVALID_FILTER);
            return;
        }

        int studentsCount = 0;
        for (String student : studentsWithMarks.keySet()) {
            if (studentsCount == numberOfStudents) {
                break;
            }

            Double mark = studentsWithMarks.get(student);

            if (filter.test(mark)) {
                OutputWriter.printStudent(student, mark);
                studentsCount++;
            }
        }
    }
}
