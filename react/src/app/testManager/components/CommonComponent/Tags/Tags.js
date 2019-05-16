import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Tooltip, Tag } from 'choerodon-ui';

const Tags = ({
  data,
  nameField,
}) => {
  if (data.length > 0) {
    return (
      <Tooltip title={data.map(label => label[nameField]).join(',')}>
        <Tag
          color="blue"
          style={{
            maxWidth: 160,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
            verticalAlign: 'bottom',
          }}
        >
          {data[0][nameField]}
        </Tag>
        {data.length > 1 && <Tag color="blue">...</Tag>}
      </Tooltip>
    );
  } else {
    return null;
  }
};

Tags.propTypes = {

};

export default Tags;
