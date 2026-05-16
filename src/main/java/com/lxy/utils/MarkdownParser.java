package com.lxy.utils;

import com.lxy.common.dto.MarkdownInfo;
import com.lxy.skills.SkillDetail;
import com.lxy.skills.SkillMetaInfo;

import java.util.LinkedHashMap;
import java.util.Map;

public class MarkdownParser {
    private static final String FRONTMATTER_DELIMITER = "---";

    private static final String FRONTMATTER_END = "\n" + FRONTMATTER_DELIMITER + "\n";


    private MarkdownParser() {
    }

    public static MarkdownInfo parse(String markdownContent) {
        if (markdownContent == null) {
            throw new IllegalArgumentException("skillMarkdown cannot be null");
        }

        String normalized = normalizeLineEndings(markdownContent);
        SkillMetaInfo metaInfo = new SkillMetaInfo();
        String body = normalized;

        MarkdownInfo markdownInfo = new MarkdownInfo();
        if (normalized.startsWith(FRONTMATTER_DELIMITER)) {
            int bodyStart = findFrontmatterEnd(normalized);
            if (bodyStart < 0) {
                throw new IllegalArgumentException("Invalid SKILL.md: missing closing frontmatter delimiter");
            }

            String frontmatter = normalized.substring(FRONTMATTER_DELIMITER.length(), bodyStart).trim();
            markdownInfo.setHeaders(parseMeta(frontmatter));
            body = normalized.substring(bodyStart + FRONTMATTER_END.length());
            while (body.startsWith("\n")) {
                body = body.substring(1);
            }
            markdownInfo.setContent(body);
        }

        return markdownInfo;
    }

    private static Map<String, String> parseMeta(String frontmatter) {
        Map<String, String> attributes = new LinkedHashMap<>();
        if (!frontmatter.isEmpty()) {
            String[] lines = frontmatter.split("\n");
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                int separatorIndex = trimmed.indexOf(':');
                if (separatorIndex <= 0) {
                    continue;
                }

                String key = trimmed.substring(0, separatorIndex).trim();
                String value = trimmed.substring(separatorIndex + 1).trim();
                attributes.put(key, stripOptionalQuotes(value));
            }
        }

        return attributes;
    }

    private static int findFrontmatterEnd(String markdown) {
        return markdown.indexOf(FRONTMATTER_END, FRONTMATTER_DELIMITER.length());
    }

    private static String stripOptionalQuotes(String value) {
        if (value.length() >= 2) {
            boolean doubleQuoted = value.startsWith("\"") && value.endsWith("\"");
            boolean singleQuoted = value.startsWith("'") && value.endsWith("'");
            if (doubleQuoted || singleQuoted) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }

    private static String normalizeLineEndings(String markdown) {
        return markdown.replace("\r\n", "\n").replace('\r', '\n');
    }
}
