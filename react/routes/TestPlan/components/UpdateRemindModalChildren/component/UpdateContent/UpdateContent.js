import React from 'react';
import { observer } from 'mobx-react-lite';
import SingleFileUpload from '../../../../../../components/SingleFileUpload';
import TestStepTable from '../TestStepTable';
import {
  delta2Html,
} from '@/common/utils';
import RichTextShow from '@/components/RichTextShow';
import './UpdateContent.less';

const prefix = 'c7ntest-testPlan-updateRemind-updateContent';

function UpdateContent(props) {
  const { tag, updateData, dataSet } = props;
  const fileList = tag === 'old' ? ((updateData && updateData.caseAttachment) || []) : ((updateData && updateData.attachment) || []);
  return (
    <div className={`${prefix}-detail`}>
      <div className={`${prefix}-detail-tag ${tag}-tag`}><span>{tag === 'new' ? '新' : '旧'}</span></div>
      <div className={`${prefix}-detail-info`}>
        <div className={`${prefix}-item`}>
          <div className={`${prefix}-item-field`}>文件夹</div>
          <div className={`${prefix}-item-value`}>{tag === 'old' ? updateData && updateData.folderName : updateData && updateData.folder}</div>
        </div>
        <div className={`${prefix}-item`}>
          <div className={`${prefix}-item-field`}>用例名称</div>
          <div className={`${prefix}-item-value`}>{updateData && updateData.summary}</div>
        </div>
        <div className={`${prefix}-item`}>
          <div className={`${prefix}-item-field`}>描述</div>
          <div className={`${prefix}-item-value`}>
            <RichTextShow data={(updateData && delta2Html(updateData.description)) || ''} />
          </div>
        </div>
        <div className={`${prefix}-item`}>
          <div className={`${prefix}-item-field`}>附件</div>
          <div className={`${prefix}-item-value`}>
            {
              fileList && fileList.length > 0 && (
                fileList.map(item => (
                  <SingleFileUpload
                    key={tag === 'new' ? item.attachmentId : item.id}
                    url={item.url}
                    fileName={tag === 'new' ? item.fileName : item.attachmentName}
                  />
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
