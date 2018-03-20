package softuni;

import softuni.anotations.Alias;
import softuni.anotations.Inject;
import softuni.contracts.*;
import softuni.contracts.repositoryContracts.Database;
import softuni.io.OutputWriter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class CommandInterpreter implements Interpreter {

    private static final String COMMANDS_LOCATION = "src/softuni/io/commands/";
    private static final String COMMANDS_PACKAGE = "softuni.io.commands.";

    private ContentComparer tester;
    private Database repository;
    private AsynchDownloader downloadManager;
    private DirectoryManager inputOutputManager;

    public CommandInterpreter(ContentComparer tester,
                              Database repository,
                              AsynchDownloader downloadManager,
                              DirectoryManager inputOutputManager) {
        this.tester = tester;
        this.repository = repository;
        this.downloadManager = downloadManager;
        this.inputOutputManager = inputOutputManager;
    }

    public void interpretCommand(String input) throws IOException {
        String[] data = input.split("\\s+");
        String commandName = data[0];

        try {
            Executable command = parseCommand(input, data, commandName);
            command.execute();
        } catch (IllegalArgumentException ex) {
            OutputWriter.displayException(ex.getMessage());
        } catch (StringIndexOutOfBoundsException ex) {
            OutputWriter.displayException(ex.getMessage());
        } catch (IOException ex) {
            OutputWriter.displayException(ex.getMessage());
        } catch (Throwable t) {
            OutputWriter.displayException(t.getMessage());
        }
    }

    private Executable parseCommand(String input, String[] data, String commandName) {
        File commandsFolder = new File(COMMANDS_LOCATION);
        Executable executable = null;

        for (File file : commandsFolder.listFiles()) {
            if (!file.isFile() || !file.getName().endsWith(".java")) {
                continue;
            }

            try {
                String className = file.getName()
                        .substring(0, file.getName().lastIndexOf("."));

                Class<Executable> exeClass = (Class<Executable>) Class.forName(COMMANDS_PACKAGE + className);

                if (!exeClass.isAnnotationPresent(Alias.class)) {
                    continue;
                }

                Alias alias = exeClass.getAnnotation(Alias.class);
                String value = alias.value();
                if (!value.equalsIgnoreCase(commandName)) {
                    continue;
                }

                Constructor exeCtor = exeClass.getConstructor(String.class, String[].class);
                executable = (Executable) exeCtor.newInstance(input, data);
                this.injectDependancies(executable, exeClass);


            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }

        return executable;
    }

    private void injectDependancies(
            Executable executable,
            Class<Executable> exeClass)
            throws ReflectiveOperationException {

        Field[] exeFields = exeClass.getDeclaredFields();

        for (Field fieldToSet : exeFields) {
            if (!fieldToSet.isAnnotationPresent(Inject.class)) {
                continue;
            }

            fieldToSet.setAccessible(true);

            Field[] theseFields = CommandInterpreter.class.getDeclaredFields();
            for (Field thisField : theseFields) {
                if (!thisField.getType().equals(fieldToSet.getType())) {
                    continue;
                }

                thisField.setAccessible(true);
                fieldToSet.set(executable, thisField.get(this));
            }
        }
    }
}
