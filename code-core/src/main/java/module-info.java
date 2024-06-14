/**
 * @author bigbird-0101
 * @date 2024-06-14 22:25
 */
open module code.core {
    exports io.github.bigbird0101.code.core.share;
    exports io.github.bigbird0101.code.core.template;
    exports io.github.bigbird0101.code.core.config;
    exports io.github.bigbird0101.code.core.context;
    exports io.github.bigbird0101.code.core.context.aware;
    exports io.github.bigbird0101.code.core.common;
    exports io.github.bigbird0101.code.core.domain;
    exports io.github.bigbird0101.code.core.exception;
    exports io.github.bigbird0101.code.core.factory;
    exports io.github.bigbird0101.code.core.factory.config;
    exports io.github.bigbird0101.code.core.filebuilder;
    exports io.github.bigbird0101.code.core.filebuilder.definedfunction;
    exports io.github.bigbird0101.code.core.template.targetfile;
    exports io.github.bigbird0101.code.core.template.variable.resource;
    exports io.github.bigbird0101.code.core.event;
    exports io.github.bigbird0101.code.core.cache;
    requires hutool.core;
    requires hutool.json;
    requires hutool.cache;
    requires hutool.log;
    requires hutool.system;
    requires hutool.http;
    requires fastjson;
    requires code.common;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires code.spi;
    requires org.apache.commons.io;
    requires jdk.httpserver;

    uses io.github.bigbird0101.code.core.template.Template;
    uses io.github.bigbird0101.code.core.common.TableNameToDomainName;
    uses io.github.bigbird0101.code.core.event.BasicCodeListener;
    uses io.github.bigbird0101.code.core.factory.config.TemplatePostProcessor;
    uses io.github.bigbird0101.code.core.filebuilder.definedfunction.DefinedFunctionResolverRule;
    uses io.github.bigbird0101.code.core.filebuilder.definedfunction.RepresentFactorReplaceRule;
    uses io.github.bigbird0101.code.core.template.domnode.DomScriptCodeNodeBuilder.CodeNodeHandler;
    uses io.github.bigbird0101.code.core.template.targetfile.TargetFilePrefixNameStrategy;
    uses io.github.bigbird0101.code.core.template.TemplateLangResolver;
    uses io.github.bigbird0101.code.core.template.variable.resource.TemplateVariableResource;
}