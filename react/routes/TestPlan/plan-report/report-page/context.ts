import { createContext, useContext } from 'react';
import TestReportStore from './store';

export interface BaseInfoRef {
  submit: () => Promise<boolean | {

  }>
}
interface Context {
  store: TestReportStore
  baseInfoRef: React.MutableRefObject<BaseInfoRef>
  preview: boolean
  loadTask?: Array<string | number>
  setPreview: React.Dispatch<React.SetStateAction<boolean>>
}
const TestReportContext = createContext({} as Context);

function useTestReportContext() {
  const context = useContext(TestReportContext);
  return context;
}
export { useTestReportContext };
export default TestReportContext;
