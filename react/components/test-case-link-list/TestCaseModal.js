/* eslint-disable react/jsx-props-no-spreading */
import React, { useCallback, useEffect, useMemo } from 'react';
import {
  Select, Form, DataSet, Modal,
} from 'choerodon-ui/pro';
import useSelect from '@choerodon/agile/lib/hooks/useSelect';

function TestCaseForm({ testLinkStore, modal }) {
  const ds = useMemo(() => new DataSet({
    autoCreate: true,
    selection: false,
    fields: [
      { name: 'caseIds', label: '关联的用例', required: true },
    ],
  }), []);
  const handleSubmit = useCallback(async () => {
    if (await ds.validate()) {
      const { caseIds } = ds.current.toData() || {};
      await testLinkStore.createLink(caseIds);
    }
    return true;
  }, [ds, testLinkStore]);
  useEffect(() => { modal.handleOk(handleSubmit); }, [handleSubmit, modal]);
  const selectProps = useSelect({
    name: 'caseIds',
    textField: 'caseNum',
    valueField: 'caseId',
    request: ({ page, filter }) => testLinkStore.loadCaseList({ page, filter }),
    paging: true,
    optionRenderer: (item = {}) => `${item.caseNum} ${item.summary}`,
  });
  return (
    <Form dataSet={ds}>
      <Select
        name="caseIds"
        multiple
        maxTagCount={3}
        maxLength={12}
        searchMatcher="content"
        {...selectProps}
      />
    </Form>
  );
}
const openTestCaseModal = (testLinkStore) => {
  Modal.open({
    key: Modal.key(),
    title: '关联测试用例',
    children: <TestCaseForm testLinkStore={testLinkStore} />,
    style: {
      width: 380,
    },
    drawer: true,

  });
};
// eslint-disable-next-line import/prefer-default-export
export { openTestCaseModal };
