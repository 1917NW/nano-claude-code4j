import com.lxy.skills.SkillDetail;
import com.lxy.skills.SkillMarkdownParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestSkillMarkdownParser {

    @Test
    public void shouldParseMetaAndBodyFromSkillMarkdown() throws IOException {
        String markdown = readResource("/.claude/skills/pdf/SKILL.md");

        SkillDetail detail = SkillMarkdownParser.parse(markdown);

        Assert.assertEquals("pdf", detail.getMetaInfo().getName());
        Assert.assertTrue(detail.getMetaInfo().getDescription().contains("Process PDF files"));
        Assert.assertTrue(detail.getSkillBody().startsWith("# PDF Processing Skill"));
        Assert.assertFalse(detail.getSkillBody().startsWith("---"));
    }

    @Test
    public void shouldReturnWholeMarkdownAsBodyWhenFrontmatterMissing() {
        String markdown = "# Title\n\nplain body";

        SkillDetail detail = SkillMarkdownParser.parse(markdown);

        Assert.assertNull(detail.getMetaInfo().getName());
        Assert.assertEquals(markdown, detail.getSkillBody());
    }

    private String readResource(String path) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + path);
            }
            byte[] bytes = new byte[inputStream.available()];
            int read = inputStream.read(bytes);
            if (read < 0) {
                throw new IOException("Failed to read resource: " + path);
            }
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}
