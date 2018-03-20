package softuni.contracts.repositoryContracts;

public interface FilteredTaker {
    void filterAndTake(String courseName, String filter);

    //MUST FIX IT
    void printFilteredStudents(String course, String filter, Integer numberOfStudents);
}
