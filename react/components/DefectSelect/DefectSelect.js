import React, { Component } from 'react';
import { Select, Button } from 'choerodon-ui';
import _ from 'lodash';
import { observer } from 'mobx-react';
import { FormattedMessage } from 'react-intl';
import { removeDefect } from '../../api/ExecuteDetailApi';
import { getIssuesForDefects } from '../../api/agileApi';
import './DefectSelect.less';
// import ExecuteDetailStore from '../../store/ExecuteDetailStore';
import ExecuteDetailStore from '../../routes/TestExecute/TestExecuteStore/ExecuteDetailStore';


const { Option } = Select;
@observer
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
      canLoadMore: false,
      page: 1,
    };
  }

  componentDidMount() {
    this.getIssuesForDefects();
  }

  getIssuesForDefects = () => {
    this.setState({
      selectLoading: true,
    });
    getIssuesForDefects('', { page: 1 }).then((issueData) => {
      this.setState({
        canLoadMore: issueData.hasNextPage,
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
    const { executeStepId, setNeedAdd } = this.props;
    const needAdd = issueList
      .filter(issue => List.includes(issue.issueId.toString()))// 取到选中的issueList
      .filter(issue => !originDefects.includes(issue.issueId.toString()))// 去掉之前已有的
      .map(item => ({
        defectType: 'CASE_STEP',
        defectLinkId: executeStepId,
        issueId: item.issueId,
        defectName: item.issueNum,
      }));
    setNeedAdd(needAdd);
    this.setState({
      defectIds: List,
    });
  }

  handleHiddenCresteBug = () => {
    ExecuteDetailStore.setCreateBugShow(false);
  }

  loadMore = () => {
    this.setState({
      selectLoading: true,
    });
    const { page } = this.state;
    getIssuesForDefects('', { page: page + 1 }).then((issueData) => {
      this.setState({
        canLoadMore: issueData.hasNextPage,
        issueList: issueData.list,
        page: issueData.page,
        selectLoading: false,
      });
    });
  }

  renderLoadMore = () => {
    const { canLoadMore } = this.state;
    if (canLoadMore) {
      return (
        <Option key="SelectFocusLoad-loadMore" className="SelectFocusLoad-loadMore" disabled>
          <Button type="primary" style={{ textAlign: 'left', width: '100%', background: 'transparent' }} onClick={this.loadMore}>更多</Button>
        </Option>
      );
    }
    return null;
  }

  render() {
    const { executeStepId, bugsToggleRef, ...otherProps } = this.props;
    const { handleSubmit } = bugsToggleRef;

    const {
      defects, selectLoading, issueList, canLoadMore,
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
          dropdownStyle={{
            width: 300,
          }}
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
          {/* {canLoadMore && (
            <Option key="SelectFocusLoad-loadMore" className="SelectFocusLoad-loadMore" disabled>
              <Button type="primary" style={{ textAlign: 'left', width: '100%', background: 'transparent' }} onClick={this.loadMore}>更多</Button>
            </Option>
          )} */}
          {this.renderLoadMore.bind()}
        </Select>
      </div>
    );
  }
}

DefectSelect.propTypes = {

};

export default DefectSelect;
