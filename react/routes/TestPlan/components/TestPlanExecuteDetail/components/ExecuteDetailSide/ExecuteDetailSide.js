import React, {
  useRef, useState, useEffect, useCallback,
} from 'react';
import { Link } from 'react-router-dom';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react-lite';
import { throttle, find } from 'lodash';
import {
  Button, Tooltip,
} from 'choerodon-ui';
import { stores } from '@choerodon/boot';

import { issueLink } from '../../../../../../common/utils';
import {
  StatusTags, User, CKEditorViewer,
  ResizeAble,
} from '../../../../../../components';
import './ExecuteDetailSide.less';
import UploadButtonExecuteDetail from './UploadButtonExecuteDetail';
import LinkIssues from './link-issues';
import { getIssueInfos } from '../../../../../../api/ExecuteDetailApi';
import PriorityTag from '../../../../../../components/PriorityTag';

const { HeaderStore } = stores;

const navs = [
  { code: 'detail', tooltip: '详情', icon: 'error_outline' },
  { code: 'des', tooltip: '描述', icon: 'subject' },
  { code: 'attachment', tooltip: '附件', icon: 'attach_file' },
  { code: 'bug', tooltip: '缺陷', icon: 'bug_report' },
];
let sign = true;
const Section = ({
  id,
  title,
  action,
  children,
  style,
  isLastOne,
}) => (
  <section id={id}>
    <div className="c7ntest-side-item-header">
      <div className="c7ntest-side-item-header-left">
        {/* <Icon type={icon} /> */}
        <span>{title}</span>
      </div>
      <div className="c7ntest-side-item-header-right">
        {action}
      </div>
    </div>
    <div className="c7ntest-side-item-content" style={style}>
      {children}
    </div>
    {
      !isLastOne && (
        <div className="c7ntest-side-item-header-line" />
      )
    }
  </section>
);
const defaultProps = {
  issueInfosVO: { issueTypeVO: {} },
};
const propTypes = {
  issueInfosVO: PropTypes.shape({}),
  cycleData: PropTypes.shape({}).isRequired,
  fileList: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  onFileRemove: PropTypes.func.isRequired,
  status: PropTypes.shape({}).isRequired,
  onClose: PropTypes.func.isRequired,
  onUpload: PropTypes.func.isRequired,
  onCommentSave: PropTypes.func.isRequired,
  onRemoveDefect: PropTypes.func.isRequired,
  onCreateBugShow: PropTypes.func.isRequired,
};

