import React, {
  useState, useEffect, useReducer, useCallback, useRef,
} from 'react';
import { Select, Button } from 'choerodon-ui';
import { axios } from '@choerodon/boot';
import _ from 'lodash';
import { observer } from 'mobx-react-lite';
import { FormattedMessage } from 'react-intl';
import { removeDefect } from '@/api/ExecuteDetailApi';
import { getIssuesForDefects } from '@/api/agileApi';
import './DefectSelect.less';
import openCreateBugModal from '@/components/create-bug';
import { delta2Html, getProjectId, getOrganizationId } from '../../../../../../../common/utils';

const { Option } = Select;
function DefectSelect(props) {
  const {
    defects, executeId, currentPageIndex, onCreateBug,
  } = props;
  const selectRef = useRef();
  const [defectIds, setDefectIds] = useState(defects ? defects.map((defect) => defect.issueId.toString()) : []);
  const [originDefects, setOriginDefects] = useState(defects ? defects.map((defect) => defect.issueId.toString()) : []);
  const [issue, dispatch] = useReducer((state, action) => {
    const { issueData, searchValue } = action;
    switch (action.type) {
      case 'loading':
        return {
          ...state,
          selectLoading: true,
        };
      case 'loaded':
        return {
          ...state,
          canLoadMore: (issueData.total / issueData.size) > issueData.pageNum,
          issueList: [...state.issueList, ...issueData.list],
          selectLoading: false,
          page: issueData.pageNum,
        };
      case 'filterLoaded':
        return {
          canLoadMore: (issueData.total / issueData.size) > issueData.pageNum,
          issueList: [...issueData.list],
          selectLoading: false,
          searchValue,
          page: issueData.pageNum,
        };
      default:
        throw new Error();
    }
  }, {
    canLoadMore: false,
    issueList: [],
    searchValue: '',
    selectLoading: false,
    page: 1,
  });
  const {
    searchValue, page, canLoadMore, selectLoading,
  } = issue;
  const initIssuesForDefects = useCallback(() => {
    dispatch({ type: 'loading' });
    getIssuesForDefects().then((issueData) => {
      dispatch({ type: 'loaded', issueData });
    });
  }, []);

  useEffect(() => {
    initIssuesForDefects();
  }, [initIssuesForDefects]);

  const handleDefectsChange = (List) => {
    const { executeStepId, setNeedAdd } = props;
    const { issueList } = issue;
    const oldList = [...defectIds];
    // window.console.log('old', oldList, 'new', List);
    // 删除元素
    if (oldList.length > List.length) {
      const deleteEle = oldList.filter((old) => !List.includes(old));
      // 如果issue已存在，调用删除接口
      if (defectIds.includes(deleteEle[0])
        && _.find(defects, { issueId: deleteEle[0] })) {
        // window.console.log(defects, oldList, deleteEle, List);
        removeDefect(_.find(defects, { issueId: deleteEle[0] }).id);
      }
      // window.console.log('delete');
    }
    // 收集需要添加的缺陷
    // console.log('List=', List, defects, issueList);
    const needAdd = issueList
      .filter((item) => List.includes(item.issueId.toString()))// 取到选中的issueList
      .filter((item) => !originDefects.includes(item.issueId.toString()))// 去掉之前已有的
      .map((item) => ({
        defectLinkId: executeStepId,
        issueId: item.issueId,
        issueInfosVO: {
          defectType: 'CASE_STEP',
          defectLinkId: executeStepId,
          issueId: item.issueId,
          issueName: item.issueNum,
          summary: item.summary,
          defectName: item.issueNum,
        },
      }));
    setNeedAdd(needAdd);
    setDefectIds(List);
  };

  const loadMore = () => {
    dispatch({ type: 'loading' });
    getIssuesForDefects(searchValue, { page: page + 1 }).then((issueData) => {
      dispatch({ type: 'loaded', issueData });
    });
  };

  // eslint-disable-next-line consistent-return
  const renderLoadMore = (
    <Option key="SelectFocusLoad-loadMore" className="SelectFocusLoad-loadMore" disabled style={{ display: canLoadMore ? 'block' : 'none' }}>
      <Button type="primary" style={{ textAlign: 'left', width: '100%', background: 'transparent' }} onClick={loadMore}>更多</Button>
    </Option>
  );
  const loadFilterData = (value) => {
    dispatch({ type: 'loading' });
    getIssuesForDefects(value).then((issueData) => {
      dispatch({ type: 'filterLoaded', issueData, searchValue: value });
    });
  };

  const DebounceLoadFilterData = _.debounce(loadFilterData, 400);
  const line = (str) => `<p>${str}</p>`;
  function render() {
    const {
      executeStepId, handleSubmit, ExecuteDetailStore, record, dataSet, ...otherProps
    } = props;
    const { issueList } = issue;
    const defectsOptions = issueList.map((item) => (
      <Option key={item.issueId} value={item.issueId.toString()}>
        {item.issueNum}
        {' '}
        {item.summary}
      </Option>
    ));
    return (
      <Select
        dropdownStyle={{
          width: 300,
        }}
        autoFocus
        filter
        mode="multiple"
        ref={selectRef}
        dropdownMatchSelectWidth={false}
        filterOption={false}
        showArrow={false}
        loading={selectLoading}
        dropdownAlign={{
          overflow: {
            adjustX: 1,
            adjustY: 1,
          },
        }}
        defaultValue={defects.map((defect) => defect.issueId.toString())}
        footer={(
          <Button
            className="primary"
            style={{ margin: '-.1rem 0' }}
            // role="none"
            onClick={() => {
              handleSubmit(record);
              const {
                caseNum, summary, description,
              } = ExecuteDetailStore.getDetailData;
              let defaultDescription = '';
              defaultDescription += line('测试用例：') + line(`${caseNum}-${summary}`);
              defaultDescription += line('前置条件：') + delta2Html(description);
              axios.get(`/test/v1/projects/${getProjectId()}/cycle/case/step/query/${executeId}?organizationId=${getOrganizationId()}&page=0&size=0`).then((res) => {
                const sliceIndex = (dataSet.currentPage - 1) * dataSet.pageSize + currentPageIndex + 1;
                (res.list || []).slice(0, sliceIndex).forEach((step, i, arr) => {
                  const { testStep, testData, expectedResult } = step;
                  defaultDescription += line(`测试步骤：${testStep}`);
                  defaultDescription += line(`测试数据：${testData || '无'}`);
                  defaultDescription += line(`预期结果：${expectedResult}`);
                });
                // defaultDescription.splice(3, 0, ...newDescription, String(newDescription[newDescription.length - 1].insert));
              });
              openCreateBugModal({
                onCreateBug,
                stepId: executeStepId,
                defaultValues: {
                  description: defaultDescription,
                },
              });
              if (selectRef.current) {
                selectRef.current.rcSelect.setOpenState(false, false);
              }
            }}
          >
            <FormattedMessage id="issue_create_bug" />
          </Button>
        )}
        style={{ width: '100%' }}
        onChange={handleDefectsChange}
        onFilterChange={(value) => {
          DebounceLoadFilterData(value);
        }}
        // eslint-disable-next-line react/jsx-props-no-spreading
        {...otherProps}
      >
        {defectsOptions}
        {renderLoadMore}
      </Select>
    );
  }
  return render();
}

DefectSelect.propTypes = {

};

export default observer(DefectSelect);
