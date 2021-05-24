import React, { useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Icon, Button, Tooltip } from 'choerodon-ui/pro';
import { map } from 'lodash';
import { injectIntl } from 'react-intl';

import CaseListItem from './components/case';
import { openTestCaseModal } from './TestCaseModal';
import useTestLinkStore from './TestLinkStore';

const TestLink = ({
  reloadIssue, issueId, disabled, intl, testLinkStoreRef,
}) => {
  const testLinkStore = useTestLinkStore(issueId);
  useEffect(() => {
    Object.assign(testLinkStoreRef, { current: testLinkStore });
  }, [testLinkStoreRef, testLinkStore]);
  useEffect(() => {
    if (issueId) {
      testLinkStore.loadData();
    }
  }, [issueId, testLinkStore]);
  const renderLinkList = (link, i) => (
    <CaseListItem
      link={link}
      i={i}
      intl={intl}
      testLinkStore={testLinkStore}
      disabled={disabled}
      onRefresh={() => {
        reloadIssue(issueId);
      }}
    />
  );

  const renderLinkIssues = () => (
    <div className="c7n-tasks">
      { map(testLinkStore.data || [], (linkIssue, i) => renderLinkList(linkIssue, i))}
    </div>
  );

  return (
    <div id="link_test">
      <div style={{ margin: '30px 0 20px 0', borderBottom: '1px solid #D8D8D8' }} />
      <div className="c7n-title-wrapper">
        <div className="c7n-title-left">
          <span>测试用例</span>
        </div>
        {
          !disabled && (
            <div className="c7n-title-right" style={{ marginLeft: '14px' }}>
              <Tooltip placement="topRight" title="关联测试用例" getPopupContainer={(triggerNode) => triggerNode.parentNode}>
                <Button onClick={() => openTestCaseModal(testLinkStore, intl)}>
                  <Icon type="playlist_add icon" />
                </Button>
              </Tooltip>
            </div>
          )
        }
      </div>
      {renderLinkIssues()}
    </div>
  );
};

export default injectIntl(observer(TestLink));
