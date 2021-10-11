import React, {
  useState, useContext, useEffect,
} from 'react';
import { observer } from 'mobx-react-lite';
import { FormattedMessage } from 'react-intl';
import {
  Button, Tooltip, Icon,
} from 'choerodon-ui/pro';
import {
  getProjectId,
} from '@/common/utils';
import useHasAgile from '@/hooks/useHasAgile';
import UserHead from '@/components/UserHead';
import ChunkUploader from '@/components/chunk-uploader';
import openLinkIssueModal from '../CreateLinkTask';
import { FileList } from '../UploadButtonNow/UploadButtonNow';
import Divider from './Component/Divider';
import EditIssueContext from './stores';
import EditDetailWrap from './Component/EditDetailWrap';
import './Detail.less';
import LinkIssues from './link-issues';
import FieldPriority from './Component/FieldPriority';

// eslint-disable-next-line no-underscore-dangle
const { API_HOST } = window._env_;

const { TitleWrap, ContentWrap, PropertyWrap } = EditDetailWrap;
/**
 * 工作项详情
 * folder
 * @param {*} linkIssues
 * @param {*} reloadIssue  重载工作项
 */
function Detail({
  onUpdate, handleCreateLinkIssue,
}) {
  const {
    store, caseId, prefixCls,
  } = useContext(EditIssueContext);
  const hasAgile = useHasAgile();
  const { issueInfo, linkIssues } = store;
  const {
    folder, attachment, createUser, priorityVO,
    lastUpdateUser, creationDate, lastUpdateDate, description, caseId: issueId,
  } = issueInfo;

  const [showMore, setShowMore] = useState(false);

  const [fileList, setFileList] = useState([]);

  useEffect(() => {
    const initialFileList = (attachment || []).map((item) => ({
      uid: item.attachmentId || item.uid,
      name: item.fileName || item.name,
      url: item.url,
      userId: item.createdBy || item.userId,
    }));
    setFileList(initialFileList);
  }, [attachment]);

  const setIssueInfo = (newFileList) => {
    store.setIssueInfo({
      ...issueInfo,
      attachment: newFileList,
    });
  };

  const handleCreateLinkIssueOk = () => {
    if (handleCreateLinkIssue) {
      handleCreateLinkIssue();
    }
  };

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
                display: 'flex', alignItems: 'center', flexWrap: 'wrap', marginLeft: 6,
              }}
            >
              <UserHead user={createUser} color="var(--text-color)" />
            </PropertyWrap>
            {/* 创建时间 */}
            <PropertyWrap valueStyle={{ marginLeft: 6 }} label={<FormattedMessage id="issue_edit_createDate" />}>
              {creationDate}
            </PropertyWrap>
            {/* 优先级 */}
            <PropertyWrap valueStyle={{ marginLeft: 1 }} label={<FormattedMessage id="issue_edit_priority" />}>
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
                  <UserHead user={lastUpdateUser} color="var(--text-color)" />
                </PropertyWrap>
                <PropertyWrap valueStyle={{ marginLeft: 6 }} label={<FormattedMessage id="issue_edit_updateDate" />}>
                  {lastUpdateDate}
                </PropertyWrap>
              </>
            ) : null}
          </ContentWrap>
          <Button onClick={() => setShowMore(!showMore)}>
            <span>{showMore ? '收起' : '展开'}</span>
            <Icon type={showMore ? 'baseline-arrow_drop_up' : 'baseline-arrow_right'} style={{ marginRight: 2 }} />
          </Button>
        </section>
        <div className="c7nTest-detail-divider" />
        {/** 附件 */}
        <section id="attachment">
          <TitleWrap title={<FormattedMessage id="attachment" />}>
            <span>
              <ChunkUploader
                prefixPatch="/hfle"
                showUploadList={false}
                fileList={fileList}
                setFileList={setFileList}
                combine={{
                  url: `${API_HOST}/test/v1/projects/${getProjectId()}/attachment/combine`,
                  requestData: {
                    caseId: issueId,
                  },
                }}
              />
            </span>
          </TitleWrap>
          <ContentWrap>
            <span>
              <FileList
                setFileList={setFileList}
                fileList={fileList}
                store={store}
                issueId={issueId}
                setIssueInfo={setIssueInfo}
              />
            </span>

          </ContentWrap>
        </section>
        <div className="c7nTest-detail-divider" />
        {/** 工作项链接 */}
        {hasAgile && (
          <section id="link_task" style={{ marginBottom: 20 }}>
            <TitleWrap title="工作项链接">
              <div style={{ marginLeft: '14px' }}>
                <Tooltip title="工作项链接">
                  <Button
                    icon="playlist_add"
                    onClick={() => {
                      openLinkIssueModal({
                        issueId,
                        onSubmit: handleCreateLinkIssueOk,
                      });
                    }}
                  />
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
        )}

      </>
    );
  }
  return render();
}
export default observer(Detail);
