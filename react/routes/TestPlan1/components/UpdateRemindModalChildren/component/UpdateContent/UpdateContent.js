import React, { useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Icon } from 'choerodon-ui';
import SingleFileUpload from '../../../../../../components/SingleFileUpload';
import TestStepTable from '../TestStepTable';
import './UpdateContent.less';

function UpdateContent(props) {
  const { tag, updateData, dataSet } = props;
  
  return (
    <div className="c7ntest-testPlan-updateRemind-updateContent-detail">
      <div className={`c7ntest-testPlan-updateRemind-updateContent-detail-tag ${tag}-tag`}><span>{tag === 'new' ? '新' : '旧'}</span></div>
      <div className="c7ntest-testPlan-updateRemind-updateContent-detail-info">
        <div className="c7ntest-testPlan-updateRemind-updateContent-item">
          <div className="c7ntest-testPlan-updateRemind-updateContent-item-field">文件夹</div>
          <div className="c7ntest-testPlan-updateRemind-updateContent-item-value">Choerodon敏捷管理</div>
        </div>
        <div className="c7ntest-testPlan-updateRemind-updateContent-item">
          <div className="c7ntest-testPlan-updateRemind-updateContent-item-field">描述</div>
          <div className="c7ntest-testPlan-updateRemind-updateContent-item-value">
            1、针对项目群的team，新增菜单pi目标
            2、创建好的目标，合并显示到项目群的PI目标中
            3、在树上，当拖到某个根目录时，直接展开目录
          </div>
        </div>
        <div className="c7ntest-testPlan-updateRemind-updateContent-item">
          <div className="c7ntest-testPlan-updateRemind-updateContent-item-field">附件</div>
          <div className="c7ntest-testPlan-updateRemind-updateContent-item-value">
            {
              updateData.fileList && updateData.fileList.length && (
                updateData.fileList.map(item => (
                  <SingleFileUpload
                    key={item.uid}
                    url={item.url}
                    fileName={item.fileName}
                  />
                ))
              )
            }
          </div>
        </div>
        <div className="c7ntest-testPlan-updateRemind-updateContent-testStep">
          <div className="c7ntest-testPlan-updateRemind-updateContent-testStep-field">测试步骤</div>
          <div className="c7ntest-testPlan-updateRemind-updateContent-testStep-table">
            <TestStepTable dataSet={dataSet} />
          </div>
        </div>
      </div>
      
    </div>
  );
}

export default observer(UpdateContent);
