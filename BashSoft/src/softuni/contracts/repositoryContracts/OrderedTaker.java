package softuni.contracts.repositoryContracts;

public interface OrderedTaker {
    void orderAndTake(String courseName, String orderType);

    //MUST FIX IT
    void printOrderedStudents(String course, String compareType, Integer numberOfStudents);
}
