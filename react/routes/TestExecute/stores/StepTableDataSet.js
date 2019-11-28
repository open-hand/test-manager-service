/* eslint-disable indent */
import React from 'react';
import { DataSet } from 'choerodon-ui/pro/lib';
import { stores } from '@choerodon/boot';
import { FormattedMessage } from 'react-intl';


function CreateIssueDataSet(projectId, caseId) {
    return {
        autoQuery: true,
        selection: false,
        paging: true,
        dataKey: null,
        fields: [
            {
                name: 'testStep', type: 'string', label: <FormattedMessage id="execute_testStep" />,
            },
            {
                name: 'testData', type: 'string', label: <FormattedMessage id="execute_testData" />,
            },
            {
                name: 'expectedResult', type: 'string', label: <FormattedMessage id="execute_expectedOutcome" />,
            },
            {
                name: 'stepStatus', type: 'string', label: <FormattedMessage id="execute_stepStatus" />,
            },
            {
                name: 'stepAttachment', type: 'string', label: <FormattedMessage id="attachment" />,
            },
            {
                name: 'defects', type: 'object', label: <FormattedMessage id="bug" />,
            },
            { name: 'comment', type: 'string', label: <FormattedMessage id="execute_comment" /> },

        ],

        transport: {
            read: {
                url: `test/v1/projects/${projectId}/cycle/case/step/query/${caseId}`,
                method: 'get',
            },
        },
    };
}
export default CreateIssueDataSet;
