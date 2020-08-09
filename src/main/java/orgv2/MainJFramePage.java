package main.java.orgv2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import main.java.common.DbUtil;
import main.java.common.Utils;
import main.java.config.CodeConfigException;
import main.java.config.DataSourceFileConfig;
import main.java.config.ProjectFileConfig;
import main.java.domain.CoreConfig;
import main.java.domain.DataSourceConfig;
import main.java.filebuilder.DefaultFileBuilder;
import main.java.filebuilder.FileAppendSuffixCodeBuilderStrategy;
import main.java.filebuilder.FileBuilder;
import main.java.filebuilder.FileCodeBuilderStrategy;
import main.java.filebuilder.definedfunction.DefaultDefinedFunctionResolver;
import main.java.template.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Administrator
 */
public class MainJFramePage extends JFrame {
    /**
     * 数据源配置
     */
    private DataSourceFileConfig dataSourceFileConfig;
    /**
     * 项目文件配额制
     */
    private ProjectFileConfig projectFileConfig;

    /**
     * 选中的组合模板
     */
    private MultipleTemplate targetMultipleTemplate;

    /**
     * 所有的表格
     */
    private List<String> tableAll=new ArrayList<>(10);

    /**
     * 选中的表格
     */
    private List<String> tableSelected=new ArrayList<>(10);

    private String CONFIG_URL = DEFAULT_CONFIG_URL;

    private static final String DEFAULT_CONFIG_URL="code.properties";

    private Map<String,String> paragramParam=null;

    /**
     * 模板页面的高度
     */
    private static final int TEMPLATE_PAGE_INFO_HEIGHT = 80;
    /**
     * 模板页面的宽度
     */
    private static final int TEMPLATE_PAGE_INFO_WIDTH = 230;

    public MainJFramePage(Map<String,String> config) throws Exception {
        this.paragramParam=config;
        //初始化配置文件
        initConfig();
        //初始化窗口
        initJFrame();
        //添加窗口监听
        addListener();
    }


    /**
     * 初始化配置
     *
     * @throws IOException
     */
    private void initConfig() throws IOException, SQLException, ClassNotFoundException {
        this.CONFIG_URL=Utils.isEmpty(paragramParam.get("config.file.url"))?DEFAULT_CONFIG_URL:paragramParam.get("config.file.url");
        dataSourceFileConfig = new DataSourceFileConfig(CONFIG_URL);
        projectFileConfig = new ProjectFileConfig(CONFIG_URL);
        initTableAll();
    }

    /**
     * 初始化所有表格名
     */
    private void initTableAll() throws SQLException, ClassNotFoundException {
        this.tableAll=DbUtil.getAllTableName(getDataSourceConfig());
    }

    /**
     * 初始化窗口
     */
    private void initJFrame() throws Exception {
        setSize(1400, 800);
        setLocationRelativeTo(null);
        // 窗口的其他参数
        setTitle("Code Builder");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initJMenuBar();
        setContentPane(getFirstPage());

    }

    /**
     * 添加窗口监听
     */
    private void addListener() {
        addWindowListener((MainJFramePageWindowListener) e -> {
                    // 此处加入操作动作
                    System.out.println("退出");
                }
        );
    }

