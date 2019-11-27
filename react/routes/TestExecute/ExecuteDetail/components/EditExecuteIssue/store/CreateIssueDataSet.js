/* eslint-disable indent */
import { DataSet } from 'choerodon-ui/pro/lib';
import { stores } from '@choerodon/boot';
import { getProjectId, beforeTextUpload } from '@/common/utils';

const { AppState } = stores;

function CreateIssueDataSet(intlPrefix, intl) {
    const summary = intl.formatMessage({ id: `${intlPrefix}_issueFilterBySummary` });
    const description = '描述';
    const folderId = intl.formatMessage({ id: `${intlPrefix}_folder` });
    const Issuelabel = intl.formatMessage({ id: 'summary_label' });

    return {
        autoQuery: false,
        selection: false,
        autoCreate: true,
        paging: false,
        dataKey: null,
        fields: [
            {
                name: 'summary', type: 'string', label: summary, required: true,
            },
            { name: 'description', type: 'object', label: description },
            {
                name: 'fileList',
                type: 'object',
                label: '附件',

            },
            {
                name: 'folder',
                type: 'object',
                label: folderId,
                required: true,
                textField: 'fileName',
                valueField: 'folderId',
                ignore: 'always',
            },
            {
                name: 'folderId',
                type: 'number',
                bind: 'folder.folderId',
            },
            {
                name: 'issueLink',
                type: 'object',
                label: Issuelabel,
                textField: 'labelName',
                valueField: 'labelId',
                multiple: ',',
                // options: linkOptions,
                lookupUrl: `agile/v1/projects/${AppState.currentMenuType.id}/issue_labels`,
            },
            {
                name: 'caseStepVOS',
                type: 'object',
            },

        ],

        transport: {
            // eslint-disable-next-line arrow-body-style
            submit: ({ data, dataSet }) => {
                // console.log('submit', data);
                const newData = {
                    ...data[0],
                    caseStepVOS: data[0].caseStepVOS.map(i => ({
                        testStep: i.testStep,
                        testData: i.testData,
                        expectedResult: i.expectedResult,
                    })),
                };
                return ({
                    url: `test/v1/projects/${getProjectId()}/case/create`,
                    method: 'post',
                    data: newData,
                });
            },
        },
    };
}
export default CreateIssueDataSet;
