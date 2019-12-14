import React, { useContext } from 'react';
import { Input, Icon } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { TextEditToggle } from '@/components';
import EditIssueContext from './stores';
import './Header.less';

const { TextArea } = Input;
const { Text, Edit } = TextEditToggle;

function Header({
  onUpdate,
}) {
  const {
    store, disabled, prefixCls, onClose,
  } = useContext(EditIssueContext);
  const { issueInfo } = store;
  const { caseNum, summary } = issueInfo;
  return (
    <div className={`${prefixCls}-content-header`}>
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          marginTop: 15,
        }}
      >
        {/* caseNum 用例编号 */}
        <div style={{
          fontSize: 16, fontWeight: 500, color: '#3F51B5',
        }}
        >
          <span>{caseNum}</span>
        </div>
        <div
          style={{
            cursor: 'pointer', fontSize: '13px', display: 'flex', alignItems: 'center', marginLeft: 'auto',
          }}
          role="none"
          onClick={onClose.bind(this, issueInfo)}
        >
          <Icon type="last_page" style={{ fontSize: '18px', fontWeight: '500' }} />
          隐藏详情
        </div>
      </div>
      <div style={{ marginBottom: 10, alignItems: 'center', marginTop: 10 }}>
        <TextEditToggle
          disabled={disabled}
          style={{ width: '100%', fontSize: '20px' }}
          formKey="summary"
          onSubmit={(value, done) => { onUpdate({ summary: value }, done); }}
          originData={summary}
        >
          <Text>
            {data => data || ''}
          </Text>
          <Edit>
            <TextArea style={{ fontSize: '20px', fontWeight: 500, padding: '0.04rem' }} maxLength={44} autosize autoFocus />
          </Edit>
        </TextEditToggle>
      </div>
    </div>
  );
}
export default observer(Header);
