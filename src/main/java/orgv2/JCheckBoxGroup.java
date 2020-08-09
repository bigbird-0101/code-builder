package main.java.orgv2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import main.java.common.Utils;
import main.java.config.ProjectFileConfig;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 复核复选框
 * @author fpp
 * @version 1.0
 * @date 2020/6/19 16:15
 */
public class JCheckBoxGroup{
    private JComponent jComponent;
    private int width;
    private int height;
    private String templateName;
    private ProjectFileConfig projectFileConfig;

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    /**
     * 每个字符的长度
     */
    private static final int EACH_CHAR_WIDTH=10;
    /**
     * 复选框组 几个复选框占一行
     */
    private static final int LINE_COUNT=3;

    /**
     * 复选框的高度
     */
    private static final int CHECK_HEIGHT=25;

    public JCheckBoxGroup(Set<String> stringSet, String templateName, JComponent jComponent, ProjectFileConfig projectFileConfig) {
        this.jComponent=jComponent;
        this.templateName=templateName;
        this.projectFileConfig=projectFileConfig;
        init(stringSet);
    }
    private void init(Set<String> stringSet){
        int a=1;
        int y=0;
        int tempBeforeStringWidthTotal=10;
        int totalWidth=0;
        int floors=1;
        List<Integer> totalWidths=new ArrayList<>();
        for(String s:stringSet) {
            JCheckBox checkBox01 = new JCheckBox(s);
            checkBox01.setName(s);
            checkBox01.setSelected(haveFunctionProperty(s));
            int currentWidth= Math.max(s.length() * EACH_CHAR_WIDTH, 80);
            checkBox01.setBounds(tempBeforeStringWidthTotal, y, currentWidth, CHECK_HEIGHT);
            if (a%LINE_COUNT==0){
                y = y + CHECK_HEIGHT;
                floors++;
            }
            tempBeforeStringWidthTotal=tempBeforeStringWidthTotal+currentWidth+20;
            if(a==LINE_COUNT){
                a=1;
                totalWidths.add(totalWidth+checkBox01.getWidth()+20*3);
                totalWidth=0;
                tempBeforeStringWidthTotal=10;
            }else {
                a++;
                totalWidth=checkBox01.getWidth()+totalWidth;
            }
            this.jComponent.add(checkBox01);
        }
        this.width=totalWidths.stream().max(Integer::compareTo).get();
        this.height=floors*CHECK_HEIGHT;
    }

    public boolean haveFunctionProperty(String functionName) {
        String property = projectFileConfig.getProperty("handle-template-build");
        if(Utils.isEmpty(property)){
            return false;
        }
        JSONArray jsonArray= (JSONArray) JSON.parse(property);
        JSONObject jsonObject= Utils.getJsonObjByKey(jsonArray,templateName);
        if (null!=jsonObject.getJSONArray(templateName)&&jsonObject.getJSONArray(templateName).contains(functionName)) {
            return true;
        }
        return false;
    }

}
