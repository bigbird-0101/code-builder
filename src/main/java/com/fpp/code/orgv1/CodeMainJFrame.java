package com.fpp.code.orgv1;

import com.fpp.code.domain.DataSourceConfig;
import com.fpp.code.common.Utils;
import com.fpp.code.config.DataSourceFileConfig;
import com.fpp.code.config.ProjectFileConfig;
import com.fpp.code.domain.FileTempleConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @author Administrator
 */
public class CodeMainJFrame extends JFrame {

    private static final long serialVersionUID = 5725706474912002588L;
    private final JCheckBox chckbxNewCheckBox;
    private final JCheckBox doMainFile;
    private final JTextArea textArea;
    private final JCheckBox addFunction;
    private final JCheckBox getByIdFunction;
    private final JCheckBox deleteFunction;
    private final JCheckBox editFunction;
    private final JCheckBox getAllFunciton;
    private final JCheckBox controllerFile;
    private final JCheckBox serviceFile;
    private final JCheckBox daoFile;
    private final JTextField textServicepackage;
    private final JTextField textDoMainpackage;
    private final JTextField textControllerPackage;
    private final JTextField textDaopackage;
    private final JTextField textServiceImpl;
    private final JButton saveConfigBtn;
    private final JButton deleteFileBtn;
    private final JPanel contentPane;
    private final JTextField textField_1;
    private final JTextField textField_2;
    private final JTextField textField4;
    private final JTextArea textFiled_5;
    private final JTextField textFiled_6;
    private final JTextField takenJabberers;
    private final JButton buildNewFileBtn;
    private final JTextField deleteFileText;
    private final JButton buildAfterFileBtn;
    private final JCheckBox getByFiledFunction;
    private final JTextField filedSText;
    private final JButton deleteAllFileBtn;
    private DataSourceFileConfig dataSourceFileConfig;
    private ProjectFileConfig projectFileConfig;

    private String configUrl="code.properties";

    private List<String> buildEdFileUrlList;
    /**
     * Create the frame.
     */
    public CodeMainJFrame(String configUrl) throws IOException {
        this.configUrl = configUrl;
        initConfig();
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500, 200, 850, 775);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        contentPane.setLayout(null);

        JScrollPane jspMain = new JScrollPane(contentPane);
        jspMain.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setContentPane(jspMain);

        JLabel label = new JLabel("数据源:");
        label.setBounds(156, 81, 154, 15);
        contentPane.add(label);

        textArea = new JTextArea(5, 10);
        textArea.setBounds(234, 78, 400, 51);
        textArea.append(this.dataSourceFileConfig.getProperty("url"));
        // 设置自动换行
        textArea.setLineWrap(true);
        contentPane.add(textArea);

        JLabel label1 = new JLabel("用户名:");
        label1.setBounds(156, 143, 54, 15);
        contentPane.add(label1);

        textField_1 = new JTextField();
        textField_1.setColumns(10);
        textField_1.setBounds(234, 143, 178, 21);
        contentPane.add(textField_1);

        JLabel label2 = new JLabel("密码:");
        label2.setBounds(156, 172, 54, 15);
        contentPane.add(label2);

        textField_2 = new JTextField();
        textField_2.setColumns(10);
        textField_2.setBounds(234, 172, 178, 21);
        contentPane.add(textField_2);

        //获取数据库地址 用户名  密码
        textField_1.setText(this.dataSourceFileConfig.getProperty("username"));
        textField_2.setText(this.dataSourceFileConfig.getProperty("password"));


        chckbxNewCheckBox = new JCheckBox("不是全库生成");
        chckbxNewCheckBox.setSelected(true);
        chckbxNewCheckBox.setBounds(234, 348, 103, 23);
        contentPane.add(chckbxNewCheckBox);

        doMainFile = new JCheckBox("生成domain文件");
        doMainFile.setSelected(haveFileProperty(FileTypeEnum.DOMAIN));
        doMainFile.setBounds(140, 378, 103, 23);
        contentPane.add(doMainFile);

