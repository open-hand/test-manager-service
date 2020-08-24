/* eslint-disable react/no-this-in-sfc */
/* eslint-disable react/destructuring-assignment */
import React, {
  useRef, useEffect, useContext,
} from 'react';
import { Choerodon } from '@choerodon/boot';
import { throttle } from 'lodash';
import { Spin } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { Tabs } from 'choerodon-ui';
import './EditIssue.less';
import { ResizeAble } from '@/components';
import {
  returnBeforeTextUpload, testCaseTableLink, testCaseDetailLink,
} from '@/common/utils';
import { updateIssue, getLabels } from '@/api/IssueManageApi';
import Loading from '@/components/Loading';
import EditIssueContext from './stores';
import DataLogs from './Component/DataLogs';
import EditTestStepTable from './Component/EditTestStepTable';
import Detail from './Detail';
import Header from './Header';

const { TabPane } = Tabs;

function EditIssue() {
  const container = useRef();
  const {
    store, caseId, prefixCls, announcementHeight, onUpdate, IssueStore,
  } = useContext(EditIssueContext);
  const { issueInfo, dataLogs, loading } = store;
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
      field: 'IssueNum',
      imageUrl,
      name,
      realName,
      loginName,
      lastUpdateDate: creationDate,
      // lastUpdatedBy: createdBy,
      newString: 'caseNum',
      newValue: 'caseNum',
    };

    // console.log([...dataLogs, createLog]);

    return (
      <DataLogs
        datalogs={[...dataLogs, createLog]}
      />
    );
  };


  const handleResizeEnd = ({ width }) => {
    localStorage.setItem('agile.EditIssue.width', `${width}px`);
  };


  const handleResize = throttle(({ width }) => {
    setQuery(width);
  }, 150);


  const handleUpdate = async (newValue, done) => {
    const key = Object.keys(newValue)[0];
    const value = newValue[key];
    const { objectVersionNumber } = issueInfo;

    let issue = {
      caseId,
      objectVersionNumber,
    };
    switch (key) {
      case 'description': {
        if (value) {
          await returnBeforeTextUpload(value, issue, updateIssue, 'description');
          store.loadIssueData();
        }
        break;
      }
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
      zIndex: 101,
      overflowY: 'hidden',
      overflowX: 'visible',
    }}
    >
      <ResizeAble
        modes={['left']}
        size={{
          maxWidth: window.innerWidth * 0.6,
          minWidth: 440,
        }}
        defaultSize={{
          width: localStorage.getItem('agile.EditIssue.width') || 600,
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
            <Header onUpdate={handleUpdate} />
            <div className={`${prefixCls}-content-bottom`} id="scroll-area" style={{ position: 'relative' }}>
              <Tabs onChange={handleTabChange}>
                <TabPane tab="步骤" key="test">
                  <EditTestStepTable onUpdateDetail={handleUpdate} />
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
