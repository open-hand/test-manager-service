import { stores, axios } from '@choerodon/master';
import QuillDeltaToHtmlConverter from 'quill-delta-to-html';
import _ from 'lodash';
// eslint-disable-next-line import/no-cycle
import { uploadImage, uploadFileAgile } from '../api/FileApi';
import humanize from './humanizeDuration';

const { AppState } = stores;

export function text2Delta(description) {
  let temp = description;
  try {
    temp = JSON.parse(description);
  } catch (error) {
    temp = description;
  }
  return temp;
}
/**
 * 将quill特有的文本结构转为html
 * @param {*} delta
 */
export function delta2Html(description, config) {
  // 修复普通文本显示
  let temp = description;
  try {
    JSON.parse(description);
    const obj = JSON.parse(description);
    if (typeof obj !== 'object' || !obj) {
      throw new Error('不是JSON格式');
    }
  } catch (error) {
    // console.log(description, error);
    temp = JSON.stringify([{ insert: description }]);
  }

  const delta = text2Delta(temp);
  const converter = new QuillDeltaToHtmlConverter(delta, config);
  const text = converter.convert();
  // if (text.substring(0, 3) === '<p>') {
  //   return text.substring(3);
  // } else {
  // console.log(description, text);
  return text;
  // }
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
 * 将以base64的图片url数据转换为Blob
 * @param {string} urlData 用url方式表示的base64图片数据
 */
export function convertBase64UrlToBlob(urlData) {
  const bytes = window.atob(urlData.split(',')[1]); // 去掉url的头，并转换为byte

  // 处理异常,将ascii码小于0的转换为大于0
  const buffer = new ArrayBuffer(bytes.length);
  const unit8Array = new Uint8Array(buffer);
  for (let i = 0; i < bytes.length; i += 1) {
    unit8Array[i] = bytes.charCodeAt(i);
  }

  return new Blob([buffer], { type: 'image/png' });
}
/**
 * 从deltaOps中获取图片数据
 * @param {DeltaOperation []} deltaOps
 */
export function getImgInDelta(deltaOps) {
  const imgBase = [];
  const formData = new FormData();
  if (deltaOps instanceof Array) {
    deltaOps.forEach((item) => {
      if (item.insert && item.insert.image) {
        if (item.insert.image.split(':').length && item.insert.image.split(':')[0] === 'data') {
          imgBase.push(item.insert.image);
          formData.append('file', convertBase64UrlToBlob(item.insert.image), 'blob.png');
        }
      }
    });
  }

  return { imgBase, formData };
}

/**
 * 将富文本中的base64图片替换为对应的url
 * @param {{url:string} []} imgUrlList 图标url对应的
 * @param {any []} imgBase base64图片数组
 * @param {*} text 富文本的文本结构
 */
export function replaceBase64ToUrl(imgUrlList, imgBase, text) {
  const deltaOps = text;
  const imgMap = {};
  imgUrlList.forEach((imgUrl, index) => {
    imgMap[imgBase[index]] = `${imgUrl}`;
  });
  deltaOps.forEach((item, index) => {
    if (item.insert && item.insert.image && imgBase.indexOf(item.insert.image) !== -1) {
      deltaOps[index].insert.image = imgMap[item.insert.image];
    }
  });
}

/**
 * 适用于各个issue的模态框编辑界面富文本上传
 * 富文本内容上传前的图片的检测与上传
 * @param {object} text 富文本的文本结构
 * @param {object} data 要发送的数据
 * @param {function} func 回调
 */
export function beforeTextUpload(text, data, func, pro = 'description') {
  const deltaOps = text;
  const send = data;
  const { imgBase, formData } = getImgInDelta(deltaOps);
  if (imgBase.length) {
    uploadImage(formData).then((imgUrlList) => {
      replaceBase64ToUrl(imgUrlList, imgBase, deltaOps);
      const converter = new QuillDeltaToHtmlConverter(deltaOps, {});
      const html = converter.convert();
      // send.gitlabDescription = html;
      send[pro] = JSON.stringify(deltaOps);
      func(send);
    });
  } else {
    const converter = new QuillDeltaToHtmlConverter(deltaOps, {});
    const html = converter.convert();
    // send.gitlabDescription = html;
    send[pro] = deltaOps ? JSON.stringify(deltaOps) : deltaOps;

    func(send);
  }
}

export function returnBeforeTextUpload(text, data, func, pro = 'description') {
  const deltaOps = text;
  const send = data;
  const { imgBase, formData } = getImgInDelta(deltaOps);
  if (imgBase.length) {
    return uploadImage(formData).then((imgUrlList) => {
      replaceBase64ToUrl(imgUrlList, imgBase, deltaOps);
      send[pro] = JSON.stringify(deltaOps);
      return func(send);
    });
  } else {
    send[pro] = JSON.stringify(deltaOps);
    return func(send);
  }
}

/**
 * 适用于富文本附件上传以及回调
 * @param {any []} propFileList 文件列表
 * @param {function} func 回调
 * @param {{issueType:string,issueId:number,fileName:string}} config 附件上传的额外信息
 */
export function handleFileUpload(propFileList, func, config) {
  const fileList = propFileList.filter(i => !i.url);
  const formData = new FormData();
  fileList.forEach((file) => {
    // file.name = encodeURI(encodeURI(file.name));
    formData.append('file', file);
  });
  uploadFileAgile(formData, config)
    .then((response) => {
      const newFileList = [
        {
          uid: -1,
          name: fileList[0].name,
          status: 'done',
          url: response,
        },
      ];
      Choerodon.prompt('上传成功');
      func(newFileList);
    })
    .catch((error) => {
      if (error.response) {
        Choerodon.prompt(error.response.data.message);
      } else {
        Choerodon.prompt(error.message);
      }
      const temp = propFileList.slice();
      temp.forEach((one) => {
        if (!one.url) {
          const tmp = one;
          tmp.status = 'error';
        }
      });
      func(temp);
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
      theRequest[strs[i].split('=')[0]] = decodeURI(strs[i].split('=')[1]);
    }
  }
  return theRequest;
}
export function commonLink(link) {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;

  return encodeURI(`/testManager${link}?type=${type}&id=${projectId}&organizationId=${organizationId}&name=${name}`);
}
export function issueLink(issueId, typeCode, issueName = null) {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;
  if (typeCode === 'issue_test' || typeCode === 'issue_auto_test') {
    return encodeURI(`/testManager/IssueManage?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}&paramIssueId=${issueId}&paramName=${issueName}`);
  } else if (issueName) {
    return encodeURI(`/agile/work-list/issue?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}&paramIssueId=${issueId}&paramName=${issueName}`);
  } else {
    return encodeURI(`/agile/work-list/issue?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}&paramIssueId=${issueId}`);
  }
}
export function createIssueLink() {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;
  return encodeURI(`/agile/work-list/issue?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}`);
}
export function agileVersionLink() {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;
  return encodeURI(`/agile/release?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}`);
}
export function TestExecuteLink(cycleId) {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;

  return encodeURI(`/testManager/TestExecute?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}${`${cycleId ? `&cycleId=${cycleId || 0}` : ''}`}`);
}
export function TestPlanLink(cycleId) {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;

  return encodeURI(`/testManager/TestPlan?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}${`${cycleId ? `&cycleId=${cycleId || 0}` : ''}`}`);
}
export function executeDetailLink(executeId, cycleId) {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;
  return encodeURI(`/testManager/TestExecute/execute/${executeId}?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}${`&cycleId=${cycleId || 0}`}`);
}
export function executeDetailShowLink(executeId) {
  return commonLink(`/TestPlan/executeShow/${executeId}`);
}
export function testCaseDetailLink(testCaseId, folderName) {
  const menu = AppState.currentMenuType;
  const {
    type, id: projectId, name, organizationId,
  } = menu;
  return encodeURI(`/testManager/IssueManage/testCase/${testCaseId}?type=${type}&id=${projectId}&name=${name}&organizationId=${organizationId}&folderName=${folderName || ''}`);
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
