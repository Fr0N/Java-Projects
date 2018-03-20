package softuni.contracts.contractsForModels;

import java.util.Map;

public interface Course extends Comparable<Course>{
    String getName();

    Map<String, Student> getStudentsByName();

    void enrollStudent(Student student);
}
