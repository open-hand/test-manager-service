import React, { Component } from 'react';
import { Tooltip } from 'choerodon-ui';
import PropTypes from 'prop-types';

const defaultStyle = {
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  whiteSpace: 'nowrap',
};
class SmartTooltip extends Component {
  state = {
    overflow: false,
  }

  static defaultProps = {
    style: {},
    placement: 'topLeft',
  };

  componentDidMount() {
    this.checkOverflow();
  }
  
  componentDidUpdate(prevProps, prevState) {
    this.checkOverflow();
  }

  checkOverflow=() => {
    if (this.container) {
      const { scrollWidth, clientWidth } = this.container;
      const isOverflow = scrollWidth > clientWidth;
      // console.log(scrollWidth, clientWidth);     
      if (this.state.overflow !== isOverflow) {
        this.setState({
          overflow: isOverflow,
        });
      }
    }    
  }

  saveRef = name => (ref) => {
    this[name] = ref;
  }

  renderContent = () => {
    const {
      title, children, style, width, placement,
    } = this.props;
    // console.log(this.props);
    const { overflow } = this.state;
    const dom = <div {...this.props} style={{ ...defaultStyle, ...style, width }} title={null} ref={this.saveRef('container')} />;
    return overflow
      ? (
        <Tooltip placement={placement} title={title || children}>
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
