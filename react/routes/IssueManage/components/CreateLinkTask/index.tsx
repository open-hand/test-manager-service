import React, {
  useEffect, useCallback, useMemo, useRef, isValidElement,
} from 'react';
import {
  DataSet, Modal, Table,
  TextField,
  Icon,
} from 'choerodon-ui/pro';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import MODAL_WIDTH from '@choerodon/agile/lib/constants/MODAL_WIDTH';
import SelectStatus from '@choerodon/agile/lib/components/select/select-status';
import SelectPriority from '@choerodon/agile/lib/components/select/select-priority';
import SelectUser from '@choerodon/agile/lib/components/select/select-user';
import SelectSprint from '@choerodon/agile/lib/components/select/select-sprint';
import renderStatus from '@choerodon/agile/lib/components/column-renderer/status';
import renderSummary from '@choerodon/agile/lib/components/column-renderer/summary';
import renderPriority from '@choerodon/agile/lib/components/column-renderer/priority';
import renderSprint from '@choerodon/agile/lib/components/column-renderer/sprint';
import renderUser from '@choerodon/agile/lib/components/column-renderer/user';
import { getProjectId } from '@/common/utils';
import { createLink } from '@/api/IssueManageApi';
import { loadStatusByProject } from '@/api/agileApi';
import styles from './index.less';
import useIsWaterfall from '@/hooks/useIsWaterfall';

interface Props {
  modal?: IModalProps,
  issueId: string
  onSubmit: () => void
}
const { Column } = Table;
interface FormSimpleProps {
  itemInterval?: React.CSSProperties['marginRight']
  dataSet: DataSet
  flat?: boolean
}
const FormSimple: React.FC<FormSimpleProps> = ({
  children, itemInterval, dataSet, flat,
}) => {
  const childrenCount = React.Children.count(children);
  const renderFormItemWithAssignProps = useCallback((ch: React.ReactElement, index: number) => (ch.props.name && !dataSet.getField(ch.props.name) ? null : React.cloneElement(ch, {
    style: childrenCount ? { marginRight: childrenCount !== (index + 1) ? itemInterval : 0, marginTop: 8 } : undefined,
    dataSet,
    flat,
    className: styles.formSimple_item,
    placeholder: ch.props.placeholder ?? (ch.props.name ? dataSet.getField(ch.props.name)?.getProps().label : ''),
  })), [childrenCount, dataSet, flat, itemInterval]);
  const newChildren = React.Children.map(children, (ch: React.ReactElement, index) => (isValidElement(ch) ? renderFormItemWithAssignProps(ch, index) : ch));
  return (
    <div className={styles.formSimple}>
      {newChildren && newChildren[0]}
      <div className={styles.formSimple_right}>
        {newChildren?.slice(1)}
      </div>
    </div>
  );
};
FormSimple.defaultProps = {
  itemInterval: 10,
  flat: true,
};
const LinkIssueModal: React.FC<Props> = (props) => {
  const { modal, issueId, onSubmit } = props;
  const dataSetRef = useRef<DataSet>();
  const { isWaterfall, isWaterfallAgile } = useIsWaterfall();
  const agileDataSetFieldConfig = useMemo(() => (!isWaterfall || isWaterfallAgile ? [{
    name: 'sprint',
    label: '冲刺',
    multiple: true,
  }] : []), [isWaterfall, isWaterfallAgile]);
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
    }, ...agileDataSetFieldConfig],
    events: {
      update() {
        dataSetRef.current?.query();
      },
    },
  }), [agileDataSetFieldConfig]);
  const dataSet = useMemo(() => new DataSet({
    autoQuery: true,
    primaryKey: 'issueId',
    cacheSelection: true,
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
      name: 'assignee',
      label: '经办人',
    },
    {
      name: 'priorityId',
      label: '优先级',
    }, ...agileDataSetFieldConfig],
    transport: {
      read: ({ data }) => ({
        method: 'post',
        url: `/test/v1/projects/${getProjectId()}/case/agile/un_link_issue/${issueId}`,
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
  }), [agileDataSetFieldConfig, issueId, queryDataSet]);
  dataSetRef.current = dataSet;
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
      <Table
        dataSet={dataSet}
        queryBar={() => (
          <FormSimple dataSet={queryDataSet} itemInterval={10}>
            <TextField name="content" prefix={<Icon type="search" />} colSpan={2} />
            <SelectStatus name="status" request={() => loadStatusByProject('')} dropdownMatchSelectWidth={false} />
            <SelectPriority name="priority" dropdownMatchSelectWidth={false} />
            <SelectUser name="assignee" dropdownMatchSelectWidth={false} />
            <SelectSprint name="sprint" dropdownMatchSelectWidth={false} />
          </FormSimple>
        )}
      >
        <Column name="summary" renderer={({ record }) => renderSummary({ record, clickable: false })} />
        <Column name="issueNum" sortable width={135} />
        <Column name="statusId" sortable width={135} renderer={renderStatus} />
        <Column name="assignee" width={120} renderer={renderUser} />
        <Column name="priorityId" sortable width={100} renderer={renderPriority} />
        <Column name="sprint" width={135} renderer={renderSprint} />
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
