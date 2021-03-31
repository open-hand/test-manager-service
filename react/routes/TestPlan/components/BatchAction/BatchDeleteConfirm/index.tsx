import React, {
  useCallback, useState,
} from 'react';
import {
  Modal, Button, Progress,
} from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { Choerodon, WSHandler } from '@choerodon/boot';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import { getProjectId } from '@/common/utils';

import { ProgressStatus } from 'choerodon-ui/lib/progress/enum';
import { TestPlanStore } from '@/routes/TestPlan/stores/TestPlanStore';
import styles from './index.less';

interface Props {
  modal?: IModalProps,
  testPlanStore: TestPlanStore
  close: () => void
}

const BatchDeleteModal: React.FC<Props> = (props) => {
  const {
    modal, testPlanStore, close,
  } = props;
  const { checkIdMap } = testPlanStore;
  const [loading, setLoading] = useState<boolean | string>(false);
  const [progress, setProgress] = useState(0);

  const handleDelete = useCallback(async () => {
    await testPlanStore.batchRemove();
    setLoading(true);
  }, [testPlanStore]);

  const handleCancel = useCallback(() => {
    modal?.close();
    close();
  }, [close, modal]);

  const handleMessage = (message: string) => {
    const data = JSON.parse(message);
    if (data) {
      const { status, rate } = data;
      switch (status) {
        case 'success': {
          setLoading('success');
          setTimeout(() => {
            Choerodon.prompt('移除成功');
            modal?.close();
            close();
            testPlanStore.loadExecutes();
          }, 2000);
          break;
        }
        case 'deleting': {
          setProgress(Number(rate));
          break;
        }
        case 'failed': {
          Choerodon.prompt(data.error, 'error');
          setLoading(false);
          break;
        }
        default: break;
      }
    }
  };

  return (
    <div>
      {`确定要移除选中的${checkIdMap.size}个执行用例吗？`}
      <span style={{ color: '#F44336' }}>
        请谨慎操作！
      </span>
      <WSHandler
        messageKey={`test-batch-delete-cycle-case-${getProjectId()}`}
        onMessage={handleMessage}
      >
        { loading && (
          <div style={{ color: 'rgba(254,71,87,1)', textAlign: 'center' }}>
            {loading === 'success' ? '移除成功' : ['正在移除，请稍等片刻', <span className={styles.dot}>…</span>]}
            <Progress status={'success' as ProgressStatus} value={Math.round(progress * 100)} />
          </div>
        )}
      </WSHandler>
      <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: 10 }}>
        <Button
          onClick={handleCancel}
          disabled={!!loading}
          style={{
            fontWeight: 500,
          }}
        >
          取消
        </Button>
        <Button
          className={styles.batchDeleteBtn}
          disabled={!!loading}
          loading={Boolean(loading)}
          style={{
            fontWeight: 500,
          }}
          onClick={() => {
            handleDelete();
          }}
        >
          移除
        </Button>
      </div>
    </div>
  );
};

const ObserverBatchDeleteModal = observer(BatchDeleteModal);
const openBatchDeleteModal = (props: Props) => {
  Modal.open({
    key: 'BatchRemoveModal',
    title: '移除执行用例',
    style: {
      width: 520,
    },
    className: styles.batchDeleteModal,
    children: <ObserverBatchDeleteModal {...props} />,
    footer: () => null,
  });
};
export default openBatchDeleteModal;
