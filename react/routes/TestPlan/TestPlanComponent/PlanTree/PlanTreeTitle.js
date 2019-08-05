import React, { Component, Fragment } from 'react';
import { observer } from 'mobx-react';
import { Draggable } from 'react-beautiful-dnd';
import {
  Menu, Dropdown, Button, Tooltip, Icon, Modal,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { SmartTooltip } from '../../../../components';
import './PlanTreeTitle.scss';
import { deleteCycleOrFolder } from '../../../../api/cycleApi';
import { syncFolder, syncFoldersInCycle, syncFoldersInVersion } from '../../../../api/IssueManageApi';
import TestPlanStore from '../../TestPlanStore/TestPlanStore';

const { confirm } = Modal;
@observer
class PlanTreeTitle extends Component {
  sync = (item) => {
    const {
      type, cycleId, folderId, versionId,
    } = item;
    TestPlanStore.enterLoading();
    if (type === 'folder') {
      syncFolder(folderId, cycleId).then((res) => {
        TestPlanStore.getTree();
      });
    } else if (type === 'cycle') {
      syncFoldersInCycle(cycleId).then(() => {
        TestPlanStore.getTree();
      });
    } else {
      syncFoldersInVersion(versionId).then(() => {
        TestPlanStore.getTree();
      });
    }
  }

  handleItemClick = ({ item, key, keyPath }) => {
    const { data, refresh } = this.props;
    const {
      type, folderId, cycleId, title,
    } = data;
    switch (key) {
      case 'add': {
        this.props.callback(data, 'ADD_FOLDER');
        break;
      }
      case 'assign': {
        this.props.callback(data, 'ASSIGN_BATCH');
        break;
      }
      case 'edit': {
        if (type === 'folder') {
          TestPlanStore.EditStage(data);
        } else if (type === 'cycle') {
          TestPlanStore.EditCycle(data);
        }
        break;
      }
      case 'delete': {
        confirm({
          title: `确定要删除${type === 'cycle' ? '循环' : '阶段'}“${title}”？`,
          content: `${type === 'cycle' ? '循环' : '阶段'}“${title}”内所有执行将被删除`,
          onOk() {
            TestPlanStore.enterLoading();
            deleteCycleOrFolder(cycleId).then((res) => {
              if (res.failed) {
                Choerodon.prompt('删除失败');
              } else {
                TestPlanStore.setCurrentCycle({});
                refresh();
              }
            }).catch((err) => {

            });
          },
        });

        break;
      }
      case 'clone': {
        if (type === 'folder') {
          this.props.callback(data, 'CLONE_FOLDER');
        } else if (type === 'cycle') {
          this.props.callback(data, 'CLONE_CYCLE');
        }
        break;
      }
      case 'export': {
        this.props.callback(data, 'EXPORT_CYCLE');
        break;
      }
      case 'sync': {
        this.sync(data);
        break;
      }
      default: break;
    }
  }

  render() {
    const getMenu = (type) => {
      let items = [];
      if (type === 'folder' || type === 'cycle') {
        if (type === 'cycle') {
          items.push(
            <Menu.Item key="add">
              <FormattedMessage id="cycle_addFolder" />
            </Menu.Item>,
          );
        }
        if (type === 'folder') {
          items.push(
            <Menu.Item key="assign">
              批量指派
            </Menu.Item>,
          );
        }
        items = items.concat([
          <Menu.Item key="edit">
            {type === 'folder' ? <FormattedMessage id="cycle_editFolder" /> : <FormattedMessage id="cycle_editCycle" />}
          </Menu.Item>,
          <Menu.Item key="delete">
            {type === 'folder' ? <FormattedMessage id="cycle_deleteFolder" /> : <FormattedMessage id="cycle_deleteCycle" />}
          </Menu.Item>,
          <Menu.Item key="clone">
            {type === 'folder' ? <FormattedMessage id="cycle_cloneStage" /> : <FormattedMessage id="cycle_cloneCycle" />}
          </Menu.Item>,
          <Menu.Item key="sync">
            <FormattedMessage id="cycle_sync" />
          </Menu.Item>,

        ]);
      }
      return <Menu onClick={this.handleItemClick} style={{ margin: '10px 0 0 28px' }}>{items}</Menu>;
    };

    const {
      title, data, icon, index, 
    } = this.props;
    const treeTitle = (
      <div className="c7ntest-plan-tree-title">
        {icon}
        {
          data.versionId ? (
            <Fragment>
              <SmartTooltip width={!data.type && '120px'} className="c7ntest-plan-tree-title-text">
                {title}
              </SmartTooltip>
              {
                <div role="none" className="c7ntest-plan-tree-title-actionButton" onClick={e => e.stopPropagation()}>
                  {data.type
                    ? (
                      <Dropdown overlay={getMenu(data.type)} trigger={['click']}>
                        <Button shape="circle" icon="more_vert" />
                      </Dropdown>
                    )
                    : (
                      <Tooltip title={<FormattedMessage id="cycle_sync" />}>
                        <Icon type="sync" className="c7ntest-add-folder" onClick={this.sync.bind(this, data)} />
                      </Tooltip>
                    )
                  }
                </div>
              }
            </Fragment>
          ) : title
        }

      </div>
    );
    return data.type === 'folder'
      ? (
        <Draggable key={data.key} draggableId={data.key} index={index} type={String(data.parentCycleId)}>
          {(providedinner, snapshotinner) => (
            <div
              ref={providedinner.innerRef}
              {...providedinner.draggableProps}
              {...providedinner.dragHandleProps}
            >
              {treeTitle}
            </div>
          )

          }
        </Draggable>
      ) : treeTitle;
    // return treeTitle;
  }
}

export default PlanTreeTitle;
