import React, { Component } from 'react';
import {
  Form, Input, Select, Modal, Spin, DatePicker,
} from 'choerodon-ui';
import { observer } from 'mobx-react';
import moment from 'moment';
import { Content } from 'choerodon-front-boot';
import { FormattedMessage } from 'react-intl';
import { editFolder } from '../../../api/cycleApi';
import { SelectFolder, SelectFocusLoad } from '../../CommonComponent';
import TestPlanStore from '../../../store/project/TestPlan/TestPlanStore';

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
        >
          <Content
            style={{
              padding: '0 0 10px 0',
            }}
            title={<FormattedMessage id="testPlan_EditStage" values={{ cycleName: title }} />}
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
                    }],
                  })(
                    <Input style={{ width: 500 }} maxLength={30} label={<FormattedMessage id="name" />} />,
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
                    <Input style={{ width: 500 }} maxLength={30} label={<FormattedMessage id="comment" />} />,
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
                      style={{ width: 500, margin: '0 0 10px 0' }}
                      label={<FormattedMessage id="testPlan_linkFolder" />}
                    />,
                  )}
                </FormItem>
                <FormItem>
                  <span className="ant-input-wrapper ant-input-has-value ant-input-has-label">
                    <div className="ant-input-label"><span>持续时间</span></div> 
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
                        style={{ width: 500 }}
                      />,
                    )}
                  </span>
                </FormItem>
              </Form>
            </Spin>
          </Content>
        </Sidebar>
      </div>
    );
  }
}

EditStage.propTypes = {

};

export default Form.create()(EditStage);
