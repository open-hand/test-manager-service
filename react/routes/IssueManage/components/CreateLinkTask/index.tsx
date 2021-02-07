import React, {
  useEffect, useCallback, useMemo,
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

import { LabelLayout } from 'choerodon-ui/pro/lib/form/enum';
import { FieldType } from 'choerodon-ui/pro/lib/data-set/enum';
import { getProjectId } from '@/common/utils';
import { createLink } from '@/api/IssueManageApi';
import { TableQueryBarType } from 'choerodon-ui/pro/lib/table/enum';

interface Props {
  modal?: IModalProps,
  issueId: string
  onSubmit: () => void
}
const { Column } = Table;
const LinkIssueModal: React.FC<Props> = (props) => {
  const { modal, issueId, onSubmit } = props;
  const handleSubmit = useCallback(async () => false, []);
  useEffect(() => {
    modal?.handleOk(handleSubmit);
  }, [handleSubmit, modal]);
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
  }), []);
  const dataSet = useMemo(() => new DataSet({
    autoQuery: true,
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
      name: 'priorityDTO',
      label: '优先级',
    }, {
      name: 'issueSprintVOS',
      label: '冲刺',
    }],
    transport: {
      read: ({ data }) => ({
        method: 'post',
        url: `/test/v1/projects/${getProjectId()}/case/agile/un_link_issue/${issueId}`,
        data: {
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
  }), [issueId, queryDataSet]);

  const handleOk = useCallback(async () => {
    if (dataSet.selected.length > 0) {
      await createLink(issueId, dataSet.selected.map((record) => record.get('issueId')));
      onSubmit();
      return true;
    }
    return true;
  }, [dataSet.selected, issueId, onSubmit]);
  useEffect(() => {
    modal?.handleOk(handleOk);
  }, [handleOk, modal]);
  return (
    <div>
      <Form dataSet={queryDataSet} columns={5}>
        <TextField name="content" prefix={<Icon type="search" />} />
        <SelectStatus name="status" request={() => statusApi.loadByProject('agile')} />
        <SelectPriority name="priority" />
        <SelectUser name="assignee" />
        <SelectSprint name="sprint" />
      </Form>
      <Table dataSet={dataSet} queryBar={'none' as TableQueryBarType}>
        <Column name="summary" renderer={({ record }) => renderSummary({ record, clickable: false })} />
        <Column name="issueNum" sortable width={135} />
        <Column name="statusId" sortable width={135} renderer={renderStatus} />
        <Column name="priorityDTO" sortable width={80} renderer={renderPriority} />
        <Column name="issueSprintVOS" sortable width={135} renderer={renderSprint} />
      </Table>
    </div>
  );
};

const openLinkIssueModal = (props: Props) => {
  Modal.open({
    key: 'LinkIssueModal',
    title: '测试用例关联问题项',
    style: {
      width: MODAL_WIDTH.middle + 100,
    },
    drawer: true,
    children: <LinkIssueModal {...props} />,
  });
};
export default openLinkIssueModal;
