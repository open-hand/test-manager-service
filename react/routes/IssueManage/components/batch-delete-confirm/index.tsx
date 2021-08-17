import React, {
  useCallback, useState,
} from 'react';
import {
  Modal, Button, Progress,
} from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { Choerodon, WSHandler } from '@choerodon/boot';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import { ButtonColor } from 'choerodon-ui/pro/lib/button/interface';
import { getProjectId } from '@/common/utils';
import STATUS_COLOR from '@/constants/STATUS';
import styles from './index.less';

interface Props {
  refresh?: () => void,
  handleDelete: () => Promise<void>,
  deleteCount: number,
}

interface ModalProps extends Props{
  modal?: IModalProps,
}

const BatchDeleteCaseModalContent: React.FC<ModalProps> = observer((props) => {
  const {
    modal, deleteCount, refresh, handleDelete,
  } = props;
  const [loading, setLoading] = useState<boolean | string>(false);
  const [progress, setProgress] = useState(0);

  const handleOk = useCallback(async () => {
    await handleDelete();
    setLoading(true);
    return false;
  }, [handleDelete]);

  const handleCancel = useCallback(() => {
    modal?.close();
  }, [modal]);

  const handleMessage = (message: string) => {
    const data = JSON.parse(message);
    if (data) {
      const { status, rate } = data;
      switch (status) {
        case 'success': {
          setLoading('success');
          setProgress(Number(rate));
          setTimeout(() => {
            Choerodon.prompt('移除成功');
            handleCancel();
            refresh && refresh();
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
      <div style={{ marginBottom: 20 }}>
        {`确定要移除选中的${deleteCount}个测试用例吗？`}
        <span style={{ color: '#F44336' }}>
          请谨慎操作！
        </span>
        <WSHandler
          messageKey={`test-batch-delete-case-${getProjectId()}`}
          onMessage={handleMessage}
        >
          { loading && (
          <div style={{ textAlign: 'center', marginTop: 16 }}>
            {loading === 'success' ? '移除成功' : ['正在移除，请稍等片刻', <span className={styles.dot}>…</span>]}
            <Progress strokeColor={STATUS_COLOR.done} value={Math.round(progress * 100)} />
          </div>
          )}
        </WSHandler>
      </div>
      <div className={styles.footer} style={{ display: 'flex', justifyContent: 'flex-end' }}>
        <Button
          onClick={handleCancel}
          disabled={!!loading}
        >
          取消
        </Button>
        <Button
          color={'primary' as ButtonColor}
          className={styles.batchDeleteBtn}
          disabled={!!loading}
          loading={Boolean(loading)}
          onClick={handleOk}
        >
          移除
        </Button>
      </div>
    </div>
  );
});

const openBatchDeleteCaseModal = (props: Props) => {
  Modal.open({
    key: 'BatchRemoveCaseModal',
    title: '移除测试用例',
    style: {
      width: 520,
    },
    className: styles.batchDeleteModal,
    children: <BatchDeleteCaseModalContent {...props} />,
    footer: () => null,
  });
};
export default openBatchDeleteCaseModal;
