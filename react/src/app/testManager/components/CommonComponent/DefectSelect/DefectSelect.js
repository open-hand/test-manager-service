import React, { Component } from 'react';
import { Select } from 'choerodon-ui';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import { removeDefect, addDefects } from '../../../api/ExecuteDetailApi';
import { getIssuesForDefects } from '../../../api/agileApi';
import './DefectSelect.scss';
import ExecuteDetailStore from '../../../store/project/TestExecute/ExecuteDetailStore';

const { Option } = Select;
class DefectSelect extends Component {
  constructor(props) {
    super(props);
    const { defects } = this.props;
    this.state = {
      selectLoading: false,
      issueList: [],
      defects: defects || [],
      defectIds: defects ? defects.map(defect => defect.issueId.toString()) : [],
      originDefects: defects ? defects.map(defect => defect.issueId.toString()) : [],
    };
  }

  componentDidMount() {
    this.getIssuesForDefects();
  }

  getIssuesForDefects = () => {
    this.setState({
      selectLoading: true,
    });
    getIssuesForDefects().then((issueData) => {
      this.setState({
        issueList: issueData.list,
        selectLoading: false,
      });
    });
  }

  handleDefectsChange = (List) => {
    const {
      originDefects, defects, defectIds, issueList, 
    } = this.state;
    const oldList = [...defectIds];
    // window.console.log('old', oldList, 'new', List);
    // 删除元素
    if (oldList.length > List.length) {
      const deleteEle = oldList.filter(old => !List.includes(old));
      // 如果isse已存在，调用删除接口
      if (defectIds.includes(deleteEle[0])
        && _.find(defects, { issueId: Number(deleteEle[0]) })) {
        // window.console.log(defects, oldList, deleteEle, List);
        removeDefect(_.find(defects, { issueId: Number(deleteEle[0]) }).id);
      }
      // window.console.log('delete');
    } else {
      // window.console.log('add', List.filter(item => !oldList.includes(item)));
    }
    // 收集需要添加的缺陷
    const needAdd = issueList
      .filter(issue => List.includes(issue.issueId.toString()))// 取到选中的issueList
      .filter(issue => !originDefects.includes(issue.issueId.toString()))// 去掉之前已有的
      .map(item => ({
        defectType: 'CASE_STEP',
        defectLinkId: this.props.executeStepId,
        issueId: item.issueId,
        defectName: item.issueNum,
      }));
    this.props.setNeedAdd(needAdd);
    this.setState({
      defectIds: List,
    });
  }

  handleHiddenCresteBug = () => {
    ExecuteDetailStore.setCreateBugShow(false);
  }

  render() {  
    const { executeStepId, ...otherProps } = this.props;        
    const { handleSubmit } = this.props.bugsToggleRef;
    const {
      defects, selectLoading, defectIds, issueList, originDefects,  
    } = this.state;
    const defectsOptions = issueList.map(issue => (
      <Option key={issue.issueId} value={issue.issueId.toString()}>
        {issue.issueNum} 
        {' '}
        {issue.summary}
      </Option>
    ));
    return (
      <div>
        <Select  
          // defaultOpen        
          dropdownStyle={{        
            width: 300,  
          }}
          getPopupContainer={this.props.getPopupContainer}
          autoFocus
          filter
          mode="multiple"
          dropdownMatchSelectWidth={false}
          filterOption={false}
          loading={selectLoading}
          defaultValue={defects.map(defect => defect.issueId.toString())}
          footer={(
            <div 
              style={{ color: '#3f51b5', cursor: 'pointer' }}
              role="none"
              onClick={() => {
                handleSubmit();
                ExecuteDetailStore.setCreateBugShow(true);
                ExecuteDetailStore.setDefectType('CASE_STEP');
                ExecuteDetailStore.setCreateDectTypeId(executeStepId);
              }}
            >
              <FormattedMessage id="issue_create_bug" />
            </div>
          )}
          style={{ width: '100%' }}
          onChange={this.handleDefectsChange}
          onFilterChange={(value) => {
            this.setState({
              selectLoading: true,
            });
            getIssuesForDefects(value).then((issueData) => {
              this.setState({
                issueList: issueData.list,
                selectLoading: false,
              });
            });
          }}
          {...otherProps}
        >
          {defectsOptions}
        </Select>
      </div>
    );
  }
}

DefectSelect.propTypes = {

};

export default DefectSelect;
