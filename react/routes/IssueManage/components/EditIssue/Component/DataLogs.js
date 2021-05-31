import React, { Component } from 'react';
import { Icon, Button } from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
import Logs from '@choerodon/agile/lib/components/Logs';
import fieldsMap from './DataLogFieldsMap';

class DataLogs extends Component {
  constructor(props, context) {
    super(props, context);
    this.state = {
      user: {},
      expand: false,
    };
  }

  setUser(user) {
    this.setState({
      user,
    });
  }

  render() {
    const { datalogs } = this.props;
    return (
      <div>
        <Logs datalogs={datalogs} expand={this.state.expand} fieldsMap={fieldsMap} />
        {
          datalogs.length > 5 && !this.state.expand ? (
            <div style={{ marginTop: 10 }}>
              <Button onClick={() => this.setState({ expand: true })}>
                <Icon type="baseline-arrow_drop_down icon" style={{ marginRight: 2 }} />
                <span><FormattedMessage id="expand" /></span>
              </Button>
            </div>
          ) : null
        }
        {
          datalogs.length > 5 && this.state.expand ? (
            <div style={{ marginTop: 10 }}>
              <Button onClick={() => this.setState({ expand: false })}>
                <Icon type="baseline-arrow_drop_up icon" style={{ marginRight: 2 }} />
                <span><FormattedMessage id="fold" /></span>
              </Button>
            </div>
          ) : null
        }
      </div>
    );
  }
}

export default DataLogs;
