import React, { Component } from 'react';
import { Select } from 'choerodon-ui';
import { getFoldersByVersion } from '../../api/IssueManageApi';

const { Option } = Select;

class SelectFolder extends Component {
  state = {
    loading: false,
    List: [],
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.versionId && !nextProps.versionId) {
      this.setState({
        List: [],
      });
    }
  }

  render() {
    const { versionId } = this.props;
    const { loading, List } = this.state;
    const Options = List.map((item) => (
      <Option value={item.folderId} key={item.folderId}>
        {item.name}
      </Option>
    ));
    return (
      <Select
        label="文件夹"
        // getPopupContainer={ele => ele.parentNode}
        loading={loading}
        style={{ width: 200 }}
        onFocus={() => {
          if (versionId) {
            this.setState({
              loading: true,
            });
            getFoldersByVersion(versionId === 'all' ? null : versionId).then((Data) => {
              this.setState({
                List: Data,
                loading: false,
              });
            });
          }
        }}
        {...this.props}
      >
        {Options}
      </Select>
    );
  }
}


export default SelectFolder;
