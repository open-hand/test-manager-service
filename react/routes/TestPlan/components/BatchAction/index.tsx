// @ts-nocheck
import React, { useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { Button } from 'choerodon-ui';
import Modal from '@choerodon/agile/lib/routes/Issue/components/Modal';
import openBatchDeleteModal from './BatchDeleteConfirm';
import BatchModal from './BatchModal';
import { TestPlanStore } from '../../stores/TestPlanStore';
import styles from './index.less';

interface Props {
  close: () => void,
  onClickDelete: () => void,
  testPlanStore: TestPlanStore
}

const Header: React.FC<Props> = ({
  close, onClickDelete, testPlanStore,
}) => {
  const { checkIdMap } = testPlanStore;
  const handleClickAssign = useCallback(() => {
    testPlanStore.setBatchAction('assign');
  }, [testPlanStore]);
  return (
    <>
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <span style={{
          fontSize: '30px', fontWeight: 500, marginRight: 12, color: '#FFFFFF',
        }}
        >
          {`${checkIdMap.size}`}
        </span>
        <span style={{ fontSize: '16px', color: 'rgba(255, 255, 255, 0.8)', marginTop: 5 }}>项已选中</span>
      </div>
      <div style={{
        marginLeft: 'auto', height: 56, display: 'flex', alignItems: 'center',
      }}
      >
        <div style={{
          display: 'inline-block', height: 56, lineHeight: '56px', borderRight: '1px solid #95A5FF',
        }}
        >
          <Button
            icon="edit-o"
            style={{ color: 'white', marginRight: 6 }}
            onClick={handleClickAssign}
          >
            批量指派
          </Button>
          <Button
            icon="delete_forever"
            style={{ color: 'white', marginRight: 18 }}
            onClick={onClickDelete}
          >
            批量移除
          </Button>
        </div>
        <Button
          icon="close"
          shape="circle"
          style={{ color: 'white', marginRight: -10, marginLeft: 10 }}
          onClick={close}
        />
      </div>
    </>
  );
};
const ObserverHeader = observer(Header);

export const OpenBatchModal = ({ testPlanStore }: Props) => {
  const close = () => {
    testPlanStore.checkIdMap.clear();
    testPlanStore.setBatchAction(undefined);
    testPlanStore.setAssignToUserId(undefined);
  };

  window.modal = Modal.open({
    key: 'batchModal',
    className: styles.batchModal,
    zIndex: 999,
    header: <ObserverHeader
      close={() => {
        modal?.close();
        close();
      }}
      testPlanStore={testPlanStore}
      onClickDelete={() => {
        modal?.close();
        testPlanStore.setBatchAction('delete');
        openBatchDeleteModal({ testPlanStore, close });
      }}
    />,
    content: <BatchModal
      testPlanStore={testPlanStore}
      onCancel={() => {
        modal?.close();
        close();
      }}
      onAssign={() => {
        modal?.close();
        close();
      }}
    />,
  });
};

export const closeBatchModal = ({ testPlanStore }) => {
  if (modal) {
    modal?.close();
    testPlanStore.checkIdMap.clear();
    testPlanStore.setBatchAction(undefined);
  }
};
