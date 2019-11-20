
import React, {
  Component, useState, useEffect, useMemo,
} from 'react';
import { Select, Icon, Tree } from 'choerodon-ui/pro';
import treeDataSet from './treeDataSet';
import './SelectTree.less';
/**
 * 下拉选择树
 * @param {*} name 字段名
 * @param {*} renderSelect select显示渲染器
 * @param {*} pDataSet 控制select的DataSet
 */
function SelectTree(props) {
  const {
    name, renderSelect, pDataSet, ...restProps
  } = props;
  const dataSet = useMemo(() => treeDataSet(pDataSet, name), []);
  /**
  * 渲染树节点
  * @param {*} record  
  */
  const renderNode = ({ record }) => {
    const fileName = record.get('name');
    return (
      <div className="test-select-tree">
        <Icon
          className="test-select-tree-primary"
          type={record.get('expand') ? 'folder_open2' : 'folder_open'}
        />
        {fileName}
      </div>

    );
  };

  /**
  * 渲染树
  * @param {*} content 
  */
  function renderTree(content) {
    // console.log('renderTree', treeDataSet);
    return (
      <Tree
        dataSet={dataSet}
        renderer={renderNode}
      />
    );
  }

  function defaultRenderSelect({ record, text, value }) {
    return text;
  }

  return (
    <Select
      name={name}
      popupContent={renderTree}
      renderer={renderSelect || defaultRenderSelect}
      {...restProps}
    />
  );
}

export default SelectTree;
