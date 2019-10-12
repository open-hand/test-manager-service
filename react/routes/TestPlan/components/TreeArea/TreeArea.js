import React from 'react';
import { FormattedMessage } from 'react-intl';
import PlanTree from '../PlanTree';

const TreeArea = ({ isTreeVisible, setIsTreeVisible, loading }) => (
  isTreeVisible
    ? (
      <div className="c7ntest-TestPlan-tree">
        <PlanTree          
          onClose={() => { setIsTreeVisible(false); }}
          loading={loading}
        />
      </div>
    )
    : (
      <div className="c7ntest-TestPlan-bar">        
        <p
          role="none"
          onClick={() => { setIsTreeVisible(true); }}
        >
          <FormattedMessage id="testPlan_name" />
        </p>
      </div>
    )
);

TreeArea.propTypes = {

};

export default TreeArea;
