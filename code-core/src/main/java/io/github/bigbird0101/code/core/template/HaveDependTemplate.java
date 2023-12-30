package io.github.bigbird0101.code.core.template;

import java.util.Objects;
import java.util.Set;

/**
 * 有依赖的模板
 * @author Administrator
 */
public interface HaveDependTemplate extends Template {
    /**
     * 获取依赖的模板名
     * @return
     */
    Set<DependTemplate> getDependTemplates();

    class DependTemplate{
        private Integer index;
        private String templateName;
        public DependTemplate() {
        }

        public DependTemplate(Integer index, String templateName) {
            this.index = index;
            this.templateName = templateName;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getTemplateName() {
            return templateName;
        }

        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DependTemplate that = (DependTemplate) o;
            return index.equals(that.index) && templateName.equals(that.templateName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, templateName);
        }

        @Override
        public String toString() {
            return "DependTemplate{" +
                    "index=" + index +
                    ", templateName='" + templateName + '\'' +
                    '}';
        }
    }


}
