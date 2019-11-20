import React, { Fragment, useState, useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { stores } from '@choerodon/boot';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import {
  Select, Button, Tooltip, Icon,
} from 'choerodon-ui';
import { UploadButtonNow, FileList } from '../CommonComponent/UploadButtonNow';
import { IssueDescription } from '../CommonComponent';
import { TextEditToggle } from '@/components';
import {
  delta2Html, handleFileUpload, text2Delta,
} from '@/common/utils';
import Timeago from '@/components/DateTimeAgo/DateTimeAgo';
import { getLabels } from '@/api/agileApi';
import { FullEditor, WYSIWYGEditor } from '@/components';
import CreateLinkTask from '../CreateLinkTask';
import UserHead from '../UserHead';
import Divider from './Component/Divider';
import EditIssueContext from './stores';
import EditDetailWrap from './Component/EditDetailWrap';
import './EditDetail.less';
import LinkIssues from './link-issues';
// 问题链接
const { Text, Edit } = TextEditToggle;
const { AppState } = stores;
const { Option } = Select;

const { TitleWrap, ContentWrap, PropertyWrap } = EditDetailWrap;
/**
 * 问题详情
 * folderName
 * @param {*} linkIssues
 * @param {*} reloadIssue  重载问题  
 */
function EditDetail({
  onUpdate,
}) {
  const {
    store, caseId, prefixCls,
  } = useContext(EditIssueContext);
  const { issueInfo } = store;
  const {
    folderName, linkIssues, fileList, createUser, lastUpdateUser, 
  } = issueInfo;
  // 编辑全屏
  const [FullEditorShow, setFullEditorShow] = useState(false);
  const [editDescriptionShow, setEditDescriptionShow] = useState(false);
  const [createLinkTaskShow, setCreateLinkTaskShow] = useState(false);
  const [showMore, setShowMore] = useState(false);
  const [labelList, setLabelList] = useState([]);
  const [selectLoading, setSelectLoading] = useState(true);

  function transToArr(arr, pro, type = 'string') {
    if (typeof arr !== 'object') {
      return '';
    }
    if (!arr.length) {
      return type === 'string' ? '无' : [];
    } else if (typeof arr[0] === 'object') {
      return type === 'string' ? _.map(arr, pro).join() : _.map(arr, pro);
    } else {
      return type === 'string' ? arr.join() : arr;
    }
  }

  /**
     *标签更改
     *
     * @memberof EditIssueNarrow
     */
  const renderSelectLabel = () => {
    const { labelIssueRelVOList } = issueInfo;
    return (
      <TextEditToggle
        // disabled={disabled}
        style={{ width: '100%' }}
        formKey="labelIssueRelVOList"
        onSubmit={(value, done) => { onUpdate({ labelIssueRelVOList: value }, done); }}
        originData={transToArr(labelIssueRelVOList, 'labelName', 'array')}
      >
        <Text>
          {data => (
            data.length > 0 ? (
              <div style={{ display: 'flex', flexWrap: 'wrap' }}>
                {
                  transToArr(data, 'labelName', 'array').map(label => (
                    <div
                      key={label}
                      className="c7ntest-text-dot"
                      style={{
                        color: '#000',
                        borderRadius: '100px',
                        fontSize: '13px',
                        lineHeight: '24px',
                        padding: '2px 12px',
                        maxWidth: 100,
                        background: 'rgba(0, 0, 0, 0.08)',
                        marginRight: '8px',
                        marginBottom: 3,
                      }}
                    >
                      {label}
                    </div>
                  ))
                }
              </div>
            ) : '无'
          )}
        </Text>
        <Edit>
          <Select
            loading={selectLoading}
            mode="tags"
            autoFocus
            getPopupContainer={triggerNode => triggerNode.parentNode}
            tokenSeparators={[',']}
            style={{ width: '200px', marginTop: 0, paddingTop: 0 }}
            onFocus={() => {
              selectLoading(true);
              getLabels().then((res) => {
                setLabelList(res);
                setSelectLoading(false);
              });
            }}
          >
            {labelList.map(label => (
              <Option
                key={label.labelName}
                value={label.labelName}
              >
                {label.labelName}
              </Option>
            ))}
          </Select>
        </Edit>
      </TextEditToggle>
    );
  };

  const setFileList = (newFileList) => {
    store.setIssueInfo({
      ...issueInfo,
      issueAttachmentVOList: newFileList,
    });
  };

  const onUploadFiles = (arr) => {
    const { issueId } = issueInfo;
    if (arr.length > 0 && arr.some(one => !one.url)) {
      const config = {
        // issueType: this.state.typeCode,
        issueId,
        fileName: arr[0].name || 'AG_ATTACHMENT',
        projectId: AppState.currentMenuType.id,
      };
      handleFileUpload(arr, store.loadIssueData, config);
    }
  };

  function renderDescription() {
    const { description } = issueInfo;
    let delta;
    if (editDescriptionShow === undefined) {
      return null;
    }
    if (!description || editDescriptionShow) {
      delta = text2Delta(description);
      return (
        editDescriptionShow && (
          <div className="line-start mt-10">
            <WYSIWYGEditor
              autoFocus
              bottomBar
              defaultValue={delta}
              style={{ height: 200, width: '100%' }}
              handleDelete={() => {
                setEditDescriptionShow(false);
              }}

              handleSave={(value) => {
                onUpdate({ description: value });

                setEditDescriptionShow(false);
              }}
            />
          </div>
        )
      );
    } else {
      delta = delta2Html(description);
      return (
        <ContentWrap>
          <IssueDescription data={delta} />
        </ContentWrap>
      );
    }
  }
  const handleCreateLinkIssue = () => {
    // const { createLinkIssue } = props;
    // if (createLinkIssue) {
    //   createLinkIssue();
    //   setCreateLinkTaskShow(false);
    // } else {
    //   setCreateLinkTaskShow(false);
    // }
  };
  function render() {
    const {
      creationDate, lastUpdateDate, description, issueId,
    } = issueInfo;
    return (
      <React.Fragment>
        <section id="detail">
          <TitleWrap style={{ marginTop: 0 }} title={<FormattedMessage id="detail" />} />
          <ContentWrap style={{ display: 'flex', flexWrap: 'wrap' }}>
            {/* 文件夹名称 */}
            <PropertyWrap label={<FormattedMessage id="issue_create_content_folder" />}>
              <div style={{ marginLeft: 6 }}>
                {folderName || '无'}
              </div>
            </PropertyWrap>
            {/* 创建人 */}
            <PropertyWrap
              className="assignee"
              label={<FormattedMessage id="issue_edit_reporter" />}
              valueStyle={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap' }}
            >
              <UserHead user={createUser} />
            </PropertyWrap>
            {/* 创建时间 */}
            <PropertyWrap valueStyle={{ marginLeft: 6 }} label={<FormattedMessage id="issue_edit_createDate" />}>
              <Timeago date={creationDate} />
            </PropertyWrap>
            {showMore ? (
              <Fragment>
                {/* 标签 */}
                <PropertyWrap label={<FormattedMessage id="summary_label" />}>
                  {renderSelectLabel()}
                </PropertyWrap>
                {/** 更新人 */}
                <PropertyWrap label={<FormattedMessage id="issue_edit_manager" />} valueStyle={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap' }}>
                  <UserHead user={lastUpdateUser} />
                </PropertyWrap>
                <PropertyWrap valueStyle={{ marginLeft: 6 }} label={<FormattedMessage id="issue_edit_updateDate" />}>
                  <Timeago date={lastUpdateDate} />
                </PropertyWrap>
              </Fragment>
            ) : null}
          </ContentWrap>
          <Button className="leftBtn" funcType="flat" onClick={() => setShowMore(!showMore)}>
            <span>{showMore ? '收起' : '展开'}</span>
            <Icon type={showMore ? 'baseline-arrow_drop_up' : 'baseline-arrow_right'} style={{ marginRight: 2 }} />
          </Button>
        </section>
        <Divider />
        {/** 描述 */}
        <section id="des">
          <TitleWrap title={<FormattedMessage id="execute_description" />}>
            <div style={{ marginLeft: '14px', display: 'flex' }}>
              <Tooltip title="全屏编辑" getPopupContainer={triggerNode => triggerNode.parentNode}>
                <Button icon="zoom_out_map" onClick={() => setFullEditorShow(true)} />
              </Tooltip>
              <Tooltip title="编辑" getPopupContainer={triggerNode => triggerNode.parentNode.parentNode}>
                <Button
                  icon="mode_edit mlr-3"
                  onClick={() => {
                    setEditDescriptionShow(true);
                  }}
                />
              </Tooltip>
            </div>
          </TitleWrap>
          {renderDescription()}
        </section>
        <Divider />
        {/** 附件 */}
        <section id="attachment">
          <TitleWrap title={<FormattedMessage id="attachment" />}>
            <span>
              <UploadButtonNow onUpload={onUploadFiles} fileList={fileList} />
            </span>            
          </TitleWrap>
          <ContentWrap>
            <FileList
              onRemove={setFileList}              
              fileList={fileList}
            />
          </ContentWrap>
        </section>
        <Divider />
        {/** 问题链接 */}
        <section id="link_task">
          <TitleWrap title="问题链接">
            <div style={{ marginLeft: '14px' }}>
              <Tooltip title="问题链接" getPopupContainer={triggerNode => triggerNode.parentNode}>
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
          FullEditorShow && (
            <FullEditor
              initValue={description}
              visible={FullEditorShow}
              onCancel={() => setFullEditorShow(false)}
              onOk={(value) => {
                setFullEditorShow(false);
                onUpdate({ description: value });
              }}
            />
          )
        }
        {
          createLinkTaskShow ? (
            <CreateLinkTask
              issueId={issueId}
              visible={createLinkTaskShow}
              onCancel={() => setCreateLinkTaskShow(false)}
              onOk={handleCreateLinkIssue}
            />
          ) : null
        }
      </React.Fragment>
    );
  }
  return render();
}
export default observer(EditDetail);
