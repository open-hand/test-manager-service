/* eslint-disable react/no-this-in-sfc */
/* eslint-disable react/destructuring-assignment */
import React, {
  useRef, useState, useEffect, useContext,
} from 'react';
import { Choerodon } from '@choerodon/boot';
import { throttle } from 'lodash';
import { observer } from 'mobx-react-lite';
import { Tabs } from 'choerodon-ui';
import './EditIssue.less';
import { ResizeAble } from '@/components';
import {
  returnBeforeTextUpload, testCaseTableLink, testCaseDetailLink,
} from '@/common/utils';
import { updateIssue } from '@/api/IssueManageApi';
import Loading from '@/components/Loading';
import EditIssueContext from './stores';
import DataLogs from './Component/DataLogs';
import EditTestStepTable from './Component/EditTestStepTable';
import EditDetail from './EditDetail';
import Header from './Header';

const { TabPane } = Tabs;

function EditIssue() {
  const container = useRef();
  const {
    store, caseId, prefixCls, announcementHeight,
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
    this.props.reloadIssue();
    if (this.props.onUpdate) {
      this.props.onUpdate();
    }
  };


  /**
   * DataLog
   */
  const renderDataLogs = () => {
    const {
      createdBy,
      createrImageUrl, createrEmail,
      createrName, createrRealName, creationDate, issueTypeVO = {},
    } = issueInfo;
    const createLog = {
      email: createrEmail,
      field: issueTypeVO.typeCode,
      imageUrl: createrImageUrl,
      name: createrName,
      realName: createrRealName,
      lastUpdateDate: creationDate,
      lastUpdatedBy: createdBy,
      newString: 'caseNum',
      newValue: 'caseNum',
    };

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


  const handleUpdate = (newValue, done) => {
    // console.log('handleUpdate', newValue);
    const key = Object.keys(newValue)[0];
    const value = newValue[key];
    const { issueId, objectVersionNumber } = issueInfo;

    let issue = {
      issueId,
      objectVersionNumber,
    };
    switch (key) {
      case 'description': {
        if (value) {
          returnBeforeTextUpload(value, issue, updateIssue, 'description')
            .then((res) => {
              this.props.reloadIssue();
            }).catch(() => {
              done();
            });
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
        updateIssue(issue)
          .then((res) => {
            this.props.reloadIssue();
            if (this.props.onUpdate) {
              this.props.onUpdate();
            }
          }).catch(() => {
            done();
          });
        break;
      }
    }
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
              <Tabs>
                <TabPane tab="步骤" key="test">
                  <EditTestStepTable />
                </TabPane>
                <TabPane tab="详情" key="detail">
                  <EditDetail
                    onUpdate={handleUpdate}
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
