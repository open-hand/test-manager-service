/* eslint-disable react/no-this-in-sfc */
/* eslint-disable react/destructuring-assignment */
import React, {
  useRef, useEffect, useContext,
} from 'react';
import { Choerodon } from '@choerodon/boot';
import { throttle } from 'lodash';
import { Tabs } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';

import './EditIssue.less';
import { ResizeAble } from '@/components';
import { updateIssue } from '@/api/IssueManageApi';
import Loading from '@/components/Loading';
import EditIssueContext from './stores';
import DataLogs from './Component/DataLogs';
import EditTestStepTable from './Component/EditTestStepTable';
import Detail from './Detail';
import Header from './Header';

const { TabPane } = Tabs;
function getStoredWidth() {
  const stored = localStorage.getItem('test.EditIssue.width');
  if (stored === null) {
    return undefined;
  }
  const width = Number(stored);
  return Number.isNaN(width) ? undefined : width;
}

function EditIssue() {
  const container = useRef();
  const {
    store, caseId, prefixCls, announcementHeight, onUpdate, IssueStore,
  } = useContext(EditIssueContext);
  const { issueInfo, dataLogs, loading } = store;
  const maxWidth = window.innerWidth * 0.6;
  const minWidth = 440;
  const defaultWidth = Math.max(minWidth, Math.min(maxWidth, getStoredWidth() ?? 640));

  const setQuery = (width = container.current.clientWidth) => {
    if (width <= 600) {
      container.current.setAttribute('max-width', '600px');
    } else {
      container.current.removeAttribute('max-width');
    }
  };
  useEffect(() => {
    setQuery();
  }, []);
  useEffect(() => {
    store.loadIssueData(caseId);
  }, [caseId, store]);

  const handleCreateLinkIssue = () => {
    store.loadIssueData();
    onUpdate();
  };

  /**
   * DataLog
   */
  const renderDataLogs = () => {
    const {
      createUser, creationDate,
    } = issueInfo || {};
    const {
      email, imageUrl, loginName, name, realName,
    } = createUser || {};

    const createLog = {
      email,
      field: 'createInitType',
      imageUrl,
      name,
      realName,
      loginName,
      lastUpdateDate: creationDate,
      newString: '测试用例',
      newValue: '测试用例',
    };

    return (
      <DataLogs
        datalogs={[...dataLogs, createLog]}
      />
    );
  };

  const handleResizeEnd = ({ width }) => {
    localStorage.setItem('test.EditIssue.width', width);
  };

  const handleResize = throttle(({ width }) => {
    setQuery(width);
  }, 150);

  const handleUpdate = async (newValue, done = () => {}) => {
    const key = Object.keys(newValue)[0];
    const value = newValue[key];
    const { objectVersionNumber } = issueInfo;

    let issue = {
      caseId,
      objectVersionNumber,
    };
    switch (key) {
      default: {
        if (key === 'summary' && value === '') {
          Choerodon.prompt('用例名不可为空！');
          done();
          break;
        }
        issue = { ...issue, ...newValue };
        await updateIssue(issue);
        await store.loadIssueData();
        onUpdate();
        done(); // done() 为了更新完之后用服务器数据替换之前选中的数据，因此应该等待前边数据加载完再更新
        break;
      }
    }
  };

  const handleTabChange = () => {
    store.loadIssueData();
  };

  return (
    <div style={{
      position: 'fixed',
      right: 0,
      top: 50 + announcementHeight,
      bottom: 0,
      zIndex: 998,
      overflowY: 'hidden',
      overflowX: 'visible',
    }}
    >
      <ResizeAble
        modes={['left']}
        size={{
          maxWidth,
          minWidth,
        }}
        defaultSize={{
          width: defaultWidth,
          height: '100%',
        }}
        onResizeEnd={handleResizeEnd}
        onResize={handleResize}
      >
        <div className={prefixCls} ref={container}>
          <div className={`${prefixCls}-divider`} />
          {
            loading && <Loading />
          }
          <div className={`${prefixCls}-content`}>
            <Header onUpdate={handleUpdate} IssueStore={IssueStore} />
            <div className={`${prefixCls}-content-bottom`} id="scroll-area" style={{ position: 'relative' }}>
              <Tabs onChange={handleTabChange}>
                <TabPane tab="步骤" key="test">
                  <EditTestStepTable onUpdateDetail={handleUpdate} IssueStore={IssueStore} />
                </TabPane>
                <TabPane tab="详情" key="detail">
                  <Detail
                    onUpdate={handleUpdate}
                    handleCreateLinkIssue={handleCreateLinkIssue}
                  />
                </TabPane>
                <TabPane tab="记录" key="log">
                  {renderDataLogs()}
                </TabPane>
              </Tabs>
            </div>
          </div>
        </div>
      </ResizeAble>
    </div>
  );
}
export default observer(EditIssue);
