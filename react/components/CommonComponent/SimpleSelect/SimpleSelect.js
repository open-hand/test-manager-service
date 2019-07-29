import React, { Component } from 'react';
import { Select } from 'choerodon-ui';

const { Option } = Select;

class SimpleSelect extends Component {
  state = {
    loading: false,
    List: [],
  }

  componentDidMount() {
    const { request } = this.props;
    request().then((Data) => {
      this.setState({
        List: Data,
        loading: false,
      });
    });
  }

  render() {
    const { request, option } = this.props;
    const { value, text } = option;
    const { loading, List } = this.state;
    const Options = List.map(item => (
      <Option value={item[value]} key={item[value]}>
        {item[text]}
      </Option>
    ));
    return (
      <Select
        label="文件夹"
        loading={loading}
        style={{ width: 200 }}
        // onFocus={() => {
        //   this.setState({
        //     loading: true,
        //   });
        //   request().then((Data) => {
        //     this.setState({
        //       List: Data,
        //       loading: false,
        //     });
        //   });
        // }}
        {...this.props}
      >
        {Options}
      </Select>
    );
  }
}


export default SimpleSelect;
