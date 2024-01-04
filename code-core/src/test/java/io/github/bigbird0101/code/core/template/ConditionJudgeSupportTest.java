package io.github.bigbird0101.code.core.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test  not in  ['true','true2']");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("test  not in  ['true','true2']", resultList,
                dataModal);
        // Verify the results
        assertFalse(result);

        // Run the test
        resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test  not in  ['true1','true2']");
        result = conditionJudgeSupportUnderTest.meetConditions("test  not in   ['true1','true2']", resultList,
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
        List<String> resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test in  ['true','true2']");
        boolean result = conditionJudgeSupportUnderTest.meetConditions("test in  ['true','true2']", resultList,
                dataModal);
        // Verify the results
        assertTrue(result);

        // Run the test
        resultList = conditionJudgeSupportUnderTest.checkFiled(dataModal, "test in  ['true1','true2']");
        result = conditionJudgeSupportUnderTest.meetConditions("test in   ['true1','true2']", resultList,
                dataModal);
        // Verify the results
        assertFalse(result);
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
}
