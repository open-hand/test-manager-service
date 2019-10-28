import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Permission } from '@choerodon/boot';
import { Choerodon } from '@choerodon/boot';
import {
  Input, Icon, Spin, Tree,
} from 'choerodon-ui';
import _ from 'lodash';
import { DragDropContext } from 'react-beautiful-dnd';
import { getProjectId, getOrganizationId } from '@/common/utils';
import './IssueTree.scss';
import IssueTreeStore from '../../stores/IssueTreeStore';
import {
  getIssueTree, addFolder, moveFolders, copyFolders,
} from '../../../../api/IssueManageApi';
import IssueTreeTitle from './IssueTreeTitle';
import IssueStore from '../../stores/IssueStore';
import { NoVersion } from '../../../../components';

const { TreeNode } = Tree;
@observer
class IssueTree extends Component {
  state = {
    autoExpandParent: true,
    searchValue: '',
  }

  componentDidMount() {
    this.getTree();
    document.addEventListener('keydown', this.enterMulti);
    document.addEventListener('keyup', this.leaveMulti);
  }

  componentWillUnmount() {
    document.removeEventListener('keydown', this.enterMulti);
    document.removeEventListener('keyup', this.leaveMulti);
  }

  enterMulti = (e) => {
    if (e.keyCode === 17 || e.keyCode === 93 || e.keyCode === 91 || e.keyCode === 224) {
      this.multi = true;
    }
  }

  leaveMulti = () => {
    this.multi = false;
  }

  addFolder = (item, e, type) => {
    if (e.target.value === '') {
      Choerodon.prompt('文件夹名不能为空');
      IssueTreeStore.removeAdding();
      return;
    }
    IssueTreeStore.setLoading(true);
    addFolder({
      name: e.target.value,
      type: 'cycle',
      versionId: item.versionId,
    }).then((res) => {
      if (res.failed) {
        Choerodon.prompt('名字重复');
        IssueTreeStore.removeAdding();
      } else {
        this.getTree();
      }
      IssueTreeStore.setLoading(false);
    });
  }

  callback = (item, code) => {
    switch (code) {
      case 'CLONE_FOLDER': {
        const parentKey = this.getParentKey(item.key, IssueTreeStore.getTreeData);
        IssueTreeStore.addItemByParentKey(parentKey, { ...item, ...{ key: `${parentKey}-CLONE_FOLDER`, type: 'CLONE_FOLDER' } });
        break;
      }

      case 'ADD_FOLDER': {
        IssueTreeStore.addItemByParentKey(item.key, { ...item, ...{ title: Choerodon.getMessage('新文件夹', 'New folder'), key: `${item.key}-ADD_FOLDER`, type: 'ADD_FOLDER' } });
        // 自动展开当前项
        const expandedKeys = IssueTreeStore.getExpandedKeys;
        if (expandedKeys.indexOf(item.key) === -1) {
          expandedKeys.push(item.key);
        }
        IssueTreeStore.setExpandedKeys(expandedKeys);
        break;
      }
      default: break;
    }
  }

  getParentKey = (key, tree) => key.split('-').slice(0, -1).join('-')

  getTree = () => {
    IssueTreeStore.setLoading(true);
    getIssueTree().then((data) => {
      IssueTreeStore.setTreeData(data.versions);
      IssueTreeStore.setLoading(false);
      this.generateList(data.versions);

      // window.console.log(dataList);
    }).catch(() => {
      IssueTreeStore.setLoading(false);
      Choerodon.prompt('网络错误');
    });
  }

