import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Table, Button, Modal, Select, Icon, message, Menu,
} from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Content, Header, TabPage as Page, stores, Breadcrumb,
} from '@choerodon/boot';
import { DragDropContext } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';
import update from 'immutability-helper';
import priorityApi from '@/api/priority';
import PriorityCreate from '../priorityCreate';
import PriorityEdit from '../priorityEdit';
import BodyRow from './bodyRow';
import TableDropMenu from '../../../common/TableDropMenu';

import './PriorityList.less';

const { Option } = Select;
const { AppState } = stores;
const { confirm } = Modal;

const ColorBlock = ({ color }) => (
  <div
    className="color-block"
    style={{
      backgroundColor: color,
    }}
  />
);

@DragDropContext(HTML5Backend)
@injectIntl
@observer
class PriorityList extends Component {
  components = {
    body: {
      row: BodyRow,
    },
  };

  constructor(props) {
    super(props);
    this.state = {
      priorityId: false,
    };
  }

  componentDidMount() {
    this.refresh();
  }

  moveRow = (dragIndex, hoverIndex) => {
    const orgId = AppState.currentMenuType.organizationId;

    const { PriorityStore } = this.props;
    const { getPriorityList } = PriorityStore;
    const dragRow = getPriorityList[dragIndex];
    if (!dragRow.enableFlag) {
      return;
    }

    const priorityListAfterDrag = update(getPriorityList, {
      $splice: [[dragIndex, 1], [hoverIndex, 0, dragRow]],
    });

    PriorityStore.setPriorityList(priorityListAfterDrag);
    // 更新顺序
    priorityApi.sort(PriorityStore.getPriorityList.map((item) => ({
      id: item.id,
      sequence: item.sequence,
      objectVersionNumber: item.objectVersionNumber,
    }))).then(() => {
      PriorityStore.loadPriorityList(orgId);
    });
  };

  refresh = () => {
    const orgId = AppState.currentMenuType.organizationId;
    this.loadPriorityList(orgId);
  };

  handleChooseMenu = (key, record) => {
    if (key === 'edit') {
      this.handleChangeEnable(record);
    } else if (key === 'del') {
      this.handleDelete(record);
    }
  };

  renderMenu = (text, record) => {
    const { PriorityStore, intl } = this.props;
    const enableList = PriorityStore.getPriorityList.filter((item) => item.enableFlag);
    let name;
    if (record.defaultFlag) {
      name = `${text} ${intl.formatMessage({ id: 'priority.default' })}`;
    } else {
      name = text;
    }
    const menu = (
      <Menu onClick={(item) => this.handleChooseMenu(item.key, record)}>
        {record.enableFlag && enableList && enableList.length === 1
          ? null
          : (
            <Menu.Item key="edit">
              <span>
                <FormattedMessage id={record.enableFlag ? 'disable' : 'enable'} />
              </span>
            </Menu.Item>
          )}
        {record.enableFlag && enableList && enableList.length === 1
          ? null
          : (
            <Menu.Item key="del">
              <span>删除</span>
            </Menu.Item>
          )}
      </Menu>
    );
    return (
      <TableDropMenu
        menu={menu}
        text={name}
        isHasMenu={!(record.enableFlag && enableList && enableList.length === 1)}
        onClickEdit={this.handleEdit.bind(this, record.id)}
      />
    );
  };

  getColumns = () => {
    const { PriorityStore, intl } = this.props;
    const enableList = PriorityStore.getPriorityList.filter((item) => item.enableFlag);
    return [
      {
        title: <FormattedMessage id="priority.name" />,
        dataIndex: 'name',
        key: 'name',
        width: 270,
        filters: [],
        onFilter: (value, record) => record.name.toString().indexOf(value) !== -1,
        render: (text, record) => this.renderMenu(text, record),
      },
      {
        title: <FormattedMessage id="priority.des" />,
        dataIndex: 'description',
        key: 'des',
        width: 450,
        filters: [],
        className: 'issue-priority-des',
        onFilter: (value, record) => record.description && record.description.toString().indexOf(value) !== -1,
      },
      {
        title: <FormattedMessage id="priority.color" />,
        dataIndex: 'colour',
        key: 'color',
        width: 100,
        render: (text, record) => (
          <ColorBlock color={text} />
        ),
      },
    ];
  };

  handleEdit = (priorityId) => {
    const { PriorityStore } = this.props;
    PriorityStore.setEditingPriorityId(priorityId);
    this.showSideBar('edit');
  };

  handleSelectChange = (id) => {
    this.setState({
      priorityId: id,
    });
  };

