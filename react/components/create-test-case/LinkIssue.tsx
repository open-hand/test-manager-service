import React, {
  useEffect, useCallback, useMemo, useRef,
} from 'react';
import {
  DataSet, Modal, Table, Form,
  TextField,
  Icon,
} from 'choerodon-ui/pro';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import MODAL_WIDTH from '@choerodon/agile/lib/constants/MODAL_WIDTH';
import { statusApi } from '@choerodon/agile/lib/api';
import SelectStatus from '@choerodon/agile/lib/components/select/select-status';
import SelectPriority from '@choerodon/agile/lib/components/select/select-priority';
import SelectUser from '@choerodon/agile/lib/components/select/select-user';
import SelectSprint from '@choerodon/agile/lib/components/select/select-sprint';
import renderStatus from '@choerodon/agile/lib/components/column-renderer/status';
import renderSummary from '@choerodon/agile/lib/components/column-renderer/summary';
import renderPriority from '@choerodon/agile/lib/components/column-renderer/priority';
import renderSprint from '@choerodon/agile/lib/components/column-renderer/sprint';
import Record from 'choerodon-ui/pro/lib/data-set/Record';
import { find } from 'lodash';
import { getProjectId } from '@/common/utils';

interface Props {
  modal?: IModalProps,
  selected?: Record[]
  onSubmit: (selected: Record[]) => void
}
const { Column } = Table;
const LinkIssueModal: React.FC<Props> = (props) => {
  const { modal, onSubmit, selected } = props;
  const dataSetRef = useRef<DataSet>();
  const queryDataSet = useMemo(() => new DataSet({
    fields: [{
      name: 'content',
      label: '请输入搜索内容',
    }, {
      name: 'status',
      label: '状态',
      multiple: true,
    }, {
      name: 'priority',
      label: '优先级',
      multiple: true,
    }, {
      name: 'assignee',
      label: '经办人',
      multiple: true,
    }, {
      name: 'sprint',
      label: '冲刺',
      multiple: true,
    }],
    events: {
      update() {
        dataSetRef.current?.query();
      },
    },
  }), []);
  const dataSet = useMemo(() => new DataSet({
    autoQuery: true,
    cacheSelection: true,
    primaryKey: 'issueId',
    fields: [{
      name: 'summary',
      label: '概要',
    }, {
      name: 'issueNum',
      label: '任务编号',
    }, {
      name: 'statusId',
      label: '状态',
    }, {
      name: 'priorityId',
      label: '优先级',
    }, {
      name: 'sprintId',
      label: '冲刺',
    }],
    transport: {
      read: ({ data }) => ({
        method: 'post',
        url: `/test/v1/projects/${getProjectId()}/case/agile/un_link_issue/0`,
        data: {
          contents: data.content ? [data.content] : undefined,
          advancedSearchArgs: {
            statusId: data.status,
            priorityId: data.priority,
          },
          otherArgs: {
            assigneeId: data.assignee,
            sprint: data.sprint,
          },
          searchArgs: {
            tree: false,
          },
        },
      }),
    },
    queryDataSet,
    events: {
      load: () => {
        dataSet.forEach((record) => {
          const checked = find(selected, (r) => record.get('issueId') === r.get('issueId'));
          if (checked) {
            dataSet.select(record);
          }
        });
      },
    },
  }), [queryDataSet, selected]);
  dataSetRef.current = dataSet;
  const handleOk = useCallback(async () => {
    onSubmit(dataSet.selected || []);
    return true;
  }, [dataSet.selected, onSubmit]);
  useEffect(() => {
    modal?.handleOk(handleOk);
  }, [handleOk, modal]);
  return (
    <div>
      <Table
        dataSet={dataSet}
        queryBar={() => (
          <Form dataSet={queryDataSet} columns={6}>
            <TextField colSpan={2} name="content" prefix={<Icon type="search" />} />
            <SelectStatus name="status" request={() => statusApi.loadByProject('agile')} />
            <SelectPriority name="priority" />
            <SelectUser name="assignee" />
            <SelectSprint name="sprint" />
          </Form>
        )}
      >
        <Column name="summary" renderer={({ record }) => renderSummary({ record, clickable: false })} />
        <Column name="issueNum" sortable width={135} />
        <Column name="statusId" sortable width={135} renderer={renderStatus} />
        <Column name="priorityId" sortable width={80} renderer={renderPriority} />
        <Column name="sprintId" width={135} renderer={renderSprint} />
      </Table>
    </div>
  );
};

const openLinkIssueModal = (props: Props) => {
  Modal.open({
    key: 'LinkIssueModal',
    title: '测试用例关联工作项',
    style: {
      width: MODAL_WIDTH.middle + 100,
    },
    drawer: true,
    children: <LinkIssueModal {...props} />,
  });
};
export default openLinkIssueModal;
