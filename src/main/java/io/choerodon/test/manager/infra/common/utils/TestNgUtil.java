package io.choerodon.test.manager.infra.common.utils;

import io.choerodon.test.manager.api.dto.testng.TestNgCase;
import io.choerodon.test.manager.api.dto.testng.TestNgResult;
import io.choerodon.test.manager.api.dto.testng.TestNgSuite;
import io.choerodon.test.manager.api.dto.testng.TestNgTest;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @since 2019/1/21
 */
public class TestNgUtil {
    public static final String ATTR_URL = "url";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_STATUS = "status";
    public static final String ATTR_DESC = "description";
    public static final String ATTR_METHOD_SIG = "signature";
    public static final String ATTR_GROUPS = "groups";
    public static final String ATTR_CLASS = "class";
    public static final String ATTR_TEST_INSTANCE_NAME = "test-instance-name";
    public static final String ATTR_INDEX = "index";
    public static final String ATTR_IS_NULL = "is-null";
    public static final String ATTR_PACKAGE = "package";
    public static final String ATTR_STARTED_AT = "started-at";
    public static final String ATTR_FINISHED_AT = "finished-at";
    public static final String ATTR_DURATION_MS = "duration-ms";
    public static final String ATTR_IS_CONFIG = "is-config";
    public static final String ATTR_DEPENDS_ON_METHODS = "depends-on-methods";
    public static final String ATTR_DEPENDS_ON_GROUPS = "depends-on-groups";
    public static final String ATTR_DATA_PROVIDER = "data-provider";
    public static final String SUITE_PATH = "//suite";
    public static final String TEST_PATH = "test";
    public static final String CASE_PATH = "class/test-method";
    public static final String TEST_PASSED = "PASS";
    public static final String TEST_FAILED = "FAIL";
    public static final String TEST_SKIPPED = "SKIP";

    public static TestNgResult parseXmlToObject(Document document) {
        Element root = document.getRootElement();
        root.attributes();//
        List<TestNgSuite> suites = new ArrayList<>();
        List<Element> suiteNodes = root.selectNodes(SUITE_PATH);
        for (Element suiteNode : suiteNodes) {
            List<TestNgTest> tests = new ArrayList<>();
            List<Element> testNodes = suiteNode.selectNodes(TEST_PATH);
            for (Element testNode : testNodes) {
                TestNgTest test = new TestNgTest();
                test.setStatus(TEST_PASSED);
                List<TestNgCase> cases = new ArrayList<>();
                List<Element> caseNodes = testNode.selectNodes(CASE_PATH);
                for (Element caseNode : caseNodes) {
                    Attribute attrIsConfig = caseNode.attribute(ATTR_IS_CONFIG);
                    if (attrIsConfig != null && attrIsConfig.getValue().equals("true")) {
                        continue;
                    }
                    TestNgCase testCase = new TestNgCase();
                    reflectField(testCase, caseNode.attributes());
                    //处理test的状态
                    if (test.getStatus().equals(TEST_PASSED) && testCase.getStatus().equals(TEST_FAILED)) {
                        test.setStatus(TEST_FAILED);
                    }
                    cases.add(testCase);
                }
                test.setCases(cases);
                reflectField(test, testNode.attributes());
                tests.add(test);
            }
            //跳过没有test的suite
            if (!tests.isEmpty()) {
                TestNgSuite suite = new TestNgSuite();
                suite.setTests(tests);
                reflectField(suite, suiteNode.attributes());
                suites.add(suite);
            }
        }
        TestNgResult result = new TestNgResult();
        result.setSuites(suites);
        reflectField(result, root.attributes());
        return result;
    }

    public static void reflectField(Object obj, List<Attribute> attrs) {
        Map<String, String> attrMap = attrs.stream().collect(Collectors.toMap(Attribute::getName, Attribute::getValue));
        Class clz = obj.getClass();
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                String field = method.getName();
                field = field.substring(field.indexOf("set") + 3);
                field = field.toLowerCase().charAt(0) + field.substring(1);
                String value = attrMap.get(NameUtil.HumpToMiddleline(field));
                try {
                    if (value != null) {
                        if (method.getParameterTypes()[0] == Long.class) {
                            method.invoke(obj, Long.valueOf(value));
                        } else {
                            method.invoke(obj, value);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

