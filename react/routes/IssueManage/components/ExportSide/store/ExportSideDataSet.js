import { DataSet } from 'choerodon-ui/pro';
import { Choerodon } from '@choerodon/boot';
import moment from 'moment';
import { getProjectId, humanizeDuration } from '../../../../../common/utils';

/**
 * 计算耗时
 * @param {*} record
 */
const onHumanizeDuration = (record) => {
  const { creationDate, lastUpdateDate } = record;
  const startTime = moment(creationDate);
  const lastTime = moment(lastUpdateDate);
  let diff = lastTime.diff(startTime);
  // console.log(diff);
  if (diff < 0) {
    diff = moment().diff(startTime);
  }
  return creationDate && lastUpdateDate
    ? humanizeDuration(diff)
    : null;
};

const ExportSideDataSet = (folderId, queryStatus = new DataSet({
  autoQuery: true,
  paging: false,
  fields: [
    { name: 'key', type: 'string' },
    { name: 'value', type: 'string' },
  ],
  data: [
    { key: 1, value: '正在进行' },
    { key: 2, value: '已完成' },
    { key: 3, value: '未完成' },

  ],
})) => new DataSet({
  autoQuery: true,
  paging: true,
  modifiedCheck: false,
  selection: false,
  fields: [
    {
      label: '导出来源',
      name: 'name',
      type: 'string',

    },
    {
      label: '用例个数',
      name: 'successfulCount',
      type: 'number',
    },
    {
      label: '导出时间',
      name: 'lastUpdateDate',
      type: 'string',
    },
    {
      label: '耗时',
      name: 'during',
      type: 'string',
    },
    {
      label: '进度',
      name: 'status',
      type: 'number',
    },
    {
      name: 'rate',
      type: 'number',
    },
    {
      label: '',
      name: 'fileUrl',
      type: 'string',
    },
  ],
  queryFields: [
    {
      label: '导出来源',
      name: 'name',
      type: 'string',
    },
    {
      label: '进度',
      name: 'status',
      type: 'string',
      textField: 'value',
      valueField: 'key',
      options: queryStatus,
    },
  ],
  transport: {
    read: ({ params, data }) => ({
      url: `/test/v1/projects/${getProjectId()}/test/fileload/history/case`,
      method: 'post',
      params: {
        ...params,
      },
      data: {
        advancedSearchArgs: {
          // status:2,
          ...data,
        },
      },
      transformResponse(res) {
        const resObj = JSON.parse(res);
        const { failed } = resObj;
        if (failed) {
          Choerodon.prompt(resObj.message);
          return resObj;
        }
        const newList = (resObj.content || []).map((item) => ({
          ...item,
          during: onHumanizeDuration(item),
        }));

        return ({
          ...JSON.parse(res),
          content: newList,
        });
      },
    }),
  },
});
export default ExportSideDataSet;
