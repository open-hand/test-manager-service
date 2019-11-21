/*eslint-disable */
import React, { Component } from 'react';
import { stores, axios, Content } from '@choerodon/boot';
import { withRouter } from 'react-router-dom';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import { Select, Form, Modal } from 'choerodon-ui';

import './CreateLinkTask.scss';
import { createLink, loadIssuesInLink } from '../../../../api/IssueManageApi';
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
      show: [],

      selected: [],
    };
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
        const { selected } = this.state;
        const { issueId } = this.props;
        const data = {
          caseId: issueId,
          issueIds: selected, 
        };
        this.setState({ createLoading: true });
        createLink(data)
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
        title="问题链接"
        visible={visible || false}
        onOk={this.handleCreateIssue}
        onCancel={onCancel}
        okText={<FormattedMessage id="create" />}
        cancelText={<FormattedMessage id="cancel" />}
        confirmLoading={this.state.createLoading}
        width={380}
      >
        <Content
          style={{
            padding: '0 0 10px 0',
          }}
        >
          <Form layout="vertical">
            <FormItem>
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
                              data={issue.issueTypeVO}
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