  renderTreeNodes = data => data.map((item, i) => {
    const { children, type } = item;
    const key = type === 'temp' ? `${item.key}temp` : item.key;
    const { searchValue } = this.state;
    const expandedKeys = IssueTreeStore.getExpandedKeys;
    const index = item.title.indexOf(searchValue);
    const beforeStr = item.title.substr(0, index);
    const afterStr = item.title.substr(index + searchValue.length);
    const icon = (
      <Icon
        className="primary"
        type={expandedKeys.includes(item.key) ? 'folder_open2' : 'folder_open'}
      />
    );
    const title = index > -1 ? (
      <span>
        {beforeStr}
        <span style={{ color: '#f50' }}>{searchValue}</span>
        {afterStr}
      </span>
    ) : <span>{item.title}</span>;
    if (type === 'ADD_FOLDER') {
      return (
        <TreeNode
          className="hidden-hover"
          title={(
            <div onClick={e => e.stopPropagation()} role="none">
              <Input
                className="hidden-label"
                defaultValue={item.title}
                autoFocus
                onBlur={(e) => {
                  this.addFolder(item, e, type);
                }}
              />
            </div>
          )}
          icon={icon}
          data={item}
        />
      );
    } else {
      return (
        <TreeNode
          title={item.cycleId || item.versionId
            ? (
              <IssueTreeTitle
                index={i}
                title={title}
                data={item}
                refresh={this.getTree}
                callback={this.callback}
              />
            )
            : title}
          key={key}
          data={item}
          showIcon
          icon={icon}
        >
          {this.renderTreeNodes(children)}
        </TreeNode>
      );
    }
  });

  /**
   * 树的遍历，将树打为数组，方便搜索
   *
   * @memberof IssueTree
   */
  generateList = (data) => {
    const dataList = [];
    if (!data) {
      return;
    }
    const stack = data;
    const currentCycle = IssueTreeStore.getCurrentCycle;
    while (stack && stack.length > 0) {
      const tempNode = stack.pop();
      if (tempNode.children && tempNode.children) {
        tempNode.children.forEach((node, i) => {
          const {
            key, title, versionId, cycleId,
          } = node;
          // 树拖动之后versionId会更新，在这里更新右侧创建用例处的版本
          if (currentCycle.cycleId && currentCycle.cycleId === node.cycleId) {
            IssueTreeStore.setCurrentCycle(node);
          }
          dataList.push({
            key, title, versionId, cycleId,
          });
          stack.push(tempNode.children[i]);
        });
      }
    }
    IssueTreeStore.setDataList(dataList);
  }

  onExpand = (expandedKeys) => {
    IssueTreeStore.setExpandedKeys(expandedKeys);
    this.setState({
      autoExpandParent: false,
    });
  }

  filterCycle = (value) => {
    if (value !== '') {
      const expandedKeys = IssueTreeStore.dataList.map((item) => {
        if (item.title.indexOf(value) > -1) {
          return this.getParentKey(item.key, IssueTreeStore.getTreeData);
        }
        return null;
      }).filter((item, i, self) => item && self.indexOf(item) === i);
      IssueTreeStore.setExpandedKeys(expandedKeys);
    }
    this.setState({
      searchValue: value,
      autoExpandParent: true,
    });
  }

  getIssuesByFolder = (selectedKeys, {
    selected, selectedNodes, node, event,
  } = {}) => {
    const { executePagination, filters } = this.state;
    const { data } = node.props;
    // console.log(data);
    // if (data.versionId) {
    if (selectedKeys) {
      // 多选过滤，因为只有文件夹可拖动，所以把其他层级和临时去掉.
      const preSelectedKeys = IssueTreeStore.getSelectedKeys;
      let filteredKeys = selectedKeys;
      const reg = /temp/g;
      const newKey = selectedKeys.slice(-1);
      if (this.multi) {
        // 增加
        if (selected) {
          filteredKeys = [...new Set(preSelectedKeys.concat(newKey))].filter(key => key.split('-').length === 4 && !reg.test(key));
        } else {
          // 减少
          filteredKeys = preSelectedKeys.filter(key => selectedKeys.includes(key));
          // preSelectedKeys.split(preSelectedKeys.indexOf(newKey), 1);
        }
        // 单击的处理
      } else if (selected) {
        filteredKeys = selectedKeys.length > 0 ? selectedKeys.slice(-1) : preSelectedKeys;
      } else {
        filteredKeys = preSelectedKeys.filter(key => !selectedKeys.includes(key));
      }
      IssueTreeStore.setSelectedKeys(filteredKeys);
    }
    IssueTreeStore.setCurrentCycle(data);
    IssueStore.loadIssues(0);
    // }
  }

  onDragStart = (source) => {
    const selectedKeys = IssueTreeStore.getSelectedKeys;
    const draggingItems = selectedKeys.map(key => IssueTreeStore.getItemByKey(key));
    const { draggableId } = source;
    const item = JSON.parse(draggableId);
    if (!_.find(draggingItems, { cycleId: item.cycleId })) {
      draggingItems.push(item);
    }
    IssueTreeStore.setDraggingFolders(draggingItems);
    IssueTreeStore.setCopy(false);
  }

