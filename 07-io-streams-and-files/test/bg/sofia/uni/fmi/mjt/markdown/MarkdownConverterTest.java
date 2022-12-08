package bg.sofia.uni.fmi.mjt.markdown;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MarkdownConverterTest {

    private static final Path TEST_FILE_MD_PATH = Path.of("testing.md");
    private static final Path TEST_FILE_HTML_PATH = Path.of("testing.html");

    private void initialize(String text, Path path) {
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new IllegalStateException("A problem occurred while creating a directory.", e);
            }
        }
        try (var bufferedWriter = Files.newBufferedWriter(path)) {
            bufferedWriter.write(text);
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while writing to a file", e);
        }
    }

    private String readFileResult(Path path) {
        StringBuilder result = new StringBuilder();
        try (var bufferedReader = Files.newBufferedReader(path)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file", e);
        }

        return result.toString().trim();
    }

    private void dispose(Path... paths) {
        for (var path : paths) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new IllegalStateException("A problem occurred while deleting a file", e);
            }
        }
    }

    private void deleteDirectoryRecursively(Path pathToDelete) {
        if (Files.exists(pathToDelete)) {
            File[] allContents = pathToDelete.toFile().listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    deleteDirectoryRecursively(file.toPath());
                }
            }
            try {
                Files.delete(pathToDelete);
            } catch (IOException e) {
                throw new IllegalStateException("A problem occurred while deleting a file", e);
            }
        }
    }

    private static final String HTML_START = "<html>" + System.lineSeparator() + "<body>";
    private static final String HTML_END = "</body>" + System.lineSeparator() + "</html>";

    private static final String HEADING_1_MD = "# Test";
    private static final String HEADING_2_MD = "## Test";
    private static final String HEADING_6_MD = "###### Test";
    private static final String BOLD_MD = "**bold text**";
    private static final String CURSIVE_MD = "*cursive text*";
    private static final String CODE_MD = "`code text`";

    private static final String HEADING_1_HTML = "<h1>Test</h1>";
    private static final String HEADING_2_HTML = "<h2>Test</h2>";
    private static final String HEADING_6_HTML = "<h6>Test</h6>";


    private static final String BOLD_HTML = "<strong>bold text</strong>";
    private static final String CURSIVE_HTML = "<em>cursive text</em>";
    private static final String CODE_HTML = "<code>code text</code>";

    private MarkdownConverter converter = new MarkdownConverter();


    @Test
    void testConvertMarkdownWithPathsShouldHandleIOException(){
        assertThrows(IllegalStateException.class,
                () -> converter.convertMarkdown(Path.of("non-exisitng"),Path.of("non-exisiting-as-well")),
                "Should throw IO Exception");
    }

    @Test
    void testConvertMarkdownWithReaderWriterShouldBeCorrectWithEmptyMD() {
        Reader reader = new StringReader("");
        Writer writer = new StringWriter();

        converter.convertMarkdown(reader, writer);

        assertEquals(HTML_START + System.lineSeparator() + HTML_END, writer.toString(), "Invalid html");
    }


    @Test
    void testConvertMarkdownWithReaderWriterShouldBeCorrectH1() {
        Reader reader = new StringReader(HEADING_1_MD);
        Writer writer = new StringWriter();

        converter.convertMarkdown(reader, writer);

        assertEquals(HTML_START + System.lineSeparator() + HEADING_1_HTML + System.lineSeparator() + HTML_END, writer.toString(), "Invalid html");
    }

    @Test
    void testConvertMarkdownWithReaderWriterShouldBeCorrectH2() {
        Reader reader = new StringReader(HEADING_2_MD);
        Writer writer = new StringWriter();

        converter.convertMarkdown(reader, writer);

        assertEquals(HTML_START + System.lineSeparator() + HEADING_2_HTML + System.lineSeparator() + HTML_END, writer.toString(), "Invalid html");
    }

    @Test
    void testConvertMarkdownWithReaderWriterShouldBeCorrectH6() {
        Reader reader = new StringReader(HEADING_6_MD);
        Writer writer = new StringWriter();

        converter.convertMarkdown(reader, writer);

        assertEquals(HTML_START + System.lineSeparator() + HEADING_6_HTML + System.lineSeparator() + HTML_END, writer.toString(), "Invalid html");
    }

    @Test
    void testConvertMarkdownWithReaderWriterShouldBeCorrectCursive() {
        Reader reader = new StringReader(CURSIVE_MD);
        Writer writer = new StringWriter();

        converter.convertMarkdown(reader, writer);

        assertEquals(HTML_START + System.lineSeparator() + CURSIVE_HTML + System.lineSeparator() + HTML_END, writer.toString(), "Invalid html");
    }

    @Test
    void testConvertMarkdownWithReaderWriterShouldBeCorrectBold() {
        Reader reader = new StringReader(BOLD_MD);
        Writer writer = new StringWriter();

        converter.convertMarkdown(reader, writer);

        assertEquals(HTML_START + System.lineSeparator() + BOLD_HTML + System.lineSeparator() + HTML_END, writer.toString(), "Invalid html");
    }

    @Test
    void testConvertMarkdownWithReaderWriterShouldBeCorrectCode() {
        Reader reader = new StringReader(CODE_MD);
        Writer writer = new StringWriter();

        converter.convertMarkdown(reader, writer);

        assertEquals(HTML_START + System.lineSeparator() + CODE_HTML + System.lineSeparator() + HTML_END, writer.toString(), "Invalid html");
    }

    @Test
    void testConvertMarkdownWithReaderWriterShouldBeCorrectMultipleOnLine() {
        Reader reader = new StringReader(CODE_MD + " " + BOLD_MD + " " + CURSIVE_MD);
        Writer writer = new StringWriter();

        converter.convertMarkdown(reader, writer);

        assertEquals(HTML_START + System.lineSeparator() + CODE_HTML + " " + BOLD_HTML + " " + CURSIVE_HTML + System.lineSeparator() + HTML_END, writer.toString(), "Invalid html");
    }

    @Test
    void testConvertMarkdownWithFilePathsShouldBeCorrectMultipleOnLine() {

        initialize(CODE_MD + " " + BOLD_MD + " " + CURSIVE_MD, TEST_FILE_MD_PATH);
        converter.convertMarkdown(TEST_FILE_MD_PATH, TEST_FILE_HTML_PATH);

        String result = readFileResult(TEST_FILE_HTML_PATH);
        dispose(TEST_FILE_HTML_PATH, TEST_FILE_MD_PATH);
        assertEquals(HTML_START + System.lineSeparator() + CODE_HTML + " " + BOLD_HTML + " " + CURSIVE_HTML + System.lineSeparator() + HTML_END, result, "Invalid html");
    }

    @Test
    void testConvertMarkdownWithFilePathsShouldBeCorrectEmpty() {

        initialize("", TEST_FILE_MD_PATH);
        converter.convertMarkdown(TEST_FILE_MD_PATH, TEST_FILE_HTML_PATH);

        String result = readFileResult(TEST_FILE_HTML_PATH);

        dispose(TEST_FILE_MD_PATH, TEST_FILE_HTML_PATH);

        assertEquals(HTML_START + System.lineSeparator() + HTML_END, result, "Invalid html");
    }

    @Test
    void testConvertMarkdownWithDirectoryPathShouldBeCorrect() {

        initialize("", Path.of("Tests", "test1.md"));
        initialize(HEADING_1_MD, Path.of("Tests", "test2.md"));

        converter.convertAllMarkdownFiles(Path.of("Tests"), Path.of("TestResults"));

        String result1 = readFileResult(Path.of("TestResults", "test1.html"));
        String result2 = readFileResult(Path.of("TestResults", "test2.html"));

        deleteDirectoryRecursively(Path.of("Tests"));
        deleteDirectoryRecursively(Path.of("TestResults"));
        assertEquals(HTML_START + System.lineSeparator() + HTML_END, result1, "Invalid html");
        assertEquals(HTML_START + System.lineSeparator() + HEADING_1_HTML + System.lineSeparator() + HTML_END, result2, "Invalid html");
    }
}