import React, { Fragment, useState, useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Choerodon } from '@choerodon/boot';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import {
  Select, Button, Tooltip, Icon,
} from 'choerodon-ui';
import { UploadButtonNow, FileList } from '../UploadButtonNow/UploadButtonNow';
import IssueDescription from './Component/IssueDescription';
import { TextEditToggle } from '@/components';
import {
  delta2Html, text2Delta,
} from '@/common/utils';
import Timeago from '@/components/DateTimeAgo/DateTimeAgo';
import { getLabels } from '@/api/agileApi';
import { uploadFile } from '@/api/IssueManageApi';
import { openFullEditor, WYSIWYGEditor } from '@/components';
import CreateLinkTask from '../CreateLinkTask';
import UserHead from '../UserHead';
import Divider from './Component/Divider';
import EditIssueContext from './stores';
import EditDetailWrap from './Component/EditDetailWrap';
import './Detail.less';
import LinkIssues from './link-issues';
// 问题链接
const { Text, Edit } = TextEditToggle;
const { Option } = Select;

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
    folder, attachment, createUser,
    lastUpdateUser, creationDate, lastUpdateDate, description, caseId: issueId,
  } = issueInfo;

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

  /**
 * 适用于富文本附件上传以及回调
 * @param {any []} propFileList 文件列表
 * @param {function} func 回调
 */
  const handleFileUpload = (propFileList, func) => {
    const fileList = propFileList.filter(i => !i.url);
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
    if (arr.length > 0 && arr.some(one => !one.url)) {
      handleFileUpload(arr, store.loadIssueData);
    }
  };

  function renderDescription() {
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
      <React.Fragment>
        <section id="detail">
          <TitleWrap style={{ marginTop: 0 }} title={<FormattedMessage id="detail" />} />
          <ContentWrap style={{ display: 'flex', flexWrap: 'wrap' }}>
            {/* 文件夹名称 */}
            <PropertyWrap label={<FormattedMessage id="issue_create_content_folder" />}>
              <div style={{ marginLeft: 6 }}>
                {folder || '无'}
              </div>
            </PropertyWrap>
            {/* 创建人 */}
            <PropertyWrap
              className="assignee"
              label={<FormattedMessage id="issue_edit_creator" />}
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
                <PropertyWrap label={<FormattedMessage id="issue_edit_updater" />} valueStyle={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap' }}>
                  <UserHead user={lastUpdateUser} />
                </PropertyWrap>
                <PropertyWrap valueStyle={{ marginLeft: 6 }} label={<FormattedMessage id="issue_edit_updateDate" />}>
                  <Timeago date={lastUpdateDate} />
                </PropertyWrap>
              </Fragment>
            ) : null}
          </ContentWrap>
          <Button className="leftBtn" funcType="flat" onClick={() => setShowMore(!showMore)}>
            <span>{showMore ? '收起更多' : '查看更多'}</span>
            <Icon type={showMore ? 'baseline-arrow_drop_up' : 'baseline-arrow_right'} style={{ marginRight: 2 }} />
          </Button>
        </section>
        <Divider />
        {/** 描述 */}
        <section id="des">
          <TitleWrap title={<FormattedMessage id="execute_description" />}>
            <div style={{ marginLeft: '14px', display: 'flex' }}>
              <Tooltip title="全屏编辑" getPopupContainer={triggerNode => triggerNode.parentNode}>
                <Button icon="zoom_out_map" onClick={handleOpenFullEditor} />
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
          createLinkTaskShow ? (
            <CreateLinkTask
              issueId={issueId}
              visible={createLinkTaskShow}
              onCancel={() => setCreateLinkTaskShow(false)}
              onOk={handleCreateLinkIssueOk}
            />
          ) : null
        }
      </React.Fragment>
    );
  }
  return render();
}
export default observer(Detail);
