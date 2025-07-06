package io.github.bigbird0101.code.fxui.fx.cache;

import cn.hutool.core.collection.CollUtil;
import io.github.bigbird0101.code.fxui.fx.bean.PageInputSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserOperateCache {
    private String templateNameSelected;
    private List<String> useMultipleTemplateSelected;
    private String useMultipleTemplateTopicOne;
    private String unUseMultipleTemplateTopicOne;
    private List<String> unUseMultipleTemplateSelected;

    public String getTemplateNameSelected() {
        return templateNameSelected;
    }

    public void setTemplateNameSelected(String templateNameSelected) {
        this.templateNameSelected = templateNameSelected;
    }

    public List<String> getUseMultipleTemplateSelected() {
        return useMultipleTemplateSelected;
    }

    public void setUseMultipleTemplateSelected(List<String> useMultipleTemplateSelected) {
        this.useMultipleTemplateSelected = useMultipleTemplateSelected;
    }

    public void addUseMultipleTemplateSelected(String multipleTemplateName) {
        if (CollUtil.isEmpty(this.useMultipleTemplateSelected)) {
            this.useMultipleTemplateSelected = new ArrayList<>();
        }
        if (CollUtil.isEmpty(this.unUseMultipleTemplateSelected)) {
            this.unUseMultipleTemplateSelected = new ArrayList<>();
        }
        this.unUseMultipleTemplateSelected.remove(multipleTemplateName);
        this.useMultipleTemplateSelected.add(multipleTemplateName);
    }

    public void addNoUseMultipleTemplateSelected(String multipleTemplateName) {
        if (CollUtil.isEmpty(this.useMultipleTemplateSelected)) {
            this.useMultipleTemplateSelected = new ArrayList<>();
        }
        if (CollUtil.isEmpty(this.unUseMultipleTemplateSelected)) {
            this.unUseMultipleTemplateSelected = new ArrayList<>();
        }
        this.useMultipleTemplateSelected.remove(multipleTemplateName);
        this.unUseMultipleTemplateSelected.add(multipleTemplateName);
    }

    public List<String> getUnUseMultipleTemplateSelected() {
        return unUseMultipleTemplateSelected;
    }

    public void setUnUseMultipleTemplateSelected(List<String> unUseMultipleTemplateSelected) {
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
