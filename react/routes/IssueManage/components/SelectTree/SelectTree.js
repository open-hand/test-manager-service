
import React, {
  useState, useMemo, useRef, useEffect, useReducer,
} from 'react';
import {
  Tree as OldTree,
} from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import {
  Select, Icon, Tree, TextField,
} from 'choerodon-ui/pro';
import PropTypes from 'prop-types';
import { Choerodon } from '@choerodon/boot';
import _ from 'lodash';
import './SelectTree.less';
import { Divider } from 'choerodon-ui';
import treeDataSet from './treeDataSet';

const { TreeNode } = OldTree;
/**
 * 下拉选择树
 * @param {*} name 字段名
 * @param {*} renderSelect select显示渲染器
 * @param {*} parentDataSet 控制select的DataSet
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
    name, renderSelect, defaultValue, parentDataSet, data, onChange, isForbidRoot = true, ...restProps
  } = props;
  const selectRef = useRef();
  const rootIdsRef = useRef([]);
  // const [searchValue, setSearchValue] = useState('');// 搜索框内值  
  // eslint-disable-next-line react-hooks/exhaustive-deps
  const dataSet = useMemo(() => treeDataSet(parentDataSet, name, defaultValue, onChange, isForbidRoot, selectRef, rootIdsRef), []);
  const map = useMemo(() => new Map(dataSet.map(record => [record.get('folderId'), record])), [dataSet.length]);
  const [treeState, dispatch] = useReducer((state, action) => {
    const { expandedKeys = [], searchValue } = action;
    switch (action.type) {
      case 'init':
        return {
          expandedKeys: [],
          searchValue: '',
          autoExpandParent: false,
        };
      case 'expand':
        return {
          ...state,
          expandedKeys,
          autoExpandParent: false,
        };
      case 'search':
        if (expandedKeys.length === 0) {
          return {
            ...state,
            searchValue,
          };
        }
        return {
          expandedKeys,
          searchValue,
          autoExpandParent: true,
        };
      default:
        return ({
          ...state,
        });
    }
  }, {
    expandedKeys: [],
    searchValue: '',
    autoExpandParent: false,
  });
  /**
  * 渲染树节点
  * @param {*} record  
  */
  const renderNode = (record) => {
    const { searchValue } = treeState;
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
  function searchParent(record, keys = []) {
    // 防止文件id与父id相同 出现死循环
    if (record.get('parentId') !== 0 && record.get('parentId') !== record.get('folderId')) {
      const temp = dataSet.find(item => record.get('parentId') === item.get('folderId'));
      if (temp) {
        temp.set('expanded', true);
        keys.push(temp.id.toString());
        searchParent(temp, keys);
      }
    }
    return keys;
  }

  /**
   * 根据文件名找寻树节点并展开
   * @param {*} value 
   */
  function onFilterNode(value) {
    const expandedKeys = [];
    dispatch({ type: 'init' });
    dataSet.forEach((record) => {
      record.set('expanded', false);
    });
    if (value !== '' && value) {
      dataSet.forEach((record) => {
        if (record.get('name').toLowerCase().indexOf(value.toLowerCase()) !== -1) {
          try {
            // expandedKeys.push([...]);
            searchParent(record, expandedKeys);
          } catch (error) {
            Choerodon.prompt('数据错误');
          }
        }
      });
      dispatch({ type: 'search', expandedKeys, searchValue: value });
    }
  }

  /**
   * 输入回调
   * @param {*} value 
   */
  const handleInput = _.debounce((value) => {
    onFilterNode(value);
  }, 450);

  const handleSelectNode = (selectedKeys, { selected }) => {
    const record = dataSet.findRecordById(Number(selectedKeys[0]));
    if (selected) {
      dataSet.select(record);
      // 待选数据
      selectRef.current.collapse();
      if (parentDataSet) {
        selectRef.current.choose(record);
        // parentDataSet.current.set(name, selectData);
      }
      if (onChange) {
        onChange(record.toData());
      }
    } else {
      dataSet.unSelect(record);
      if (parentDataSet) {
        selectRef.current.unChoose();
        // parentDataSet.current.set(name, undefined);
      }
      if (onChange) {
        onChange({ folderId: undefined });
      }
    }
  };
  /**
   * 渲染树节点
   */
  function renderTreeNode(ids) {
    return ids.map((folderId) => {
      const record = map.get(folderId);
      return (
        <TreeNode selectable={isForbidRoot ? record.get('children').length === 0 : true} title={renderNode(record)} key={record.id}>
          {renderTreeNode(record.get('children'))}
        </TreeNode>
      );
    });    
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
          onInput={(e) => {
            handleInput(e.target.value);
          }}
          // onChange={_.debounce(handleInput, 300)}

          id="onTextField"
          autoFocus
          prefix={<Icon type="search" />}
          readOnly={false}
          // onChange={_.debounce(handleFilterNode, 300)}
          // onEnterDown={handleEnd}
          clearButton
          onClear={() => {
            dispatch({ type: 'init' });
            dataSet.forEach((record) => {
              record.set('expanded', false);
            });
          }}
        />
      </div>
      <Divider />

      {dataSet.totalCount > 0 ? (
        <OldTree
          expandedKeys={treeState.expandedKeys}
          autoExpandParent={treeState.autoExpandParent}
          onExpand={(expandedKeys, { expanded, node }) => {
            const { eventKey } = node.props;
            dispatch({ type: 'expand', expandedKeys });
            const record = dataSet.findRecordById(Number(eventKey));
            record.set('expanded', expanded);
          }}
          onSelect={handleSelectNode}
          className="test-select-tree-body"
        >
          {renderTreeNode(rootIdsRef.current)}

        </OldTree>
      ) : ''}
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
    return '请选择目录';
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

export default observer(SelectTree);
