/* eslint-disable indent */
import { DataSet } from 'choerodon-ui/pro/lib';
import { stores } from '@choerodon/boot';
import { Choerodon, axios } from '@choerodon/boot';
import {
    getProjectId, returnBeforeTextUpload, getOrganizationId, text2Delta,
} from '@/common/utils';
import { updateSidebarDetail } from '@/api/ExecuteDetailApi';
import { uploadFile } from '@/api/FileApi';

const { AppState } = stores;
export function UpdateExecuteData(data) {
    const testCycleCaseStepUpdateVOS = data.testCycleCaseStepUpdateVOS.map(
        (i) => {
            let { stepId } = i;
            let { executeStepId } = i;
            if (String(i.stepId).indexOf('.') !== -1) {
                stepId = 0;
                executeStepId = null;
            }
            return {
                ...i,
                stepId,
                executeStepId,
            };
        },
    );
    return new Promise((resolve) => {
        returnBeforeTextUpload(data.description, data, async (res) => {
            const newData = {
                ...res,
                fileList: [],
                testCycleCaseStepUpdateVOS,
            };
            const { fileList } = res;
            await updateSidebarDetail(newData);
            if (fileList) {
                const formData = new FormData();
                fileList.forEach((file) => {
                    formData.append('file', file);
                });

                const config = {
                    bucketName: 'test', attachmentLinkId: res.executeId, attachmentType: 'CYCLE_CASE',
                };
                await uploadFile(formData, config);
            }
            resolve(true);
        });
    });
}

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
            { name: 'description', type: 'any', label: description },

            // 读取的原始附件列表
            {
                name: 'cycleCaseAttachmentRelVOList',
                type: 'object',
                label: '附件',
                ignore: 'always',

            },
            // 修改操作时的附件列表
            {
                name: 'fileList',
                type: 'object',
                label: '附件',
                ignore: 'always',
            },
            {
                name: 'testCycleCaseStepUpdateVOS',
                type: 'object',
            },
            {
                name: 'caseStepVOS',
                type: 'object',
                ignore: 'always',
            },

        ],

        transport: {
            // /v1/projects/{project_id}/cycle/case/case_step/{execute_id}  
            read: () => ({
                url: `test/v1/projects/${getProjectId()}/cycle/case/case_step/${executeId}`,
                method: 'get',
                transformResponse: (data) => {
                    const newData = JSON.parse(data);
                    return {
                        ...newData,
                        description: text2Delta(newData.description),
                        testCycleCaseStepUpdateVOS: [],
                        caseStepVOS: newData.testCycleCaseStepUpdateVOS,
                    };
                },
            }),
        },
    };
}

export default EditIssueDataSet;
