/* eslint-disable react/jsx-props-no-spreading */
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { stores, axios, Permission } from '@choerodon/master';
import { observer } from 'mobx-react';
import {
  Menu, Input, Dropdown, Button, Tooltip, Icon, Modal,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { Draggable, Droppable } from 'react-beautiful-dnd';
import FileSaver from 'file-saver';
import { SmartTooltip } from '../../../../components';
import IssueTreeStore from '../../IssueManagestore/IssueTreeStore';
import {
  editFolder, deleteFolder, moveIssues, copyIssues, exportIssuesFromFolder, exportIssuesFromVersion,
} from '../../../../api/IssueManageApi';
import IssueStore from '../../IssueManagestore/IssueStore';
import './IssueTreeTitle.less';

const { AppState } = stores;
const { confirm } = Modal;
@observer
class IssueTreeTitle extends Component {
  state = {
    editing: false,
    enter: false,
  }

  addFolder = (data) => {
    this.props.callback(data, 'ADD_FOLDER');
  }

  handleItemClick = ({ item, key, keyPath }) => {
    const { data, refresh } = this.props;
    const { type, cycleId, title } = data;
    switch (key) {
      case 'rename': {
        this.setState({
          editing: true,
        });
        break;
      }
      case 'item_1': {
        confirm({
          title: '确定删除文件夹?',
          content: '删除文件夹后将删除文件夹内所有测试用例，以及相关的测试阶段和执行',
          onOk() {
            IssueTreeStore.setLoading(true);
            IssueStore.setLoading(true);
            deleteFolder(cycleId).then((res) => {
              if (res.failed) {
                Choerodon.prompt('删除失败');
              } else {
                // 删除文件夹后，清空现有文件夹信息
                IssueTreeStore.setCurrentCycle({});
                IssueStore.loadIssues();
                refresh();
              }
            }).catch((err) => {
              IssueStore.setLoading(false);
              IssueTreeStore.setLoading(false);
              Choerodon.prompt('网络异常');
            });
          },
          onCancel() {
            // console.log('Cancel');
          },
        });

        break;
      }
      case 'copy': {
        // deleteCycleOrFolder(cycleId).then((res) => {
        //   if (res.failed) {
        //     Choerodon.prompt('删除失败');
        //   } else {
        //     IssueTreeStore.setCurrentCycle({});
        //     refresh();
        //   }
        // }).catch((err) => {

        // });
        break;
      }
      case 'paste': {
        // this.props.callback(data, 'CLONE_FOLDER');
        // cloneFolder(cycleId, data).then((data) => {
        // this.props.refresh();
        break;
      }
      case 'export': {
        exportIssuesFromFolder(cycleId).then((excel) => {
          const blob = new Blob([excel], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
          const fileName = `${AppState.currentMenuType.name}-${title}.xlsx`;
          FileSaver.saveAs(blob, fileName);
        });
        break;
      }
      default: break;
    }
  }

  handleEdit = (data) => {
    editFolder(data).then((res) => {
      if (res.failed) {
        Choerodon.prompt('文件夹名字重复');
      } else {
        this.props.refresh();
      }
    });
    this.setState({
      editing: false,
    });
  }


  enterCopy = (e) => {
    e.preventDefault();
    e.stopImmediatePropagation();
    // console.log(e.keyCode);
    // 17 ctrl 91 左command 93 右command 224 其他浏览器（firefox）command
    if (e.keyCode === 17 || e.keyCode === 93 || e.keyCode === 91 || e.keyCode === 224) {
      const templateCopy = document.getElementById('template_folder_copy').cloneNode(true);
      templateCopy.style.display = 'block';
      IssueTreeStore.setCopy(true);
      if (this.instance.firstElementChild) {
        this.instance.replaceChild(templateCopy, this.instance.firstElementChild);
      } else {
        this.instance.appendChild(templateCopy);
      }
      // this.instance.innerText = '复制';
    }
  }

  leaveCopy = (e) => {
    e.preventDefault();
    e.stopImmediatePropagation();
    const templateMove = document.getElementById('template_folder_move').cloneNode(true);
    templateMove.style.display = 'block';
    IssueTreeStore.setCopy(false);
    if (this.instance.firstElementChild) {
      this.instance.replaceChild(templateMove, this.instance.firstElementChild);
    } else {
      this.instance.appendChild(templateMove);
    }
  }

  moveIssues = (cycleId, versionId, e) => {
    this.setState({
      enter: false,
    });
    // console.log(e.ctrlKey, cycleId, IssueStore.getDraggingTableItems);
    const isCopy = e.ctrlKey || e.metaKey;
    const issueLinks = IssueStore.getDraggingTableItems.map((issue) => ({
      issueId: issue.issueId,
      summary: issue.summary,
      objectVersionNumber: issue.objectVersionNumber,
    }));
    //
    if (!isCopy) {
      IssueStore.setLoading(true);
      moveIssues(versionId, cycleId, issueLinks).then((res) => {
        IssueStore.setDraggingTableItems([]);
        IssueStore.loadIssues();
      }).catch((err) => {
        IssueStore.setLoading(false);
        Choerodon.prompt('网络错误');
      });
    } else {
      IssueStore.setLoading(true);
      copyIssues(versionId, cycleId, issueLinks).then((res) => {
        if (res.failed) {
          Choerodon.prompt('存在同名文件夹');
        }
        IssueStore.setDraggingTableItems([]);
        IssueStore.loadIssues();
      }).catch((err) => {
        IssueStore.setLoading(false);
        Choerodon.prompt('网络错误');
      });
    }
  }

  exportIssueFromVersion = (data) => {
    exportIssuesFromVersion(data.versionId).then((excel) => {
      const blob = new Blob([excel], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const fileName = `${AppState.currentMenuType.name}-${data.title}.xlsx`;
      FileSaver.saveAs(blob, fileName);
    });
  }

  render() {
    const menu = AppState.currentMenuType;
    const {
      type: Menutype, id: projectId, organizationId: orgId, name,
    } = menu;
    const draggingItems = IssueTreeStore.getDraggingFolders;

    // 过滤，这里只要文件夹,显示时可以显示和当前版本一样的，但最终处理时过滤掉
    const filteredItems = draggingItems.filter((item) => item && item.cycleId);
    const getMenu = () => {
      let items = [];
      // if (type === 'temp') {
      items.push(
        <Menu.Item key="rename">
          <FormattedMessage id="issue_tree_rename" />
        </Menu.Item>,
      );
      // } else {
      // if (type === 'cycle') {
      items = items.concat([
        <Permission
          type={Menutype}
          projectId={projectId}
          organizationId={orgId}
          service={['agile-service.project-info.updateProjectInfo']}
          noAccessChildren={(
            <Menu.Item
              key="delete"
              disabled
            >
              <FormattedMessage id="issue_tree_delete" />
            </Menu.Item>
          )}
        >
          <Menu.Item key="delete">
            <FormattedMessage id="issue_tree_delete" />
          </Menu.Item>
        </Permission>,
        // <Menu.Item key="export">
        //   导出
        // </Menu.Item>,
        // <Menu.Item key="paste">
        //   <FormattedMessage id="issue_tree_paste" />
        // </Menu.Item>,
      ]);
      // }
      return <Menu onClick={this.handleItemClick} style={{ margin: '10px 0 0 28px' }}>{items}</Menu>;
    };
    const {
      editing, enter, mode,
    } = this.state;
    const { data, title, index } = this.props;
    // const { title } = data;
    let type = null;
    if (data.type === 'temp') {
      type = 'temp';
    } else if (data.versionId && data.type !== 'cycle') {
      type = 'version';
    } else if (data.type === 'cycle') {
      type = 'cycle';
    }
    const treeTitle = (
      <div
        className="c7ntest-issue-tree-title"
      >
        {editing
          ? (
            <Input
              style={{ width: 100 }}
              defaultValue={data.title}
              autoFocus
              onBlur={(e) => {
                if (e.target.value === '') {
                  Choerodon.prompt('文件夹名不能为空');
                  this.setState({
                    editing: false,
                  });
                  return;
                }
                if (e.target.value === data.title) {
                  this.setState({
                    editing: false,
                  });
                  return;
                }
                this.handleEdit({
                  folderId: data.cycleId,
                  name: e.target.value,
                  type: 'cycle',
                  objectVersionNumber: data.objectVersionNumber,
                });
              }}
            />
          )
          : (
            <div className="c7ntest-issue-tree-title-text">
              <SmartTooltip title={title} placement="topLeft">
                {title}
              </SmartTooltip>
            </div>
          )}
        <div role="none" className="c7ntest-issue-tree-title-actionButton" onClick={(e) => e.stopPropagation()}>
          {/* {data.type === 'temp'
        ? null : */}
          {
            type === 'version'
              ? (
                <Tooltip title="添加文件夹"><Icon type="create_new_folder" className="c7ntest-add-folder" onClick={this.addFolder.bind(this, data)} /></Tooltip>
              )
              : null
          }
          {
            type === 'cycle'
            && (
              <Dropdown overlay={getMenu(data.type)} trigger={['click']}>
                <Button shape="circle" icon="more_vert" />
              </Dropdown>
            )
          }
        </div>
      </div>
    );
    if (type === 'version') {
      return (
        <Droppable droppableId={data.versionId}>
          {(provided, snapshot) => (
            <div
              ref={provided.innerRef}
              style={{ border: snapshot.isDraggingOver && JSON.parse(snapshot.draggingOverWith).versionId !== data.versionId && '2px dashed green', height: 30 }}
            >
              {treeTitle}
              {provided.placeholder}
            </div>
          )}
        </Droppable>
      );
    } else if (type === 'cycle') {
      return (
        <Droppable droppableId={data.key} isDropDisabled={!IssueStore.tableDraging}>
          {(provided, snapshot) => (
            <div
              role="none"
              ref={provided.innerRef}
              style={{ border: IssueStore.tableDraging && enter && '2px dashed green', height: 30 }}
              {...{
                onMouseEnter: IssueStore.tableDraging ? () => {
                  this.setState({
                    enter: true,
                  });
                } : null,
                onMouseLeave: IssueStore.tableDraging ? () => {
                  this.setState({
                    enter: false,
                  });
                } : null,
                onMouseUp:
                  IssueStore.tableDraging ? this.moveIssues.bind(this, data.cycleId, data.versionId) : null,
              }}
            >
              <Draggable index={index} key={data.key} draggableId={JSON.stringify({ cycleId: data.cycleId, versionId: data.versionId, objectVersionNumber: data.objectVersionNumber })}>
                {(providedinner, snapshotinner) => {
                  if (snapshotinner.isDragging) {
                    document.addEventListener('keydown', this.enterCopy);
                    document.addEventListener('keyup', this.leaveCopy);
                  } else {
                    document.removeEventListener('keydown', this.enterCopy);
                    document.removeEventListener('keyup', this.leaveCopy);
                  }
                  return (
                    <div
                      ref={providedinner.innerRef}
                      {...providedinner.draggableProps}
                      {...providedinner.dragHandleProps}
                    >
                      <div
                        style={{
                          position: 'relative',
                          // background: snapshotinner.isDragging && 'white',
                        }}
                      >
                        {snapshotinner.isDragging
                          && (
                            <div style={{
                              position: 'absolute',
                              width: 15,
                              height: 15,
                              fontSize: '12px',
                              lineHeight: '15px',
                              background: 'red',
                              textAlign: 'center',
                              color: 'white',
                              borderRadius: '50%',
                              top: 0,
                              left: -20,
                            }}
                            >
                              {filteredItems.length}
                            </div>
                          )}
                        {treeTitle}
                        {snapshotinner.isDragging
                          && (
                            <div className="IssueTree-drag-prompt">
                              <div>复制或移动文件夹</div>
                              <div>按下ctrl/command复制</div>
                              <div
                                ref={(instance) => { this.instance = instance; }}
                              >
                                <div>
                                  {'当前状态：'}
                                  <span style={{ fontWeight: 500 }}>移动</span>
                                </div>
                              </div>
                            </div>
                          )}
                      </div>
                    </div>
                  );
                }}
              </Draggable>
              {provided.placeholder}
            </div>
          )}
        </Droppable>
      );
    } else if (type === 'temp') {
      return (
        <Droppable droppableId={data.key} isDropDisabled={!IssueStore.tableDraging}>
          {(provided, snapshot) => (
            <div
              role="none"
              ref={provided.innerRef}
              style={{ border: IssueStore.tableDraging && enter && '2px dashed green', height: 30 }}
              {...{
                onMouseEnter: IssueStore.tableDraging ? (e) => {
                  this.setState({
                    enter: true,
                  });
                } : null,
                onMouseLeave: IssueStore.tableDraging ? () => {
                  this.setState({
                    enter: false,
                  });
                } : null,
                onMouseUp:
                  IssueStore.tableDraging ? this.moveIssues.bind(this, data.cycleId, data.versionId) : null,
              }}
            >
              {treeTitle}
              {provided.placeholder}
            </div>
          )}
        </Droppable>
      );
    } else {
      return treeTitle;
    }
  }
}

IssueTreeTitle.propTypes = {
  data: PropTypes.object.isRequired,
  title: PropTypes.element.isRequired,
};

export default IssueTreeTitle;
