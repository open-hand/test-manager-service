package io.choerodon.test.manager.infra.util;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.testng.TestNgCase;
import io.choerodon.test.manager.api.vo.testng.TestNgResult;
import io.choerodon.test.manager.api.vo.testng.TestNgSuite;
import io.choerodon.test.manager.api.vo.testng.TestNgTest;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private TestNgUtil() {
    }

    public static final Logger logger = LoggerFactory.getLogger(TestNgUtil.class);

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
    public static final String PARAM_PATH = "reporter-output/line";
    public static final String ERROR_PATH = "exception/message";
    public static final String TEST_PASSED = "PASS";
    public static final String TEST_FAILED = "FAIL";
    public static final String TEST_SKIPPED = "SKIP";
    public static final String INPUT = "[INPUT]";
    public static final String EXPECT = "[EXPECT]";

    public static TestNgResult parseXmlToObject(Document document) {
        Element root = document.getRootElement();
        root.attributes();
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
                handleCases(caseNodes, cases, test);
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

    /**
     * 处理case
     *
     * @param caseNodes
     * @param cases
     * @param test
     */
    private static void handleCases(List<Element> caseNodes, List<TestNgCase> cases, TestNgTest test) {
        for (Element caseNode : caseNodes) {
            Attribute attrIsConfig = caseNode.attribute(ATTR_IS_CONFIG);
            if (attrIsConfig != null && attrIsConfig.getValue().equals("true")) {
                continue;
            }
            TestNgCase testCase = new TestNgCase();
            reflectField(testCase, caseNode.attributes());
            //case有一个没通过则整个test都failed
            if (test.getStatus().equals(TEST_PASSED) && !testCase.getStatus().equals(TEST_PASSED)) {
                test.setStatus(TEST_FAILED);
            }
            //设置执行失败的错误信息
            handleError(testCase, caseNode);
            //获取步骤相关参数
            handleParams(testCase, caseNode);
            cases.add(testCase);
        }
    }

    /**
     * 获取失败异常信息
     *
     * @param testCase
     * @param caseNode
     */
    private static void handleError(TestNgCase testCase, Element caseNode) {
        List<Element> errorNdoes = caseNode.selectNodes(ERROR_PATH);
        for (Element errorNdoe : errorNdoes) {
            String text = errorNdoe.getText();
            String content = text.substring(text.indexOf('\n'), text.lastIndexOf('\n')).trim();
            testCase.setExceptionMessage(content);
        }
    }
    /**
     * 获取步骤相关参数
     *
     * @param testCase
     * @param caseNode
     */
    private static void handleParams(TestNgCase testCase, Element caseNode) {
        //只处理第一个input和第一个expect
        Boolean isInputHandle = true;
        Boolean isExpectHandle = true;
        List<Element> lineNodes = caseNode.selectNodes(PARAM_PATH);
        for (Element lineNode : lineNodes) {
            String text = lineNode.getText();
            String content = text.substring(text.indexOf('['), text.lastIndexOf('\n'));
            if (content.startsWith(INPUT) && isInputHandle) {
                handleInputParams(testCase, content);
                isInputHandle = false;
            } else if (content.startsWith(EXPECT) && isExpectHandle) {
                handleExpectParams(testCase, content);
                isExpectHandle = false;
            }
        }
    }

    /**
     * 输入参数的处理
     */
    private static void handleInputParams(TestNgCase testCase, String content) {
        String input = content.split("\\[INPUT\\]")[1];
        try {
            Map<String, Object> map = (Map<String, Object>) JSONObject.parse(input);
            String method = (String) map.get("method");
            if (method != null) {
                switch (method) {
                    case "GET":
                        input = "请求参数：" + map.get("queryParams") + "，路由参数：" + map.get("pathParams");
                        break;
                    case "POST":
                        input = "请求体：" + map.get("body");
                        break;
                    case "PUT":
                        input = "请求体：" + map.get("body");
                        break;
                    case "DELETE":
                        input = "请求参数：" + map.get("queryParams") + "，路由参数：" + map.get("pathParams");
                        break;
                    default:
                        break;
                }
            }
            input = input.length() > 255 ? input.substring(0, 255) : input;
        } catch (Exception e) {
            logger.info("handleInputParams is not Map");
        }
        testCase.setInputData(input);
    }

    /**
     * 预期结果的处理
     */
    private static void handleExpectParams(TestNgCase testCase, String content) {
        String expect = content.split("\\[EXPECT\\]")[1];
        try {
            List<Map<String, String>> list = (List<Map<String, String>>) JSONObject.parse(expect);
            if (list != null && !list.isEmpty()) {
                expect = "预期结果：";
                for (Map<String, String> map : list) {
                    expect += map.get("key") + "=" + map.get("value") + ";";
                }
            }
            expect = expect.length() > 255 ? expect.substring(0, 255) : expect;
        } catch (Exception e) {
            logger.info("handleExpectParams is not MapList");
        }
        testCase.setExpectData(expect);
    }

    private static void reflectField(Object obj, List<Attribute> attrs) {
        Map<String, String> attrMap = attrs.stream().collect(Collectors.toMap(Attribute::getName, Attribute::getValue));
        Class clz = obj.getClass();
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                String field = method.getName();
                field = field.substring(field.indexOf("set") + 3);
                field = field.toLowerCase().charAt(0) + field.substring(1);
                String value = attrMap.get(NameUtil.humpToMiddleline(field));
                try {
                    if (value != null) {
                        if (method.getParameterTypes()[0] == Long.class) {
                            method.invoke(obj, Long.valueOf(value));
                        } else {
                            method.invoke(obj, value);
                        }
                    }
                } catch (Exception e) {
                    throw new CommonException(e.getMessage());
                }
            }
        }
    }
}

