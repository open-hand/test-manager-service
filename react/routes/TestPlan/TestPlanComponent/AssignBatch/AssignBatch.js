import React, { PureComponent } from 'react';
import { Modal } from 'choerodon-ui';
import { SelectFocusLoad } from '../../../../components/CommonComponent';
import { assignBatch } from '../../../../api/cycleApi';

class AssignBatch extends PureComponent {
  state = {
    assign: 0,
    loading: false,
  }

  handleOk = () => {
    const { assign } = this.state;
    const { currentEditValue } = this.props;
    const { cycleId } = currentEditValue;
    if (assign) {
      this.setState({
        loading: true,
      });
      // console.log(currentEditValue);
      assignBatch(assign, cycleId).then((res) => {
        this.props.onOk(cycleId);
      }).catch((err) => {
        Choerodon.prompt('网络出错');
      }).finally(() => {
        this.setState({
          loading: false,
        });
      });
    } else {
      Choerodon.prompt('请选择指派人');
    }
  }

  render() {
    return (
      <Modal
        title="批量指派"
        visible={this.props.visible}
        onOk={this.handleOk}
        onCancel={this.props.onCancel}
        confirmLoading={this.state.loading}
      >
        <SelectFocusLoad
          style={{ width: '100%' }}
          label="指派人"
          type="user"
          onChange={(value) => {
            this.setState({
              assign: value,
            });
          }}
        />
      </Modal>
    );
  }
}


export default AssignBatch;
