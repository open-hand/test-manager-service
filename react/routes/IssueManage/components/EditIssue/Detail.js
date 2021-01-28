import React, {
  useState, useContext, useEffect,
} from 'react';
import { observer } from 'mobx-react-lite';
import { FormattedMessage } from 'react-intl';
import {
  Button, Tooltip, Icon,
} from 'choerodon-ui';
import {
  getProjectId,
} from '@/common/utils';
import Timeago from '@/components/DateTimeAgo/DateTimeAgo';
import useHasAgile from '@/hooks/useHasAgile';
import UserHead from '@/components/UserHead';
import ChunkUploader from '@/components/chunk-uploader';
import CreateLinkTask from '../CreateLinkTask';
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
  const hasAgile = useHasAgile();
  const { issueInfo, linkIssues } = store;
  const {
    folder, attachment, createUser, priorityVO,
    lastUpdateUser, creationDate, lastUpdateDate, description, caseId: issueId,
  } = issueInfo;

  const [createLinkTaskShow, setCreateLinkTaskShow] = useState(false);
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
      setCreateLinkTaskShow(false);
    } else {
      setCreateLinkTaskShow(false);
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
        <Divider />
        {/** 问题链接 */}
        {hasAgile && (
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
        )}
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