        controllerFile = new JCheckBox("生成Controller文件");
        controllerFile.setSelected(haveFileProperty(FileTypeEnum.CONTROLLER));
        controllerFile.setBounds(280, 378, 103, 23);
        contentPane.add(controllerFile);

        serviceFile = new JCheckBox("生成Service文件");
        serviceFile.setSelected(haveFileProperty(FileTypeEnum.SERVICE));
        serviceFile.setBounds(420, 378, 103, 23);
        contentPane.add(serviceFile);

        daoFile = new JCheckBox("生成Dao文件");
        daoFile.setSelected(haveFileProperty(FileTypeEnum.DAO));
        daoFile.setBounds(540, 378, 103, 23);
        contentPane.add(daoFile);

        addFunction = new JCheckBox("生成添加方法");
        addFunction.setSelected(haveFunctionProperty(FunctionTypeEnum.ADD));
        addFunction.setBounds(140, 408, 103, 23);
        contentPane.add(addFunction);

        editFunction = new JCheckBox("生成修改方法");
        editFunction.setSelected(haveFunctionProperty(FunctionTypeEnum.EDIT));
        editFunction.setBounds(280, 408, 103, 23);
        contentPane.add(editFunction);

        getByIdFunction = new JCheckBox("生成根据id查询方法");
        getByIdFunction.setSelected(haveFunctionProperty(FunctionTypeEnum.GET_BY_ID));
        getByIdFunction.setBounds(420, 408, 163, 23);
        contentPane.add(getByIdFunction);

        deleteFunction = new JCheckBox("生成删除方法");
        deleteFunction.setSelected(haveFunctionProperty(FunctionTypeEnum.DELETE));
        deleteFunction.setBounds(590, 408, 103, 23);
        contentPane.add(deleteFunction);

        getAllFunciton = new JCheckBox("生成分页方法");
        getAllFunciton.setSelected(haveFunctionProperty(FunctionTypeEnum.GET_ALL));
        getAllFunciton.setBounds(140, 438, 103, 23);
        contentPane.add(getAllFunciton);

        JLabel filedSLabel = new JLabel("字段名(多个,分开)");
        filedSLabel.setBounds(280, 438, 180, 23);
        contentPane.add(filedSLabel);

        filedSText = new JTextField();
        filedSText.setBounds(390, 438, 122, 23);
        contentPane.add(filedSText);
        filedSText.setColumns(10);

        getByFiledFunction = new JCheckBox("生成根据字段查询方法");
        getByFiledFunction.setSelected(haveFunctionProperty(FunctionTypeEnum.GET_BY_FILED));
        getByFiledFunction.setBounds(280, 470, 183, 23);
        contentPane.add(getByFiledFunction);

        buildNewFileBtn = new JButton("生成");
        buildNewFileBtnListener();

        buildNewFileBtn.setBounds(124, 535, 93, 23);
        contentPane.add(buildNewFileBtn);


        buildAfterFileBtn = new JButton("在已有的文件末尾处添加生成");
        buildAfterFileBtnListener();

        buildAfterFileBtn.setBounds(124, 565, 213, 23);
        contentPane.add(buildAfterFileBtn);


        saveConfigBtn = new JButton("持久化保存配置");
        saveConfigBtnListener();

        saveConfigBtn.setBounds(300, 535, 133, 23);
        contentPane.add(saveConfigBtn);

        deleteFileBtn = new JButton("删除最后生成的文件->依次");
        deleteFileBtnListener();

        deleteFileBtn.setBounds(500, 535, 210, 23);
        contentPane.add(deleteFileBtn);

        deleteAllFileBtn = new JButton("删除所有一次操作生成的文件");
        deleteAllFileBtnListener();

        deleteAllFileBtn.setBounds(500, 565, 220, 23);
        contentPane.add(deleteAllFileBtn);

        JLabel lblNewLabelFile = new JLabel("文件名");
        lblNewLabelFile.setBounds(500, 505, 54, 23);
        contentPane.add(lblNewLabelFile);

        deleteFileText = new JTextField();
        deleteFileText.setBounds(560, 505, 122, 23);
        contentPane.add(deleteFileText);
        deleteFileText.setColumns(10);
        
        
        JLabel lblNewLabel = new JLabel("目标表名");
        lblNewLabel.setBounds(358, 348, 54, 15);
        contentPane.add(lblNewLabel);

