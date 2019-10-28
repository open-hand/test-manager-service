import React from 'react';
import { Link } from 'react-router-dom';
import { Page, Header, Content } from '@choerodon/boot';
import { FormattedMessage } from 'react-intl';
import { getProjectName, commonLink } from '../../../common/utils';
import ReporterSwitcher from '../components';
import Pic from './pic.svg';
import Pic2 from './pic2.svg';
import Pic3 from './pic3.svg';

const styles = {
  itemContainer: {
    marginRight: 24,
    width: 280, 
    height: 296, 
    background: '#FAFAFA', 
    display: 'flex', 
    flexDirection: 'column', 
    alignItems: 'center',
    padding: 18,
    fontSize: '13px',
  },
  imgContainer: {                
    width: 220,
    height: 154,
    textAlign: 'center',
    lineHeight: '154px',   
    boxShadow: '0 1px 0 0 rgba(0,0,0,0.16), 0 0 0 1px rgba(0,0,0,0.12), 0 2px 1px -1px rgba(0,0,0,0.12)',
    borderRadius: 2, 
    background: 'white',
  },
  itemTextBold: { 
    color: 'black',
    width: '100%', 
    margin: '18px 0', 
    fontWeight: 500, 
  },
};
const ReportHome = () => (
  <Page className="c7ntest-report-home">
    <Header title={<FormattedMessage id="report_title" />}>
      <ReporterSwitcher isHome />
    </Header>
    <Content>
      <div style={{ display: 'flex' }}>
        <Link to={commonLink('/report/story')}>
          <div style={styles.itemContainer}>
            <div style={styles.imgContainer}>
              <img src={Pic} alt="" />
            </div>
            <div style={styles.itemTextBold}><FormattedMessage id="report_demandToDefect" /></div>
            <div style={{ color: 'rgba(0,0,0,0.65)' }}><FormattedMessage id="report_demandToDefect_description" /></div>
          </div>
        </Link>
        <Link to={commonLink('/report/test')}>
          <div style={styles.itemContainer}>
            <div style={styles.imgContainer}>
              <img src={Pic2} alt="" />
            </div>
            <div style={styles.itemTextBold}><FormattedMessage id="report_defectToDemand" /></div>
            <div style={{ color: 'rgba(0,0,0,0.65)' }}><FormattedMessage id="report_defectToDemand_description" /></div>
          </div>
        </Link>
        <Link to={commonLink('/report/progress')}>
          <div style={styles.itemContainer}>
            <div style={styles.imgContainer}>
              <img src={Pic3} alt="" />
            </div>
            <div style={styles.itemTextBold}><FormattedMessage id="report_defectToProgress" /></div>
            <div style={{ color: 'rgba(0,0,0,0.65)' }}><FormattedMessage id="report_defectToProgress_description" /></div>
          </div>
        </Link>
      </div>
    </Content>
  </Page>
);

export default ReportHome;
