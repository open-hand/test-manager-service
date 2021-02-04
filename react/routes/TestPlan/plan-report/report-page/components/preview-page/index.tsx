import React, {
  useEffect, useMemo, useRef, useState,
} from 'react';
import TestReportContext, { BaseInfoRef } from '../../context';
import DetailCard from '../detail-card';
import PieChart from '../pie-chart';
import FailedTable from '../table-card/FailedTable';
import BugTable from '../table-card/BugTable';
import TestReportStore from '../../store';
import styles from './index.less';

interface Props {
    store: TestReportStore
    baseInfoRef: React.MutableRefObject<BaseInfoRef>
    loadTask: (number | string)[]
}
const PreviewPage: React.FC<Props> = ({ store, baseInfoRef, loadTask }) => {
  useEffect(() => {
    if (loadTask.length < 5) {
      loadTask.push(...new Array(5 - loadTask.length).fill(0));
    }
  }, []);
  return (
    <TestReportContext.Provider value={{
      store,
      baseInfoRef,
      loadTask,
      preview: true,
      setPreview: () => { },
    }}
    >
      <div className={styles.page}>
        <div className={styles.title}>
          <h3>测试报告</h3>
          <span>测试计划名称</span>
        </div>
        <div className={styles.content}>
          <DetailCard />
          <PieChart style={{ height: 'auto' }} />
        </div>
        <FailedTable />
        <BugTable />
      </div>
    </TestReportContext.Provider>
  );
};
export default PreviewPage;
