import React, { useState, useEffect } from 'react';
import { Modal } from 'choerodon-ui/pro';
import WYSIWYGEditor from '../WYSIWYGEditor';
import './FullEditor.scss';

const key = Modal.key();
function formatValue(value) {
  let delta = value;
  try {
    JSON.parse(value);
    delta = JSON.parse(value);
  } catch (error) {
    delta = value;
  }
  return delta;
}

export function FullEditor({
  initValue, onOk, modal,
}) {
  const [delta, setDelta] = useState(formatValue(initValue));

  useEffect(() => {
    setDelta(formatValue(initValue));
  }, [initValue]);

  const handleOk = () => {
    onOk(delta);
  };
  modal.handleOk(handleOk);
  return (
    <WYSIWYGEditor
      autoFocus
      hideFullScreen
      value={delta}
      style={{ height: '100%', width: '100%' }}
      onChange={(value) => {
        setDelta(value);
      }}
    />
  );
}
export default function openFullEditor({
  initValue, onOk,
}) {
  Modal.open({
    title: '编辑描述',
    key,
    className: 'c7n-editor-fullscreen',
    fullScreen: true,
    children: <FullEditor initValue={initValue} onOk={onOk} />,
  });
}
