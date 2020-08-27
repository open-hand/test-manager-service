import React, { useState, useEffect } from 'react';
import { Select } from 'choerodon-ui/pro/lib';
import { TextEditToggle } from '@/components';
import priorityApi from '@/api/priority';

const { Option } = Select;
function FieldPriority({ priority, onUpdate }) {
  const { colour, name, id } = priority;
  const [options, setOptions] = useState([]);
  useEffect(() => {
    priorityApi.load().then((res) => {
      if (Array.isArray(res)) {
        setOptions(res.filter(item => item.enableFlag || item.id === id));
      }
    });
  }, []);
  const disabled = false;
  return (
    <TextEditToggle
      disabled={disabled}
      formKey="priorityId"
      onSubmit={(value, done) => {
        if (value) {
          onUpdate({ priorityId: value }, done);
        }
      }}
      originData={id}
    >
      <TextEditToggle.Text>
        {id ? (
          <div
            className="c7n-level"
            style={{
              backgroundColor: `${colour}1F`,
              color: colour,
              borderRadius: '2px',
              padding: '0 8px',
              display: 'inline-block',
            }}
          >
            {name}
          </div>
        ) : (
          <div>
            无
          </div>
        )}
      </TextEditToggle.Text>
      <TextEditToggle.Edit>
        <Select>
          {options.map(option => <Option value={option.id}>{option.name}</Option>)}
        </Select>
      </TextEditToggle.Edit>

    </TextEditToggle>
  );
}
export default FieldPriority;
