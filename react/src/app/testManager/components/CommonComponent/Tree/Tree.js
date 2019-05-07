import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Draggable, Droppable, DragDropContext } from 'react-beautiful-dnd';
import TreeNode from './TreeNode';
import './Tree.scss';

class Tree extends Component {
  onDragEnd=() => {
    console.log('end');
  }

  onDragStart=() => {
    console.log('start');
  }

  render() {
    return (
      <DragDropContext onDragEnd={this.onDragEnd} onDragStart={this.onDragStart}>
        <ul>
          {this.props.children}
        </ul>
      </DragDropContext>
    );
  }
}

Tree.propTypes = {

};
Tree.TreeNode = TreeNode;
export default Tree;
