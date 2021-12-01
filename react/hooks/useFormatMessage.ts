import useFormat from '@choerodon/agile/lib/hooks/useFormatMessage';
import type { IUseFormatMessageHook } from '@choerodon/agile/lib/hooks/useFormatMessage';

const useFormatMessage = (intlPrefix?: string) => useFormat<{ [key: string]: string }>(intlPrefix!);
export default useFormatMessage;
