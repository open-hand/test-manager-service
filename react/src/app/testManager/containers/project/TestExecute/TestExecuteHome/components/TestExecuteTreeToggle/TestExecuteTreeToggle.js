import React, { memo } from 'react';
import PropTypes from 'prop-types';
import isEqual from 'react-fast-compare';
import { observer } from 'mobx-react';
import { Icon, Input } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import TestExecuteStore from '../../../../../../store/project/TestExecute/TestExecuteStore';
import { RadioButton } from '../../../../../../components/CommonComponent';
import TestExecuteTree from '../TestExecuteTree';
import './TestExecuteTreeToggle.scss';

const prefix = <Icon type="filter_list" />;
const propTypes = {
  leftVisible: PropTypes.bool.isRequired,
  filterCycle: PropTypes.func.isRequired,
  onTreeAssignedToChange: PropTypes.func.isRequired,
  treeSearchValue: PropTypes.string.isRequired,  
  autoExpandParent: PropTypes.bool.isRequired,
  treeData: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  expandedKeys: PropTypes.arrayOf(PropTypes.string).isRequired,
  selectedKeys: PropTypes.arrayOf(PropTypes.string).isRequired,
  treeAssignedTo: PropTypes.number.isRequired,
  onTreeNodeExpand: PropTypes.func.isRequired,
  onTreeNodeSelect: PropTypes.func.isRequired,
};
const TestExecuteTreeToggle = ({
  leftVisible,
  treeAssignedTo,
  filterCycle,
  onTreeAssignedToChange,
  ...treeProps
}) => (
  <div className="c7ntest-TestExecuteTreeToggle">
    {/* 树隐藏 */}
    <div className={!leftVisible ? 'c7ntest-TestExecuteTreeToggle-side' : 'c7ntest-TestExecuteTreeToggle-hidden'} style={{ minHeight: window.innerHeight - 128 }}>
      <div className="c7ntest-TestExecuteTreeToggle-button">
        <div
          role="none"          
          onClick={() => {
            TestExecuteStore.setLeftVisible(true);
          }}
        >
          <Icon type="navigate_next" />
        </div>
      </div>
      <div className="c7ntest-TestExecuteTreeToggle-side-bar">
        {leftVisible ? '' : (
          <p
            role="none"
            onClick={() => {
              TestExecuteStore.setLeftVisible(true);
            }}
          >
            <FormattedMessage id="cycle_name" />
          </p>
        )}
      </div>
    </div>
    {/* 树显示区域 */}
    <div className={leftVisible ? 'c7ntest-TestExecuteTreeToggle-tree-area' : 'c7ntest-TestExecuteTreeToggle-hidden'}>
      <RadioButton
        style={{ marginBottom: 20 }}
        onChange={onTreeAssignedToChange}
        value={treeAssignedTo === 0 ? 'all' : 'my'}
        data={[{
          value: 'my',
          text: 'cycle_my',
        }, {
          value: 'all',
          text: 'cycle_all',
        }]}
      />
      <div className="c7ntest-TestExecuteTreeToggle-tree-area-head">
        <div className="c7ntest-TestExecuteTreeToggle-tree-area-head-search">
          <Input prefix={prefix} placeholder="过滤" onChange={e => _.debounce(filterCycle, 200).call(null, e.target.value)} />
        </div>
        <div className="c7ntest-TestExecuteTreeToggle-button" style={{ margin: '0 5px 0 10px' }}>
          <div
            role="none"            
            onClick={() => {
              TestExecuteStore.setLeftVisible(false);
            }}
          >
            <Icon type="navigate_before" />
          </div>
        </div>
      </div>    
      <TestExecuteTree
        treeAssignedTo={treeAssignedTo}
        {...treeProps}
      />   
    </div>
    <div style={{ width: 1, background: 'rgba(0,0,0,0.26)' }} />
  </div>
);

TestExecuteTreeToggle.propTypes = propTypes;

export default observer(memo(TestExecuteTreeToggle, isEqual));
