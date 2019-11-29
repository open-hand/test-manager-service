import React, { useEffect, useState, useContext } from 'react';
import {
  toJS,
} from 'mobx';
import { observer } from 'mobx-react-lite';
import {
  Input, Icon, Spin, Tree,
} from 'choerodon-ui';
import _ from 'lodash';
import UpdateContent from './component/UpdateContent';
import './UpdateRemindModalChildren.less';
    
const UpdateRemindModalChildren = (props) => {
  const { testPlanStore, oldStepTableDataSet, newStepTableDataSet } = props;

  const getUpdateContent = () => {

  };
  
  useEffect(() => {
    getUpdateContent();
  }, []);

  return (
    <div className="c7ntest-testPlan-updateRemind-modal-children">
      <div className="c7ntest-testPlan-updateRemind-item">
        <span className="c7ntest-testPlan-updateRemind-item-field">更新人</span>
        <span className="c7ntest-testPlan-updateRemind-item-value">李文斐</span>
      </div>
      <div className="c7ntest-testPlan-updateRemind-item">
        <span className="c7ntest-testPlan-updateRemind-item-field">更新时间</span>
        <span className="c7ntest-testPlan-updateRemind-item-value">2019-11-05 10:30:00</span>
      </div>
      <div className="c7ntest-testPlan-updateRemind-updateContent">
        <span className="c7ntest-testPlan-updateRemind-updateContent-span">变更内容</span>
        <div className="c7ntest-testPlan-updateRemind-updateContent-div">
          <UpdateContent tag="old" updateData={{}} dataSet={oldStepTableDataSet} />
          <div className="c7ntest-testPlan-updateRemind-updateContent-div-icon">
            <Icon type="arrow_forward" />
          </div>
          <UpdateContent tag="new" updateData={{}} dataSet={oldStepTableDataSet} />
        </div>
      </div>
    </div>
  );
}; 

export default observer(UpdateRemindModalChildren);