    /**
     * 首页页面
     *
     * @return
     */
    public JComponent getFirstPage() throws Exception {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(null);
        jPanel.setVisible(true);

        JScrollPane firstPage = new JScrollPane(jPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        firstPage.setBounds(0, 0, 850, 440);

        JPanel sonPage=new JPanel();
        sonPage.setLayout(null);
        sonPage.setBounds(0, 0, 850, 775);

        JComponent selectPage = getSelectTemplatePage();
        sonPage.add(selectPage);
        JComponent jComponent = getTemplateInfoPage();
        sonPage.add(jComponent);
        //判断是否有控制方法的模板
        if(hasHandleFunctionTemplate()) {
            JPanel definePage = getDefinePage();
            sonPage.add(definePage);
        }
        JPanel buttonPage=getButtonPage(sonPage.getComponent(sonPage.getComponentCount()-1).getY());
        sonPage.add(buttonPage);

        //获取组件中最宽的宽度
        int width=Stream.of(sonPage.getComponents()).map(Component::getWidth).max(Integer::compareTo).get();
        //获取组件中最宽的高度
        Component componentLast=sonPage.getComponent(sonPage.getComponentCount()-1);
        int height=componentLast.getY()+componentLast.getHeight();
        sonPage.setSize(width + 30,height);
        jPanel.add(sonPage);

        firstPage.setSize(width- 30, height);

        jPanel.setPreferredSize(new Dimension(width, height));
        jPanel.revalidate();

        if(width>getWidth()||height>getHeight()) {
            setSize(width, height);
        }
        revalidate();
        setLocationRelativeTo(null);
        return firstPage;
    }

    /**
     * 有控制方法的模板
     * @return
     */
    private boolean hasHandleFunctionTemplate() throws Exception {
        return this.targetMultipleTemplate.getMultipleTemplate(projectFileConfig).stream().anyMatch(s -> s instanceof HandleFunctionTemplate);
    }

    /**
     * 自定义方法页面
     * @return
     */
    public JPanel getDefinePage(){
        JPanel jPanel=new JPanel();
        jPanel.setLayout(null);
        jPanel.setVisible(true);
        jPanel.setBounds(156, 530, 850, 123);

        JLabel filedLabel = new JLabel("字段名(多个,分开)");
        filedLabel.setBounds(0, 0, 180, 23);
        jPanel.add(filedLabel);

        JTextField filedSText = new JTextField();
        filedSText.setName("DefinedValue");
        filedSText.setBounds(180, 0, 200, 30);
        jPanel.add(filedSText);
        filedSText.setColumns(20);


        JLabel representFactorJLabel = new JLabel("代表因子:");
        representFactorJLabel.setBounds(400, 0, 180, 23);
        jPanel.add(representFactorJLabel);

        JTextField representFactorText = new JTextField();
        representFactorText.setName("RepresentFactor");
        representFactorText.setBounds(500, 0, 200, 30);
        jPanel.add(representFactorText);
        representFactorText.setColumns(20);

        JCheckBox getByFiledFunction = new JCheckBox("生成以模板方法体为模板的方法");
        getByFiledFunction.setName("DefinedFunctionCheckBox");
        getByFiledFunction.setSelected(false);
        getByFiledFunction.setBounds(0, 30, 250, 23);
        jPanel.add(getByFiledFunction);
        return jPanel;
    }

    /**
     * 按钮页面
     * @return
     */
    public JPanel getButtonPage(int beforeY) {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(null);
        jPanel.setVisible(true);
        jPanel.setBounds(165, beforeY+130, 820, 123);

        int fontLoadFactor = 15;
        String btn1Value = "生成";
        JButton buildNewFileBtn = new JButton(btn1Value);
        buildNewFileBtn.setBounds(0, 0, btn1Value.length() * fontLoadFactor + 50, 23);
        int currentBeforeX = btn1Value.length() * fontLoadFactor + 50;
        buildNewFileBtnListenerImpl(buildNewFileBtn);
        jPanel.add(buildNewFileBtn);

        String btn2Value = "在已有的文件末尾处添加生成";
        JButton buildNewFileBtn2 = new JButton(btn2Value);
        currentBeforeX = currentBeforeX + 20;
        buildNewFileBtn2.setBounds(currentBeforeX, 0, btn2Value.length() * fontLoadFactor + 50, 23);
        currentBeforeX = currentBeforeX + btn2Value.length() * fontLoadFactor + 50;
        buildFileAppendAfterListenerImpl(buildNewFileBtn2);
        jPanel.add(buildNewFileBtn2);


        String btn3Value = "持久化配置";
        JButton buildNewFileBtn3 = new JButton(btn3Value);
        currentBeforeX = currentBeforeX + 20;
        buildNewFileBtn3.setBounds(currentBeforeX, 0, btn3Value.length() * fontLoadFactor + 50, 23);
        currentBeforeX = currentBeforeX + btn3Value.length() * fontLoadFactor + 50;
        firstPageDurableConfig(buildNewFileBtn3);
        jPanel.add(buildNewFileBtn3);

        String titleValue = "文件名:";
        JLabel lblNewLabelFile = new JLabel(titleValue);
        currentBeforeX = currentBeforeX + 20;
        lblNewLabelFile.setBounds(currentBeforeX, 0, titleValue.length() * fontLoadFactor + 50, 23);
        jPanel.add(lblNewLabelFile);

        JTextField deleteFileText = new JTextField();
        deleteFileText.setBounds(currentBeforeX + titleValue.length() * fontLoadFactor + 20, 0, 122, 23);
        jPanel.add(deleteFileText);
        deleteFileText.setColumns(10);


        String btn4Value = "删除最后生成的文件->依次";
        JButton buildNewFileBtn4 = new JButton(btn4Value);
        buildNewFileBtn4.setBounds(currentBeforeX, 25, btn4Value.length() * fontLoadFactor + 50, 23);
        buildNewFileBtn4.addActionListener(e -> {
        });
        jPanel.add(buildNewFileBtn4);


        String btn5Value = "删除所有一次操作生成的文件";
        JButton buildNewFileBtn5 = new JButton(btn5Value);
        buildNewFileBtn5.setBounds(currentBeforeX, 50, btn5Value.length() * fontLoadFactor + 50, 23);
        buildNewFileBtn5.addActionListener(e -> {
        });

        jPanel.add(buildNewFileBtn5);
        return jPanel;
    }

    /**
     * 在文件的末尾处添加
     * @param buildNewFileBtn
     */
    private void buildFileAppendAfterListenerImpl(JButton buildNewFileBtn) {
        buildNewFileBtn.addActionListener(e -> {
            FileBuilder fileBuilder = new DefaultFileBuilder();
            FileCodeBuilderStrategy fileCodeBuilderStrategy=new FileAppendSuffixCodeBuilderStrategy();
            fileCodeBuilderStrategy.setDefinedFunctionResolver(new DefaultDefinedFunctionResolver());
            fileBuilder.setFileCodeBuilderStrategy(fileCodeBuilderStrategy);
            buildFile(fileBuilder);
        });
    }

    /**
     * 生成新文件按钮监听实现
     * @param buildNewFileBtn
     */
    private void buildNewFileBtnListenerImpl(JButton buildNewFileBtn) {
        buildNewFileBtn.addActionListener(e -> {
            FileBuilder fileBuilder = new DefaultFileBuilder();
            buildFile(fileBuilder);
        });
    }

    /**
     * 创建文件
     * @param fileBuilder
     */
    private void buildFile(FileBuilder fileBuilder) {
        this.tableSelected.clear();
        try {
            ProjectTemplateInfoConfig projectTemplateInfoConfig = getProjectTemplateInfoConfig();
            CoreConfig coreConfig = new CoreConfig(getDataSourceConfig(), projectTemplateInfoConfig);
            JSONArray handleTemplateBuild = projectTemplateInfoConfig.getHandleTemplateBuild();
            JSONArray noHandleTemplateBuild = projectTemplateInfoConfig.getNoHandleTemplateBuild();
            if(searchComponentByName(getContentPane(),"AllGenerate",JCheckBox.class).isSelected()){
                this.tableSelected=this.tableAll;
            }else{
                String tableName=searchComponentByName(getContentPane(),"TargetTableName",JTextField.class).getText();
                if(Utils.isEmpty(tableName)){
                    JOptionPane.showMessageDialog(getContentPane(), "请填写目标表名", "提示", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                this.tableSelected.add(tableName);
            }
            for (String tableName:tableSelected){
                for (Object item : handleTemplateBuild) {
                    JSONObject jsonObject = (JSONObject) item;
                    Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Object> entry = iterator.next();
                        String templateName = entry.getKey();
                        Template template = getTemplateFromTargetByName(templateName);
                        fileBuilder.build(coreConfig, tableName, template);
                    }
                }
                for (Object item : noHandleTemplateBuild) {
                    String templateName = (String) item;
                    Template template;
                    template = getTemplateFromTargetByName(templateName);
                    fileBuilder.build(coreConfig, tableName, template);
                }
            }
            JOptionPane.showMessageDialog(getContentPane(), "操作成功!", "提示", JOptionPane.PLAIN_MESSAGE);
        }catch (Exception exception){
            exception.printStackTrace();
            JOptionPane.showMessageDialog(getContentPane(), "操作失败!原因:"+exception.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 首页持久化配置
     *
     * @param buildNewFileBtn
     */
    private void firstPageDurableConfig(JButton buildNewFileBtn) {
        buildNewFileBtn.addActionListener(e -> {
            Map<String, String> newConfig = new HashMap<>(2);
            ProjectTemplateInfoConfig projectTemplateInfoConfig = getProjectTemplateInfoConfig();
            newConfig.put("handle-template-build", projectTemplateInfoConfig.getHandleTemplateBuild().toJSONString());
            newConfig.put("nohandle-template-build", projectTemplateInfoConfig.getNoHandleTemplateBuild().toJSONString());
            reloadConfig(newConfig);
        });
    }

    /**
     * 获取项目模板详情配置
     *
     * @return
     */
    public ProjectTemplateInfoConfig getProjectTemplateInfoConfig() {
        JSONArray handleFunctionConfig = new JSONArray();
        JSONArray noHandleFunctionConfig = new JSONArray();
        searchComponentByClass(getContentPane(), JCheckBox.class).forEach(item -> {
            String name = item.getName();
            String parentName = item.getParent().getName();
            if (Utils.isNotEmpty(parentName)&& item.isSelected()) {
                if (parentName.equals(name)) {
                    noHandleFunctionConfig.add(name);
                } else {
                    JSONObject jsonObject = Utils.getJsonObjByKey(handleFunctionConfig, parentName);
                    if (jsonObject.isEmpty()) {
                        jsonObject = new JSONObject();
                        JSONArray temp = new JSONArray();
                        temp.add(name);
                        jsonObject.put(parentName, temp);
                        handleFunctionConfig.add(jsonObject);
                    } else {
                        JSONArray jsonArray = (JSONArray) jsonObject.get(parentName);
                        jsonArray.add(name);
                    }
                }
            }
        });
        List<DefinedFunctionDomain> definedFunctionDomainList=new ArrayList<>();
        if(searchComponentByName(getContentPane(), "DefinedFunctionCheckBox",JCheckBox.class).isSelected()) {
            String definedValue= Optional.of(searchComponentByName(getContentPane(), "DefinedValue", JTextField.class)).get().getText();
            if(Utils.isEmpty(definedValue)){
                throw new NullPointerException("请填写字段名");
            }
            String representFactor= Optional.of(searchComponentByName(getContentPane(), "RepresentFactor", JTextField.class)).get().getText();
            if(Utils.isEmpty(representFactor)){
                throw new NullPointerException("请填写代表因子");
            }
            if(handleFunctionConfig.isEmpty()){
                throw new NullPointerException("请选择模板方法作为此自定义方法的模板");
            }
            handleFunctionConfig.stream().map(s->(JSONObject)s).forEach(data-> data.forEach((key, value) ->{
                JSONArray jsonArray=(JSONArray) value;
                jsonArray.forEach(functionName->{
                    definedFunctionDomainList.add(new DefinedFunctionDomain(definedValue, (String) functionName,representFactor));
                });
            }));
        }
        System.out.println("控制方法：" + handleFunctionConfig.toJSONString());
        System.out.println("不控制方法：" + noHandleFunctionConfig.toJSONString());
        return new ProjectTemplateInfoConfig(handleFunctionConfig, noHandleFunctionConfig, projectFileConfig.getProperty("project-complete-url"), projectFileConfig.getProperty("project-target-packageurl"),definedFunctionDomainList);
    }

    /**
     * 获取数据源配置
     * @return
     */
    public DataSourceConfig getDataSourceConfig(){
        String url=dataSourceFileConfig.getProperty("url");
        String quDongName = url.indexOf("mysql") > 0 ? "com.mysql.jdbc.Driver" : url.indexOf("oracle") > 0 ? "" : "";
        return new DataSourceConfig(quDongName,dataSourceFileConfig.getProperty("username"),url,dataSourceFileConfig.getProperty("password"));
    }

    /**
     * 重新设置配置
     */
    private void reloadConfig(Map<String, String> newConfig) {

        Map<String, String> properties = new LinkedHashMap<>();
        if (Utils.isNotEmpty(newConfig.get("url"))) {
            properties.put("url", newConfig.get("url"));
        }
        if (Utils.isNotEmpty(newConfig.get("username"))) {
            properties.put("username", newConfig.get("username"));
        }
        if (Utils.isNotEmpty(newConfig.get("password"))) {
            properties.put("password", newConfig.get("password"));
        }
        try {
            if (!properties.isEmpty()) {
                dataSourceFileConfig.coverProperty(properties);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> propertiesProject = new LinkedHashMap<>();
        if (Utils.isNotEmpty(newConfig.get("project-complete-url"))) {
            propertiesProject.put("project-complete-url", newConfig.get("project-complete-url"));
        }
        if (Utils.isNotEmpty(newConfig.get("project-target-packageurl"))) {
            propertiesProject.put("project-target-packageurl", newConfig.get("project-target-packageurl"));
        }
        if (Utils.isNotEmpty(newConfig.get("handle-template-build"))) {
            propertiesProject.put("handle-template-build", newConfig.get("handle-template-build"));
        }
        if (Utils.isNotEmpty(newConfig.get("nohandle-template-build"))) {
            propertiesProject.put("nohandle-template-build", newConfig.get("nohandle-template-build"));
        }

        try {
            if (!propertiesProject.isEmpty()) {
                projectFileConfig.coverProperty(propertiesProject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(getContentPane(), "配置成功", "提示", JOptionPane.PLAIN_MESSAGE);
    }

    public JComponent getTemplateInfoPage() throws Exception {
        JPanel jPanel = new JPanel();
        jPanel.setBounds(10, 50, 850, 400);
        jPanel.setLayout(null);
        jPanel.setVisible(true);

        JScrollPane firstPage = new JScrollPane(jPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        firstPage.setBounds(10, 110, 850, 400);
        //生成模板页面
        JPanel jPanelPage = createTemplatePage(getAllMultipleTemplate().get(0));
        jPanel.add(jPanelPage);

        jPanel.setSize(jPanelPage.getWidth(), 400);
        if (jPanelPage.getHeight() < 450) {
            jPanel.setBounds(126, 100, jPanel.getWidth() + 120, jPanel.getHeight());
            return jPanel;
        } else {
            firstPage.setSize(jPanelPage.getWidth() - 30, 400);
            jPanel.setPreferredSize(new Dimension(850, jPanelPage.getHeight()));
            return firstPage;
        }
    }

    public JPanel getSelectTemplatePage() {
        JPanel page = new JPanel();
        page.setBounds(156, 50, 950, 50);
        page.setLayout(null);
        // 添加一个标签
        JLabel label = new JLabel("请选择模板：");
        label.setBounds(0, 0, 100, 50);
        page.add(label);
        // 创建一个下拉列表框
        String[] allTemplates = getTeplateNameAllMultipleTemplate();
        JComboBox<String> comboBox = new JComboBox<>(allTemplates);
        // 添加条目选中状态改变的监听器
        comboBox.addItemListener(e -> {
            // 只处理选中的状态
            if (e.getStateChange() == ItemEvent.SELECTED) {
                this.targetMultipleTemplate=getAllMultipleTemplate().get(comboBox.getSelectedIndex());
                System.out.println("选中: " + comboBox.getSelectedIndex() + " = " + comboBox.getSelectedItem());
            }
        });
        // 设置默认选中的条目
        comboBox.setSelectedIndex(0);
        if(null==this.targetMultipleTemplate){
            this.targetMultipleTemplate=getAllMultipleTemplate().get(0);
        }
        comboBox.setBounds(150, 0, 200, 50);
        page.add(comboBox);

        JCheckBox jCheckBox=new JCheckBox("全库生成");
        jCheckBox.setName("AllGenerate");
        jCheckBox.setBounds(400,0,100,50);
        page.add(jCheckBox);

        JLabel jLabel=new JLabel("目标表名:");
        jLabel.setBounds(510,0,100,50);
        page.add(jLabel);

        JTextField jTextField=new JTextField();
        jTextField.setName("TargetTableName");
        jTextField.setBounds(610,20,120,20);
        page.add(jTextField);

        JButton reloadBotton=new JButton("刷新模板");
        reloadBotton.setBounds(750,0,120,30);
        reloadBotton.addActionListener(e->{
            try {
                initConfig();
                showSpecifiedPanel(getFirstPage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        page.add(reloadBotton);
        return page;
    }

    /**
     * 生成模板页面
     */
    public JPanel createTemplatePage(MultipleTemplate multipleTemplate) throws Exception {
        JPanel jPanelPage = new JPanel();
        jPanelPage.setLayout(null);
        jPanelPage.setBounds(10, 0, 850, 510);
        List<Template> templateList = multipleTemplate.getMultipleTemplate(projectFileConfig);
        Integer[] totalWidthTotalHeight = new Integer[]{0, 0};
        loadHandleFunctionPage(jPanelPage, templateList, totalWidthTotalHeight);
        loadNoHandleFunctionPage(jPanelPage, templateList, totalWidthTotalHeight);
        jPanelPage.setSize(totalWidthTotalHeight[0], totalWidthTotalHeight[1]);
        jPanelPage.invalidate();
        return jPanelPage;
    }

    /**
     * 不使用instanceof 判断是否是同一种类型,
     * 判断目标类型和源类型的字节码对象(Class)是否是同一种类型
     * 通过class类的toString 方法,判断打印出来的类型是否相等,或者直接判断
     *
     * @param c
     * @param t
     * @return
     */
    public <T extends JComponent> List<T> searchComponentByClass(Container c, Class<T> t) {//泛型方法
        List<T> result = new ArrayList<>();
        Component[] components = c.getComponents();
        if (null != components && components.length > 0) {
            for (Component component : components) {
                if (component.getClass().equals(t)) {
                    result.add((T) component);
                } else if (result.isEmpty()) {
                    //递归调用所有下级组件列表
                    if (component instanceof Container) {
                        result.addAll(searchComponentByClass((Container) component, t));
                    }
                } else {
                    if (component instanceof Container) {
                        result.addAll(searchComponentByClass((Container) component, t));
                    }
                }
            }
        }
        return result;
    }

    public <T extends JComponent> T searchComponentByName(Container c, String name, Class<T> t){
        Component[] components = c.getComponents();
        Component componentResult=null;
        if (null != components && components.length > 0) {
            for (Component component : components) {
                if (name.equals(component.getName())&&component.getClass().equals(t)) {
                    componentResult=(T) component;
                }else if(null==componentResult){
                    if (component instanceof Container) {
                        componentResult=searchComponentByName((Container) component, name, t);
                    }
                }
            }
        }
        return (T) componentResult;
    }



    /**
     * 加载不需要控制方法的模板页面
     *
     * @param jPanelPage
     * @param templateList
     * @param totalWidthTotalHeight
     */
    private void loadNoHandleFunctionPage(JPanel jPanelPage, List<Template> templateList, Integer[] totalWidthTotalHeight) {
        int a = 1;
        int y = 30 + totalWidthTotalHeight[1];
        int tempBeforeStringWidthTotal = 10;
        List<NoHandleFunctionTemplate> noHandleFunctionTemplateList = templateList.stream().filter(s -> s instanceof NoHandleFunctionTemplate).map(s -> (NoHandleFunctionTemplate) s).collect(Collectors.toList());
        for (NoHandleFunctionTemplate noHandleFunctionTemplate : noHandleFunctionTemplateList) {
            String templateName = noHandleFunctionTemplate.getTemplateName();
            String checkBoxValue = "子模板:" + templateName;
            JPanel jPanel = new JPanel();
            jPanel.setName(templateName);
            jPanel.setLayout(null);
            jPanel.setBounds(tempBeforeStringWidthTotal, y, checkBoxValue.length() * 13, 100);
            JCheckBox jCheckBox = new JCheckBox(checkBoxValue);
            jCheckBox.setName(templateName);
            jCheckBox.setSelected(haveNoHandleTemplate(templateName));
            jCheckBox.setBounds(0, 30, checkBoxValue.length() * 13, 30);
            jPanel.add(jCheckBox);
            jPanelPage.add(jPanel);
            tempBeforeStringWidthTotal = tempBeforeStringWidthTotal + jPanel.getWidth();
            totalWidthTotalHeight[0] = totalWidthTotalHeight[0] + (a < 3 ? jPanel.getWidth() : 0);
            totalWidthTotalHeight[1] = totalWidthTotalHeight[1] + (a == 1 ? jPanel.getHeight() : 0);

            if (a % 3 == 0) {
                y = y + jPanel.getHeight();
            }
            if (a == 3) {
                a = 1;
                tempBeforeStringWidthTotal = 10;
            } else {
                a++;
            }
        }
    }

    private boolean haveNoHandleTemplate(String templateName) {
        String property = projectFileConfig.getProperty("nohandle-template-build");
        if(Utils.isEmpty(property)){
            return true;
        }
        JSONArray array = (JSONArray) JSONObject.parse(property);
        if (Utils.isEmpty(property) || null==array || array.contains(templateName)) {
            return true;
        }
        return false;
    }

    /**
     * 加载需要控制方法的页面
     *
     * @param jPanelPage
     * @param templateList
     * @param totalWidthTotalHeight
     */
    private void loadHandleFunctionPage(JPanel jPanelPage, List<Template> templateList, Integer[] totalWidthTotalHeight) throws TemplateResolveException {
        List<HandleFunctionTemplate> handleFunctionTemplateList = templateList.stream().filter(s -> s instanceof HandleFunctionTemplate).map(s -> (HandleFunctionTemplate) s).collect(Collectors.toList());
        int a = 1;
        int y = 30;
        int tempBeforeStringWidthTotal = 10;

        for (HandleFunctionTemplate handleFunctionTemplate : handleFunctionTemplateList) {
            JPanel jPanel = new JPanel();
            jPanel.setLayout(null);
            jPanel.setBounds(tempBeforeStringWidthTotal, y, TEMPLATE_PAGE_INFO_WIDTH, TEMPLATE_PAGE_INFO_HEIGHT);
            String templateName = handleFunctionTemplate.getTemplateName();
            JCheckBox labelCheckBox = new JCheckBox("子模板：" + templateName);
            labelCheckBox.setName(templateName);
            labelCheckBox.setSelected(checkSelectAllFunctionWhenTemplate(handleFunctionTemplate));
            labelCheckBox.setBounds(0, 0, TEMPLATE_PAGE_INFO_WIDTH, 30);
            labelCheckBox.addActionListener(e -> {
                searchComponentByClass(jPanel,JCheckBox.class).forEach(item->{
                    JCheckBox jCheckBox= (JCheckBox) e.getSource();
                    JCheckBox result= item;
                    result.setSelected(jCheckBox.isSelected());
                });
            });
            jPanel.add(labelCheckBox);
            JLabel label2 = new JLabel("子模板方法名：");
            label2.setBounds(0, 30, TEMPLATE_PAGE_INFO_WIDTH, 30);
            jPanel.add(label2);
            Set<String> functionNameS = handleFunctionTemplate.getTemplateFunctionNameS();
            JPanel jPanel2 = new JPanel();
            jPanel2.setLayout(null);
            jPanel2.setName(templateName);
            jPanel2.setSize(TEMPLATE_PAGE_INFO_WIDTH, TEMPLATE_PAGE_INFO_HEIGHT);
            JCheckBoxGroup jCheckBoxGroup = new JCheckBoxGroup(functionNameS, templateName, jPanel2, projectFileConfig);
            jPanel2.setBounds(0, 60, jCheckBoxGroup.getWidth(), jCheckBoxGroup.getHeight());
            jPanel.add(jPanel2);
            jPanel.setBounds(tempBeforeStringWidthTotal, y, Math.max(jPanel2.getWidth(),TEMPLATE_PAGE_INFO_WIDTH),label2.getY()+label2.getHeight()+jPanel2.getHeight());
            jPanelPage.add(jPanel);
            tempBeforeStringWidthTotal = tempBeforeStringWidthTotal + jPanel.getWidth();
            totalWidthTotalHeight[0] = totalWidthTotalHeight[0] + (a < 3 ? jPanel.getWidth() : 0);
            totalWidthTotalHeight[1] = totalWidthTotalHeight[1] + (a == 1 ? jPanel.getHeight() : 0);

            if (a % 3 == 0) {
                y = y + jPanel.getHeight();
            }
            if (a == 3) {
                a = 1;
                tempBeforeStringWidthTotal = 10;
            } else {
                a++;
            }
        }
    }

    /**
     * 是否选中所有方法
     * @param template
     * @return
     */
    public boolean checkSelectAllFunctionWhenTemplate(HandleFunctionTemplate template) throws TemplateResolveException {
        String property = projectFileConfig.getProperty("handle-template-build");
        if(Utils.isEmpty(property)){
            return true;
        }
        JSONArray jsonArray= (JSONArray) JSON.parse(property);
        JSONArray jsonArrayReal=Utils.getJsonArrayByKey(jsonArray,template.getTemplateName());
        if(null!=jsonArrayReal&&template.getTemplateFunctionNameS().size()==jsonArrayReal.size()){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 配置界面
     *
     * @return
     */
    public JPanel getConfigPage() {
        JPanel config = new JPanel();
        config.setLayout(null);
        JLabel label = new JLabel("数据源:");
        label.setBounds(156, 81, 154, 15);
        config.add(label);

        JTextArea textArea = new JTextArea(5, 10);
        textArea.setName("url");
        textArea.setBounds(234, 78, 400, 51);
        textArea.setText(dataSourceFileConfig.getProperty("url"));
        // 设置自动换行
        textArea.setLineWrap(true);
        config.add(textArea);

        JLabel label1 = new JLabel("用户名:");
        label1.setBounds(156, 143, 54, 15);
        config.add(label1);

        JTextField textField_1 = new JTextField();
        textField_1.setColumns(10);
        textField_1.setName("username");
        textField_1.setBounds(234, 143, 178, 21);
        textField_1.setText(dataSourceFileConfig.getProperty("username"));
        config.add(textField_1);

        JLabel label2 = new JLabel("密码:");
        label2.setBounds(156, 172, 54, 15);
        config.add(label2);

        JTextField textField_2 = new JTextField();
        textField_2.setColumns(10);
        textField_2.setName("password");
        textField_2.setBounds(234, 172, 178, 21);
        textField_2.setText(dataSourceFileConfig.getProperty("password"));
        config.add(textField_2);

        JLabel lblController = new JLabel("项目路径地址:");
        lblController.setBounds(133, 207, 203, 15);
        config.add(lblController);


        JTextArea textFiled_5 = new JTextArea(5, 10);
        textFiled_5.setName("project-complete-url");
        textFiled_5.setColumns(10);
        JScrollPane jsp = new JScrollPane(textFiled_5);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        textFiled_5.setText(projectFileConfig.getProperty("project-complete-url"));
        textFiled_5.setLineWrap(true);
        jsp.setBounds(234, 204, 278, 51);
        config.add(jsp);

        JLabel label3 = new JLabel("源码包路径:");
        label3.setBounds(133, 270, 84, 15);
        config.add(label3);

        JTextField textField_3 = new JTextField();
        textField_3.setName("project-target-packageurl");
        textField_3.setColumns(10);
        textField_3.setBounds(234, 270, 178, 21);
        textField_3.setText(projectFileConfig.getProperty("project-target-packageurl"));
        config.add(textField_3);

        JButton saveConfigBtn = new JButton("持久化保存配置");
        configPageSumbitBtnListenerImpl(saveConfigBtn);
        saveConfigBtn.setBounds(300, 335, 133, 23);
        config.add(saveConfigBtn);
        config.setVisible(true);

        return config;
    }

    /**
     * 配置页面提交按钮监听实现
     *
     * @param saveConfigBtn
     */
    private void configPageSumbitBtnListenerImpl(JButton saveConfigBtn) {
        saveConfigBtn.addActionListener(e -> {
            Map<String, String> newConfig = new HashMap<>(10);
            Arrays.stream(getContentPane().getComponents()).forEach(component -> {
                if (component instanceof JTextComponent) {
                    JTextComponent jTextComponent = (JTextComponent) component;
                    System.out.println(jTextComponent.getName() + "->" + jTextComponent.getText());
                    newConfig.put(jTextComponent.getName(), jTextComponent.getText());
                }
                if (component instanceof JScrollPane && ((JScrollPane) component).getViewport().getView() instanceof JTextArea) {
                    JTextArea jTextArea = (JTextArea) ((JScrollPane) component).getViewport().getView();
                    System.out.println(jTextArea.getName() + "->" + jTextArea.getText());
                    newConfig.put(jTextArea.getName(), jTextArea.getText());
                }
            });
            reloadConfig(newConfig);
        });
    }

    /**
     * 以contentPanel为底，其上覆盖想要展示的panel内容(切换panel)。
     */
    private void showSpecifiedPanel(JComponent showPanel) {
        this.remove(this.getContentPane());
        this.setContentPane(showPanel);
        this.validate();
        this.repaint();
    }

    /**
     * 初始化菜单栏
     */
    private void initJMenuBar() {
        HashMap<String, MenuBarPage.JMenuPageInfo> map = new HashMap<>(1);
        map.put("首页", new MenuBarPage.JMenuPageInfo(new MenuBarPage.MenuListenerImpl(() -> {
            initConfig();
            showSpecifiedPanel(getFirstPage());
        })));
        map.put("数据源和项目配置", new MenuBarPage.JMenuPageInfo(new MenuBarPage.MenuListenerImpl(() -> {
            initConfig();
            showSpecifiedPanel(getConfigPage());
        })));
        map.put("关于", new MenuBarPage.JMenuPageInfo(new MenuBarPage.MenuListenerImpl(() -> {
            initConfig();
            showSpecifiedPanel(getFirstPage());
        })));
        MenuBarPage menuBarPage = new MenuBarPage(map);
        setJMenuBar(menuBarPage);
    }

    /**
     * 获取所有的组合模板
     *
     * @return
     */
    public List<MultipleTemplate> getAllMultipleTemplate() {
        List<MultipleTemplate> multipleTemplateList = new ArrayList<>();
        String templates=paragramParam.get("templates");
        if(Utils.isNotEmpty(templates)){
            JSONArray jsonArray=JSONArray.parseArray(templates);
            jsonArray.forEach(v->{
                JSONObject jsonObject=(JSONObject)v;
                multipleTemplateList.add(new DefaultMultipleTemplate(jsonObject));
            });
        }else {
            multipleTemplateList.add(new DefaultMultipleTemplate());
        }
        return multipleTemplateList;
    }

    /**
     * 通过模板名获取模板
     * @param templateName 模板名
     * @return
     * @throws IOException
     */
    public Template getTemplateByName(String templateName) throws CodeConfigException, IOException {
        for(MultipleTemplate multipleTemplate:getAllMultipleTemplate()){
            for (Template template:multipleTemplate.getMultipleTemplate(projectFileConfig)){
                if(template.getTemplateName().equals(templateName)){
                    return template;
                }
            }
        }
        return null;
    }


    /**
     * 从目标组合模板中通过模板名获取模板
     * @return
     */
    public Template getTemplateFromTargetByName(String templateName) throws IOException, CodeConfigException {
         return targetMultipleTemplate.getMultipleTemplate(projectFileConfig).stream().filter(item->item.getTemplateName().equals(templateName)).findFirst().get();
    }

    /**
     * 获取所有组合模板名
     *
     * @return
     */
    public String[] getTeplateNameAllMultipleTemplate() {
        List<MultipleTemplate> multipleTemplateList = getAllMultipleTemplate();
        return multipleTemplateList.stream().map(MultipleTemplate::getTemplateName).collect(Collectors.toList()).toArray(new String[multipleTemplateList.size()]);
    }

    /**
     * 当前主窗口的监听
     */
    private interface MainJFramePageWindowListener extends WindowListener {
        /**
         * Invoked the first time a window is made visible.
         *
         * @param e
         */
        @Override
        default void windowOpened(WindowEvent e) {

        }

        /**
         * Invoked when a window has been closed as the result
         * of calling dispose on the window.
         *
         * @param e
         */
        @Override
        default void windowClosed(WindowEvent e) {

        }

        /**
         * Invoked when a window is changed from a normal to a
         * minimized state. For many platforms, a minimized window
         * is displayed as the icon specified in the window's
         * iconImage property.
         *
         * @param e
         * @see Frame#setIconImage
         */
        @Override
        default void windowIconified(WindowEvent e) {

        }

        /**
         * Invoked when a window is changed from a minimized
         * to a normal state.
         *
         * @param e
         */
        @Override
        default void windowDeiconified(WindowEvent e) {

        }

        /**
         * Invoked when the Window is set to be the active Window. Only a Frame or
         * a Dialog can be the active Window. The native windowing system may
         * denote the active Window or its children with special decorations, such
         * as a highlighted title bar. The active Window is always either the
         * focused Window, or the first Frame or Dialog that is an owner of the
         * focused Window.
         *
         * @param e
         */
        @Override
        default void windowActivated(WindowEvent e) {

        }

        /**
         * Invoked when a Window is no longer the active Window. Only a Frame or a
         * Dialog can be the active Window. The native windowing system may denote
         * the active Window or its children with special decorations, such as a
         * highlighted title bar. The active Window is always either the focused
         * Window, or the first Frame or Dialog that is an owner of the focused
         * Window.
         *
         * @param e
         */
        @Override
        default void windowDeactivated(WindowEvent e) {
        }

        ;
    }
}
