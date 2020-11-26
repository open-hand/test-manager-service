/* eslint-disable react/jsx-props-no-spreading */
import React, { useCallback, useEffect, useMemo } from 'react';
import {
  Select, Form, DataSet, Modal, Button,
} from 'choerodon-ui/pro';
import CreateIssue from '@/components/create-test-case';
import useSelect from './useSelect';

function TestCaseForm({ testLinkStore, modal, intl }) {
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
  async function handleCreateIssue(data) {
    return testLinkStore.createCaseAndLink(data);
  }
  const handleOpenCreateIssue = () => {
    Modal.open({
      key: 'createCase_link_Issue',
      // title:<FormattedMessage id='issue_create_name'  />,
      title: intl.formatMessage({ id: 'issue_create_name', defaultMessage: '创建测试用例并关联' }),
      drawer: true,
      style: {
        width: 740,
      },
      children: (
        <CreateIssue
          request={handleCreateIssue.bind(this)}
          intl={intl}
          onOk={(res) => res && modal.close()}
        // caseId={clickIssue && clickIssue.caseId}
        />
      ),
      okText: '创建',
    });
  };
  return (
    <>
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
      <Button color="primary" icon="playlist_add" onClick={handleOpenCreateIssue}>创建测试用例并关联</Button>
    </>
  );
}
const openTestCaseModal = (testLinkStore, intl) => {
  Modal.open({
    key: Modal.key(),
    title: '关联测试用例',
    children: <TestCaseForm testLinkStore={testLinkStore} intl={intl} />,
    style: {
      width: 380,
    },
    drawer: true,

  });
};
// eslint-disable-next-line import/prefer-default-export
export { openTestCaseModal };
