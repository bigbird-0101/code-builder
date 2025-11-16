package io.github.bigbird0101.code.fxui.fx.cache;

import cn.hutool.core.collection.CollUtil;
import io.github.bigbird0101.code.fxui.fx.bean.PageInputSnapshot;

import java.util.LinkedHashSet;

/**
 * @author Lily
 */
public class UserOperateCache {
    private String templateNameSelected;
    private LinkedHashSet<String> useMultipleTemplateSelected;
    private String useMultipleTemplateTopicOne;
    private String unUseMultipleTemplateTopicOne;
    private LinkedHashSet<String> unUseMultipleTemplateSelected;

    public String getTemplateNameSelected() {
        return templateNameSelected;
    }

    public void setTemplateNameSelected(String templateNameSelected) {
        this.templateNameSelected = templateNameSelected;
    }

    public LinkedHashSet<String> getUseMultipleTemplateSelected() {
        return useMultipleTemplateSelected;
    }

    public void setUseMultipleTemplateSelected(LinkedHashSet<String> useMultipleTemplateSelected) {
        this.useMultipleTemplateSelected = useMultipleTemplateSelected;
    }

    public void addUseMultipleTemplateSelected(String multipleTemplateName) {
        if (CollUtil.isEmpty(this.useMultipleTemplateSelected)) {
            this.useMultipleTemplateSelected = new LinkedHashSet<>();
        }
        if (CollUtil.isEmpty(this.unUseMultipleTemplateSelected)) {
            this.unUseMultipleTemplateSelected = new LinkedHashSet<>();
        }
        this.unUseMultipleTemplateSelected.remove(multipleTemplateName);
        this.useMultipleTemplateSelected.add(multipleTemplateName);
    }

    public void addNoUseMultipleTemplateSelected(String multipleTemplateName) {
        if (CollUtil.isEmpty(this.useMultipleTemplateSelected)) {
            this.useMultipleTemplateSelected = new LinkedHashSet<>();
        }
        if (CollUtil.isEmpty(this.unUseMultipleTemplateSelected)) {
            this.unUseMultipleTemplateSelected = new LinkedHashSet<>();
        }
        this.useMultipleTemplateSelected.remove(multipleTemplateName);
        this.unUseMultipleTemplateSelected.add(multipleTemplateName);
    }

    public LinkedHashSet<String> getUnUseMultipleTemplateSelected() {
        return unUseMultipleTemplateSelected;
    }

    public void setUnUseMultipleTemplateSelected(LinkedHashSet<String> unUseMultipleTemplateSelected) {
        this.unUseMultipleTemplateSelected = unUseMultipleTemplateSelected;
    }

    public String getUseMultipleTemplateTopicOne() {
        return useMultipleTemplateTopicOne;
    }

    public void setUseMultipleTemplateTopicOne(String useMultipleTemplateTopicOne) {
        this.useMultipleTemplateTopicOne = useMultipleTemplateTopicOne;
    }

    public String getUnUseMultipleTemplateTopicOne() {
        return unUseMultipleTemplateTopicOne;
    }

    public void setUnUseMultipleTemplateTopicOne(String unUseMultipleTemplateTopicOne) {
        this.unUseMultipleTemplateTopicOne = unUseMultipleTemplateTopicOne;
    }

    public void init(PageInputSnapshot pageInputSnapshot) {
        if (null == pageInputSnapshot) {
            return;
        }
        this.templateNameSelected = pageInputSnapshot.getCurrentMultipleTemplate();
        this.unUseMultipleTemplateSelected = pageInputSnapshot.getUnUseMultipleTemplateSelected();
        this.useMultipleTemplateSelected = pageInputSnapshot.getUseMultipleTemplateSelected();
        this.unUseMultipleTemplateTopicOne = pageInputSnapshot.getUnUseMultipleTemplateTopicOne();
        this.useMultipleTemplateTopicOne = pageInputSnapshot.getUseMultipleTemplateTopicOne();
    }
}
