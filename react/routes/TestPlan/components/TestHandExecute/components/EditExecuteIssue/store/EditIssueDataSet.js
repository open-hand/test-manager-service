/* eslint-disable no-console */
/* eslint-disable indent */
import { DataSet } from 'choerodon-ui/pro/lib';
import { stores } from '@choerodon/boot';
import { getProjectId, beforeTextUpload, getOrganizationId } from '@/common/utils';

const { AppState } = stores;

function EditIssueDataSet(executeId, intlPrefix, intl) {
    const summary = intl.formatMessage({ id: `${intlPrefix}_issueFilterBySummary` });
    const description = '描述';

    return {
        autoQuery: true,
        selection: false,
        autoCreate: false,
        paging: false,
        dataKey: null,
        fields: [
            {
                name: 'summary', type: 'string', label: summary, required: true,
            },
            { name: 'description', type: 'object', label: description },
            {
                name: 'cycleCaseAttachmentRelVOList',
                type: 'object',
                label: '附件',

            },
            {
                name: 'testCycleCaseStepUpdateVOS',
                type: 'object',
            },
            {
                name: 'caseStepVOS',
                type: 'object',
            },

        ],

        transport: {
            // /v1/projects/{project_id}/cycle/case/case_step/{execute_id}  
            read: () => ({
                url: `test/v1/projects/${getProjectId()}/cycle/case/case_step/${executeId}`,
                method: 'get',
                transformResponse: (data) => {
                    console.log('data=', JSON.parse(data));
                    return {
                        ...JSON.parse(data),
                        
                    };
                },
            }),
            // eslint-disable-next-line arrow-body-style
            submit: ({ data, dataSet }) => {
                // console.log('submit', data);
                const newData = {
                    ...data[0],
                };
                // /v1/projects/28/cycle/case/case_step
                return ({
                    url: `test/v1/projects/${getProjectId()}/cycle/case/case_step`,
                    method: 'put',
                    data: newData,
                });
            },
        },
    };
}

export default EditIssueDataSet;
