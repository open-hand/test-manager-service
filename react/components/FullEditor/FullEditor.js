import React, { Component } from 'react';
import { Choerodon } from '@choerodon/boot';
import { withRouter } from 'react-router-dom';
import { Form, Modal } from 'choerodon-ui';
import WYSIWYGEditor from '../WYSIWYGEditor';

class FullEditor extends Component {
  constructor(props) {
    super(props);
    this.state = {
      delta: this.formatValue(props.initValue),
    };
  }

  formatValue = (value) => {
    let delta = value;
    try {
      JSON.parse(value);
      delta = JSON.parse(value);
    } catch (error) {
      delta = value;
    }
    return delta;
  }

  componentWillReceiveProps(nextProps) {   
    this.setState({
      delta: this.formatValue(nextProps.initValue),
    });  
  }


  handleOk = () => {
    this.props.onOk(this.state.delta);
  }

  render() {
    const {
      visible, onCancel, onOk, loading, 
    } = this.props;

    return (
      <Modal
        title={Choerodon.getMessage('编辑描述', 'Edit description')}
        visible={visible || false}
        maskClosable={false}
        width={1200}
        onCancel={this.props.onCancel}
        onOk={this.handleOk}
        confirmLoading={loading}
      >
        <WYSIWYGEditor
          autoFocus
          hideFullScreen
          value={this.state.delta}
          style={{ height: 500, width: '100%', marginTop: 20 }}
          onChange={(value) => {
            this.setState({ delta: value });
          }}
        />
      </Modal>
    );
  }
}
export default Form.create({})(withRouter(FullEditor));
