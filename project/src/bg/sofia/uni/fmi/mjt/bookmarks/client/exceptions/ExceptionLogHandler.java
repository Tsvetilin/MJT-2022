package bg.sofia.uni.fmi.mjt.bookmarks.client.exceptions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ExceptionLogHandler {

    private static final String LOG_FILE_ROOT = "./clientErrors/";

    public static void logException(Exception e) {
        var filePath = Path.of(LOG_FILE_ROOT + "Log_" + LocalDate.now() + ".txt");
        createLogFile(filePath);

        try (var writer = new BufferedWriter(new FileWriter(filePath.toString()))) {
            writer.write(Arrays
                .stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining(System.lineSeparator()))
            );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private static void createLogFile(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent().toAbsolutePath());
                Files.createFile(path.toAbsolutePath());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
