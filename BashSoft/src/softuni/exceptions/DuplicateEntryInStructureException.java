package softuni.exceptions;

/**
 * Created by user1 on 8/4/2016.
 */
public class DuplicateEntryInStructureException extends RuntimeException {
    private static final String DUPLICATE_ENTRY = "The %s already exists in %s.";

    public DuplicateEntryInStructureException(String entryName, String structureName) {
        super(String.format(DUPLICATE_ENTRY, entryName, structureName));
    }

    public DuplicateEntryInStructureException(String message) {
        super(message);
    }
}
