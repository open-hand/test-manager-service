import React from 'react';
import Animate from 'choerodon-ui/lib/animate';
import EditIssue from './EditIssue';
import { EditIssueContextProvider } from './stores';

export default function Index(props) {
  return (
    <Animate
      // key={props.key}
      component="div"
      transitionAppear
      transitionName="slide-right"
      hiddenProp="hidden"
    >
      {props.visible && (
        <EditIssueContextProvider {...props}>
          <EditIssue />
        </EditIssueContextProvider>
      )}
    </Animate>
  );
}
