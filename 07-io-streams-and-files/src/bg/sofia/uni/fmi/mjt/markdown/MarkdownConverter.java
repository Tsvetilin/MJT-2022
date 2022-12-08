package bg.sofia.uni.fmi.mjt.markdown;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MarkdownConverter implements MarkdownConverterAPI {

    private static final Map<String, String> CONVERTS;
    private static final String HTML_START = "<html>" + System.lineSeparator() + "<body>";
    private static final String HTML_END = "</body>" + System.lineSeparator() + "</html>";

    static {
        CONVERTS = new HashMap<>();
        CONVERTS.put("\\*\\*", "strong");
        CONVERTS.put("\\*", "em");
        CONVERTS.put("`", "code");
    }

    private String replaceBounded(String content, String mdTag, String htmlTag) {
        content = content.replaceFirst(mdTag, "<" + htmlTag + ">");
        content = content.replaceFirst(mdTag, "</" + htmlTag + ">");
        return content;
    }

    private String surroundWithTag(String content, String htmlTag) {
        return "<" + htmlTag + ">" + content + "</" + htmlTag + ">";
    }

    private String replaceHeading(String content) {
        content = content.trim();

        if (content.isEmpty() || content.isBlank()) {
            return content;
        }

        int i = 0;
        while (content.charAt(i) == '#') {
            ++i;
        }

        return i > 0 ? surroundWithTag(content.substring(i + 1), "h" + i) : content;
    }

    @Override
    public void convertMarkdown(Reader source, Writer output) {
        try (var reader = new BufferedReader(source);
             var writer = new BufferedWriter(output)) {
            String line;
            writer.write(HTML_START);
            writer.write(System.lineSeparator());
            while ((line = reader.readLine()) != null) {
                line = replaceHeading(line);
                for (var entry : CONVERTS.entrySet()) {
                    line = replaceBounded(line, entry.getKey(), entry.getValue());
                }
                writer.write(line);
                writer.write(System.lineSeparator());
                writer.flush();
            }
            writer.write(HTML_END);
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file", e);
        }
    }

    @Override
    public void convertMarkdown(Path from, Path to) {
        Path parent = to.getParent();
        if (parent != null && !Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new IllegalStateException("A problem occurred while creating a directory.", e);
            }
        }

        try (var bufferedReader = Files.newBufferedReader(from);
             var bufferedWriter = Files.newBufferedWriter(to)) {
            convertMarkdown(bufferedReader, bufferedWriter);
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file", e);
        }
    }

    @Override
    public void convertAllMarkdownFiles(Path sourceDir, Path targetDir) {

        for (final File fileEntry : sourceDir.toFile().listFiles()) {
            if (!fileEntry.isDirectory()) {
                convertMarkdown(fileEntry.toPath(),
                        Path.of(targetDir.toString(),
                                fileEntry.getName().replace(".md", ".html")));
            }
        }

    }
}
