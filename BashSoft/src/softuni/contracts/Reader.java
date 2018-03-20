package softuni.contracts;

import java.io.IOException;

public interface Reader {
    void readCommands() throws IOException, InterruptedException;
}