        textField4 = new JTextField();
        textField4.setBounds(422, 348, 122, 21);
        contentPane.add(textField4);
        textField4.setColumns(10);

        textFiled_5 = new JTextArea(5, 10);
        textFiled_5.setText(projectFileConfig.getProperty("projecturl"));
        textFiled_5.setColumns(10);
        JScrollPane jsp = new JScrollPane(textFiled_5);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        textFiled_5.setLineWrap(true);
        jsp.setBounds(234, 204, 278, 51);
        contentPane.add(jsp);

        JLabel lblController = new JLabel("项目路径地址:");
        lblController.setBounds(133, 207, 203, 15);
        contentPane.add(lblController);

        JLabel lblService = new JLabel("源代码前缀地址:");
        lblService.setBounds(133, 262, 103, 15);
        contentPane.add(lblService);


        textFiled_6 = new JTextField();
        textFiled_6.setText(projectFileConfig.getProperty("srcurl-prefix"));
        textFiled_6.setColumns(10);
        textFiled_6.setBounds(234, 262, 100, 21);
        contentPane.add(textFiled_6);

        JLabel lblServiceimpl = new JLabel("源代码地址:");
        lblServiceimpl.setBounds(133, 292, 89, 15);
        contentPane.add(lblServiceimpl);

        takenJabberers = new JTextField();
        takenJabberers.setText(projectFileConfig.getProperty("srcurl"));
        takenJabberers.setColumns(10);
        takenJabberers.setBounds(234, 292, 100, 21);
        contentPane.add(takenJabberers);

        JLabel lblServiceImplimpl = new JLabel("service包实现类包名:");
        lblServiceImplimpl.setBounds(133, 322, 160, 15);
        contentPane.add(lblServiceImplimpl);

        textServiceImpl = new JTextField();
        textServiceImpl.setText(projectFileConfig.getProperty("serviceimpl.package"));
        textServiceImpl.setColumns(10);
        textServiceImpl.setBounds(264, 322, 100, 21);
        contentPane.add(textServiceImpl);

        JLabel daoPackName = new JLabel("dao包路径:");
        daoPackName.setBounds(380, 262, 89, 15);
        contentPane.add(daoPackName);

        textDaopackage = new JTextField();
        textDaopackage.setText(projectFileConfig.getProperty("dao.package"));
        textDaopackage.setColumns(10);
        textDaopackage.setBounds(450, 262, 100, 21);
        contentPane.add(textDaopackage);

        JLabel domainPackName = new JLabel("domain包路径:");
        domainPackName.setBounds(560, 262, 89, 15);
        contentPane.add(domainPackName);

        textDoMainpackage = new JTextField();
        textDoMainpackage.setText(projectFileConfig.getProperty("com.fpp.code.domain.package"));
        textDoMainpackage.setColumns(10);
        textDoMainpackage.setBounds(660, 262, 100, 21);
        contentPane.add(textDoMainpackage);

        JLabel servicePackName = new JLabel("service包路径:");
        servicePackName.setBounds(360, 290, 100, 15);
        contentPane.add(servicePackName);

        textServicepackage = new JTextField();
        textServicepackage.setText(projectFileConfig.getProperty("service.package"));
        textServicepackage.setColumns(10);
        textServicepackage.setBounds(450, 290, 100, 21);
        contentPane.add(textServicepackage);

        JLabel controllerPackName = new JLabel("controller包路径:");
        controllerPackName.setBounds(560, 290, 100, 15);
        contentPane.add(controllerPackName);

        textControllerPackage = new JTextField();
        textControllerPackage.setText(projectFileConfig.getProperty("controller.package"));
        textControllerPackage.setColumns(10);
        textControllerPackage.setBounds(660, 290, 100, 21);
        contentPane.add(textControllerPackage);

