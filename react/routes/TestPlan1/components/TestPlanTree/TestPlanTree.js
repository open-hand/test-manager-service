import React, { memo } from 'react';
import isEqual from 'react-fast-compare';
import { Icon, Tree } from 'choerodon-ui';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import TreeTitle from './TreeTitle';
import { NoVersion } from '../../../../components';
import './TestPlanTree.scss';

const { TreeNode } = Tree;
const propTypes = {
  treeSearchValue: PropTypes.string.isRequired,
  autoExpandParent: PropTypes.bool.isRequired,
  treeData: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  expandedKeys: PropTypes.arrayOf(PropTypes.string).isRequired,
  selectedKeys: PropTypes.arrayOf(PropTypes.string).isRequired,
  onTreeNodeExpand: PropTypes.func.isRequired,
  onTreeNodeSelect: PropTypes.func.isRequired,
};
const TestPlanTree = ({
  treeSearchValue,
  selectedKeys,
  expandedKeys,
  onTreeNodeExpand,
  onTreeNodeSelect,
  autoExpandParent,
  treeData,
  loading,
}) => {
  const noVersion = treeData.length === 0 || treeData[0].children.length === 0;
  const renderTreeNodes = data => data.map((item) => {
    const {
      children, key, cycleCaseList, type,
    } = item;
    const index = item.title.indexOf(treeSearchValue);
    const beforeStr = item.title.substr(0, index);
    const afterStr = item.title.substr(index + treeSearchValue.length);
    const icon = (
      <Icon
        className="primary"
        type={expandedKeys.includes(item.key) ? 'folder_open2' : 'folder_open'}
      />
    );
    if (children) {
      const title = index > -1 ? (
        <span>
          {beforeStr}
          <span style={{ color: '#f50' }}>{treeSearchValue}</span>
          {afterStr}
        </span>
      ) : <span>{item.title}</span>;
      return (
        <TreeNode
          title={item.cycleId
            ? (
              <TreeTitle
                text={item.title}
                key={key}
                data={item}
                title={title}
                progress={cycleCaseList}
              />
            ) : title}
          key={key}
          data={item}
          showIcon
          icon={icon}
        >
          {renderTreeNodes(children)}
        </TreeNode>
      );
    }
    return (
      <TreeNode
        icon={icon}
        {...item}
        data={item}
      />
    );
  });

  return (
    <div className="c7ntest-testPlanTree">
      {!loading && noVersion ? <NoVersion /> : (
        <Tree
          selectedKeys={selectedKeys}
          expandedKeys={expandedKeys}
          showIcon
          onExpand={onTreeNodeExpand}
          onSelect={onTreeNodeSelect}
          autoExpandParent={autoExpandParent}
        >
          {renderTreeNodes(treeData)}
        </Tree>
      )}
    </div>
  );
};

TestPlanTree.propTypes = propTypes;

export default observer(memo(TestPlanTree, isEqual));
