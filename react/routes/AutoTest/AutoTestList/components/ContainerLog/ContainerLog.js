import React, { Component } from 'react';
import { Content } from '@choerodon/boot';
import { Button, Select, Modal } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import CodeMirror from 'react-codemirror';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/base16-dark.css';
import { getLog } from '../../../../../api/AutoTestApi';

import './ContainerLog.less';

const uuidv1 = require('uuid');

const { Sidebar } = Modal;
const { Option } = Select;
const options = {
  readOnly: true,
  lineNumbers: true,
  lineWrapping: true,
  autofocus: true,
  theme: 'base16-dark',
};
function removeEndsChar(str, char) {
  if (typeof str !== 'string') return '';

  return str.endsWith(char) ? str.slice(0, -1) : str;
}
class ContainerLog extends Component {
  state = {
    visible: false,
    logType: 'local',
    ws: null,
  }

  open = (data) => {
    const { testAppInstanceVO } = data;
    if (testAppInstanceVO) {
      const {
        envId, podName, podStatus, containerName, logId,
      } = testAppInstanceVO;
      this.setState({
        envId,
        logType: podStatus === 1 ? 'socket' : 'local',
        podName,
        containerName,
        logId,
        visible: true,
      }, () => {
        setTimeout(() => {
          this.loadLog();
        }, 1000);
      });
    }
  }

  /**
   * 关闭日志
   */
  close = () => {
    this.setState({
      visible: false,
    });  
    const editor = this.editorLog.getCodeMirror();
    const { ws } = this.state;
    clearInterval(this.timer);
    this.timer = null;
    if (ws) {
      ws.close();
    }
    editor.setValue('');
  };


  loadLog = (followingOK) => {
    const {
      envId, podName, containerName, following, logType, 
    } = this.state;
    const logId = this.state.logId || (Math.random() * 1000);
    // const logId = Math.random();
    const authToken = document.cookie.split('=')[1];
    const logs = [];
    let oldLogs = [];
    let editor = null;
    // console.log('load', this, this.editorLog);
    if (this.editorLog) {
      editor = this.editorLog.getCodeMirror();
      // console.log(logType);
      if (logType === 'local') {
        getLog(logId).then((res) => {
          // console.log(res);
          editor.setValue(res);
        });
      } else {
        try { 
          // eslint-disable-next-line no-underscore-dangle
          const wsUrl = removeEndsChar(window._env_.DEVOPS_HOST, '/');
          // eslint-disable-next-line no-underscore-dangle
          const secretKey = window._env_.DEVOPS_WEBSOCKET_SECRET_KEY;
          const key = `cluster:${envId}.log:${uuidv1()}`;
          const url = `${wsUrl}/websocket?key=${key}&group=from_front:${key}&processor=front_log&secret_key=${secretKey}&env=${'choerodon-test'}&podName=${podName}&containerName=${containerName}&logId=${logId}&clusterId=${envId}`;
          const ws = new WebSocket(url);
          // console.log(ws);
          this.setState({ ws, following: true });
          if (!followingOK) {
            editor.setValue('Loading...');
          }
          ws.onopen = () => {
            editor.setValue('Loading...');
          };
          ws.onerror = (e) => {
            if (this.timer) {
              clearInterval(this.timer);
              this.timer = null;
            }
            logs.push('连接出错，请重新打开');
            editor.setValue(_.join(logs, ''));
            editor.execCommand('goDocEnd');
          };
          ws.onclose = (e) => {
            if (this.timer) {
              clearInterval(this.timer);
              this.timer = null;
            }
            if (following) {
              logs.push('连接已断开');
              editor.setValue(_.join(logs, ''));
            }
            editor.execCommand('goDocEnd');
          };
          ws.onmessage = (e) => {
            if (e.data.size) {
              const reader = new FileReader();
              reader.readAsText(e.data, 'utf-8');
              reader.onload = () => {
                if (reader.result !== '') {
                  logs.push(reader.result);
                }
              };
            }
            if (!logs.length) {
              const logString = _.join(logs, '');
              editor.setValue(logString);
            }
          };

          this.timer = setInterval(() => {
            if (logs.length > 0) {
              if (!_.isEqual(logs, oldLogs)) {
                const logString = _.join(logs, '');
                editor.setValue(logString);
                editor.execCommand('goDocEnd');
                // 如果没有返回数据，则不进行重新赋值给编辑器
                oldLogs = _.cloneDeep(logs);
              }
            } else if (!followingOK) {
              editor.setValue('Loading...');
            }
          });
        } catch (e) {
          // eslint-disable-next-line no-console
          console.log(e);
          editor.setValue('连接失败');
        }
      }
    }
  };

