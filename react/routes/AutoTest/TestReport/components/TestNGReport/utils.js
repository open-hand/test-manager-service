/* eslint-disable */
import _ from 'lodash';

export const toArray = any => (any ? any instanceof Array ? any : [any] : []);
// 统计suite中所有test的通过状况

export const groupClassByStatus = (classes) => {
  const TestClasses = toArray(classes);
  const getTestByStatus = (type) => {
    const Classes = JSON.parse(JSON.stringify(TestClasses));
    Classes.forEach((TestClass) => {
      const len = TestClass['test-method'].filter(method => !method['is-config'] && method.status === type).length;
      if (len === 0) {
        TestClass.empty = true;
      }
      TestClass['test-method'] = TestClass['test-method'].filter(method => !method['is-config'] && method.status === type);
    });

    return Classes.filter(Class => !Class.empty);
  };
  const PassClasses = getTestByStatus('PASS');
  const SkipClasses = getTestByStatus('SKIP');
  const FailClasses = getTestByStatus('FAIL');
  return { PassClasses, SkipClasses, FailClasses };
};
// 统计一个test中的所有class的方法通过情况
export const calculateTestByClass = (classes) => {
  const TestClasses = toArray(classes);
  let pass = 0;
  let skip = 0;
  let fail = 0;
  let all = 0;
  TestClasses.forEach((TestClass) => {
    // console.log(TestClass);
    all += TestClass['test-method'].filter(method => !method['is-config']).length;
    pass += TestClass['test-method'].filter(method => !method['is-config'] && method.status === 'PASS').length;
    skip += TestClass['test-method'].filter(method => !method['is-config'] && method.status === 'SKIP').length;
    fail += TestClass['test-method'].filter(method => !method['is-config'] && method.status === 'FAIL').length;
  });
  return {
    pass, skip, fail, all, passPercent: isNaN(pass / all * 100) ? 0 : pass / all * 100,
  };
};
export const calculateTestByTest = (tests) => {
  let pass = 0;
  let skip = 0;
  let fail = 0;
  let all = 0;
  tests.forEach((test) => {
    // console.log(TestClass);
    all += calculateTestByClass(test.class).all;
    pass += calculateTestByClass(test.class).pass;
    skip += calculateTestByClass(test.class).skip;
    fail += calculateTestByClass(test.class).fail;
  });
  tests.push({
    name: '总计',
    pass,
    skip,
    fail,
    passPercent: isNaN(pass / all * 100) ? 0 : pass / all * 100,
    class: { 'test-method': [] },
  });
};
function findClassByTest(test, className) {
  const classes = toArray(test.class);
  return _.find(classes, { name: className });
}
function findMethodByClass(Class, methodName) {
  const methods = toArray(Class['test-method']);
  return _.find(methods, { name: methodName });
}

/**
 * 为method class test存入组信息, 并且进行筛选
 *
 * @export
 * @param {*} groups
 * @param {*} tests
 */
export function setGroup(groups, suite, selectedGroup) {
  toArray(suite.test).forEach((test) => {
    groups.forEach((group) => {
      const methods = toArray(group.method);
      methods.forEach((method) => {
        const targetClass = findClassByTest(test, method.class);
        if (targetClass) {
          const targetMethod = findMethodByClass(targetClass, method.name);
          if (targetMethod) {
            if (!test.groups) {
              test.groups = [];
            }
            if (!targetClass.groups) {
              targetClass.groups = [];
            }
            if (!targetMethod.groups) {
              targetMethod.groups = [];
            }
            targetMethod.groups.push(group.name);
            targetClass.groups.push(group.name);
            test.groups.push(group.name);
          }
        }
      });
    });
    toArray(test.class).forEach((Class) => {
      Class['test-method'] = toArray(Class['test-method']).filter(testMethod => selectedGroup.length === 0 || (testMethod.groups && _.intersection(testMethod.groups, selectedGroup).length > 0));
    });   
    test.class = toArray(test.class).filter(Class => selectedGroup.length === 0 || (Class.groups && _.intersection(Class.groups, selectedGroup).length > 0));
  }); 
  suite.test = toArray(suite.test).filter(test => selectedGroup.length === 0 || (test.groups && _.intersection(test.groups, selectedGroup).length > 0));
}
