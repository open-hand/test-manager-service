
import React, { useState, useMemo, useRef } from 'react';
import {
  Select, Icon, Tree, TextField,
} from 'choerodon-ui/pro';
import PropTypes from 'prop-types';
import { Choerodon } from '@choerodon/boot';
import _ from 'lodash';
import './SelectTree.less';
import { Divider } from 'choerodon-ui';
import treeDataSet from './treeDataSet';

/**
 * 下拉选择树
 * @param {*} name 字段名
 * @param {*} renderSelect select显示渲染器
 * @param {*} pDataSet 控制select的DataSet
 */
const propTypes = {
  name: PropTypes.string,
  onChange: PropTypes.func,
  isForbidRoot: PropTypes.bool,
  renderSelect: PropTypes.element,
  defaultValue: PropTypes.object,
};
function SelectTree(props) {
  const {
    name, renderSelect, defaultValue, pDataSet, data, onChange, isForbidRoot = true, ...restProps
  } = props;
  const selectRef = useRef();
  const [searchValue, setSearchValue] = useState('');// 搜索框内值
  const dataSet = useMemo(() => treeDataSet(pDataSet, name, defaultValue, onChange, isForbidRoot, selectRef), [isForbidRoot, name, onChange, pDataSet]);
  /**
  * 渲染树节点
  * @param {*} record  
  */
  const renderNode = ({ record }) => {
    const fileName = record.get('name');
    const index = fileName.toLowerCase().indexOf(String(searchValue).toLowerCase());
    const beforeFileName = fileName.substr(0, index);
    const afterFileName = fileName.substr(index + String(searchValue).length);
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

  /**
   * 搜索此节点父节点并展开
   * @param {*} record 
   */
  function searchParent(record) {
    // 防止文件id与父id相同 出现死循环
    if (record.get('parentId') !== 0 && record.get('folderId') !== record.get('parentId')) {
      record.parent.set('expanded', true);
      searchParent(record.parent);
    }
  }

  /**
   * 根据文件名找寻树节点并展开
   * @param {*} value 
   */
  function handleFilterNode(value) {
    dataSet.forEach((record) => {
      record.set('expanded', false);
    });
    if (value !== '' && value) {
      dataSet.forEach((record) => {
        if (record.get('name').toLowerCase().indexOf(value.toLowerCase()) !== -1) {
          try {
            searchParent(record);
          } catch (error) {
            Choerodon.prompt('数据错误');
          }
        }
      });
    }
  }

  /**
 * 根据文件名ID找寻树节点并展开
 * @param {*} value 
 * 
 */
  const handleFilterNodeByFolerId = (value) => {
    dataSet.forEach((record) => {
      record.set('expanded', false);
    });
    let result;
    if (value) {
      dataSet.forEach((record) => {
        if (record.get('folderId') === value) {
          try {
            searchParent(record);
            result = record;
          } catch (error) {
            Choerodon.prompt('数据错误');
          }
        }
      });
    }
    return result;
  };


  /**
   * 输入回调
   * @param {*} value 
   */
  function handleInput(value) {
    setSearchValue(value);
    handleFilterNode(value);
  }


  /**
  * 渲染树
  * @param {*} content 
  */
  const renderTree = (
    <div
      role="none"
      className="test-select-tree"
      onMouseDown={e => e.stopPropagation()}
    >
      <div className="test-select-tree-search">
        <TextField
          placeholder="输入文字以进行过滤 "
          onInput={e => _.debounce(handleInput, 300).call(this, e.target.value)}
          id="onTextField"
          autoFocus
          prefix={<Icon type="search" />}
          readOnly={false}
          onChange={handleFilterNode}
          // onEnterDown={handleEnd}
          clearButton
          onClear={() => setSearchValue('')}
        />
      </div>
      <Divider />
      <Tree
        dataSet={dataSet}
        renderer={renderNode}
        className="test-select-tree-body"
  
      />
    </div>
  );

  /**
   * 默认渲染select选中项
   * @param {*} param0 
   */
  function defaultRenderSelect({ record, text, value }) {
    return text;
  }

  function handleSelectClear(e) {
    dataSet.unSelectAll();
  }
  function renderValidation(validationResult, validationProps) {
    return '请选择文件夹';
  }
  return (
    <Select
      name={name}
      ref={selectRef}
      popupContent={renderTree}
      trigger={['click']}
      validationRenderer={renderValidation}
      popupCls="test-select-tree-wrap"
      onClear={handleSelectClear}
      renderer={renderSelect || defaultRenderSelect}
      {...restProps}
    />
  );
}
SelectTree.propTypes = propTypes;

export default SelectTree;