        JTextPane jTextPane = new JTextPane();
        jTextPane.setBackground(Color.WHITE);
        jTextPane.setText("javabean目录 默认为bean，controller目录默认为controller ，service 目录为默认service.inter   serviceimpl目录为默认 service.impl   dao目录为默认 dao");
        jTextPane.setBounds(23, 10, 750, 48);
        contentPane.add(jTextPane);
    }

    private void deleteAllFileBtnListener() {
        deleteAllFileBtn.addActionListener(e -> {
            if(null!=this.buildEdFileUrlList&&!this.buildEdFileUrlList.isEmpty()){
                for (String filePath : buildEdFileUrlList) {
                    File file = new File(filePath);
                    String fileName = getFileName(filePath);
                    if (file.exists()) {
                        int result = JOptionPane.showConfirmDialog(
                                contentPane,
                                "确认删除" + fileName + "吗？",
                                "提示",
                                JOptionPane.YES_NO_OPTION
                        );
                        System.out.println("选择结果: " + result);
                        if (JOptionPane.YES_OPTION == result) {
                            file.delete();
                            JOptionPane.showMessageDialog(contentPane, "删除成功", "提示", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
                this.buildEdFileUrlList.clear();
            }else{
                JOptionPane.showMessageDialog(contentPane, "上一步操作文件已删空", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void buildAfterFileBtnListener() {
        buildCodeFile(buildAfterFileBtn,BuildCodeType.AFTER_FILE);
    }

    private void deleteFileBtnListener() {
        deleteFileBtn.addActionListener(e -> {
            if(null!=this.buildEdFileUrlList&&!this.buildEdFileUrlList.isEmpty()){
                int index=Utils.isEmpty(deleteFileText.getText())?this.buildEdFileUrlList.size() - 1:getDeleteFileIndex();
                if(index<0){
                    JOptionPane.showMessageDialog(contentPane, "文件名不存在", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String filePath = this.buildEdFileUrlList.get(index);
                File file = new File(filePath);
                String fileName = getFileName(filePath);
                if (file.exists()) {
                    int result = JOptionPane.showConfirmDialog(
                            contentPane,
                            "确认删除" + fileName + "吗？",
                            "提示",
                            JOptionPane.YES_NO_OPTION
                    );
                    System.out.println("选择结果: " + result);
                    if (JOptionPane.YES_OPTION == result) {
                        file.delete();
                        this.buildEdFileUrlList.remove(index);
                        JOptionPane.showMessageDialog(contentPane, "删除成功", "提示", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }else{
                JOptionPane.showMessageDialog(contentPane, "上一步操作文件已删空", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

    }

    private int getDeleteFileIndex() {
        int a=0;
        boolean isHave=false;
        for(String filePath:this.buildEdFileUrlList){
            String fileName = getFileName(filePath);
            if(fileName.contains(deleteFileText.getText())){
                isHave=true;
                break;
            }
            a++;
        }
        return isHave?a:-1;
    }

    /**
     * 获取文件名
     * @param filePath
     * @return
     */
    private static String getFileName(String filePath) {
        String fileName=filePath.contains("/") ?filePath.substring(filePath.lastIndexOf("/")+1):filePath;
        fileName=fileName.contains("\\") ?fileName.substring(fileName.lastIndexOf("\\")+1):filePath;
        return fileName;
    }

    private void saveConfigBtnListener() {
        saveConfigBtn.addActionListener(e -> {
            DataSourceConfig dataSourcePojo = getDataSouceConfigPojo();
            FileTempleConfig fileTemplePojo = getFileTempleConfigPojo();
            try {
                reloadConfig(Arrays.asList(dataSourcePojo,fileTemplePojo));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void reloadConfig(List<Object> configPojoList) throws IOException {

        configPojoList.forEach(item->{
            if(item instanceof DataSourceConfig){
                Map<String,String> properties=new LinkedHashMap<>();
                properties.put("url",((DataSourceConfig) item).getUrl());
                properties.put("username",((DataSourceConfig) item).getUserName());
                properties.put("password",((DataSourceConfig) item).getPassword());
                try {
                    dataSourceFileConfig.coverProperty(properties);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(item instanceof FileTempleConfig){
                Map<String,String> properties=new LinkedHashMap<>();
                properties.put("projecturl",((FileTempleConfig) item).getProjectUrl());
                properties.put("srcurl",((FileTempleConfig) item).getSrcUrl());
                properties.put("srcurl-prefix",((FileTempleConfig) item).getSrcUrlPrefix());
                properties.put("dao.package",((FileTempleConfig) item).getDaoPackage());
                properties.put("com.fpp.code.domain.package",((FileTempleConfig) item).getDomainPackage());
                properties.put("service.package",((FileTempleConfig) item).getServicePackage());
                properties.put("serviceimpl.package",((FileTempleConfig) item).getServiceImplPackage());
                properties.put("controller.package",((FileTempleConfig) item).getControllerPackage());
                properties.put("type-build",getFileTypeString());
                properties.put("function-build",getFunctionTypeString());
                try {
                    projectFileConfig.coverProperty(properties);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        JOptionPane.showMessageDialog(contentPane, "成功", "提示", JOptionPane.WARNING_MESSAGE);
    }


    private void initConfig() throws IOException {
        dataSourceFileConfig = new DataSourceFileConfig(configUrl);
        projectFileConfig = new ProjectFileConfig(configUrl);
    }

    private void buildCodeFile(JButton button, BuildCodeType buildNewFile){
        button.addActionListener(e -> {
            String tableName = textField4.getText();
            DataSourceConfig dataSourcePojo = getDataSouceConfigPojo();
            FileTempleConfig fileTemplePojo = getFileTempleConfigPojo();
            TableToJavaTool tableToJavaTool = new TableToJavaTool(dataSourcePojo, fileTemplePojo,buildNewFile);
            try {
                //判断是不是全库生成
                if (chckbxNewCheckBox.isSelected()) {
                    tableToJavaTool.process(tableName);
                } else {
                    tableToJavaTool.process();
                }
                this.buildEdFileUrlList=tableToJavaTool.getBuildEdFileUrlList();
                JOptionPane.showMessageDialog(contentPane, "成功", "提示", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(contentPane, "生成失败!\n\r\n原因:" + e1.getMessage(), "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

    }

    private void buildNewFileBtnListener() {
        buildCodeFile(buildNewFileBtn,BuildCodeType.BUILD_NEW_FILE);
    }

    /**
     * 获取用户输入的数据连接配置
     * @return
     */
    public DataSourceConfig getDataSouceConfigPojo(){
        String url = textArea.getText();
        String user = textField_1.getText();
        String password = textField_2.getText();
        String quDongName = url.indexOf("mysql") > 0 ? "com.mysql.jdbc.Driver" : url.indexOf("oracle") > 0 ? "" : "";
        return new DataSourceConfig(quDongName, user, url, password);
    }

    /**
     * 获取用户输入的数据连接配置
     * @return
     */
    public FileTempleConfig getFileTempleConfigPojo(){
        String projectUrl = textFiled_5.getText().replaceAll("\\\\","/");
        String srcUrlPrefix = textFiled_6.getText().replaceAll("\\\\","/");
        String srcUrl = takenJabberers.getText().replaceAll("\\\\","/");
        String daoPackage = textDaopackage.getText().replaceAll("\\\\","/");
        String servicePackage = textServicepackage.getText().replaceAll("\\\\","/");
        String domainPackage = textDoMainpackage.getText().replaceAll("\\\\","/");
        String controllerPackage = textControllerPackage.getText().replaceAll("\\\\","/");
        String serviceImplPackage = textServiceImpl.getText().replaceAll("\\\\","/");
        String getByFiledFiledS=filedSText.getText();
        int funcitonType = getFunctionType();
        int fileType = getFileType();
        return new FileTempleConfig(projectUrl, srcUrl, srcUrlPrefix, daoPackage, servicePackage, controllerPackage, serviceImplPackage, domainPackage, fileType, funcitonType,getByFiledFiledS);
    }

    /**
     * 获取需要生成的文件类型
     *
     * @return
     */
    private String getFileTypeString() {
        StringBuilder fileType=new StringBuilder();
        if (controllerFile.isSelected()) {
            fileType.append(FileTypeEnum.CONTROLLER.name().toLowerCase()).append(",");
        }
        if (doMainFile.isSelected()) {
            fileType.append(FileTypeEnum.DOMAIN.name().toLowerCase()).append(",");
        }
        if (daoFile.isSelected()) {
            fileType.append(FileTypeEnum.DAO.name().toLowerCase()).append(",");
        }
        if (serviceFile.isSelected()) {
            fileType.append(FileTypeEnum.SERVICE.name().toLowerCase()).append(",");
        }
        fileType=new StringBuilder(fileType.toString().substring(0,fileType.toString().length()-1));
        return fileType.toString();
    }

    /**
     * 获取需要生成的文件类型
     *
     * @return
     */
    private int getFileType() {
        int fileType = 0;
        if (controllerFile.isSelected()) {
            fileType = fileType | FileTypeEnum.CONTROLLER.getType();
        }
        if (doMainFile.isSelected()) {
            fileType = fileType | FileTypeEnum.DOMAIN.getType();
        }
        if (daoFile.isSelected()) {
            fileType = fileType | FileTypeEnum.DAO.getType();
        }
        if (serviceFile.isSelected()) {
            fileType = fileType | FileTypeEnum.SERVICE.getType();
        }
        return fileType;
    }

    /**
     * 获取需要生成的方法
     *
     * @return
     */
    private String getFunctionTypeString() {
        StringBuilder funcitonType = new StringBuilder();
        if (deleteFunction.isSelected()) {
            funcitonType.append(FunctionTypeEnum.DELETE.name().toLowerCase()).append(",");
        }
        if (addFunction.isSelected()) {
            funcitonType.append(FunctionTypeEnum.ADD.name().toLowerCase()).append(",");
        }
        if (editFunction.isSelected()) {
            funcitonType.append(FunctionTypeEnum.EDIT.name().toLowerCase()).append(",");
        }
        if (getByIdFunction.isSelected()) {
            funcitonType.append(FunctionTypeEnum.GET_BY_ID.name().toLowerCase()).append(",");
        }
        if (getAllFunciton.isSelected()) {
            funcitonType.append(FunctionTypeEnum.GET_ALL.name().toLowerCase()).append(",");
        }
        if (getByFiledFunction.isSelected()) {
            funcitonType.append(FunctionTypeEnum.GET_BY_FILED.name().toLowerCase()).append(",");
        }
        funcitonType=new StringBuilder(funcitonType.toString().substring(0,funcitonType.toString().length()-1));
        return funcitonType.toString();
    }

    /**
     * 获取需要生成的方法
     *
     * @return
     */
    private int getFunctionType() {
        int funcitonType = 0;
        if (deleteFunction.isSelected()) {
            funcitonType = funcitonType | FunctionTypeEnum.DELETE.getType();
        }
        if (addFunction.isSelected()) {
            funcitonType = funcitonType | FunctionTypeEnum.ADD.getType();
        }
        if (editFunction.isSelected()) {
            funcitonType = funcitonType | FunctionTypeEnum.EDIT.getType();
        }
        if (getByIdFunction.isSelected()) {
            funcitonType = funcitonType | FunctionTypeEnum.GET_BY_ID.getType();
        }
        if (getAllFunciton.isSelected()) {
            funcitonType = funcitonType | FunctionTypeEnum.GET_ALL.getType();
        }
        if (getByFiledFunction.isSelected()) {
            funcitonType = funcitonType | FunctionTypeEnum.GET_BY_FILED.getType();
        }
        return funcitonType;
    }

    public boolean haveFunctionProperty(FunctionTypeEnum functionTypeEnum) {
        String property = projectFileConfig.getProperty("function-build");
        String functionName = functionTypeEnum.name().toLowerCase();
        if (Utils.isEmpty(property) || property.toLowerCase().contains(functionName)) {
            return true;
        }
        return false;
    }

    public boolean haveFileProperty(FileTypeEnum fileTypeEnum) {
        String property = projectFileConfig.getProperty("type-build");
        String fileName = fileTypeEnum.name().toLowerCase();
        if (Utils.isEmpty(property) || property.toLowerCase().contains(fileName)) {
            return true;
        }
        return false;
    }
}
///