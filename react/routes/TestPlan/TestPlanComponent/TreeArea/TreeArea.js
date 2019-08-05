import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { FormattedMessage } from 'react-intl';
import { Icon } from 'choerodon-ui';
import PlanTree from '../PlanTree';

const TreeArea = ({ isTreeVisible, setIsTreeVisible }) => (
  isTreeVisible
    ? (
      <div className="c7ntest-TestPlan-tree">
        <PlanTree          
          onClose={() => { setIsTreeVisible(false); }}
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
