import React, { useState } from 'react';
import classNames from 'classnames';
import { Icon, Popconfirm, Tooltip } from 'choerodon-ui';
import PriorityTag from '@/components/PriorityTag';
import TypeTag from '@/components/TypeTag';
import TestPlanItem from '../test-plan';
import styles from './index.less';

const CaseListItem = ({
  link, i, disabled, testLinkStore,
}) => {
  const [expand, setExpand] = useState(false);
  function handleDelete() {
    testLinkStore.delete(link.linkId);
  }

  return (
    <>
      <div className={styles.case}>
        <Icon
          type="baseline-arrow_right"
          className={classNames(styles.icon, {
            [styles.icon_expand]: expand,
          })}
          onClick={() => {
            setExpand(!expand);
          }}
        />
        <Tooltip mouseEnterDelay={0.5} title="测试用例">
          <div>
            <TypeTag
              data={{ icon: 'test-case', colour: 'rgb(77, 144, 254)' }}
            />
          </div>
        </Tooltip>
        <Tooltip title={`编号概要： ${link.caseNum} ${link.summary}`}>
          <div style={{ marginLeft: 8, flex: 1, overflow: 'hidden' }}>
            <p
              className="c7n-issueList-summary"
              style={{
                color: '#3F51B5', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0,
              }}
              role="none"
              onClick={() => {
                testLinkStore.toLink(link.caseId, link.caseNum, link.folderId);
              }}
            >
              {`${link.caseNum} ${link.summary}`}
            </p>
          </div>
        </Tooltip>

        <div style={{ marginRight: '8px', overflow: 'hidden' }}>
          <Tooltip mouseEnterDelay={0.5} title={`优先级： ${link.priorityName}`}>
            <div>
              <PriorityTag
                priority={{ colour: link.priorityColour, name: link.priorityName }}
              />
            </div>
          </Tooltip>
        </div>
        {
        !disabled && (
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              fontSize: '16px',
            }}
          >
            <Popconfirm
              title="确认要删除该问题关联的测试用例吗?"
              placement="left"
              onConfirm={() => handleDelete()}
              //   onCancel={this.cancel}
              okText="删除"
              cancelText="取消"
              okType="danger"
            >
              <Icon type="delete_forever pointer" />
            </Popconfirm>
          </div>
        )
      }
      </div>
      {expand && <TestPlanItem />}
    </>
  );
};
export default CaseListItem;
