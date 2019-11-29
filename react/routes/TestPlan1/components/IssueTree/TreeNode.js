import React, { useState, useContext } from 'react';
import { observer } from 'mobx-react-lite';

function TreeNode({ children, item }) {
  return (
    <div
      role="none"
    >
      {children}
    </div>
  );
}
export default observer(TreeNode);
