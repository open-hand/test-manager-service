import React from 'react';
import { observer } from 'mobx-react-lite';
import { Tooltip } from 'choerodon-ui';
import {
  delta2Html,
} from '@/common/utils';
import RichTextShow from '@/components/RichTextShow';
import SingleFileUpload from '../../../../../../components/SingleFileUpload';
import TestStepTable from '../TestStepTable';
import './UpdateContent.less';

const prefix = 'c7ntest-testPlan-updateRemind-updateContent';

function UpdateContent(props) {
  const {
    tag, updateData, dataSet, cycleName,
  } = props;
  const fileList = tag === 'old' ? ((updateData && updateData.caseAttachment) || []) : ((updateData && updateData.attachment) || []);
  return (
    <div className={`${prefix}-detail`}>
      <div className={`${prefix}-detail-tag ${tag}-tag`}><span>{tag === 'new' ? '新' : '旧'}</span></div>
      <div className={`${prefix}-detail-info`}>
        <div className={`${prefix}-item`}>
          <div className={`${prefix}-item-field`}>目录</div>
          <div className={`${prefix}-item-value`}>{cycleName || ''}</div>
        </div>
        <div className={`${prefix}-item`}>
          <div className={`${prefix}-item-field`}>用例名称</div>
          <div className={`${prefix}-item-value`}>{updateData && updateData.summary}</div>
        </div>
        <div className={`${prefix}-item`}>
          <div className={`${prefix}-item-field`}>自定义编号</div>
          <div className={`${prefix}-item-value`}>{updateData && updateData.customNum}</div>
        </div>
        <div className={`${prefix}-item`}>
          <div className={`${prefix}-item-field`}>描述</div>
          <div
            className={`${prefix}-item-value`}
            style={{
              width: 'calc(100% - 1.36rem)',
            }}
          >
            <RichTextShow data={(updateData && delta2Html(updateData.description)) || ''} />
          </div>
        </div>
        <div className={`${prefix}-item`}>
          <div className={`${prefix}-item-field`}>附件</div>
          <div className={`${prefix}-item-value`} style={{ display: 'flex', flexWrap: 'wrap' }}>
            {
              fileList && fileList.length > 0 && (
                fileList.map((item) => (
                  <Tooltip title={tag === 'new' ? item.fileName : item.attachmentName}>
                    <div style={{ width: 150 }}>
                      <SingleFileUpload
                        key={tag === 'new' ? item.attachmentId : item.id}
                        url={item.url}
                        fileName={tag === 'new' ? item.fileName : item.attachmentName}
                      />
                    </div>
                  </Tooltip>
                ))
              )
            }
          </div>
        </div>
        <div className={`${prefix}-testStep`}>
          <div className={`${prefix}-testStep-field`}>测试步骤</div>
          <div className={`${prefix}-testStep-table`}>
            <TestStepTable dataSet={dataSet} />
          </div>
        </div>
      </div>
    </div>
  );
}

export default observer(UpdateContent);
