import React, { useContext } from 'react';
import moment from 'moment';
import { observer } from 'mobx-react-lite';
import Card from '../card';
import Analytics from './images/analytics.svg';
import BugDone from './images/bug_done.svg';
import Bug from './images/bug.svg';
import Clock from './images/clock.svg';
import User from './images/user.svg';
import Server from './images/server.svg';
import DetailItem from './DetailItem';
import context from '../../context';
import styles from './index.less';

export interface Props {
}

const DetailCard: React.FC<Props> = () => {
  const { store } = useContext(context);
  const { baseInfo } = store;
  const {
    manager,
    startDate,
    endDate,
    totalCaseCount,
    relatedIssueCount,
    passedCaseCount,
    totalBugCount,
    solvedBugCount,
  } = baseInfo;
  return (
    <div className={styles.detail_card}>
      <Card>
        <DetailItem img={User} title="测试负责人" content={manager?.realName || ''} />
      </Card>
      <Card>
        <DetailItem img={Clock} title="持续时间" content={`${startDate ? moment(startDate).format('YYYY-MM-DD') : '-'} ~ ${endDate ? moment(endDate).format('YYYY-MM-DD') : '-'}`} />
      </Card>
      <Card>
        <DetailItem img={Server} title="测试通过/总关联问题" content={`${passedCaseCount || 0}/${relatedIssueCount || 0}`} />
      </Card>
      <Card>
        <DetailItem img={Analytics} title="测试执行" content={totalCaseCount} />
      </Card>
      <Card>
        <DetailItem img={Bug} title="产生缺陷" content={totalBugCount} />
      </Card>
      <Card>
        <DetailItem img={BugDone} title="解决缺陷" content={solvedBugCount} />
      </Card>
    </div>
  );
};
export default observer(DetailCard);
