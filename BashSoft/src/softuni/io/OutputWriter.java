package softuni.io;

public class OutputWriter {
    public static void writeMessage(String message) {
        System.out.print(message);
    }

    public static void writeMessageOnNewLine(String message) {
        System.out.println(message);
    }

    public static void writeEmptyLine() {
        System.out.println();
    }

    public static void displayException(String message) {
        System.out.println(message);
    }

    public static void printStudent(String name, Double mark) {
        String output = String.format("%s - %f", name, mark);
        OutputWriter.writeMessageOnNewLine(output);
    }
}
