/*
 * @Author: LainCarl
 * @Date: 2019-01-25 14:53:51
 * @Last Modified by: LainCarl
 * @Last Modified time: 2019-01-25 15:04:00
 * @Feature: 测试摘要
 */

import React, { Component } from 'react';
import { FormattedMessage } from 'react-intl';
import moment from 'moment';
import _ from 'lodash';
import {
  getCaseNotPlain, getCaseNotRun, getCaseNum, getCycleRange, getCreateRange, getIssueStatistic,
} from '../../../api/summaryApi';
import {
  getProjectVersion, getLabels, getModules, getIssueCount,
} from '../../../api/agileApi';
import SummaryHome from './SummaryHome';

class SummaryHomeContainer extends Component {
  state = {
    loading: false,
    range: '7',
    excuteList: [],
    createList: [],
    totalTest: 0,
    notPlan: 0,
    notRun: 0,
    caseNum: 0,
    totalExcute: 0,
    totalCreate: 0,
    versionTable: [],
    componentTable: [],
    labelTable: [],
  }

  componentDidMount() {
    this.getInfo();
  }

  getInfo = () => {
    this.setState({ loading: true });
    const { date, range } = this.state;
    Promise.all([getIssueCount(), getCaseNotPlain(), getCaseNotRun(), getCaseNum(),
      getCycleRange(moment().format('YYYY-MM-DD'), range),
      getCreateRange(range), getProjectVersion(), getModules(), getLabels()])
      .then(([totalData, notPlan, notRun, caseNum, excuteList,
        createList, versionList, componentList, labelList]) => {
        this.setState({

          // totalIssue: totalIssueData.totalElements,
          totalTest: totalData.total,
          notPlan,
          notRun,
          caseNum,
          excuteList: this.listTransform(excuteList),
          totalExcute: _.sum(excuteList),
          createList: this.createTransform(createList, range),
          totalCreate: _.sumBy(createList, 'issueCount'),
        });
        Promise.all([
          this.getVersionTable(versionList),
          this.getLabelTable(labelList),
          this.getComponentTable(componentList),
        ]).then(([versionTable, labelTable, componentTable]) => {
          this.setState({
            loading: false,
            versionTable,
            labelTable,
            componentTable,
          });
        });
      }).catch(() => {
        this.setState({ loading: false });
        Choerodon.prompt('网络异常');
      });
  }

  getVersionTable = versionList => new Promise((resolve) => {
    getIssueStatistic('version').then((data) => {
      const versionTable = versionList.reverse().map((version) => {
        let num = 0;
        if (_.find(data, { typeName: version.versionId.toString() })) {
          num = _.find(data, { typeName: version.versionId.toString() }).value;
        }
        return { name: version.name, versionId: version.versionId, num };
      });
      // const noVersionData = _.find(data, { typeName: null }) || {};
      // const noVersion = {
      //   num: noVersionData.value || 0,
      //   id: null,
      //   name: <FormattedMessage id="summary_noVersion" />,
      // };
      // versionTable.unshift(noVersion);
      resolve(versionTable);
    });
  })

  getLabelTable = labelList => new Promise((resolve) => {
    getIssueStatistic('label').then((data) => {
      const labelTable = labelList.map((label) => {
        let num = 0;
        if (_.find(data, { typeName: label.labelId.toString() })) {
          num = _.find(data, { typeName: label.labelId.toString() }).value;
        }
        return { name: label.labelName, id: label.labelId, num };
      });
      // 加入无标签项
      const noLabelData = _.find(data, { typeName: null }) || {};
      const noLabel = {
        num: noLabelData.value || 0,
        id: null,
        name: <FormattedMessage id="summary_noLabel" />,
      };
      labelTable.unshift(noLabel);
      resolve(labelTable);
    });
  })

  getComponentTable = componentList => new Promise((resolve) => {
    getIssueStatistic('component').then((data) => {
      const componentTable = componentList.map((component) => {
        let num = 0;
        if (_.find(data, { typeName: component.componentId.toString() })) {
          num = _.find(data, { typeName: component.componentId.toString() }).value;
        }
        return { name: component.name, id: component.componentId, num };
      });
      const noComponentData = _.find(data, { typeName: null }) || {};
      const noComponent = {
        num: noComponentData.value || 0,
        id: null,
        name: <FormattedMessage id="summary_noComponent" />,
      };
      componentTable.unshift(noComponent);
      resolve(componentTable);
    });
  })

  handleRangeChange = (e) => {
    this.setState({ loading: true });
    Promise.all([
      getCycleRange(moment().format('YYYY-MM-DD'), e.target.value),
      getCreateRange(e.target.value)]).then(([excuteList, createList]) => {
      this.setState({
        loading: false,
        range: e.target.value,
        excuteList: this.listTransform(excuteList),
        totalExcute: _.sum(excuteList),
        createList: this.createTransform(createList, e.target.value),
        totalCreate: _.sumBy(createList, 'issueCount'),
      });
    });
  }

  createTransform = (source, range) => Array(Number(range)).fill(0).map((item, i) => {
    const time = moment().subtract(range - i - 1, 'days').format('YYYY-MM-DD');
    if (_.find(source, { creationDay: time })) {
      const { creationDay, issueCount } = _.find(source, { creationDay: time });
      return {
        time: moment(creationDay).format('MM/DD'),
        value: issueCount,
      };
    } else {
      return {
        time: moment().subtract(range - i - 1, 'days').format('MM/DD'),
        value: 0,
      };
    }
  });

  listTransform = list => list.map((item, i) => ({
    time: moment().subtract(list.length - i - 1, 'days').format('MM/DD'),
    value: item,
  }))

  handleRefreshClick=() => {
    this.getInfo();
  }

  render() {
    return (<SummaryHome {...this.state} onRefreshClick={this.handleRefreshClick} onRangeChange={this.handleRangeChange} />);
  }
}


export default SummaryHomeContainer;
