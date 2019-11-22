import { DataSet } from 'choerodon-ui/pro/lib';
import { getProjectId } from '../../../../../common/utils';

/**
 * 根据字段isRoot判断是否禁止根节点可选
 * @param {*} dataSet 数据集
 */
function forbidRootsSelect({ dataSet }) {
  dataSet.forEach((record) => {
    // eslint-disable-next-line no-param-reassign
    record.selectable = !record.children;
    // record.selectable = record.get('parentId') !== 0;
  });
}
/**
 * 有默认值时初始化数据
 */
const initData = () => {
  // const record = handleFilterNodeByFolerId(deafultValue.folderId);
  // dataSet.forEach((record) => {
  //   if (record.get('folderId') === value) {
  //     try {
  //       searchParent(record);
  //     } catch (error) {
  //       Choerodon.prompt('数据错误');
  //     }
  //   }
  // }
  // dataSet.select(record);
  // console.log('initData', record);
  // if (record) {
  //   const selectData = { fileName: record.get('name'), folderId: record.get('folderId'), versionId: record.get('versionId') };
  //   pDataSet.current.set(name, selectData);
  // }
};
function initLoad(isForbidRoot, deafultValue, props) {
  if (isForbidRoot) {
    forbidRootsSelect(props);
  }
  if (deafultValue) {
    initData({ folderId: deafultValue.folderId, ...props });
  }
}
/**
 * 
 * @param {*} pDataSet  控制Select下拉框 DataSet
 * @param {*} name  字段名
 * @param {*} setData  设置当前选中项数据
 * @param {*} isForbidRoot  是否禁止根节点可选 默认禁止
 */
const treeDataSet = (pDataSet, name, deafultValue, setData = false, isForbidRoot = true) => new DataSet({
  primaryKey: 'folderId',
  paging: false,
  autoQuery: true,
  selection: 'single',
  parentField: 'parentId', // 父节点字段名
  expandField: 'expanded', // 是否打开节点字段名
  idField: 'folderId',
  fields: [
    { name: 'name', type: 'string' },
    { name: 'folderId', type: 'number' },
    { name: 'expanded', type: 'boolean' },
    { name: 'parentId', type: 'number' },
    { name: 'versionId', type: 'number' },
  ],

  transport: {
    read: () => ({
      url: `/test/v1/projects/${getProjectId()}/issueFolder/query`,
      method: 'get',
      transformResponse: (res) => {
        const resObj = JSON.parse(res);
        const newArr = resObj.treeFolder.map(item => ({
          expanded: item.expanded,
          ...item.issueFolderVO,
        }));
        // console.log('read', newArr);
        return newArr;
      },
    }),
  },
  events: {
    // 选中事件
    select: ({ record, dataSet }) => {
      // console.log('record', record);
      dataSet.select(record);
      const selectData = { fileName: record.get('name'), folderId: record.get('folderId'), versionId: record.get('versionId') };
      if (pDataSet) {
        pDataSet.current.set(name, selectData);
      }
      if (setData) {
        setData(selectData);
      }
    },
    // 数据加载完成后初始化事件
    load: initLoad.bind(this, isForbidRoot, deafultValue),
    // 取消选中事件
    unSelect: ({ record, dataSet }) => {
      dataSet.unSelect(record);
      if (pDataSet) {
        pDataSet.current.set(name, undefined);
      }
      if (setData) {
        setData(undefined);
      }
    },
  },
});

export default treeDataSet;
