package io.github.bigbird0101.code.fxui.loadsmart;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 智能模板配置生成器
 * 功能：
 * - 前缀相同 → 同一个 multipleTemplates
 * - 自动去重 Copy/Copy1 文件
 * - 从历史配置学习 module/package/dependencies/prefixStrategy
 * - 注入默认 templateClassName
 *
 * @author bigbird0101
 */
public class CorrectSmartConfigGenerator {
    private static final Logger logger = LogManager.getLogger(CorrectSmartConfigGenerator.class);

    private final String templateDir;
    private final String projectRoot;

    // 知识库：用于自学习
    private final KnowledgeBase knowledge = new KnowledgeBase();

    public CorrectSmartConfigGenerator(String templateDir, String projectRoot) {
        this.templateDir = templateDir;
        this.projectRoot = projectRoot == null ? inferProjectRoot(templateDir) : projectRoot;
        learnFromHistory();
    }

    // ==================== 主入口 ====================

    public JSONObject generate() {
        File dir = new File(templateDir);
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Template directory does not exist: " + templateDir);
            return new JSONObject();
        }

        File[] files = dir.listFiles(f -> f.getName().endsWith(".template"));
        if (files == null || files.length == 0) {
            System.out.println("No .template files found in: " + templateDir);
            return new JSONObject();
        }

        // 按 prefix 分组，用于构建 multipleTemplates
        Map<String, List<TemplateInfo>> groupedByPrefix = new HashMap<>();
        // 去重 key: prefix-suffix
        Set<String> emitted = new HashSet<>();
        JSONArray templatesArray = new JSONArray();

        for (File file : files) {
            TemplateInfo info = parseTemplateFile(file);

            groupedByPrefix.computeIfAbsent(info.prefix, k -> new ArrayList<>()).add(info);

            // 仅输出非 Copy 文件
            if (!emitted.contains(info.uniqueKey)) {
                templatesArray.add(buildTemplateConfig(info));
                emitted.add(info.uniqueKey);
            }
        }

        // 构建 multipleTemplates
        JSONArray multipleTemplates = groupedByPrefix.entrySet().stream()
                .map(e -> {
                    JSONObject obj = new JSONObject();
                    obj.put("name", e.getKey()); // 组合模板名 = 前缀
                    JSONArray tplNames = e.getValue().stream()
                            .map(t -> t.prefix + "-" + t.suffix)
                            .distinct()
                            .sorted()
                            .collect(JSONArray::new, JSONArray::add, JSONArray::addAll);
                    obj.put("templates", tplNames);
                    return obj;
                })
                .collect(JSONArray::new, JSONArray::add, JSONArray::addAll);

        // 返回最终 JSON
        JSONObject result = new JSONObject();
        result.put("multipleTemplates", multipleTemplates);
        result.put("templates", templatesArray);

