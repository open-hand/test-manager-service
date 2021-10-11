import React from 'react';
import { ILog } from '@choerodon/agile/lib/common/types';

const fieldsMap = new Map([

  ['summary', {
    name: '测试用例概要',
  }],
  ['Attachment', {
    name: '附件',
    create: {
      operation: '上传',
      transform: ({ newString }: { newString: string }) => newString.split('@')[1],
    },
    delete: {
      operation: '删除',
      transform: ({ oldString }: { oldString: string }) => oldString.split('@')[1],
    },
  }],
  ['Comment', {
    name: '评论',
    create: {
      hidden: true,
    },
    update: {
      hidden: true,
    },
    delete: {
      operation: '删除',
      hidden: true,
    },
  }],
  ['description', {
    name: '描述',
    create: {
      hidden: true,
    },
    update: {
      hidden: true,
    },
    delete: {
      hidden: true,
    },
  }],
  ['priority', {
    name: '优先级',
  }],
  ['createInitType', {
    name: '创建测试用例',
    create: {
      render: (log: ILog) => (
        <span>
          <span className="c7n-Log-operation">创建</span>
          <span className="c7n-Log-value">{`【${log.newString}】`}</span>
        </span>
      ),
    },
    update: {
      dontJudge: true,
    },
    delete: {
      dontJudge: true,
    },
  }],
]);

export default fieldsMap;
