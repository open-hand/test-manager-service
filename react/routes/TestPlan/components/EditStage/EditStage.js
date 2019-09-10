import React, { Component } from 'react';
import {
  Form, Input, Modal, Spin, DatePicker,
} from 'choerodon-ui';
import { observer } from 'mobx-react';
import moment from 'moment';
import { FormattedMessage } from 'react-intl';
import { editFolder, checkCycleName } from '../../../../api/cycleApi';
import { SelectFocusLoad } from '../../../../components';
import TestPlanStore from '../../stores/TestPlanStore';

const FormItem = Form.Item;
const { Sidebar } = Modal;
const { RangePicker } = DatePicker;
@observer
class EditStage extends Component {
  state = {
    selectLoading: false,
    loading: false,
  }

  componentWillReceiveProps(nextProps) {
    const { resetFields } = this.props.form;
    if (nextProps.visible && !this.props.visible) {
      resetFields();
    }
  }

  onOk = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const { folderVersionName, folderName, folderId } = TestPlanStore.CurrentEditStage;
        const originFolder = `${folderVersionName}-${folderName}`;
        this.setState({ loading: true });
        const { range } = values;
        const [fromDate, toDate] = range;
        const initialValue = TestPlanStore.CurrentEditStage;
        editFolder({
          ...values,
          ...{
            folderId: [originFolder, folderId].includes(values.folderId) ? undefined : values.folderId,
            cycleId: initialValue.cycleId,
            objectVersionNumber: initialValue.objectVersionNumber,
            type: 'folder',
            fromDate: fromDate ? fromDate.startOf('day').format('YYYY-MM-DD HH:mm:ss') : null,
            toDate: toDate ? toDate.endOf('day').format('YYYY-MM-DD HH:mm:ss') : null,
          },
        }).then((res) => {
          if (res.failed) {
            Choerodon.prompt('同名循环已存在');
          } else {
            TestPlanStore.getTree();
            TestPlanStore.ExitEditStage();
          }
          this.setState({ loading: false });
        }).catch(() => {
          Choerodon.prompt('网络异常');
          this.setState({ loading: false });
        });
      }
    });
  }

  validateName = async (rule, name, callback) => {
    const initialValue = TestPlanStore.CurrentEditStage;
    const { title: cycleName, parentCycleId, versionId } = initialValue;
    if (cycleName === name) {
      callback();
      return;
    }
    const hasSame = await checkCycleName({
      type: 'folder',
      cycleName: name,
      versionId,
      parentCycleId,
    });
    if (hasSame) {
      callback('含有同名阶段');
    } else {
      callback();
    }
  }

  onCancel = () => {
    TestPlanStore.ExitEditStage();
  }


  render() {
    const { visible } = this.props;
    const {
      title, description, fromDate, toDate, folderVersionName, folderName, versionId,
    } = TestPlanStore.CurrentEditStage;
    const { getFieldDecorator } = this.props.form;
    const { loading, selectLoading } = this.state;
    return (
      <div>
        <Sidebar
          destroyOnClose
          title={<FormattedMessage id="testPlan_EditStage_title" />}
          visible={visible}
          onOk={this.onOk}
          onCancel={this.onCancel}
          width={380}
        >         
          <Spin spinning={loading}>
            <Form>
              <FormItem
                  // {...formItemLayout}
                label={null}
              >
                {getFieldDecorator('cycleName', {
                  initialValue: title,
                  rules: [{
                    required: true, message: '请输入名称!',
                  }, {
                    validator: this.validateName,
                  }],
                })(
                  <Input maxLength={30} label={<FormattedMessage id="name" />} />,
                )}
              </FormItem>
              <FormItem
                  // {...formItemLayout}
                label={null}
              >
                {getFieldDecorator('description', {
                  initialValue: description,
                  // rules: [{
                  //   required: true, message: '请输入说明!',
                  // }],
                })(
                  <Input maxLength={30} label={<FormattedMessage id="comment" />} />,
                )}
              </FormItem>
              <FormItem
                  // {...formItemLayout}
                label={null}
              >
                {getFieldDecorator('folderId', {
                  initialValue: `${folderVersionName}-${folderName}`,
                  rules: [{
                    required: true, message: '请选择文件夹!',
                  }],
                })(
                  <SelectFocusLoad
                    type="folder"
                    versionId={versionId}                  
                    label={<FormattedMessage id="testPlan_linkFolder" />}
                  />,
                )}
              </FormItem>
              <FormItem>
                <span className="c7n-input-wrapper c7n-input-has-value c7n-input-has-label">
                  <div className="c7n-input-label" style={{ transform: 'none' }}><span>持续时间</span></div>
                  {getFieldDecorator('range', {
                    rules: [{
                      type: 'array',
                      required: true,
                      message: '请选择日期!',
                    }],
                    initialValue: fromDate && toDate ? [moment(fromDate), moment(toDate)] : undefined,
                  })(
                    <RangePicker
                      format="YYYY-MM-DD"
                      style={{ width: '100%' }}
                    />,
                  )}
                </span>
              </FormItem>
            </Form>
          </Spin>         
        </Sidebar>
      </div>
    );
  }
}

EditStage.propTypes = {

};

export default Form.create()(EditStage);
