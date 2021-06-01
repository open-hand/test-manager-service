import { stores, axios } from '@choerodon/boot';
import QuillDeltaToHtmlConverter from 'quill-delta-to-html';
import queryString from 'query-string';
import humanize from './humanizeDuration';

const { AppState } = stores;

export function text2Delta(description) {
  if (!description) {
    return undefined;
  }
  // eslint-disable-next-line no-restricted-globals
  if (!isNaN(description)) {
    return String(description);
  }
  let temp = description;

  try {
    temp = description.replace(/\\n/g, '\\n')
      .replace(/\\'/g, "\\'")
      .replace(/\\"/g, '\\"')
      .replace(/\\&/g, '\\&')
      .replace(/\\r/g, '\\r')
      .replace(/\\t/g, '\\t')
      .replace(/\\b/g, '\\b')
      .replace(/\\f/g, '\\f');
    temp = JSON.parse(temp);
  } catch (error) {
    temp = description;
    if (typeof (description) === 'string') {
      temp = description.split(/\n|\r|\f/g).map((item) => ({ insert: `${item}\n` }));
    }
  }
  // return temp;
  return temp || '';
}
/**
 * 将quill特有的文本结构转为html
 * @param {*} delta
 */
export function delta2Html(description) {
  let isDelta = true;
  try {
    JSON.parse(description);
  } catch (error) {
    isDelta = false;
  }
  if (!isDelta) {
    return description;
  }
  const delta = text2Delta(description);
  const converter = new QuillDeltaToHtmlConverter(delta, {});
  const text = converter.convert();
  if (text.substring(0, 3) === '<p>') {
    return text.substring(3);
  }
  return text;
}

// 获取文件名后缀
export function getFileSuffix(fileName) {
  return fileName.replace(/.+\./, '').toLowerCase();
}

export function delta2Text(delta) {
  // console.log(delta2Html(delta, { encodeHtml: false }).replace(/<[^>]+>/g, ''));
  return delta2Html(delta, { encodeHtml: false }).replace(/<br\/>/g, '\n').replace(/<[^>]+>/g, '');
}
export function escape(str) {
  return str.replace(/<\/script/g, '<\\/script').replace(/<!--/g, '<\\!--');
}

/**
 * 获取 blob
 * @param  {String} url 目标文件地址
 * @return {Promise}
 */
function getBlob(url) {
  return new Promise((resolve) => {
    const xhr = new XMLHttpRequest();

    xhr.open('GET', url, true);
    xhr.responseType = 'blob';
    xhr.onload = () => {
      if (xhr.status === 200) {
        resolve(xhr.response);
      }
    };

    xhr.send();
  });
}

/**
* 保存
* @param  {Blob} blob
* @param  {String} filename 想要保存的文件名称
*/
function saveAs(blob, filename) {
  if (window.navigator.msSaveOrOpenBlob) {
    navigator.msSaveBlob(blob, filename);
  } else {
    const link = document.createElement('a');
    const body = document.querySelector('body');

    link.href = window.URL.createObjectURL(blob);
    link.download = filename;

    // fix Firefox
    link.style.display = 'none';
    body.appendChild(link);

    link.click();
    body.removeChild(link);

    window.URL.revokeObjectURL(link.href);
  }
}

/**
* 重命名下载文件
* @param  {String} url 目标文件地址
* @param  {String} filename 想要保存的文件名称
*/
export function renameDownload(url, filename) {
  getBlob(url).then((blob) => {
    saveAs(blob, filename);
  });
}
export function formatDate(str) {
  const MONTH = ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二'];
  if (!str) {
    return '';
  }
  const arr = str.split(' ');
  if (arr.length < 1) {
    return '';
  }
  const date = arr[0];
  const time = arr[1];
  if (!arr[0] || !arr[1]) {
    return '';
  }
  const d = date.split('-');
  const t = time.split(':');
  if (d.length < 3 || t.length < 3) {
    return '';
  }
  return `${d[2]}/${MONTH[(d[1] * 1) - 1]}月/${d[0].slice(2)} ${t[0] < 12 ? t[0] : (t[0] * 1) - 12}:${t[1]}  ${t[0] * 1 < 12 ? ' 上' : ' 下'}午`;
}

export function getParams(url) {
  const theRequest = {};
  if (url.indexOf('?') !== -1) {
    const str = url.split('?')[1];
    const strs = str.split('&');
    for (let i = 0; i < strs.length; i += 1) {
      theRequest[strs[i].split('=')[0]] = decodeURIComponent(strs[i].split('=')[1]);
    }
  }
  return theRequest;
}
export function commonLink(link) {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;

  return encodeURI(`/testManager${link}?type=${type}&id=${projectId}&organizationId=${organizationId}&orgId=${organizationId}&name=${name}`);
}
export function issueLink(issueId, typeCode, issueName, folderId) {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;
  if (typeCode === 'issue_test' || typeCode === 'issue_auto_test') {
    return encodeURI(`/testManager/IssueManage?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}&orgId=${organizationId}&paramIssueId=${issueId}&paramName=${issueName}&folderId=${folderId}`);
  } if (issueName) {
    return encodeURI(`/agile/work-list/issue?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}&orgId=${organizationId}&paramIssueId=${issueId}&paramName=${issueName}`);
  }
  return encodeURI(`/agile/work-list/issue?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}&orgId=${organizationId}&paramIssueId=${issueId}`);
}
export function createIssueLink() {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;
  return encodeURI(`/agile/work-list/issue?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}&orgId=${organizationId}`);
}
export function agileVersionLink() {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;
  return encodeURI(`/agile/work-list/version?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}&orgId=${organizationId}`);
}
export function TestExecuteLink(cycleId) {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;

  return encodeURI(`/testManager/TestPlan/execute/${cycleId}?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}&orgId=${`${cycleId ? `&cycleId=${cycleId || 0}` : ''}`}`);
}
export function TestPlanLink(cycleId) {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;

  return encodeURI(`/testManager/TestPlan?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}&orgId=${organizationId}${`${cycleId ? `&cycleId=${cycleId || 0}` : ''}`}`);
}
export function executeDetailLink(executeId, filters) {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;
  return encodeURI(`/testManager/TestPlan/execute/${executeId}?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}&orgId=${organizationId}&${queryString.stringify(filters, { encode: false })}`);
}
export function executeDetailShowLink(executeId) {
  return commonLink(`/TestPlan/executeShow/${executeId}`);
}
export function testCaseDetailLink(testCaseId, folderName) {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;
  return encodeURI(`/testManager/IssueManage/testCase/${testCaseId}?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}&orgId=${organizationId}&folderName=${folderName || ''}`);
}
export function testCaseTableLink(params) {
  return commonLink('/IssueManage');
}
/**
 * 颜色转rgba
 * @param {*} color
 * @param {*} alpha
 */
export function color2rgba(color, alpha = 1) {
  if (typeof color !== 'string') {
    return '';
  }
  const r = parseInt(color.slice(1, 3), 16);
  const g = parseInt(color.slice(3, 5), 16);
  const b = parseInt(color.slice(5, 7), 16);
  return `rgba(${r},${g},${b},${alpha})`;
}
/**
 * 时间（毫秒）转文字显示
 * @param {*} ms
 */
export function humanizeDuration(ms, config = {}) {
  return humanize(ms, {
    language: 'zh_CN',
    delimiter: '',
    spacer: '',
    largest: 2,
    round: true,
    ...config,
  });
}
export const getProjectId = () => AppState.currentMenuType.id;
export const getProjectName = () => AppState.currentMenuType.name;
export const getOrganizationId = () => AppState.currentMenuType.organizationId;
/**
 * 生成指定长度的随机字符串
 * @param len 字符串长度
 * @returns {string}
 */
export function randomString(len = 32) {
  let code = '';
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  const maxPos = chars.length;
  for (let i = 0; i < len; i += 1) {
    code += chars.charAt(Math.floor(Math.random() * (maxPos + 1)));
  }
  return code;
}
/**
 * randomWord 产生任意长度随机字母数字组合
 * @param randomFlag 是否任意长度 min-任意长度最小位[固定位数] max-任意长度最大位
 * @param min
 * @param max
 * @returns {string}
 */
export function randomWord(randomFlag, min, max) {
  let str = '';
  let range = min;
  const arr = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];

  // 随机产生
  if (randomFlag) {
    range = Math.round(Math.random() * (max - min)) + min;
  }
  for (let i = 0; i < range; i += 1) {
    const pos = Math.round(Math.random() * (arr.length - 1));
    str += arr[pos];
  }
  return str;
}
// 全局拦截
class Request {
  constructor() {
    // 请求队列，相同请求时，取消前一个请求
    // this.requestQueue = [];
    ['get', 'post', 'options', 'delete', 'put'].forEach((type) => {
      this[type] = (...args) => new Promise((resolve, reject) => {
        // const CancelToken = axios.CancelToken;
        // const source = CancelToken.source();
        let url = args[0];

        // const preSameRequest = _.find(this.requestQueue, { url, type });
        // if (preSameRequest) {
        //   this.requestQueue.splice(_.findIndex(this.requestQueue, { url, type }), 1);
        //   preSameRequest.cancel(`Request canceled ${url} ${type}`);
        // }
        // const requestObject = {
        //   url,
        //   type,
        //   cancel: source.cancel,
        // };
        // this.requestQueue.push(requestObject);
        if (Object.keys(getParams(url)).length > 0) {
          url += `&organizationId=${getOrganizationId()}`;
        } else {
          url += `?organizationId=${getOrganizationId()}`;
        }
        // eslint-disable-next-line no-param-reassign
        args[0] = url;
        // const cancelToken = source.token;
        // args.push({
        //   cancelToken,
        // });
        axios[type](...args).then((data) => {
          // if (data && data.failed) {
          //   // Choerodon.prompt(data.message);
          //   resolve(data);
          // } else {
          resolve(data);
          // }
        }).catch((error) => {
          // if (axios.isCancel(error)) {
          //   console.log('Rquest canceled', error.message); // 请求如果被取消，这里是返回取消的message
          // } else {
          // Choerodon.prompt(error.message);
          reject(error);
          // }
        }).finally(() => {
          // this.requestQueue.splice(_.findIndex(this.requestQueue, {
          //   url,
          //   type,
          // }), 1);
        });
      });
    });
  }
}
export const request = new Request();
export function getDragRank(sourceIndex, targetIndex, List) {
  let lastRank;
  let nextRank;
  if (sourceIndex < targetIndex) {
    lastRank = List[targetIndex].rank;
    nextRank = List[targetIndex + 1] ? List[targetIndex + 1].rank : null;
  } else if (sourceIndex > targetIndex) {
    lastRank = List[targetIndex - 1] ? List[targetIndex - 1].rank : null;
    nextRank = List[targetIndex].rank;
  }
  return {
    lastRank,
    nextRank,
  };
}
export function validateFile(rule, fileList, callback) {
  if (fileList) {
    fileList.forEach((file) => {
      if (file.size > 1024 * 1024 * 30) {
        callback('文件不能超过30M');
      } else if (file.name && encodeURI(file.name).length > 210) {
        callback('文件名过长');
      }
    });
    callback();
  } else {
    callback();
  }
}
export function normFile(e) {
  if (Array.isArray(e)) {
    return e;
  }
  return e && e.fileList;
}
export function handleRequestFailed(promise) {
  return promise.then((res) => {
    if (!res.failed) {
      return res;
    }
    return '';
  });
}