  /**
   *  全屏查看日志
   */
  setFullscreen = () => {
    const cm = this.editorLog.getCodeMirror();
    const wrap = cm.getWrapperElement();
    cm.state.fullScreenRestore = {
      scrollTop: window.pageYOffset,
      scrollLeft: window.pageXOffset,
      width: wrap.style.width,
      height: wrap.style.height,
    };
    wrap.style.width = '';
    wrap.style.height = 'auto';
    wrap.className += ' c7ntest-CodeMirror-fullscreen';
    this.setState({ fullscreen: true });
    document.documentElement.style.overflow = 'hidden';
    cm.refresh();
    window.addEventListener('keydown', (e) => {
      this.setNormal(e.which);
    });
  };

  /**
   * 任意键退出全屏查看
   */
  setNormal = () => {
    const cm = this.editorLog.getCodeMirror();
    const wrap = cm.getWrapperElement();
    wrap.className = wrap.className.replace(/\s*c7ntest-CodeMirror-fullscreen\b/, '');
    this.setState({ fullscreen: false });
    document.documentElement.style.overflow = '';
    const info = cm.state.fullScreenRestore;
    wrap.style.width = info.width; wrap.style.height = info.height;
    window.scrollTo(info.scrollLeft, info.scrollTop);
    cm.refresh();
    window.removeEventListener('keydown', (e) => {
      this.setNormal(e.which);
    });
  };

  /**
   * 日志go top
   */
  goTop = () => {
    const editor = this.editorLog.getCodeMirror();
    editor.execCommand('goDocStart');
  };

  /**
   * top log following
   */
  stopFollowing = () => {
    const { ws } = this.state;
    if (ws) {
      ws.close();
    }
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
    this.setState({
      following: false,
    });
  };

  saveRef = name => (ref) => {
    this[name] = ref;
  }

  render() {
    const {
      fullscreen, following, visible, podName, containerName, logId, logType,
    } = this.state;

    return (
      <Sidebar
        visible={visible}
        title={<FormattedMessage id="container.log.header.title" />}
        onOk={this.close}
        className="c7ntest-podLog-content c7ntest-region"
        okText={<FormattedMessage id="close" />}
        okCancel={false}       
      >
        <Content className="sidebar-content" code="container.log" values={{ name: podName }}>
          <section className="c7ntest-podLog-section">
            <div className="c7ntest-podLog-hei-wrap">
              <div className="c7ntest-podShell-title">
                <FormattedMessage id="container.term.log" />
                <Select value={containerName} style={{ marginLeft: 5 }}>
                  <Option key={logId} value={`${logId}+${containerName}`}>{containerName}</Option>
                </Select>
                <Button type="primary" funcType="flat" shape="circle" icon="fullscreen" onClick={this.setFullscreen} />
              </div>
              {logType === 'socket' && (
                <span>
                  {following ? <div className={`c7ntest-podLog-action log-following ${fullscreen ? 'f-top' : ''}`} onClick={this.stopFollowing} role="none">Stop Following</div>
                    : <div className={`c7ntest-podLog-action log-following ${fullscreen ? 'f-top' : ''}`} onClick={this.loadLog.bind(this, true)} role="none">Start Following</div>}
                </span>
              )}
              <CodeMirror
                ref={this.saveRef('editorLog')}
                value="Loading..."
                className="c7ntest-podLog-editor"
                options={options}
              />
              <div className={`c7ntest-podLog-action log-goTop ${fullscreen ? 'g-top' : ''}`} onClick={this.goTop} role="none">Go Top</div>
            </div>
          </section>
        </Content>
      </Sidebar>
    );
  }
}

ContainerLog.propTypes = {

};

export default ContainerLog;
