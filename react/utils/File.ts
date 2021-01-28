import { Choerodon } from '@choerodon/boot';

const MAX_FILE_SIZE = 1024 * 1024 * 30;
const MAX_FILE_NAME = 210;
export default function validateFile(fileList: FileList | File[], accept?: string) {
  if (fileList) {
    const sizeTooBig = Array.prototype.some.call(fileList, (file: File) => file.size > MAX_FILE_SIZE);
    const nameTooLong = Array.prototype.some.call(fileList, (file: File) => file.name && encodeURI(file.name).length > MAX_FILE_NAME);
    const dontAccept = accept && Array.prototype.some.call(fileList, (file: File) => file.name.includes(accept));
    if (sizeTooBig) {
      Choerodon.prompt('文件不能超过30M', 'error');
      return false;
    }
    if (nameTooLong) {
      Choerodon.prompt('文件名过长', 'error');
      return false;
    }

    if (dontAccept) {
      Choerodon.prompt('上传文件类型错误', 'error');
      return false;
    }
    return true;
  }
  return true;
}
