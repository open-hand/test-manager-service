import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Page, Header, Content, Breadcrumb, 
} from '@choerodon/master';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import {
  Button, Icon, Spin, Modal,
} from 'choerodon-ui';
import { editExecuteDetail, deleteExecute } from '../../../api/cycleApi';
import {
  EventCalendar, CreateCycle, EditStage, EditCycle, ExportSide, TreeArea,
} from '../TestPlanComponent';
import { Injecter, NoCycle, Loading } from '../../../components';
import { TestPlanTable, BatchClone } from './components';
import TestPlanStore from '../TestPlanStore/TestPlanStore';
import { executeDetailShowLink, getDragRank } from '../../../common/utils';
import RunWhenProjectChange from '../../../common/RunWhenProjectChange';
import './TestPlanHome.scss';

const { confirm } = Modal;
@observer
class TestPlanHome extends Component { 
  componentDidMount() {
    RunWhenProjectChange(TestPlanStore.clearStore);
    TestPlanStore.setFilters({});
    TestPlanStore.setAssignedTo(null);
    TestPlanStore.setLastUpdatedBy(null);
    this.refresh();
  }

  saveRef = name => (ref) => {
    this[name] = ref;
  }

  refresh = () => { 
    TestPlanStore.getTree();
  }

  handleItemClick = (item) => {
    const { type } = item;
    if (type === 'folder') {
      TestPlanStore.EditStage(item);
    } else if (type === 'cycle') {
      TestPlanStore.EditCycle(item);
    }
  }

  /**
   * 点击table的一项
   *
   * @memberof TestPlanHome
   */
  handleTableRowClick=(record) => {
    const { history } = this.props;
    history.push(executeDetailShowLink(record.executeId));
  }

  handleExecuteTableChange = (pagination, filters, sorter, barFilters) => {
    const Filters = { ...filters };
    if (barFilters && barFilters.length > 0) {
      Filters.summary = barFilters;
    }
    if (pagination.current) {
      TestPlanStore.setFilters(Filters);
      TestPlanStore.rightEnterLoading();
      TestPlanStore.setExecutePagination(pagination);
      TestPlanStore.reloadCycle();
    }
  }

  onDragEnd = (sourceIndex, targetIndex) => {
    const { testList } = TestPlanStore;
    const { lastRank, nextRank } = getDragRank(sourceIndex, targetIndex, testList);    
    const source = testList[sourceIndex];
    const temp = { ...source };
    delete temp.defects;
    delete temp.caseAttachment;
    delete temp.testCycleCaseStepES;
    delete temp.issueInfosVO;
    temp.assignedTo = temp.assignedTo || 0;
    TestPlanStore.rightEnterLoading();
    editExecuteDetail({
      ...temp,
      ...{
        lastRank,
        nextRank,
      },
    }).then((res) => {
      TestPlanStore.reloadCycle();
    }).catch((err) => {
      Choerodon.prompt('网络错误');
      TestPlanStore.rightLeaveLoading();
    });
  }

  handleLastUpdatedByChange=(value) => {
    TestPlanStore.setLastUpdatedBy(value);
    TestPlanStore.loadCycle();
  }

  handleAssignedToChange=(value) => {
    TestPlanStore.setAssignedTo(value);
    TestPlanStore.loadCycle();
  }

  handleDeleteExecute = (record) => {
    const { executeId } = record;
    confirm({
      width: 560,
      title: Choerodon.getMessage('确认删除吗?', 'Confirm delete'),
      content: Choerodon.getMessage('当您点击删除后，该条执行将从此计划阶段中移除!', 'When you click delete, after which the data will be deleted !'),
      onOk: () => {
        TestPlanStore.rightEnterLoading();
        deleteExecute(executeId)
          .then((res) => {           
            TestPlanStore.reloadCycle();
          }).catch((err) => {
            /* console.log(err); */
            Choerodon.prompt('网络异常');
            TestPlanStore.rightLeaveLoading();
          });
      },
      okText: '删除',
      okType: 'danger',
    });
  }

  render() {    
    const {
      setCreateCycleVisible, 
    } = TestPlanStore;
    return (
      <Page className="c7ntest-TestPlan">
        <Header title={<FormattedMessage id="testPlan_name" />}>
          <Button icon="playlist_add" onClick={() => { setCreateCycleVisible(true); }}>            
            <FormattedMessage id="testPlan_creatCycle" />
          </Button>
          <Button icon="archive" onClick={() => this.ExportSide.open()}>           
            <FormattedMessage id="testPlan_export" />
          </Button>
          <Button icon="collections_bookmark" onClick={() => this.BatchClone.open()}>            
            批量克隆
          </Button>
          {/* <Button icon="autorenew" onClick={this.refresh}>           
            <FormattedMessage id="refresh" />
          </Button> */}
        </Header>
        <Breadcrumb title="" />
        <div className="breadcrumb-border" />
        <Content
          title={null}
          description={null}
          style={{ padding: 0, display: 'flex' }}
        >
          <Injecter store={TestPlanStore} item="loading">
            {loading => <Loading loading={loading} />}
          </Injecter>
          <div className="c7ntest-TestPlan-content">
            <Injecter store={TestPlanStore} item="EditCycleVisible">
              {visible => <EditCycle visible={visible} />}
            </Injecter>
            <Injecter store={TestPlanStore} item="EditStageVisible">
              {visible => <EditStage visible={visible} />}
            </Injecter>
            <Injecter store={TestPlanStore} item="CreateCycleVisible">
              {visible => (
                <CreateCycle
                  visible={visible}
                  onCancel={() => { setCreateCycleVisible(false); }}
                  onOk={() => { setCreateCycleVisible(false); this.refresh(); }}
                />
              )}
            </Injecter>              
            <ExportSide ref={this.saveRef('ExportSide')} />
            <BatchClone ref={this.saveRef('BatchClone')} onOk={this.refresh} />
            <Injecter store={TestPlanStore} item="isTreeVisible">
              {isTreeVisible => <TreeArea isTreeVisible={isTreeVisible} setIsTreeVisible={TestPlanStore.setIsTreeVisible} />}
            </Injecter>
            <Injecter store={TestPlanStore} item={['currentCycle', 'getTimes', 'calendarShowMode', 'getTimesLength']}>
              {([currentCycle, times, calendarShowMode, getTimesLength]) => (currentCycle.key && getTimesLength ? (
                <div className="c7ntest-TestPlan-content-right">
                  <EventCalendar key={`${currentCycle.key}_${times.length}`} showMode={calendarShowMode} times={times} onItemClick={this.handleItemClick} />
                  {calendarShowMode === 'single' && (
                    <Injecter store={TestPlanStore} item={['statusList', 'prioritys', 'getTestList', 'executePagination', 'rightLoading']}>
                      {([statusList, prioritys, testList, executePagination, rightLoading]) => (
                        <TestPlanTable
                          prioritys={prioritys}
                          statusList={statusList}
                          loading={rightLoading}
                          pagination={executePagination}
                          dataSource={testList}
                          onLastUpdatedByChange={this.handleLastUpdatedByChange}
                          onAssignedToChange={this.handleAssignedToChange}
                          onDragEnd={this.onDragEnd}                        
                          onTableChange={this.handleExecuteTableChange}
                          onTableRowClick={this.handleTableRowClick}
                          onDeleteExecute={this.handleDeleteExecute}
                        />
                      )}
                    </Injecter>
                  
                  )}
                </div> 
              ) : <NoCycle />)}
            </Injecter>
          </div>
        </Content>
      </Page>
    );
  }
}

TestPlanHome.propTypes = {

};

export default TestPlanHome;
