package com.fpp.code.fxui.fx.bean;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public class PageInputSnapshot {

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


    public static final class PageInputSnapshotBuilder {
        private PageInputSnapshot pageInputSnapshot;

        private PageInputSnapshotBuilder() {
            pageInputSnapshot = new PageInputSnapshot();
        }

        public static PageInputSnapshotBuilder aPageInputSnapshot() {
            return new PageInputSnapshotBuilder();
        }

        public PageInputSnapshotBuilder withTableNames(String tableNames) {
            pageInputSnapshot.setTableNames(tableNames);
            return this;
        }

        public PageInputSnapshotBuilder withSelectTableAll(Boolean selectTableAll) {
            pageInputSnapshot.setSelectTableAll(selectTableAll);
            return this;
        }

        public PageInputSnapshotBuilder withDefinedFunction(Boolean definedFunction) {
            pageInputSnapshot.setDefinedFunction(definedFunction);
            return this;
        }

        public PageInputSnapshotBuilder withRepresentFactor(String representFactor) {
            pageInputSnapshot.setRepresentFactor(representFactor);
            return this;
        }

        public PageInputSnapshotBuilder withFields(String fields) {
            pageInputSnapshot.setFields(fields);
            return this;
        }

        public PageInputSnapshotBuilder withSelectTemplateGroup(Map<String, Map<String, List<String>>> selectTemplateGroup) {
            pageInputSnapshot.setSelectTemplateGroup(selectTemplateGroup);
            return this;
        }

        public PageInputSnapshotBuilder but() {
            return aPageInputSnapshot().withTableNames(pageInputSnapshot.getTableNames()).withSelectTableAll(pageInputSnapshot.getSelectTableAll()).withDefinedFunction(pageInputSnapshot.getDefinedFunction()).withRepresentFactor(pageInputSnapshot.getRepresentFactor()).withFields(pageInputSnapshot.getFields()).withSelectTemplateGroup(pageInputSnapshot.getSelectTemplateGroup());
        }

        public PageInputSnapshot build() {
            return pageInputSnapshot;
        }
    }
}
