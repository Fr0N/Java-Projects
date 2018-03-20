package softuni.network;

import softuni.contracts.AsynchDownloader;
import softuni.io.OutputWriter;
import softuni.staticData.ExceptionMessages;
import softuni.staticData.SessionData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class DownloadManager implements AsynchDownloader{
    public void download(String fileUrl) {
        URL url = null;
        ReadableByteChannel rbc = null;
        FileOutputStream fos = null;

        try {
            if (Thread.currentThread().getName().equals("main")) {
                OutputWriter.writeMessageOnNewLine("Started downloading..");
            }

            url = new URL(fileUrl);
            rbc = Channels.newChannel(url.openStream());
            String fileName = extractNameOfFile(url.toString());
            File file = new File(SessionData.currentPath + "/" + fileName);
            fos = new FileOutputStream(file);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            if (Thread.currentThread().getName().equals("main")) {
                OutputWriter.writeMessageOnNewLine("Download complete..");
            }
        } catch (MalformedURLException e) {
            OutputWriter.displayException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }

                if (rbc != null) {
                    rbc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void downloadOnNewThread(String fileUrl) {
//        the "Download complete" message is not printed
//        should make it to be printed by the main thread

        Thread thread = new Thread(() -> download(fileUrl));
        OutputWriter.writeMessageOnNewLine(String.format("Worker thread %d started download..", thread.getId()));
        thread.setDaemon(false);
        thread.start();
    }

    private String extractNameOfFile(String fileUrl) throws MalformedURLException {
        int indexOfLastSlash = fileUrl.lastIndexOf('/');
        if (indexOfLastSlash == -1) {
            throw new MalformedURLException(ExceptionMessages.INVALID_PATH);
        }

        return fileUrl.substring(indexOfLastSlash + 1);
    }
}
