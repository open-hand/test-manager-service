import React, {
  Fragment, useState, useContext,
} from 'react';
import { observer } from 'mobx-react-lite';
import { Choerodon } from '@choerodon/boot';
import { FormattedMessage } from 'react-intl';
import {
  Button, Tooltip, Icon,
} from 'choerodon-ui';
import {
  delta2Html, text2Delta,
} from '@/common/utils';
import Timeago from '@/components/DateTimeAgo/DateTimeAgo';
import { uploadFile } from '@/api/IssueManageApi';
import { openFullEditor, WYSIWYGEditor } from '@/components';
import UserHead from '@/components/UserHead';
import CreateLinkTask from '../CreateLinkTask';
import IssueDescription from './Component/IssueDescription';
import { UploadButtonNow, FileList } from '../UploadButtonNow/UploadButtonNow';
import Divider from './Component/Divider';
import EditIssueContext from './stores';
import EditDetailWrap from './Component/EditDetailWrap';
import './Detail.less';
import LinkIssues from './link-issues';
import PriorityTag from '../../../../components/PriorityTag';
import FieldPriority from './Component/FieldPriority';

const { TitleWrap, ContentWrap, PropertyWrap } = EditDetailWrap;
/**
 * 问题详情
 * folder
 * @param {*} linkIssues
 * @param {*} reloadIssue  重载问题  
 */
function Detail({
  onUpdate, handleCreateLinkIssue,
}) {
  const {
    store, caseId, prefixCls,
  } = useContext(EditIssueContext);
  const { issueInfo, linkIssues } = store;
  const {
    folder, attachment, createUser, priorityVO,
    lastUpdateUser, creationDate, lastUpdateDate, description, caseId: issueId,
  } = issueInfo;

  const [editDescriptionShow, setEditDescriptionShow] = useState(false);
  const [createLinkTaskShow, setCreateLinkTaskShow] = useState(false);
  const [showMore, setShowMore] = useState(false);

  const setFileList = (newFileList) => {
    store.setIssueInfo({
      ...issueInfo,
      issueAttachmentVOList: newFileList,
    });
  };

  /**
 * 适用于富文本附件上传以及回调
 * @param {any []} propFileList 文件列表
 * @param {function} func 回调
 */
  const handleFileUpload = (propFileList, func) => {
    const fileList = propFileList.filter((i) => !i.url);
    const formData = new FormData();
    fileList.forEach((file) => {
      // file.name = encodeURI(encodeURI(file.name));
      formData.append('file', file);
    });
    uploadFile(issueId, formData)
      .then(() => {
        Choerodon.prompt('上传成功');
        func(issueId);
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
  };

  const onUploadFiles = (arr) => {
    if (arr.length > 0 && arr.some((one) => !one.url)) {
      handleFileUpload(arr, store.loadIssueData);
    }
  };

  const handleCreateLinkIssueOk = () => {
    if (handleCreateLinkIssue) {
      handleCreateLinkIssue();
      setCreateLinkTaskShow(false);
    } else {
      setCreateLinkTaskShow(false);
    }
  };
  function handleOpenFullEditor() {
    openFullEditor({
      initValue: description,
      onOk: async (value) => { await onUpdate({ description: value }); },
    });
  }

  function render() {
    return (
      <>
        <section id="detail">
          <TitleWrap style={{ marginTop: 0 }} title={<FormattedMessage id="detail" />} />
          <ContentWrap style={{ display: 'flex', flexWrap: 'wrap' }}>
            {/* 目录名称 */}
            <PropertyWrap label={<FormattedMessage id="issue_create_content_folder" />}>
              <div style={{ marginLeft: 6 }}>
                {folder || '无'}
              </div>
            </PropertyWrap>
            {/* 创建人 */}
            <PropertyWrap
              className="assignee"
              label={<FormattedMessage id="issue_edit_creator" />}
              valueStyle={{
                display: 'flex', alignItems: 'center', flexWrap: 'wrap', marginLeft: 5,
              }}
            >
              <UserHead user={createUser} color="#000000" />
            </PropertyWrap>
            {/* 创建时间 */}
            <PropertyWrap valueStyle={{ marginLeft: 6 }} label={<FormattedMessage id="issue_edit_createDate" />}>
              <Timeago date={creationDate} />
            </PropertyWrap>
            {/* 优先级 */}
            <PropertyWrap valueStyle={{ marginLeft: 6 }} label={<FormattedMessage id="issue_edit_priority" />}>
              <FieldPriority priority={priorityVO || {}} onUpdate={onUpdate} />
            </PropertyWrap>
            {showMore ? (
              <>
                <PropertyWrap
                  label={<FormattedMessage id="issue_edit_updater" />}
                  valueStyle={{
                    display: 'flex', alignItems: 'center', flexWrap: 'wrap', marginLeft: 6,
                  }}
                >
                  <UserHead user={lastUpdateUser} color="#000000" />
                </PropertyWrap>
                <PropertyWrap valueStyle={{ marginLeft: 6 }} label={<FormattedMessage id="issue_edit_updateDate" />}>
                  <Timeago date={lastUpdateDate} />
                </PropertyWrap>
              </>
            ) : null}
          </ContentWrap>
          <Button className="leftBtn" funcType="flat" onClick={() => setShowMore(!showMore)}>
            <span>{showMore ? '收起更多' : '查看更多'}</span>
            <Icon type={showMore ? 'baseline-arrow_drop_up' : 'baseline-arrow_right'} style={{ marginRight: 2 }} />
          </Button>
        </section>
        <Divider />

        {/** 附件 */}
        <section id="attachment">
          <TitleWrap title={<FormattedMessage id="attachment" />}>
            <span>
              <UploadButtonNow onUpload={onUploadFiles} fileList={attachment || []} />
            </span>
          </TitleWrap>
          <ContentWrap>
            <FileList
              onRemove={setFileList}
              fileList={attachment}
              store={store}
              issueId={issueId}
            />
          </ContentWrap>
        </section>
        <Divider />
        {/** 问题链接 */}
        <section id="link_task" style={{ marginBottom: 20 }}>
          <TitleWrap title="问题链接">
            <div style={{ marginLeft: '14px' }}>
              <Tooltip title="问题链接" getPopupContainer={(triggerNode) => triggerNode.parentNode}>
                <Button icon="playlist_add" onClick={() => setCreateLinkTaskShow(true)} />
              </Tooltip>
            </div>
          </TitleWrap>
          <div className="c7ntest-tasks">
            <LinkIssues
              issueId={issueId}
              linkIssues={linkIssues}
              reloadIssue={store.loadIssueData}
            />
          </div>
        </section>
        {
          createLinkTaskShow ? (
            <CreateLinkTask
              issueId={issueId}
              visible={createLinkTaskShow}
              onCancel={() => setCreateLinkTaskShow(false)}
              onOk={handleCreateLinkIssueOk}
            />
          ) : null
        }
      </>
    );
  }
  return render();
}
export default observer(Detail);
