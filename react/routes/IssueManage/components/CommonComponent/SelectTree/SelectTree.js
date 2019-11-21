
import React, {
  Component, useState, useEffect, useMemo,
} from 'react';
import {
  Select, Icon, Tree, TextField,
} from 'choerodon-ui/pro';
import { Choerodon } from '@choerodon/boot';
import './SelectTree.less';
import { Input, Divider } from 'choerodon-ui';
import Search from 'choerodon-ui/lib/input/Search';
import { runInAction } from 'mobx';
import { observer } from 'mobx-react-lite';
import treeDataSet from './treeDataSet';

/**
 * 下拉选择树
 * @param {*} name 字段名
 * @param {*} renderSelect select显示渲染器
 * @param {*} pDataSet 控制select的DataSet
 */
function SelectTree(props) {
  const {
    name, renderSelect, pDataSet, data, setData, ...restProps
  } = props;
  const [isfocus, setIsfocus] = useState(false);
  const [searchValue, setSearchValue] = useState('');
  const dataSet = useMemo(() => treeDataSet(pDataSet, name), [name, pDataSet]);
  /**
  * 渲染树节点
  * @param {*} record  
  */
  const renderNode = ({ record }) => {
    const fileName = record.get('name');
    const index = fileName.toLowerCase().indexOf(searchValue.toLowerCase());
    const beforeFileName = fileName.substr(0, index);
    const afterFileName = fileName.substr(index + searchValue.length);

    return (
      <div className="test-select-tree-node">
        <Icon
          className="test-select-tree-node-primary"
          type={record.get('expanded') ? 'folder_open2' : 'folder_open'}
        />
        {index !== -1 ? (
          <span>
            {beforeFileName}
            <span style={{ color: '#f50' }}>{searchValue}</span>
            {afterFileName}
          </span>
        )
          : <span>{fileName}</span>
        }
      </div>

    );
  };

  function searchParent(record) {
    // 防止文件id与父id相同 出现死循环
    if (record.get('parentId') !== 0 && record.get('folderId') !== record.get('parentId')) {
      record.parent.set('expanded', true);
      searchParent(record.parent);
    }
  }

  function handleFilterNode(value) {
    setSearchValue(value);
    runInAction(() => {
      dataSet.forEach((record) => {
        if (record.get('name').toLowerCase().indexOf(value.toLowerCase()) !== -1) {
          try {
            searchParent(record);
          } catch (error) {
            Choerodon.prompt('数据错误');
          }
        }
      });
    });
  }

  /**
  * 渲染树
  * @param {*} content 
  */
  function renderTree(content) {
    // console.log('renderTree', treeDataSet);
    return (
      <div className="test-select-tree">
        <div className="test-select-tree-search">
          <TextField
            placeholder="输入文字以进行过滤 "
            onClick={(e) => {
              const input = document.getElementById('onTextField');
              if (input) {
                input.focus();
              }
              // e.currentTarget();
            }}
            // onInput={(value)=>console.log('input',value)}
            id="onTextField"
            autoFocus={isfocus}
            prefix={<Icon type="search" />}
            readOnly={false}
            onChange={handleFilterNode}
          // onEnterDown={handleEnd}
          />
        </div>
        <Divider />
        <Tree
          dataSet={dataSet}
          renderer={renderNode}
        />
      </div>
    );
  }

  function defaultRenderSelect({ record, text, value }) {
    return text;
  }
  function handleChange(hidden) {
    if (!hidden) {
      setIsfocus(true);
    } else {
      setIsfocus(false);
    }
  }
  return (
    <Select
      name={name}
      popupContent={renderTree}
      trigger={['click']}
      popupCls="test-select-tree-wrap"
      renderer={renderSelect || defaultRenderSelect}
      onPopupHiddenChange={handleChange}
      {...restProps}
    />
  );
}

export default observer(SelectTree);
