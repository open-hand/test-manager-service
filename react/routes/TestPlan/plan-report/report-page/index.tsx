import React, { useRef, useState } from 'react';
import ReactDOM from 'react-dom';
import {
  Page, Breadcrumb, Content, Header,
} from '@choerodon/boot';
import { useMount, useCreation } from 'ahooks';
import { Button, Modal } from 'choerodon-ui/pro/lib';
import html2canvas from 'html2canvas';
import JsPDF from 'jspdf';

import { getProjectName } from '@/common/utils';
import TestReportContext, { BaseInfoRef } from './context';
import TestReportStore from './store';
import DetailCard from './components/detail-card';
import PieChart from './components/pie-chart';
import FailedTable from './components/table-card/FailedTable';
import BugTable from './components/table-card/BugTable';
import PreviewPage from './components/preview-page';

interface Props {
  preview?: boolean
  planId: string
}
const ReportPage: React.FC<Props> = ({ preview: forcePreview, planId }) => {
  const baseInfoRef = useRef<BaseInfoRef>({} as BaseInfoRef);
  const [preview, setPreview] = useState(forcePreview !== undefined ? forcePreview : false);
  const store = useCreation(() => new TestReportStore({ planId }), [planId]);
  useMount(() => {
    store.loadData();
  });
  async function handleExportReport() {
    const newEm = document.createElement('div');
    newEm.style.width = 'max-content';
    newEm.style.height = 'max-content';
    document.body.appendChild(newEm);

    const loadTask: (number | string)[] = new Array<number>(5).fill(0);
    ReactDOM.render((
      <PreviewPage store={store} baseInfoRef={baseInfoRef} loadTask={loadTask} />
    ), newEm);

    console.log('em', loadTask, newEm);
    // loadTask.length
    await new Promise((r) => {
      let maxWaitTimes = 100; /** 最大等待任务完成循环时间次数 */
      const doingTasks: string[] = [];
      function f1() {
        if (maxWaitTimes !== 0 && (loadTask.length > 0 || doingTasks.length > 0)) {
          const doingTaskIndex = loadTask.findIndex((item) => typeof (item) === 'string');
          if (doingTaskIndex !== -1 && loadTask[doingTaskIndex] !== 'error') {
            maxWaitTimes = 100;
            const doingTask = loadTask[doingTaskIndex] as string;
            const willFinishTaskIndex = doingTasks.findIndex((item) => doingTask.includes(item));
            willFinishTaskIndex !== -1 ? doingTasks.splice(willFinishTaskIndex, 1) : doingTasks.push(doingTask);
            loadTask.splice(doingTaskIndex, 1);
          } else {
            const waitIndex = loadTask.findIndex((item) => item === 0);
            waitIndex !== -1 && loadTask.splice(waitIndex, 1);
          }
          if (loadTask.length === 0) {
            maxWaitTimes -= 1;
          }
          setTimeout(f1, 200);
          return;
        }
        r(0);
      }
      f1();
    });
    await html2canvas(newEm.childNodes.item(0) as HTMLElement, {
      useCORS: true,
      allowTaint: true,
      // height: newEm.scrollHeight,
      // width: newEm.scrollWidth,
      scale: 2,
    }).then((canvas) => {
      const contentWidth = canvas.width;
      const contentHeight = canvas.height;
      console.log(`width:${contentWidth} height:${contentHeight}`);

      // 将canvas转为base64图片
      const pageData = canvas.toDataURL('image/jpeg', 1.0);

      const pdf = new JsPDF('l', 'pt', [contentWidth, contentHeight]);
      // 将内容图片添加到pdf中，因为内容宽高和pdf宽高一样，就只需要一页，位置就是 0,0
      pdf.addImage(pageData, 'jpeg', 0, 0, contentWidth, contentHeight);
      pdf.save(`${getProjectName()}-测试计划报告.pdf`);
      document.body.removeChild(newEm);
    }).catch(() => {
      document.body.removeChild(newEm);
    });
  }
  return (
    <TestReportContext.Provider value={{
      store,
      baseInfoRef,
      preview,
      setPreview,
    }}
    >
      <Page>
        <Header>
          <Button icon="unarchive" onClick={handleExportReport}>导出报告</Button>
          {/* <Button icon="send">发送报告</Button> */}
        </Header>
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
