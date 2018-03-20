package softuni.io.commands;

import softuni.anotations.Alias;
import softuni.anotations.Inject;
import softuni.contracts.AsynchDownloader;
import softuni.exceptions.InvalidCommandException;

@Alias("downloadasynch")
public class DownloadFileOnNewThreadCommand extends Command {


    @Inject
    private AsynchDownloader downloadManager;

    public DownloadFileOnNewThreadCommand(String input, String[] data) {
        super(input, data);
    }

    @Override
    public void execute() throws Exception {
        String[] data = this.getData();
        if (data.length != 2) {
            throw new InvalidCommandException(this.getInput());
        }

        String fileUrl = data[1];
        this.downloadManager.downloadOnNewThread(fileUrl);
    }
}