  handleDelete = async (priority) => {
    const { intl, PriorityStore } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    const that = this;
    const count = await priorityApi.checkBeforeDel(priority.id);
    const priorityList = PriorityStore.getPriorityList.filter((item) => item.id !== priority.id);
    confirm({
      className: 'c7n-deletePriority-confirm',
      title: intl.formatMessage({ id: 'priority.delete.title' }),
      content: (
        <div>
          <div style={{ marginBottom: 10 }}>
            {`${intl.formatMessage({ id: 'priority.delete.title' })}：${priority.name}`}
          </div>
          {count !== 0
            && (
              <div style={{ marginBottom: 10 }}>
                <Icon
                  type="error"
                  style={{
                    verticalAlign: 'top',
                    color: 'red',
                    marginRight: 5,
                  }}
                />
                {intl.formatMessage({ id: 'priority.delete.used.tip.prefix' })}
                <span style={{ color: 'red' }}>{count}</span>
                {intl.formatMessage({ id: 'priority.delete.used.tip.suffix' })}
              </div>
            )}
          <div style={{ marginBottom: 15 }}>
            {intl.formatMessage({ id: 'priority.delete.notice' })}
            {count !== 0 && intl.formatMessage({ id: 'priority.delete.used.notice' })}
          </div>
          {count !== 0
            && (
              <div>
                <Select
                  label={intl.formatMessage({ id: 'priority.title' })}
                  placeholder={intl.formatMessage({ id: 'priority.delete.chooseNewPriority.placeholder' })}
                  onChange={this.handleSelectChange}
                  style={{ width: 470 }}
                  defaultValue={priorityList[0].id}
                >
                  {priorityList.map(
                    (item) => <Option value={item.id} key={String(item.id)}>{item.name}</Option>,
                  )}
                </Select>
              </div>
            )}
        </div>),
      width: 520,
      onOk() {
        that.deletePriority(priority.id, priorityList[0].id);
        that.setState({
          priorityId: false,
        });
      },
      onCancel() {
        that.setState({
          priorityId: false,
        });
      },
      okText: '删除',
      cancelText: '取消',
    });
  };

  deletePriority = async (id, defaultId) => {
    const { PriorityStore } = this.props;
    const { priorityId } = this.state;
    const orgId = AppState.currentMenuType.organizationId;
    try {
      await priorityApi.delete(id, priorityId || defaultId);
      PriorityStore.loadPriorityList(orgId);
    } catch (err) {
      message.error('删除失败');
    }
  };

  handleChangeEnable = (priority) => {
    const { intl } = this.props;
    if (priority.enableFlag) {
      const that = this;
      confirm({
        title: intl.formatMessage({ id: 'priority.disable.title' }),
        content: (
          <div>
            <div style={{ marginBottom: 10 }}>
              {intl.formatMessage({ id: 'priority.disable.title' })}
              :
              {' '}
              {priority.name}
            </div>
            <div>{intl.formatMessage({ id: 'priority.disable.notice' })}</div>
          </div>),
        onOk() {
          that.enablePriority(priority);
        },
        onCancel() { },
        okText: '确认',
        cancelText: '取消',
      });
    } else {
      this.enablePriority(priority);
    }
  };

  enablePriority = async (priority) => {
    const { PriorityStore } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    try {
      await priorityApi.updateStatus(priority.id, !priority.enableFlag);
      PriorityStore.loadPriorityList(orgId);
    } catch (err) {
      message.error('修改状态失败');
    }
  };

  showSideBar = (operation) => {
    const { PriorityStore } = this.props;
    switch (operation) {
      case 'create':
        PriorityStore.setOnCreatingPriority(true);
        break;
      case 'edit':
        PriorityStore.setOnEditingPriority(true);
        break;
      default:
        break;
    }
  };

  async loadPriorityList(orgId) {
    const { PriorityStore } = this.props;
    try {
      await PriorityStore.loadPriorityList(orgId);
    } catch (err) {
      message.error('加载失败');
    }
  }

  render() {
    const { PriorityStore } = this.props;
    const {
      getPriorityList,
      onLoadingList,
      onEditingPriority,
      onCreatingPriority,
    } = PriorityStore;

    return (
      <Page
        className="c7ntest-priority"
        service={[
          'choerodon.code.organization.setting.issue.priority.ps.default',
        ]}
      >
        <Header title={<FormattedMessage id="priority.title" />}>
          <Button onClick={() => this.showSideBar('create')}>
            <Icon type="playlist_add" />
            <FormattedMessage id="priority.create" />
          </Button>
        </Header>
        <Breadcrumb />
        <Content>
          <Table
            filterBarPlaceholder="过滤表"
            columns={this.getColumns()}
            dataSource={getPriorityList}
            rowKey={(record) => record.id}
            loading={onLoadingList}
            pagination={false}
            components={this.components}
            onRow={(record, index) => ({
              index,
              moveRow: this.moveRow,
            })}
            rowClassName={(record, index) => (!record.enableFlag ? 'issue-priority-disable' : '')}
          />

          {
            onCreatingPriority ? <PriorityCreate PriorityStore={PriorityStore} /> : null
          }
          {
            onEditingPriority ? <PriorityEdit PriorityStore={PriorityStore} /> : null
          }
        </Content>
      </Page>
    );
  }
}

export default injectIntl(PriorityList);
