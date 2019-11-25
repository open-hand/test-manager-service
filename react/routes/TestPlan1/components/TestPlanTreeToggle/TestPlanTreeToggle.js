import React, { memo } from 'react';
import PropTypes from 'prop-types';
import isEqual from 'react-fast-compare';
import { observer } from 'mobx-react';
import { Icon, Input } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import TestPlanTree from '../TestPlanTree';
import './TestPlanTreeToggle.scss';

const prefix = <Icon type="filter_list" />;
const propTypes = {
  filterCycle: PropTypes.func.isRequired,
  treeSearchValue: PropTypes.string.isRequired,  
  autoExpandParent: PropTypes.bool.isRequired,
  treeData: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  expandedKeys: PropTypes.arrayOf(PropTypes.string).isRequired,
  selectedKeys: PropTypes.arrayOf(PropTypes.string).isRequired,
  onTreeNodeExpand: PropTypes.func.isRequired,
  onTreeNodeSelect: PropTypes.func.isRequired,
};
const testPlanTreeToggle = ({
  filterCycle,
  ...treeProps
}) => (
  <div className="c7ntest-testPlanTreeToggle">
    <div className="c7ntest-testPlanTreeToggle-tree-area">
      <div className="c7ntest-testPlanTreeToggle-tree-area-head">
        <div className="c7ntest-treeTop">
          <Input
            className="hidden-label"
            prefix={<Icon type="search" style={{ color: 'rgba(0,0,0,0.45)' }} />}
            placeholder="搜索"
            style={{ marginTop: 0, backgroundColor: 'rgba(0,0,0,0.06)', borderRadius: '2px' }}
            onChange={e => _.debounce(filterCycle, 200).call(null, e.target.value)}
          />
        </div>
      </div>    
      <TestPlanTree
        {...treeProps}
      />   
    </div>
  </div>
);

testPlanTreeToggle.propTypes = propTypes;

export default observer(memo(testPlanTreeToggle, isEqual));
