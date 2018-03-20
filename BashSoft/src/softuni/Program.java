package softuni;

import softuni.contracts.*;
import softuni.contracts.repositoryContracts.Database;
import softuni.io.IOManager;
import softuni.io.InputReader;
import softuni.io.OutputWriter;
import softuni.judge.Tester;
import softuni.network.DownloadManager;
import softuni.repository.RepositoryFilter;
import softuni.repository.RepositorySorter;
import softuni.repository.StudentsRepository;

import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException, InterruptedException {
        ContentComparer tester = new Tester();
        AsynchDownloader downloadManager = new DownloadManager();
        DirectoryManager ioManager = new IOManager();
        DataSorter sorter = new RepositorySorter();
        DataFilter filter = new RepositoryFilter();
        Database repository = new StudentsRepository(filter, sorter);
        Interpreter currentInterpreter =
                new CommandInterpreter(tester, repository, downloadManager, ioManager);
        Reader reader = new InputReader(currentInterpreter);

        try {
            reader.readCommands();
        } catch (Exception ex) {
            OutputWriter.displayException(ex.getMessage());
        }
    }
}
