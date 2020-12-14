import React, { useRef, useState } from 'react';
import {
  Page, Breadcrumb, Content,
} from '@choerodon/boot';
import { useMount, useCreation } from 'ahooks';
import TestReportContext, { BaseInfoRef } from './context';
import TestReportStore from './store';
import DetailCard from './components/detail-card';
import PieChart from './components/pie-chart';
import FailedTable from './components/table-card/FailedTable';
import BugTable from './components/table-card/BugTable';

interface Props {
  preview?: boolean
  planId:string
}
const ReportPage: React.FC<Props> = ({ preview: forcePreview, planId }) => {
  const baseInfoRef = useRef<BaseInfoRef>({} as BaseInfoRef);
  const [preview, setPreview] = useState(forcePreview !== undefined ? forcePreview : false);
  const store = useCreation(() => new TestReportStore({ planId }), [planId]);
  useMount(() => {
    store.loadData();
  });
  return (
    <TestReportContext.Provider value={{
      store,
      baseInfoRef,
      preview,
      setPreview,
    }}
    >
      <Page>
        {/* <Header>
          <Button icon="unarchive">导出报告</Button>
          <Button icon="send">发送报告</Button>
        </Header> */}
        <Breadcrumb title="报告" />
        <Content style={{ borderTop: '1px solid #0000001F' }}>
          <div style={{ display: 'flex' }}>
            <DetailCard />
            <PieChart />
          </div>
          <FailedTable />
          <BugTable />
        </Content>
      </Page>
    </TestReportContext.Provider>
  );
};
export default ReportPage;
