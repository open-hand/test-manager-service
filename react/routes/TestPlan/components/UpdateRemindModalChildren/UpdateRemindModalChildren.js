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
import './UpdateRemindModalChildren.less';
import UpdateStepTableDataSet from '../../stores/UpdateStepTableDataSet';
import { getUpdateCompared } from '../../../../api/TestPlanApi';

const prefix = 'c7ntest-testPlan-updateRemind';
    
const UpdateRemindModalChildren = (props) => {
  const { testPlanStore } = props;
  const oldStepTableDataSet = useMemo(() => new DataSet(UpdateStepTableDataSet({ stepData: testPlanStore.executeOldData.stepData })), [testPlanStore.executeOldData.stepData]);
  const newStepTableDataSet = useMemo(() => new DataSet(UpdateStepTableDataSet({ stepData: testPlanStore.executeNewData.stepData })), [testPlanStore.executeNewData.stepData]);

  const getUpdateContent = () => {
    getUpdateCompared().then((res) => {
      console.log(res);
    }).catch(() => {
      Choerodon.prompt('获取更新详情失败');
    });
  };
  
  useEffect(() => {
    getUpdateContent();
  }, []);

  return (
    <div className={`${prefix}-modal-children`}>
      <div className={`${prefix}-item`}>
        <span className={`${prefix}-item-field`}>更新人</span>
        <span className={`${prefix}-item-value`}>李文斐</span>
      </div>
      <div className={`${prefix}-item`}>
        <span className={`${prefix}-item-field`}>更新时间</span>
        <span className={`${prefix}-item-value`}>2019-11-05 10:30:00</span>
      </div>
      <div className={`${prefix}-updateContent`}>
        <span className={`${prefix}-updateContent-span`}>变更内容</span>
        <div className={`${prefix}-updateContent-div`}>
          <UpdateContent tag="old" updateData={{}} dataSet={oldStepTableDataSet} />
          <div className={`${prefix}-updateContent-div-icon`}>
            <Icon type="arrow_forward" />
          </div>
          <UpdateContent tag="new" updateData={{}} dataSet={newStepTableDataSet} />
        </div>
      </div>
    </div>
  );
}; 

export default observer(UpdateRemindModalChildren);
