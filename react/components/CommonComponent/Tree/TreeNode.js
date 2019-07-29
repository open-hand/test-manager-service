import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Icon } from 'choerodon-ui';
import { Draggable, Droppable, DragDropContext } from 'react-beautiful-dnd';

class TreeNode extends Component {
  state = {
    expand: false,
  }

  handleExpand = () => {
    const { expand } = this.state;
    if (!this.props.children) {
      return;
    }
    this.setState({
      expand: !expand,
    });
  }

  renderChildren = () => {
    const { expand } = this.state;
    const {
      children, title, icon, data, 
    } = this.props;
    return (
      children && children.length > 0 ? (
        <div>
          <div role="none" className="tree-item" onClick={this.handleExpand}>
            <Icon type="baseline-arrow_right" className={expand ? 'toggler toggled' : 'toggler'} />
            <div style={{ marginRight: 5 }}>
              {icon}
            </div>
            {title}
          </div>
          <div className={expand ? 'collapsible-wrapper' : 'collapsible-wrapper collapsed'}>
            {children ? (
              <ul className="collapsible" ref={(node) => { this.node = node; }}>
                {children}
              </ul>
            ) : null}
          </div>
        </div>
      ) : (
        <Droppable droppableId={data.key}>
          {(provided, snapshot) => (
            <div
              className="tree-item"
              ref={provided.innerRef}
              style={{ background: snapshot.isDraggingOver && 'green' }}
            >
              <div style={{ marginRight: 5 }}>
                {icon}
              </div>
              {title}
              {provided.placeholder}
            </div>
          )
            }
        </Droppable>
      )
    );
  };

  render() {
    return (
      <li>
        {this.renderChildren()}
      </li>
    );
  }
}

TreeNode.propTypes = {

};

export default TreeNode;
