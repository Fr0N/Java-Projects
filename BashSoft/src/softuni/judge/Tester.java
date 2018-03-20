package softuni.judge;

import softuni.contracts.ContentComparer;
import softuni.io.OutputWriter;
import softuni.staticData.ExceptionMessages;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Tester  implements ContentComparer{
    public void compareContent(String actualOutput, String expectedOutput) throws IOException {
        try {
            OutputWriter.writeMessageOnNewLine("Reading files...");
            String mismatchPath = getMismatchPath(expectedOutput);

            List<String> actualOuputString = readTextFile(actualOutput);
            List<String> expectedOuputString = readTextFile(expectedOutput);
            boolean mismatch = compareStrings(actualOuputString, expectedOuputString, mismatchPath);

            if (mismatch) {
                List<String> mismatchString = readTextFile(mismatchPath);
                mismatchString.forEach(OutputWriter::writeMessageOnNewLine);
            } else {
                OutputWriter.writeMessageOnNewLine("Files are identical. There are no mismatches.");
            }
        } catch (IOException ex) {
            throw new IOException(ExceptionMessages.INVALID_PATH);
        }
    }

    private String getMismatchPath(String expectedOutput) {
        int index = expectedOutput.lastIndexOf('\\');
        String directoryPath = expectedOutput.substring(0, index);
        return directoryPath + "\\mismatch.txt";
    }

    private List<String> readTextFile(String filePath) throws IOException {
        List<String> text = new ArrayList<>();

        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = reader.readLine();

        while (line != null) {
            text.add(line);
            line = reader.readLine();
        }

        reader.close();

        return text;
    }

    private boolean compareStrings(
            List<String> actualOutputString,
            List<String> expectedOutputString,
            String mismatchPath) {
        OutputWriter.writeMessageOnNewLine("Comparing files...");
        String output = "";
        boolean isMismatch = false;

        try {
            FileWriter fileWriter = new FileWriter(mismatchPath);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            int maxLength = Math.max(actualOutputString.size(), expectedOutputString.size());
            for (int i = 0; i < maxLength; i++) {
                String actualLine = actualOutputString.get(i);
                String expectedLine = expectedOutputString.get(i);

                if (!actualLine.equals(expectedLine)) {
                    output = String.format("mismatch -> expected(%s), actual(%s)%n", expectedLine, actualLine);
                    isMismatch = true;
                } else {
                    output = String.format("line match -> %s%n", actualLine);
                }

                writer.write(output);
            }

            writer.close();
        } catch (IOException ex) {
            isMismatch = true;
            OutputWriter.displayException(ExceptionMessages.CANNOT_ACCESS_FILE);
        } catch (IndexOutOfBoundsException ex) {
            isMismatch = true;
            OutputWriter.displayException(ExceptionMessages.INVALID_OUTPUT_LENGTH);
        }

        return isMismatch;
    }
}
