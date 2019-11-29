import React, {
  Component, useEffect, useState, useContext, 
} from 'react';
import {
  toJS,
} from 'mobx';
import { observer } from 'mobx-react-lite';
import {
  Input, Icon, Spin, Tree,
} from 'choerodon-ui';
import _ from 'lodash';
import axios from 'axios';
import UpdateContent from './component/UpdateContent';
import './UpdateRemindModalChildren.less';
    
const UpdateRemindModalChildren = props => (
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
        <UpdateContent tag="旧" updateData={{}} />
        <Icon type="arrow_forward" />
        <UpdateContent tag="新" updateData={{}} />
      </div>
    </div>
  </div>
); 

export default UpdateRemindModalChildren;
