import React, { useCallback, useContext } from 'react';
import { Input, Icon } from 'choerodon-ui';
import { TextField } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { TextEditToggle, TextEditTogglePro } from '@/components';
import { checkCustomNum } from '@/api/IssueManageApi';
import EditIssueContext from './stores';
import './Header.less';
import TypeTag from '../../../../components/TypeTag';

const { TextArea } = Input;
const { Text, Edit } = TextEditToggle;

function Header({
  onUpdate,
}) {
  const {
    store, disabled, prefixCls, onClose,
  } = useContext(EditIssueContext);
  const { issueInfo } = store;
  const { caseNum, summary, customNum } = issueInfo;

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
          fontSize: 16, fontWeight: 500, color: '#3F51B5', display: 'flex', alignItems: 'center',
        }}
        >
          <TypeTag data={{ icon: 'test-case', colour: 'rgb(77, 144, 254)' }} style={{ marginRight: 5 }} />
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
            {(data) => data || ''}
          </Text>
          <Edit>
            <TextArea style={{ fontSize: '20px', fontWeight: 500, padding: '0.04rem' }} maxLength={44} autosize autoFocus />
          </Edit>
        </TextEditToggle>
      </div>
      <div className={`${prefixCls}-content-header-customNum`}>
        <span className={`${prefixCls}-content-header-customNum-field`}>自定义编号：</span>
        <div
          style={{
            width: '150px',
          }}
        >
          <TextEditTogglePro
            disabled={disabled}
            formKey="customNum"
            onSubmit={(value, done) => { onUpdate({ customNum: value }, () => { }); }}
            initValue={customNum ? String(customNum) : undefined}
            editor={({ submit }) => (
              <TextField
                style={{
                  height: 32,
                }}
                maxLength={16}
                autoFocus
                clearButton
                validator={async (newVal) => {
                  if (newVal === customNum) {
                    return true;
                  }
                  const result = await checkCustomNum(newVal);
                  return result ? '自定义编码重复' : true;
                }}
                pattern={/^(([A-Za-z]+)|([0-9]+)|([A-Za-z]+-[0-9]+))$/}
                validationRenderer={(result) => {
                  if (result.ruleName === 'patternMismatch') {
                    return <span>编码只能由大小写字母、数字、&quot;-&quot;组成，如有字母，须以字母开头，不能以&quot;-&quot;结尾</span>;
                  }
                  return result.validationMessage;
                }}
              />
            )}
          >
            <div style={{ whiteSpace: 'nowrap' }}>
              {customNum || '无'}
            </div>
          </TextEditTogglePro>
        </div>
      </div>
    </div>
  );
}
export default observer(Header);