        return result;
    }

    // ==================== 解析文件名 ====================

    private TemplateInfo parseTemplateFile(File file) {
        String rawName = file.getName().replaceFirst("\\.template$", "");
        // 去除 Copy / Copy1
        String cleanName = rawName.replaceAll("Copy\\d*$", "");

        // 尝试用已知模式匹配
        for (NamingPattern pattern : knowledge.namingPatterns) {
            Map<String, String> parts = pattern.match(cleanName);
            if (parts != null) {
                String prefix = parts.get("prefix");
                String suffix = parts.get("suffix");
                return new TemplateInfo(file, prefix, suffix, rawName, prefix + "-" + suffix);
            }
        }

        // fallback：启发式解析
        return heuristicParse(cleanName, file, rawName);
    }

    private TemplateInfo heuristicParse(String name, File file, String rawName) {
        String[] separators = {"-", "_", "\\."};
        for (String sep : separators) {
            int lastSep = name.lastIndexOf(sep);
            if (lastSep > 0 && lastSep < name.length() - 1) {
                String prefix = name.substring(0, lastSep);
                String suffix = name.substring(lastSep + 1);
                if (prefix.length() >= 2 && suffix.length() >= 2) {
                    return new TemplateInfo(file, prefix, suffix, rawName, prefix + "-" + suffix);
                }
            }
        }
        // 默认
        return new TemplateInfo(file, "unknown", name, rawName, "unknown-" + name);
    }

    // ==================== 构建模板配置 ====================

    private JSONObject buildTemplateConfig(TemplateInfo info) {
        JSONObject tpl = new JSONObject();

        tpl.put("fileName", info.file.getName());
        tpl.put("name", info.prefix + "-" + info.suffix);
        tpl.put("fileSuffixName", "java");
        tpl.put("sourcesRoot", "src/main/java");
        tpl.put("projectUrl", this.projectRoot);

        // 学习 module 和 package
        TemplateRule rule = knowledge.rules.getOrDefault(info.prefix, new TemplateRule());
        tpl.put("module", rule.module != null ? rule.module : "/default-module-" + info.prefix);
        tpl.put("srcPackage", rule.pkg != null ? rule.pkg.replace(".", "/") : "unknown/package");

        // 依赖
        JSONArray deps = knowledge.dependencies.getOrDefault(info.prefix, new JSONArray());
        if (!deps.isEmpty()) {
            tpl.put("dependTemplates", deps);
        }

        // 前缀策略
        JSONObject prefixStrategy = knowledge.prefixStrategies.getOrDefault(
                info.prefix,
                buildDefaultPrefixStrategy(info.suffix)
        );
        tpl.put("filePrefixNameStrategy", prefixStrategy);

        // 默认 templateClassName
        String defaultClassName = "io.github.bigbird0101.code.core.template.HaveDependTemplateNoHandleFunctionTemplate";
        if (!tpl.containsKey("templateClassName")) {
            tpl.put("templateClassName", defaultClassName);
        }

        return tpl;
    }

    private JSONObject buildDefaultPrefixStrategy(String suffix) {
        JSONObject s = new JSONObject();
        s.put("value", 3);
        s.put("pattern", "*{tableInfo.domainName}*" + suffix);
        return s;
    }

    // ==================== 学习历史配置 ====================

    private void learnFromHistory() {
        File root = new File(this.projectRoot);
        if (!root.exists() || !root.isDirectory()) {
            return;
        }

        Collection<File> jsonFiles = FileUtils.listFiles(root, new String[]{"json"}, true);
        for (File file : jsonFiles) {
            try {
                String content = Files.readString(file.toPath());
                JSONObject config = JSON.parseObject(content);
                learnFromConfig(config);
            } catch (Exception e) {
                logger.error("Failed to parse history config: ", e);
            }
        }
    }

    private void learnFromConfig(JSONObject config) {
        if (!config.containsKey("templates")) {
            return;
        }
        JSONArray templates = config.getJSONArray("templates");
        for (int i = 0; i < templates.size(); i++) {
            JSONObject t = templates.getJSONObject(i);
            String name = t.getString("name");
            if (name == null || !name.contains("-")) {
                continue;
            }

            int lastDash = name.lastIndexOf('-');
            String prefix = name.substring(0, lastDash);

            // 学习 module 和 package
            String module = t.getString("module");
            String pkg = t.getString("srcPackage");
            TemplateRule rule = knowledge.rules.computeIfAbsent(prefix, k -> new TemplateRule());
            if (module != null) {
                rule.module = module;
            }
            if (pkg != null) {
                rule.pkg = pkg.replace("/", ".");
            }

            // 学习依赖
            JSONArray deps = t.getJSONArray("dependTemplates");
            if (deps != null) {
                knowledge.dependencies.put(prefix, deps);
            }

            // 学习前缀策略
            JSONObject strategy = t.getJSONObject("filePrefixNameStrategy");
            if (strategy != null) {
                knowledge.prefixStrategies.put(prefix, strategy);
            }
        }
    }

    // ==================== 工具方法 ====================

    private String inferProjectRoot(String templateDir) {
        File dir = new File(templateDir);
        while (dir != null) {
            if (new File(dir, "pom.xml").exists() || new File(dir, ".git").isDirectory()) {
                return dir.getAbsolutePath();
            }
            dir = dir.getParentFile();
        }
        return new File(templateDir).getParent();
    }

    // ==================== 内部类 ====================

    /**
     * @param uniqueKey 用于去重
     */
    record TemplateInfo(File file, String prefix, String suffix, String rawName, String uniqueKey) {
    }

    static class TemplateRule {
        String module;
        String pkg;
    }

    static class NamingPattern {
        final Pattern regex;
        final int prefixGroupIndex;
        final int suffixGroupIndex;

        NamingPattern(String regex, int prefixGroupIndex, int suffixGroupIndex) {
            this.regex = Pattern.compile(regex);
            this.prefixGroupIndex = prefixGroupIndex;
            this.suffixGroupIndex = suffixGroupIndex;
        }

        Map<String, String> match(String input) {
            Matcher m = regex.matcher(input);
            if (m.matches()) {
                Map<String, String> map = new HashMap<>();
                map.put("prefix", m.group(prefixGroupIndex));
                map.put("suffix", m.group(suffixGroupIndex));
                return map;
            }
            return null;
        }

        @Override
        public boolean equals(Object object) {
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            NamingPattern that = (NamingPattern) object;
            return prefixGroupIndex == that.prefixGroupIndex && suffixGroupIndex == that.suffixGroupIndex && Objects.equals(regex, that.regex);
        }

        @Override
        public int hashCode() {
            return Objects.hash(regex, prefixGroupIndex, suffixGroupIndex);
        }
    }

    static class KnowledgeBase {
        final Map<String, TemplateRule> rules = new HashMap<>();
        final Map<String, JSONArray> dependencies = new HashMap<>();
        final Map<String, JSONObject> prefixStrategies = new HashMap<>();
        final Set<NamingPattern> namingPatterns = new HashSet<>();

        KnowledgeBase() {
            namingPatterns.add(new NamingPattern("^(.+)-(.+)$", 1, 2));
            namingPatterns.add(new NamingPattern("^(.+)_(.+)$", 1, 2));
            namingPatterns.add(new NamingPattern("^(.+)\\.(.+)$", 1, 2));
        }
    }
}