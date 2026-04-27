import cn.hutool.json.JSONUtil;
import com.lxy.skills.SkillMetaInfo;
import com.lxy.skills.SkillRegistry;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestSkillRegistry {

    @Test
    public void shouldLoadAllSkillMarkdownFiles() {
        List<SkillMetaInfo> metaInfos = SkillRegistry.getSkillMetaInfo();

        System.out.println(JSONUtil.toJsonStr(metaInfos));

        for(SkillMetaInfo smi : metaInfos) {
            System.out.println(SkillRegistry.getSkillBody(smi.getName()));
        }
    }
}
