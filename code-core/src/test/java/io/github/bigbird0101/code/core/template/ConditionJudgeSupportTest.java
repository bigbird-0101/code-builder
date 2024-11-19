package io.github.bigbird0101.code.core.template;

import cn.hutool.core.map.MapUtil;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConditionJudgeSupportTest {

    private ConditionJudgeSupport conditionJudgeSupportUnderTest;

    @BeforeEach
    void setUp() {
        conditionJudgeSupportUnderTest = new ConditionJudgeSupport();
    }

    @Test
    void testCheckFiledMeetConditions() {
        // Setup
        final Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("test","123");
        // Run the test
        final List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test");
        final boolean result = conditionJudgeSupportUnderTest.meetConditions("test", resultList,
                dataModal);
        // Verify the results
        assertFalse(result);
    }

    @Test
    void testCheckFiledMeetConditions2() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("test","true");
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("test", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);

        // Run the test
        resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "!test");
        result = conditionJudgeSupportUnderTest.meetConditions("!test", resultList,
                dataModal);
        // Verify the results
        assertFalse(result);
    }


    @Test
    void testCheckFiledMeetConditionsNotIn() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("test","true");
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test  not in  ['true', 'true2']");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("test  not in  ['true', 'true2']", resultList,
                dataModal);
        // Verify the results
        assertFalse(result);

        // Run the test
        resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test  not in  ['true1', 'true2']");
        result = conditionJudgeSupportUnderTest.meetConditions("test  not in   ['true1', 'true2']", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);
    }


    @Test
    void testCheckFiledMeetConditionsIn() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("test","true");
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test in  ['true', 'true2']");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("test in  ['true', 'true2']", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);

        // Run the test
        resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test in  ['true1', 'true2']");
        result = conditionJudgeSupportUnderTest.meetConditions("test in   ['true1', 'true2']", resultList,
                dataModal);
        // Verify the results
        assertFalse(result);
    }

    @Test
    void testCheckFiledMeetConditionsIn23() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("test","true");
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test in 'true'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("test in 'true'", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);

        // Run the test
        resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test in  'true1'");
        result = conditionJudgeSupportUnderTest.meetConditions("test in  'true1'", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);
    }


    @Test
    void testCheckFiledMeetConditionsIn2() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("test","true");
        Map<String, Object> dataModal2 = new HashMap<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("true");
        dataModal2.put("testb", arrayList);
        dataModal.put("testa",dataModal2);
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test in  testa.testb");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("test in  testa.testb", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);
    }

    @Test
    void testCheckFiledMeetConditionsAnd() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("test","true");
        Map<String, Object> dataModal2 = new HashMap<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("true");
        dataModal2.put("testb", arrayList);
        dataModal.put("testa",dataModal2);
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test in  testa.testb &&test");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("test in  testa.testb&&test", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);

        resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test in  testa.testb &&!test");
        result = conditionJudgeSupportUnderTest.meetConditions("test in  testa.testb&&!test", resultList,
                dataModal);
        // Verify the results
        assertFalse(result);
    }

    @Test
    void testCheckFiledMeetConditionsOr() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("test","true");
        Map<String, Object> dataModal2 = new HashMap<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("true");
        dataModal2.put("testb", arrayList);
        dataModal.put("testa",dataModal2);
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test in  testa.testb || test");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("test in  testa.testb||test", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);

        resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test not in  testa.testb || !test");
        result = conditionJudgeSupportUnderTest.meetConditions("test not in  testa.testb ||!test", resultList,
                dataModal);
        // Verify the results
        assertFalse(result);
    }


    @Test
    void testCheckFiledMeetConditionsNotEquals() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("test","true");
        Map<String, Object> dataModal2 = new HashMap<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("true");
        dataModal2.put("testb", arrayList);
        dataModal.put("testa",dataModal2);
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test !=true || !test");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("test !=true || !test", resultList,
                dataModal);
        // Verify the results
        assertFalse(result);
    }

    @Test
    void testCheckFiledMeetConditionsEquals() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("test","true");
        Map<String, Object> dataModal2 = new HashMap<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("true");
        dataModal2.put("testb", arrayList);
        dataModal.put("testa",dataModal2);
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test ==true || !test");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("test ==true || !test", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);


        // Run the test
        resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test =='true' || !test");
        result = conditionJudgeSupportUnderTest.meetConditions("test =='true' || !test", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);
    }


    @Test
    void testCheckFiledMeetConditionsNotIn3() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("column", MapUtil.builder().put("javaType","String").put("name","created_by").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name not in " +
                "['id','create_time','create_by', " +
                "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                "'updated_by'] && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name not in " +
                        "['id','create_time','create_by', " +
                        "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                        "'updated_by'] && column.javaType=='String'", resultList,
                dataModal);
        // Verify the results
        assertFalse(result);
    }


    @Test
    void testCheckFiledMeetConditionsAnyMatch() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name anyMatch " +
                "['id','create_time','create_by', " +
                "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                "'updated_by'] && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name anyMatch " +
                        "['id','create_time','create_by', " +
                        "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                        "'updated_by'] && column.javaType=='String'", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);
    }

    @Test
    void testCheckFiledMeetConditionsAnyMatch4() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        Map<Object, Object> build = MapUtil.builder().put("javaType", "String").put("name", "created_by2").build();
        Map<Object, Object> build2 = MapUtil.builder().put("javaType", "String").put("name", "created_by3").build();
        dataModal.put("tableInfo", MapUtil.builder().put("columnList", Arrays.asList(build, build2)).build());
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by2").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "tableInfo.columnList anyMatch ('name'," +
                "['id','create_time','create_by', " +
                "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                "'updated_by'])&& column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("tableInfo.columnList anyMatch ('name'," +
                        "['id','create_time','create_by', " +
                        "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                        "'updated_by']) && column.javaType=='String'", resultList,
                dataModal);
        assertFalse(result);
    }

    @Test
    void testCheckFiledMeetConditionsAnyMatch5() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        Map<Object, Object> build = MapUtil.builder().put("javaType", "String").put("name", "created_by").build();
        Map<Object, Object> build2 = MapUtil.builder().put("javaType", "String").put("name", "created_by4").build();
        dataModal.put("tableInfo", MapUtil.builder().put("columnList", Arrays.asList(build, build2)).build());
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by2").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "tableInfo.columnList anyMatch ('name'," +
                "['id','create_time','create_by', " +
                "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                "'updated_by'])&& column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("tableInfo.columnList anyMatch ('name'," +
                        "['id','create_time','create_by', " +
                        "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                        "'updated_by']) && column.javaType=='String'", resultList,
                dataModal);
        assertTrue(result);
    }

    @Test
    void testCheckFiledMeetConditionsAllMatch6() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        Map<Object, Object> build = MapUtil.builder().put("javaType", "String").put("name", "created_by").build();
        Map<Object, Object> build2 = MapUtil.builder().put("javaType", "String").put("name", "updated_time").build();
        dataModal.put("tableInfo", MapUtil.builder().put("columnList", Arrays.asList(build, build2)).build());
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by2").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "tableInfo.columnList allMatch ('name'," +
                "['id','create_time','create_by', " +
                "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                "'updated_by'])&& column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("tableInfo.columnList allMatch ('name'," +
                        "['id','create_time','create_by', " +
                        "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                        "'updated_by']) && column.javaType=='String'", resultList,
                dataModal);
        assertTrue(result);
    }

    @Test
    void testCheckFiledMeetConditionsAnyMatch3() {
        assertThrows(TemplateResolveException.class, () -> {
            // Setup
            Map<String, Object> dataModal = new HashMap<>();
            dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by2").build());
            // Run the test
            List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name anyMatch ('name'," +
                    "['id','create_time','create_by', " +
                    "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                    "'updated_by'])&& column.javaType=='String'");
            boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name anyMatch ('name'," +
                            "['id','create_time','create_by', " +
                            "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                            "'updated_by']) && column.javaType=='String'", resultList,
                    dataModal);
        });
    }

    @Test
    void testCheckFiledMeetConditionsAnyMatch2() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by2").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name anyMatch " +
                "['id','create_time','create_by', " +
                "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                "'updated_by'] && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name anyMatch " +
                        "['id','create_time','create_by', " +
                        "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                        "'updated_by'] && column.javaType=='String'", resultList,
                dataModal);
        // Verify the results
        assertFalse(result);
    }

    @Test
    void testCheckFiledMeetConditionsAnyMatch1() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name",
                Arrays.asList("id", "create_time", "create_by")).build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name anyMatch " +
                "['id','create_time','create_by', " +
                "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                "'updated_by'] && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name anyMatch " +
                        "['id','create_time','create_by', " +
                        "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                        "'updated_by'] && column.javaType=='String'", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);
    }

    @Test
    void testCheckFiledMeetConditionsNoMatch() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name noneMatch " +
                "['id','create_time','create_by', " +
                "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                "'updated_by'] && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name noneMatch " +
                        "['id','create_time','create_by', " +
                        "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                        "'updated_by'] && column.javaType=='String'", resultList,
                dataModal);
        // Verify the results
        assertFalse(result);
    }

    @Test
    void testCheckFiledMeetConditionsNoMatch2() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by2").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name noneMatch " +
                "['id','create_time','create_by', " +
                "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                "'updated_by'] && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name noneMatch " +
                        "['id','create_time','create_by', " +
                        "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                        "'updated_by'] && column.javaType=='String'", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);
    }

    @Test
    void testCheckFiledMeetConditionsAllMatch() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name",
                Arrays.asList("id", "create_time", "create_by")).build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name allMatch " +
                "['id','create_time','create_by'] && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name allMatch " +
                        "['id','create_time','create_by'] && column.javaType=='String'", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);
    }

    @Test
    void testCheckFiledMeetConditionsAllMatch2() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name allMatch " +
                "['id','create_time','create_by', " +
                "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                "'updated_by'] && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name allMatch " +
                        "['id','create_time','create_by', " +
                        "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                        "'updated_by'] && column.javaType=='String'", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);
    }


    @Test
    void testCheckFiledMeetConditionsContains() {
        assertThrows(TemplateResolveException.class, () -> {
            // Setup
            Map<String, Object> dataModal = new HashMap<>();
            dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by").build());
            // Run the test
            List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name contains " +
                    "['id','create_time','create_by', " +
                    "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                    "'updated_by'] && column.javaType=='String'");
            boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name contains " +
                            "['id','create_time','create_by', " +
                            "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                            "'updated_by'] && column.javaType=='String'", resultList,
                    dataModal);
            // Verify the results
            assertTrue(result);
        });
    }

    @Test
    void testCheckFiledMeetConditionsContains2() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name contains 'created'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name contains 'created'", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);
    }


    @Test
    void testCheckFiledMeetConditionsContains3() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name contains 'created2'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name contains 'created2'", resultList,
                dataModal);
        // Verify the results
        assertFalse(result);
    }

    @Test
    void testCheckFiledMeetConditionsContainsIgnoreCase() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name containsIgnoreCase 'created2'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name containsIgnoreCase 'created2'", resultList,
                dataModal);
        // Verify the results
        assertFalse(result);
    }


    @Test
    void testCheckFiledMeetConditionsContainsIgnoreCase2() {
        assertThrows(TemplateResolveException.class, () -> {
            // Setup
            Map<String, Object> dataModal = new HashMap<>();
            dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by").build());
            // Run the test
            List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name containsIgnoreCase " +
                    "['id','create_time','create_by', " +
                    "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                    "'updated_by'] && column.javaType=='String'");
            boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name containsIgnoreCase " +
                            "['id','create_time','create_by', " +
                            "'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', " +
                            "'updated_by'] && column.javaType=='String'", resultList,
                    dataModal);
            // Verify the results
            assertTrue(result);
        });
    }

    @Test
    void testCheckFiledMeetConditionsContainsIgnoreCase3() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name containsIgnoreCase 'created'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name containsIgnoreCase 'created'", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);
    }

    @Test
    void testCheckFiledMeetConditionsContainsIgnoreCase4() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "column.name containsIgnoreCase 'Created'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("column.name containsIgnoreCase 'Created'", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);
    }


    @Test
    void testCheckFiledMeetConditionsContainsAnyIgnoreCase5() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        Map<Object, Object> build = MapUtil.builder().put("javaType", "String").put("name", "created_by").build();
        Map<Object, Object> build2 = MapUtil.builder().put("javaType", "String").put("name", "updated_time").build();
        dataModal.put("tableInfo", MapUtil.builder().put("columnList", Arrays.asList(build, build2)).build());
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by2").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "tableInfo.columnList containsAnyIgnoreCase ('name'," +
                "['created']) && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("tableInfo.columnList containsAnyIgnoreCase ('name'," +
                "['created']) && column.javaType=='String'", resultList, dataModal);
        assertTrue(result);
    }

    @Test
    void testCheckFiledMeetConditionsContainsAnyIgnoreCase6() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        Map<Object, Object> build = MapUtil.builder().put("javaType", "String").put("name", "created_by").build();
        Map<Object, Object> build2 = MapUtil.builder().put("javaType", "String").put("name", "updated_time").build();
        dataModal.put("tableInfo", MapUtil.builder().put("columnList", Arrays.asList(build, build2)).build());
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by2").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "tableInfo.columnList containsAnyIgnoreCase ('name'," +
                "['Dcreated']) && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("tableInfo.columnList containsAnyIgnoreCase ('name'," +
                "['Dcreated']) && column.javaType=='String'", resultList, dataModal);
        assertFalse(result);
    }

    @Test
    void testCheckFiledMeetConditionsContainsAnyIgnoreCase7() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        Map<Object, Object> build = MapUtil.builder().put("javaType", "String").put("name", "created_by").build();
        Map<Object, Object> build2 = MapUtil.builder().put("javaType", "String").put("name", "updated_time").build();
        dataModal.put("tableInfo", MapUtil.builder().put("columnList", Arrays.asList(build, build2)).build());
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by2").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "tableInfo.columnList containsAnyIgnoreCase ('name'," +
                "['Created']) && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("tableInfo.columnList containsAnyIgnoreCase ('name'," +
                "['Created']) && column.javaType=='String'", resultList, dataModal);
        assertTrue(result);
    }

    @Test
    void testCheckFiledMeetConditionsContainsAnyIgnoreCase8() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        Map<Object, Object> build = MapUtil.builder().put("javaType", "String").put("name", "created_by").build();
        Map<Object, Object> build2 = MapUtil.builder().put("javaType", "String").put("name", "updated_time").build();
        dataModal.put("tableInfo", MapUtil.builder().put("columnList", Arrays.asList(build, build2)).build());
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by2").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "tableInfo.columnList containsAnyIgnoreCase ('name'," +
                "['Created']) && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("tableInfo.columnList containsAnyIgnoreCase ('name'," +
                "['Created']) && column.javaType=='String'", resultList, dataModal);
        assertTrue(result);
    }

    @Test
    void testCheckFiledMeetConditionsContainsAllIgnoreCase() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        Map<Object, Object> build = MapUtil.builder().put("javaType", "String").put("name", "created_by").build();
        Map<Object, Object> build2 = MapUtil.builder().put("javaType", "String").put("name", "updated_time").build();
        dataModal.put("tableInfo", MapUtil.builder().put("columnList", Arrays.asList(build, build2)).build());
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by2").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "tableInfo.columnList containsAll ('name'," +
                "['ed']) && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("tableInfo.columnList containsAll ('name'," +
                "['ed']) && column.javaType=='String'", resultList, dataModal);
        assertTrue(result);
    }


    @Test
    void testCheckFiledMeetConditionsContainsAllIgnoreCase2() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        Map<Object, Object> build = MapUtil.builder().put("javaType", "String").put("name", "created_by").build();
        Map<Object, Object> build2 = MapUtil.builder().put("javaType", "String").put("name", "updated_time").build();
        dataModal.put("tableInfo", MapUtil.builder().put("columnList", Arrays.asList(build, build2)).build());
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by2").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "tableInfo.columnList containsAll ('name'," +
                "['edd']) && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("tableInfo.columnList containsAll ('name'," +
                "['edd']) && column.javaType=='String'", resultList, dataModal);
        assertFalse(result);
    }

    @Test
    void testCheckFiledMeetConditionsContainsAllIgnoreCase3() {
        // Setup
        Map<String, Object> dataModal = new HashMap<>();
        Map<Object, Object> build = MapUtil.builder().put("javaType", "String").put("name", "created_by").build();
        Map<Object, Object> build2 = MapUtil.builder().put("javaType", "String").put("name", "updated_time").build();
        dataModal.put("tableInfo", MapUtil.builder().put("columnList", Arrays.asList(build, build2)).build());
        dataModal.put("column", MapUtil.builder().put("javaType", "String").put("name", "created_by2").build());
        // Run the test
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "tableInfo.columnList containsAll ('name'," +
                "['ed','edd']) && column.javaType=='String'");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("tableInfo.columnList containsAll ('name'," +
                "['ed','edd']) && column.javaType=='String'", resultList, dataModal);
        assertFalse(result);
    }
}
