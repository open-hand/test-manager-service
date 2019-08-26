import React, { Component } from 'react';
import { Tooltip } from 'choerodon-ui';
import PropTypes from 'prop-types';

const defaultStyle = {
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  whiteSpace: 'nowrap',
};
const defaultProps = {
  style: {},
  placement: 'topLeft',
};
class SmartTooltip extends Component {
  constructor(props) {
    super(props);
    this.state = {
      overflow: false,
    };
  }

  componentDidMount() {
    this.checkOverflow();
  }

  componentDidUpdate(prevProps, prevState) {
    this.checkOverflow();
  }

  checkOverflow = () => {
    if (this.container) {
      const { scrollWidth, clientWidth } = this.container;
      const isOverflow = scrollWidth > clientWidth;
      const { overflow } = this.state;
      if (overflow !== isOverflow) {
        this.setState({
          overflow: isOverflow,
        });
      }
    }
  }

  saveRef = (name) => (ref) => {
    this[name] = ref;
  }

  renderContent = () => {
    const {
      title, children, style, width, placement,
    } = this.props;
    const { overflow } = this.state;
    // eslint-disable-next-line react/jsx-props-no-spreading
    const dom = <div {...this.props} style={{ ...defaultStyle, ...style, width }} title={null} ref={this.saveRef('container')} />;
    return overflow
      ? (
        <Tooltip placement={placement} title={title || children.props.children}>
          {dom}
        </Tooltip>
      )
      : dom;
  }

  render() {
    return this.renderContent();
  }
}

SmartTooltip.propTypes = {
  placement: PropTypes.string,
  style: PropTypes.object,
};

export default SmartTooltip;
