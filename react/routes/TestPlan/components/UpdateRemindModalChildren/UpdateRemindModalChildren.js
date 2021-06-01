import React, {
  useEffect, useState, useContext, useMemo,
} from 'react';
import {
  toJS,
} from 'mobx';
import { Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import {
  Input, Icon, Spin, Tree,
} from 'choerodon-ui';
import { DataSet } from 'choerodon-ui/pro';
import _ from 'lodash';
import UpdateContent from './component/UpdateContent';
import User from '../../../../components/User';
import './UpdateRemindModalChildren.less';
import UpdateStepTableDataSet from '../../stores/UpdateStepTableDataSet';
import { getUpdateCompared } from '../../../../api/TestPlanApi';

const prefix = 'c7ntest-testPlan-updateRemind';

const UpdateRemindModalChildren = (props) => {
  const { testPlanStore, executeId, cycleName } = props;
  const newStepTableDataSet = useMemo(() => new DataSet(UpdateStepTableDataSet({ stepData: (testPlanStore.comparedInfo.testCase && testPlanStore.comparedInfo.testCase.testCaseStepS) || [] })), [testPlanStore.comparedInfo.testCase]);
  const oldStepTableDataSet = useMemo(() => new DataSet(UpdateStepTableDataSet({ stepData: (testPlanStore.comparedInfo.testCycleCase && testPlanStore.comparedInfo.testCycleCase.cycleCaseStep) || [] })), [testPlanStore.comparedInfo.testCycleCase]);
  const [loading, setLoading] = useState(false);
  useEffect(() => {
    const getUpdateContent = () => {
      setLoading(true);
      getUpdateCompared(executeId).then((res) => {
        testPlanStore.setComparedInfo(res);
        setLoading(false);
      }).catch((e) => {
        Choerodon.prompt('获取更新详情失败');
      });
    };
    getUpdateContent();
  }, [executeId, setLoading, testPlanStore]);

  const { comparedInfo } = testPlanStore;
  const { testCase, testCycleCase } = comparedInfo;
  const { lastUpdateUser, lastUpdateDate } = testCase || {};

  return (
    <div className={`${prefix}-modal-children`}>
      <Spin spinning={loading}>
        <div className={`${prefix}-item`}>
          <span className={`${prefix}-item-field`}>更新人</span>
          <span className={`${prefix}-item-value`}><User user={lastUpdateUser} style={{ color: 'var(--text-color)' }} /></span>
        </div>
        <div className={`${prefix}-item`}>
          <span className={`${prefix}-item-field`}>更新时间</span>
          <span className={`${prefix}-item-value`}>{lastUpdateDate || ''}</span>
        </div>
        <div className={`${prefix}-updateContent`}>
          <span className={`${prefix}-updateContent-span`}>变更内容</span>
          <div className={`${prefix}-updateContent-div`}>
            <UpdateContent tag="old" updateData={testCycleCase} dataSet={oldStepTableDataSet} cycleName={cycleName} />
            <div className={`${prefix}-updateContent-div-icon`}>
              <Icon type="arrow_forward" />
            </div>
            <UpdateContent tag="new" updateData={testCase} dataSet={newStepTableDataSet} cycleName={cycleName} />
          </div>
        </div>
      </Spin>
    </div>
  );
};

export default observer(UpdateRemindModalChildren);
