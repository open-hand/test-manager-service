// @ts-nocheck
import React, { useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { Button } from 'choerodon-ui/pro';
import { C7NLocaleProvider } from '@choerodon/master';
import Modal from '@choerodon/agile/lib/routes/Issue/components/Modal';
import classNames from 'classnames';
import openBatchDeleteModal from './BatchDeleteConfirm';
import BatchModal from './BatchModal';
import { TestPlanStore } from '../../stores/TestPlanStore';
import styles from './index.less';
import useFormatMessage from '@/hooks/useFormatMessage';

interface Props {
  close: () => void,
  onClickDelete: () => void,
  testPlanStore: TestPlanStore
}

const Header: React.FC<Props> = ({
  close, onClickDelete, testPlanStore,
}) => {
  const { checkIdMap } = testPlanStore;
  const formatMessage = useFormatMessage();

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
            onClick={handleClickAssign}
            className={classNames(styles.batch_btn, {
              [styles.currentBatch_btn]: testPlanStore.batchAction === 'assign',
            })}
          >
            {formatMessage({ id: 'test.plan.batch.assign' })}
          </Button>
          <Button
            icon="delete_forever"
            onClick={onClickDelete}
            className={classNames(styles.batch_btn, {
              [styles.currentBatch_btn]: testPlanStore.batchAction === 'delete',
            })}
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
  const handleImport = (language) => import(/* webpackInclude: /\index.(ts|js)$/ */`../../../../locale/${language}`);
  const close = () => {
    testPlanStore.checkIdMap.clear();
    testPlanStore.setBatchAction(undefined);
    testPlanStore.setAssignToUserId(undefined);
  };

  window.modal = Modal.open({
    key: 'batchModal',
    className: styles.batchModal,
    zIndex: 999,
    header: (
      <C7NLocaleProvider importer={handleImport}>
        <ObserverHeader
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
        />
      </C7NLocaleProvider>
    ),
    content: (
      <C7NLocaleProvider importer={handleImport}>
        <BatchModal
          testPlanStore={testPlanStore}
          onCancel={() => {
        modal?.close();
        close();
          }}
          onAssign={() => {
        modal?.close();
        close();
          }}
        />
      </C7NLocaleProvider>
    ),
  });
};

export const closeBatchModal = ({ testPlanStore }) => {
  if (window.modal) {
    window.modal?.close();
    testPlanStore.checkIdMap.clear();
    testPlanStore.setBatchAction(undefined);
  }
};
