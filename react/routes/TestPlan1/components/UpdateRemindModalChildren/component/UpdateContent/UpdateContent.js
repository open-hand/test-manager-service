import React from 'react';
import { observer } from 'mobx-react-lite';
import SingleFileUpload from '../../../../../../components/SingleFileUpload';

function UpdateContent(props) {
  const { tag, updateData } = props;
  return (
    <div className="c7ntest-testPlan-updateRemind-updateContent">
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
    </div>
  );
}

export default observer(UpdateContent);
