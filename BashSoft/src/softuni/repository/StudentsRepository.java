package softuni.repository;

import softuni.contracts.DataFilter;
import softuni.contracts.DataSorter;
import softuni.contracts.contractsForModels.Course;
import softuni.contracts.contractsForModels.Student;
import softuni.contracts.repositoryContracts.Database;
import softuni.dataStructures.SimpleSortedList;
import softuni.io.OutputWriter;
import softuni.models.SoftuniCourse;
import softuni.models.SoftuniStudent;
import softuni.staticData.ExceptionMessages;
import softuni.staticData.SessionData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentsRepository implements Database{

    private boolean isDataInitialized = false;

    private LinkedHashMap<String, Course> courses;
    private LinkedHashMap<String, Student> students;

    private DataFilter filter;
    private DataSorter sorter;

    public StudentsRepository(DataFilter filter, DataSorter sorter) {
        this.filter = filter;
        this.sorter = sorter;
    }

    public void loadData(String fileName) throws IOException {
        if (isDataInitialized) {
            throw new RuntimeException(ExceptionMessages.DATA_ALREADY_INITIALIZED);
        }

        this.students = new LinkedHashMap<>();
        this.courses = new LinkedHashMap<>();
        this.readData(fileName);
    }

    public void unloadData() throws IOException {
        if (!this.isDataInitialized) {
            throw new RuntimeException(ExceptionMessages.DATA_NOT_INITIALIZED);
        }

        this.students = null;
        this.courses = null;
        this.isDataInitialized = false;
    }

    public void readData(String fileName) throws IOException {
        String regex = "([A-Z][a-zA-Z#\\+]*_[A-Z][a-z]{2}_\\d{4})\\s+([A-Za-z]+\\d{2}_\\d{2,4})\\s([\\s0-9]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;

        String path = SessionData.currentPath + "\\" + fileName;
        List<String> lines = Files.readAllLines(Paths.get(path));

        for (String line : lines) {
            matcher = pattern.matcher(line);

            if (!line.isEmpty() && matcher.find()) {
                String courseName = matcher.group(1);
                String studentName = matcher.group(2);
                String scoreStr = matcher.group(3);

                try {
                    String[] splitScores = scoreStr.split("\\s+");
                    int[] scores = new int[splitScores.length];
                    for (int i = 0; i < splitScores.length; i++) {
                        scores[i] = Integer.parseInt(splitScores[i]);
                    }

                    if (Arrays.stream(scores).anyMatch(score -> score > 100 || score < 0)) {
                        OutputWriter.displayException(ExceptionMessages.INVALID_SCORE);
                        continue;
                    }

                    if (scores.length > SoftuniCourse.NUMBER_OF_TASKS_ON_EXAM) {
                        OutputWriter.displayException(ExceptionMessages.INVALID_NUMBER_OF_SCORES);
                        continue;
                    }

                    if (!this.students.containsKey(studentName)) {
                        this.students.put(studentName, new SoftuniStudent(studentName));
                    }

                    if (!this.courses.containsKey(courseName)) {
                        this.courses.put(courseName, new SoftuniCourse(courseName));
                    }

                    Course course = this.courses.get(courseName);
                    Student student = this.students.get(studentName);
                    student.enrollInCourse(course);
                    student.setMarkOnCourse(courseName, scores);
                    course.enrollStudent(student);
                } catch (NumberFormatException ex) {
                    OutputWriter.displayException(ex.getMessage());
                }
            }
        }

        isDataInitialized = true;
        OutputWriter.writeMessageOnNewLine("Data read.");
    }

    private boolean isQueryForCoursePossible(String courseName) {
        if (!isDataInitialized) {
            OutputWriter.displayException(ExceptionMessages.DATA_NOT_INITIALIZED);
            return false;
        }

        if (!this.courses.containsKey(courseName)) {
            OutputWriter.displayException(ExceptionMessages.NON_EXISTENT_COURSE);
            return false;
        }
        return true;
    }

    private boolean isQueryForStudentPossible(String courseName, String studentName) {
        if (!isQueryForCoursePossible(courseName)) {
            return false;
        }

        if (!this.courses.get(courseName).getStudentsByName().containsKey(studentName)) {
            OutputWriter.displayException(ExceptionMessages.NON_EXISTING_STUDENT);
            return false;
        }

        return true;
    }

    public void getStudentsMarkInCourse(String course, String student) {
        if (!isQueryForStudentPossible(course, student)) {
            return;
        }

        double mark = this.courses.get(course).getStudentsByName().get(student).getMarksByCourseName().get(course);
        OutputWriter.printStudent(student, mark);
    }

    public void getStudentsByCourse(String course) {
        if (!isQueryForCoursePossible(course)) {
            return;
        }

        OutputWriter.writeMessageOnNewLine(course + ":");
        for (Map.Entry<String, Student> student : this.courses.get(course).getStudentsByName().entrySet()) {
            this.getStudentsMarkInCourse(course, student.getKey());
        }
    }

    @Override
    public SimpleSortedList<Course> getAllCoursesSorted(Comparator<Course> cmp) {
        SimpleSortedList<Course> courseSortedList =
                new SimpleSortedList<Course>(Course.class, cmp);

        courseSortedList.addAll(this.courses.values());

        return courseSortedList;
    }

    @Override
    public SimpleSortedList<Student> getAllStudentsSorted(Comparator<Student> cmp) {
        SimpleSortedList<Student> studentSortedList =
                new SimpleSortedList<Student>(Student.class, cmp);

        studentSortedList.addAll(this.students.values());

        return studentSortedList;
    }

    public void printFilteredStudents(String course, String filter, Integer numberOfStudents) {
        if (!isQueryForCoursePossible(course)) {
            return;
        }

        LinkedHashMap<String, Double> marks = new LinkedHashMap<>();
        for (Map.Entry<String, Student> entry : this.courses.get(course).getStudentsByName().entrySet()) {
            marks.put(entry.getKey(), entry.getValue().getMarksByCourseName().get(course));
        }

        this.filter.printFilteredStudents(marks, filter, numberOfStudents);
    }

    public void filterAndTake(String courseName, String filter) {
        int studentsToTake = this.courses.get(courseName).getStudentsByName().size();
        this.printFilteredStudents(courseName, filter, studentsToTake);
    }

    public void printOrderedStudents(String course, String compareType, Integer numberOfStudents) {
        if (!isQueryForCoursePossible(course)) {
            return;
        }

        LinkedHashMap<String, Double> marks = new LinkedHashMap<>();
        for (Map.Entry<String, Student> entry : this.courses.get(course).getStudentsByName().entrySet()) {
            marks.put(entry.getKey(), entry.getValue().getMarksByCourseName().get(course));
        }

        this.sorter.printSortedStudents(marks, compareType, numberOfStudents);
    }

    public void orderAndTake(String courseName, String orderType) {
        int studentsToTake = this.courses.get(courseName).getStudentsByName().size();
        this.printOrderedStudents(courseName, orderType, studentsToTake);
    }
}
