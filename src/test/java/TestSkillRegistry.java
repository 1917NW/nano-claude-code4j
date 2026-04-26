import com.lxy.skills.SkillMetaInfo;
import com.lxy.skills.SkillRegistry;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestSkillRegistry {

    @Test
    public void shouldLoadAllSkillMarkdownFiles() {
        List<SkillMetaInfo> metaInfos = SkillRegistry.getSkillMetaInfo();

        Assert.assertEquals(2, metaInfos.size());
        Assert.assertTrue(metaInfos.stream().anyMatch(meta -> "pdf".equals(meta.getName())));
        Assert.assertTrue(metaInfos.stream().anyMatch(meta -> "code-review".equals(meta.getName())));
        Assert.assertNotNull(SkillRegistry.getSkillBody("pdf"));
        Assert.assertNotNull(SkillRegistry.getSkillBody("code-review"));
    }
}