  onDragEnd = (result) => {
    // console.log(IssueTreeStore.isCopy);
    const { destination } = result;
    if (!destination) {
      return;
    }
    const draggingItems = IssueTreeStore.getDraggingFolders;

    // 过滤，这里只要文件夹
    const filteredItems = draggingItems.filter(item => item && destination.droppableId !== item.versionId && item.cycleId);
    // console.log(draggingItems, filteredItems);
    IssueTreeStore.setSelectedKeys([]);
    if (filteredItems.length > 0) {
      const data = filteredItems.map(item => ({ versionId: destination.droppableId, folderId: item.cycleId, objectVersionNumber: item.objectVersionNumber }));
      // console.log(data);
      IssueTreeStore.setLoading(true);
      // 清掉之前的拖动数据
      IssueTreeStore.setDraggingFolders([]);
      if (IssueTreeStore.isCopy) {
        copyFolders(data, destination.droppableId).then((res) => {
          if (res.failed) {
            IssueTreeStore.setLoading(false);
            Choerodon.prompt('存在同名文件夹');
          }
          // this.getTree();
        }).catch((err) => {
          IssueTreeStore.setLoading(false);
          Choerodon.prompt('网络错误');
        }).finally(() => {
          this.getTree();
        });
      } else {
        moveFolders(data).then((res) => {
          if (res.failed) {
            IssueTreeStore.setLoading(false);
            Choerodon.prompt('存在同名文件夹');
          }
          // this.getTree();
        }).catch((err) => {
          IssueTreeStore.setLoading(false);
          Choerodon.prompt('网络错误');
        }).finally(() => {
          this.getTree();
        });
      }
    }


    // console.log(result);
  }

  render() {
    const { onClose } = this.props;
    const { autoExpandParent } = this.state;
    const { loading } = IssueTreeStore;
    const treeData = IssueTreeStore.getTreeData;
    const noVersion = treeData.length === 0 || treeData[0].children.length === 0;
    const expandedKeys = IssueTreeStore.getExpandedKeys;
    const selectedKeys = IssueTreeStore.getSelectedKeys;
    return (
      <div className="c7ntest-IssueTree">
        <div id="template_folder_copy" style={{ display: 'none' }}>
          当前状态：
          <span style={{ fontWeight: 500 }}>复制</span>
        </div>
        <div id="template_folder_move" style={{ display: 'none' }}>
          当前状态：
          <span style={{ fontWeight: 500 }}>移动</span>
        </div>
        <div className="c7ntest-treeTop">
          <Input
            className="hidden-label"
            prefix={<Icon type="search" style={{ color: 'rgba(0,0,0,0.45)' }} />}
            placeholder="搜索"
            style={{ marginTop: 10, backgroundColor: 'rgba(0,0,0,0.06)', borderRadius: '2px' }}
            onChange={e => _.debounce(this.filterCycle, 200).call(null, e.target.value)}
          />
          {/* <Icon type="close" className="c7ntest-pointer" onClick={onClose} /> */}
        </div>

        <div
          className="c7ntest-IssueTree-tree"
        >
          <Spin spinning={loading}>
            {noVersion && !loading ? <NoVersion /> : (
              <DragDropContext onDragEnd={this.onDragEnd} onDragStart={this.onDragStart}>
                <Tree
                  multiple
                  selectedKeys={selectedKeys}
                  expandedKeys={expandedKeys}
                  showIcon
                  onExpand={this.onExpand}
                  onSelect={this.getIssuesByFolder}
                  autoExpandParent={autoExpandParent}
                >
                  {this.renderTreeNodes(treeData)}
                </Tree>
                <Permission
                  type="project"
                  projectId={getProjectId()}
                  organizationId={getOrganizationId()}
                  service={['agile-service.project-info.updateProjectInfo']}
                >
                  {null}
                </Permission>
              </DragDropContext>
            )}
          </Spin>
        </div>
      </div>
    );
  }
}

IssueTree.propTypes = {

};

export default IssueTree;