function ExecuteDetailSide(props) {
  const container = useRef();
  const { detailData } = props;
  const [currentNav, setCurrentNav] = useState('detail');
  const [issueInfosVO, setIssueInfosVO] = useState([]);
  function isInLook(ele) {
    const a = ele.offsetTop;
    const target = document.getElementById('scroll-area');
    return a + ele.offsetHeight > target.scrollTop;
  }

  const getCurrentNav = useCallback(() => find(navs.map((nav) => nav.code), (i) => isInLook(document.getElementById(i))), []);

  const handleScroll = useCallback((e) => {
    if (sign) {
      const newCurrentNav = getCurrentNav(e);
      if (currentNav !== newCurrentNav && newCurrentNav) {
        setCurrentNav(newCurrentNav);
      }
    }
  }, [currentNav, getCurrentNav]);

  const setQuery = (width = container.current.clientWidth) => {
    if (width <= 600) {
      container.current.setAttribute('max-width', '600px');
    } else {
      container.current.removeAttribute('max-width');
    }
  };

  // componentDidMount() {
  //   document.getElementById('scroll-area').addEventListener('scroll', handleScroll);
  //   setQuery();
  // }
  useEffect(() => {
    if (document.getElementById('scroll-area')) {
      document.getElementById('scroll-area').removeEventListener('scroll', handleScroll);
    }
    document.getElementById('scroll-area').addEventListener('scroll', handleScroll);
    setQuery();
  }, [handleScroll]);

  useEffect(() => {
    const { caseId } = detailData;
    if (caseId) {
      getIssueInfos(caseId).then((res) => {
        setIssueInfosVO(res);
      });
    }
  }, [detailData]);

  const scrollToAnchor = (anchorName) => {
    if (anchorName) {
      const anchorElement = document.getElementById(anchorName);
      if (anchorElement) {
        sign = false;
        anchorElement.scrollIntoView({
          behavior: 'smooth',
          block: 'start',
          // inline: "nearest",
        });
        setTimeout(() => {
          sign = true;
        }, 2000);
      }
    }
  };

  const handleResizeEnd = ({ width }) => {
    localStorage.setItem('agile.ExecuteDetail.width', `${width}px`);
  };

  const handleResize = throttle(({ width }) => {
    setQuery(width);
    // console.log(width, parseInt(width / 100) * 100);
  }, 150);

  function render() {
    const {
      fileList, status, onClose,
    } = props;
    const { statusColor, statusName } = status;
    const {
      executor, description, executorDate, summary, caseId, caseFolderId, caseNum, caseHasExist, priorityVO, customNum,
    } = detailData;

    return (
      <div style={{
        position: 'fixed',
        right: 0,
        top: HeaderStore.announcementClosed ? 50 : 100,
        bottom: 0,
        zIndex: 101,
        overflowY: 'hidden',
        overflowX: 'visible',
      }}
      >

        <ResizeAble
          modes={['left']}
          size={{
            maxWidth: window.innerWidth * 0.6,
            minWidth: 440,
          }}
          defaultSize={{
            width: localStorage.getItem('agile.ExecuteDetail.width') || 600,
            height: '100%',
          }}
          onResizeEnd={handleResizeEnd}
          onResize={handleResize}
        >

          <div className="c7ntest-ExecuteDetailSide" ref={container}>
            <div className="c7ntest-ExecuteDetailSide-divider" />
            <div className="c7ntest-content">
              <div className="c7ntest-content-top">
                <div className="c7ntest-between-center">
                  <div style={{ fontSize: '16px', fontWeight: 500 }}>
                    <div style={{
                      height: 44, display: 'flex', alignItems: 'center', justifyContent: 'center',
                    }}
                    >
                      <span>相关用例：</span>
                      {caseHasExist ? <Link className="primary c7ntest-text-dot" style={{ marginLeft: 5 }} to={issueLink(caseId, 'issue_test', caseNum, caseFolderId)}>{caseNum}</Link>
                        : '用例已被删除'}
                    </div>
                  </div>
                  <Button funcType="flat" icon="last_page" onClick={onClose}>
                    <span style={{ fontSize: 13 }}>隐藏详情</span>
                  </Button>
                </div>
                <div style={{ fontSize: '20px', marginRight: '5px', marginBottom: '15px' }}>
                  {summary}
                </div>
                <div style={{ marginRight: '5px', marginBottom: '15px' }}>
                  <span>自定义编号：</span>
                  {customNum || '无'}
                </div>
              </div>
              <div className="c7ntest-content-bottom" id="scroll-area" style={{ position: 'relative' }}>
                {/* 详情 */}
                <Section
                  id="detail"
                  icon="error_outline"
                  title="详情"
                >
                  {/* 状态 */}
                  <div className="c7ntest-item-one-line">
                    <div className="c7ntest-item-one-line-left">状态：</div>
                    <div className="c7ntest-item-one-line-right">
                      {statusColor && (
                        <StatusTags
                          style={{
                            height: 20,
                            fontSize: '12px',
                            lineHeight: '20px',
                            marginRight: 15,
                            display: 'block',
                            width: 'max-content',
                          }}
                          color={statusColor}
                          name={statusName}
                        />
                      )}
                    </div>
                  </div>
                  {/* 优先级 */}
                  <div className="c7ntest-item-one-line">
                    <div className="c7ntest-item-one-line-left">优先级：</div>
                    <div className="c7ntest-item-one-line-right">
                      {priorityVO && (
                        <PriorityTag
                          priority={priorityVO}

                        />
                      )}
                    </div>
                  </div>
                  {/* 执行人 */}
                  <div className="c7ntest-item-one-line">
                    <div className="c7ntest-item-one-line-left">执行人：</div>
                    <div className="c7ntest-item-one-line-right">
                      <User user={executor} />
                    </div>
                  </div>

                  {/* 执行日期 */}
                  <div className="c7ntest-item-one-line">
                    <div className="c7ntest-item-one-line-left">执行日期：</div>
                    <div className="c7ntest-item-one-line-right">
                      {executorDate}
                    </div>
                  </div>
                </Section>
                {/* 描述 */}
                <Section
                  id="des"
                  icon="edit-o"
                  title="前置条件"
                  style={{ padding: '0 15px 0 0' }}
                >
                  <CKEditorViewer value={description} />
                </Section>
                {/* 附件 */}
                <Section
                  id="attachment"
                  icon="backup-o"
                  title="附件"
                >
                  <UploadButtonExecuteDetail
                    fileList={fileList}
                  />
                </Section>
                {/* 工作项链接 */}
                <Section
                  id="issueLink"
                  icon="attach_file"
                  title="工作项链接"
                  isLastOne
                >
                  <LinkIssues
                    linkIssues={issueInfosVO}
                  />

                </Section>

              </div>
            </div>
          </div>
        </ResizeAble>
      </div>

    );
  }
  return render();
}

ExecuteDetailSide.propTypes = propTypes;
ExecuteDetailSide.defaultProps = defaultProps;
export default observer(ExecuteDetailSide);
