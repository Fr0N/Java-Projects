package softuni.contracts.repositoryContracts;

import softuni.contracts.contractsForModels.Course;
import softuni.contracts.contractsForModels.Student;
import softuni.dataStructures.SimpleSortedList;

import java.util.Comparator;

public interface Requester {
    void getStudentsMarkInCourse(String course, String student);

    void getStudentsByCourse(String course);

    SimpleSortedList<Course> getAllCoursesSorted(Comparator<Course> cmp);

    SimpleSortedList<Student> getAllStudentsSorted(Comparator<Student> cmp);
}
