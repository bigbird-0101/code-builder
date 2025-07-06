package io.github.bigbird0101.code.fxui.fx.bean;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public class PageInputSnapshot {

    private String currentMultipleTemplate;
    private List<String> useMultipleTemplateSelected;
    private List<String> unUseMultipleTemplateSelected;
    private String useMultipleTemplateTopicOne;
    private String unUseMultipleTemplateTopicOne;
    private String tableNames;
    private Boolean selectTableAll;
    private Boolean definedFunction;
    private String representFactor;
    private String fields;
    private Map<String, Map<String, List<String>>> selectTemplateGroup;

    public String getTableNames() {
        return tableNames;
    }

    public void setTableNames(String tableNames) {
        this.tableNames = tableNames;
    }

    public Boolean getSelectTableAll() {
        return selectTableAll;
    }

    public void setSelectTableAll(Boolean selectTableAll) {
        this.selectTableAll = selectTableAll;
    }

    public Boolean getDefinedFunction() {
        return definedFunction;
    }

    public void setDefinedFunction(Boolean definedFunction) {
        this.definedFunction = definedFunction;
    }

    public String getRepresentFactor() {
        return representFactor;
    }

    public void setRepresentFactor(String representFactor) {
        this.representFactor = representFactor;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public Map<String, Map<String, List<String>>> getSelectTemplateGroup() {
        return selectTemplateGroup;
    }

    public void setSelectTemplateGroup(Map<String, Map<String, List<String>>> selectTemplateGroup) {
        this.selectTemplateGroup = selectTemplateGroup;
    }

    public String getCurrentMultipleTemplate() {
        return currentMultipleTemplate;
    }

    public void setCurrentMultipleTemplate(String currentMultipleTemplate) {
        this.currentMultipleTemplate = currentMultipleTemplate;
    }

    public List<String> getUseMultipleTemplateSelected() {
        return useMultipleTemplateSelected;
    }

    public void setUseMultipleTemplateSelected(List<String> useMultipleTemplateSelected) {
        this.useMultipleTemplateSelected = useMultipleTemplateSelected;
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

    public static final class Builder {
        private final PageInputSnapshot pageInputSnapshot;

        private Builder() {
            pageInputSnapshot = new PageInputSnapshot();
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withCurrentMultipleTemplate(String currentMultipleTemplate) {
            pageInputSnapshot.setCurrentMultipleTemplate(currentMultipleTemplate);
            return this;
        }

        public Builder withUseMultipleTemplateSelected(List<String> useMultipleTemplateSelected) {
            pageInputSnapshot.setUseMultipleTemplateSelected(useMultipleTemplateSelected);
            return this;
        }

        public Builder withUnUseMultipleTemplateSelected(List<String> unUseMultipleTemplateSelected) {
            pageInputSnapshot.setUnUseMultipleTemplateSelected(unUseMultipleTemplateSelected);
            return this;
        }

        public Builder withUseMultipleTemplateTopicOne(String useMultipleTemplateTopicOne) {
            pageInputSnapshot.setUseMultipleTemplateTopicOne(useMultipleTemplateTopicOne);
            return this;
        }

        public Builder withUnUseMultipleTemplateTopicOne(String unUseMultipleTemplateTopicOne) {
            pageInputSnapshot.setUnUseMultipleTemplateTopicOne(unUseMultipleTemplateTopicOne);
            return this;
        }

        public Builder withTableNames(String tableNames) {
            pageInputSnapshot.setTableNames(tableNames);
            return this;
        }

        public Builder withSelectTableAll(Boolean selectTableAll) {
            pageInputSnapshot.setSelectTableAll(selectTableAll);
            return this;
        }

        public Builder withDefinedFunction(Boolean definedFunction) {
            pageInputSnapshot.setDefinedFunction(definedFunction);
            return this;
        }

        public Builder withRepresentFactor(String representFactor) {
            pageInputSnapshot.setRepresentFactor(representFactor);
            return this;
        }

        public Builder withFields(String fields) {
            pageInputSnapshot.setFields(fields);
            return this;
        }

        public Builder withSelectTemplateGroup(Map<String, Map<String, List<String>>> selectTemplateGroup) {
            pageInputSnapshot.setSelectTemplateGroup(selectTemplateGroup);
            return this;
        }

        public PageInputSnapshot build() {
            return pageInputSnapshot;
        }
    }
}
