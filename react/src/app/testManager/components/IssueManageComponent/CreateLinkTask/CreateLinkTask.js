/*eslint-disable */
import React, { Component } from 'react';
import { stores, axios, Content } from '@choerodon/boot';
import { withRouter } from 'react-router-dom';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import { Select, Form, Modal } from 'choerodon-ui';

import './CreateLinkTask.scss';
import { createLink, loadIssuesInLink } from '../../../api/IssueManageApi';
import TypeTag from '../TypeTag';

const { AppState } = stores;
const { Sidebar } = Modal;
const { Option } = Select;
const FormItem = Form.Item;
let sign = false;

class CreateLinkTask extends Component {
  constructor(props) {
    super(props);
    this.state = {
      createLoading: false,
      selectLoading: true,

      originIssues: [],
      originLinks: [],
      show: [],

      selected: [],
    };
  }

  componentDidMount() {
    this.getLinks();
  }

  getLinks() {
    this.setState({
      selectLoading: true,
    });
    axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/issue_link_types/query_all`, {
      contents: [],
      linkName: '',
    })
      .then((res) => {
        this.setState({
          selectLoading: false,
          originLinks: res.list,
        });
        this.transform(res.list);
      });
  }

  transform(links) {
    // split active and passive
    const active = links.map(link => ({
      name: link.outWard,
      linkTypeId: link.linkTypeId,
    }));
    const passive = [];
    links.forEach((link) => {
      if (link.inWard !== link.outWard) {
        passive.push({
          name: link.inWard,
          linkTypeId: link.linkTypeId,
        });
      }
    });
    this.setState({
      show: active.concat(passive),
    });
  }

  handleSelect(value, option) {
    const selected = _.map(option.slice(), v => v.key);
    this.setState({ selected });
  }

  onFilterChange(input) {
    if (!sign) {
      this.setState({
        selectLoading: true,
      });
      loadIssuesInLink(0, 20, this.props.issueId, input).then((res) => {
        this.setState({
          originIssues: res.list,
          selectLoading: false,
        });
      });
      sign = true;
    } else {
      this.debounceFilterIssues(input);
    }
  }

  debounceFilterIssues = _.debounce((input) => {
    this.setState({
      selectLoading: true,
    });
    loadIssuesInLink(0, 20, this.props.issueId, input).then((res) => {
      this.setState({
        originIssues: res.list,
        selectLoading: false,
      });
    });
  }, 500);

  handleCreateIssue = () => {
    this.props.form.validateFields((err, values) => {
      if (!err) {
        const labelIssueRelDTOList = _.map(this.state.selected, (issue) => {
          const currentLinkType = this.state.originLinks[0];
          // if (currentLinkType.outWard === linkTypeId.split('+')[1]) {
          return ({
            linkTypeId: currentLinkType.linkTypeId,
            linkedIssueId: issue * 1,
            issueId: this.props.issueId,
          });
          // } else {
          //   return ({
          //     linkTypeId: linkTypeId.split('+')[0] * 1,
          //     issueId: issue * 1,
          //     linkedIssueId: this.props.issueId,
          //   });
          // }
        });
        this.setState({ createLoading: true });
        createLink(this.props.issueId, labelIssueRelDTOList)
          .then((res) => {
            this.setState({ createLoading: false });
            this.props.onOk();
          });
      }
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      initValue, visible, onCancel, onOk,
    } = this.props;

    return (
      <Sidebar
        className="c7ntest-newLink"
        title="关联问题"
        visible={visible || false}
        onOk={this.handleCreateIssue}
        onCancel={onCancel}
        okText={<FormattedMessage id="create" />}
        cancelText={<FormattedMessage id="cancel" />}
        confirmLoading={this.state.createLoading}
      >
        <Content
          style={{
            padding: '0 0 10px 0',
          }}
          title="对用例进行问题关联"
          description="请在下面选中要关联的问题"
        >
          <Form layout="vertical">
            {/* <FormItem style={{ width: 520 }}>
              {getFieldDecorator('linkTypeId', {
                rules: [{
                  required: true, message: '请选择类型!',
                }],
              })(
                <Select
                  label={<FormattedMessage id="issue_create_link_content_create_relation" />}
                  // labelInValue
                  loading={this.state.selectLoading}
                >
                  {this.state.show.map(link => (
                    <Option key={`${link.linkTypeId}+${link.name}`} value={`${link.linkTypeId}+${link.name}`}>
                      {link.name}
                    </Option>
                  ))}
                </Select>,
              )}
            </FormItem> */}

            <FormItem style={{ width: 520 }}>
              {getFieldDecorator('issues', {
                rules: [{
                  required: true, message: '请选择问题!',
                }],
              })(
                <Select
                  label={<FormattedMessage id="issue_create_link_content_create_question" />}
                  mode="multiple"
                  loading={this.state.selectLoading}
                  optionLabelProp="value"
                  filter
                  filterOption={false}
                  onFilterChange={this.onFilterChange.bind(this)}
                  onChange={this.handleSelect.bind(this)}
                  dropdownClassName="c7ntest-issueSelectDropDown"
                >
                  {this.state.originIssues.map(issue => (
                    <Option
                      key={issue.issueId}
                      value={issue.issueNum}
                    >
                      <div style={{ display: 'inline-block', width: '100%' }}>
                        <div className="c7ntest-link-select-item">
                          <div>
                            <TypeTag
                              type={issue.issueTypeDTO}
                            />
                          </div>
                          <div style={{
                            paddingLeft: 12, paddingRight: 12, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
                          }}
                          >
                            {issue.issueNum}
                          </div>
                          <div style={{ overflow: 'hidden', flex: 1 }}>
                            <p style={{
                              paddingRight: '25px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0, maxWidth: 'unset',
                            }}
                            >
                              {issue.summary}
                            </p>
                          </div>
                        </div>
                      </div>
                    </Option>
                  ))}
                </Select>,
              )}
            </FormItem>
          </Form>
        </Content>
      </Sidebar>
    );
  }
}
export default Form.create({})(withRouter(CreateLinkTask));
