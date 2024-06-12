package io.github.bigbird0101.code.core.share;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.http.server.SimpleServer;
import cn.hutool.http.server.action.Action;
import com.alibaba.fastjson.JSONObject;
import io.github.bigbird0101.code.core.config.Resource;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.core.exception.NoSuchTemplateDefinitionException;
import io.github.bigbird0101.code.core.factory.DefaultListableTemplateFactory;
import io.github.bigbird0101.code.core.factory.RootTemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;
import io.github.bigbird0101.code.core.template.AbstractTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static io.github.bigbird0101.code.core.share.ShareConstant.DEPEND_TEMPLATE_MAPS;
import static io.github.bigbird0101.code.core.share.ShareConstant.TEMPLATE_CONTENT;
import static io.github.bigbird0101.code.core.share.ShareConstant.TEMPLATE_MAPS;
import static io.github.bigbird0101.code.core.share.ShareConstant.TEMPLATE_NAME;

/**
 * @author bigbird-0101
 * @date 2024-06-08 22:02
 */
public class ShareServer extends AbstractTemplateContextProvider {
    private static final Logger LOGGER = LogManager.getLogger(AbstractTemplate.class);

    private SimpleServer server;
    private int port;

    public void init() {
        port = 4321;
        int property = getTemplateContext().getEnvironment().getPropertyOrDefault("share.server.port",
                int.class, 4321);
        server = HttpUtil.createServer(port);
        RuntimeUtil.addShutdownHook(this::destroy);
    }

    public String getUrl() {
        return "http://" + NetUtil.getLocalhostStr() + ":" + port;
    }

    public String getTemplateShareUrl(String templateName) {
        return getUrl() + ShareConstant.TEMPLATE + "?t=" + templateName;
    }

    public String getMultipleTemplateShareUrl(String multipleTemplateName) {
        return getUrl() + ShareConstant.MULTIPLE_TEMPLATE + "?t=" + multipleTemplateName;
    }

    public void start() {
        GenericAction genericAction = new GenericAction();
        server.addAction(ShareConstant.TEMPLATE, genericAction);
        server.addAction(ShareConstant.MULTIPLE_TEMPLATE, genericAction);
        server.start();
    }

    public void destroy() {
        server.getRawServer().stop(1000);
    }

    class GenericAction implements Action {
        private final GenericTemplateContext templateContext = (GenericTemplateContext) getTemplateContext();
        private final  DefaultListableTemplateFactory factory = templateContext.getTemplateFactory();
        @Override
        public void doAction(HttpServerRequest request, HttpServerResponse response) {
            String path = request.getPath();
            String t = request.getParam("t");
            response.setContentType("application/json");
            if (StrUtil.isBlank(t)) {
                response.sendError(400, "t param is not empty");
                return;
            }
            try {
                if (path.startsWith(ShareConstant.TEMPLATE)) {
                    TemplateDefinition templateDefinition = factory.getTemplateDefinition(t);
                    if (null == templateDefinition) {
                        response.sendError(404, String.format("%s template is not find", t));
                        return;
                    }
                    JSONObject json = getTemplateJson(templateDefinition, t);
                    response.write(json.toString());
                } else if (path.startsWith(ShareConstant.MULTIPLE_TEMPLATE)) {
                    MultipleTemplateDefinition multipleTemplateDefinition = factory
                            .getMultipleTemplateDefinition(t);
                    if (null == multipleTemplateDefinition) {
                        response.sendError(404, String.format("%s multiple template is not find", t));
                        return;
                    }
                    JSONObject json = (JSONObject) JSONObject.toJSON(multipleTemplateDefinition);
                    JSONObject templateMaps=new JSONObject();
                    json.put(TEMPLATE_MAPS, templateMaps);
                    json.put(TEMPLATE_NAME, t);
                    factory.getTemplateNames()
                            .forEach(s -> templateMaps.put(s, getTemplateJson(factory.getTemplateDefinition(s), s)));
                    response.write(json.toString());
                }
            } catch (NoSuchTemplateDefinitionException exception) {
                response.sendError(404, String.format("%s template is not find", t));
            } catch (Exception exception){
                LOGGER.error("{} template is error",t,exception);
                response.sendError(500, String.format("%s template is error %s", t,exception.getMessage()));
            }
        }

        private JSONObject getTemplateJson(TemplateDefinition templateDefinition, String templateName) {
            JSONObject json = getTemplateJson(templateDefinition);
            JSONObject dependTemplates = new JSONObject();
            if (templateDefinition instanceof RootTemplateDefinition) {
                RootTemplateDefinition rootTemplateDefinition = (RootTemplateDefinition) templateDefinition;
                if (CollUtil.isNotEmpty(rootTemplateDefinition.getDependTemplates())) {
                    rootTemplateDefinition.getDependTemplates().forEach(s -> {
                        TemplateDefinition dependTemplateDefinition = factory.getTemplateDefinition(s);
                        JSONObject templateJson = getTemplateJson(dependTemplateDefinition, s);
                        dependTemplates.put(s, templateJson);
                    });
                }
            }
            if (!dependTemplates.isEmpty()) {
                json.put(DEPEND_TEMPLATE_MAPS, dependTemplates);
            }
            json.put(TEMPLATE_NAME, templateName);
            return json;
        }

        private JSONObject getTemplateJson(TemplateDefinition templateDefinition) {
            Resource templateResource = templateDefinition.getTemplateResource();
            JSONObject json = (JSONObject) JSONObject.toJSON(templateDefinition);
            try {
                json.put(TEMPLATE_CONTENT, templateResource.readUtf8Str());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return json;
        }
    }
}
